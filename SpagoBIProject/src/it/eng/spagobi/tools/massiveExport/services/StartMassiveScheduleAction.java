/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.tools.massiveExport.services;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.massiveExport.utils.Utilities;
import it.eng.spagobi.tools.scheduler.bo.CronExpression;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
import it.eng.spagobi.tools.scheduler.jobs.ExecuteBIDocumentJob;
import it.eng.spagobi.tools.scheduler.jobs.XExecuteBIDocumentJob;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class StartMassiveScheduleAction extends AbstractSpagoBIAction {

	private static final long serialVersionUID = 1L;

	private final String SERVICE_NAME = "START_MASSIVE_SCHEDULE_ACTION";

	// Objects recieved
	private final String PARAMETERS_PAGE = "Sbi.browser.mexport.MassiveExportWizardParametersPage";
	private final String OPTIONS_PAGE = "Sbi.browser.mexport.MassiveExportWizardOptionsPage";
	private final String TRIGGER_PAGE = "Sbi.browser.mexport.MassiveExportWizardTriggerPage";


	private final String FUNCTIONALITY_ID = "functId";
	private final String ROLE = "selectedRole";
	private final String MIME_TYPE = "mimeType";
	private final String TYPE = "type";  
	private final String SPLITTING_FILTER = "splittingFilter"; 


	// logger component
	private static Logger logger = Logger.getLogger(StartMassiveScheduleAction.class); 

	@Override
	public void doService() {

		ISchedulerDAO schedulerDAO;
		Trigger trigger = null;
		Job job = null;
		Integer folderId = null;
		String documentType = null;
		String role = null; 
		String outputMIMEType = null;
		boolean splittingFilter = false;
		JSONObject optionsPageContentJSON = null;
		JSONObject parametersPageContentJSON = null;
		JSONObject triggerPageContentJSON = null;
		boolean triggerSuccesfullySaved = false;
		boolean jobSuccesfullySaved = false;
		
		logger.debug("IN");

		schedulerDAO = null;
		try{
		
			
			try{
				folderId = this.getAttributeAsInteger(FUNCTIONALITY_ID);
				logger.debug("Input parameter [" + FUNCTIONALITY_ID + "] is equal to [" + folderId + "]");
				Assert.assertNotNull(folderId, "Input parameter [" + FUNCTIONALITY_ID + "] cannot be null");

				documentType = this.getAttributeAsString(TYPE);
				logger.debug("Input parameter [" + TYPE + "] is equal to [" + documentType + "]");

				optionsPageContentJSON = this.getAttributeAsJSONObject(OPTIONS_PAGE);
				logger.debug("Input parameter [" + OPTIONS_PAGE + "] is equal to [" + optionsPageContentJSON + "]");
				Assert.assertNotNull(optionsPageContentJSON, "Input parameter [" + OPTIONS_PAGE + "] cannot be null");

				role = optionsPageContentJSON.getString(ROLE);
				logger.debug("Input parameter [" + ROLE + "] is equal to [" + role + "]");
				Assert.assertNotNull(role, "Input parameter [" + ROLE + "] cannot be null");

				outputMIMEType = optionsPageContentJSON.getString(MIME_TYPE);
				logger.debug("Input parameter [" + MIME_TYPE + "] is equal to [" + outputMIMEType + "]");
				Assert.assertNotNull(outputMIMEType, "Input parameter [" + MIME_TYPE + "] cannot be null");

				String cycleOnFilters = optionsPageContentJSON.getString(SPLITTING_FILTER);
				logger.debug("Input parameter [" + SPLITTING_FILTER + "] is equal to [" + cycleOnFilters + "]");
				splittingFilter = false;
				if(cycleOnFilters != null) splittingFilter = Boolean.valueOf(cycleOnFilters);

				parametersPageContentJSON = this.getAttributeAsJSONObject(PARAMETERS_PAGE);
				logger.debug("Input parameter [" + PARAMETERS_PAGE + "] is equal to [" + parametersPageContentJSON + "]");
				Assert.assertNotNull(parametersPageContentJSON, "Input parameter [" + PARAMETERS_PAGE + "] cannot be null");
				
				triggerPageContentJSON = this.getAttributeAsJSONObject(TRIGGER_PAGE);
				logger.debug("Input parameter [" + OPTIONS_PAGE + "] is equal to [" + triggerPageContentJSON + "]");
				Assert.assertNotNull(triggerPageContentJSON, "Input parameter [" + OPTIONS_PAGE + "] cannot be null");

			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Error in retrieving parameters: ", t);
			} 

			LowFunctionality folder = getFolder(folderId);
			logger.debug("Target folder is [" + folder.getName() + "]");
			List<BIObject> documentsToExport = getDocumentsToExport(folder, documentType);
			logger.debug("Target folder [" + folder.getName() + "] contains [" + documentsToExport.size() + "] document(s) of type [" + documentType + "] to export");
			
			JSONObject generalConfJSON = triggerPageContentJSON.getJSONObject("generalConf");
			
			// create the job
			JSONObject jobConfJSON = generalConfJSON.getJSONObject("job");
			try {
					
				jobConfJSON.put("name", getName(getUserProfile(), folder));
				jobConfJSON.put("description", getDescription(getUserProfile(), folder));
				jobConfJSON.put("groupName", getGroupName(getUserProfile(), folder));
				job = createJob(jobConfJSON, documentsToExport, parametersPageContentJSON);
				
				job.addParameters( createDistpachChannelParameters(documentsToExport, getUserProfile(), folder) );				
				job.addParameter("modality", SpagoBIConstants.MASSIVE_EXPORT_MODALITY);
				job.addParameter("outputMIMEType", outputMIMEType);
				job.addParameter("isSplittingFilter", splittingFilter? "true": "false");
				
				
				
				Assert.assertNotNull(job, "Impossible to create job [" + jobConfJSON + "]");
				logger.debug("Job [" + job + "] succesfully created");
			} catch (SpagoBIServiceException t) {
				throw (t);		
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An unexpecte error occered while creating job [" + jobConfJSON + "]", t);
			} 
			
			
			// create the trigger
			JSONObject triggerConfJSON = generalConfJSON.getJSONObject("trigger");
			try {
				triggerConfJSON.put("name", getName(getUserProfile(), folder));
				triggerConfJSON.put("description", getDescription(getUserProfile(), folder));
				triggerConfJSON.put("groupName", getGroupName(getUserProfile(), folder));
				
				trigger = createTrigger(triggerConfJSON);
				JSONObject cronConfJSON = triggerPageContentJSON.getJSONObject("cronConf");
				CronExpression cronExpression = getChronExpression(cronConfJSON);
				trigger.setCronExpression(cronExpression);			
				trigger.setJob(job);
				
				Assert.assertNotNull(job, "Impossible to create trugger [" + triggerConfJSON + "]");
				
				logger.debug("Trigger [" + trigger + "] succesfully created");
			} catch (SpagoBIServiceException t) {
				throw (t);		
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "An unexpecte error occered while creating trigger [" + triggerConfJSON + "]", t);
			} 
			
			// save job and trigger
			schedulerDAO = DAOFactory.getSchedulerDAO();
		
			// the job first
			try {			
				schedulerDAO.insertJob(job);
				jobSuccesfullySaved = true;
				logger.debug("Job [" + job + "] succesfully saved");
			} catch (SpagoBIServiceException t) {
				throw (t);		
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to save job [" + job + "]", t);
			} 
			// the trigger then
			try {
				schedulerDAO.saveTrigger(trigger);
				triggerSuccesfullySaved = true;
				logger.debug("Trigger [" + trigger + "] succesfully saved");
			} catch (SpagoBIServiceException t) {
				throw (t);		
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to save trigger [" + job + "]", t);
			} 
			
		} catch (SpagoBIServiceException t) {
			throw (t);		
		} catch (Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An unexpected error occuerd while executing service ["+ SERVICE_NAME + "]", t);
		} finally {
			if(jobSuccesfullySaved && !triggerSuccesfullySaved) {
				logger.debug("Rolback operation is required");
				schedulerDAO.deleteJob(job.getName(), job.getGroupName());
				logger.debug("Job [" + job + " has been deleted]");
				logger.debug("Rolback operation executed succesfully");
			}
			logger.debug("OUT");
		}
	}
	
	// we use the same group name for job and trigger
	private String getGroupName(IEngUserProfile userProfile, LowFunctionality folder) {
		String name = "private/users" + "/" + userProfile.getUserUniqueIdentifier() + "/massive/" + folder.getName();
		return name;
	}
	
	// we use the same name for job and trigger
	private String getName(IEngUserProfile userProfile, LowFunctionality folder) {
		String name = userProfile.getUserUniqueIdentifier() + "@" + folder.getCode();
		return name;
	}
	
	// we use the same name for job and trigger
	private String getDescription(IEngUserProfile userProfile, LowFunctionality folder) {
		String description = "Massive scheduling defined by user [" + userProfile.getUserUniqueIdentifier() + "] on folder [" + folder.getName() + "]";
		return description;
	}
	
	private LowFunctionality getFolder(Integer folderId) {
		LowFunctionality folder;
		
		logger.debug("OUT");
		
		folder = null;
		try {
			ILowFunctionalityDAO functionalityTreeDao = DAOFactory.getLowFunctionalityDAO();
			folder = functionalityTreeDao.loadLowFunctionalityByID(folderId, true);
			Assert.assertNotNull(folder, "Folder [" + folderId + "] cannot be loaded");
		} catch(Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An unexpected error occuerd while loading folder ["+ folderId + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return folder;
	}
	
	private List<BIObject> getDocumentsToExport(LowFunctionality folder, String documentType) {
		List<BIObject> documentsToExport = null;
		documentsToExport = Utilities.getContainedObjFilteredbyType(folder, documentType);
		return documentsToExport;
	}
	
	private Job createJob(JSONObject jobConfJSON, List<BIObject> documentsToExport, JSONObject documentsParameterValuesJSON) {
		Job job;
		
		logger.debug("IN");
		
		job = null;
		try {
			job = new Job();
			job.setName( jobConfJSON.getString("name") );
			job.setDescription( jobConfJSON.optString("description") );
			job.setGroupName( jobConfJSON.getString("groupName") );
			job.setRequestsRecovery(false);
			job.setJobClass(XExecuteBIDocumentJob.class);
			
			Map<String, String> parameters = createJobParameters(documentsToExport, documentsParameterValuesJSON);			
			job.addParameters(parameters);
			
		} catch(Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An unexpected error occuerd while creating job", t);
		} finally {
			logger.debug("OUT");
		}
		
		return job;
	}

	private Map<String, String> createJobParameters(List<BIObject> documentsToExport, JSONObject documentsParameterValuesJSON) {
		Map<String, String> parameters;
		
		logger.debug("IN");
		
		parameters = new HashMap<String,String>();
		try {
			// documentLabel__num this is necessary because the same document can be added to one scheduled activity more than one time
			int docNo = 1;
			for(BIObject document : documentsToExport) {
				String pName = document.getLabel() + "__" + docNo++;
				String pValue = "";
				String separetor = "";
				List<BIObjectParameter> documentParameters = document.getBiObjectParameters();
				for(BIObjectParameter documentParameter : documentParameters) {
					String documentParameterLabel = documentParameter.getLabel();
					String value = documentsParameterValuesJSON.getString(documentParameterLabel);
					pValue += separetor + documentParameterLabel + "="+ value;
					separetor = "%26";
				}
				parameters.put(pName, pValue);
			}
			
			String value = createDocumentLabelsParameterValue(documentsToExport);
			parameters.put("documentLabels", value);
		
		} catch(Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An unexpected error occuerd while creating job's parameters", t);
		} finally {
			logger.debug("OUT");
		}
		
		return parameters;
	}
	
	private String createDocumentLabelsParameterValue(List<BIObject> documentsToExport) {
		String value = "";
		
		String separetor = "";
		int docNo = 1;
		for(BIObject document : documentsToExport) {
			value += separetor + document.getLabel() + "__" + docNo++;
			separetor = ",";				
		}
		return value;
	}
	
	private Map<String, String> createDistpachChannelParameters(List<BIObject> documentsToExport, IEngUserProfile userProfile, LowFunctionality folder) {
		Map<String, String> parameters;
		String name;
		String value;
		
		parameters = new HashMap<String, String>();

		File destinationFolder = Utilities.getMassiveScheduleZipFolder(
				(String)userProfile.getUserUniqueIdentifier(), folder.getCode());
		
		
		name = "globalDispatcherContext";
		value = "saveasfile=true"
			+ "%26" + "destinationfolder=" + destinationFolder.getAbsolutePath()
			+ "%26" + "functionalitytreefolderlabel=" + folder.getCode()
			+ "%26" + "owner=" + (String)userProfile.getUserUniqueIdentifier();
	

		parameters.put(name, value);
		
		return parameters;   	   
		
//		int docNo = 1;
//		for(BIObject document : documentsToExport) {
//			name = "biobject_id_" + document.getId() + "__" + docNo++;
//			value = "saveassnapshot=true"
//				+ "%26" + "snapshotname=" + "scheduler"	
//				+ "%26" + "snapshotdescription=" + "generatedByScheduler"
//				+ "%26" + "snapshothistorylength=" + "5";
//			
//			parameters.put(name, value);
//			
//			name = "biobject_id_" + document.getId() + "__" + docNo++;
//			value = "saveasfile=true";
//
//			
//			parameters.put(name, value);
//		}
	}

	private Date getTime(String dateStr, String timeStr) throws ParseException {
		Calendar calendar;
	
		calendar = null;
		
		if(StringUtilities.isNotEmpty(dateStr)) {
			DateFormat dataFormat = new SimpleDateFormat( GeneralUtilities.getServerDateFormat());
			Date date = dataFormat.parse(dateStr);
			calendar = new GregorianCalendar();
			calendar.setTime(date);
			
			if(StringUtilities.isNotEmpty(timeStr)) {
				DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
				Date time = timeFormat.parse(timeStr);
				Calendar timeCalendar = new GregorianCalendar();
				timeCalendar.setTime(time);
				calendar.set(calendar.HOUR, timeCalendar.get(calendar.HOUR));
				calendar.set(calendar.MINUTE, timeCalendar.get(calendar.MINUTE));
				calendar.set(calendar.AM_PM, timeCalendar.get(calendar.AM_PM));
			}
		}
		
		return calendar != null? calendar.getTime(): null;
	}

	private Trigger createTrigger(JSONObject triggerConfJSON) {
		Trigger trigger;
		
		logger.debug("IN");
		
		trigger = null;
		try {
			trigger = new Trigger();
			
			trigger.setName( triggerConfJSON.getString("name") );
			trigger.setDescription( triggerConfJSON.optString("description") );
			
			String startDateStr = triggerConfJSON.optString("startDate");
			if( StringUtilities.isEmpty(startDateStr) ) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Start date cannot be empty");
			}
			String startTimeStr = triggerConfJSON.optString("startTime");
			Date startTime = getTime(startDateStr, startTimeStr);
			trigger.setStartTime(startTime);
			
			String endDateStr = triggerConfJSON.optString("endDate");
			String endTimeStr = triggerConfJSON.optString("endTime");
			Date endTime = getTime(endDateStr, endTimeStr);
			trigger.setEndTime(endTime);		
		} catch(Throwable t) {
			throw new SpagoBIServiceException(SERVICE_NAME, "An unexpected error occuerd while creating job's parameters", t);
		} finally {
			logger.debug("OUT");
		}
		
		return trigger;
	}
	
	private CronExpression getChronExpression(JSONObject cronConfJSON) {
       
		StringBuffer expression;
		JSONObject minutesOptionsJSON;
		JSONObject hourlyOptionsJSON;
        
        // TODO manage no cron
        //boolean noRepetition = triggerJSON.optBoolean("noRepetition");
        
		logger.debug("IN");
		try {
	        minutesOptionsJSON = cronConfJSON.optJSONObject("minutes");
	        hourlyOptionsJSON = cronConfJSON.optJSONObject("hourly");
	//       
	//    	if($('single_repetitionKind').checked) {
	//    		repStr = repStr + 'single{}';
	//    	}
	//    	
	        expression = new StringBuffer();
	        
	    	if(minutesOptionsJSON != null) {
	    		String minutes = minutesOptionsJSON.optString("minutes");
	    		if( StringUtilities.isNotEmpty(minutes) ) {
		    		expression.append("minute{");    		
		    		expression.append("numRepetition=");
		    		expression.append(minutes);
		    		expression.append("}");
	    		}
	    	}
	    	
	    	if(hourlyOptionsJSON != null) {
	    		String houres = hourlyOptionsJSON.optString("houres");
	    		if( StringUtilities.isNotEmpty(houres)) {
		    		expression.append("hour{");
		    		expression.append("numRepetition=");
		    		expression.append(houres);
		    		expression.append("}");
	    		}
	    	}
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occuerd while creating cron expression", t);
		} finally {
			logger.debug("OUT");
		}
    	
    	return new CronExpression(expression.toString());
    	
//    	if($('day_repetitionKind').checked) {
//    		repStr = repStr + 'day{';
//    		rep_n = $('day_repetition_n').options[$('day_repetition_n').selectedIndex].value;
//    		repStr = repStr + 'numRepetition='+rep_n;
//    		repStr = repStr + '}';
//    	}
//    	if($('week_repetitionKind').checked) {
//    		repStr = repStr + 'week{';
//    		rep_n = $('week_repetition_n').options[$('week_repetition_n').selectedIndex].value;
//    		repStr = repStr + 'numRepetition='+rep_n+';days=';
//    		
//    		if($('day_in_week_rep_sun').checked) {
//    			repStr = repStr + 'SUN,';
//    		}
//    		if($('day_in_week_rep_mon').checked) {
//    			repStr = repStr + 'MON,';
//    		}
//    		if($('day_in_week_rep_tue').checked) {
//    			repStr = repStr + 'TUE,';
//    		}
//    		if($('day_in_week_rep_wed').checked) {
//    			repStr = repStr + 'WED,';
//    		}
//    		if($('day_in_week_rep_thu').checked) {
//    			repStr = repStr + 'THU,';
//    		}
//    		if($('day_in_week_rep_fri').checked) {
//    			repStr = repStr + 'FRI,';
//    		}
//    		if($('day_in_week_rep_sat').checked) {
//    			repStr = repStr + 'SAT,';
//    		}
//    		repStr = repStr + '}';
//    	}
//    	if($('month_repetitionKind').checked) {
//    		repStr = repStr + 'month{';
//    		if($('month_selection_interval').checked) {
//    			rep_n = $('monthrep_n').options[$('monthrep_n').selectedIndex].value;
//    			repStr = repStr + 'numRepetition='+rep_n+';';
//    			repStr = repStr + 'months=NONE;';
//    		}
//    		if($('month_selection_checks').checked) {
//    			repStr = repStr + 'numRepetition=0;';
//    			repStr = repStr + 'months=';
//    			if($('monthrep_jan').checked) repStr = repStr + 'JAN,';
//    			if($('monthrep_feb').checked) repStr = repStr + 'FEB,';
//    			if($('monthrep_mar').checked) repStr = repStr + 'MAR,';
//    			if($('monthrep_apr').checked) repStr = repStr + 'APR,';
//    			if($('monthrep_may').checked) repStr = repStr + 'MAY,';
//    			if($('monthrep_jun').checked) repStr = repStr + 'JUN,';
//    			if($('monthrep_jul').checked) repStr = repStr + 'JUL,';
//    			if($('monthrep_aug').checked) repStr = repStr + 'AUG,';
//    			if($('monthrep_sep').checked) repStr = repStr + 'SEP,';
//    			if($('monthrep_oct').checked) repStr = repStr + 'OCT,';
//    			if($('monthrep_nov').checked) repStr = repStr + 'NOV,';
//    			if($('monthrep_dic').checked) repStr = repStr + 'DIC,';	
//    			repStr = repStr + ';';
//    		}
//    		if($('dayinmonth_selection_interval').checked) {
//    			rep_n = $('dayinmonthrep_n').options[$('dayinmonthrep_n').selectedIndex].value;
//    			repStr = repStr + 'dayRepetition='+rep_n+';';
//    			repStr = repStr + 'weeks=NONE;';
//    			repStr = repStr + 'days=NONE;';
//    		}
//    		if($('dayinmonth_selection_checks').checked) {
//    			repStr = repStr + 'dayRepetition=0;';
//    			repStr = repStr + 'weeks=';
//    			weekstr = '';
//    			if($('dayinmonthrep_week1').checked) weekstr = weekstr + '1';
//    			if($('dayinmonthrep_week2').checked) weekstr = weekstr + '2';
//    			if($('dayinmonthrep_week3').checked) weekstr = weekstr + '3';
//    			if($('dayinmonthrep_week4').checked) weekstr = weekstr + '4';
//    			if($('dayinmonthrep_weekL').checked) weekstr = weekstr + 'L';
//    			if(weekstr=='') weekstr='NONE';
//    			repStr = repStr + weekstr + ';';
//    			repStr = repStr + 'days=';
//    			daystr = '';
//    			if($('dayinmonthrep_sun').checked) daystr = daystr + 'SUN,';
//    			if($('dayinmonthrep_mon').checked) daystr = daystr + 'MON,';
//    			if($('dayinmonthrep_tue').checked) daystr = daystr + 'TUE,';
//    			if($('dayinmonthrep_wed').checked) daystr = daystr + 'WED,';
//    			if($('dayinmonthrep_thu').checked) daystr = daystr + 'THU,';
//    			if($('dayinmonthrep_fri').checked) daystr = daystr + 'FRI,';
//    			if($('dayinmonthrep_sat').checked) daystr = daystr + 'SAT,';
//    			if(daystr=='') daystr='NONE';
//    			repStr = repStr + daystr + ';';
//    		}
//    		repStr = repStr + '}';
//    	}

    }
}
