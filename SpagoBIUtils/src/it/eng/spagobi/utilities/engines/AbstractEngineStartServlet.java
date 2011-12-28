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
package it.eng.spagobi.utilities.engines;



import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.configuration.FileCreatorConfiguration;
import it.eng.spagobi.utilities.service.AbstractBaseServlet;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractEngineStartServlet extends AbstractBaseServlet {


    /**
     * Logger component
     */
    private static transient Logger logger = Logger.getLogger(AbstractEngineStartServlet.class);
    
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);	
    	
    	String path = getServletConfig().getServletContext().getRealPath("/WEB-INF");
    	ConfigSingleton.setConfigurationCreation( new FileCreatorConfiguration( path ) );
    	ConfigSingleton.setRootPath( path );
    	ConfigSingleton.setConfigFileName("/empty.xml");    	
    }
    
    public void doService( BaseServletIOManager servletIOManager ) throws SpagoBIEngineException {
    	
    	EngineStartServletIOManager engineServletIOManager;
    	
    	engineServletIOManager = new EngineStartServletIOManager(servletIOManager);
    	
    	try {
			this.doService( engineServletIOManager );
		} catch (Throwable t) {
			handleException(servletIOManager, t);
		}
		
    }
    
    public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
    	
    	logger.debug("User Id: " + servletIOManager.getUserId());
		logger.debug("Audit Id: " + servletIOManager.getAuditId());
		logger.debug("Document Id: " + servletIOManager.getDocumentId());
		logger.debug("Template: " + servletIOManager.getTemplateAsSourceBean());
				
    }

    public void handleException(EngineStartServletIOManager servletIOManager, Throwable t) {
    	logger.error("Service execution failed", t);
    	
    	servletIOManager.auditServiceErrorEvent(t.getMessage());			
		
    	String reponseMessage = servletIOManager.getLocalizedMessage("msg.error.generic");
    	if(t instanceof SpagoBIEngineException) {
    		SpagoBIEngineException e = (SpagoBIEngineException)t;
    		reponseMessage = servletIOManager.getLocalizedMessage(e.getDescription());
    		  		
    	} 		
    	
    	servletIOManager.tryToWriteBackToClient( reponseMessage );		
    }
    
    public void handleException(BaseServletIOManager servletIOManager, Throwable t) {
    	handleException( new EngineStartServletIOManager(servletIOManager), t);
    }
    

   
	
}
