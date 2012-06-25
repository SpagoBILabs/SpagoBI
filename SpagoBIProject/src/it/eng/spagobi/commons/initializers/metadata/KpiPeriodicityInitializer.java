/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class KpiPeriodicityInitializer extends SpagoBIInitializer {

	static private Logger logger = Logger.getLogger(KpiPeriodicityInitializer.class);

	public KpiPeriodicityInitializer() {
		targetComponentName = "Kpi Periodicity";
		configurationFileName = "it/eng/spagobi/commons/initializers/metadata/config/kpi.xml";
	}

	
	public void init(SourceBean config, Session hibernateSession) {
		logger.debug("IN");
		try {
			String hql = "from SbiKpiPeriodicity";
			Query hqlQuery = hibernateSession.createQuery(hql);
			List periodicities = hqlQuery.list();
			if (periodicities.isEmpty()) {
				logger.info("Periodicity table is empty. Starting populating predefined periodicities...");
				writePeriodicities(hibernateSession);
			} else {
				logger.debug("Periodicity table is already populated");
			}
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Ab unexpected error occured while initializeng Kpi Periodicity", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private void writePeriodicities(Session aSession) throws Exception {
		logger.debug("IN");
		SourceBean kpiSB = getConfiguration();
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

}
