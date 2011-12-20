/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
    * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
    * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

**/
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
		
    	String reponseMessage = servletIOManager.getLocalizedMessage("an.unpredicted.error.occured");
    	if(t instanceof SpagoBIEngineException) {
    		SpagoBIEngineException e = (SpagoBIEngineException)t;
    		reponseMessage = servletIOManager.getLocalizedMessage(e.getErrorDescription());
    		  		
    	} 		
    	
    	servletIOManager.tryToWriteBackToClient( reponseMessage );		
    }
    
    public void handleException(BaseServletIOManager servletIOManager, Throwable t) {
    	handleException( new EngineStartServletIOManager(servletIOManager), t);
    }
    

   
	
}
