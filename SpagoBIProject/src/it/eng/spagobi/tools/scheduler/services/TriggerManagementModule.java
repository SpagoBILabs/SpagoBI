/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.scheduler.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.AbstractModule;
import it.eng.spago.error.EMFErrorCategory;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spago.validation.EMFValidationError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.services.scheduler.service.SchedulerServiceSupplier;
import it.eng.spagobi.tools.distributionlist.bo.DistributionList;
import it.eng.spagobi.tools.distributionlist.dao.IDistributionListDAO;
import it.eng.spagobi.tools.scheduler.to.JobInfo;
import it.eng.spagobi.tools.scheduler.to.SaveInfo;
import it.eng.spagobi.tools.scheduler.to.TriggerInfo;
import it.eng.spagobi.tools.scheduler.utils.JavaClassDestination;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;


public class TriggerManagementModule extends AbstractModule {
	
	static private Logger logger = Logger.getLogger(TriggerManagementModule.class);
	private RequestContainer reqCont = null;
	private SessionContainer sessCont = null;
	 private EMFErrorHandler errorHandler=null; 
	
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.module.AbstractModule#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {	
		
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spago.dispatching.service.ServiceIFace#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception { 
		String message = (String) request.getAttribute("MESSAGEDET");
		logger.debug("begin of trigger management service =" +message);
		reqCont = getRequestContainer();
		sessCont = reqCont.getSessionContainer();

		errorHandler = getErrorHandler();
		
