package it.eng.spagobi.engines.kpi.utils;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.kpi.bo.KpiLine;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.KpiValue;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;

import java.net.URLEncoder;
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

				ExecutionInstance docExecInst = ExecutionInstance.getExecutionInstanceByLabel(kpiInstance, docLabel, kpiInstanceLocale);
				String executionUrl = docExecInst.getExecutionUrl(kpiInstanceLocale);
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
