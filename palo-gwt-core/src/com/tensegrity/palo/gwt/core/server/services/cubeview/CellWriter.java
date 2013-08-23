/*
*
* @file CellWriter.java
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
* @version $Id: CellWriter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.palo.api.Cube;
import org.palo.api.Element;
import org.palo.viewapi.AuthUser;

import com.tensegrity.palo.gwt.core.client.models.palo.XCell;

/**
 * <code>DataWriter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CellWriter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class CellWriter {

	//--------------------------------------------------------------------------
	// SINGLETON FACTORY
	//
	private static final CellWriter instance = new CellWriter();
	
	public static final CellWriter getInstance() {
		return instance;
	}
	
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//		
	private CellWriter() {		
	}
	
	private static final DecimalFormat cellFormat = 
			new DecimalFormat("#,##0.00");
	
	public final void writeCell(XCell cell, Element[] coordinate, Cube cube, NumberFormat format, AuthUser user) {
		if(isStringCell(coordinate)) {
			writeString(cell, coordinate, cube);
		} else {
			writeNumeric(cell, coordinate, cube, format, user);
		}
	}

	
	private final void writeString(XCell cell, Element[] coordinate, Cube cube) {
		cube.setData(coordinate, cell.value);
	}

	private final void writeNumeric(XCell cell, Element[] coordinate, Cube cube, NumberFormat format, AuthUser user) {
		if (format == null) {
			format = cellFormat;
		}
		if(cell.value.equals(""))
			cell.value = "0";	//TODO should we ignore setting of empty numeric cell, i.e. simply return in this case??
		if(isConsolidatedCell(coordinate)) {			
//			if (user.getAccounts())
			//(1009) insufficient access rights
			cube.setDataSplashed(coordinate, cell.value, format);			
		} else {
			cube.setData(coordinate, cell.value, format);
		}
	}
	
	private final boolean isStringCell(Element[] coordinate) {
		for(Element element : coordinate) {
			if(element.getType() == Element.ELEMENTTYPE_STRING)
				return true;
		}
		return false;
	}
	
	private final boolean isConsolidatedCell(Element[] coordinate) {
		for(Element element : coordinate) {
			if(element.getChildCount() > 0)
				return true;
		}
		return false;		
	}
}
