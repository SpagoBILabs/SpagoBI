/*
*
* @file DatabaseLoader.java
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
* @version $Id: DatabaseLoader.java,v 1.3 2009/12/14 12:46:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloInfo;

/**
 * <p><code>DatabaseInfoLoader</code></p>
 * This abstract base class manages the loading of {@link DatabaseInfo} objects.
 *
 * @author ArndHouben
 * @version $Id: DatabaseLoader.java,v 1.3 2009/12/14 12:46:57 PhilippBouillon Exp $
 **/
public abstract class DatabaseLoader extends PaloInfoLoader {

	/**
	 * Creates a new loader instance.
	 * @param paloConnection
	 */
	public DatabaseLoader(DbConnection paloConnection) {
		super(paloConnection);
	}
	
	public abstract int getDatabaseCount();
	/**
	 * Returns the identifiers of all databases currently known to the palo 
	 * server.
	 * @return ids of all known palo databases
	 */
	public abstract String[] getAllDatabaseIds();
	
	/**
	 * Loads the <code>DatabaseInfo</code> object by its name
	 * @param name the name of the <code>DatabaseInfo</code> to load
	 * @return the loaded <code>DatabaseInfo</code> instance
	 */
	public abstract DatabaseInfo loadByName(String name);
	
	
	/**
	 * Creates a new {@link DatabaseInfo} instance with the given name
	 * @param name the name of the new database
	 * @return a new <code>DatabaseInfo</code> object
	 */
	public final DatabaseInfo create(String name, int type) {
		DatabaseInfo dbInfo = paloConnection.addDatabase(name, type);
		loaded(dbInfo);
		return dbInfo;
	}

	/**
	 * Deletes the given <code>DatabaseInfo</code> instance from the palo 
	 * server as well as from the internal used cache.
	 * @param dbInfo the <code>DatabaseInfo</code> instance to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public final boolean  delete(DatabaseInfo dbInfo) {
		if(paloConnection.delete(dbInfo)) {
			removed(dbInfo);
			return true;
		} 
		return false;
	}

	/**
	 * Loads the <code>DatabaseInfo</code> object which corresponds to the given
	 * id
	 * @param id the identifier of the <code>DatabaseInfo</code> object to load
	 * @return the loaded <code>DatabaseInfo</code> object
	 */	
	public final DatabaseInfo load(String id) {
		PaloInfo db = loadedInfo.get(id);
		if (db == null) {
			db = paloConnection.getDatabase(id);
			loaded(db);
		}
		return (DatabaseInfo)db;
	}
	
	/**
	 * Loads the <code>DatabaseInfo</code> object at the specified index
	 * @param index the index of the <code>DatabaseInfo</code> object to load
	 * @return the loaded <code>DatabaseInfo</code> object
	 */	
	public final DatabaseInfo load(int index) {
		String[] dbIds = getAllDatabaseIds();
		if(index<0 || index > dbIds.length-1)
			return null;
		return load(dbIds[index]);
	}
}
