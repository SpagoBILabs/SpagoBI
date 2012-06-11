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
package it.eng.spagobi.services.sbidocument.service;

import it.eng.spagobi.services.common.AbstractServiceImpl;
import it.eng.spagobi.services.sbidocument.SbiDocumentService;
import it.eng.spagobi.services.sbidocument.bo.SpagobiAnalyticalDriver;
import it.eng.spagobi.services.security.exceptions.SecurityException;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * @author Andrea Gioia
 */
public class SbiDocumentServiceImpl extends AbstractServiceImpl  implements SbiDocumentService {
	 
	private SbiDocumentSupplier supplier = new SbiDocumentSupplier();
	    
	static private Logger logger = Logger.getLogger(SbiDocumentServiceImpl.class);

	/**
     * Instantiates a new data source service impl.
     */
    public SbiDocumentServiceImpl(){
    	super();
    }
    
    public SpagobiAnalyticalDriver[] getDocumentAnalyticalDrivers(String token, String user, Integer id, String language, String country){
    	logger.debug("IN");
    	Monitor monitor = MonitorFactory.start("spagobi.service.sbidocument.getDocumentParameters");
    	try {
    	    validateTicket(token, user);
    	    this.setTenantByUserId(user);
    	    return supplier.getDocumentAnalyticalDrivers(id, language, country);
    	} catch (SecurityException e) {
    	    logger.error("SecurityException", e);
    	    return null;
    	} finally {
    		this.unsetTenant();
    	    monitor.stop();
    	    logger.debug("OUT");
    	}	
    }
    
    public String getDocumentAnalyticalDriversJSON(String token, String user, Integer id, String language, String country){
    	logger.debug("IN");
    	Monitor monitor = MonitorFactory.start("spagobi.service.sbidocument.getDocumentParametersJSON");
    	try {
    	    validateTicket(token, user);
    	    this.setTenantByUserId(user);
    	    return supplier.getDocumentAnalyticalDriversJSON(id, language, country);
    	} catch (SecurityException e) {
    	    logger.error("SecurityException", e);
    	    return null;
    	} finally {
    		this.unsetTenant();
    	    monitor.stop();
    	    logger.debug("OUT");
    	}	
    }
    
        

}
