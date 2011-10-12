/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.worksheet.services.designer;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.commons.presentation.DynamicPublisher;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.services.initializers.WorksheetEngineStartAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.utilities.engines.EngineConstants;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Davide Zerbetto (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class WorksheetWithDatasetStartEditAction extends WorksheetEngineStartAction{ //WorksheetEngineStartAction {	

	private static final long serialVersionUID = 6272194014941617286L;

	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;

	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(WorksheetStartEditAction.class);

	String dsLabel = null;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		logger.debug("IN");
		dsLabel = serviceRequest.getAttribute("dataset_label") != null ?
				serviceRequest.getAttribute("dataset_label").toString() : null;

				super.service(serviceRequest, serviceResponse);

				//publisher for the qbe edit
				String publisherName = "WORKSHEET_START_EDIT_ACTION_DATASET_PUBLISHER";

				try {
					serviceResponse.setAttribute(DynamicPublisher.PUBLISHER_NAME, publisherName);
				} catch (SourceBeanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				logger.debug("OUT");
	}


	/**
	 *  Override bcause datasource and docuemtn are not prsesetn
	 */


	public Map getEnv() {
		Map env = new HashMap();

		copyRequestParametersIntoEnv(env, getSpagoBIRequestContainer());
		//env.put(EngineConstants.ENV_DATASOURCE, getDataSource());
		// document id can be null (when using QbE for dataset definition)
		//		   if (getDocumentId() != null) {
		//			   env.put(EngineConstants.ENV_DOCUMENT_ID, getDocumentId());
		//		   }
		env.put(EngineConstants.ENV_USER_PROFILE, getUserProfile());
		// env.put(EngineConstants.ENV_CONTENT_SERVICE_PROXY, getContentServiceProxy());
		env.put(EngineConstants.ENV_AUDIT_SERVICE_PROXY, getAuditServiceProxy() );
		env.put(EngineConstants.ENV_DATASET_PROXY, getDataSetServiceProxy()); 
		env.put(EngineConstants.ENV_LOCALE, getLocale()); 

		return env;
	}

	@Override
	public IDataSet getDataSet() {
		//dsLabel="modello";
		IDataSet dataSet = getDataSetServiceProxy().getDataSetByLabel(dsLabel);  	

		return dataSet;


	}

	@Override
	public void initDataSource(WorksheetEngineInstance worksheetEngineInstance) {
		// TODO Auto-generated method stub
		return;
	}


	@Override
	public SourceBean getTemplateAsSourceBean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentId() {
		// TODO Auto-generated method stub
		return null;
	}

}