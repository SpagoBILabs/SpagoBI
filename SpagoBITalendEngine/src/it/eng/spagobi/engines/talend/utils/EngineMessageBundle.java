/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
**/
package it.eng.spagobi.engines.talend.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class EngineMessageBundle {

	private static final String DEFAULT_BUNDLE = "messages";
	private static HashMap bundles = null;
	
    static {
        bundles = new HashMap();
    }
	
    /**
     * Returns an internazionalized message.
     * 
     * @param code the code of the message.
     * @param bundle the message bundle.
     * @param userLocale the user locale
     * 
     * @return the internazionalized message.
     */
    public static String getMessage(String code, String bundle, Locale userLocale) {
        
    	if (code == null) return null;
    	if (userLocale == null) return code;
    	//logger.debug("Input parameters: code = [" + code + "] ; bundle = [" + bundle + "] ; " +
    	//		"userlocale = [" + userLocale + "]");
    	if (bundle == null || bundle.trim().equals("")) {
        //	logger.debug("Bundle not specified; considering \"" + DEFAULT_BUNDLE + "\" as default value");
    		bundle = DEFAULT_BUNDLE;
    	}
    	
        String bundleKey = bundle + "_" + userLocale.getLanguage() + "_" + userLocale.getCountry();
        ResourceBundle messages = null;
        if (bundles.containsKey(bundleKey)) {
            messages = (ResourceBundle) bundles.get(bundleKey);
        } else {
            // First access to this bundle
            try {
                messages = ResourceBundle.getBundle(bundle, userLocale);
            } catch (java.util.MissingResourceException ex) {
                //logger.error("ResourceBundle with bundle = [" + bundle + "] and locale = " +
                //		"[" + userLocale + "] missing.");
            }
            
            // Put bundle in cache
            bundles.put(bundleKey, messages);
        }
        
        if (messages == null) {
            // Bundle non existent
            return code;
        } // if (messages == null)

        String message = null;
        try {
            message = messages.getString(code);
        } // try
        catch (Exception ex) {
            // No trace: may be this is not an error
        } // catch (Exception ex)
        if (message == null) return code;
        else return message;
    }
	
    /**
     * Gets the message.
     * 
     * @param code the code
     * @param userLocale the user locale
     * 
     * @return the message
     */
    public static String getMessage(String code, Locale userLocale) {
    	return getMessage(code, DEFAULT_BUNDLE, userLocale);
    }
    
    /**
     * Gets the message.
     * 
     * @param code the code
     * @param bundle the bundle
     * @param userLocale the user locale
     * @param arguments the arguments
     * 
     * @return the message
     */
    public static String getMessage(String code, String bundle, Locale userLocale, String[] arguments) {
    	String message = getMessage(code, DEFAULT_BUNDLE, userLocale);
        for (int i = 0; i < arguments.length; i++){
        	message = replace(message, i, arguments[i].toString());
        }
    	return message;
    }
    
    /**
     * Gets the message.
     * 
     * @param code the code
     * @param userLocale the user locale
     * @param arguments the arguments
     * 
     * @return the message
     */
    public static String getMessage(String code, Locale userLocale, String[] arguments) {
    	return getMessage(code, DEFAULT_BUNDLE, userLocale, arguments);
    }
    
    /**
     * Substitutes the message value to the placeholders.
     * 
     * @param messageFormat The String representing the message format
     * @param iParameter	The numeric value defining the replacing string
     * @param value	Input object containing parsing information
     * @return	The parsed string
     */
    protected static String replace(String messageFormat, int iParameter, Object value) {
		if (value != null) {
			String toParse = messageFormat;
			String replacing = "%" + iParameter;
			String replaced = value.toString();
			StringBuffer parsed = new StringBuffer();
			int parameterIndex = toParse.indexOf(replacing);
			while (parameterIndex != -1) {
				parsed.append(toParse.substring(0, parameterIndex));
				parsed.append(replaced);
				toParse = toParse.substring(
						parameterIndex + replacing.length(), toParse.length());
				parameterIndex = toParse.indexOf(replacing);
			} // while (parameterIndex != -1)
			parsed.append(toParse);
			return parsed.toString();
		} else {
			return messageFormat;
		}
	}
	
}
