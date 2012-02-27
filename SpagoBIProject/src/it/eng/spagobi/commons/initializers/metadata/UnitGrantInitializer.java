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

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.dao.AbstractHibernateDAO;
import it.eng.spagobi.kpi.ou.metadata.SbiOrgUnitGrant;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class UnitGrantInitializer extends SpagoBIInitializer {

	static private Logger logger = Logger.getLogger(UnitGrantInitializer.class);

	public UnitGrantInitializer() {
		targetComponentName = "Unit Grant";
	}
	
	
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			//resets grants availability to true if grants are present
			String hql = "from SbiOrgUnitGrant g where g.isAvailable != true ";
			Query hqlQuery = hibernateSession.createQuery(hql);
			List grants = hqlQuery.list();
			if (grants.isEmpty()) {
				logger.info("Grants table is empty. Nothing to reset...");
				
			} else {
				logger.debug("Grants table is populated. Start resetting availability...");
				resetGrantsAvailable(hibernateSession, grants);
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Ab unexpected error occured while initializeng Kpi Periodicity", t);
		} finally {
			logger.debug("OUT");
		}
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

}
