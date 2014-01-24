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

package com.mb.intervention.annotations;

import com.mb.intervention.context.InterceptionPolicy;
import com.mb.intervention.context.ScriptPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Represents a Java class whose methods can be intercepted by a dynamic language script.
 * 
 * @author @author Maroun Baydoun <maroun.baydoun@gmail.com>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dynamic {

    /**
     * The unique identifier of this class. Defaults to the fully qualified class name.
     */
    public String id() default "";
    
    /**
     * The script file name for the dynamic class, without the file extension
     */
    public String script() default "";
    
    
    /**
     * The interception policy of the dynamic class. Defaults to {@code InterceptionPolicy.UNSPECIFIED }
     * @see InterceptionPolicy
     */
    public InterceptionPolicy interceptionPolicy() default InterceptionPolicy.UNSPECIFIED;
    
    /**
     * The script policy of the dynamic class. Defaults to {@code ScriptPolicy.UNSPECIFIED}
     * @see ScriptPolicy 
     */
    public ScriptPolicy scriptPolicy() default ScriptPolicy.UNSPECIFIED;
}
