
package com.mb.intervention.log;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class MyLogger {

    public static void log(String className, Level level, String message, Object... params) {
   
        Logger.getLogger(className, "com.mb.intervention.messages").log(level, message, params);
    }

    public static void warn(String className, String message, Object... params) {

        log(className, Level.WARNING, message, params);
    }

    public static void severe(String className, String message, Object... params) {

        log(className, Level.SEVERE, message, params);
    }

    public static void info(String className, String message, Object... params) {

        log(className, Level.INFO, message, params);
    }
}
