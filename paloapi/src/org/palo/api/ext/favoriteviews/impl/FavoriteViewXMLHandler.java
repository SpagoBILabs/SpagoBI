/*
*
* @file FavoriteViewXMLHandler.java
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
* @version $Id: FavoriteViewXMLHandler.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007.
 * All rights reserved.
 */
package org.palo.api.ext.favoriteviews.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;
import org.palo.api.ext.favoriteviews.FavoriteViewsFolder;
import org.palo.api.impl.xml.BaseXMLHandler;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.api.persistence.PersistenceError;
import org.palo.api.persistence.PersistenceObserver;
import org.palo.api.persistence.PersistenceObserverAdapter;
import org.xml.sax.Attributes;

/**
 * <code>FavoriteViewXMLHandler</code>
 * Parses a given XML file representing favorite views and translates it into a
 * tree consisting of <code>FavoriteViewsFolder</code> and
 * <code>FavoriteView</code> objects. 
 * 
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewXMLHandler.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class FavoriteViewXMLHandler extends BaseXMLHandler {
	/**
	 * A stack representing the folders in the xml structure.
	 */
	private Stack folderStack = new Stack();

	/**
	 * Currently active folder. All views read subsequently are added to this
	 * folder.
	 */
	private FavoriteViewTreeNode currentFolder = null;

	/**
	 * The root of the generated tree.
	 */
	private FavoriteViewTreeNode root = null;
			
	/**
	 * Returns the root of the generated tree.
	 * @return the root of the generated tree.
	 */
	public FavoriteViewTreeNode getRoot() {
		return root;
	}

	/**
	 * This method is used to find a <code>CubeView</code> object in the
	 * set of currently active databases and connections. To identify the
	 * specific view, the server name, service name, user name, database id,
	 * cube id and cube view id are used.
	 * 
	 * @param connection the connection of the current user.
	 * @param dbId the unique database id belonging to the view in question.
	 * @param cubeId the cube id belongig to the view in question.
	 * @param viewId the cube view id belonging to the view in question.
	 * @return the <code>CubeView</code> object representing the specified
	 * strings, or null if no matching <code>CubeView</code> could be found.
	 */
    public CubeView findCubeViewById(Connection connection, String dbId,
    		String cubeId, String viewId) {
		// Identify the correct database:
		Database [] dbs = connection.getDatabases();
		for (int j = 0; j < dbs.length; j++) {
			if (dbs[j].getId().equals(dbId)) {
				// Identify the correct cube:
				Cube c = dbs[j].getCubeById(cubeId);
				if (c != null) {
					final ArrayList <CubeView> views = 
						new ArrayList<CubeView>();
					c.getCubeViews(new PersistenceObserverAdapter(){
						public void loadComplete(Object source) {
							if (source instanceof CubeView) {
								views.add((CubeView) source);
							}
						}

						public void loadFailed(String sourceId,
									PersistenceError[] errors) {
							for(PersistenceError error : errors) {
								Object src = error.getSource();
									if(src != null && src instanceof CubeView)
										views.add((CubeView) src);
							}
						}

						public void loadIncomplete(Object source,
								PersistenceError[] errors) {
							if (source != null && source instanceof CubeView) {
								views.add((CubeView) source);
							}
						}
					});
						
					CubeView view = null;
					for (CubeView v: views) {
						if (viewId.equals(v.getId())) {
							view = v;
							break;
						}
					}
					if (view != null) {
						return view;
					}
				}
			}
		}
		return null;    	
    }

	/**
	 * The constructor adds the handlers for the XML structure.
	 * 
	 * @param connection the <code>Connection</code> from which this xml file
	 * is read.  
	 */
	public FavoriteViewXMLHandler(final Connection connection) {
		super(true);

		// The start handler for folders creates a new
		// <code>FavoriteViewsFolder</code> object and links it to its parent.
		putStartHandler("folder", new StartHandler() {
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String folderName = attributes.getValue("name");
				String position = attributes.getValue("position");
				if (folderName != null && folderName.length() > 0) {
					int pos = Integer.parseInt(position);
					FavoriteViewsFolder fold = 
						new FavoriteViewsFolderImpl(folderName, connection, pos);
					FavoriteViewTreeNode folder = new FavoriteViewTreeNode(fold);
					if (currentFolder == null) {
						root = folder;
					}
					if (currentFolder != null) {
						currentFolder.addChild(folder);
						folder.setParent(currentFolder);
					}
					folderStack.push(folder);
					currentFolder = folder;
				}
			}
		});
		
		// The start handler for views finds the <code>FavoriteView</code>
		// object in the database and links it to its parent folder.		
		putStartHandler("bookmark", new StartHandler() {
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String name = attributes.getValue("name");
				String position = attributes.getValue("position");
				String queryName = attributes.getValue("queryName");
				String viewId = null;
				if (queryName == null) {
					viewId = attributes.getValue("viewId");
				}
				String databaseId = attributes.getValue("databaseID");
				String cubeId = attributes.getValue("cubeID");

				CubeView view =
					findCubeViewById(connection, databaseId, cubeId, viewId);
				FavoriteViewImpl bm = new FavoriteViewImpl(name, view, 
						Integer.parseInt(position));	
				FavoriteViewTreeNode node = new FavoriteViewTreeNode(bm);
				currentFolder.addChild(node);
				node.setParent(currentFolder);
			}
		});
		
		// The end handler for folders pops the previous folder from the
		// stack and adds all subsequent elements to it (if it is not null;
		// if it is null, no more elements will follow).
		putEndHandler("folder", new EndHandler() {
			public void endElement(String uri, String localName, String qName) {
				if (folderStack.isEmpty()) {
					currentFolder = null;
					return;
				}
				currentFolder = (FavoriteViewTreeNode) folderStack.pop();
				if (folderStack.isEmpty()) {
					currentFolder = null;
				} else {
					// Return the top of the stack but do not remove it:
					// It might still have children...
					currentFolder = (FavoriteViewTreeNode) folderStack.lastElement();
				}
			}
		});
	}	
}
