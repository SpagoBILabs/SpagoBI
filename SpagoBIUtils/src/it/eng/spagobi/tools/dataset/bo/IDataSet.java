package it.eng.spagobi.tools.dataset.bo;

import java.util.Map;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.IDataSetBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;

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
}