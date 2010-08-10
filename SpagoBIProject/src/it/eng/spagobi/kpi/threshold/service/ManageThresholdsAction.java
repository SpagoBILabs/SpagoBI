/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.kpi.threshold.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.kpi.threshold.dao.IThresholdDAO;
import it.eng.spagobi.kpi.threshold.dao.IThresholdValueDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class ManageThresholdsAction extends AbstractSpagoBIAction{
		// logger component
	private static Logger logger = Logger.getLogger(ManageThresholdsAction.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String THRESHOLDS_LIST = "THRESHOLDS_LIST";
	private final String THRESHOLD_INSERT = "THRESHOLD_INSERT";
	private final String THRESHOLD_DELETE = "THRESHOLD_DELETE";
	
	private final String THRESHOLD_DOMAIN_TYPE = "THRESHOLD_TYPE";
	private final String THRESHOLD_SEVERITY_TYPE = "SEVERITY";
	
	// RES detail
	private final String ID = "id";
	private final String NAME = "name";
	private final String CODE = "code";
	private final String DESCRIPTION = "description";
	private final String NODE_TYPE_CODE = "typeCd";	
	private static final String THRESHOLD_VALUES = "thrValues";
	
	private static final String THR_VAL_ID = "itThrVal";
	private static final String THR_VAL_LABEL = "label";
	private static final String THR_VAL_POSITION = "position";
	private static final String THR_VAL_MIN = "min";
	private static final String THR_VAL_MIN_INCLUDED = "minIncluded";
	private static final String THR_VAL_MAX = "max";
	private static final String THR_VAL_MAX_INCLUDED = "maxIncluded";
	private static final String THR_VAL_VALUE = "val";
	private static final String THR_VAL_COLOR = "color";
	private static final String THR_VAL_SEVERITY_CD = "severityCd";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 16;
	
	@Override
	public void doService() {
		logger.debug("IN");
		IThresholdDAO thrDao;
		IThresholdValueDAO tDao;
		try {
			thrDao = DAOFactory.getThresholdDAO();
			tDao = DAOFactory.getThresholdValueDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();
	
		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(THRESHOLDS_LIST)) {
			
			try {		
				
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}
	
				Integer totalItemsNum = thrDao.countThresholds();
				List thresholds = thrDao.loadPagedThresholdList(start,limit);
				logger.debug("Loaded thresholds list");
				JSONArray resourcesJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(thresholds, locale);
				JSONObject resourcesResponseJSON = createJSONResponseResources(resourcesJSON, totalItemsNum);
	
				writeBackToClient(new JSONSuccess(resourcesResponseJSON));
	
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving thresholds", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving thresholds", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(THRESHOLD_INSERT)) {
			String id = getAttributeAsString(ID);
			String code = getAttributeAsString(CODE);
			String name = getAttributeAsString(NAME);
			String description = getAttributeAsString(DESCRIPTION);
			String typeCD = getAttributeAsString(NODE_TYPE_CODE);		
			JSONArray thrValuesJSON = getAttributeAsJSONArray(THRESHOLD_VALUES);
			
			String thrValId = getAttributeAsString(THR_VAL_ID);
			String label = getAttributeAsString(THR_VAL_LABEL);
			Integer position = getAttributeAsInteger(THR_VAL_POSITION);
			String colourString = getAttributeAsString(THR_VAL_COLOR);
			String valueS = getAttributeAsString(THR_VAL_VALUE);
			Double value = null;
			if(valueS!=null && valueS != ""){
				value = new Double(valueS);
			}
			String severityCd = getAttributeAsString(THR_VAL_SEVERITY_CD);
			Boolean minClosed = getAttributeAsBoolean(THR_VAL_MIN_INCLUDED);
			String minValueS = getAttributeAsString(THR_VAL_MIN);
			Double minValue = null;
			if(minValueS!=null && minValueS!=""){
				minValue = new Double(minValueS);
			}
			Boolean maxClosed = getAttributeAsBoolean(THR_VAL_MAX_INCLUDED);
			String maxValueS = getAttributeAsString(THR_VAL_MAX);
			Double maxValue = null;
			if(maxValueS!=null && maxValueS!=""){
				maxValue = new Double(maxValueS);
			}
			
			List<Domain> domains = (List<Domain>)getSessionContainer().getAttribute("nodeTypesList");
			List<Domain> domainsthrValues = (List<Domain>)getSessionContainer().getAttribute("thrSeverityTypes");
			domains.addAll(domainsthrValues);
			
		    HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    if(domains != null){
			    for(int i=0; i< domains.size(); i++){
			    	domainIds.put(domains.get(i).getValueCd(), domains.get(i).getValueId());
			    }
		    }
		    
		    Integer typeID = domainIds.get(typeCD);
		    if(typeID == null){
		    	logger.error("Threshold type CD does not exist");
		    	throw new SpagoBIServiceException(SERVICE_NAME,	"Threshold Type ID is undefined");
		    }
	
			if (name != null && typeID != null && code != null) {
				Threshold thr = new Threshold();
				thr.setName(name);
				thr.setThresholdTypeCode(typeCD);
				thr.setThresholdTypeId(typeID);
				thr.setCode(code);
				
				
				if(description != null){
					thr.setDescription(description);
				}	
				
				List thrValuesList = new ArrayList();;
				if(typeCD !=null){
					if(typeCD.equals("MINIMUM") || typeCD.equals("MAXIMUM")){
						ThresholdValue tVal = new ThresholdValue();
						if(thrValId!= null && !thrValId.equals("") && !thrValId.equals("0")){
							tVal.setId(Integer.valueOf(thrValId));
						}
						tVal.setLabel(label);						
						tVal.setPosition(position);
						tVal.setColourString(colourString);
						tVal.setValue(value);
						tVal.setSeverityCd(severityCd);
						Integer severityId = domainIds.get(severityCd);				   
						tVal.setSeverityId(severityId);		
						
						if(typeCD.equals("MINIMUM")){
							tVal.setMinClosed(minClosed);
							tVal.setMinValue(minValue);
						}else if(typeCD.equals("MAXIMUM")){
							tVal.setMaxClosed(maxClosed);
							tVal.setMaxValue(maxValue);
						}	
						thrValuesList.add(tVal);
						thr.setThresholdValues(thrValuesList);
						
					}else if(typeCD.equals("RANGE")){
						
					}
				}
					
				
				try {
					
					/*if(thrValuesJSON != null){
						//thrValuesList = deserializeThrValuesJSONArray(thrValuesJSON);
					}*/
					Integer idToReturnToClient = null;
					
					if(id != null && !id.equals("") && !id.equals("0")){	
						//modify
						thr.setId(Integer.valueOf(id));
						thrDao.modifyThreshold(thr);
						idToReturnToClient = Integer.valueOf(id);						
					}else{
						//insert new
						idToReturnToClient = thrDao.insertThreshold(thr);		
					}
					
					List thrValueIds = new ArrayList();
					if(thrValuesList!=null && !thrValuesList.isEmpty()){							
						Iterator it = thrValuesList.iterator();
						while(it.hasNext()){
							ThresholdValue tVal = (ThresholdValue)it.next();
							tVal.setThresholdId(Integer.valueOf(idToReturnToClient));							
							//insert or update all threshold values
							Integer thrValueId = tDao.saveOrUpdateThresholdValue(tVal);
							thrValueIds.add(thrValueId);
						}				
					}
					
					logger.debug("Threshold inserted or updated");
					JSONObject attributesResponseSuccessJSON = new JSONObject();
					attributesResponseSuccessJSON.put("success", true);
					attributesResponseSuccessJSON.put("responseText", "Operation succeded");
					attributesResponseSuccessJSON.put("id", idToReturnToClient);
					if(thrValueIds!=null && !thrValueIds.isEmpty()){
						if(thrValueIds.size()==1){
							attributesResponseSuccessJSON.put("idThrVal", thrValueIds.get(0));
						}else{
							//attacco l'array di id
						}
					}
					writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
	
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception occurred while saving new threshold", e);
				}
								
			}else{
				logger.error("Resource name, code or type are missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please fill threshold name, code and type");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(THRESHOLD_DELETE)) {
			Integer id = getAttributeAsInteger(ID);
			try {
				thrDao.deleteThreshold(id);
				logger.debug("Resource deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving resource to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving resource to delete", e);
			}
		}else if(serviceType == null){
			try {
				List nodeTypes = DAOFactory.getDomainDAO().loadListDomainsByType(THRESHOLD_DOMAIN_TYPE);
				getSessionContainer().setAttribute("nodeTypesList", nodeTypes);
				List thrSeverityTypes = DAOFactory.getDomainDAO().loadListDomainsByType(THRESHOLD_SEVERITY_TYPE);
				getSessionContainer().setAttribute("thrSeverityTypes", thrSeverityTypes);
				
			} catch (EMFUserError e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception retrieving resources types", e);
			}
		}
		logger.debug("OUT");
	}
	
	/**
	 * Creates a json array with children users informations
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseResources(JSONArray rows, Integer totalResNumber)
			throws JSONException {
		JSONObject results;
	
		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "Thresholds");
		results.put("rows", rows);
		return results;
	}
}
