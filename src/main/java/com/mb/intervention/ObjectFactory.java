/**Copyright 2012 Maroun Baydoun

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
**/

package com.mb.intervention;

import com.mb.intervention.context.AnnotationContextProvider;
import com.mb.intervention.context.Context;
import com.mb.intervention.context.ContextProvider;
import com.mb.intervention.context.InterceptionPolicy;
import com.mb.intervention.context.JsonContextProvider;
import com.mb.intervention.exceptions.InterventionException;
import com.mb.intervention.proxy.ProxyMethodHandler;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;


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
     *Builds an ObjectFactory with an {@link AnnotationContextProvider}
     */
    public static void build() {

        build(new AnnotationContextProvider());

    }
    
    /**
     * Builds an ObjectFactory with the specified {@link ContextProvider}
     * 
     * @param contextProvider 
     */
     public static void build(ContextProvider contextProvider) {

        if (instance == null) {
            instance = new ObjectFactory();
            instance.contextProvider = contextProvider;
            
            instance.contextProvider.build();
            instance.context = instance.contextProvider.getContext();
        }

    }

    /**
     *Builds an ObjectFactory with the specified context file
     * 
     * @param contextFile The path of the context file
     */
    public static void build(String contextFile) throws InterventionException {
        try {
            
            FileReader contextFileReader=new FileReader(contextFile);
            build(new JsonContextProvider(contextFileReader));
            
        } catch (FileNotFoundException ex) {
           
            throw  new InterventionException("context_file_not_found",ex,contextFile);
        }
    }

     public static void build(InputStream contextInputStream) {
         
         if(contextInputStream!=null){
             
            InputStreamReader contextInputStreamReader=new InputStreamReader(contextInputStream);
            build(new JsonContextProvider(contextInputStreamReader));
         }
         else{
             throw new InterventionException("context_inputstream_null");
         }
    }
     
     public static void build(Reader contextReader) {

       build(new JsonContextProvider(contextReader));
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
                methodFilter = new ProxyMethodFilter(contextEntry);
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
                throw new InterventionException("exception_occurred", ex,ex.getMessage());
            }
        } else {
            throw new InterventionException("dynamic_class_not_registered", dynamicClassKey);
            
        }

        return createdObject;
    }

    private static class ProxyMethodFilter implements MethodFilter {

        private static final InterceptionPolicy GLOBAL_INTERCEPTION_POLICY = ObjectFactory.instance.context.getConfiguration().getInterceptionPolicy();
       
        private Context.ContextEntry contextEntry;
        
        public ProxyMethodFilter(Context.ContextEntry contextEntry) {

            this.contextEntry = contextEntry;
        }

        @Override
        public boolean isHandled(Method method) {

            String methodName = method.getName();

            if (contextEntry.getExcludedMethods() != null && contextEntry.getExcludedMethods().contains(methodName)) {
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