		try {
			if(message == null) {
				EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 101);
				logger.error("The message is null");
				throw userError;
			}
			if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_GET_JOB_SCHEDULES) ||
			   message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_ORDER_LIST)) {
				getTriggersForJob(request, response);
			} else if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_NEW_SCHEDULE)) {
				newScheduleForJob(request, response);
			} else if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_SAVE_SCHEDULE)) {
				saveScheduleForJob(request, response);
			} else if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_DELETE_SCHEDULE)) {
				deleteSchedule(request, response);
			} else if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_GET_SCHEDULE_DETAIL)) {
				getSchedule(request, response);
			} else if(message.trim().equalsIgnoreCase(SpagoBIConstants.MESSAGE_RUN_SCHEDULE)) {
				runSchedule(request, response);
			} 
		} catch (EMFUserError eex) {
			errorHandler.addError(eex);
			return;
		} catch (Exception ex) {
			logger.error("Error while executing trigger management service", ex);
			EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
			errorHandler.addError(internalError);
			return;
		}
		logger.debug("end of trigger management service =" +message);
	}
	
	
	
	private void runSchedule(SourceBean request, SourceBean response) throws EMFUserError {
		try {
			RequestContainer reqCont = getRequestContainer();
			SessionContainer sessCont = reqCont.getSessionContainer();
			SessionContainer permSess = sessCont.getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile)permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

			
		    SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			String jobName = (String)request.getAttribute("jobName");
			String jobGroupName = (String)request.getAttribute("jobGroupName");
			getSchedule(request, response);
			TriggerInfo tInfo = (TriggerInfo)sessCont.getAttribute(SpagoBIConstants.TRIGGER_INFO);
			StringBuffer message = createMessageSaveSchedulation(tInfo, true,profile);
			// call the web service to create the schedule
			String resp = schedulerService.scheduleJob(message.toString());
			SourceBean schedModRespSB = SchedulerUtilities.getSBFromWebServiceResponse(resp);
			if(schedModRespSB!=null) {
				String outcome = (String)schedModRespSB.getAttribute("outcome");
				if(outcome.equalsIgnoreCase("fault"))
					throw new Exception("Immediate Trigger not created by the web service");
			}
			// fill spago response
			response.updAttribute(SpagoBIConstants.PUBLISHER_NAME, "ReturnToTriggerList");
			response.setAttribute(SpagoBIConstants.JOB_GROUP_NAME, jobGroupName);
			response.setAttribute(SpagoBIConstants.JOB_NAME, jobName);
		} catch (Exception e) {
			logger.error("Error while create immediate trigger ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	 
	 
	private void deleteSchedule(SourceBean request, SourceBean response) throws EMFUserError {
		String jobName = (String)request.getAttribute("jobName");
		String jobGroupName = (String)request.getAttribute("jobGroupName");
		String triggerName = (String) request.getAttribute("triggerName");
		String triggerGroup = (String) request.getAttribute("triggerGroup");
		try {
				DAOFactory.getDistributionListDAO().eraseAllRelatedDistributionListObjects(triggerName);
		        SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			String resp = schedulerService.deleteSchedulation(triggerName, triggerGroup);
			SourceBean schedModRespSB = SchedulerUtilities.getSBFromWebServiceResponse(resp);
			if(schedModRespSB!=null) {
				String outcome = (String)schedModRespSB.getAttribute("outcome");
				if(outcome.equalsIgnoreCase("fault"))
					throw new Exception("Trigger not deleted by the service");
			}
			// fill spago response
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ReturnToTriggerList");
			response.setAttribute(SpagoBIConstants.JOB_GROUP_NAME, jobGroupName);
			response.setAttribute(SpagoBIConstants.JOB_NAME, jobName);
		} catch (Exception e) {
			logger.error("Error while deleting schedule (trigger) ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}

	private void getSchedule(SourceBean request, SourceBean response) throws EMFUserError {
		try {
		    SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			String jobName = (String)request.getAttribute("jobName");
			String jobGroupName = (String)request.getAttribute("jobGroupName");
			String triggerName = (String) request.getAttribute("triggerName");
			String triggerGroup = (String) request.getAttribute("triggerGroup");
			String respStr_gt = schedulerService.getJobSchedulationDefinition(triggerName, triggerGroup);
	        SourceBean triggerDetailSB = SchedulerUtilities.getSBFromWebServiceResponse(respStr_gt);			
			String respStr_gj = schedulerService.getJobDefinition(jobName, jobGroupName);
            SourceBean jobDetailSB = SchedulerUtilities.getSBFromWebServiceResponse(respStr_gj);						
			if(triggerDetailSB!=null) {
				if(jobDetailSB!=null){
					TriggerInfo tInfo = SchedulerUtilities.getTriggerInfoFromTriggerSourceBean(triggerDetailSB, jobDetailSB);
					sessCont.setAttribute(SpagoBIConstants.TRIGGER_INFO, tInfo);
				} else {
					throw new Exception("Detail not recovered for job " + jobName + 
							            "associated to trigger " + triggerName);
				}
			} else {
				throw new Exception("Detail not recovered for trigger " + triggerName);
			}
			List functionalities = DAOFactory.getLowFunctionalityDAO().loadAllLowFunctionalities(false);
			response.setAttribute(SpagoBIConstants.FUNCTIONALITIES_LIST, functionalities);
			List allDatasets = DAOFactory.getDataSetDAO().loadAllActiveDataSets();
			response.setAttribute(SpagoBIConstants.DATASETS_LIST, allDatasets);
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "TriggerDetail");
		} catch (Exception ex) {
			logger.error("Error while getting detail of the schedule(trigger)", ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	} 
	
	
	private void saveScheduleForJob(SourceBean request, SourceBean response) throws EMFUserError {
		try{
			RequestContainer reqCont = getRequestContainer();
			SessionContainer sessCont = reqCont.getSessionContainer();
			SessionContainer permSess = sessCont.getPermanentContainer();
			IEngUserProfile profile = (IEngUserProfile)permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		    SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			TriggerInfo triggerInfo = (TriggerInfo)sessCont.getAttribute(SpagoBIConstants.TRIGGER_INFO);
			JobInfo jobInfo = triggerInfo.getJobInfo();
			String jobName = jobInfo.getJobName();
			String jobGroupName = jobInfo.getJobGroupName();
			String triggername = (String)request.getAttribute("triggername");	
			String triggerDescription  = (String)request.getAttribute("triggerdescription");	
			String startdate  = (String)request.getAttribute("startdate");	
			String starttime = (String)request.getAttribute("starttime");	
			String chronstr = (String)request.getAttribute("chronstring");
			String enddate = (String)request.getAttribute("enddate");	
			String endtime = (String)request.getAttribute("endtime");	
			String repeatinterval = (String)request.getAttribute("repeatInterval");
			triggerInfo.setEndDate(enddate);
			triggerInfo.setEndTime(endtime);
			triggerInfo.setRepeatInterval(repeatinterval);
			triggerInfo.setStartDate(startdate);
			triggerInfo.setStartTime(starttime);
			triggerInfo.setChronString(chronstr);
			triggerInfo.setTriggerDescription(triggerDescription);
			triggerInfo.setTriggerName(triggername);

			Map saveOptions = new HashMap();
			List biobjIds = jobInfo.getBiobjectIds();
			Iterator iterBiobjIds = biobjIds.iterator();
			int index = 0;
			while(iterBiobjIds.hasNext()){
				index ++;
				SaveInfo sInfo = new SaveInfo();
				Integer biobId = (Integer)iterBiobjIds.next();
				String saveassnap = (String)request.getAttribute("saveassnapshot_"+biobId+"__"+index);	
				if(saveassnap!=null) {
					sInfo.setSaveAsSnapshot(true);
					String snapname = (String)request.getAttribute("snapshotname_"+biobId+"__"+index);	
					sInfo.setSnapshotName(snapname);
					String snapdescr = (String)request.getAttribute("snapshotdescription_"+biobId+"__"+index);
					sInfo.setSnapshotDescription(snapdescr);
					String snaphistlength = (String)request.getAttribute("snapshothistorylength_"+biobId+"__"+index);
					sInfo.setSnapshotHistoryLength(snaphistlength);
				}  
			
				String sendToJavaClass = (String)request.getAttribute("sendtojavaclass_"+biobId+"__"+index);	
				if(sendToJavaClass!=null) {
					sInfo.setSendToJavaClass(true);
					String javaClassPath = (String)request.getAttribute("javaclasspath_"+biobId+"__"+index);	
					JavaClassDestination tryClass=null;
					try{
					tryClass=(JavaClassDestination)Class.forName(javaClassPath).newInstance();
					}
					catch (ClassCastException e) {
						logger.error("Error in istantiating class");
						EMFValidationError emfError=new EMFValidationError(EMFErrorSeverity.ERROR, "sendtojavaclass_"+biobId+"__"+index, "12200");
						errorHandler.addError(emfError);
					
					}				
					catch (Exception e) {
						logger.error("Error in istantiating class");
						EMFValidationError emfError=new EMFValidationError(EMFErrorSeverity.ERROR, "sendtojavaclass_"+biobId+"__"+index, "12100");
						errorHandler.addError(emfError);
					}					
					sInfo.setJavaClassPath(javaClassPath);
				}  
				
				
				String saveasdoc = (String)request.getAttribute("saveasdocument_"+biobId+"__"+index);	
				if(saveasdoc!=null) {
					sInfo.setSaveAsDocument(true);
					String docname = (String)request.getAttribute("documentname_"+biobId+"__"+index);	
					sInfo.setDocumentName(docname);
					String docdescr = (String)request.getAttribute("documentdescription_"+biobId+"__"+index);	
					sInfo.setDocumentDescription(docdescr);
					boolean useFixedFolder = "true".equalsIgnoreCase((String) request.getAttribute("useFixedFolder_"+biobId+"__"+index));
					sInfo.setUseFixedFolder(useFixedFolder);
					if (useFixedFolder) {
						String functIdsConcat = "";
						String tmpValReq = "tree_"+biobId+"__"+index+"_funct_id";
						List functIds = request.getAttributeAsList(tmpValReq);	
						Iterator iterFunctIds = functIds.iterator();
						while(iterFunctIds.hasNext()) {
							String idFunct = (String)iterFunctIds.next();
							functIdsConcat += idFunct;
							if(iterFunctIds.hasNext()){
								functIdsConcat += ",";
							}
						}
						sInfo.setFunctionalityIds(functIdsConcat);
					}
					//gestire acquisizione folder 
					boolean useFolderDataset = "true".equalsIgnoreCase((String) request.getAttribute("useFolderDataset_"+biobId+"__"+index));
					sInfo.setUseFolderDataSet(useFolderDataset);
					if (useFolderDataset) {
						String dsLabel = (String)request.getAttribute("datasetFolderLabel_"+biobId+"__"+index);	
						sInfo.setDataSetFolderLabel(dsLabel);
						String datasetParameterLabel = (String)request.getAttribute("datasetFolderParameter_"+biobId+"__"+index);	
						sInfo.setDataSetFolderParameterLabel(datasetParameterLabel);
						if (dsLabel == null || dsLabel.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingDataSet", params, "component_scheduler_messages"));
						}
						if (datasetParameterLabel == null || datasetParameterLabel.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingDataSetParameter", params, "component_scheduler_messages"));
						}
					//	sInfo.setFunctionalityIds(functIdsConcat);
					}
				}
				String sendmail = (String)request.getAttribute("sendmail_"+biobId+"__"+index);	
				if(sendmail!=null) {
					sInfo.setSendMail(true);
					boolean useFixedRecipients = "true".equalsIgnoreCase((String) request.getAttribute("useFixedRecipients_"+biobId+"__"+index));
					sInfo.setUseFixedRecipients(useFixedRecipients);
					if (useFixedRecipients) {
						String mailtos = (String)request.getAttribute("mailtos_"+biobId+"__"+index);
						sInfo.setMailTos(mailtos);
						if (mailtos == null || mailtos.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingFixedRecipients", params, "component_scheduler_messages"));
						}
					}
					boolean useDataset = "true".equalsIgnoreCase((String) request.getAttribute("useDataset_"+biobId+"__"+index));
					sInfo.setUseDataSet(useDataset);
					if (useDataset) {
						String dsLabel = (String)request.getAttribute("datasetLabel_"+biobId+"__"+index);	
						sInfo.setDataSetLabel(dsLabel);
						String datasetParameterLabel = (String)request.getAttribute("datasetParameter_"+biobId+"__"+index);	
						sInfo.setDataSetParameterLabel(datasetParameterLabel);
						if (dsLabel == null || dsLabel.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingDataSet", params, "component_scheduler_messages"));
						}
						if (datasetParameterLabel == null || datasetParameterLabel.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingDataSetParameter", params, "component_scheduler_messages"));
						}
					}
					boolean useExpression = "true".equalsIgnoreCase((String) request.getAttribute("useExpression_"+biobId+"__"+index));
					sInfo.setUseExpression(useExpression);
					if (useExpression) {
						String expression = (String)request.getAttribute("expression_"+biobId+"__"+index);	
						sInfo.setExpression(expression);
						if (expression == null || expression.trim().equals("")) {
							BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
							List params = new ArrayList();
							params.add(biobj.getName());
							this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingExpression", params, "component_scheduler_messages"));
						}
					}
					
					if (!useFixedRecipients && !useDataset && !useExpression) {
						BIObject biobj = DAOFactory.getBIObjectDAO().loadBIObjectById(biobId);
						List params = new ArrayList();
						params.add(biobj.getName());
						this.getErrorHandler().addError(new EMFValidationError(EMFErrorSeverity.ERROR, null, "errors.trigger.missingRecipients", params, "component_scheduler_messages"));	
					}
					
					String mailsubj = (String)request.getAttribute("mailsubj_"+biobId+"__"+index);	
					sInfo.setMailSubj(mailsubj);
					String mailtxt = (String)request.getAttribute("mailtxt_"+biobId+"__"+index);	
					sInfo.setMailTxt(mailtxt);
				}
				String sendtodl = (String)request.getAttribute("saveasdl_"+biobId+"__"+index);	
				if(sendtodl!=null) {
					sInfo.setSendToDl(true);
					sInfo.setBiobjId(biobId.intValue());
					List dlist = DAOFactory.getDistributionListDAO().loadAllDistributionLists();	
					Iterator it = dlist.iterator();
					while(it.hasNext()){
						DistributionList dl = (DistributionList)it.next();
						int dlId = dl.getId();
						String listID = (String)request.getAttribute("sendtodl_"+dlId+"_"+biobId+"__"+index);
						if(listID!=null){
							sInfo.addDlId(new Integer(listID));
						}
						else{
							
							DAOFactory.getDistributionListDAO().eraseDistributionListObjects(dl,biobId.intValue(),triggername);
						}
											
					}
					
				}
				
				saveOptions.put(biobId+"__"+index, sInfo);
			}
			triggerInfo.setSaveOptions(saveOptions);
			
			// check for input validation errors 
			if(!this.getErrorHandler().isOKByCategory(EMFErrorCategory.VALIDATION_ERROR)) {
				List functionalities = DAOFactory.getLowFunctionalityDAO().loadAllLowFunctionalities(false);
				response.setAttribute(SpagoBIConstants.FUNCTIONALITIES_LIST, functionalities);
				List allDatasets = DAOFactory.getDataSetDAO().loadAllActiveDataSets();
				response.setAttribute(SpagoBIConstants.DATASETS_LIST, allDatasets);
				response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "TriggerDetail");
				return;
			}
			
			StringBuffer message = createMessageSaveSchedulation(triggerInfo, false,profile);
			// call the web service to create the schedule
			String servoutStr = schedulerService.scheduleJob(message.toString());
			SourceBean execOutSB = SchedulerUtilities.getSBFromWebServiceResponse(servoutStr);
			if(execOutSB!=null) {
				String outcome = (String)execOutSB.getAttribute("outcome");
				if(outcome.equalsIgnoreCase("fault"))
					throw new Exception("Trigger "+triggername+" not created by the web service");
			}
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ReturnToTriggerList");
			response.setAttribute(SpagoBIConstants.JOB_GROUP_NAME, jobGroupName);
			response.setAttribute(SpagoBIConstants.JOB_NAME, jobName);
		} catch (Exception ex) {
			logger.error("Error while saving schedule for job", ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	
	
	
	private void newScheduleForJob(SourceBean request, SourceBean response) throws EMFUserError {
		String jobName = "";
		try{
		    SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			jobName = (String)request.getAttribute("jobName");
			String jobGroupName = (String)request.getAttribute("jobGroupName");
			TriggerInfo ti = new TriggerInfo();
			String respStr = schedulerService.getJobDefinition(jobName, jobGroupName);
            SourceBean jobDetailSB = SchedulerUtilities.getSBFromWebServiceResponse(respStr);			
			if(jobDetailSB!=null) {
				JobInfo jobInfo = SchedulerUtilities.getJobInfoFromJobSourceBean(jobDetailSB);
				ti.setJobInfo(jobInfo);
				Map saveOptions = new HashMap();
				List biobjids = jobInfo.getBiobjectIds();
				Iterator iterbiobjids = biobjids.iterator();
				int index = 0;
				while(iterbiobjids.hasNext()) {
					index ++;
					Integer idobj = (Integer)iterbiobjids.next();
					saveOptions.put(idobj+"__" + index, new SaveInfo());
				}
				ti.setSaveOptions(saveOptions);
			} else {
				throw new Exception("Cannot recover job " + jobName);
			}		
			
			List functionalities = DAOFactory.getLowFunctionalityDAO().loadAllLowFunctionalities(false);
			response.setAttribute(SpagoBIConstants.FUNCTIONALITIES_LIST, functionalities);
			List allDatasets = DAOFactory.getDataSetDAO().loadAllActiveDataSets();
			response.setAttribute(SpagoBIConstants.DATASETS_LIST, allDatasets);
			sessCont.setAttribute(SpagoBIConstants.TRIGGER_INFO, ti);
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "TriggerDetail");
		} catch (Exception ex) {
			logger.error("Error while creating a new schedule for job " + jobName, ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	
	private void getTriggersForJob(SourceBean request, SourceBean response) throws EMFUserError {
		String jobName = "";
		try{
		    SchedulerServiceSupplier schedulerService=new SchedulerServiceSupplier();
			// create the sourcebean of the list
			SourceBean pageListSB  = new SourceBean("PAGED_LIST");
			jobName = (String)request.getAttribute("jobName");
			String jobGroupName = (String)request.getAttribute("jobGroupName");
			String serviceResp = schedulerService.getJobSchedulationList(jobName, jobGroupName);
			SourceBean rowsSB = SourceBean.fromXMLString(serviceResp);
			if(rowsSB==null) {
				rowsSB = new SourceBean("ROWS");
			}
			// fill the list sourcebean			
			pageListSB.setAttribute(rowsSB);
			
			//ordering of list
			String typeOrder = (request.getAttribute("TYPE_ORDER")==null)?" ASC":(String)request.getAttribute("TYPE_ORDER");
			String fieldOrder = (request.getAttribute("FIELD_ORDER")==null)?" triggerDescription":(String)request.getAttribute("FIELD_ORDER");
			pageListSB = orderJobList(pageListSB, typeOrder, fieldOrder);

			// populate response with the right values
			response.setAttribute(pageListSB);
			response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "ListTriggers");	
		} catch (Exception ex) {
			logger.error("Error while recovering triggers of the job " + jobName, ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		}
	}
	
	
	
	
	
	private StringBuffer createMessageSaveSchedulation(TriggerInfo tInfo, boolean runImmediately,IEngUserProfile profile) throws EMFUserError {
		StringBuffer message = new StringBuffer();
		JobInfo jInfo = tInfo.getJobInfo();
		Map saveOptions = tInfo.getSaveOptions();
		Set biobjids_so =  saveOptions.keySet();
		Iterator iterbiobjids_s = biobjids_so.iterator();
		
		message.append("<SERVICE_REQUEST ");
		
		message.append(" jobName=\""+jInfo.getJobName()+"\" ");
		
		message.append(" jobGroup=\""+jInfo.getJobGroupName()+"\" ");
		if(runImmediately) {
			message.append(" runImmediately=\"true\" ");
		} else {
			message.append(" triggerName=\""+tInfo.getTriggerName()+"\" ");
			
			message.append(" triggerDescription=\""+tInfo.getTriggerDescription()+"\" ");
			message.append(" startDate=\""+tInfo.getStartDate()+"\" ");
			
			message.append(" startTime=\""+tInfo.getStartTime()+"\" ");
			
			message.append(" chronString=\""+tInfo.getChronString()+"\" ");
			
			String enddate = tInfo.getEndDate();
			String endtime = tInfo.getEndTime();
			if(!enddate.trim().equals("")){
				message.append(" endDate=\""+enddate+"\" ");
				
				if(!endtime.trim().equals("")){
					message.append(" endTime=\""+endtime+"\" ");
					
				}
			}
		}
		String repeatinterval = tInfo.getRepeatInterval();
		if(!repeatinterval.trim().equals("")){
			message.append(" repeatInterval=\""+repeatinterval+"\" ");
			
		}	
		message.append(">");
		
		
		
		message.append("   <PARAMETERS>");
		while(iterbiobjids_s.hasNext()) {
			String biobjidstr_so =  (String)iterbiobjids_s.next();
		//	Integer biobjid_so = Integer.valueOf(biobjidstr_so.substring(0, biobjidstr_so.lastIndexOf("__")));
			SaveInfo sInfo = (SaveInfo)saveOptions.get(biobjidstr_so);
			String saveOptString = "";
			if(sInfo.isSaveAsSnapshot()) {
				saveOptString += "saveassnapshot=true%26";
				if( (sInfo.getSnapshotName()!=null) && !sInfo.getSnapshotName().trim().equals("") ) {
					saveOptString += "snapshotname="+sInfo.getSnapshotName()+"%26";
				}
				if( (sInfo.getSnapshotDescription()!=null) && !sInfo.getSnapshotDescription().trim().equals("") ) {
					saveOptString += "snapshotdescription="+sInfo.getSnapshotDescription()+"%26";
				}
				if( (sInfo.getSnapshotHistoryLength()!=null) && !sInfo.getSnapshotHistoryLength().trim().equals("") ) {
					saveOptString += "snapshothistorylength="+sInfo.getSnapshotHistoryLength()+"%26";
				}
			}
			if(sInfo.isSendToJavaClass()) {
				saveOptString += "sendtojavaclass=true%26";
				if( (sInfo.getJavaClassPath()!=null) && !sInfo.getJavaClassPath().trim().equals("") ) {
					saveOptString += "javaclasspath="+sInfo.getJavaClassPath()+"%26";
				}
			}			
			if(sInfo.isSaveAsDocument()) {
				saveOptString += "saveasdocument=true%26";
				if( (sInfo.getDocumentName()!=null) && !sInfo.getDocumentName().trim().equals("") ) {
					saveOptString += "documentname="+sInfo.getDocumentName()+"%26";
				}
				if( (sInfo.getDocumentDescription()!=null) && !sInfo.getDocumentDescription().trim().equals("") ) {
					saveOptString += "documentdescription="+sInfo.getDocumentDescription()+"%26";
				}
				if(sInfo.isUseFixedFolder() && sInfo.getFoldersTo() != null && !sInfo.getFoldersTo().trim().equals("")) {
					saveOptString += "foldersTo="+sInfo.getFoldersTo()+"%26";
				}
				if(sInfo.isUseFolderDataSet() && sInfo.getDataSetFolderLabel() != null && !sInfo.getDataSetFolderLabel().trim().equals("")) {
					saveOptString += "datasetFolderLabel="+sInfo.getDataSetFolderLabel()+"%26";
					if (sInfo.getDataSetFolderParameterLabel() != null && !sInfo.getDataSetFolderParameterLabel().trim().equals("")) {
						saveOptString += "datasetFolderParameterLabel="+sInfo.getDataSetFolderParameterLabel()+"%26";
					}
				}
				if( (sInfo.getDocumentHistoryLength()!=null) && !sInfo.getDocumentHistoryLength().trim().equals("") ) {
					saveOptString += "documenthistorylength="+sInfo.getDocumentHistoryLength()+"%26";
				}
				if( (sInfo.getFunctionalityIds()!=null) && !sInfo.getFunctionalityIds().trim().equals("") ) {
					saveOptString += "functionalityids="+sInfo.getFunctionalityIds()+"%26";
				}
			}
			if(sInfo.isSendMail()) {
				saveOptString += "sendmail=true%26";
				if(sInfo.isUseFixedRecipients() && sInfo.getMailTos() != null && !sInfo.getMailTos().trim().equals("")) {
					saveOptString += "mailtos="+sInfo.getMailTos()+"%26";
				}
				if(sInfo.isUseDataSet() && sInfo.getDataSetLabel() != null && !sInfo.getDataSetLabel().trim().equals("")) {
					saveOptString += "datasetLabel="+sInfo.getDataSetLabel()+"%26";
					if (sInfo.getDataSetParameterLabel() != null && !sInfo.getDataSetParameterLabel().trim().equals("")) {
						saveOptString += "datasetParameterLabel="+sInfo.getDataSetParameterLabel()+"%26";
					}
				}
				if(sInfo.isUseExpression() && sInfo.getExpression() != null && !sInfo.getExpression().trim().equals("")) {
					saveOptString += "expression="+sInfo.getExpression()+"%26";
				}
				if( (sInfo.getMailSubj()!=null) && !sInfo.getMailSubj().trim().equals("") ) {
					saveOptString += "mailsubj="+sInfo.getMailSubj()+"%26";
				}
				if( (sInfo.getMailTxt()!=null) && !sInfo.getMailTxt().trim().equals("") ) {
					saveOptString += "mailtxt="+sInfo.getMailTxt()+"%26";
				}
			}
			if(sInfo.isSendToDl()) {
				String xml = "";
				if(!runImmediately){
					xml += "<SCHEDULE ";
					xml += " jobName=\""+jInfo.getJobName()+"\" ";					
					xml += " triggerName=\""+tInfo.getTriggerName()+"\" ";					
					xml += " startDate=\""+tInfo.getStartDate()+"\" ";					
					xml += " startTime=\""+tInfo.getStartTime()+"\" ";					
					xml += " chronString=\""+tInfo.getChronString()+"\" ";
					String enddate = tInfo.getEndDate();
					String endtime = tInfo.getEndTime();
					if(!enddate.trim().equals("")){
						xml += " endDate=\""+enddate+"\" ";
						if(!endtime.trim().equals("")){
							xml += " endTime=\""+endtime+"\" ";
						}
					}			
					if(!repeatinterval.trim().equals("")){
						xml += " repeatInterval=\""+repeatinterval+"\" ";
					}	
					xml += ">";
					
					String params = "<PARAMETERS>";
					
					List biObjects = jInfo.getBiobjects();
					Iterator iterbiobj = biObjects.iterator();
					int index = 0;
					while (iterbiobj.hasNext()){
						index ++;
						BIObject biobj = (BIObject)iterbiobj.next();
						String objpref = biobj.getId().toString()+"__" + new Integer(index).toString();
						if(biobjidstr_so.equals(objpref)){
						
						List pars = biobj.getBiObjectParameters();
						Iterator iterPars = pars.iterator();
						String queryString= "";
						while(iterPars.hasNext()) {
							BIObjectParameter biobjpar = (BIObjectParameter)iterPars.next();
							String concatenatedValue = "";
							List values = biobjpar.getParameterValues();
							if(values!=null) {
								Iterator itervalues = values.iterator();
								while(itervalues.hasNext()) {
									String value = (String)itervalues.next();
									concatenatedValue += value + ",";
								}
								if(concatenatedValue.length()>0) {
									concatenatedValue = concatenatedValue.substring(0, concatenatedValue.length() - 1);
									queryString += biobjpar.getParameterUrlName() + "=" + concatenatedValue + "%26";
								}
							}
						}
						if(queryString.length()>0) {
							queryString = queryString.substring(0, queryString.length()-3);
						}
						params += "<PARAMETER name=\""+biobj.getLabel()+"__"+index+"\" value=\""+queryString+"\" />";
						}else{  
							continue;
						}
					}
					params += "</PARAMETERS>";
					
					xml += params ;
					xml += "</SCHEDULE>";
				}
				
				saveOptString += "sendtodl=true%26";
				
				List l= sInfo.getDlIds();
				if(!l.isEmpty()){
					
					String dlIds = "dlId=";
					int objId = sInfo.getBiobjId();
					Iterator iter = l.iterator();
					while (iter.hasNext()){
						
						Integer dlId = (Integer)iter.next();
						try {if(!runImmediately){
							IDistributionListDAO dao=DAOFactory.getDistributionListDAO();
							dao.setUserProfile(profile);
							DistributionList dl = dao.loadDistributionListById(dlId);
							dao.insertDLforDocument(dl, objId, xml);
						}
						} catch (Exception ex) {
							logger.error("Cannot fill response container" + ex.getLocalizedMessage());	
							throw new EMFUserError(EMFErrorSeverity.ERROR, 100);			
						}
						
						if (iter.hasNext()) {dlIds += dlId.intValue()+"," ;}
						else {dlIds += dlId.intValue();}
						
					}
					saveOptString += dlIds+"%26";
				
				}	
			}
			
			message.append("   	   <PARAMETER name=\"biobject_id_"+biobjidstr_so+"\" value=\""+saveOptString+"\" />");
		}
		
		message.append("   </PARAMETERS>");
		message.append("</SERVICE_REQUEST>");
		
		return message;
	}
	
	
	private SourceBean orderJobList(SourceBean pageListSB, String typeOrder, String fieldOrder) throws EMFUserError {
		try {
			List tmpAllList = pageListSB.getAttributeAsList("ROWS.ROW");
			List tmpFieldList = new ArrayList();
			
			if (tmpAllList != null){
				for (int i=0; i < tmpAllList.size(); i++){
					SourceBean tmpSB = (SourceBean)tmpAllList.get(i);
					tmpFieldList.add(tmpSB.getAttribute(fieldOrder.trim()));
				}
			}
			Object[] orderList = tmpFieldList.toArray();
			Arrays.sort(orderList);
			//create a source bean with the list ordered
			SourceBean orderedPageListSB  = new SourceBean("PAGED_LIST");
			SourceBean rows = new SourceBean("ROWS");
			int i = 0;
			if (typeOrder.trim().equals("DESC"))				 
					i = tmpFieldList.size()-1;
			
			while (tmpFieldList != null && tmpFieldList.size() > 0){	
					SourceBean newSB = (SourceBean)tmpAllList.get(tmpFieldList.indexOf(orderList[i]));					
					rows.setAttribute(newSB);
					//remove elements from temporary lists
					tmpAllList.remove(tmpFieldList.indexOf(orderList[i]));
					tmpFieldList.remove(tmpFieldList.indexOf(orderList[i]));
					if (typeOrder.trim().equals("DESC"))
						i--;
					else
						i++;
			}
			orderedPageListSB.setAttribute(rows);
			return orderedPageListSB;
		} catch (Exception ex) {
			logger.error("Error while recovering all job definition", ex);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "errors.1000", "component_scheduler_messages");
		}
	}

	
	
}	
	
	
