/*
*
* @file CellConverter.java
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
* @version $Id: CellConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.converter.palo;

import org.palo.api.Cell;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.exceptions.OperationFailedException;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.core.server.converter.BaseConverter;

/**
 * <code>CellConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CellConverter.java,v 1.5 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class CellConverter extends BaseConverter {

	protected Class<?> getNativeClass() {
		return Cell.class;
	}

	protected Class<?> getXObjectClass() {
		return XCell.class;
	}

	public Object toNative(XObject obj) {
//		PaloService.get
		return null;
	}

	public XObject toXObject(Object nativeObj) {
		Cell cell = (Cell) nativeObj;		
		String value = cell.isEmpty() ? "" : cell.getValue().toString();
		int type = cell.getType() == Cell.NUMERIC ? XCell.TYPE_NUMERIC : XCell.TYPE_STRING;
		XCell xCell = new XCell(0, 0);
		xCell.value = value;
		xCell.type = type;
		xCell.isConsolidated = cell.isConsolidated();
		xCell.isRuleBased = cell.hasRule();
		
		return xCell;
	}

	protected String getNativeClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getXObjectClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object toNative(XObject obj, AuthUser loggedInUser)
			throws OperationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

}
