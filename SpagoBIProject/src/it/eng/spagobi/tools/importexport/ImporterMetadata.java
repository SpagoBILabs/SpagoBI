/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.importexport;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFunc;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjPar;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreports;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRole;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFunctions;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParview;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParameters;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCk;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDet;
import it.eng.spagobi.behaviouralmodel.check.metadata.SbiChecks;
import it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.config.metadata.SbiKpi;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstPeriod;
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.kpi.config.metadata.SbiKpiRel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelInst;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelResources;
import it.eng.spagobi.kpi.model.metadata.SbiResources;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnit;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrantNodes;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitHierarchies;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitNodes;
import it.eng.spagobi.kpi.threshold.metadata.SbiThreshold;
import it.eng.spagobi.kpi.threshold.metadata.SbiThresholdValue;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMaps;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetacontents;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetadata;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements methods to gather information from exported database and to make
 * some checks into the current SpagoBI database
 */
public class ImporterMetadata {

	static private Logger logger = Logger.getLogger(ImporterMetadata.class);

	/**
	 * Get the list of exported hibernate role objects.
	 * 
	 * @param session  Hiberante session for the exported database
	 * 
	 * @return         The list of exported hibernate roles
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getAllExportedRoles(Session session) throws EMFUserError {
		logger.debug("IN");
		List roles = new ArrayList();
		try {
			Query hibQuery = session.createQuery(" from SbiExtRoles");
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();
			while (it.hasNext()) {
				SbiExtRoles hibRole = (SbiExtRoles) it.next();
				Role role = new Role();
				role.setCode(hibRole.getCode());
				role.setDescription(hibRole.getDescr());
				role.setId(hibRole.getExtRoleId());
				role.setName(hibRole.getName());
				role.setRoleTypeCD(hibRole.getRoleTypeCode());
				role.setRoleTypeID(hibRole.getRoleType().getValueId());
				roles.add(role);
			}
		} catch (HibernateException he) {
			logger.error("Error while getting exported roles ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
		return roles;
	}

	/**
	 * Get the list of exported hibernate engine objects.
	 * 
	 * @param session  Hibernate session for the exported database
	 * 
	 * @return         The list of exported hibernate engines
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getAllExportedEngines(Session session) throws EMFUserError {
		logger.debug("IN");
		List engines = new ArrayList();
		try {
			Query hibQuery = session.createQuery(" from SbiEngines");
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();
			while (it.hasNext()) {
				SbiEngines hibEngine = (SbiEngines) it.next();
				Engine eng = new Engine();
				eng.setCriptable(new Integer(hibEngine.getEncrypt().intValue()));
				eng.setDescription(hibEngine.getDescr());
				eng.setDirUpload(hibEngine.getObjUplDir());
				eng.setDirUsable(hibEngine.getObjUseDir());
				eng.setDriverName(hibEngine.getDriverNm());
				eng.setId(hibEngine.getEngineId());
				eng.setName(hibEngine.getName());
				eng.setLabel(hibEngine.getLabel());
				eng.setSecondaryUrl(hibEngine.getSecnUrl());
				eng.setUrl(hibEngine.getMainUrl());
				eng.setLabel(hibEngine.getLabel());
				eng.setClassName(hibEngine.getClassNm());
				engines.add(eng);
			}
		} catch (HibernateException he) {
			logger.error("Error while getting exported engine ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
		return engines;
	}

	/**
	 * Get the list of exported hibernate objects.
	 * 
	 * @param session  Hibernate session for the exported database
	 * @param table    The name of the table corresponding to the hibernate
	 * objects to gather
	 * 
	 * @return The list of exported hibernate objects
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List getAllExportedSbiObjects(Session session, String table, String orderByField) throws EMFUserError {
		logger.debug("IN");
		List hibList = null;
		String hql = " from " + table + " t";
		if (orderByField != null) {
			hql += " order by t." + orderByField + " asc";
		}
		try {
			Query hibQuery = session.createQuery(hql);
			hibList = hibQuery.list();
		} catch (HibernateException he) {
			logger.error("Error while getting exported sbi objects ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
		return hibList;
	}

	public List getFilteredExportedSbiObjects(Session session, String table, String fieldName, Object fieldValue) throws EMFUserError {
		logger.debug("IN");
		List hibList = null;
		String hql = " from " + table + " t";
		if (fieldName != null && fieldValue != null) {
			if (fieldValue instanceof String) {
				hql += " where t." + fieldName + " = '" + fieldValue + "'";
			} else {
				hql += " where t." + fieldName + " = " + fieldValue.toString();
			}
		}
		try {
			Query hibQuery = session.createQuery(hql);
			hibList = hibQuery.list();
		} catch (HibernateException he) {
			logger.error("Error while getting exported sbi objects ", he);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
		return hibList;
	}

	/**
	 * Get an existing object identified by the id and the class.
	 * 
	 * @param id        The Object id
	 * @param objClass  The class of the object
	 * @param session   Hibernate session for a database
	 * @param tx the tx
	 * 
	 * @return          The existing hibernate object
	 */
	public Object getObject(Integer id, Class objClass, Transaction tx, Session session) {
		logger.debug("IN");
		Object hibObject = null;
		try {
			hibObject = session.load(objClass, id);
		} catch (HibernateException he) {
			logger.error("Error while getting the existing object with class " + objClass.getName() + " " + "and id "
					+ id + " \n ", he);
		} finally {
			logger.debug("OUT");
		}
		return hibObject;
	}

