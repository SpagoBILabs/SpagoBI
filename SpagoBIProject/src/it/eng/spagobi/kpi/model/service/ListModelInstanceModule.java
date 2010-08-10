package it.eng.spagobi.kpi.model.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.utils.AbstractConfigurableListModule;

import java.util.List;

import org.apache.log4j.Logger;

public class ListModelInstanceModule extends AbstractConfigurableListModule {

	private static transient Logger logger = Logger.getLogger(ListModelInstanceModule.class);

	@Override
	protected List getObjectList(SourceBean request) {
		List result = null;
		String fieldOrder = (String) request.getAttribute("FIELD_ORDER");
		String typeOrder = (String) request.getAttribute("TYPE_ORDER");
		try {
			result = DAOFactory.getModelInstanceDAO().loadModelsInstanceRoot(fieldOrder, typeOrder);
		} catch (EMFUserError e) {
			logger.error(e);
		}
		return result;
	}

	@Override
	protected void setRowAttribute(SourceBean rowSB, Object obj)
			throws SourceBeanException {
		ModelInstance aModelInstance = (ModelInstance) obj;
		rowSB.setAttribute("name", aModelInstance.getName());
		rowSB.setAttribute("id", aModelInstance.getId());

	}
	@Override
	public boolean delete(SourceBean request, SourceBean response){
		boolean toReturn = false;
		String modelInstId = (String) request.getAttribute("ID");
		try {
			toReturn = DAOFactory.getModelInstanceDAO().deleteModelInstance(
					Integer.parseInt(modelInstId));
			toReturn = true;
		} catch (NumberFormatException e) {
			EMFErrorHandler engErrorHandler = getErrorHandler();
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING,
					"10012", "component_kpi_messages"));
		} catch (EMFUserError e) {
			EMFErrorHandler engErrorHandler = getErrorHandler();
			engErrorHandler.addError(e);
		}

		return toReturn; 
	}

}
