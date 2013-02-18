/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.scheduler.jobs;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.DocumentMetadataProperty;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionController;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterValuesRetriever;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.ExecutionProxy;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.events.EventsManager;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetadataDAO;
import it.eng.spagobi.tools.scheduler.Formula;
import it.eng.spagobi.tools.scheduler.FormulaParameterValuesRetriever;
import it.eng.spagobi.tools.scheduler.RuntimeLoadingParameterValuesRetriever;
import it.eng.spagobi.tools.scheduler.dispatcher.DocumentDispatcher;
import it.eng.spagobi.tools.scheduler.to.DispatchContext;
import it.eng.spagobi.tools.scheduler.utils.BIObjectParametersIterator;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.mime.MimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class XExecuteBIDocumentJob extends AbstractSpagoBIJob implements Job {

	static private Logger logger = Logger.getLogger(XExecuteBIDocumentJob.class);	

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("IN");
		try {
			this.setTenant(jobExecutionContext);
			this.executeInternal(jobExecutionContext);
		} finally {
			this.unsetTenant();
			logger.debug("OUT");
		}
	}
	
	private void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		IEngUserProfile userProfile;
		JobDataMap jobDataMap;
		
		// documentLabel__num this is necessary because the same document can be added to one scheduled activity more than one time
		String documentInstanceName;
		String documentLabel;
		
		// par1=val1&par2=val2... for parameters already set in scheduled activity's configuration
		String inputParametersQueryString;
		
		IBIObjectDAO documentDAO;
		BIObject document;
		ExecutionController executionController;
		ExecutionProxy executionProxy;
		EventsManager eventManager;
		
		String modality;
		String outputMIMEType;
		boolean isSplittingFilter;
		
		logger.debug("IN");
		
		try {
			userProfile = UserProfile.createSchedulerUserProfile();
			jobDataMap = jobExecutionContext.getMergedJobDataMap();
			documentDAO = DAOFactory.getBIObjectDAO();
			
			String encodedDocumentLabels = jobDataMap.getString("documentLabels");
			String[] documentLabels = encodedDocumentLabels.split(",");
			
			Iterator itr = jobDataMap.keySet().iterator();
			while(itr.hasNext()) {
				Object key = itr.next();
				Object value = jobDataMap.get(key);
				logger.debug("jobDataMap parameter [" + key + "] is equal to [" + value + "]");
			}
			
			// please refactor this 
			modality = jobDataMap.getString("modality");
			if(StringUtilities.isEmpty(modality)) modality = "SCHEDULATION";			
			outputMIMEType = jobDataMap.getString("outputMIMEType");			
			String cycleOnFilters = jobDataMap.getString("isSplittingFilter");
			isSplittingFilter = false;
			if("true".equalsIgnoreCase(cycleOnFilters)) isSplittingFilter = true;
			
			long startSchedule = System.currentTimeMillis();
			logger.debug("Scheduled activity contains [" + documentLabels.length + "] documnt(s)");

			DispatchContext globalDispatchContext = null;
			DocumentDispatcher globalDocumentDispatcher = null;
			String encodedGlobalDispatchContext = jobDataMap.getString("globalDispatcherContext");
			if( StringUtilities.isNotEmpty(encodedGlobalDispatchContext)) {
				globalDispatchContext = SchedulerUtilities.decodeDispatchContext(encodedGlobalDispatchContext);
				globalDispatchContext.setUserProfile(userProfile);
				globalDocumentDispatcher = new DocumentDispatcher(globalDispatchContext); 
			}
					
			
			for(int documentIndex = 0; documentIndex < documentLabels.length; documentIndex++) {
				documentInstanceName = documentLabels[documentIndex];
				documentLabel = documentInstanceName.substring(0, documentInstanceName.lastIndexOf("__"));
				logger.debug("Processing document [" + (documentIndex+1) + "] with label [" + documentLabel + "] ...");
								
				inputParametersQueryString = jobDataMap.getString(documentInstanceName);
				logger.debug("Input parameters query string for documet [" + documentLabel + "] is equal to [" + inputParametersQueryString + "]");
				
				// load document
				document = documentDAO.loadBIObjectByLabel(documentLabel);
				loadDocumentMetadata(document);
				
				
				// create the execution controller 
				executionController = new ExecutionController();
				executionController.setBiObject(document);
				
				// fill parameters 
				executionController.refreshParameters(document, inputParametersQueryString);

				String iterativeParametersString = jobDataMap.getString(documentInstanceName + "_iterative");
				logger.debug("Iterative parameter configuration for documet [" + documentLabel + "] is equal to [" + iterativeParametersString + "]");
				setIterativeParameters(document, iterativeParametersString);
				
				String loadAtRuntimeParametersString = jobDataMap.getString(documentInstanceName + "_loadAtRuntime");
				logger.debug("Runtime parameter configuration for documet [" + documentLabel + "] is equal to [" + loadAtRuntimeParametersString + "]");
				setLoadAtRuntimeParameters(document, loadAtRuntimeParametersString);
				
				String useFormulaParametersString = jobDataMap.getString(documentInstanceName + "_useFormula");
				logger.debug("Formuula based parameter configuration for documet [" + documentLabel + "] is equal to [" + useFormulaParametersString + "]");
				setUseFormulaParameters(document, useFormulaParametersString);

				retrieveParametersValues(document);
				
				eventManager = EventsManager.getInstance();
				List roles = DAOFactory.getBIObjectDAO().getCorrectRolesForExecution(document.getId());
				
				String startExecMsg = "${scheduler.startexecsched} " + document.getName();	
				Integer idEvent = eventManager.registerEvent("Scheduler", startExecMsg, "", roles);

				
				Map<String, String> parametersMap = new HashMap<String, String>();
				BIObjectParametersIterator objectParametersIterator = new BIObjectParametersIterator(document.getBiObjectParameters());
				while (objectParametersIterator.hasNext()) {
					List parameters = (List) objectParametersIterator.next();
					document.setBiObjectParameters(parameters);
				

					StringBuffer nameSuffix = new StringBuffer();
					StringBuffer descriptionSuffix = new StringBuffer(" [");
					Iterator parametersIt = parameters.iterator();
					while (parametersIt.hasNext()) {
						
						BIObjectParameter aParameter = (BIObjectParameter) parametersIt.next();
						
						parametersMap.put(aParameter.getParameterUrlName(), aParameter.getParameterValuesAsString());
						if (aParameter.isIterative()) {
							nameSuffix.append("_" + aParameter.getParameterValuesAsString());
							descriptionSuffix.append(aParameter.getLabel() + ":" + aParameter.getParameterValuesAsString() + "; ");
						}
					}
					// if there are no iterative parameters, toBeAppendedToDescription is " [" and must be cleaned
					if (descriptionSuffix.length() == 2) {
						descriptionSuffix.delete(0, 2);
					} else {
						// toBeAppendedToDescription ends with "; " and must be cleaned
						descriptionSuffix.delete(descriptionSuffix.length() - 2, descriptionSuffix.length());
						descriptionSuffix.append("]");
					}

					// appending the current date
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern("dd-MM-yyyy");
					String dateStr = sdf.format(date);
					nameSuffix.append("_" + dateStr);
					
					

					//check parameters value: if a parameter hasn't value but isn't mandatory the process 
					//must go on and so hasValidValue is set to true
					List tmpBIObjectParameters = document.getBiObjectParameters();
					Iterator it = tmpBIObjectParameters.iterator();
					while (it.hasNext()){
						boolean isMandatory = false;
						BIObjectParameter aBIObjectParameter = (BIObjectParameter)it.next();
						List checks = aBIObjectParameter.getParameter().getChecks();
						if (checks != null && !checks.isEmpty()) {
							Iterator checksIt = checks.iterator();
							while (checksIt.hasNext()) {
								Check check = (Check) checksIt.next();
								if (check.getValueTypeCd().equalsIgnoreCase("MANDATORY")) {
									isMandatory = true;
									break;
								}
							}
						}
						if (!isMandatory && 
								(aBIObjectParameter.getParameterValues() == null  || aBIObjectParameter.getParameterValues().size() == 0)) {
							aBIObjectParameter.setParameterValues(new ArrayList());
							aBIObjectParameter.setHasValidValues(true);
						}
					}

					
					
					
					// do some checks : exec the document only if all its parameter are filled
					if(executionController.directExecution()) {
						
						// get the save options
						DocumentDispatcher documentDispatcher = null;
						DispatchContext dispatchContext = null;
						
						if(globalDocumentDispatcher != null) {
							documentDispatcher = globalDocumentDispatcher;
							dispatchContext = globalDispatchContext;
						} else {
							String encodedDispatchContext = jobDataMap.getString("biobject_id_" + document.getId() + "__"+ (documentIndex+1));
							dispatchContext = SchedulerUtilities.decodeDispatchContext(encodedDispatchContext);
							dispatchContext.setUserProfile(userProfile);
							documentDispatcher = new DocumentDispatcher(dispatchContext); 
						}
						
						logger.debug("Dispatch to a snapshot is equal to [" + dispatchContext.isSnapshootDispatchChannelEnabled() + "]");
						logger.debug("Dispatch to a file is equal to [" + dispatchContext.isFileSystemDispatchChannelEnabled() + "]");
						logger.debug("Dispatch to a distribution list is eual to [" + dispatchContext.isDistributionListDispatchChannelEnabled() + "]");
						logger.debug("Dispatch to a java class is equal to [" + dispatchContext.isJavaClassDispatchChannelEnabled() + "]");
						logger.debug("Dispatch by mail-list is equal to [" + dispatchContext.isMailDispatchChannelEnabled() + "]");
						logger.debug("Dispatch by folder-list is equal to [" + dispatchContext.isFunctionalityTreeDispatchChannelEnabled() + "]");
						
						if( documentDispatcher.canDispatch(document) == false ) {
							logger.debug("No valid dispatch target for document [" + (documentIndex+1) + "] with label [" + documentInstanceName + "] and parameters [" + descriptionSuffix +"]");
							logger.warn("Document [" + (documentIndex+1) + "] with label [" + documentInstanceName + "] and parameters [" + descriptionSuffix + "] will be executed but not dispatched");
							//continue;
						} else{
							logger.debug("There is at list one dispatch target for document with label [" + documentInstanceName + "]");
						}
						

						// execute document
						executionProxy = new ExecutionProxy();
						executionProxy.setBiObject(document);
						
						logger.info("Executing document [" + (documentIndex+1) + "] with label [" + documentInstanceName + "] and parameters " + descriptionSuffix +" ...");
						long start = System.currentTimeMillis();

						// TODO manage this shit
						executionProxy.setSplittingFilter(isSplittingFilter);
						executionProxy.setMimeType(outputMIMEType);
						
					
						byte[] executionOutput = executionProxy.exec(userProfile, modality, null);
						if (executionOutput == null || executionOutput.length == 0) {
							logger.debug("Document executed without any response");
						}
						String contentType = executionProxy.getReturnedContentType();
						
						String fileExtension = null;
						if("application/vnd.ms-excel".equals(contentType)) {
							fileExtension = "xls";
						} else if("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
							fileExtension = "xlsx";
						} else {
							fileExtension = MimeUtils.getFileExtension(contentType);
						}
						long end = System.currentTimeMillis();			
						long elapsed = (end - start)/1000;
						logger.info("Document [" + (documentIndex+1) + "] with label [" + documentInstanceName + "] and parameters " + descriptionSuffix +" executed in [" + elapsed + "]");
						
						// update the dispatch context
						dispatchContext.setNameSuffix( nameSuffix.toString() );
						dispatchContext.setDescriptionSuffix( descriptionSuffix.toString() );
						dispatchContext.setJobExecutionContext(jobExecutionContext);
						dispatchContext.setContentType(contentType);
						dispatchContext.setFileExtension("." + fileExtension);
						dispatchContext.setParametersMap(parametersMap);
						dispatchContext.setTotalNumberOfDocumentsToDispatch(documentLabels.length);
						dispatchContext.setIndexNumberOfDocumentToDispatch(documentIndex);
						
						documentDispatcher.setDispatchContext(dispatchContext);
						documentDispatcher.dispatch(document, executionOutput);
						if(globalDocumentDispatcher == null) {
							documentDispatcher.dispose();
						}
						
						
					} else {
						logger.warn("The document with label "+documentInstanceName+" cannot be executed directly, " +
						"maybe some prameters are not filled ");
						throw new Exception("The document with label "+documentInstanceName+" cannot be executed directly, " +
						"maybe some prameters are not filled ");
					}
				}
				
				String endExecMsg = "${scheduler.endexecsched} " + document.getName();
				eventManager.registerEvent("Scheduler", endExecMsg, "", roles);

			}
			
			if(globalDocumentDispatcher != null) {
				globalDocumentDispatcher.dispose();
			}
			
			long endSchedule = System.currentTimeMillis();
			long elapsedSchedule = (endSchedule-startSchedule)/1000;
			logger.info("Scheduled activity succesfully ended in [" + elapsedSchedule +"] sec.");
		} catch (Exception e) {
			logger.error("Error while executiong job ", e);
		} finally {
			logger.debug("OUT");
		}
	}
	
