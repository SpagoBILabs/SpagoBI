/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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

import it.eng.qbe.query.WhereField;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.crosstable.CrossTab;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSExporterFromJavaObject;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSXExporterFromJavaObject;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetPDFExporter;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetXLSExporter;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetXLSXExporter;
import it.eng.spagobi.engines.worksheet.services.runtime.ExecuteWorksheetQueryAction;
import it.eng.spagobi.engines.worksheet.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	public static final String SPLITTING_FILTER= "splittingFilter";
	
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

			} else if( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase( mimeType ) ) {

				exportFile = File.createTempFile("worksheet", ".xlsx");
				FileOutputStream stream = new FileOutputStream(exportFile);
				try {
					exportToXLSX(worksheetJSON, stream);
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
			if (isTableContent(sheetJ)) {
				IDataStore dataStore = getTableDataStore(sheetJ);
				exporter.addSheet(sheetJ, dataStore);
			} else {
				exporter.addSheet(sheetJ);
			}
		}
		
		exporter.close();
		outputStream.flush();
	}
	
	public void exportToXLS(JSONObject worksheetJSON, OutputStream stream) throws Exception {

		HSSFWorkbook wb = new HSSFWorkbook();
		int sheetsNumber = worksheetJSON.getInt(SHEETS_NUM);
		WorkSheetXLSExporter exporter = new WorkSheetXLSExporter();
		CreationHelper createHelper = wb.getCreationHelper();
		
		JSONArray exportedSheets = worksheetJSON.getJSONArray(EXPORTED_SHEETS);
			
		
		for (int i = 0; i < sheetsNumber; i++) {
			
			JSONObject sheetJ = exportedSheets.getJSONObject(i);
			JSONObject optionalFilters = sheetJ.optJSONObject(QbeEngineStaticVariables.FILTERS);
			String sheetName = sheetJ.getString(SHEET);
			List<WhereField> splittingWF = getSplittingFieldValues(optionalFilters, sheetName);
			WhereField splittingWhereField = null;
			if(splittingWF==null || splittingWF.size()==0){
				exportSheetToXLS(wb, sheetJ, exporter, createHelper, splittingWhereField);
			}else{
				for(int y=0; y< splittingWF.size(); y++){
					splittingWhereField = splittingWF.get(y);
					exportSheetToXLS( wb, sheetJ, exporter, createHelper, splittingWhereField);
				}
			}
		}
		
		wb.write(stream);
		stream.flush();

	}
	
	public void exportSheetToXLS(HSSFWorkbook wb,JSONObject sheetJ, WorkSheetXLSExporter exporter,CreationHelper createHelper, WhereField splittingWhereField) throws Exception{
		
		//The numeber of row of the sheet
		int sheetRow;

		String sheetName = sheetJ.getString(SHEET);
		
		sheetRow=0;

		if(splittingWhereField!=null){
			sheetName = sheetName+" ("+splittingWhereField.getRightOperand().values[0]+")";
		}
		
		HSSFSheet sheet = wb.createSheet(sheetName);

		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		
//		sheet.createRow(sheetRow);
//		sheetRow++;
		
		if(sheetJ.has(WorkSheetXLSExporter.HEADER)){
			JSONObject header = sheetJ.getJSONObject(WorkSheetXLSExporter.HEADER);
			if(header!=null){
				sheetRow = exporter.setHeader(sheet, header, createHelper, wb, patriarch, sheetRow);
			}
			sheet.createRow(sheetRow);
			sheetRow++;
		}	
		
		if(sheetJ.has(WorkSheetXLSExporter.CONTENT)){
			sheetRow = fillSheetContent(wb, sheet, sheetJ,splittingWhereField, createHelper, exporter, patriarch, sheetRow);
		}			
		
		sheet.createRow(sheetRow);
		sheetRow++;
		
		if(sheetJ.has(WorkSheetXLSExporter.FOOTER)){
			JSONObject footer = sheetJ.getJSONObject(WorkSheetXLSExporter.FOOTER);
			if(footer!=null){
				exporter.setFooter(sheet, footer, createHelper, wb, patriarch, sheetRow);
			}
		}	
	}
	
	public int fillSheetContent(HSSFWorkbook wb, HSSFSheet sheet, JSONObject sheetJ, WhereField splittingWhereField,
			CreationHelper createHelper, WorkSheetXLSExporter exporter, HSSFPatriarch patriarch, int sheetRow) throws Exception {
		
		JSONObject content = sheetJ.getJSONObject(WorkSheetXLSExporter.CONTENT);
		String sheetType = content.getString(WorkSheetXLSExporter.SHEET_TYPE);
		

		if (sheetType != null && !sheetType.equals("")) {
			
			if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CHART)) {
				File jpgImage = WorkSheetXLSExporter.createJPGImage(content);
				int col = 1;
				int colend = 20;
				int charHeight = 20;
				for(int i=0; i<charHeight; i++){
					sheet.createRow(sheetRow+i);
				}
				exporter.setImageIntoWorkSheet(wb, patriarch, jpgImage, col, colend, sheetRow, charHeight,HSSFWorkbook.PICTURE_TYPE_JPEG);
				sheetRow= sheetRow+30;
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CROSSTAB)) {
				JSONArray calculateFieldsJSON=null;
				String crosstabDefinition = content.getString("CROSSTABDEFINITION");
				String crosstab = content.getString(WorkSheetXLSExporter.CROSSTAB);
				String sheetName = sheetJ.getString(SHEET);
				JSONObject filters = sheetJ.optJSONObject(QbeEngineStaticVariables.FILTERS);
				JSONObject crosstabDefinitionJSON = new JSONObject(crosstabDefinition);
				JSONObject crosstabJSON = new JSONObject(crosstab);	
				
				String calculateFields = content.optString("CF");
				if(calculateFields!=null){
					calculateFieldsJSON = new JSONArray(calculateFields);
				}
				
				
				CrossTab cs = getCrosstab(crosstabDefinitionJSON, filters, sheetName, splittingWhereField, calculateFieldsJSON);
				CrosstabXLSExporterFromJavaObject expCr = new CrosstabXLSExporterFromJavaObject();
				//int rows = expCr.initSheet(sheet, cs);
				sheetRow = expCr.fillAlreadyCreatedSheet(sheet, cs, crosstabJSON, createHelper, sheetRow);
				//sheetRow = sheetRow+rows;
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.TABLE)) {

				IDataStore dataStore = getTableDataStore(sheetJ);
				long recCount = dataStore.getRecordsCount();
				recCount = (new Long(recCount)).intValue() + 5;
				int startRow = sheetRow;
				for(int i=0; i<recCount; i++){
					sheet.createRow(sheetRow);
					sheetRow++;
				}
				exporter.designTableInWorksheet(sheet, wb, createHelper, dataStore,startRow);			
			}
		}
		return sheetRow;
	}
	
	public void exportToXLSX(JSONObject worksheetJSON, OutputStream stream) throws Exception {

		XSSFWorkbook wb = new XSSFWorkbook();
		int sheetsNumber = worksheetJSON.getInt(SHEETS_NUM);
		WorkSheetXLSXExporter exporter = new WorkSheetXLSXExporter();
		CreationHelper createHelper = wb.getCreationHelper();
		
		JSONArray exportedSheets = worksheetJSON.getJSONArray(EXPORTED_SHEETS);
			
		
		for (int i = 0; i < sheetsNumber; i++) {
			
			JSONObject sheetJ = exportedSheets.getJSONObject(i);
			JSONObject optionalFilters = sheetJ.optJSONObject(QbeEngineStaticVariables.FILTERS);
			String sheetName = sheetJ.getString(SHEET);
			List<WhereField> splittingWF = getSplittingFieldValues(optionalFilters, sheetName);
			WhereField splittingWhereField = null;
			if(splittingWF==null || splittingWF.size()==0){
				exportSheetToXLSX(wb, sheetJ, exporter, createHelper, splittingWhereField);
			}else{
				for(int y=0; y< splittingWF.size(); y++){
					splittingWhereField = splittingWF.get(y);
					exportSheetToXLSX( wb, sheetJ, exporter, createHelper, splittingWhereField);
				}
			}
		}
		
		wb.write(stream);
		stream.flush();

	}
	
	public void exportSheetToXLSX(XSSFWorkbook wb,JSONObject sheetJ, WorkSheetXLSXExporter exporter,CreationHelper createHelper, WhereField splittingWhereField) throws Exception{
		
		//The numeber of row of the sheet
		int sheetRow;

		String sheetName = sheetJ.getString(SHEET);
		
		sheetRow=0;

		if(splittingWhereField!=null){
			sheetName = sheetName+" ("+splittingWhereField.getRightOperand().values[0]+")";
		}
		
		XSSFSheet sheet = wb.createSheet(sheetName);

		XSSFDrawing patriarch = sheet.createDrawingPatriarch();
		
//		sheet.createRow(sheetRow);
//		sheetRow++;
		
		if(sheetJ.has(WorkSheetXLSExporter.HEADER)){
			JSONObject header = sheetJ.getJSONObject(WorkSheetXLSExporter.HEADER);
			if(header!=null){
				sheetRow = exporter.setHeader(sheet, header, createHelper, wb, patriarch, sheetRow);
			}
			sheet.createRow(sheetRow);
			sheetRow++;
		}	
		
		if(sheetJ.has(WorkSheetXLSExporter.CONTENT)){
			sheetRow = fillSheetContent(wb, sheet, sheetJ,splittingWhereField, createHelper, exporter, patriarch, sheetRow);
		}			
		
		sheet.createRow(sheetRow);
		sheetRow++;
		
		if(sheetJ.has(WorkSheetXLSExporter.FOOTER)){
			JSONObject footer = sheetJ.getJSONObject(WorkSheetXLSExporter.FOOTER);
			if(footer!=null){
				exporter.setFooter(sheet, footer, createHelper, wb, patriarch, sheetRow);
			}
		}	
	}
	
	public int fillSheetContent(XSSFWorkbook wb, XSSFSheet sheet, JSONObject sheetJ, WhereField splittingWhereField,
			CreationHelper createHelper, WorkSheetXLSXExporter exporter, XSSFDrawing patriarch, int sheetRow) throws Exception {
		
		JSONObject content = sheetJ.getJSONObject(WorkSheetXLSXExporter.CONTENT);
		String sheetType = content.getString(WorkSheetXLSXExporter.SHEET_TYPE);
		

		if (sheetType != null && !sheetType.equals("")) {
			
			if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CHART)) {
				File jpgImage = WorkSheetXLSXExporter.createJPGImage(content);
				int col = 1;
				int colend = 20;
				int charHeight = 20;
				for(int i=0; i<charHeight; i++){
					sheet.createRow(sheetRow+i);
				}
				exporter.setImageIntoWorkSheet(wb, patriarch, jpgImage, col, colend, sheetRow, charHeight,XSSFWorkbook.PICTURE_TYPE_JPEG);
				sheetRow= sheetRow+30;
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.CROSSTAB)) {
				JSONArray calculateFieldsJSON=null;
				String crosstabDefinition = content.getString("CROSSTABDEFINITION");
				String crosstab = content.getString(WorkSheetXLSExporter.CROSSTAB);
				String sheetName = sheetJ.getString(SHEET);
				JSONObject filters = sheetJ.optJSONObject(QbeEngineStaticVariables.FILTERS);
				JSONObject crosstabDefinitionJSON = new JSONObject(crosstabDefinition);
				JSONObject crosstabJSON = new JSONObject(crosstab);	
				
				String calculateFields = content.optString("CF");
				if(calculateFields!=null){
					calculateFieldsJSON = new JSONArray(calculateFields);
				}

				CrossTab cs = getCrosstab(crosstabDefinitionJSON,filters, sheetName, splittingWhereField, calculateFieldsJSON);
				CrosstabXLSXExporterFromJavaObject expCr = new CrosstabXLSXExporterFromJavaObject();
				sheetRow  = expCr.fillAlreadyCreatedSheet(sheet, cs, crosstabJSON, createHelper, sheetRow);
			} else if (sheetType.equalsIgnoreCase(WorkSheetXLSExporter.TABLE)) {

				IDataStore dataStore = getTableDataStore(sheetJ);
				long recCount = dataStore.getRecordsCount();
				recCount = (new Long(recCount)).intValue() + 5;
				int startRow = sheetRow;
				for(int i=0; i<recCount; i++){
					sheet.createRow(sheetRow);
					sheetRow++;
				}
				exporter.designTableInWorksheet(sheet, wb, createHelper, dataStore,startRow);			
			}
		}
		return sheetRow;
	}
	
	
	
	/**
	 * Execute the query active in the engine instance and return
	 * the data store
	 * @return the data store after the execution of the active query
	 */
	private IDataStore getTableDataStore(JSONObject sheetJ) throws Exception {
		JSONObject sheetContentPars = null;
		JSONArray jsonVisibleSelectFields = null;
		String sheetName = sheetJ.getString(SHEET);
		JSONObject sheetContent = sheetJ.optJSONObject(CONTENT);
		sheetContentPars = sheetContent.optJSONObject(CONTENT_PARS);
		// get the visible columns
		if (sheetContentPars != null) {
			jsonVisibleSelectFields = sheetContentPars
					.optJSONArray(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS);
		}
		JSONObject filters = sheetJ.optJSONObject(QbeEngineStaticVariables.FILTERS);
		IDataStore dataStore = executeQuery(jsonVisibleSelectFields, filters, sheetName);
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
	

	public CrossTab getCrosstab(JSONObject crosstabDefinitionJSON, JSONObject optionalFilters,String sheetName, WhereField splittingWhereField, JSONArray calculateFieldsJSON) throws Exception{

		// retrieve engine instance
		WorksheetEngineInstance engineInstance = getEngineInstance();
		Assert.assertNotNull(engineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");

		// persist dataset into temporary table	
		IDataSetTableDescriptor descriptor = this.persistDataSet();
		
		IDataSet dataset = engineInstance.getDataSet();
		// build SQL query against temporary table
		List<WhereField> whereFields = new ArrayList<WhereField>();
		if (!dataset.hasBehaviour(FilteringBehaviour.ID)) {
			/* 
			 * If the dataset had the FilteringBehaviour, data was already filtered on domain values by the FilteringBehaviour itself.
			 * If the dataset hadn't the FilteringBehaviour, we must pust filters on domain values on query to temporary table 
			 */
			Map<String, List<String>> globalFilters = getGlobalFiltersOnDomainValues();
			LogMF.debug(logger, "Global filters on domain values detected: {0}", globalFilters);
			List<WhereField> temp = transformIntoWhereClauses(globalFilters);
			whereFields.addAll(temp);
		}
		
		/* 
		 * We must consider sheet filters anyway because temporary table contains data for all sheets,
		 * but different sheets could have different filters defined on them
		 */
		Map<String, List<String>> sheetFilters = getSheetFiltersOnDomainValues(sheetName);
		LogMF.debug(logger, "Sheet filters on domain values detected: {0}", sheetFilters);
		List<WhereField> temp = transformIntoWhereClauses(sheetFilters);
		whereFields.addAll(temp);

		temp = getOptionalFilters(optionalFilters);
			
		whereFields.addAll(temp);
		
		//ADD THE WHERE FIELD TO SPLIT THE CROSSTAB INTO DIFFERENT SHEET
		if(splittingWhereField!=null){
			whereFields.add(splittingWhereField);
		}
		

		// deserialize crosstab definition
		CrosstabDefinition crosstabDefinition = (CrosstabDefinition) SerializationManager.deserialize(crosstabDefinitionJSON, "application/json", CrosstabDefinition.class);
		crosstabDefinition.setCellLimit(0);//FOR THE EXPORT WE REMOVE THE CELL LIMIT
		
		String worksheetQuery = this.buildSqlStatement(crosstabDefinition, descriptor, whereFields, engineInstance.getDataSource());
		// execute SQL query against temporary table
		logger.debug("Executing query on temporary table : " + worksheetQuery);
		IDataStore dataStore = this.executeWorksheetQuery(worksheetQuery, null, null);
		LogMF.debug(logger, "Query on temporary table executed successfully; datastore obtained: {0}", dataStore);
		Assert.assertNotNull(dataStore, "Datastore obatined is null!!");
		/* since the datastore, at this point, is a JDBC datastore, 
		* it does not contain information about measures/attributes, fields' name and alias...
		* therefore we adjust its metadata
		*/
		this.adjustMetadata((DataStore) dataStore, dataset, descriptor);
		LogMF.debug(logger, "Adjusted metadata: {0}", dataStore.getMetaData());
		logger.debug("Decoding dataset ...");
		dataStore = dataset.decode(dataStore);
		LogMF.debug(logger, "Dataset decoded: {0}", dataStore);
		
		CrossTab crossTab = new CrossTab(dataStore, crosstabDefinition,calculateFieldsJSON);
		
		return crossTab;
	}
	
	/**
	 * Build the sql statement to query the temporary table 
	 * @param crosstabDefinition definition of the crosstab
	 * @param descriptor the temporary table descriptor
	 * @param dataSource the datasource
	 * @param tableName the temporary table name
	 * @return the sql statement to query the temporary table 
	 */
	protected String buildSqlStatement(CrosstabDefinition crosstabDefinition,
			IDataSetTableDescriptor descriptor, List<WhereField> filters, IDataSource dataSource) {
		return CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, descriptor, filters, dataSource);
	}
	
	public List<WhereField> getSplittingFieldValues(JSONObject optionalFilters, String sheetName) throws JSONException{
		
		
		String[] fields = new String[0];
		List<WhereField> splittingWhereFields = new ArrayList<WhereField>();
		
		try {
			if(optionalFilters!=null){
				fields = JSONObject.getNames(optionalFilters);
			}
			
		} catch (Exception e) {
			logger.error("Error loading the splitting filters from the string "+optionalFilters);
		}

		
		for (String field : fields) {
			if(optionalFilters.getString(field).equals(SPLITTING_FILTER)){
				IDataStore ds = getUserSheetFilterValues(sheetName, field);
				JSONDataWriter dataSetWriter = new JSONDataWriter();
				JSONObject gridDataFeed = (JSONObject) dataSetWriter.write(ds);
				JSONArray rows = gridDataFeed.getJSONArray(JSONDataWriter.ROOT);
				
				for(int j=0; j<rows.length(); j++){
					JSONArray ja = new JSONArray();
					ja.put(((JSONObject)(rows.get(j))).get("column_1"));
					JSONObject jo = new JSONObject();
					jo.put(field, ja);
					splittingWhereFields.addAll(transformIntoWhereClauses(jo));
				}

			}
		}
		return splittingWhereFields;
		
		
//		

	}
	
}
