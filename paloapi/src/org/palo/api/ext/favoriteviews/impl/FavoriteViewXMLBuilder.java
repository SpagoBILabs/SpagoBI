/*
*
* @file FavoriteViewXMLBuilder.java
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
* @version $Id: FavoriteViewXMLBuilder.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. 
 * All rights reserved.
 */
package org.palo.api.ext.favoriteviews.impl;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;
import org.palo.api.ext.favoriteviews.FavoriteViewsFolder;

/**
 * <code>FavoriteViewXMLBuilder</code>
 * Translates a tree built from <code>FavoriteViewTreeNode</code> objects (more
 * precisely from <code>FavoriteViewsFolder</code> and
 * <code>FavoriteView</code> objects) into an XML text.
 * Note that the <code>preOrderTraversal</code> method has been copied from
 * the <code>TreeUtils</code> class. It needed to be slightly modified as we
 * needed to watch for _entry_ and _exit_ events during visitation of nodes.
 * 
 * @author Philipp Bouillon
 * @version $Id: FavoriteViewXMLBuilder.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class FavoriteViewXMLBuilder {
	/**
	 * XML header string.
	 */
	private final static String XML_HEADER =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; 
	
	/**
	 * The result StringBuffer will contain the results of the XML translation.
	 */
	private StringBuffer result;
	
	/**
	 * To (slightly) pretty-print the XML output, an indentation counter is
	 * maintained.
	 */
	private int indent;
	
	/**
	 * The connection where this favorite view xml file has been stored.
	 */
	private Connection connection;
	
	/**
	 * The constructor initializes the result with the XML header description
	 * and sets initial indentation to 0.
	 */
	public FavoriteViewXMLBuilder(Connection con) {
		if (con == null) {
			throw new NullPointerException("Connection must not be null.");
		}
		result = new StringBuffer(XML_HEADER);
		indent = 0;
		this.connection = con;
	}
	
	/**
     * Traverses the specified tree in pre-order (children before the parent)
     * and signals visit-begin and visit-end notifications upon doing so.
     * 
     * @param node the tree node which is to be visited (usually, the user calls
     * the method with the root of a tree).
     */
	public void preOrderTraversal(FavoriteViewTreeNode node) {
        visitNodeBegin(node);
        FavoriteViewTreeNode children [] = node.getChildren();        
        if (children == null) {
        	visitNodeEnd(node);
        	return;
        }
        for (int i = 0; i < children.length; ++i) {
            preOrderTraversal(children[i]);
        }
        visitNodeEnd(node);
    }

	/**
	 * Whenever a <code>FavoriteViewsFolder</code> node is encountered in a
	 * tree, upon the start of visiting, a new beginning XML tag for folders is
	 * created.
	 * If a <code>FavoriteView</code> node is hit, a new bookmark XML tag is
	 * stored in the result buffer.
	 *  
	 * @param node the node being visited.
	 */
	protected void visitNodeBegin(FavoriteViewTreeNode node) {
		if (node.getUserObject() instanceof FavoriteViewsFolder) {			
			FavoriteViewsFolder folder = 
				(FavoriteViewsFolder) node.getUserObject();
			if (folder.getName().equals("Invisible Root")) {
				return;
			}
			doIndent();
			result.append("<folder name=\"" +
					((FavoriteViewsFolder) node.getUserObject()).getName() + "\" position=\"" +
					((FavoriteViewsFolder) node.getUserObject()).getPosition() + "\">\n");
			indent += 2;
		} else if (node.getUserObject() instanceof FavoriteViewImpl) {
			doIndent();
			FavoriteViewImpl bm = (FavoriteViewImpl) node.getUserObject();
			CubeView view = bm.getCubeView();
			if (view != null) {
				if (connection != null) {
					Connection con = bm.getConnection();
					if (!(con.getServer().equals(connection.getServer())) ||
						!(con.getService().equals(connection.getService()))) {
						return;
					}
				}
				Cube cube = view.getCube();
				result.append("<bookmark name=\"" +
							bm.getName() + "\" position=\"" +
							bm.getPosition() + "\" viewId=\"" +
							view.getId() + "\" databaseID=\"" +
							bm.getDatabaseId() + "\" cubeID=\"" +							
							cube.getId() + "\"/>\n");	
			}
		}
	}
	
	/**
	 * Whenever a <code>FavoriteViewsFolder</code> node is encountered in a tree, upon
	 * the end of visiting, the XML folder tag is closed.
	 * Nothing needs to be done, if a <code>FavoriteView</code> object is hit.
	 *  
	 * @param node the node being visited.
	 */
	protected void visitNodeEnd(FavoriteViewTreeNode node) {
		if (node.getUserObject() instanceof FavoriteViewsFolder) {
			FavoriteViewsFolder folder = (FavoriteViewsFolder) node.getUserObject();
			if (folder.getName().equals("Invisible Root")) {
				return;
			}
			indent -= 2;
			doIndent();
			result.append("</folder>\n");
		}
	}
	
	/**
	 * Adds a couple of spaces according to the currently specified indentation
	 * depth.
	 */
	private void doIndent() {
		for (int i = 0; i < indent; i++) {
			result.append(" ");
		}
	}
	
	/**
	 * Returns the result buffer as a string.
	 * @return the result buffer as a string.
	 */
	public String getResult() {
		return result.toString();
	}
}
