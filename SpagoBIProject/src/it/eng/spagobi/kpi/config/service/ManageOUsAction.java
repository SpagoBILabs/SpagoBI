package it.eng.spagobi.kpi.config.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializationException;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.model.service.ManageModelInstancesAction;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrant;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrantNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNodeWithGrant;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageOUsAction extends AbstractSpagoBIAction {

	private static final long serialVersionUID = -8920524215721282986L;
	// logger component
	private static Logger logger = Logger.getLogger(ManageModelInstancesAction.class);

	private final String MESSAGE_DET = "MESSAGE_DET";
	
	private final String GRANT_LIST = "GRANT_LIST";
	private final String OU_LIST = "OU_LIST";
	private final String OU_CHILDS_LIST = "OU_CHILDS_LIST";
	private final String OU_HIERARCHY_ROOT = "OU_HIERARCHY_ROOT";
	private final String OU_GRANT_ERESE = "OU_GRANT_ERESE";
	private final String OU_GRANT_INSERT = "OU_GRANT_INSERT";
	
	private final String GRANT = "grant";
	private final String GRANTNODES = "grantnodes";
	

	@Override
	public void doService() {
		logger.debug("IN");

		try {
			String serviceType = this.getAttributeAsString(MESSAGE_DET);
			
			if (serviceType != null && serviceType.equalsIgnoreCase(GRANT_LIST)) {
				getGrantsList();
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_LIST)) {
				getHierarchiesList();
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_CHILDS_LIST)) {
				Integer grantId;
				Integer nodeId =  getAttributeAsInteger("nodeId");
				try{
					grantId =  getAttributeAsInteger("grantId");
				}catch(Exception e){
					grantId = null;
				}
				getOUChildrenNodes(nodeId, grantId);
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_HIERARCHY_ROOT)) {
				Integer hierarchyId = getAttributeAsInteger("hierarchyId");
				Integer grantId;
				try{
					grantId =  getAttributeAsInteger("grantId");
				}catch(Exception e){
					grantId = null;
				}
				getHierarchyRootNode(hierarchyId, grantId);
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_GRANT_ERESE)) {
				Integer grantId = getAttributeAsInteger("grantId");
				eraseGrant(grantId);
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_GRANT_INSERT)) {
				JSONArray grantNodesJSON = getAttributeAsJSONArray(GRANTNODES);
				JSONObject grantJSON = getAttributeAsJSONObject(GRANT);
				insertGrant(grantJSON, grantNodesJSON);
				
			}else if(serviceType == null){
				logger.debug("no service");
			}
			
		} finally {
			logger.debug("OUT");
		}

	}
	
	private void getGrantsList(){
		List<OrganizationalUnitGrant> grants = DAOFactory.getOrganizationalUnitDAO().getGrantsList();
	
		try {
			JSONArray grantsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize( grants, null);
			JSONObject grantsJSONObject = new JSONObject();
			grantsJSONObject.put("rows", grantsJSON);
			writeBackToClient( new JSONSuccess( grantsJSONObject ) );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		}
	}
	
	private void getHierarchiesList(){
		List<OrganizationalUnitHierarchy> ous = DAOFactory.getOrganizationalUnitDAO().getHierarchiesList();
	
		try {
			JSONArray grantsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize( ous, null);
			writeBackToClient( new JSONSuccess( grantsJSON ) );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (SerializationException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		}
	}
	
	private void getOUChildrenNodes(Integer nodeId, Integer grantId){
		List<OrganizationalUnitNodeWithGrant> ousWithGrants = null;
		if(grantId==null){
			List<OrganizationalUnitNode> ous = DAOFactory.getOrganizationalUnitDAO().getChildrenNodes(nodeId);
			ousWithGrants = new ArrayList<OrganizationalUnitNodeWithGrant>();
			for(int i=0; i<ous.size(); i++){
				ousWithGrants.add(new OrganizationalUnitNodeWithGrant(ous.get(i), new ArrayList<OrganizationalUnitGrantNode>()));
			}
		}else{
			ousWithGrants = DAOFactory.getOrganizationalUnitDAO().getChildrenNodesWithGrants(nodeId, grantId);
		} try {
			JSONArray grantsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize( ousWithGrants, null);
			writeBackToClient( new JSONSuccess( grantsJSON ) );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (SerializationException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		}
	}
	
	private void getHierarchyRootNode(Integer hierarchyId, Integer grantId){
		OrganizationalUnitNodeWithGrant ouWithGrant;
		
			if(grantId==null){
				OrganizationalUnitNode ou = DAOFactory.getOrganizationalUnitDAO().getRootNode(hierarchyId);
				ouWithGrant = new OrganizationalUnitNodeWithGrant(ou, new ArrayList<OrganizationalUnitGrantNode>());

			}else{
				ouWithGrant = DAOFactory.getOrganizationalUnitDAO().getRootNodeWithGrants(hierarchyId, grantId);

			}
		try {
			JSONObject grantsJSON = ((JSONObject) SerializerFactory.getSerializer("application/json").serialize( ouWithGrant, null));
			writeBackToClient( new JSONSuccess( grantsJSON ) );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (SerializationException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		} 
	}
	
	private void eraseGrant(Integer grantId){
		DAOFactory.getOrganizationalUnitDAO().eraseGrant(grantId);
		try {
			writeBackToClient( new JSONAcknowledge() );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		}
	}
	
	private void insertGrant(JSONObject grantJSON,JSONArray grantNodesJSON){
		try {
			OrganizationalUnitGrant grant = deserializeOrganizationalUnitGrant(grantJSON);
			if(grant.getId()!=null){
				DAOFactory.getOrganizationalUnitDAO().eraseNodeGrants(grant.getId());
				DAOFactory.getOrganizationalUnitDAO().modifyGrant(grant);
			}else{
				DAOFactory.getOrganizationalUnitDAO().insertGrant(grant);
			}

			List<OrganizationalUnitGrantNode> grantNodes = deserializeOrganizationalUnitGrantNodes(grantNodesJSON, grant);
			DAOFactory.getOrganizationalUnitDAO().insertNodeGrants(grantNodes);
			writeBackToClient( new JSONAcknowledge() );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		} 
	}
	
	
	private OrganizationalUnitGrant deserializeOrganizationalUnitGrant(JSONObject JSONGrant) throws Exception{
		OrganizationalUnitGrant organizationalUnitGrant = new OrganizationalUnitGrant();
		organizationalUnitGrant.setDescription(JSONGrant.getString("description"));
		
        ConfigSingleton config = ConfigSingleton.getInstance();
        SourceBean formatSB = (SourceBean) config.getAttribute("SPAGOBI.DATE-FORMAT-SERVER");
	    String format = (String) formatSB.getAttribute("format");
	    
		organizationalUnitGrant.setEndDate(toDate(JSONGrant.getString("enddate"), format));
		organizationalUnitGrant.setStartDate(toDate(JSONGrant.getString("startdate"), format));
		organizationalUnitGrant.setLabel(JSONGrant.getString("label"));
		organizationalUnitGrant.setName(JSONGrant.getString("name"));
		try{
			organizationalUnitGrant.setId(JSONGrant.getInt("id"));
		}catch(JSONException e){}
		int hierarchyId = JSONGrant.getInt("hierarchy");
		int modelInstanceId = JSONGrant.getInt("modelinstance");
		ModelInstance modelInstance = DAOFactory.getModelInstanceDAO().loadModelInstanceWithChildrenById(modelInstanceId);
		OrganizationalUnitHierarchy organizationalUnitHierarchy = DAOFactory.getOrganizationalUnitDAO().getHierarchy(hierarchyId);
		organizationalUnitGrant.setModelInstance(modelInstance);
		organizationalUnitGrant.setHierarchy(organizationalUnitHierarchy);
		
		return organizationalUnitGrant;
	}
	
	
	private List<OrganizationalUnitGrantNode> deserializeOrganizationalUnitGrantNodes(JSONArray JSONGrantNodes, OrganizationalUnitGrant grant) throws Exception{
		List<OrganizationalUnitGrantNode> nodes = new ArrayList<OrganizationalUnitGrantNode>();
		for(int i=0; i<JSONGrantNodes.length(); i++){
			nodes.add(deserializeOrganizationalUnitGrantNode( JSONGrantNodes.getJSONObject(i), grant));
		}
		
		return nodes;
	}
	
	private OrganizationalUnitGrantNode deserializeOrganizationalUnitGrantNode(JSONObject JSONGrantNode, OrganizationalUnitGrant grant) throws Exception{
		OrganizationalUnitGrantNode node = new OrganizationalUnitGrantNode();
		int hierarchyId = JSONGrantNode.getInt("hierarchyId");
		int modelInstanceId = JSONGrantNode.getInt("modelinstance");
		String ouPath = JSONGrantNode.getString("ouPath");
		ModelInstanceNode modelInstanceNode = DAOFactory.getModelInstanceDAO().loadModelInstanceById(modelInstanceId, null);
		OrganizationalUnitNode ouNode = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitNode(ouPath, hierarchyId);
		node.setGrant(grant);
		node.setModelInstanceNode(modelInstanceNode);
		node.setOuNode(ouNode);
		return node;
	}
	
	public Date toDate(String dateStr, String format) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat();


		Date date = null;
		try {
			dateFormat.applyPattern("yyyy-MM-dd");
			dateFormat.setLenient(false);
			date = dateFormat.parse(dateStr);
		} catch (Exception e) { 
			throw e;
		}
		return date;
	}

}
