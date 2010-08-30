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
package it.eng.spagobi.kpi.config.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.dao.IKpiDAO;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.dao.IThresholdDAO;
import it.eng.spagobi.kpi.threshold.service.ManageThresholdsAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageKpisAction extends AbstractSpagoBIAction {
	// logger component
	private static Logger logger = Logger.getLogger(ManageThresholdsAction.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String KPIS_LIST = "KPIS_LIST";
	private final String KPI_INSERT = "KPI_INSERT";
	private final String KPI_DELETE = "KPI_DELETE";
	
	private final String KPI_DOMAIN_TYPE = "KPI_TYPE";
	private final String THRESHOLD_SEVERITY_TYPE = "SEVERITY";
	private final String METRIC_SCALE_DOMAIN_TYPE = "METRIC_SCALE_TYPE";
	private final String MEASURE_DOMAIN_TYPE = "MEASURE_TYPE";
	private final String THRESHOLD_DOMAIN_TYPE = "THRESHOLD_TYPE";
	
	// RES detail
	private final String ID = "id";
	private final String NAME = "name";
	private final String CODE = "code";
	private final String DESCRIPTION = "description";
	private final String WEIGHT = "weight";
	private final String DATASET = "dataset";
	private final String THR = "threshold";
	private final String DOCS = "documents";
	private final String INTERPRETATION = "interpretation";
	private final String ALGDESC = "algdesc";
	private final String INPUT_ATTR = "inputAttr";
	private final String MODEL_REFERENCE = "modelReference";
	private final String TARGET_AUDIENCE = "targetAudience";
	
	private final String KPI_TYPE_ID = "kpiTypeId";
	private final String KPI_TYPE_CD = "kpiTypeCd";
	private final String METRIC_SCALE_TYPE_ID = "metricScaleId";
	private final String METRIC_SCALE_TYPE_CD = "metricScaleCd";
	private final String MEASURE_TYPE_ID = "measureTypeId";
	private final String MEASURE_TYPE_CD = "measureTypeCd";
	
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 16;
	
	@Override
	public void doService() {
		logger.debug("IN");
		IKpiDAO kpiDao;
		IDataSetDAO dsDao;
		IThresholdDAO thrDao;
		try {
			kpiDao = DAOFactory.getKpiDAO();
			dsDao = DAOFactory.getDataSetDAO();
			thrDao = DAOFactory.getThresholdDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();
	
		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(KPIS_LIST)) {
			
			try {		
				
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}
	
				Integer totalItemsNum = kpiDao.countKpis();
				List kpis = kpiDao.loadPagedKpiList(start,limit);
				logger.debug("Loaded thresholds list");
				JSONArray resourcesJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(kpis, locale);
				JSONObject resourcesResponseJSON = createJSONResponseResources(resourcesJSON, totalItemsNum);
	
				writeBackToClient(new JSONSuccess(resourcesResponseJSON));
	
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving thresholds", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving thresholds", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(KPI_INSERT)) {
			
			String id = getAttributeAsString(ID);
			String code = getAttributeAsString(CODE);
			String name = getAttributeAsString(NAME);
			String description = getAttributeAsString(DESCRIPTION);
			String weight = getAttributeAsString(WEIGHT);
			String dsLabel = getAttributeAsString(DATASET);
			String thresholdCode = getAttributeAsString(THR);
			String documentLabels = getAttributeAsString(DOCS);
			String interpretation = getAttributeAsString(INTERPRETATION);
			String algdesc = getAttributeAsString(ALGDESC);
			String inputAttr = getAttributeAsString(INPUT_ATTR);
			String modelReference = getAttributeAsString(MODEL_REFERENCE);
			String targetAudience = getAttributeAsString(TARGET_AUDIENCE);
			
			String kpiTypeCd = getAttributeAsString(KPI_TYPE_CD);	
			String metricScaleCd = getAttributeAsString(METRIC_SCALE_TYPE_CD);
			String measureTypeCd = getAttributeAsString(MEASURE_TYPE_CD);				
			
			List<Domain> domains = (List<Domain>)getSessionContainer().getAttribute("kpiTypesList");
			List<Domain> domains1 = (List<Domain>)getSessionContainer().getAttribute("measureTypesList");
			List<Domain> domains2 = (List<Domain>)getSessionContainer().getAttribute("metricScaleTypesList");
			domains.addAll(domains1);
			domains.addAll(domains2);
			
		    HashMap<String, Integer> domainIds = new HashMap<String, Integer> ();
		    if(domains != null){
			    for(int i=0; i< domains.size(); i++){
			    	domainIds.put(domains.get(i).getValueCd(), domains.get(i).getValueId());
			    }
		    }
		    
		    Integer kpiTypeId = domainIds.get(kpiTypeCd);
		    Integer metricScaleId = domainIds.get(metricScaleCd);
		    Integer measureTypeId = domainIds.get(measureTypeCd);
	
			if (name != null && code != null) {
				Kpi k = new Kpi();
				
				try {
					
				k.setKpiName(name);
				k.setCode(code);
				
				if(description != null){
					k.setDescription(description);
				}
				if(weight != null && !weight.equalsIgnoreCase("")){
					k.setStandardWeight(Double.valueOf(weight));
				}	
				if(dsLabel != null){
					k.setDsLabel(dsLabel);
					IDataSet ds = dsDao.loadDataSetByLabel(dsLabel);
					
					if(ds!=null){
						int dsId = ds.getId();
						k.setKpiDsId(new Integer(dsId));
					}				
				}
				if(thresholdCode != null){
					Threshold t = thrDao.loadThresholdByCode(thresholdCode);
					k.setThreshold(t);
				}
				if(documentLabels != null){
					k.setDocumentLabel(documentLabels);
				}
				if(interpretation != null){
					k.setInterpretation(interpretation);
				}
				if(algdesc != null){
					k.setMetric(algdesc);
				}
				if(inputAttr != null){
					k.setInputAttribute(inputAttr);
				}
				if(modelReference != null){
					k.setModelReference(modelReference);
				}
				if(targetAudience != null){
					k.setTargetAudience(targetAudience);
				}
				if(kpiTypeCd != null){
					k.setKpiTypeCd(kpiTypeCd);
					k.setKpiTypeId(kpiTypeId);
				}
				if(metricScaleCd != null){
					k.setMetricScaleCd(metricScaleCd);
					k.setMetricScaleId(metricScaleId);
				}
				if(measureTypeCd != null){
					k.setMeasureTypeCd(measureTypeCd);
					k.setMeasureTypeId(measureTypeId);
				}			

					if(id != null && !id.equals("") && !id.equals("0")){							
						k.setKpiId(Integer.valueOf(id));
						kpiDao.modifyKpi(k);
						logger.debug("threshold "+id+" updated");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						attributesResponseSuccessJSON.put("id", id);
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}else{
						Integer kpiID = kpiDao.insertKpi(k);
						logger.debug("New threshold inserted");
						JSONObject attributesResponseSuccessJSON = new JSONObject();
						attributesResponseSuccessJSON.put("success", true);
						attributesResponseSuccessJSON.put("responseText", "Operation succeded");
						attributesResponseSuccessJSON.put("id", kpiID);
						writeBackToClient( new JSONSuccess(attributesResponseSuccessJSON) );
					}
	
				} catch(EMFUserError e){
					logger.error("EMFUserError");
					e.printStackTrace();
				} catch (JSONException e) {
					logger.error("JSONException");
					e.printStackTrace();
				} catch (IOException e) {
					logger.error("IOException");
					e.printStackTrace();
				}
								
			}else{
				logger.error("Resource name, code or type are missing");
				throw new SpagoBIServiceException(SERVICE_NAME,	"Please fill threshold name, code and type");
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(KPI_DELETE)) {
			Integer id = getAttributeAsInteger(ID);
			try {
				kpiDao.deleteKpi(id);
				logger.debug("Resource deleted");
				writeBackToClient( new JSONAcknowledge("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving resource to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving resource to delete", e);
			}
		}else if(serviceType == null){
			try {
				List kpiTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(KPI_DOMAIN_TYPE);
				getSessionContainer().setAttribute("kpiTypesList", kpiTypesList);
				List thrSeverityTypes = DAOFactory.getDomainDAO().loadListDomainsByType(THRESHOLD_SEVERITY_TYPE);
				getSessionContainer().setAttribute("thrSeverityTypes", thrSeverityTypes);
				List measureTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(MEASURE_DOMAIN_TYPE);
				getSessionContainer().setAttribute("measureTypesList", measureTypesList);
				List metricScaleTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(METRIC_SCALE_DOMAIN_TYPE);
				getSessionContainer().setAttribute("metricScaleTypesList", metricScaleTypesList);
				List thrTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(THRESHOLD_DOMAIN_TYPE);
				getSessionContainer().setAttribute("thrTypesList", thrTypesList);
				
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
		results.put("title", "Kpis");
		results.put("rows", rows);
		return results;
	}

}
