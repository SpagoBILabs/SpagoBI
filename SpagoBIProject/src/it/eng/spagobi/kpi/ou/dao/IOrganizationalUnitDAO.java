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
package it.eng.spagobi.kpi.ou.dao;

import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrant;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrantNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNodeWithGrant;

import java.util.List;


public interface IOrganizationalUnitDAO {

	/**
	 * @return the list of OU
	 */
	public List<OrganizationalUnit> getOrganizationalUnitList();
	
	/**
	 * @return the OU with the given identifier
	 */
	public OrganizationalUnit getOrganizationalUnit(Integer id);
	
	/**
	 * Removes the organizational unit
	 */
	public void eraseOrganizationalUnit(Integer ouId);
	
	/**
	 * Inserts the organizational unit
	 */
	public void insertOrganizationalUnit(OrganizationalUnit ou);
	
	/**
	 * Modifies the organizational unit
	 */
	public void modifyOrganizationalUnit(OrganizationalUnit ou);
	
	/**
	 * @return the list of hierarchies
	 */
	public List<OrganizationalUnitHierarchy> getHierarchiesList();
	
	/**
	 * @return the Hierarchy with the given identifier
	 */
	public OrganizationalUnitHierarchy getHierarchy(Integer id);
	
	/**
	 * Removes the hierarchy
	 */
	public void eraseHierarchy(Integer hierarchyId);
	
	/**
	 * Inserts the hierarchy
	 */
	public void insertHierarchy(OrganizationalUnitHierarchy h);
	
	/**
	 * Modifies the hierarchy
	 */
	public void modifyHierarchy(OrganizationalUnitHierarchy h);
	
	/**
	 * @return the root node for a single hierarchy
	 */
	public OrganizationalUnitNode getRootNode(Integer hierarchyId);
	
	/**
	 * @return the root node (with grants) for a single hierarchy
	 */
	public OrganizationalUnitNodeWithGrant getRootNodeWithGrants(Integer hierarchyId, Integer grantId);
	
	/**
	 * @return the list of children nodes for a single node of a hierarchy
	 */
	public List<OrganizationalUnitNode> getChildrenNodes(Integer nodeId);
	
	/**
	 * @return the list of children nodes for a single node of a hierarchy
	 */
	public List<OrganizationalUnitNodeWithGrant> getChildrenNodesWithGrants(Integer nodeId, Integer grantId);
	
	/**
	 * @return the list of grants (i.e. association between a KPI model instance and a OU hierarchy)
	 */
	public List<OrganizationalUnitGrant> getGrantsList();
	
	/**
	 * Inserts a new grant
	 * @param grant
	 */
	public void insertGrant(OrganizationalUnitGrant grant);
	
	/**
	 * Modify a grant
	 * @param grant
	 */
	public void modifyGrant(OrganizationalUnitGrant grant);
	
	/**
	 * Removes a grant
	 * @param grantId The grant identifier
	 */
	public void eraseGrant(Integer grantId);
	
	/**
	 * @return the list of grants for a single node of a hierarchy (i.e. association between a KPI model instance node and a OU hierarchy node)
	 */
	public List<OrganizationalUnitGrantNode> getNodeGrants(Integer nodeId, Integer grantId);
	
	/**
	 * Inserts a list of grant nodes (a grant node is an association between a hierarchy node and a KPI model instance node 
	 * in the context of a grant)
	 * @param grantNodes
	 */
	public void insertNodeGrants(List<OrganizationalUnitGrantNode> grantNodes);
	
	/**
	 * Removes a node from the structure with its descendants
	 * @param node The node to be removed
	 */
	public void eraseOrganizationalUnitNode(OrganizationalUnitNode node);

	/**
	 * Checks if the input path exists in the given hierarchy
	 * @param path
	 * @param hierarchyId The hierarchy identifier
	 * @return true if the path exists in the given hierarchy, false otherwise
	 */
	public boolean existsNodeInHierarchy(String path, Integer hierarchyId);
	
	/**
	 * Retrieve the node with the input path in the given hierarchy
	 * @param path
	 * @param hierarchyId The hierarchy identifier
	 * @return the node with the input path in the given hierarchy
	 */
	public OrganizationalUnitNode getOrganizationalUnitNode(String path, Integer hierarchyId);

	/**
	 * Inserts the input node in the hierarchy
	 * @param aNode
	 */
	public void insertOrganizationalUnitNode(OrganizationalUnitNode aNode);
}
