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

import it.eng.spagobi.services.common.AbstractServiceImpl;
import it.eng.spagobi.services.datasource.DataSourceService;
import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;
import it.eng.spagobi.services.security.exceptions.SecurityException;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Provide the Data Source information
 */
public class DataSourceServiceImpl extends AbstractServiceImpl implements DataSourceService {
    static private Logger logger = Logger.getLogger(DataSourceServiceImpl.class);
    private DataSourceSupplier supplier=new DataSourceSupplier();

    

    /**
     * Instantiates a new data source service impl.
     */
    public DataSourceServiceImpl(){
	super();
    }
    
    /**
     * Gets the data source.
     * 
     * @param token  String
     * @param user String
     * @param documentId String
     * 
     * @return SpagoBiDataSource
     */
    public SpagoBiDataSource getDataSource(String token,String user, String documentId) {
	logger.debug("IN");
	Monitor monitor =MonitorFactory.start("spagobi.service.datasource.getDataSource");
	try {
	    validateTicket(token, user);
	    return supplier.getDataSource(documentId);
	} catch (SecurityException e) {
	    logger.error("SecurityException", e);
	    return null;
	} finally {
	    monitor.stop();
	    logger.debug("OUT");
	}	

    }
    
    /**
     * Gets the data source by label.
     * 
     * @param token  String
     * @param user String
     * @param label String
     * 
     * @return SpagoBiDataSource
     */    
    public SpagoBiDataSource getDataSourceByLabel(String token,String user,String label){
	logger.debug("IN");
	Monitor monitor =MonitorFactory.start("spagobi.service.datasource.getDataSourceByLabel");
	try {
	    validateTicket(token, user);
	    return supplier.getDataSourceByLabel(label);
	} catch (SecurityException e) {
	    logger.error("SecurityException", e);
	    return null;
	} finally {
	    monitor.stop();
	    logger.debug("OUT");
	}	
    }
    
    /**
     * Gets the all data source.
     * 
     * @param token String
     * @param user String
     * 
     * @return SpagoBiDataSource[]
     */
    public SpagoBiDataSource[] getAllDataSource(String token,String user){
	logger.debug("IN");
	Monitor monitor =MonitorFactory.start("spagobi.service.datasource.getAllDataSource");
	try {
	    validateTicket(token, user);
	    return supplier.getAllDataSource();
	} catch (SecurityException e) {
	    logger.error("SecurityException", e);
	    return null;
	} finally {
	    monitor.stop();
	    logger.debug("OUT");
	}	

    }
}
