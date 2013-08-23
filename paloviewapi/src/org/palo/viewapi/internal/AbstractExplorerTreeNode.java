/*
*
* @file AbstractExplorerTreeNode.java
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
* @version $Id: AbstractExplorerTreeNode.java,v 1.8 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;

/**
 * <code>AbstractExplorerTreeNode</code>
 * General implementation of an explorer tree node.
 *
 * @author Philipp Bouillon
 * @version $Id: AbstractExplorerTreeNode.java,v 1.8 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public abstract class AbstractExplorerTreeNode extends GuardedObjectImpl implements ExplorerTreeNode {
	/**
	 * Globally unique id.
	 */
	//protected final String id;
	
	/**
	 * Name of the node.
	 */
	protected String name;
	
	/**
	 * Parent node. Null if none.
	 */
	protected ExplorerTreeNode parent;
	
	/**
	 * All child nodes.
	 */
	protected ArrayList <ExplorerTreeNode> children;

	protected String connectionId;
	
	/**
	 * Creates a new abstract tree node with no parents, no kids, no name, and
	 * a randomly generated unique id.
	 * 
	 * @param prefix a prefix for the id. Usually indicating the subclass from
	 * which this tree node has been created.
	 */
	protected AbstractExplorerTreeNode(String prefix) {		
		super(prefix + UUID.randomUUID().toString());
		children = new ArrayList<ExplorerTreeNode>();
	}

	/**
	 * Creates a new abstract tree node with no parent, no kids, and no
	 * name. This constructor is used when a tree node is loaded.
	 * 
	 * @param id the unique id of the new node.
	 */
	protected AbstractExplorerTreeNode(String id, boolean internal) {		
		super(id);
		children = new ArrayList<ExplorerTreeNode>();
	}	
	
	/**
	 * Creates a new abstract tree node with the given parent.
	 * 
	 * @param prefix a prefix for the id. Usually indicating the subclass from
	 * which this tree node has been created.
	 * @param parent the parent of this abstract tree node. Note that
	 * implementors must still make sure that this new node is added to the
	 * children of its parent.
	 */
	protected AbstractExplorerTreeNode(String prefix, ExplorerTreeNode parent) {
		super(prefix + UUID.randomUUID().toString());
		this.parent = parent;
		children = new ArrayList<ExplorerTreeNode>();
	}
	
	/**
	 * Creates a new abstract tree node with the given parent and the given
	 * name and a new random unique id.
	 * 
	 * @param prefix a prefix for the id. Usually indicating the subclass from
	 * which this tree node has been created.
	 * @param parent parent the parent of this abstract tree node. Note that
	 * implementors must still make sure that this new node is added to the
	 * children of its parent.
	 * @param name the name of the new node.
	 */
	protected AbstractExplorerTreeNode(String prefix, ExplorerTreeNode parent, String name) {
		super(prefix + UUID.randomUUID().toString());
		this.name = name;
		this.parent = parent;
		children = new ArrayList<ExplorerTreeNode>();
	}

	/**
	 * Creates a new abstract tree node with the given parent and the given
	 * name and given id.
	 * 
	 * @param parent parent the parent of this abstract tree node. Note that
	 * implementors must still make sure that this new node is added to the
	 * children of its parent.
	 * @param id the id of the node.
	 * @param name the name of the new node.
	 */
	protected AbstractExplorerTreeNode(ExplorerTreeNode parent, String id, String name) {
		super(id);
		this.name = name;
		this.parent = parent;
		children = new ArrayList<ExplorerTreeNode>();
	}

	/**
	 * Returns the id of this tree node object. The id is globally unique.
	 * 
	 * @return the globally unique id of this tree node.
	 */
