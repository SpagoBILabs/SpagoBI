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

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class ExecuteMobileDocumentAction extends AbstractSpagoBIAction {	

	private static final long serialVersionUID = -349776903181827582L;

	public void doService() {

		try{
			//Load the BIObject
			BIObject documentBIObject = (BIObject)getAttributeFromSession(MobileConstants.DOCUMENT_BI_OBJECT);
			
			//Load the template of the document
			ObjTemplate objTemp = documentBIObject.getActiveTemplate();	
			
			//Load the dataset
			Integer id = documentBIObject.getDataSetId();
			IDataSet dataset = DAOFactory.getDataSetDAO().loadActiveIDataSetByID(id);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}