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
package it.eng.spagobi.utilities.service;

import java.io.IOException;

import it.eng.spagobi.utilities.container.HttpServletRequestContainer;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.BaseServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractBaseServlet extends HttpServlet {
	
	
	 /**
     * Logger component
     */
    private static transient Logger logger = Logger.getLogger(AbstractBaseServlet.class);
    
	

    public void init(ServletConfig config) throws ServletException {
    	super.init(config);	
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) {
    	BaseServletIOManager servletIOManager;
    	
    	servletIOManager = new BaseServletIOManager(request, response);
    	
    	try {
			this.doService( servletIOManager );
		} catch (Throwable t) {
			handleException(servletIOManager, t);
		}
    }
    
    public abstract void doService(BaseServletIOManager servletIOManager) throws SpagoBIEngineException;
    
    public abstract void handleException(BaseServletIOManager servletIOManager, Throwable t);
	
	
    
	
	
	
	
	
	
	
	
	
	
	
	
}
