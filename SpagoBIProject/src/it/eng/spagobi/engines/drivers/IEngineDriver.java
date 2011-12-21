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

package it.eng.spagobi.engines.drivers;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;

import java.util.Map;



/**
 * Defines the methods implements by the SpagoBI drivers that, 
 * starting from a SpagoBI BIOBject, produce the parameters for a 
 * specific engine to which they are associated. The names anv values of the map parameters 
 * will be used by the system to produce a POST request to the engine application.
 * Each driver can extract and trasform the BIParameter of the BIObject in order to create a 
 * a right request based on the engine specificaion.
 * The methods can be used also to do some setting operation like for example handshake 
 * security requests.    
 */
public interface IEngineDriver {

    
	/**
	 * Returns a map of parameters which will be send in the request to the
	 * engine application.
	 * 
	 * @param profile Profile of the user
	 * @param roleName the name of the execution role
	 * @param biobject the biobject
	 * 
	 * @return Map The map of the execution call parameters
	 */
    public Map getParameterMap(Object biobject, IEngUserProfile profile, String roleName);
	
    /**
     * Returns a map of parameters which will be send in the request to the
     * engine application.
     * 
     * @param subObject SubObject to execute
     * @param profile Profile of the user
     * @param roleName the name of the execution role
     * @param object the object
     * 
     * @return Map The map of the execution call parameters
     */
    public Map getParameterMap(Object object, Object subObject, IEngUserProfile profile, String roleName);  
    
    /**
     * Returns the EngineURL for the creation of a new template for the document.
     * 
     * @param biobject the biobject
     * @param profile the profile
     * 
     * @return the EngineURL for the creation of a new template for the document
     * 
     * @throws InvalidOperationRequest the invalid operation request
     */
    public EngineURL getNewDocumentTemplateBuildUrl(Object biobject, IEngUserProfile profile) throws InvalidOperationRequest;
    
    /**
     * Returns the EngineURL for the modification of the document template.
     * 
     * @param biobject the biobject
     * @param profile the profile
     * 
     * @return the EngineURL for the modification of the document template
     * 
     * @throws InvalidOperationRequest the invalid operation request
     */
    public EngineURL getEditDocumentTemplateBuildUrl(Object biobject, IEngUserProfile profile) throws InvalidOperationRequest;
    
}
