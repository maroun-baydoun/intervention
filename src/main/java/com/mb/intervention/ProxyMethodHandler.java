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

import com.mb.intervention.context.Context.ContextEntry;
import com.mb.intervention.script.DynamicScriptEngine;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;


public class ProxyMethodHandler implements MethodHandler {

    private ContextEntry contextEntry;
    
    private static final String PRE_INVOKE_FUNCTION=ObjectFactory.getInstance().getContext().getConfiguration().getPreInvokeFunction();
    private static final String POST_INVOKE_FUNCTION=ObjectFactory.getInstance().getContext().getConfiguration().getPostInvokeFunction();
    private static final String DEFAULT_SCRIPT=ObjectFactory.getInstance().getContext().getConfiguration().getDefaultScript();
    
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

        
        boolean defaultScriptPresent = defaultScriptEngine.evalScript(DEFAULT_SCRIPT);

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
