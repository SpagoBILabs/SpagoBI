package it.eng.spagobi.kpi.model.utils;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.Model;

import java.util.ArrayList;
import java.util.List;

public class DetailModelUtil {
	
	static public void selectModel(Integer parentId, SourceBean serviceResponse) throws Exception{
		Model toReturn = DAOFactory.getModelDAO().loadModelWithoutChildrenById(parentId);
		serviceResponse.setAttribute("MODEL", toReturn);
	}

	static public void updateModelFromRequest(SourceBean serviceRequest, Integer idModel) throws Exception{
		Model model = getModelFromRequest(serviceRequest);
		String modelAttributeName = (String)serviceRequest.getAttribute("MODELATTRIBUTESNAME");
		
		model.setId(idModel);
		DAOFactory.getModelDAO().modifyModel(model);		
	}

	static private Model getModelFromRequest(SourceBean serviceRequest) {
		String modelName = (String) serviceRequest.getAttribute("modelName");
		String modelDescription = (String) serviceRequest.getAttribute("modelDescription");
		String modelCode = (String) serviceRequest.getAttribute("modelCode");
		String modelKpiId = (String) serviceRequest.getAttribute("kpiId");
		String modelLabel = (String) serviceRequest.getAttribute("modelLabel");
		Integer kpiId = null;
		
		if (modelKpiId!=null && Integer.parseInt(modelKpiId)!= -1){
			kpiId = Integer.parseInt(modelKpiId);
		}

		Model toReturn = new Model();
		toReturn.setName(modelName);
		toReturn.setDescription(modelDescription);
		toReturn.setCode(modelCode);
		toReturn.setKpiId(kpiId);
		toReturn.setLabel(modelLabel);

		return toReturn;
	}

	static public void newModel(SourceBean serviceRequest, SourceBean serviceResponse,Integer parentId) throws Exception {
		Model toCreate = getModelFromRequest(serviceRequest);
		if (parentId != null)
			toCreate.setParentId(parentId);
		
		String modelTypeId = (String) serviceRequest.getAttribute("modelTypeId");

		// insert the new model
		Integer modelId = DAOFactory.getModelDAO().insertModel(toCreate, Integer.parseInt(modelTypeId));

		serviceResponse.setAttribute("ID", modelId);
		serviceResponse.setAttribute("MESSAGE",SpagoBIConstants.DETAIL_SELECT);
		selectModel(modelId, serviceResponse);
	}

	public static void restoreModelValue(Integer id, SourceBean serviceRequest,
			SourceBean serviceResponse) throws Exception {
		Model toReturn = getModelFromRequest(serviceRequest);
	
		if (id != null) {
			toReturn.setId(id);
		}
		
		serviceResponse.setAttribute("MODEL", toReturn);
		
	}

}
