/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.utilities;


import it.eng.qbe.datasource.transaction.hibernate.HibernateTransaction;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	
	public static transient Logger logger = Logger.getLogger(HibernateUtil.class);

	private static final SessionFactory sessionFactory;
	static {
		try {


				String fileCfg = "hibernate.cfg.xml";
				fileCfg = fileCfg.trim();
				logger.info( "Initializing hibernate Session Factory Described by [" + fileCfg +"]");
				Configuration conf = new Configuration();
				conf = conf.configure(fileCfg);
				sessionFactory = conf.buildSessionFactory();

		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			logger.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	

	/**
	 * Current session.
	 * 
	 * @return the session
	 */
	public static Session currentSession() {
		return sessionFactory.openSession();
	}
	
	
	public static Connection getConnection(Session session) {
		return HibernateTransaction.getConnection(session);
	}

	
}
