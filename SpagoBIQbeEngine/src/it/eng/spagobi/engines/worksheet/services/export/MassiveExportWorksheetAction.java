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
package it.eng.spagobi.engines.worksheet.services.export;

import it.eng.qbe.query.WhereField;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.bo.Field;
import it.eng.spagobi.engines.worksheet.bo.Filter;
import it.eng.spagobi.engines.worksheet.bo.Sheet;
import it.eng.spagobi.engines.worksheet.bo.SheetContent;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.engines.worksheet.bo.WorksheetFieldsOptions;
import it.eng.spagobi.engines.worksheet.exporter.WorkSheetXLSExporter;
import it.eng.spagobi.engines.worksheet.template.WorksheetTemplate;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.engines.worksheet.widgets.TableDefinition;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MassiveExportWorksheetAction extends ExportWorksheetAction {

	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(MassiveExportWorksheetAction.class);

	public static final String ENGINE_NAME = "SpagoBIWorksheetEngine";

	public static final String SPLITTING_FILTER = "SPLITTING_FILTER";
	
	
	

	boolean splittingFilter = false;

	public void init(SourceBean config) {
		super.init(config);
	}


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
			String splittingFilterS = getAttributeAsString( SPLITTING_FILTER );
			if(splittingFilterS != null) splittingFilter = Boolean.valueOf(splittingFilterS);

			WorksheetEngineInstance inst = getEngineInstance();

			logger.debug("Worksheet instance created "+inst.toString());

			worksheetJSON = ((WorkSheetDefinition)(inst.getAnalysisState())).getConf();

			logger.debug(WORKSHEETS + ": " + worksheetJSON);

			writeBackResponseInline = RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType);

			if( "application/vnd.ms-excel".equalsIgnoreCase( mimeType ) ) {
				logger.debug("Export in excel");
				exportFile = File.createTempFile("worksheet", ".xls");
				FileOutputStream stream = new FileOutputStream(exportFile);
				try {
					JSONObject exportJSON = convertToExportJSON(inst, worksheetJSON);
					logger.debug("JSOn modified as for export: "+exportJSON.toString());
					exportToXLS(exportJSON, null, null, stream);
					logger.debug("Export executed");

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




	//
	//
	//	/** TODO: has to evaluate all parameters possibilities
	//	 * 
	//	 * @param engineInstance
	//	 * @return
	//	 * @throws JSONException
	//	 */
	//
	//
	public JSONObject convertToExportJSON(WorksheetEngineInstance engineInstance, JSONObject worksheetJSON) throws JSONException{
		logger.debug("IN");

		WorksheetTemplate template = engineInstance.getTemplate();
		WorkSheetDefinition definition = template.getWorkSheetDefinition();	
		List<Field> fields =definition.getAllFields();
		List<Sheet> sheets = definition.getSheets();
		Integer sheetsNum = sheets.size();

		JSONArray arraySheets = worksheetJSON.getJSONArray("sheets");

		JSONObject tobuild = new JSONObject();
		JSONArray exportedSheetsJSON = new JSONArray();

		// *** SHEETS
		for (int i = 0; i < arraySheets.length(); i++) {
			JSONObject sheetObj = (JSONObject)arraySheets.get(i);
			JSONObject sheetObjContent = sheetObj.getJSONObject("content");

			String sheetName = sheetObj.getString("name");

			Sheet sheet = sheets.get(i);
			List<Filter> filters = sheet.getFilters();

			JSONObject sheetToInsert = new JSONObject();
			JSONObject contentJSON = null;

			// get the content
			String sheetType = null;
			SheetContent content= sheet.getContent();
			if(content instanceof TableDefinition){
				contentJSON = new JSONObject();
				sheetType = WorkSheetXLSExporter.TABLE;
				// PARS is present only with table. TODO why?
				contentJSON.put("PARS", sheetObjContent );
			}
			else if(content instanceof CrosstabDefinition){
				sheetType = WorkSheetXLSExporter.CROSSTAB;
				contentJSON = sheetObjContent;

			}
			else{
				// others not handled
			}

			contentJSON.put("SHEET_TYPE", sheetType);

			sheetToInsert.put("sheetName", sheetName);
			sheetToInsert.put(WorkSheetXLSExporter.CONTENT, contentJSON);
			sheetToInsert.put(WorkSheetXLSExporter.HEADER, sheet.getHeader());
			sheetToInsert.put(WorkSheetXLSExporter.FOOTER, sheet.getFooter());

			// build filters!
			JSONArray filtersJSON = new JSONArray();

			if(filters.size()>1)
			{			
				for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
					Filter filter = (Filter) iterator.next();
					JSONObject filterJSON = new JSONObject();
					// if filter is splittingValue do not put values but splittingValue!
					if(splittingFilter==true && filter.isSplittingFilter()){
						filterJSON.put(filter.getEntityId(), "splittingFilter");
					}
					else{
						filterJSON.put(filter.getEntityId(), filter.getValues());				
					}
					filtersJSON.put(filterJSON);
				}
				sheetToInsert.put("FILTERS", filtersJSON);
			}
			else if(filters.size() == 1){				// SINGLE FILTER CASE
					JSONObject filterJSON = new JSONObject();
					Filter filter = filters.get(0);
					if(splittingFilter==true && filter.isSplittingFilter()){
						filterJSON.put(filter.getEntityId(), "splittingFilter");
					}
					else{
						filterJSON.put(filter.getEntityId(), filter.getValues());				
					}
					sheetToInsert.put("FILTERS", filterJSON);
			}

			exportedSheetsJSON.put(sheetToInsert);
		}
			// *** end SHEETs
		
		tobuild.put("SHEETS_NUM", sheetsNum.toString());

		tobuild.put(ExportWorksheetAction.EXPORTED_SHEETS, exportedSheetsJSON);

		// Additional information
		//WorksheetFieldsOptions fieldOptions = definition.getFieldsOptions();
		JSONArray fielsdOption = worksheetJSON.getJSONArray(ExportWorksheetAction.FIELDS_OPTIONS);
		JSONObject additionalData = new JSONObject();
		additionalData.put(ExportWorksheetAction.FIELDS_OPTIONS, fielsdOption);
		tobuild.put(ExportWorksheetAction.WORKSHEETS_ADDITIONAL_DATA, additionalData);
		
		//System.out.println(tobuild);
		logger.debug(tobuild);
		logger.debug("OUT");
		return tobuild;

	}


	/** overridden to use splittingFilter parametrical
	 * 
	 */


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
			if(optionalFilters.getString(field).equals(ExportWorksheetAction.SPLITTING_FILTER) && splittingFilter == true){
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
