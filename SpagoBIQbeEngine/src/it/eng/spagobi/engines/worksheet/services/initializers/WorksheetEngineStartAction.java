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
package it.eng.spagobi.engines.worksheet.services.initializers;

import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.worksheet.WorksheetEngine;
import it.eng.spagobi.engines.worksheet.WorksheetEngineAnalysisState;
import it.eng.spagobi.engines.worksheet.WorksheetEngineException;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *          Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class WorksheetEngineStartAction extends AbstractEngineStartAction {	

	private static final long serialVersionUID = 4631203497610373565L;

	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(WorksheetEngineStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIWorksheetEngine";
		
    public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
    	WorksheetEngineInstance worksheetEngineInstance = null;
    	WorksheetEngineAnalysisState analysisState;
    	Locale locale;
    	
    	logger.debug("IN");
       
    	try {
    		setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			SourceBean templateBean = getTemplateAsSourceBean();
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			logger.debug("Document Id: " + getDocumentId());
			logger.debug("Template: " + templateBean);
						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			try {
				worksheetEngineInstance = WorksheetEngine.createInstance(templateBean, getEnv());
				QbeEngineInstance qbeEngineInstance = this.getQbeEngineInstance(worksheetEngineInstance);
				if (qbeEngineInstance != null) {
					worksheetEngineInstance.setQbeEngineInstance(qbeEngineInstance);
					setAttribute(EngineConstants.ENGINE_INSTANCE, qbeEngineInstance);
					setAttributeInSession(EngineConstants.ENGINE_INSTANCE, qbeEngineInstance);
				}
				
				this.initDataSet(worksheetEngineInstance);
				this.initDataSource(worksheetEngineInstance);
				
			} catch(Throwable t) {
				SpagoBIEngineStartupException serviceException;
				String msg = "Impossible to create engine instance for document [" + getDocumentId() + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
				
				if(rootException instanceof WorksheetEngineException) {
					WorksheetEngineException e = (WorksheetEngineException)rootException;
					serviceException.setDescription( e.getDescription());
					serviceException.setHints( e.getHints() );
				} 
				throw serviceException;
			}
			logger.debug("Engine instance succesfully created");
			
			worksheetEngineInstance.setAnalysisMetadata( getAnalysisMetadata() );
			if( getAnalysisStateRowData() != null ) {
				logger.debug("Loading subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "] ...");
				try {
					analysisState = new WorksheetEngineAnalysisState();
					analysisState.load( getAnalysisStateRowData() );
					worksheetEngineInstance.setAnalysisState( analysisState );
				} catch(Throwable t) {
					SpagoBIEngineStartupException serviceException;
					String msg = "Impossible load subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "].";
					Throwable rootException = t;
					while(rootException.getCause() != null) {
						rootException = rootException.getCause();
					}
					String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
					msg += "\nThe root cause of the error is: " + str;
					serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
					
					throw serviceException;
				}
				logger.debug("Subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "] succesfully loaded");
			}
			
			locale = (Locale)worksheetEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			
			setAttributeInSession( WorksheetEngineInstance.class.getName() , worksheetEngineInstance );	
			setAttribute(WorksheetEngineInstance.class.getName(), worksheetEngineInstance);
			
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());
			
			if(!this.goToWorksheetPreentation()){
				writeBackToClient(new JSONAcknowledge());
			}
			
		} catch (Throwable e) {
			SpagoBIEngineStartupException serviceException = null;
			
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			logger.debug("OUT");
		}		
	}
    
    protected boolean goToWorksheetPreentation() {
		return true;
	}

	protected QbeEngineInstance getQbeEngineInstance(WorksheetEngineInstance worksheetEngineInstance) {
    	QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		return qbeEngineInstance;
	}

	public void initDataSet(WorksheetEngineInstance worksheetEngineInstance) {
		IDataSet dataset = null;
		QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		if (qbeEngineInstance != null) {
			// retrieves dataset as the Qbe active query
			logger.debug("Qbe engine instance found. Retrieving Qbe query as dataset...");
			dataset = qbeEngineInstance.getActiveQueryAsDataSet();
		} else {
			// retrieves dataset from document configuration (i.e. the dataset associated to the document)
			logger.debug("Qbe engine instance not found. Retrieving dataset from document configuration...");
			dataset = getDataSet();
		}
		
		// update parameters into the dataset
		logger.debug("Setting parameters into dataset...");
		logger.debug( worksheetEngineInstance.getEnv() );
		dataset.setParamsMap( worksheetEngineInstance.getEnv() );
		
		// update profile attributes into dataset
		Map<String, Object> userAttributes = new HashMap<String, Object>();
		UserProfile profile = (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE);
		userAttributes.putAll(profile.getUserAttributes());
		userAttributes.put(SsoServiceInterface.USER_ID, profile.getUserId().toString());
		logger.debug("Setting user profile attributes into dataset...");
		logger.debug( userAttributes );
		dataset.setUserProfileAttributes(userAttributes);
		
		worksheetEngineInstance.setDataSet(dataset);
    }
    
    public void initDataSource(WorksheetEngineInstance worksheetEngineInstance) {
		IDataSource datasource = null;
		QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		if (qbeEngineInstance != null) {
			// retrieves datasource as the Qbe datasource
			logger.debug("Qbe engine instance found. Retrieving Qbe datasource...");
			datasource = ((AbstractDataSource) qbeEngineInstance
							.getDataSource()).getToolsDataSource();
		} else {
			// retrieves datasource from document configuration (i.e. the datasource associated to the document)
			logger.debug("Qbe engine instance not found. Retrieving datasource from document configuration...");
			datasource = getDataSource();
		}
		worksheetEngineInstance.setDataSource(datasource);
    }
}
