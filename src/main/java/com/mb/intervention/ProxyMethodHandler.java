package com.mb.intervention;

import com.mb.intervention.context.Context.ContextEntry;
import com.mb.intervention.script.DynamicScriptEngine;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;


public class ProxyMethodHandler implements MethodHandler {

    private ContextEntry contextEntry;
    
    private static final String PRE_INVOKE_FUNCTION=ObjectFactory.getInstance().getContext().getConfiguration().getPreInvokeFunction();
    private static final String POST_INVOKE_FUNCTION=ObjectFactory.getInstance().getContext().getConfiguration().getPostInvokeFunction();

    /**
     *
     * @param contextEntry
     */
    public ProxyMethodHandler(ContextEntry contextEntry) {

        this.contextEntry = contextEntry;
    }

    /**
     *
     * @param object
     * @param method
     * @param original
     * @param arguments
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object object, Method method, Method original, Object[] arguments) throws Throwable {


        Object invokeResult = null;

        String methodName = method.getName();
        
       
        DynamicScriptEngine dynamicScriptEngine = DynamicScriptEngine.getInstance();
        DynamicScriptEngine defaultScriptEngine = DynamicScriptEngine.getDefaultInstance();


        boolean defaultScriptPresent = defaultScriptEngine.evalScript("default");

        if (defaultScriptPresent) {
 
            invokeResult = defaultScriptEngine.invoke(PRE_INVOKE_FUNCTION,contextEntry.getDynamicClassId(),object, methodName, arguments);

            if ((invokeResult instanceof Boolean) && Boolean.parseBoolean(invokeResult.toString()) == false) {

                return null;
            }
        }
        

        if (dynamicScriptEngine.evalScript(contextEntry.getScript())) {

            invokeResult = dynamicScriptEngine.invoke(PRE_INVOKE_FUNCTION,object,methodName, arguments);

            if ((invokeResult instanceof Boolean) && Boolean.parseBoolean(invokeResult.toString()) == false) {

                return null;
            }

            invokeResult = original.invoke(object, arguments);

            dynamicScriptEngine.invoke(POST_INVOKE_FUNCTION, methodName, arguments);
        }

        if (defaultScriptPresent) {
            defaultScriptEngine.invoke(POST_INVOKE_FUNCTION,contextEntry.getDynamicClassId(),object, methodName, arguments);
        }

        return invokeResult;
    }

}
