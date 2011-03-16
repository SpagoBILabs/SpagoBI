/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.qbe.export;

import javax.persistence.EntityManager;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class HqlToSqlQueryRewriter.
 * 
 * @author Giachino
 */
public class JPQLToSqlQueryRewriter {
	
	/** The entity manager. */
	private EntityManager entityManager;
	
	/** Logger component. */
    public static transient Logger logger = LoggerFactory.getLogger(JPQLToSqlQueryRewriter.class);
	
	
	/**
	 * Instantiates a new hql to sql query rewriter.
	 * 
	 * @param session the session
	 */
	public JPQLToSqlQueryRewriter(EntityManager em) {
		this.entityManager = em;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.export.IQueryRewriter#rewrite(java.lang.String)
	 */
	//TODO
	public String rewrite(String query) {
		return "";
	}
	
	

}
