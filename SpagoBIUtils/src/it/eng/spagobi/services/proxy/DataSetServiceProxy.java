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
