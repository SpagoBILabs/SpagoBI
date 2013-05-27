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
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

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
		return null;
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

	// ===============================================
    // Core methods
    // ===============================================
	public void loadData() {
		loadData(0, -1, -1);
	}
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		throw new RuntimeException("Unsupported method");
	}

}
