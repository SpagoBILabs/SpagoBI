/*
*
* @file HttpDatabaseLoader.java
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
* @author ArndHouben
*
* @version $Id: HttpDatabaseLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.loader;

import java.util.Collection;

import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.loader.DatabaseLoader;

/**
 * <code>HttpDatabaseLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: HttpDatabaseLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public class HttpDatabaseLoader extends DatabaseLoader {

	public HttpDatabaseLoader(DbConnection paloConnection) {
		super(paloConnection);
	}

	public int getDatabaseCount() {
		reload();
		return getLoaded().size();
	}

	public final String[] getAllDatabaseIds() {
		if(!loaded) {			
			reload();
			loaded = true;
		}
		return getLoadedIds();
	}

	public final DatabaseInfo loadByName(String name) {
		//first check if we have it loaded already
		DatabaseInfo dbInfo = findDatabase(name);
		if(dbInfo == null) {
			//if not, we have to ask server...
			reload();
			dbInfo = findDatabase(name);
		}
		return dbInfo;
	}
	
	protected final void reload() {
		reset();
		DatabaseInfo[] dbInfos = paloConnection.getDatabases();
		for (DatabaseInfo dbInfo : dbInfos) {
			loaded(dbInfo);
		}
	}
	
	private final DatabaseInfo findDatabase(String name) {
		Collection<PaloInfo> infos = getLoaded();
		for(PaloInfo info : infos) {
			if (info instanceof DatabaseInfo) {
				DatabaseInfo dbInfo = (DatabaseInfo) info;
				//PALO IS NOT CASESENSETIVE...
				if (dbInfo.getName().equalsIgnoreCase(name))
					return dbInfo;
			}
		}
		return null;
	}
}
