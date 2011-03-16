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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.services.AbstractConsoleEngineAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.UserProfileUtils;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ExportAction extends AbstractConsoleEngineAction {
	
	// INPUT PARAMETERS
	public static final String MIME_TYPE = "mimeType";
	public static final String RESPONSE_TYPE = "responseType";
	public static final String DATASET_LABEL = "datasetLabel";
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
		String mimeType;
		String responseType;
		JSONArray jsonArray;
		
		IDataSet dataSet;
		IDataStore dataStore;
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
				
			String test = getAttributeAsString(META);
			logger.debug("Parameter [" + META + "] is equal to [" + test + "]");
			
			jsonArray = this.getAttributeAsJSONArray(META);
			logger.debug("Parameter [" + META + "] is equal to [" + jsonArray.toString(4) + "]");
		
			dataSet = null;
			try {
				dataSet = getConsoleEngineInstance().getDataSetServiceProxy().getDataSetByLabel( dataSetLabel );
			} catch(Throwable t) {
				throw new SpagoBIServiceException("Impossible to find a dataset whose label is [" + dataSetLabel + "]", t);
			}
			Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
				
			
			Assert.assertNotNull(dataSet, "Impossible to find a dataset whose label is [" + dataSetLabel + "]");
			Map params = getConsoleEngineInstance().getAnalyticalDrivers();
			dataSet.setParamsMap(params);
			dataSet.setUserProfileAttributes(UserProfileUtils.getProfileAttributes( (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE)));
			dataSet.loadData();
			dataStore = dataSet.getDataStore();
			Assert.assertNotNull(dataStore, "The dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			
			
			// dataStore decoration ....
			Object resultNumber = dataStore.getMetaData().getProperty("resultNumber");
			if(resultNumber == null) dataStore.getMetaData().setProperty("resultNumber", new Integer((int)dataStore.getRecordsCount()));
			
			if(jsonArray != null && jsonArray.length() > 0) {
				int fieldNo = dataStore.getMetaData().getFieldCount();
				for(int i = 0; i < fieldNo; i++) {
					dataStore.getMetaData().getFieldMeta(i).setProperty("visible", Boolean.FALSE);
				}
				
				List actionColumns = new ArrayList();
				
				for(int i = 0; i < jsonArray.length(); i++) {
					String fieldName = jsonArray.getJSONObject(i).optString("name", null);
					String fieldHeader = jsonArray.getJSONObject(i).optString("header", null);
					Boolean isActionColumn = jsonArray.getJSONObject(i).optBoolean("actionColumn", Boolean.FALSE);
					
					if(isActionColumn.booleanValue() == false) {
						if(StringUtilities.isEmpty(fieldName)) {
							logger.warn("no name for column: " + jsonArray.getJSONObject(i).toString(4));
							continue;
						}
						int fieldIndex = dataStore.getMetaData().getFieldIndex(fieldName);
						if(fieldIndex < 0){
							logger.warn("dataStore does not conatin a column named [" + fieldIndex + "]");
							continue;
						}
						if(jsonArray.getJSONObject(i).optBoolean("hidden", true) == false) {
							dataStore.getMetaData().getFieldMeta(fieldIndex).setProperty("visible", Boolean.TRUE);
							dataStore.getMetaData().getFieldMeta(fieldIndex).setAlias(fieldHeader);
						}
					} else {
						String actionConfig = jsonArray.getJSONObject(i).optString("actionConfig");
						logger.debug("Parameter [actionConfig] is equal to [" + actionConfig + "]");
						Assert.assertNotNull(actionConfig, "Parameter [actionConfig]connot be undefined if parameter [actionColumn] is true");
						JSONObject actionConfigJson = new JSONObject(actionConfig);
						actionColumns.add( actionConfigJson );
					}
				}
				
				dataStore.getMetaData().setProperty("actionColumns", actionColumns);
			}
			
			
			
			params = new HashMap();
			params.put("pagination", "false" );
			
			TemplateBuilder templateBuilder = new TemplateBuilder(dataStore, params);
			String templateContent = templateBuilder.buildTemplate();
			logger.debug(templateContent);
			try {
				reportFile = File.createTempFile("report", ".rpt");
			} catch (IOException ioe) {
				throw new SpagoBIEngineException("Impossible to create a temporary file to store the template generated on the fly", ioe);
			}
			
			JasperReportRunner runner = new JasperReportRunner();
			JRSpagoBIDataStoreDataSource dataSource = new JRSpagoBIDataStoreDataSource( dataStore );
			Locale locale = this.getLocale();
			try {
				runner.run( templateContent, reportFile, mimeType, dataSource, locale);
			}  catch (Exception e) {
				throw new SpagoBIEngineException("Impossible compile or to export the report", e);
			}
			
			
			writeBackResponseInline = RESPONSE_TYPE_INLINE.equalsIgnoreCase(responseType);
			//fileExtension = MimeUtils.getFileExtension( mimeType );
			//fileExtension = fileExtension != null? fileExtension: DEFAULT_FILE_EXTENSION;
			fileName = "report"; //, +  "." + fileExtension;
			
			
			try {				
				writeBackToClient(reportFile, null, writeBackResponseInline, fileName, mimeType);
			} catch (IOException ioe) {
				throw new SpagoBIEngineException("Impossible to write back the responce to the client", ioe);
			}	
			
			
		} catch(Throwable t) {
			logger.error("Impossible to export doc", t);
		} finally {
			logger.debug("OUT");
		}
	}
}
