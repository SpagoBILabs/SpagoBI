/*
*
* @file RangePosition.java
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
* @version $Id: RangePosition.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

/**
 * <code>RangePosition</code> TODO DOCUMENT ME
 * 
 * @version $Id: RangePosition.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public enum RangePosition {
	NONE, TOP, TOP_LEFT, TOP_RIGHT, LEFT, RIGHT, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT, MIDDLE;

	static RangePosition getPositionInRange(int index, int r1, int c1, int r2,
			int c2) {
		int rows = r2 - r1;
		int cols = c2 - c1;
		int indexRow = index / (cols + 1);
		int indexCol = index % (cols + 1);
		if (indexRow == 0) {
			if (indexCol == 0)
				return TOP_LEFT;
			if (indexCol == cols)
				return TOP_RIGHT;
			if (indexCol > 0 && indexCol < cols)
				return TOP;
		} else if (indexRow == rows) {
			if (indexCol == 0)
				return BOTTOM_LEFT;
			if (indexCol == cols)
				return BOTTOM_RIGHT;
			if (indexCol > 0 && indexCol < cols)
				return BOTTOM;
		} else if (indexRow > 0 && indexRow < rows) {
			if (indexCol == 0)
				return LEFT;
			if (indexCol == cols)
				return RIGHT;
			if (indexCol > 0 && indexCol < cols)
				return MIDDLE;
		}
		return NONE;
	}
}
