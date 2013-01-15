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
import com.mb.intervention.exceptions.InterventionException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonContextProvider extends ContextProvider {

    private Reader jsonContextReader;
    private static final String CONFIG_PROP = "config";
    private static final String DYNAMIC_PROP = "dynamic";
    private static final String DYNAMIC_CLASS_PROP = "class";
    private static final String DYNAMIC_ID_PROP = "id";
    private static final String DYNAMIC_SCRIPT_PROP = "script";
    private static final String DYNAMIC_INTERCEPTION_POLICY_PROP = "interceptionPolicy";
    private static final String DYNAMIC_EXCLUDE_PROP = "exclude";


    public JsonContextProvider(Reader jsonReader) {
        this.jsonContextReader = jsonReader;
    }

    @Override
    public void build() throws InterventionException{
        
        try {           

            Gson gson = new Gson();
            Map<String, Object> contextMap = gson.fromJson(jsonContextReader, Map.class);

            Map<String, String> configurationMap = (Map<String, String>) contextMap.get(CONFIG_PROP);

            if (configurationMap != null) {

                Context.Configuration configuration = buildConfigurationFromMap(configurationMap);

                configurationDiscovered(configuration);
            }

            ArrayList<Map<String, Object>> dynamicList = (ArrayList<Map<String, Object>>) contextMap.get(DYNAMIC_PROP);

            if (dynamicList != null) {
                for (Map<String, Object> dynamicMap : dynamicList) {
                    Context.ContextEntry contextEntry = buildContextEntryFromMap(dynamicMap);

                    if (contextEntry != null) {

                        contextEntryDiscovered(contextEntry);
                    }
                }
            }

        } catch (JsonSyntaxException ex) {

            throw new InterventionException("context_error", ex, ex.getLocalizedMessage());

        } catch (JsonIOException ex) {
            throw new InterventionException("exception_occurred", ex, ex.getLocalizedMessage());
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

                throw new InterventionException("context_error", ex, ex.getLocalizedMessage());
                
            }
        }

        return configuration;
    }

    private Context.ContextEntry buildContextEntryFromMap(Map<String, Object> dynamicMap) throws InterventionException{

        Context.ContextEntry contextEntry = null;

        if (dynamicMap.containsKey(DYNAMIC_CLASS_PROP)) {
            String dynamicClassName = dynamicMap.get(DYNAMIC_CLASS_PROP).toString();
            try {
                Class<?> dynamiClass = Class.forName(dynamicClassName);

                contextEntry = new Context.ContextEntry();
                contextEntry.setDynamicClass(dynamiClass);

            } catch (ClassNotFoundException ex) {
                
                throw new InterventionException("class_not_found", ex, dynamicClassName);
                
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

                        throw new InterventionException("context_error", ex,ex.getLocalizedMessage());
                    }
                }


                try {

                    List<String> excludedMethodsList = (List<String>) dynamicMap.get(DYNAMIC_EXCLUDE_PROP);

                    if (excludedMethodsList != null) {
                        for (String method : excludedMethodsList) {
                            contextEntry.addExcludedMethod(method);
                        }
                    }

                } catch (ClassCastException ex) {
                    throw new InterventionException("context_property_array", ex,DYNAMIC_EXCLUDE_PROP);
                }

            }
        } else {
            throw new InterventionException("context_property_required","class");
        }

        return contextEntry;
    }
}
