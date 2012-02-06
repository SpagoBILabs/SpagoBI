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
package it.eng.spagobi.tools.scheduler.dispatcher;

import java.util.List;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.dao.ISnapshotDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.massiveExport.services.StartMassiveScheduleAction;
import it.eng.spagobi.tools.scheduler.to.DispatchContext;
import it.eng.spagobi.tools.scheduler.utils.JavaClassDestination;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JavaClassDocumentDispatchChannel implements IDocumentDispatchChannel {
	
	private DispatchContext dispatchContext;
	
	// logger component
	private static Logger logger = Logger.getLogger(JavaClassDocumentDispatchChannel.class); 
	
	public JavaClassDocumentDispatchChannel(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
	}
	
	public void setDispatchContext(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
	}

	public void close() {
		
	}
	
	public boolean canDispatch(BIObject document)  {
		return true;
	}
	
	public boolean dispatch(BIObject document, byte[] executionOutput) {
		
		logger.debug("IN");

		String javaClass = dispatchContext.getJavaClassPath();
		if( (javaClass==null) || javaClass.trim().equals("")) {
			logger.error("Classe java nons specificata");
			return false;
		}
		// try to get new Instance
		JavaClassDestination jcDest=null;
		try{
			jcDest=(JavaClassDestination)Class.forName(javaClass).newInstance();
		}
		catch (ClassCastException e) {
			logger.error("Class "+javaClass+" does not extend JavaClassDestination class as expected");
			return false;
		}
		catch (Exception e) {
			logger.error("Error while instantiating the class "+javaClass);
			return false;
			}

		logger.debug("Sucessfull instantiation of "+javaClass);

		jcDest.setBiObj(document);
		jcDest.setDocumentByte(executionOutput);

		try{
			jcDest.execute();
		}
		catch (Exception e) {
			logger.error("Error during execution",e);
			return false;
		}


		logger.debug("OUT");

		return true;
	}
}
