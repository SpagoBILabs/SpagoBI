/*
*
* @file FavoriteViewsFolderImpl.java
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
* @version $Id: FavoriteViewsFolderImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007.
 * All rights reserved.
 */
package org.palo.api.ext.favoriteviews.impl;

import org.palo.api.Connection;
import org.palo.api.ext.favoriteviews.FavoriteViewsFolder;

/**
 * <code>FavoriteViewsFolderImpl</code>
 * A folder is a container object with a name. It also holds a position
 * information so that the user can place it anywhere she likes (in relation
 * to its siblings in the tree).
 * Furthermore the folder has information on the connection from which it was
 * created. This is used to assign favorite view structures to specific
 * connections. If a folder is being moved to a favorite view structure of a
 * different connection, the transfer is aborted.
 *  
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewsFolderImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class FavoriteViewsFolderImpl implements FavoriteViewsFolder {
	/**
	 * The name of the folder.
	 */
	private String name;
	
	/**
	 * The position of the folder in the tree structure. This is used by the
	 * TreeSorter to "sort" the folders (i.e. the user can put them where she
	 * likes).
	 */
	private int position;
	
	/**
	 * The <code>Connection</code> to which this folder belongs.
	 */
	private Connection connection;
	
	/**
	 * Creates a new folder with the specified name and connection.
	 * 
	 * @param name the name of the folder.
	 * @param con the connection to which this bookmark folder belongs.
	 */
	public FavoriteViewsFolderImpl(String name, Connection con) {
		this.name = name;
		this.connection = con;
		this.position = 0;		
	}
	
	/**
	 * Creates a new folder with the specified name and position.
	 * 
	 * @param name the name of the folder.
	 * @param con the connection to which this bookmark folder belongs.
	 * @param position the position in relation to its siblings.
	 */
	public FavoriteViewsFolderImpl(String name, Connection con, int position) {
		this.name = name;
		this.connection = con;
		this.position = position;
	}
	
	/**
	 * Returns the name of the folder.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets a new name for the folder.
	 * 
	 * @param newName the new name for the folder.
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * Returns the position of the folder.
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets a new position for the folder.
	 * 
	 * @param newPosition the new position for the folder.
	 */
	public void setPosition(int newPosition) {
		position = newPosition;
	}
	
	/**
	 * Returns the connection for this folder.
	 * 
	 * @return the connection for this folder.
	 */
	public Connection getConnection() {
		return connection;
	}
}
