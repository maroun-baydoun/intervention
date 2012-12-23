package com.mb.intervention.context;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
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
                HashMap<String, Object> map = gson.fromJson(jsonBufferedReader, HashMap.class);

                Context.Configuration configuration=buildConfigurationFromMap(map);
                
                Object dynamicLanguage,scriptExtension,defualtScript,interceptionPolicy,preInvokeFunction,postInvokeFunction;
                
               
                
            }
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
        }
        
         catch (JsonIOException ex) {
            ex.printStackTrace();
        }
        

    }
    
    private static  Context.Configuration buildConfigurationFromMap(HashMap<String,Object> map){
        
        Context.Configuration configuration=new Context.Configuration();
        
        
        Set<String> keys=map.keySet();
        
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if(value instanceof java.lang.String){
                
            }
        }
        
        return configuration;
    }
}
