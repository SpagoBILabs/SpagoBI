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

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class TenantsInitializer extends SpagoBIInitializer {

	private static String TENANTS_CONFIG_TAG_NAME = "TENANTS";
	private static String TENANT_CONFIG_TAG_NAME = "TENANT";
	private static String TENANT_CONFIG_NAME_ATTRIBUTE = "name";
	
	@Override
	SourceBean getConfiguration() throws Exception {
		SourceBean config = (SourceBean) ConfigSingleton.getInstance().getAttribute(TENANTS_CONFIG_TAG_NAME);
		if (config == null) {
			throw new Exception("Tenants configuration not found!!!");
		}
		return config;
	}

	static private Logger logger = Logger.getLogger(TenantsInitializer.class);

	public TenantsInitializer() {
		targetComponentName = "Tenants";
	}
	
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			String hql = "from SbiTenant";
			Query hqlQuery = hibernateSession.createQuery(hql);
			List<SbiTenant> existingTenants = hqlQuery.list();
			List<String> configuredTenants = getConfiguredTenants();
			for (String aConfiguredTenant : configuredTenants) {
				if (exists(aConfiguredTenant, existingTenants)) {
					LogMF.debug(logger, "Tenant {0} already exists", aConfiguredTenant);
				} else {
					LogMF.info(logger, "Tenant {0} does not exist. It will be inserted", aConfiguredTenant);
					writeTenant(aConfiguredTenant, hibernateSession);
					LogMF.debug(logger, "Tenant {0} was inserted", aConfiguredTenant);
				}
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Ab unexpected error occured while initializeng Tenants", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private boolean exists(String aConfiguredTenant, List<SbiTenant> existingTenants) {
		for (SbiTenant aTenant : existingTenants) {
			if (aTenant.getName().equals(aConfiguredTenant)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getConfiguredTenants() throws Exception {
		logger.debug("IN");
		List<String> toReturn = new ArrayList<String>();
		SourceBean configuration = this.getConfiguration();
		List tenantsSB = configuration.getAttributeAsList(TENANT_CONFIG_TAG_NAME);
		if (tenantsSB == null || tenantsSB.isEmpty()) {
			throw new Exception("No configured tenants found!!!");
		}
		Iterator it = tenantsSB.iterator();
		while (it.hasNext()) {
			SourceBean aTenantSB = (SourceBean) it.next();
			String name = (String) aTenantSB.getAttribute(TENANT_CONFIG_NAME_ATTRIBUTE);
			LogMF.debug(logger, "Found configured tenant: [{0}]", name);
			toReturn.add(name);
		}
		logger.debug("OUT");
		return toReturn;
	}

	private void writeTenant(String tenantName, Session hibernateSession) throws Exception {
		logger.debug("IN");
		SbiTenant aTenant = new SbiTenant();
		aTenant.setName(tenantName);
		logger.debug("Inserting tenant with name = [" + tenantName + "]...");
		hibernateSession.save(aTenant);
		logger.debug("OUT");
	}

}
