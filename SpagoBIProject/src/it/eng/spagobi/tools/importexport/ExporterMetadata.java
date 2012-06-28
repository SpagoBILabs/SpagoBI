/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.importexport;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFunc;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFuncId;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjPar;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjTemplates;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSnapshots;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreports;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreportsId;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRole;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRoleId;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFunctions;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParview;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParuseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParviewDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuseId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParview;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParviewId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParameters;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCk;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCkId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDet;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDetId;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.behaviouralmodel.check.metadata.SbiChecks;
import it.eng.spagobi.behaviouralmodel.lov.bo.DatasetDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.Subreport;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IBinContentDAO;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.metadata.SbiBinContents;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.alarm.bo.Alarm;
import it.eng.spagobi.kpi.alarm.bo.AlarmContact;
import it.eng.spagobi.kpi.alarm.dao.ISbiAlarmDAO;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiDocuments;
import it.eng.spagobi.kpi.config.bo.KpiInstPeriod;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.KpiRel;
import it.eng.spagobi.kpi.config.bo.MeasureUnit;
import it.eng.spagobi.kpi.config.bo.Periodicity;
import it.eng.spagobi.kpi.config.dao.IKpiDAO;
import it.eng.spagobi.kpi.config.dao.IKpiInstPeriodDAO;
import it.eng.spagobi.kpi.config.dao.IKpiInstanceDAO;
import it.eng.spagobi.kpi.config.dao.IMeasureUnitDAO;
import it.eng.spagobi.kpi.config.dao.IPeriodicityDAO;
import it.eng.spagobi.kpi.config.metadata.SbiKpi;
import it.eng.spagobi.kpi.config.metadata.SbiKpiDocument;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstPeriod;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstance;
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.kpi.config.metadata.SbiKpiRel;
import it.eng.spagobi.kpi.config.metadata.SbiMeasureUnit;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResources;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.model.dao.IModelDAO;
import it.eng.spagobi.kpi.model.dao.IModelInstanceDAO;
import it.eng.spagobi.kpi.model.dao.IModelResourceDAO;
import it.eng.spagobi.kpi.model.dao.IResourceDAO;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelInst;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelResources;
import it.eng.spagobi.kpi.model.metadata.SbiResources;
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
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.kpi.threshold.dao.IThresholdDAO;
import it.eng.spagobi.kpi.threshold.metadata.SbiThreshold;
import it.eng.spagobi.kpi.threshold.metadata.SbiThresholdValue;
import it.eng.spagobi.mapcatalogue.bo.GeoFeature;
import it.eng.spagobi.mapcatalogue.bo.GeoMap;
import it.eng.spagobi.mapcatalogue.bo.GeoMapFeature;
import it.eng.spagobi.mapcatalogue.dao.ISbiGeoFeaturesDAO;
import it.eng.spagobi.mapcatalogue.dao.ISbiGeoMapFeaturesDAO;
import it.eng.spagobi.mapcatalogue.dao.ISbiGeoMapsDAO;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeaturesId;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMaps;
import it.eng.spagobi.tools.dataset.bo.CustomDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.metadata.SbiCustomDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQbeDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetacontents;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetadata;
import it.eng.spagobi.tools.udp.bo.Udp;
import it.eng.spagobi.tools.udp.bo.UdpValue;
import it.eng.spagobi.tools.udp.metadata.SbiUdp;
import it.eng.spagobi.tools.udp.metadata.SbiUdpValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Implements methods to insert exported metadata into the exported database 
 */
public class ExporterMetadata {

	static private Logger logger = Logger.getLogger(ExporterMetadata.class);
	private List biObjectToInsert=null;

	// list of ids of models that have an attribute with value filled.
	List modelsWithAttributeValued = null;

