/*
*
* @file FavoriteViewFactoryImpl.java
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
* @version $Id: FavoriteViewFactoryImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.favoriteviews.impl;

import org.palo.api.Connection;
import org.palo.api.CubeView;
import org.palo.api.ext.favoriteviews.FavoriteView;
import org.palo.api.ext.favoriteviews.FavoriteViewFactory;
import org.palo.api.ext.favoriteviews.FavoriteViewsFolder;

/**
 * <code>FavoriteViewFactoryImpl</code>
 * 
 * <p>The <code>FavoriteViewFactoryImpl</code> class provides factory methods 
 * to create favorite views and favorite view folders. The method calls are
 * translated into constructors of the respective classes, thus the clients
 * will never see the real constructor and so, the implementation is hidden
 * from the clients.
 * </p>
 *
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewFactoryImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class FavoriteViewFactoryImpl extends FavoriteViewFactory {
	/**
	 * Creates a new favorite view with a default position (0). Can be
	 * used by clients that would like to implement a more complicated ordering
	 * scheme.
	 */
	public FavoriteView createFavoriteView(String name, CubeView view) {
		return new FavoriteViewImpl(name, view);
	}

	/**
	 * Creates a new favorite view.
	 */
	public FavoriteView createFavoriteView(String name, CubeView view,
			int position) {
		return new FavoriteViewImpl(name, view, position);
	}

	/**
	 * Creates a new favorite views folder with a default position (0).
	 */
	public FavoriteViewsFolder createFolder(String name, Connection con) {
		return new FavoriteViewsFolderImpl(name, con);
	}

	/**
	 * Creates a new favorite views folder.
	 */
	public FavoriteViewsFolder createFolder(String name, Connection con,
			int position) {
		return new FavoriteViewsFolderImpl(name, con, position);
	}
}
