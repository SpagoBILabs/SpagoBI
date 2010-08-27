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
package it.eng.spagobi.kpi.model.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.analiticalmodel.document.x.SaveMetadataAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResources;
import it.eng.spagobi.kpi.model.bo.ModelResourcesExtended;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.model.dao.IModelDAO;
import it.eng.spagobi.kpi.model.dao.IModelInstanceDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.regexp.internal.RESyntaxException;

public class ManageModelInstancesAction extends AbstractSpagoBIAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8920524215721282986L;
	// logger component
	private static Logger logger = Logger.getLogger(ManageModelInstancesAction.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String MODELINSTS_LIST = "MODELINSTS_LIST";
	private final String MODELINST_RESOURCE_LIST = "MODELINST_RESOURCE_LIST";
	private final String MODELINSTS_NODES_LIST = "MODELINSTS_NODES_LIST";
	private final String MODELINSTS_NODES_SAVE = "MODELINSTS_NODES_SAVE";
	private final String MODELINSTS_NODE_DELETE = "MODELINSTS_NODE_DELETE";

	
	private final String MODEL_DOMAIN_TYPE_ROOT = "MODEL_ROOT";
	private final String MODEL_DOMAIN_TYPE_NODE = "MODEL_NODE";
	
	private final String THRESHOLD_DOMAIN_TYPE = "THRESHOLD_TYPE";
	private final String KPI_CHART_TYPE = "KPI_CHART";
	
	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 16;
	
	private final String NODES_TO_SAVE = "nodes";



	@Override
	public void doService() {
		logger.debug("IN");
		IModelInstanceDAO modelDao;
		try {
			modelDao = DAOFactory.getModelInstanceDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME,	"Error occurred");
		}
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		
		if (serviceType != null && serviceType.equalsIgnoreCase(MODELINSTS_LIST)) {
			
			try {				
				List modelRootsList = modelDao.loadModelsInstanceRoot();
				
				logger.debug("Loaded models list");
				JSONArray modelsListJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(modelRootsList,locale);
				JSONObject modelsResponseJSON = createJSONResponseModelsList(modelsListJSON,modelRootsList.size());

				writeBackToClient(new JSONSuccess(modelsResponseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving model tree", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving model tree", e);
			}
		  }else if (serviceType != null && serviceType.equalsIgnoreCase(MODELINSTS_NODES_LIST)) {
			
			try {	
				
				String parentId = (String)getAttributeAsString("modelInstId");
				if(parentId == null || parentId.startsWith("xnode")){
					writeBackToClient(new JSONSuccess("OK"));
					return;
				}
				ModelInstance aModel = modelDao.loadModelInstanceWithChildrenById(Integer.parseInt(parentId));
				
				logger.debug("Loaded model tree");
				JSONArray modelChildrenJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(aModel.getChildrenNodes(),	locale);
				writeBackToClient(new JSONSuccess(modelChildrenJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving model tree", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving model tree", e);
			}
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(MODELINSTS_NODES_SAVE)) {
			JSONArray nodesToSaveJSON = getAttributeAsJSONArray(NODES_TO_SAVE);
			List<ModelInstance> modelNodes = null;
			if(nodesToSaveJSON != null){
				try {
					modelNodes = deserializeNodesJSONArray(nodesToSaveJSON);
					
					//save them
					JSONObject response = saveModelNodeInstances(modelNodes);
					writeBackToClient(new JSONSuccess(response));
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					writeErrorsBackToClient();
					throw new SpagoBIServiceException(SERVICE_NAME,
							"Exception saving model instance nodes", e);
				}
			}
			
		} else if (serviceType != null	&& serviceType.equalsIgnoreCase(MODELINSTS_NODE_DELETE)) {
			
			Integer modelInstId = getAttributeAsInteger("modelInstId");
			try {
				boolean result = DAOFactory.getModelInstanceDAO().deleteModelInstance(modelInstId);
				logger.debug("Model instance node deleted");
				writeBackToClient( new JSONSuccess("Operation succeded") );
			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving model instance to delete", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving model instance to delete", e);
			}
			
			
		}else if (serviceType != null	&& serviceType.equalsIgnoreCase(MODELINST_RESOURCE_LIST)) {
			
			Integer modelInstId = getAttributeAsInteger("modelInstId");
			try {
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}
				List<ModelResourcesExtended> modelResourcesExtenList = new ArrayList<ModelResourcesExtended>();
				//extract resources
				List<ModelResources> modelResources = DAOFactory.getModelResourcesDAO().loadModelResourceByModelId(modelInstId);
				
				HashMap<Integer, ModelResources> modResourcesIds = new HashMap<Integer, ModelResources>();
				if(modelResources != null){
					for(int i =0;i<modelResources.size(); i++){
						ModelResources mr = modelResources.get(i);
						modResourcesIds.put(mr.getResourceId(), mr);
					}
				}
				//extract all resources
				Vector resourcesIds = new Vector<Integer>();

				List<Resource> allResources = (List<Resource>)getSessionContainer().getAttribute("ALL_RESOURCES_LIST");
				
				//if null than extract
				if(allResources == null){
					allResources = DAOFactory.getResourceDAO().loadPagedResourcesList(start,limit);
				}
				modelResourcesExtendedListCreate(modelResourcesExtenList, allResources, modResourcesIds);
				
				logger.debug("Loaded model resources");
				JSONArray modelsResourcesJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(modelResourcesExtenList,locale);
				JSONObject modelsResourcesResponseJSON = createJSONResponsemodelsResourcesList(modelsResourcesJSON, modelResourcesExtenList.size());

				writeBackToClient(new JSONSuccess(modelsResourcesResponseJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving model tree", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving model tree", e);
			}
			
		}else if(serviceType == null){
			try {

				List thrTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(THRESHOLD_DOMAIN_TYPE);
				getSessionContainer().setAttribute("thrTypesList", thrTypesList);
				Integer start = getAttributeAsInteger( START );
				Integer limit = getAttributeAsInteger( LIMIT );
				
				if(start==null){
					start = START_DEFAULT;
				}
				if(limit==null){
					limit = LIMIT_DEFAULT;
				}
				List<Resource> allResources = DAOFactory.getResourceDAO().loadPagedResourcesList(start,limit);
				getSessionContainer().setAttribute("ALL_RESOURCES_LIST", allResources);
				//Chart Types
				List kpiChartTypesList = DAOFactory.getDomainDAO().loadListDomainsByType(KPI_CHART_TYPE);
				getSessionContainer().setAttribute("kpiChartTypesList", kpiChartTypesList);
				
				//Periodicity
				List kpiPeriodicityList = DAOFactory.getPeriodicityDAO().loadPeriodicityList();
				getSessionContainer().setAttribute("kpiPeriodicityList", kpiPeriodicityList);
								
			} catch (EMFUserError e) {
				logger.error(e.getMessage(), e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception retrieving resources types", e);
			}
		}
		logger.debug("OUT");

	}
	
	private void modelResourcesExtendedListCreate(List<ModelResourcesExtended> modelResourcesExtenList,
													List<Resource> allResources,
													HashMap<Integer, ModelResources> modResourcesIds ){
		if(allResources != null){
			for(int i =0;i<allResources.size(); i++){
				Resource res = allResources.get(i);
				if(!modResourcesIds.keySet().contains(res.getId())){
					ModelResourcesExtended extendedRes = new ModelResourcesExtended(res, new ModelResources());
					modelResourcesExtenList.add(extendedRes);
				}else{
					ModelResourcesExtended extendedRes = new ModelResourcesExtended(res, modResourcesIds.get(res.getId()));
					modelResourcesExtenList.add(extendedRes);
				}
			}
		}
	}

	/**
	 * Creates a json array with children models informations
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponseModelsList(JSONArray rows, Integer totalModelsNumber)throws JSONException {
		JSONObject results;
		results = new JSONObject();
		results.put("total", totalModelsNumber);
		results.put("title", "ModelsList");
		results.put("rows", rows);
		return results;
	}
	
	/**
	 * Creates a json array with children resources
	 * 
	 * @param rows
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createJSONResponsemodelsResourcesList(JSONArray rows, Integer totalModelsNumber)throws JSONException {
		JSONObject results;
		results = new JSONObject();
		results.put("total", totalModelsNumber);
		results.put("title", "ResourcesList");
		results.put("rows", rows);
		return results;
	}
	private List<ModelInstance> deserializeNodesJSONArray(JSONArray rows) throws JSONException{
		List<ModelInstance> toReturn = new ArrayList<ModelInstance>();
		for(int i=0; i< rows.length(); i++){
			
			JSONObject obj = (JSONObject)rows.get(i);

			ModelInstance modelInst = new ModelInstance();
			//always present guiId
			String guiId = obj.getString("id");
			modelInst.setGuiId(guiId);

			try{
				modelInst.setId(obj.getInt("modelInstId"));
			}catch(Throwable t){
				//nothing
				modelInst.setId(null);
			}
			
			try{
				modelInst.setParentId(obj.getInt("parentId"));
			}catch(Throwable t){
				//nothing
				modelInst.setParentId(null);
			}
			try{
				
				modelInst.setDescription(obj.getString("description"));
				modelInst.setLabel(obj.getString("label"));
				modelInst.setName(obj.getString("name"));
				Integer modelId = obj.getInt("modelId");
				try{
					Model model = DAOFactory.getModelDAO().loadModelWithoutChildrenById(modelId);
					modelInst.setModel(model);
				}catch(Throwable t){
					//nothing
					logger.error("no model!");
					modelInst.setModel(null);
				}
				try{
					KpiInstance kpiInst = DAOFactory.getKpiInstanceDAO().loadKpiInstanceById(obj.getInt("kpiInstId"));
					modelInst.setKpiInstance(kpiInst);
				}catch(Throwable t){
					//nothing
					modelInst.setKpiInstance(null);
				}
				String value = obj.getString("toSave");
			}catch(Throwable t){
				logger.debug("Deserialization error on node: "+guiId);
			}
			toReturn.add(modelInst);
		}	
		return toReturn;
	}
	
	private JSONObject saveModelNodeInstances(List<ModelInstance> nodesToSave) throws JSONException{
		JSONArray errorNodes = new JSONArray();
		
		JSONObject respObj = new JSONObject();
		
		//loop over nodes and order them ascending
		TreeMap<Integer, ModelInstance> treeMap = new TreeMap<Integer, ModelInstance>();
		for(int i= 0; i<nodesToSave.size(); i++){
			
			ModelInstance modelInstance = (ModelInstance)nodesToSave.get(i);
			//loads all nodes guiid with type error
			
			respObj.put(modelInstance.getGuiId(), "OK");
			
			if(modelInstance.getParentId() != null){
				//look up for its id: if null --> newly created node
				Integer id = modelInstance.getId();
				if(id == null){
					treeMap.put(Integer.valueOf("-"+i+1), modelInstance);
				}else{
				//else to modify node
					treeMap.put(modelInstance.getId(), modelInstance);
				}
				
			}else{
				//root node --> save first
				try {
					if(modelInstance.getId()  != null){
						DAOFactory.getModelInstanceDAO().modifyModelInstance(modelInstance);
						respObj.put(modelInstance.getGuiId(), modelInstance.getId());
					}else{
						Integer index = DAOFactory.getModelInstanceDAO().insertModelInstance(modelInstance);
						respObj.put(modelInstance.getGuiId(), index);
					}
				} catch (Exception e) {
					//send error!!!		
					respObj.put(modelInstance.getGuiId(), "KO");
					
				}
			}
		}
		
		Set set = treeMap.entrySet();
		// Get an iterator
		Iterator it = set.iterator(); 
		//loop again over treemap
		while(it.hasNext()) {
			Map.Entry orderedEntry = (Map.Entry)it.next();
			//check that parent exists
			Model orderedNode = (Model)orderedEntry.getValue();
			
			//GET JSON OBJECT VALUE
			Integer parentId = orderedNode.getParentId();
			try {
				Model parent = DAOFactory.getModelDAO().loadModelWithoutChildrenById(parentId);
				if(parent != null){						
					//if parent exists--> save					
					//if node id is negative --> insert
					if(orderedNode.getId() == null){
						Integer newId = DAOFactory.getModelDAO().insertModel(orderedNode);
						if (newId != null){
							orderedNode.setId(newId);
							respObj.put(orderedNode.getGuiId(), newId);
						}else{						
							respObj.put(orderedNode.getGuiId(), "KO");
						}
					}else{
					//else update
						DAOFactory.getModelDAO().modifyModel(orderedNode);
						respObj.put(orderedNode.getGuiId(), orderedNode.getId());
					}
					
				}
			} catch (Exception e) {
				//if parentId != null but no parent node stored on db --> exception
				respObj.put(orderedNode.getGuiId(), "KO");
			}

		} 
		return respObj;
	}
}
