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


import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.services.datasource.stub.DataSourceServiceServiceLocator;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.tools.datasource.bo.DataSourceFactory;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

/**
 * This is a proxy for using the Data Source Service:
 * Ex.
 * 
	DataSourceServiceProxy proxyDS=new DataSourceServiceProxy();
	SpagoBiDataSource ds=proxyDS.getDataSource("2");
	logger.debug("DS="+ds.getJndiName());

 *
 */
public final class DataSourceServiceProxy extends AbstractServiceProxy{
    
	static private final String SERVICE_NAME = "DataSource Service";
	
    static private Logger logger = Logger.getLogger(DataSourceServiceProxy.class);
    

    /**
     * The Constructor.
     * 
     * @param user User ID utente
     * @param session Sessione http
     */
    public DataSourceServiceProxy(String user,HttpSession session) {
	super(user,session);
    }

    private DataSourceServiceProxy() {
	super();
    }   
    
    private it.eng.spagobi.services.datasource.stub.DataSourceService lookUp() throws SecurityException {
	try {
	    DataSourceServiceServiceLocator locator = new DataSourceServiceServiceLocator();
	    it.eng.spagobi.services.datasource.stub.DataSourceService service=null;
	    if (serviceUrl!=null ){
		    service = locator.getDataSourceService(serviceUrl);		
	    }else {
		    service = locator.getDataSourceService();		
	    }
	    return service;
	} catch (ServiceException e) {
	    logger.error("Impossible to locate [" + SERVICE_NAME + "] at [" + serviceUrl + "]");
	    throw new SecurityException("Impossible to locate [" + SERVICE_NAME + "] at [" + serviceUrl + "]", e);
	}
    }
    
    /**
     * Return the SpagoBiDataSource object, contains the information about
     * the DWH connection.
     * 
     * @param documentId document id
     * 
     * @return SpagoBiDataSource object
     */
    public IDataSource getDataSource(String documentId) {
    	IDataSource dataSource = null;
    	SpagoBiDataSource dataSourceConfig = null;
		
    	logger.debug("IN.documentId="+documentId);
		if (documentId==null || documentId.length()==0){
		    logger.error("documentId is NULL");
		    return null;
		}	
		try {
			dataSourceConfig = lookUp().getDataSource(readTicket(), userId,documentId);
			if(dataSourceConfig != null) {
				dataSource = DataSourceFactory.getDataSource( dataSourceConfig );
			}
			
		} catch (Exception e) {
		    logger.error("Error during Service LookUp",e);
		}finally{
		    logger.debug("OUT");
		}
		return dataSource;
    }
    
    /**
     * Return the SpagoBiDataSource object, contains the information about
     * the DWH connection.
     * 
     * @param label document label
     * 
     * @return SpagoBiDataSource object
     */
    public IDataSource getDataSourceByLabel(String label) {
    	IDataSource dataSource = null;
    	SpagoBiDataSource dataSourceConfig = null;
		
    	
    	logger.debug("IN.label="+label);
		if (label==null || label.length()==0){
		    logger.error("label is NULL");
		    return null;
		}	
		try {
			dataSourceConfig = lookUp().getDataSourceByLabel(readTicket(), userId,label);
			if(dataSourceConfig != null) {
				dataSource = DataSourceFactory.getDataSource( dataSourceConfig );
			}
		} catch (Exception e) {
		    logger.error("Error during Service LookUp",e);
		}finally{
		    logger.debug("OUT");
		}
		return dataSource;
    }    

}
