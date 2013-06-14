/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.dataset.persist.PersistedTableManager;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.sql.SQLStatementConditionalOperators;
import it.eng.spagobi.utilities.sql.SQLStatementConditionalOperators.IConditionalOperator;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors 
 * 		Angelo Bernabei (angelo.bernabei@eng.it)
 * 		Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractDataSet implements IDataSet {

    private int id;
    private String name;
    private String description;
    private String label;
    private Integer categoryId;
    private String categoryCd;
    
    // duplication ahead!
    private String parameters;
    private Map paramsMap;
    Map<String, Object> properties;
    
    // Transformer attributes (better to remove them. 
    // They should be stored only into dataSetTransformer (see above)
    protected Integer transformerId;
    protected String transformerCd;
    protected String pivotColumnName;
    protected String pivotRowName;
    protected String pivotColumnValue;
    protected boolean numRows;
    
    protected IDataStoreTransformer dataSetTransformer;
    
    // hook for extension points
    private Map behaviours;
    
    private String dsMetadata;
    private String userIn;
    private Date dateIn;
    
    private String dsType;   	

    // Attribute related to the particular dataset implementation
    // TODO the do not belong here. just store at this level a generic
    // configuration object that it s then handled properly by the
    // specific subclasses
    protected String resPath;
    protected Object query;	
    protected String queryScript;	
    protected String queryScriptLanguage;	

    protected boolean persisted;
    protected Integer dataSourcePersistId;
    protected boolean flatDataset;
    protected Integer dataSourceFlatId;
    protected String flatTableName;	
    protected String configuration;
    protected List noActiveVersions;
    
    protected String owner;
    protected boolean isPublic;
    
    private static transient Logger logger = Logger.getLogger(AbstractDataSet.class);

    public AbstractDataSet() {
    	super();
    	behaviours = new HashMap();
    }
    
    public AbstractDataSet(SpagoBiDataSet dataSet) {
    	super();
    	setId(dataSet.getDsId());
    	setName(dataSet.getName());
    	setLabel(dataSet.getLabel());
    	setDescription(dataSet.getDescription());
		setLabel(dataSet.getLabel());
		setConfiguration(dataSet.getConfiguration());
		setCategoryId(dataSet.getCategoryId());
		setParameters(dataSet.getParameters());
		
		setTransformerId(dataSet.getTransformerId());
		setPivotColumnName(dataSet.getPivotColumnName());
		setPivotRowName(dataSet.getPivotRowName());
		setPivotColumnValue(dataSet.getPivotColumnValue());
		setNumRows(dataSet.isNumRows());
		setDsMetadata(dataSet.getDsMetadata());
		
		if(this.getPivotColumnName() != null 
				&& this.getPivotColumnValue() != null
				&& this.getPivotRowName() != null){
			setDataStoreTransformer(
					new PivotDataSetTransformer(getPivotColumnName(), getPivotColumnValue(), getPivotRowName(), isNumRows()));
		}
		
		behaviours = new HashMap();
    }
    
    public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd = new SpagoBiDataSet();
		
		sbd.setDsId(getId());
		sbd.setLabel(getLabel());
		sbd.setName(getName());
		sbd.setParameters(getParameters());
		sbd.setDescription(getDescription());
		sbd.setCategoryId(getCategoryId());
		sbd.setDsMetadata(getDsMetadata());
		sbd.setConfiguration(getConfiguration());
		
		sbd.setTransformerId(getTransformerId());
		sbd.setPivotColumnName(getPivotColumnName());
		sbd.setPivotRowName(getPivotRowName());
		sbd.setPivotColumnValue(getPivotColumnValue());
		sbd.setNumRows(isNumRows());
		return sbd;
	}
    
    // ===============================================
    // Generic dataset's attributes accessor methods
    // ===============================================
    public int getId() {
    	return id;
    }

    public void setId(int id) {
    	this.id = id;
    }
    
    public String getLabel() {
    	return label;
    }

    public void setLabel(String label) {
    	this.label = label;
    }

	public String getName() {
    	return name;
    }

    public void setName(String name) {
    	this.name = name;
    }

    public String getDescription() {
    	return description;
    }

    public void setDescription(String description) {
    	this.description = description;
    }
    
    public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}
	
	public String getCategoryCd() {
		return categoryCd;
	}

	public boolean hasMetadata() {
		return (getDsMetadata() != null && getDsMetadata().trim().equals("") == false);
	}
	public String getDsMetadata() {
		return dsMetadata;
	}

	public void setDsMetadata(String dsMetadata) {
		this.dsMetadata = dsMetadata;
	}
	
	public IMetaData getMetadata() {
		IMetaData toReturn = null;
		String xmlMetadata = this.getDsMetadata();
		if (xmlMetadata == null || xmlMetadata.trim().equals("")) {
			logger.error("This dataset has no metadata");
			throw new SpagoBIRuntimeException("This dataset has no metadata");
		}
		DatasetMetadataParser parser = new DatasetMetadataParser();
		try {
			toReturn = parser.xmlToMetadata(xmlMetadata);
		} catch (Exception e) {
			logger.error("Error parsing dataset's metadata", e);
			throw new SpagoBIRuntimeException("Error parsing dataset's metadata", e);
		}
		return toReturn;
	}

	public void setMetadata(IMetaData metadata) {
		// do nothings
	}


	public String getDsType() {
		return dsType;
	}

	public void setDsType(String dsType) {
		this.dsType = dsType;
	}
	
	// -----------------------------------------------
    // Parameters management
	// -----------------------------------------------
	
	public String getParameters() {
    	return parameters;
    }
	
	public Map getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}  
	
	// these has to be implemented by the user creating a custom DataSet	
	public Map getProperties() {
		// TODO Auto-generated method stub
		return this.properties;
	}
	public void setProperties(Map map) {
		this.properties = map;		
	}

	public String getTemporaryTableName() {
		if (this.getParamsMap() == null) {
			return null;
		}
		String toReturn = (String) this.getParamsMap().get(SpagoBIConstants.TEMPORARY_TABLE_NAME);
		return toReturn;
	}
	
	public IDataSource getDataSourceForPersistence() {
		if (this.getParamsMap() == null) {
			return null;
		}
		IDataSource toReturn = (IDataSource) this.getParamsMap().get(EngineConstants.ENV_DATASOURCE);
		return toReturn;
	}

	// -----------------------------------------------
    // Transformer management
	// -----------------------------------------------
    public void setParameters(String parameters) {
    	this.parameters = parameters;
    }

	public Integer getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(Integer transformerId) {
		this.transformerId = transformerId;
	}
	
	public String getTransformerCd() {
		return transformerCd;
	}

	public void setTransformerCd(String transformerCd) {
		this.transformerCd = transformerCd;
	}

	public String getPivotColumnName() {
		return pivotColumnName;
	}

	public void setPivotColumnName(String pivotColumnName) {
		this.pivotColumnName = pivotColumnName;
	}

	public String getPivotRowName() {
		return pivotRowName;
	}

	public void setPivotRowName(String pivotRowName) {
		this.pivotRowName = pivotRowName;
	}

	public String getPivotColumnValue() {
		return pivotColumnValue;
	}

	public void setPivotColumnValue(String pivotColumnValue) {
		this.pivotColumnValue = pivotColumnValue;
	}
	
	public boolean isNumRows() {
		return numRows;
	}

	public void setNumRows(boolean numRows) {
		this.numRows = numRows;
	}
	
	public boolean hasDataStoreTransformer() {
		return getDataStoreTransformer() != null;
	}
	
	public void removeDataStoreTransformer() {
		setDataStoreTransformer(null);
	}

	public void setDataStoreTransformer(IDataStoreTransformer dataSetTransformer) {
		this.dataSetTransformer = dataSetTransformer;
	}
	
	public IDataStoreTransformer getDataStoreTransformer() {
		return this.dataSetTransformer;
	}

	// -----------------------------------------------
    // Extension point hook
	// -----------------------------------------------
	
	public boolean hasBehaviour(String behaviourId) {
		return behaviours.containsKey(behaviourId);
	}
	
	public Object getBehaviour(String behaviourId) {
		return behaviours.get(behaviourId);
	}
	
	public void addBehaviour(IDataSetBehaviour behaviour) {
		behaviours.put(behaviour.getId(), behaviour);
	}
    
	// ===============================================
    // Custom dataset's attributes accessor methods
    // ===============================================
	
    public String getResourcePath() {
    	if (resPath == null) {
			try {
				String jndiName = SingletonConfig.getInstance().getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
				resPath = SpagoBIUtilities.readJndiResource(jndiName);
			} catch (Throwable t) {
				logger.debug(t);
				resPath = EnginConf.getInstance().getResourcePath();
			}
    	}
		if (resPath == null) {
			throw new SpagoBIRuntimeException("Resource path not found!!!");
		}
		return resPath;
	}
    
    
    public void setResourcePath(String resPath) {
    	this.resPath = resPath;
	}
    
	public Object getQuery() {
		return query;
	}

	public void setQuery(Object query) {
		this.query = query;
	}
	
	public String getQueryScript() {
		return queryScript;
	}

	public void setQueryScript(String script) {
		this.queryScript = script;
	}
    
	public String getQueryScriptLanguage() {
		return queryScriptLanguage;
	}

	public void setQueryScriptLanguage(String queryScriptLanguage) {
		this.queryScriptLanguage = queryScriptLanguage;
	}

	/**
	 * @return the persisted
	 */
	public boolean isPersisted() {
		return persisted;
	}

	/**
	 * @param persisted the persisted to set
	 */
	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	/**
	 * @return the dataSourcePersistId
	 */
	public Integer getDataSourcePersistId() {
		return dataSourcePersistId;
	}

	/**
	 * @param dataSourcePersistId the dataSourcePersistId to set
	 */
	public void setDataSourcePersistId(Integer dataSourcePersistId) {
		this.dataSourcePersistId = dataSourcePersistId;
	}

	
	/**
	 * @return the flatDataset
	 */
	public boolean isFlatDataset() {
		return flatDataset;
	}

	/**
	 * @param flatDataset the flatDataset to set
	 */
	public void setFlatDataset(boolean flatDataset) {
		this.flatDataset = flatDataset;
	}

	/**
	 * @return the dataSourceFlatId
	 */
	public Integer getDataSourceFlatId() {
		return dataSourceFlatId;
	}

	/**
	 * @param dataSourceFlatId the dataSourceFlatId to set
	 */
	public void setDataSourceFlatId(Integer dataSourceFlatId) {
		this.dataSourceFlatId = dataSourceFlatId;
	}

	/**
	 * @return the flatTableName
	 */
	public String getFlatTableName() {
		return flatTableName;
	}

	/**
	 * @param flatTableName the flatTableName to set
	 */
	public void setFlatTableName(String flatTableName) {
		this.flatTableName = flatTableName;
	}

	
	/**
	 * @return the configuration
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	
	/**
	 * @return the userIn
	 */
	public String getUserIn() {
		return userIn;
	}

	/**
	 * @param userIn the userIn to set
	 */
	public void setUserIn(String userIn) {
		this.userIn = userIn;
	}

	/**
	 * @return the dateIn
	 */
	public Date getDateIn() {
		return dateIn;
	}

	/**
	 * @param dateIn the dateIn to set
	 */
	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	/**
	 * @return the oldVersions
	 */
	public List getNoActiveVersions() {
		return noActiveVersions;
	}

	/**
	 * @param oldVersions the oldVersions to set
	 */
	public void setNoActiveVersions(List noActiveVersions) {
		this.noActiveVersions = noActiveVersions;
	}

	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	// ===============================================
    // Core methods
    // ===============================================
	public void loadData() {
		loadData(0, -1, -1);
	}
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		throw new RuntimeException("Unsupported method");
	}

	public IDataSetTableDescriptor persist(String tableName,
			IDataSource dataSource) {
		PersistedTableManager persister = new PersistedTableManager();
		persister.setTableName(tableName);
		try {
			persister.persistDataSet(this, dataSource);
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while persisting dataset", e);
		}
		List<String> fields = this.getFieldsList();
		IDataSetTableDescriptor descriptor = null;
		try {
			descriptor = TemporaryTableManager.getTableDescriptor(fields, tableName, dataSource);
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error while getting table information", e);
		}
		return descriptor;
	}
	
	protected List<String> getFieldsList() {
		List<String> toReturn = new ArrayList<String>();
		IMetaData metadata = this.getMetadata();
		int count = metadata.getFieldCount();
		for (int i = 0 ; i < count ; i++) {
			toReturn.add(metadata.getFieldName(i));
		}
		return toReturn;
	}

	public IDataStore getDomainValues(String fieldName, Integer start,
			Integer limit, IDataStoreFilter filter) {
		IDataStore toReturn = null;
		try {
			String tableName = this.getTemporaryTableName();
			logger.debug("Temporary table name : [" + tableName + "]");
			if (tableName == null) {
				logger.error("Temporary table name not set, cannot proceed!!");
				throw new SpagoBIEngineRuntimeException("Temporary table name not set");
			}
			IDataSource dataSource = this.getDataSourceForPersistence();
			if (dataSource == null) {
				logger.error("Datasource for persistence not set, cannot proceed!!");
				throw new SpagoBIEngineRuntimeException("Datasource for persistence not set");
			}
			String signature = this.getSignature();
			IDataSetTableDescriptor tableDescriptor = null;
			if (signature.equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
				// signature matches: no need to create a TemporaryTable
				tableDescriptor = TemporaryTableManager.getLastDataSetTableDescriptor(tableName);
			} else {
				tableDescriptor = this.persist(tableName, dataSource);
				TemporaryTableManager.setLastDataSetTableDescriptor(tableName, tableDescriptor);
				TemporaryTableManager.setLastDataSetSignature(tableName, signature);
			}
			String filterColumnName = tableDescriptor.getColumnName(fieldName);
			StringBuffer buffer = new StringBuffer("Select DISTINCT " + filterColumnName + " FROM " + tableName);
			manageFilterOnDomainValues(buffer, fieldName, tableDescriptor, filter);
			String sqlStatement = buffer.toString();
			toReturn = TemporaryTableManager.queryTemporaryTable(sqlStatement, dataSource, start, limit);
			toReturn.getMetaData().changeFieldAlias(0, fieldName);
		} catch (Exception e) {
			logger.error("Error loading the domain values for the field " + fieldName, e);
			throw new SpagoBIEngineRuntimeException("Error loading the domain values for the field "+fieldName, e);

		}
		return toReturn;
	}
	
	private void manageFilterOnDomainValues(StringBuffer buffer,
			String fieldName, IDataSetTableDescriptor tableDescriptor,
			IDataStoreFilter filter) {
		if (filter != null) {
			String filterColumnName = tableDescriptor.getColumnName(fieldName);
			if (filterColumnName == null) {
				throw new SpagoBIRuntimeException("Field name [" + fieldName
						+ "] not found");
			}
			String columnName = tableDescriptor.getColumnName(fieldName);
			Class clazz = tableDescriptor.getColumnType(fieldName);
			String value = getFilterValue(filter.getValue(), clazz);
			IConditionalOperator conditionalOperator = (IConditionalOperator) SQLStatementConditionalOperators
					.getOperator(filter.getOperator());
			String temp = conditionalOperator.apply(columnName,
					new String[] { value });
			buffer.append(" WHERE " + temp);
		}
	}
	

	private String getFilterValue(String value, Class clazz) {
		String toReturn = null;
		if ( String.class.isAssignableFrom(clazz) ) {
			value = StringUtils.escapeQuotes(value);
			toReturn = StringUtils.bound(value, "'");
		} else if ( Number.class.isAssignableFrom(clazz) ) {
			toReturn = value;
		} else if ( Boolean.class.isAssignableFrom(clazz) ) {
			toReturn = value;
		} else {
			// TODO manage other types, such as date and timestamp
			throw new SpagoBIRuntimeException("Unsupported operation: cannot filter on a fild type " + clazz.getName());
		}
		return toReturn;
	}
	
}
