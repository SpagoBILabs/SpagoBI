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
package it.eng.spagobi.engines.qbe.services.worksheet;

import it.eng.qbe.query.Query;
import it.eng.qbe.serializer.SerializationException;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSExporter;
import it.eng.spagobi.engines.qbe.services.worksheet.exporter.WorkSheetXLSExporter;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * The Class ExecuteQueryAction.
 */
public class ExportWorksheetAction extends ExecuteWorksheetQueryAction {
	
	// INPUT PARAMETERS
	public static final String MIME_TYPE = "MIME_TYPE";
	public static final String RESPONSE_TYPE = "RESPONSE_TYPE";
	public static final String WORKSHEETS = "WORKSHEETS";
	public static final String SHEETS_NUM = "SHEETS_NUM";
	public static final String EXPORTED_SHEETS = "EXPORTED_SHEETS";
	
	// misc
	public static final String RESPONSE_TYPE_INLINE = "RESPONSE_TYPE_INLINE";
	public static final String RESPONSE_TYPE_ATTACHMENT = "RESPONSE_TYPE_ATTACHMENT";
	
	public static String SVG = "svg";
	public static String OUTPUT_FORMAT = "type";
	
	public static String OUTPUT_FORMAT_PNG = "image/png";
	public static String OUTPUT_FORMAT_JPEG = "image/jpeg";
	public static String OUTPUT_FORMAT_PDF = "application/pdf";
	public static String OUTPUT_FORMAT_SVG = "image/svg+xml";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ExportWorksheetAction.class);
    
	
	public void service(SourceBean request, SourceBean response) {				
		
		logger.debug("IN");
    	String responseType = null;
		boolean writeBackResponseInline = false;
		String mimeType = null;
		JSONObject worksheetJSON = null;
		File exportFile = null;
    	
    	try {
			setSpagoBIRequestContainer( request );
			setSpagoBIResponseContainer( response );
			
			mimeType = getAttributeAsString( MIME_TYPE );
			logger.debug(MIME_TYPE + ": " + mimeType);		
			responseType = getAttributeAsString( RESPONSE_TYPE );
			logger.debug(RESPONSE_TYPE + ": " + responseType);
			
			worksheetJSON = getAttributeAsJSONObject( WORKSHEETS );	
			logger.debug(WORKSHEETS + ": " + worksheetJSON);
			
			writeBackResponseInline = RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType);
			
			if( "application/vnd.ms-excel".equalsIgnoreCase( mimeType ) ) {
								
				Workbook wb = exportWorksheet(worksheetJSON);

				exportFile = File.createTempFile("worksheet", ".xls");
				FileOutputStream stream = new FileOutputStream(exportFile);
				wb.write(stream);
				stream.flush();
				stream.close();
				try {				
					writeBackToClient(exportFile, null, writeBackResponseInline, "worksheet.xls", mimeType);
				} catch (IOException ioe) {
					throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
				}
			} else {
				throw new SpagoBIEngineException("Cannot export crosstab in " + mimeType + " format, only application/vnd.ms-excel is supported");
			}
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}	
	}
	
	public HSSFWorkbook exportWorksheet(JSONObject worksheetJSON) throws JSONException, IOException, SerializationException{
		WorkSheetXLSExporter exporter = new WorkSheetXLSExporter();
		HSSFWorkbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

		int sheetsNumber = worksheetJSON.getInt(SHEETS_NUM);
		JSONArray exportedSheets = worksheetJSON.getJSONArray(EXPORTED_SHEETS);
		for (int i = 0; i < sheetsNumber; i++) {
			JSONObject sheetJ = exportedSheets.getJSONObject(i);
			String sheetName = "Sheet " + i;
			HSSFSheet sheet = wb.createSheet(sheetName);
			
			if(sheetJ.has(exporter.HEADER)){
				JSONObject header = sheetJ.getJSONObject(exporter.HEADER);
				if(header!=null){
					exporter.setHeader(sheet, header, createHelper, wb);
				}
			}	
			
			if(sheetJ.has(exporter.CONTENT)){
				JSONObject content = sheetJ.getJSONObject(exporter.CONTENT);
				fillSheetContent(wb, sheet, content, createHelper, exporter);
			}
			if(sheetJ.has(exporter.FOOTER)){
				JSONObject footer = sheetJ.getJSONObject(exporter.FOOTER);
				if(footer!=null){
					exporter.setFooter(sheet, footer, createHelper, wb);
				}
			}		
		}
		return wb;
	}
	
	public void fillSheetContent(HSSFWorkbook wb, HSSFSheet sheet, JSONObject content, 
			CreationHelper createHelper, WorkSheetXLSExporter exporter) throws IOException, JSONException, SerializationException{
		
		String sheetType = content.getString(exporter.SHEET_TYPE);

		if (sheetType != null && !sheetType.equals("")) {
			
			if (sheetType.equalsIgnoreCase(exporter.CHART)) {
				File jpgImage = exporter.createJPGImage(content);
				int col = 1;
				int row = 4;
				int colend = 13;
				int rowend = 37;
				exporter.setImageIntoWorkSheet(wb, sheet, jpgImage, col, row, colend, rowend,HSSFWorkbook.PICTURE_TYPE_JPEG);
				
			} else if (sheetType.equalsIgnoreCase(exporter.CROSSTAB)) {
				
				String crosstab = content.getString(exporter.CROSSTAB);
				JSONObject crosstabJSON = new JSONObject(crosstab);	
				CrosstabXLSExporter expCr = new CrosstabXLSExporter();
				expCr.fillSheet(sheet, crosstabJSON, createHelper);
				
			} else if (sheetType.equalsIgnoreCase(exporter.TABLE)) {

				String params = content.getString("PARS");
				JSONObject paramsJSON = new JSONObject(params);	
				JSONObject optionalUserFiltersJSON = exporter.getOptionalUserFilters(paramsJSON);				
				List<String> visibleSelectFields = exporter.getJsonVisibleSelectFields(paramsJSON);
				
				Query query = getQuery();					
				if(getEngineInstance().getActiveQuery() != null && getEngineInstance().getActiveQuery().getId().equals(query.getId())) {
					query = getEngineInstance().getActiveQuery();
				} else {
					logger.debug("Query with id [" + query.getId() + "] is not the current active query. A new statment will be generated");
					getEngineInstance().setActiveQuery(query);
					
				}				
				IStatement statement = getStatementForWorksheet(visibleSelectFields, query, optionalUserFiltersJSON);
				IDataStore dataStore = executeQuery(statement, new Integer(0),  new Integer(1000));
				exporter.designTableInWorksheet(sheet, wb, createHelper, dataStore);			
			}
		}
	}
}
