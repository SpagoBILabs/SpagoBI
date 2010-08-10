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
package it.eng.spagobi.analiticalmodel.execution.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractAction;

import org.apache.log4j.Logger;

public class SaveObjectAction extends AbstractAction {

    private static transient Logger logger=Logger.getLogger(SaveObjectAction.class);
    
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean responseSb) throws Exception {
	    logger.debug("IN");
		

		
		String operation=(String)request.getAttribute("op");
		if (operation!=null && operation.equals("a")){
		    logger.debug("operation="+operation); 
		}else {
		    
		}
	    logger.debug("OUT");
	}

}
