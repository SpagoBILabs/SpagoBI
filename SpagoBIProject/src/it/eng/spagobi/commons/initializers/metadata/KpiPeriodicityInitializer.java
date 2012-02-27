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
