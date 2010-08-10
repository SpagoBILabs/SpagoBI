package it.eng.spagobi.kpi.config.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.detail.impl.DefaultDetailModule;
import it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spago.validation.coordinator.ValidationCoordinator;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.kpi.config.utils.DetailKpiUtil;

import java.util.Collection;
import java.util.Iterator;

public class DetailKpiModule extends DefaultDetailModule {

	private static final String VALIDATION_PAGE = "KpiDetailPage";

	public void service(SourceBean request, SourceBean response)
			throws Exception {
		boolean validationError = false;
		String message = (String) request.getAttribute("MESSAGE");
		if (message == null) {
			message = SpagoBIConstants.DETAIL_SELECT;
		}
		// VALIDATION
		validationError = hasValidationError(message);

		// DETAIL_SELECT
		if (message.equalsIgnoreCase(SpagoBIConstants.DETAIL_SELECT)) {
			String id = (String) request.getAttribute("ID");
			DetailKpiUtil.selectKpi(Integer.parseInt(id), response);
		}
		// DETAIL_UPDATE
		if (message.equalsIgnoreCase(DelegatedDetailService.DETAIL_UPDATE)) {
			String id = (String) request.getAttribute("ID");
			response.setAttribute("ID", Integer.parseInt(id));
			response.setAttribute("MESSAGE", SpagoBIConstants.DETAIL_SELECT);
			if (!validationError) {
				DetailKpiUtil.updateKpiFromRequest(request, Integer.parseInt(id));
				DetailKpiUtil.selectKpi(Integer.parseInt(id), response);
			} else {
				DetailKpiUtil.restoreKpiValue(Integer.parseInt(id), request, response);
			}
		}
		// DETAIL_INSERT
		if (message.equalsIgnoreCase(DelegatedDetailService.DETAIL_INSERT)) {
			if (!validationError){
				DetailKpiUtil.newKpi(request, response);
			} else {
				DetailKpiUtil.restoreKpiValue(null, request, response);
			}
		}
	}

	private boolean hasValidationError(String message) {
		boolean toReturn = false;
		if (message.equalsIgnoreCase(DelegatedDetailService.DETAIL_UPDATE)
				|| message
						.equalsIgnoreCase(DelegatedDetailService.DETAIL_INSERT)) {
			ValidationCoordinator.validate("PAGE", VALIDATION_PAGE, this);

			EMFErrorHandler errorHandler = getErrorHandler();

			// if there are some validation errors into the errorHandler does
			// not write into DB
			Collection errors = errorHandler.getErrors();

			if (errors != null && errors.size() > 0) {
				Iterator iterator = errors.iterator();
				while (iterator.hasNext()) {
					Object error = iterator.next();
					if (error instanceof EMFValidationError) {
						toReturn = true;
					}
				}
			}
		}
		return toReturn;
	}
}