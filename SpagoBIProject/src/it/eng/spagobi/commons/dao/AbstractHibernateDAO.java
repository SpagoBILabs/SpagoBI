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
package it.eng.spagobi.commons.dao;


import it.eng.spagobi.commons.utilities.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;



/**
 * Abstract class that al DAO will have to extend.
 * 
 * @author Zoppello
 */
public class AbstractHibernateDAO {
	
    private static transient Logger logger = Logger.getLogger(AbstractHibernateDAO.class);
    
	/**
	 * Gets tre current session.
	 * 
	 * @return The current session object.
	 */
	public Session getSession(){
		return HibernateUtil.currentSession();
	}
	
	/**
	 * Traces the exception information of a throwable input object.
	 * 
	 * @param t The input throwable object
	 */
	public void logException(Throwable t){
	    logger.error(t.getClass().getName()+" "+t.getMessage(),t);
	}
}
