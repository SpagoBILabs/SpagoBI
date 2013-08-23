/*
*
* @file FavoriteViewModel.java
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
* @author PhilippBouillon
*
* @version $Id: FavoriteViewModel.java,v 1.14 2009/12/14 12:46:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.favoriteviews.impl;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.PaloConstants;
import org.palo.api.ext.favoriteviews.FavoriteViewFactory;
import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;

import com.tensegrity.palo.xmla.ext.views.SQLConnection;

/**
 * The <code>FavoriteViewModel</code> class provides internal methods to load
 * and save the favorite view tree. Namely, this class checks if a cube to
 * store bookmarks is available and creates one if that is not the case. The
 * class will also generate the xml code for a favorite view tree and save that
 * in the correct position in the database.
 * 
 * @author PhilippBouillon
 * @version $Id: FavoriteViewModel.java,v 1.14 2009/12/14 12:46:57 PhilippBouillon Exp $
 */
public class FavoriteViewModel {
    /**
     * Takes the XML representation of a favorite view tree structure and
     * transforms it into a tree.
     * 
     * @param input the xml representation of the favorite views.
     * @return the root of the newly generated tree.
     */
    final protected FavoriteViewTreeNode fromXML(String input, Connection con) {
		FavoriteViewXMLHandler defaultHandler = new FavoriteViewXMLHandler(con);
		
		SAXParserFactory sF = SAXParserFactory.newInstance();
		SAXParser parser = null;		
		try {
			ByteArrayInputStream bin = 
				new ByteArrayInputStream(input.getBytes("UTF-8")); //$NON-NLS-1$
			parser = sF.newSAXParser();
			parser.parse(bin, defaultHandler);			
			return defaultHandler.getRoot();
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}    
    
    /**
     * If a connection is found that does not (yet) have an
     * <code>AdvancedSystem</code> database, the database is created in this
     * method, then dimensions (#user and #bookmarkedViews) are added along
     * with the entries (the current user name and "Bookmarks"). Finally a
     * cube is created using these two dimensions. The cube is then returned.
     * 
     * @param con the connection in which the new AdvancedSystem database is to
     * be added.
     * @return the bookmark cube of the AdvancedSystem database consisting of
     * the user and bookmark dimensions. 
     */
    public Cube createBookmarkCubeInNewDatabase(Connection con) {
    	Cube cube = null;
    	
    	try {
    		Database paloDb = con.addDatabase(PaloConstants.PALO_CLIENT_SYSTEM_DATABASE);
    		Dimension userDim = paloDb.addDimension("#user");
    		Hierarchy userHier = userDim.getDefaultHierarchy();
    		userHier.addElement(con.getUsername(), Element.ELEMENTTYPE_STRING);
    		Dimension bookmarkDim = paloDb.addDimension("#bookmarkedViews");
    		Hierarchy bookmarkHier = bookmarkDim.getDefaultHierarchy();
    		bookmarkHier.addElement("Bookmarks", Element.ELEMENTTYPE_STRING);
    		cube = paloDb.addCube("#userBookmarks", 
    				new Dimension [] {userDim, bookmarkDim});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		
		return cube;
    }
    
    /**
     * If a connection is found that _does_ have an <code>AdvancedSystem</code>
     * database but which does not include the bookmarks cube, this method
     * creates the new bookmarks cube.
     * 
     * Dimensions will be created if necessary.
     * 
     * @param db the AdvancedSystem database of the current connection.
     * @return the newly created bookmark cube.
     */
    public Cube createBookmarkCubeInExistingDatabase(Database db) {
		Dimension user;
		Dimension bookmarks;

		// Check if the #user dimension exists.
		user = db.getDimensionByName("#user");
		if (user == null) {
			// No. Create it.
			user = db.addDimension("#user");
			user.getDefaultHierarchy().addElement(db.getConnection().getUsername(),
					Element.ELEMENTTYPE_STRING);
		} else {
			// Yes it exists, now check if it has the current user as an
			// element.
			if (user.getDefaultHierarchy().getElementByName(db.getConnection().getUsername()) == null) {
				user.getDefaultHierarchy().addElement(db.getConnection().getUsername(),
						Element.ELEMENTTYPE_STRING);
			}
		}

		// Check if the #bookmarkedViews dimension exists.
		bookmarks = db.getDimensionByName("#bookmarkedViews");
		if (bookmarks == null) {
			// No. Create it.
			bookmarks = db.addDimension("#bookmarkedViews");
			bookmarks.getDefaultHierarchy().addElement("Bookmarks", Element.ELEMENTTYPE_STRING);
		} else {
			// Yes it exists, now check if it has the "Bookmarks" element.
			if (bookmarks.getDefaultHierarchy().getElementByName("Bookmarks") == null) {
				bookmarks.getDefaultHierarchy().addElement("Bookmarks",
						Element.ELEMENTTYPE_STRING);
			}
		}

		// Everything is prepared, create the cube.
		Cube cube = db.addCube("#userBookmarks", new Dimension[] { user,
					bookmarks });
		return cube;
    }

    /**
     * Finds the bookmark cube for a given connection. If it is not present
     * yet, the program will try to create it.
     * 
     * @param con the connection from which the bookmark cube is to be found.
     * @return the bookmark cube for the connection or null if it could not
     * be created.
     */
    private final Cube findBookmarksCube(Connection con, boolean create) {
    	Database db = null;
    	
    	// Get the system database.
   		db = con.getDatabaseByName(
   				PaloConstants.PALO_CLIENT_SYSTEM_DATABASE);
		Cube cube = null;
		if (db == null && create) {
			// It's not there yet, create it (and the bookmark cube along
			// with it).
			cube = createBookmarkCubeInNewDatabase(con);
		} else if (db != null) {
			// The database exists, get the bookmark cube. 
			cube = db.getCubeByName("#userBookmarks");
			if (cube == null && create) {
				// The cube does not exist, create it.
				cube = createBookmarkCubeInExistingDatabase(db);
			}
		}
		
		return cube;
    }
    
    /**
     * The method retrieves the AdvancedSystem database of the connection (or
     * creates it, if it is not yet in the connection) and also gets the
     * bookmark-cube from the AdvancedSystem database (or creates it, if it had
     * not been present (for this user)).
     * Then, this methods loads all stored favorite views and transforms the
     * xml structure to a new favorite views tree. The root of which is
     * returned.
     * 
     * @param con the connection which is being added to the bookmark model.
     * @return the root of the favorite views tree or null if no bookmarks
     * exist.
     */
    public FavoriteViewTreeNode loadFavoriteViews(Connection con) {    	
   		if (con.getType() == Connection.TYPE_XMLA) {
   			SQLConnection sql = new SQLConnection();
   			String xmlData = "";
   			try {
   				xmlData = sql.loadFavoriteView(con.getServer(), con.getService(), con.getUsername());
   			} finally {
   				sql.close();   				
   			}
   			if (xmlData.trim().length() > 0) {
   				return fromXML(xmlData, con);
   			}
   			return null;
   		}
   		
   		if (con.getType() == Connection.TYPE_WSS) {
   			return null;
   		}

    	Cube cube = findBookmarksCube(con, false);
    	if (cube == null) {
			return new FavoriteViewTreeNode(
					FavoriteViewFactory.getInstance().createFolder("Root", con));
		}
		
		// Now check if the user already has saved some bookmarks
		Dimension userDim = cube.getDimensionByName("#user");
		if (userDim == null) {
			// Hmm. The favorite views cube seems to be corrupted. So, ignore
			// it.
			return new FavoriteViewTreeNode(
					FavoriteViewFactory.getInstance().createFolder("Root", con));
		}
		if (userDim.getDefaultHierarchy().getElementByName(con.getUsername()) == null) {
			try {
				userDim.getDefaultHierarchy().addElement(con.getUsername(), Element.ELEMENTTYPE_STRING);
			} catch (Exception e) {
				throw new PaloAPIException(e.getLocalizedMessage());
			}
		}
		try {
			// Read the bookmark data from the cube.
			String xmlData = (String) cube.getData(new String[] {
					con.getUsername(), "Bookmarks" });
			if (xmlData != null) {
				if (xmlData.length() > 0) {
					return fromXML(xmlData, con);
				}
			}
		} catch (PaloAPIException e) {
			/* failed to load favorite view! */
			System.err.println("Failed to load favorite view!\nReason: "
					+ e.getMessage());
		}
		return null;
    }    
        
    /**
     * Stores a favorite views tree in the given connection.
     * 
     * @param con the connection which is to store the favorite views.
     * @param root the root of the favorite views tree that is to be stored.
     */
    public synchronized void storeFavoriteViews(Connection con, FavoriteViewTreeNode root) {
		if (con == null) {
       		throw new NullPointerException(
       				"Connection to store the bookmark must not be null.");
       	}
   		if (!con.isConnected()) {
   			// Theoretically, this should never happen, but just to be one the
   			// safe side...
   			return;
   		}
   		   		
		FavoriteViewXMLBuilder builder = new FavoriteViewXMLBuilder(con);
		builder.preOrderTraversal(root);
				
		String xmlBookmarkText = builder.getResult();
   		if (con.getType() == Connection.TYPE_XMLA) {
   			SQLConnection sql = new SQLConnection();
   			try {
   				sql.writeFavoriteViews(con.getServer(), con.getService(), con.getUsername(), xmlBookmarkText);
   			} finally {
   				sql.close();   				
   			}
   			return;
   		}
   		
   		if (con.getType() == Connection.TYPE_WSS) {
   			return;
   		}
   		
   		Cube cube = findBookmarksCube(con, true);
   		if (cube == null) {
   			// There won't be a cube, if the user did not have enough access
   			// rights to create a database. So bail out with an exception.
   			throw new PaloAPIException(
   					"Insufficient rights to store favorite views.");
   		}
   		// Everything's alright, so write the xml structure.
   		cube.setData(new String [] {con.getUsername(), "Bookmarks"}, 
   				xmlBookmarkText);
    }    
}
