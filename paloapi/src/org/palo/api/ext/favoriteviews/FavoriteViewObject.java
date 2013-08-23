/*
*
* @file FavoriteViewObject.java
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
* @version $Id: FavoriteViewObject.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.favoriteviews;

import org.palo.api.Connection;
import org.palo.api.NamedEntity;

/**
 * A <code>FavoriteViewObject</code> represents a
 * <code>{@link FavoriteView}</code> or a
 * <code>{@link FavoriteViewsFolder}</code> object. Since both objects are
 * connection specific, they both have a connection attached to them. This
 * connection can be retrieved by using the <code>getConnection</code> method  
 * declared in this interface.
 *  
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewObject.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public interface FavoriteViewObject extends NamedEntity {	
	/**
	 * Returns the <code>Connection</code> object that is attached to this
	 * favorite view or favorite views folder.
	 * 
	 * @return the attached Connection object.
	 */
	public Connection getConnection();
}