	/**
	 * Insert a domain into the exported database.
	 * 
	 * @param domain Domain object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDomain(Domain domain, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiDomains where valueId = " + domain.getValueId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains hibDomain = new SbiDomains(domain.getValueId());
			hibDomain.setDomainCd(domain.getDomainCode());
			hibDomain.setDomainNm(domain.getDomainName());
			hibDomain.setValueCd(domain.getValueCd());
			hibDomain.setValueDs(domain.getValueDescription());
			hibDomain.setValueNm(domain.getValueName());
			session.save(hibDomain);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting domain into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}


	/**
	 * Insert a Object Metadata Category into the exported database.
	 * 
	 * @param objMetadata ObjMetadata object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjMetadata(ObjMetadata objMetadata, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjMetadata where objMetaId = " + objMetadata.getObjMetaId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiObjMetadata hibObjMeta = new SbiObjMetadata();
			hibObjMeta.setObjMetaId(objMetadata.getObjMetaId());
			hibObjMeta.setCreationDate(objMetadata.getCreationDate());
			hibObjMeta.setDescription(objMetadata.getDescription());
			hibObjMeta.setLabel(objMetadata.getLabel());
			hibObjMeta.setName(objMetadata.getName());

			if(objMetadata.getDataType() != null){
				SbiDomains dataType=(SbiDomains)session.load(SbiDomains.class, objMetadata.getDataType());
				hibObjMeta.setDataType(dataType);
			}

			session.save(hibObjMeta);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting objMetadata into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}


	/**
	 * Insert a Object Metadata Content into the exported database.
	 * 
	 * @param objMetadataContent ObjMetadataContent object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjMetacontent(ObjMetacontent objMetacontent, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiObjMetacontents where objMetacontentId = " + objMetacontent.getObjMetacontentId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// first of all insert binary content
			if (objMetacontent.getBinaryContentId() != null) {
				// get the content			
				IBinContentDAO binContentDAO = DAOFactory.getBinContentDAO();
				byte[] content = binContentDAO.getBinContent(objMetacontent.getBinaryContentId());
				insertBinContet(objMetacontent.getBinaryContentId(), content, session);
			}

			Transaction tx = session.beginTransaction();

			SbiObjMetacontents hibObjMetacontents = new SbiObjMetacontents();

			hibObjMetacontents.setObjMetacontentId(objMetacontent.getObjMetacontentId());
			hibObjMetacontents.setCreationDate(objMetacontent.getCreationDate());
			hibObjMetacontents.setLastChangeDate(objMetacontent.getLastChangeDate());
			hibObjMetacontents.setObjmetaId(objMetacontent.getObjmetaId());

			// get the object to insert if present
			if (objMetacontent.getBiobjId() != null) {
				SbiObjects sbiObjects = (SbiObjects) session.load(SbiObjects.class, objMetacontent.getBiobjId());
				hibObjMetacontents.setSbiObjects(sbiObjects);
				logger.debug("inserted sbi " + objMetacontent.getBiobjId() + " Object metacontent");
			}
			// get the sub object to insert if present
			if (objMetacontent.getSubobjId() != null) {
				SbiSubObjects sbiSubObjects = (SbiSubObjects) session.load(SbiSubObjects.class, objMetacontent.getSubobjId());
				hibObjMetacontents.setSbiSubObjects(sbiSubObjects);
				logger.debug("inserted sbi " + objMetacontent.getSubobjId() + " SubObject metacontent");
			}
			// get the content
			if (objMetacontent.getBinaryContentId() != null) {
				SbiBinContents sbiBinContents = (SbiBinContents) session.load(SbiBinContents.class, objMetacontent.getBinaryContentId());
				hibObjMetacontents.setSbiBinContents(sbiBinContents);
				// insert the binary content!!				
				logger.debug("inserted sbi " + objMetacontent.getBinaryContentId() + " Binary Content metacontent");
			}

			session.save(hibObjMetacontents);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting objMetadataContent into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}

	/**
	 * Insert data source.
	 * 
	 * @param ds the ds
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDataSource(IDataSource ds, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiDataSource where dsId = " + ds.getDsId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains dialect=(SbiDomains)session.load(SbiDomains.class, ds.getDialectId());

			SbiDataSource hibDS = new SbiDataSource(ds.getDsId());
			hibDS.setDescr(ds.getDescr());
			hibDS.setDriver(ds.getDriver());
			hibDS.setJndi(ds.getJndi());
			hibDS.setLabel(ds.getLabel());
			hibDS.setPwd(ds.getPwd());
			hibDS.setUrl_connection(ds.getUrlConnection());
			hibDS.setUser(ds.getUser());
			hibDS.setDialect(dialect);
			hibDS.setSchemaAttribute(ds.getSchemaAttribute());
			hibDS.setMultiSchema(ds.getMultiSchema());		

			// va aggiunto il legame con gli engine e il doc ????

			session.save(hibDS);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting dataSource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert data set if not already present
	 * @param dataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#insertDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void insertDataSet(GuiGenericDataSet dataSet, Session session) throws EMFUserError {
		logger.debug("IN");
		Transaction tx = null;
		Transaction tx2 = null;
		Transaction tx3 = null;

		
		try {
			// check if it's not already present a dataset with id  dataSet.getDsId
			Query hibQuery = session.createQuery(" from SbiDataSetConfig where dsId = " + dataSet.getDsId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
					logger.debug("dataset with id "+dataSet.getDsId()+" already inserted");
				return;
			}
			
			SbiDataSetHistory hibDataSetHistory =null;
			if(dataSet!=null){
				// get active dataset
				GuiDataSetDetail dataSetActiveDetail = dataSet.getActiveDetail();

				if(dataSetActiveDetail instanceof FileDataSetDetail){
					hibDataSetHistory=new SbiFileDataSet();
					if(((FileDataSetDetail)dataSetActiveDetail).getFileName()!=null){
						((SbiFileDataSet)hibDataSetHistory).setFileName(((FileDataSetDetail)dataSetActiveDetail).getFileName());
					}
				}	
				else if(dataSetActiveDetail instanceof QueryDataSetDetail){
					hibDataSetHistory=new SbiQueryDataSet();
					if(((QueryDataSetDetail)dataSetActiveDetail).getQuery()!=null){
						((SbiQueryDataSet)hibDataSetHistory).setQuery(((QueryDataSetDetail)dataSetActiveDetail).getQuery());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getQueryScript()!=null){
						((SbiQueryDataSet)hibDataSetHistory).setQueryScript(((QueryDataSetDetail)dataSetActiveDetail).getQueryScript());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getQueryScriptLanguage()!=null){
						((SbiQueryDataSet)hibDataSetHistory).setQueryScriptLanguage(((QueryDataSetDetail)dataSetActiveDetail).getQueryScriptLanguage());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel()!=null){
						SbiDataSource hibDataSource = null;
						String dataSourceLabel = ((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel();
						if(dataSourceLabel!=null && !dataSourceLabel.equals("")){
							IDataSourceDAO dataSourceDao = DAOFactory.getDataSourceDAO();
							IDataSource ds = dataSourceDao.loadDataSourceByLabel(dataSourceLabel);
							insertDataSource(ds, session);				
							Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
							Criteria criteria = session.createCriteria(SbiDataSource.class);
							criteria.add(labelCriterrion);	
							hibDataSource = (SbiDataSource) criteria.uniqueResult();
							((SbiQueryDataSet)hibDataSetHistory).setDataSource(hibDataSource);	
						}
					}				
				}else if(dataSetActiveDetail instanceof QbeDataSetDetail){
					hibDataSetHistory = new SbiQbeDataSet();
					SbiQbeDataSet hibQbeDataSet = (SbiQbeDataSet) hibDataSetHistory;
					QbeDataSetDetail qbeDataSet = (QbeDataSetDetail) dataSetActiveDetail;
					hibQbeDataSet.setSqlQuery(qbeDataSet.getSqlQuery());
					hibQbeDataSet.setJsonQuery(qbeDataSet.getJsonQuery());
					hibQbeDataSet.setDatamarts(qbeDataSet.getDatamarts());
					String dataSourceLabel = qbeDataSet.getDataSourceLabel();
					Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
					Criteria criteria = session.createCriteria(SbiDataSource.class);
					criteria.add(labelCriterrion);	
					SbiDataSource hibDataSource = (SbiDataSource) criteria.uniqueResult();
					hibQbeDataSet.setDataSource(hibDataSource);	
				}		
				else if(dataSetActiveDetail instanceof WSDataSetDetail){
					hibDataSetHistory=new SbiWSDataSet();
					if(((WSDataSetDetail)dataSetActiveDetail).getAddress()!=null){
						((SbiWSDataSet)hibDataSetHistory).setAdress(((WSDataSetDetail)dataSetActiveDetail).getAddress());
					}
					if(((WSDataSetDetail)dataSetActiveDetail).getOperation()!=null){
						((SbiWSDataSet)hibDataSetHistory).setOperation(((WSDataSetDetail)dataSetActiveDetail).getOperation());
					}	
				}

				else if(dataSetActiveDetail instanceof JClassDataSetDetail){
					hibDataSetHistory=new SbiJClassDataSet();
					if(((JClassDataSetDetail)dataSetActiveDetail).getJavaClassName()!=null){
						((SbiJClassDataSet)hibDataSetHistory).setJavaClassName(((JClassDataSetDetail)dataSetActiveDetail).getJavaClassName());
					}
				}

				else if(dataSetActiveDetail instanceof ScriptDataSetDetail){
					hibDataSetHistory=new SbiScriptDataSet();
					if(((ScriptDataSetDetail)dataSetActiveDetail).getScript()!=null){
						((SbiScriptDataSet)hibDataSetHistory).setScript(((ScriptDataSetDetail)dataSetActiveDetail).getScript());
					}
					if(((ScriptDataSetDetail)dataSetActiveDetail).getLanguageScript()!=null){
						((SbiScriptDataSet)hibDataSetHistory).setLanguageScript(((ScriptDataSetDetail)dataSetActiveDetail).getLanguageScript());
					}
				}
				
				else if (dataSetActiveDetail instanceof CustomDataSetDetail) {
					hibDataSetHistory=new SbiCustomDataSet();
					if (((CustomDataSetDetail)dataSetActiveDetail).getCustomData() != null) {
						((SbiCustomDataSet)hibDataSetHistory).setCustomData(((CustomDataSetDetail)dataSetActiveDetail).getCustomData());
					}
					if (((CustomDataSetDetail)dataSetActiveDetail).getJavaClassName() != null) {
						((SbiCustomDataSet)hibDataSetHistory).setJavaClassName(((CustomDataSetDetail)dataSetActiveDetail).getJavaClassName());
					}	
				}

				SbiDomains transformer = null;
				if (dataSetActiveDetail.getTransformerId() != null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSetActiveDetail.getTransformerId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);

					transformer = (SbiDomains) criteria.uniqueResult();

					if (transformer == null){
						logger.error("The Domain with value_id= "+dataSetActiveDetail.getTransformerId()+" does not exist.");
						throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
					}
				}

				SbiDomains category = null;
				if (dataSetActiveDetail.getCategoryId()!= null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSetActiveDetail.getCategoryId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);

					category = (SbiDomains) criteria.uniqueResult();

					if (category == null){
						logger.error("The Domain with value_id= "+dataSetActiveDetail.getCategoryId()+" does not exist.");
						throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
					}
				}
				Date currentTStamp = new Date();

				// Fill dataset config values

				SbiDataSetConfig hibDataSetConfig = new SbiDataSetConfig();	

				hibDataSetConfig.setDsId(dataSet.getDsId());
				hibDataSetConfig.setLabel(dataSet.getLabel());
				hibDataSetConfig.setDescription(dataSet.getDescription());
				hibDataSetConfig.setName(dataSet.getName());	

				tx = session.beginTransaction();
				session.save(hibDataSetConfig);
				tx.commit();

				// Fill Data set history values
				hibDataSetHistory.setSbiDsConfig(hibDataSetConfig);
				if(dataSetActiveDetail.getDsHId()!=0){
					hibDataSetHistory.setDsHId(dataSetActiveDetail.getDsHId());
				}			
				hibDataSetHistory.setVersionNum(1);
				hibDataSetHistory.setActive(true);			

				hibDataSetHistory.setTransformer(transformer);
				hibDataSetHistory.setPivotColumnName(dataSetActiveDetail.getPivotColumnName());
				hibDataSetHistory.setPivotRowName(dataSetActiveDetail.getPivotRowName());
				hibDataSetHistory.setPivotColumnValue(dataSetActiveDetail.getPivotColumnValue());
				hibDataSetHistory.setNumRows(dataSetActiveDetail.isNumRows());

				hibDataSetHistory.setCategory(category);
				hibDataSetHistory.setParameters(dataSetActiveDetail.getParameters());
				hibDataSetHistory.setDsMetadata(dataSetActiveDetail.getDsMetadata());
				
				tx2 = session.beginTransaction();
				session.save(hibDataSetHistory);
				tx2.commit();
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting the New Data Set ", he);
			if (tx != null)
				tx.rollback();
			if (tx2 != null)
				tx2.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			//if (session!=null){
			//	if (session.isOpen()) session.close();
			logger.debug("OUT");
		}
	}

	/**
	 * Insert data set.
	 * 
	 * @param dataset the dataset
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDataSetAndDataSource(IDataSet dataset, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			// if it is a query data set, insert datasource first, before opening a new transaction
			if (dataset instanceof JDBCDataSet) {
				IDataSource ds = ((JDBCDataSet) dataset).getDataSource();
				if (ds != null) insertDataSource(ds, session);
			}

			
			Transaction tx = session.beginTransaction();
			GuiGenericDataSet ds = DAOFactory.getDataSetDAO().toGuiGenericDataSet(dataset);
			if(ds!=null){
				insertDataSet(ds,session);
			}
		} catch (Exception e) {
			logger.error("Error while inserting dataSet into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert an engine into the exported database.
	 * 
	 * @param engine Engine Object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertEngine(Engine engine, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiEngines where engineId = " + engine.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiEngines hibEngine = new SbiEngines(engine.getId());
			hibEngine.setName(engine.getName());
			hibEngine.setLabel(engine.getLabel());
			hibEngine.setDescr(engine.getDescription());
			hibEngine.setDriverNm(engine.getDriverName());
			hibEngine.setEncrypt(new Short((short)engine.getCriptable().intValue()));
			hibEngine.setMainUrl(engine.getUrl());
			hibEngine.setObjUplDir(engine.getDirUpload());
			hibEngine.setObjUseDir(engine.getDirUsable());
			hibEngine.setSecnUrl(engine.getSecondaryUrl());
			SbiDomains objTypeDom = (SbiDomains)session.load(SbiDomains.class, engine.getBiobjTypeId());
			hibEngine.setBiobjType(objTypeDom);
			hibEngine.setClassNm(engine.getClassName());
			SbiDomains engineTypeDom = (SbiDomains)session.load(SbiDomains.class, engine.getEngineTypeId());
			hibEngine.setEngineType(engineTypeDom);
			hibEngine.setUseDataSource(new Boolean(engine.getUseDataSource()));
			if (engine.getUseDataSource() && engine.getDataSourceId() != null) {
				SbiDataSource ds = (SbiDataSource) session.load(SbiDataSource.class, engine.getDataSourceId());
				hibEngine.setDataSource(ds);
			}
			hibEngine.setUseDataSet(new Boolean(engine.getUseDataSet()));
			session.save(hibEngine);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting engine into export database " ,e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert all Snapshot and their binary content.
	 * 
	 * @param biobj the biobj
	 * @param snapshotLis the snapshot lis
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAllSnapshot(BIObject biobj, List snapshotLis, Session session) throws EMFUserError {
		logger.debug("IN");
		Iterator iter=snapshotLis.iterator();
		while(iter.hasNext()){
			insertSnapshot(biobj,(Snapshot)iter.next(),session);
		}
		logger.debug("OUT");
	}

	/**
	 * Insert a single sub object and their binary content
	 * @param biobj
	 * @param subObject
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertSnapshot(BIObject biobj, Snapshot snapshot, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiSnapshots where snapId = " + snapshot.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				logger.warn("Exist another SbiSnapshot");
				return;
			}

			SbiObjects hibBIObj = new SbiObjects(biobj.getId());

			byte[] template = snapshot.getContent();

			SbiBinContents hibBinContent = new SbiBinContents();
			hibBinContent.setId(snapshot.getBinId());
			hibBinContent.setContent(template);

			SbiSnapshots sub=new SbiSnapshots();
			sub.setCreationDate(snapshot.getDateCreation());
			sub.setDescription(snapshot.getDescription());
			sub.setName(snapshot.getName());
			sub.setSbiBinContents(hibBinContent);
			sub.setSbiObject(hibBIObj);
			sub.setSnapId(snapshot.getId());


			session.save(sub);
			session.save(hibBinContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	

	/**
	 * Insert all SubObject and their binary content.
	 * 
	 * @param biobj the biobj
	 * @param subObjectLis the sub object lis
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAllSubObject(BIObject biobj, List subObjectLis, Session session) throws EMFUserError {
		logger.debug("IN");
		Iterator iter=subObjectLis.iterator();
		while(iter.hasNext()){
			SubObject subObject = (SubObject)iter.next();
			insertSubObject(biobj,subObject,session);

			//  insert metadata associated to subObject
			logger.debug("search for metadata associate to subobject wit ID "+subObject.getId());
			IObjMetacontentDAO objMetacontentDAO = DAOFactory.getObjMetacontentDAO();
			//  get metacontents associated to object
			List metacontents = objMetacontentDAO.loadObjOrSubObjMetacontents(biobj.getId(), subObject.getId());
			for (Iterator iterator = metacontents.iterator(); iterator.hasNext();) {
				ObjMetacontent metacontent = (ObjMetacontent) iterator.next();
				insertObjMetacontent(metacontent, session);
			}

		}
		logger.debug("OUT");
	}
	/**
	 * Insert a single sub object and their binary content
	 * @param biobj
	 * @param subObject
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertSubObject(BIObject biobj, SubObject subObject, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiSubObjects where subObjId = " + subObject.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				logger.warn("Exist another SbiSubObjects");
				return;
			}

			SbiObjects hibBIObj = new SbiObjects(biobj.getId());

			SbiBinContents hibBinContent = new SbiBinContents();
			hibBinContent.setId(subObject.getBinaryContentId());
			hibBinContent.setContent(subObject.getContent());

			SbiSubObjects sub=new SbiSubObjects();
			sub.setCreationDate(subObject.getCreationDate());
			sub.setDescription(subObject.getDescription());
			sub.setIsPublic(subObject.getIsPublic());
			sub.setName(subObject.getName());
			sub.setOwner(subObject.getOwner());
			sub.setLastChangeDate(subObject.getLastChangeDate());
			sub.setSbiBinContents(hibBinContent);
			sub.setSbiObject(hibBIObj);
			sub.setSubObjId(subObject.getId());

			session.save(sub);
			session.save(hibBinContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	

	/*public void insertKpiDocuments(KpiDocuments docs, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			session.save(hibBIObj);
			tx.commit();
		}catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}*/

