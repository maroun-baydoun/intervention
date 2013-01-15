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
import com.mb.intervention.annotations.Configuration;
import com.mb.intervention.annotations.Dynamic;
import com.mb.intervention.annotations.Exclude;
import com.mb.intervention.exceptions.InterventionException;
import java.lang.reflect.Method;

public class AnnotationContextProvider extends ContextProvider {

    private ClassAnnotationScannerListener classAnnotationDiscoveryListener;
    

    public AnnotationContextProvider() {

        classAnnotationDiscoveryListener = new ClassAnnotationScannerListener();
        
    }

    @Override
    public void build() throws InterventionException{

        ClasspathDiscoverer discoverer = new ClasspathDiscoverer();
        discoverer.addAnnotationListener(classAnnotationDiscoveryListener);
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
                    
                    Method[] dynamicClassMethods=dynamicClass.getMethods();
                    
                    for (Method method : dynamicClassMethods) {
                        
                        if(method.isAnnotationPresent(Exclude.class)){
                            contextEntry.addExcludedMethod(method.getName());
                        }
                    }

                    contextEntryDiscovered(contextEntry);

                } catch (ClassNotFoundException ex) {
                    throw new InterventionException("class_not_found", ex, className);
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
                        throw new InterventionException("configuration_already_defined");
                    }

                } catch (ClassNotFoundException ex) {
                    throw new InterventionException("class_not_found", ex, className);
                }
            }

        }

        @Override
        public String[] supportedAnnotations() {
            return new String[]{Dynamic.class.getName(), Configuration.class.getName()};
        }
    }

  
}
