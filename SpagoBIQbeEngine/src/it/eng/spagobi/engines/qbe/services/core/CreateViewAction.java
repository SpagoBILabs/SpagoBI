/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.services.core;

import java.io.IOException;

import org.apache.log4j.Logger;

import it.eng.qbe.query.Query;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

/**
 * The Class CreateViewAction.
 * 
 * @author Andrea Gioia
 */
public class CreateViewAction extends AbstractQbeEngineAction {
	
	// INPUT PARAMETERS
	public static final String VIEW_NAME = "viewName";	
	public static final String QUERY = "query";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CreateViewAction.class);
    
	
    
	public void service(SourceBean request, SourceBean response)  {				
		
		String viewName = null;
		String jsonEncodedQuery = null;
		Query query = null;
		
		logger.debug("IN");
		
		try {
		
			super.service(request, response);		
		
			viewName = getAttributeAsString(VIEW_NAME);
			logger.debug(VIEW_NAME + " = [" + viewName + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			Assert.assertNotNull(getEngineInstance().getActiveQuery(), "Query object cannot be null in oder to execute " + this.getActionName() + " service");
			Assert.assertTrue(getEngineInstance().getActiveQuery().isEmpty() == false, "Query object cannot be empty in oder to execute " + this.getActionName() + " service");
			Assert.assertNotNull(viewName, "Input parameter [" + VIEW_NAME + "] cannot be null in oder to execute " + this.getActionName() + " service");
			
			getEngineInstance().getDatamartModel().addView(viewName, getEngineInstance().getActiveQuery());
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				throw new SpagoBIEngineException("Impossible to write back the responce to the client", e);
			}
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			// no resources need to be released
		}	
		
		logger.debug("OUT");
	}
}
