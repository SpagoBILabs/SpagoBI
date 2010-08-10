package it.eng.spagobi.kpi.model.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.utils.AbstractConfigurableListModule;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class ListModelResourcesModule extends AbstractConfigurableListModule {

	private static transient Logger logger = Logger
			.getLogger(ListModelResourcesModule.class);

	private Integer modelInstanceId = null;

	@Override
	public void service(SourceBean request, SourceBean response)
			throws Exception {
		
		String message = (String) request.getAttribute("MESSAGE");
		String modelInstId = (String) request.getAttribute("ID");
		modelInstanceId = Integer.parseInt(modelInstId);
		String resourceId = (String) request.getAttribute("resourceId");
		if (resourceId != null && message !=null && message.equalsIgnoreCase(SpagoBIConstants.DETAIL_SELECT)) {
			if (DAOFactory.getModelResourcesDAO().isSelected(modelInstanceId,
					Integer.parseInt(resourceId))){
				DAOFactory.getModelResourcesDAO().removeModelResource(
						Integer.parseInt(modelInstId), Integer.parseInt(resourceId));
			}
			else {
				DAOFactory.getModelResourcesDAO().addModelResource(
						Integer.parseInt(modelInstId), Integer.parseInt(resourceId));
			}
		}
		super.service(request, response);
		
		HashMap parametersMap = (HashMap)response.getAttribute("PARAMETERS_MAP");
		if (parametersMap == null){
			parametersMap = new HashMap();
			parametersMap.put("ID", modelInstanceId);
			response.setAttribute("PARAMETERS_MAP", parametersMap);
		}
		else{
		parametersMap.put("ID", modelInstanceId);
		response.updAttribute("PARAMETERS_MAP", parametersMap);
		}
	}

	@Override
	protected List getObjectList(SourceBean request) {
		List toReturn = null;
		String fieldOrder = (String)request.getAttribute("FIELD_ORDER");
		String typeOrder = (String)request.getAttribute("TYPE_ORDER");
		try {
			toReturn = DAOFactory.getResourceDAO().loadResourcesList(fieldOrder, typeOrder);
		} catch (EMFUserError e) {
			logger.error(e);
		}
		return toReturn;
	}

	@Override
	protected void setRowAttribute(SourceBean rowSB, Object obj)
			throws SourceBeanException {
		Resource aResource = (Resource) obj;
		rowSB.setAttribute("name", aResource.getName());
		rowSB.setAttribute("resourceId", aResource.getId());
		rowSB.setAttribute("Id", modelInstanceId);
		try {
			if (DAOFactory.getModelResourcesDAO().isSelected(modelInstanceId,
					aResource.getId()))
			rowSB.setAttribute("selected", "selected");
			else
				rowSB.setAttribute("selected", "not selected");
		} catch (EMFUserError e) {
			logger.error(e);
		}
	}

}