	/**
	 * Insert a biobject into the exported database.
	 * 
	 * @param biobj BIObject to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBIObject(BIObject biobj, Session session, boolean insertDataSet) throws EMFUserError {
		logger.debug("IN");
		logger.debug("Insert BI Object with id "+biobj.getId()+ " with inserting dataset "+insertDataSet);
		
		try {

			Query hibQuery = session.createQuery(" from SbiObjects where biobjId = " + biobj.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			Engine engine = biobj.getEngine();	
			SbiEngines hibEngine = (SbiEngines)session.load(SbiEngines.class, engine.getId());
			SbiDomains hibState = (SbiDomains)session.load(SbiDomains.class, biobj.getStateID());
			SbiDomains hibObjectType = (SbiDomains)session.load(SbiDomains.class, biobj.getBiObjectTypeID());
			SbiObjects hibBIObj = new SbiObjects(biobj.getId());
			hibBIObj.setSbiEngines(hibEngine); 
			hibBIObj.setDescr(biobj.getDescription());
			hibBIObj.setLabel(biobj.getLabel());
			hibBIObj.setName(biobj.getName());
			hibBIObj.setEncrypt(new Short(biobj.getEncrypt().shortValue()));
			hibBIObj.setRelName(biobj.getRelName());
			hibBIObj.setState(hibState);
			hibBIObj.setStateCode(biobj.getStateCode());
			hibBIObj.setObjectType(hibObjectType);
			hibBIObj.setObjectTypeCode(biobj.getBiObjectTypeCode());
			hibBIObj.setPath(biobj.getPath());
			hibBIObj.setUuid(biobj.getUuid());
			Integer visFlagIn = biobj.getVisible();
			Short visFlagSh = new Short(visFlagIn.toString());
			hibBIObj.setVisible(visFlagSh);
			Integer dataSourceId = biobj.getDataSourceId();
			if (dataSourceId != null) {
				SbiDataSource ds = (SbiDataSource) session.load(SbiDataSource.class, dataSourceId);
				hibBIObj.setDataSource(ds);
			}
			Integer dataSetId = biobj.getDataSetId();

			if (dataSetId != null) { 
				// if the transaction is new insert dataset if missing   
				IDataSetDAO datasetDao=DAOFactory.getDataSetDAO();
				// insert dataset if parameter insertDataSet is true (in case of KPI export)
				if(insertDataSet){
					GuiGenericDataSet guiGenericDataSet = datasetDao.loadDataSetById(dataSetId);
					if(guiGenericDataSet!=null){
						insertDataSet(guiGenericDataSet, session);
					}
				}
				SbiDataSetConfig dataset = (SbiDataSetConfig) session.load(SbiDataSetConfig.class, dataSetId);
				hibBIObj.setDataSet(dataset);
			}

			hibBIObj.setCreationDate(biobj.getCreationDate());
			hibBIObj.setCreationUser(biobj.getCreationUser());
			hibBIObj.setRefreshSeconds(biobj.getRefreshSeconds());
			hibBIObj.setProfiledVisibility(biobj.getProfiledVisibility());
			Transaction tx = session.beginTransaction();
			session.save(hibBIObj);
			tx.commit();
			ObjTemplate template = biobj.getActiveTemplate();
			if (template == null) {
				logger.warn("Biobject with id = " + biobj.getId() + ", label = " + biobj.getLabel() + " and name = " + biobj.getName() + 
				" has not active template!!");
			} else {
				insertBIObjectTemplate(hibBIObj, template, session);
			}

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert Object Template and Binary Content
	 * @param hibBIObj
	 * @param biobjTempl
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertBIObjectTemplate(SbiObjects hibBIObj,ObjTemplate biobjTempl, Session session) throws EMFUserError {
		logger.debug("IN");

		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjTemplates where objTempId = " + biobjTempl.getBiobjId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			byte[] template = biobjTempl.getContent();

			SbiBinContents hibBinContent = new SbiBinContents();
			SbiObjTemplates hibObjTemplate = new SbiObjTemplates();
			hibObjTemplate.setObjTempId(biobjTempl.getBiobjId());
			hibBinContent.setId(biobjTempl.getBinId());
			hibBinContent.setContent(template);


			hibObjTemplate.setActive(new Boolean(true));
			hibObjTemplate.setCreationDate(biobjTempl.getCreationDate());
			hibObjTemplate.setCreationUser(biobjTempl.getCreationUser());
			hibObjTemplate.setDimension(biobjTempl.getDimension());
			hibObjTemplate.setName(biobjTempl.getName());
			hibObjTemplate.setProg(biobjTempl.getProg());
			hibObjTemplate.setSbiBinContents(hibBinContent);
			hibObjTemplate.setSbiObject(hibBIObj);

			session.save(hibBinContent);
			session.save(hibObjTemplate);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	

	/**
	 * Insert a BIObject Parameter into the exported database.
	 * 
	 * @param biobjpar BIObject parameter to insert
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBIObjectParameter(BIObjectParameter biobjpar,  Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjPar where objParId = " + biobjpar.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			/*
			Integer parid = biobjpar.getParameter().getId();
			Integer objid = biobj.getId();
			String query = " from SbiObjPar where id.sbiParameters.parId = " + parid +
						   " and id.sbiObjects.biobjId = " + objid;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiObjParId hibBIObjParId = new SbiObjParId();
			SbiParameters hibParameter = (SbiParameters)session.load(SbiParameters.class, parid);
			SbiObjects hibBIObject = (SbiObjects)session.load(SbiObjects.class, objid);
			hibBIObjParId.setSbiObjects(hibBIObject);
			hibBIObjParId.setSbiParameters(hibParameter);
			hibBIObjParId.setProg(new Integer(0));
			 */

