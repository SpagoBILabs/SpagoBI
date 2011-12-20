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
package it.eng.spagobi.services.dataset.service;

import it.eng.spagobi.services.common.AbstractServiceImpl;
import it.eng.spagobi.services.dataset.DataSetService;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.services.security.exceptions.SecurityException;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;





/**
 * @author Andrea Gioia
 */
public class DataSetServiceImpl extends AbstractServiceImpl  implements DataSetService {
	 
	private DataSetSupplier supplier = new DataSetSupplier();
	    
	static private Logger logger = Logger.getLogger(DataSetServiceImpl.class);

	/**
     * Instantiates a new data source service impl.
     */
    public DataSetServiceImpl(){
    	super();
    }
    
    public SpagoBiDataSet getDataSet(String token, String user, String documentId){
    	logger.debug("IN");
    	Monitor monitor = MonitorFactory.start("spagobi.service.dataset.getDataSet");
    	try {
    	    validateTicket(token, user);
    	    return supplier.getDataSet(documentId);
    	} catch (SecurityException e) {
    	    logger.error("SecurityException", e);
    	    return null;
    	} finally {
    	    monitor.stop();
    	    logger.debug("OUT");
    	}	
    }
    
    
    public SpagoBiDataSet getDataSetByLabel(String token,String user,String label){
    	logger.debug("IN");
    	Monitor monitor = MonitorFactory.start("spagobi.service.dataset.getDataSetByLabel");
    	try {
    	    validateTicket(token, user);
    	    return supplier.getDataSetByLabel(label);
    	} catch (SecurityException e) {
    	    logger.error("SecurityException", e);
    	    return null;
    	} finally {
    	    monitor.stop();
    	    logger.debug("OUT");
    	}	
    }
    
    
    /**
     * 
     * @param token String
     * @param user String
     * @return SpagoBiDataSet[]
     */
    public SpagoBiDataSet[] getAllDataSet(String token,String user){
    	logger.debug("IN");
    	Monitor monitor =MonitorFactory.start("spagobi.service.dataset.getAllDataSet");
    	try {
    	    validateTicket(token, user);
    	    return supplier.getAllDataSet();
    	} catch (SecurityException e) {
    	    logger.error("SecurityException", e);
    	    return null;
    	} finally {
    	    monitor.stop();
    	    logger.debug("OUT");
    	}	
    }
    
    
    

}
