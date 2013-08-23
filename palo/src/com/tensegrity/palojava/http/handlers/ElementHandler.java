/*
*
* @file ElementHandler.java
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
* @version $Id: ElementHandler.java,v 1.12 2010/01/12 14:37:03 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava.http.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.ServerInfo;
import com.tensegrity.palojava.http.HttpConnection;
import com.tensegrity.palojava.http.builders.ElementInfoBuilder;
import com.tensegrity.palojava.http.builders.InfoBuilderRegistry;
import com.tensegrity.palojava.impl.ElementInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: ElementHandler.java,v 1.12 2010/01/12 14:37:03 PhilippBouillon Exp $
 */
public class ElementHandler extends HttpHandler {

	//have to add the database, dimension and element ids as well as children
	//ids and their weights, e.g.: /element/append?idatabase=0&idimension=0&ielement=29&ichildren=6,7,8&weights=0.1,0.5,0.4
	private static final String APPEND_PREFIX = "/element/append?database=";
	//have to add the database, dimension and element ids, e.g.: /element/delete?idatabase=0&idimension=0&ielement=112
	private static final String DELETE_PREFIX= "/element/destroy?database=";
	private static final String DELETE_BULK_PREFIX= "/element/destroy_bulk?database=";
	//have to add the database and dimension ids, the new element name, its type
	//the ids of its children and their corresponding weight, e.g.:
	//element/create?idatabase=0&idimension=0&new_name=new+element&type=4&ichildren=1,2,3&weights=0.1,0.5,0.4
	private static final String CREATE_PREFIX = "/element/create?database=";	
	private static final String CREATE_BULK_PREFIX = "/element/create_bulk?database=";
	//have to add the database, dimension and element ids, e.g.: /element/info?idatabase=0&idimension=0&ielement=0
	private static final String INFO_PREFIX = "/element/info?database=";	
	//have to add the database, dimension and element ids as well as the new
	//position, e.g.:/element/move?idatabase=0&idimension=0&ielement=4&position=2
	private static final String MOVE_PREFIX = "/element/move?database=";	
	//have to add the database, dimension and element ids as well as the new
	//name, e.g.: /element/rename?idatabase=0&idimension=0&ielement=29&new_name=week
	private static final String RENAME_PREFIX = "/element/rename?database=";	
	//have to add the database, dimension and element ids
	///element/replace?idatabase=0&idimension=0&new_name=new+element&type=4&ichildren=6,7,8&weight=0.1,0.5,0.4
	//OR 
	///element/replace?idatabase=0&idimension=0&ielement=30&type=4&ichildren=6,7,8&weight=0.1,0.5,0.4
	private static final String REPLACE_PREFIX = "/element/replace?database=";
	private static final String REPLACE_BULK_PREFIX = "/element/replace_bulk?database=";

	
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final ElementHandler instance = new ElementHandler();
	static final ElementHandler getInstance(HttpConnection connection) {
		instance.use(connection);
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final InfoBuilderRegistry builderReg;
	private ElementHandler() {
		builderReg = InfoBuilderRegistry.getInstance();
	}

//	/element/append  	Adds children to consolidated elements.  	dimension
	public final ElementInfo append(ElementInfo element, ElementInfo[] children,
			double[] weights) throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();
		String childrenStr = getIdString(children);
		String weightStr = getWeightString(weights);

		StringBuffer query = new StringBuffer();
		query.append(APPEND_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
		query.append("&children=");
		query.append(childrenStr);
		query.append("&weights=");
		query.append(weightStr);
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		elementBuilder.update((ElementInfoImpl)element, response[0]);
		return element;
	}
	
//	/element/create 	Creates new element. 	dimension
	public final ElementInfo create(DimensionInfo dimension, String name, int type,
			ElementInfo[] children, double[] weights) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		String childrenStr = getIdString(children);
		String weightStr = getWeightString(weights);
		// TODO adjust type:
		// type = getNewType(type);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		// name = name.replaceAll("\\s","+");
		query.append("&new_name=");
		query.append(encode(name));
		query.append("&type=");
		query.append(type);
		query.append("&children=");
		query.append(childrenStr);
		query.append("&weights=");
		query.append(weightStr);
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		return elementBuilder.create(dimension, response[0]);
	}

	public final boolean createBulk(DimensionInfo dimension, ElementInfo[] names, int type,
			ElementInfo[][] children, double[][] weights) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		String parents = getParentString(names);		
		String childrenStr = getChildrenString(children);
		String weightStr = getWeightsString(weights);
		// TODO adjust type: -- shouldn't be necessary, since types are equal.
		// type = getNewType(type);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_BULK_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		// name = name.replaceAll("\\s","+");
		query.append("&name_elements=");
		query.append(encode(parents));
		query.append("&type=");
		query.append(type);
		if (children.length != 0) {
			query.append("&children=");
			query.append(childrenStr);
		}
		if (weights.length != 0) {
			query.append("&weights=");
			query.append(weightStr);
		}
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
	
