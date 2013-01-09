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
package com.mb.intervention.annotations;

import com.mb.intervention.context.InterceptionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an annotation-based global configuration
 *
 * @author Maroun Baydoun <maroun.baydoun@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {

    /**
     * The default script file name
     */
    public String defaultScript() default "";

    /**
     * The location of the script files.
     *
     */
    public String scriptLocation() default "";

    /**
     * The programming language of the script files.
     */
    public String dynamicLanguage() default "";

    /**
     * The extension of the script files.
     */
    public String scriptExtension() default "";

    /**
     * The name of the function in the script files that will be invoked before invoking the Java methods.
     */
    public String preInvokeFunction() default "";

    /**
     * The name of the function in the script files that will be invoked after invoking the Java methods.
     */
    public String postInvokeFunction() default "";

    /**
     * The global {@link InterceptionPolicy} for all dynamic classes. Defaults to {@code InterceptionPolicy.ALL}
     */
    public InterceptionPolicy interceptionPolicy() default InterceptionPolicy.ALL;
}
