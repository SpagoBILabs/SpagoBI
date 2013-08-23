/*
*
* @file ExplorerTreeNode.java
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
* @version $Id: ExplorerTreeNode.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import org.palo.api.parameters.ParameterReceiver;
import org.palo.viewapi.Account;
import org.palo.viewapi.GuardedObject;
import org.palo.viewapi.PaloConnection;


/**
 * <code>ExplorerTreeNode</code>
 * Base interface for all nodes of the repository manager.
 *
 * @author Philipp Bouillon
 * @version $Id: ExplorerTreeNode.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface ExplorerTreeNode extends ParameterReceiver, GuardedObject {
	/**
	 * Returns the id of this tree node object. The id is globally unique.
	 * 
	 * @return the globally unique id of this tree node.
	 */
	String getId();
	
	/**
	 * Returns the name of this tree node. The name will be translated to
	 * the currently set alias, or, if either no alias is set or the name
	 * does not exist in the alias, the default name is returned.
	 * 
	 * @return the name of the tree node.
	 */
	String getName();
	
	/**
	 * Sets a new name for this tree node.
	 * 
	 * @param newName the new name of the node.
	 */
	void setName(String newName);
	
	/**
	 * Returns the root node of the tree.
	 * 
	 * @return the root node of the tree.
	 */
	ExplorerTreeNode getRoot();
	
	/**
	 * Returns the parent of this node or null, if this node is the root.
	 * 
	 * @return the parent of this node.
	 */
	ExplorerTreeNode getParent();
		
	/**
	 * Sets a new parent for this node.
	 * 
	 * @param newParent the new parent node for this node.
	 */
	void setParent(ExplorerTreeNode newParent);
	
	/**
	 * Returns all children of this node or an empty array, if the node has
	 * no children.
	 * 
	 * @return all children of this node.
	 */
	ExplorerTreeNode [] getChildren();
	
	/**
	 * Adds the specified child to the list of children of this node.
	 * 
	 * @param child the tree node to add to this node.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	boolean addChild(ExplorerTreeNode child);
	
	/**
	 * Removes the specified child from the list of children of this
	 * node.
	 * 
	 * @param child the tree node which is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	boolean removeChild(ExplorerTreeNode child);
	
	/**
	 * Removes the child identified by the given id.
	 * 
	 * @param id the id of the child that is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	boolean removeChildById(String id);
	
	/**
	 * Removes the child identified by the given name.
	 * 
	 * @param name the name of the child that is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	boolean removeChildByName(String name);
	
	/**
	 * Removes all children of this node.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */	
	boolean clearAllChildren();	
	
	/**
	 * Returns the xml description String for this tree node.
	 * 
	 * @return the xml description String for this tree node.
	 */
	String getPersistenceString();
	
	String getConnectionId();
	int getType();
}
