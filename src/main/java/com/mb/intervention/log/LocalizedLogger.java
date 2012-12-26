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

package com.mb.intervention.log;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class LocalizedLogger {

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
