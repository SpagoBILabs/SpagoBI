/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.dao;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.SpagoBIDOAException;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.VersionedDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetId;
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
	public Integer insertDataSet(IDataSet dataSet) {
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
 
			SbiDomains transformer = null;
			if (dataSet.getTransformerId() != null){ 
				Criterion aCriterion = Expression.eq("valueId",	dataSet.getTransformerId());
				Criteria criteria = session.createCriteria(SbiDomains.class);
				criteria.add(aCriterion);

				transformer = (SbiDomains) criteria.uniqueResult();

				if (transformer == null){
					throw new SpagoBIDOAException("The Domain with value_id= "+dataSet.getTransformerId()+" does not exist");
				}
			}

			SbiDomains category = null;
			if (dataSet.getCategoryId()!= null){ 
				Criterion aCriterion = Expression.eq("valueId",	dataSet.getCategoryId());
				Criteria criteria = session.createCriteria(SbiDomains.class);
				criteria.add(aCriterion);

				category = (SbiDomains) criteria.uniqueResult();

				if (category == null){
					throw new SpagoBIDOAException("The Domain with value_id= "+dataSet.getCategoryId()+" does not exist");
				}
			}
			
			SbiDataSetId compositeKey = getDataSetKey(session, dataSet, true);
			SbiDataSet hibDataSet = new SbiDataSet(compositeKey);
			
			Date currentTStamp = new Date();							
			hibDataSet.setLabel(dataSet.getLabel());
			hibDataSet.setDescription(dataSet.getDescription());
			hibDataSet.setName(dataSet.getName());
			hibDataSet.setType(dataSet.getDsType());
			updateSbiCommonInfo4Insert(hibDataSet);

			String userIn = hibDataSet.getCommonInfo().getUserIn();
			String sbiVersionIn = hibDataSet.getCommonInfo().getSbiVersionIn();
			hibDataSet.setUserIn(userIn);
			hibDataSet.setSbiVersionIn(sbiVersionIn);		
			hibDataSet.getId().setVersionNum(1);
			hibDataSet.setTimeIn(currentTStamp);
			hibDataSet.setOrganization(hibDataSet.getCommonInfo().getOrganization());
			hibDataSet.setConfiguration(dataSet.getConfiguration());
			hibDataSet.setActive(true);			

			hibDataSet.setTransformer(transformer);
			hibDataSet.setPivotColumnName(dataSet.getPivotColumnName());
			hibDataSet.setPivotRowName(dataSet.getPivotRowName());
			hibDataSet.setPivotColumnValue(dataSet.getPivotColumnValue());
			hibDataSet.setNumRows(dataSet.isNumRows());
			
			//manage of persistence fields
			if (dataSet.getDataSourcePersistId() != null){
				Integer dataSourcePersist = dataSet.getDataSourcePersistId();
				Criterion labelCriterrion = Expression.eq("dsId", dataSourcePersist);
				Criteria criteria = session.createCriteria(SbiDataSource.class);
				criteria.add(labelCriterrion);	
				SbiDataSource hibDataSourcePersist = (SbiDataSource) criteria.uniqueResult();
				hibDataSet.setDataSourcePersist(hibDataSourcePersist);
			}
			hibDataSet.setPersisted(dataSet.isPersisted());
			//manage of flat fields
			hibDataSet.setFlatDataset(dataSet.isFlatDataset());
			if (dataSet.getDataSourceFlatId() != null){
				Integer dataSourceFlat = dataSet.getDataSourceFlatId();
				Criterion labelCriterrion = Expression.eq("dsId", dataSourceFlat);
				Criteria criteria = session.createCriteria(SbiDataSource.class);
				criteria.add(labelCriterrion);	
				SbiDataSource hibDataSourceFlat = (SbiDataSource) criteria.uniqueResult();
				hibDataSet.setDataSourceFlat(hibDataSourceFlat);
			}
			hibDataSet.setFlatTableName(dataSet.getFlatTableName());

			hibDataSet.setCategory(category);
			hibDataSet.setParameters(dataSet.getParameters());
			hibDataSet.setDsMetadata(dataSet.getDsMetadata());
			hibDataSet.setOwner(userIn);
			hibDataSet.setPublicDS(dataSet.isPublic());

			session.save(hibDataSet);
			
			idToReturn = hibDataSet.getId().getDsId();
			transaction.commit();
	
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
			
			Query query = session.createQuery("from SbiDataSet h where h.active = ? " );
			query.setBoolean(0, true);

			List<SbiDataSet> sbiDataSetList = query.list();
			for (SbiDataSet sbiDataSet : sbiDataSetList) {
				if(sbiDataSet != null){
					toReturn.add(DataSetFactory.toDataSet(sbiDataSet));				
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
	 * Load all data set by owner.
	 * @param ower the owner
	 * @return list of data set
	 * @see List#loadActiveDataSetByOwner(string)
	 */	
	public  List<IDataSet> loadAllActiveDataSetsByOwner(String owner) {
		List<IDataSet>  toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = new ArrayList<IDataSet>();
		session = null;
		transaction = null;
		try {
			if(owner == null) {
				throw new IllegalArgumentException("Input parameter [owner] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and (h.publicDS = ? or h.owner = ?)" );
			hibQuery.setBoolean(0, true);
			hibQuery.setBoolean(1, true);
			hibQuery.setString(2, owner);	
			
			List<SbiDataSet> sbiDataSetList = hibQuery.list();
			for (SbiDataSet sbiDataSet : sbiDataSetList) {
				if(sbiDataSet != null){
					toReturn.add(DataSetFactory.toDataSet(sbiDataSet));				
				}
			}
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose label is equal to [" + owner+ "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		
		return toReturn;		
	}
	

	/**
	 * Load all data set by owner.
	 * @param ower the owner
	 * @return list of data set
	 * @see List#loadActiveDataSetByOwner(string)
	 */	
	public  List<IDataSet> loadAllActiveDataSetsByOwnerAndType(String owner, String type) {
		List<IDataSet>  toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = new ArrayList<IDataSet>();
		session = null;
		transaction = null;
		try {
			if(owner == null) {
				throw new IllegalArgumentException("Input parameter [owner] cannot be null");
			}
			if(type == null) {
				throw new IllegalArgumentException("Input parameter [type] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.type = ? and (h.publicDS = ? or h.owner = ?)" );
			hibQuery.setBoolean(0, true);
			hibQuery.setString(1, type);
			hibQuery.setBoolean(2, true);
			hibQuery.setString(3, owner);	
			
			List<SbiDataSet> sbiDataSetList = hibQuery.list();
			for (SbiDataSet sbiDataSet : sbiDataSetList) {
				if(sbiDataSet != null){
					toReturn.add(DataSetFactory.toDataSet(sbiDataSet));				
				}
			}
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose label is equal to [" + owner+ "]", t);	
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
			
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.label = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setString(1, label);					
			SbiDataSet dsActiveDetail =(SbiDataSet)hibQuery.uniqueResult();
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
			
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, id);	
			SbiDataSet dsActiveDetail =(SbiDataSet)hibQuery.uniqueResult();
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
	
	
	

	public List<IDataSet> loadFilteredDatasetList(String hsql, Integer offset, Integer fetchSize) {
		
		List<IDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<IDataSet>();
			
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
			List<SbiDataSet> sbiDatasetVersions = listQuery.list();	

			if( sbiDatasetVersions != null && sbiDatasetVersions.isEmpty() == false ){
				for(SbiDataSet sbiDatasetVersion : sbiDatasetVersions) {
					IDataSet guiDataSet = DataSetFactory.toDataSet(sbiDatasetVersion);
					
					List<IDataSet> oldDsVersion = new ArrayList();

					if(Integer.valueOf(sbiDatasetVersion.getId().getVersionNum())!=null){
						Integer dsId = sbiDatasetVersion.getId().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSet> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSet hibOldDataSet = (SbiDataSet) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									IDataSet dsD = DataSetFactory.toDataSet(hibOldDataSet);
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					guiDataSet.setNoActiveVersions(oldDsVersion);
					toReturn.add(guiDataSet);
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
	
	public List<IDataSet> loadFilteredDatasetList(String hsql, Integer offset, Integer fetchSize, String owner) {
		
		List<IDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<IDataSet>();
			
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
			List<SbiDataSet> sbiDatasetVersions = listQuery.list();	

			if( sbiDatasetVersions != null && sbiDatasetVersions.isEmpty() == false ){
				for(SbiDataSet sbiDatasetVersion : sbiDatasetVersions) {
					IDataSet guiDataSet = DataSetFactory.toDataSet(sbiDatasetVersion);
					
					List<IDataSet> oldDsVersion = new ArrayList();

					if(Integer.valueOf(sbiDatasetVersion.getId().getVersionNum())!=null){
						Integer dsId = sbiDatasetVersion.getId().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ? and (h.publicDS = ? or h.owner = ?) " );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);
						hibQuery.setBoolean(2, true);
						hibQuery.setString(3, owner);	

						List<SbiDataSet> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSet hibOldDataSet = (SbiDataSet) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){
									IDataSet dsD = DataSetFactory.toDataSet(hibOldDataSet);
									oldDsVersion.add(dsD);
								}
							}
						}			
					}
					guiDataSet.setNoActiveVersions(oldDsVersion);
					toReturn.add(guiDataSet);
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
	public List<IDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize) {

		List<IDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<IDataSet>();
			
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

			Query countQuery = session.createQuery("select count(*) from SbiDataSet sb where sb.active = ? ");
			countQuery.setBoolean(0, true);
			Long resultNumber = (Long)countQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0) ? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			Query listQuery = session.createQuery("from SbiDataSet h where h.active = ? order by h.name " );
			listQuery.setBoolean(0, true);
			listQuery.setFirstResult(offset);
			if(fetchSize > 0) listQuery.setMaxResults(fetchSize);			

			List sbiActiveDatasetsList = listQuery.list();	

			if(sbiActiveDatasetsList!=null && !sbiActiveDatasetsList.isEmpty()){
				Iterator it = sbiActiveDatasetsList.iterator();		
				while (it.hasNext()) {
					SbiDataSet hibDataSet = (SbiDataSet)it.next();
					IDataSet ds = DataSetFactory.toDataSet(hibDataSet);
					List<IDataSet> oldDsVersion = new ArrayList();

					if(Integer.valueOf(hibDataSet.getId().getDsId()) != null){
						Integer dsId = hibDataSet.getId().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	

						List<SbiDataSet> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSet hibOldDataSet = (SbiDataSet) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){	
									VersionedDataSet dsD = (VersionedDataSet)DataSetFactory.toDataSet(hibOldDataSet);
									oldDsVersion.add(dsD);
								}
							}
						}			
					} 
					ds.setNoActiveVersions(oldDsVersion);
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
	 * Returns List of all existent IDataSets with current active version for the owner
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent IDataSets with current active version
	 * @throws EMFUserError the EMF user error
	 */
	public List<IDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize, String owner) {

		List<IDataSet> toReturn;
		Session session;
		Transaction transaction;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<IDataSet>();
			
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

			Query countQuery = session.createQuery("select count(*) from SbiDataSet sb where sb.active = ? ");
			countQuery.setBoolean(0, true);
			Long resultNumber = (Long)countQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0) ? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			Query listQuery = session.createQuery("from SbiDataSet h where h.active = ? and (h.publicDS = ? or h.owner = ?) order by h.name " );
			listQuery.setBoolean(0, true);
			listQuery.setBoolean(1, true);
			listQuery.setString(2, owner);
			listQuery.setFirstResult(offset);
			if(fetchSize > 0) listQuery.setMaxResults(fetchSize);			

			List sbiActiveDatasetsList = listQuery.list();	

			if(sbiActiveDatasetsList!=null && !sbiActiveDatasetsList.isEmpty()){
				Iterator it = sbiActiveDatasetsList.iterator();		
				while (it.hasNext()) {
					SbiDataSet hibDataSet = (SbiDataSet)it.next();
					IDataSet ds = DataSetFactory.toDataSet(hibDataSet);
					List<IDataSet> oldDsVersion = new ArrayList();

					if(Integer.valueOf(hibDataSet.getId().getDsId()) != null){
						Integer dsId = hibDataSet.getId().getDsId();
						Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?  and (h.publicDS = ? or h.owner = ?)  " );
						hibQuery.setBoolean(0, false);
						hibQuery.setInteger(1, dsId);	
						hibQuery.setBoolean(2, true);
						hibQuery.setString(3, owner);

						List<SbiDataSet> olderTemplates = hibQuery.list();
						if(olderTemplates!=null && !olderTemplates.isEmpty()){
							Iterator it2 = olderTemplates.iterator();
							while(it2.hasNext()){
								SbiDataSet hibOldDataSet = (SbiDataSet) it2.next();
								if(hibOldDataSet!=null && !hibOldDataSet.isActive()){	
									VersionedDataSet dsD = (VersionedDataSet)DataSetFactory.toDataSet(hibOldDataSet);
									oldDsVersion.add(dsD);
								}
							}
						}			
					} 
					ds.setNoActiveVersions(oldDsVersion);
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
	public IDataSet loadDataSetById(Integer id) {
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
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
			hibQuery.setBoolean(0, true);
			hibQuery.setInteger(1, id);	
			SbiDataSet sbiDataSet =(SbiDataSet)hibQuery.uniqueResult();
			if(sbiDataSet!=null){
				toReturn = DataSetFactory.toDataSet(sbiDataSet);
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
	public IDataSet loadDataSetByLabel(String label) {
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
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.label = ? " );
			hibQuery.setBoolean(0, true);
			hibQuery.setString(1, label);	
			SbiDataSet sbiDataSet =(SbiDataSet)hibQuery.uniqueResult();
			if(sbiDataSet!=null){
				//GuiDataSetDetail detail = DataSetFactory.toGuiDataSet(sbiDataSet);
				//toReturn = DataSetFactory.toGuiDataSet(sbiDataSet);
				toReturn = DataSetFactory.toDataSet(sbiDataSet);
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
	 * Load data set by owner.
	 * @param owner the ds owner
	 * @return the data set as genericGuiDataset
	 * @throws EMFUserError the EMF user error
	 */
	public IDataSet loadDataSetByOwner(String owner) {
		IDataSet toReturn;
		Session session;
		Transaction transaction;

		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			if(owner == null) {
				throw new IllegalArgumentException("Input parameter [owner] cannot be null");
			}
			
			try {
				session = getSession();
				Assert.assertNotNull(session, "session cannot be null");
				transaction = session.beginTransaction();
				Assert.assertNotNull(transaction, "transaction cannot be null");
			} catch(Throwable t) {
				throw new SpagoBIDOAException("An error occured while creating the new transaction", t);
			}
			Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and owner = ? " );
			hibQuery.setBoolean(0, true);
			hibQuery.setString(1, owner);	
			SbiDataSet sbiDataSet =(SbiDataSet)hibQuery.uniqueResult();
			if(sbiDataSet!=null){
				//GuiDataSetDetail detail = DataSetFactory.toGuiDataSet(sbiDataSet);
				//toReturn = DataSetFactory.toGuiDataSet(sbiDataSet);
				toReturn = DataSetFactory.toDataSet(sbiDataSet);
			}
			
			transaction.commit();

		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("An unexpected error occured while loading dataset whose label is equal to [" + owner + "]", t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return toReturn;
	}
	
	/**
	 * Returns List of all existent SbiDataSet elements (NO DETAIL, only name, label, descr...).
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent SbiDataSet
	 * @throws EMFUserError the EMF user error
	 */
	public List<SbiDataSet> loadPagedSbiDatasetConfigList(Integer offset, Integer fetchSize) {
		
		List<SbiDataSet> toReturn;
		Session session;
		Transaction transaction;
		Long resultNumber;
		
		logger.debug("IN");
		
		toReturn = null;
		session = null;
		transaction = null;
		try {
			toReturn = new ArrayList<SbiDataSet>();
			
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

			Query countQuery = session.createQuery("select count(*) from SbiDataSet ds where ds.active = ?");
			countQuery.setBoolean(0, true);
			resultNumber = (Long)countQuery.uniqueResult();

			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? 
						Math.min(fetchSize, resultNumber.intValue()) 
						: resultNumber.intValue();
			}

			Query listQuery = session.createQuery("from SbiDataSet ds where ds.active=true order by label");
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

			String hql = "select count(*) from SbiObjects s where s.dataSet = ? ";
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

			String hql = "select count(*) from SbiDataSet ds where ds.active = ? ";
			Query hqlQuery = session.createQuery(hql);
			hqlQuery.setBoolean(0, true);
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

			String hql = " from SbiObjects s where s.dataSet = ?";
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
	
	
	/**
	 * Checks for bi kpi associated.
	 * @param dsId the ds id
	 * @return true, if checks for bi kpi associated
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataSet.dao.IDataSetDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBIKpiAssociated (String dsId) {
		logger.debug("IN");		
		boolean bool = false; 

		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			Integer dsIdInt = Integer.valueOf(dsId);

			String hql = " from SbiKpi s where s.sbiDataSet = ?";
			Query aQuery = session.createQuery(hql);
			aQuery.setInteger(0, dsIdInt.intValue());
			List biKPIAssocitedWithDs = aQuery.list();
			if (biKPIAssocitedWithDs.size() > 0)
				bool = true;
			else
				bool = false;
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while getting the kpi associated with the data set with id " + dsId, t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
		return bool;
	}
	
	/**
	 * Checks for bi lovs associated.
	 * @param dsId the ds id
	 * @return true, if checks for lovs associated
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataSet.dao.IDataSetDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBILovAssociated (String dsId) {
		logger.debug("IN");		
		boolean bool = false; 

		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			Integer dsIdInt = Integer.valueOf(dsId);

			String hql = " from SbiLov s where datasetId = ?";
			Query aQuery = session.createQuery(hql);
			aQuery.setInteger(0, dsIdInt.intValue());
			List biKPIAssocitedWithDs = aQuery.list();
			if (biKPIAssocitedWithDs.size() > 0)
				bool = true;
			else
				bool = false;
			transaction.commit();
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while getting the lovs associated with the data set with id " + dsId, t);	
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
	public void modifyDataSet(IDataSet dataSet) {
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
			SbiDataSetId compositeKey = getDataSetKey(session, dataSet, false);
			SbiDataSet hibDataSet = new SbiDataSet(compositeKey);
			if(dataSet!=null){				
				Integer dsId = dataSet.getId();
				hibDataSet.setActive(true);		
				SbiDomains transformer = null;
				if (dataSet.getTransformerId() != null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSet.getTransformerId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);
					transformer = (SbiDomains) criteria.uniqueResult();
					if (transformer == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dataSet.getTransformerId()+" does not exist");
					}
				}

				SbiDomains category = null;
				if (dataSet.getCategoryId()!= null){ 
					Criterion aCriterion = Expression.eq("valueId",	dataSet.getCategoryId());
					Criteria criteria = session.createCriteria(SbiDomains.class);
					criteria.add(aCriterion);	
					category = (SbiDomains) criteria.uniqueResult();	
					if (category == null){
						throw new SpagoBIDOAException("The Domain with value_id= "+dataSet.getCategoryId()+" does not exist");
					}
				}
				Date currentTStamp = new Date();
				hibDataSet.setTimeIn(currentTStamp);		
				hibDataSet.setTransformer(transformer);
				hibDataSet.setPivotColumnName(dataSet.getPivotColumnName());
				hibDataSet.setPivotRowName(dataSet.getPivotRowName());
				hibDataSet.setPivotColumnValue(dataSet.getPivotColumnValue());
				hibDataSet.setNumRows(dataSet.isNumRows());

				hibDataSet.setCategory(category);
				hibDataSet.setParameters(dataSet.getParameters());
				hibDataSet.setDsMetadata(dataSet.getDsMetadata());
				
				//manage of persistence fields
			
				if (dataSet.getDataSourcePersistId() != null){
					Integer dataSourcePersist = dataSet.getDataSourcePersistId();
					Criterion labelCriterrion = Expression.eq("id", dataSourcePersist);
					Criteria criteria = session.createCriteria(SbiDataSource.class);
					criteria.add(labelCriterrion);	
					SbiDataSource hibDataSourcePersist = (SbiDataSource) criteria.uniqueResult();
					hibDataSet.setDataSourcePersist(hibDataSourcePersist);
				}
				hibDataSet.setPersisted(dataSet.isPersisted());
				//manage of flat fields
				hibDataSet.setFlatDataset(dataSet.isFlatDataset());
				if (dataSet.getDataSourceFlatId() != null){
					Integer dataSourceFlat = dataSet.getDataSourceFlatId();
					Criterion labelCriterrion = Expression.eq("id", dataSourceFlat);
					Criteria criteria = session.createCriteria(SbiDataSource.class);
					criteria.add(labelCriterrion);	
					SbiDataSource hibDataSourceFlat = (SbiDataSource) criteria.uniqueResult();
					hibDataSet.setDataSourceFlat(hibDataSourceFlat);
				}
				
				hibDataSet.setFlatTableName(dataSet.getFlatTableName());			
				hibDataSet.setLabel(dataSet.getLabel());
				hibDataSet.setDescription(dataSet.getDescription());
				hibDataSet.setName(dataSet.getName());	
				hibDataSet.setConfiguration(dataSet.getConfiguration());
				hibDataSet.setType(dataSet.getDsType());
				updateSbiCommonInfo4Insert(hibDataSet);

				String userIn = hibDataSet.getCommonInfo().getUserIn();
				String sbiVersionIn = hibDataSet.getCommonInfo().getSbiVersionIn();
				hibDataSet.setUserIn(userIn);
				hibDataSet.setSbiVersionIn(sbiVersionIn);	
				hibDataSet.setTimeIn(currentTStamp);
				hibDataSet.setOrganization(hibDataSet.getCommonInfo().getOrganization());
				hibDataSet.setPublicDS(dataSet.isPublic());
				hibDataSet.setOwner(userIn);

				Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSet dsActiveDetail =(SbiDataSet)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);
				session.update(dsActiveDetail);
				session.save(hibDataSet);

				transaction.commit();
			}
		} catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new SpagoBIDOAException("Error while modifing the data Set with id " + ((dataSet == null)?"":String.valueOf(dataSet.getId())), t);	
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
			logger.debug("OUT");
		}
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
				Query hibQuery = session.createQuery("select max(h.id.versionNum) from SbiDataSet h where h.id.dsId = ?" );
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
			
			// check dataset is not used by any document:
//			Query hibernateQuery = session.createQuery("from SbiObjects h where h.dataSet = ?" );	
//			hibernateQuery.setInteger(0, datasetId);
//			List objectsRelated = hibernateQuery.list();
//			if(objectsRelated != null && objectsRelated.size() > 0){
//				String message = "Dataset with id [" + datasetId + "] " +
//						"cannot be erased because it is referenced by [" + objectsRelated.size() + "] document(s)";
//				throw new SpagoBIDOAException(message);
//			}
			boolean bObjects = hasBIObjAssociated(String.valueOf(datasetId));
			boolean bLovs = hasBILovAssociated(String.valueOf(datasetId));
			boolean bKpis = hasBIKpiAssociated(String.valueOf(datasetId));
			if (bObjects || bLovs || bKpis) {
				String message = "[deleteInUseDSError]: Dataset with id [" + datasetId + "] " +
				"cannot be erased because it is referenced by documents or kpis or lovs.";
				throw new SpagoBIDOAException(message);
			}

			//deletes all versions of the dataset specified
			Query hibernateQuery = session.createQuery("from SbiDataSet h where h.id.dsId = ? " );	
			hibernateQuery.setInteger(0, datasetId);				
			List<SbiDataSet> sbiDataSetList = hibernateQuery.list();
			for (SbiDataSet sbiDataSet : sbiDataSetList) {
				if(sbiDataSet != null){
					session.delete(sbiDataSet);		
				}
			}


			transaction.commit();			
			
		}  catch (Throwable t) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			String msg = (t.getMessage()!=null)?t.getMessage():"An unexpected error occured while deleting dataset " +
					"whose id is equal to [" + datasetId + "]";
			throw new SpagoBIDOAException(msg, t);
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
	public IDataSet restoreOlderDataSetVersion(Integer dsId, Integer dsVersion) {
		logger.debug("IN");
		Session session = null;
		Transaction transaction = null;
		IDataSet toReturn = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			if(dsId!=null && dsVersion!=null){
	
				Query hibQuery = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
				hibQuery.setBoolean(0, true);
				hibQuery.setInteger(1, dsId);	
				SbiDataSet dsActiveDetail =(SbiDataSet)hibQuery.uniqueResult();
				dsActiveDetail.setActive(false);
	
				Query hibernateQuery = session.createQuery("from SbiDataSet h where h.id.versionNum = ? and h.id.dsId = ?" );
				hibernateQuery.setInteger(0, dsVersion);
				hibernateQuery.setInteger(1, dsId);	
				SbiDataSet dsDetail =(SbiDataSet)hibernateQuery.uniqueResult();
				dsDetail.setActive(true);
				
				session.update(dsActiveDetail);
				session.update(dsDetail);
				transaction.commit();
				//toReturn = DataSetFactory.toGuiDataSet(dsDetail);
				toReturn = DataSetFactory.toDataSet(dsDetail);
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

			//SbiDataSet sbiDataSet = (SbiDataSet) session.load(SbiDataSet.class, datasetVersionId);
			Query countQuery = session.createQuery("from SbiDataSet ds where ds.active = ? and ds.id.versionNum = ?");
			countQuery.setBoolean(0, false);
			countQuery.setInteger(1, datasetVersionId);
			SbiDataSet sbiDataSet = (SbiDataSet)countQuery.uniqueResult();
			if(sbiDataSet != null && sbiDataSet.isActive() == false ){
				session.delete(sbiDataSet);
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
			
			Query query = session.createQuery("from SbiDataSet h where h.active = ? and h.id.dsId = ?" );
			query.setBoolean(0, false);
			query.setInteger(1, datasetId);	
			
			List toBeDeleted = query.list();
			
			if(toBeDeleted != null && toBeDeleted.isEmpty() == false){
				Iterator it = toBeDeleted.iterator();
				while(it.hasNext()){
					SbiDataSet sbiDataSet = (SbiDataSet) it.next();
					if(sbiDataSet!=null && !sbiDataSet.isActive()){
						session.delete(sbiDataSet);			
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
	public IDataSet toGuiGenericDataSet(IDataSet iDataSet) {		
		return DataSetFactory.toGuiDataSet(iDataSet);
	}
	
	/** 
	 * copy a dataset 
	 * 
	 * @param hibDataSet
	 * @return
	 */

	public SbiDataSet copyDataSet(SbiDataSet hibDataSet) {		

		logger.debug("IN");		
		SbiDataSet hibNew = hibDataSet;
		
		
		/*
		SbiDataSet hibNew = null;

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
		
		if(hibDataSet instanceof SbiQbeDataSet){			
			hibNew =new SbiQbeDataSet();
			((SbiQbeDataSet) hibNew ).setSqlQuery(((SbiQbeDataSet)hibDataSet).getSqlQuery());
			((SbiQbeDataSet) hibNew ).setJsonQuery(((SbiQbeDataSet)hibDataSet).getJsonQuery());
			((SbiQbeDataSet) hibNew ).setDataSource(((SbiQbeDataSet)hibDataSet).getDataSource());
			((SbiQbeDataSet) hibNew ).setDatamarts(((SbiQbeDataSet)hibDataSet).getDatamarts());
			
		
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
*/
		logger.debug("OUT");
		return hibNew;
	}
	
	private SbiDataSetId getDataSetKey(Session aSession, IDataSet dataSet, boolean isInsert){
		SbiDataSetId toReturn = new SbiDataSetId();
		// get the next id or version num of the dataset managed
		Integer maxId = null;
		Integer nextId = null;
		String hql = null;
		Query query = null;
		if (isInsert){
			hql = " select max(sb.id.dsId) as maxId from SbiDataSet sb ";
			toReturn.setVersionNum(new Integer("1"));
			query = aSession.createQuery(hql);
		}else{
			hql = " select max(sb.id.versionNum) as maxId from SbiDataSet sb where sb.id.dsId = ? ";
			query = aSession.createQuery(hql);
			query.setInteger(0, dataSet.getId());
			toReturn.setDsId(dataSet.getId());
		}

		List result = query.list();
		Iterator it = result.iterator();
		while (it.hasNext()){
			maxId = (Integer) it.next();				
		}
		logger.debug("Current max prog : " + maxId);
		if (maxId == null) {
			nextId = new Integer(1);
		} else {
			nextId = new Integer(maxId.intValue() + 1);
		}	
		
		if (isInsert){
			logger.debug("Nextid: " + nextId);
			toReturn.setDsId(nextId);
		}else{
			logger.debug("NextVersion: " + nextId);
			toReturn.setVersionNum(nextId);
		}

		return toReturn;
	}
}



