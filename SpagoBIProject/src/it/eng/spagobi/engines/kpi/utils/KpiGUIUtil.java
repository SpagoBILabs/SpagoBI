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
package it.eng.spagobi.engines.kpi.utils;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.kpi.bo.KpiLine;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.KpiValue;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.tools.udp.bo.UdpValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KpiGUIUtil {
	static transient Logger logger = Logger.getLogger(KpiGUIUtil.class);
	private static ExecutionInstance kpiInstance;
	private static Locale kpiInstanceLocale;
	private static List parameters ;
	private static String visibilityParameterValues = null;
	
	public void setExecutionInstance(ExecutionInstance instance, Locale locale){
		kpiInstance = instance;
		kpiInstanceLocale = locale;
		parameters = kpiInstance.getBIObject().getBiObjectParameters();
		if(parameters != null){
			for(int i=0; i<parameters.size(); i++){
				BIObjectParameter par = (BIObjectParameter)parameters.get(i);
				if(par.getParameterUrlName().equals("visibilityParameter")){
					visibilityParameterValues = par.getParameterValuesAsString();
				}
			}
		}
		
	}
	public boolean isVisible(KpiLine kpiLine){
		boolean visible = false;
		if(visibilityParameterValues == null){
			return true;
		}
		Integer modelInstId = kpiLine.getModelInstanceNodeId();
		try {
			ModelInstanceNode node = DAOFactory.getModelInstanceDAO().loadModelInstanceById(modelInstId, null);
			Integer modelId= node.getModelNodeId();
			if(modelId != null){
				Model model = DAOFactory.getModelDAO().loadModelWithoutChildrenById(modelId);
				List<UdpValue> udps = model.getUdpValues();
				if(udps != null){
					for(int i=0; i<udps.size(); i++){
						UdpValue udpVal = udps.get(i);
						String udpName = udpVal.getName();
						if(udpName.equals("VISIBILITY")){
							String val = udpVal.getValue();
							if(val != null && !val.equals("")){
								//can be multivalue with 'aa','bb','cc'...format
								String [] multival = val.split(",");
								if(multival.length != 0){
									for(int k = 0; k< multival.length; k++){
										String v = multival[k].replaceAll("'", "").trim();
										logger.debug(v+"-"+visibilityParameterValues);
										if(visibilityParameterValues.equals(v)){
											visible = true;
										}
									}
								}else{
									//single value
									if(visibilityParameterValues.equals(val)){
										visible = true;									
									}
								}
							}
						}

					}
					logger.debug("if udp is present passes a upd name = parameter name to dataset, by ading it to HashMap pars");
				}
				
			}
			
		} catch (Exception e) {
			logger.error("Error retrieving modelinstance "+modelInstId, e);
		}
		
		return visible;
	}
	public JSONObject recursiveGetJsonObject(KpiLine kpiLine) {

		JSONObject jsonToReturn = new JSONObject();
		try {

			boolean isVisible = isVisible(kpiLine);
			if(!isVisible){
				jsonToReturn.put("hidden",true);
			}
			String name = kpiLine.getModelNodeName();
			if(name.length() >= 30){
				name = name.substring(0,30) + "...";
			}
			jsonToReturn.put("name", name);
			jsonToReturn.put("qtip", kpiLine.getModelNodeName());
			
			List<KpiLine> children = (List<KpiLine>) kpiLine.getChildren();

			if (children != null) {
				
				JSONArray jsonArrayChildren = new JSONArray();
				for (int i = 0; i < children.size(); i++) {

					KpiLine kpiChildLine = children.get(i);
					JSONObject child  = recursiveGetJsonObject(kpiChildLine);
					jsonArrayChildren.put(child);
				}
				jsonToReturn.put("children", jsonArrayChildren);
			}
			KpiValue kpivalue= kpiLine.getValue();
			if (kpivalue != null) {
				jsonToReturn.put("actual", kpiLine.getValue().getValue());
				jsonToReturn.put("target", kpiLine.getValue().getTarget());

				if(children != null && !children.isEmpty()){
					jsonToReturn.put("iconCls", "folder");
					jsonToReturn.put("cls", "node-folder");
				}else{
					jsonToReturn.put("iconCls","");
				}
			}else{				
				jsonToReturn.put("actual", "");
				jsonToReturn.put("target", "");
				if(children != null && !children.isEmpty()){
					jsonToReturn.put("iconCls", "folder");
					jsonToReturn.put("cls", "node-folder");
				}else{
					jsonToReturn.put("iconCls","");
				}

			}
			String color = detectColor(kpivalue);
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

				ExecutionInstance docExecInst = ExecutionInstance.getExecutionInstanceByLabel(kpiInstance, docLabel, kpiInstanceLocale);
				String executionUrl = docExecInst.getExecutionUrl(kpiInstanceLocale);
				jsonToReturn.putOpt("documentExecUrl", executionUrl);
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
		if(value == null || value.getValue() == null){
			return ret;
		}

		ThresholdValue thrVal = value.getThresholdOfValue();
		if(thrVal != null ){
			if(thrVal.getColourString() != null){
				return thrVal.getColourString();
			}
			
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
			if(kpiInst != null){
				Integer kpiInstId = kpiInst.getKpiInstanceId();
				toReturn = kpiInstId;
				Integer trend = DAOFactory.getKpiDAO().getKpiTrend(null, kpiInstId, value.getBeginDate());
				row.putOpt("trend", trend);
				
			}
			
		} catch (Exception e) {
			logger.error("Error retrieving modelinstance "+modelInstId, e);
		}
		return toReturn;
	}
}
