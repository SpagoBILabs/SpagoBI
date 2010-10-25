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
import it.eng.spagobi.utilities.assertion.Assert;
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

	//Service parameter
	private final String MESSAGE_DET = "MESSAGE_DET";
	
	//Service parameter values
	private final String GRANT_LIST = "GRANT_LIST";
	private final String OU_LIST = "OU_LIST";
	private final String OU_CHILDS_LIST = "OU_CHILDS_LIST";
	private final String OU_HIERARCHY_ROOT = "OU_HIERARCHY_ROOT";
	private final String OU_GRANT_ERESE = "OU_GRANT_ERESE";
	private final String OU_GRANT_INSERT = "OU_GRANT_INSERT";
	
	//JSON Objects fields names
	private final String GRANT = "grant";
	private final String GRANTNODES = "grantnodes";
	

	@Override
	public void doService() {
		logger.debug("IN");

		try {
			String serviceType = this.getAttributeAsString(MESSAGE_DET);
			if (serviceType != null && serviceType.equalsIgnoreCase(GRANT_LIST)) {
				logger.debug("Loading the list of grants..");
				getGrantsList();
				logger.debug("List of grant loaded.");
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_LIST)) {
				logger.debug("Loading the list of ous..");
				getHierarchiesList();
				logger.debug("List of ous loaded.");
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_CHILDS_LIST)) {
				Integer grantId;
				Integer nodeId =  getAttributeAsInteger("nodeId");
				
				try{
					grantId =  getAttributeAsInteger("grantId");
					logger.debug("Loading the list of ous childs of the node with id"+nodeId+" and grant "+grantId+"...");
				}catch(Exception e){
					grantId = null;
					logger.debug("Loading the list of ous childs of the node with id"+nodeId+"...");
				}
				getOUChildrenNodes(nodeId, grantId);
				logger.debug("Loaded the list of ous childs of the node with id"+nodeId+".");
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_HIERARCHY_ROOT)) {
				Integer hierarchyId = getAttributeAsInteger("hierarchyId");
				Integer grantId;
				try{
					grantId =  getAttributeAsInteger("grantId");
					logger.debug("Loading the ou root of the hierarchy with id"+hierarchyId+" and grant "+grantId+"...");
				}catch(Exception e){
					grantId = null;
					logger.debug("Loading the ou root of the hierarchy with id"+hierarchyId+"...");
				}
				getHierarchyRootNode(hierarchyId, grantId);
				logger.debug("Loaded the ou root of the hierarchy with id"+hierarchyId+"...");
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_GRANT_ERESE)) {
				Integer grantId = getAttributeAsInteger("grantId");
				logger.debug("Eresing the grant with id "+grantId+"...");
				eraseGrant(grantId);
				logger.debug("Eresed the grant with id "+grantId+"...");
			}else if (serviceType != null && serviceType.equalsIgnoreCase(OU_GRANT_INSERT)) {
				JSONArray grantNodesJSON = getAttributeAsJSONArray(GRANTNODES);
				JSONObject grantJSON = getAttributeAsJSONObject(GRANT);
				logger.debug("Adding the grant "+grantNodesJSON+"..."+grantJSON);
				insertGrant(grantJSON, grantNodesJSON);
				logger.debug("Added the grant.");
			}else if(serviceType == null){
				logger.debug("no service");
				Assert.assertUnreachable("No service defined.");
			}
			
		} finally {
			logger.debug("OUT");
		}

	}
	
	/**
	 * Load the list of grants and serialize them in a JSOMObject. The list live in the attributes with name rows
	 */
	private void getGrantsList(){
		List<OrganizationalUnitGrant> grants = DAOFactory.getOrganizationalUnitDAO().getGrantsList();
	
		try {
			JSONArray grantsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize( grants, null);
			JSONObject grantsJSONObject = new JSONObject();
			grantsJSONObject.put("rows", grantsJSON);
			writeBackToClient( new JSONSuccess( grantsJSONObject ) );
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		}
	}
	
	/**
	 * Load the list of OUHierarchies and serialize them in a JSOMObject. 
	 */
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
	
	/**
	 * Load the children ou node of a passed node 
	 * @param nodeId the id of the parent node
	 * @param grantId the id of the grant because we return OrganizationalUnitNodeWithGrant. If null
	 * 			the grant object in the OrganizationalUnitNodeWithGrant will be null
	 */
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
	
	/**
	 * Load the ou root node of a hierarchy
	 * @param hierarchyId the id of the hierarchy
	 * @param grantId the id of the grant because we return OrganizationalUnitNodeWithGrant. If null
	 * 			the grant object in the OrganizationalUnitNodeWithGrant will be null
	 */
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
	
	/**
	 * Erase a grant
	 * @param grantId the id of the grant to erase
	 */
	private void eraseGrant(Integer grantId){
		DAOFactory.getOrganizationalUnitDAO().eraseGrant(grantId);
		try {
			writeBackToClient( new JSONAcknowledge() );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		}
	}
	
	/**
	 * Give a grant to a grant nodes
	 * @param grantJSON the JSON representation of the grant
	 * @param grantNodesJSON the JSON representation of the nodes 
	 */
	private void insertGrant(JSONObject grantJSON,JSONArray grantNodesJSON){
		try {
			OrganizationalUnitGrant grant = deserializeOrganizationalUnitGrant(grantJSON);
			if(grant.getId()!=null){
				DAOFactory.getOrganizationalUnitDAO().eraseNodeGrants(grant.getId());
				DAOFactory.getOrganizationalUnitDAO().modifyGrant(grant);
			}else{
				DAOFactory.getOrganizationalUnitDAO().insertGrant(grant);
			}

			List<OrganizationalUnitGrantNode> grantNodes = deserializeOrganizationalUnitGrantNodesAndUpdateChilds(grantNodesJSON, grant);
			DAOFactory.getOrganizationalUnitDAO().insertNodeGrants(grantNodes);
			writeBackToClient( new JSONAcknowledge() );
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (Exception e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to serialize the responce to the client", e);
		} 
	}
	
	/**
	 * Deserialize a OrganizationalUnitGrant object
	 * @param JSONGrant the JSON representation of the OrganizationalUnitGrant object
	 * @return the OrganizationalUnitGrant
	 * @throws Exception
	 */
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
	
	/**
	 * Deserialize a list of OrganizationalUnitGrantNode objects
	 * @param JSONGrantNodes the JSON representation of the list of OrganizationalUnitGrantNode
	 * @param grant the grant of that list
	 * @return the deserialized object
	 * @throws Exception
	 */
	private List<OrganizationalUnitGrantNode> deserializeOrganizationalUnitGrantNodes(JSONArray JSONGrantNodes, OrganizationalUnitGrant grant) throws Exception{
		List<OrganizationalUnitGrantNode> nodes = new ArrayList<OrganizationalUnitGrantNode>();
		for(int i=0; i<JSONGrantNodes.length(); i++){
			nodes.add(deserializeOrganizationalUnitGrantNode( JSONGrantNodes.getJSONObject(i), grant));
		}
		return nodes;
	}
	
	/**
	 * Deserialize a OrganizationalUnitGrantNode object
	 * @param JSONGrantNodes the JSON representation of the OrganizationalUnitGrantNode
	 * @param grant the grant of that object
	 * @return the deserialized object
	 * @throws Exception
	 */
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
	
	
	
	/**
	 * Deserialize a list of OrganizationalUnitGrantNode objects. If the nodes have not been expanded by the
	 * user, this methods load the subtree rooted in all the JSONGrantNodes and
	 * build one grant node for each node of the subtree
	 * @param JSONGrantNodes the JSON representation of the list of OrganizationalUnitGrantNode
	 * @param grant the grant of that list
	 * @return the deserialized object
	 * @throws Exception
	 */
	private List<OrganizationalUnitGrantNode> deserializeOrganizationalUnitGrantNodesAndUpdateChilds(JSONArray JSONGrantNodes, OrganizationalUnitGrant grant) throws Exception{
		List<OrganizationalUnitGrantNode> nodes = new ArrayList<OrganizationalUnitGrantNode>();
		for(int i=0; i<JSONGrantNodes.length(); i++){
			nodes.addAll(deserializeOrganizationalUnitGrantNodeAndUpdateChilds( JSONGrantNodes.getJSONObject(i), grant));
		}
		return nodes;
	}
	
	/**
	 * Deserialize a OrganizationalUnitGrantNode object. If the node has not been expanded by the
	 * user, this methods load the subtree rooted in the JSONGrantNodes and
	 * build one grant node for each node of the subtree
	 * @param JSONGrantNodes the JSON representation of the OrganizationalUnitGrantNode
	 * @param grant the grant of that object
	 * @return the deserialized object
	 * @throws Exception
	 */
	private List<OrganizationalUnitGrantNode> deserializeOrganizationalUnitGrantNodeAndUpdateChilds(JSONObject JSONGrantNode, OrganizationalUnitGrant grant) throws Exception{
		OrganizationalUnitGrantNode node = new OrganizationalUnitGrantNode();
		int hierarchyId = JSONGrantNode.getInt("hierarchyId");
		int modelInstanceId = JSONGrantNode.getInt("modelinstance");
		String ouPath = JSONGrantNode.getString("ouPath");
		ModelInstanceNode modelInstanceNode = DAOFactory.getModelInstanceDAO().loadModelInstanceById(modelInstanceId, null);
		OrganizationalUnitNode ouNode = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitNode(ouPath, hierarchyId);
		node.setGrant(grant);
		node.setModelInstanceNode(modelInstanceNode);
		node.setOuNode(ouNode);
		List<OrganizationalUnitGrantNode> nodes = new ArrayList<OrganizationalUnitGrantNode>();
		nodes.add(node);
		boolean expanded = JSONGrantNode.getBoolean("expanded");
		if(!expanded){
			nodes.addAll(buildGrantForChilds(ouNode, modelInstanceNode, grant));
		}
		return nodes;
	}
	
	/**
	 * For each child of the OrganizationalUnitNode ouNode, build a OrganizationalUnitGrantNode
	 * with grant grant and model Instance Node modelInstanceNode
	 * @param ouNode
	 * @param modelInstanceNode
	 * @param grant
	 * @return
	 */
	private List<OrganizationalUnitGrantNode> buildGrantForChilds(OrganizationalUnitNode ouNode, ModelInstanceNode modelInstanceNode,  OrganizationalUnitGrant grant){
		List<OrganizationalUnitGrantNode> nodes = new ArrayList<OrganizationalUnitGrantNode>();
		OrganizationalUnitGrantNode childNode;
		List<OrganizationalUnitNode> childOus = DAOFactory.getOrganizationalUnitDAO().getChildrenNodes(ouNode.getNodeId());
		for(int i=0; i<childOus.size(); i++){
			childNode= new OrganizationalUnitGrantNode();
			childNode.setGrant(grant);
			childNode.setModelInstanceNode(modelInstanceNode);
			childNode.setOuNode(childOus.get(i));
			nodes.add(childNode);
			nodes.addAll(buildGrantForChilds(childOus.get(i), modelInstanceNode, grant));
		}
		return nodes;
	}
	
	public Date toDate(String dateStr, String format) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		Date date = null;
		dateFormat.applyPattern("yyyy-MM-dd");
		dateFormat.setLenient(false);
		date = dateFormat.parse(dateStr);
		return date;
	}

}
