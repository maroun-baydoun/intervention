/**
 * Copyright 2012 Maroun Baydoun
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
*
 */
package com.mb.intervention.proxy;

import com.mb.intervention.ObjectFactory;
import com.mb.intervention.context.Context.ContextEntry;
import com.mb.intervention.context.ScriptPolicy;
import com.mb.intervention.script.DynamicScriptEngine;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import javax.script.ScriptException;

public class ProxyMethodHandler implements MethodHandler {

    private ContextEntry contextEntry;
    private static final String PRE_INVOKE_FUNCTION = ObjectFactory.getInstance().getContext().getConfiguration().getPreInvokeFunction();
    private static final String POST_INVOKE_FUNCTION = ObjectFactory.getInstance().getContext().getConfiguration().getPostInvokeFunction();
    private static final String DEFAULT_SCRIPT = ObjectFactory.getInstance().getContext().getConfiguration().getDefaultScript();
    private static final ScriptPolicy GLOBAL_SCRIPT_POLICY=ObjectFactory.getInstance().getContext().getConfiguration().getScriptPolicy();
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

        ScriptPolicy scriptPolicy=contextEntry.getScriptPolicy();
        

        if(scriptPolicy==null || scriptPolicy==ScriptPolicy.UNSPECIFIED){
            scriptPolicy=GLOBAL_SCRIPT_POLICY;
        }

        boolean evaluateDefaultScript=scriptPolicy==ScriptPolicy.DEFAULT_AND_SELF || scriptPolicy==ScriptPolicy.DEFAULT_ONLY;
        boolean evaluateSelfScript=scriptPolicy==ScriptPolicy.DEFAULT_AND_SELF || scriptPolicy==ScriptPolicy.SELF_ONLY;
        
        if(evaluateDefaultScript){
            if(defaultScriptPreInvoke(defaultScriptEngine, contextEntry, object, methodName, arguments)){

                return null;
            }
        }
        
        if(evaluateSelfScript){
        
            if(selfScriptPreInvoke(dynamicScriptEngine, contextEntry, object, methodName, arguments)){

                return null;
            }
        }
        
        invokeResult = original.invoke(object, arguments);
        
        
        if(evaluateSelfScript){
            selfScriptPostInvoke(dynamicScriptEngine, object, methodName, arguments);
        }
        
        if(evaluateDefaultScript){
            defaultScriptPostInvoke(defaultScriptEngine, contextEntry, object, methodName, arguments);
        }
    
        return invokeResult;
    }
    
   private static boolean defaultScriptPreInvoke(DynamicScriptEngine defaultScriptEngine,ContextEntry contextEntry,Object object,String methodName,Object[] arguments) throws ScriptException, NoSuchMethodException{
       
       defaultScriptEngine.evalScript(DEFAULT_SCRIPT);

       Object invokeResult = defaultScriptEngine.invoke(PRE_INVOKE_FUNCTION, contextEntry.getDynamicClassId(), object, methodName, arguments);

       return ((invokeResult instanceof Boolean) && Boolean.parseBoolean(invokeResult.toString()) == false); 

   }
   
   private static void defaultScriptPostInvoke(DynamicScriptEngine defaultScriptEngine,ContextEntry contextEntry,Object object,String methodName,Object[] arguments) throws ScriptException, NoSuchMethodException{
       
       defaultScriptEngine.invoke(POST_INVOKE_FUNCTION, contextEntry.getDynamicClassId(), object, methodName, arguments);

   }
   
   private static boolean selfScriptPreInvoke(DynamicScriptEngine dynamicScriptEngine,ContextEntry contextEntry,Object object,String methodName,Object[] arguments) throws ScriptException, NoSuchMethodException{
       
      dynamicScriptEngine.evalScript(contextEntry.getScript()); 

      Object invokeResult = dynamicScriptEngine.invoke(PRE_INVOKE_FUNCTION, object, methodName, arguments);

      return ((invokeResult instanceof Boolean) && Boolean.parseBoolean(invokeResult.toString()) == false);

   }
   
   private static void selfScriptPostInvoke(DynamicScriptEngine dynamicScriptEngine, Object object,String methodName,Object[] arguments) throws ScriptException, NoSuchMethodException{
       
      dynamicScriptEngine.invoke(POST_INVOKE_FUNCTION,object, methodName, arguments);

   }
   
   
   
}
