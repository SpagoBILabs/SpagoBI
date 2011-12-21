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
package it.eng.spagobi.workflow;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;


public class JbpmContextInitializer implements InitializerIFace {
	
	/** 
	 * SourceBean that contains the configuration parameters
	 */
	private SourceBean _config = null;

	
	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
		JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
		DbPersistenceServiceFactory dbpsf = (DbPersistenceServiceFactory)jbpmConfiguration.getServiceFactory(Services.SERVICENAME_PERSISTENCE);
		try{
			SessionFactory sessionFactHib = dbpsf.getSessionFactory();
			Session sessionHib = sessionFactHib.openSession();
			Query hibQuery = sessionHib.createQuery(" from ProcessDefinition");
			List hibList = hibQuery.list();			
		} catch (HibernateException he) {
			jbpmConfiguration.createSchema();
		} 
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#getConfig()
	 */
	public SourceBean getConfig() {
		return _config;
	}

	
}