			// build BI Object Parameter
			SbiObjPar hibBIObjPar = new SbiObjPar(biobjpar.getId());
			hibBIObjPar.setLabel(biobjpar.getLabel());
			hibBIObjPar.setReqFl(new Short(biobjpar.getRequired().shortValue()));
			hibBIObjPar.setModFl(new Short(biobjpar.getModifiable().shortValue()));
			hibBIObjPar.setViewFl(new Short(biobjpar.getVisible().shortValue()));
			hibBIObjPar.setMultFl(new Short(biobjpar.getMultivalue().shortValue()));
			hibBIObjPar.setParurlNm(biobjpar.getParameterUrlName());
			hibBIObjPar.setPriority(biobjpar.getPriority());
			hibBIObjPar.setProg(biobjpar.getProg());
			Integer biobjid = biobjpar.getBiObjectID();
			SbiObjects sbiob = (SbiObjects)session.load(SbiObjects.class, biobjid);
			Integer parid = biobjpar.getParID();			
			SbiParameters sbipar = (SbiParameters)session.load(SbiParameters.class, parid);
			hibBIObjPar.setSbiObject(sbiob);
			hibBIObjPar.setSbiParameter(sbipar);
			// save the BI Object Parameter
			session.save(hibBIObjPar);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting BIObjectParameter into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert a parameter into the exported database.
	 * 
	 * @param param The param object to insert
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParameter(Parameter param, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiParameters where parId = " + param.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains hibParamType = (SbiDomains)session.load(SbiDomains.class, param.getTypeId());
			SbiParameters hibParam = new SbiParameters(param.getId());
			hibParam.setDescr(param.getDescription());
			hibParam.setLength(new Short(param.getLength().shortValue()));
			hibParam.setLabel(param.getLabel());
			hibParam.setName(param.getName());
			hibParam.setParameterTypeCode(param.getType());
			hibParam.setMask(param.getMask());
			hibParam.setParameterType(hibParamType);
			hibParam.setFunctionalFlag(param.isFunctional() ? new Short((short) 1) : new Short((short) 0));
			hibParam.setTemporalFlag(param.isTemporal() ? new Short((short) 1) : new Short((short) 0));
			session.save(hibParam);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting parameter into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert a parameter use into the exported database.
	 * 
	 * @param parUse The Parameter use object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParUse(ParameterUse parUse, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiParuse where useId = " + parUse.getUseID());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiParuse hibParuse = new SbiParuse(parUse.getUseID());
			// Set the relation with parameter
			SbiParameters hibParameters = (SbiParameters)session.load(SbiParameters.class, parUse.getId());
			hibParuse.setSbiParameters(hibParameters);
			// Set the relation with idLov (if the parameter ha a lov related)
			Integer lovId = parUse.getIdLov();
			if(lovId!=null){ 
				SbiLov hibLov = (SbiLov)session.load(SbiLov.class, parUse.getIdLov());
				hibParuse.setSbiLov(hibLov);
			}
			hibParuse.setLabel(parUse.getLabel());
			hibParuse.setName(parUse.getName());
			hibParuse.setDescr(parUse.getDescription());
			hibParuse.setManualInput(parUse.getManualInput());
			hibParuse.setMaximizerEnabled(parUse.isMaximizerEnabled());
			hibParuse.setSelectionType(parUse.getSelectionType());
			hibParuse.setMultivalue(parUse.isMultivalue()? new Integer(1): new Integer(0));
			session.save(hibParuse);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting parameter use into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Dependencies between parameters.
	 * 
	 * @param biparams the biparams
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBiParamDepend(List biparams, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Iterator iterBIParams = biparams.iterator();
			while(iterBIParams.hasNext()) {
				BIObjectParameter biparam = (BIObjectParameter)iterBIParams.next();			    
				IObjParuseDAO objparuseDao = DAOFactory.getObjParuseDAO();
				List objparlist = objparuseDao.loadObjParuses(biparam.getId());
				Iterator iterObjParuse = objparlist.iterator();
				while(iterObjParuse.hasNext()) {
					ObjParuse objparuse = (ObjParuse)iterObjParuse.next();
					Transaction tx = session.beginTransaction();
					// TODO controllare perché serve questo controllo: le dipendenze non dovrebbero essere riutilizzabili, per 
					// cui vengono inseriti una sola volta
					Query hibQuery = session.createQuery(" from SbiObjParuse where id.sbiObjPar.objParId = " + objparuse.getObjParId() + 
							" and id.sbiParuse.useId = " + objparuse.getParuseId() + 
							" and id.sbiObjParFather.objParId = " + objparuse.getObjParFatherId() + 
							" and id.filterOperation = '" + objparuse.getFilterOperation() + "'" );
					List hibList = hibQuery.list();
					if(!hibList.isEmpty()) {
						continue;
					}
					// built key
					SbiObjParuseId hibObjParuseId = new SbiObjParuseId();
					SbiObjPar hibObjPar = (SbiObjPar)session.load(SbiObjPar.class, objparuse.getObjParId());
					SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, objparuse.getParuseId());
					SbiObjPar objparfather = (SbiObjPar)session.load(SbiObjPar.class, objparuse.getObjParFatherId());
					hibObjParuseId.setSbiObjPar(hibObjPar);
					hibObjParuseId.setSbiParuse(hibParuse);
					hibObjParuseId.setFilterOperation(objparuse.getFilterOperation());
					hibObjParuseId.setSbiObjParFather(objparfather);
					SbiObjParuse hibObjParuse = new SbiObjParuse(hibObjParuseId);
					hibObjParuse.setFilterColumn(objparuse.getFilterColumn());
					hibObjParuse.setProg(objparuse.getProg());
					hibObjParuse.setPreCondition(objparuse.getPreCondition());
					hibObjParuse.setPostCondition(objparuse.getPostCondition());
					hibObjParuse.setLogicOperator(objparuse.getLogicOperator());
					session.save(hibObjParuse);
					tx.commit();	
				}
			}

		} catch (Exception e) {
			logger.error("Error while inserting parameter dependencied into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert Visibility Dependencies between parameters.
	 * 
	 * @param biparams the biparams
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBiParamViewDepend(List biparams, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Iterator iterBIParams = biparams.iterator();
			while(iterBIParams.hasNext()) {
				BIObjectParameter biparam = (BIObjectParameter)iterBIParams.next();			    
				IObjParviewDAO objparviewDao = DAOFactory.getObjParviewDAO();
				List objparlist = objparviewDao.loadObjParviews(biparam.getId() );
				Iterator iterObjParview = objparlist.iterator();
				while(iterObjParview.hasNext()) {
					ObjParview objParview = (ObjParview)iterObjParview.next();
					Transaction tx = session.beginTransaction();
					// TODO controllare perché serve questo controllo: le dipendenze non dovrebbero essere riutilizzabili, per 
					// cui vengono inseriti una sola volta
					Query hibQuery = session.createQuery(" from SbiObjParview where id.sbiObjPar.objParId = " + objParview.getObjParId() + 
							" and id.sbiObjParFather.objParId = " + objParview.getObjParFatherId() + 
							" and id.compareValue = '" + objParview.getCompareValue() +"'"+ 
							" and id.operation = '" + objParview.getOperation() + "'" );
					List hibList = hibQuery.list();
					if(!hibList.isEmpty()) {
						continue;
					}
					// built key
					SbiObjParviewId hibObjParviewId = new SbiObjParviewId();
					SbiObjPar hibObjPar = (SbiObjPar)session.load(SbiObjPar.class, objParview.getObjParId());
					SbiObjPar objparfather = (SbiObjPar)session.load(SbiObjPar.class, objParview.getObjParFatherId());
					hibObjParviewId.setSbiObjPar(hibObjPar);
					hibObjParviewId.setOperation(objParview.getOperation());
					hibObjParviewId.setCompareValue(objParview.getCompareValue());
					hibObjParviewId.setSbiObjParFather(objparfather);
					SbiObjParview hibObjParview = new SbiObjParview(hibObjParviewId);
					hibObjParview.setViewLabel(objParview.getViewLabel());
					hibObjParview.setProg(objParview.getProg());
					session.save(hibObjParview);
					tx.commit();	
				}
			}

		} catch (Exception e) {
			logger.error("Error while inserting parameter view dependencied into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert a list of value into the exported database.
	 * 
	 * @param lov The list of values object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertLov(ModalitiesValue lov, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiLov where lovId = " + lov.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiLov hibLov = new SbiLov(lov.getId());
			hibLov.setName(lov.getName());
			hibLov.setLabel(lov.getLabel());
			hibLov.setDescr(lov.getDescription());
			SbiDomains inpType = (SbiDomains)session.load(SbiDomains.class, new Integer(lov.getITypeId()));
			hibLov.setInputType(inpType);
			hibLov.setInputTypeCd(lov.getITypeCd());
			hibLov.setLovProvider(lov.getLovProvider());
			String lovProvider = lov.getLovProvider();
			try {
				ILovDetail lovDetail = LovDetailFactory
						.getLovFromXML(lovProvider);
				if (lovDetail instanceof DatasetDetail) {
					// export dataset
					DatasetDetail datasetDetail = (DatasetDetail) lovDetail;
					String datasetLabel = datasetDetail.getDatasetLabel();
					GuiGenericDataSet existingDataset = DAOFactory.getDataSetDAO().loadDataSetByLabel(datasetLabel);
					this.insertDataSet(existingDataset, session);
					// previuos transaction was closed
					tx = session.beginTransaction();
				}
			} catch (Exception e) {
				logger.error("Error in evaluating lov provider for exporter lov ["
						+ lov.getLabel() + "].", e);
			}
			session.save(hibLov);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting lov into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert a check into the exported database.
	 * 
	 * @param check The check object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertCheck(Check check, Session session) throws EMFUserError {
		logger.debug("IN");
		try{
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiChecks where checkId = " + check.getCheckId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains checkType = (SbiDomains)session.load(SbiDomains.class, check.getValueTypeId());
			SbiChecks hibCheck = new SbiChecks(check.getCheckId());
			hibCheck.setCheckType(checkType);
			hibCheck.setDescr(check.getDescription());
			hibCheck.setName(check.getName());
			hibCheck.setLabel(check.getLabel());
			hibCheck.setValue1(check.getFirstValue());
			hibCheck.setValue2(check.getSecondValue());
			hibCheck.setValueTypeCd(check.getValueTypeCd());
			session.save(hibCheck);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting check into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert an association between a parameter use and a check into the exported database.
	 * 
	 * @param parUse The paruse object which is an element of the association
	 * @param check The check object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParuseCheck(ParameterUse parUse, Check check, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer paruseId = parUse.getUseID();
			Integer checkId = check.getCheckId();
			String query = " from SbiParuseCk where id.sbiParuse.useId = " + paruseId +
			" and id.sbiChecks.checkId = " + checkId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiParuseCkId hibParuseCkId = new SbiParuseCkId();
			SbiChecks hibChecks = (SbiChecks)session.load(SbiChecks.class, check.getCheckId());
			SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, parUse.getUseID());
			hibParuseCkId.setSbiChecks(hibChecks);
			hibParuseCkId.setSbiParuse(hibParuse);
			SbiParuseCk hibParuseCheck = new SbiParuseCk(hibParuseCkId);
			hibParuseCheck.setProg(new Integer(0));
			session.save(hibParuseCheck);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting paruse and check association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a parameter use and a role into the exported database.
	 * 
	 * @param parUse The paruse object which is an element of the association
	 * @param role The role object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParuseRole(ParameterUse parUse, Role role, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer paruseId = parUse.getUseID();
			Integer roleId = role.getId();
			String query = " from SbiParuseDet where id.sbiParuse.useId = " + paruseId +
			" and id.sbiExtRoles.extRoleId = " + roleId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiParuseDetId hibParuseDetId = new SbiParuseDetId();
			SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, parUse.getUseID());
			SbiExtRoles hibExtRole = (SbiExtRoles)session.load(SbiExtRoles.class, role.getId());
			hibParuseDetId.setSbiExtRoles(hibExtRole);
			hibParuseDetId.setSbiParuse(hibParuse);
			SbiParuseDet hibParuseDet = new SbiParuseDet(hibParuseDetId);
			session.save(hibParuseDet);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting paruse and role association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a master report and a subreport.
	 * 
	 * @param sub The subreport
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertSubReportAssociation(Subreport sub, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();

			Integer masterId = sub.getMaster_rpt_id();
			Integer subId = sub.getSub_rpt_id();
			String query = " from SbiSubreports as subreport where " +
			"subreport.id.masterReport.biobjId = " + masterId + " and " +
			"subreport.id.subReport.biobjId = " + subId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			SbiSubreportsId hibSubreportid = new SbiSubreportsId();
			SbiObjects masterReport = (SbiObjects) session.load(SbiObjects.class, sub.getMaster_rpt_id());
			SbiObjects subReport = (SbiObjects) session.load(SbiObjects.class, sub.getSub_rpt_id());
			hibSubreportid.setMasterReport(masterReport);
			hibSubreportid.setSubReport(subReport);
			SbiSubreports hibSubreport = new SbiSubreports(hibSubreportid);
			session.save(hibSubreport);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting subreport " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert a functionality into the exported database.
	 * 
	 * @param funct Functionality Object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertFunctionality(LowFunctionality funct, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiFunctions where funct_id = " + funct.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			IDomainDAO domDAO = DAOFactory.getDomainDAO();
			Domain functTypeDom = domDAO.loadDomainByCodeAndValue("FUNCT_TYPE", funct.getCodType());
			SbiDomains hibFunctType = (SbiDomains)session.load(SbiDomains.class, functTypeDom.getValueId());
			SbiFunctions hibFunct = new SbiFunctions(funct.getId());
			hibFunct.setCode(funct.getCode());
			hibFunct.setDescr(funct.getDescription());
			hibFunct.setFunctTypeCd(funct.getCodType());
			hibFunct.setFunctType(hibFunctType);
			hibFunct.setName(funct.getName());
			hibFunct.setPath(funct.getPath());
			hibFunct.setProg(funct.getProg());
			session.save(hibFunct);
			tx.commit();
			Role[] devRoles = funct.getDevRoles();
			Domain devDom = domDAO.loadDomainByCodeAndValue(SpagoBIConstants.PERMISSION_ON_FOLDER, 
					SpagoBIConstants.PERMISSION_ON_FOLDER_TO_DEVELOP);
			for(int i=0; i<devRoles.length; i++) {
				Role devRole = devRoles[i];
				insertRole(devRole, session);
				insertFunctRole(devRole, funct, devDom.getValueId(), devDom.getValueCd(), session);
			}
			Role[] testRoles = funct.getTestRoles();
			Domain testDom = domDAO.loadDomainByCodeAndValue(SpagoBIConstants.PERMISSION_ON_FOLDER, 
					SpagoBIConstants.PERMISSION_ON_FOLDER_TO_TEST);
			for(int i=0; i<testRoles.length; i++) {
				Role testRole = testRoles[i];
				insertRole(testRole, session);
				insertFunctRole(testRole, funct, testDom.getValueId(), testDom.getValueCd(), session);
			}
			Role[] execRoles = funct.getExecRoles();
			Domain execDom = domDAO.loadDomainByCodeAndValue(SpagoBIConstants.PERMISSION_ON_FOLDER, 
					SpagoBIConstants.PERMISSION_ON_FOLDER_TO_EXECUTE);
			for(int i=0; i<execRoles.length; i++) {
				Role execRole = execRoles[i];
				insertRole(execRole, session);
				insertFunctRole(execRole, funct, execDom.getValueId(), execDom.getValueCd(), session);
			}
			Role[] createRoles = funct.getCreateRoles();
			Domain createDom = domDAO.loadDomainByCodeAndValue(SpagoBIConstants.PERMISSION_ON_FOLDER, 
					SpagoBIConstants.PERMISSION_ON_FOLDER_TO_CREATE);
			for(int i=0; i<createRoles.length; i++) {
				Role createRole = createRoles[i];
				insertRole(createRole, session);
				insertFunctRole(createRole, funct, createDom.getValueId(), createDom.getValueCd(), session);
			}

		} catch (Exception e) {
			logger.error("Error while inserting Functionality into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}

		// recursively insert parent functionalities
		Integer parentId = funct.getParentId();
		if(parentId!=null){
			ILowFunctionalityDAO lowFunctDAO = DAOFactory.getLowFunctionalityDAO();
			LowFunctionality functPar = lowFunctDAO.loadLowFunctionalityByID(parentId, false);
			insertFunctionality(functPar, session);
		}
		logger.debug("OUT");

	}





	/**
	 * Insert a role into the exported database.
	 * 
	 * @param role The role object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertRole(Role role, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiExtRoles where extRoleId = " + role.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiExtRoles hibRole = new SbiExtRoles(role.getId());
			hibRole.setCode(role.getCode());
			hibRole.setDescr(role.getDescription());
			hibRole.setName(role.getName());
			SbiDomains roleType = (SbiDomains)session.load(SbiDomains.class, role.getRoleTypeID());
			hibRole.setRoleType(roleType);
			hibRole.setRoleTypeCode(role.getRoleTypeCD());
			hibRole.setIsAbleToSaveIntoPersonalFolder(new Boolean(role.isAbleToSaveIntoPersonalFolder()));
			hibRole.setIsAbleToSaveRememberMe(new Boolean(role.isAbleToSaveRememberMe()));
			hibRole.setIsAbleToSeeMetadata(new Boolean(role.isAbleToSeeMetadata()));
			hibRole.setIsAbleToSeeNotes(new Boolean(role.isAbleToSeeNotes()));;
			hibRole.setIsAbleToSeeSnapshots(new Boolean(role.isAbleToSeeSnapshots()));
			hibRole.setIsAbleToSeeSubobjects(new Boolean(role.isAbleToSeeSubobjects()));
			hibRole.setIsAbleToSeeViewpoints(new Boolean(role.isAbleToSeeViewpoints()));
			hibRole.setIsAbleToSendMail(new Boolean(role.isAbleToSendMail()));
			hibRole.setIsAbleToBuildQbeQuery(role.isAbleToBuildQbeQuery());
			hibRole.setIsAbleToDoMassiveExport(role.isAbleToDoMassiveExport());
			hibRole.setIsAbleToEditWorksheet(role.isAbleToEditWorksheet());
			
			session.save(hibRole);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting role into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a functionality and a role into the exported database.
	 * 
	 * @param role The role object which is an element of the association
	 * @param funct The functionality object which is an element of the association
	 * @param permissionId The id of the permission associated to the couple role / functionality
	 * @param permissionCd The code of the permission associated to the couple role / functionality
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertFunctRole(Role role, LowFunctionality funct, Integer permissionId, String permissionCd, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer roleid = role.getId();
			Integer functid = funct.getId();
			String query = " from SbiFuncRole where id.function = " + functid +
			" and id.role = " + roleid + " and id.state = " + permissionId ;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiFuncRoleId hibFuncRoleId = new SbiFuncRoleId();
			SbiFunctions hibFunct = (SbiFunctions)session.load(SbiFunctions.class, funct.getId());
			SbiExtRoles hibRole = (SbiExtRoles)session.load(SbiExtRoles.class, role.getId());
			SbiDomains hibPermission = (SbiDomains)session.load(SbiDomains.class, permissionId);
			hibFuncRoleId.setFunction(hibFunct);
			hibFuncRoleId.setRole(hibRole);
			hibFuncRoleId.setState(hibPermission);
			SbiFuncRole hibFunctRole = new SbiFuncRole(hibFuncRoleId);
			hibFunctRole.setStateCd(permissionCd);
			session.save(hibFunctRole);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting function and role association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a functionality and a biobject into the exported database.
	 * 
	 * @param biobj The BIObject which is an element of the association
	 * @param funct The functionality object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjFunct(BIObject biobj, LowFunctionality funct, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();

			Integer biobjid = biobj.getId();
			Integer functid = funct.getId();
			String query = " from SbiObjFunc where id.sbiFunctions = " + functid +
			" and id.sbiObjects = " + biobjid;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiObjFuncId hibObjFunctId = new SbiObjFuncId();
			SbiFunctions hibFunct = (SbiFunctions)session.load(SbiFunctions.class, funct.getId());
			SbiObjects hibObj = (SbiObjects)session.load(SbiObjects.class, biobj.getId());
			hibObjFunctId.setSbiObjects(hibObj);
			hibObjFunctId.setSbiFunctions(hibFunct);
			SbiObjFunc hibObjFunct = new SbiObjFunc(hibObjFunctId);
			hibObjFunct.setProg(new Integer(0));
			session.save(hibObjFunct);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting function and object association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Exports the map catalogue (maps and features)
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	public void insertMapCatalogue(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			// controls if the maps are already inserted into export db
			Transaction tx = session.beginTransaction();
			String query = " from SbiGeoMaps";
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				// maps are already exported
				return;
			}
			tx.commit();

			insertMaps(session);
			insertFeatures(session);
			insertMapFeaturesAssociations(session);

		} catch (Exception e) {
			logger.error("Error while inserting map catalogue into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}

	}

	/**
	 * Insert the maps of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertMaps(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoMapsDAO mapsDAO = DAOFactory.getSbiGeoMapsDAO();
			List allMaps = mapsDAO.loadAllMaps();
			Iterator mapsIt = allMaps.iterator();
			while (mapsIt.hasNext()) {
				GeoMap map = (GeoMap) mapsIt.next();
				SbiGeoMaps hibMap = new SbiGeoMaps(map.getMapId());
				hibMap.setDescr(map.getDescr());
				hibMap.setFormat(map.getFormat());
				hibMap.setName(map.getName());
				hibMap.setUrl(map.getUrl());

				if (map.getBinId() == 0) {
					logger.warn("Map with id = " + map.getMapId() + " and name = " + map.getName() + 
					" has not binary content!!");
					hibMap.setBinContents(null);
				} else {
					SbiBinContents hibBinContent = new SbiBinContents();
					hibBinContent.setId(map.getBinId());
					byte[] content = DAOFactory.getBinContentDAO().getBinContent(map.getBinId());
					hibBinContent.setContent(content);
					hibMap.setBinContents(hibBinContent);

					session.save(hibBinContent);
				}

				session.save(hibMap);

			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting maps into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert the features of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertFeatures(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoFeaturesDAO featuresDAO = DAOFactory.getSbiGeoFeaturesDAO();
			List allFeatures = featuresDAO.loadAllFeatures();
			Iterator featureIt = allFeatures.iterator();
			while (featureIt.hasNext()) {
				GeoFeature feature = (GeoFeature) featureIt.next();
				SbiGeoFeatures hibFeature = new SbiGeoFeatures(feature.getFeatureId());
				hibFeature.setDescr(feature.getDescr());
				hibFeature.setName(feature.getName());
				hibFeature.setType(feature.getType());
				session.save(hibFeature);
			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting features into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert the association between maps and features of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertMapFeaturesAssociations(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoMapsDAO mapsDAO = DAOFactory.getSbiGeoMapsDAO();
			List allMaps = mapsDAO.loadAllMaps();
			ISbiGeoMapFeaturesDAO mapFeaturesDAO = DAOFactory.getSbiGeoMapFeaturesDAO();
			Iterator mapsIt = allMaps.iterator();
			while (mapsIt.hasNext()) {
				GeoMap map = (GeoMap) mapsIt.next();
				List mapFeatures = mapFeaturesDAO.loadFeaturesByMapId(new Integer(map.getMapId()));
				Iterator mapFeaturesIt = mapFeatures.iterator();
				while (mapFeaturesIt.hasNext()) {
					GeoFeature feature = (GeoFeature) mapFeaturesIt.next();
					GeoMapFeature mapFeature = mapFeaturesDAO.loadMapFeatures(new Integer(map.getMapId()), new Integer(feature.getFeatureId()));
					SbiGeoMapFeatures hibMapFeature = new SbiGeoMapFeatures();	
					SbiGeoMapFeaturesId hibMapFeatureId = new SbiGeoMapFeaturesId();			
					hibMapFeatureId.setMapId(mapFeature.getMapId());
					hibMapFeatureId.setFeatureId(mapFeature.getFeatureId());
					hibMapFeature.setId(hibMapFeatureId);
					hibMapFeature.setSvgGroup(mapFeature.getSvgGroup());
					hibMapFeature.setVisibleFlag(mapFeature.getVisibleFlag());
					session.save(hibMapFeature);
				}
			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting association between maps and features into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}






	/**
	 * Insert Model Instance Tree.
	 * 
	 * @param mi the Model Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List insertAllFromModelInstance(ModelInstance mi, Session session) throws EMFUserError {
		logger.debug("IN");

		biObjectToInsert=new ArrayList();

		//I want to insert the whole model instance tree, first of all I get the model instance root
		IModelInstanceDAO modInstDAO=DAOFactory.getModelInstanceDAO();
		ModelInstance miRoot=modInstDAO.loadModelInstanceRoot(mi);


		// insert the model (model instance root points to model root)
		logger.debug("Insert the model root and the tree");		

		// insert model tree starting from root.
		Model modelRoot=miRoot.getModel();
		insertModelTree(modelRoot, session);

		logger.debug("Insert the model Instance root and the tree");		

		//insert the Model Instanceroot
		insertModelInstanceTree(miRoot, session);

		logger.debug("OUT");
		return biObjectToInsert;
	}










	/**
	 * Insert Model Instance.
	 * 
	 * @param mi the Model Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelInstanceTree(ModelInstance mi, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiModelInst where kpiModelInst = " + mi.getId());

			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// main attributes			
			SbiKpiModelInst hibMi = new SbiKpiModelInst();
			hibMi.setKpiModelInst(mi.getId());
			hibMi.setName(mi.getName());
			hibMi.setLabel(mi.getLabel());
			hibMi.setDescription(mi.getDescription());
			hibMi.setStartDate(mi.getStartDate());
			hibMi.setEndDate(mi.getEndDate());
			hibMi.setModelUUID(mi.getModelUUID());

			// insert Parent
			if(mi.getParentId()!=null){
				SbiKpiModelInst hibKpiModelInstParent = (SbiKpiModelInst) session.load(SbiKpiModelInst.class, mi.getParentId());
				hibMi.setSbiKpiModelInst(hibKpiModelInstParent);
			}

			// model
			if(mi.getModel()!=null){
				SbiKpiModel hibModel = (SbiKpiModel) session.load(SbiKpiModel.class, mi.getModel().getId());
				hibMi.setSbiKpiModel(hibModel);
			}

			// Load tKpi Instance
			if (mi.getKpiInstance() != null) {
				KpiInstance kpiInstance=mi.getKpiInstance();
				insertKpiInstance(kpiInstance.getKpiInstanceId(), session);
				SbiKpiInstance hibKpiInst = (SbiKpiInstance) session.load(SbiKpiInstance.class, kpiInstance.getKpiInstanceId());
				hibMi.setSbiKpiInstance(hibKpiInst);

			}

			//load all organizational units
			List<OrganizationalUnitGrantNode> grants = DAOFactory.getOrganizationalUnitDAO().getGrants(mi.getId());
			if(grants != null){
				for (OrganizationalUnitGrantNode organizationalUnitGrantNode : grants) {
					OrganizationalUnitGrant organizationalUnitGrant = organizationalUnitGrantNode.getGrant();
					insertOrgUnitGrant(organizationalUnitGrant, session);
					insertOrgUnitGrantNodes(organizationalUnitGrantNode, session);
				}
			}


			Transaction tx = session.beginTransaction();
			session.save(hibMi);
			tx.commit();

			// Load all the model resources of the current instance model
			// after having inserted model instance
			IModelResourceDAO modelResourceDao=DAOFactory.getModelResourcesDAO();			
			List modelResources=modelResourceDao.loadModelResourceByModelId(mi.getId());
			for (Iterator iterator = modelResources.iterator(); iterator.hasNext();) {
				ModelResources modRes = (ModelResources) iterator.next();
				insertModelResources(modRes, session);
				// TODO: maybe insert also the set
			}

			Set modelInstanceChildren=new HashSet();
			logger.debug("insert current model instance children");
			// get the Model Instance children
			IModelInstanceDAO modelInstDao=DAOFactory.getModelInstanceDAO();
			ModelInstance modInstWithChildren=modelInstDao.loadModelInstanceWithChildrenById(mi.getId());
			List childrenList=modInstWithChildren.getChildrenNodes();
			if(childrenList!=null){
				for (Iterator iterator = childrenList.iterator(); iterator.hasNext();) {
					ModelInstance childNode = (ModelInstance) iterator.next();
					logger.debug("insert child "+childNode.getLabel());
					insertModelInstanceTree(childNode,session);				
					SbiKpiModelInst hibKpiModelInst = (SbiKpiModelInst) session.load(SbiKpiModelInst.class, childNode.getId());
					modelInstanceChildren.add(hibKpiModelInst);
				}
			}
			hibMi.setSbiKpiModelInsts(modelInstanceChildren);  // serve?




		} catch (Exception e) {
			logger.error("Error while inserting ModelInstance tree into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Model .
	 * 
	 * @param mod the Model
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelTree(Model mod, Session session) throws EMFUserError {
		logger.debug("IN");
		IModelDAO modelDao=DAOFactory.getModelDAO();
		try {



			Query hibQuery = session.createQuery(" from SbiKpiModel where kpiModelId = " + mod.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}


			// main attributes			
			SbiKpiModel hibMod = new SbiKpiModel();
			hibMod.setKpiModelId(mod.getId());
			hibMod.setKpiModelLabel(mod.getLabel());
			hibMod.setKpiModelCd(mod.getCode());
			hibMod.setKpiModelDesc(mod.getDescription());
			hibMod.setKpiModelNm(mod.getName());

			// insert Parent
			if(mod.getParentId()!=null){
				SbiKpiModel hibKpiModelParent = (SbiKpiModel) session.load(SbiKpiModel.class, mod.getParentId());
				hibMod.setSbiKpiModel(hibKpiModelParent);

			}

			// sbiDomain
			Criterion nameCriterrion = Expression.eq("valueCd", mod.getTypeCd());
			Criteria criteria = session.createCriteria(SbiDomains.class);
			criteria.add(nameCriterrion);	
			SbiDomains domainType = (SbiDomains) criteria.uniqueResult();
			hibMod.setModelType(domainType);

			// load kpi
			if (mod.getKpiId() != null) {
				Integer kpiId=mod.getKpiId();
				insertKpi(kpiId,session);
				SbiKpi sbiKpi= (SbiKpi) session.load(SbiKpi.class, mod.getKpiId());
				hibMod.setSbiKpi(sbiKpi);
			}

			// save current Model
			Transaction tx = session.beginTransaction();
			session.save(hibMod);
			tx.commit();
			logger.debug("current model "+mod.getCode()+" inserted");

			//manage insert of udp values

			List udpValues = DAOFactory.getUdpDAOValue().findByReferenceId(mod.getId(), "Model");
			if(udpValues != null && !udpValues.isEmpty()){
				for (Iterator iterator = udpValues.iterator(); iterator.hasNext();) {
					UdpValue udpValue = (UdpValue) iterator.next();
					insertUdpValue(udpValue, session);

				}
			}

			Set modelChildren=new HashSet();
			logger.debug("insert current model children");

			//Load model childred
			Model modWithChildren=modelDao.loadModelWithChildrenById(mod.getId());

			List childrenList=modWithChildren.getChildrenNodes();
			if(childrenList!=null){
				for (Iterator iterator = childrenList.iterator(); iterator.hasNext();) {
					Model childNode = (Model) iterator.next();
					logger.debug("insert child "+childNode.getCode());
					insertModelTree(childNode,session);				
					SbiKpiModel hibKpiModel = (SbiKpiModel) session.load(SbiKpiModel.class, childNode.getId());
					modelChildren.add(hibKpiModel);
				}
			}
			hibMod.setSbiKpiModels(modelChildren);

		} catch (Exception e) {
			logger.error("Error while inserting Model into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Kpi .
	 * 
	 * @param kpi the Kpi
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpi(Integer kpiId, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpi where kpiId = " + kpiId);
			List hibList = hibQuery.list();
			if(hibList!=null && !hibList.isEmpty()) {
				return;
			}
			// get the Kpi BO from id
			IKpiDAO kpiDao=DAOFactory.getKpiDAO();
			Kpi kpi=kpiDao.loadKpiById(kpiId);

			// main attributes			
			SbiKpi hibKpi = new SbiKpi();
			hibKpi.setKpiId(kpi.getKpiId());
			hibKpi.setCode(kpi.getCode());
			hibKpi.setDescription(kpi.getDescription());
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setName(kpi.getKpiName());
			hibKpi.setWeight(kpi.getStandardWeight());
			char isFather=kpi.getIsParent().equals(true)? 'T' : 'F';
			hibKpi.setFlgIsFather(new Character(isFather));
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setInputAttributes(kpi.getInputAttribute());
			hibKpi.setModelReference(kpi.getModelReference());
			hibKpi.setTargetAudience(kpi.getTargetAudience());
			hibKpi.setIsAdditive(kpi.getIsAdditive());

			if(kpi.getMeasureTypeId()!=null){
				SbiDomains measureType=(SbiDomains)session.load(SbiDomains.class, kpi.getMeasureTypeId());			
				hibKpi.setSbiDomainsByMeasureType(measureType);
			}
			if(kpi.getKpiTypeId()!=null){
				SbiDomains kpiType=(SbiDomains)session.load(SbiDomains.class, kpi.getKpiTypeId());			
				hibKpi.setSbiDomainsByKpiType(kpiType);
			}
			if(kpi.getMetricScaleId()!=null){
				SbiDomains metricScaleType=(SbiDomains)session.load(SbiDomains.class, kpi.getMetricScaleId());			
				hibKpi.setSbiDomainsByMetricScaleType(metricScaleType);
			}

			// load dataset
			if (kpi.getKpiDsId() != null) {    
				Integer dsID = kpi.getKpiDsId();				
				GuiGenericDataSet guiGenericDataSet = DAOFactory.getDataSetDAO().loadDataSetById(dsID);
				if(guiGenericDataSet!=null){
					insertDataSet(guiGenericDataSet, session);
					SbiDataSetConfig sbiDs= (SbiDataSetConfig) session.load(SbiDataSetConfig.class, guiGenericDataSet.getDsId());
					hibKpi.setSbiDataSet(sbiDs);
				}
			}

			// load threshold
			if (kpi.getThreshold() != null) {
				Threshold th=kpi.getThreshold();
				insertThreshold(th, session);
				SbiThreshold sbiTh= (SbiThreshold) session.load(SbiThreshold.class, th.getId());
				hibKpi.setSbiThreshold(sbiTh);
			}

			// Measure Unit   ???
			if(kpi.getScaleCode()!=null && !kpi.getScaleCode().equalsIgnoreCase("")){
				IMeasureUnitDAO muDao=DAOFactory.getMeasureUnitDAO();
				MeasureUnit mu=muDao.loadMeasureUnitByCd(kpi.getScaleCode());
				insertMeasureUnit(mu, session);
				SbiMeasureUnit sbiMu= (SbiMeasureUnit) session.load(SbiMeasureUnit.class, mu.getId());
				hibKpi.setSbiMeasureUnit(sbiMu);
			}

			Transaction tx = session.beginTransaction();
			Integer kpiIdReturned = (Integer)session.save(hibKpi);
			tx.commit();

			List kpiDocsList = kpi.getSbiKpiDocuments();
			Iterator i = kpiDocsList.iterator();
			while (i.hasNext()) {
				KpiDocuments doc = (KpiDocuments) i.next();
				String label = doc.getBiObjLabel();

				IBIObjectDAO biobjDAO = DAOFactory.getBIObjectDAO();
				BIObject biobj = biobjDAO.loadBIObjectByLabel(label);
				if(biobj!=null){
					insertBIObject(biobj, session, true);
					doc.setBiObjId(biobj.getId());				
				}

				Integer origDocId = doc.getBiObjId();
				Criterion labelCriterrion = Expression.eq("label",label);
				Criteria criteria = session.createCriteria(SbiObjects.class);
				criteria.add(labelCriterrion);
				SbiObjects hibObject = (SbiObjects) criteria.uniqueResult();

				if(hibObject!=null){
					SbiKpiDocument temp = new SbiKpiDocument();
					temp.setSbiKpi(hibKpi);
					temp.setSbiObjects(hibObject);
					KpiDocuments docK = kpiDao.loadKpiDocByKpiIdAndDocId(kpiId, origDocId);
					if(docK!=null && docK.getKpiDocId()!=null){
						temp.setIdKpiDoc(docK.getKpiDocId());
						Transaction tx2 = session.beginTransaction();
						session.save(temp);
						tx2.commit();
					}
				}
			}
			//manage insert of kpi relations
			List<KpiRel> relations = DAOFactory.getKpiDAO().loadKpiRelListByParentId(kpi.getKpiId());
			if(relations != null && !relations.isEmpty()){
				for (int j = 0; j < relations.size(); j++) {
					KpiRel kpiRel = (KpiRel)relations.get(j);
					//insert child kpi first
					insertKpi(kpiRel.getKpiChildId(), session);
					insertKpiRel(kpiRel, session);					
				}

			}

			//manage insert of udp values

			List udpValues = DAOFactory.getUdpDAOValue().findByReferenceId(kpiId, "Kpi");
			if(udpValues != null && !udpValues.isEmpty()){
				for (Iterator iterator = udpValues.iterator(); iterator.hasNext();) {
					UdpValue udpValue = (UdpValue) iterator.next();
					insertUdpValue(udpValue, session);

				}
			}
		} catch (Exception e) {
			logger.error("Error while inserting kpi into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert Kpi child for relations.
	 * 
	 * @param kpiId the Kpi child id
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public SbiKpi insertKpiNorelations(Integer kpiId, Session session) throws EMFUserError {
		logger.debug("IN");
		SbiKpi hibKpi = null;
		try {
			Query hibQuery = session.createQuery(" from SbiKpi where kpiId = " + kpiId);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return null;
			}
			// get the Kpi BO from id
			IKpiDAO kpiDao=DAOFactory.getKpiDAO();
			Kpi kpi=kpiDao.loadKpiById(kpiId);

			// main attributes			
			hibKpi = new SbiKpi();
			hibKpi.setKpiId(kpi.getKpiId());
			hibKpi.setCode(kpi.getCode());
			hibKpi.setDescription(kpi.getDescription());
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setName(kpi.getKpiName());
			// Weight???	hibKpi.setWeight(kpi.get)
			hibKpi.setWeight(kpi.getStandardWeight());
			char isFather=kpi.getIsParent().equals(true)? 'T' : 'F';
			hibKpi.setFlgIsFather(new Character(isFather));
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setInputAttributes(kpi.getInputAttribute());
			hibKpi.setModelReference(kpi.getModelReference());
			hibKpi.setTargetAudience(kpi.getTargetAudience());
			hibKpi.setIsAdditive(kpi.getIsAdditive());

			if(kpi.getMeasureTypeId()!=null){
				SbiDomains measureType=(SbiDomains)session.load(SbiDomains.class, kpi.getMeasureTypeId());			
				hibKpi.setSbiDomainsByMeasureType(measureType);
			}
			if(kpi.getKpiTypeId()!=null){
				SbiDomains kpiType=(SbiDomains)session.load(SbiDomains.class, kpi.getKpiTypeId());			
				hibKpi.setSbiDomainsByKpiType(kpiType);
			}
			if(kpi.getMetricScaleId()!=null){
				SbiDomains metricScaleType=(SbiDomains)session.load(SbiDomains.class, kpi.getMetricScaleId());			
				hibKpi.setSbiDomainsByMetricScaleType(metricScaleType);
			}

			// load dataset
			if (kpi.getKpiDsId() != null) {    
				Integer dsID = kpi.getKpiDsId();				
				GuiGenericDataSet guiGenericDataSet = DAOFactory.getDataSetDAO().loadDataSetById(dsID);
				if(guiGenericDataSet!=null){
					insertDataSet(guiGenericDataSet, session);
					SbiDataSetConfig sbiDs= (SbiDataSetConfig) session.load(SbiDataSetConfig.class, guiGenericDataSet.getDsId());
					hibKpi.setSbiDataSet(sbiDs);
				}
			}

			// load threshold
			if (kpi.getThreshold() != null) {
				Threshold th=kpi.getThreshold();
				insertThreshold(th, session);
				SbiThreshold sbiTh= (SbiThreshold) session.load(SbiThreshold.class, th.getId());
				hibKpi.setSbiThreshold(sbiTh);
			}

			// Measure Unit   ???
			if(kpi.getScaleCode()!=null && !kpi.getScaleCode().equalsIgnoreCase("")){
				IMeasureUnitDAO muDao=DAOFactory.getMeasureUnitDAO();
				MeasureUnit mu=muDao.loadMeasureUnitByCd(kpi.getScaleCode());
				insertMeasureUnit(mu, session);
				SbiMeasureUnit sbiMu= (SbiMeasureUnit) session.load(SbiMeasureUnit.class, mu.getId());
				hibKpi.setSbiMeasureUnit(sbiMu);
			}

			Transaction tx = session.beginTransaction();
			Integer kpiIdReturned = (Integer)session.save(hibKpi);
			tx.commit();

			List kpiDocsList = kpi.getSbiKpiDocuments();
			Iterator i = kpiDocsList.iterator();
			while (i.hasNext()) {
				KpiDocuments doc = (KpiDocuments) i.next();
				String label = doc.getBiObjLabel();

				IBIObjectDAO biobjDAO = DAOFactory.getBIObjectDAO();
				BIObject biobj = biobjDAO.loadBIObjectByLabel(label);
				if(biobj!=null){
					insertBIObject(biobj, session, true);
					doc.setBiObjId(biobj.getId());				
				}

				Integer origDocId = doc.getBiObjId();
				Criterion labelCriterrion = Expression.eq("label",label);
				Criteria criteria = session.createCriteria(SbiObjects.class);
				criteria.add(labelCriterrion);
				SbiObjects hibObject = (SbiObjects) criteria.uniqueResult();

				if(hibObject!=null){
					SbiKpiDocument temp = new SbiKpiDocument();
					temp.setSbiKpi(hibKpi);
					temp.setSbiObjects(hibObject);
					KpiDocuments docK = kpiDao.loadKpiDocByKpiIdAndDocId(kpiId, origDocId);
					if(docK!=null && docK.getKpiDocId()!=null){
						temp.setIdKpiDoc(docK.getKpiDocId());
						Transaction tx2 = session.beginTransaction();
						session.save(temp);
						tx2.commit();
					}
				}
			}

			//manage insert of udp values

			List udpValues = DAOFactory.getUdpDAOValue().findByReferenceId(kpiId, "Kpi");
			if(udpValues != null && !udpValues.isEmpty()){
				for (Iterator iterator = udpValues.iterator(); iterator.hasNext();) {
					UdpValue udpValue = (UdpValue) iterator.next();
					insertUdpValue(udpValue, session);

				}
			}
		} catch (Exception e) {
			logger.error("Error while inserting kpi into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
			return hibKpi;


		}
	}


	/**
	 * Insert Kpi Instance.
	 * 
	 * @param kpiInst the Kpi Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiInstance(Integer kpiInstId, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiInstance where idKpiInstance = " + kpiInstId);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// recover kpi instance from Id
			IKpiInstanceDAO kpiInstDAO=DAOFactory.getKpiInstanceDAO();
			KpiInstance kpiInst=kpiInstDAO.loadKpiInstanceById(kpiInstId);

			// main attributes			
			SbiKpiInstance hibKpiInst = new SbiKpiInstance();
			hibKpiInst.setIdKpiInstance(kpiInst.getKpiInstanceId());
			hibKpiInst.setBeginDt(kpiInst.getD());
			hibKpiInst.setWeight(kpiInst.getWeight());
			hibKpiInst.setTarget(kpiInst.getTarget());

			if(kpiInst.getChartTypeId()!=null){
				SbiDomains chartType=(SbiDomains)session.load(SbiDomains.class, kpiInst.getChartTypeId());			
				hibKpiInst.setChartType(chartType);
			}

			// Kpi
			if (kpiInst.getKpi()!=null) {    
				insertKpi(kpiInst.getKpi(), session);
				SbiKpi sbiKpi= (SbiKpi) session.load(SbiKpi.class, kpiInst.getKpi());
				hibKpiInst.setSbiKpi(sbiKpi);
			}

			// load threshold
			if (kpiInst.getThresholdId() != null) {
				IThresholdDAO thresholdDAO=DAOFactory.getThresholdDAO();
				Threshold th=thresholdDAO.loadThresholdById(kpiInst.getThresholdId());
				insertThreshold(th, session);
				SbiThreshold sbiTh= (SbiThreshold) session.load(SbiThreshold.class, th.getId());
				hibKpiInst.setSbiThreshold(sbiTh);
			}

			// load measureUnit!
			if(kpiInst.getScaleCode()!=null){
				IMeasureUnitDAO muDao=DAOFactory.getMeasureUnitDAO();
				MeasureUnit mu=muDao.loadMeasureUnitByCd(kpiInst.getScaleCode());
				insertMeasureUnit(mu, session);
				SbiMeasureUnit sbiMu= (SbiMeasureUnit) session.load(SbiMeasureUnit.class, mu.getId());
				hibKpiInst.setSbiMeasureUnit(sbiMu);
			}

			// Insert KPI Instance

			Transaction tx = session.beginTransaction();
			session.save(hibKpiInst);
			tx.commit();



			// after inserted Kpi Instance insert periods		
			// load all alarms
			ISbiAlarmDAO sbiAlarmDAO=DAOFactory.getAlarmDAO();
			List<Alarm> alarmsToLoad=sbiAlarmDAO.loadAllByKpiInstId(kpiInstId);
			for (Iterator iterator = alarmsToLoad.iterator(); iterator.hasNext();) {
				Alarm alarm = (Alarm) iterator.next();
				insertAlarm(alarm, session);				

			}


			// after inserted Kpi Instance insert periods
			// Load all the kpi inst period and the periodicity s well
			IKpiInstPeriodDAO kpiInstPeriodDao=DAOFactory.getKpiInstPeriodDAO();			
			List kpiInstPeriodList=kpiInstPeriodDao.loadKpiInstPeriodId(kpiInst.getKpiInstanceId());
			for (Iterator iterator = kpiInstPeriodList.iterator(); iterator.hasNext();) {
				KpiInstPeriod modKpiInst = (KpiInstPeriod) iterator.next();
				insertKpiInstancePeriod(modKpiInst, session);
			}



		} catch (Exception e) {
			logger.error("Error while inserting kpi instance into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Threshold .
	 * 
	 * @param th the Threshold
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertThreshold(Threshold th, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiThreshold where thresholdId = " + th.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiDomains thresholdType=(SbiDomains)session.load(SbiDomains.class, th.getThresholdTypeId());

			// main attributes			
			SbiThreshold hibTh = new SbiThreshold();
			hibTh.setThresholdId(th.getId());
			hibTh.setName(th.getName());
			hibTh.setCode(th.getCode());
			hibTh.setDescription(th.getDescription());
			hibTh.setThresholdType(thresholdType);
			Transaction tx = session.beginTransaction();
			session.save(hibTh);
			tx.commit();

			// load Threshold Value
			if (th.getThresholdValues() != null && th.getThresholdValues().size()>0) {
				Set thresholdValues=new HashSet(0);
				for (Iterator iterator = th.getThresholdValues().iterator(); iterator.hasNext();) {
					ThresholdValue thValue = (ThresholdValue) iterator.next();
					insertThresholdValue(thValue, session, hibTh);
					Integer thValueId=thValue.getId();
					SbiThresholdValue sbiTh= (SbiThresholdValue) session.load(SbiThresholdValue.class, thValue.getId());
					thresholdValues.add(sbiTh);
				}
				//hibTh.setSbiThresholdValues(thresholdValues);
			}

		} catch (Exception e) {
			logger.error("Error while inserting dataSource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Threshold Value.
	 * 
	 * @param th the Threshold Value
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertThresholdValue(ThresholdValue thValue, Session session, SbiThreshold sbiTh) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiThresholdValue where idThresholdValue = " + thValue.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiThresholdValue hibThValue = new SbiThresholdValue();
			
			if(thValue.getSeverityId()!=null){
				SbiDomains severity=(SbiDomains)session.load(SbiDomains.class, thValue.getSeverityId());
				hibThValue.setSeverity(severity);	
			}
			
			hibThValue.setIdThresholdValue(thValue.getId());
			hibThValue.setLabel(thValue.getLabel());
			hibThValue.setMaxValue(thValue.getMaxValue());
			hibThValue.setMinValue(thValue.getMinValue());
			hibThValue.setMinClosed(thValue.getMinClosed());
			hibThValue.setMaxClosed(thValue.getMaxClosed());
			hibThValue.setThValue(thValue.getValue());

			String colour=thValue.getColourString();
			hibThValue.setColour(colour);
			hibThValue.setPosition(thValue.getPosition());
			

			// put association with Threshold
			hibThValue.setSbiThreshold(sbiTh);

			Transaction tx = session.beginTransaction();			
			session.save(hibThValue);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting threshold value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Measure Unit.
	 * 
	 * @param mu the Measure Unit
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertMeasureUnit(MeasureUnit mu, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiMeasureUnit where idMeasureUnit = " + mu.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiMeasureUnit hibMu = new SbiMeasureUnit();
			hibMu.setIdMeasureUnit(mu.getId());
			hibMu.setName(mu.getName());
			hibMu.setScaleCd(mu.getScaleCd());
			hibMu.setScaleNm(mu.getScaleNm());

			SbiDomains scaleType=(SbiDomains)session.load(SbiDomains.class, mu.getScaleTypeId());

			hibMu.setScaleType(scaleType);

			Transaction tx = session.beginTransaction();			
			session.save(hibMu);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting threshold value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




	/**
	 * Insert Kpi Instance Periodicity.
	 * 
	 * @param kpiPeriod kpiInstancePeriodicity
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiInstancePeriod( KpiInstPeriod kpiInstPeriod, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiInstPeriod where kpiInstPeriodId = " + kpiInstPeriod.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiInstPeriod hibKpiInstPeriod = new SbiKpiInstPeriod();
			hibKpiInstPeriod.setKpiInstPeriodId(kpiInstPeriod.getId());
			hibKpiInstPeriod.setDefault_(kpiInstPeriod.getDefaultValue());

			// Kpi instance should be already inserted

			if (kpiInstPeriod.getKpiInstId()!= null) {
				Integer kpiInstPeriodId=kpiInstPeriod.getKpiInstId();
				SbiKpiInstance sbiKpiInstance= (SbiKpiInstance) session.load(SbiKpiInstance.class, kpiInstPeriodId);
				if(sbiKpiInstance!=null){
					hibKpiInstPeriod.setSbiKpiInstance(sbiKpiInstance);
				}
			}

			// load Periodicity

			if (kpiInstPeriod.getPeriodicityId() != null) {
				Integer periodicityId=kpiInstPeriod.getPeriodicityId();
				IPeriodicityDAO periodicityDAO=DAOFactory.getPeriodicityDAO();
				Periodicity period=periodicityDAO.loadPeriodicityById(periodicityId);
				insertPeriodicity(period, session);
				SbiKpiPeriodicity sbiKpiPeriodicity= (SbiKpiPeriodicity) session.load(SbiKpiPeriodicity.class, period.getIdKpiPeriodicity());
				if(sbiKpiPeriodicity!=null){
					hibKpiInstPeriod.setSbiKpiPeriodicity(sbiKpiPeriodicity);
				}
			}



			Transaction tx = session.beginTransaction();			
			session.save(hibKpiInstPeriod);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting kpiInstPeriod value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Periodicity.
	 * 
	 * @param mu the Measure Unit
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertPeriodicity(Periodicity per, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiPeriodicity where idKpiPeriodicity = " + per.getIdKpiPeriodicity());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiPeriodicity hibPer = new SbiKpiPeriodicity();
			hibPer.setIdKpiPeriodicity(per.getIdKpiPeriodicity());
			hibPer.setName(per.getName());
			hibPer.setChronString(per.getCronString());
			hibPer.setDays(per.getDays());
			hibPer.setHours(per.getHours());
			hibPer.setMinutes(per.getMinutes());
			hibPer.setMonths(per.getMonths());
			hibPer.setStartDate(null);
			Transaction tx = session.beginTransaction();			
			session.save(hibPer);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting Periodicity into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}






	/**
	 * Insert ModelResources.
	 * 
	 * @param modRes the modelResource
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelResources(ModelResources modRes, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiModelResources where kpiModelResourcesId = " + modRes.getModelResourcesId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiModelResources hibModRes = new SbiKpiModelResources();
			hibModRes.setKpiModelResourcesId(modRes.getModelResourcesId());

			// Model instance should be already inserted

			if (modRes.getModelInstId() != null) {
				Integer modelInstId=modRes.getModelInstId();
				SbiKpiModelInst sbiKpiModInst= (SbiKpiModelInst) session.load(SbiKpiModelInst.class, modelInstId);
				if(sbiKpiModInst!=null){
					hibModRes.setSbiKpiModelInst(sbiKpiModInst);
				}
			}

			// load resource

			if (modRes.getResourceId() != null) {
				Integer resId=modRes.getResourceId();
				IResourceDAO resDAO=DAOFactory.getResourceDAO();
				Resource res=resDAO.loadResourceById(resId);

				insertResource(res, session);
				SbiResources sbiRes= (SbiResources) session.load(SbiResources.class, res.getId());
				if(sbiRes!=null){
					hibModRes.setSbiResources(sbiRes);
				}
			}


			Transaction tx = session.beginTransaction();			
			session.save(hibModRes);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting model resources value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




	/**
	 * Insert Resources.
	 * 
	 * @param res the resource
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertResource(Resource res, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiResources where resourceId = " + res.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiResources hibRes = new SbiResources();

			hibRes.setResourceId(res.getId());
			hibRes.setResourceCode(res.getCode());
			hibRes.setResourceName(res.getName());
			hibRes.setResourceDescr(res.getDescr());
			hibRes.setColumnName(res.getColumn_name());
			hibRes.setTableName(res.getTable_name());

			//sbi Domains
			if(res.getType()!=null){
				SbiDomains type=(SbiDomains)session.load(SbiDomains.class, res.getTypeId());			
				hibRes.setType(type);
			}

			Transaction tx = session.beginTransaction();			
			session.save(hibRes);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting resource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	public List getBiObjectToInsert() {
		return biObjectToInsert;
	}

	public void setBiObjectToInsert(List biObjectToInsert) {
		this.biObjectToInsert = biObjectToInsert;
	}




	/**
	 * Insert Alarm.
	 * 
	 * @param res the Alarm
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAlarm(Alarm alarm, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiAlarm where id = " + alarm.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiAlarm hibAlarm = new SbiAlarm();

			hibAlarm.setId(alarm.getId());
			hibAlarm.setDescr(alarm.getDescr());
			hibAlarm.setLabel(alarm.getLabel());
			hibAlarm.setName(alarm.getName());
			hibAlarm.setText(alarm.getText());
			hibAlarm.setUrl(alarm.getUrl()); 
			hibAlarm.setAutoDisabled(alarm.isAutoDisabled()); 
			hibAlarm.setSingleEvent(alarm.isSingleEvent());

			// kpi Instance (already inserted)
			if(alarm.getIdKpiInstance()!=null){
				SbiKpiInstance sbiKpiInst=(SbiKpiInstance)session.load(SbiKpiInstance.class, alarm.getIdKpiInstance());			
				hibAlarm.setSbiKpiInstance(sbiKpiInst);
			}

			// Threshold Value (already inserted)
			if(alarm.getIdThresholdValue()!=null){
				SbiThresholdValue sbiThValue=(SbiThresholdValue)session.load(SbiThresholdValue.class, alarm.getIdThresholdValue());			
				hibAlarm.setSbiThresholdValue(sbiThValue);
			}

			// SbiDomains modality

			if(alarm.getModalityId()!=null){
				SbiDomains modality=(SbiDomains)session.load(SbiDomains.class, alarm.getModalityId());			
				hibAlarm.setModality(modality);
			}

			// insert all the contacts
			Set<SbiAlarmContact> listSbiContacts = new HashSet<SbiAlarmContact>();
			if(alarm.getSbiAlarmContacts()!=null){
				for (Iterator iterator = alarm.getSbiAlarmContacts().iterator(); iterator.hasNext();) {
					AlarmContact alarmContact = (AlarmContact) iterator.next();
					insertAlarmContact(alarmContact, session);
					SbiAlarmContact sbiAlCon=(SbiAlarmContact)session.load(SbiAlarmContact.class, alarmContact.getId());			
					listSbiContacts.add(sbiAlCon);
				}
			}
			hibAlarm.setSbiAlarmContacts(listSbiContacts);	



			Transaction tx = session.beginTransaction();			
			session.save(hibAlarm);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting alarm into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}











	/**
	 * Insert Alarm Contact
	 * 
	 * @param con the Alarm Contact
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAlarmContact(AlarmContact con, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiAlarmContact where id = " + con.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiAlarmContact hibCon = new SbiAlarmContact();

			hibCon.setId(con.getId());
			hibCon.setEmail(con.getEmail());
			hibCon.setMobile(con.getMobile());
			hibCon.setName(con.getName());
			hibCon.setResources(con.getResources());

			Transaction tx = session.beginTransaction();			
			session.save(hibCon);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting alarm contact into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert BinContent.
	 * 
	 * @param id and content 
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBinContet(Integer idContent, byte[] content, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiBinContents s where s.id = " + idContent);
			List hibRes = hibQuery.list();
			if(!hibRes.isEmpty()) {
				return;
			} 

			SbiBinContents hibContent = new SbiBinContents();
			hibContent.setId(idContent);
			hibContent.setContent(content);
			Transaction tx = session.beginTransaction();			
			session.save(hibContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting binContent into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert KpiRel .
	 * 
	 * @param kpiRel the KpiRel
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiRel(KpiRel kpiRel, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiRel where kpiRelId = " + kpiRel.getKpiRelId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 
			SbiKpiRel hibRel = new SbiKpiRel();
			SbiKpi kpiChild=(SbiKpi)session.load(SbiKpi.class, kpiRel.getKpiChildId());

			try {
				kpiChild.getKpiId();
				logger.error("kpi child id= "+kpiChild.getKpiId());

				hibRel.setSbiKpiByKpiChildId(kpiChild);
				logger.error("set in try__ok: kpi child saved before");
			}catch(Throwable t){
				logger.error("set in try__ok: kpi child didn't exist");
			}

			SbiKpi kpiFather=(SbiKpi)session.load(SbiKpi.class, kpiRel.getKpiFatherId());

			// main attributes			

			hibRel.setParameter(kpiRel.getParameter());			
			hibRel.setSbiKpiByKpiFatherId(kpiFather);
			hibRel.setKpiRelId(kpiRel.getKpiRelId());
			Transaction tx = session.beginTransaction();
			session.save(hibRel);
			logger.error("saved rel");
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting kpi relation into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert Udp .
	 * 
	 * @param udp the Udp
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertUdp(Udp udp, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiUdp where udpId = " + udp.getUdpId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiDomains udpType=(SbiDomains)session.load(SbiDomains.class, udp.getDataTypeId());

			// main attributes			
			SbiUdp hibUdp = new SbiUdp();
			hibUdp.setDescription(udp.getDescription());
			hibUdp.setFamilyId(udp.getFamilyId());
			hibUdp.setIsMultivalue(udp.getMultivalue());
			hibUdp.setLabel(udp.getLabel());
			hibUdp.setName(udp.getName());

			hibUdp.setTypeId(udpType.getValueId());
			hibUdp.setUdpId(udp.getUdpId());

			Transaction tx = session.beginTransaction();
			session.save(hibUdp);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting udp into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Udp Value.
	 * 
	 * @param udpValue the Udp Value
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertUdpValue(UdpValue udpValue, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiUdpValue where udpValueId = " + udpValue.getUdpValueId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiUdp udp =(SbiUdp)session.load(SbiUdp.class, udpValue.getUdpId());

			// main attributes			
			SbiUdpValue hibUdpValue = new SbiUdpValue();
			hibUdpValue.setBeginTs(udpValue.getBeginTs());
			hibUdpValue.setEndTs(udpValue.getEndTs());
			hibUdpValue.setFamily(udpValue.getFamily());
			hibUdpValue.setLabel(udpValue.getLabel());
			hibUdpValue.setName(udpValue.getName());
			hibUdpValue.setProg(udpValue.getProg());
			hibUdpValue.setReferenceId(udpValue.getReferenceId());
			hibUdpValue.setSbiUdp(udp);
			hibUdpValue.setUdpValueId(udpValue.getUdpValueId());
			hibUdpValue.setValue(udpValue.getValue());

			Transaction tx = session.beginTransaction();			
			session.save(hibUdpValue);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting udp value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	/**
	 * Insert OU .
	 * 
	 * @param ou the OU
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertOu(OrganizationalUnit ou, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiOrgUnit o where o.id = " + ou.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 
			// main attributes			
			SbiOrgUnit hibOu = new SbiOrgUnit();
			hibOu.setDescription(ou.getDescription());
			hibOu.setId(ou.getId());
			hibOu.setLabel(ou.getLabel());
			hibOu.setName(ou.getName());

			Transaction tx = session.beginTransaction();
			session.save(hibOu);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting ou into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	/**
	 * Insert Hierarchy .
	 * 
	 * @param hier the Hierarchy
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertHierarchy(OrganizationalUnitHierarchy hier, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiOrgUnitHierarchies h where h.id = " + hier.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 
			// main attributes			
			SbiOrgUnitHierarchies hibHier = new SbiOrgUnitHierarchies();
			hibHier.setDescription(hier.getDescription());
			hibHier.setId(hier.getId());
			hibHier.setLabel(hier.getLabel());
			hibHier.setName(hier.getName());
			hibHier.setTarget(hier.getTarget());
			hibHier.setCompany(hier.getCompany());

			Transaction tx = session.beginTransaction();
			session.save(hibHier);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting Hierarchy into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	/**
	 * Insert ou node .
	 * 
	 * @param node the ou node
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertOuNode(OrganizationalUnitNode node, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiOrgUnitNodes n where n.nodeId = " + node.getNodeId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 
			SbiOrgUnit ou = (SbiOrgUnit)session.load(SbiOrgUnit.class, node.getOu().getId());
			SbiOrgUnitHierarchies hier =(SbiOrgUnitHierarchies)session.load(SbiOrgUnitHierarchies.class, node.getHierarchy().getId());
			SbiOrgUnitNodes parent = null;
			if(node.getParentNodeId() != null){
				parent =(SbiOrgUnitNodes)session.load(SbiOrgUnitNodes.class, node.getParentNodeId());
			}
			// main attributes			
			SbiOrgUnitNodes hibNode = new SbiOrgUnitNodes();
			hibNode.setNodeId(node.getNodeId());
			hibNode.setPath(node.getPath());
			hibNode.setSbiOrgUnit(ou);
			hibNode.setSbiOrgUnitHierarchies(hier);
			hibNode.setSbiOrgUnitNodes(parent);

			Transaction tx = session.beginTransaction();
			session.save(hibNode);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting ou node into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	/**
	 * Insert all grants for model instance node.
	 * 
	 * @param grant the OrganizationalUnitGrant
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertOrgUnitGrant(OrganizationalUnitGrant grant, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiOrgUnitGrant where id = " + grant.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiOrgUnitHierarchies hier =(SbiOrgUnitHierarchies)session.load(SbiOrgUnitHierarchies.class, grant.getHierarchy().getId());
			SbiKpiModelInst mi =(SbiKpiModelInst)session.load(SbiKpiModelInst.class, grant.getModelInstance().getId());

			// main attributes			
			SbiOrgUnitGrant hibGrant = new SbiOrgUnitGrant();
			hibGrant.setDescription(grant.getDescription());
			hibGrant.setEndDate(grant.getEndDate());
			hibGrant.setId(grant.getId());
			hibGrant.setLabel(grant.getLabel());
			hibGrant.setName(grant.getName());
			hibGrant.setSbiKpiModelInst(mi);
			hibGrant.setSbiOrgUnitHierarchies(hier);
			hibGrant.setStartDate(grant.getStartDate());
			hibGrant.setIsAvailable(grant.getIsAvailable());

			Transaction tx = session.beginTransaction();			
			session.save(hibGrant);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting grants into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	/**
	 * Insert all grants nodes for model instance node.
	 * 
	 * @param ou the OrganizationalUnitGrantNode
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertOrgUnitGrantNodes(OrganizationalUnitGrantNode ou, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiOrgUnitGrantNodes s where s.id.nodeId = " + ou.getOuNode().getNodeId()
					+" and s.id.kpiModelInstNodeId = "+ou.getModelInstanceNode().getModelInstanceNodeId()
					+" and s.id.grantId = "+ou.getGrant().getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiKpiModelInst mi =(SbiKpiModelInst)session.load(SbiKpiModelInst.class, ou.getModelInstanceNode().getModelInstanceNodeId());
			SbiOrgUnitGrant g =(SbiOrgUnitGrant)session.load(SbiOrgUnitGrant.class, ou.getGrant().getId());
			SbiOrgUnitNodes n =(SbiOrgUnitNodes)session.load(SbiOrgUnitNodes.class, ou.getOuNode().getNodeId());
			// main attributes			
			SbiOrgUnitGrantNodes hibGrant = new SbiOrgUnitGrantNodes();
			SbiOrgUnitGrantNodesId id = new SbiOrgUnitGrantNodesId();
			id.setKpiModelInstNodeId(ou.getModelInstanceNode().getModelInstanceNodeId());
			id.setGrantId(ou.getGrant().getId());
			id.setNodeId(ou.getOuNode().getNodeId());
			hibGrant.setId(id);		


			hibGrant.setSbiKpiModelInst(mi);
			hibGrant.setSbiOrgUnitGrant(g);
			hibGrant.setSbiOrgUnitNodes(n);

			Transaction tx = session.beginTransaction();			
			session.save(hibGrant);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting grants into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}
	//Query hibQuery = session.createQuery(" from SbiOrgUnitGrant where grant.id = " + ou.getGrant().getId());
}

