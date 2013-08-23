/*
*
* @file FavoriteViewImpl.java
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
* @author Philipp Bouillon
*
* @version $Id: FavoriteViewImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.favoriteviews.impl;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.ext.favoriteviews.FavoriteView;

/**
 * <code>FavoriteViewImpl</code>
 * This class describes a favorite view. A favorite view has a name and a
 * <code>CubeView</code> object attached. It also stores (for convenience) the 
 * identifying attributes of its cube view (database id, cube id and cube view
 * name).
 * In addition, a favorite view holds its own position in relation to its
 * parent, so that the user can directly modify it (move it up or down).
 *  
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class FavoriteViewImpl implements FavoriteView {
	/**
	 * The name of the favorite view. Initially, the favorite views's name is
	 * equal to the name of its cube view, but the user can rename the favorite
	 * view.
	 */
	private String name;
	
	/**
	 * The cube view object that belongs to this favorite view.  
	 */
	private CubeView cubeView;
	
	/**
	 * The position of this favorite view in relation to its parent.
	 */
	private int position;
	
	/**
	 * Convenience field to store the database id of the cube view for this
	 * favorite view.
	 */
	private String databaseId;

	/**
	 * Convenience field to store the cube id of the cube view for this
	 * favorite view.
	 */
	private String cubeId;
	
	/**
	 * Convenience field to store the cube view name for this favorite view.
	 */
	private String cubeViewName;
	
	/**
	 * Creates a new <code>FavoriteView</code> with a name and an attached cube
	 * view.
	 * 
	 * @param name the name of the favorite view.
	 * @param query the attached cube view.
	 */
	public FavoriteViewImpl(String name, CubeView view) {
		this.name = name;
		this.cubeView = view;
		this.position = 0;
		initCubeView();
	}
	
	/**
	 * Creates a new <code>FavoriteView</code> with a name, an attached cube
	 * view, and a position.
	 * 
	 * @param name the name of the favorite view.
	 * @param query the attached cube view.
	 * @param position the position of this favorite view (the index to the
	 * array of children of its parent).
	 */
	public FavoriteViewImpl(String name, CubeView view, int position) {
		this.name = name;
		this.cubeView = view;
		this.position = position;	
		initCubeView();
	}
	
	/**
	 * Stores the characterizing information for the cube view attached to this
	 * favorite view in the convenience fields. If the cube view is null, the
	 * fields will be left untouched.
	 */
	private void initCubeView() {
		if (cubeView == null) {
			return;
		}
		cubeViewName = cubeView.getName();
		Database db = cubeView.getCube().getDatabase();
		Cube cube = cubeView.getCube();
		cubeId = cube.getId();
		databaseId = db.getId();
	}
	
	/**
	 * Returns the name of this favorite view.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a new name for this favorite view.
	 * 
	 * @param newName the new name for this favorite view.
	 */
	public void setName(String newName) {
		name = newName;		
	}
		
	/**
	 * Returns the position of this favorite view in relation to its parent.
	 * @return the position of this favorite view in relation to its parent.
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets a new position for this favorite view.
	 * 
	 * @param newPosition the new position for this favorite view.
	 */
	public void setPosition(int newPosition) {
		position = newPosition;
	}
	
	/**
	 * Returns the cube view that is attached to this favorite view.
	 * @return the cube view that is attached to this favorite view.
	 */
	public CubeView getCubeView() {
		return cubeView;
	}
			
	/**
	 * Returns the cube id of the attached cube view.
	 * @return the cube id of the attached cube view.
	 */
	public String getCubeId() {
		return cubeId;
	}
	
	/**
	 * Returns the database id of the attached cube view.
	 * @return the database id of the attached cube view.
	 */
	public String getDatabaseId() {
		return databaseId;
	}
	
	/**
	 * Returns the name of the attached cube view.
	 * @return the name of the attached cube view.
	 */
	public String getCubeViewName() {
		return cubeViewName;
	}
		
	/**
	 * Returns the connection that belongs to the attached cube view.
	 */
	public Connection getConnection() {
		return cubeView.getCube().getDatabase().getConnection();
	}
}
