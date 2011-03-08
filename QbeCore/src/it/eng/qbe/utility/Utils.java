/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.utility;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;


// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {

	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(Utils.class);
	
	
	
	/**
	 * Update the QBE_LAST_UPDATE_TIMESTAMP in session container.
	 * 
	 * @param reqContainer the req container
	 */
	public static void updateLastUpdateTimeStamp(RequestContainer reqContainer){
			String str = String.valueOf(System.currentTimeMillis());
			logger.debug("Last Update Timestamp ["+str+"]");
			reqContainer.getSessionContainer().setAttribute("QBE_LAST_UPDATE_TIMESTAMP", str);
	}
		
	
	/**
	 * Hash m d5.
	 * 
	 * @param original the original
	 * 
	 * @return the string
	 * 
	 * @throws EMFInternalError the EMF internal error
	 */
	public static String hashMD5(String original) throws EMFInternalError {
		 byte[] stringByteArray = new byte[original.length()];
		 try {
			 stringByteArray = original.getBytes("UTF-8");
	        } // try
	        catch (UnsupportedEncodingException uee) {
	            throw new EMFInternalError(EMFErrorSeverity.ERROR, "Autenticazione fallita", uee);
	        } // catch (UnsupportedEncodingException uee)
	        MessageDigest algorithm = null;
	        try {
	            algorithm = MessageDigest.getInstance("SHA-1");
	        } // try
	        catch (NoSuchAlgorithmException nsae) {
	            throw new EMFInternalError(EMFErrorSeverity.ERROR, "Autenticazione fallita", nsae);
	        } // catch (NoSuchAlgorithmException nsae)
	        algorithm.reset();
	        algorithm.update(stringByteArray);
	        byte[] digestedString = algorithm.digest();
	        return new BASE64Encoder().encodeBuffer(digestedString);
	}
        
   
	/**
	 * Checks if is user able.
	 * 
	 * @param userProfile the user profile
	 * @param func the func
	 * 
	 * @return true, if is user able
	 */
	public static boolean isUserAble(IEngUserProfile userProfile, String func){
		try{
			Collection userFuncs = userProfile.getFunctionalities();
			return userFuncs.contains(func) || userFuncs.contains(func.toUpperCase());
		}catch (EMFInternalError e) {
			return false;
		}
	}
   



	
	
	
	
	
	

	
	/**
	 * As java class identifier.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the string
	 */
	public static String asJavaClassIdentifier(String identifier){
		return capitalize(asJavaIdentifier(identifier));
	}
	
	/**
	 * As java property identifier.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the string
	 */
	public static String asJavaPropertyIdentifier(String identifier){
		return unCapitalize(asJavaIdentifier(identifier));
	}
	
	/**
	 * As java identifier.
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the string
	 */
	public static String asJavaIdentifier(String identifier) {
		
		StringBuffer sb = new StringBuffer();
		String originalIdentifier = identifier;
		
		if (identifier.equalsIgnoreCase(identifier.toUpperCase())){
			originalIdentifier = identifier.toLowerCase();
		}
			
			StringTokenizer st = new StringTokenizer(originalIdentifier, "_ ", false);
			boolean isFirstToken = false;
			while (st.hasMoreTokens()){
				if (!isFirstToken){
					sb.append(capitalize(st.nextToken()));
				}else{
					sb.append(st.nextToken());
					isFirstToken = true;
				}
			}	
			
		return sb.toString();
	}
	
	/**
	 * Capitalize.
	 * 
	 * @param value the value
	 * 
	 * @return the string
	 */
	public static String capitalize(String value) {
        if (value == null) {
            return null;
        }

        java.util.StringTokenizer tokenizer = new StringTokenizer(value, " ");
        StringBuffer result = new StringBuffer();

        while (tokenizer.hasMoreTokens()) {
            StringBuffer word = new StringBuffer(tokenizer.nextToken());

            // upper case first character
            word.replace(0, 1, word.substring(0, 1).toUpperCase());

            if (!tokenizer.hasMoreTokens()) {
                result.append(word);
            } else {
                result.append(word + " ");
            }
        }

        return result.toString();
    }
	
	/**
	 * Un capitalize.
	 * 
	 * @param value the value
	 * 
	 * @return the string
	 */
	public static String unCapitalize(String value) {
        if (value == null) {
            return null;
        }

        java.util.StringTokenizer tokenizer = new StringTokenizer(value, " ");
        StringBuffer result = new StringBuffer();

        while (tokenizer.hasMoreTokens()) {
            StringBuffer word = new StringBuffer(tokenizer.nextToken());

            // upper case first character
            word.replace(0, 1, word.substring(0, 1).toLowerCase());

            if (!tokenizer.hasMoreTokens()) {
                result.append(word);
            } else {
                result.append(word + " ");
            }
        }

        return result.toString();
    }
	
	/**
	 * Package as dir.
	 * 
	 * @param packageName the package name
	 * 
	 * @return the string
	 */
	public static String packageAsDir(String packageName){
		String dir = packageName.replace('.', File.separatorChar);
		return dir;
	}
	
	//	 Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    /**
	 * Delete dir.
	 * 
	 * @param dir the dir
	 * 
	 * @return true, if successful
	 */
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    
}
