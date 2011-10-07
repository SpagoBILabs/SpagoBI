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

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;

import java.sql.Connection;
import java.util.Map;

public interface IDataSet {
	
	String getDsMetadata();
	void setDsMetadata(String dsMetadata);
	
	// general properties ....
	int getId();
	void setId(int id);
	
	String getName();
	void setName(String name);

	String getDescription();
	void setDescription(String description);
	
	String getLabel();
	void setLabel(String label);
	
	Integer getCategoryId();
	void setCategoryId(Integer categoryId);
	
	String getCategoryCd();
	void setCategoryCd(String categoryCd);
	
	String getDsType();
	void setDsType(String dsType);

	Map getProperties();
	void setProperties(Map map);
	
	
	// parametrization ....
	// --------------------------------------------------------------------------------------------------
	// INVESTIGATE: why this 2 similar methods ??? FIND OUT & REFACTOR !
	String getParameters();
	void setParameters(String parameters);

	Map getParamsMap();
	void setParamsMap(Map params);
	// --------------------------------------------------------------------------------------------------
	
	// profilation ...
	public Map getUserProfileAttributes();
	public void setUserProfileAttributes(Map attributes);
	
	// execution ...
	// --------------------------------------------------------------------------------------------------
	void loadData();
	void loadData(int offset, int fetchSize, int maxResults);
	// --------------------------------------------------------------------------------------------------
    
	String getResourcePath();
	void setResourcePath(String resPath);
	
	String getGroovyFileName();
	void setGroovyFileName(String groovyFileName);
	String getJsFileName();
	void setJsFileName(String jsFileName);
	
	IDataStore getDataStore();	
	
	// just 4 querable dataSet....
	Object getQuery();
	void setQuery(Object query);	
	
	// extension points ...
	boolean hasBehaviour(String behaviourId);
	Object getBehaviour(String behaviourId);
	void addBehaviour(IDataSetBehaviour behaviour);	
	
	// =================================================================================================
	// TO BE DEPRECATED ( do not cross this line ;-) )
	// =================================================================================================
	
	// --------------------------------------------------------------------------------------------------
	// TODO these methods do NOT belong to the dataset interface. remove them and refactor the code.
	Integer getTransformerId();
	void setTransformerId(Integer transformerId);
	
	String getTransformerCd() ;
    void setTransformerCd(String transfomerCd);

	String getPivotColumnName();
	void setPivotColumnName(String pivotColumnName);

	String getPivotRowName();
	void setPivotRowName(String pivotRowName);
	
	boolean isNumRows();
	void setNumRows(boolean numRows);

	String getPivotColumnValue();
	void setPivotColumnValue(String pivotColumnValue);
	
	boolean hasDataStoreTransformer() ;
	void removeDataStoreTransformer() ;
	
	void setAbortOnOverflow(boolean abortOnOverflow);
	void addBinding(String bindingName, Object bindingValue);
	
	void setDataStoreTransformer(IDataStoreTransformer transformer);
	IDataStoreTransformer getDataStoreTransformer();
	// TODO these methods do NOT belong to the dataset interface. remove them and refactor the code.
	// --------------------------------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------------------------------
	// TODO these methods must be moved into a proper factory that convert SpagoBI BO into data bean passed
	// to the DAO. For the conversion from data bean and SpagoBI BO such a factory alredy exist
	// NOTE: SpagoBiDataSet: change when possible the name SpagoBiDataSet following a convention common to 
	// all data bean
	SpagoBiDataSet toSpagoBiDataSet();
	// --------------------------------------------------------------------------------------------------
	
	IMetaData getMetadata();
	void setMetadata(IMetaData metadata);
	
	IDataStore test();
	IDataStore test(int offset, int fetchSize, int maxResults);
	
	String getSignature();
	
	IDataSetTableDescriptor persist(String tableName, Connection connection);
	
	public IDataStore getDomainValues(String fieldName, 
            Integer start, Integer limit, IDataStoreFilter filter);
	
	public IDataStore decode(IDataStore datastore); 
	
}