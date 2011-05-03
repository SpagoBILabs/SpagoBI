/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.dao;

import it.eng.qbe.dataset.QbeDataSet;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQbeDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.DataSourceDAOHibImpl;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.exception.ConstraintViolationException;

public class DataSetDAOImpl extends AbstractHibernateDAO implements IDataSetDAO  {

	static private Logger logger = Logger.getLogger(DataSetDAOImpl.class);

	public static final String JDBC_DS_TYPE = "Query";
	public static final String FILE_DS_TYPE = "File";
	public static final String SCRIPT_DS_TYPE = "Script";
	public static final String JCLASS_DS_TYPE = "Java Class";
	public static final String WS_DS_TYPE = "Web Service";
	public static final String QBE_DS_TYPE = "Qbe";

	/*****************USED by new GUI******/
	/**
	 * Delete data set.
	 * @param dsID the a data set ID
	 * @throws EMFUserError the EMF user error
	 */
	public void deleteDataSet(Integer dsID) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiDataSetConfig hibDataSet = (SbiDataSetConfig) aSession.load(SbiDataSetConfig.class,dsID);
			aSession.delete(hibDataSet);

			tx.commit();			
		}  catch (ConstraintViolationException cve) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Impossible to delete DataSet", cve);
			throw new EMFUserError(EMFErrorSeverity.WARNING, 10014);

		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Error while deleting the DataSet with id " + ((dsID == null)?"":dsID.toString()), e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			aSession.close();
		}
	}

	/**
	 * Delete the inactive dataset version.
	 * @param dsVerionID the a data set version ID
	 * @throws EMFUserError the EMF user error
	 */
	public boolean deleteInactiveDataSetVersion(Integer dsVerionID) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		boolean deleted = false;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			if(dsVerionID!=null){
				SbiDataSetHistory hibDataSet = (SbiDataSetHistory) aSession.load(SbiDataSetHistory.class,dsVerionID);
				if(hibDataSet!=null && !hibDataSet.isActive()){
					aSession.delete(hibDataSet);
					tx.commit();	
					deleted = true;
				}	
			}
		}  catch (ConstraintViolationException cve) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Impossible to delete DataSet Version", cve);
			throw new EMFUserError(EMFErrorSeverity.WARNING, 10014);

		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Error while deleting the DataSet with varsionId " + ((dsVerionID == null)?"":dsVerionID.toString()), e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			aSession.close();
		}
		return deleted;
	}

	/**
	 * Delete all inactive dataset versions.
	 * @param dsID the a data set fo which all old versions need to eb deleted
	 * @throws EMFUserError the EMF user error
	 */
	public void deleteAllInactiveDataSetVersions(Integer dsID) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		boolean deleted = false;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			if(dsID!=null){
				Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
				hibQuery.setBoolean(0, false);
				hibQuery.setInteger(1, dsID);	
				List toBeDeleted = hibQuery.list();
				if(toBeDeleted!=null && !toBeDeleted.isEmpty()){
					Iterator it = toBeDeleted.iterator();
					while(it.hasNext()){
						SbiDataSetHistory hibDataSet = (SbiDataSetHistory) it.next();
						if(hibDataSet!=null && !hibDataSet.isActive()){
							aSession.delete(hibDataSet);			
						}
					}
					tx.commit();
				}	
			}					
		}  catch (ConstraintViolationException cve) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Impossible to delete All DataSet Version", cve);
			throw new EMFUserError(EMFErrorSeverity.WARNING, 10014);

		} catch (HibernateException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error("Error while deleting the Older Versions of DataSet with id " + ((dsID == null)?"":dsID.toString()), e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, 101);

		} finally {
			aSession.close();
		}
	}

	/**
	 * Insert data set.
	 * @param dataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#insertDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public Integer insertDataSet(GuiGenericDataSet dataSet) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Integer idToReturn = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDataSetHistory hibDataSet =null;
			if(dataSet!=null && dataSet.getActiveDetail()!=null){
				GuiDataSetDetail dataSetActiveDetail = dataSet.getActiveDetail();

				if(dataSetActiveDetail instanceof FileDataSetDetail){
					hibDataSet=new SbiFileDataSet();
					if(((FileDataSetDetail)dataSetActiveDetail).getFileName()!=null){
						((SbiFileDataSet)hibDataSet).setFileName(((FileDataSetDetail)dataSetActiveDetail).getFileName());
					}
				}

				else if(dataSetActiveDetail instanceof QueryDataSetDetail){
					hibDataSet=new SbiQueryDataSet();
					if(((QueryDataSetDetail)dataSetActiveDetail).getQuery()!=null){
						((SbiQueryDataSet)hibDataSet).setQuery(((QueryDataSetDetail)dataSetActiveDetail).getQuery().toString());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel()!=null){
						SbiDataSource hibDataSource = null;
						String dataSourceLabel = ((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel();
						Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
						Criteria criteria = aSession.createCriteria(SbiDataSource.class);
						criteria.add(labelCriterrion);	
						hibDataSource = (SbiDataSource) criteria.uniqueResult();
						((SbiQueryDataSet)hibDataSet).setDataSource(hibDataSource);	
					}				
				}	
				
				else if(dataSetActiveDetail instanceof QbeDataSetDetail){
					hibDataSet = new SbiQbeDataSet();
					SbiQbeDataSet hibQbeDataSet = (SbiQbeDataSet) hibDataSet;
					QbeDataSetDetail qbeDataSet = (QbeDataSetDetail) dataSetActiveDetail;
					hibQbeDataSet.setSqlQuery(qbeDataSet.getSqlQuery());
					hibQbeDataSet.setJsonQuery(qbeDataSet.getJsonQuery());
					hibQbeDataSet.setDatamarts(qbeDataSet.getDatamarts());
					String dataSourceLabel = qbeDataSet.getDataSourceLabel();
					Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
					Criteria criteria = aSession.createCriteria(SbiDataSource.class);
					criteria.add(labelCriterrion);	
					SbiDataSource hibDataSource = (SbiDataSource) criteria.uniqueResult();
					hibQbeDataSet.setDataSource(hibDataSource);	
				}

				else if(dataSetActiveDetail instanceof WSDataSetDetail){
					hibDataSet=new SbiWSDataSet();
					if(((WSDataSetDetail)dataSetActiveDetail).getAddress()!=null){
						((SbiWSDataSet)hibDataSet).setAdress(((WSDataSetDetail)dataSetActiveDetail).getAddress());
					}
					if(((WSDataSetDetail)dataSetActiveDetail).getOperation()!=null){
						((SbiWSDataSet)hibDataSet).setOperation(((WSDataSetDetail)dataSetActiveDetail).getOperation());
					}	
				}

				else if(dataSetActiveDetail instanceof JClassDataSetDetail){
					hibDataSet=new SbiJClassDataSet();
					if(((JClassDataSetDetail)dataSetActiveDetail).getJavaClassName()!=null){
						((SbiJClassDataSet)hibDataSet).setJavaClassName(((JClassDataSetDetail)dataSetActiveDetail).getJavaClassName());
					}
				}

				else if(dataSetActiveDetail instanceof ScriptDataSetDetail){
					hibDataSet=new SbiScriptDataSet();
					if(((ScriptDataSetDetail)dataSetActiveDetail).getScript()!=null){
						((SbiScriptDataSet)hibDataSet).setScript(((ScriptDataSetDetail)dataSetActiveDetail).getScript());
					}
					if(((ScriptDataSetDetail)dataSetActiveDetail).getLanguageScript()!=null){
						((SbiScriptDataSet)hibDataSet).setLanguageScript(((ScriptDataSetDetail)dataSetActiveDetail).getLanguageScript());
					}
				}

				SbiDomains transformer = null;
				if (dataSetActiveDetail.getTransformerId() != null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSetActiveDetail.getTransformerId());
					Criteria criteria = aSession.createCriteria(SbiDomains.class);
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
					Criteria criteria = aSession.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);

					category = (SbiDomains) criteria.uniqueResult();

					if (category == null){
						logger.error("The Domain with value_id= "+dataSetActiveDetail.getCategoryId()+" does not exist.");
						throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
					}
				}
				Date currentTStamp = new Date();
				SbiDataSetConfig dsConfig = new SbiDataSetConfig();			
				dsConfig.setLabel(dataSet.getLabel());
				dsConfig.setDescription(dataSet.getDescription());
				dsConfig.setName(dataSet.getName());	
				updateSbiCommonInfo4Insert(dsConfig);

				String userIn = dsConfig.getCommonInfo().getUserIn();
				String sbiVersionIn = dsConfig.getCommonInfo().getSbiVersionIn();
				hibDataSet.setUserIn(userIn);
				hibDataSet.setSbiVersionIn(sbiVersionIn);		
				hibDataSet.setVersionNum(1);
				hibDataSet.setTimeIn(currentTStamp);	
				
				hibDataSet.setActive(true);			

				hibDataSet.setTransformer(transformer);
				hibDataSet.setPivotColumnName(dataSetActiveDetail.getPivotColumnName());
				hibDataSet.setPivotRowName(dataSetActiveDetail.getPivotRowName());
				hibDataSet.setPivotColumnValue(dataSetActiveDetail.getPivotColumnValue());
				hibDataSet.setNumRows(dataSetActiveDetail.isNumRows());

				hibDataSet.setCategory(category);
				hibDataSet.setParameters(dataSetActiveDetail.getParameters());
				hibDataSet.setDsMetadata(dataSetActiveDetail.getDsMetadata());

				Integer dsId =(Integer) aSession.save(dsConfig);
				dsConfig.setDsId(dsId);
				hibDataSet.setSbiDsConfig(dsConfig);

				aSession.save(hibDataSet);

				idToReturn = dsId;
				tx.commit();
			}
		} catch (HibernateException he) {
			logger.error("Error while inserting the New Data Set ", he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		return idToReturn;
	}

	/**
	 * Restore an Older Version of the dataset
	 * @param dsId the a data set ID
	 * @param dsVersion the a data set Version
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet restoreOlderDataSetVersion(Integer dsId, Integer dsVersion) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		GuiGenericDataSet toReturn = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			if(dsId!=null && dsVersion!=null){

				Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);

				Query hibernateQuery = aSession.createQuery("from SbiDataSetHistory h where h.versionNum = ? and h.sbiDsConfig = ?" );
				hibernateQuery.setInteger(0, dsVersion);
				hibernateQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsDetail =(SbiDataSetHistory)hibernateQuery.uniqueResult();
				dsDetail.setActive(true);
				
				if(dsActiveDetail.getSbiDsConfig()!=null){
					SbiDataSetConfig hibDs = dsActiveDetail.getSbiDsConfig();
					updateSbiCommonInfo4Update(hibDs);
					aSession.update(hibDs);
				}
				
				if(dsDetail.getSbiDsConfig()!=null){
					SbiDataSetConfig hibDs = dsDetail.getSbiDsConfig();
					updateSbiCommonInfo4Update(hibDs);
					aSession.update(hibDs);
				}

				aSession.update(dsActiveDetail);
				aSession.update(dsDetail);
				tx.commit();
				toReturn = toDataSet(dsDetail);
			}
		} catch (HibernateException he) {
			logger.error("Error while modifing the data Set with id " + ((dsId == null)?"":String.valueOf(dsId)), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}

	/**
	 * Returns the Higher Version Number of a selected DS
	 * @param dsId the a data set ID
	 * @throws EMFUserError the EMF user error
	 */
	public Integer getHigherVersionNumForDS(Integer dsId) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Integer toReturn = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			if(dsId!=null){			
				Query hibQuery = aSession.createQuery("select max(h.versionNum) from SbiDataSetHistory h where h.sbiDsConfig = ?" );
				hibQuery.setInteger(0, dsId);	
				toReturn =(Integer)hibQuery.uniqueResult();
			}
		} catch (HibernateException he) {
			logger.error("Error while modifing the data Set with id " + ((dsId == null)?"":String.valueOf(dsId)), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}

	/**
	 * Modify data set.
	 * @param aDataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#modifyDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void modifyDataSet(GuiGenericDataSet dataSet) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDataSetHistory hibDataSet =null;
			if(dataSet!=null){
				Integer dsId = dataSet.getDsId();
				GuiDataSetDetail dsActiveDetailToSet = dataSet.getActiveDetail();	

				if(dsActiveDetailToSet instanceof FileDataSetDetail){
					hibDataSet=new SbiFileDataSet();
					if(((FileDataSetDetail)dsActiveDetailToSet).getFileName()!=null){
						((SbiFileDataSet)hibDataSet).setFileName(((FileDataSetDetail)dsActiveDetailToSet).getFileName());
					}
				}

				else if(dsActiveDetailToSet instanceof QueryDataSetDetail){
					hibDataSet=new SbiQueryDataSet();
					if(((QueryDataSetDetail)dsActiveDetailToSet).getQuery()!=null){
						((SbiQueryDataSet)hibDataSet).setQuery(((QueryDataSetDetail)dsActiveDetailToSet).getQuery().toString());
					}
					if(((QueryDataSetDetail)dsActiveDetailToSet).getDataSourceLabel()!=null){
						SbiDataSource hibDataSource = null;
						String dataSourceLabel = ((QueryDataSetDetail)dsActiveDetailToSet).getDataSourceLabel();
						Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
						Criteria criteria = aSession.createCriteria(SbiDataSource.class);
						criteria.add(labelCriterrion);	
						hibDataSource = (SbiDataSource) criteria.uniqueResult();
						((SbiQueryDataSet)hibDataSet).setDataSource(hibDataSource);	
					}				
				}
				
				else if (dsActiveDetailToSet instanceof QbeDataSetDetail) {
					hibDataSet = new SbiQbeDataSet();
					SbiQbeDataSet hibQbeDataSet = (SbiQbeDataSet) hibDataSet;
					QbeDataSetDetail qbeDataSet = (QbeDataSetDetail) dsActiveDetailToSet;
					hibQbeDataSet.setSqlQuery(qbeDataSet.getSqlQuery());
					hibQbeDataSet.setJsonQuery(qbeDataSet.getJsonQuery());
					hibQbeDataSet.setDatamarts(qbeDataSet.getDatamarts());
					String dataSourceLabel = qbeDataSet.getDataSourceLabel();
					Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
					Criteria criteria = aSession.createCriteria(SbiDataSource.class);
					criteria.add(labelCriterrion);	
					SbiDataSource hibDataSource = (SbiDataSource) criteria.uniqueResult();
					hibQbeDataSet.setDataSource(hibDataSource);	
				}

				else if(dsActiveDetailToSet instanceof WSDataSetDetail){
					hibDataSet=new SbiWSDataSet();
					if(((WSDataSetDetail)dsActiveDetailToSet).getAddress()!=null){
						((SbiWSDataSet)hibDataSet).setAdress(((WSDataSetDetail)dsActiveDetailToSet).getAddress());
					}
					if(((WSDataSetDetail)dsActiveDetailToSet).getOperation()!=null){
						((SbiWSDataSet)hibDataSet).setOperation(((WSDataSetDetail)dsActiveDetailToSet).getOperation());
					}	
				}

				else if(dsActiveDetailToSet instanceof JClassDataSetDetail){
					hibDataSet=new SbiJClassDataSet();
					if(((JClassDataSetDetail)dsActiveDetailToSet).getJavaClassName()!=null){
						((SbiJClassDataSet)hibDataSet).setJavaClassName(((JClassDataSetDetail)dsActiveDetailToSet).getJavaClassName());
					}
				}

				else if(dsActiveDetailToSet instanceof ScriptDataSetDetail){
					hibDataSet=new SbiScriptDataSet();
					if(((ScriptDataSetDetail)dsActiveDetailToSet).getScript()!=null){
						((SbiScriptDataSet)hibDataSet).setScript(((ScriptDataSetDetail)dsActiveDetailToSet).getScript());
					}
					if(((ScriptDataSetDetail)dsActiveDetailToSet).getLanguageScript()!=null){
						((SbiScriptDataSet)hibDataSet).setLanguageScript(((ScriptDataSetDetail)dsActiveDetailToSet).getLanguageScript());
					}
				}

				SbiDomains transformer = null;
				if (dsActiveDetailToSet.getTransformerId() != null){ 
					Criterion aCriterion = Expression.eq("valueId",	dsActiveDetailToSet.getTransformerId());
					Criteria criteria = aSession.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);
					transformer = (SbiDomains) criteria.uniqueResult();
					if (transformer == null){
						logger.error("The Domain with value_id= "+dsActiveDetailToSet.getTransformerId()+" does not exist.");
						throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
					}
				}

				SbiDomains category = null;
				if (dsActiveDetailToSet.getCategoryId()!= null){ 
					Criterion aCriterion = Expression.eq("valueId",	dsActiveDetailToSet.getCategoryId());
					Criteria criteria = aSession.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);	
					category = (SbiDomains) criteria.uniqueResult();	
					if (category == null){
						logger.error("The Domain with value_id= "+dsActiveDetailToSet.getCategoryId()+" does not exist.");
						throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
					}
				}
				Date currentTStamp = new Date();

				hibDataSet.setTimeIn(currentTStamp);		
				hibDataSet.setActive(true);			

				hibDataSet.setTransformer(transformer);
				hibDataSet.setPivotColumnName(dsActiveDetailToSet.getPivotColumnName());
				hibDataSet.setPivotRowName(dsActiveDetailToSet.getPivotRowName());
				hibDataSet.setPivotColumnValue(dsActiveDetailToSet.getPivotColumnValue());
				hibDataSet.setNumRows(dsActiveDetailToSet.isNumRows());

				hibDataSet.setCategory(category);
				hibDataSet.setParameters(dsActiveDetailToSet.getParameters());
				hibDataSet.setDsMetadata(dsActiveDetailToSet.getDsMetadata());

				SbiDataSetConfig hibGenericDataSet = (SbiDataSetConfig) aSession.load(SbiDataSetConfig.class,dsId);					
				hibGenericDataSet.setLabel(dataSet.getLabel());
				hibGenericDataSet.setDescription(dataSet.getDescription());
				hibGenericDataSet.setName(dataSet.getName());	
				
				updateSbiCommonInfo4Update(hibGenericDataSet);

				String userUp = hibGenericDataSet.getCommonInfo().getUserUp();
				String sbiVersionUp = hibGenericDataSet.getCommonInfo().getSbiVersionUp();
				hibDataSet.setUserIn(userUp);
				hibDataSet.setSbiVersionIn(sbiVersionUp);	
				hibDataSet.setTimeIn(currentTStamp);

				Integer currenthigherVersion = getHigherVersionNumForDS(dsId);
				Integer newVersion = currenthigherVersion+1;
				hibDataSet.setVersionNum(newVersion);

				Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);
				aSession.update(dsActiveDetail);

				aSession.update(hibGenericDataSet);
				hibDataSet.setSbiDsConfig(hibGenericDataSet);				
				aSession.save(hibDataSet);

				tx.commit();
			}
		} catch (HibernateException he) {
			logger.error("Error while modifing the data Set with id " + ((dataSet == null)?"":String.valueOf(dataSet.getDsId())), he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
	}

	/**
	 * Returns List of all existent SbiDataSetConfig elements (NO DETAIL, only name, label, descr...).
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent SbiDataSetConfig
	 * @throws EMFUserError the EMF user error
	 */
	public List<SbiDataSetConfig> loadPagedSbiDatasetConfigList(Integer offset, Integer fetchSize)
	throws EMFUserError {
		logger.debug("IN");
		List<SbiDataSetConfig> toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		Long resultNumber;
		Query hibernateQuery; 

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			toReturn = new ArrayList();

			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Long)hqlQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			hibernateQuery = aSession.createQuery("from SbiDataSetConfig order by label");
			hibernateQuery.setFirstResult(offset);
			if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			

			toReturn = hibernateQuery.list();	

		} catch (HibernateException he) {
			logger.error("Error while loading the list of Resources", he);	
			if (tx != null)
				tx.rollback();	
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);		
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}

	public List<GuiGenericDataSet> loadFilteredDatasetList(String hsql,Integer offset, Integer fetchSize) throws EMFUserError {
		logger.debug("IN");
		List toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		Integer resultNumber;
		Query hibernateQuery;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			toReturn = new ArrayList();
			List toTransform = null;

			String hql = "select count(*) "+hsql;
			Query hqlQuery = aSession.createQuery(hql);
			Long temp = (Long)hqlQuery.uniqueResult();
			resultNumber = new Integer(temp.intValue());

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? Math.min(fetchSize, resultNumber): resultNumber;
			}
			
			hibernateQuery = aSession.createQuery(hsql);
			hibernateQuery.setFirstResult(offset);
			if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			

			List sbiActiveDatasetsList = hibernateQuery.list();	

			if(sbiActiveDatasetsList!=null && !sbiActiveDatasetsList.isEmpty()){
				Iterator it = sbiActiveDatasetsList.iterator();		
				while (it.hasNext()) {
					SbiDataSetHistory hibDataSet = (SbiDataSetHistory)it.next();
					GuiGenericDataSet ds = toDataSet(hibDataSet);
					List<GuiDataSetDetail> oldDsVersion = new ArrayList();

					if(hibDataSet.getSbiDsConfig()!=null){
						Integer dsId = hibDataSet.getSbiDsConfig().getDsId();
						Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSetHistory> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSetHistory hibOldDataSet = (SbiDataSetHistory) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									GuiDataSetDetail dsD = toDataSetDetail(hibOldDataSet);		
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					ds.setNonActiveDetails(oldDsVersion);
					toReturn.add(ds);
				}
			}			

		} catch (HibernateException he) {
			logger.error("Error while loading the list of Threshold", he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}
	
	/**
	 * Returns List of all existent IDataSets with current active version
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent IDataSets with current active version
	 * @throws EMFUserError the EMF user error
	 */
	public List<GuiGenericDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize)
	throws EMFUserError {

		logger.debug("IN");
		List toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		Long resultNumber;
		Query hibernateQuery; 

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			toReturn = new ArrayList();

			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Long)hqlQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0) ? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			hibernateQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? order by h.sbiDsConfig.name " );
			hibernateQuery.setBoolean(0, true);
			hibernateQuery.setFirstResult(offset);
			if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			

			List sbiActiveDatasetsList = hibernateQuery.list();	

			if(sbiActiveDatasetsList!=null && !sbiActiveDatasetsList.isEmpty()){
				Iterator it = sbiActiveDatasetsList.iterator();		
				while (it.hasNext()) {
					SbiDataSetHistory hibDataSet = (SbiDataSetHistory)it.next();
					GuiGenericDataSet ds = toDataSet(hibDataSet);
					List<GuiDataSetDetail> oldDsVersion = new ArrayList();

					if(hibDataSet.getSbiDsConfig()!=null){
						Integer dsId = hibDataSet.getSbiDsConfig().getDsId();
						Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSetHistory> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSetHistory hibOldDataSet = (SbiDataSetHistory) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									GuiDataSetDetail dsD = toDataSetDetail(hibOldDataSet);		
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					ds.setNonActiveDetails(oldDsVersion);
					toReturn.add(ds);
				}
			}			
		} catch (HibernateException he) {
			logger.error("Error while loading the list of Resources", he);	
			if (tx != null)
				tx.rollback();	
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);		
		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return toReturn;
	}

	public GuiGenericDataSet toDataSet(SbiDataSetHistory hibDataSet) throws EMFUserError{		
		logger.debug("IN");
		GuiGenericDataSet ds = new GuiGenericDataSet();
		GuiDataSetDetail dsActiveDetail = null;

		if(hibDataSet instanceof SbiFileDataSet){		
			dsActiveDetail = new FileDataSetDetail();
			((FileDataSetDetail)dsActiveDetail).setFileName(((SbiFileDataSet)hibDataSet).getFileName());		
			dsActiveDetail.setDsType(FILE_DS_TYPE);
			logger.debug("File dataset");
		}

		if(hibDataSet instanceof SbiQueryDataSet){			
			dsActiveDetail=new QueryDataSetDetail();
			((QueryDataSetDetail)dsActiveDetail).setQuery(((SbiQueryDataSet)hibDataSet).getQuery());
			SbiDataSource sbids=((SbiQueryDataSet)hibDataSet).getDataSource();
			if(sbids!=null){
				String dataSourceLabel = sbids.getLabel();
				((QueryDataSetDetail)dsActiveDetail).setDataSourceLabel(dataSourceLabel);
			}
			dsActiveDetail.setDsType(JDBC_DS_TYPE);
			logger.debug("Jdbc dataset");
		}
		
		if(hibDataSet instanceof SbiQbeDataSet){			
			dsActiveDetail = new QbeDataSetDetail();
			QbeDataSetDetail aQbeDataSetDetail = (QbeDataSetDetail) dsActiveDetail;
			SbiQbeDataSet qbeHibDataSet = (SbiQbeDataSet) hibDataSet;
			aQbeDataSetDetail.setSqlQuery(qbeHibDataSet.getSqlQuery());
			aQbeDataSetDetail.setJsonQuery(qbeHibDataSet.getJsonQuery());
			aQbeDataSetDetail.setDatamarts(qbeHibDataSet.getDatamarts());
			SbiDataSource sbids = qbeHibDataSet.getDataSource();
			aQbeDataSetDetail.setDataSourceLabel(sbids.getLabel());
			dsActiveDetail.setDsType(QBE_DS_TYPE);
		}

		if(hibDataSet instanceof SbiWSDataSet){			
			dsActiveDetail=new WSDataSetDetail();
			((WSDataSetDetail)dsActiveDetail).setAddress(((SbiWSDataSet)hibDataSet).getAdress());
			((WSDataSetDetail)dsActiveDetail).setOperation(((SbiWSDataSet)hibDataSet).getOperation());
			dsActiveDetail.setDsType(WS_DS_TYPE);
			logger.debug("Ws dataset");
		}

		if(hibDataSet instanceof SbiScriptDataSet){			
			dsActiveDetail=new ScriptDataSetDetail();
			((ScriptDataSetDetail)dsActiveDetail).setScript(((SbiScriptDataSet)hibDataSet).getScript());
			((ScriptDataSetDetail)dsActiveDetail).setLanguageScript(((SbiScriptDataSet)hibDataSet).getLanguageScript());
			dsActiveDetail.setDsType(SCRIPT_DS_TYPE);
			logger.debug("Script dataset");
		}

		if(hibDataSet instanceof SbiJClassDataSet){			
			dsActiveDetail=new JClassDataSetDetail();
			((JClassDataSetDetail)dsActiveDetail).setJavaClassName(((SbiJClassDataSet)hibDataSet).getJavaClassName());
			dsActiveDetail.setDsType(JCLASS_DS_TYPE);
			logger.debug("JClass dataset");
		}

		if(hibDataSet.getSbiDsConfig()!=null){
			ds.setDsId(hibDataSet.getSbiDsConfig().getDsId());
			ds.setName(hibDataSet.getSbiDsConfig().getName());
			ds.setLabel(hibDataSet.getSbiDsConfig().getLabel());
			ds.setDescription(hibDataSet.getSbiDsConfig().getDescription());	
			ds.setMetaVersion(hibDataSet.getMetaVersion());
			ds.setUserIn(hibDataSet.getUserIn());
			ds.setTimeIn(new Date());
			dsActiveDetail.setDsId(hibDataSet.getSbiDsConfig().getDsId());

		}

		dsActiveDetail.setDsHId(hibDataSet.getDsHId());
		dsActiveDetail.setCategoryId((hibDataSet.getCategory()== null)? null:hibDataSet.getCategory().getValueId());
		dsActiveDetail.setCategoryCd((hibDataSet.getCategory()== null)? null:hibDataSet.getCategory().getValueCd());
		dsActiveDetail.setTransformerId((hibDataSet.getTransformer()== null)? null:hibDataSet.getTransformer().getValueId());
		dsActiveDetail.setTransformerCd((hibDataSet.getTransformer()== null)? null:hibDataSet.getTransformer().getValueCd());
		dsActiveDetail.setPivotColumnName(hibDataSet.getPivotColumnName());
		dsActiveDetail.setPivotRowName(hibDataSet.getPivotRowName());
		dsActiveDetail.setPivotColumnValue(hibDataSet.getPivotColumnValue());
		dsActiveDetail.setNumRows(hibDataSet.isNumRows());			
		dsActiveDetail.setParameters(hibDataSet.getParameters());		
		dsActiveDetail.setDsMetadata(hibDataSet.getDsMetadata());		
		dsActiveDetail.setUserIn(hibDataSet.getUserIn());
		dsActiveDetail.setTimeIn(hibDataSet.getTimeIn());
		dsActiveDetail.setVersionNum(hibDataSet.getVersionNum());
		dsActiveDetail.setSbiVersionIn(hibDataSet.getSbiVersionIn());
		dsActiveDetail.setDsHId(hibDataSet.getDsHId());

		ds.setActiveDetail(dsActiveDetail);

		logger.debug("OUT");
		return ds;
	}

	public GuiDataSetDetail toDataSetDetail(SbiDataSetHistory hibDataSet) throws EMFUserError{		

		GuiDataSetDetail dsVersionDetail = null;

		if(hibDataSet instanceof SbiFileDataSet){		
			dsVersionDetail = new FileDataSetDetail();
			((FileDataSetDetail)dsVersionDetail).setFileName(((SbiFileDataSet)hibDataSet).getFileName());		
			dsVersionDetail.setDsType(FILE_DS_TYPE);
		}

		if(hibDataSet instanceof SbiQueryDataSet){			
			dsVersionDetail=new QueryDataSetDetail();
			((QueryDataSetDetail)dsVersionDetail).setQuery(((SbiQueryDataSet)hibDataSet).getQuery());
			SbiDataSource sbids=((SbiQueryDataSet)hibDataSet).getDataSource();
			if(sbids!=null){
				String dataSourceLabel = sbids.getLabel();
				((QueryDataSetDetail)dsVersionDetail).setDataSourceLabel(dataSourceLabel);
			}
			dsVersionDetail.setDsType(JDBC_DS_TYPE);
		}
		
		if (hibDataSet instanceof SbiQbeDataSet){			
			dsVersionDetail = new QbeDataSetDetail();
			QbeDataSetDetail aQbeDataSetDetail = (QbeDataSetDetail) dsVersionDetail;
			SbiQbeDataSet qbeHibDataSet = (SbiQbeDataSet) hibDataSet;
			aQbeDataSetDetail.setSqlQuery(qbeHibDataSet.getSqlQuery());
			aQbeDataSetDetail.setJsonQuery(qbeHibDataSet.getJsonQuery());
			aQbeDataSetDetail.setDatamarts(qbeHibDataSet.getDatamarts());
			SbiDataSource sbids = qbeHibDataSet.getDataSource();
			if (sbids!=null) aQbeDataSetDetail.setDataSourceLabel(sbids.getLabel());
			dsVersionDetail.setDsType(QBE_DS_TYPE);
		}

		if(hibDataSet instanceof SbiWSDataSet){			
			dsVersionDetail=new WSDataSetDetail();
			((WSDataSetDetail)dsVersionDetail).setAddress(((SbiWSDataSet)hibDataSet).getAdress());
			((WSDataSetDetail)dsVersionDetail).setOperation(((SbiWSDataSet)hibDataSet).getOperation());
			dsVersionDetail.setDsType(WS_DS_TYPE);
		}

		if(hibDataSet instanceof SbiScriptDataSet){			
			dsVersionDetail=new ScriptDataSetDetail();
			((ScriptDataSetDetail)dsVersionDetail).setScript(((SbiScriptDataSet)hibDataSet).getScript());
			((ScriptDataSetDetail)dsVersionDetail).setLanguageScript(((SbiScriptDataSet)hibDataSet).getLanguageScript());
			dsVersionDetail.setDsType(SCRIPT_DS_TYPE);
		}

		if(hibDataSet instanceof SbiJClassDataSet){			
			dsVersionDetail=new JClassDataSetDetail();
			((JClassDataSetDetail)dsVersionDetail).setJavaClassName(((SbiJClassDataSet)hibDataSet).getJavaClassName());
			dsVersionDetail.setDsType(JCLASS_DS_TYPE);
		}

		dsVersionDetail.setCategoryId((hibDataSet.getCategory()== null)? null:hibDataSet.getCategory().getValueId());
		dsVersionDetail.setCategoryCd((hibDataSet.getCategory()== null)? null:hibDataSet.getCategory().getValueCd());
		dsVersionDetail.setTransformerId((hibDataSet.getTransformer()== null)? null:hibDataSet.getTransformer().getValueId());
		dsVersionDetail.setTransformerCd((hibDataSet.getTransformer()== null)? null:hibDataSet.getTransformer().getValueCd());
		dsVersionDetail.setPivotColumnName(hibDataSet.getPivotColumnName());
		dsVersionDetail.setPivotRowName(hibDataSet.getPivotRowName());
		dsVersionDetail.setPivotColumnValue(hibDataSet.getPivotColumnValue());
		dsVersionDetail.setNumRows(hibDataSet.isNumRows());			
		dsVersionDetail.setParameters(hibDataSet.getParameters());		
		dsVersionDetail.setDsMetadata(hibDataSet.getDsMetadata());	

		dsVersionDetail.setUserIn(hibDataSet.getUserIn());
		dsVersionDetail.setTimeIn(hibDataSet.getTimeIn());
		dsVersionDetail.setVersionNum(hibDataSet.getVersionNum());
		dsVersionDetail.setSbiVersionIn(hibDataSet.getSbiVersionIn());
		dsVersionDetail.setDsHId(hibDataSet.getDsHId());

		return dsVersionDetail;
	}


	/** copy a dataset history
	 * 
	 * @param hibDataSet
	 * @return
	 * @throws EMFUserError
	 */

	public SbiDataSetHistory copyDataSetHistory(SbiDataSetHistory hibDataSet) throws EMFUserError{		

		logger.debug("IN");
		SbiDataSetHistory hibNew = null;

		if(hibDataSet instanceof SbiFileDataSet){		
			hibNew  = new SbiFileDataSet();
			((SbiFileDataSet)hibNew).setFileName(((SbiFileDataSet)hibDataSet).getFileName());		
		}

		if(hibDataSet instanceof SbiQueryDataSet){			
			hibNew  = new SbiQueryDataSet();
			((SbiQueryDataSet)hibNew).setQuery(((SbiQueryDataSet)hibDataSet).getQuery());
		}

		if(hibDataSet instanceof SbiWSDataSet){			
			hibNew  = new SbiWSDataSet();
			((SbiWSDataSet)hibNew ).setAdress(((SbiWSDataSet)hibDataSet).getAdress());
			((SbiWSDataSet)hibNew ).setOperation(((SbiWSDataSet)hibDataSet).getOperation());
		}

		if(hibDataSet instanceof SbiScriptDataSet){			
			hibNew =new SbiScriptDataSet();
			((SbiScriptDataSet)	hibNew ).setScript(((SbiScriptDataSet)hibDataSet).getScript());
			((SbiScriptDataSet)	hibNew ).setLanguageScript(((SbiScriptDataSet)hibDataSet).getLanguageScript());

		}

		if(hibDataSet instanceof SbiJClassDataSet){			
			hibNew =new SbiJClassDataSet();
			((SbiJClassDataSet)	hibNew ).setJavaClassName(((SbiJClassDataSet)hibDataSet).getJavaClassName());
		}

		hibNew.setCategory(hibDataSet.getCategory());
		hibNew.setDsMetadata(hibDataSet.getDsMetadata());
		hibNew.setMetaVersion(hibDataSet.getMetaVersion());
		hibNew.setParameters(hibDataSet.getParameters());
		hibNew.setPivotColumnName(hibDataSet.getPivotColumnName());
		hibNew.setPivotColumnValue(hibDataSet.getPivotColumnValue());
		hibNew.setPivotRowName(hibDataSet.getPivotRowName());
		hibNew.setTransformer(hibDataSet.getTransformer());
		hibNew.setSbiVersionIn(hibDataSet.getSbiVersionIn());
		hibNew.setUserIn(hibDataSet.getUserIn());
		hibNew.setTimeIn(hibDataSet.getTimeIn());
		hibNew.setVersionNum(hibDataSet.getVersionNum());
		hibNew.setDsHId(hibDataSet.getDsHId());

		logger.debug("OUT");
		return hibNew;
	}


	/**
	 * From the IDataSet as input, return the corrispondent <code>GuiGenericDataSet</code> object.
	 * 
	 * @param iDataSet The IDataSet 
	 * @return The corrispondent <code>GuiGenericDataSet</code> object
	 * @throws EMFUserError 
	 */
	public GuiGenericDataSet toDataSet(IDataSet iDataSet) throws EMFUserError{		
		GuiGenericDataSet ds = new GuiGenericDataSet();
		GuiDataSetDetail dsActiveDetail = null;

		if(iDataSet instanceof FileDataSet){		
			dsActiveDetail = new FileDataSetDetail();
			((FileDataSetDetail)dsActiveDetail).setFileName(((FileDataSet)iDataSet).getFileName());		
			dsActiveDetail.setDsType(FILE_DS_TYPE);
		}

		if(iDataSet instanceof JDBCDataSet){			
			dsActiveDetail=new QueryDataSetDetail();
			((QueryDataSetDetail)dsActiveDetail).setQuery((String)(((JDBCDataSet)iDataSet).getQuery()));
			IDataSource iDataSource=((JDBCDataSet)iDataSet).getDataSource();
			if(iDataSource!=null){
				String dataSourceLabel = iDataSource.getLabel();
				((QueryDataSetDetail)dsActiveDetail).setDataSourceLabel(dataSourceLabel);
			}
			dsActiveDetail.setDsType(JDBC_DS_TYPE);
		}
		
		if(iDataSet instanceof QbeDataSet){			
			dsActiveDetail = new QbeDataSetDetail();
			QbeDataSetDetail aQbeDataSetDetail = (QbeDataSetDetail) dsActiveDetail;
			QbeDataSet aQbeDataSet = (QbeDataSet) iDataSet;
			aQbeDataSetDetail.setJsonQuery(aQbeDataSet.getJsonQuery());
			aQbeDataSetDetail.setDatamarts(aQbeDataSet.getDatamarts());
			IDataSource iDataSource = aQbeDataSet.getDataSource();
			if (iDataSource!=null){
				String dataSourceLabel = iDataSource.getLabel();
				aQbeDataSetDetail.setDataSourceLabel(dataSourceLabel);
			}
			dsActiveDetail.setDsType(QBE_DS_TYPE);
		}

		if(iDataSet instanceof WebServiceDataSet){			
			dsActiveDetail=new WSDataSetDetail();
			((WSDataSetDetail)dsActiveDetail).setAddress(((WebServiceDataSet)iDataSet).getAddress());
			((WSDataSetDetail)dsActiveDetail).setOperation(((WebServiceDataSet)iDataSet).getOperation());
			dsActiveDetail.setDsType(WS_DS_TYPE);
		}

		if(iDataSet instanceof ScriptDataSet){			
			dsActiveDetail=new ScriptDataSetDetail();
			((ScriptDataSetDetail)dsActiveDetail).setScript(((ScriptDataSet)iDataSet).getScript());
			((ScriptDataSetDetail)dsActiveDetail).setLanguageScript(((ScriptDataSet)iDataSet).getLanguageScript());
			dsActiveDetail.setDsType(SCRIPT_DS_TYPE);
		}

		if(iDataSet instanceof JavaClassDataSet){			
			dsActiveDetail=new JClassDataSetDetail();
			((JClassDataSetDetail)dsActiveDetail).setJavaClassName(((JavaClassDataSet)iDataSet).getClassName());
			dsActiveDetail.setDsType(JCLASS_DS_TYPE);
		}

		ds.setDsId(iDataSet.getId());
		ds.setName(iDataSet.getName());
		ds.setLabel(iDataSet.getLabel());
		ds.setDescription(iDataSet.getDescription());	

		// set detail dataset ID
		dsActiveDetail.setTransformerId((iDataSet.getTransformerId() == null)? null:iDataSet.getTransformerId());
		dsActiveDetail.setPivotColumnName(iDataSet.getPivotColumnName());
		dsActiveDetail.setPivotRowName(iDataSet.getPivotRowName());
		dsActiveDetail.setPivotColumnValue(iDataSet.getPivotColumnValue());
		dsActiveDetail.setNumRows(iDataSet.isNumRows());			
		dsActiveDetail.setParameters(iDataSet.getParameters());		
		dsActiveDetail.setDsMetadata(iDataSet.getDsMetadata());		

		ds.setActiveDetail(dsActiveDetail);

		return ds;
	}

	/**
	 * Counts number of BIObj associated.
	 * @param dsId the ds id
	 * @return Integer, number of BIObj associated
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countBIObjAssociated (Integer dsId) throws EMFUserError{
		logger.debug("IN");		
		Integer resultNumber = new Integer(0); 
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			String hql = "select count(*) from SbiObjects s where s.dataSet.dsId = ? ";
			Query aQuery = aSession.createQuery(hql);
			aQuery.setInteger(0, dsId.intValue());
			resultNumber = new Integer(((Long) aQuery.uniqueResult()).intValue());

		} catch (HibernateException he) {
			logger.error("Error while getting the objects associated with the data set with id " + dsId, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");
		return resultNumber;
	}

	/**
	 * Counts number of existent DataSets
	 * @return Integer, number of existent DataSets
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countDatasets() throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Long resultNumber;		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Long)hqlQuery.uniqueResult();

		} catch (HibernateException he) {
			logger.error("Error while loading the list of SbiDataSet", he);	
			if (tx != null)
				tx.rollback();	
			throw new EMFUserError(EMFErrorSeverity.ERROR, 9104);

		} finally {
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
				logger.debug("OUT");
			}
		}
		return new Integer(resultNumber.intValue());
	}

	/*****************USED by OLD GUI******/
	/**
	 * Checks for bi obj associated.
	 * @param dsId the ds id
	 * @return true, if checks for bi obj associated
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataSet.dao.IDataSetDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBIObjAssociated (String dsId) throws EMFUserError{
		logger.debug("IN");		
		boolean bool = false; 

		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Integer dsIdInt = Integer.valueOf(dsId);

			String hql = " from SbiObjects s where s.dataSet.dsId = ?";
			Query aQuery = aSession.createQuery(hql);
			aQuery.setInteger(0, dsIdInt.intValue());
			List biObjectsAssocitedWithDs = aQuery.list();
			if (biObjectsAssocitedWithDs.size() > 0)
				bool = true;
			else
				bool = false;
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while getting the objects associated with the data set with id " + dsId, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");
		return bool;
	}



	/**
	 * Load data set by id.
	 * @param dsID the ds id
	 * @return the data set as genericGuiDataset
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet loadDataSetById(Integer dsId)throws EMFUserError {
		logger.debug("IN");
		GuiGenericDataSet toReturn = null;
		GuiDataSetDetail detail = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQueryHistory = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQueryHistory.setBoolean(0, true);
			hibQueryHistory.setInteger(1, dsId);	
			SbiDataSetHistory sbiDataSetHistory =(SbiDataSetHistory)hibQueryHistory.uniqueResult();
			if(sbiDataSetHistory!=null){
				detail = toDataSetDetail(sbiDataSetHistory);
			}
			toReturn = toDataSet(sbiDataSetHistory);

			//			Query hibQueryConfig = aSession.createQuery("from SbiDataSetConfig h where h.dsId = ?" );
			//			hibQueryConfig.setBoolean(0, true);
			//			hibQueryConfig.setInteger(1, dsId);	
			//			SbiDataSetConfig dsActiveDetail =(SbiDataSetConfig)hibQueryConfig.uniqueResult();
			//			if(dsActiveDetail!=null){
			//				toReturn = to)(dsActiveDetail);
			//			}
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading the data Set with id " + dsId.toString(), he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	/**
	 * Load data set by id.
	 * @param dsLabel the ds label
	 * @return the data set as genericGuiDataset
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet loadDataSetByLabel(String dsLabel)throws EMFUserError {
		logger.debug("IN");
		GuiGenericDataSet toReturn = null;
		GuiDataSetDetail detail = null;

		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQueryHistory = aSession.createQuery("from SbiDataSetHistory h, SbiDataSetConfig d where h.active = ? and d.label = ? and d.dsId = h.sbiDsConfig" );
			hibQueryHistory.setBoolean(0, true);
			hibQueryHistory.setString(1, dsLabel);	
			SbiDataSetHistory sbiDataSetHistory =(SbiDataSetHistory)hibQueryHistory.uniqueResult();
			if(sbiDataSetHistory!=null){
				detail = toDataSetDetail(sbiDataSetHistory);
			}
			toReturn = toDataSet(sbiDataSetHistory);

			//			Query hibQueryConfig = aSession.createQuery("from SbiDataSetConfig h where h.dsId = ?" );
			//			hibQueryConfig.setBoolean(0, true);
			//			hibQueryConfig.setInteger(1, dsId);	
			//			SbiDataSetConfig dsActiveDetail =(SbiDataSetConfig)hibQueryConfig.uniqueResult();
			//			if(dsActiveDetail!=null){
			//				toReturn = to)(dsActiveDetail);
			//			}
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading the data Set with id " + dsLabel.toString(), he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}





	/*****************USED many times but not in new GUI******/
	/**
	 * Load data set by id.
	 * @param dsID the ds id
	 * @return the data set
	 * @throws EMFUserError the EMF user error
	 */
	public IDataSet loadActiveIDataSetByID(Integer dsId) throws EMFUserError {
		logger.debug("IN");
		IDataSet toReturn = null;
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, dsId);	
			SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
			if(dsActiveDetail!=null){
				toReturn = toIDataSet(dsActiveDetail);
			}
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading the data Set with id " + dsId.toString(), he);			
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("OUT");
			}
		}
		logger.debug("OUT");
		return toReturn;
	}

	/**
	 * Load data set by label.
	 * @param label the label
	 * @return the data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#loadDataSetByLabel(string)
	 */	
	public IDataSet loadActiveDataSetByLabel(String label) throws EMFUserError {
		logger.debug("IN");
		IDataSet toReturn = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label", label);
			Criteria criteria = aSession.createCriteria(SbiDataSetConfig.class);
			criteria.add(labelCriterrion);	
			SbiDataSetConfig hibDS = (SbiDataSetConfig) criteria.uniqueResult();
			if (hibDS == null) return null;

			Integer dsId = hibDS.getDsId();

			Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, dsId);	
			SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
			if(dsActiveDetail!=null){
				toReturn = toIDataSet(dsActiveDetail);
			}
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading the data set with label " + label, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		logger.debug("OUT");
		return toReturn;		
	}

	/**
	 * Load all active data sets.
	 * @return the list
	 * @throws EMFUserError the EMF user error
	 */
	public List loadAllActiveDataSets() throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery("from SbiDataSetHistory h where h.active = ? " );
			hibQuery.setBoolean(0, true);

			List<SbiDataSetHistory> hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				SbiDataSetHistory temp = (SbiDataSetHistory) it.next();
				if(temp!=null){
					realResult.add(toIDataSet(temp));				
				}
			}
		} catch (HibernateException he) {
			logger.error("Error while loading all data sets ", he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();				
			}
		}
		logger.debug("OUT");
		return realResult;
	}

	/**
	 * From the hibernate DataSet as input, gives the corrispondent <code>DataSet</code> object.
	 * 
	 * @param hibDataSet The hybernate data set
	 * @return The corrispondent <code>DataSet</code> object
	 * @throws EMFUserError 
	 */
	public IDataSet toIDataSet(SbiDataSetHistory hibDataSet) throws EMFUserError{
		IDataSet ds = null;
		if(hibDataSet instanceof SbiFileDataSet){		
			ds = new FileDataSet();
			((FileDataSet)ds).setFileName(((SbiFileDataSet)hibDataSet).getFileName());		
		}

		if(hibDataSet instanceof SbiQueryDataSet){			
			ds=new JDBCDataSet();
			((JDBCDataSet)ds).setQuery(((SbiQueryDataSet)hibDataSet).getQuery());

			SbiDataSource sbids=((SbiQueryDataSet)hibDataSet).getDataSource();
			if(sbids!=null){
				DataSourceDAOHibImpl dataSourceDao=new DataSourceDAOHibImpl();
				IDataSource dataSource=dataSourceDao.toDataSource(sbids);
				((JDBCDataSet)ds).setDataSource(dataSource);
			}
		}

		if(hibDataSet instanceof SbiWSDataSet){			
			ds=new WebServiceDataSet();
			((WebServiceDataSet)ds).setAddress(((SbiWSDataSet)hibDataSet).getAdress());
			((WebServiceDataSet)ds).setOperation(((SbiWSDataSet)hibDataSet).getOperation());
		}

		if(hibDataSet instanceof SbiScriptDataSet){			
			ds=new ScriptDataSet();
			((ScriptDataSet)ds).setScript(((SbiScriptDataSet)hibDataSet).getScript());
			((ScriptDataSet)ds).setLanguageScript(((SbiScriptDataSet)hibDataSet).getLanguageScript());
		}

		if(hibDataSet instanceof SbiJClassDataSet){			
			ds=new JavaClassDataSet();
			((JavaClassDataSet)ds).setClassName(((SbiJClassDataSet)hibDataSet).getJavaClassName());
		}
		
		if (hibDataSet instanceof SbiQbeDataSet) {			
			ds = new QbeDataSet();
			SbiQbeDataSet aSbiQbeDataSet = (SbiQbeDataSet) hibDataSet;
			QbeDataSet qbeDataset = (QbeDataSet) ds;
			qbeDataset.setJsonQuery(aSbiQbeDataSet.getJsonQuery());
			qbeDataset.setDatamarts(aSbiQbeDataSet.getDatamarts());
			
			SbiDataSource sbids = aSbiQbeDataSet.getDataSource();
			DataSourceDAOHibImpl dataSourceDao = new DataSourceDAOHibImpl();
			IDataSource dataSource = dataSourceDao.toDataSource(sbids);
			qbeDataset.setDataSource(dataSource);
			
		}

		if(ds!=null){
			if(hibDataSet.getSbiDsConfig()!=null){
				ds.setId(hibDataSet.getSbiDsConfig().getDsId());
				ds.setName(hibDataSet.getSbiDsConfig().getName());
				ds.setLabel(hibDataSet.getSbiDsConfig().getLabel());
				ds.setDescription(hibDataSet.getSbiDsConfig().getDescription());	
			}
	
			ds.setTransformerId((hibDataSet.getTransformer()==null)?null:hibDataSet.getTransformer().getValueId());
			ds.setPivotColumnName(hibDataSet.getPivotColumnName());
			ds.setPivotRowName(hibDataSet.getPivotRowName());
			ds.setPivotColumnValue(hibDataSet.getPivotColumnValue());
			ds.setNumRows(hibDataSet.isNumRows());
	
			ds.setParameters(hibDataSet.getParameters());		
			ds.setDsMetadata(hibDataSet.getDsMetadata());		
	
			if(ds.getPivotColumnName() != null 
					&& ds.getPivotColumnValue() != null
					&& ds.getPivotRowName() != null){
				ds.setDataStoreTransformer(
						new PivotDataSetTransformer(ds.getPivotColumnName(), ds.getPivotColumnValue(), ds.getPivotRowName(), ds.isNumRows()));
			}
		}
		return ds;
	}

}



