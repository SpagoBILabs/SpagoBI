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
package it.eng.spagobi.engines.worksheet.services.designer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.worksheet.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class GetWorksheetFieldsAction  extends AbstractWorksheetEngineAction {	

	private static final long serialVersionUID = -5874137232683097175L;
	
	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(GetWorksheetFieldsAction.class);

	public void service(SourceBean request, SourceBean response)  {

		JSONObject resultsJSON;
		
		logger.debug("IN");

		try {		
			super.service(request, response);	

			WorksheetEngineInstance engineInstance = this.getEngineInstance();
			IDataSet dataset = engineInstance.getDataSet();
			IMetaData metadata = dataset.getMetadata();
			
			JSONDataWriter dataWriter = new JSONDataWriter();
			resultsJSON = (JSONObject) dataWriter.write(metadata);

			try {
				writeBackToClient( new JSONSuccess( resultsJSON ) );
			} catch (IOException e) {
				throw new SpagoBIEngineServiceException(getActionName(), "Impossible to write back the responce to the client [" + resultsJSON.toString(2)+ "]", e);
			}

		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {			
			logger.debug("OUT");
		}	
	}
}
