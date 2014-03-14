/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.metadata.SbiCommonInfo;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.metadata.SbiOrganizationDatasource;
import it.eng.spagobi.commons.metadata.SbiOrganizationDatasourceId;
import it.eng.spagobi.commons.metadata.SbiOrganizationEngine;
import it.eng.spagobi.commons.metadata.SbiOrganizationEngineId;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.alarm.service.AlarmInspectorJob;
import it.eng.spagobi.profiling.bean.SbiExtUserRoles;
import it.eng.spagobi.profiling.bean.SbiExtUserRolesId;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.security.Password;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.tools.scheduler.bo.CronExpression;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.safehaus.uuid.UUIDGenerator;

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
			
			this.disableTenantFilter(aSession);
			
			updateSbiCommonInfo4Insert(aTenant);	
			Integer idTenant = (Integer)aSession.save(aTenant);			
			aSession.flush();
			
			aTenant.setId(idTenant);
			
			createUser(aTenant, aSession);
			
			SbiCommonInfo sbiCommoInfo = new SbiCommonInfo();
			sbiCommoInfo.setOrganization(aTenant.getName());
			
			Set<SbiOrganizationDatasource> ds = aTenant.getSbiOrganizationDatasources();
			for (SbiOrganizationDatasource sbiOrganizationDatasource: ds) {
				SbiDataSource sbiDs = sbiOrganizationDatasource.getSbiDataSource();
				sbiOrganizationDatasource.setId(new SbiOrganizationDatasourceId(sbiDs.getDsId(), idTenant));
				sbiOrganizationDatasource.setCommonInfo(sbiCommoInfo);
				updateSbiCommonInfo4Insert(sbiOrganizationDatasource);
				aSession.save(sbiOrganizationDatasource);
				aSession.flush();
			}
			
			Set<SbiOrganizationEngine> engines = aTenant.getSbiOrganizationEngines();
			for (SbiOrganizationEngine sbiOrganizationEngine: engines) {
				SbiEngines sbiEngine = sbiOrganizationEngine.getSbiEngines();
				sbiOrganizationEngine.setId(new SbiOrganizationEngineId(sbiEngine.getEngineId(), idTenant));
				sbiOrganizationEngine.setCommonInfo(sbiCommoInfo);
				updateSbiCommonInfo4Insert(sbiOrganizationEngine);
				aSession.save(sbiOrganizationEngine);
				aSession.flush();
			}
			tx.commit();
			
			initAlarmForTenant(aTenant);
			
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
	
	/*
	 * Method copied from it.eng.spagobi.tools.scheduler.init.AlarmQuartzInitializer for removing dependecy
	 */
	public void initAlarmForTenant(SbiTenant tenant) {
		try {
			logger.debug("IN");
			boolean alreadyInDB = false;
			ISchedulerDAO schedulerDAO = DAOFactory.getSchedulerDAO();
			schedulerDAO.setTenant(tenant.getName());
			alreadyInDB = schedulerDAO.jobExists("AlarmInspectorJob", "AlarmInspectorJob");
			if (!alreadyInDB) {

				// CREATE JOB DETAIL
				Job jobDetail = new Job();
				jobDetail.setName("AlarmInspectorJob");
				jobDetail.setGroupName("AlarmInspectorJob");
				jobDetail.setDescription("AlarmInspectorJob");
				jobDetail.setDurable(true);
				jobDetail.setVolatile(false);
				jobDetail.setRequestsRecovery(true);
				jobDetail.setJobClass(AlarmInspectorJob.class);

				schedulerDAO.insertJob(jobDetail);

				Calendar startDate = new java.util.GregorianCalendar(2012,
						Calendar.JANUARY, 01);
				startDate.set(Calendar.AM_PM, Calendar.AM);
				startDate.set(Calendar.HOUR, 00);
				startDate.set(Calendar.MINUTE, 00);
				startDate.set(Calendar.SECOND, 0);
				startDate.set(Calendar.MILLISECOND, 0);

				String nameTrig = "schedule_uuid_"
						+ UUIDGenerator.getInstance().generateTimeBasedUUID()
								.toString();

				CronExpression cronExpression = new CronExpression("minute{numRepetition=5}");
				
				Trigger simpleTrigger = new Trigger();
				simpleTrigger.setName(nameTrig);
				simpleTrigger.setStartTime(startDate.getTime());
				simpleTrigger.setJob(jobDetail);
				simpleTrigger.setCronExpression(cronExpression);
				simpleTrigger.setRunImmediately(false);

				schedulerDAO.insertTrigger(simpleTrigger);

				logger.debug("Added job with name AlarmInspectorJob");
			}
			logger.debug("OUT");
		} catch (Exception e) {
			logger.error("Error while initializing scheduler ", e);
		}
	}	
	
	private void createUser(SbiTenant aTenant, Session aSession) throws HibernateException {
		
		logger.debug("createUser IN");
			
		try {
				
			ISbiUserDAO userDAO = DAOFactory.getSbiUserDAO();
			String userId = aTenant.getName().toLowerCase()+"_admin";
			
			SbiUser existingUser = userDAO.loadSbiUserByUserId(userId);

		    if (existingUser == null) {
		    	
		    	createRole(aTenant.getName(), aSession);
			    logger.debug("Storing user [" + userId + "] into database ");
			    SbiUser tenantAdmin = new SbiUser();
			    tenantAdmin.setUserId(userId);
			    tenantAdmin.setFullName(aTenant.getName()+" ADMIN");
			    String pwd = Password.encriptPassword(userId);
			    tenantAdmin.setPassword(pwd);
			    tenantAdmin.setIsSuperadmin(false);
			    tenantAdmin.getCommonInfo().setOrganization(aTenant.getName());
			    Integer newId = userDAO.saveSbiUser(tenantAdmin);
			    setRole(aTenant.getName(), newId, aSession);
			    logger.debug("User [" + userId + "] sucesfully stored into database with id [" + newId + "]");
			}			  
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while creating user", t);
			throw new HibernateException(t);
		} finally {
			logger.debug("createUser OUT");
		}
	}
	
	private void createRole(String tenant, Session aSession) throws HibernateException{
		
		Role aRole = new Role();
		try {
			RoleDAOHibImpl roleDAO = new RoleDAOHibImpl();
			String roleDes = "/"+tenant.toLowerCase()+"/admin";
			aRole.setName(roleDes);
			aRole.setDescription(roleDes);
			aRole.setOrganization(tenant);
			Domain domain = DAOFactory.getDomainDAO().loadDomainByCodeAndValue("ROLE_TYPE", "ADMIN");	
			aRole.setRoleTypeCD("ADMIN");
			aRole.setRoleTypeID(domain.getValueId());
			roleDAO.insertRoleWithSession(aRole, aSession);
		} catch(Throwable t) {
			logger.error("An unexpected error occurred while setting user role", t);
			throw new HibernateException(t);
		} finally {
			logger.debug("setRole OUT");
		}
	}
	
	private void setRole(String tenant, int userIdInt, Session aSession) throws HibernateException {
		SbiExtUserRoles sbiExtUserRole = new SbiExtUserRoles();
		SbiExtUserRolesId id = new SbiExtUserRolesId();

		try {
			RoleDAOHibImpl roleDAO = new RoleDAOHibImpl();	
			SbiExtRoles role = roleDAO.loadByNameInSession("/"+tenant.toLowerCase()+"/admin", aSession);
			id.setExtRoleId(role.getExtRoleId());//role Id
			id.setId(userIdInt);//user ID
			sbiExtUserRole.setId(id);
			SbiCommonInfo commonInfo = new SbiCommonInfo();
			commonInfo.setOrganization(tenant);
			sbiExtUserRole.setCommonInfo(commonInfo);
			aSession.saveOrUpdate(sbiExtUserRole);

		} catch(Throwable t) {
			logger.error("An unexpected error occurred while setting user role", t);
			throw new HibernateException(t);
		} finally {
			logger.debug("setRole OUT");
		}
	}

	public void modifyTenant(SbiTenant aTenant) throws EMFUserError, Exception {
		logger.debug("modifyTenant IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = getSession();
			tx = aSession.beginTransaction();
			
			// carica il tenant tramite ID
			// verifica che il nome sia uguale altrimenti eccezione
			SbiTenant tenant = loadTenantById(aTenant.getId());
			if(!tenant.getName().equalsIgnoreCase(aTenant.getName()))
				new SpagoBIRuntimeException("It's not allowed to modify the name of an existing Tenant.");
			
			updateSbiCommonInfo4Update(aTenant);	
			aSession.update(aTenant);			
			aSession.flush();
			
			this.disableTenantFilter(aSession);
			
			// cancello tutti data source associati al tenant

			Query hibQuery = aSession.createQuery("from SbiOrganizationDatasource ds where ds.sbiOrganizations.id = :idTenant");
			hibQuery.setInteger("idTenant", aTenant.getId());
			ArrayList<SbiOrganizationDatasource> existingDsAssociated  = (ArrayList<SbiOrganizationDatasource>) hibQuery.list();
			if(existingDsAssociated != null){
				Iterator it = existingDsAssociated.iterator();
				while (it.hasNext()) {
					SbiOrganizationDatasource assDS = (SbiOrganizationDatasource)it.next();
					Query docsQ = aSession.createQuery("from SbiObjects o where o.dataSource.dsId = :idDS and o.commonInfo.organization = :tenant");
					docsQ.setInteger("idDS", assDS.getSbiDataSource().getDsId());
					docsQ.setString("tenant", aTenant.getName());
					ArrayList<Object> docs = (ArrayList<Object>) docsQ.list();
					if(docs != null && !docs.isEmpty()){
						tx.rollback();	
						throw new Exception("datasource:"+assDS.getSbiDataSource().getLabel());
						
					}else{
						
						//check no model associated
						Query modelQ = aSession.createQuery("from SbiMetaModel m where m.dataSource.dsId = :idDS and m.commonInfo.organization = :tenant");
						modelQ.setInteger("idDS", assDS.getSbiDataSource().getDsId());
						modelQ.setString("tenant", aTenant.getName());
						ArrayList<Object> models = (ArrayList<Object>) modelQ.list();
						if(models != null && !models.isEmpty()){
							tx.rollback();	
							throw new Exception("datasource:"+assDS.getSbiDataSource().getLabel());
							
						}else{				
						
							aSession.delete(assDS);
							aSession.flush();
						}
					}
				} 
			}
			
			SbiCommonInfo sbiCommoInfo = new SbiCommonInfo();
			sbiCommoInfo.setOrganization(aTenant.getName());
			
			// associo i datasource al tenant
			Set<SbiOrganizationDatasource> ds = aTenant.getSbiOrganizationDatasources();
			for (SbiOrganizationDatasource sbiOrganizationDatasource: ds) {
				SbiDataSource sbiDs = sbiOrganizationDatasource.getSbiDataSource();
				sbiOrganizationDatasource.setId(new SbiOrganizationDatasourceId(sbiDs.getDsId(), aTenant.getId()));
				sbiOrganizationDatasource.setCommonInfo(sbiCommoInfo);
				updateSbiCommonInfo4Insert(sbiOrganizationDatasource);
				aSession.save(sbiOrganizationDatasource);
				aSession.flush();
			}
			
			// cancello tutte le Engine associate al tenant
			hibQuery = aSession.createQuery("from SbiOrganizationEngine en where en.sbiOrganizations.id = :idTenant");
			hibQuery.setInteger("idTenant", aTenant.getId());
			ArrayList<SbiOrganizationEngine> existingEnginesAssociated  = (ArrayList<SbiOrganizationEngine>) hibQuery.list();
			if(existingEnginesAssociated != null){
				Iterator it = existingEnginesAssociated.iterator();
				while (it.hasNext()) {
					SbiOrganizationEngine assEng = (SbiOrganizationEngine)it.next();
					Query docsQ = aSession.createQuery("from SbiObjects o where o.sbiEngines.engineId = :idEngine and o.commonInfo.organization = :tenant");
					docsQ.setInteger("idEngine", assEng.getSbiEngines().getEngineId());
					docsQ.setString("tenant", aTenant.getName());
					ArrayList<Object> docs = (ArrayList<Object>) docsQ.list();
					if(docs != null && !docs.isEmpty()){
						tx.rollback();	
						throw new Exception("engine:"+assEng.getSbiEngines().getName());
						
					}else{
						aSession.delete(assEng);
						aSession.flush();
					}
				} 
			}


			Set<SbiOrganizationEngine> engines = aTenant.getSbiOrganizationEngines();
			for (SbiOrganizationEngine sbiOrganizationEngine: engines) {
				SbiEngines sbiEngine = sbiOrganizationEngine.getSbiEngines();
				sbiOrganizationEngine.setId(new SbiOrganizationEngineId(sbiEngine.getEngineId(), aTenant.getId()));
				sbiOrganizationEngine.setCommonInfo(sbiCommoInfo);
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
	
	public void deleteTenant(SbiTenant aTenant)throws EMFUserError {
				
		logger.debug("deleteTenant IN");
		Session aSession = null;
		Connection jdbcConnection = null;
		InputStream is = null;
		try {
			aSession = getSession();			
			jdbcConnection = HibernateUtil.getConnection(aSession);
			jdbcConnection.setAutoCommit(false);
			Thread curThread = Thread.currentThread();
			ClassLoader classLoad = curThread.getContextClassLoader();
			is = classLoad.getResourceAsStream("it/eng/spagobi/commons/dao/deleteTenant.sql");
			String str = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is!=null) {                         
				while ((str = reader.readLine()) != null) { 
					if(!str.trim().startsWith("--")){
						PreparedStatement statement = jdbcConnection.prepareStatement(str);
						//System.out.println("\n"+str+"\n");
						statement.setString(1, aTenant.getName());
						statement.execute();		
						statement.close();
					}
				}               
			}
			StringEscapeUtils seu = new StringEscapeUtils();
			
			str = "DELETE FROM QRTZ_CRON_TRIGGERS WHERE trigger_name IN (SELECT DISTINCT t.trigger_name " +
				  "FROM QRTZ_TRIGGERS t WHERE t.JOB_GROUP LIKE '"+seu.escapeSql(aTenant.getName())+"/%') " +
			      "AND trigger_group IN (SELECT DISTINCT t.trigger_group FROM QRTZ_TRIGGERS t WHERE t.JOB_GROUP " +
			      "LIKE '"+seu.escapeSql(aTenant.getName())+"/%')";
			PreparedStatement statement = jdbcConnection.prepareStatement(str);
			statement.execute();		
			statement.close();
					
			str = "DELETE FROM QRTZ_TRIGGERS WHERE JOB_GROUP LIKE '"+seu.escapeSql(aTenant.getName())+"/%'";
			statement = jdbcConnection.prepareStatement(str);
			statement.execute();		
			statement.close();
			
			str = "DELETE FROM QRTZ_JOB_DETAILS WHERE JOB_GROUP LIKE '"+seu.escapeSql(aTenant.getName())+"/%'";
			statement = jdbcConnection.prepareStatement(str);
			statement.execute();		
			statement.close();

			jdbcConnection.commit();
			
		} catch (Exception e) {
			logger.error("Error while deleting the tenant with id " + ((aTenant == null)?"":String.valueOf(aTenant.getId())), e);

			if (jdbcConnection != null) {
				try{
					jdbcConnection.rollback();
				} catch (SQLException ex) {
					logger.error("Error while deleting the tenant with id " + ((aTenant == null)?"":String.valueOf(aTenant.getId())), ex);
				}
			}

			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);

		} finally {		
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);
				}
			try{
				if (jdbcConnection != null && !jdbcConnection.isClosed()) {
					jdbcConnection.close();
				}
			} catch (SQLException ex) {
				logger.error("Error while deleting the tenant with id " + ((aTenant == null)?"":String.valueOf(aTenant.getId())), ex);
			}
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
				logger.debug("deleteTenant OUT");
			}
		}
	}

}