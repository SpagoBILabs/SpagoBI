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
package it.eng.spagobi.engines.qbe.services.crosstab;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSExporter;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;


/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 * This action export crosstab data into a XLS file.
 * The crosstab data must be sent by the client, this action just read the coming data (a JSONObject) and put it into a XLS file.
 */
public class ExportCrosstabAction extends AbstractQbeEngineAction {
	
	// INPUT PARAMETERS
	public static final String MIME_TYPE = "MIME_TYPE";
	public static final String RESPONSE_TYPE = "RESPONSE_TYPE";
	public static final String CROSSTAB = "CROSSTAB";
	public static final String SHEET_TYPE = "SHEET_TYPE";
	
	// misc
	public static final String RESPONSE_TYPE_INLINE = "RESPONSE_TYPE_INLINE";
	public static final String RESPONSE_TYPE_ATTACHMENT = "RESPONSE_TYPE_ATTACHMENT";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ExportCrosstabAction.class);
    
	
	public void service(SourceBean request, SourceBean response) {				
		
		String responseType = null;
		boolean writeBackResponseInline = false;
		String mimeType = null;
		JSONObject crosstabJSON = null;
		File exportFile = null;
		
		logger.debug("IN");
		
		try {
			super.service(request, response);	
			
			mimeType = getAttributeAsString( MIME_TYPE );
			logger.debug(MIME_TYPE + ": " + mimeType);		
			responseType = getAttributeAsString( RESPONSE_TYPE );
			logger.debug(RESPONSE_TYPE + ": " + responseType);
			
			crosstabJSON = getAttributeAsJSONObject( CROSSTAB );	
			logger.debug(CROSSTAB + ": " + crosstabJSON);
			
			writeBackResponseInline = RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType);
			
			if( "application/vnd.ms-excel".equalsIgnoreCase( mimeType ) ) {
				CrosstabXLSExporter exporter = new CrosstabXLSExporter();
				Workbook wb = exporter.export(crosstabJSON);
				
				exportFile = File.createTempFile("crosstab", ".xls");
				FileOutputStream stream = new FileOutputStream(exportFile);
				wb.write(stream);
				stream.flush();
				stream.close();
				try {				
					writeBackToClient(exportFile, null, writeBackResponseInline, "crosstab.xls", mimeType);
				} catch (IOException ioe) {
					throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
				}
			} else {
				throw new SpagoBIEngineException("Cannot export crosstab in " + mimeType + " format, only application/vnd.ms-excel is supported");
			}

		} catch (Throwable t) {			
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			
			if (exportFile != null && exportFile.exists()) {
				try {
					exportFile.delete();
				} catch (Exception e) {
					logger.warn("Impossible to delete temporary file " + exportFile, e);
				}
			}
		}
		
		logger.debug("OUT");
	}


}
