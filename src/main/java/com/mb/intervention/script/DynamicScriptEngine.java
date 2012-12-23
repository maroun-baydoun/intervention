package com.mb.intervention.script;

import com.mb.intervention.ObjectFactory;
import com.mb.intervention.log.MyLogger;
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
            InputStream scriptStream = getClass().getResourceAsStream("/" + scriptName +"."+SCRIPT_EXTENSION);

            if (scriptStream != null) {
                Reader scriptReader = new InputStreamReader(scriptStream);

                script = compilableEngine.compile(scriptReader);
                compiledScripts.put(scriptName, script);

            } else {
                if (!scriptName.equals(DEFAULT_SCRIPT)) {
                    MyLogger.severe(DynamicScriptEngine.class.getName(), "script_file_not_found", scriptName);
                }

            }
        }

        return script;
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
            MyLogger.severe(DynamicScriptEngine.class.getName(), "script_exception_occurred", ex.getMessage(), scriptName);
        }

        return scriptOk;

    }

    public Object invoke(String functionName, Object... args) {

        Object functionReturn = null;

        try {
            functionReturn = invocableEngine.invokeFunction(functionName, args);
        } catch (ScriptException ex) {
            MyLogger.severe(DynamicScriptEngine.class.getName(), "script_exception_occurred", ex.getMessage(), "");
        } catch (NoSuchMethodException ex) {
            MyLogger.severe(DynamicScriptEngine.class.getName(), "script_function_not_found", functionName);
        }

        return functionReturn;
    }
}
