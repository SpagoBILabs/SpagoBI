/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.hierarchiesmanagement.service.rest;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.tools.dataset.bo.AbstractJDBCDataset;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.tools.hierarchiesmanagement.Hierarchies;
import it.eng.spagobi.tools.hierarchiesmanagement.HierarchiesSingleton;
import it.eng.spagobi.tools.hierarchiesmanagement.HierarchyTreeNode;
import it.eng.spagobi.tools.hierarchiesmanagement.HierarchyTreeNodeData;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Service for Hierarchies Management
 * 
 * @author Marco Cortella (marco.cortella@eng.it)
 * 
 */

@Path("/hierarchies")
public class HierarchiesService {

	static private Logger logger = Logger.getLogger(HierarchiesService.class);
	private static String DIMENSIONS = "DIMENSIONS";
	private static String DIMENSION = "DIMENSION";
	private static String NAME = "NAME";
	private static String DATASOURCE = "DATASOURCE";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test(@Context HttpServletRequest req) {
		// TODO: to remove, just for testing rest service

		return "{\"response\":\"ok\"}";
	}

	@GET
	@Path("/dimensions")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getDimensions(@Context HttpServletRequest req) {

		Hierarchies hierarchies = HierarchiesSingleton.getInstance();
		SourceBean sb = hierarchies.getTemplate();
		JSONArray dimesionsJSONArray = new JSONArray();

		try {
			SourceBean dimensions = (SourceBean) sb.getAttribute(DIMENSIONS);

			List lst = dimensions.getAttributeAsList(DIMENSION);
			for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
				JSONObject dimension = new JSONObject();
				SourceBean sbRow = (SourceBean) iterator.next();
				String name = sbRow.getAttribute(NAME) != null ? sbRow.getAttribute(NAME).toString() : null;
				dimension.put("DIMENSION_NM", name);
				String datasource = sbRow.getAttribute(DATASOURCE) != null ? sbRow.getAttribute(DATASOURCE).toString() : null;
				dimension.put("DIMENSION_DS", datasource);
				dimesionsJSONArray.put(dimension);
			}

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving dimensions names", t);
		}

