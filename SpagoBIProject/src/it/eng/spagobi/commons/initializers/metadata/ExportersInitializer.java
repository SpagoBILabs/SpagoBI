/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.initializers.metadata;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.engines.config.metadata.SbiExporters;
import it.eng.spagobi.engines.config.metadata.SbiExportersId;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ExportersInitializer extends SpagoBIInitializer {

	static private Logger logger = Logger.getLogger(ExportersInitializer.class);

	public ExportersInitializer() {
		targetComponentName = "Exporters";
		configurationFileName = "it/eng/spagobi/commons/initializers/metadata/config/exporters.xml";
	}
	
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			List<SbiTenant> tenants = DAOFactory.getTenantsDAO().loadAllTenants();
			for (SbiTenant tenant : tenants) {
				init(config, hibernateSession, tenant);
			}
		} finally {
			logger.debug("OUT");
		}
	}
	
	public void init(SourceBean config, Session hibernateSession, SbiTenant tenant) {
		logger.debug("IN");
		try {
			String hql = "from SbiExporters e where e.sbiEngines.commonInfo.organization = :organization";
			Query hqlQuery = hibernateSession.createQuery(hql);
			hqlQuery.setString("organization", tenant.getName());
			List exporters = hqlQuery.list();
			if (exporters.isEmpty()) {
				logger.info("No exporters for tenant " + tenant.getName() + ". Starting populating predefined exporters...");
				writeExporters(hibernateSession, tenant);
			} else {
				logger.debug("Exporters table is already populated for tenant " + tenant.getName());
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Ab unexpected error occured while initializeng LOVs", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private void writeExporters(Session aSession, SbiTenant tenant) throws Exception {
		logger.debug("IN");
		SourceBean exportersSB = getConfiguration();
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
			SbiEngines hibEngine = findEngine(aSession, engineLabel, tenant);
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
}
