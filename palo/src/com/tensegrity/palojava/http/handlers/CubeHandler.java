/*
*
* @file CubeHandler.java
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
* @version $Id: CubeHandler.java,v 1.10 2010/02/22 11:38:54 PhilippBouillon Exp $
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
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.LockInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.builders.CubeInfoBuilder;
import com.tensegrity.palojava.http.builders.InfoBuilderRegistry;
import com.tensegrity.palojava.http.builders.LockInfoBuilder;
import com.tensegrity.palojava.http.builders.RuleInfoBuilder;
import com.tensegrity.palojava.impl.CubeInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: CubeHandler.java,v 1.10 2010/02/22 11:38:54 PhilippBouillon Exp $
 */
public class CubeHandler extends HttpHandler {
	
	//have to add database, cube id and the area definition 
	//e.g.: /cube/clear?idatabase=0&icube=0&area=0:1,1:2,2:3,3:4,0,0
	private static final String CLEAR_AREA_PREFIX = "/cube/clear?database=";
	//have to add database id, the new cube name and the dimension ids
	//e.g.: /cube/create?idatabase=0&new_name=new+cube&idimensions=0,1,2,3
	private static final String CREATE_PREFIX = "/cube/create?database=";
	//have to add database and cube id e.g.: /cube/delete?idatabase=0&icube=1
	private static final String DELETE_PREFIX = "/cube/destroy?database=";
	//have to add database and cube id e.g.: /cube/info?idatabase=0&icube=0
	private static final String INFO_PREFIX = "/cube/info?database=";
	//have to add database and cube id e.g.: /cube/load?idatabase=0&icube=0
	private static final String LOAD_PREFIX = "/cube/load?database=";
	//have to add database and cube id as well as the new cube name, e.g.:
	// /cube/rename?idatabase=0&icube=0&new_name=changed+name
	private static final String RENAME_PREFIX = "/cube/rename?database=";
	//have to add database and cube id e.g.: /cube/save?idatabase=0&icube=0
	private static final String SAVE_PREFIX = "/cube/save?database=";
	//have to add database and cube id e.g.: /cube/unload?idatabase=0&icube=0
	private static final String UNLOAD_PREFIX = "/cube/unload?database=";
	private static final String RULES_PREFIX = "/cube/rules?database=";
	
	private static final String LOCK_PREFIX = "/cube/lock?database=";
	private static final String LIST_LOCKS_PREFIX = "/cube/locks?database=";
	private static final String ROLLBACK_PREFIX = "/cube/rollback?database=";
	private static final String COMMIT_PREFIX = "/cube/commit?database=";
	private static final String CONVERT_PREFIX = "/cube/convert?database=";
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final CubeHandler instance = new CubeHandler();
	static final CubeHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final InfoBuilderRegistry builderReg;
	private CubeHandler() {		
		builderReg = InfoBuilderRegistry.getInstance();
	}
	
//	/cube/clear  	Clears a cube.  	cube	
//	area  	area  	Comma separate list of element identifier lists. An identifier list is seperated by colons. The area is the cartesian product.
//	complete 	boolean 	(Optional) If complete is "1" then the whole cube - regardless of the specified area - will be cleared. It is not necassary to even specify the parameter "area" in this case. Default is to use "area".
	public final CubeInfo clear(CubeInfo cube, ElementInfo[][] area, boolean complete)
			throws IOException {
		if (area == null || area.length == 0)
			complete = true;		
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(CLEAR_AREA_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		if (complete)
			query.append("&complete=1");
		else {
			String paths = getAreaString(area); //getPaths(area);
			query.append("&area=");
			query.append(paths);
		}
		String[][] response = request(query.toString());
		//returns the updated cube info:
		if(response[0].length < 7) {
			String[] _response = new String[8];
			System.arraycopy(response,0,_response,0,6);
			_response[7] = Integer.toString(cube.getType());
			_response[8] = Integer.toString(cube.getToken());
			response[0] = _response;
		}
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		return cubeBuilder.create(database, response[0]);

	}

//	/cube/create 	Creates a new cube. 	database
	public final CubeInfo create(DatabaseInfo database, String name,
			DimensionInfo[] dimensions) throws IOException {
		String idStr = getIdString(dimensions);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(database.getId());
		query.append("&new_name=");
		query.append(encode(name));
		query.append("&dimensions=");
		query.append(idStr);
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		return cubeBuilder.create(database, response[0]);
	}

//	/cube/create 	Creates a new cube. 	database
	public final CubeInfo create(DatabaseInfo database, String name,
			DimensionInfo[] dimensions, int type) throws IOException {
		String idStr = getIdString(dimensions);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(database.getId());
		query.append("&new_name=");
		query.append(encode(name));
		query.append("&dimensions=");
		query.append(idStr);
		query.append("&type=");
		query.append(type);				
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		return cubeBuilder.create(database, response[0]);
	}
	
//	/cube/destroy 	Deletes a cube. 	database
	public final boolean destroy(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer request = new StringBuffer();
		request.append(DELETE_PREFIX);
		request.append(database.getId());
		request.append(CUBE_PREFIX);
		request.append(cube.getId());
		String[][] response = request(request.toString());
		return response[0][0].equals(OK);
	}

	public final int convert(CubeInfo cube, int type) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer request = new StringBuffer();
		request.append(CONVERT_PREFIX);
		request.append(database.getId());
		request.append(CUBE_PREFIX);
		request.append(cube.getId());
		request.append("&type=");
		request.append(type);
		String [][] response = request(request.toString());
		if (response[0].length < 9) {
			throw new PaloException("Not enough information to create cube info data.");
		}		
		return Integer.parseInt(response[0][7]);
	}
	
//	/cube/info 	Show identifier, name and dimension identifiers of a cube. 	cube
	public final CubeInfo getInfo(DatabaseInfo database, String cubeId)
			throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cubeId);
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		return cubeBuilder.create(database, response[0]);
	}
	
	public final RuleInfo[] getRules(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(RULES_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());
		RuleInfo[] rules = new RuleInfo[response.length];
		RuleInfoBuilder ruleBuilder = builderReg.getRuleBuilder();
		for(int i=0;i<response.length;++i) {
			rules[i] = ruleBuilder.create(cube, response[i]);
		}
		return rules;
/*		
		if(response.length>0) {
			//WORKAROUND FOR PALO SERVER PROBLEM!!!
//			ArrayList rules = new ArrayList();
//			RuleInfoBuilder ruleBuilder = builderReg.getRuleBuilder(); 
//			String[] ruleResponse = new String[2];
//			for (int i = 0; i < response[0].length; ++i) {
//				String[] splitted = splitRuleResponse(response[0][i]);
//				if(splitted.length == 1) {
//					if(i == response[0].length-1) {
//						ruleResponse[1] = splitted[0];
//						rules.add(ruleBuilder.create(cube, ruleResponse));
//					}						
//					ruleResponse[0] = splitted[0];
//				} else if(splitted.length > 1) {
//					ruleResponse[1] = splitted[0]; 
//					rules.add(ruleBuilder.create(cube, ruleResponse));
//					ruleResponse[0] = splitted[1];
//				}
//			}
//			return (RuleInfo[]) rules.toArray(new RuleInfo[rules.size()]);			
		}
		return new RuleInfo[0];
*/		
	}
	
	
