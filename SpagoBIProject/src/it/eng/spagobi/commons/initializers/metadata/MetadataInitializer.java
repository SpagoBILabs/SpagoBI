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
package it.eng.spagobi.commons.initializers.metadata;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.behaviouralmodel.check.metadata.SbiChecks;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.metadata.SbiConfig;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiUserFunctionality;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.engines.config.metadata.SbiExporters;
import it.eng.spagobi.engines.config.metadata.SbiExportersId;
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xml.sax.InputSource;

/**
 * @author Zerbetto (davide.zerbetto@eng.it)
 * 
 * This class initializes SpagoBI metadata repository, if it is empty, with predefined domains, checks, lovs, engines, user functionalities...
 **/
public class MetadataInitializer extends AbstractHibernateDAO implements InitializerIFace {

	static private Logger logger = Logger.getLogger(MetadataInitializer.class);

	public SourceBean getConfig() {
		return null;
	}

	public void init(SourceBean config) {
		logger.debug("IN");
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = this.getSession();
			tx = aSession.beginTransaction();
			String hql = "from SbiDomains";
			Query hqlQuery = aSession.createQuery(hql);
			List domains = hqlQuery.list();
			if (domains.isEmpty()) {
				logger.info("Domains table is empty. Starting populating domains...");
				writeDomains(aSession);
			} else {
				logger.debug("Domains table is already populated, only missing domains will be populated");
				writeMissingDomains(aSession);				
			}

			hql = "from SbiEngines";
			hqlQuery = aSession.createQuery(hql);
			List engines = hqlQuery.list();
			if (engines.isEmpty()) {
				logger.info("Engines table is empty. Starting populating predefined engines...");
				writeEngines(aSession);
			} else {
				logger.debug("Engines table is already populated");
			}

			hql = "from SbiChecks";
			hqlQuery = aSession.createQuery(hql);
			List checks = hqlQuery.list();
			if (checks.isEmpty()) {
				logger.info("Checks table is empty. Starting populating predefined checks...");
				writeChecks(aSession);
			} else {
				logger.debug("Checks table is already populated");
			}

			hql = "from SbiLov";
			hqlQuery = aSession.createQuery(hql);
			List lovs = hqlQuery.list();
			if (lovs.isEmpty()) {
				logger.info("Lovs table is empty. Starting populating predefined lovs...");
				writeLovs(aSession);
			} else {
				logger.debug("Lovs table is already populated");
			}

			hql = "from SbiUserFunctionality";
			hqlQuery = aSession.createQuery(hql);
			List userFunctionalities = hqlQuery.list();
			if (userFunctionalities.isEmpty()) {
				logger.info("User functionality table is empty. Starting populating predefined User functionalities...");
				writeUserFunctionalities(aSession);
			} else {
				logger.debug("User functionality table is already populated");
			}


			hql = "from SbiExporters";
			hqlQuery = aSession.createQuery(hql);
			List exporters = hqlQuery.list();
			if (exporters.isEmpty()) {
				logger.info("Exporters table is empty. Starting populating predefined engines...");
				writeExporters(aSession);
			} else {
				logger.debug("Exporters table is already populated");
			}
			
			hql = "from SbiConfig";
			hqlQuery = aSession.createQuery(hql);
			List configs = hqlQuery.list();
			if (configs.isEmpty()) {
				logger.info("Config table is empty. Starting populating predefined configuration parameters...");
				writeConfig(aSession);
			} else {
				logger.debug("Config table is already populated");
			}
			
			
			hql = "from SbiKpiPeriodicity";
			hqlQuery = aSession.createQuery(hql);
			List periodicities = hqlQuery.list();
			if (periodicities.isEmpty()) {
				logger.info("Periodicity table is empty. Starting populating predefined periodicities...");
				writePeriodicities(aSession);
			} else {
				logger.debug("Periodicity table is already populated");
			}

			//resets grants availability to true if grants are present
			hql = "from SbiOrgUnitGrant g where g.isAvailable != true ";
			hqlQuery = aSession.createQuery(hql);
			List grants = hqlQuery.list();
			if (grants.isEmpty()) {
				logger.info("Grants table is empty. Nothing to reset...");
				
			} else {
				logger.debug("Grants table is populated. Start resetting availability...");
				resetGrantsAvailable(aSession, grants);
			}

			tx.commit();

			SingletonConfig.getInstance().clearCache();
			
		} catch (Exception e) {
			logger.error("Error while initializing metadata", e);
			if (tx != null)
				tx.rollback();
		} finally {
			if (aSession != null){
				if (aSession.isOpen()) aSession.close();
			}
			logger.debug("OUT");
		}
	}

	private SourceBean getConfiguration(String resource) throws Exception {
		logger.debug("IN");
		InputStream is = null;
		SourceBean toReturn = null;
		try {
			Thread curThread = Thread.currentThread();
			ClassLoader classLoad = curThread.getContextClassLoader();
			is = classLoad.getResourceAsStream(resource);
			InputSource source = new InputSource(is);
			toReturn = SourceBean.fromXMLStream(source);
			logger.debug("Configuration successfully read from resource " + resource);
		} catch (Exception e) {
			logger.error("Error while reading configuration from resource " + resource, e);
			throw e;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e);
				}
				logger.debug("OUT");
		}
		return toReturn;
	}
	private void resetGrantsAvailable(Session aSession, List grants) throws Exception {
		logger.debug("IN");
		for(int i=0; i< grants.size(); i++){
			SbiOrgUnitGrant grant = (SbiOrgUnitGrant)grants.get(i);
			grant.setIsAvailable(true);
			aSession.save(grant);
			aSession.flush();
		}
		
		logger.debug("OUT");
	}
	private void writeDomains(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean domainsSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/domains.xml");
		if (domainsSB == null) {
			throw new Exception("Domains configuration file not found!!!");
		}
		List domainsList = domainsSB.getAttributeAsList("DOMAIN");
		if (domainsList == null || domainsList.isEmpty()) {
			throw new Exception("No predefined domains found!!!");
		}
		Iterator it = domainsList.iterator();
		while (it.hasNext()) {
			SourceBean aDomainSB = (SourceBean) it.next();
			SbiDomains aDomain = new SbiDomains();
			aDomain.setDomainCd((String) aDomainSB.getAttribute("domainCd"));
			aDomain.setDomainNm((String) aDomainSB.getAttribute("domainNm"));
			aDomain.setValueCd((String) aDomainSB.getAttribute("valueCd"));
			aDomain.setValueNm((String) aDomainSB.getAttribute("valueNm"));
			aDomain.setValueDs((String) aDomainSB.getAttribute("valueDs"));
			logger.debug("Inserting Domain with valueCd = [" + aDomainSB.getAttribute("valueCd") + "], domainCd = [" + aDomainSB.getAttribute("domainCd") + "] ...");
			aSession.save(aDomain);
		}
		logger.debug("OUT");
	}
	
	private void writeMissingDomains(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean domainsSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/domains.xml");
		if (domainsSB == null) {
			throw new Exception("Domains configuration file not found!!!");
		}
		List domainsList = domainsSB.getAttributeAsList("DOMAIN");
		if (domainsList == null || domainsList.isEmpty()) {
			throw new Exception("No predefined domains found!!!");
		}
		
		List alreadyExamined = new ArrayList();
		Iterator it = domainsList.iterator();
		while (it.hasNext()) {
			SourceBean aDomainSB = (SourceBean) it.next();
			if(!alreadyExamined.contains(aDomainSB)){
				
			String domainCd = (String) aDomainSB.getAttribute("domainCd");
			if (domainCd == null || domainCd.equals("")) {
				logger.error("No predefined domains code found!!!");
				throw new Exception("No predefined domains code found!!!");
			}
			//Retrieving all the domains in the DB with the specified domain Code
			logger.debug("Retrieving all the domains in the DB with the specified domain Code");
			String hql = "from SbiDomains where domainCd = '"+domainCd+"'";
			Query hqlQuery = aSession.createQuery(hql);
			List result = hqlQuery.list();
			
			logger.debug("Retrieving all the domains in the XML file with the specified domain Code");
			//Retrieving all the domains in the XML file with the specified domain Code
			List domainsXmlList = domainsSB.getFilteredSourceBeanAttributeAsList("DOMAIN", "domainCd", domainCd);
			
			logger.debug("Retrieving all the domains in the XML file with the specified domain Code");
			//Checking if the domains in the DB are less than the ones in the xml file
			if(result.size() < domainsXmlList.size()){
				//Less domains in the DB than in the XML file, will add new ones
				logger.debug("Less domains in the DB than in the XML file, will add new ones");
				addMissingDomains(aSession,result,domainsXmlList);
			}
			//Removing form the list of XML domains the ones already checked
			logger.debug("Adding to the list of XML domains already checked");
			alreadyExamined.addAll(domainsXmlList);
			}
		}
		logger.debug("OUT");
	}
	
	private void addMissingDomains(Session aSession, List dbDomains, List xmlDomains){
		logger.debug("IN");
		
		Iterator it2 = xmlDomains.iterator();
		while(it2.hasNext()){
			boolean existsInDb = false;
			SourceBean aDomainSB = (SourceBean) it2.next();
			String valueCdXml = (String) aDomainSB.getAttribute("valueCd");
			logger.debug("Retrieved valueCd of XML Domain: "+valueCdXml);
			
			Iterator it = dbDomains.iterator();
			while (it.hasNext()) {
				SbiDomains d = (SbiDomains)it.next();
				String valueCd = d.getValueCd();
				logger.debug("Retrieved valueCd of DB Domain: "+valueCd);
				
				if(valueCdXml.equalsIgnoreCase(valueCd)){
					existsInDb = true;
					logger.debug("Domain already exists in the DB");
					break;
				}				
			}	
			if(!existsInDb){
				logger.debug("Domain doesn't exist in the DB");
				SbiDomains aDomain = new SbiDomains();
				aDomain.setDomainCd((String) aDomainSB.getAttribute("domainCd"));
				aDomain.setDomainNm((String) aDomainSB.getAttribute("domainNm"));
				aDomain.setValueCd((String) aDomainSB.getAttribute("valueCd"));
				aDomain.setValueNm((String) aDomainSB.getAttribute("valueNm"));
				aDomain.setValueDs((String) aDomainSB.getAttribute("valueDs"));
				logger.debug("New Domain ready to be iserted in the DB");
				logger.debug("Inserting Domain with valueCd = [" + aDomainSB.getAttribute("valueCd") + "], domainCd = [" + aDomainSB.getAttribute("domainCd") + "] ...");
				aSession.save(aDomain);
				logger.debug("New Domain iserted in the DB");
			}
		}		
		logger.debug("OUT");
	}
	
	private void writePeriodicities(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean kpiSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/kpi.xml");
		if (kpiSB == null) {
			throw new Exception("Kpis configuration file not found!!!");
		}
		List periodicitiesList = kpiSB.getAttributeAsList("PERIODICITY");
		if (periodicitiesList == null || periodicitiesList.isEmpty()) {
			throw new Exception("No predefined periodicities found!!!");
		}
		Iterator it = periodicitiesList.iterator();
		while (it.hasNext()) {
			SourceBean aPeriodicitySB = (SourceBean) it.next();
			SbiKpiPeriodicity periodicity = new SbiKpiPeriodicity();
			periodicity.setName((String) aPeriodicitySB.getAttribute("name"));
			periodicity.setMonths(new Integer((String) aPeriodicitySB.getAttribute("months")));
			periodicity.setDays(new Integer((String) aPeriodicitySB.getAttribute("days")));
			periodicity.setHours(new Integer((String) aPeriodicitySB.getAttribute("hours")));
			periodicity.setMinutes(new Integer((String) aPeriodicitySB.getAttribute("minutes")));
			logger.debug("Inserting Periodicity with name = [" + aPeriodicitySB.getAttribute("name") + "]");
			aSession.save(periodicity);
		}
		logger.debug("OUT");
	}

	private void writeEngines(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean enginesSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/engines.xml");
		if (enginesSB == null) {
			logger.info("Configuration file for predefined engines not found");
			return;
		}
		List enginesList = enginesSB.getAttributeAsList("ENGINE");
		if (enginesList == null || enginesList.isEmpty()) {
			logger.info("No predefined engines available from configuration file");
			return;
		}
		Iterator it = enginesList.iterator();
		while (it.hasNext()) {
			SourceBean anEngineSB = (SourceBean) it.next();
			SbiEngines anEngine = new SbiEngines();
			anEngine.setName((String) anEngineSB.getAttribute("name"));
			anEngine.setDescr((String) anEngineSB.getAttribute("descr"));
			anEngine.setMainUrl((String) anEngineSB.getAttribute("mainUrl"));
			anEngine.setDriverNm((String) anEngineSB.getAttribute("driverNm"));
			anEngine.setLabel((String) anEngineSB.getAttribute("label"));
			anEngine.setClassNm((String) anEngineSB.getAttribute("classNm"));
			anEngine.setUseDataSet(new Boolean((String) anEngineSB.getAttribute("useDataSet")));
			anEngine.setUseDataSource(new Boolean((String) anEngineSB.getAttribute("useDataSource")));
			anEngine.setEncrypt(new Short((String) anEngineSB.getAttribute("encrypt")));
			anEngine.setObjUplDir((String) anEngineSB.getAttribute("objUplDir"));
			anEngine.setObjUseDir((String) anEngineSB.getAttribute("objUseDir"));
			anEngine.setSecnUrl((String) anEngineSB.getAttribute("secnUrl"));

			String engineTypeCd = (String) anEngineSB.getAttribute("engineTypeCd");
			SbiDomains domainEngineType = findDomain(aSession, engineTypeCd, "ENGINE_TYPE");
			anEngine.setEngineType(domainEngineType);

			String biobjTypeCd = (String) anEngineSB.getAttribute("biobjTypeCd");
			SbiDomains domainBiobjectType = findDomain(aSession, biobjTypeCd, "BIOBJ_TYPE");
			anEngine.setBiobjType(domainBiobjectType);

			logger.debug("Inserting Engine with label = [" + anEngineSB.getAttribute("label") + "] ...");

			aSession.save(anEngine);
		}
		logger.debug("OUT");
	}


	private void writeExporters(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean exportersSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/exporters.xml");
		if (exportersSB == null) {
			logger.info("Configuration file for predefined exporters not found");
			return;
		}
		List exportersList = exportersSB.getAttributeAsList("EXPORTER");
		if (exportersList == null || exportersList.isEmpty()) {
			logger.info("No predefined exporters available from configuration file");
			return;
		}
		Iterator it = exportersList.iterator();
		while (it.hasNext()) {
			SourceBean anExporterSB = (SourceBean) it.next();

			String domainLabel = ((String) anExporterSB.getAttribute("domain"));
			SbiDomains hibDomain = findDomain(aSession, domainLabel, "EXPORT_TYPE");
			if (hibDomain == null) {
				logger.error("Could not find domain for exporter");
				return;
			}

			String engineLabel = ((String) anExporterSB.getAttribute("engine"));
			SbiEngines hibEngine = findEngine(aSession, engineLabel);
			if (hibEngine == null) {
				logger.error("Could not find engine with label [" + engineLabel + "] for exporter");
			}else{

				String defaultValue=((String) anExporterSB.getAttribute("defaultValue"));
	
				SbiExporters anExporter=new SbiExporters();
				SbiExportersId exporterId=new SbiExportersId(hibEngine.getEngineId(), hibDomain.getValueId());
				anExporter.setId(exporterId);
				anExporter.setSbiDomains(hibDomain);
				anExporter.setSbiEngines(hibEngine);
	
				Boolean value=defaultValue!=null ? Boolean.valueOf(defaultValue) : Boolean.FALSE;
				anExporter.setDefaultValue(value.booleanValue());
	
				logger.debug("Inserting Exporter for engine "+hibEngine.getLabel());
	
				aSession.save(anExporter);
			}
		}
		logger.debug("OUT");
	}


	private void writeChecks(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean checksSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/checks.xml");
		if (checksSB == null) {
			logger.info("Configuration file for predefined checks not found");
			return;
		}
		List checksList = checksSB.getAttributeAsList("CHECK");
		if (checksList == null || checksList.isEmpty()) {
			logger.info("No predefined checks available from configuration file");
			return;
		}
		Iterator it = checksList.iterator();
		while (it.hasNext()) {
			SourceBean aChecksSB = (SourceBean) it.next();
			SbiChecks aCheck = new SbiChecks();
			aCheck.setLabel((String) aChecksSB.getAttribute("label"));
			aCheck.setName((String) aChecksSB.getAttribute("name"));
			aCheck.setDescr((String) aChecksSB.getAttribute("descr"));

			String valueTypeCd = (String) aChecksSB.getAttribute("valueTypeCd");
			SbiDomains domainValueType = findDomain(aSession, valueTypeCd, "PRED_CHECK");
			aCheck.setCheckType(domainValueType);
			aCheck.setValueTypeCd(valueTypeCd);

			aCheck.setValue1((String) aChecksSB.getAttribute("value1"));
			aCheck.setValue2((String) aChecksSB.getAttribute("value2"));

			logger.debug("Inserting Check with label = [" + aChecksSB.getAttribute("label") + "] ...");

			aSession.save(aCheck);
		}
		logger.debug("OUT");
	}

	private void writeLovs(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean lovsSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/lovs.xml");
		if (lovsSB == null) {
			logger.info("Configuration file for predefined lovs not found");
			return;
		}
		List lovsList = lovsSB.getAttributeAsList("LOV");
		if (lovsList == null || lovsList.isEmpty()) {
			logger.info("No predefined lovs available from configuration file");
			return;
		}
		Iterator it = lovsList.iterator();
		while (it.hasNext()) {
			SourceBean aLovsSB = (SourceBean) it.next();
			SbiLov aLov = new SbiLov();
			aLov.setLabel((String) aLovsSB.getAttribute("label"));
			aLov.setName((String) aLovsSB.getAttribute("name"));
			aLov.setDescr((String) aLovsSB.getAttribute("descr"));
			aLov.setDefaultVal((String) aLovsSB.getAttribute("defaultVal"));
			aLov.setProfileAttr((String) aLovsSB.getAttribute("profileAttr"));

			SourceBean lovProviderSB = (SourceBean) aLovsSB.getAttribute("LOV_PROVIDER");
			aLov.setLovProvider(lovProviderSB.getCharacters());

			String inputTypeCd = (String) aLovsSB.getAttribute("inputTypeCd");
			SbiDomains domainInputType = findDomain(aSession, inputTypeCd, "INPUT_TYPE");
			aLov.setInputType(domainInputType);
			aLov.setInputTypeCd(inputTypeCd);

			logger.debug("Inserting Lov with label = [" + aLovsSB.getAttribute("label") + "] ...");

			aSession.save(aLov);
		}
		logger.debug("OUT");
	}

	private void writeUserFunctionalities(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean userFunctionalitiesSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/userFunctionalities.xml");
		SourceBean roleTypeUserFunctionalitiesSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/roleTypeUserFunctionalities.xml");
		if (userFunctionalitiesSB == null) {
			throw new Exception("User functionalities configuration file not found!!!");
		}
		if (roleTypeUserFunctionalitiesSB == null) {
			throw new Exception("Role type user functionalities configuration file not found!!!");
		}
		List userFunctionalitiesList = userFunctionalitiesSB.getAttributeAsList("USER_FUNCTIONALITY");
		if (userFunctionalitiesList == null || userFunctionalitiesList.isEmpty()) {
			throw new Exception("No predefined user functionalities found!!!");
		}
		Iterator it = userFunctionalitiesList.iterator();
		while (it.hasNext()) {
			SourceBean aUSerFunctionalitySB = (SourceBean) it.next();
			SbiUserFunctionality aUserFunctionality = new SbiUserFunctionality();
			String userFunctionality = (String) aUSerFunctionalitySB.getAttribute("name");
			aUserFunctionality.setName(userFunctionality);
			aUserFunctionality.setDescription((String) aUSerFunctionalitySB.getAttribute("description"));
			Object roleTypesObject = roleTypeUserFunctionalitiesSB.getFilteredSourceBeanAttribute("ROLE_TYPE_USER_FUNCTIONALITY", "userFunctionality", userFunctionality);
			if (roleTypesObject == null) {
				throw new Exception("No role type found for user functionality [" + userFunctionality + "]!!!");
			}
			StringBuffer roleTypesStrBuffer = new StringBuffer();
			Set roleTypes = new HashSet();
			if (roleTypesObject instanceof SourceBean) {
				SourceBean roleTypeSB = (SourceBean) roleTypesObject;
				String roleTypeCd = (String) roleTypeSB.getAttribute("roleType");
				roleTypesStrBuffer.append(roleTypeCd);
				SbiDomains domainRoleType = findDomain(aSession, roleTypeCd, "ROLE_TYPE");
				roleTypes.add(domainRoleType);
			} else if (roleTypesObject instanceof List) {
				List roleTypesSB = (List) roleTypesObject;
				Iterator roleTypesIt = roleTypesSB.iterator();
				while (roleTypesIt.hasNext()) {
					SourceBean roleTypeSB = (SourceBean) roleTypesIt.next();
					String roleTypeCd = (String) roleTypeSB.getAttribute("roleType");
					roleTypesStrBuffer.append(roleTypeCd);
					if (roleTypesIt.hasNext()) {
						roleTypesStrBuffer.append(";");
					}
					SbiDomains domainRoleType = findDomain(aSession, roleTypeCd, "ROLE_TYPE");
					roleTypes.add(domainRoleType);
				}
			}
			aUserFunctionality.setRoleType(roleTypes);

			logger.debug("Inserting UserFunctionality with name = [" + aUSerFunctionalitySB.getAttribute("name") + "] associated to role types [" + roleTypesStrBuffer.toString() + "]...");

			aSession.save(aUserFunctionality);
		}
		logger.debug("OUT");
	}
	
	private void writeConfig(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean configSB = getConfiguration("it/eng/spagobi/commons/initializers/metadata/config/configs.xml");
		if (configSB == null) {
			logger.info("Configuration file for predefined configuration parameters not found");
			return;
		}
		List configList = configSB.getAttributeAsList("CONFIG");
		if (configList == null || configList.isEmpty()) {
			logger.info("No predefined configuration parameters available from configuration file");
			return;
		}
		Iterator it = configList.iterator();
		while (it.hasNext()) {
			SourceBean aConfigSB = (SourceBean) it.next();
			SbiDomains hibDomain = null;
			
			String valueTypeCd = (String) aConfigSB.getAttribute("valueType");
			if (!"".equals(valueTypeCd)){
				hibDomain = findDomain(aSession, valueTypeCd, "PAR_TYPE");
				if (hibDomain == null) {
					logger.error("Could not find domain for configuration parameter");
					return;
				}
			} 
			


			String confLabel=((String) aConfigSB.getAttribute("label"));
			String confName=((String) aConfigSB.getAttribute("name"));
			String confDesc=((String) aConfigSB.getAttribute("description"));
			String confIsActive=((String) aConfigSB.getAttribute("isActive"));
			String confValueCheck=((String) aConfigSB.getAttribute("valueCheck"));	
			String confCategory=((String) aConfigSB.getAttribute("category"));
			

			SbiConfig aConfig=new SbiConfig();
			//aConfig.setId(exporterId);
			aConfig.setLabel(confLabel);
			aConfig.setName(confName);
			aConfig.setDescription(confDesc);
			aConfig.setValueCheck(confValueCheck);
			aConfig.setSbiDomains(hibDomain);
			aConfig.setCategory(confCategory);

			Boolean value = confIsActive!=null ? Boolean.valueOf(confIsActive) : Boolean.FALSE;
			aConfig.setIsActive(value.booleanValue());

			logger.debug("Inserting Config parameter "+aConfig.getLabel());

			aSession.save(aConfig);

		}
		logger.debug("OUT");
	}


	private SbiDomains findDomain(Session aSession, String valueCode, String domainCode) {
		logger.debug("IN");
		String hql = "from SbiDomains where valueCd = ? and domainCd = ?";
		Query hqlQuery = aSession.createQuery(hql);
		hqlQuery.setParameter(0, valueCode);
		hqlQuery.setParameter(1, domainCode);
		SbiDomains domain = (SbiDomains) hqlQuery.uniqueResult();
		logger.debug("OUT");
		return domain;
	}
	
	private SbiEngines findEngine(Session aSession, String label) {
		logger.debug("IN");
		String hql = "from SbiEngines where label = ?";
		Query hqlQuery = aSession.createQuery(hql);
		hqlQuery.setParameter(0, label);
		SbiEngines engine = (SbiEngines) hqlQuery.uniqueResult();
		logger.debug("OUT");
		return engine;
	}
}
