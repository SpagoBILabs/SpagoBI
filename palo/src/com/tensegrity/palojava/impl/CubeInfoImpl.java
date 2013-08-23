/*
*
* @file CubeInfoImpl.java
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
* @version $Id: CubeInfoImpl.java,v 1.6 2010/02/22 11:38:54 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.impl;

import java.math.BigInteger;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;

public class CubeInfoImpl implements CubeInfo {

	
	private final String id;
	private int type;
	private final DatabaseInfo database;
	private final String[] dimensions;  //dimension ids...
	
	private String name;
	private	int dimCount;
//	private int cellCount;
//	private int filledCellCount;
	private BigInteger cellCount;
	private BigInteger filledCellCount;
	private int status;
	private int token;
 
	
	public CubeInfoImpl(DatabaseInfo database, String id, 
			int type, String[] dimensions) {
		this.id = id;
		this.type = type;
		this.database = database;
		this.dimensions = dimensions;
	}

	public void setType(int newType) {
		this.type = newType;
	}
	
	public final String getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final int getType() {
		return type;
	}
	
	public final DatabaseInfo getDatabase() {
		return database;
	}

	public final String[] getDimensions() {
		return dimensions;
	}

	public final synchronized void setCellCount(BigInteger cellCount) {
		this.cellCount = cellCount;
	}

	public final synchronized void setDimensionCount(int dimCount) {
		this.dimCount = dimCount;
	}

	public final synchronized void setFilledCellCount(BigInteger filledCellCount) {
		this.filledCellCount = filledCellCount;
	}

	public final synchronized void setName(String name) {
		this.name = name;
	}
	
	public final synchronized void setStatus(int status) {
		this.status = status;
	}

	public final synchronized void setToken(int token) {
		this.token = token;
	}

	public final synchronized BigInteger getCellCount() {
		return cellCount;
	}

	public final synchronized int getDimensionCount() {
		return dimCount;
	}

	public final synchronized BigInteger getFilledCellCount() {
		return filledCellCount;
	}

	public final synchronized int getStatus() {
		return status;
	}

	public final synchronized int getToken() {
		return token;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}

}
