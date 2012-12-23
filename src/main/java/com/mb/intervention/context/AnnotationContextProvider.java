package com.mb.intervention.context;

import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.listener.ClassAnnotationDiscoveryListener;
import com.impetus.annovention.listener.MethodAnnotationDiscoveryListener;
import com.mb.intervention.annotations.Configuration;
import com.mb.intervention.annotations.Dynamic;
import com.mb.intervention.annotations.Exclude;
import com.mb.intervention.log.MyLogger;
import java.io.IOException;
import java.util.Properties;

public class AnnotationContextProvider extends ContextProvider {

    private ClassAnnotationScannerListener classAnnotationDiscoveryListener;
    private MethodAnnotationDiscoveryListener methodAnnotationDiscoveryListener;

    public AnnotationContextProvider() {

        classAnnotationDiscoveryListener = new ClassAnnotationScannerListener();
        methodAnnotationDiscoveryListener = new MethodAnnotationScannerListener();
    }

    @Override
    public Context getContext() {

        if (context.getConfiguration() == null) {
            context.setConfiguration(createDefaultConfiguration());
        }


        return super.getContext();
    }

    private static Context.Configuration createDefaultConfiguration() {

        Context.Configuration configuration = null;
        
        try {
            Properties properties = new Properties();
            properties.load(AnnotationContextProvider.class.getResourceAsStream("../defaults.properties"));
            
            configuration=new Context.Configuration();
            configuration.setDefaultScript(properties.getProperty("defaultScript"));
            configuration.setDynamicLanguage(properties.getProperty("dynamicLanguage"));
            configuration.setScriptExtension(properties.getProperty("scriptExtension"));
            configuration.setPostInvokeFunction(properties.getProperty("postInvokeFunction"));
            configuration.setPreInvokeFunction(properties.getProperty("preInvokeFunction"));
            configuration.setInterceptionPolicy(InterceptionPolicy.valueOf(properties.getProperty("interceptionPolicy").toUpperCase()));
            
            
        } catch (IOException ex) {
            MyLogger.severe(AnnotationContextProvider.class.getName(), "exception_occurred", ex.getMessage());
        }
        
        return configuration;
    }

    @Override
    public void build() {

        ClasspathDiscoverer discoverer = new ClasspathDiscoverer();
        discoverer.addAnnotationListener(classAnnotationDiscoveryListener);
        discoverer.addAnnotationListener(methodAnnotationDiscoveryListener);
        discoverer.discover(true, false, true, true, false);



    }

    private final class ClassAnnotationScannerListener implements ClassAnnotationDiscoveryListener {

        @Override
        public void discovered(String className, String annotationName) {

            if (annotationName.equals(Dynamic.class.getName())) {

                try {

                    Class dynamicClass = Class.forName(className);
                    Dynamic dynamicAnnotation = (Dynamic) dynamicClass.getAnnotation(Dynamic.class);
                    String dynamicClassKey = dynamicAnnotation.id().length() > 0 ? dynamicAnnotation.id() : className;
                    String script = dynamicAnnotation.script().length() > 0 ? dynamicAnnotation.script() : className;


                    if (!context.containsContextEntry(dynamicClassKey)) {

                        Context.ContextEntry contextEntry = new Context.ContextEntry();
                        contextEntry.setDynamicClass(dynamicClass);
                        contextEntry.setDynamicClassId(dynamicClassKey);
                        contextEntry.setInterceptionPolicy(dynamicAnnotation.interceptionPolicy());
                        contextEntry.setScript(script);

                        context.addContextEntry(contextEntry);

                    } else {
                        MyLogger.severe(className, "dynamic_class_id_already_registered", dynamicClassKey);
                    }

                } catch (ClassNotFoundException ex) {
                    MyLogger.severe(className, "exception_occurred", ex);

                }
            } else if (annotationName.equals(Configuration.class.getName())) {

                try {

                    if (context.getConfiguration() == null) {

                        Class classWithConfiguration = Class.forName(className);
                        Configuration configurationAnnotation = (Configuration) classWithConfiguration.getAnnotation(Configuration.class);

                        Context.Configuration configuration = new Context.Configuration();
                        configuration.setDefaultScript(configurationAnnotation.defaultScript());
                        configuration.setDynamicLanguage(configurationAnnotation.dynamicLanguage());
                        configuration.setScriptExtension(configurationAnnotation.scriptExtension());
                        configuration.setPostInvokeFunction(configurationAnnotation.postInvokeFunction());
                        configuration.setPreInvokeFunction(configurationAnnotation.preInvokeFunction());
                        configuration.setInterceptionPolicy(configurationAnnotation.interceptionPolicy());

                        context.setConfiguration(configuration);
                    } else {
                        MyLogger.warn(className, "configuration_already_defined");
                    }

                } catch (ClassNotFoundException ex) {
                    MyLogger.severe(className, "exception_occurred", ex);

                }
            }

        }

        @Override
        public String[] supportedAnnotations() {
            return new String[]{Dynamic.class.getName(), Configuration.class.getName()};
        }
    }

    private final class MethodAnnotationScannerListener implements MethodAnnotationDiscoveryListener {

        public void discovered(String className, String methodName, String annotationName) {
            try {
                Class dynamicClass = Class.forName(className);

                context.addExcludedMethod(dynamicClass, methodName);

            } catch (ClassNotFoundException ex) {
                MyLogger.severe(className, "exception_occurred", ex);
            }
        }

        public String[] supportedAnnotations() {
            return new String[]{Exclude.class.getName()};
        }
    }
}
