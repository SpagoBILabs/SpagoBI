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
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class TenantsInitializer extends SpagoBIInitializer {

	static private Logger logger = Logger.getLogger(TenantsInitializer.class);

	public TenantsInitializer() {
		targetComponentName = "Tenants";
		configurationFileName = "it/eng/spagobi/commons/initializers/metadata/config/tenants.xml";
	}
	
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			String hql = "from SbiTenant";
			Query hqlQuery = hibernateSession.createQuery(hql);
			List tenants = hqlQuery.list();
			if (tenants.isEmpty()) {
				logger.info("Tenants table is empty. Starting populating domains...");
				writeTenants(hibernateSession);
			} else {
				logger.debug("Tenants table is already populated.");
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Ab unexpected error occured while initializeng Domains", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private void writeTenants(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean tenantsSB = getConfiguration();
		if (tenantsSB == null) {
			throw new Exception("Tenants configuration file not found!!!");
		}
		List tenantsList = tenantsSB.getAttributeAsList("TENANT");
		if (tenantsList == null || tenantsList.isEmpty()) {
			throw new Exception("No predefined tenants found!!!");
		}
		Iterator it = tenantsList.iterator();
		while (it.hasNext()) {
			SourceBean aTenantSB = (SourceBean) it.next();
			SbiTenant aTenant = new SbiTenant();
			aTenant.setName((String) aTenantSB.getAttribute("name"));
			logger.debug("Inserting tenant with name = [" + aTenantSB.getAttribute("name") + "]...");
			aSession.save(aTenant);
		}
		logger.debug("OUT");
	}

}
