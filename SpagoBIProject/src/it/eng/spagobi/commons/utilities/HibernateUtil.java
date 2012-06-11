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
