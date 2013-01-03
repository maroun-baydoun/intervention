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
package com.mb.intervention.context;

import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.listener.ClassAnnotationDiscoveryListener;
import com.impetus.annovention.listener.MethodAnnotationDiscoveryListener;
import com.mb.intervention.annotations.Configuration;
import com.mb.intervention.annotations.Dynamic;
import com.mb.intervention.annotations.Exclude;
import com.mb.intervention.log.LocalizedLogger;

public class AnnotationContextProvider extends ContextProvider {

    private ClassAnnotationScannerListener classAnnotationDiscoveryListener;
    private MethodAnnotationDiscoveryListener methodAnnotationDiscoveryListener;

    public AnnotationContextProvider() {

        classAnnotationDiscoveryListener = new ClassAnnotationScannerListener();
        methodAnnotationDiscoveryListener = new MethodAnnotationScannerListener();
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


                    Context.ContextEntry contextEntry = new Context.ContextEntry();
                    contextEntry.setDynamicClass(dynamicClass);
                    contextEntry.setDynamicClassId(dynamicAnnotation.id());
                    contextEntry.setInterceptionPolicy(dynamicAnnotation.interceptionPolicy());
                    contextEntry.setScript(dynamicAnnotation.script());

                    contextEntryDiscovered(contextEntry);

                } catch (ClassNotFoundException ex) {
                    LocalizedLogger.severe(className, "exception_occurred", ex);

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
                        configuration.setScriptLocation(configurationAnnotation.scriptLocation());

                        configurationDiscovered(configuration);

                    } else {
                        LocalizedLogger.warn(className, "configuration_already_defined");
                    }

                } catch (ClassNotFoundException ex) {
                    LocalizedLogger.severe(className, "exception_occurred", ex);

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

               contextEntryExcludedMethodDiscovered(dynamicClass, methodName);
                
            } catch (ClassNotFoundException ex) {
                LocalizedLogger.severe(className, "exception_occurred", ex);
            }
        }

        public String[] supportedAnnotations() {
            return new String[]{Exclude.class.getName()};
        }
    }
}
