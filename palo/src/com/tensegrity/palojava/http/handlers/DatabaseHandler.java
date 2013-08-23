/*
*
* @file DatabaseHandler.java
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
* @version $Id: DatabaseHandler.java,v 1.11 2010/01/29 09:33:31 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava.http.handlers;

import java.io.IOException;
import java.net.ConnectException;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.builders.CubeInfoBuilder;
import com.tensegrity.palojava.http.builders.DatabaseInfoBuilder;
import com.tensegrity.palojava.http.builders.DimensionInfoBuilder;
import com.tensegrity.palojava.http.builders.InfoBuilderRegistry;
import com.tensegrity.palojava.impl.DatabaseInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: DatabaseHandler.java,v 1.11 2010/01/29 09:33:31 PhilippBouillon Exp $
 */
public class DatabaseHandler extends HttpHandler {
	
	//have to add database name to prefix
	private static final String CREATE_PREFIX = "/database/create?new_name=";
	//have to add database id to prefix	
	private static final String DELETE_PREFIX = "/database/destroy?database=";
	//have to add database id and new name (id&new_name=) to prefix	
	private static final String RENAME_PREFIX = "/database/rename?database=";
	//have to add database id to prefix	
	private static final String DIMENSIONS_PREFIX = "/database/dimensions?database=";
	//have to add database id to prefix	
	private static final String CUBES_PREFIX = "/database/cubes?database=";
	//have to add database id to prefix	
	private static final String INFO_PREFIX = "/database/info?database=";
	//have to add database id to prefix	
	private static final String LOAD_PREFIX = "/database/load?database=";
	//have to add database id to prefix	
	private static final String UNLOAD_PREFIX = "/database/unload?database=";
	//have to add database id to prefix	
	private static final String SAVE_PREFIX = "/database/save?database=";
	private static final String SHOW_NORMAL = "&show_normal=1";
	private static final String SHOW_SYSTEM = "&show_system=1";
	private static final String SHOW_USER_INFO = "&show_info=1";
	private static final String SHOW_ATTRIBUTE = "&show_attribute=1";
	private static final String HIDE_NORMAL = "&show_normal=0";
	private static final String HIDE_SYSTEM = "&show_system=0";
	private static final String HIDE_USER_INFO = "&show_info=0";
	private static final String HIDE_ATTRIBUTE = "&show_attribute=0";

	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final DatabaseHandler instance = new DatabaseHandler();
	static final DatabaseHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final InfoBuilderRegistry builderReg;
	private DatabaseHandler() {		
		builderReg = InfoBuilderRegistry.getInstance();
	}

//	/database/cubes  	Shows the list of cubes  	database
	public final CubeInfo [] getAllCubes(DatabaseInfo database) throws IOException {
		return getCubesInternal(database, true, -1); //false, false, false, false);
	}
	
	public final CubeInfo [] getCubes(DatabaseInfo database, int typeMask) throws IOException {
		return getCubesInternal(database, false, typeMask);
		
//		return getCubesInternal(database, false, 
//				(typeMask & CubeInfo.CUBETYPE_NORMAL) > 0, 
//				(typeMask & CubeInfo.CUBETYPE_SYSTEM) > 0,
//				(typeMask & CubeInfo.CUBETYPE_ATTRIBUTE) > 0,
//				(typeMask & CubeInfo.CUBETYPE_USERINFO) > 0);
	}
	
//	public final CubeInfo[] getCubes(DatabaseInfo database) throws IOException {
//		return getCubesInternal(database,true,false,false);
//	}
//		
//	public final CubeInfo[] getSystemCubes(DatabaseInfo database) throws IOException {
//		return getCubesInternal(database,false,true,false);
//
//	}
//	public final CubeInfo[] getNormalCubes(DatabaseInfo database) throws IOException {
//		return getCubesInternal(database,false,false,false);
//	}
//	
//	public final CubeInfo [] getUserInfoCubes(DatabaseInfo database) throws IOException {
//		return getCubesInternal(database, false, false, true);
//	}
	
//	/database/create 	Creates new database. 	server
	public final DatabaseInfo create(String name, int type) throws ConnectException,
			IOException {
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(encode(name));
		query.append("&type=");
		query.append(type);
		String[][] response = request(query.toString());
		DatabaseInfoBuilder databaseBuilder = builderReg.getDatabaseBuilder();
		return databaseBuilder.create(null, response[0]);

	}
	
//	/database/destroy 	Deletes a database. 	server
	public final boolean destroy(DatabaseInfo database) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(DELETE_PREFIX);	
		query.append(database.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);	
	}
	
//	/database/dimensions 	Shows the list of dimensions 	database
	public final DimensionInfo[] getAllDimensions(DatabaseInfo database)
			throws IOException {
		return getDimensionsInternal(database, true, -1);
	}
	
	public final DimensionInfo [] getDimensions(DatabaseInfo database, int typeMask) 
		throws IOException {
		return getDimensionsInternal(database, false, typeMask); 
//				(typeMask & DimensionInfo.DIMTYPE_NORMAL) > 0, 
//				(typeMask & DimensionInfo.DIMTYPE_SYSTEM) > 0,
//				(typeMask & DimensionInfo.DIMTYPE_ATTRIBUTE) > 0,
//				(typeMask & DimensionInfo.DIMTYPE_USERINFO) > 0);
	}
