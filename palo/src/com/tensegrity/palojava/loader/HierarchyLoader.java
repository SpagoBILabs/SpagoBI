/*
*
* @file HierarchyLoader.java
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
* @version $Id: HierarchyLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;

/**
 * </p><code>HierarchyInfoLoader</code></p>
 * This abstract base class manages the loading of {@link HierarchyInfo} objects.
 *
 * @author ArndHouben
 * @version $Id: HierarchyLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public abstract class HierarchyLoader extends PaloInfoLoader {

	protected final DimensionInfo dimension;
	
	/**
	 * Creates a new loader instance.
	 * @param paloConnection
	 * @param database
	 */
	public HierarchyLoader(DbConnection paloConnection, DimensionInfo dimension) {
		super(paloConnection);
		this.dimension = dimension;
	}
		
	/**
	 * Returns the number of hierarchies.
	 * @return the number of hierarchies
	 */
	public abstract int getHierarchyCount();
	
	/**
	 * Returns the identifiers of all hierarchies currently known to the palo 
	 * server.
	 * @return ids of all known palo hierarchies
	 */
	public abstract String[] getAllHierarchyIds();
	
	/**
	 * Creates a new {@link HierarchyInfo} instance
	 * @return a new <code>HierarchyInfo</code> object
	 */
	public final HierarchyInfo create() {
		throw new PaloException("Currently not possible!!");
//		HierarchyInfo hierInfo = paloConnection.addHierarchy(database,name);
//		loaded(hierInfo);
//		return hierInfo;

	}
	
	/**
	 * Deletes the given <code>HierarchyInfo</code> instance from the palo 
	 * server as well as from the internal used cache.
	 * @param hierInfo the <code>HierarchyInfo</code> instance to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public final boolean delete(HierarchyInfo hierInfo) {
		throw new PaloException("Currently not possible!!");
//		if(paloConnection.delete(hierInfo)) {
//		removed(hierInfo);
//		return true;
//	} 
//	return false;
	}

	/**
	 * Loads the <code>HierarchyInfo</code> object which corresponds to the given
	 * id
	 * @param id the identifier of the <code>HierarchyInfo</code> object to load
	 * @return the loaded <code>HierarchyInfo</code> object
	 */	
	public final HierarchyInfo load(String id) {
		PaloInfo hierarchy = loadedInfo.get(id);
		if (hierarchy == null) {
			hierarchy = paloConnection.getHierarchy(dimension, id);
			loaded(hierarchy);
		}
		return (HierarchyInfo)hierarchy;
	}

	/**
	 * Loads the <code>HierarchyInfo</code> object at the specified index
	 * @param index the index of the <code>HierarchyInfo</code> object to load
	 * @return the loaded <code>HierarchyInfo</code> object
	 */	
	public final HierarchyInfo load(int index) {
		String[] hierIds = getAllHierarchyIds();
		if(index<0 || index > hierIds.length-1)
			return null;
		return load(hierIds[index]);
	}
}
