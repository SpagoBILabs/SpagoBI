package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttributeValue;

import java.util.List;

public interface IModelAttrValDAO {
	
	/**
	 * 
	 * @param id domain id
	 * @return 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public ModelAttributeValue loadModelAttrValByAttrIdAndModelId(Integer attrId, Integer modelId) throws EMFUserError;

	
	
	/**
	 * 
	 * @param id domain id
	 * @return 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public List allModelsIdWithAttribute() throws EMFUserError;

}
