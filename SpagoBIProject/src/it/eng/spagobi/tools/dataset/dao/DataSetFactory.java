/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.dao;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.tools.dataset.bo.CustomDataSet;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.VersionedDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.constants.DataSetConstants;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.DataSourceDAOHibImpl;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataSetFactory {
	
	public static final String JDBC_DS_TYPE = "Query";
	public static final String FILE_DS_TYPE = "File";
	public static final String SCRIPT_DS_TYPE = "Script";
	public static final String JCLASS_DS_TYPE = "Java Class";
	public static final String WS_DS_TYPE = "Web Service";
	public static final String QBE_DS_TYPE = "Qbe";
	public static final String CUSTOM_DS_TYPE = "Custom";
	
	static private Logger logger = Logger.getLogger(DataSetFactory.class);
	
	public static IDataSet toGuiDataSet(SbiDataSet sbiDataSet) {		
		IDataSet guiDataSet;
		
		guiDataSet = new VersionedDataSet();
		
		if(sbiDataSet!=null){
			guiDataSet.setId(sbiDataSet.getId().getDsId());
			guiDataSet.setName(sbiDataSet.getName());
			guiDataSet.setLabel(sbiDataSet.getLabel());
			guiDataSet.setDescription(sbiDataSet.getDescription());	
		
			guiDataSet.setDsMetadata(sbiDataSet.getDsMetadata());
			guiDataSet.setUserIn(sbiDataSet.getUserIn());
			guiDataSet.setDateIn(new Date());
			
			guiDataSet.setId(sbiDataSet.getId().getDsId());

		}

		return guiDataSet;
	}


	public static IDataSet toGuiDataSet(IDataSet dataSet) {		
		IDataSet toReturn  = dataSet;
		
		if(dataSet instanceof FileDataSet){					
			toReturn.setDsType(FILE_DS_TYPE);
		}

		if(dataSet instanceof JDBCDataSet){			
			toReturn.setDsType(JDBC_DS_TYPE);
		}
		
		if(dataSet instanceof QbeDataSet){			
		
			QbeDataSet aQbeDataSet = (QbeDataSet) dataSet;
			aQbeDataSet.setJsonQuery(aQbeDataSet.getJsonQuery());
			aQbeDataSet.setDatamarts(aQbeDataSet.getDatamarts());
			IDataSource iDataSource = aQbeDataSet.getDataSource();
			if (iDataSource!=null){
				aQbeDataSet.setDataSource(iDataSource);
			}
			
			toReturn.setDsType(QBE_DS_TYPE);
		}

		if(dataSet instanceof WebServiceDataSet){			
			toReturn.setDsType(WS_DS_TYPE);
		}

		if(dataSet instanceof ScriptDataSet){			
			toReturn.setDsType(SCRIPT_DS_TYPE);
		}

		if(dataSet instanceof JavaClassDataSet){			
			toReturn.setDsType(JCLASS_DS_TYPE);
		}
		
		if(dataSet instanceof CustomDataSet){			
			toReturn.setDsType(CUSTOM_DS_TYPE);
		}

		toReturn.setId(dataSet.getId());
		toReturn.setName(dataSet.getName());
		toReturn.setLabel(dataSet.getLabel());
		toReturn.setDescription(dataSet.getDescription());	

		// set detail dataset ID
		toReturn.setTransformerId((dataSet.getTransformerId() == null)? null:dataSet.getTransformerId());
		toReturn.setPivotColumnName(dataSet.getPivotColumnName());
		toReturn.setPivotRowName(dataSet.getPivotRowName());
		toReturn.setPivotColumnValue(dataSet.getPivotColumnValue());
		toReturn.setNumRows(dataSet.isNumRows());			
		toReturn.setParameters(dataSet.getParameters());		
		toReturn.setDsMetadata(dataSet.getDsMetadata());		
		
		//set persist values
		toReturn.setPersisted(dataSet.isPersisted());
		toReturn.setDataSourcePersistId(dataSet.getDataSourcePersistId());
		toReturn.setFlatDataset(dataSet.isFlatDataset());
		toReturn.setDataSourceFlatId(dataSet.getDataSourceFlatId());
		toReturn.setFlatTableName(dataSet.getFlatTableName());

		return toReturn;
	}
	
	public static IDataSet toDataSet(SbiDataSet sbiDataSet) {
		IDataSet ds = null;		
		VersionedDataSet versionDS = null;
		String config = JSONUtils.escapeJsonString(sbiDataSet.getConfiguration());
		JSONObject jsonConf  = ObjectUtils.toJSONObject(config);
		try{
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_FILE)){
				ds = new FileDataSet();			
				if (jsonConf.getString(DataSetConstants.FILE_TYPE) != null){
					((FileDataSet)ds).setFileType(jsonConf.getString(DataSetConstants.FILE_TYPE));		
				}
				((FileDataSet)ds).setFileName(jsonConf.getString(DataSetConstants.FILE_NAME));		
				ds.setDsType(FILE_DS_TYPE);
			}
	
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_QUERY)) { 
				ds=new JDBCDataSet();
				((JDBCDataSet)ds).setQuery(jsonConf.getString(DataSetConstants.QUERY));
				((JDBCDataSet)ds).setQueryScript(jsonConf.getString(DataSetConstants.QUERY_SCRIPT));
				((JDBCDataSet)ds).setQueryScriptLanguage(jsonConf.getString(DataSetConstants.QUERY_SCRIPT_LANGUAGE));				
				DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
				IDataSource dataSource= dataSourceDao.loadDataSourceByLabel(jsonConf.getString(DataSetConstants.DATA_SOURCE));				
				((JDBCDataSet)ds).setDataSource(dataSource);				
				ds.setDsType(JDBC_DS_TYPE);
			}
	
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_WS)) { 			
				ds=new WebServiceDataSet();
				((WebServiceDataSet)ds).setAddress(jsonConf.getString(DataSetConstants.WS_ADDRESS));
				((WebServiceDataSet)ds).setOperation(jsonConf.getString(DataSetConstants.WS_OPERATION));
				ds.setDsType(WS_DS_TYPE);
			}
	
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_SCRIPT)) {	
				ds=new ScriptDataSet();
				((ScriptDataSet)ds).setScript(jsonConf.getString(DataSetConstants.SCRIPT));
				((ScriptDataSet)ds).setScriptLanguage(jsonConf.getString(DataSetConstants.SCRIPT_LANGUAGE));
				ds.setDsType(SCRIPT_DS_TYPE);
			}
	
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_JCLASS)) { 			
				ds=new JavaClassDataSet();
				((JavaClassDataSet)ds).setClassName(jsonConf.getString(DataSetConstants.JCLASS_NAME));
				ds.setDsType(JCLASS_DS_TYPE);
			}
			
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_CUSTOM)) { 			
				ds=new CustomDataSet();
				((CustomDataSet)ds).setCustomData(jsonConf.getString(DataSetConstants.CUSTOM_DATA));
				((CustomDataSet)ds).setJavaClassName(jsonConf.getString(DataSetConstants.JCLASS_NAME));
				ds.setDsType(CUSTOM_DS_TYPE);
			}
			
			if(sbiDataSet.getType().equalsIgnoreCase(DataSetConstants.DS_QBE)) { 		
				ds = new QbeDataSet();				
				((QbeDataSet)ds).setJsonQuery(jsonConf.getString(DataSetConstants.QBE_JSON_QUERY));
				((QbeDataSet)ds).setDatamarts( jsonConf.getString(DataSetConstants.QBE_DATAMARTS));
				DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
				IDataSource dataSource= dataSourceDao.loadDataSourceByLabel(jsonConf.getString(DataSetConstants.QBE_DATA_SOURCE));									
				if (dataSource!=null){				
					((QbeDataSet)ds).setDataSource(dataSource);				
				}			
				ds.setDsType(QBE_DS_TYPE);
				
			}
		}catch (Exception e){
			logger.error("Error while defining dataset configuration.  Error: " + e.getMessage());
		}
		
		if(ds!=null){		
			if (sbiDataSet.getCategory()!= null){
				ds.setCategoryCd(sbiDataSet.getCategory().getValueCd());
				ds.setCategoryId(sbiDataSet.getCategory().getValueId());
			}
			ds.setConfiguration(sbiDataSet.getConfiguration());
			ds.setId(sbiDataSet.getId().getDsId());
			ds.setName(sbiDataSet.getName());
			ds.setLabel(sbiDataSet.getLabel());
			ds.setDescription(sbiDataSet.getDescription());				
			
			ds.setTransformerId((sbiDataSet.getTransformer()==null)?null:sbiDataSet.getTransformer().getValueId());
			ds.setTransformerCd((sbiDataSet.getTransformer()==null)?null:sbiDataSet.getTransformer().getValueCd());
			ds.setPivotColumnName(sbiDataSet.getPivotColumnName());
			ds.setPivotRowName(sbiDataSet.getPivotRowName());
			ds.setPivotColumnValue(sbiDataSet.getPivotColumnValue());
			ds.setNumRows(sbiDataSet.isNumRows());
	
			ds.setParameters(sbiDataSet.getParameters());		
			ds.setDsMetadata(sbiDataSet.getDsMetadata());		
	
			if(ds.getPivotColumnName() != null 
					&& ds.getPivotColumnValue() != null
					&& ds.getPivotRowName() != null){
				ds.setDataStoreTransformer(
						new PivotDataSetTransformer(ds.getPivotColumnName(), ds.getPivotColumnValue(), ds.getPivotRowName(), ds.isNumRows()));
			}
			ds.setPersisted(sbiDataSet.isPersisted());
			ds.setDataSourcePersistId((sbiDataSet.getDataSourcePersist()==null)?null:sbiDataSet.getDataSourcePersist().getDsId());
			ds.setFlatDataset(sbiDataSet.isFlatDataset());
			ds.setDataSourceFlatId((sbiDataSet.getDataSourceFlat()==null)?null:sbiDataSet.getDataSourceFlat().getDsId());
			ds.setFlatTableName(sbiDataSet.getFlatTableName());
			ds.setOwner(sbiDataSet.getOwner());
			ds.setPublic(sbiDataSet.isPublicDS());
			ds.setUserIn(sbiDataSet.getCommonInfo().getUserIn());
			ds.setDateIn(sbiDataSet.getCommonInfo().getTimeIn());
			versionDS = new VersionedDataSet(ds, Integer.valueOf(sbiDataSet.getId().getVersionNum()), sbiDataSet.isActive());	
		}
		return versionDS;
	}
}
