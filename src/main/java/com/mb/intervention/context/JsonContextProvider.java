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

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mb.intervention.log.LocalizedLogger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonContextProvider extends ContextProvider {

    private String jsonFile;
    private static final String CONFIG_PROP = "config";
    private static final String DYNAMIC_PROP = "dynamic";
    private static final String DYNAMIC_CLASS_PROP = "class";
    private static final String DYNAMIC_ID_PROP = "id";
    private static final String DYNAMIC_SCRIPT_PROP = "script";
    private static final String DYNAMIC_INTERCEPTION_POLICY_PROP = "interceptionPolicy";
    private static final String DYNAMIC_EXCLUDE_PROP = "exclude";

    public JsonContextProvider(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    @Override
    public void build() {
        try {
            InputStream jsonInputStream = JsonContextProvider.class.getResourceAsStream(jsonFile);

            if (jsonInputStream != null) {

                InputStreamReader jsonInputStreamReader = new InputStreamReader(jsonInputStream);
                BufferedReader jsonBufferedReader = new BufferedReader(jsonInputStreamReader);

                Gson gson = new Gson();
                Map<String, Object> contextMap = gson.fromJson(jsonBufferedReader, Map.class);

                Map<String, String> configurationMap = (Map<String, String>) contextMap.get(CONFIG_PROP);

                if (configurationMap != null) {

                    Context.Configuration configuration = buildConfigurationFromMap(configurationMap);

                    configurationDiscovered(configuration);
                }

                ArrayList<Map<String, Object>> dynamicList = (ArrayList<Map<String, Object>>) contextMap.get(DYNAMIC_PROP);

                if (dynamicList != null) {
                    for (Map<String, Object> dynamicMap : dynamicList) {
                        Context.ContextEntry contextEntry = buildContextEntryFromMap(dynamicMap, context);

                        if (contextEntry != null) {

                            contextEntryDiscovered(contextEntry);
                        }
                    }
                }



            }
        } catch (JsonSyntaxException ex) {

            LocalizedLogger.severe(JsonContextProvider.class.getName(), "context_error", ex.getMessage());

        } catch (JsonIOException ex) {
            LocalizedLogger.severe(JsonContextProvider.class.getName(), "exception_occurred", ex.getMessage());
        }

    }

    private Context.Configuration buildConfigurationFromMap(Map<String, String> configurationMap) {

        Context.Configuration configuration = new Context.Configuration();

        configuration.setDefaultScript(configurationMap.get("defualtScript"));
        configuration.setDynamicLanguage(configurationMap.get("dynamicLanguage"));
        configuration.setPostInvokeFunction(configurationMap.get("postInvokeFunction"));
        configuration.setPreInvokeFunction(configurationMap.get("preInvokeFunction"));
        configuration.setScriptExtension(configurationMap.get("scriptExtension"));
        configuration.setScriptLocation(configurationMap.get("scriptLocation"));

        String configurationInterceptionPolicy = configurationMap.get("interceptionPolicy");

        if (configurationInterceptionPolicy != null) {

            try {

                configuration.setInterceptionPolicy(InterceptionPolicy.valueOf(configurationInterceptionPolicy));
            } catch (IllegalArgumentException ex) {

                LocalizedLogger.severe(JsonContextProvider.class.getName(), "context_error", ex.getMessage());
            }
        }

        return configuration;
    }

    private Context.ContextEntry buildContextEntryFromMap(Map<String, Object> dynamicMap, Context context) {

        Context.ContextEntry contextEntry = null;

        if (dynamicMap.containsKey(DYNAMIC_CLASS_PROP)) {
            String dynamicClassName = dynamicMap.get(DYNAMIC_CLASS_PROP).toString();
            try {
                Class<?> dynamiClass = Class.forName(dynamicClassName);

                contextEntry = new Context.ContextEntry();
                contextEntry.setDynamicClass(dynamiClass);

            } catch (ClassNotFoundException ex) {
                LocalizedLogger.severe(JsonContextProvider.class.getName(), "class_not_found", dynamicClassName);
            }

            if (contextEntry != null) {
                contextEntry.setDynamicClassId((String) dynamicMap.get(DYNAMIC_ID_PROP));
                contextEntry.setScript((String) dynamicMap.get(DYNAMIC_SCRIPT_PROP));

                contextEntry.setInterceptionPolicy(InterceptionPolicy.ALL);

                String configurationInterceptionPolicy = (String) dynamicMap.get(DYNAMIC_INTERCEPTION_POLICY_PROP);

                if (configurationInterceptionPolicy != null) {

                    try {

                        contextEntry.setInterceptionPolicy(InterceptionPolicy.valueOf(configurationInterceptionPolicy));
                    } catch (IllegalArgumentException ex) {

                        LocalizedLogger.severe(JsonContextProvider.class.getName(), "context_error", ex.getMessage());
                    }
                }


                try {

                    List<String> excludedMethodsList = (List<String>) dynamicMap.get(DYNAMIC_EXCLUDE_PROP);

                    if(excludedMethodsList!=null){
                        for (String excludedMethod : excludedMethodsList) {
                            contextEntryExcludedMethodDiscovered(contextEntry.getDynamicClass(), excludedMethod);
                        }
                    }

                } catch (ClassCastException ex) {
                    
                    LocalizedLogger.warn(JsonContextProvider.class.getName(), "context_property_array", DYNAMIC_EXCLUDE_PROP);
                }

            }
        } else {

            LocalizedLogger.severe(JsonContextProvider.class.getName(), "context_property_required", "class");
        }






        return contextEntry;
    }
}
