package it.eng.spagobi.kpi.model.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.detail.impl.DefaultDetailModule;
import it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spago.validation.coordinator.ValidationCoordinator;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.kpi.model.utils.DetailModelUtil;

import java.util.Collection;
import java.util.Iterator;

public class DetailModelModule extends DefaultDetailModule {

	private static final String VALIDATION_PAGE = "ModelDetailPage";

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
			String parentId = (String) request.getAttribute("ID");
			DetailModelUtil.selectModel(Integer.parseInt(parentId), response);
		}
		// DETAIL_UPDATE
		if (message.equalsIgnoreCase(DelegatedDetailService.DETAIL_UPDATE)) {
			String idModel = (String) request.getAttribute("ID");
			response.setAttribute("ID", Integer.parseInt(idModel));
			response.setAttribute("MESSAGE", SpagoBIConstants.DETAIL_SELECT);
			if (!validationError) {
				try {
				DetailModelUtil.updateModelFromRequest(request, Integer
						.parseInt(idModel));
				} catch (EMFUserError e) {
					EMFErrorHandler engErrorHandler = getErrorHandler();
					engErrorHandler.addError(e);
				}
				DetailModelUtil
						.selectModel(Integer.parseInt(idModel), response);
			} else {
				DetailModelUtil.restoreModelValue(Integer.parseInt(idModel),
						request, response);
			}

		}
		// DETAIL_INSERT
		if (message.equalsIgnoreCase(DelegatedDetailService.DETAIL_INSERT)) {
			if (!validationError) {
				try {
				DetailModelUtil.newModel(request, response, null);
				} catch (EMFUserError e) {
					EMFErrorHandler engErrorHandler = getErrorHandler();
					engErrorHandler.addError(e);
				}
			} else {
				DetailModelUtil.restoreModelValue(null, request, response);
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