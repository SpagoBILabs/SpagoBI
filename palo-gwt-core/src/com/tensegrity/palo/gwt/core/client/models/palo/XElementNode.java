/*
*
* @file XElementNode.java
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
* @version $Id: XElementNode.java,v 1.11 2010/02/16 13:54:00 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.palo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XElementNode</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XElementNode.java,v 1.11 2010/02/16 13:54:00 PhilippBouillon Exp $
 **/
public class XElementNode extends XObject {

	public static final String TYPE = XElementNode.class.getName();
	
	/* the wrapped element */
	private XElement xElement;
	private String axisHierarchyId;
	private String viewId;

	/* hierarchy */
	private XElementNode parent;
	private List<XElementNode> children = new ArrayList<XElementNode>();
	private int childCount;
	
	private static long id;
	
	/** for serialization only */
	public XElementNode() {		
	}
	
	public XElementNode(XElement xElement, String xAxisHierarchyId, String viewId) {
		setId(Long.toString(id++)); //xAxisHierarchy.getId() + ":" + xElement.getId());
		setName(xElement.getName());
		this.xElement = xElement;
		this.axisHierarchyId = xAxisHierarchyId;
		this.viewId = viewId;
	}
	
	public void setAxisHierarchyId(String hierId, String viewId) {
		this.axisHierarchyId = hierId;
		this.viewId = viewId;
	}
	
	public String getAxisHierarchyId() {
		return axisHierarchyId;
	}
	
	public String getViewId() {
		return viewId;
	}
	
	public final XElement getElement() {
		return xElement;
	}
	
	public final String getType() {
		return TYPE;
	}

	public final XElementNode getParent() {
		return parent;
	}
	public final void setParent(XElementNode parent) {
		this.parent = parent;
	}
	
	public final void addChild(XElementNode xElementNode) {
		if(!children.contains(xElementNode))
			children.add(xElementNode);
	}
	public final void forceAddChild(XElementNode xElementNode) {
		children.add(xElementNode);
	}
	
	public final XElementNode[] getChildren() {
		return children.toArray(new XElementNode[0]);
	}
	
	public final void removeChildren() {
    	Iterator<XElementNode> allChildren = children.iterator();
    	while(allChildren.hasNext()) {
    		XElementNode child = allChildren.next();
    		child.setParent(null);
    		allChildren.remove();
    	}
	}
	
	public void setChildCount(int cc) {
		childCount = cc;
	}

	public int getChildCount() {
		return childCount;
	}
}
