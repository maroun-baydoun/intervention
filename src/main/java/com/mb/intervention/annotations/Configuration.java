
package com.mb.intervention.annotations;

import com.mb.intervention.context.InterceptionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {

    public String defaultScript() default "default";
    public String scriptLocation() default "local";
    public String dynamicLanguage() default "JavaScript";
    public String scriptExtension() default "js";
    
    
    public String preInvokeFunction() default "preInvoke";
    public String postInvokeFunction() default "postInvoke";
    public InterceptionPolicy interceptionPolicy() default InterceptionPolicy.ALL;
    
   
   
    
}
