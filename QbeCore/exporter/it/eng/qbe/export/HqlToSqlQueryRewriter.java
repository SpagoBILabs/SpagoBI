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

import it.eng.qbe.model.accessmodality.ModelAccessModality;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;

/**
 * The Class HqlToSqlQueryRewriter.
 * 
 * @author Gioia
 */
public class HqlToSqlQueryRewriter {
	
	/** The session. */
	private Session session;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ModelAccessModality.class);
	
	
	/**
	 * Instantiates a new hql to sql query rewriter.
	 * 
	 * @param session the session
	 */
	public HqlToSqlQueryRewriter(Session session) {
		this.session = session;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.export.IQueryRewriter#rewrite(java.lang.String)
	 */
	public String rewrite(String query) {
		String sqlQuery = null;		
		logger.debug("rewrite: HQL query to convert: " + query);		
		
		
		Query hibQuery = session.createQuery(query);
		SessionFactory sessFact = session.getSessionFactory();
		SessionFactoryImplementor imple = (SessionFactoryImplementor) sessFact;
		ASTQueryTranslatorFactory factory = new ASTQueryTranslatorFactory();
		QueryTranslator trans = null; 
		// Hibernate 3.0
		Class[] parsTypes = null;
		
		parsTypes = new Class[3];
		
		parsTypes[0] = String.class;
		parsTypes[1] = Map.class;
		parsTypes[2] = SessionFactoryImplementor.class;
		
		Method createQueryTranslatorMethod = null;
		try{
			
			createQueryTranslatorMethod = factory.getClass().getMethod("createQueryTranslator", parsTypes);
			try{
				trans = (QueryTranslator)createQueryTranslatorMethod.invoke(factory, new Object[]{hibQuery.getQueryString(), Collections.EMPTY_MAP, imple});
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}catch (NoSuchMethodException e) {
			
			parsTypes = new Class[4];
			
			parsTypes[0] = String.class;
			parsTypes[1] = String.class;
			parsTypes[2] = Map.class;
			parsTypes[3] = SessionFactoryImplementor.class;
			
			try{
				createQueryTranslatorMethod = factory.getClass().getMethod("createQueryTranslator", parsTypes); 
			
				if (createQueryTranslatorMethod != null){
					try{
						trans = (QueryTranslator)createQueryTranslatorMethod.invoke(factory, new Object[]{String.valueOf(System.currentTimeMillis()), hibQuery.getQueryString(),Collections.EMPTY_MAP, imple});
					}catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}catch (NoSuchMethodException ex) {
				e.printStackTrace();
			}
		}
		
		trans.compile(new HashMap(), false);
		sqlQuery = trans.getSQLString();
		
		logger.debug("rewrite: generated SQL query: " + sqlQuery);		
		
		return sqlQuery;
	}
	
	

}
