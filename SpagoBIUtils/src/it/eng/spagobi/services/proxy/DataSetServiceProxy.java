/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.services.proxy;


import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.services.dataset.stub.DataSetServiceServiceLocator;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.tools.dataset.bo.DataSetFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

/**
 * 
 * Proxy of Data Set Service
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it 
 * 
 */
public final class DataSetServiceProxy extends AbstractServiceProxy{
	
	static private final String SERVICE_NAME = "DataSet Service";
	
    static private Logger logger = Logger.getLogger(DataSetServiceProxy.class);



    public DataSetServiceProxy(String user,HttpSession session) {
    	super( user,session);
    	if (user==null) logger.error("User ID IS NULL....");
    	if (session==null) logger.error("HttpSession IS NULL....");
    }
    
    private DataSetServiceProxy() {
    	super ();
    }  
    
    public DataSetServiceProxy(String user,String secureAttributes,String serviceUrlStr,String spagoBiServerURL,String token, String pass) {
    	super( user,secureAttributes,serviceUrlStr,spagoBiServerURL,token, pass);
    	if (user==null) logger.error("User ID IS NULL....");
    	if (pass==null) logger.error("pass IS NULL....");
    	if (secureAttributes==null) logger.error("secureAttributes IS NULL....");
    	if (serviceUrlStr==null) logger.error("serviceUrlStr NULL....");
    	if (spagoBiServerURL==null) logger.error("spagoBiServerURL IS NULL....");
    	if (token==null) logger.error("token IS NULL....");
    }
    
    private it.eng.spagobi.services.dataset.stub.DataSetService lookUp() throws SecurityException {
	try {
	    DataSetServiceServiceLocator locator = new DataSetServiceServiceLocator();
	    it.eng.spagobi.services.dataset.stub.DataSetService service = null;
	    if (serviceUrl!=null ){
		    service = locator.getDataSetService(serviceUrl);		
	    }else {
		    service = locator.getDataSetService();		
	    }
	    return service;
	} catch (ServiceException e) {
	    logger.error("Impossible to locate [" + SERVICE_NAME + "] at [" + serviceUrl + "]");
	    throw new SecurityException("Impossible to locate [" + SERVICE_NAME + "] at [" + serviceUrl + "]", e);
	}
    }


    public IDataSet getDataSetByLabel(String label) {
    	IDataSet dataSet = null;
    	SpagoBiDataSet dataSetConfig = null;
    	
    	logger.debug("IN.dataset.label="+label);
    	
    	if (label==null || label.length()==0){
    	    logger.error("dataset label is NULL");
    	    return null;
    	}	
    	try {
    		dataSetConfig = lookUp().getDataSetByLabel(readTicket(), userId, label);
    		if(dataSetConfig != null) {
    			dataSet = DataSetFactory.getDataSet( dataSetConfig );
    		}
    	} catch (Exception e) {
    	    logger.error("Error during Service LookUp",e);
    	}finally{
    	    logger.debug("OUT");
    	}
    	return dataSet;
    }
    
   
    public IDataSet getDataSet(String documentId) {
    	IDataSet dataSet = null;
    	SpagoBiDataSet dataSetConfig = null;
    	
    	logger.debug("IN.documentId="+documentId);
    	
    	if (documentId==null || documentId.length()==0){
    	    logger.error("documentId is NULL");
    	    return null;
    	}	
    	try {
    		dataSetConfig = lookUp().getDataSet(readTicket(), userId, documentId);
    		if(dataSetConfig != null) {
    			dataSet = DataSetFactory.getDataSet( dataSetConfig );
    		}
    	} catch (Exception e) {
    	    logger.error("Error during Service LookUp",e);
    	}finally{
    	    logger.debug("OUT");
    	}
    	return dataSet;
     }
}
