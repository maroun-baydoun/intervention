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

package com.mb.intervention.exceptions;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *Represents the base class of all the custom exceptions in Intervention
 * @author Maroun Baydoun <maroun.baydoun@gmail.com>
 */
public class InterventionException extends RuntimeException{

    private static final String MSG_BUNDLE_NAME="com.mb.intervention.messages";
    private static ResourceBundle messagesBundle=ResourceBundle.getBundle(MSG_BUNDLE_NAME);
    
    public InterventionException(String message,Object...params) {
        super(getParameterizedMessage(message, params));
        
    }

    public InterventionException(Throwable cause) {
        super(cause);
    }

    public InterventionException(String message, Throwable cause,Object...params) {
        super(getParameterizedMessage(message, params),cause);
    }
    
    private static String getParameterizedMessage(String message,Object...params){
        
        message=messagesBundle.getString(message);      
        return  MessageFormat.format(message, params);
    }
    
    
}
