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
/*
 * Created on 20-giu-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.engines.config.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.bo.Exporters;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.engines.config.metadata.SbiExporters;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Defines the Hibernate implementations for all DAO methods,
 * for an engine.
 * 
 * @author zoppello
 */
public class EngineDAOHibImpl extends AbstractHibernateDAO implements IEngineDAO{

	/**
	 * Load engine by id.
	 * 
	 * @param engineID the engine id
	 * 
	 * @return the engine
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#loadEngineByID(java.lang.Integer)
	 */
	public Engine loadEngineByID(Integer engineID) throws EMFUserError {
		Engine toReturn = null;
		Session aSession = null;
		Transaction tx = null;

		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiEngines hibEngine = (SbiEngines)aSession.load(SbiEngines.class,  engineID);
			toReturn = toEngine(hibEngine);
			tx.commit();

		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}

		return toReturn;
	}



	/**
	 * Load engine by label.
	 * 
	 * @param engineLabel the engine label
	 * 
	 * @return the engine
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#loadEngineByID(java.lang.Integer)
	 */


	public Engine loadEngineByLabel(String engineLabel) throws EMFUserError {
		Engine engine = null;
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("label",
					engineLabel);
			Criteria criteria = aSession.createCriteria(SbiEngines.class);
			criteria.add(labelCriterrion);
			SbiEngines hibEngine = (SbiEngines) criteria.uniqueResult();
			if (hibEngine == null) return null;
			engine = toEngine(hibEngine);
			tx.commit();
		} catch (HibernateException he) {
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		return engine;
	}


	/**
	 * Load all engines.
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#loadAllEngines()
	 */
	public List loadAllEngines() throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		List realResult = new ArrayList();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiEngines");

			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				realResult.add(toEngine((SbiEngines) it.next()));
			}
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		return realResult;
	}

	/**
	 * Load all engines for bi object type.
	 * 
	 * @param biobjectType the biobject type
	 * 
	 * @return the list
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#loadAllEnginesForBIObjectType(java.lang.String)
	 */
	public List<Engine> loadAllEnginesForBIObjectType(String biobjectType) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		List<Engine> realResult = new ArrayList<Engine>();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			Query hibQuery = aSession.createQuery(" from SbiEngines engines where engines.biobjType.valueCd = ?" );
			hibQuery.setString(0, biobjectType);
			List hibList = hibQuery.list();
			Iterator it = hibList.iterator();

			while (it.hasNext()) {
				realResult.add(toEngine((SbiEngines) it.next()));
			}
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		return realResult;
	}

	/**
	 * Modify engine.
	 * 
	 * @param aEngine the a engine
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#modifyEngine(it.eng.spagobi.engines.config.bo.Engine)
	 */
	public void modifyEngine(Engine aEngine) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			SbiEngines hibEngine = (SbiEngines) aSession.load(SbiEngines.class,
					aEngine.getId());
			SbiDomains hibDomainBiobjType = (SbiDomains) aSession.load(SbiDomains.class,
					aEngine.getBiobjTypeId());
			SbiDomains hibDomainEngineType = (SbiDomains) aSession.load(SbiDomains.class,
					aEngine.getEngineTypeId());
			SbiDataSource hibDataSource = null;
			if (aEngine.getDataSourceId() != null) {
				hibDataSource = (SbiDataSource) aSession.load(SbiDataSource.class, aEngine.getDataSourceId());
			}
			hibEngine.setName(aEngine.getName());
			hibEngine.setLabel(aEngine.getLabel());
			hibEngine.setDescr(aEngine.getDescription());
			hibEngine.setDriverNm(aEngine.getDriverName());
			hibEngine.setEncrypt(new Short((short) aEngine.getCriptable()
					.intValue()));
			hibEngine.setMainUrl(aEngine.getUrl());
			hibEngine.setObjUplDir(aEngine.getDirUpload());
			hibEngine.setObjUseDir(aEngine.getDirUsable());
			hibEngine.setSecnUrl(aEngine.getSecondaryUrl());
			hibEngine.setEngineType(hibDomainEngineType);
			hibEngine.setClassNm(aEngine.getClassName());
			hibEngine.setBiobjType(hibDomainBiobjType);
			hibEngine.setUseDataSet(new Boolean(aEngine.getUseDataSet()));			
			hibEngine.setUseDataSource(new Boolean(aEngine.getUseDataSource()));						
			hibEngine.setDataSource(hibDataSource);
			updateSbiCommonInfo4Update(hibEngine);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}

	}

	/**
	 * Insert engine.
	 * 
	 * @param aEngine the a engine
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#insertEngine(it.eng.spagobi.engines.config.bo.Engine)
	 */
	public void insertEngine(Engine aEngine) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiDomains hibDomainBiobjType = (SbiDomains) aSession.load(SbiDomains.class,
					aEngine.getBiobjTypeId());
			SbiDomains hibDomainEngineType = (SbiDomains) aSession.load(SbiDomains.class,
					aEngine.getEngineTypeId());
			SbiDataSource hibDataSource = null;
			if (aEngine.getDataSourceId() != null) {
				hibDataSource = (SbiDataSource) aSession.load(SbiDataSource.class, aEngine.getDataSourceId());
			}
			SbiEngines hibEngine = new SbiEngines();
			hibEngine.setName(aEngine.getName());
			hibEngine.setLabel(aEngine.getLabel());
			hibEngine.setDescr(aEngine.getDescription());
			hibEngine.setDriverNm(aEngine.getDriverName());
			hibEngine.setEncrypt(new Short((short) aEngine.getCriptable()
					.intValue()));
			hibEngine.setMainUrl(aEngine.getUrl());
			hibEngine.setObjUplDir(aEngine.getDirUpload());
			hibEngine.setObjUseDir(aEngine.getDirUsable());
			hibEngine.setSecnUrl(aEngine.getSecondaryUrl());
			hibEngine.setEngineType(hibDomainEngineType);
			hibEngine.setClassNm(aEngine.getClassName());
			hibEngine.setBiobjType(hibDomainBiobjType);
			hibEngine.setDataSource(hibDataSource);
			hibEngine.setUseDataSet(new Boolean(aEngine.getUseDataSet()));
			hibEngine.setUseDataSource(new Boolean(aEngine.getUseDataSource()));
			hibEngine.setDataSource(hibDataSource);	
			updateSbiCommonInfo4Insert(hibEngine);
			aSession.save(hibEngine);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
	}

	/**
	 * Erase engine.
	 * 
	 * @param aEngine the a engine
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#eraseEngine(it.eng.spagobi.engines.config.bo.Engine)
	 */
	public void eraseEngine(Engine aEngine) throws EMFUserError {
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			SbiEngines hibEngine = (SbiEngines) aSession.load(SbiEngines.class,
					aEngine.getId());
			Set <SbiExporters> exporters = hibEngine.getSbiExporterses();
			if(exporters != null && exporters.size() != 0){
				//query hsql to load exporters
				String hql = " from SbiExporters s where s.sbiEngines.engineId = ?";
				Query aQuery = aSession.createQuery(hql);
				aQuery.setInteger(0, aEngine.getId().intValue());
				List associatedExportersToDelete = aQuery.list();
				for(int i=0; i<associatedExportersToDelete.size(); i++){
					SbiExporters exporter = (SbiExporters)associatedExportersToDelete.get(i);
					aSession.delete(exporter);
					aSession.flush();
				}
			}
			aSession.delete(hibEngine);
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}

	}

	/**
	 * From the hibernate Engine at input, gives
	 * the corrispondent <code>Engine</code> object.
	 * 
	 * @param hibEngine The hybernate engine
	 * 
	 * @return The corrispondent <code>Engine</code> object
	 */
	public Engine toEngine(SbiEngines hibEngine){
		Engine eng = new Engine();
		eng.setCriptable(new Integer(hibEngine.getEncrypt().intValue()));
		eng.setDescription(hibEngine.getDescr());
		eng.setDirUpload(hibEngine.getObjUplDir());
		eng.setDirUsable(hibEngine.getObjUseDir());
		eng.setDriverName(hibEngine.getDriverNm());
		eng.setId(hibEngine.getEngineId());
		eng.setName(hibEngine.getName());
		eng.setLabel(hibEngine.getLabel());
		eng.setUseDataSet(hibEngine.getUseDataSet().booleanValue());
		eng.setUseDataSource(hibEngine.getUseDataSource().booleanValue());		
		eng.setSecondaryUrl(hibEngine.getSecnUrl());
		eng.setUrl(hibEngine.getMainUrl());
		eng.setLabel(hibEngine.getLabel());
		eng.setEngineTypeId(hibEngine.getEngineType().getValueId());
		eng.setClassName(hibEngine.getClassNm());
		eng.setBiobjTypeId(hibEngine.getBiobjType().getValueId());
		eng.setDataSourceId(hibEngine.getDataSource() == null ? null : new Integer(hibEngine.getDataSource().getDsId()));
		return eng;
	}





	/**
	 * From the hibernate Exporter at input, gives
	 * the corrispondent <code>Engine</code> object.
	 * 
	 * @param hibEngine The hybernate engine
	 * 
	 * @return The corrispondent <code>Engine</code> object
	 */
	public Exporters toExporter(SbiExporters hibExps){
		Exporters exp = new Exporters();

		SbiEngines hibEngine=hibExps.getSbiEngines();
		exp.setEngineId(hibEngine.getEngineId());

		SbiDomains hibDomains=hibExps.getSbiDomains();
		exp.setDomainId(hibDomains.getValueId());

		exp.setDefaultValue(hibExps.isDefaultValue());

		return exp;
	}




	/**
	 * Checks for bi obj associated.
	 * 
	 * @param engineId the engine id
	 * 
	 * @return true, if checks for bi obj associated
	 * 
	 * @throws EMFUserError the EMF user error
	 * 
	 * @see it.eng.spagobi.engines.config.dao.IEngineDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBIObjAssociated (String engineId) throws EMFUserError{
		/**
		 * TODO Hibernate Implementation
		 */
		boolean bool = false; 


		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Integer engineIdInt = Integer.valueOf(engineId);

			//String hql = " from SbiObjects s where s.sbiEngines.engineId = "+ engineIdInt;
			String hql = " from SbiObjects s where s.sbiEngines.engineId = ?";
			Query aQuery = aSession.createQuery(hql);
			aQuery.setInteger(0, engineIdInt.intValue());
			List biObjectsAssocitedWithEngine = aQuery.list();
			if (biObjectsAssocitedWithEngine.size() > 0)
				bool = true;
			else
				bool = false;
			tx.commit();
		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		return bool;
	}

	public List getAssociatedExporters(Engine engine) throws EMFUserError {
		Session aSession = null;
		List<Exporters> toReturn=new ArrayList<Exporters>();
		Transaction tx = null;
		Integer engineId=engine.getId();
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();

			//String hql = " from SbiObjects s where s.sbiEngines.engineId = "+ engineIdInt;
			String hql = " from SbiExporters s where s.sbiEngines.engineId = ?";
			Query aQuery = aSession.createQuery(hql);
			aQuery.setInteger(0, engineId.intValue());
			List exportersOfEngine = aQuery.list();
			if(exportersOfEngine!=null){
				for (Iterator iterator = exportersOfEngine.iterator(); iterator.hasNext();) {
					SbiExporters object = (SbiExporters) iterator.next();
					Exporters exp=toExporter(object);
					toReturn.add(exp);
				}
			}
			tx.commit();

			return toReturn;

		} catch (HibernateException he) {
			logException(he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}

	}



}
