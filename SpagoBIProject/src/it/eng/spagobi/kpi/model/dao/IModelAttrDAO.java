package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttribute;

import java.util.List;

public interface IModelAttrDAO {
	
	/**
	 * 
	 * @param id domain id
	 * @return 
	 * @throws EMFUserError If an Exception occurred
	 */	
	
	public Model loadModelAttrByDomainId(Integer domainId) throws EMFUserError;

	
	/**
	 * 
	 * @param id domain id
	 * @return 
	 * @throws EMFUserError If an Exception occurred
	 */	
	
	public ModelAttribute loadModelAttrById(Integer id) throws EMFUserError;

	public List loadAllModelAttrs() throws EMFUserError;

	
}
