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

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.provider.OrganizationalUnitListProvider;
import it.eng.spagobi.kpi.ou.provider.OrganizationalUnitListProviderMock;
import it.eng.spagobi.utilities.tree.Node;
import it.eng.spagobi.utilities.tree.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class synchronizes OU list, hierarchies list and hierarchies structure.
 * It retrieves information by a <i>it.eng.spagobi.kpi.ou.provider.OrganizationalUnitListProvider</i>.
 * Inside an instance of class <i>it.eng.spagobi.kpi.ou.bo.OrganizationalUnit</i> retrieved by the OrganizationalUnitListProvider, the "id"
 * property does not make sense, since it does not match the "id" in the SpagoBI repository. An existing OU is matched to a OU 
 * coming from the OrganizationalUnitListProvider if it has the same label; in this case, we set the id coming from SpagoBI repository.
 * For hierarchies it is the same.
 * For hierarchies structure, we consider the path of each node: if an existing path does no more exists, it is deleted (with its descendants); 
 * if a new path does not exist, it is inserted (recursively).
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class OrganizationalUnitSynchronizer {
	
	static private Logger logger = Logger.getLogger(OrganizationalUnitSynchronizer.class);
	
	public void synchronize() {
		logger.debug("IN");
        try {
        	OrganizationalUnitListProvider provider = getProvider();
        	logger.debug("OrganizationalUnitListProvider retrieved: " + provider);
        	provider.initialize();
        	logger.debug("Provider Initialized");
        	adjustHierarchies(provider);
        	logger.debug("Hierarchies' names adjusted");
        	synchronizeOU(provider);
        	logger.debug("OU synchronized");
        	synchronizeHierarchies(provider);
        	logger.debug("Hierarchies synchronized");
        	synchronizeHierarchiesStructure(provider);
        	logger.debug("Hierarchies's structure synchronized");
		} finally {
			logger.debug("OUT");
		}
	}

	/**
	 * Adjust hierarchies' label in order to have the label starting with the company name
	 * @param provider The Organizational Units info provider
	 */
	private void adjustHierarchies(OrganizationalUnitListProvider provider) {
		logger.debug("IN: provider = " + provider);
		List<OrganizationalUnitHierarchy> newHierarchies = provider.getHierarchies();
		logger.debug("Hierarchies retrieved by the provider:");
		logger.debug(newHierarchies);
		Iterator<OrganizationalUnitHierarchy> it = newHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			String label = h.getLabel();
			String company = h.getCompany();
			logger.debug("Hierarchy label = [" + label + "], company = [" + company + "]");
			if (company != null && !company.trim().equals("") && !label.startsWith(company + " - ")) {
				h.setLabel(company + " - " + label);
				logger.info("Hierarchy label modified : new label is [" + label + "]");
			}
		}
		logger.debug("OUT");
	}

	private void synchronizeHierarchiesStructure(
			OrganizationalUnitListProvider provider) {
		logger.debug("IN: provider = " + provider);
		List<OrganizationalUnitHierarchy> hierarchies = DAOFactory.getOrganizationalUnitDAO().getHierarchiesList();
		Iterator<OrganizationalUnitHierarchy> it = hierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy hierarchy = it.next();
			Tree<OrganizationalUnit> tree = provider.getHierarchyStructure(hierarchy);
			logger.debug("Tree structure for hierarchy " + hierarchy + ":");
			logger.debug(tree);
			synchronizeHierarchyStructure(tree, hierarchy);
			logger.debug("Structure of hierarchy " + hierarchy + " synchronized");
		}
		logger.debug("OUT");
	}
	
	private void synchronizeHierarchyStructure(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		removeNoMoreExistingNodes(tree, hierarchy);
		insertNewNodes(tree, hierarchy);
		logger.debug("OUT");
	}

	private void insertNewNodes(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		Node<OrganizationalUnit> root = tree.getRoot();
		insertNewNodes(root, hierarchy);
		logger.debug("OUT");
	}
	
	private void insertNewNodes(Node<OrganizationalUnit> node, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN: node = " + node + ", hierarchy = " + hierarchy);
		if (!exists(node, hierarchy)) {
			logger.debug("Node " + node + " does not exist in hierarchy " + hierarchy + ", it will be inserted.");
			Node<OrganizationalUnit> parent = node.getParent();
			insertNode(node, parent, hierarchy);
			logger.debug("Node " + node + " inserted in hierarchy " + hierarchy + ".");
		}
		// recursion on children
		List<Node<OrganizationalUnit>> children = node.getChildren();
		if (children != null && children.size() > 0) {
			Iterator<Node<OrganizationalUnit>> it = children.iterator();
			while (it.hasNext()) {
				Node<OrganizationalUnit> aChild = it.next();
				insertNewNodes(aChild, hierarchy);
			}
		}
		logger.debug("OUT");
	}
	
	private void insertNode(Node<OrganizationalUnit> node,
			Node<OrganizationalUnit> parent,
			OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN: node = " + node + ", parent = " + parent + ", hierarchy = " + hierarchy);
		OrganizationalUnitNode aNode = new OrganizationalUnitNode();
		aNode.setHierarchy(hierarchy);
		OrganizationalUnit content = node.getNodeContent();
		content = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitByLabelAndName(content.getLabel(), content.getName());
		if(content == null){
			//then insert it!!there could be a misalignment
			DAOFactory.getOrganizationalUnitDAO().insertOrganizationalUnit(node.getNodeContent());
			content = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitByLabelAndName(node.getNodeContent().getLabel(), node.getNodeContent().getName());
		}
		aNode.setOu(content);
		aNode.setPath(node.getPath());
		if (parent != null) {
			OrganizationalUnitNode parentNode = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitNode(parent.getPath(), hierarchy.getId());
			Integer parentNodeId = parentNode.getNodeId();
			aNode.setParentNodeId(parentNodeId);
		}
		DAOFactory.getOrganizationalUnitDAO().insertOrganizationalUnitNode(aNode);
		logger.debug("OUT");
	}

	private boolean exists(Node<OrganizationalUnit> node, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN: node = " + node + ", hierarchy = " + hierarchy);
		boolean toReturn = DAOFactory.getOrganizationalUnitDAO().existsNodeInHierarchy(node.getPath(), hierarchy.getId());
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	private void removeNoMoreExistingNodes(Tree<OrganizationalUnit> tree, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		OrganizationalUnitNode rootNode = DAOFactory.getOrganizationalUnitDAO().getRootNode(hierarchy.getId());
		if (rootNode != null) {
			List<OrganizationalUnitNode> nodes = new ArrayList<OrganizationalUnitNode>();
			nodes.add(rootNode);
			removeNoMoreExistingNodes(tree, nodes, hierarchy);
		}
		logger.debug("OUT");
	}
	
	private void removeNoMoreExistingNodes(Tree<OrganizationalUnit> tree, List<OrganizationalUnitNode> nodes, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		Iterator<OrganizationalUnitNode> it = nodes.iterator();
		while (it.hasNext()) {
			OrganizationalUnitNode aNode = it.next();
			logger.debug("Examining node " + aNode + " ....");
			if (tree.containsPath(aNode.getPath())) {
				logger.debug("Node " + aNode + " exists in hierarchy " + hierarchy + ".");
				// recursion
				List<OrganizationalUnitNode> children = DAOFactory.getOrganizationalUnitDAO().getChildrenNodes(aNode.getNodeId());
				removeNoMoreExistingNodes(tree, children, hierarchy);
			} else {
				logger.debug("Node " + aNode + " does no more exists. Removing it ....");
				DAOFactory.getOrganizationalUnitDAO().eraseOrganizationalUnitNode(aNode);
				logger.debug("Node " + aNode + " removed.");
			}
		}
		logger.debug("OUT");
	}

	/**
	 * Synchronizes hierarchies' list by removing no more existing hierarchies and inserting new ones
	 * @param provider The Organizational Units info provider
	 */
	private void synchronizeHierarchies(OrganizationalUnitListProvider provider) {
		logger.debug("IN: provider = " + provider);
		List<OrganizationalUnitHierarchy> newHierarchies = provider.getHierarchies();
		logger.debug("Hierarchies retrieved by the provider:");
		logger.debug(newHierarchies);
		List<OrganizationalUnitHierarchy> oldHierarchies = DAOFactory.getOrganizationalUnitDAO().getHierarchiesList();
		logger.debug("Current Hierarchies in repository:");
		logger.debug(oldHierarchies);
		removeNoMoreExistingHierarchies(newHierarchies, oldHierarchies);
		modifyExistingHierarchies(newHierarchies, oldHierarchies);
		insertNewHierarchies(newHierarchies, oldHierarchies);
		logger.debug("OUT");
	}

	/**
	 * Synchronizes OU list by removing no more existing OUs and inserting new ones
	 * @param provider The Organizational Units info provider
	 */
	private void synchronizeOU(OrganizationalUnitListProvider provider) {
		logger.debug("IN: provider = " + provider);
		List<OrganizationalUnit> newOUs = provider.getOrganizationalUnits();
		logger.debug("Organizational Units retrieved by the provider:");
		logger.debug(newOUs);
		List<OrganizationalUnit> oldOUs = DAOFactory.getOrganizationalUnitDAO().getOrganizationalUnitList();
		logger.debug("Current Organizational Units in repository:");
		logger.debug(oldOUs);
		removeNoMoreExistingOUs(newOUs, oldOUs);
		modifyExistingOUs(newOUs, oldOUs);
		insertNewOUs(newOUs, oldOUs);
		logger.debug("OUT");
	}
	
	private void removeNoMoreExistingOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		logger.debug("IN");
		Iterator<OrganizationalUnit> it = oldOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			if (!newOUs.contains(ou)) {
				logger.debug("OU " + ou + " does no more exists. Removing it ...");
				DAOFactory.getOrganizationalUnitDAO().eraseOrganizationalUnit(ou.getId());
				logger.debug("OU " + ou + " removed.");
			}
		}
		logger.debug("OUT");
	}
	
	private void modifyExistingOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		logger.debug("IN");
		Iterator<OrganizationalUnit> it = oldOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			int index = newOUs.indexOf(ou);
			if (index >= 0) {
				OrganizationalUnit newOU = newOUs.get(index);
				if (!newOU.deepEquals(ou)) {
					logger.debug("OU " + ou + " has been changed. Updating it ...");
					ou.setName(newOU.getName());
					ou.setDescription(newOU.getDescription());
					DAOFactory.getOrganizationalUnitDAO().modifyOrganizationalUnit(ou);
					logger.debug("OU updated: " + ou);
				}
				newOU.setId(ou.getId()); // setting the current OU id
				logger.debug("OU id updated: " + newOU);
			}
		}
		logger.debug("OUT");
	}
	
	private void insertNewOUs(List<OrganizationalUnit> newOUs, List<OrganizationalUnit> oldOUs) {
		logger.debug("IN");
		Iterator<OrganizationalUnit> it = newOUs.iterator();
		while (it.hasNext()) {
			OrganizationalUnit ou = it.next();
			if (!oldOUs.contains(ou)) {
				logger.debug("OU " + ou + " does not exists. Inserting it ...");
				DAOFactory.getOrganizationalUnitDAO().insertOrganizationalUnit(ou);
				logger.debug("OU inserted: " + ou);
			}
		}
		logger.debug("OUT");
	}
	
	private void removeNoMoreExistingHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		logger.debug("IN");
		Iterator<OrganizationalUnitHierarchy> it = oldHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			if (!newHierarchies.contains(h)) {
				logger.debug("Hierarchy " + h + " does no more exists. Removing it ...");
				DAOFactory.getOrganizationalUnitDAO().eraseHierarchy(h.getId());
				logger.debug("Hierarchy " + h + " removed.");
			}
		}
		logger.debug("OUT");
	}
	
	private void modifyExistingHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		logger.debug("IN");
		Iterator<OrganizationalUnitHierarchy> it = oldHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			int index = newHierarchies.indexOf(h);
			if (index >= 0) {
				OrganizationalUnitHierarchy newHierarchy = newHierarchies.get(index);
				if (!newHierarchy.deepEquals(h)) {
					logger.debug("Hierarchy" + h + " has been changed. Updating it ...");
					h.setName(newHierarchy.getName());
					h.setDescription(newHierarchy.getDescription());
					h.setTarget(newHierarchy.getTarget());
					h.setCompany(newHierarchy.getCompany());
					DAOFactory.getOrganizationalUnitDAO().modifyHierarchy(h);
					logger.debug("Hierarchy updated: " + h);
				}
				newHierarchy.setId(h.getId()); // setting the current hierarchy id
				logger.debug("Hierarchy id updated: " + newHierarchy);
			}
		}
		logger.debug("OUT");
	}
	
	private void insertNewHierarchies(List<OrganizationalUnitHierarchy> newHierarchies, List<OrganizationalUnitHierarchy> oldHierarchies) {
		logger.debug("IN");
		Iterator<OrganizationalUnitHierarchy> it = newHierarchies.iterator();
		while (it.hasNext()) {
			OrganizationalUnitHierarchy h = it.next();
			if (!oldHierarchies.contains(h)) {
				logger.debug("Hierarchy " + h + " does not exists. Inserting it ...");
				DAOFactory.getOrganizationalUnitDAO().insertHierarchy(h);
				logger.debug("Hierarchy inserted: " + h);
			}
		}
		logger.debug("OUT");
	}

	private OrganizationalUnitListProvider getProvider() {
		logger.debug("IN");
		OrganizationalUnitListProvider o = null;
		try {
			SourceBean ouConfig = (SourceBean) ConfigSingleton.getInstance().getAttribute("SPAGOBI.ORGANIZATIONAL-UNIT");
			String prodiverClassName = (String) ouConfig.getAttribute("provider");
			o = (OrganizationalUnitListProvider) Class.forName(prodiverClassName).newInstance();
			logger.debug("OUT");
		} catch (Exception e) {
			logger.error("Cannot get Organizational Unit list provider class", e);
			throw new RuntimeException("Cannot get Organizational Unit list provider class", e);
		}
		return o;
	}

	
	
}
