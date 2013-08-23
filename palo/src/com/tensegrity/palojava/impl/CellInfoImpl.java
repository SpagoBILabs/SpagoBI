/*
*
* @file CellInfoImpl.java
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
* @author ArndHouben
*
* @version $Id: CellInfoImpl.java,v 1.13 2009/09/29 14:45:38 ArndHouben Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 * All rights reserved
 */
package com.tensegrity.palojava.impl;

import com.tensegrity.palojava.CellInfo;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: CellInfoImpl.java,v 1.13 2009/09/29 14:45:38 ArndHouben Exp $
 */
public class CellInfoImpl implements CellInfo {

	private final int type;
	private final boolean exists;
	private final Object value;
	private String[] coordinate;
	private String rule;
	
	public CellInfoImpl(int type, boolean exists, Object value) {
		this.exists = exists;
		this.value = value;
		this.type = type;
	}
	
	public final boolean exists() {
		return exists;
	}

	public final int getType() {
		return type;
	}

	public final Object getValue() {
		return value;
	}
	
	public final String getId() {
		return null;
	}
	
	public final String[] getCoordinate() {
		return coordinate;
	}
	
	public final void setCoordinate(String[] coord) {
		coordinate = coord != null ? coord.clone() : null;
	}

	public final String toString() {
		return value.toString();
	}

	public final boolean canBeModified() {
		return true;
	}

	public final boolean canCreateChildren() {
		return false;
	}

	public final String getRule() {
		return rule;
	}
	
	public final void setRule(String id) {
		if(id.equals(""))
			id = null;
		this.rule = id;
	}
}
