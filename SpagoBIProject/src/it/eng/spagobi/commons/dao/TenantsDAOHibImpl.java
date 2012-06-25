/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.dao;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */

public class TenantsDAOHibImpl extends AbstractHibernateDAO implements ITenantsDAO {

    static private Logger logger = Logger.getLogger(TenantsDAOHibImpl.class);

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

}