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
 * Represents the different policies for determining which Java methods will be intercepted by the dynamic script.
 * 
 * @author Maroun Baydoun <maroun.baydoun@gmail.com>
 */

public enum InterceptionPolicy {
 
    /**
     * All methods will be intercepted.
     */
    ALL,
    
    /**
     * No methods will be intercepted.
     */
    NONE,
    
    /**
     * Only getter methods will be intercepted.
     */
    GETTERS,
    
    /**
     * Only setter methods will be intercepted
     */
    SETTERS,
    
    /**
     * Only getter and setter methods will be intercepted
     */
    GETTERS_SETTERS,
    
    /**
     * No policy is specified. Defaults to {@code InterceptionPolicy.ALL} when no global InterceptionPolicy is specified.
     */
    UNSPECIFIED;

    
}
