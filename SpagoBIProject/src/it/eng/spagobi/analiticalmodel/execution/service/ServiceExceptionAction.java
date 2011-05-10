/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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

import it.eng.spago.error.EMFAbstractError;
import it.eng.spago.error.EMFInternalError;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.utilities.exceptions.CannotWriteErrorsToClientException;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ServiceExceptionAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "SERVICE_EXCEPTION_ACTION";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ServiceExceptionAction.class);
    
	public void doService()  {
		logger.debug("IN");
		try {
			Collection<EMFAbstractError> errors = getErrorHandler().getErrors();
			Iterator it = errors.iterator();
			// if there is a CannotWriteErrorsToClient exception, CANNOT SEND ERRORS TO CLIENT
			while (it.hasNext()) {
				EMFAbstractError error = (EMFAbstractError) it.next();
				if (error instanceof EMFInternalError) {
					EMFInternalError internalError = (EMFInternalError) error;
					Exception e = internalError.getNativeException();
					if(e instanceof CannotWriteErrorsToClientException) {
						logger.error(e);
						return;
					}
				}
			}
			writeErrorsBackToClient();
		} finally {
			logger.debug("OUT");
		}
	}

}
