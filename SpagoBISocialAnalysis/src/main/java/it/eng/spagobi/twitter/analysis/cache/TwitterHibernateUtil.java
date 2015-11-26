/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.cache;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

public class TwitterHibernateUtil {

	private static final SessionFactory sessionFactory;

	static {
		try {

			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();

		} catch (Throwable e) {

			throw new SpagoBIRuntimeException("TwitterHibernateUtil: Impossible initialize Hibernate SessionFactory." + e);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}