/*
*
* @file XCell.java
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
* @version $Id: XCell.java,v 1.7 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.palo;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellFormat;

/**
 * <code>XViewCell</code>
 * A simple cell model representation for GWTs RPC feature.
 *
 * @version $Id: XCell.java,v 1.7 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class XCell extends XObject implements IsSerializable {

	public static final int TYPE_NUMERIC = 1;
	/** Constant for value type STRING */
	public static final int TYPE_STRING = 2;

	public int type;
	public String value;
	public boolean isRuleBased;
	public boolean isConsolidated;
	public int row;
	public int col;
	
	public XCellFormat format;
	public XCell() {
	}
	
	public XCell(int row, int col) {// , String value, int type) {
		this.row = row;
		this.col = col;
//		this.type = type;
//		this.value = value;
	}

	public String getType() {
		return XCell.class.getName();
	}
}
