/*
*
* @file HSqlDbConnection.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: HSqlDbConnection.java,v 1.5 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.server.dbconnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.palo.viewapi.internal.DbService;



/**
 * <code>HSqlDbConnection</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HSqlDbConnection.java,v 1.5 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class HSqlDbConnection extends SqlDbConnection {

	
	//sole one instance:
//	private static final HSqlDbConnection instance = new HSqlDbConnection();
	public static HSqlDbConnection newInstance() {
		return new HSqlDbConnection();
	}

	
	private static final String DEFAULT_SQL_DB = "/sql/hsql";

	private HSqlDbConnection() {		
	}
	
	protected final String getSqlHomeDir() {
		return DEFAULT_SQL_DB;
	}

	protected final void initialize() {
		// nothing to do		
	}
	
	public void disconnect() {		
		try {
			shutdown();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.disconnect();
	}

	private void shutdown() throws SQLException {
		String shutdown = DbService.getQuery("shutdown");
		PreparedStatement stmt = null;
		try {
			try {
				stmt = connection.prepareStatement(shutdown);
				stmt.execute();
			} catch (SQLException e) {				
			}			
		} finally {
			if(stmt != null)
				stmt.close();
		}

	}
	
	
}
