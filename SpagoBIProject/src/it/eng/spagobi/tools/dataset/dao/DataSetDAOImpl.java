/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.dao;

import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.SpagoBIDOAException;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.tools.dataset.bo.CustomDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.FileDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JClassDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QbeDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.QueryDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.WSDataSetDetail;
import it.eng.spagobi.tools.dataset.metadata.SbiCustomDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQbeDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Implement CRUD operations over spagobi datsets
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataSetDAOImpl extends AbstractHibernateDAO implements IDataSetDAO  {

	static private Logger logger = Logger.getLogger(DataSetDAOImpl.class);

	// ========================================================================================
	// CREATE operations (Crud)
	// ========================================================================================
	
	/**
	 * Insert data set.
	 * @param dataSet the a data set
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#insertDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public Integer insertDataSet(GuiGenericDataSet dataSet) {
		Integer idToReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		idToReturn = null;
		session = null;
		transaction = null;
		try {
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			SbiDataSetHistory hibDataSet = null;
			
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
						((SbiQueryDataSet)hibDataSet).setQuery(((QueryDataSetDetail)dataSetActiveDetail).getQuery());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getQueryScript()!=null){
						((SbiQueryDataSet)hibDataSet).setQueryScript(((QueryDataSetDetail)dataSetActiveDetail).getQueryScript());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getQuery()!=null){
						((SbiQueryDataSet)hibDataSet).setQueryScriptLanguage(((QueryDataSetDetail)dataSetActiveDetail).getQueryScriptLanguage());
					}
					if(((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel()!=null){
						SbiDataSource hibDataSource = null;
						String dataSourceLabel = ((QueryDataSetDetail)dataSetActiveDetail).getDataSourceLabel();
						Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
						Criteria criteria = session.createCriteria(SbiDataSource.class);
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
					Criteria criteria = session.createCriteria(SbiDataSource.class);
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
				
				else if(dataSetActiveDetail instanceof CustomDataSetDetail){
					hibDataSet=new SbiCustomDataSet();
					if(((CustomDataSetDetail)dataSetActiveDetail).getCustomData()!=null){
						((SbiCustomDataSet)hibDataSet).setCustomData(((CustomDataSetDetail)dataSetActiveDetail).getCustomData());
					}
					if(((CustomDataSetDetail)dataSetActiveDetail).getJavaClassName()!=null){
						((SbiCustomDataSet)hibDataSet).setJavaClassName(((CustomDataSetDetail)dataSetActiveDetail).getJavaClassName());
					}

				}
				

				SbiDomains transformer = null;
				if (dataSetActiveDetail.getTransformerId() != null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSetActiveDetail.getTransformerId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);

					transformer = (SbiDomains) criteria.uniqueResult();

					if (transformer == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dataSetActiveDetail.getTransformerId()+" does not exist");
					}
				}

				SbiDomains category = null;
				if (dataSetActiveDetail.getCategoryId()!= null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSetActiveDetail.getCategoryId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);

					category = (SbiDomains) criteria.uniqueResult();

					if (category == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dataSetActiveDetail.getCategoryId()+" does not exist");
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
				hibDataSet.setOrganization(dsConfig.getCommonInfo().getOrganization());
				
				hibDataSet.setActive(true);			

				hibDataSet.setTransformer(transformer);
				hibDataSet.setPivotColumnName(dataSetActiveDetail.getPivotColumnName());
				hibDataSet.setPivotRowName(dataSetActiveDetail.getPivotRowName());
				hibDataSet.setPivotColumnValue(dataSetActiveDetail.getPivotColumnValue());
				hibDataSet.setNumRows(dataSetActiveDetail.isNumRows());

				hibDataSet.setCategory(category);
				hibDataSet.setParameters(dataSetActiveDetail.getParameters());
				hibDataSet.setDsMetadata(dataSetActiveDetail.getDsMetadata());

				Integer dsId =(Integer) session.save(dsConfig);
				dsConfig.setDsId(dsId);
				hibDataSet.setSbiDsConfig(dsConfig);

				session.save(hibDataSet);

				idToReturn = dsId;
				transaction.commit();
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while inserting dataset", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return idToReturn;
	}
	
	

	// ========================================================================================
	// READ operations (cRud)
	// ========================================================================================

	public List<IDataSet> loadAllActiveDataSets() {
		List<IDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<IDataSet>();
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query query = session.createQuery("from SbiDataSetHistory h where h.active = ? " );
			query.setBoolean(0, true);

			List<SbiDataSetHistory> sbiDataSetHistoryList = query.list();
			for (SbiDataSetHistory sbiDataSetHistory : sbiDataSetHistoryList) {
				if(sbiDataSetHistory != null){
					toReturn.add(DataSetFactory.toDataSet(sbiDataSetHistory));				
				}
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading datasets", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return toReturn;
	}	
	
	/**
	 * Load data set by label.
	 * @param label the label
	 * @return the data set
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#loadDataSetByLabel(string)
	 */	
	public IDataSet loadActiveDataSetByLabel(String label) {
		IDataSet toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			if(label == null) {
				throw new IllegalArgumentException("Input parameter [label] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			
			Criterion labelCriterrion = Expression.eq("label", label);
			Criteria criteria = session.createCriteria(SbiDataSetConfig.class);
			criteria.add(labelCriterrion);	
			SbiDataSetConfig hibDS = (SbiDataSetConfig) criteria.uniqueResult();
			if (hibDS == null) return null;

			Integer dsId = hibDS.getDsId();

			Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, dsId);	
			SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
			if(dsActiveDetail!=null){
				toReturn = DataSetFactory.toDataSet(dsActiveDetail);
			}
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose label is equal to [" + label+ "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return toReturn;		
	}
	
	public IDataSet loadActiveIDataSetByID(Integer id) {
		IDataSet toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");

		toReturn = null;
		session = null;
		transaction = null;
		try {
			if(id == null) {
				throw new IllegalArgumentException("Input parameter [id] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, id);	
			SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
			if(dsActiveDetail!=null){
				toReturn = DataSetFactory.toDataSet(dsActiveDetail);
			}
			transaction.commit();

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose id is equal to [" + id+ "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return toReturn;
	}
	
	

	public List<GuiGenericDataSet> loadFilteredDatasetList(String hsql, Integer offset, Integer fetchSize) {
		
		List<GuiGenericDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<GuiGenericDataSet>();
			
			if(offset == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [0]");
				offset = new Integer(0);
			}
			if(fetchSize == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [" + Integer.MAX_VALUE + "]");
				fetchSize = Integer.MAX_VALUE;
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query countQuery = session.createQuery("select count(*) " + hsql);
			Long temp = (Long)countQuery.uniqueResult();
			Integer resultNumber = new Integer(temp.intValue());

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? Math.min(fetchSize, resultNumber): resultNumber;
			}
			
			Query listQuery = session.createQuery(hsql);
			listQuery.setFirstResult(offset);
			if(fetchSize > 0) listQuery.setMaxResults(fetchSize);			
			List<SbiDataSetHistory> sbiDatasetVersions = listQuery.list();	

			if( sbiDatasetVersions != null && sbiDatasetVersions.isEmpty() == false ){
				for(SbiDataSetHistory sbiDatasetVersion : sbiDatasetVersions) {
					GuiGenericDataSet guiGenericDataSet = DataSetFactory.toGuiGenericDataSet(sbiDatasetVersion);
					
					List<GuiDataSetDetail> oldDsVersion = new ArrayList();

					if(sbiDatasetVersion.getSbiDsConfig()!=null){
						Integer dsId = sbiDatasetVersion.getSbiDsConfig().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSetHistory> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSetHistory hibOldDataSet = (SbiDataSetHistory) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									GuiDataSetDetail dsD = DataSetFactory.toGuiDataSetDetail(hibOldDataSet);		
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					guiGenericDataSet.setNonActiveDetails(oldDsVersion);
					toReturn.add(guiGenericDataSet);
				}
			}			

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset versions", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
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
	public List<GuiGenericDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize) {

		List<GuiGenericDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<GuiGenericDataSet>();
			
			if(offset == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [0]");
				offset = new Integer(0);
			}
			if(fetchSize == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [" + Integer.MAX_VALUE + "]");
				fetchSize = Integer.MAX_VALUE;
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}

			Query countQuery = session.createQuery("select count(*) from SbiDataSetConfig");
			Long resultNumber = (Long)countQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0) ? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			Query listQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? order by h.sbiDsConfig.name " );
			listQuery.setBoolean(0, true);
			listQuery.setFirstResult(offset);
			if(fetchSize > 0) listQuery.setMaxResults(fetchSize);			

			List sbiActiveDatasetsList = listQuery.list();	

			if(sbiActiveDatasetsList!=null && !sbiActiveDatasetsList.isEmpty()){
				Iterator it = sbiActiveDatasetsList.iterator();		
				while (it.hasNext()) {
					SbiDataSetHistory hibDataSet = (SbiDataSetHistory)it.next();
					GuiGenericDataSet ds = DataSetFactory.toGuiGenericDataSet(hibDataSet);
					List<GuiDataSetDetail> oldDsVersion = new ArrayList();

					if(hibDataSet.getSbiDsConfig()!=null){
						Integer dsId = hibDataSet.getSbiDsConfig().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSetHistory> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSetHistory hibOldDataSet = (SbiDataSetHistory) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									GuiDataSetDetail dsD = DataSetFactory.toGuiDataSetDetail(hibOldDataSet);		
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					ds.setNonActiveDetails(oldDsVersion);
					toReturn.add(ds);
				}
			}			
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading datasets", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	/**
	 * Load data set by id.
	 * @param dsID the ds id
	 * @return the data set as genericGuiDataset
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet loadDataSetById(Integer id) {
		GuiGenericDataSet toReturn;
		Session session;
		Transaction transaction;

		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			if(id == null) {
				throw new IllegalArgumentException("Input parameter [id] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			Query hibQueryHistory = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			hibQueryHistory.setBoolean(0, true);
			hibQueryHistory.setInteger(1, id);	
			SbiDataSetHistory sbiDataSetHistory =(SbiDataSetHistory)hibQueryHistory.uniqueResult();
			if(sbiDataSetHistory!=null){
				toReturn = DataSetFactory.toGuiGenericDataSet(sbiDataSetHistory);
			}	
			transaction.commit();

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose id is equal to [" + id + "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	/**
	 * Load data set by id.
	 * @param label the ds label
	 * @return the data set as genericGuiDataset
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet loadDataSetByLabel(String label) {
		GuiGenericDataSet toReturn;
		Session session;
		Transaction transaction;

		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			if(label == null) {
				throw new IllegalArgumentException("Input parameter [label] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			Query hibQueryHistory = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig.label = ? " );
			hibQueryHistory.setBoolean(0, true);
			hibQueryHistory.setString(1, label);	
			SbiDataSetHistory sbiDataSetHistory =(SbiDataSetHistory)hibQueryHistory.uniqueResult();
			if(sbiDataSetHistory!=null){
				GuiDataSetDetail detail = DataSetFactory.toGuiDataSetDetail(sbiDataSetHistory);
				toReturn = DataSetFactory.toGuiGenericDataSet(sbiDataSetHistory);
			}
			
			transaction.commit();

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose label is equal to [" + label + "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	/**
	 * Returns List of all existent SbiDataSetConfig elements (NO DETAIL, only name, label, descr...).
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent SbiDataSetConfig
	 * @throws EMFUserError the EMF user error
	 */
	public List<SbiDataSetConfig> loadPagedSbiDatasetConfigList(Integer offset, Integer fetchSize) {
		
		List<SbiDataSetConfig> toReturn;
		Session session;
		Transaction transaction;
		Long resultNumber;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<SbiDataSetConfig>();
			
			if(offset == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [0]");
				offset = new Integer(0);
			}
			if(fetchSize == null) {
				logger.warn("Input parameter [offset] is null. It will be set to [" + Integer.MAX_VALUE + "]");
				fetchSize = Integer.MAX_VALUE;
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}

			Query countQuery = session.createQuery("select count(*) from SbiDataSetConfig");
			resultNumber = (Long)countQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			Query listQuery = session.createQuery("from SbiDataSetConfig order by label");
			listQuery.setFirstResult(offset);
			if(fetchSize > 0) listQuery.setMaxResults(fetchSize);			
			toReturn = listQuery.list();	

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading datasets", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	/**
	 * Counts number of BIObj associated.
	 * @param dsId the ds id
	 * @return Integer, number of BIObj associated
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countBIObjAssociated (Integer dsId) {
		logger.debug("IN");		
		Integer resultNumber = new Integer(0); 
		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();

			String hql = "select count(*) from SbiObjects s where s.dataSet.dsId = ? ";
			Query aQuery = session.createQuery(hql);
			aQuery.setInteger(0, dsId.intValue());
			resultNumber = new Integer(((Long) aQuery.uniqueResult()).intValue());

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while getting the objects associated with the data set with id " + dsId, t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return resultNumber;
	}

	/**
	 * Counts number of existent DataSets
	 * @return Integer, number of existent DataSets
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countDatasets() {
		logger.debug("IN");
		Session session = null;
		Transaction transaction = null;
		Long resultNumber;		
		try {
			session = getSession();
			transaction = session.beginTransaction();

			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = session.createQuery(hql);
			resultNumber = (Long)hqlQuery.uniqueResult();

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while loading the list of SbiDataSet", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return new Integer(resultNumber.intValue());
	}

	/**
	 * Checks for bi obj associated.
	 * @param dsId the ds id
	 * @return true, if checks for bi obj associated
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataSet.dao.IDataSetDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBIObjAssociated (String dsId) {
		logger.debug("IN");		
		boolean bool = false; 

		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			Integer dsIdInt = Integer.valueOf(dsId);

			String hql = " from SbiObjects s where s.dataSet.dsId = ?";
			Query aQuery = session.createQuery(hql);
			aQuery.setInteger(0, dsIdInt.intValue());
			List biObjectsAssocitedWithDs = aQuery.list();
			if (biObjectsAssocitedWithDs.size() > 0)
				bool = true;
			else
				bool = false;
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while getting the objects associated with the data set with id " + dsId, t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return bool;
	}
	
	// ========================================================================================
	// UPDATE operations (crUd)
	// ========================================================================================

	/**
	 * Modify data set.
	 * @param aDataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#modifyDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void modifyDataSet(GuiGenericDataSet dataSet) {
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		session = null;
		transaction = null;
		try {
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
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
						((SbiQueryDataSet)hibDataSet).setQuery(((QueryDataSetDetail)dsActiveDetailToSet).getQuery());
					}
					if(((QueryDataSetDetail)dsActiveDetailToSet).getQueryScript()!=null){
						((SbiQueryDataSet)hibDataSet).setQueryScript(((QueryDataSetDetail)dsActiveDetailToSet).getQueryScript());
					}
					if(((QueryDataSetDetail)dsActiveDetailToSet).getQueryScriptLanguage()!=null){
						((SbiQueryDataSet)hibDataSet).setQueryScriptLanguage(((QueryDataSetDetail)dsActiveDetailToSet).getQueryScriptLanguage());
					}
					if(((QueryDataSetDetail)dsActiveDetailToSet).getDataSourceLabel()!=null){
						SbiDataSource hibDataSource = null;
						String dataSourceLabel = ((QueryDataSetDetail)dsActiveDetailToSet).getDataSourceLabel();
						Criterion labelCriterrion = Expression.eq("label", dataSourceLabel);
						Criteria criteria = session.createCriteria(SbiDataSource.class);
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
					Criteria criteria = session.createCriteria(SbiDataSource.class);
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
				
				else if(dsActiveDetailToSet instanceof CustomDataSetDetail){
					hibDataSet=new SbiCustomDataSet();
					if(((CustomDataSetDetail)dsActiveDetailToSet).getCustomData()!=null){
						((SbiCustomDataSet)hibDataSet).setCustomData(((CustomDataSetDetail)dsActiveDetailToSet).getCustomData());
					}
					if(((CustomDataSetDetail)dsActiveDetailToSet).getJavaClassName()!=null){
					((SbiCustomDataSet)hibDataSet).setJavaClassName(((CustomDataSetDetail)dsActiveDetailToSet).getJavaClassName());
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
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);
					transformer = (SbiDomains) criteria.uniqueResult();
					if (transformer == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dsActiveDetailToSet.getTransformerId()+" does not exist");
					}
				}

				SbiDomains category = null;
				if (dsActiveDetailToSet.getCategoryId()!= null){ 
					Criterion aCriterion = Expression.eq("valueId",	dsActiveDetailToSet.getCategoryId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);	
					category = (SbiDomains) criteria.uniqueResult();	
					if (category == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dsActiveDetailToSet.getCategoryId()+" does not exist");
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

				SbiDataSetConfig hibGenericDataSet = (SbiDataSetConfig) session.load(SbiDataSetConfig.class,dsId);					
				hibGenericDataSet.setLabel(dataSet.getLabel());
				hibGenericDataSet.setDescription(dataSet.getDescription());
				hibGenericDataSet.setName(dataSet.getName());	
				
				updateSbiCommonInfo4Update(hibGenericDataSet);

				String userUp = hibGenericDataSet.getCommonInfo().getUserUp();
				String sbiVersionUp = hibGenericDataSet.getCommonInfo().getSbiVersionUp();
				hibDataSet.setUserIn(userUp);
				hibDataSet.setSbiVersionIn(sbiVersionUp);	
				hibDataSet.setTimeIn(currentTStamp);
				hibDataSet.setOrganization(hibGenericDataSet.getCommonInfo().getOrganization());

				Integer currenthigherVersion = getHigherVersionNumForDS(dsId);
				Integer newVersion = currenthigherVersion+1;
				hibDataSet.setVersionNum(newVersion);

				Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);
				session.update(dsActiveDetail);

				session.update(hibGenericDataSet);
				hibDataSet.setSbiDsConfig(hibGenericDataSet);				
				session.save(hibDataSet);

				transaction.commit();
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while modifing the data Set with id " + ((dataSet == null)?"":String.valueOf(dataSet.getDsId())), t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
	}
	
	/**
	 * Restore an Older Version of the dataset
	 * @param dsId the a data set ID
	 * @param dsVersion the a data set Version
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet restoreOlderDataSetVersion(Integer dsId, Integer dsVersion) {
		logger.debug("IN");
		Session session = null;
		Transaction transaction = null;
		GuiGenericDataSet toReturn = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			if(dsId!=null && dsVersion!=null){

				Query hibQuery = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsActiveDetail =(SbiDataSetHistory)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);

				Query hibernateQuery = session.createQuery("from SbiDataSetHistory h where h.versionNum = ? and h.sbiDsConfig = ?" );
				hibernateQuery.setInteger(0, dsVersion);
				hibernateQuery.setInteger(1, dsId);	
				SbiDataSetHistory dsDetail =(SbiDataSetHistory)hibernateQuery.uniqueResult();
				dsDetail.setActive(true);
				
				if(dsActiveDetail.getSbiDsConfig()!=null){
					SbiDataSetConfig hibDs = dsActiveDetail.getSbiDsConfig();
					updateSbiCommonInfo4Update(hibDs);
					session.update(hibDs);
				}
				
				if(dsDetail.getSbiDsConfig()!=null){
					SbiDataSetConfig hibDs = dsDetail.getSbiDsConfig();
					updateSbiCommonInfo4Update(hibDs);
					session.update(hibDs);
				}

				session.update(dsActiveDetail);
				session.update(dsDetail);
				transaction.commit();
				toReturn = DataSetFactory.toGuiGenericDataSet(dsDetail);
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while modifing the data Set with id " + ((dsId == null)?"":String.valueOf(dsId)), t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}

	/**
	 * Returns the Higher Version Number of a selected DS
	 * @param dsId the a data set ID
	 * @throws EMFUserError the EMF user error
	 */
	public Integer getHigherVersionNumForDS(Integer dsId)  {
		logger.debug("IN");
		Session session = null;
		Transaction transaction = null;
		Integer toReturn = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			if(dsId!=null){			
				Query hibQuery = session.createQuery("select max(h.versionNum) from SbiDataSetHistory h where h.sbiDsConfig = ?" );
				hibQuery.setInteger(0, dsId);	
				toReturn =(Integer)hibQuery.uniqueResult();
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while modifing the data Set with id " + ((dsId == null)?"":String.valueOf(dsId)), t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	// ========================================================================================
	// DELETE operations (cruD)
	// ========================================================================================
	
	/**
	 * Delete data set whose ID is equal to <code>datasetId</code> if it is not referenced by
	 * some analytical documents.
	 * 
	 * @param datasetId the ID of the dataset to delete. Cannot be null.
	 * 
	 * @throws SpagoBIDOAException if the dataset is referenced by at least one analytical document
	 */
	public void deleteDataSet(Integer datasetId)  {
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		session = null;
		transaction = null;
		
		try {
			if(datasetId == null) {
				throw new IllegalArgumentException("Input parameter [datasetId] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			// check dataset is not used by any document: a DB constraint should be added in future
			Query hibernateQuery = session.createQuery("from SbiObjects h where h.dataSet = ?" );	
			hibernateQuery.setInteger(0, datasetId);
			List objectsRelated = hibernateQuery.list();
			if(objectsRelated != null && objectsRelated.size() > 0){
				String message = "Dataset with id [" + datasetId + "] " +
						"cannot be erased because it is referenced by [" + objectsRelated.size() + "] document(s)";
				throw new SpagoBIDOAException(message);
			}
			
			SbiDataSetConfig sbiDataSetConfig = (SbiDataSetConfig) session.load(SbiDataSetConfig.class, datasetId);
			session.delete(sbiDataSetConfig);

			transaction.commit();			
			
		}  catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while deleting dataset " +
					"whose id is equal to [" + datasetId + "]", t);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
	}
	

	/**
	 * Delete the dataset version whose id is equal to <code>datasetVersionId</code> if and 
	 * only if it is inactive.
	 * 
	 * @param datasetVersionId the id of the version of the dataset to delete. Cannot be null.
	 * 
	 * @return true if the version whose id is equal to <code>datasetVersionId</code> is deleted from database.
	 * false otherwise (the version does not exist or it exists but it is active).
	 */
	public boolean deleteInactiveDataSetVersion(Integer datasetVersionId) {
		Session session;
		Transaction transaction;		
		boolean deleted;
	
		logger.debug("IN");
		
		session = null;
		transaction = null;	
		deleted = false;
		
		try {
			
			if(datasetVersionId == null) {
				throw new IllegalArgumentException("Input parameter [datasetVersionId] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}

			SbiDataSetHistory sbiDataSetHistory = (SbiDataSetHistory) session.load(SbiDataSetHistory.class, datasetVersionId);
			if(sbiDataSetHistory != null && sbiDataSetHistory.isActive() == false ){
				session.delete(sbiDataSetHistory);
				transaction.commit();	
				deleted = true;
			}	
			
		}  catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while deleting dataset version" +
					"whose id is equal to [" + datasetVersionId + "]", t);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return deleted;
	}

	/**
	 * Delete all inactive versions of dataset whose id is equal to <code>datasetId</code>
	 * 
	 * @param datasetId the id of the of the dataset whose incative version must be deleted
	 * 
	 * @return true if the incative versions of dataset whose id is equal to  <code>datasetId</code> have
	 * been succesfully deleted from database. false otherwise (i.e. the dtaset does not have any inactive versions)
	 */
	public boolean deleteAllInactiveDataSetVersions(Integer datasetId) {
		Session session;
		Transaction transaction;
		boolean deleted;
		
		logger.debug("IN");
		
		session = null;
		transaction = null;
		deleted = false;		
		try {
			if(datasetId == null) {
				throw new IllegalArgumentException("Input parameter [datasetId] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query query = session.createQuery("from SbiDataSetHistory h where h.active = ? and h.sbiDsConfig = ?" );
			query.setBoolean(0, false);
			query.setInteger(1, datasetId);	
			
			List toBeDeleted = query.list();
			
			if(toBeDeleted != null && toBeDeleted.isEmpty() == false){
				Iterator it = toBeDeleted.iterator();
				while(it.hasNext()){
					SbiDataSetHistory sbiDataSetHistory = (SbiDataSetHistory) it.next();
					if(sbiDataSetHistory!=null && !sbiDataSetHistory.isActive()){
						session.delete(sbiDataSetHistory);			
					}
				}
				transaction.commit();
				deleted = true;
			}	
							
		}  catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while deleting inactive versions of dataset " +
					"whose id is equal to [" + datasetId + "]", t);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return deleted;
	}
	

	// ========================================================================================
	// CONVERSIONS
	// ========================================================================================
	
	/**
	 * @deprecated
	 */
	public GuiGenericDataSet toGuiGenericDataSet(IDataSet iDataSet) {		
		return DataSetFactory.toGuiGenericDataSet(iDataSet);
	}
	
	/** 
	 * copy a dataset history
	 * 
	 * @param hibDataSet
	 * @return
	 */

	public SbiDataSetHistory copyDataSetHistory(SbiDataSetHistory hibDataSet) {		

		logger.debug("IN");
		SbiDataSetHistory hibNew = null;

		if(hibDataSet instanceof SbiFileDataSet){		
			hibNew  = new SbiFileDataSet();
			((SbiFileDataSet)hibNew).setFileName(((SbiFileDataSet)hibDataSet).getFileName());		
		}

		if(hibDataSet instanceof SbiQueryDataSet){			
			hibNew  = new SbiQueryDataSet();
			((SbiQueryDataSet)hibNew).setQuery(((SbiQueryDataSet)hibDataSet).getQuery());
			((SbiQueryDataSet)hibNew).setQueryScript(((SbiQueryDataSet)hibDataSet).getQueryScript());
			((SbiQueryDataSet)hibNew).setQueryScriptLanguage(((SbiQueryDataSet)hibDataSet).getQueryScriptLanguage());
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
		
		if(hibDataSet instanceof SbiCustomDataSet){			
			hibNew =new SbiCustomDataSet();
			((SbiCustomDataSet)	hibNew ).setCustomData(((SbiCustomDataSet)hibDataSet).getCustomData());
			((SbiCustomDataSet)	hibNew ).setJavaClassName(((SbiCustomDataSet)hibDataSet).getJavaClassName());
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
}



