
package com.mb.intervention;

import com.mb.intervention.context.AnnotationContextProvider;
import com.mb.intervention.context.Context;
import com.mb.intervention.context.ContextProvider;
import com.mb.intervention.context.InterceptionPolicy;
import com.mb.intervention.context.JsonContextProvider;
import com.mb.intervention.log.MyLogger;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 *
 * @author user
 */
public final class ObjectFactory {

    private static ObjectFactory instance;
    private final ProxyFactory proxyFactory;
    private ContextProvider contextProvider;
    private Context context;
    private final Map<String, MethodHandler> methodHandlers;
    private final Map<String, MethodFilter> methodFilters;

    private ObjectFactory() {
        proxyFactory = new ProxyFactory();
        methodHandlers = new HashMap<String, MethodHandler>();
        methodFilters = new HashMap<String, MethodFilter>();
    }

    

    /**
     *
     */
    public static void build() {

        if (instance == null) {
            instance = new ObjectFactory();
            instance.contextProvider = new AnnotationContextProvider();
        }

        instance.contextProvider.build();
        instance.context = instance.contextProvider.getContext();
    }

    /**
     *
     * @param configurationFile
     */
    public static void build(String configurationFile) {

        if (instance == null) {
            instance = new ObjectFactory();
            instance.contextProvider=new JsonContextProvider(configurationFile);
        }
        
        build();
    }

    /**
     *
     * @return
     */
    public static ObjectFactory getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     *
     * @param dynamicClassKey
     * @return
     */
    public Object create(String dynamicClassKey) {

        Object createdObject = null;

        Context.ContextEntry contextEntry = context.getContextEntry(dynamicClassKey);

        if (contextEntry != null) {

            Class<?> dynamicClass = contextEntry.getDynamicClass();

            proxyFactory.setSuperclass(dynamicClass);

            MethodFilter methodFilter = methodFilters.get(dynamicClassKey);

            if (methodFilter == null) {
                methodFilter = new ProxyMethodFilter(contextEntry, context.getExcludedMethodsForClass(dynamicClass));
                methodFilters.put(dynamicClassKey, methodFilter);
            }

            proxyFactory.setFilter(methodFilter);

            MethodHandler methodHandler = methodHandlers.get(dynamicClassKey);

            if (methodHandler == null) {
                methodHandler = new ProxyMethodHandler(contextEntry);
                methodHandlers.put(dynamicClassKey, methodHandler);
            }

            try {

                createdObject = proxyFactory.create(new Class[0], new Object[0], methodHandler);

            } catch (Exception ex) {
                MyLogger.severe(getClass().getName(), "exception_occurred", ex);
            }
        } else {
            MyLogger.severe(getClass().getName(), "dynamic_class_not_registered", dynamicClassKey);
        }

        return createdObject;
    }

    private static class ProxyMethodFilter implements MethodFilter {

        private static final InterceptionPolicy GLOBAL_INTERCEPTION_POLICY = ObjectFactory.instance.context.getConfiguration().getInterceptionPolicy();
       
        private Context.ContextEntry contextEntry;
        private List<String> excludedMethods;

        public ProxyMethodFilter(Context.ContextEntry contextEntry, List<String> excludedMethods) {

            this.contextEntry = contextEntry;
            this.excludedMethods = excludedMethods;
        }

        @Override
        public boolean isHandled(Method method) {

            String methodName = method.getName();

            if (excludedMethods != null && excludedMethods.contains(methodName)) {
                return false;
            }

            InterceptionPolicy entryPolicy = contextEntry.getInterceptionPolicy();

            if (entryPolicy == InterceptionPolicy.UNSPECIFIED) {
                entryPolicy = GLOBAL_INTERCEPTION_POLICY;
            }


            boolean shouldHandle = false;
            switch (entryPolicy) {
                case ALL:
                    shouldHandle = (!methodName.equals("finalize"));
                    break;
                case NONE:
                    shouldHandle = false;
                    break;
                case GETTERS:
                    shouldHandle = (methodName.startsWith("get") && methodName.length() > 3);
                    break;
                case SETTERS:
                    shouldHandle = (methodName.startsWith("set") && methodName.length() > 3);
                    break;
                case GETTERS_SETTERS:
                    shouldHandle = ((methodName.startsWith("get") || methodName.startsWith("set")) && methodName.length() > 3);
                    break;

            }

            return shouldHandle;

        }
    }
}