private void loadDocumentMetadata(BIObject document) {
		
		IObjMetadataDAO metadataPropertyDefiniyionDAO;
		IObjMetacontentDAO documentMetadataPropertyValuesDAO;
		List<DocumentMetadataProperty> documentMetadataProperties = null; 

		
		logger.debug("IN");
		
		metadataPropertyDefiniyionDAO = null;
		documentMetadataPropertyValuesDAO = null;
		documentMetadataProperties = null;
		
		try {
		
			try {
				metadataPropertyDefiniyionDAO = DAOFactory.getObjMetadataDAO();
				documentMetadataPropertyValuesDAO = DAOFactory.getObjMetacontentDAO();
			} catch (Throwable e) {
				throw new SpagoBIServiceException("Impossible to instatiate DAOs to access to document's metadata properties and values", e);
			}
			
			DocumentMetadataProperty documentMetadataProperty = null;
			List<ObjMetadata> metadataPropertyDefinitions = metadataPropertyDefiniyionDAO.loadAllObjMetadata();
			Map<Integer, ObjMetacontent> metadataPropertyIdToPropertyValueMap =  new HashMap<Integer, ObjMetacontent>();
	
			List<ObjMetacontent> documentMetadataPropertyValues = documentMetadataPropertyValuesDAO.loadObjOrSubObjMetacontents(document.getId(), null);
			for (ObjMetacontent metadataPropertyValue : documentMetadataPropertyValues) {
				Integer metadataPropertyId = metadataPropertyValue.getObjmetaId();
				metadataPropertyIdToPropertyValueMap.put(metadataPropertyId, metadataPropertyValue);
			}
	
			documentMetadataProperties = new ArrayList<DocumentMetadataProperty>();
			for (ObjMetadata metadataPropertyDefinition : metadataPropertyDefinitions) {
				documentMetadataProperty = new DocumentMetadataProperty();
				documentMetadataProperty.setMetadataPropertyDefinition(metadataPropertyDefinition);
				documentMetadataProperty.setMetadataPropertyValue(metadataPropertyIdToPropertyValueMap.get(metadataPropertyDefinition.getObjMetaId()));
				
				documentMetadataProperties.add(documentMetadataProperty);
			}
			
			document.setObjMetaDataAndContents(documentMetadataProperties);	
		} catch(SpagoBIRuntimeException t) {
			throw t;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while loading document [" + document + "] metadata", t);
		} finally {
			logger.debug("OUT");
		}
	}

	private void retrieveParametersValues(BIObject biobj) throws Exception {
		logger.debug("IN");
		try {
			List parameters = biobj.getBiObjectParameters();
			if (parameters == null || parameters.isEmpty()) {
				logger.debug("Document has no parameters");
				return;
			}
			Iterator it = parameters.iterator();
			while (it.hasNext()) {
				BIObjectParameter parameter = (BIObjectParameter) it.next();
				ParameterValuesRetriever retriever = parameter.getParameterValuesRetriever();
				if (retriever != null) {
					logger.debug("Document parameter with url name [" + parameter.getParameterUrlName() + "] has a parameter values retriever: " + retriever);
					logger.debug("Retrieving values...");
					List<String> values = null;
					try {
						values = retriever.retrieveValues(parameter);
					} catch (Exception e) {
						logger.error("Error while retrieving values for parameter with url name [" + parameter.getParameterUrlName() + "] of document [" + biobj.getLabel() + "].", e);
						throw e;
					}
					logger.debug("Values retrieved.");
					parameter.setParameterValues(values);
					parameter.setTransientParmeters(true);
				}
			}
		} finally {
			logger.debug("OUT");
		}
	}

	private void setLoadAtRuntimeParameters(BIObject biobj, String loadAtRuntimeParametersString) {
		logger.debug("IN");
		try {
			List parameters = biobj.getBiObjectParameters();
			if (parameters == null || parameters.isEmpty()) {
				logger.debug("Document has no parameters");
				return;
			}
			if (loadAtRuntimeParametersString == null || loadAtRuntimeParametersString.trim().trim().equals("")) {
				logger.debug("No load-at-runtime parameters found");
				return;
			}
			String[] loadAtRuntimeParameters = loadAtRuntimeParametersString.split(";");

			Map<String, String> loadAtRuntimeParametersMap = new HashMap<String, String>();
			for (int count = 0; count < loadAtRuntimeParameters.length; count++) {
				String loadAtRuntime = loadAtRuntimeParameters[count];
				int parameterUrlNameIndex = loadAtRuntime.lastIndexOf("(");
				String parameterUrlName = loadAtRuntime.substring(0, parameterUrlNameIndex);
				String userAndRole = loadAtRuntime.substring(parameterUrlNameIndex + 1, loadAtRuntime.length() - 1);
				loadAtRuntimeParametersMap.put(parameterUrlName, userAndRole);
			}

			Iterator it = parameters.iterator();
			while (it.hasNext()) {
				BIObjectParameter parameter = (BIObjectParameter) it.next();
				if (loadAtRuntimeParametersMap.containsKey(parameter.getParameterUrlName())) {
					logger.debug("Document parameter with url name [" + parameter.getParameterUrlName() + "] was configured to be calculated at runtime.");
					RuntimeLoadingParameterValuesRetriever strategy = new RuntimeLoadingParameterValuesRetriever();
					String userRoleStr = loadAtRuntimeParametersMap.get(parameter.getParameterUrlName());
					String[] userRole = userRoleStr.split("\\|");
					strategy.setUserIndentifierToBeUsed(userRole[0]);
					strategy.setRoleToBeUsed(userRole[1]);
					parameter.setParameterValuesRetriever(strategy);
				}
			}
		} finally {
			logger.debug("OUT");
		}
	}

	private void setIterativeParameters(BIObject biobj, String iterativeParametersString) {
		logger.debug("IN");
		try {
			List parameters = biobj.getBiObjectParameters();
			if (parameters == null || parameters.isEmpty()) {
				logger.debug("Document has no parameters");
				return;
			}
			if (iterativeParametersString == null || iterativeParametersString.trim().trim().equals("")) {
				logger.debug("No iterative parameters found");
				return;
			}
			String[] iterativeParameters = iterativeParametersString.split(";");
			List iterativeParametersList = Arrays.asList(iterativeParameters);
			Iterator it = parameters.iterator();
			while (it.hasNext()) {
				BIObjectParameter parameter = (BIObjectParameter) it.next();
				parameter.setIterative(false);
				if (iterativeParametersList.contains(parameter.getParameterUrlName())) {
					logger.debug("Document parameter with url name [" + parameter.getParameterUrlName() + "] was configured to be iterative.");
					parameter.setIterative(true);
				}
			}
		} finally {
			logger.debug("OUT");
		}
	}

	private void setUseFormulaParameters(BIObject biobj, String useFormulaParametersString) {
		logger.debug("IN");
		try {
			List parameters = biobj.getBiObjectParameters();
			if (parameters == null || parameters.isEmpty()) {
				logger.debug("Document has no parameters");
				return;
			}
			if (useFormulaParametersString == null || useFormulaParametersString.trim().trim().equals("")) {
				logger.debug("No parameters using formula found");
				return;
			}

			String[] useFormulaParameters = useFormulaParametersString.split(";");

			Map<String, String> useFormulaParametersMap = new HashMap<String, String>();
			for (int count = 0; count < useFormulaParameters.length; count++) {
				String useFormula = useFormulaParameters[count];
				int parameterUrlNameIndex = useFormula.lastIndexOf("(");
				String parameterUrlName = useFormula.substring(0, parameterUrlNameIndex);
				String userAndRole = useFormula.substring(parameterUrlNameIndex + 1, useFormula.length() - 1);
				useFormulaParametersMap.put(parameterUrlName, userAndRole);
			}

			Iterator it = parameters.iterator();
			while (it.hasNext()) {
				BIObjectParameter parameter = (BIObjectParameter) it.next();
				if (useFormulaParametersMap.containsKey(parameter.getParameterUrlName())) {
					logger.debug("Document parameter with url name [" + parameter.getParameterUrlName() + "] was configured to use a formula.");
					FormulaParameterValuesRetriever strategy = new FormulaParameterValuesRetriever();
					String fName = useFormulaParametersMap.get(parameter.getParameterUrlName());
					Formula f = Formula.getFormula(fName);
					strategy.setFormula(f);
					parameter.setParameterValuesRetriever(strategy);
				}
			}
		} finally {
			logger.debug("OUT");
		}
	}
}
