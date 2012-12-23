
package com.mb.intervention.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Context {
    
    private Configuration configuration;
    private Map<String,ContextEntry> contextEntires;
    private Map<Class<?>,List<String>> excludedMethods;
   

    public Context() {
        
        contextEntires=new HashMap<String, ContextEntry>();
    }
    
    public void addContextEntry(ContextEntry entry){
            
         contextEntires.put(entry.dynamicClassId, entry);
    }

    public Map<String, ContextEntry> getContextEntires() {
        return contextEntires;
    }
    
    public ContextEntry getContextEntry(String dynamicClassId) {
        return contextEntires.get(dynamicClassId);
    }
    
    
    public boolean containsContextEntry(String dynamicClassId) {
        return contextEntires.containsKey(dynamicClassId);
    }
    
    public void addExcludedMethod(Class<?> dynamicClass,String methodName){
 
            if(excludedMethods==null){
                excludedMethods=new  HashMap<Class<?>, List<String>>();
            }
            
            List<String> classExcludedMethods=excludedMethods.get(dynamicClass);
            
            if(classExcludedMethods==null){
                classExcludedMethods=new ArrayList<String>();
                classExcludedMethods.add(methodName);
                excludedMethods.put(dynamicClass, classExcludedMethods);
            }
            
            else{
                 classExcludedMethods.add(methodName);
            }
            
        }
    
    public List<String> getExcludedMethodsForClass(Class<?> dynamicClass){
        
        return excludedMethods.get(dynamicClass);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
    
    
    
    public static class ContextEntry{
        
        private String dynamicClassId;
        private Class<?> dynamicClass;
        private InterceptionPolicy interceptionPolicy;
        private String script;
        

        public ContextEntry() {
            
        }

        public Class<?> getDynamicClass() {
            return dynamicClass;
        }

        public String getDynamicClassId() {
            return dynamicClassId;
        }

        public InterceptionPolicy getInterceptionPolicy() {
            return interceptionPolicy;
        }

        public String getScript() {
            return script;
        }

        
        
        public void setDynamicClass(Class<?> dynamicClass) {
            this.dynamicClass = dynamicClass;
        }

        public void setDynamicClassId(String dynamicClassId) {
            this.dynamicClassId = dynamicClassId;
        }

        public void setInterceptionPolicy(InterceptionPolicy interceptionPolicy) {
            this.interceptionPolicy = interceptionPolicy;
        }

        public void setScript(String script) {
            this.script = script;
        }
        
    }
    
    public static class Configuration {
        
        private String defaultScript;
        private String dynamicLanguage;
        private String scriptExtension;
        private String preInvokeFunction;
        private String postInvokeFunction;
        private InterceptionPolicy interceptionPolicy;

        public String getDefaultScript() {
            return defaultScript;
        }

        public String getDynamicLanguage() {
            return dynamicLanguage;
        }

        public String getScriptExtension() {
            return scriptExtension;
        }

        public InterceptionPolicy getInterceptionPolicy() {
            return interceptionPolicy;
        }

        public String getPostInvokeFunction() {
            return postInvokeFunction;
        }

        public String getPreInvokeFunction() {
            return preInvokeFunction;
        }
        
        

        public void setDefaultScript(String defaultScript) {
            this.defaultScript = defaultScript;
        }

        public void setDynamicLanguage(String dynamicLanguage) {
            this.dynamicLanguage = dynamicLanguage;
        }

        public void setScriptExtension(String scriptExtension) {
            this.scriptExtension = scriptExtension;
        }

        public void setInterceptionPolicy(InterceptionPolicy interceptionPolicy) {
            this.interceptionPolicy = interceptionPolicy;
        }

        public void setPostInvokeFunction(String postInvokeFunction) {
            this.postInvokeFunction = postInvokeFunction;
        }

        public void setPreInvokeFunction(String preInvokeFunction) {
            this.preInvokeFunction = preInvokeFunction;
        }
        
        
        
        
        
        
    }
}