	public final boolean createBulk(DimensionInfo dimension, String[] names, int type,
			ElementInfo[][] children, double[][] weights) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer par = new StringBuffer();
		for (int i = 0, n = names.length; i < n; i++) {
			String val = names[i];
			
			val = val.replaceAll("\"", "\"\"");
			val = encode(val);
			par.append("%22"); // <=> "
			par.append(val);
			par.append("%22"); // <=> "
			
//			val = val.replaceAll("\"", "\"\"");
//			val = encode(val);
			//par.append("\"");
			//val = encode(val);
			//par.append(val);
			//par.append("\"");
			//par.append(val);
			if (i < (n - 1)) {
				par.append("%2C"); // ,
			}
		}
		String parents = par.toString(); //encode(names, ','); //getIdString(names);		
		// TODO adjust type: -- shouldn't be necessary, since types are equal.
		//type = getNewType(type);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_BULK_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		// name = name.replaceAll("\\s","+");
		query.append("&name_elements=");
		query.append(parents);
		query.append("&type=");
		query.append(type);
		if (children.length != 0) {
			String childrenStr = getChildrenString(children);
			query.append("&children=");
			query.append(childrenStr);
		} 
		if (weights.length != 0) {
			String weightStr = getWeightsString(weights);		
			query.append("&weights=");
			query.append(weightStr);
		}

		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
//		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
//		return elementBuilder.create(dimension, response[0]);
	}

	private final void verifyParameters(String [] names, int [] types, ElementInfo [][] children, double [][] weights) {
		HashSet <String> created = new HashSet<String>();
		ArrayList <String> newNames = new ArrayList<String>();
		ArrayList <Integer> newTypes = new ArrayList<Integer>();
		ArrayList <ElementInfo []> newChildren = new ArrayList<ElementInfo []>();
		ArrayList <Double []> newWeights = new ArrayList<Double []>(); 
		
		for (int i = 0, n = names.length; i < n; i++) {
			String e = names[i];
			String s = e.toLowerCase();
			if (!created.contains(s)) {
				created.add(s);
			}
		}
	}
	
	public final boolean createBulk(DimensionInfo dimension, String[] names, int [] types,
			ElementInfo[][] children, double[][] weights) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer par = new StringBuffer();
		for (int i = 0, n = names.length; i < n; i++) {
			String val = names[i];
			
			val = val.replaceAll("\"", "\"\"");
			val = encode(val);
			par.append("%22"); // <=> "
			par.append(val);
			par.append("%22"); // <=> "			
			if (i < (n - 1)) {
				par.append("%2C"); // ,
			}
		}
		String parents = par.toString(); //encode(names, ','); //getIdString(names);		
		// TODO adjust type: -- shouldn't be necessary, since types are equal.
		//type = getNewType(type);
		verifyParameters(names, types, children, weights);
		StringBuffer query = new StringBuffer();
		query.append(CREATE_BULK_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		// name = name.replaceAll("\\s","+");
		query.append("&name_elements=");
		query.append(parents);
		query.append("&types=");
		for (int i = 0, n = types.length; i < n; i++) {
			query.append(types[i]);
			if (i < (n - 1)) {
				query.append(",");
			}
		}
		if (children.length != 0) {
			String childrenStr = getChildrenNameString(children);			
			query.append("&name_children=");		
			query.append(childrenStr);
		} 
		if (weights.length != 0) {
			String weightStr = getWeightsString(weights);
			query.append("&weights=");
			query.append(weightStr);
		}

		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
//		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
//		return elementBuilder.create(dimension, response[0]);
	}

	public final boolean replaceBulk(DimensionInfo dimension, ElementInfo [] elements,
			int type, ElementInfo [][] children, Double [][] weights) throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(REPLACE_BULK_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&elements=");
		for (int i = 0, n = elements.length; i < n; i++) {
			query.append(elements[i].getId());
			if (i < (n - 1)) {
				query.append(",");
			}
		}
		query.append("&type=");
		query.append(type);
		if (children != null && children.length != 0) {
			String childrenStr = getChildrenString(children);			
			query.append("&children=");		
			query.append(childrenStr);
		} 
		if (weights != null && weights.length != 0) {
			String weightStr = getWeightsString(weights);
			query.append("&weights=");
			query.append(weightStr);
		}
		
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
	
//	/element/destroy 	Deletes an element. 	dimension
	public final boolean destroy(ElementInfo element) throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(DELETE_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}
	
//TODO we wait for jedox:    	
	public final boolean destroy(ElementInfo[] elements) throws IOException {
		if(elements.length < 1)
			return true;
		DimensionInfo dimension = elements[0].getDimension();
		DatabaseInfo database = dimension.getDatabase();
		String elementIDs = getIdString(elements);
		StringBuffer query = new StringBuffer();
		query.append(DELETE_BULK_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&elements=");
		query.append(elementIDs);
		String[][] response = request(query.toString());
		return response[0][0].equals(OK);
	}

	
//	/element/info 	Shows identifer, name, position, level, depth, parents and children of an element. 	dimension
	public final ElementInfo getInfo(DimensionInfo dimension, String id)
			throws IOException {
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(id);
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		return elementBuilder.create(dimension, response[0]);
	}
	
//	/element/move 	Changes position of an element. 	dimension
	public final ElementInfo move(ElementInfo element, int newPosition)
			throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();

		StringBuffer query = new StringBuffer();
		query.append(MOVE_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
		query.append("&position=");
		query.append(newPosition);
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		elementBuilder.update((ElementInfoImpl)element, response[0]);
//		return elementBuilder.create(dimension, response[0]);
		return element;
	}
	
	public final ElementInfo reload(ElementInfo element) throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(INFO_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		elementBuilder.update((ElementInfoImpl)element, response[0]);
		return element;
	}
//	/element/rename 	Renames an element. 	dimension
	public final ElementInfo rename(ElementInfo element, String newName)
			throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();
		StringBuffer query = new StringBuffer();
		query.append(RENAME_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
		query.append("&new_name=");
		query.append(encode(newName));
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		elementBuilder.update((ElementInfoImpl)element, response[0]);
		return element;
	}
	
//	/element/replace 	Changes or creates a new element. Replaces children in consolidated elements. 	dimension
	public final void update(ElementInfo element, int type, String[] children, double[] weights, ServerInfo serverInfo) throws IOException {
		DimensionInfo dimension = element.getDimension();
		DatabaseInfo database = dimension.getDatabase();
		
		String childrenStr = getIdString(children);
		String weightStr = getWeightString(weights);
		StringBuffer query = new StringBuffer();
		query.append(REPLACE_PREFIX);
		query.append(database.getId());
		query.append("&dimension=");
		query.append(dimension.getId());
		query.append("&element=");
		query.append(element.getId());
//		query.append("&name_element=");
//		query.append(name);
		query.append("&type=");
		query.append(type);
		if (serverInfo.getMajor() < 3 || childrenStr.length() > 0) { 
			query.append("&children=");
			query.append(childrenStr);
		}
		if (serverInfo.getMajor() < 3 || weightStr.length() > 0) {
			query.append("&weights=");
			query.append(weightStr);
		}
		String[][] response = request(query.toString());
		ElementInfoBuilder elementBuilder = builderReg.getElementBuilder();
		elementBuilder.update((ElementInfoImpl)element, response[0]);
	}


	private final String getParentString(ElementInfo[] parents) {
		StringBuffer str = new StringBuffer();
		int lastParent = parents.length - 1;
		for(int i=0; i < lastParent; ++i) {
			str.append(parents[i].getName());
			str.append("_NEW");
			str.append(",");
		}
		//add last parent:
		str.append(parents[lastParent].getName());
		str.append("_NEW");
		return str.toString();
	}

	private final String getChildrenString(ElementInfo[][] children) {
		StringBuffer str = new StringBuffer();
		int lastChild = children.length - 1;
		for(int i = 0; i < lastChild; i++) {
			str.append(getIdString(children[i]));
			str.append(":");
		}
		//add last child:
		str.append(getIdString(children[lastChild]));
		return str.toString();
	}
	private final String getChildrenNameString(ElementInfo[][] children) {
		StringBuffer str = new StringBuffer();
		int lastChild = children.length - 1;
		for(int i = 0; i < lastChild; i++) {
			str.append(getNameString(children[i]));
			str.append(":");
		}
		//add last child:
		str.append(getNameString(children[lastChild]));
		return str.toString();
	}	
	private final String getWeightsString(double[][] weights) {
		StringBuffer str = new StringBuffer();
		int lastWeight = weights.length - 1;
		for(int i = 0; i < lastWeight; i++) {
			str.append(getWeightString(weights[i]));
			str.append(":");
		}
		//add last weight:
		str.append(getWeightString(weights[lastWeight]));
		return str.toString();
	}
	private final String getWeightsString(Double[][] weights) {
		StringBuffer str = new StringBuffer();
		int lastWeight = weights.length - 1;
		for(int i = 0; i < lastWeight; i++) {
			str.append(getWeightString(weights[i]));
			str.append(":");
		}
		//add last weight:
		str.append(getWeightString(weights[lastWeight]));
		return str.toString();
	}

}
