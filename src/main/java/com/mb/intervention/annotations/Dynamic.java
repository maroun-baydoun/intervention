
package com.mb.intervention.annotations;

import com.mb.intervention.context.InterceptionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dynamic {

    public String id() default "";
    public String script() default "";
    public String preInvoke() default "";
    public String postInvoke() default "";
    public InterceptionPolicy interceptionPolicy() default InterceptionPolicy.UNSPECIFIED;
}
