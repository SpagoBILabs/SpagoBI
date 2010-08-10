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

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.model.metadata.SbiResources;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.common.transformer.PivotDataSetTransformer;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.DataSourceDAOHibImpl;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

import java.util.ArrayList;
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

public class DataSetDAOHibImpl extends AbstractHibernateDAO implements IDataSetDAO  {
	static private Logger logger = Logger.getLogger(DataSetDAOHibImpl.class);

	/**
	 * Load data set by id.
	 * 
	 * @param dsID the ds id
	 * 
	 * @return the data set
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#loadDataSetByID(java.lang.Integer)
	 */
	public IDataSet loadDataSetByID(Integer dsID) throws EMFUserError {
		logger.debug("IN");
		IDataSet toReturn = null;
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDataSetConfig hibDataSet = (SbiDataSetConfig)aSession.load(SbiDataSetConfig.class,  dsID);

			toReturn = toDataSet(hibDataSet);
			tx.commit();

		} catch (HibernateException he) {
			logger.error("Error while loading the data Set with id " + dsID.toString(), he);			

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
	 * 
	 * @param label the label
	 * 
	 * @return the data set
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#loadDataSetByLabel(string)
	 */	
	public IDataSet loadDataSetByLabel(String label) throws EMFUserError {
		logger.debug("IN");
		IDataSet biDS = null;
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label", label);
			Criteria criteria = tmpSession.createCriteria(SbiDataSetConfig.class);
			criteria.add(labelCriterrion);	
			SbiDataSetConfig hibDS = (SbiDataSetConfig) criteria.uniqueResult();
			if (hibDS == null) return null;
			biDS = toDataSet(hibDS);				

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading the data set with label " + label, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
		}
		logger.debug("OUT");
		return biDS;		
	}

