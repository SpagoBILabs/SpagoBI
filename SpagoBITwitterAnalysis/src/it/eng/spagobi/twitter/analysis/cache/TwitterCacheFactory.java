/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.twitter.analysis.cache;

/**
 * @author Marco Cortella (marco.cortella@eng.it), Giorgio Federici
 *         (giorgio.federici@eng.it)
 *
 */
public class TwitterCacheFactory {

	public final String MYSQL = "MySql";

	public ITwitterCache getCache(String type) {
		if (type.equalsIgnoreCase(MYSQL)) {

			// TODO parametri da inserire via configuration
			String url = "jdbc:mysql://localhost:3306/twitterdb";
			String driver = "com.mysql.jdbc.Driver";
			String userName = "root";
			String password = "root";
			// String tableName = "tweetdata";

			// return new MySQLTwitterCache(url, driver, userName, password,
			// tableName);
			return new MySQLTwitterCache(url, driver, userName, password);
		} else {
			return null; // TODO: gestire caso di errore
		}
	}
}
