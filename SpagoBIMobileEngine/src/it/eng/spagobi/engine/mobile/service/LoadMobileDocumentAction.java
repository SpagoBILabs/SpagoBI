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
package it.eng.spagobi.engine.mobile.service;

import org.json.JSONObject;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.constants.ObjectsTreeConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class LoadMobileDocumentAction extends AbstractSpagoBIAction{
	
	private static final long serialVersionUID = 1097895294216873018L;

	public void doService() {

		try{
			// recover BiObject Name
			Integer biObjectID = this.getAttributeAsInteger(ObjectsTreeConstants.OBJECT_ID);
			
			//Load the BIObject
			BIObject documentBIObject = DAOFactory.getBIObjectDAO().loadBIObjectById(biObjectID);

			//Load the template of the document
			//ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			
			setAttributeInSession(ObjectsTreeConstants.OBJECT_ID, documentBIObject);
			//setAttributeInSession(MobileConstants.DOCUMENT_TEMPLATE_OBJECT, objTemp);
			
			writeBackToClient(new JSONSuccess("userhome"));
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
