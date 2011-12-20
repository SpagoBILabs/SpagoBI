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
package it.eng.qbe.datasource.transaction.jpa;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;

import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.datasource.transaction.ITransaction;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class JPAHibernateTransaction implements ITransaction{
	
	private IJpaDataSource dataSource;
	private Session session;
	
	public JPAHibernateTransaction(IJpaDataSource dataSource){
		this.dataSource = dataSource;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#open()
	 */
	public void open(){
		session = ( (HibernateEntityManager) dataSource.getEntityManager()).getSession();
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#close()
	 */
	public void close(){
		//we use the active session so we should not close it
		//session.close();
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.transaction.ITransaction#getSQLConnection()
	 */
	public java.sql.Connection getSQLConnection(){
		return session.connection();
	}
}