		return dimesionsJSONArray.toString();
	}

	// get hierarchies names of a dimension
	@GET
	@Path("/hierarchiesOfDimension")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getHierarchiesOfDimensions(@QueryParam("dimension") String dimension) {
		JSONArray hierarchiesJSONArray = new JSONArray();

		try {

			// 1 - get hierarchy table postfix(ex: _CDC)
			Hierarchies hierarchies = HierarchiesSingleton.getInstance();
			String hierarchyPrefix = hierarchies.getHierarchyTablePrefixName(dimension);
			// 2 - get datasource label name
			String dataSourceName = hierarchies.getDataSourceOfDimension(dimension);
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceByLabel(dataSourceName);
			if (dataSource == null) {
				throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchies names", "No datasource found for Hierarchies");
			}
			// 3- execute query to get hierarchies names
			String hierarchyNameColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_NM", dataSource);
			String typeColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_TP", dataSource);

			String tableName = "HIER_" + hierarchyPrefix;
			IDataStore dataStore = dataSource.executeStatement("SELECT DISTINCT(" + hierarchyNameColumn + ") FROM " + tableName + " WHERE " + typeColumn
					+ "=\"AUTO\"", 0, 0);
			for (Iterator iterator = dataStore.iterator(); iterator.hasNext();) {
				IRecord record = (IRecord) iterator.next();
				IField field = record.getFieldAt(0);
				String hierarchyName = (String) field.getValue();
				JSONObject hierarchy = new JSONObject();
				hierarchy.put("HIERARCHY_NM", hierarchyName);
				hierarchiesJSONArray.put(hierarchy);

			}

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving automatic hierarchies names", t);
		}
		return hierarchiesJSONArray.toString();
	}

	// get automatic hierarchy structure for tree visualization
	@GET
	@Path("/getAutomaticHierarchyTree")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getAutomaticHierarchyTree(@QueryParam("dimension") String dimension, @QueryParam("hierarchy") String hierarchy) {
		HierarchyTreeNode hierarchyTree;
		JSONObject treeJSONObject;
		try {
			Hierarchies hierarchies = HierarchiesSingleton.getInstance();

			// 1 - get datasource label name
			String dataSourceName = hierarchies.getDataSourceOfDimension(dimension);
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceByLabel(dataSourceName);
			if (dataSource == null) {
				throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchies names", "No datasource found for Hierarchies");
			}
			// 2 -get hierarchy table postfix
			String hierarchyPrefix = hierarchies.getHierarchyTablePrefixName(dimension);
			String hierarchyFK = hierarchies.getHierarchyTableForeignKeyName(dimension);
			// 3 - execute query to get hierarchies leafs
			String queryText = this.createQueryAutomaticHierarchy(dataSource, hierarchyFK, hierarchyPrefix, hierarchy);
			IDataStore dataStore = dataSource.executeStatement(queryText, 0, 0);

			// 4 - Create ADT for Tree from datastore
			hierarchyTree = createHierarchyTreeStructure(dataStore);

			treeJSONObject = convertHierarchyTreeAsJSON(hierarchyTree);

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchy structure", t);
		}

		return treeJSONObject.toString();

	}

	// get custom hierarchies names
	@GET
	@Path("/getCustomHierarchies")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getCustomHierarchies(@QueryParam("dimension") String dimension) {
		JSONArray hierarchiesJSONArray = new JSONArray();

		try {

			// 1 - get hierarchy table postfix(ex: _CDC)
			Hierarchies hierarchies = HierarchiesSingleton.getInstance();
			String hierarchyPrefix = hierarchies.getHierarchyTablePrefixName(dimension);
			// 2 - get datasource label name
			String dataSourceName = hierarchies.getDataSourceOfDimension(dimension);
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceByLabel(dataSourceName);
			if (dataSource == null) {
				throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchies names", "No datasource found for Hierarchies");
			}
			// 3- execute query to get hierarchies names
			String hierarchyNameColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_NM", dataSource);
			String typeColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_TP", dataSource);

			String tableName = "HIER_" + hierarchyPrefix;
			IDataStore dataStore = dataSource.executeStatement("SELECT DISTINCT(" + hierarchyNameColumn + ")," + typeColumn + " FROM " + tableName + " WHERE "
					+ typeColumn + "=\"MANUAL\" OR " + typeColumn + "=\"SEMIMANUAL\" ", 0, 0);
			for (Iterator iterator = dataStore.iterator(); iterator.hasNext();) {
				IRecord record = (IRecord) iterator.next();
				IField field = record.getFieldAt(0);
				String hierarchyName = (String) field.getValue();
				field = record.getFieldAt(1);
				String hierarchyType = (String) field.getValue();
				JSONObject hierarchy = new JSONObject();
				hierarchy.put("HIERARCHY_NM", hierarchyName);
				hierarchy.put("HIERARCHY_TP", hierarchyType);
				hierarchiesJSONArray.put(hierarchy);

			}

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving custom hierarchies names", t);
		}
		return hierarchiesJSONArray.toString();

		// return "{\"response\":\"customHierarchies\"}";

	}

	@GET
	@Path("/getCustomHierarchyTree")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getCustomHierarchyTree(@QueryParam("dimension") String dimension, @QueryParam("hierarchy") String hierarchy) {
		// get custom hierarchy structure for tree visualization
		HierarchyTreeNode hierarchyTree;
		JSONObject treeJSONObject;
		try {
			Hierarchies hierarchies = HierarchiesSingleton.getInstance();

			// 1 - get datasource label name
			String dataSourceName = hierarchies.getDataSourceOfDimension(dimension);
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceByLabel(dataSourceName);
			if (dataSource == null) {
				throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchies names", "No datasource found for Hierarchies");
			}
			// 2 -get hierarchy table postfix
			String hierarchyPrefix = hierarchies.getHierarchyTablePrefixName(dimension);
			String hierarchyFK = hierarchies.getHierarchyTableForeignKeyName(dimension);

			// 3 - execute query to get hierarchies leafs
			String queryText = this.createQueryCustomHierarchy(dataSource, hierarchyFK, hierarchyPrefix, hierarchy);
			IDataStore dataStore = dataSource.executeStatement(queryText, 0, 0);

			// 4 - Create ADT for Tree from datastore
			hierarchyTree = createHierarchyTreeStructure(dataStore);

			treeJSONObject = convertHierarchyTreeAsJSON(hierarchyTree);

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving custom hierarchy structure", t);
		}

		return treeJSONObject.toString();

	}

	@POST
	@Path("/saveCustomHierarchy")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String saveCustomHierarchy(@Context HttpServletRequest req) {
		// TODO: get custom hierarchy structure for tree visualization

		String root = req.getParameter("root");
		JSONObject rootJSONObject = ObjectUtils.toJSONObject(root);
		String hierarchyName = req.getParameter("name");
		String hierarchyScope = req.getParameter("scope");

		Collection<List<HierarchyTreeNodeData>> paths = findRootToLeavesPaths(rootJSONObject);
		for (List<HierarchyTreeNodeData> path : paths) {
			for (HierarchyTreeNodeData node : path) {
				System.out.print(node.getNodeName() + "->");
			}
			System.out.println("\n -------");
		}

		return "{\"response\":\"saveCustomHierarchy\"}";

	}

	@GET
	@Path("/deleteCustomHierarchy")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String deleteCustomHierarchy(@Context HttpServletRequest req) {
		// TODO: get custom hierarchy structure for tree visualization
		return "{\"response\":\"deleteCustomHierarchy\"}";

	}

	/*----------------------------------------------
	 * Utilities functions
	 *----------------------------------------------/
	
	/*
	 * Find all paths from root to leaves
	 */
	private Collection<List<HierarchyTreeNodeData>> findRootToLeavesPaths(JSONObject node) {
		Collection<List<HierarchyTreeNodeData>> collectionOfPaths = new HashSet<List<HierarchyTreeNodeData>>();
		try {
			String nodeName = node.getString("text");
			String nodeCode = node.getString("id");
			String nodeLeafId = node.getString("leafId");
			HierarchyTreeNodeData nodeData = new HierarchyTreeNodeData(nodeCode, nodeName, nodeLeafId);

			// current node is a leaf?
			boolean isLeaf = node.getBoolean("leaf");
			if (isLeaf) {
				List<HierarchyTreeNodeData> aPath = new ArrayList<HierarchyTreeNodeData>();

				aPath.add(nodeData);
				collectionOfPaths.add(aPath);
				return collectionOfPaths;
			} else {
				// node has children
				JSONArray childs = node.getJSONArray("children");
				for (int i = 0; i < childs.length(); i++) {
					JSONObject child = childs.getJSONObject(i);
					Collection<List<HierarchyTreeNodeData>> childPaths = findRootToLeavesPaths(child);
					for (List<HierarchyTreeNodeData> path : childPaths) {
						// add this node to start of the path
						path.add(0, nodeData);
						collectionOfPaths.add(path);
					}
				}

			}
			return collectionOfPaths;
		} catch (JSONException je) {
			logger.error("An unexpected error occured while retriving custom hierarchy root-leafs paths");
			throw new SpagoBIServiceException("An unexpected error occured while retriving custom hierarchy root-leafs paths", je);
		}

	}

	/**
	 * Create query for extracting automatic hierarchy rows
	 */
	private String createQueryAutomaticHierarchy(IDataSource dataSource, String hierarchyFK, String hierarchyPrefix, String hierarchyName) {

		String tableName = "HIER_" + hierarchyPrefix;

		// select
		StringBuffer selectClauseBuffer = new StringBuffer(" ");
		for (int i = 1; i < 11; i++) {
			String CD_LEV = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_CD_LEV" + i, dataSource);
			String NM_LEV = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_NM_LEV" + i, dataSource);
			selectClauseBuffer.append(CD_LEV + "," + NM_LEV + ",");
		}
		String CD_LEAF = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_CD_LEAF", dataSource);
		String NM_LEAF = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_NM_LEAF", dataSource);
		String LEAF_ID = AbstractJDBCDataset.encapsulateColumnName(hierarchyFK, dataSource);

		selectClauseBuffer.append(CD_LEAF + "," + NM_LEAF + "," + LEAF_ID + " ");
		String selectClause = selectClauseBuffer.toString();

		// where
		String hierNameColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_NM", dataSource);
		String hierTypeColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_TP", dataSource);

		String query = "SELECT " + selectClause + " FROM " + tableName + " WHERE " + hierNameColumn + " = \"" + hierarchyName + "\" AND " + hierTypeColumn
				+ " = \"AUTO\" ";

		return query;
	}

	/**
	 * Create query for extracting automatic hierarchy rows
	 */
	private String createQueryCustomHierarchy(IDataSource dataSource, String hierarchyFK, String hierarchyPrefix, String hierarchyName) {

		String tableName = "HIER_" + hierarchyPrefix;

		// select
		StringBuffer selectClauseBuffer = new StringBuffer(" ");
		for (int i = 1; i < 11; i++) {
			String CD_LEV = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_CD_LEV" + i, dataSource);
			String NM_LEV = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_NM_LEV" + i, dataSource);
			selectClauseBuffer.append(CD_LEV + "," + NM_LEV + ",");
		}
		String CD_LEAF = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_CD_LEAF", dataSource);
		String NM_LEAF = AbstractJDBCDataset.encapsulateColumnName(hierarchyPrefix + "_NM_LEAF", dataSource);
		String LEAF_ID = AbstractJDBCDataset.encapsulateColumnName(hierarchyFK, dataSource);

		selectClauseBuffer.append(CD_LEAF + "," + NM_LEAF + "," + LEAF_ID + " ");
		String selectClause = selectClauseBuffer.toString();

		// where
		String hierNameColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_NM", dataSource);
		String hierTypeColumn = AbstractJDBCDataset.encapsulateColumnName("HIER_TP", dataSource);

		String query = "SELECT " + selectClause + " FROM " + tableName + " WHERE " + hierNameColumn + " = \"" + hierarchyName + "\" AND (" + hierTypeColumn
				+ "=\"MANUAL\" OR " + hierTypeColumn + "=\"SEMIMANUAL\" )";

		return query;
	}

	/**
	 * Create HierarchyTreeNode tree from datastore with leafs informations
	 */
	private HierarchyTreeNode createHierarchyTreeStructure(IDataStore dataStore) {
		HierarchyTreeNode root = null;

		// contains the code of the last level node (not null) inserted in the
		// tree

		for (Iterator iterator = dataStore.iterator(); iterator.hasNext();) {
			String lastLevelFound = null;

			IRecord record = (IRecord) iterator.next();
			List<IField> recordFields = record.getFields();
			int fieldsCount = recordFields.size();

			for (int i = 0; i < fieldsCount - 1; i = i + 2) {
				IField codeField = record.getFieldAt(i); // NODE CODE
				IField nameField = record.getFieldAt(i + 1); // NODE NAME

				if (codeField.getValue() == null) {
					continue; // skip to next iteration
				} else {
					String nodeCode = (String) codeField.getValue();
					String nodeName = (String) nameField.getValue();
					HierarchyTreeNodeData data = new HierarchyTreeNodeData(nodeCode, nodeName);

					// Here I will contruct the nodes of the tree
					switch (i) {
					case 0:
						// first level (root)
						if (root == null) {
							root = new HierarchyTreeNode(data, nodeCode);
						}
						lastLevelFound = nodeCode;
						break;
					case 2:
						// second level (root's childrens)
						if (!root.getChildrensKeys().contains(nodeCode)) {
							// node not already attached to the root
							HierarchyTreeNode aNode = new HierarchyTreeNode(data, nodeCode);
							root.add(aNode, nodeCode);
						}
						lastLevelFound = nodeCode;
						break;
					case 4:
					case 6:
					case 8:
					case 10:
					case 12:
					case 14:
					case 16:
					case 18:
					case 20:
						// inject leafID into node
						IField leafIdField = record.getFieldAt(i + 2);
						Long leafId = (Long) leafIdField.getValue();
						String leafIdString = String.valueOf(leafId);
						data.setLeafId(leafIdString);

						attachNodeToLevel(root, nodeCode, lastLevelFound, data);
						lastLevelFound = nodeCode;
						// leaf level
						break;
					}
				}

			}

		}

		// System.out.println(TreeString.toString(root));

		return root;

	}

	/**
	 * Attach a node as a child of another node (with key lastLevelFound)
	 */
	private void attachNodeToLevel(HierarchyTreeNode root, String nodeCode, String lastLevelFound, HierarchyTreeNodeData data) {
		HierarchyTreeNode treeNode = null;
		// first search parent node
		for (Iterator<HierarchyTreeNode> treeIterator = root.iterator(); treeIterator.hasNext();) {
			treeNode = treeIterator.next();
			if (treeNode.getKey().equals(lastLevelFound)) {
				// parent node found
				break;
			}
		}
		// then check if node was already added as a child of this parent

		if (!treeNode.getChildrensKeys().contains(nodeCode)) {
			// node not already attached to the level

			HierarchyTreeNode aNode = new HierarchyTreeNode(data, nodeCode);
			treeNode.add(aNode, nodeCode);

		}
	}

	/**
	 * Serialize HierarchyTreeNode to JSON
	 * 
	 * @param root
	 *            the root of the tree structure
	 * @return a JSONObject representing the tree
	 */
	private JSONObject convertHierarchyTreeAsJSON(HierarchyTreeNode root) {
		JSONObject rootJSONObject = new JSONObject();
		try {
			HierarchyTreeNodeData rootData = (HierarchyTreeNodeData) root.getObject();
			rootJSONObject.put("text", rootData.getNodeName());
			rootJSONObject.put("id", rootData.getNodeCode());
			// rootJSONObject.put("root", true);
			rootJSONObject.put("expanded", true);
			rootJSONObject.put("leaf", false);

			JSONArray childrenJSONArray = new JSONArray();

			for (int i = 0; i < root.getChildCount(); i++) {
				HierarchyTreeNode childNode = root.getChild(i);
				JSONObject subTreeJSONObject = getSubTreeJSONObject(childNode);
				childrenJSONArray.put(subTreeJSONObject);
			}

			rootJSONObject.put("children", childrenJSONArray);
			// fake root
			JSONObject mainObject = new JSONObject();
			mainObject.put("text", "root");
			mainObject.put("root", true);
			mainObject.put("children", rootJSONObject);
			mainObject.put("leaf", false);
			mainObject.put("expanded", true);

			return mainObject;

		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while retriving hierarchy structure", t);
		}

	}

	/**
	 * get the JSONObject representing the tree having the passed node as a root
	 * 
	 * @param node
	 *            the root of the subtree
	 * @return JSONObject representing the subtree
	 */
	private JSONObject getSubTreeJSONObject(HierarchyTreeNode node) {
		try {
			if (node.getChildCount() > 0) {
				JSONObject nodeJSONObject = new JSONObject();
				HierarchyTreeNodeData nodeData = (HierarchyTreeNodeData) node.getObject();
				nodeJSONObject.put("text", nodeData.getNodeName());
				nodeJSONObject.put("id", nodeData.getNodeCode());
				nodeJSONObject.put("leafId", nodeData.getLeafId());

				JSONArray childrenJSONArray = new JSONArray();

				for (int i = 0; i < node.getChildCount(); i++) {
					HierarchyTreeNode childNode = node.getChild(i);
					JSONObject subTree = getSubTreeJSONObject(childNode);
					childrenJSONArray.put(subTree);
				}
				nodeJSONObject.put("children", childrenJSONArray);
				nodeJSONObject.put("leaf", false);
				nodeJSONObject.put("expanded", true);
				return nodeJSONObject;

			} else {
				HierarchyTreeNodeData nodeData = (HierarchyTreeNodeData) node.getObject();
				JSONObject nodeJSONObject = new JSONObject();

				nodeJSONObject.put("text", nodeData.getNodeName());
				nodeJSONObject.put("id", nodeData.getNodeCode());
				nodeJSONObject.put("leafId", nodeData.getLeafId());
				nodeJSONObject.put("leaf", true);
				return nodeJSONObject;

			}
		} catch (Throwable t) {
			throw new SpagoBIServiceException("An unexpected error occured while serializing hierarchy structure to JSON", t);
		}

	}

}
