package com.expeditors.framework.cucumber.annotations;

/**
 * Created by chq-gurmits on 4/28/2016.
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD })
public @interface BeforeSuite {

}