//	public final DimensionInfo[] getSystemDimensions(DatabaseInfo database)
//			throws IOException {
//		return getDimensionsInternal(database, false, true, false);
//	}
//
//	public final DimensionInfo[] getNormalDimensions(DatabaseInfo database)
//			throws IOException {
//		return getDimensionsInternal(database, false, false, false);
//	}
//	
//	public final DimensionInfo [] getUserInfoDimensions(DatabaseInfo database)
//		throws IOException {
//		return getDimensionsInternal(database, false, false, true);
//	}
		

//	/database/info 	Shows identifier, name, number of dimensions and number of cubes. 	database
	public final DatabaseInfo getInfo(String id) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(id);
		String[][] response = request(query.toString());
		DatabaseInfoBuilder databaseBuilder = builderReg.getDatabaseBuilder();
		return databaseBuilder.create(null, response[0]);
	}
	
//	/database/load 	Loads the database data (does not load cube data). 	database
	public final boolean load(DatabaseInfo database) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(LOAD_PREFIX);
		query.append(database.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
	
	public final DatabaseInfo reload(DatabaseInfo database) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		String[][] response = request(query.toString());
		DatabaseInfoBuilder databaseBuilder = builderReg.getDatabaseBuilder();
		databaseBuilder.update((DatabaseInfoImpl)database, response[0]);
		return database;
	}

//	/database/rename 	Renames a database 	database
	public final void rename(DatabaseInfo database, String newName)
			throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(RENAME_PREFIX);
		query.append(database.getId());
		query.append("&new_name=");
		query.append(newName);
		String[][] response = request(query.toString());
		if (response[0].length == 6) {
			String[] _response = new String[7];
			_response[6] = Integer.toString(database.getToken());
			System.arraycopy(response, 0, _response, 0, response.length);
			response[0] = _response;
		}
		DatabaseInfoBuilder databaseBuilder = builderReg.getDatabaseBuilder();
		databaseBuilder.update((DatabaseInfoImpl) database, response[0]);
	}
	
//	/database/save 	Saves the database data (does not save cube data). 	database
	public final boolean save(DatabaseInfo database) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(SAVE_PREFIX);
		query.append(database.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);	
	}
	
//	/database/unload 	Unloads the database, dimension and cube data from memory. 	database
	public final boolean unload(DatabaseInfo database) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(UNLOAD_PREFIX);
		query.append(database.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);	
	}
	
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final CubeInfo[] getCubesInternal(DatabaseInfo database,
			boolean getAll, int typeMask) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(CUBES_PREFIX);
		query.append(database.getId());
		if (!getAll) {
			query.append(getTypeString(typeMask));
		} else {
			query.append(SHOW_SYSTEM);
			query.append(SHOW_NORMAL);
			query.append(SHOW_ATTRIBUTE);
			query.append(SHOW_USER_INFO);
		}
		String[][] response = request(query.toString());
		CubeInfo[] cubes = new CubeInfo[response.length];
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder(); 
		for (int i = 0; i < cubes.length; ++i) {
			cubes[i] = cubeBuilder.create(database, response[i]);
		}
		return cubes;
	}
	
	private final DimensionInfo[] getDimensionsInternal(DatabaseInfo database,
			boolean getAll, int typeMask) throws IOException {
		if(database.getStatus() == DatabaseInfo.STATUS_UNLOADED) {
			System.err.println("WARNING: database '"+database.getName()+"' not loaded!!");
//			if(load(database))
//				System.err.println("\tsuccess!!");
		}
		StringBuffer query = new StringBuffer();
		query.append(DIMENSIONS_PREFIX);
		query.append(database.getId());
		if (!getAll) {
			query.append(getTypeString(typeMask));
		} else {
			query.append(SHOW_SYSTEM);
			query.append(SHOW_NORMAL);
			query.append(SHOW_ATTRIBUTE);
			query.append(SHOW_USER_INFO);
		}
		String[][] response = request(query.toString());
		DimensionInfo[] dimensions = new DimensionInfo[response.length];
		DimensionInfoBuilder dimensionBuilder = 
					builderReg.getDimensionBuilder();
		for (int i = 0; i < response.length; ++i) {
			dimensions[i] = dimensionBuilder.create(database, response[i]);
		}

		return dimensions;
	}
	
	private final String getTypeString(int typeMask) {
		StringBuffer showTypes = new StringBuffer();
		//TODO currently we know the constants from checking src code - CHANGE!!
		showTypes.append(hasType(1<<3, typeMask) ? SHOW_ATTRIBUTE : HIDE_ATTRIBUTE);
		showTypes.append(hasType(1<<4, typeMask) ? SHOW_USER_INFO: HIDE_USER_INFO);
		showTypes.append(hasType(1<<1, typeMask) ? SHOW_NORMAL : HIDE_NORMAL);
		showTypes.append(hasType(1<<2, typeMask) ? SHOW_SYSTEM: HIDE_SYSTEM);
		return showTypes.toString();
	}
	
	private final boolean hasType(int type, int typeMask) {
		return (typeMask & type) > 0;
	}
}
