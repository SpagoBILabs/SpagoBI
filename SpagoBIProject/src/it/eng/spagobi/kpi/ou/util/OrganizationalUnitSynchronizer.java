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

package it.eng.spagobi.kpi.ou.util;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.ou.bo.Node;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.bo.Tree;
import it.eng.spagobi.kpi.ou.provider.OrganizationalUnitListProvider;
import it.eng.spagobi.kpi.ou.provider.OrganizationalUnitListProviderMock;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class OrganizationalUnitSynchronizer {
	
	static private Logger logger = Logger.getLogger(OrganizationalUnitSynchronizer.class);
	
	public void synchronize() throws Exception {
		logger.debug("IN");
        try {
        	OrganizationalUnitListProvider provider = getProvider();
        	synchronizeOU(provider);
        	synchronizeHierarchies(provider);
        	synchronizeHierarchiesStructure(provider);
		} finally {
			logger.debug("OUT");
		}
	}

	private void synchronizeHierarchiesStructure(
			OrganizationalUnitListProvider provider) {
		List<OrganizationalUnitHierarchy> hierarchies = DAOFactory.getOrganizationalUnitDAO().getHierarchiesList();
		Iterator<OrganizationalUnitHierarchy> it = hierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy hierarchy = it.next();
			Tree<OrganizationalUnit> tree = provider.getHierarchyStructure(hierarchy);
			synchronizeHierarchyStructure(tree, hierarchy);
		}
	}
	
	private void synchronizeHierarchyStructure(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		removeNonExistingNodes(tree, hierarchy);
		insertNewNodes(tree, hierarchy);
	}

	private void insertNewNodes(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		Node<OrganizationalUnit> root = tree.getRoot();
		insertNewNodes(root, hierarchy);
	}
	
	private void insertNewNodes(Node<OrganizationalUnit> node, OrganizationalUnitHierarchy hierarchy) {
		if (!exists(node, hierarchy)) {
			Node<OrganizationalUnit> parent = node.getParent();
			insertNode(node, parent, hierarchy);
		}
		List<Node<OrganizationalUnit>> children = node.getChildren();
		if (children != null && children.size() > 0) {
			Iterator<Node<OrganizationalUnit>> it = children.iterator();
			while (it.hasNext()) {
				Node<OrganizationalUnit> aChild = it.next();
				insertNewNodes(aChild, hierarchy);
			}
		}
	}
	
	private void insertNode(Node<OrganizationalUnit> node,
			Node<OrganizationalUnit> parent,
			OrganizationalUnitHierarchy hierarchy) {
		OrganizationalUnitNode aNode = new OrganizationalUnitNode();
		aNode.setHierarchy(hierarchy);
		aNode.setOu(node.getNodeContent());
		aNode.setPath(node.getPath());
		if (parent != null) {
			OrganizationalUnitNode parentNode = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitNode(parent.getPath(), hierarchy);
			Integer parentNodeId = parentNode.getNodeId();
			aNode.setParentNodeId(parentNodeId);
		}
		DAOFactory.getOrganizationalUnitDAO().insertOrganizationalUnitNode(aNode);
	}

	private boolean exists(Node<OrganizationalUnit> node, OrganizationalUnitHierarchy hierarchy) {
		return DAOFactory.getOrganizationalUnitDAO().existsNodeInHierarchy(node.getPath(), hierarchy);
	}
	
	private void removeNonExistingNodes(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		List<OrganizationalUnitNode> rootNodes = DAOFactory.getOrganizationalUnitDAO().getRootNodes(hierarchy.getId());
		removeNonExistingNodes(tree, rootNodes, hierarchy);
	}
	
	private void removeNonExistingNodes(Tree<OrganizationalUnit> tree, List<OrganizationalUnitNode> nodes, OrganizationalUnitHierarchy hierarchy) {
		Iterator<OrganizationalUnitNode> it = nodes.iterator();
		while (it.hasNext()) {
			OrganizationalUnitNode aNode = it.next();
			if (tree.containsPath(aNode.getPath())) {
				List<OrganizationalUnitNode> children = DAOFactory.getOrganizationalUnitDAO().getChildrenNodes(hierarchy.getId(), aNode.getNodeId());
				removeNonExistingNodes(tree, children, hierarchy);
			} else {
				DAOFactory.getOrganizationalUnitDAO().eraseOrganizationalUnitNode(aNode);
			}
		}
	}

	/**
	 * Synchronizes hierarchies' list by removing no more existing hierarchies and inserting new ones
	 * @param provider The Organizational Units info provider
	 */
	private void synchronizeHierarchies(OrganizationalUnitListProvider provider) {
		List<OrganizationalUnitHierarchy> newHierarchies = provider.getHierarchies();
		List<OrganizationalUnitHierarchy> oldHierarchies = DAOFactory.getOrganizationalUnitDAO().getHierarchiesList();
		removeNonExistingHierarchies(newHierarchies, oldHierarchies);
		modifyExistingHierarchies(newHierarchies, oldHierarchies);
		insertNewHierarchies(newHierarchies, oldHierarchies);
	}

	/**
	 * Synchronizes OU list by removing no more existing OUs and inserting new ones
	 * @param provider The Organizational Units info provider
	 */
	private void synchronizeOU(OrganizationalUnitListProvider provider) {
		List<OrganizationalUnit> newOUs = provider.getOrganizationalUnits();
		List<OrganizationalUnit> oldOUs = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitList();
		removeNonExistingOUs(newOUs, oldOUs);
		modifyExistingOUs(newOUs, oldOUs);
		insertNewOUs(newOUs, oldOUs);
	}
	
	private void removeNonExistingOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		Iterator<OrganizationalUnit> it = oldOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			if (!newOUs.contains(ou)) {
				DAOFactory.getOrganizationalUnitDAO().eraseOrganizationalUnit(ou);
			}
		}
	}
	
	private void modifyExistingOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		Iterator<OrganizationalUnit> it = oldOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			int index = newOUs.indexOf(ou);
			if (index >= 0) {
				OrganizationalUnit newOU = newOUs.get(index);
				if (!newOU.deepEquals(ou)) {
					ou.setName(newOU.getName());
					ou.setDescription(newOU.getDescription());
					DAOFactory.getOrganizationalUnitDAO().modifyOrganizationalUnit(ou);
				}
				newOU.setId(ou.getId()); // setting the current OU id
			}
		}
	}
	
	private void insertNewOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		Iterator<OrganizationalUnit> it = newOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			if (!oldOUs.contains(ou)) {
				DAOFactory.getOrganizationalUnitDAO().insertOrganizationalUnit(ou);
			}
		}
	}
	
	private void removeNonExistingHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		Iterator<OrganizationalUnitHierarchy> it = oldHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			if (!newHierarchies.contains(h)) {
				DAOFactory.getOrganizationalUnitDAO().eraseHierarchy(h);
			}
		}
	}
	
	private void modifyExistingHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		Iterator<OrganizationalUnitHierarchy> it = oldHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			int index = newHierarchies.indexOf(h);
			if (index >= 0) {
				OrganizationalUnitHierarchy newHierarchy = newHierarchies.get(index);
				if (!newHierarchy.deepEquals(h)) {
					h.setName(newHierarchy.getName());
					h.setDescription(newHierarchy.getDescription());
					h.setTarget(newHierarchy.getTarget());
					DAOFactory.getOrganizationalUnitDAO().modifyHierarchy(h);
				}
				newHierarchy.setId(h.getId());
			}
		}
	}
	
	private void insertNewHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		Iterator<OrganizationalUnitHierarchy> it = newHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			if (!oldHierarchies.contains(h)) {
				DAOFactory.getOrganizationalUnitDAO().insertHierarchy(h);
			}
		}
	}

	private OrganizationalUnitListProvider getProvider() {
		return new OrganizationalUnitListProviderMock(); // TODO: read from configuration implementation class
	}

	
	
}
