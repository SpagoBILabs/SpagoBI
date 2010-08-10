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
package it.eng.spagobi.engines.dossier;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.InternalEngineIFace;
import it.eng.spagobi.engines.dossier.constants.DossierConstants;
import it.eng.spagobi.engines.dossier.dao.IDossierPresentationsDAO;
import it.eng.spagobi.engines.drivers.exceptions.InvalidOperationRequest;

import java.util.List;

import org.apache.log4j.Logger;

public class SpagoBIDossierInternalEngine implements InternalEngineIFace {

	public static final String messageBundle = "component_dossier_messages";
	
	static private Logger logger = Logger.getLogger(SpagoBIDossierInternalEngine.class);
	
	/**
	 * Executes the document and populates the response.
	 * 
	 * @param requestContainer The <code>RequestContainer</code> object (the session can be retrieved from this object)
	 * @param response The response <code>SourceBean</code> to be populated
	 * @param biobj the biobj
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void execute(RequestContainer requestContainer, BIObject biobj, SourceBean response) throws EMFUserError {
		logger.debug("IN");
		try {
			IDossierPresentationsDAO dpDao = DAOFactory.getDossierPresentationDAO();
			List presVersions = dpDao.getPresentationVersions(biobj.getId());
			response.setAttribute(DossierConstants.PUBLISHER_NAME, "DossierPresentationVersion");
			response.setAttribute(DossierConstants.DOSSIER_PRESENTATION_VERSIONS, presVersions);
			response.setAttribute(DossierConstants.DOSSIER_ID, biobj.getId().toString());
		} catch (Exception e) {
			logger.error("error while setting response attribute " + e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			logger.debug("OUT");
		}
	}
	
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
	public void executeSubObject(RequestContainer requestContainer, BIObject obj, SourceBean response, Object subObjectInfo) throws EMFUserError {
		logger.error("Method not implemented");
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.InternalEngineIFace#handleDocumentTemplateEdit(it.eng.spago.base.RequestContainer, it.eng.spagobi.analiticalmodel.document.bo.BIObject, it.eng.spago.base.SourceBean)
	 */
	public void handleDocumentTemplateEdit(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError, InvalidOperationRequest {
		try {
			response.setAttribute(DossierConstants.PUBLISHER_NAME, "DossierManagementLoopCall");
			response.setAttribute(SpagoBIConstants.OPERATION, SpagoBIConstants.EDIT_DOCUMENT_TEMPLATE);
			response.setAttribute(SpagoBIConstants.OBJECT_ID, obj.getId().toString());
		} catch (Exception e) {
			logger.error("error while setting response attribute " + e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.InternalEngineIFace#handleNewDocumentTemplateCreation(it.eng.spago.base.RequestContainer, it.eng.spagobi.analiticalmodel.document.bo.BIObject, it.eng.spago.base.SourceBean)
	 */
	public void handleNewDocumentTemplateCreation(RequestContainer requestContainer, BIObject obj, SourceBean response) throws EMFUserError, InvalidOperationRequest {
		try {
			response.setAttribute(DossierConstants.PUBLISHER_NAME, "DossierManagementLoopCall");
			response.setAttribute(SpagoBIConstants.OPERATION, SpagoBIConstants.NEW_DOCUMENT_TEMPLATE);
			response.setAttribute(SpagoBIConstants.OBJECT_ID, obj.getId().toString());
		} catch (Exception e) {
			logger.error("error while setting response attribute " + e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	

}
