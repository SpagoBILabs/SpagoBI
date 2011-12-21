/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.utilities;

import java.util.HashMap;

public class SessionBridge {
    
    private static HashMap data=new HashMap();
    private static SessionBridge instance=null;
    
    /**
     * Gets the single instance of SessionBridge.
     * 
     * @return single instance of SessionBridge
     */
    public static synchronized SessionBridge getInstance(){
		if (instance==null){
		    instance=new SessionBridge();
		}
		return instance;	    
    }
    
    /**
     * Put object.
     * 
     * @param key the key
     * @param obj the obj
     */
    public synchronized void putObject(String key,Object obj){
	data.put(key, obj);
    } 
    
    /**
     * Removes the object.
     * 
     * @param key the key
     * 
     * @return the object
     */
    public synchronized Object removeObject(String key){
	   Object tmp=data.get(key);
	   if (tmp!=null){
	       data.remove(key);
	   }
	   return tmp;
    }     

}
