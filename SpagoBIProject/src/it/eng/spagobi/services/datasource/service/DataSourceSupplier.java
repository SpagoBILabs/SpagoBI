/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.services.datasource.service;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class DataSourceSupplier {
    static private Logger logger = Logger.getLogger(DataSourceSupplier.class);

    /**
     * Gets the data source.
     * 
     * @param documentId the document id
     * 
     * @return the data source
     */
    public SpagoBiDataSource getDataSource(String documentId) {
	logger.debug("IN.documentId:" + documentId);

	SpagoBiDataSource sbds = null;
	if (documentId == null)
	    return null;

	// gets data source data from database
	try {
	    BIObject obj = DAOFactory.getBIObjectDAO().loadBIObjectById(Integer.valueOf(documentId));
	    if (obj == null) {
		logger.error("The object with id " + documentId + " is not found on the database.");
		return null;
	    }
	    Integer dsId = null;
	    if (obj.getDataSourceId() != null) {
	    	dsId = obj.getDataSourceId();
	    	logger.debug("Using document datasource id = " + dsId);
	    } else {
	    	Engine engine = obj.getEngine();
	    	dsId = engine.getDataSourceId();
	    	logger.debug("Using document's engine datasource id = " + dsId);
	    }
    	if (dsId == null) {
    		logger.error("Data source is not configured neither for document nor for its engine.");
    		return null;
    	}
	    IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(dsId);
	    if (ds == null) {
		logger.error("The data source with id " + obj.getDataSourceId() + " is not found on the database.");
		return null;
	    }
	    
	    Domain dialectHB = DAOFactory.getDomainDAO().loadDomainById(ds.getDialectId());
	    if (ds == null) {
		logger.error("The data source with id " + obj.getDataSourceId() + " is not found on the database.");
		return null;
	    }	    
	    sbds = new SpagoBiDataSource();
	    sbds.setLabel(ds.getLabel());
	    sbds.setJndiName(ds.getJndi());
	    sbds.setUrl(ds.getUrlConnection());
	    sbds.setUser(ds.getUser());
	    sbds.setPassword(ds.getPwd());
	    sbds.setDriver(ds.getDriver());
	    sbds.setHibDialectName(dialectHB.getValueName());
//change
//	    sbds.setHibDialectClass(dialectHB.getValueDescription());
	    sbds.setHibDialectClass(dialectHB.getValueCd());
	    logger.info("read DS: Label="+sbds.getLabel()+" Jndi="+sbds.getJndiName()+" HIB="+sbds.getHibDialectClass());
	    
	    //gets dialect informations
	    IDomainDAO domaindao = DAOFactory.getDomainDAO();
	    Domain doDialect = domaindao.loadDomainById(ds.getDialectId());
	    sbds.setHibDialectClass(doDialect.getValueCd());
	    sbds.setHibDialectName(doDialect.getValueName());
	    sbds.setMultiSchema(ds.getMultiSchema());
	    sbds.setSchemaAttribute(ds.getSchemaAttribute());

	} catch (Exception e) {
	    logger.error("The data source is not correctly returned", e);
	    sbds=null;

	}
	logger.debug("OUT");
	return sbds;
    }

    /**
     * Gets the data source by label.
     * 
     * @param dsLabel the ds label
     * 
     * @return the data source by label
     */
    public SpagoBiDataSource getDataSourceByLabel(String dsLabel) {
	logger.debug("IN");
	SpagoBiDataSource sbds = new SpagoBiDataSource();

	// gets data source data from database
	try {
	    IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dsLabel);
	    if (ds == null) {
		logger.warn("The data source with label " + dsLabel + " is not found on the database.");
		return null;
	    }
	    sbds.setLabel(ds.getLabel());
	    sbds.setJndiName(ds.getJndi());
	    sbds.setUrl(ds.getUrlConnection());
	    sbds.setUser(ds.getUser());
	    sbds.setPassword(ds.getPwd());
	    sbds.setDriver(ds.getDriver());
	    sbds.setMultiSchema(ds.getMultiSchema());
	    sbds.setSchemaAttribute(ds.getSchemaAttribute());
	    
	  //gets dialect informations
	    IDomainDAO domaindao = DAOFactory.getDomainDAO();
	    Domain doDialect = domaindao.loadDomainById(ds.getDialectId());
	    sbds.setHibDialectClass(doDialect.getValueCd());
	    sbds.setHibDialectName(doDialect.getValueName());
	    
	} catch (Exception e) {
	    logger.error("The data source is not correctly returned", e);
	}
	logger.debug("OUT");
	return sbds;
    }

    /**
     * Gets the all data source.
     * 
     * @return the all data source
     */
    public SpagoBiDataSource[] getAllDataSource() {
	logger.debug("IN");
	ArrayList tmpList = new ArrayList();

	// gets all data source from database
	try {
	    List lstDs = DAOFactory.getDataSourceDAO().loadAllDataSources();
	    if (lstDs == null) {
		logger.warn("Data sources aren't found on the database.");
		return null;
	    }

	    Iterator dsIt = lstDs.iterator();
	    while (dsIt.hasNext()) {
		IDataSource ds = (IDataSource) dsIt.next();
		SpagoBiDataSource sbds = new SpagoBiDataSource();
		sbds.setJndiName(ds.getJndi());
		sbds.setUrl(ds.getUrlConnection());
		sbds.setUser(ds.getUser());
		sbds.setPassword(ds.getPwd());
		sbds.setDriver(ds.getDriver());
		//gets dialect informations
	    IDomainDAO domaindao = DAOFactory.getDomainDAO();
	    Domain doDialect = domaindao.loadDomainById(ds.getDialectId());
	    sbds.setHibDialectClass(doDialect.getValueCd());
	    sbds.setHibDialectName(doDialect.getValueName());
	    sbds.setMultiSchema(ds.getMultiSchema());
	    sbds.setSchemaAttribute(ds.getSchemaAttribute());
	    
		tmpList.add(sbds);
	    }
	} catch (Exception e) {
	    logger.error("The data sources are not correctly returned", e);
	}
	// mapping generic array list into array of SpagoBiDataSource objects
	SpagoBiDataSource[] arDS = new SpagoBiDataSource[tmpList.size()];
	for (int i = 0; i < tmpList.size(); i++) {
	    arDS[i] = (SpagoBiDataSource) tmpList.get(i);
	}
	logger.debug("OUT");
	return arDS;
    }
}
