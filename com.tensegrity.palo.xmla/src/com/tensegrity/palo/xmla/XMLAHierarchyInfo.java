/*
*
* @file XMLAHierarchyInfo.java
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
* @version $Id: XMLAHierarchyInfo.java,v 1.8 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloConstants;

public class XMLAHierarchyInfo implements HierarchyInfo {
	private String name;
	private String id;
	private final DimensionInfo dimension;
	private int cardinality;
	
	public XMLAHierarchyInfo(DimensionInfo dim, String name, String uniqueName) {
		dimension = dim;
		this.id = uniqueName;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}

	public int getType() {
		return PaloConstants.TYPE_NORMAL;
	}

	public void rename(String name) {		
	}

	public boolean canBeModified() {
		return false;
	}

	public boolean canCreateChildren() {
		return false;
	}

	public DimensionInfo getDimension() {		
		return dimension;
	}

	public int getElementCount() {
		return cardinality;
	}
	
	public void setCardinality(String card) {
		try {
			cardinality = Integer.parseInt(card);
		} catch (NumberFormatException e) {			
		}
	}

	public int getMaxDepth() {
		return dimension.getMaxDepth();
	}

	public int getMaxLevel() {
		return dimension.getMaxLevel();
	}
}
