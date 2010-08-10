/*
*
* @file SimpleTreeNode.java
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
* @version $Id: SimpleTreeNode.java,v 1.1 2010/01/13 08:02:41 PhilippBouillon Exp $
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

/**
 * <code>TreeNode</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: SimpleTreeNode.java,v 1.1 2010/01/13 08:02:41 PhilippBouillon Exp $
 **/
public class SimpleTreeNode extends BaseTreeModel<SimpleTreeNode> implements IsSerializable, Serializable {		
	/** generated serial id */
	private static final long serialVersionUID = -2675935643691683979L;
	private static final String DISPLAY_PROPERTY = "name";
	private static int ID = 0;
	
	private String dataObjectName;
	private String dataObjectId;
	private String dataObjectType;
	private int dataObjectChildCount;
	private int id;
	private XObject xObject;
	
//	private ArrayList <String> children;
	
	public SimpleTreeNode() {
		dataObjectName = null;
		dataObjectId = null;
		dataObjectType = null;
		dataObjectChildCount = 0;
		this.parent = null;
		this.id = ID++;
	}
	
	public SimpleTreeNode(SimpleTreeNode parent, String dataObjectType, String dataObjectId, String dataObjectName, int childCount, XObject xobj) {
		this.parent = parent;
		this.dataObjectId = dataObjectId;
		this.dataObjectName = dataObjectName;
		this.dataObjectType = dataObjectType;
		this.dataObjectChildCount = childCount;
		this.id = ID++;
		this.xObject = xobj;
	}

	public XObject getXObject() {
		return xObject;
	}
	
	public int getId() {
		return id;
	}
		
	public String getDataObjectName() {
		return dataObjectName;
	}
	
	public String getDataObjectId() {
		return dataObjectId;
	}

	public String getDataObjectType() {
		return dataObjectType;
	}
	
	public int getDataObjectChildCount() {
		return dataObjectChildCount;
	}
	
	public void setDataObject(String type, String id, String name, int childCount) {
		this.dataObjectType = type;
		this.dataObjectId = id;
		this.dataObjectName = name;
		this.dataObjectChildCount = childCount;
	}
	
	@SuppressWarnings("unchecked")
	public String get(String key) {
		if(key.equals(DISPLAY_PROPERTY)) {
			return dataObjectName;
		}
		if (key.equals("nameAndKids")) {
			return dataObjectChildCount == 0 ? dataObjectName : dataObjectName + " <i><font color=\"gray\">(" + dataObjectChildCount + ")</i></font>";
		}
		
		return "";
	}
			
	public String toString() {
		return get(DISPLAY_PROPERTY);
	}
        	    
	public boolean hasChildren() {
		return dataObjectChildCount > 0;
	}	
	
	public boolean isLeaf() {
		return dataObjectChildCount == 0;
	}
	
	public int hashCode() {
		int result =  7 * (dataObjectType == null ? 1 : dataObjectType.hashCode()) + 
			         11 * (dataObjectId == null ? 1 : dataObjectId.hashCode()) + 
				     17 * (dataObjectName == null ? 1 : dataObjectName.hashCode()) + 
				     31 * (parent == null ? 1 : parent.hashCode());
		return result;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof SimpleTreeNode)  {
			SimpleTreeNode tn = (SimpleTreeNode) o;
			return (dataObjectType == null ? tn.dataObjectType == null : dataObjectType.equals(tn.dataObjectType)) &&
				   (dataObjectId == null ? tn.dataObjectId == null : dataObjectId.equals(tn.dataObjectId)) &&
			       (dataObjectName == null ? tn.dataObjectName == null : dataObjectName.equals(tn.dataObjectName)) &&
			       (parent == null ? tn.parent == null : parent.equals(tn.parent));
		}
		return false;
	}
	
	public void fastSetChildren(List <SimpleTreeNode> kids) {
		children = kids;
	}
}
