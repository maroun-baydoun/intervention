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
package com.mb.intervention.context;

import com.mb.intervention.log.LocalizedLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Context {

    private Configuration configuration;
    private Map<String, ContextEntry> contextEntires;
    private Map<Class<?>, List<String>> excludedMethods;

    public Context() {

        contextEntires = new HashMap<String, ContextEntry>();
    }

    public void addContextEntry(ContextEntry entry) {

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

    public void addExcludedMethod(Class<?> dynamicClass, String methodName) {

        if (excludedMethods == null) {
            excludedMethods = new HashMap<Class<?>, List<String>>();
        }

        List<String> classExcludedMethods = excludedMethods.get(dynamicClass);

        if (classExcludedMethods == null) {
            classExcludedMethods = new ArrayList<String>();
            classExcludedMethods.add(methodName);
            excludedMethods.put(dynamicClass, classExcludedMethods);
        } else {
            classExcludedMethods.add(methodName);
        }

    }

    public List<String> getExcludedMethodsForClass(Class<?> dynamicClass) {

        return excludedMethods.get(dynamicClass);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static class ContextEntry {

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
        private static Configuration defaultConfiguration;

        public static Configuration getDefault() {


            if (defaultConfiguration == null) {

                try {
                    Properties properties = new Properties();
                    properties.load(AnnotationContextProvider.class.getResourceAsStream("../defaults.properties"));

                    defaultConfiguration = new Configuration();
                    defaultConfiguration.setDefaultScript(properties.getProperty("defaultScript"));
                    defaultConfiguration.setDynamicLanguage(properties.getProperty("dynamicLanguage"));
                    defaultConfiguration.setScriptExtension(properties.getProperty("scriptExtension"));
                    defaultConfiguration.setPostInvokeFunction(properties.getProperty("postInvokeFunction"));
                    defaultConfiguration.setPreInvokeFunction(properties.getProperty("preInvokeFunction"));
                    defaultConfiguration.setInterceptionPolicy(InterceptionPolicy.valueOf(properties.getProperty("interceptionPolicy").toUpperCase()));

                } catch (IOException ex) {
                    LocalizedLogger.severe(Configuration.class.getName(), "exception_occurred", ex.getMessage());
                }
            }

            return defaultConfiguration;
        }

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
