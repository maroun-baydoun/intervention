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

package com.mb.intervention.script;

import com.mb.intervention.ObjectFactory;
import com.mb.intervention.context.Context;
import com.mb.intervention.log.LocalizedLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.script.*;

public class DynamicScriptEngine {

    private static final String DYNAMIC_LANGUAGE=ObjectFactory.getInstance().getContext().getConfiguration().getDynamicLanguage();
    private static final String SCRIPT_EXTENSION=ObjectFactory.getInstance().getContext().getConfiguration().getScriptExtension();
    private static final String DEFAULT_SCRIPT=ObjectFactory.getInstance().getContext().getConfiguration().getDefaultScript();
    private static final String SCRIPT_LOCATION=ObjectFactory.getInstance().getContext().getConfiguration().getScriptLocation();
    private static final String SCRIPT_LOCATION_TYPE=ObjectFactory.getInstance().getContext().getConfiguration().getScriptLocationType();
    
    
    private static DynamicScriptEngine instance;
    private static DynamicScriptEngine defaultInstance;
    private ScriptEngine engine;
    private Invocable invocableEngine;
    private Compilable compilableEngine;
    private Map<String, CompiledScript> compiledScripts;
    
    

    private DynamicScriptEngine(String language) {

        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName(language);
        invocableEngine = (Invocable) engine;
        compilableEngine = (Compilable) engine;
        compiledScripts = new HashMap<String, CompiledScript>();

    }

    public static DynamicScriptEngine getInstance() {

        if (instance == null) {
            instance = new DynamicScriptEngine(DYNAMIC_LANGUAGE);
        }
        return instance;
    }

    public static DynamicScriptEngine getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new DynamicScriptEngine(DYNAMIC_LANGUAGE);
        }

        return defaultInstance;
    }

    private CompiledScript getCompiledScript(String scriptName) throws ScriptException {

        CompiledScript script = compiledScripts.get(scriptName);

        if (script == null) {
            InputStream scriptStream =getScriptAsStream(scriptName);
            
            if (scriptStream != null) {
                Reader scriptReader = new InputStreamReader(scriptStream);

                script = compilableEngine.compile(scriptReader);
                compiledScripts.put(scriptName, script);

            } else {
                if (!scriptName.equals(DEFAULT_SCRIPT)) {
                    LocalizedLogger.severe(DynamicScriptEngine.class.getName(), "script_file_not_found", scriptName,SCRIPT_EXTENSION,SCRIPT_LOCATION);
                }

            }
        }

        return script;
    }

    private static InputStream getScriptAsStream(String scriptName){
     
        InputStream stream=null;
        
       
        if(SCRIPT_LOCATION_TYPE.equals(Context.Configuration.SCRIPT_LOCATION_CLASSPATH)){
            
            stream=DynamicScriptEngine.class.getResourceAsStream(SCRIPT_LOCATION+""+scriptName+"."+SCRIPT_EXTENSION);
        }
        else if(SCRIPT_LOCATION_TYPE.equals(Context.Configuration.SCRIPT_LOCATION_FOLDER)){
            
            try{
                stream=new FileInputStream(SCRIPT_LOCATION+""+scriptName+"."+SCRIPT_EXTENSION);
            }
            catch(FileNotFoundException ex){
                
            }
        }
        
        
        
        return stream;
    }
    
    public boolean evalScript(String scriptName) {


        boolean scriptOk = false;
        try {
            CompiledScript script = getCompiledScript(scriptName);
            if (script != null) {
                
                script.eval();
                
                scriptOk = true;

            }
        } catch (ScriptException ex) {
            scriptOk = false;
            LocalizedLogger.severe(DynamicScriptEngine.class.getName(), "script_exception_occurred", ex.getMessage(), scriptName);
        }

        return scriptOk;

    }

    public Object invoke(String functionName, Object... args) {

        Object functionReturn = null;

        try {
            functionReturn = invocableEngine.invokeFunction(functionName, args);
        } catch (ScriptException ex) {
            LocalizedLogger.severe(DynamicScriptEngine.class.getName(), "script_exception_occurred", ex.getMessage(), "");
        } catch (NoSuchMethodException ex) {
            LocalizedLogger.severe(DynamicScriptEngine.class.getName(), "script_function_not_found", functionName);
        }

        return functionReturn;
    }
}
