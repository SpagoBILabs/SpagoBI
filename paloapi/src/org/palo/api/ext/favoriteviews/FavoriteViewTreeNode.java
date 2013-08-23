/*
*
* @file FavoriteViewTreeNode.java
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
* @version $Id: FavoriteViewTreeNode.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007.
 * All rights reserved.
 */
package org.palo.api.ext.favoriteviews;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>FavoriteViewTreeNode</code> is a specialized tree which stores the
 * favorite views structure of a <code>Connection</code>.
 * 
 * You can use the favorite view tree to add {@link FavoriteView} or
 * {@link FavoriteViewsFolder} objects. While favorite views are the leafs of
 * the tree and thus may not have children, favorite views folders can be added
 * to the tree and can have children (either folders or favorite views).
 * 
 * The root of the favorite view tree is always a
 * <code>FavoriteViewsFolder</code>.
 * 
 * Example of a favorite view tree (to the left, the name of the element is
 * given, followed by its type in square brackets):
 * 
 * <code>
 *    RootFolder                     [FavoriteViewsFolder]
 *      |       
 *      |--- My Favorite View 1      [FavoriteView]
 *      |--- Another Folder          [FavoriteViewsFolder]
 *      | |
 *      | |--- My second view        [FavoriteView]
 *      | |--- My third view         [FavoriteView]
 *      |
 *      |--- My fourth view          [FavoriteView]
 * </code>
 * 
 * A complete favorite view tree can be loaded from a <code>Connection</code>
 * via the <i>loadFavoriteViews</i> method, which will return the root node of
 * the tree.
 * 
 * The tree can also be saved in a connection by using the
 * <i>storeFavoriteViews</i> method, which expects the root node of a favorite
 * views tree, so that it can be saved.
 * 
 * @author Philipp Bouillon
 * @version $ID$
 */
public class FavoriteViewTreeNode {
    /**
     * The user object of the tree node. That is the object containing the
     * data.
     */
	private FavoriteViewObject userObject;
    
	/**
	 * This tree node's parent or null if it has no parent.
	 */
	private FavoriteViewTreeNode parent;
    
	/**
	 * The list of children. If this node does not have any children, the
	 * list will be empty.
	 */
	private List children;

    /**
     * Creates a new <code>FavoriteViewTreeNode</code> with the given user
     * object. The user object can either be a favorite view or a favorite
     * view folder and represents the data of this node.
     * 
     * @param userObject the user object for this node.
     */
	public FavoriteViewTreeNode(FavoriteViewObject userObject) {
        this.userObject = userObject;
        this.children = new ArrayList(1);
    }
    
    //-------------------------------------------------------------------------
    // basic
    
    /**
     * Returns the user object for this tree node. The user object is either
     * a <code>FavoriteView</code> or a <code>FavoriteViewsFolder</code>.
     * 
     * @return the user object for this node.
     */
	public FavoriteViewObject getUserObject() {
        return userObject;
    }
    
    /**
     * Sets a new parent for this node.
     * 
     * @param parent the new parent for this node.
     */
	public void setParent(FavoriteViewTreeNode parent) {
        this.parent = parent;
    }
    
    /**
     * Returns the parent of this node.
     * 
     * @return this node's parent.
     */
	public FavoriteViewTreeNode getParent() {
        return parent;
    }
    
    /**
     * Two FavoriteViewTreeNodes are considered equal if their user object is
     * equal. Thus the hashCode method is overridden to implement this logic.
     */
	public int hashCode() {
        if (userObject != null) {
            return userObject.hashCode();
        }       
        return super.hashCode();
    }
    
    /**
     * Two FavoriteViewTreeNodes are considered equal if their user object is
     * equal. Thus the equals method is overridden to implement this logic.
     */
    public boolean equals(Object obj) {
        if (obj instanceof FavoriteViewTreeNode) {
            FavoriteViewObject uo1 = userObject;
            FavoriteViewObject uo2 = ((FavoriteViewTreeNode)obj).getUserObject();
            
            if (uo1 != null && uo2 != null) {
                return uo1.equals(uo2);
            }
        }
        return super.equals(obj);
    }
    
    //-------------------------------------------------------------------------
    // children
    
    /**
     * Adds a child at the end of the list of children of this node. The parent
     * of the specified node is automatically set to be this node.
     * 
     * @param child the node to be added to the list of children.
     */
    public void addChild(FavoriteViewTreeNode child) {
        children.add(child);
        child.setParent(this);
    }
    
    /**
     * Inserts a child at the given index into the list of children of this
     * node. If the index is out of bounds, the child will automatically be
     * added at the end of the list of children.
     * 
     * @param index the position at which the new child is going to be inserted.
     * @param child the child node to be inserted into the list of children.
     */
    public void insertChild(int index, FavoriteViewTreeNode child) {
        if (index > children.size() || index < 0) {
            children.add(child);
        } else {
            children.add(index, child);
        }    
        child.setParent(this);
    }
    
    /**
     * Removes the specified child from the list of children and sets the
     * child's parent to null. Note that if the child is not in the list of
     * children of this node, its parent will nevertheless be set to null.
     * 
     * @param child the child to be removed from this node.
     */
    public void removeChild(FavoriteViewTreeNode child) {
        children.remove(child);
        child.setParent(null);
    }
    
    /**
     * Removes all children from this node and sets their parent to null.
     */
    public void removeChildren() {
        for (int i = children.size() - 1; i >= 0; --i) {
            FavoriteViewTreeNode child = (FavoriteViewTreeNode) children.get(i);
            children.remove(child);
            child.setParent(null);
        }
    }
    
    /**
     * Returns the index of the specified child or -1 if it is not a child of
     * this node.
     * 
     * @param child the child node of which the index is to be retieved.
     * @return the index of the child or -1 if this node is not its parent.
     */
    public int indexOfChild(FavoriteViewTreeNode child) {
        return children.indexOf(child);
    }
    
    /**
     * Returns all children of this node.
     * 
     * @return all children of this node.
     */
    public FavoriteViewTreeNode [] getChildren() {
        return (FavoriteViewTreeNode []) children.toArray(
            new FavoriteViewTreeNode [children.size()]);
    }
    
    /**
     * Returns the number of children of this node.
     * 
     * @return the number of children of this node.
     */
    public int getChildCount() {
        return children.size();
    }
    
    /**
     * Returns the child at the specified index.
     * 
     * @param index the index of the child which is to be retrieved.
     * @return the child at the specified index.
     */
    public FavoriteViewTreeNode getChildAt(int index) {
        return (FavoriteViewTreeNode) children.get(index);
    }
    
    /**
     * Returns true if this node has children and false otherwise.
     * 
     * @return true if this node has children and false otherwise.
     */
    public boolean hasChildren() {
        return children.size() > 0;
    }
}

