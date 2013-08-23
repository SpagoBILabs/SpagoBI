/*
*
* @file CellHandler.java
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
* @version $Id: CellHandler.java,v 1.15 2010/02/26 13:55:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava.http.handlers;

import java.io.IOException;

import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.ExportContextInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.builders.CellInfoBuilder;
import com.tensegrity.palojava.http.builders.InfoBuilderRegistry;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: CellHandler.java,v 1.15 2010/02/26 13:55:49 PhilippBouillon Exp $
 */
public class CellHandler extends HttpHandler {
	
	//have to add database and cube ids and define the area, e.g.:
	// /cell/area?idatabase=0&icube=0&area=0:1,1:2,2:3,3:4,0,0
	private static final String AREA_PREFIX = "/cell/area?database="; 	
	//have to add database and cube ids, e.g.:	/cell/export?idatabase=0&icube=0
	private static final String EXPORT_PREFIX = "/cell/export?database=";
	//have to add database and cube ids and define the path, i.e. the element
	//ids and the new value and the splash mode, e.g.:	
	// /cell/replace?idatabase=0&icube=0&path=1,1,1,1,1,1&value=123.00&splash=1
	private static final String REPLACE_PREFIX = "/cell/replace?database=";	
	//have to add database and cube ids and define the path, i.e. the element
	//ids and their new values and the splash mode, e.g.:
	// /cell/replace_bulk?idatabase=0&icube=0&paths=1,1,1,1,1,1:2,2,2,2,2,2&values=123.00:-1&splash=1
	private static final String REPLACE_BULK_PREFIX = "/cell/replace_bulk?database=";
	//have to add database and cube ids and define the path, i.e. the element
	//ids, e.g.: /cell/value?idatabase=0&icube=0&path=0,0,2,3,0,0
	private static final String VALUE_PREFIX = "/cell/value?database=";	
	//have to add database and cube ids and define the paths, i.e. the elements
	//ids, e.g.: /cell/values?idatabase=0&icube=0&paths=0,0,0,0,0,0:1,0,0,0,0,0
	private static final String VALUES_PREFIX = "/cell/values?database=";	
	
