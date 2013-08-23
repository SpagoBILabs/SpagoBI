/*
*
* @file PropertyInfoImpl.java
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
* @version $Id: PropertyInfoImpl.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/**
 * 
 */
package com.tensegrity.palojava.impl;

import java.util.LinkedHashSet;

import com.tensegrity.palojava.PropertyInfo;

/**
 * @author PhilippBouillon
 *
 */
public class PropertyInfoImpl implements PropertyInfo {
	// Property types:
	public static final int 
		TYPE_NUMERIC = 1,
		TYPE_STRING = 2,
		TYPE_BOOLEAN = 3;

	private final LinkedHashSet <PropertyInfo> children;
	private String id;
	private String value;
	private PropertyInfo parent;
	private int type;
	private boolean readOnly;
	
	public PropertyInfoImpl(String id, String value, PropertyInfo parent,
			                int type, boolean readOnly) {
		children = new LinkedHashSet <PropertyInfo> ();
		this.id = id;
		this.value = value;
		this.parent = parent;
		this.type = type;
		this.readOnly = readOnly;
	}
	
	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#addChild(com.tensegrity.palojava.PropertyInfo)
	 */
	public void addChild(PropertyInfo child) {
		children.add(child);
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#clearChildren()
	 */
	public void clearChildren() {
		children.clear();
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#getChildCount()
	 */
	public int getChildCount() {
		return children.size();
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#getChildren()
	 */
	public PropertyInfo[] getChildren() {
		return children.toArray(new PropertyInfo[0]);
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#getParent()
	 */
	public PropertyInfo getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#removeChild(com.tensegrity.palojava.PropertyInfo)
	 */
	public void removeChild(PropertyInfo child) {
		if (!readOnly) {
			children.remove(child);
		}
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PropertyInfo#setValue(java.lang.String)
	 */
	public void setValue(String newValue) {
		if (!readOnly) {
			value = newValue;
		}
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PaloInfo#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.tensegrity.palojava.PaloInfo#getType()
	 */
	public int getType() {
		return type;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}

	public PropertyInfo getChild(String id) {
		for (PropertyInfo info: children) {
			if (info.getId().equals(id)) {
				return info;
			}
		}
		return null;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}
}