	/**
	 * Load all data sets.
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#loadAllDataSets()
	 */
	public List loadAllDataSets() throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiDataSetConfig");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				realResult.add(toDataSet((SbiDataSetConfig) it.next()));
			}
			tx.commit();
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
	 * Modify data set.
	 * 
	 * @param aDataSet the a data set
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#modifyDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void modifyDataSet(IDataSet aDataSet) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();


			SbiDataSetConfig hibDataSet = (SbiDataSetConfig) aSession.load(SbiDataSetConfig.class,
					new Integer(aDataSet.getId()));			

			if(aDataSet instanceof FileDataSet){
				//hibDataSet=new SbiFileDataSet();
				if(((FileDataSet)aDataSet).getFileName()!=null){
					((SbiFileDataSet)hibDataSet).setFileName(((FileDataSet)aDataSet).getFileName());
				}
			}

			else if(aDataSet instanceof JDBCDataSet){
				//hibDataSet=new SbiQueryDataSet();
				if(((JDBCDataSet)aDataSet).getQuery()!=null){
					((SbiQueryDataSet)hibDataSet).setQuery(((JDBCDataSet)aDataSet).getQuery().toString());
				}
				if(((JDBCDataSet)aDataSet).getDataSource()!=null){
					SbiDataSource hibDataSource = null;
					hibDataSource = (SbiDataSource) aSession.load(SbiDataSource.class, new Integer(((JDBCDataSet)aDataSet).getDataSource().getDsId()));
					((SbiQueryDataSet)hibDataSet).setDataSource(hibDataSource);	
				}				
			}

			else if(aDataSet instanceof WebServiceDataSet){
				//hibDataSet=new SbiWSDataSet();
				if(((WebServiceDataSet)aDataSet).getAddress()!=null){
					((SbiWSDataSet)hibDataSet).setAdress(((WebServiceDataSet)aDataSet).getAddress());
				}
				if(((WebServiceDataSet)aDataSet).getOperation()!=null){
					((SbiWSDataSet)hibDataSet).setOperation(((WebServiceDataSet)aDataSet).getOperation());
				}	
			}

			else if(aDataSet instanceof ScriptDataSet){

				if(((ScriptDataSet)aDataSet).getScript()!=null){
					((SbiScriptDataSet)hibDataSet).setScript(((ScriptDataSet)aDataSet).getScript());
				}
				if(((ScriptDataSet)aDataSet).getLanguageScript()!=null){
					((SbiScriptDataSet)hibDataSet).setLanguageScript(((ScriptDataSet)aDataSet).getLanguageScript());
				}

			}

			else if(aDataSet instanceof JavaClassDataSet){

				if(((JavaClassDataSet)aDataSet).getClassName()!=null){
					((SbiJClassDataSet)hibDataSet).setJavaClassName(((JavaClassDataSet)aDataSet).getClassName());
				}
			}
			SbiDomains transformer = null;
			if (aDataSet.getTransformerId() != null){ 
				Criterion aCriterion = Expression.eq("valueId",	aDataSet.getTransformerId());
				Criteria criteria = aSession.createCriteria(SbiDomains.class);
				criteria.add(aCriterion);

				transformer = (SbiDomains) criteria.uniqueResult();

				if (transformer == null){
					logger.error("The Domain with value_id= "+aDataSet.getTransformerId()+" does not exist.");
					throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
				}
			}
			hibDataSet.setLabel(aDataSet.getLabel());
			hibDataSet.setTransformer(transformer);
			hibDataSet.setPivotColumnName(aDataSet.getPivotColumnName());
			hibDataSet.setPivotRowName(aDataSet.getPivotRowName());
			hibDataSet.setPivotColumnValue(aDataSet.getPivotColumnValue());
			hibDataSet.setNumRows(aDataSet.isNumRows());
			hibDataSet.setName(aDataSet.getName());			
			hibDataSet.setDescription(aDataSet.getDescription());
			hibDataSet.setParameters(aDataSet.getParameters());
			hibDataSet.setDsMetadata(aDataSet.getDsMetadata());



			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while modifing the data Set with id " + ((aDataSet == null)?"":String.valueOf(aDataSet.getId())), he);

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
	 * Insert data set.
	 * 
	 * @param aDataSet the a data set
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#insertDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void insertDataSet(IDataSet aDataSet) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDataSetConfig hibDataSet =null;


			if(aDataSet instanceof FileDataSet){
				hibDataSet=new SbiFileDataSet();
				if(((FileDataSet)aDataSet).getFileName()!=null){
					((SbiFileDataSet)hibDataSet).setFileName(((FileDataSet)aDataSet).getFileName());
				}
			}

			else if(aDataSet instanceof JDBCDataSet){
				hibDataSet=new SbiQueryDataSet();
				if(((JDBCDataSet)aDataSet).getQuery()!=null){
					((SbiQueryDataSet)hibDataSet).setQuery(((JDBCDataSet)aDataSet).getQuery().toString());
				}
				if(((JDBCDataSet)aDataSet).getDataSource()!=null){
					SbiDataSource hibDataSource = null;
					hibDataSource = (SbiDataSource) aSession.load(SbiDataSource.class, new Integer(((JDBCDataSet)aDataSet).getDataSource().getDsId()));
					((SbiQueryDataSet)hibDataSet).setDataSource(hibDataSource);	
				}				
			}

			else if(aDataSet instanceof WebServiceDataSet){
				hibDataSet=new SbiWSDataSet();
				if(((WebServiceDataSet)aDataSet).getAddress()!=null){
					((SbiWSDataSet)hibDataSet).setAdress(((WebServiceDataSet)aDataSet).getAddress());
				}
				if(((WebServiceDataSet)aDataSet).getOperation()!=null){
					((SbiWSDataSet)hibDataSet).setOperation(((WebServiceDataSet)aDataSet).getOperation());
				}	
			}

			else if(aDataSet instanceof JavaClassDataSet){
				hibDataSet=new SbiJClassDataSet();
				if(((JavaClassDataSet)aDataSet).getClassName()!=null){
					((SbiJClassDataSet)hibDataSet).setJavaClassName(((JavaClassDataSet)aDataSet).getClassName());
				}
			}

			else if(aDataSet instanceof ScriptDataSet){
				hibDataSet=new SbiScriptDataSet();
				if(((ScriptDataSet)aDataSet).getScript()!=null){
					((SbiScriptDataSet)hibDataSet).setScript(((ScriptDataSet)aDataSet).getScript());
				}
				if(((ScriptDataSet)aDataSet).getLanguageScript()!=null){
					((SbiScriptDataSet)hibDataSet).setLanguageScript(((ScriptDataSet)aDataSet).getLanguageScript());
				}
			}

			SbiDomains transformer = null;
			if (aDataSet.getTransformerId() != null){ 
				Criterion aCriterion = Expression.eq("valueId",	aDataSet.getTransformerId());
				Criteria criteria = aSession.createCriteria(SbiDomains.class);
				criteria.add(aCriterion);

				transformer = (SbiDomains) criteria.uniqueResult();

				if (transformer == null){
					logger.error("The Domain with value_id= "+aDataSet.getTransformerId()+" does not exist.");
					throw new EMFUserError(EMFErrorSeverity.ERROR, 1035);
				}
			}

			hibDataSet.setLabel(aDataSet.getLabel());
			hibDataSet.setTransformer(transformer);
			hibDataSet.setPivotColumnName(aDataSet.getPivotColumnName());
			hibDataSet.setPivotRowName(aDataSet.getPivotRowName());
			hibDataSet.setPivotColumnValue(aDataSet.getPivotColumnValue());
			hibDataSet.setNumRows(aDataSet.isNumRows());
			hibDataSet.setDescription(aDataSet.getDescription());
			hibDataSet.setName(aDataSet.getName());
			hibDataSet.setParameters(aDataSet.getParameters());
			hibDataSet.setDsMetadata(aDataSet.getDsMetadata());

			aSession.save(hibDataSet);
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while inserting the data Set with id " + ((aDataSet == null)?"":String.valueOf(aDataSet.getId())), he);

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
	 * Erase data set.
	 * 
	 * @param aDataSet the a data set
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#eraseDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void eraseDataSet(IDataSet aDataSet) throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDataSetConfig hibDataSet = (SbiDataSetConfig) aSession.load(SbiDataSetConfig.class,
					new Integer(aDataSet.getId()));

			aSession.delete(hibDataSet);
			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while erasing the data Set with id " + ((aDataSet == null)?"":String.valueOf(aDataSet.getId())), he);

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
	 * From the hibernate DataSet at input, gives
	 * the corrispondent <code>DataSet</code> object.
	 * 
	 * @param hibDataSet The hybernate data set
	 * 
	 * @return The corrispondent <code>DataSet</code> object
	 * @throws EMFUserError 
	 */
	public IDataSet toDataSet(SbiDataSetConfig hibDataSet) throws EMFUserError{
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

		ds.setId(hibDataSet.getDsId());
		ds.setName(hibDataSet.getName());
		ds.setLabel(hibDataSet.getLabel());
		ds.setTransformerId((hibDataSet.getTransformer()==null)?null:hibDataSet.getTransformer().getValueId());
		ds.setPivotColumnName(hibDataSet.getPivotColumnName());
		ds.setPivotRowName(hibDataSet.getPivotRowName());
		ds.setPivotColumnValue(hibDataSet.getPivotColumnValue());
		ds.setNumRows(hibDataSet.isNumRows());
		ds.setDescription(hibDataSet.getDescription());		
		ds.setParameters(hibDataSet.getParameters());		
		ds.setDsMetadata(hibDataSet.getDsMetadata());		

		if(ds.getPivotColumnName() != null 
				&& ds.getPivotColumnValue() != null
				&& ds.getPivotRowName() != null){
			ds.setDataStoreTransformer(
					new PivotDataSetTransformer(ds.getPivotColumnName(), ds.getPivotColumnValue(), ds.getPivotRowName(), ds.isNumRows()));
		}

		return ds;
	}

	/**
	 * Checks for bi obj associated.
	 * 
	 * @param dsId the ds id
	 * 
	 * @return true, if checks for bi obj associated
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
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

			//String hql = " from SbiObjects s where s.dataSet.dsId = "+ dsIdInt;
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

	public Integer countDatasets() throws EMFUserError {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		Integer resultNumber;
		
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
		
			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Integer)hqlQuery.uniqueResult();

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
		return resultNumber;
	}

	public List loadPagedDatasetList(Integer offset, Integer fetchSize)
			throws EMFUserError {
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
		
			String hql = "select count(*) from SbiDataSetConfig ";
			Query hqlQuery = aSession.createQuery(hql);
			resultNumber = (Integer)hqlQuery.uniqueResult();
			
			offset = offset < 0 ? 0 : offset;
			if(resultNumber > 0) {
				fetchSize = (fetchSize > 0)? Math.min(fetchSize, resultNumber): resultNumber;
			}
			
			hibernateQuery = aSession.createQuery("from SbiDataSetConfig order by label");
			hibernateQuery.setFirstResult(offset);
			if(fetchSize > 0) hibernateQuery.setMaxResults(fetchSize);			

			toTransform = hibernateQuery.list();			
			Iterator it = toTransform.iterator();

			while (it.hasNext()) {
				SbiDataSetConfig temp = (SbiDataSetConfig) it.next();
				toReturn.add(temp);
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
}



