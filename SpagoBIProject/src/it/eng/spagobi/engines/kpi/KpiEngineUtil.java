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

package it.eng.spagobi.engines.kpi;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.engines.kpi.bo.KpiLine;
import it.eng.spagobi.engines.kpi.bo.KpiLineVisibilityOptions;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.KpiValue;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KpiEngineUtil {
	
	static transient Logger logger = Logger.getLogger(KpiEngineUtil.class);
	private static ExecutionInstance kpiInstance;
	private static Locale kpiInstanceLocale;
	
	protected static SourceBean getTemplate(String documentId) throws EMFUserError{
		logger.debug("IN");
		SourceBean content = null;
		byte[] contentBytes = null;
		try {
			ObjTemplate template = DAOFactory.getObjTemplateDAO().getBIObjectActiveTemplate(Integer.valueOf(documentId));
			if (template == null) {
				logger.warn("Active Template null.");
				throw new Exception("Active Template null.");
			}
			contentBytes = template.getContent();
			if (contentBytes == null) {
				logger.warn("Content of the Active template null.");
				throw new Exception("Content of the Active template null");
			}
			// get bytes of template and transform them into a SourceBean
			String contentStr = new String(contentBytes);
			content = SourceBean.fromXMLString(contentStr);
			logger.debug("Got the content of the template");
		} catch (Exception e) {
			logger.error("Error while converting the Template bytes into a SourceBean object");
			EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 2003);
			userError.setBundle("messages");
			throw userError;
		}
		logger.debug("OUT");
		return content;
	}

	
	protected static KpiEnginData setGeneralVariables(RequestContainer requestContainer){
		logger.debug("IN");
		KpiEnginData data = new KpiEnginData();
		String isScheduledExec = (String)requestContainer.getAttribute("scheduledExecution");
		if(isScheduledExec != null && Boolean.valueOf(isScheduledExec)){
			data.setExecutionModalityScheduler(true);
		}
		SessionContainer session = requestContainer.getSessionContainer();
		data.setProfile( (IEngUserProfile) session.getPermanentContainer().getAttribute(
				IEngUserProfile.ENG_USER_PROFILE));

		data.setLocale(GeneralUtilities.getDefaultLocale());
		data.setLang((String)session.getPermanentContainer().getAttribute(SpagoBIConstants.AF_LANGUAGE));
		data.setCountry((String)session.getPermanentContainer().getAttribute(SpagoBIConstants.AF_COUNTRY));
		if(data.getLang()!=null && data.getCountry()!=null){
			data.setLocale(new Locale(data.getLang(), data.getCountry(),""));
		}

		String internationalizedFormatSB = null; 
		if(data.getLang()!=null && data.getCountry()!=null){
			internationalizedFormatSB = (SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-"+data.getLang().toUpperCase()+"_"+data.getCountry().toUpperCase()+".format"));				
		}else{
			internationalizedFormatSB = (SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-SERVER.format"));
		}
		data.setInternationalizedFormat(internationalizedFormatSB);	
		String formatServerSB = (SingletonConfig.getInstance().getConfigValue(("SPAGOBI.DATE-FORMAT-SERVER.format")));
		data.setFormatServer( formatServerSB);
		
		logger.debug("OUT");
		return data;
	}
	public static KpiLineVisibilityOptions setVisibilityOptions(KpiTemplateConfiguration templateConfiguration){
		logger.debug("IN");
		KpiLineVisibilityOptions options = new KpiLineVisibilityOptions();
		options.setClosed_tree(templateConfiguration.isClosed_tree());
		options.setDisplay_alarm(templateConfiguration.isDisplay_alarm());
		options.setDisplay_bullet_chart(templateConfiguration.isDisplay_bullet_chart());
		options.setDisplay_semaphore(templateConfiguration.isDisplay_semaphore());
		options.setDisplay_threshold_image(templateConfiguration.isDisplay_threshold_image());
		options.setDisplay_weight(templateConfiguration.isDisplay_weight());
		options.setShow_axis(templateConfiguration.isShow_axis());
		options.setWeighted_values(templateConfiguration.isWeighted_values());

		options.setBullet_chart_title(templateConfiguration.getBullet_chart_title());
		options.setKpi_title(templateConfiguration.getKpi_title());
		options.setModel_title(templateConfiguration.getModel_title());
		options.setThreshold_image_title(templateConfiguration.getThreshold_image_title());
		options.setWeight_title(templateConfiguration.getWeight_title());
		options.setValue_title(templateConfiguration.getValue_title());
		logger.debug("OUT");
		return options;
	}
	
	protected static HashMap readParameters(List parametersList, SpagoBIKpiInternalEngine engine) throws EMFUserError {
		logger.debug("IN");
		if (parametersList == null) {
			logger.warn("parametersList si NULL!!!");
			return new HashMap();
		}
		HashMap parametersMap = new HashMap();
		logger.debug("Check for BIparameters and relative values");

		for (Iterator iterator = parametersList.iterator(); iterator.hasNext();) {
			SimpleDateFormat f = new SimpleDateFormat();
			f.applyPattern(engine.data.getFormatServer());
			BIObjectParameter par = (BIObjectParameter) iterator.next();
			String url = par.getParameterUrlName();
			List values = par.getParameterValues();
			if (values != null) {
				if (values.size() == 1) {
					if (url.equals("ParKpiResources")) {
						engine.resources = new ArrayList();
						String value = (String) values.get(0);
						Integer res = new Integer(value);
						Resource toAdd = DAOFactory.getResourceDAO().loadResourceById(res);
						engine.resources.add(toAdd);
					}else if (url.equals("ParKpiResourcesCode")) {
						engine.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceByCode(value);
							engine.resources.add(toAdd);
						}
					}else if(url.equals("register_values")){
						String value = (String) values.get(0);
						if (value.equalsIgnoreCase("true")){
							engine.templateConfiguration
									.setRegister_values(true);
							engine.templateConfiguration
									.setRegister_par_setted(true);
						}else if (value.equalsIgnoreCase("false")){
							engine.templateConfiguration
									.setRegister_values(false);
							engine.templateConfiguration
									.setRegister_par_setted(true);
						}
					}else if (url.equals("behaviour")){
						String value = (String) values.get(0);
						engine.parameters.setBehaviour(value);
						logger.debug("Behaviour is: "+ engine.parameters.getBehaviour());
					}else if(url.equals("dataset_multires")){
						String value = (String) values.get(0);
						if (value.equalsIgnoreCase("true")){
							engine.templateConfiguration
									.setDataset_multires(true);
						}else if (value.equalsIgnoreCase("false")){
							engine.templateConfiguration
									.setDataset_multires(false);
						}
					}else{
						String value = (String) values.get(0);	
						if (url.equals("ParKpiDate")) {
							value = setCalculationDateOfKpi(value, engine);		
						}else if (url.equals("TimeRangeFrom")) {
							try {
								engine.parameters.setTimeRangeFrom(f.parse(value));
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE FROM");
						}else if (url.equals("TimeRangeTo")) {
							try {
								engine.parameters.setTimeRangeTo(f.parse(value));
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}else if(url.equals("dateIntervalFrom")){
							try {
								engine.parameters.setDateIntervalFrom(f.parse(value));
								value = getDateForDataset(engine.parameters.getDateIntervalFrom());
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}else if(url.equals("dateIntervalTo")){
							try {
								engine.parameters.setDateIntervalTo(f.parse(value));
								value = getDateForDataset(engine.parameters.getDateIntervalTo());
							} catch (ParseException e) {
								logger.error("ParseException.value=" + value, e);
							}
							logger.debug("Setted TIME RANGE TO");
						}
						parametersMap.put(url, value);
					}   
					//instead if parameter has more than one value
				}else if (values != null && values.size() >= 1) {
					if (url.equals("ParKpiResources")) {
						engine.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Integer res = new Integer(value);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceById(res);
							engine.resources.add(toAdd);
						}
					}else if (url.equals("ParKpiResourcesCode")) {
						engine.resources = new ArrayList();
						for (int k = 0; k < values.size(); k++) {
							String value = (String) values.get(k);
							Resource toAdd = DAOFactory.getResourceDAO().loadResourceByCode(value);
							engine.resources.add(toAdd);
						}
					}else {
						String value = "'" + (String) values.get(0) + "'";
						for (int k = 1; k < values.size(); k++) {
							value = value + ",'" + (String) values.get(k) + "'";
						}					
						parametersMap.put(url, value);
					}
				}
			}
		}
		logger.debug("OUT. Date:" + engine.parameters.getDateOfKPI());
		return parametersMap;
	}

	protected static String getDateForDataset(Date d){
		String toReturn = "";
		String formatSB = (SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-SERVER.format"));
		String format = formatSB;
		SimpleDateFormat f = new SimpleDateFormat();
		f.applyPattern(format);
		toReturn = f.format(d);
		return toReturn;
	}

	private static String setCalculationDateOfKpi(String value, SpagoBIKpiInternalEngine engine){
		logger.debug("IN");
		String formatSB = (SingletonConfig.getInstance().getConfigValue("SPAGOBI.DATE-FORMAT-SERVER.format"));
		String format = formatSB;
		SimpleDateFormat f = new SimpleDateFormat();
		f.applyPattern(format);
		String temp = f.format(engine.parameters.getDateOfKPI());
		try {
			engine.parameters.setDateOfKPI(f.parse(value));
			Long milliseconds = engine.parameters.getDateOfKPI().getTime();
			//If the date required is today then the time considered will be the actual date
			if(temp.equals(value)){
				Calendar calendar = new GregorianCalendar();
				int hrs = calendar.get(Calendar.HOUR); 
				int mins = calendar.get(Calendar.MINUTE); 
				int secs = calendar.get(Calendar.SECOND); 
				int AM = calendar.get(Calendar.AM_PM);//if AM then int=0, if PM then int=1
				if(AM==0){
					int millisec =  (secs*1000) + (mins *60*1000) + (hrs*60*60*1000);
					Long milliSecToAdd = new Long (millisec);
					milliseconds = new Long(milliseconds.longValue()+milliSecToAdd.longValue());
					engine.parameters.setDateOfKPI(new Date(milliseconds));
				}else{
					int millisec =  (secs*1000) + (mins *60*1000) + ((hrs+12)*60*60*1000);
					Long milliSecToAdd = new Long (millisec);
					milliseconds = new Long(milliseconds.longValue()+milliSecToAdd.longValue());
					engine.parameters.setDateOfKPI(new Date(milliseconds));
				}    

				String h = "";
				String min = "";
				String sec = "";
				if(hrs<10){
					if(AM==0){
						h="0"+hrs;
					}else{
						hrs = hrs+12;
						h=""+hrs;
					}
				}else{
					if(AM==0){
						h =""+ hrs;
					}else{
						hrs = hrs+12;
						h=""+hrs;
					}
				}
				if(mins<10){
					min="0"+mins;
				}else{
					min =""+ mins;
				}
				if(secs<10){
					sec="0"+secs;
				}else{
					sec =""+ secs;
				}
				value ="'"+ value +" "+h+":"+min+":"+sec+"'";
			}else{
				value ="'"+ value +" 00:00:00'";
			}
		} catch (ParseException e) {
			logger.error("ParseException.value=" + value, e);
		}
		logger.debug("OUT");
		return value;
	}
	
	public void setExecutionInstance(ExecutionInstance instance, Locale locale){
		kpiInstance = instance;
		kpiInstanceLocale = locale;
	}

	public JSONObject recursiveGetJsonObject(KpiLine kpiLine) {

		JSONObject jsonToReturn = new JSONObject();
		try {

			jsonToReturn.put("name", kpiLine.getModelNodeName());
			if (kpiLine.getValue() != null) {
				jsonToReturn.put("actual", kpiLine.getValue().getValue());
				jsonToReturn.put("target", kpiLine.getValue().getTarget());
				//jsonToReturn.put("iconCls","has-kpi");
				jsonToReturn.put("iconCls","");
			}else{
				jsonToReturn.put("actual", "");
				jsonToReturn.put("target", "");
				jsonToReturn.put("iconCls", "folder");
				jsonToReturn.put("cls", "node-folder");
			}
			String color = detectColor(kpiLine.getValue());
			jsonToReturn.put("status", color);

			jsonToReturn.put("expanded", true);
			
			setKpiInfos(kpiLine, jsonToReturn);
			setDetailInfos(kpiLine, jsonToReturn);
			
			//documents
			List documents = kpiLine.getDocuments();
			if(documents != null && !documents.isEmpty()){
				String docLabel =(String)documents.get(0);
				//return only one document
				jsonToReturn.putOpt("documentLabel", docLabel);
				//gets url for execution

				ExecutionInstance docExecInst = ExecutionInstance.getExecutionInstanceByLabel(kpiInstance, docLabel);
				String executionUrl = docExecInst.getExecutionUrl(kpiInstanceLocale);
				String encodedUrl = URLEncoder.encode(executionUrl);
				jsonToReturn.putOpt("documentExecUrl", executionUrl);
			}

			List<KpiLine> children = (List<KpiLine>) kpiLine.getChildren();
			JSONArray JSONArrayChildren = new JSONArray();

			if (children != null) {
				for (int i = 0; i < children.size(); i++) {

					KpiLine kpiChildLine = children.get(i);
					JSONObject child  = recursiveGetJsonObject(kpiChildLine);
					JSONArrayChildren.put(child);
				}
				jsonToReturn.put("children", JSONArrayChildren);
			}

		} catch (JSONException e) {
			logger.error("Error setting children");
		} catch (Exception e) {
			logger.error("Error getting execution instances");
		}

		return jsonToReturn;

	}
	private void setKpiInfos(KpiLine kpiLine, JSONObject row) throws JSONException{
		Integer kpiInstId = getTrend(kpiLine, row);
		
		try {
			if(kpiInstId != null){
				KpiInstance kpiInst = DAOFactory.getKpiInstanceDAO().loadKpiInstanceById(kpiInstId);
				Integer kpiId = kpiInst.getKpi();
				Kpi kpi = DAOFactory.getKpiDAO().loadKpiById(kpiId);
				row.putOpt("kpiDescr", kpi.getDescription());
				row.putOpt("kpiName", kpi.getKpiName());
				row.putOpt("kpiCode", kpi.getCode());
				row.putOpt("kpiDsLbl", kpi.getDsLabel());
				row.putOpt("kpiTypeCd", kpi.getKpiTypeCd());
				row.putOpt("measureTypeCd", kpi.getMeasureTypeCd());
				row.putOpt("scaleName", kpi.getScaleName());
				row.putOpt("targetAudience", kpi.getTargetAudience());
				
				row.putOpt("kpiInstId", kpiInstId);
			}
		} catch (EMFUserError e) {
			logger.error(e);
		}

	}
	private void setDetailInfos(KpiLine kpiLine, JSONObject row){
		JSONArray thresholds = new JSONArray();
		if(kpiLine.getValue() != null){
			Double weight = kpiLine.getValue().getWeight();
			
			List thrs = kpiLine.getValue().getThresholdValues();
			if(thrs != null ){
				
				for(int i=0; i< thrs.size(); i++){
					JSONObject threshold = new JSONObject();
					ThresholdValue tv = (ThresholdValue)thrs.get(i);
					String color = tv.getColourString();
					String label = tv.getLabel();
					String type = tv.getThresholdType();
					Double max = tv.getMaxValue();
					Double min = tv.getMinValue();
					
					try {
						threshold.putOpt("color", color);
						threshold.putOpt("label", label);
						threshold.putOpt("type", type);
						threshold.putOpt("max", max);
						threshold.putOpt("min", min);
						
						thresholds.put(threshold);
						
					} catch (JSONException e) {
						logger.error("Error setting threshold");
					}
				}
				try {
					row.put("thresholds", thresholds);
					row.putOpt("weight", weight);
				} catch (JSONException e) {
					logger.error("Error setting thresholds");
				}
			}
		}
	}
	private String detectColor(KpiValue value){
		String ret = "";
		if(value == null){
			return ret;
		}
		if(value.getThresholdOfValue() != null && value.getThresholdOfValue().getColourString() != null){
			return value.getThresholdOfValue().getColourString();
		}else{
			//calculate it
			String val = value.getValue();
			getStatus(value.getThresholdValues(), Double.parseDouble(val));
			
		}

		return ret;
		
	}
	public String getStatus(List thresholdValues, double val) {
		logger.debug("IN");
		String status = "";
		if(thresholdValues!=null && !thresholdValues.isEmpty()){
			Iterator it = thresholdValues.iterator();

			while(it.hasNext()){
				ThresholdValue t = (ThresholdValue)it.next();
				String type = t.getThresholdType();
				Double min = t.getMinValue();
				Double max = t.getMaxValue();
				if(val <= max && val >= min){
					status = t.getColourString();
				}		
				logger.debug("New interval added to the Vector");
			}
		}
		logger.debug("OUT");
		return status;
		
	}
	
	private Integer getTrend(KpiLine kpiLine, JSONObject row){
		Integer toReturn = null;
		KpiValue value = kpiLine.getValue();
		Integer modelInstId = kpiLine.getModelInstanceNodeId();
		try {
			ModelInstanceNode node = DAOFactory.getModelInstanceDAO().loadModelInstanceById(modelInstId, null);
			KpiInstance kpiInst= node.getKpiInstanceAssociated();
			Integer kpiInstId = kpiInst.getKpiInstanceId();
			Integer trend = DAOFactory.getKpiDAO().getKpiTrend(null, kpiInstId, value.getBeginDate());
			row.putOpt("trend", trend);
			toReturn = kpiInstId;
		} catch (Exception e) {
			logger.error("Error retrieving modelinstance "+modelInstId, e);
		}
		return toReturn;
	}
}
