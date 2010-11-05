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

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelInstanceNode;
import it.eng.spagobi.kpi.model.dao.ModelInstanceDAOImpl;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelInst;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnit;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrant;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitGrantNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitHierarchy;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNode;
import it.eng.spagobi.kpi.ou.bo.OrganizationalUnitNodeWithGrant;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnit;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodes;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodesId;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitHierarchies;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitNodes;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.tree.Tree;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class OrganizationalUnitDAOImpl extends AbstractHibernateDAO implements IOrganizationalUnitDAO {

	static private Logger logger = Logger.getLogger(OrganizationalUnitDAOImpl.class);

	public List<OrganizationalUnit> getOrganizationalUnitList() {
		logger.debug("IN");
		List<OrganizationalUnit> toReturn = new ArrayList<OrganizationalUnit>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnit");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnit((SbiOrgUnit) it.next()));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public List<OrganizationalUnitHierarchy> getHierarchiesList() {
		logger.debug("IN");
		List<OrganizationalUnitHierarchy> toReturn = new ArrayList<OrganizationalUnitHierarchy>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitHierarchies");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitHierarchy((SbiOrgUnitHierarchies) it.next()));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public OrganizationalUnit getOrganizationalUnit(Integer id) {
		logger.debug("IN: id = " + id);
		OrganizationalUnit toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnit hibOU = (SbiOrgUnit) aSession.load(SbiOrgUnit.class, id);

			toReturn = toOrganizationalUnit(hibOU);
			
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public OrganizationalUnitHierarchy getHierarchy(Integer id) {
		logger.debug("IN: id = " + id);
		OrganizationalUnitHierarchy toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitHierarchies hibHierarchy = (SbiOrgUnitHierarchies) aSession.load(SbiOrgUnitHierarchies.class, id);

			toReturn = toOrganizationalUnitHierarchy(hibHierarchy);
			
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	public OrganizationalUnitNode getRootNode(Integer hierarchyId) {
		logger.debug("IN: hierarchyId = " + hierarchyId);
		OrganizationalUnitNode toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.sbiOrgUnitNodes is null");
			hibQuery.setInteger(0, hierarchyId);
			
			SbiOrgUnitNodes root = (SbiOrgUnitNodes) hibQuery.uniqueResult();

			if (root != null) {
				toReturn = toOrganizationalUnitNode(root);
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public List<OrganizationalUnitNode> getChildrenNodes(Integer nodeId) {
		logger.debug("IN: nodeId = " + nodeId);
		List<OrganizationalUnitNode> toReturn = new ArrayList<OrganizationalUnitNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitNodes.nodeId = ? ");
			hibQuery.setInteger(0, nodeId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitNode((SbiOrgUnitNodes) it.next()));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	public List<OrganizationalUnitGrant> getGrantsList() {
		logger.debug("IN");
		List<OrganizationalUnitGrant> toReturn = new ArrayList<OrganizationalUnitGrant>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrant ");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitGrant((SbiOrgUnitGrant) it.next(), aSession));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	
	public List<OrganizationalUnitGrantNode> getNodeGrants(Integer nodeId, Integer grantId) {
		logger.debug("IN: nodeId = " + nodeId);
		List<OrganizationalUnitGrantNode> toReturn = new ArrayList<OrganizationalUnitGrantNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrantNodes s where s.id.nodeId = ? " +
					" and s.id.grantId = ?");
			hibQuery.setInteger(0, nodeId);
			hibQuery.setInteger(1, grantId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitGrantNode((SbiOrgUnitGrantNodes) it.next(), aSession));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	
	public void eraseOrganizationalUnit(Integer ouId) {
		logger.debug("IN: ouId = " + ouId);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiOrgUnit hibOU = (SbiOrgUnit) aSession.load(SbiOrgUnit.class, ouId);
			aSession.delete(hibOU);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnit removed successfully.");
	}

	public void insertOrganizationalUnit(OrganizationalUnit ou) {
		logger.debug("IN: ou = " + ou);
		if (ou.getLabel().contains(Tree.NODES_PATH_SEPARATOR)) 
			throw new SpagoBIRuntimeException("OrganizationalUnit label cannot contain " + Tree.NODES_PATH_SEPARATOR + " character");
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiOrgUnit hibOU = new SbiOrgUnit();
			hibOU.setLabel(ou.getLabel());
			hibOU.setName(ou.getName());
			hibOU.setDescription(ou.getDescription());

			aSession.save(hibOU);
			
			ou.setId(hibOU.getId());
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnit inserted successfully with id " + ou.getId());
	}
	
	public void modifyOrganizationalUnit(OrganizationalUnit ou) {
		logger.debug("IN: ou = " + ou);
		if (ou.getLabel().contains(Tree.NODES_PATH_SEPARATOR)) 
			throw new SpagoBIRuntimeException("OrganizationalUnit label cannot contain " + Tree.NODES_PATH_SEPARATOR + " character");
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiOrgUnit hibOU = (SbiOrgUnit) aSession.load(SbiOrgUnit.class, ou.getId());
			hibOU.setLabel(ou.getLabel());
			hibOU.setName(ou.getName());
			hibOU.setDescription(ou.getDescription());

			aSession.save(hibOU);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnit modified successfully");
	}

	public void eraseHierarchy(Integer hierarchyId) {
		logger.debug("IN: hierarchyId = " + hierarchyId);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitHierarchies hibHierarchy = (SbiOrgUnitHierarchies) aSession.load(SbiOrgUnitHierarchies.class, hierarchyId);
			aSession.delete(hibHierarchy);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: Hierarchy removed successfully");
		
	}

	public void insertHierarchy(OrganizationalUnitHierarchy h) {
		logger.debug("IN: h = " + h);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiOrgUnitHierarchies hibHierarchy = new SbiOrgUnitHierarchies();
			hibHierarchy.setLabel(h.getLabel());
			hibHierarchy.setName(h.getName());
			hibHierarchy.setDescription(h.getDescription());
			hibHierarchy.setTarget(h.getTarget());

			aSession.save(hibHierarchy);
			
			h.setId(hibHierarchy.getId());
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: Hierarchy inserted successfully with id " + h.getId());
		
	}
	
	public void modifyHierarchy(OrganizationalUnitHierarchy h) {
		logger.debug("IN: h = " + h);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			SbiOrgUnitHierarchies hibHierarchy = (SbiOrgUnitHierarchies) aSession.load(SbiOrgUnitHierarchies.class, h.getId());
			hibHierarchy.setLabel(h.getLabel());
			hibHierarchy.setName(h.getName());
			hibHierarchy.setDescription(h.getDescription());
			hibHierarchy.setTarget(h.getTarget());

			aSession.save(hibHierarchy);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: Hierarchy modified successfully");
		
	}

	public void eraseOrganizationalUnitNode(OrganizationalUnitNode node) {
		logger.debug("IN: node = " + node);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitNodes hibNode = (SbiOrgUnitNodes) aSession.load(SbiOrgUnitNodes.class, node.getNodeId());
			aSession.delete(hibNode);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: node removed successfully.");
	}

	public boolean existsNodeInHierarchy(String path, Integer hierarchyId) {
		logger.debug("IN: path = " + path + ", hierarchy = " + hierarchyId);
		boolean toReturn = false;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.path = ? ");
			hibQuery.setInteger(0, hierarchyId);
			hibQuery.setString(1, path);
			
			List hibList = hibQuery.list();
			toReturn = !hibList.isEmpty();
			
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	public OrganizationalUnitNode getOrganizationalUnitNode(String path, Integer hierarchyId) {
		logger.debug("IN: path = " + path + ", hierarchy = " + hierarchyId);
		OrganizationalUnitNode toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.path = ? ");
			hibQuery.setInteger(0, hierarchyId);
			hibQuery.setString(1, path);
			
			SbiOrgUnitNodes hibNode = (SbiOrgUnitNodes) hibQuery.uniqueResult();
			toReturn = toOrganizationalUnitNode(hibNode);
			
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public void insertOrganizationalUnitNode(OrganizationalUnitNode aNode) {
		logger.debug("IN: aNode = " + aNode);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitNodes hibNode = new SbiOrgUnitNodes();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitHierarchies s where s.id = ? ");
			hibQuery.setInteger(0, aNode.getHierarchy().getId());
			SbiOrgUnitHierarchies hierarchy = (SbiOrgUnitHierarchies) hibQuery.uniqueResult();
			hibNode.setSbiOrgUnitHierarchies(hierarchy);
			
			hibNode.setPath(aNode.getPath());
			
			hibQuery = aSession.createQuery(" from SbiOrgUnit s where s.id = ? ");
			hibQuery.setInteger(0, aNode.getOu().getId());
			SbiOrgUnit ou = (SbiOrgUnit) hibQuery.uniqueResult();
			hibNode.setSbiOrgUnit(ou);
			
			if (aNode.getParentNodeId() != null) {
				hibQuery = aSession.createQuery(" from SbiOrgUnitNodes s where s.nodeId = ? ");
				hibQuery.setInteger(0, aNode.getParentNodeId());
				SbiOrgUnitNodes parentNode = (SbiOrgUnitNodes) hibQuery.uniqueResult();
				hibNode.setSbiOrgUnitNodes(parentNode);
			}

			aSession.save(hibNode);
			
			aNode.setNodeId(hibNode.getNodeId());
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitNode inserted successfully with id " + aNode.getNodeId());
	}
	

	public void insertGrant(OrganizationalUnitGrant grant) {
		logger.debug("IN: grant = " + grant);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitGrant hibGrant = new SbiOrgUnitGrant();
			hibGrant.setLabel(grant.getLabel());
			hibGrant.setName(grant.getName());
			hibGrant.setDescription(grant.getDescription());
			hibGrant.setStartDate(grant.getStartDate());
			hibGrant.setEndDate(grant.getEndDate());
			
			// set hierarchy
			Integer hierachyId = grant.getHierarchy().getId();
			Query query = aSession.createQuery(" from SbiOrgUnitHierarchies s where s.id = ? ");
			query.setInteger(0, hierachyId);
			SbiOrgUnitHierarchies h = (SbiOrgUnitHierarchies) query.uniqueResult();
			hibGrant.setSbiOrgUnitHierarchies(h);
			
			// set kpi model instance
			Integer kpiModelInstId = grant.getModelInstance().getId();
			query = aSession.createQuery(" from SbiKpiModelInst s where s.kpiModelInst = ? ");
			query.setInteger(0, kpiModelInstId);
			SbiKpiModelInst s = (SbiKpiModelInst) query.uniqueResult();
			hibGrant.setSbiKpiModelInst(s);
			
			aSession.save(hibGrant);
			
			grant.setId(hibGrant.getId());
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitGrant inserted successfully with id " + grant.getId());
	}

	public void modifyGrant(OrganizationalUnitGrant grant) {
		logger.debug("IN: grant = " + grant);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitGrant hibGrant = (SbiOrgUnitGrant) aSession.load(SbiOrgUnitGrant.class, grant.getId());
			hibGrant.setLabel(grant.getLabel());
			hibGrant.setName(grant.getName());
			hibGrant.setDescription(grant.getDescription());
			hibGrant.setStartDate(grant.getStartDate());
			hibGrant.setEndDate(grant.getEndDate());
			
			// if hierarchy and/or kpi model instance have been changed, erase previous defined node grants
			Integer previousHierachyId = hibGrant.getSbiOrgUnitHierarchies().getId();
			Integer newHierachyId = grant.getHierarchy().getId();
			Integer previousKpiModelInstId = hibGrant.getSbiKpiModelInst().getKpiModelInst();
			Integer newKpiModelInstId = grant.getModelInstance().getId();
			if (previousHierachyId.intValue() != newHierachyId.intValue() 
					|| previousKpiModelInstId.intValue() != newKpiModelInstId.intValue()) {
				String hql = "delete from SbiOrgUnitGrantNodes s where s.id.grantId = ?";
		        Query query = aSession.createQuery(hql);
		        query.setInteger(0, hibGrant.getId());
		        query.executeUpdate();
			}
			
			// update hierarchy
			if (previousHierachyId.intValue() != newHierachyId.intValue()) {
				Query query = aSession.createQuery(" from SbiOrgUnitHierarchies s where s.id = ? ");
				query.setInteger(0, newHierachyId);
				SbiOrgUnitHierarchies h = (SbiOrgUnitHierarchies) query.uniqueResult();
				hibGrant.setSbiOrgUnitHierarchies(h);
			}
			
			// update kpi model instance
			if (previousKpiModelInstId.intValue() != newKpiModelInstId.intValue()) {
				Query query = aSession.createQuery(" from SbiKpiModelInst s where s.kpiModelInst = ? ");
				query.setInteger(0, newKpiModelInstId);
				SbiKpiModelInst s = (SbiKpiModelInst) query.uniqueResult();
				hibGrant.setSbiKpiModelInst(s);
			}
			
			aSession.save(hibGrant);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitGrant modified successfully.");
	}
	
	public void eraseNodeGrants(Integer grantId) {
		logger.debug("IN: grantId = " + grantId);
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			String hql = "delete from SbiOrgUnitGrantNodes s where s.id.grantId = ?";
			Query query = aSession.createQuery(hql);
			query.setInteger(0, grantId);
			query.executeUpdate();

			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitGrant modified successfully.");
	}

	public void eraseGrant(Integer grantId) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitGrant hibGrant = (SbiOrgUnitGrant) aSession.load(SbiOrgUnitGrant.class, grantId);
			aSession.delete(hibGrant);
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitGrant removed successfully.");
	}
	
	public void insertNodeGrants(List<OrganizationalUnitGrantNode> grantNodes) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Iterator<OrganizationalUnitGrantNode> it = grantNodes.iterator();
			while (it.hasNext()) {
				OrganizationalUnitGrantNode aGrantNode = it.next();
				Integer grantId = aGrantNode.getGrant().getId();
				Integer hierarchyNodeId = aGrantNode.getOuNode().getNodeId();
				Integer kpiModelInstNodeId = aGrantNode.getModelInstanceNode().getModelInstanceNodeId();
				
				SbiOrgUnitGrantNodes grantNode = new SbiOrgUnitGrantNodes();
			
				SbiOrgUnitGrantNodesId grantNodeId = new SbiOrgUnitGrantNodesId(hierarchyNodeId, kpiModelInstNodeId, grantId);
				grantNode.setId(grantNodeId);
				
				SbiOrgUnitNodes hibNode = (SbiOrgUnitNodes) aSession.load(SbiOrgUnitNodes.class, hierarchyNodeId);
				grantNode.setSbiOrgUnitNodes(hibNode);
				
				SbiKpiModelInst kpiModelInst = (SbiKpiModelInst) aSession.load(SbiKpiModelInst.class, kpiModelInstNodeId);
				grantNode.setSbiKpiModelInst(kpiModelInst);
				
				SbiOrgUnitGrant hibGrant = (SbiOrgUnitGrant) aSession.load(SbiOrgUnitGrant.class, grantId);
				grantNode.setSbiOrgUnitGrant(hibGrant);
				logger.debug("Saving grant node with node Id:"+grantNodeId.getNodeId()+" modelInst Id "+grantNodeId.getKpiModelInstNodeId()+" ang grant Id "+grantNodeId.getGrantId());
				System.out.println("Saving grant node with node Id:"+grantNodeId.getNodeId()+" modelInst Id "+grantNodeId.getKpiModelInstNodeId()+" ang grant Id "+grantNodeId.getGrantId());
				aSession.save(grantNode);
			}
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: List of OrganizationalUnitGrantNode inserted successfully.");
		
	}
	

	public OrganizationalUnitNodeWithGrant getRootNodeWithGrants(
			Integer hierarchyId, Integer grantId) {
		logger.debug("IN: hierarchyId = " + hierarchyId + ", grantId = " + grantId);
		OrganizationalUnitNodeWithGrant toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.sbiOrgUnitNodes is null");
			hibQuery.setInteger(0, hierarchyId);
			
			SbiOrgUnitNodes root = (SbiOrgUnitNodes) hibQuery.uniqueResult();

			if (root != null) {
				OrganizationalUnitNode node = toOrganizationalUnitNode(root);
				toReturn = getNodeWithGrants(node, grantId, aSession);
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	private OrganizationalUnitNodeWithGrant getNodeWithGrants(
			OrganizationalUnitNode node, Integer grantId, Session aSession) {
		logger.debug("IN");
		OrganizationalUnitNodeWithGrant toReturn = null;
		List<OrganizationalUnitGrantNode> grants = new ArrayList<OrganizationalUnitGrantNode>();
		Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrantNodes n where n.id.nodeId = ? " +
			" and n.id.grantId = ?");
		hibQuery.setInteger(0, node.getNodeId());
		hibQuery.setInteger(1, grantId);
		List hibList = hibQuery.list();
		Iterator it = hibList.iterator();
		while (it.hasNext()) {
			grants.add(toOrganizationalUnitGrantNode((SbiOrgUnitGrantNodes) it.next(), aSession));
		}
		toReturn = new OrganizationalUnitNodeWithGrant(node, grants);
		logger.debug("OUT");
		return toReturn;
	}
	
	public List<OrganizationalUnitNodeWithGrant> getChildrenNodesWithGrants(
			Integer nodeId, Integer grantId) {
		logger.debug("IN: nodeId = " + nodeId + ", grantId = " + grantId);
		List<OrganizationalUnitNodeWithGrant> toReturn = new ArrayList<OrganizationalUnitNodeWithGrant>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitNodes.nodeId = ? ");
			hibQuery.setInteger(0, nodeId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				OrganizationalUnitNode node = toOrganizationalUnitNode((SbiOrgUnitNodes) it.next());
				OrganizationalUnitNodeWithGrant nodeWithGrants = getNodeWithGrants(node, grantId, aSession);
				toReturn.add(nodeWithGrants);
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	
	public List<OrganizationalUnitGrantNode> getGrants(
			Integer kpiModelInstanceId) {
		logger.debug("IN: kpiModelInstanceId = " + kpiModelInstanceId);
		List<OrganizationalUnitGrantNode> toReturn = new ArrayList<OrganizationalUnitGrantNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrantNodes n where n.id.kpiModelInstNodeId = ? ");
			hibQuery.setInteger(0, kpiModelInstanceId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitGrantNode((SbiOrgUnitGrantNodes) it.next(), aSession));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	public List<OrganizationalUnitGrantNode> getGrantsValidByDate(
			Integer kpiModelInstanceId, Date now) {
		logger.debug("IN: kpiModelInstanceId = " + kpiModelInstanceId);
		List<OrganizationalUnitGrantNode> toReturn = new ArrayList<OrganizationalUnitGrantNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrantNodes n where n.id.kpiModelInstNodeId = ? and ? between n.sbiOrgUnitGrant.startDate and n.sbiOrgUnitGrant.endDate");
			hibQuery.setInteger(0, kpiModelInstanceId);
			hibQuery.setDate(1, now);
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitGrantNode((SbiOrgUnitGrantNodes) it.next(), aSession));
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	public OrganizationalUnitGrant toOrganizationalUnitGrant(
			SbiOrgUnitGrant hibGrant, Session aSession) {
		OrganizationalUnitHierarchy hierarchy = toOrganizationalUnitHierarchy(hibGrant.getSbiOrgUnitHierarchies());
		ModelInstance modelInstance = ModelInstanceDAOImpl.toModelInstanceWithoutChildren(hibGrant.getSbiKpiModelInst(), aSession);
		OrganizationalUnitGrant grant = new OrganizationalUnitGrant(hibGrant.getId(), modelInstance, 
				hierarchy, hibGrant.getStartDate(), hibGrant.getEndDate(), hibGrant.getLabel(), 
				hibGrant.getName(), hibGrant.getDescription());
		return grant;
	}

	public OrganizationalUnit toOrganizationalUnit(SbiOrgUnit hibOrgUnit){
		OrganizationalUnit ou = new OrganizationalUnit(hibOrgUnit.getId(), hibOrgUnit.getLabel(), 
				hibOrgUnit.getName(), hibOrgUnit.getDescription());
		return ou;
	}
	
	public OrganizationalUnitHierarchy toOrganizationalUnitHierarchy(SbiOrgUnitHierarchies hibOrgUnitHierarchies){
		OrganizationalUnitHierarchy hierarchy = new OrganizationalUnitHierarchy(hibOrgUnitHierarchies.getId(), 
				hibOrgUnitHierarchies.getLabel(), hibOrgUnitHierarchies.getName(), hibOrgUnitHierarchies.getDescription(), 
				hibOrgUnitHierarchies.getTarget(), hibOrgUnitHierarchies.getCompany());
		return hierarchy;
	}
	
	public OrganizationalUnitNode toOrganizationalUnitNode(SbiOrgUnitNodes hibOrgUnitNode) {
		OrganizationalUnit ou = toOrganizationalUnit(hibOrgUnitNode.getSbiOrgUnit());
		OrganizationalUnitHierarchy hierarchy = toOrganizationalUnitHierarchy(hibOrgUnitNode.getSbiOrgUnitHierarchies());
		OrganizationalUnitNode node = new OrganizationalUnitNode(hibOrgUnitNode.getNodeId(), ou, hierarchy, 
				hibOrgUnitNode.getPath(), 
				hibOrgUnitNode.getSbiOrgUnitNodes() == null ? null : hibOrgUnitNode.getSbiOrgUnitNodes().getNodeId() );

		Session aSession = null;
		Transaction tx = null;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitNodes.nodeId = ? ");
			hibQuery.setInteger(0, node.getNodeId());
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			node.setLeaf(!it.hasNext());

		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		
		return node;
	}
	
	
	public OrganizationalUnitGrantNode toOrganizationalUnitGrantNode(
			SbiOrgUnitGrantNodes hibGrantNode, Session aSession) {
		OrganizationalUnitNode ouNode = toOrganizationalUnitNode(hibGrantNode.getSbiOrgUnitNodes());
		ModelInstanceNode modelInstanceNode;
		try {
			modelInstanceNode = ModelInstanceDAOImpl.toModelInstanceNode(hibGrantNode.getSbiKpiModelInst());
		} catch (EMFUserError e) {
			throw new RuntimeException(e);
		}
		OrganizationalUnitGrant grant = toOrganizationalUnitGrant(hibGrantNode.getSbiOrgUnitGrant(), aSession);
		OrganizationalUnitGrantNode grantNode = new OrganizationalUnitGrantNode(ouNode, modelInstanceNode, grant);
		return grantNode;
	}

	public OrganizationalUnit getOrganizationalUnitByLabel(String label) {
		logger.debug("IN: label = " + label);
		OrganizationalUnit toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Query hibQuery = aSession.createQuery(" from SbiOrgUnit o where o.label = ?");
			hibQuery.setString(0, label);
			SbiOrgUnit hibOU = (SbiOrgUnit)hibQuery.uniqueResult();

			toReturn = toOrganizationalUnit(hibOU);
			
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public List<OrganizationalUnitNode> getNodes() {
		logger.debug("IN");
		List<OrganizationalUnitNode> toReturn = new ArrayList<OrganizationalUnitNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes ");
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				OrganizationalUnitNode node = toOrganizationalUnitNode((SbiOrgUnitNodes) it.next());
				toReturn.add(node);
			}
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	public void eraseNodeGrant(OrganizationalUnitGrantNode grantNode) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			String hql = "delete from SbiOrgUnitGrantNodes s where s.id.grantId = ? and s.id.nodeId = ? and s.id.kpiModelInstNodeId = ? ";
			Query query = aSession.createQuery(hql);
			query.setInteger(0, grantNode.getGrant().getId());
			query.setInteger(1, grantNode.getOuNode().getNodeId());
			query.setInteger(2, grantNode.getModelInstanceNode().getModelInstanceNodeId());
			query.executeUpdate();
			
			tx.commit();
		} finally {
			rollbackIfActiveAndClose(tx, aSession);
		}
		logger.debug("OUT: OrganizationalUnitGrantNode deleted successfully.");
	}



}
