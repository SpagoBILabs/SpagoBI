package it.eng.spagobi.kpi.model.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;

import java.util.Date;
import java.util.List;

public interface IModelInstanceDAO {

	/**
	 * 
	 * Returns the List of ModelInstance Root.
	 * 
	 * @return List of ModelInstance Root.
	 * @throws EMFUserError If an Exception occurred.
	 */
	public List loadModelsInstanceRoot() throws EMFUserError;

	public ModelInstance loadModelInstanceWithoutChildrenById(Integer parentId)throws EMFUserError;

	public ModelInstance loadModelInstanceWithoutChildrenByLabel(String parentId)throws EMFUserError;
	
	public void modifyModelInstance(ModelInstance modelInstance) throws EMFUserError;

	public Integer insertModelInstance(ModelInstance toCreate) throws EMFUserError;

	public ModelInstance loadModelInstanceWithChildrenById(Integer parseInt) throws EMFUserError;

	public ModelInstance loadModelInstanceWithChildrenByLabel(String parseInt) throws EMFUserError;
	
	public List getCandidateModelChildren(Integer parentId) throws EMFUserError;

	public ModelInstanceNode loadModelInstanceById(Integer id, Date requestedDate) throws EMFUserError;
	
	public ModelInstanceNode loadModelInstanceByLabel(String label,Date requestedDate) throws EMFUserError;
	
	/**
	 * Delete a Model Instance. 
	 * @param modelId id of the model instance to delete.
	 * @return Return true if the model is deleted.
	 * @throws EMFUserError If an Exception occurred.
	 */
	public boolean deleteModelInstance(Integer modelId)throws EMFUserError;

	public List loadModelsInstanceRoot(String fieldOrder, String typeOrder)throws EMFUserError;

	public ModelInstance loadModelInstanceRoot(ModelInstance mi)throws EMFUserError;
	
	public Integer insertModelInstanceWithKpi(ModelInstance toCreate) throws EMFUserError;

}