/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiOrganizationDatasource;
import it.eng.spagobi.commons.metadata.SbiOrganizationDatasourceId;
import it.eng.spagobi.commons.metadata.SbiOrganizationEngine;
import it.eng.spagobi.commons.metadata.SbiOrganizationEngineId;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.Date;
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
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
@SuppressWarnings("all")
public class TenantsDAOHibImpl extends AbstractHibernateDAO implements ITenantsDAO {

    static private Logger logger = Logger.getLogger(TenantsDAOHibImpl.class);
    
	public SbiTenant loadTenantByName(String name) throws EMFUserError {
		logger.debug("IN");
		SbiTenant tenant = null;
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("name", name);
			Criteria criteria = tmpSession.createCriteria(SbiTenant.class);
			criteria.add(labelCriterrion);	
			tenant = (SbiTenant) criteria.uniqueResult();

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading the tenant with name " + name, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
		}
		logger.debug("OUT");
		return tenant;		
	}
	
	public SbiTenant loadTenantById(Integer id) throws EMFUserError {
		logger.debug("IN");
		SbiTenant tenant = null;
		Session tmpSession = null;
		Transaction tx = null;
		try {
			tmpSession = getSession();
			tx = tmpSession.beginTransaction();
			Criterion labelCriterrion = Expression.eq("id", id);
			Criteria criteria = tmpSession.createCriteria(SbiTenant.class);
			criteria.add(labelCriterrion);	
			tenant = (SbiTenant) criteria.uniqueResult();

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while loading the tenant with id " + id, he);
			if (tx != null)
				tx.rollback();
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (tmpSession!=null){
				if (tmpSession.isOpen()) tmpSession.close();
			}
		}
		logger.debug("OUT");
		return tenant;		
	}
	
	public List<SbiTenant> loadAllTenants() {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			String q = "from SbiTenant";
			Query query = aSession.createQuery(q);
			ArrayList<SbiTenant> result = (ArrayList<SbiTenant>) query.list();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new SpagoBIRuntimeException("Error getting tenants", he);
		} finally {
			logger.debug("OUT");
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}

	public List<SbiOrganizationEngine> loadSelectedEngines(String tenant) throws EMFUserError {
		
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Query hibQuery = aSession.createQuery("from SbiOrganizationEngine en where en.sbiOrganizations.name = :tenantName");
			hibQuery.setString("tenantName", tenant);
			ArrayList<SbiOrganizationEngine> result = (ArrayList<SbiOrganizationEngine>) hibQuery.list();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new SpagoBIRuntimeException("Error getting Tenant Engines", he);
		} finally {
			logger.debug("OUT");
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}
	
	public List<SbiOrganizationDatasource> loadSelectedDS(String tenant) throws EMFUserError {
		
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			Query hibQuery = aSession.createQuery("from SbiOrganizationDatasource ds where ds.sbiOrganizations.name = :tenantName");
			hibQuery.setString("tenantName", tenant);
			ArrayList<SbiOrganizationDatasource> result = (ArrayList<SbiOrganizationDatasource>) hibQuery.list();
			return result;
		} catch (HibernateException he) {
			logger.error(he.getMessage(), he);
			if (tx != null)
				tx.rollback();
			throw new SpagoBIRuntimeException("Error getting Tenant Data Sources", he);
		} finally {
			logger.debug("OUT");
			if (aSession != null) {
				if (aSession.isOpen())
					aSession.close();
			}
		}
	}

	public void insertTenant(SbiTenant aTenant) throws EMFUserError {
		logger.debug("insertTenant IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			updateSbiCommonInfo4Insert(aTenant);	
			Integer idTenant = (Integer)aSession.save(aTenant);			
			aSession.flush();
			
			Set<SbiOrganizationDatasource> ds = aTenant.getSbiOrganizationDatasources();
			for (SbiOrganizationDatasource sbiOrganizationDatasource: ds) {
				SbiDataSource sbiDs = sbiOrganizationDatasource.getSbiDataSource();
				sbiOrganizationDatasource.setId(new SbiOrganizationDatasourceId(sbiDs.getDsId(), idTenant));
				sbiOrganizationDatasource.setCreationDate(new Date());
				sbiOrganizationDatasource.setLastChangeDate(new Date());
				updateSbiCommonInfo4Insert(sbiOrganizationDatasource);
				aSession.save(sbiOrganizationDatasource);
				aSession.flush();
			}
			
			Set<SbiOrganizationEngine> engines = aTenant.getSbiOrganizationEngines();
			for (SbiOrganizationEngine sbiOrganizationEngine: engines) {
				SbiEngines sbiEngine = sbiOrganizationEngine.getSbiEngines();
				sbiOrganizationEngine.setId(new SbiOrganizationEngineId(sbiEngine.getEngineId(), idTenant));
				sbiOrganizationEngine.setCreationDate(new Date());
				sbiOrganizationEngine.setLastChangeDate(new Date());
				updateSbiCommonInfo4Insert(sbiOrganizationEngine);
				aSession.save(sbiOrganizationEngine);
				aSession.flush();
			}

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while inserting the tenant with id " + ((aTenant == null)?"":String.valueOf(aTenant.getId())), he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("insertTenant OUT");
			}
		}
	}

	public void modifyTenant(SbiTenant aTenant) throws EMFUserError {
		logger.debug("modifyTenant IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			updateSbiCommonInfo4Update(aTenant);	
			aSession.update(aTenant);			
			aSession.flush();
			
			// cancello tutti data source associati al tenant
			Query hibQuery = aSession.createQuery("delete from SbiOrganizationDatasource ds where ds.sbiOrganizations.id = :idTenant");
			hibQuery.setInteger("idTenant", aTenant.getId());
			hibQuery.executeUpdate();
			aSession.flush();
			
			// associo i datasource al tenant
			Set<SbiOrganizationDatasource> ds = aTenant.getSbiOrganizationDatasources();
			for (SbiOrganizationDatasource sbiOrganizationDatasource: ds) {
				SbiDataSource sbiDs = sbiOrganizationDatasource.getSbiDataSource();
				sbiOrganizationDatasource.setId(new SbiOrganizationDatasourceId(sbiDs.getDsId(), aTenant.getId()));
				sbiOrganizationDatasource.setCreationDate(new Date());
				sbiOrganizationDatasource.setLastChangeDate(new Date());
				updateSbiCommonInfo4Insert(sbiOrganizationDatasource);
				aSession.save(sbiOrganizationDatasource);
				aSession.flush();
			}
			
			// cancello tutte le Engine associate al tenant
			hibQuery = aSession.createQuery("delete from SbiOrganizationEngine en where en.sbiOrganizations.id = :idTenant");
			hibQuery.setInteger("idTenant", aTenant.getId());
			hibQuery.executeUpdate();
			aSession.flush();

			Set<SbiOrganizationEngine> engines = aTenant.getSbiOrganizationEngines();
			for (SbiOrganizationEngine sbiOrganizationEngine: engines) {
				SbiEngines sbiEngine = sbiOrganizationEngine.getSbiEngines();
				sbiOrganizationEngine.setId(new SbiOrganizationEngineId(sbiEngine.getEngineId(), aTenant.getId()));
				sbiOrganizationEngine.setCreationDate(new Date());
				sbiOrganizationEngine.setLastChangeDate(new Date());
				updateSbiCommonInfo4Insert(sbiOrganizationEngine);
				aSession.save(sbiOrganizationEngine);
				aSession.flush();
			}

			tx.commit();
		} catch (HibernateException he) {
			logger.error("Error while inserting the tenant with id " + ((aTenant == null)?"":String.valueOf(aTenant.getId())), he);

			if (tx != null)
				tx.rollback();

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("modifyTenant OUT");
			}
		}
	}
	

}