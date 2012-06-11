/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.datasource.transaction.hibernate;

import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.datasource.transaction.ITransaction;

import java.sql.Connection;

import org.hibernate.Session;


/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class HibernateTransaction implements ITransaction{

	private IHibernateDataSource dataSource;
	private Session session;
	
	public HibernateTransaction(IHibernateDataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#open()
	 */
	public void open(){
		session = dataSource.getHibernateSessionFactory().openSession();	
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#close()
	 */
	public void close(){
		//the session in new so we should close it
		session.close();
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#getSQLConnection()
	 */
	public java.sql.Connection getSQLConnection(){
		return getConnection(this.session);
	}
	
	public static Connection getConnection(Session session) {
		// Hibernate 3
		return session.connection();
		
		// following code for Hibernate 4
//		try {
//			// reflective lookup to bridge between Hibernate 3.x and 4.x
//			// see http://stackoverflow.com/questions/3526556/session-connection-deprecated-on-hibernate
//			Method connectionMethod = session.getClass().getMethod(
//						"connection");
//			return (Connection) connectionMethod.invoke(session);
//		} catch (NoSuchMethodException e) {
//			throw new IllegalStateException(
//					"Cannot find connection() method on Hibernate session", e);
//		} catch (IllegalArgumentException e) {
//			throw new IllegalStateException(
//					"IllegalArgumentException invoking connection() method on Hibernate session", e);
//		} catch (IllegalAccessException e) {
//			throw new IllegalStateException(
//					"IllegalAccessException invoking connection() method on Hibernate session", e);
//		} catch (InvocationTargetException e) {
//			throw new IllegalStateException(
//					"InvocationTargetException invoking connection() method on Hibernate session", e);
//		}
	}
	
}
