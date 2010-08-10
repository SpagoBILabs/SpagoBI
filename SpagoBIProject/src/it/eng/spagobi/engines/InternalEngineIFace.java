/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/

package it.eng.spagobi.engines;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;


/**
 * Defines the interface for internal engines.
 * 
 * @author Zerbetto
 */
public interface InternalEngineIFace {
	
	/**
	 * Executes the document and populates the response.
	 * 
	 * @param requestContainer The <code>RequestContainer</code> object (the session can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document to be executed
	 * @param response The response <code>SourceBean</code> to be populated
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void execute(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError;

	/**
	 * Executes the subobject of the document and populates the response.
	 * 
	 * @param requestContainer The <code>RequestContainer</code> object (the session can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document
	 * @param response The response <code>SourceBean</code> to be populated
	 * @param subObjectInfo An object describing the subobject to be executed
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void executeSubObject(RequestContainer requestContainer, BIObject obj, SourceBean response, Object subObjectInfo) throws EMFUserError;
	
	/**
	 * Handles the request for the creation of a new document template.
	 * 
	 * @param requestContainer The <code>RequestContainer</code> object (the session can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document
	 * @param response The response <code>SourceBean</code> to be populated
	 * 
	 * @throws EMFUserError the EMF user error
	 * @throws InvalidOperationRequest the invalid operation request
	 */
	public void handleNewDocumentTemplateCreation(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError, InvalidOperationRequest;
	
	/**
	 * Handles the request for the modification of the current document template.
	 * 
	 * @param requestContainer The <code>RequestContainer</code> object (the session can be retrieved from this object)
	 * @param obj The <code>BIObject</code> representing the document
	 * @param response The response <code>SourceBean</code> to be populated
	 * 
	 * @throws EMFUserError the EMF user error
	 * @throws InvalidOperationRequest the invalid operation request
	 */
	public void handleDocumentTemplateEdit(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError, InvalidOperationRequest;
	
}
