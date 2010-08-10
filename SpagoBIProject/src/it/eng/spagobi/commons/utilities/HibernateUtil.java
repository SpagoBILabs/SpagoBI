/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.commons.utilities;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	
	private static Log log = LogFactory.getLog(HibernateUtil.class);

	private static final SessionFactory sessionFactory;
	static {
		try {
			// Create the SessionFactory
			SourceBean fileCfgSb = ((SourceBean)ConfigSingleton.getInstance().getAttribute("SPAGOBI.HIBERNATE-CFGFILE"));
			
			if (fileCfgSb != null){
				String fileCfg = fileCfgSb.getCharacters();
				fileCfg = fileCfg.trim();
				log.info( "Initializing hibernate Session Factory Described by [" + fileCfg +"]");
				Configuration conf = new Configuration();
				conf = conf.configure(fileCfg);
				sessionFactory = conf.buildSessionFactory();
			}else{
			    log.info("Initializing hibernate Session Factory with default configuration [hibernate.cfg.xml]");
				sessionFactory = new Configuration().configure()
					.buildSessionFactory();
			}
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			log.error("Initial SessionFactory creation failed.", ex);
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

	
}
