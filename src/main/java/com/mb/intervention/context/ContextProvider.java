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

import com.mb.intervention.log.LocalizedLogger;

public abstract class ContextProvider {

    protected Context context;

    public ContextProvider() {

        context = new Context();
    }

    public abstract void build();

    public Context getContext() {

        if (context.getConfiguration() == null) {
            context.setConfiguration(Context.Configuration.getDefault());
        }

        return context;
    }

    protected void contextEntryDiscovered(Context.ContextEntry contextEntry) {

        String dynamicClassKey = contextEntry.getDynamicClassId()!=null && contextEntry.getDynamicClassId().length() > 0 ? contextEntry.getDynamicClassId() : contextEntry.getDynamicClass().getName();


        if (!context.containsContextEntry(dynamicClassKey)) {

            String script = (contextEntry.getScript()!=null && contextEntry.getScript().length() > 0) ? contextEntry.getScript() : (contextEntry.getDynamicClassId()!=null && contextEntry.getDynamicClassId().length()>0)?contextEntry.getDynamicClassId(): contextEntry.getDynamicClass().getName();

            contextEntry.setDynamicClassId(dynamicClassKey);
            contextEntry.setScript(script);

            context.addContextEntry(contextEntry);

        } else {
            LocalizedLogger.severe(ContextProvider.class.getName(), "dynamic_class_id_already_registered", dynamicClassKey);
        }
    }

    /*protected void contextEntryExcludedMethodDiscovered(Class<?> dynamicClass,String methodName){
        
        context.addExcludedMethod(dynamicClass, methodName);
    }*/
    
    protected void configurationDiscovered(Context.Configuration configuration) {

        configuration = configuration.merge(Context.Configuration.getDefault());
        context.setConfiguration(configuration);
    }
}
