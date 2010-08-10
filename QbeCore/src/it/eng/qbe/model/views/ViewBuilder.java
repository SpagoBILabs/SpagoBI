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
package it.eng.qbe.model.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewBuilder.
 * 
 * @author Andrea Gioia
 */
public class ViewBuilder {
	
	/** The Constant CREATE_VIEW_DDL_SCRIPT. */
	public static final String CREATE_VIEW_DDL_SCRIPT = "CREATE VIEW {0} AS {1}";
	
	/**
	 * Load create view ddl script.
	 * 
	 * @param templateScriptFile the template script file
	 * 
	 * @return the string
	 */
	private String loadCreateViewDDLScript(File templateScriptFile) {		
		String createViewDDLScript = null;
		
		StringBuffer buffer;
		BufferedReader in;
		String line;
		
		buffer = new StringBuffer();
		try {
	        in = new BufferedReader(new FileReader(templateScriptFile));	        
	        while ((line = in.readLine()) != null) {
	            buffer.append(line);
	        }	        
	        createViewDDLScript = buffer.toString();
	        in.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } 
	    
	    return createViewDDLScript;
	}
	
	/**
	 * Gets the creates the view ddl script.
	 * 
	 * @param templateScriptFile the template script file
	 * 
	 * @return the creates the view ddl script
	 */
	private String getCreateViewDDLScript(File templateScriptFile) {
		String createViewDDLScript = null;
		
		if(templateScriptFile != null && templateScriptFile.exists()) {
			createViewDDLScript = loadCreateViewDDLScript(templateScriptFile);
		}
		
		if(createViewDDLScript == null) createViewDDLScript = CREATE_VIEW_DDL_SCRIPT;
		
		return createViewDDLScript;
	}
	
	/**
	 * Builds the view.
	 * 
	 * @param viewName the view name
	 * @param query the query
	 * @param connection the connection
	 * @param templateScriptFile the template script file
	 * 
	 * @throws SQLException the SQL exception
	 */
	public void buildView(String viewName, String query, Connection connection, File templateScriptFile) throws SQLException {
		
		Statement statement;
		String createViewDDLScript;
		
		statement = connection.createStatement();
		createViewDDLScript = getCreateViewDDLScript(templateScriptFile);
		
		Object[] params = new Object[] {
				viewName, query	
		};
		
		String sqlCreateView = MessageFormat.format(createViewDDLScript, params);
		
		statement.execute(sqlCreateView);				
	}
}