//	/cube/load 	Loads cube data. 	cube	
	public final boolean load(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(LOAD_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);	
	}

	public final CubeInfo reload(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		cubeBuilder.update((CubeInfoImpl)cube, response[0]);
		return cube;
	}

//	/cube/rename 	Renames a cube. 	cube
	public final void rename(CubeInfo cube, String newName) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(RENAME_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&new_name=");
		query.append(newName);
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		cubeBuilder.update((CubeInfoImpl)cube, response[0]);
	}

//	/cube/save 	Saves cube data. 	cube
	public final boolean save(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(SAVE_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);		
	}
	
//	/cube/unload 	Unloads cube data from memory. 	cube
	public final boolean unload(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(UNLOAD_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);		
	}
	
	public final CubeInfo getAttributeCube(DimensionInfo dimension) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(dimension.getAttributeCube());
		String[][] response = request(query.toString());
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		return cubeBuilder.create(database, response[0]);
	}
	
//	private static final String LOCK_PREFIX = "/cube/lock?database=";
	public final LockInfo requestLock(CubeInfo cube, ElementInfo[][] coordinates)
			throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(LOCK_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		// the area...
		query.append("&area=");
		query.append(getAreaString(coordinates));
		String[][] response = request(query.toString());

		LockInfoBuilder lockBuilder = builderReg.getLockBuilder();
		return lockBuilder.create(cube, response[0]);
	}
	
//	private static final String LOCKS_PREFIX = "/cube/locks?database=";
	public final LockInfo[] listLocks(CubeInfo cube) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(LIST_LOCKS_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		String[][] response = request(query.toString());

		LockInfo[] locks = new LockInfo[response.length];
		LockInfoBuilder lockBuilder = builderReg.getLockBuilder(); 
		for (int i = 0; i < locks.length; ++i) {
			locks[i] = lockBuilder.create(cube, response[i]);
		}
		return locks;
	}
	
//	private static final String ROLLBACK_PREFIX = "/cube/rollback?database=";	
	public final boolean rollback(CubeInfo cube, LockInfo lock, int steps) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(ROLLBACK_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&lock=");
		query.append(lock.getId());
		if(steps>-1) {
			query.append("&steps=");
			query.append(steps);
		}			
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);		
	}
	
	public final boolean commit(CubeInfo cube, LockInfo lock) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(COMMIT_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&lock=");
		query.append(lock.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);		
	}
	
//	private final String[] splitRuleResponse(String ruleResponse) {
//		return ruleResponse.split("\n");
//	}
//	
//	private final String getAreaString(ElementInfo[][] area) {
//		StringBuffer str = new StringBuffer();
//		int coordsMax = area.length - 1;
//		for(int i=0;i<area.length;i++) {
//			int idMax = area[i].length - 1;
//			for(int j=0;j<area[i].length;j++) {
//				str.append(area[i][j].getId());
//				if(j<idMax)
//					str.append(":");
//			}
//			if(i<coordsMax)
//				str.append(",");
//		}
//		return str.toString();
//	}

	private final String getAreaString(ElementInfo[][] area) {
		StringBuffer ids = new StringBuffer();
		int lastCoordinate = area.length-1;
		for(int i=0;i<area.length;++i) {
			ids.append(getIdString(area[i],":"));			
			if(i<lastCoordinate)
				ids.append(",");
		}
		return ids.toString();
	}
}
