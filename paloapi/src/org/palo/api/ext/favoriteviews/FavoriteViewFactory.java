/*
*
* @file FavoriteViewFactory.java
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
* @version $Id: FavoriteViewFactory.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.favoriteviews;

import org.palo.api.Connection;
import org.palo.api.CubeView;

/**
 * <code>FavoriteViewFactory</code>
 * 
 * <p>An instance of <code>FavoriteViewFactory</code> is obtained with the 
 * {@link #getInstance()} method. Subsequently a favorite view or a favorite
 * view folder can be created by one of the create methods below.
 * </p>
 * 
 * <p>Example:
 * <pre>
        FavoriteView favoriteView = FavoriteViewFactory.getInstance().
        	createFolder(&quot;MyFolder&quot;, connection);            
 * </pre>
 * </p>
 *
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewFactory.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public abstract class FavoriteViewFactory {
	private static FavoriteViewFactory instance;
	
    static {
        try {
            instance = (FavoriteViewFactory)
                Class.forName("org.palo.api.ext.favoriteviews.impl.FavoriteViewFactoryImpl").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static FavoriteViewFactory getInstance() {
        return instance;
    }
	
    //-------------------------------------------------------------------------

    /**
	 * Creates a new <code>FavoriteView</code> with a name and an attached cube
	 * view.
	 * 
	 * @param name the name of the new favorite view.
	 * @param view the attached cube view.
	 * 
	 * @return a new FavoriteView with the specified parameters.
	 */
	public abstract FavoriteView createFavoriteView(String name, CubeView view);
	
	/**
	 * Creates a new <code>FavoriteView</code> with a name, an attached cube
	 * view, and a position.
	 * 
	 * @param name the name of the new favorite view.
	 * @param view the attached cube view.
	 * @param position the position of this favorite view (the index to the
	 * children of its parent).
	 * 
 	 * @return a new FavoriteView with the specified parameters.
	 */
	public abstract FavoriteView createFavoriteView(
			String name, CubeView view, int position);

	/**
	 * Creates a new <code>FavoriteViewsFolder</code> with the specified name
	 * and connection. Folders have to be created with a fixed connection,
	 * because all favorite views are saved per connection.
	 * 
	 * @param name the name of the folder.
	 * @param con the connection to which this favorite views folder belongs.
	 * 
	 * @return a new FavoriteViewFolder with the specified parameters.
	 */
	public abstract FavoriteViewsFolder createFolder(
			String name, Connection con);
	
	/**
	 * Creates a new <code>FavoriteViewsFolder</code> with the specified name
	 * and connection. Folders have to be created with a fixed connection,
	 * because all favorite views are saved per connection.
	 * 
	 * @param name the name of the folder.
	 * @param con the connection to which this favorite views folder belongs.
	 * @param position the position in relation to its siblings.
	 * 
 	 * @return a new FavoriteViewFolder with the specified parameters.
	 */
	public abstract FavoriteViewsFolder createFolder(
			String name, Connection con, int position);	
}
