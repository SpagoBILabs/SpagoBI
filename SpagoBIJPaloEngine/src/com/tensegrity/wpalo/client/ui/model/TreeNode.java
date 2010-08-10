/*
*
* @file TreeNode.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: TreeNode.java,v 1.25 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.model;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.account.XConnection;
import com.tensegrity.palo.gwt.core.client.models.admin.XGroup;
import com.tensegrity.palo.gwt.core.client.models.admin.XRole;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolderElement;
import com.tensegrity.palo.gwt.core.client.models.folders.XStaticFolder;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.wpalo.client.ui.mvc.fasttree.FastMSTreeItem;

/**
 * <code>TreeNode</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TreeNode.java,v 1.25 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class TreeNode extends BaseTreeModel<TreeNode> implements IsSerializable, Serializable {		
	/** generated serial id */
	private static final long serialVersionUID = -2675935643691683979L;
	private static final String DISPLAY_PROPERTY = "name";
	private static int ID = 0;
	
	private String type;
	private XObject xObject;
	private int id;
	private String path;
	private transient FastMSTreeItem item;
	
	public TreeNode() {
		xObject = null;
		this.parent = null;
		this.id = ID++;
	}
	
	public TreeNode(TreeNode parent, XObject xObject) {
		this.parent = parent;
		this.xObject = xObject;		
		this.type = xObject.getType();
		this.id = ID++;
	}

	public TreeNode(TreeNode parent, XObject xObject, boolean createPath) {
		this.parent = parent;
		this.xObject = xObject;		
		this.type = xObject.getType();
		this.id = ID++;
		if (createPath && xObject instanceof XElementNode) {
			createPath();
		}
	}

	public TreeNode(String parentId, XObject xObject, int rep) {
		this.parent = null;
		this.xObject = xObject;		
		this.type = xObject.getType();
		this.id = ID++;
		if (xObject instanceof XElementNode) {
			createPath(parentId, rep);
		}
	}
	
	public final void setPath(String path) {
		this.path = path;
	}
	
	private final void createPath(String parentPath, int pRep) {
		// TODO Root node repetitions: What happens if we have
		// "Europe", "Europe" on the root level (need to add (2) to the
		// second Europe).
		StringBuffer buffer = new StringBuffer();				
		buffer.append(((XElementNode) getXObject()).getElement().getId());
		if (getParent() != null) {
			int rep = 0;
			for (TreeNode kid: getParent().getChildren()) {
				if (kid.equals(this)) {
					break;
				}
				if (((XElementNode) kid.getXObject()).getElement().equals(
						((XElementNode) getXObject()).getElement())) {
					rep++;
				}					
			}
			if (rep != 0) {
				buffer.append("(");
				buffer.append(rep);
				buffer.append(")");
			}
			buffer.append(":");
			this.path = getParent().getPath() + buffer.toString();
			return;
		} else if (parentPath != null) {
			if (pRep != 0) {
				buffer.append("(");
				buffer.append(pRep);
				buffer.append(")");				
			}
			buffer.append(":");
			this.path = parentPath + buffer.toString();
			return;
		} else {
			buffer.append(":");
		}
		this.path = buffer.toString();		
	}
	
	private final void createPath() {
		createPath(null, 0);
	}
	
	public int getId() {
		return id;
	}
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public XObject getXObject() {
		return xObject;
	}
	
	public void setXObject(XObject xObject) {
		this.xObject = xObject;
	}
	
	@SuppressWarnings("unchecked")
	public String get(String key) {
		if(key.equals(DISPLAY_PROPERTY)) {
//			if (xObject instanceof XAccount) {
//				XAccount acc = (XAccount) xObject;
//				StringBuffer ret = new StringBuffer();
//				if (acc.getUser() != null && acc.getUser().getName() != null) {
//					ret.append(acc.getUser().getName());
//				} else {
//					ret.append("unknown");
//				}
//				ret.append(" - ");
//				if (acc.getConnection() != null && acc.getConnection().getName() != null) {
//					ret.append(acc.getConnection().getName());
//				} else {
//					ret.append("unknown");
//				}
//				return ret.toString();
//			}
			return xObject.getName();
		}
		if (key.equals("nameAndKids")) {
			if (xObject instanceof XElementNode) {
				int count = ((XElementNode) xObject).getChildCount();
				return count == 0 ? xObject.getName() : xObject.getName() + " <i><font color=\"gray\">(" + count + ")</i></font>";
			} else {
				return xObject.getName();
			}
		}
		
		return super.get(key);
	}
	
	public final String getType() {
		return type;
	}
	
	public final void add(TreeNode[] children) {
		for (int i = 0; i < children.length; i++) {			
			add(children[i]);
		}
	}
	
	public final void addChild(TreeNode child) {
		children.add(child);
	}
	
	public String toString() {
		return get(DISPLAY_PROPERTY);
	}
    
    public void setChildren(List<TreeNode> children) {
    	super.setChildren(children);
    }
    	    
	public boolean hasChildren() {
		return xObject.hasChildren();
	}	
	
	public boolean isLeaf() {
		return !xObject.hasChildren();
	}
	
	public int hashCode() {
		int typeCode = (type == null ? 0 : type.hashCode());
		int xObjectCode = (xObject == null ? 0 : xObject.hashCode());		
		int parentCode = 0;
		if (xObject != null) {
			if (!(xObject instanceof XFolderElement) &&
				!(xObject instanceof XStaticFolder) &&
				!(xObject instanceof XGroup) &&
				!(xObject instanceof XUser) &&
				!(xObject instanceof XRole) &&
				!(xObject instanceof XConnection) &&
				!(xObject instanceof XAccount) &&
				!"com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel".equals(xObject.getType())) {
				parentCode = (parent == null ? 0 : parent.hashCode());
			}
		}
		int result = 11 * typeCode + 17 * xObjectCode + 31 * parentCode;
		return result;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof TreeNode)  {
			TreeNode tn = (TreeNode) o;
			boolean eq = (type == null ? tn.type == null : type.equals(tn.type));
			eq &= (xObject == null ? tn.xObject == null : xObject.equals(tn.xObject));
			if (xObject != null) {
				if (!(xObject instanceof XFolderElement) &&
					!(xObject instanceof XStaticFolder) &&
					!(xObject instanceof XGroup) &&
					!(xObject instanceof XUser) &&
					!(xObject instanceof XRole) &&
					!(xObject instanceof XConnection) &&
					!(xObject instanceof XAccount) &&					
					!"com.tensegrity.wpalo.client.ui.mvc.viewbrowser.ViewBrowserModel".equals(xObject.getType())) {
						eq &= (parent == null ? tn.parent == null : parent.equals(tn.parent));
				}
			}
			return eq;
		}
		return false;
	}
		
	public String getPath() {
		return path == null ? "" : path;
	}
	
	public void setItem(FastMSTreeItem item) {
		this.item = item;
	}
	
	public FastMSTreeItem getItem() {
		return item;
	}
}
