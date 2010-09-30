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
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnit;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodes;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodesId;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitHierarchies;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitNodes;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.tree.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	public List<OrganizationalUnitNode> getRootNodes(Integer hierarchyId) {
		logger.debug("IN");
		List<OrganizationalUnitNode> toReturn = new ArrayList<OrganizationalUnitNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					"and n.sbiOrgUnitNodes is null");
			hibQuery.setInteger(0, hierarchyId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitNode((SbiOrgUnitNodes) it.next()));
			}
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	public List<OrganizationalUnitNode> getChildrenNodes(Integer hierarchyId, Integer nodeId) {
		logger.debug("IN");
		List<OrganizationalUnitNode> toReturn = new ArrayList<OrganizationalUnitNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					"and n.sbiOrgUnitNodes.nodeId = ?");
			hibQuery.setInteger(0, hierarchyId);
			hibQuery.setInteger(1, nodeId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitNode((SbiOrgUnitNodes) it.next()));
			}
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	
	public List<OrganizationalUnitGrantNode> getNodeGrants(Integer nodeId) {
		logger.debug("IN");
		List<OrganizationalUnitGrantNode> toReturn = new ArrayList<OrganizationalUnitGrantNode>();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitGrantNodes s where s.sbiOrgUnitNodes.nodeId = ? ");
			hibQuery.setInteger(0, nodeId);
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				toReturn.add(toOrganizationalUnitGrantNode((SbiOrgUnitGrantNodes) it.next(), aSession));
			}
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	
	public void eraseOrganizationalUnit(OrganizationalUnit ou) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			// deletes nodes (and their children) on hierarchies' structures
			/*
			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes s where s.sbiOrgUnit.id = ? ");
			hibQuery.setInteger(0, ou.getId());
			
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				SbiOrgUnitNodes aNode = (SbiOrgUnitNodes) it.next();
				removeChildren(aNode, aSession);
				aSession.delete(aNode);
			}
			*/
			
			// deletes ou from list
			SbiOrgUnit hibOU = (SbiOrgUnit) aSession.load(SbiOrgUnit.class, ou.getId());
			aSession.delete(hibOU);
			
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}

	/*
	private void removeChildren(SbiOrgUnitNodes aNode, Session aSession) {
		String hql = "delete from SbiOrgUnitNodes where path like :path";
        Query query = aSession.createQuery(hql);
        query.setString("path", aNode.getPath() + NODES_PATH_SEPARATOR + "%");
        query.executeUpdate();
	}
	*/

	public void insertOrganizationalUnit(OrganizationalUnit ou) {
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}
	
	public void modifyOrganizationalUnit(OrganizationalUnit ou) {
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}

	public void eraseHierarchy(OrganizationalUnitHierarchy h) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitHierarchies hibHierarchy = (SbiOrgUnitHierarchies) aSession.load(SbiOrgUnitHierarchies.class, h.getId());
			aSession.delete(hibHierarchy);
			
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		
	}

	public void insertHierarchy(OrganizationalUnitHierarchy h) {
		logger.debug("IN");
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		
	}
	
	public void modifyHierarchy(OrganizationalUnitHierarchy h) {
		logger.debug("IN");
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		
	}

	public void eraseOrganizationalUnitNode(OrganizationalUnitNode node) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitNodes hibNode = (SbiOrgUnitNodes) aSession.load(SbiOrgUnitNodes.class, node.getNodeId());
			aSession.delete(hibNode);
			
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}

	public boolean existsNodeInHierarchy(String path, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		boolean toReturn = false;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.path = ? ");
			hibQuery.setInteger(0, hierarchy.getId());
			hibQuery.setString(1, path);
			
			List hibList = hibQuery.list();
			toReturn = !hibList.isEmpty();
			
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	public OrganizationalUnitNode getOrganizationalUnitNode(String path, OrganizationalUnitHierarchy hierarchy) {
		logger.debug("IN");
		OrganizationalUnitNode toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiOrgUnitNodes n where n.sbiOrgUnitHierarchies.id = ? " +
					" and n.path = ? ");
			hibQuery.setInteger(0, hierarchy.getId());
			hibQuery.setString(1, path);
			
			SbiOrgUnitNodes hibNode = (SbiOrgUnitNodes) hibQuery.uniqueResult();
			toReturn = toOrganizationalUnitNode(hibNode);
			
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	public void insertOrganizationalUnitNode(OrganizationalUnitNode aNode) {
		logger.debug("IN");
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		
	}
	

	public void insertGrant(OrganizationalUnitGrant grant) {
		logger.debug("IN");
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}

	public void modifyGrant(OrganizationalUnitGrant grant) {
		logger.debug("IN");
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
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
	}

	public void eraseGrant(OrganizationalUnitGrant grant) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiOrgUnitGrant hibGrant = (SbiOrgUnitGrant) aSession.load(SbiOrgUnitGrant.class, grant.getId());
			aSession.delete(hibGrant);
			
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
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
				
				SbiOrgUnitGrantNodesId grantNodeId = new SbiOrgUnitGrantNodesId(hierarchyNodeId, kpiModelInstNodeId, grantId);
				SbiOrgUnitGrantNodes grantNode = new SbiOrgUnitGrantNodes(grantNodeId, null, null, null);
				aSession.save(grantNode);
			}
			
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (aSession != null && aSession.isOpen()) {
				aSession.close();
			}
			logger.debug("OUT");
		}
		
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
				hibOrgUnitHierarchies.getTarget());
		return hierarchy;
	}
	
	public OrganizationalUnitNode toOrganizationalUnitNode(SbiOrgUnitNodes hibOrgUnitNode) {
		OrganizationalUnit ou = toOrganizationalUnit(hibOrgUnitNode.getSbiOrgUnit());
		OrganizationalUnitHierarchy hierarchy = toOrganizationalUnitHierarchy(hibOrgUnitNode.getSbiOrgUnitHierarchies());
		OrganizationalUnitNode node = new OrganizationalUnitNode(hibOrgUnitNode.getNodeId(), ou, hierarchy, 
				hibOrgUnitNode.getPath(), 
				hibOrgUnitNode.getSbiOrgUnitNodes() == null ? null : hibOrgUnitNode.getSbiOrgUnitNodes().getNodeId() );
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

}
