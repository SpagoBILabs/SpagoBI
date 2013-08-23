/*
*
* @file DatabaseInfoImpl.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: DatabaseInfoImpl.java,v 1.6 2009/11/23 08:25:26 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.impl;

import com.tensegrity.palojava.DatabaseInfo;

public class DatabaseInfoImpl implements DatabaseInfo {

	private final String id;
	private final int type;
	private String name;
	
	private int cubeCount;
	private int dimCount;
	private int status;
	private int token;

	public DatabaseInfoImpl(String id, int type) {
		this.id = id;
		this.type = type;
	}

	public final synchronized int getCubeCount() {
		return cubeCount;
	}

	public final synchronized void setCubeCount(int cubeCount) {
		this.cubeCount = cubeCount;
	}

	public final synchronized int getDimensionCount() {
		return dimCount;
	}

	public final synchronized void setDimensionCount(int dimCount) {
		this.dimCount = dimCount;
	}

	public final synchronized int getStatus() {
		return status;
	}

	public final synchronized void setStatus(int status) {
		this.status = status;
	}

	public final synchronized int getToken() {
		return token;
	}

	public final synchronized void setToken(int token) {
		this.token = token;
	}


	public final synchronized void setName(String name) {
		this.name = name;
	}

	
	public final String getId() {
		return id;
	}

	public final synchronized String getName() {
		return name;
	}

	public final int getType() {
		return type;
	}

	public boolean isSystem() {
		return type == TYPE_SYSTEM;
	}
	
	public boolean isUserInfo() {
		return type == TYPE_INFO;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}
	

}
