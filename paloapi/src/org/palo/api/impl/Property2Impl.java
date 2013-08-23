/*
*
* @file Property2Impl.java
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
* @version $Id: Property2Impl.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/**
 * 
 */
package org.palo.api.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.palo.api.Connection;
import org.palo.api.Property2;

import com.tensegrity.palojava.PropertyInfo;

/**
 * @author PhilippBouillon
 *
 */
public class Property2Impl implements Property2 {
	private PropertyInfo propertyInfo;
	private Property2 parent;
	private final LinkedHashSet <Property2> children;
	
    final static Property2Impl create(Property2 parent, 
    		PropertyInfo propertyInfo) {
    	return new Property2Impl(parent, propertyInfo);
    }
    
    final static Property2Impl create(Connection con, String id, String value, Property2 parent,
    		int type, boolean readOnly) {
    	PropertyInfo par;
    	if (parent == null) {
    		par = null;
    	} else {
    		par = ((Property2Impl) parent).getPropInfo();
    	}    		 
    	PropertyInfo prop = ((ConnectionImpl) con).getConnectionInternal().
    		createNewProperty(id, value, par, type, readOnly);
    	return new Property2Impl(parent, prop);
    }
    
    private Property2Impl(Property2 parent, PropertyInfo propertyInfo) {
    	this.parent = parent;
    	this.propertyInfo = propertyInfo;
    	children = new LinkedHashSet <Property2> ();
    }
    
    /* (non-Javadoc)
	 * @see org.palo.api.Property2#addChild(org.palo.api.Property2)
	 */
	public void addChild(Property2 child) {
		children.add(child);
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#clearChildren()
	 */
	public void clearChildren() {
		if (!propertyInfo.isReadOnly()) {
			children.clear();
		}
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#getChildCount()
	 */
	public int getChildCount() {
		return children.size();
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#getChildren()
	 */
	public Property2[] getChildren() {
		return children.toArray(new Property2[0]);
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#getParent()
	 */
	public Property2 getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#getValue()
	 */
	public String getValue() {
		return propertyInfo.getValue();
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#removeChild(org.palo.api.Property2)
	 */
	public void removeChild(Property2 child) {
		if (!propertyInfo.isReadOnly()) {
			children.remove(child.getId());
		}
	}

	/* (non-Javadoc)
	 * @see org.palo.api.Property2#setValue(java.lang.String)
	 */
	public void setValue(String newValue) {
		if (!propertyInfo.isReadOnly()) {
			propertyInfo.setValue(newValue);
		}
	}

	/* (non-Javadoc)
	 * @see org.palo.api.PaloObject#getId()
	 */
	public String getId() {
		return propertyInfo.getId();
	}

	/* (non-Javadoc)
	 * @see org.palo.api.NamedEntity#getName()
	 */
	public String getName() {
		return propertyInfo.getId();
	}
	
	public boolean isReadOnly() {
		return propertyInfo.isReadOnly();
	}
	
	PropertyInfo getPropInfo() {
		return propertyInfo;		
	}
	
	public String getChildValue(String childId) {
		for (Property2 prop: children) {
			if (prop.getId().equals(childId)) {
				return prop.getValue();
			}
		}
		return "";
	}

	public Property2 [] getChildren(String childId) {
		ArrayList <Property2> kids = new ArrayList <Property2> ();
		for (Property2 prop: children) {
			if (prop.getId().equals(childId)) {
				kids.add(prop);
			}
		}
		return kids.toArray(new Property2[0]);
	}
	
	final void clearCache() {
		System.err.println("clearCache: nothing todo for Property2...");
	}

	public boolean canBeModified() {
		return propertyInfo.canBeModified();
	}

	public boolean canCreateChildren() {
		return propertyInfo.canCreateChildren();
	}
	
	public int getType() {
		return propertyInfo.getType();
	}
}
