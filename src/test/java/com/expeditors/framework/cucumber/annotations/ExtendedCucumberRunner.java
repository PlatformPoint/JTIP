package com.expeditors.framework.cucumber.annotations;

/**
 * Created by chq-gurmits on 4/28/2016.
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.expeditors.framework.cucumber.annotations.BeforeSuite;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import com.expeditors.framework.cucumber.annotations.AfterSuite;

import cucumber.api.junit.Cucumber;

public class ExtendedCucumberRunner extends Runner {

    private Class clazz;
    private Cucumber cucumber;

    public ExtendedCucumberRunner(Class clazzValue) throws Exception {
        clazz = clazzValue;
        cucumber = new Cucumber(clazzValue);
    }

    @Override
    public Description getDescription() {
        return cucumber.getDescription();
    }

    private void runPredefinedMethods(Class annotation) throws Exception {
        if (!annotation.isAnnotation()) {
            return;
        }
        Method[] methodList = this.clazz.getMethods();
        for (Method method : methodList) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation item : annotations) {
                if (item.annotationType().equals(annotation)) {
                    method.invoke(null);
                    break;
                }
            }
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            runPredefinedMethods(BeforeSuite.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cucumber.run(notifier);
        try {
            runPredefinedMethods(AfterSuite.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}