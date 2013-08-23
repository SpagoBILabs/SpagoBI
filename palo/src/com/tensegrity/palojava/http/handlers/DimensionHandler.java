/*
*
* @file DimensionHandler.java
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
* @version $Id: DimensionHandler.java,v 1.9 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava.http.handlers;

import java.io.IOException;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.builders.CubeInfoBuilder;
import com.tensegrity.palojava.http.builders.DimensionInfoBuilder;
import com.tensegrity.palojava.http.builders.ElementInfoBuilder;
import com.tensegrity.palojava.http.builders.InfoBuilderRegistry;
import com.tensegrity.palojava.impl.DimensionInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * <code>HttpHandler</code> for requests regarding dimensions.
 * 
 * @author ArndHouben
 * @version $Id: DimensionHandler.java,v 1.9 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class DimensionHandler extends HttpHandler {
	
	//have to add database id and dimension id (id&idimension=id) to prefix	
	private static final String CLEAR_PREFIX = "/dimension/clear?database=";
	//have to add database id and dimension name (id&ndimension=a+new+dim) to prefix	
	private static final String CREATE_PREFIX = "/dimension/create?database=";
	//have to add database and dimension id, e.g.: /dimension/cubes?idatabase=0&idimension=0
	private static final String CUBES_PREFIX = "/dimension/cubes?database=";
	//have to add database and dimension id, e.g.: /dimension/delete?idatabase=0&idimension=10
	private static final String DELETE_PREFIX = "/dimension/destroy?database=";
	//have to add database and dimension id and the element position, e.g.:	
	// /dimension/element?idatabase=0&idimension=0&position=0
	private static final String ELEMENT_PREFIX = "/dimension/element?database=";
	//have to add database and dimension id, e.g.:/dimension/elements?idatabase=0&idimension=0
	private static final String ELEMENTS_PREFIX = "/dimension/elements?database=";
	//have to add database and dimension id, e.g.:/dimension/info?idatabase=0&idimension=0
	private static final String INFO_PREFIX = "/dimension/info?database=";	
	//have to add database and dimension id as well as the new dimension name, 
	//e.g.:	/dimension/rename?idatabase=0&idimension=0&new_name=olap4711
	private static final String RENAME_PREFIX = "/dimension/rename?database=";

//	private static final String SHOW_ATTRIBUTE = "&show_attribute=1";


	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final DimensionHandler instance = new DimensionHandler();
	static final DimensionHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final InfoBuilderRegistry builderReg;
	private DimensionHandler() {
		builderReg = InfoBuilderRegistry.getInstance();
	}

//	/dimension/clear  	Clears a dimension.  	dimension
	public final DimensionInfo clear(DimensionInfo dimension) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(CLEAR_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		String[][] response = request(query.toString());
		if (response[0].length < 11) {
			String[] _response = new String[11];
			System.arraycopy(response, 0, _response, 0, 7);
			_response[7] = dimension.getAttributeDimension();
			_response[8] = dimension.getAttributeCube();
			_response[9] = dimension.getRightsCube();
			_response[10] = Integer.toString(dimension.getToken());
			response[0] = _response;
		}
		DimensionInfoBuilder dimensionBuilder = 
					builderReg.getDimensionBuilder();
		return dimensionBuilder.create(database, response[0]);
	}
	
//	/dimension/create 	Creates a new dimension. 	database
	public final DimensionInfo create(DatabaseInfo database, String name)
			throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(database.getId());
		query.append("&new_name=");
		query.append(encode(name));
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = 
				builderReg.getDimensionBuilder();
		return dimensionBuilder.create(database, response[0]);
	}

//  /dimension/create    Creates a new dimension with a specified type. database	
	public final DimensionInfo create(DatabaseInfo database, String name,
			int type) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(database.getId());
		query.append("&new_name=");
		query.append(encode(name));
		query.append("&type=");
		query.append(type);
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = builderReg
				.getDimensionBuilder();
		return dimensionBuilder.create(database, response[0]);
	}

//	/dimension/cubes 	Shows the list cubes using a dimension 	dimension
	public final CubeInfo[] getCubes(DimensionInfo dimension) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(CUBES_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		String[][] response = request(query.toString());
		CubeInfo[] cubes = new CubeInfo[response.length];
		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
		for (int i = 0; i < cubes.length; ++i) {
			cubes[i] = cubeBuilder.create(database,response[i]);
		}
		return cubes;
	}
//	public final CubeInfo[] getCubes(DimensionInfo dimension, int type) throws IOException {
//		DatabaseInfo database = dimension.getDatabase();
//		StringBuffer query = new StringBuffer();
//		query.append(CUBES_PREFIX);
//		query.append(database.getId());
//		query.append("&dimension=");
//		query.append(dimension.getId());
//		//which types to show...
//		if(PaloUtils.isNormal(type))
//			query.append("&show_normal=1");
//		else
//			query.append("&show_normal=0");
//		if(PaloUtils.isSystem(type))
//			query.append("&show_system=1");
//		else
//			query.append("&show_system=0");
//		if(PaloUtils.isAttribute(type))
//			query.append("&show_attribute=1");
//		else
//			query.append("&show_attribute=0");
//		if(PaloUtils.isInfo(type))
//			query.append("&show_info=1");
//		else
//			query.append("&show_info=0");
//
//		String[][] response = request(query.toString());
//		CubeInfo[] cubes = new CubeInfo[response.length];
//		CubeInfoBuilder cubeBuilder = builderReg.getCubeBuilder();
//		for (int i = 0; i < cubes.length; ++i) {
//			cubes[i] = cubeBuilder.create(database,response[0]);
//		}
//		return cubes;
//	}
	
//	/dimension/destroy 	Deletes a dimension. 	database
	public final boolean delete(DimensionInfo dimension) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer req = new StringBuffer();
		req.append(DELETE_PREFIX);
		req.append(database.getId());
		req.append("&dimension=");
		req.append(dimension.getId());
		String[][] response = request(req.toString());
		return response[0][0].equals(OK);
	}
	
//	/dimension/element 	Shows one element at a given position 	dimension
	public final ElementInfo getElementAt(DimensionInfo dimension, int position)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(ELEMENT_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&position=");
		query.append(position);
		String[][] response = request(query.toString());		
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		return elementBuilder.create(dimension, response[0]);
	}
	
//	/dimension/elements 	Shows the list of elements. The list contains identifer, name, position, level, depth, parents and children of the elements. 	dimension
	public final ElementInfo[] getElements(DimensionInfo dimension)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(ELEMENTS_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		String[][] response = request(query.toString());
		ElementInfo[] elements = new ElementInfo[response.length];
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		for (int i = 0; i < elements.length; ++i) {
			elements[i] = elementBuilder.create(dimension, response[i]);
		}

		return elements;
	}
	
//	/dimension/info 	Shows identifier, name, maximum level, maximum depth and number of elements of a dimension. 	dimension
	public final DimensionInfo getInfo(DatabaseInfo database, String id)
			throws IOException {
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(id);
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = 
				builderReg.getDimensionBuilder();
		return dimensionBuilder.create(database, response[0]);
	}
	
//	/dimension/rename 	Renames a dimension. 	dimension
	public final void rename(DimensionInfo dimension, String newName)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(RENAME_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&new_name=");
		query.append(encode(newName));
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = 
			builderReg.getDimensionBuilder();
		dimensionBuilder.update((DimensionInfoImpl)dimension, response[0]);
	}

	public final DimensionInfo reload(DimensionInfo dimension)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = builderReg
				.getDimensionBuilder();
		dimensionBuilder.update((DimensionInfoImpl)dimension, response[0]);
		return dimension;
	}

	public final DimensionInfo getAttributeDimension(DimensionInfo dimension)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getAttributeDimension());
		String[][] response = request(query.toString());
		DimensionInfoBuilder dimensionBuilder = builderReg
				.getDimensionBuilder();
		return dimensionBuilder.create(database, response[0]);
	}
}
