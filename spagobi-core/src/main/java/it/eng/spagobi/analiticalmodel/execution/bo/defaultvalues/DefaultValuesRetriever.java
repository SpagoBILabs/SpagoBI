/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.bo.defaultvalues;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.handlers.DocumentUrlManager;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.analiticalmodel.document.handlers.LovResultCacheManager;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovResultHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

public class DefaultValuesRetriever {
	
	private static Logger logger = Logger.getLogger(DefaultValuesRetriever.class);

	public DefaultValuesList getDefaultValues(
			BIObjectParameter analyticalDocumentParameter,
			ExecutionInstance executionInstance, IEngUserProfile profile) {
		logger.debug("IN");
		DefaultValuesList defaultValues = null;
		try {
			ILovDetail lovForDefault = executionInstance.getLovDetailForDefault(analyticalDocumentParameter);
			if (lovForDefault != null) {
				logger.debug("A LOV for default values is defined : " + lovForDefault);
				defaultValues = getDefaultValuesFromDefaultLov(
						executionInstance, profile, lovForDefault);
			} else {
				logger.debug("No LOV for default values defined");
				String formulaForDefault = analyticalDocumentParameter.getParameter().getDefaultFormula();
				if (formulaForDefault != null) {
					IDefaultFormula defaultFormula = DefaultFormulas.get(formulaForDefault);
					defaultValues = defaultFormula.getDefaultValues(analyticalDocumentParameter, executionInstance, profile);
				}
			}
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Impossible to get parameter's default values", e);
		}
		if (defaultValues == null) {
			defaultValues = new DefaultValuesList();
		}
		logger.debug("OUT");
		return defaultValues;
	}

	protected DefaultValuesList getDefaultValuesFromDefaultLov(
			ExecutionInstance executionInstance, IEngUserProfile profile,
			ILovDetail lovForDefault) throws Exception, SourceBeanException {
		logger.debug("IN");
		DefaultValuesList defaultValues = new DefaultValuesList();
		
		// get from cache, if available
		LovResultCacheManager executionCacheManager = new LovResultCacheManager();
		String lovResult = executionCacheManager.getLovResult(profile, lovForDefault, new ArrayList<ObjParuse>(), executionInstance, true);
		LovResultHandler lovResultHandler = new LovResultHandler(lovResult);		
		List rows = lovResultHandler.getRows();
		logger.debug("LOV result retrieved without errors");
		logger.debug("LOV contains " + rows.size() + " values");
		Iterator it = rows.iterator();
		String valueColumn = lovForDefault.getValueColumnName();
		String descriptionColumn = lovForDefault.getDescriptionColumnName();
		while (it.hasNext()) {
			SourceBean row = (SourceBean) it.next();
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.setValue(row.getAttribute(valueColumn));
			defaultValue.setDescription(row.getAttribute(descriptionColumn));
			defaultValues.add(defaultValue);
		}
		logger.debug("OUT");
		return defaultValues;
	}
	
	/*
	 * GET DEFAULT VALUE FROM DOCUMENT_URL_MANAGER
	 */

	public DefaultValuesList getDefaultValuesDum(BIObjectParameter analyticalDocumentParameter, BIObject object, IEngUserProfile profile, Locale locale,
			String role) {
		logger.debug("IN");
		DocumentUrlManager dum = new DocumentUrlManager(profile, locale);
		DefaultValuesList defaultValues = null;
		try {
			ILovDetail lovForDefault = dum.getLovDetailForDefault(analyticalDocumentParameter);
			if (lovForDefault != null) {
				logger.debug("A LOV for default values is defined : " + lovForDefault);
				defaultValues = getDefaultValuesFromDefaultLovDum(object, profile, lovForDefault, locale);
			} else {
				logger.debug("No LOV for default values defined");
				String formulaForDefault = analyticalDocumentParameter.getParameter().getDefaultFormula();
				if (formulaForDefault != null) {
					IDefaultFormulaDum defaultFormulaDum = DefaultFormulasDum.get(formulaForDefault);
					defaultValues = defaultFormulaDum.getDefaultValues(analyticalDocumentParameter, dum, profile, object, locale, role);
				}
			}
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Impossible to get parameter's default values", e);
		}
		if (defaultValues == null) {
			defaultValues = new DefaultValuesList();
		}
		logger.debug("OUT");
		return defaultValues;
	}

	protected DefaultValuesList getDefaultValuesFromDefaultLovDum(BIObject object, IEngUserProfile profile, ILovDetail lovForDefault, Locale locale)
			throws Exception, SourceBeanException {
		logger.debug("IN");
		DefaultValuesList defaultValues = new DefaultValuesList();

		// get from cache, if available
		LovResultCacheManager executionCacheManager = new LovResultCacheManager();
		String lovResult = executionCacheManager.getLovResultDum(profile, lovForDefault, new ArrayList<ObjParuse>(), object, true, locale);
		LovResultHandler lovResultHandler = new LovResultHandler(lovResult);
		List rows = lovResultHandler.getRows();
		logger.debug("LOV result retrieved without errors");
		logger.debug("LOV contains " + rows.size() + " values");
		Iterator it = rows.iterator();
		String valueColumn = lovForDefault.getValueColumnName();
		String descriptionColumn = lovForDefault.getDescriptionColumnName();
		while (it.hasNext()) {
			SourceBean row = (SourceBean) it.next();
			DefaultValue defaultValue = new DefaultValue();
			defaultValue.setValue(row.getAttribute(valueColumn));
			defaultValue.setDescription(row.getAttribute(descriptionColumn));
			defaultValues.add(defaultValue);
		}
		logger.debug("OUT");
		return defaultValues;
	}

	public DefaultValuesList getDefaultQueryValuesDum(BIObjectParameter biparam, DocumentUrlManager dum, IEngUserProfile userProfile, BIObject object,
			Locale locale, String role) {

		LovResultCacheManager executionCacheManager = new LovResultCacheManager();
		ILovDetail lovProvDet = dum.getLovDetail(biparam);
		String columnName = null;
		String lovResult = null;
		try {
			lovResult = executionCacheManager.getLovResultDum(userProfile, lovProvDet, dum.getDependencies(biparam, role), object, true, locale);

			columnName = lovProvDet.getValueColumnName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get all the rows of the result
		LovResultHandler lovResultHandler = null;
		try {
			lovResultHandler = new LovResultHandler(lovResult);
		} catch (SourceBeanException e) {
			e.printStackTrace();
		}

		DefaultValuesList defaultValuesList = new DefaultValuesList();
		List<SourceBean> rows = lovResultHandler.getRows();

		for (SourceBean row : rows) {
			String rowValue = (String) row.getAttribute(columnName);

			DefaultValue defaultValue = new DefaultValue();
			defaultValue.setValue(rowValue);

			defaultValuesList.add(defaultValue);
		}

		return defaultValuesList;
	}

}