//	public String getId() {
//		return id;
//	}
	
	/**
	 * Returns the name of this tree node. The name will be translated to
	 * the currently set alias, or, if either no alias is set or the name
	 * does not exist in the alias, the default name is returned.
	 * 
	 * @return the name of the tree node.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets a new name for this tree node.
	 * 
	 * @param newName the new name of the node.
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * Returns the root node of the tree.
	 * 
	 * @return the root node of the tree.
	 */
	public ExplorerTreeNode getRoot() {
		ExplorerTreeNode node = this;
		while (node.getParent() != null) {
			node = node.getParent();
		}
		return node;
	}	
	
	/**
	 * Returns the parent of this node or null, if this node is the root.
	 * 
	 * @return the parent of this node.
	 */
	public ExplorerTreeNode getParent() {
		return parent;
	}
	
	/**
	 * Sets a new parent for this node.
	 * 
	 * @param newParent the new parent node for this node.
	 */
	public void setParent(ExplorerTreeNode newParent) {
		if (parent != null) {
			parent.removeChildById(getId());
		}
		parent = newParent;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	/**
	 * Returns all children of this node or an empty array, if the node has
	 * no children.
	 * 
	 * @return all children of this node.
	 */
	public ExplorerTreeNode [] getChildren() {
		return children.toArray(new ExplorerTreeNode[0]);
	}
	
	/**
	 * Adds the specified child to the list of children of this node.
	 * 
	 * @param child the tree node to add to this node.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	public boolean addChild(ExplorerTreeNode child) {
		if (!children.contains(child)) {
			return children.add(child);
		}
		return false;
	}
	
	/**
	 * Removes the specified child from the list of children of this
	 * node.
	 * 
	 * @param child the tree node which is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	public boolean removeChild(ExplorerTreeNode child) {
		return children.remove(child);
	}
	
	/**
	 * Removes the child identified by the given id.
	 * 
	 * @param id the id of the child that is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	public boolean removeChildById(String id) {
		int removeIndex = -1;
		for (int i = 0, n = children.size(); i < n; i++) {
			ExplorerTreeNode node = children.get(i);
			if (node.getId().equals(id)) {
				removeIndex = i;
				break;
			}
		}
		if (removeIndex > -1) {
			children.remove(removeIndex);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the child identified by the given name.
	 * 
	 * @param name the name of the child that is to be removed.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */
	public boolean removeChildByName(String name) {
		ExplorerTreeNode removeNode = null;
		for (ExplorerTreeNode node: children) {
			if (node.getName().equals(name)) {
				removeNode = node;
				break;
			}
		}
		if (removeNode != null) {
			return children.remove(removeNode);
		}
		return false;		
	}
	
	/**
	 * Removes all children of this node.
	 * 
	 * @return true if the operation succeeded, false otherwise.
	 */	
	public boolean clearAllChildren() {
		children.clear();
		return true;
	}
	
	/**
	 * Checks if two tree nodes are equal. They are equal if and only if their
	 * id matches.
	 */
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof ExplorerTreeNode)) {
			return false;
		}
		return getId().equals(((ExplorerTreeNode) other).getId());
	}
	
	/**
	 * Returns the hashcode for this tree node.
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + getId().hashCode();
		return hash;
	}
	
	public void addParameterValue(String parameterName, Object parameterValue) {
		if (parameterValue == null) {
			return;
		}
		Object o = getParameterValue(parameterName);
		if (o == null) {
			setParameter(parameterName, parameterValue);
		} else {
			if (o instanceof List) {
				if (!((List) o).contains(parameterValue)) {
					((List) o).add(parameterValue);
					setParameter(parameterName, o);
				}
			} else if (o instanceof Object []) {
				Object [] result = (Object []) o;
				Object [] nVal = new Object[result.length + 1];
				for (int i = 0; i < result.length; i++) {
					nVal[i] = result[i];
				}
				nVal[result.length] = parameterValue;
				setParameter(parameterName, nVal);
			} else {
				Object [] nVal = new Object[2];
				nVal[0] = o;
				nVal[1] = parameterValue;
				setParameter(parameterName, nVal);
			}
		}		
	}	
	
	public String getConnectionId() {
		return "1";
	}
	
	final void setConnectionId(String connectionId) {
		this.connectionId = connectionId;		
	}	
	
	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		public final String id;
//		private final Connection connection;
		public String xml;
		public String name;
		public User owner;
		public int type;
		public PaloConnection paloConn;
		public Set<Role> roles = new HashSet<Role>();
		public String parentId;
		
		static HashMap <User, ExplorerTreeNode> roots = new HashMap<User, ExplorerTreeNode>();
		
		public Builder(String id) {
			AccessController.checkAccess(ExplorerTreeNode.class);
			this.id = id;
		}

		public Builder definition(String xml) {
			this.xml = xml;
			return this;
		}
		
		public Builder parent(String parentId) {
			this.parentId = parentId;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}
		
		public Builder type(String type) {
			this.type = Integer.parseInt(type);
			return this;
		}
		
		public Builder connection(PaloConnection paloConn) {
			this.paloConn = paloConn;
			return this;
		}
		
		public Builder add(Role role) {
			roles.add(role);
			return this;
		}
		
		private final ExplorerTreeNode findNode(ExplorerTreeNode root, String id) {
			if (root == null || id == null) {
				return null;
			}
			if (id.equals(root.getId())) {
				return root;
			}
			for (ExplorerTreeNode kid: root.getChildren()) {
				ExplorerTreeNode f = findNode(kid, id);
				if (f != null) {
					return f;
				}
			}
			return null;
		}
		
		public ExplorerTreeNode build(AuthUser user) {
			if (!roots.containsKey(user) || roots.get(user) == null) {
				try {
					roots.put(user, FolderModel.getInstance().loadPure(user));
				} catch (PaloIOException e) {
					e.printStackTrace();
					return null;
				}				
			}
			AbstractExplorerTreeNode node = (AbstractExplorerTreeNode) findNode(roots.get(user), id);
			if (node == null) {
				try {
					roots.put(user, FolderModel.getInstance().loadPure(user));
					node = (AbstractExplorerTreeNode) findNode(roots.get(user), id);
				} catch (PaloIOException e) {
					e.printStackTrace();
				}
			}
			if (node == null) {
				if (type == 2) { // Static Folder
					node = new StaticFolder(roots.get(user), id, name);
				} else if (type == 3) { // Folder Element
					node = new FolderElement(roots.get(user), id, name);
				}				
			}
			if (node != null) {
				node.setOwner(owner);
				if (paloConn != null) {
					node.setConnectionId(paloConn.getId());
				} else {
					node.setConnectionId("");
				}
			} 
			return node;
		}
	}
	
	protected String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("\'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}
}
