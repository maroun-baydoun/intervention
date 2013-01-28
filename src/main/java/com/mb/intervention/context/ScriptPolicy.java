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
package com.mb.intervention.context;

/**
 * Represents the different policies for determining which script files will be evaluated to intercept the Java methods.
 * @author Maroun Baydoun <maroun.baydoun@gmail.com>
 */
public enum ScriptPolicy {
    /**
     * Only the default script will be evaluated.
     */
    DEFAULT_ONLY,
    
    /**
     * Only the script associated with the dynamic Java class will be evaluated.
     */
    SELF_ONLY,
    
    /**
     * Both the default and the script associated with the dynamic Java class will be evaluated.
     */
    DEFAULT_AND_SELF,
    
    /**
     * No policy specified. Defaults to {@link ScriptPolicy.DEFAULT_AND_SELF}
     */
    UNSPECIFIED
}
