/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Angelo Bernabei angelo.bernabei@eng.it
 */
public abstract class AbstractDataSet implements IDataSet {

    private int id;
    private String name;
    private String description;
    private String label;
    private Integer categoryId;
    private String categoryCd;
    
    private String dsType;

    private String parameters;
    private Map paramsMap;
    private Map behaviours;
    
    protected Integer transformerId;
    protected String transformerCd;
    protected String pivotColumnName;
    protected String pivotRowName;
    protected String pivotColumnValue;
    protected boolean numRows;
    private String dsMetadata;
    	
    protected IDataStoreTransformer dataSetTransformer;
    
    protected String filePath;
    
    private static transient Logger logger = Logger.getLogger(AbstractDataSet.class);

    public AbstractDataSet() {
    	super();
    	behaviours = new HashMap();
    }
    
    public AbstractDataSet(SpagoBiDataSet dataSetConfig) {
    	super();
    	setId(dataSetConfig.getDsId());
    	setName(dataSetConfig.getName());
    	setLabel(dataSetConfig.getLabel());
    	setDescription(dataSetConfig.getDescription());
		setLabel(dataSetConfig.getLabel());
		setCategoryId(dataSetConfig.getCategoryId());
		setParameters(dataSetConfig.getParameters());
		
		setTransformerId(dataSetConfig.getTransformerId());
		setPivotColumnName(dataSetConfig.getPivotColumnName());
		setPivotRowName(dataSetConfig.getPivotRowName());
		setPivotColumnValue(dataSetConfig.getPivotColumnValue());
		setNumRows(dataSetConfig.isNumRows());
		setDsMetadata(dataSetConfig.getDsMetadata());
		
		if(this.getPivotColumnName() != null 
				&& this.getPivotColumnValue() != null
				&& this.getPivotRowName() != null){
			setDataStoreTransformer(
					new PivotDataSetTransformer(getPivotColumnName(), getPivotColumnValue(), getPivotRowName(), isNumRows()));
		}
		
		behaviours = new HashMap();
    }
    
    
    public String getResourcePath() {
    	if (filePath == null) {
			try {
				String jndiName = SingletonConfig.getInstance().getConfigValue("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
				filePath = SpagoBIUtilities.readJndiResource(jndiName);
			} catch (Throwable t) {
				logger.debug(t);
				filePath = EnginConf.getInstance().getResourcePath();
			}
    	}
		if (filePath == null) {
			throw new SpagoBIRuntimeException("Resource path not found!!!");
		}
		return filePath;
	}
    
    
    public void setResourcePath(String resPath) {
    	filePath = resPath;
	}
    
    public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd = new SpagoBiDataSet();
		
		sbd.setDsId(getId());
		sbd.setLabel(getLabel());
		sbd.setName(getName());
		sbd.setParameters(getParameters());
		sbd.setDescription(getDescription());
		sbd.setCategoryId(getCategoryId());
		
		sbd.setTransformerId(getTransformerId());
		sbd.setPivotColumnName(getPivotColumnName());
		sbd.setPivotRowName(getPivotRowName());
		sbd.setPivotColumnValue(getPivotColumnValue());
		sbd.setNumRows(isNumRows());
		return sbd;
	}


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
    
    public String getParameters() {
    	return parameters;
    }

    
    public void setParameters(String parameters) {
    	this.parameters = parameters;
    }

	public Integer getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(Integer transformerId) {
		this.transformerId = transformerId;
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

	public Map getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map paramsMap) {
		this.paramsMap = paramsMap;
	}  
	
	public boolean hasBehaviour(String behaviourId) {
		return behaviours.containsKey(behaviourId);
	}
	
	public Object getBehaviour(String behaviourId) {
		return behaviours.get(behaviourId);
	}
	
	public void addBehaviour(IDataSetBehaviour behaviour) {
		behaviours.put(behaviour.getId(), behaviour);
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

	
	public boolean isNumRows() {
		return numRows;
	}

	
	public void setNumRows(boolean numRows) {
		this.numRows = numRows;
	}
	
	public void loadData() {
		loadData(0, -1, -1);
	}
	
	public void loadData(int offset, int fetchSize, int maxResults) {
		throw new RuntimeException("Unsupported method");
	}
	
	public String getDsMetadata() {
		return dsMetadata;
	}

	public void setDsMetadata(String dsMetadata) {
		this.dsMetadata = dsMetadata;
	}
	

	public String getCategoryCd() {
		return categoryCd;
	}

	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}

	public String getDsType() {
		return dsType;
	}

	public void setDsType(String dsType) {
		this.dsType = dsType;
	}

	public String getTransformerCd() {
		return transformerCd;
	}

	public void setTransformerCd(String transformerCd) {
		this.transformerCd = transformerCd;
	}
}
