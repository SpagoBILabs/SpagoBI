/*
*
* @file LockInfoImpl.java
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
* @version $Id: LockInfoImpl.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palojava.impl;

import com.tensegrity.palojava.LockInfo;

/**
 * <code>LockInfo</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: LockInfoImpl.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public class LockInfoImpl implements LockInfo {

	private final String id;	
	private final String user;
	
	private int steps;
	private String[][] area;
	
	public LockInfoImpl(String id, String user) {
		this.id = id;
		this.user = user;		
	}
	
	public final String getId() {
		return id;
	}
	
	public final String getUser() {
		return user;
	}
	
	public final void setSteps(int steps) {
		this.steps = steps;
	}

	public final int getSteps() {
		return steps;
	}

	public final void setArea(String[][] area) {
		this.area = area;
	}
	
	public final String[][] getArea() {
		return area; 
	}
	
	public final int hashCode() {
		int hc = 17;
		hc += 23 * id.hashCode();
		hc += 23 * user.hashCode();
		return hc;
	}
}