	/**
	 * Upadates the data source name into the query lov objects based on the
	 * association defined by the user between exported and current SpagoBI
	 * data source.
	 * 
	 * @param associations    Map of associations between exported data sources and
	 * data sources of the current SpagoBI platform
	 * @param session         Hibernate session for the exported database
	 * @param log the log
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public void updateDSRefs(Map associations,  Session session, MetadataLogger log)
	throws EMFUserError {
		logger.debug("IN");
		SbiLov lov = null;
		try {
			List lovs = getAllExportedSbiObjects(session, "SbiLov", null);
			Iterator iterLovs = lovs.iterator();
			while (iterLovs.hasNext()) {
				lov = (SbiLov) iterLovs.next();
				if (lov.getInputTypeCd().equalsIgnoreCase("QUERY")) {
					String lovProv = lov.getLovProvider();
					QueryDetail qDet = QueryDetail.fromXML(lovProv);
					String oldDataSource = qDet.getDataSource();
					String assDataSource = (String) associations.get(oldDataSource);

					// register user association
					if ((assDataSource != null) && !assDataSource.trim().equals("") && (oldDataSource != null)
							&& !oldDataSource.trim().equals("")) {

						qDet.setDataSource(assDataSource);
						lovProv = qDet.toXML();
						lov.setLovProvider(lovProv);
						session.save(lov);
						log.log("Changed the data source label from " + oldDataSource + " to " + assDataSource
								+ " for the lov " + lov.getName());
					}
				}
			}
		} catch (Exception e) {
			if (lov != null) {
				logger.error("Error while updating connection reference for exported lov with label [" + lov.getLabel() + "].", e);
			}
			logger.error("Error while updating connection references ", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8004", "component_impexp_messages");
		} finally {
			logger.debug("OUT");
		}
	}


	/**
	 * Check the existance of an object, based on his unique constraints, into
	 * the current SpagoBI database.
	 * 
	 * @param unique          The object which contains the unique constraints for the
	 * object
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistence(Object unique, Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;
		if (hibObj instanceof SbiParameters) {
			String label = (String) unique;
			hql = "from SbiParameters sp where sp.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiParameters hibPar = (SbiParameters) hqlQuery.uniqueResult();
			return hibPar;
		} else if (hibObj instanceof SbiExtRoles) {
			String roleName = (String) unique;
			hql = "from SbiExtRoles er where er.name = '" + roleName + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiExtRoles hibRole = (SbiExtRoles) hqlQuery.uniqueResult();
			return hibRole;
		} else if (hibObj instanceof SbiObjects) {
			String label = (String) unique;
			hql = "from SbiObjects obj where obj.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjects hibBIObj = (SbiObjects) hqlQuery.uniqueResult();
			return hibBIObj;
		} else if (hibObj instanceof SbiLov) {
			String label = (String) unique;
			hql = "from SbiLov lov where lov.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiLov hibLov = (SbiLov) hqlQuery.uniqueResult();
			return hibLov;
		} else if (hibObj instanceof SbiFunctions) {
			String code = (String) unique;
			hql = "from SbiFunctions f where f.code = '" + code + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiFunctions hibFunct = (SbiFunctions) hqlQuery.uniqueResult();
			return hibFunct;
		} else if (hibObj instanceof SbiEngines) {
			String label = (String) unique;
			hql = "from SbiEngines eng where eng.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiEngines hibEng = (SbiEngines) hqlQuery.uniqueResult();
			return hibEng;
		} else if (hibObj instanceof SbiChecks) {
			String label = (String) unique;
			hql = "from SbiChecks ch where ch.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiChecks hibCheck = (SbiChecks) hqlQuery.uniqueResult();
			return hibCheck;
		} else if (hibObj instanceof SbiParuse) {
			Map uniqueMap = (Map) unique;
			String label = (String) uniqueMap.get("label");
			Integer parid = (Integer) uniqueMap.get("idpar");
			hql = "from SbiParuse pu where pu.label='" + label + "' and pu.sbiParameters.parId = " + parid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiParuse hibParuse = (SbiParuse) hqlQuery.uniqueResult();
			return hibParuse;
		} 
		else if (hibObj instanceof SbiFuncRole) {
			Map uniqueMap = (Map) unique;
			Integer stateid = (Integer) uniqueMap.get("stateid");
			Integer roleid = (Integer) uniqueMap.get("roleid");
			Integer functionid = (Integer) uniqueMap.get("functionid");
			hql = "from SbiFuncRole fr where fr.id.state.valueId=" + stateid + " and fr.id.role.extRoleId = " + roleid
			+ " and fr.id.function.functId = " + functionid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiFuncRole hibFunctRole = (SbiFuncRole) hqlQuery.uniqueResult();
			return hibFunctRole;
		} else if (hibObj instanceof SbiParuseDet) {
			Map uniqueMap = (Map) unique;
			Integer paruseid = (Integer) uniqueMap.get("paruseid");
			Integer roleid = (Integer) uniqueMap.get("roleid");
			hql = "from SbiParuseDet pud where pud.id.sbiExtRoles.extRoleId = " + roleid
			+ " and pud.id.sbiParuse.useId = " + paruseid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiParuseDet hibParuseDet = (SbiParuseDet) hqlQuery.uniqueResult();
			return hibParuseDet;
		} else if (hibObj instanceof SbiParuseCk) {
			Map uniqueMap = (Map) unique;
			Integer paruseid = (Integer) uniqueMap.get("paruseid");
			Integer checkid = (Integer) uniqueMap.get("checkid");
			hql = "from SbiParuseCk puc where puc.id.sbiChecks.checkId = " + checkid + " and puc.id.sbiParuse.useId = "
			+ paruseid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiParuseCk hibParuseCk = (SbiParuseCk) hqlQuery.uniqueResult();
			return hibParuseCk;
		} else if (hibObj instanceof SbiObjPar) {
			Map uniqueMap = (Map) unique;
			Integer paramid = (Integer) uniqueMap.get("paramid");
			Integer biobjid = (Integer) uniqueMap.get("biobjid");
			String urlname = (String) uniqueMap.get("urlname");
			hql = "from SbiObjPar op where op.sbiParameter.parId = " + paramid + " and op.sbiObject.biobjId = "
			+ biobjid + " and op.parurlNm = '" + urlname + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjPar hibObjPar = (SbiObjPar) hqlQuery.uniqueResult();
			return hibObjPar;
		} else if (hibObj instanceof SbiDomains) {
			Map uniqueMap = (Map) unique;
			String valuecd = (String) uniqueMap.get("valuecd");
			String domaincd = (String) uniqueMap.get("domaincd");
			hql = "from SbiDomains dom where dom.valueCd='" + valuecd + "' and dom.domainCd='" + domaincd + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiDomains hibDom = (SbiDomains) hqlQuery.uniqueResult();
			return hibDom;
		} else if (hibObj instanceof SbiObjFunc) {
			Map uniqueMap = (Map) unique;
			Integer objid = (Integer) uniqueMap.get("objectid");
			Integer functionid = (Integer) uniqueMap.get("functionid");
			hql = "from SbiObjFunc objfun where objfun.id.sbiObjects.biobjId = " + objid
			+ " and objfun.id.sbiFunctions.functId = " + functionid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjFunc hibObjFunct = (SbiObjFunc) hqlQuery.uniqueResult();
			return hibObjFunct;
		} else if (hibObj instanceof SbiSubreports) {
			Map uniqueMap = (Map) unique;
			Integer masterid = (Integer) uniqueMap.get("masterid");
			Integer subid = (Integer) uniqueMap.get("subid");
			hql = "from SbiSubreports subreport where subreport.id.masterReport.biobjId = " + masterid
			+ " and subreport.id.subReport.biobjId = " + subid;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiSubreports hibSubRep = (SbiSubreports) hqlQuery.uniqueResult();
			return hibSubRep;
		} else if (hibObj instanceof SbiObjParuse) {
			Map uniqueMap = (Map) unique;
			Integer objparid = (Integer) uniqueMap.get("objparid");
			Integer paruseid = (Integer) uniqueMap.get("paruseid");
			Integer objparfathid = (Integer) uniqueMap.get("objparfathid");
			String filterOp = (String) uniqueMap.get("filterop");
			hql = "from SbiObjParuse objparuse where objparuse.id.sbiObjPar.objParId = " + objparid
			+ " and objparuse.id.sbiParuse.useId = " + paruseid
			+ " and objparuse.id.sbiObjParFather.objParId = " + objparfathid
			+ " and objparuse.id.filterOperation = '" + filterOp + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjParuse hibObjParUse = (SbiObjParuse) hqlQuery.uniqueResult();
			return hibObjParUse;
		} else if (hibObj instanceof SbiObjParview) {
			Map uniqueMap = (Map) unique;
			Integer objparid = (Integer) uniqueMap.get("objparid");
			Integer objparfathid = (Integer) uniqueMap.get("objparfathid");
			String operation = (String) uniqueMap.get("operation");
			String compareValue = (String) uniqueMap.get("compareValue");
			hql = "from SbiObjParview objparview where objparview.id.sbiObjPar.objParId = " + objparid
			+ " and objparview.id.sbiObjParFather.objParId = " + objparfathid
			+ " and objparview.id.compareValue = '" + compareValue + "'"
			+ " and objparview.id.operation = '" + operation + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjParview hibObjParview = (SbiObjParview) hqlQuery.uniqueResult();
			return hibObjParview;
		} 
		else if (hibObj instanceof SbiDataSetConfig) {
			String label = (String) unique;
			hql = "from SbiDataSetConfig ds where ds.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiDataSetConfig hibDs = (SbiDataSetConfig) hqlQuery.uniqueResult();
			return hibDs;
		} else if (hibObj instanceof SbiDataSource) {
			String label = (String) unique;
			hql = "from SbiDataSource ds where ds.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiDataSource hibDs = (SbiDataSource) hqlQuery.uniqueResult();
			return hibDs;
		} else if (hibObj instanceof SbiGeoMaps) {
			String name = (String) unique;
			hql = "from SbiGeoMaps where name = '" + name + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiGeoMaps hibMap = (SbiGeoMaps) hqlQuery.uniqueResult();
			return hibMap;
		} else if (hibObj instanceof SbiGeoFeatures) {
			String name = (String) unique;
			hql = "from SbiGeoFeatures where name = '" + name + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiGeoFeatures hibMap = (SbiGeoFeatures) hqlQuery.uniqueResult();
			return hibMap;
		} else if (hibObj instanceof SbiGeoMapFeatures) {
			Map uniqueMap = (Map) unique;
			Integer mapId = (Integer) uniqueMap.get("mapId");
			Integer featureId = (Integer) uniqueMap.get("featureId");
			hql = "from SbiGeoMapFeatures where id.mapId = " + mapId + " and id.featureId = " + featureId;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiGeoMapFeatures hibMapFeature = (SbiGeoMapFeatures) hqlQuery.uniqueResult();
			return hibMapFeature;
		} else if (hibObj instanceof it.eng.spagobi.kpi.threshold.metadata.SbiThreshold) {
			String label = (String) unique;
			hql = "from SbiThreshold ds where ds.code = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiThreshold hibDs = null;
			try{
				hibDs = (SbiThreshold) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9001", "component_impexp_messages");
			}
			return hibDs;
		}
		/*else if (hibObj instanceof SbiThresholdValue) {
			String label = (String) unique;
			hql = "from SbiThresholdValue ds where ds.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiThresholdValue hibDs = (SbiThresholdValue) hqlQuery.uniqueResult();
			return hibDs;
		} */	else if (hibObj instanceof SbiKpi) {
			String label = (String) unique;
			hql = "from SbiKpi ds where ds.code = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiKpi hibDs =null;
			try{
				hibDs= (SbiKpi) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9002", "component_impexp_messages");
			}
			return hibDs;
		} 	else if (hibObj instanceof SbiKpiModel) {
			// if unique == null means we are importing form a SpagoBI version < 2.4
			if(unique == null) return null;
			String label = (String) unique;
			hql = "from SbiKpiModel ds where ds.kpiModelLabel = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiKpiModel hibDs =null;
			try{
				hibDs=(SbiKpiModel) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9003", "component_impexp_messages");
			}
			return hibDs;
		} 	else if (hibObj instanceof SbiKpiModelInst) {
			String label = (String) unique;
			hql = "from SbiKpiModelInst ds where ds.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiKpiModelInst hibDs = null;
			try{
				hibDs=(SbiKpiModelInst) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9004", "component_impexp_messages");
			}
			return hibDs;
		} 	else if (hibObj instanceof SbiResources) {
			String label = (String) unique;
			hql = "from SbiResources ds where ds.resourceCode = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiResources hibDs = null;
			try{
				hibDs=(SbiResources) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9005", "component_impexp_messages");
			}
			return hibDs;
		} 	else if (hibObj instanceof SbiKpiPeriodicity) {
			String label = (String) unique;
			hql = "from SbiKpiPeriodicity ds where ds.name = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiKpiPeriodicity hibDs = null;
			try{
				hibDs=(SbiKpiPeriodicity) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9006", "component_impexp_messages");
			}
			return hibDs;
		}  	else if (hibObj instanceof SbiAlarm) {
			String label = (String) unique;
			hql = "from SbiAlarm ds where ds.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiAlarm hibDs = null;
			try{
				hibDs=(SbiAlarm) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9007", "component_impexp_messages");
			}
			return hibDs;
		}  	else if (hibObj instanceof SbiAlarmContact) {
			String label = (String) unique;
			hql = "from SbiAlarmContact ds where ds.name = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiAlarmContact hibDs = null;
			try{
				hibDs=(SbiAlarmContact) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9008", "component_impexp_messages");
			}
			return hibDs;
		}
		else if (hibObj instanceof SbiObjMetadata) {
			String metaName = (String) unique;
			hql = "from SbiObjMetadata er where er.label = '" + metaName + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiObjMetadata hibMeta = (SbiObjMetadata) hqlQuery.uniqueResult();
			return hibMeta;		
		}else if (hibObj instanceof SbiKpiRel) {
			Map uniqueMap = (Map) unique;
			Integer fatherId = (Integer) uniqueMap.get("fatherId");
			Integer childId = (Integer) uniqueMap.get("childId");
			String parameter = (String) uniqueMap.get("parameter");
			hql = "from SbiKpiRel kr where kr.sbiKpiByKpiChildId.kpiId=" + childId + " and kr.sbiKpiByKpiFatherId.kpiId = " + fatherId
			+ " and kr.parameter = '" + parameter+ "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiKpiRel hibKpiRel = null;
			try{
				hibKpiRel = (SbiKpiRel) hqlQuery.uniqueResult();
			}
			catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.ERROR, "9010", "component_impexp_messages");
			}
			return hibKpiRel;	
		}else if (hibObj instanceof SbiUdpValue) {
			Map uniqueMap = (Map) unique;
			String family = (String) uniqueMap.get("family");
			Integer udpId = (Integer) uniqueMap.get("udpId");
			Integer referenceId = (Integer) uniqueMap.get("referenceId");
			hql = "from SbiUdpValue u where u.sbiUdp.udpId = " + udpId + " and u.referenceId ="+referenceId +" and u.family ='"+family+"'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			List<SbiUdpValue> udpValues = (List<SbiUdpValue>)hqlQuery.list();
			SbiUdpValue hibUdpVal = null;
			//TODO to control why there is a list of udpValues and not only one
			if(udpValues!=null && !udpValues.isEmpty()){
				hibUdpVal = udpValues.get(0);
			}
			return hibUdpVal;		
		}else if (hibObj instanceof SbiUdp) {
			//logical unique key but table just looks for label
/*			Map uniqueMap = (Map) unique;
			Integer typeId = (Integer) uniqueMap.get("typeId");
			Integer familyId = (Integer) uniqueMap.get("familyId");
			String label = (String) uniqueMap.get("label");
			hql = "from SbiUdp u where u.label = '" + label + "' and u.typeId ="+typeId+" and u.familyId = "+familyId;*/
						
			String label = (String) unique;
			hql = "from SbiUdp u where u.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiUdp hibUdp = (SbiUdp) hqlQuery.uniqueResult();
			return hibUdp;		
		}else if (hibObj instanceof SbiOrgUnit) {
			//checks existence in import db
			Map uniqueMap = (Map) unique;
			String label = (String) uniqueMap.get("label");
			String name = (String) uniqueMap.get("name");
			
			hql = "from SbiOrgUnit u where u.label = '" + label  + "' and u.name = ?";
			hqlQuery = sessionCurrDB.createQuery(hql);
			hqlQuery.setString(0, name);

			SbiOrgUnit hibOu = (SbiOrgUnit) hqlQuery.uniqueResult();
			return hibOu;		
		}else if (hibObj instanceof SbiOrgUnitHierarchies) {

			Map uniqueMap = (Map) unique;
			String label = (String) uniqueMap.get("label");
			String company = (String) uniqueMap.get("company");
			
			hql = "from SbiOrgUnitHierarchies h where h.label = '" + label + "' and h.company = '"+company+"'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiOrgUnitHierarchies hibHier = (SbiOrgUnitHierarchies) hqlQuery.uniqueResult();
			return hibHier;		
		}else if (hibObj instanceof SbiOrgUnitNodes) {
			Map uniqueMap = (Map) unique;
			Integer ouId = (Integer) uniqueMap.get("ouId");
			Integer hierarchyId = (Integer) uniqueMap.get("hierarchyId");
			hql = "from SbiOrgUnitNodes n where n.sbiOrgUnit.id = " + ouId + " and n.sbiOrgUnitHierarchies.id = "+hierarchyId;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiOrgUnitNodes hibnode = (SbiOrgUnitNodes) hqlQuery.uniqueResult();
			return hibnode;		
		}else if (hibObj instanceof SbiOrgUnitGrant) {
			String label = (String) unique;
			hql = "from SbiOrgUnitGrant u where u.label = '" + label + "'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiOrgUnitGrant hibOu = (SbiOrgUnitGrant) hqlQuery.uniqueResult();
			return hibOu;		
		}else if (hibObj instanceof SbiOrgUnitGrantNodes) {
			Map uniqueMap = (Map) unique;
			Integer nodeId = (Integer) uniqueMap.get("nodeId");
			Integer grantId = (Integer) uniqueMap.get("grantId");
			Integer modelInstId = (Integer) uniqueMap.get("modelInstId");
			hql = "from SbiOrgUnitGrantNodes n where n.id.nodeId = " + nodeId + " and n.id.kpiModelInstNodeId = "+modelInstId+" and n.id.grantId = "+grantId;
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiOrgUnitGrantNodes hibOu = (SbiOrgUnitGrantNodes) hqlQuery.uniqueResult();
			return hibOu;		
		}


		logger.debug("OUT");
		return null;
	}


	/**
	 * Check the existance of a domain, based on his unique constraints, into
	 * the current SpagoBI database. Requires two labels
	 * 
	 * @param unique          The object which contains the unique constraints for the
	 * object
	 * @param domainCd          domain code
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceDomain(String valueCd, String domainCd,Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;
		if (hibObj instanceof SbiDomains) {
			hql = "from SbiDomains where valueCd = '" + valueCd + "'"+"AND domainCd = '"+domainCd+"'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			SbiDomains hibDs = (SbiDomains) hqlQuery.uniqueResult();
			return hibDs;
		} 	
		logger.debug("OUT");
		return null;
	}




	/**
	 * Check the existance of a ThresholdValue, based on his unique constraints, into
	 * the current SpagoBI database. Requires two labels
	 * 
	 * @param unique          The object which contains the unique constraints for the
	 * object
	 * @param domainCd          domain code
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceThresholdValue(String labelThValue, String thresholdId,Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;
		hql = "from SbiThresholdValue where label = '" + labelThValue + "'"+"AND sbiThreshold = '"+thresholdId+"'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiThresholdValue hibDs=null;
		try{
			hibDs = (SbiThresholdValue) hqlQuery.uniqueResult();
		}
		catch (Exception e) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, "9009", "component_impexp_messages");
		}
		return hibDs;
	}



	/**
	 * Check the existance of a ModelInstance, based on his unique constraints, into
	 * the current SpagoBI database. Requires two labels
	 * 
	 * @param unique          The object which contains the unique constraints for the
	 * object
	 * @param domainCd          domain code
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error 
	 */
	/*	public Object checkExistenceModelInst(String labelModInst, String modelId,Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;
		hql = "from SbiKpiModelInst ds where ds.label = '" + labelModInst + "'"+"AND sbiKpiModel = '"+modelId+"'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiKpiModelInst hibDs = (SbiKpiModelInst) hqlQuery.uniqueResult();
		return hibDs;
	}*/


	/**
	 * Check the existance of a ModelInstance Resource association, must get ids from model instance and resource
	 * 
	 * @param modelInstLabel     model instance label     
	 * @param resourceLabel      resource Label
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceModelResource(String modelInstLabel, String resourceCode, Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;

		// get Model Instance
		Integer idModelInst=null;
		hql = "from SbiKpiModelInst where label = '" + modelInstLabel + "'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiKpiModelInst hibDs = (SbiKpiModelInst) hqlQuery.uniqueResult();
		if(hibDs==null) return null;

		idModelInst=hibDs.getKpiModelInst();

		// get Resource
		Integer idResource=null;
		hql = "from SbiResources where resourceCode = '" + resourceCode + "'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiResources hibRes = (SbiResources) hqlQuery.uniqueResult();
		if(hibRes==null) return null;

		idResource=hibRes.getResourceId();

		// check now if association exists
		hql = "from SbiKpiModelResources where sbiKpiModelInst = '" + idModelInst + "' AND sbiResources ='"+ idResource +"'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiKpiModelResources hibModRes = (SbiKpiModelResources) hqlQuery.uniqueResult();

		logger.debug("OUT");
		return hibModRes;
	}



	/**
	 * Check the existance of a Metacontents association, must refer to the same object (label) and to the same metadata (label)
	 * 
	 * @param objLabel     label of the SbiObject    
	 * @param metaLabel      label of metadata
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceObjMetacontent(String objLabel , String metaLabel, Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;


		// get Metadata
		logger.debug("get metadata with label "+metaLabel);
		hql = "from SbiObjMetadata s where s.label = '" + metaLabel + "'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiObjMetadata hibMetadata = (SbiObjMetadata) hqlQuery.uniqueResult();
		if(hibMetadata==null) return null;
		Integer idMeta = hibMetadata.getObjMetaId();
		logger.debug("Id meta is "+idMeta);


		// check now if association exists
		logger.debug("Get metacontent with label "+objLabel+", metaId "+idMeta);

		// distinguish if serching a metacontent related to a subobject or to an object: subObject not associated here!

		//		if(subObjectName != null){
		//			hql = "from SbiObjMetacontents so where so.sbiObjects.label = '" + objLabel + "'"+
		//			" AND so.sbiSubObjects.name ='"+ subObjectName +"'" +
		//			" AND so.objmetaId ='"+ idMeta +"'";
		//		}
		//		else {
		hql = "from SbiObjMetacontents so where so.sbiObjects.label = '" + objLabel + "'"+
		" AND so.sbiSubObjects is null" +
		" AND so.objmetaId ='"+ idMeta +"'";			
		//		}


		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiObjMetacontents hibMetacontents = (SbiObjMetacontents) hqlQuery.uniqueResult();

		logger.debug("OUT");
		return hibMetacontents;
	}


	/**
	 * Check the existance of a Kpi Instance Periodicity association, must get ids from kpi instance and periodicity
	 * 
	 * @param modInstLabel         modelInstanceLabel
	 * @param periodicityLabel          periodicity Label
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceKpiInstPeriod(Integer newIdKpiInstance, String periodicityLabel, Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;


		// get Periodicity
		Integer idPeriodicity=null;
		hql = "from SbiKpiPeriodicity where name = '" + periodicityLabel + "'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiKpiPeriodicity hibPer = (SbiKpiPeriodicity) hqlQuery.uniqueResult();
		if(hibPer==null) return null;
		idPeriodicity=hibPer.getIdKpiPeriodicity();

		// check now if association exists
		hql = "from SbiKpiInstPeriod where sbiKpiInstance = '" + newIdKpiInstance + "' AND sbiKpiPeriodicity ='"+ idPeriodicity +"'";
		hqlQuery = sessionCurrDB.createQuery(hql);
		SbiKpiInstPeriod hibKInstPer = (SbiKpiInstPeriod) hqlQuery.uniqueResult();

		logger.debug("OUT");
		return hibKInstPer;
	}


	/**
	 * Check the existance of a KpiModelAttr, based on his unique constraints, into
	 * the current SpagoBI database.existance means referring to the same domain and having the same label
	 * 
	 * @param unique          The object which contains the unique constraints for the
	 * object
	 * @param domainCd          domain code
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceKpiModelAttr(Integer newSbiDomainId, String kpiModelAttrCd,Session sessionCurrDB, Object hibObj) throws EMFUserError {
		// TODO cambiare con i nuovi UDP VAlues
		logger.debug("IN");
		String hql = null;
		Query hqlQuery = null;
		/*SbiKpiModelAttr toReturn = null;

		if (hibObj instanceof SbiKpiModelAttr && newSbiDomainId != null) {
			// check if there is a model attribute referring to the same domain (with new ID) and with the same Label
			hql = "from SbiKpiModelAttr s where s.sbiDomains.valueId = " + newSbiDomainId + " "+"AND s.kpiModelAttrCd = '"+kpiModelAttrCd+"'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			toReturn = (SbiKpiModelAttr) hqlQuery.uniqueResult();
		} 	*/

		logger.debug("OUT");
		return null;
	}


	/**
	 * Check the existance of a KpiModelAttrVal, 
	 * .existance means referring to the same attribute and to the same model
	 * 
	 * @param kpiModelAttrId The id of the attribute referred
	 * @param KpiModelID id of the model referred
	 * @param sessionCurrDB   Hibernate session for the current SpagoBI database
	 * @param hibObj          An empty object usefull to identify the kind of object to
	 * analize
	 * 
	 * @return                The existing Object or null if it doesn't exist
	 * 
	 * @throws                EMFUserError
	 * @throws EMFUserError the EMF user error
	 */
	public Object checkExistenceKpiModelAttrVal(Integer kpiModelAttrId, Integer kpiModelId,Session sessionCurrDB, Object hibObj) throws EMFUserError {
		logger.debug("IN");
		// TODO cambiare con i nuovi UDP VAlues
		String hql = null;
		Query hqlQuery = null;
		/*SbiKpiModelAttrVal toReturn = null;

		if (hibObj instanceof SbiKpiModelAttrVal && kpiModelAttrId != null && kpiModelId != null) {
			// check if there is a model attribute referring to the same domain (with new ID) and with the same Label
			hql = "from SbiKpiModelAttrVal s where s.sbiKpiModelAttr.kpiModelAttrId = " + kpiModelAttrId + " "+"AND s.sbiKpiModel.kpiModelId = '"+kpiModelId+"'";
			hqlQuery = sessionCurrDB.createQuery(hql);
			toReturn = (SbiKpiModelAttrVal) hqlQuery.uniqueResult();
		} 	

		logger.debug("OUT");
		return toReturn;*/
		return null;
	}

}
