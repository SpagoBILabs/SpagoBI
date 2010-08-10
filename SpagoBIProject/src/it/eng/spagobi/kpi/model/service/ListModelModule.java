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
package it.eng.spagobi.kpi.model.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.utils.AbstractConfigurableListModule;

import java.util.List;

import org.apache.log4j.Logger;

public class ListModelModule extends AbstractConfigurableListModule {

	private static transient Logger logger = Logger
			.getLogger(ListModelModule.class);

	protected List getObjectList(SourceBean request) {
		List result = null;
		String fieldOrder = (String)request.getAttribute("FIELD_ORDER");
		String typeOrder = (String)request.getAttribute("TYPE_ORDER");
		try {
			result = DAOFactory.getModelDAO().loadModelsRoot(fieldOrder, typeOrder);
		} catch (EMFUserError e) {
			logger.error(e);
		}
		return result;
	}

	protected void setRowAttribute(SourceBean rowSB, Object obj)
			throws SourceBeanException {
		Model aModel = (Model) obj;
		rowSB.setAttribute("name", aModel.getName());
		rowSB.setAttribute("id", aModel.getId());
		String code = "";
		if(aModel.getCode()!= null)
			code = aModel.getCode();
		rowSB.setAttribute("code", code);
	}

	@Override
	public boolean delete(SourceBean request, SourceBean response) {
		boolean toReturn = false;
		String modelId = (String) request.getAttribute("ID");
		try {
			toReturn = DAOFactory.getModelDAO().deleteModel(
					Integer.parseInt(modelId));
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
