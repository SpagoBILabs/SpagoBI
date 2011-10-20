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

package it.eng.spagobi.tools.dataset.bo;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/** user defines a javaClass Name (extending IDAtaSet) and a set of properties, written as JSON Object, that are translated in a map
 * Dataset Execution makes inst5ance of user class
 * @author gavardi
 *
 */

public class CustomDataSet extends ConfigurableDataSet {

	String customData;
	String javaClassName;

	Map customDataMap = null;

	IDataSet classToLaunch;

	public static String DS_TYPE = "SbiCustomDataSet";

	private static transient Logger logger = Logger.getLogger(CustomDataSet.class);


	public CustomDataSet() {
		super();
	}

	public CustomDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);

		setCustomData( dataSetConfig.getCustomData() );
		setJavaClassName( dataSetConfig.getJavaClassName() );
	}

	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;

		sbd = super.toSpagoBiDataSet();

		sbd.setType( DS_TYPE );

		sbd.setCustomData( getCustomData() );
		sbd.setJavaClassName( getJavaClassName() );

		return sbd;
	}

	public IDataSet instantiate(){
		logger.debug("IN");
		Class classRet = null;
		try {
			classRet = Class.forName(javaClassName);
		} catch (ClassNotFoundException e) {
			logger.error("Could not find class "+javaClassName);
			throw new SpagoBIRuntimeException("Could not find class "+javaClassName, e);	
		}
		Object obj = null;
		try {
			obj = classRet.newInstance();
		} catch (InstantiationException e) {
			logger.error("Could not locate class "+javaClassName);
			throw new SpagoBIRuntimeException("Could not locad class "+javaClassName, e);	
		} catch (IllegalAccessException e) {
			logger.error("Could not locad class "+javaClassName);		
			throw new SpagoBIRuntimeException("Could not locate class "+javaClassName, e);	
		}

		if(!(obj instanceof AbstractDataSet)){
			logger.error("class "+javaClassName+ "does not extends AbstractDataset as should do");
			throw new SpagoBIRuntimeException("class "+javaClassName+ "does not extends AbstractDataset as should do");
		}

		IDataSet toreturn = (IDataSet) obj;
		toreturn.setDsMetadata(getDsMetadata());
		toreturn.setMetadata(getMetadata());
		toreturn.setParamsMap(getParamsMap());
		LogMF.debug(logger, "Setting properties into dataset : {0}", this.customDataMap);
		toreturn.setProperties(this.customDataMap);

		logger.debug("OUT");
		return (IDataSet) obj;

	}


	@Override
	public IDataStore test() {
		if(classToLaunch == null)
			classToLaunch = instantiate();

		return classToLaunch.test();
	}

	@Override
	public IDataStore test(int offset, int fetchSize, int maxResults) {
		if(classToLaunch == null)
			classToLaunch = instantiate();
		return classToLaunch.test(offset, fetchSize, maxResults);
	}


	public void setCustomData(String customData) {
		this.customData = customData;
		this.customDataMap = convertStringToMap(customData);
	}

	public String getCustomData() {
		return customData;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void loadData() {
		logger.debug("IN");
		if(classToLaunch == null)
			classToLaunch = instantiate();
		classToLaunch.loadData();
		logger.debug("OUT");
	}



	public void loadData(int offset, int fetchSize, int maxResults) {
		logger.debug("IN");
		if(classToLaunch == null)
			classToLaunch = instantiate();
		classToLaunch.loadData(offset, fetchSize, maxResults);
		logger.debug("OUT");

	}

	public IDataStore getDataStore() {

		if(classToLaunch == null)
			classToLaunch = instantiate();
		return classToLaunch.getDataStore();
	}



	private Map convertStringToMap(String _customData) {
		LogMF.debug(logger, "IN : {0}", _customData);
		Map toInsert = new HashMap<String, Object>();
		try {
			if (_customData != null && !_customData.equals("")) {
				JSONObject jsonObject = new JSONObject(_customData);

				String[] names = JSONObject.getNames(jsonObject);

				for (int i = 0; i < names.length; i++) {
					String nm = names[i];
					Object value = jsonObject.get(nm);
					logger.debug("Property read: key is [" + nm
							+ "], value is [" + value + "]");
					toInsert.put(nm, value);
				}
			}
		} catch (Exception e) {
			logger.error("cannot parse to Map the Json string " + customData, e);
		}
		LogMF.debug(logger, "OUT : {0}", toInsert);
		return toInsert;

	}



	/**
	 *  Methos used to instantiate user class and set theere properties.
	 * @throws EMFUserError 
	 */
	public void init() throws EMFUserError{
		try{
			classToLaunch = (IDataSet) Class.forName( javaClassName ).newInstance();
			classToLaunch.setProperties(customDataMap);
			classToLaunch.setParamsMap(getParamsMap());
			classToLaunch.setDsMetadata(getDsMetadata());
		}
		catch (ClassCastException e) {
			logger.error("Class cast ecepstion, check this class implements IDAtaset "+javaClassName, e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9228);
		}
		catch (Exception e) {
			logger.error("Error in loading class "+javaClassName, e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9217);
		}

		logger.debug("OUT");
	}

	public IDataSet getClassToLaunch() {
		return classToLaunch;
	}

	public void setClassToLaunch(IDataSet classToLaunch) {
		this.classToLaunch = classToLaunch;
	}


	
	
	
//	IDataSet iDataSet = DAOFactory.getDataSetDAO().loadActiveDataSetByLabel(dataSet.getLabel());
//
//	IDataStore dsStore = iDataSet.test();
//	if(dsStore != null){
//		IMetaData meta = dsStore.getMetaData();
//
//		DatasetMetadataParser metadataParser = new DatasetMetadataParser();
//		String xml = metadataParser.metadataToXML(dsStore);
//
//		iDataSet.setDsMetadata(xml);
//		dataSet.setDsId(iDataSet.getId());
//		GuiDataSetDetail guiDataSetDetail = dataSet.getActiveDetail();
//		guiDataSetDetail.setDsMetadata(xml);			
	
	
	
	@Override
	public IMetaData getMetadata() {
		IMetaData metadata = null;
		try {
			// search if dsMetadaa exist, otherwise calculate them
			if(dsMetadata != null && !dsMetadata.equals("")){
				DatasetMetadataParser dsp = new DatasetMetadataParser();
				metadata = dsp.xmlToMetadata(dsMetadata);
				
			}
			else{
				
//				IDataStore dsStore = test();
//				if(dsStore != null){
//					metadata = dsStore.getMetaData();
//				}
			}
		} catch (Exception e) {
			logger.error("Error loading the metadata",e);
			throw new SpagoBIEngineRuntimeException("Error loading the metadata",e);
		}
		return metadata;
	}

	


//	@Override
//	public Map<String, List<String>> getDomainDescriptions(
//			Map<String, List<String>> codes) {
//		if(classToLaunch == null)
//			classToLaunch = instantiate();
//		
//		return classToLaunch.getDomainDescriptions(codes);
//	}
//	
	
	

	@Override
	public IDataStore getDomainValues(String fieldName, Integer start,
			Integer limit, IDataStoreFilter filter) {
		if(classToLaunch == null)
			classToLaunch = instantiate();		
		return classToLaunch.getDomainValues(fieldName, start, limit, filter);
	}

	@Override
	public IDataStore decode(IDataStore datastore) {
		if(classToLaunch == null)
			classToLaunch = instantiate();		
		return classToLaunch.decode(datastore);
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName,
			Connection connection) {
		if(classToLaunch == null)
			classToLaunch = instantiate();		
		return classToLaunch.persist(tableName,
				connection) ;
	}

	
	
	

}

