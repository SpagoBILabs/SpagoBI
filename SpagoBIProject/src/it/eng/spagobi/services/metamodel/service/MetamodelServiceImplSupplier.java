/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.services.metamodel.service;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.catalogue.bo.Content;
import it.eng.spagobi.tools.catalogue.dao.IArtifactsDAO;
import it.eng.spagobi.tools.catalogue.dao.IMetaModelsDAO;

import javax.activation.DataHandler;

import org.apache.axis.attachments.ManagedMemoryDataSource;
import org.apache.log4j.Logger;

public class MetamodelServiceImplSupplier {
    static private Logger logger = Logger.getLogger(MetamodelServiceImplSupplier.class);

    /**
	 * return the metamodel by the name
	 * @param token. The token.
	 * @param user. The user.
	 * @param id. The metamodel's name.
	 * @return the content of the metamodel.
	 */
    public DataHandler getMetamodelContentByName(String name){
    	logger.debug("IN.name:" + name);
    	DataHandler toReturn;
    	
    	if (name == null)
    	    return null;

    	// gets artifact metamodel from database
		try {
			IMetaModelsDAO metamodelsDAO = DAOFactory.getMetaModelsDAO();
			Content content = metamodelsDAO.loadActiveMetaModelContentByName(name);
			byte[] cont = content.getContent();
			ManagedMemoryDataSource mods =  new ManagedMemoryDataSource(new java.io.ByteArrayInputStream(cont), Integer.MAX_VALUE - 2,
			null, true);
			toReturn = new DataHandler(mods);		
			return toReturn;
		} catch (Exception e) {
			logger.error("The metamodel is not correctly returned", e);
			return null;
		} finally {
			logger.debug("OUT");
		}				
    }
}