	private static final String BLOCKSIZE_PREFIX = "&blocksize=";
	private static final String TYPE_PREFIX = "&type=";
	private static final String CONDITION_PREFIX = "&condition=";
	private static final String BASE_ONLY_PREFIX = "&base_only=";
	private static final String SKIP_EMPTY_PREFIX = "&skip_empty=";
	private static final String USE_RULES_PREFIX = "&use_rules=";
	private static final String SHOW_RULE = "&show_rule=1";	
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final CellHandler instance = new CellHandler();
	static final CellHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final InfoBuilderRegistry builderReg;
	private CellHandler() {		
		builderReg = InfoBuilderRegistry.getInstance();
	}

//	/cell/area  	Shows datatype and value of an area of cube cells.  	cube
	public final CellInfo[] getCellArea(CubeInfo cube, ElementInfo[][] coordinates)
			throws IOException {
		DatabaseInfo database = cube.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(AREA_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&area=");
		query.append(getAreaIds(coordinates));
		query.append(SHOW_RULE);
		String[][] response = request(query.toString());
		CellInfo[] cells = new CellInfo[response.length];
		CellInfoBuilder cellBuilder = builderReg.getCellBuilder();
		for (int i = 0; i < response.length; ++i) {
			cells[i] = cellBuilder.create(null,response[i]);
		}

		return cells;
	}

	// /cell/copy Copies cells. cube
	public final boolean copyCell(CubeInfo cube, ElementInfo[] fromCoordinate,
			ElementInfo[] toCoordinate) {
		throw new PaloException("Currently not supported!!");
	}

	public final boolean copyCell(CubeInfo cube, ElementInfo[] fromCoordinate,
			ElementInfo[] targetCoordinate, Object targetValue) {
		throw new PaloException("Currently not supported!!");
	}

//	/cell/replace 	Sets value of a cube cell. 	cube
	public final boolean replaceValue(CubeInfo cube, ElementInfo[] coordinate,
			Object newValue, int splashMode) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		String path = getIdString(coordinate);
		if (splashMode == -1)
			splashMode = CellInfo.SPLASH_MODE_DISABLED;
		StringBuffer query = new StringBuffer();
		query.append(REPLACE_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&path=");
		query.append(path);
		query.append("&value=");
		query.append(encode(newValue.toString())); // ,true));
		query.append("&splash=");
		query.append(splashMode);
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
	
//	/cell/replace_bulk 	Sets values of cube cells. 	cube
	public final boolean replaceValues(CubeInfo cube, ElementInfo[][] coordinates,
			Object[] newValues, boolean add, int splashMode,
			boolean notifyProcessors) throws IOException {
		DatabaseInfo database = cube.getDatabase();
		String paths = getPaths(coordinates);
		if (splashMode == -1)
			splashMode = CellInfo.SPLASH_MODE_DISABLED; // default
		StringBuffer query = new StringBuffer();
		query.append(REPLACE_BULK_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&paths=");
		query.append(paths);
		query.append("&values=");
		query.append(encode(newValues));
		if(add)
			query.append("&add=1");
		else
			query.append("&add=0");
		query.append("&splash=");
		query.append(splashMode);
		if(notifyProcessors)
			query.append("&event_processor=1");
		else
			query.append("&event_processor=0");
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
		
	public final String getRule(CubeInfo cube, ElementInfo[] coordinate)
			throws IOException {
		StringBuffer path = new StringBuffer();
		int lastId = coordinate.length - 1;
		for (int i = 0; i < coordinate.length; ++i) {
			path.append(coordinate[i].getId());
			if (i < lastId)
				path.append(",");
		}
		StringBuffer query = new StringBuffer();
		query.append(VALUE_PREFIX);
		query.append(cube.getDatabase().getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&path=");
		query.append(path);
		query.append(SHOW_RULE);
		String[][] response = request(query.toString());
		if(response[0].length > 3)
			return response[0][3];
		return null;
	}

	/**
	 * Receives the cell value at the given coordinate. 
	 * @param cube the cube which contains the cell
	 * @param coordinate cell coordinate within the cube 
	 * @return the value of the cube cell
	 * @throws IOException if an I/O exception occurs
	 */
	public final CellInfo getValue(CubeInfo cube, ElementInfo[] coordinate) throws IOException {
		//the path or coordinate consist of element ids
		StringBuffer path = new StringBuffer();
		int lastId = coordinate.length-1;
		for(int i=0;i<coordinate.length;++i) {
			path.append(coordinate[i].getId());
			if(i<lastId)
				path.append(",");
		}
		StringBuffer query = new StringBuffer();		
		query.append(VALUE_PREFIX);	query.append(cube.getDatabase().getId());
		query.append(CUBE_PREFIX);		query.append(cube.getId());
		query.append("&path="); 	query.append(path);
		query.append(SHOW_RULE);
		String[][] response = request(query.toString());
		CellInfoBuilder cellBuilder = builderReg.getCellBuilder();
		CellInfo cell = cellBuilder.create(null, response[0]);
		return cell;
	}

	
//	/cell/values 	Shows datatype and value of a list of cube cells. 	cube
	public final CellInfo[] getValues(CubeInfo cube, ElementInfo[][] area)
			throws IOException {
		DatabaseInfo database = cube.getDatabase();
		String paths = getPaths(area);
		StringBuffer query = new StringBuffer();
		query.append(VALUES_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append("&paths=");
		query.append(paths);
		query.append(SHOW_RULE);
		String[][] response = request(query.toString());
		CellInfo[] cells = new CellInfo[response.length];
		CellInfoBuilder cellBuilder = builderReg.getCellBuilder();
		for (int i = 0; i < response.length; ++i) {
			cells[i] = cellBuilder.create(null,response[i]);
		}
		return cells;
	}

	
	public final CellInfo[] export(CubeInfo cube, ExportContextInfo context)
			throws IOException {
		DatabaseInfo database = cube.getDatabase();
		String[] dimIds = cube.getDimensions();
		StringBuffer query = new StringBuffer();
		query.append(EXPORT_PREFIX);
		query.append(database.getId());
		query.append(CUBE_PREFIX);
		query.append(cube.getId());
		query.append(BLOCKSIZE_PREFIX);
		query.append(context.getBlocksize());		
		query.append(TYPE_PREFIX);
		query.append(context.getType());
		// export condition:
		String condition = context.getConditionRepresentation();
		if (condition != null && condition.length() > 0) {
			query.append(CONDITION_PREFIX);
			query.append(encode(condition));
		}
		query.append(USE_RULES_PREFIX).append(context.useRules() ? "1" : "0");
		query.append(BASE_ONLY_PREFIX).append(
				context.isBaseCellsOnly() ? "1" : "0");
		query.append(SKIP_EMPTY_PREFIX).append(
				context.ignoreEmptyCells() ? "1" : "0");
		String[] startAfterPath = context.getExportAfter();
		if (startAfterPath != null && startAfterPath.length > 0) {
			query.append("&path=");
			int lastElement = dimIds.length - 1;
			for (int i = 0; i < dimIds.length; i++) {
				query.append(startAfterPath[i]);
				if (i < lastElement)
					query.append(",");
			}
		}
		query.append("&area=");
		StringBuffer elIDPaths = new StringBuffer(1000);
		String[][] area = context.getCellsArea();
		int lastPath = area.length - 1;
		for (int i = 0; i < area.length; i++) {
			int lastID = area[i].length - 1;
			for (int j = 0; j < area[i].length; j++) {
				elIDPaths.append(area[i][j]);
				if (j < lastID)
					elIDPaths.append(":");
			}
			if (i < lastPath)
				elIDPaths.append(",");
		}
		query.append(elIDPaths);
		String[][] response = request(query.toString());
		if (response.length == 1 && response[0].length == 3)
			throw new PaloException("getDataExport failed: " + response[0][0]
					+ ", " + response[0][1] + ", " + response[0][2]);
		int lastCell = response.length-1;
		CellInfo[] cells = new CellInfo[response.length-1];
		CellInfoBuilder cellBuilder = builderReg.getCellBuilder();
		for(int i=0;i<lastCell;++i) {
			cells[i] =cellBuilder.create(null, response[i]);
		}

		context.setProgress((Double
				.parseDouble(response[lastCell][0]))
				/ (Double.parseDouble(response[lastCell][1])));
		return cells;
	}
	
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final String getAreaIds(ElementInfo[][] coordinates) {
		StringBuffer ids = new StringBuffer();
		int lastCoordinate = coordinates.length-1;
		for(int i=0;i<coordinates.length;++i) {
			ids.append(getIdString(coordinates[i],":"));			
			if(i<lastCoordinate)
				ids.append(",");
		}
		return ids.toString();
	}
}
