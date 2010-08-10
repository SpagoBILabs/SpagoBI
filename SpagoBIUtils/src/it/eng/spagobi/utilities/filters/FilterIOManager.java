/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.utilities.filters;

import it.eng.spagobi.container.ContextManager;
import it.eng.spagobi.container.SpagoBIHttpSessionContainer;
import it.eng.spagobi.container.strategy.ExecutionContextRetrieverStrategy;
import it.eng.spagobi.container.strategy.IContextRetrieverStrategy;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class FilterIOManager {
	ServletRequest request;
	ServletResponse response;
	ContextManager contextManager;
	
	private static final String EXECUTION_ID = "SBI_EXECUTION_ID";
	
	//----------------------------------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------------------------------
	
	public FilterIOManager(ServletRequest request, ServletResponse response) {
		setRequest( request );
		setResponse( response );
	}
	
	//----------------------------------------------------------------------------------------------------
    // Accessor methods
    //----------------------------------------------------------------------------------------------------
    
    
	public ServletRequest getRequest() {
		return request;
	}

	public void setRequest(ServletRequest request) {
		this.request = request;
	}
	
	public ServletResponse getResponse() {
		return response;
	}

	public void setResponse(ServletResponse response) {
		this.response = response;
	}
	
	/**
	 * @deprecated 
	 */
	public HttpSession getSession() {
    	return ((HttpServletRequest)getRequest()).getSession();
    }
	
	/**
	 * @deprecated 
	 */
	public Object getFromSession(String key) {
		return getSession().getAttribute(key);
	}
	
	/**
	 * @deprecated 
	 */
	public void setInSession(String key, Object value) {
		getSession().setAttribute(key, value);
	}
	
	public void initConetxtManager() {
		SpagoBIHttpSessionContainer sessionContainer;
		IContextRetrieverStrategy contextRetriveStrategy;
		String executionId;		
		
		sessionContainer = new SpagoBIHttpSessionContainer( getSession() );	
		
		executionId = (String)getRequest().getParameter(EXECUTION_ID);
		contextRetriveStrategy = new ExecutionContextRetrieverStrategy( executionId );
		
		setContextManager( new ContextManager(sessionContainer, contextRetriveStrategy) );
	}

	public ContextManager getContextManager() {
		return contextManager;
	}

	public void setContextManager(ContextManager contextManager) {
		this.contextManager = contextManager;
	}
}
