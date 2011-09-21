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
package it.eng.spagobi.engines.worksheet.services.export;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSExporter;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetPDFExporter;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetXLSExporter;
import it.eng.spagobi.engines.worksheet.services.runtime.ExecuteWorksheetQueryAction;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ExportWorksheetAction extends ExecuteWorksheetQueryAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7193307157829114510L;
	// INPUT PARAMETERS
	public static final String MIME_TYPE = "MIME_TYPE";
	public static final String RESPONSE_TYPE = "RESPONSE_TYPE";
	public static final String WORKSHEETS = "WORKSHEETS";
	public static final String SHEETS_NUM = "SHEETS_NUM";
	public static final String EXPORTED_SHEETS = "EXPORTED_SHEETS";
	public static final String CONTENT = "CONTENT";
	public static final String CONTENT_PARS = "PARS";
	public static final String FILTERS = "FILTERS";
	
	// misc
	public static final String RESPONSE_TYPE_INLINE = "RESPONSE_TYPE_INLINE";
	public static final String RESPONSE_TYPE_ATTACHMENT = "RESPONSE_TYPE_ATTACHMENT";
	
	public static String SVG = "svg";
	public static String OUTPUT_FORMAT = "type";
	
	public static String OUTPUT_FORMAT_PNG = "image/png";
	public static String OUTPUT_FORMAT_JPEG = "image/jpeg";
	public static String OUTPUT_FORMAT_PDF = "application/pdf";
	public static String OUTPUT_FORMAT_SVG = "image/svg+xml";
	
	private DecimalFormat numberFormat;
	private String userDateFormat;
	
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
			
			Locale locale = (Locale)getEngineInstance().getEnv().get(EngineConstants.ENV_LOCALE);	
			numberFormat =  (DecimalFormat) NumberFormat.getInstance(locale);
			numberFormat.applyPattern("##,##0.00");
			userDateFormat = (String)getEngineInstance().getEnv().get(EngineConstants.ENV_USER_DATE_FORMAT);	
			
			
			mimeType = getAttributeAsString( MIME_TYPE );
			logger.debug(MIME_TYPE + ": " + mimeType);		
			responseType = getAttributeAsString( RESPONSE_TYPE );
			logger.debug(RESPONSE_TYPE + ": " + responseType);
			
			worksheetJSON = getAttributeAsJSONObject( WORKSHEETS );	
			logger.debug(WORKSHEETS + ": " + worksheetJSON);
			
			writeBackResponseInline = RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType);
			
			if( "application/vnd.ms-excel".equalsIgnoreCase( mimeType ) ) {

				exportFile = File.createTempFile("worksheet", ".xls");
				FileOutputStream stream = new FileOutputStream(exportFile);
				try {
					exportToXLS(worksheetJSON, stream);
				} finally {
					if (stream != null) {
						stream.close();
					}
				}

			} else if ( "application/pdf".equalsIgnoreCase( mimeType ) ) {
				
				exportFile = File.createTempFile("worksheet", ".pdf");
				FileOutputStream stream = new FileOutputStream(exportFile);
				try {
					exportToPDF(worksheetJSON, stream);
				} finally {
					if (stream != null) {
						stream.close();
					}
				}

			} else {
				throw new SpagoBIEngineException("Cannot export worksheet in " + mimeType + " format, only application/vnd.ms-excel ans application/pdf are supported");
			}
			
			try {				
				writeBackToClient(exportFile, null, writeBackResponseInline, exportFile.getName(), mimeType);
			} catch (IOException ioe) {
				throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
			}
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}	
	}
	
	public void exportToPDF(JSONObject worksheetJSON, OutputStream outputStream) throws Exception {
		
		WorkSheetPDFExporter exporter = new WorkSheetPDFExporter();
		exporter.open(outputStream);
		exporter.setNumberFormat(numberFormat);
		exporter.setUserDateFormat(userDateFormat);
		
		int sheetsNumber = worksheetJSON.getInt(SHEETS_NUM);
		JSONArray exportedSheets = worksheetJSON.getJSONArray(EXPORTED_SHEETS);
		for (int i = 0; i < sheetsNumber; i++) {
			JSONObject sheetJ = exportedSheets.getJSONObject(i);
			if(isTableContent(sheetJ)){
				IDataStore dataStore = getTableDataStore(sheetJ);
				exporter.addSheet(sheetJ, dataStore);
			}else{
				exporter.addSheet(sheetJ);
			}
		}
		
		exporter.close();
		outputStream.flush();
	}
	
	public void exportToXLS(JSONObject worksheetJSON, OutputStream stream) throws Exception {
		WorkSheetXLSExporter exporter = new WorkSheetXLSExporter();
		HSSFWorkbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

		int sheetsNumber = worksheetJSON.getInt(SHEETS_NUM);
		JSONArray exportedSheets = worksheetJSON.getJSONArray(EXPORTED_SHEETS);
		for (int i = 0; i < sheetsNumber; i++) {
			JSONObject sheetJ = exportedSheets.getJSONObject(i);
			String sheetName = "Sheet " + i;
			HSSFSheet sheet = wb.createSheet(sheetName);
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			
			for(int j = 0; j < 50; j++){
				sheet.createRow(j);
			}
			
			if(sheetJ.has(WorkSheetXLSExporter.HEADER)){
				JSONObject header = sheetJ.getJSONObject(WorkSheetXLSExporter.HEADER);
				if(header!=null){
					exporter.setHeader(sheet, header, createHelper, wb, patriarch);
				}
			}	
			
			int endRowNum = 37;
			if(sheetJ.has(WorkSheetXLSExporter.CONTENT)){
				endRowNum = fillSheetContent(wb, sheet, sheetJ, createHelper, exporter, patriarch);
			}			
			
			if(sheetJ.has(WorkSheetXLSExporter.FOOTER)){
				JSONObject footer = sheetJ.getJSONObject(WorkSheetXLSExporter.FOOTER);
				if(footer!=null){
					exporter.setFooter(sheet, footer, createHelper, wb, endRowNum, patriarch);
				}
			}		
		}
		
		wb.write(stream);
		stream.flush();

	}
	
	public int fillSheetContent(HSSFWorkbook wb, HSSFSheet sheet, JSONObject sheetJ, 
			CreationHelper createHelper, WorkSheetXLSExporter exporter, HSSFPatriarch patriarch) throws Exception {
		
		JSONObject content = sheetJ.getJSONObject(WorkSheetXLSExporter.CONTENT);
		String sheetType = content.getString(WorkSheetXLSExporter.SHEET_TYPE);
		int endRowNum = 0;
		

		if (sheetType != null && !sheetType.equals("")) {
			
			if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CHART)) {
				File jpgImage = WorkSheetXLSExporter.createJPGImage(content);
				int col = 1;
				int row = 4;
				int colend = 13;
				int rowend = 37;
				exporter.setImageIntoWorkSheet(wb, patriarch, jpgImage, col, row, colend, rowend,HSSFWorkbook.PICTURE_TYPE_JPEG);
				endRowNum = rowend;
				
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CROSSTAB)) {
				
				String crosstab = content.getString(WorkSheetXLSExporter.CROSSTAB);
				JSONObject crosstabJSON = new JSONObject(crosstab);	
				CrosstabXLSExporter expCr = new CrosstabXLSExporter();
				endRowNum = expCr.fillAlreadyCreatedSheet(sheet, crosstabJSON, createHelper);
				
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.TABLE)) {

				IDataStore dataStore = getTableDataStore(sheetJ);
				long recCount = dataStore.getRecordsCount();
				endRowNum = (new Long(recCount)).intValue() + 5;
				exporter.designTableInWorksheet(sheet, wb, createHelper, dataStore);			
			}
		}
		return endRowNum;
	}
	

	
	/**
	 * Execute the query active in the engine instance and return
	 * the data store
	 * @return the data store after the execution of the active query
	 */
	private IDataStore getTableDataStore(JSONObject sheetJ) throws Exception {
		JSONObject sheetContentPars = null;
		JSONArray jsonVisibleSelectFields = null;
		JSONObject sheetContent = sheetJ.optJSONObject(CONTENT);
		sheetContentPars = sheetContent.optJSONObject(CONTENT_PARS);
		// get the visible columns
		if (sheetContentPars != null) {
			jsonVisibleSelectFields = sheetContentPars
					.optJSONArray(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS);
		}
		IDataStore dataStore = executeQuery(jsonVisibleSelectFields);
		return dataStore;
	}
	
	/**
	 * Return true if the content of a sheet is a table
	 * @param sheetJSON a sheet
	 * @return true if the content of a sheet is a table
	 */
	public boolean isTableContent(JSONObject sheetJSON){
		try{
			JSONObject content = sheetJSON.getJSONObject(WorkSheetPDFExporter.CONTENT);
			String sheetType = content.getString(WorkSheetPDFExporter.SHEET_TYPE);
			return (WorkSheetPDFExporter.TABLE.equalsIgnoreCase(sheetType));	
		}catch (JSONException e){
			return false;
		}
	}
	

	
	
}
