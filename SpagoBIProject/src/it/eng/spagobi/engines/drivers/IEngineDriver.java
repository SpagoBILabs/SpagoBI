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
