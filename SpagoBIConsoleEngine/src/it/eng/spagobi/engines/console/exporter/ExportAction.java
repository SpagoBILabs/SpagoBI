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
package it.eng.spagobi.engines.console.exporter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineConfig;
import it.eng.spagobi.engines.console.services.AbstractConsoleEngineAction;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;

import utilities.DataSourceUtilities;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExportAction extends AbstractConsoleEngineAction {
	
	// INPUT PARAMETERS
	public static final String MIME_TYPE = "mimeType";
	public static final String RESPONSE_TYPE = "responseType";
	public static final String DATASET_LABEL = "datasetLabel";
	public static final String DATASET_HEADERS_LABEL = "datasetHeadersLabel";
	public static final String LOCALE = "LOCALE";
	public static final String META = "meta";
	
	// misc
	public static final String RESPONSE_TYPE_INLINE = "inline";
	public static final String RESPONSE_TYPE_ATTACHMENT = "attachment";
	
	//public static final String DEFAULT_MIME_TYPE = "text/plain";
	//public static final String DEFAULT_FILE_EXTENSION = "txt";
	public static final String DEFAULT_MIME_TYPE = "text/plain";
	public static final String DEFAULT_FILE_EXTENSION = "txt";
	
	
	public static final String SERVICE_NAME = "EXPORT_ACTION";
	
	// logger component
	private static Logger logger = Logger.getLogger(ExportAction.class);
	
	public void service(SourceBean request, SourceBean response) {
		
		File reportFile;
		String dataSetLabel;
		String dataSetHeadersLabel;
		String mimeType;
		String responseType;
		String locale;
		JSONArray jsonArray;
		
		IDataSet dataSet;
		IDataSet dataSetHeaders;
		IDataStore dataStore;
		IDataStore dataStoreHeaders;
		IDataSource dataSource;
		JSONObject dataSetJSON;
		
		String fileExtension;
		String fileName;
		boolean writeBackResponseInline;
		
		logger.debug("IN");
		
		try {
			super.service(request,response);
			
			Assert.assertNotNull(getConsoleEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			Assert.assertNotNull(getConsoleEngineInstance().getDataSetServiceProxy(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of DatasetServiceProxy class");			

			dataSetLabel = getAttributeAsString( DATASET_LABEL );
			logger.debug("Parameter [" + DATASET_LABEL + "] is equals to [" + dataSetLabel + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( dataSetLabel ), "Parameter [" + DATASET_LABEL + "] cannot be null or empty");
				
			dataSetHeadersLabel = getAttributeAsString( DATASET_HEADERS_LABEL );
			logger.debug("Parameter [" + DATASET_HEADERS_LABEL + "] is equals to [" + dataSetHeadersLabel + "]");
			
			locale = getAttributeAsString( LOCALE );
			logger.debug("Parameter [" + LOCALE + "] is equals to [" + locale + "]");
			
			mimeType = getAttributeAsString( MIME_TYPE );
			logger.debug("Parameter [" + MIME_TYPE + "] is equal to [" + mimeType + "]");
			if(mimeType == null) {
				logger.warn("Parameter [" + MIME_TYPE + "] has not been valorized");
				mimeType = DEFAULT_MIME_TYPE;
				logger.debug("Parameter [" + MIME_TYPE + "] has been set equal to [" + mimeType + "]");
			}
			
			responseType = getAttributeAsString( RESPONSE_TYPE );
			logger.debug("Parameter [" + RESPONSE_TYPE + "] is equal to [" + responseType + "]");
			if(!RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType) && !RESPONSE_TYPE_ATTACHMENT.equalsIgnoreCase(responseType)) {
				logger.warn("Value [" + responseType + "] is not a valid for parameter [" + RESPONSE_TYPE + "]");
				responseType = RESPONSE_TYPE_ATTACHMENT;
				logger.debug("Parameter [" + RESPONSE_TYPE + "] has been set equal to [" + responseType + "]");
			}
				
			ConsoleEngineConfig conf = ConsoleEngineConfig.getInstance();
			
			String test = getAttributeAsString(META);
			logger.debug("Parameter [" + META + "] is equal to [" + test + "]");
			Object m = getAttribute(META);
			try{
				jsonArray = getAttributeAsJSONArray(META);
				logger.debug("Parameter [" + META + "] is equal to [" + jsonArray.toString(4) + "]");
			}catch(Throwable t){
				logger.debug("Not a json array: "+test);
				jsonArray = new JSONArray();
				JSONObject obj = getAttributeAsJSONObject(META);
				jsonArray.put(obj);
			}
			dataSet = null;
			try {
				dataSet = getConsoleEngineInstance().getDataSetServiceProxy().getDataSetByLabel( dataSetLabel );
			} catch(Throwable t) {
				throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetLabel + "]", t);
			}
			Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
				
			//read the dataset with headers
			dataSetHeaders = null;
			dataStoreHeaders = null;
			if (dataSetHeadersLabel != null && !dataSetHeadersLabel.equals("")){				
				try {
					dataSetHeaders = getConsoleEngineInstance().getDataSetServiceProxy().getDataSetByLabel( dataSetHeadersLabel );
				} catch(Throwable t) {
					throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetHeadersLabel + "]", t);
				}
				Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetHeadersLabel + "]");
				Map params = getConsoleEngineInstance().getAnalyticalDrivers();
				dataSetHeaders.setParamsMap(params);
				dataSetHeaders.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE)));
				dataSetHeaders.loadData();
				dataStoreHeaders = dataSetHeaders.getDataStore();
				Assert.assertNotNull(dataStoreHeaders, "The dataStore returned by loadData method of the class [" + dataSetHeaders.getClass().getName()+ "] cannot be null");
			}
			
			Map params = getConsoleEngineInstance().getAnalyticalDrivers();
			dataSet.setParamsMap(params);
			dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE)));
			dataSet.loadData();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			
			
			// dataStore decoration ....
			Object resultNumber = dataStore.getMetaData().getProperty("resultNumber");
			if(resultNumber == null) dataStore.getMetaData().setProperty("resultNumber", new Integer((int)dataStore.getRecordsCount()));
			IDataSource ds = getConsoleEngineInstance().getDataSource();				
			DataSourceUtilities dsu = new DataSourceUtilities(ds);
			Vector extractedFields = dsu.readFields(dataSet.getQuery().toString());
			List extractedFieldsMetaData = new ArrayList<IFieldMetaData>();
			if(jsonArray != null && jsonArray.length() > 0) {
				int fieldNo = dataStore.getMetaData().getFieldCount();
				for(int i = 0; i < fieldNo; i++) {
					dataStore.getMetaData().getFieldMeta(i).setProperty("visible", Boolean.FALSE);
				}
				
				List actionColumns = new ArrayList();
				for(int k = 0; k < jsonArray.length(); k++){
					JSONObject resultHeaders = jsonArray.getJSONObject(k);
					Iterator it = resultHeaders.keys();
					while(it.hasNext()) {
						String key = (String)it.next();
						JSONObject header = resultHeaders.getJSONObject(key);
						String fieldHeader = header.optString("header", "");
						String fieldHeaderType =  header.optString("headerType", "");		
	//					// in case of dynamic headers gets the value from the dataset (of data)
						if (fieldHeaderType.equalsIgnoreCase("dataset")){
							int posHeader = dataStore.getMetaData().getFieldIndex(fieldHeader);
							int fieldValsize = ((List)dataStore.getFieldValues(posHeader)).size();
							if(fieldValsize != 0){
								fieldHeader =((List)dataStore.getFieldValues(posHeader)).get(0).toString();
							}							
						}else if (fieldHeaderType.equalsIgnoreCase("datasetI18N") && dataStoreHeaders != null){
							//gets the header value from the specific dataset (only with labels: code - label - locale) 
							int headersFieldNo = dataStoreHeaders.getMetaData().getFieldCount();
							//adds index informations to the metadata properties
							for(int i = 0; i < headersFieldNo; i++) {
								dataStoreHeaders.getMetaData().getFieldMeta(i).setProperty("index", i+1);
							}
							// gets the specific label using the code and the locale
							int posCode = (dataStoreHeaders.getMetaData().getFieldIndex("code") != -1) ?
									dataStoreHeaders.getMetaData().getFieldIndex("code") : 
									dataStoreHeaders.getMetaData().getFieldIndex("CODE");
							
							int posLabel = (dataStoreHeaders.getMetaData().getFieldIndex("label") != -1) ?
									dataStoreHeaders.getMetaData().getFieldIndex("label") : 
									dataStoreHeaders.getMetaData().getFieldIndex("LABEL");
									
							int posLocale = (dataStoreHeaders.getMetaData().getFieldIndex("locale") != -1) ?
									dataStoreHeaders.getMetaData().getFieldIndex("locale") : 
									dataStoreHeaders.getMetaData().getFieldIndex("LOCALE");
									
							List filterCodes = new ArrayList<String>();
							List filterValues = new ArrayList<String>();
							filterCodes.add(posCode);
							filterValues.add(fieldHeader);
							filterCodes.add(posLocale);
							filterValues.add(locale);

							List headersFieldValues = (List)dataStoreHeaders.findRecords(filterCodes, filterValues);
							if (headersFieldValues != null && headersFieldValues.size() > 0){
								Record headerRec = (Record)headersFieldValues.get(0);
								IField headerField = null;
								if (headerRec != null){
									headerField = (IField) headerRec.getFieldAt(posLabel);
								}
								String label = (headerField != null) ? headerField.toString() : "";
									
								if(label != null && !("").equalsIgnoreCase(label)){
									fieldHeader = label;
								}
							}
						}else if (fieldHeaderType.equalsIgnoreCase("I18N")){						
							//gets the header value from the locale files 
							/*
							EnginConf tmp = conf.getEngineConfig();
							System.out.println(tmp);
							*/
							logger.debug("Export headers by locale file doesn't supported yet!");
						}
						
						Field headerF = new Field(fieldHeader, "java.lang.String", 100);
						extractedFields.add(headerF);
						for(int i = 0; i < fieldNo; i++) {
							IFieldMetaData fFound = dataStore.getMetaData().getFieldMeta(i);
							if(fFound.getName().equals(key)){
								fFound.setProperty("visible", Boolean.TRUE);
								fFound.setAlias(fieldHeader);
								fFound.setProperty("index", k);							
								extractedFieldsMetaData.add(fFound);
								break;
							}
							
						}
	
					}


				}
				
				dataStore.getMetaData().setProperty("actionColumns", actionColumns);
			}
			params = new HashMap();
			params.put("pagination", "false" );
			
			
			if( "application/vnd.ms-excel".equalsIgnoreCase( mimeType ) ) {

				Exporter exp = new Exporter(dataStore);
				
				long numberOfRows = dataStore.getRecordsCount();
				String configLimit = (String)conf.getProperty("EXPORT_ROWS_LIMIT");
				if(configLimit == null){
					configLimit = "65000";
				}
				if(numberOfRows >= Long.parseLong(configLimit)){
					numberOfRows = Long.parseLong(configLimit);
					logger.info("Result set exceded maximum rows number "+configLimit);
					
				}
				
				exp.setNumberOfRows(numberOfRows);
				
				exp.setExtractedFields(extractedFields);
				exp.setExtractedFieldsMetaData(extractedFieldsMetaData);
				
				Workbook wb = exp.exportInExcel();
				
				File file = File.createTempFile("console", ".xls");
				FileOutputStream stream = new FileOutputStream(file);
				wb.write(stream);
				stream.flush();
				stream.close();
				try {				
					writeBackToClient(file, null, true, "console.xls", mimeType);
				} catch (IOException ioe) {
					throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
				}	finally{
					if(file != null && file.exists()) {
						try {
							file.delete();
						} catch (Exception e) {
							logger.warn("Impossible to delete temporary file " + file, e);
						}
					}
				}
		
			}

			
		} catch(Throwable t) {
			logger.error("Impossible to export doc", t);
		} finally {
			logger.debug("OUT");
		}
	}
}

