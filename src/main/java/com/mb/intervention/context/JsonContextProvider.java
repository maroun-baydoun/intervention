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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonContextProvider extends ContextProvider {

    private String jsonFile;

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
                Map<String, Object> map = gson.fromJson(jsonBufferedReader, HashMap.class);

                Map<String, String> configurationMap = (Map<String, String>) map.get("config");

                if (configurationMap != null) {
                    context.setConfiguration(buildConfigurationFromMap(configurationMap));
                }

                Object dynamicLanguage, scriptExtension, defualtScript, interceptionPolicy, preInvokeFunction, postInvokeFunction;
            }
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
        } catch (JsonIOException ex) {
            ex.printStackTrace();
        }

    }

    private static Context.Configuration buildConfigurationFromMap(Map<String, String> configurationMap) {

        Context.Configuration configuration = new Context.Configuration();

        configuration.setDefaultScript(configurationMap.get("defualtScript"));
        configuration.setDynamicLanguage(configurationMap.get("dynamicLanguage"));
        configuration.setPostInvokeFunction(configurationMap.get("postInvokeFunction"));
        configuration.setPreInvokeFunction(configurationMap.get("preInvokeFunction"));
        configuration.setScriptExtension(configurationMap.get("scriptExtension"));


        InterceptionPolicy interceptionPolicy = null;
        String configurationInterceptionPolicy = configurationMap.get("interceptionPolicy");

        if(configurationInterceptionPolicy!=null){
            
            try {

                interceptionPolicy = InterceptionPolicy.valueOf(configurationInterceptionPolicy);
            } 
            catch (IllegalArgumentException ex) {
                
                LocalizedLogger.severe(JsonContextProvider.class.getName(), "configuration_error", ex.getMessage());
            }
            
        }
        configuration.setInterceptionPolicy(interceptionPolicy);
        
        
        configuration=configuration.merge(Context.Configuration.getDefault());
 
        return configuration;
    }
}
