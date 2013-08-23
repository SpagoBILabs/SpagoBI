/*
*
* @file RightsImpl.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: RightsImpl.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import java.util.LinkedHashSet;

import org.palo.api.Cell;
import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.PaloObject;
import org.palo.api.Rights;

import com.tensegrity.palojava.ElementInfo;

public class RightsImpl implements Rights {
	//the default system database:
	public static final String SYSTEM_DATABASE = "System"; //$NON-NLS-1$
	
	//the default dimensions:
	/** this indicates a dimension which contains user name elements */
	public static final String DIMENSION_USER = "#_USER_"; //$NON-NLS-1$
	/** 
	 * this indicates a dimension which contains the password, expire date and 
	 * the must change elements 
	 **/
	public static final String DIMENSION_USER_PROPERTIES = "#_USER_PROPERTIES_"; //$NON-NLS-1$
	/** this indicates a dimension which contains user name elements */
	public static final String DIMENSION_GROUP = "#_GROUP_"; //$NON-NLS-1$
	/** this indicates a dimension which contains user name elements */
	public static final String DIMENSION_ROLE = "#_ROLE_"; //$NON-NLS-1$
	
	public static final String DIMENSION_RIGHT_OBJECT = "#_RIGHT_OBJECT_"; //$NON-NLS-1$
	
	//the default cubes:
	/** 
	 * cube with the user and the user_properties dimensions. use this to set
	 * and read user password
	 */
	public static final String CUBE_USER_USER_PROPERTIES = "#_USER_USER_PROPERTIES"; //$NON-NLS-1$
	/**
	 * Cube with the user and the group dimensions. use this to add users to 
	 * groups or to check if the user belongs to a special group (cell value
	 * should be 1 to indicate OK, everything else is interpreted as false)
	 */
	public static final String CUBE_USER_GROUP = "#_USER_GROUP"; //$NON-NLS-1$
	
	public static final String CUBE_ROLE_RIGHT_OBJECT = "#_ROLE_RIGHT_OBJECT"; //$NON-NLS-1$
	
	public static final String CUBE_GROUP_ROLE = "#_GROUP_ROLE"; //$NON-NLS-1$
	
	public static final String CUBE_GROUP_CUBE_DATA = "#_GROUP_CUBE_DATA"; //$NON-NLS-1$
	public static final String CUBE_GROUP_DIMENSION_DATA = "#_GROUP_DIMENSION_DATA_"; //$NON-NLS-1$
	public static final String CUBE_VIEW_GLOBAL = "#_VIEW_GLOBAL";
	public static final String CUBE_SUBSET_GLOBAL = "#_SUBSET_GLOBAL";
	
	private Database systemDb;
	private Cube rightsCube;
	private String [] roles;
	private String [] groups;
	private final ConnectionImpl connection;
	
	interface IRightsTraverser {
		boolean checkData(Object data);
	}
	
	RightsImpl(ConnectionImpl connection) {
		this.connection = connection;
	}
	
	private final void init() {
		Database [] sysDbs = connection.getSystemDatabases();
		LinkedHashSet <String> groupList = new LinkedHashSet<String>();
		LinkedHashSet <String> roleList = new LinkedHashSet<String>();
		
		if (sysDbs != null && sysDbs.length > 0) {
			systemDb = sysDbs[0];
			initializeGroupsAndRoles(connection, groupList, roleList);
			rightsCube = systemDb.getCubeByName(CUBE_ROLE_RIGHT_OBJECT);
		} else {
			systemDb = null;
			rightsCube = null;
		}
		groups = groupList.toArray(new String[0]);
		roles = roleList.toArray(new String[0]);			
	}
	
	public boolean mayDelete(PaloObject object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return
					data.toString().equals("S") || 
					data.toString().equals("D");
			}
		});
	}
	
	public boolean mayDelete(Class <? extends PaloObject> object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return
					data.toString().equals("S") || 
					data.toString().equals("D");
			}
		});		
	}

	public boolean mayRead(PaloObject object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return getElementForObjectType(object) != null;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S") || 
						data.toString().equals("D") ||
						data.toString().equals("W") || 
						data.toString().equals("R");
			}
		});
	}

	public boolean mayRead(Class <? extends PaloObject> object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return getElementForObjectClass(object) != null;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S") || 
						data.toString().equals("D") ||
						data.toString().equals("W") || 
						data.toString().equals("R");
			}
		});
	}

	public boolean maySplash(PaloObject object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S");
			}
		});
	}

	public boolean maySplash(Class <? extends PaloObject> object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S");
			}
		});
	}

	public boolean mayWrite(PaloObject object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		final boolean ignoreW;
		
		if (object instanceof Cube) {
			if (((Cube) object).getName().equals(CUBE_VIEW_GLOBAL) ||
				((Cube) object).getName().equals(CUBE_SUBSET_GLOBAL)) {
				ignoreW = true;
			} else {
				ignoreW = false;
			}
		} else {
			ignoreW = false;
		}
		
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S") || 
						data.toString().equals("D") ||
						(data.toString().equals("W") && !ignoreW);
			}
		});
	}

	public boolean mayWrite(Class <? extends PaloObject> object) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return false;
		}
		return traverseRights(object, new IRightsTraverser(){
			public boolean checkData(Object data) {
				return 	data.toString().equals("S") || 
						data.toString().equals("D") ||
						data.toString().equals("W");
			}
		});
	}
	
	public void allowSplash(String group, PaloObject object) {				
		setRight(group, object, "S");
	}
	
	public void allowDelete(String group, PaloObject object) {		
		setRight(group, object, "D");
	}
	
	public void allowWrite(String group, PaloObject object) {	
		if (object instanceof Cube) {
			if (((Cube) object).getName().equals(CUBE_VIEW_GLOBAL) ||
					((Cube) object).getName().equals(CUBE_SUBSET_GLOBAL)) {
				setRight(group, object, "D");
				return;
			}
		}
		setRight(group, object, "W");
	}

	public void allowRead(String group, PaloObject object) {		
		setRight(group, object, "R");
	}
	
	public void preventAccess(String group, PaloObject object) {		
		setRight(group, object, "");
	}
	
	public void allowSplash(String role, Class <? extends PaloObject> object) {		
		setRight(role, object, "S");
	}
	
	public void allowDelete(String role, Class <? extends PaloObject> object) {	
		setRight(role, object, "D");
	}
	
	public void allowWrite(String role, Class <? extends PaloObject> object) {		
		setRight(role, object, "W");
	}
	
	public void allowRead(String role, Class <? extends PaloObject> object) {		
		setRight(role, object, "R");
	}
	
	public void preventAccess(String role, Class <? extends PaloObject> object) {		
		setRight(role, object, "");
	}
	
	private final Element getElementForObjectType(Object object) {
		Dimension d = rightsCube.getDimensionByName(DIMENSION_RIGHT_OBJECT);
		if (object instanceof Database) {
			return d.getElementByName("database");
		} else if (object instanceof Cube) {
			return d.getElementByName("cube");
		} else if (object instanceof Dimension) {
			return d.getElementByName("dimension");
		} else if (object instanceof Element) {
			return d.getElementByName("dimension element");
		} else if (object instanceof Cell) {
			return d.getElementByName("cell data");
		}
		return null;		
	}
	
	private final Element getElementForObjectClass(Class <? extends PaloObject> object) {
		Dimension d = rightsCube.getDimensionByName(DIMENSION_RIGHT_OBJECT);
		String name = object.getSimpleName();
		if (name.equals("Database")) {
			return d.getElementByName("database");
		} else if (name.equals("Cube")) {
			return d.getElementByName("cube");
		} else if (name.equals("Dimension")) {
			return d.getElementByName("dimension");
		} else if (name.equals("Element")) {
			return d.getElementByName("dimension element");
		} else if (name.equals("Cell")) {
			return d.getElementByName("cell data");
		}
		return null;				
	}
	
	private final Database getDatabaseForObject(PaloObject object) {
		if (object instanceof Cube) {
			return ((Cube) object).getDatabase();
		} else if (object instanceof Element) {
			return ((Element) object).getDimension().getDatabase();
		}
		return null;
	}
	
	private final void setRight(String role, Class <? extends PaloObject> object, String right) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return;
		}
		init();
		if (!mayWrite(rightsCube)) {
			return;
		}
		if (systemDb == null || rightsCube == null) {
			return;
		}
		Element objectElement = getElementForObjectClass(object);
		if (objectElement == null) {
			return;
		}
		if (right.equals("S") && !(object.getSimpleName().equals("Cell"))) {
			return;
		}
		Element roleElement = rightsCube.getDimensionAt(0).getElementByName(role);
		if (roleElement == null) {
			return;
		}
		rightsCube.setData(new Element [] {roleElement, objectElement}, right);
	}
	
	private final void setRight(String group, PaloObject object, String right) {
		if (connection.getType() == Connection.TYPE_XMLA) {
			return;
		}
		init();
		if (systemDb == null || rightsCube == null) {
			return;
		}
		if (!(object instanceof Cube) && !(object instanceof Element)) {
			return;
		}
		
		Database db = getDatabaseForObject(object);
		if (db == null) {
			return;
		}
		
		Cube cube;		
		if (object instanceof Cube) {
			cube = db.getCubeByName(CUBE_GROUP_CUBE_DATA);
		} else {
			Dimension d = ((Element) object).getDimension();
			cube = db.getCubeByName(CUBE_GROUP_DIMENSION_DATA + d.getName());
		}
		if (cube == null) {
			return;
		}
		Element objectElement = cube.getDimensionAt(1).
			getElementByName(object.getName()); 
		if (objectElement == null) {
			return;
		}
		Element groupElement = cube.getDimensionAt(0).getElementByName(group);
		if (groupElement == null) {
			return;
		}
		
		cube.setData(new Element [] {groupElement, objectElement}, right);
	}
	
	private final boolean traverseRights(Class <? extends PaloObject> object,
										 IRightsTraverser traverser) {
		init();
		if (systemDb == null || rightsCube == null) {
			return false;
		}
		Element objectElement = getElementForObjectClass(object);
		if (objectElement == null) {
			return false;
		}
		
		return performCheck(objectElement, traverser);
	}
	
	private final boolean traverseRights(PaloObject object, 
			                             IRightsTraverser traverser) {
		init();
		if (systemDb == null || rightsCube == null) {
			return false;
		}
		Element objectElement = getElementForObjectType(object);
		if (objectElement == null) {
			return false;
		}
		
		if (object instanceof Cube || object instanceof Element) {
			boolean [] result = checkCubeOrDimensionRight(object, traverser);
			if (result[0]) {
				return result[1];
			}
		}
		
		return performCheck(objectElement, traverser);
	}
	
	private final boolean performCheck(Element objectElement, 
									   IRightsTraverser traverser) { 
		for (String role: roles) {
			Element roleElement = rightsCube.getDimensionAt(0).
				getElementByName(role);
			Object data = rightsCube.getData(new Element [] {
					roleElement, objectElement});
			if (data != null) {
				if (traverser.checkData(data)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private final boolean [] checkCubeOrDimensionRight(PaloObject object,
													IRightsTraverser traverser) {
		Database db = getDatabaseForObject(object);
		if (db == null) {
			return new boolean [] {false, false};
		}
		Cube cube;
		
		if (object instanceof Cube) {
			cube = db.getCubeByName(CUBE_GROUP_CUBE_DATA);
		} else {
			Dimension d = ((Element) object).getDimension();
			cube = db.getCubeByName(CUBE_GROUP_DIMENSION_DATA + d.getName());
		}
		
		boolean restricted = cube != null;
		if (cube != null) {
			Element objectElement = cube.getDimensionAt(1).
				getElementByName(object.getName()); 
			if (objectElement == null) {
				return new boolean [] {false, false};
			}
			
			for (String group: groups) {
				Element groupElement = 
					cube.getDimensionAt(0).getElementByName(group);
				Object data = cube.getData(new Element [] {
						groupElement, objectElement});
				if (data != null) {
					if (data.toString().trim().length() != 0) {
						String result = data.toString().trim();
						if (!result.equals("S") && !result.equals("D") &&
								!result.equals("W") && !result.equals("R") &&
								!result.equals("N")) {
							restricted = false;
						}
					} else {
						restricted = false;
					}
					if (traverser.checkData(data)) {						
						return new boolean [] {true, true};
					}
				} else {
					restricted = false;
				}
			}
		}
		return new boolean [] {restricted, false};
	}
	
	private final void initializeGroupsAndRoles(
				ConnectionImpl connection, 
				LinkedHashSet <String> groupList,
				LinkedHashSet <String> roleList) {
		Cube cube = systemDb.getCubeByName(CUBE_USER_GROUP);
		if (cube != null) {
			Element userElement = cube.getDimensionAt(0).
									getElementByName(connection.getUsername());
			Element [] groupElements = cube.getDimensionAt(1).getElements(); 
			Object [] groupResult = cube.getDataArray(
					new Element [][] {{userElement}, groupElements});
			
			for (int i = 0, n = groupResult.length; i < n; i++) {
				if (groupResult[i] != null && 
				    groupResult[i].toString().equals("1")) {
					groupList.add(groupElements[i].getName());
				}
			}
			if (groupList.size() > 0) {
				initializeRoles(groupList, roleList);
			}
		}
	}
	
	private final void initializeRoles(LinkedHashSet <String> groupList,
									   LinkedHashSet <String> roleList) {
		Cube cube = systemDb.getCubeByName(CUBE_GROUP_ROLE);
		if (cube != null) {
			Element [] groupElements = new Element[groupList.size()];
			Dimension groupDimension = cube.getDimensionAt(0);
			int groupElementSize = 0;
			for (String elId: groupList) {
				groupElements[groupElementSize++] = 
					groupDimension.getElementByName(elId);
			}
			Element [] roleElements = cube.getDimensionAt(1).getElements();
			Object [] groupRoleResults = cube.getDataArray(
					new Element [][] {groupElements, roleElements});
			for (int i = 0, n = groupRoleResults.length; i < n; i++) {
				if (groupRoleResults[i] != null && 
					groupRoleResults[i].toString().equals("1")) {
					roleList.add(roleElements[i / groupElementSize].getName());
				}
			}
		}		
	}
}
