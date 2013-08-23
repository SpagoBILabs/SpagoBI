/*
*
* @file IndentComparator.java
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
* @version $Id: IndentComparator.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.api.subsets.filter;

import org.palo.api.Element;
import org.palo.api.subsets.Subset2;

/**
 * <code>IndentComparator</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: IndentComparator.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class IndentComparator {

	private final Subset2 subset;
	
	IndentComparator(Subset2 subset) {
		this.subset = subset;
	}

	/**
	 * Compares the level of given {@link Element} with the specified one.
	 * The result depends of the indent setting of the currently used subset.
	 * @param el
	 * @param level
	 * @return  a negative integer, zero, or a positive integer as this element
     *		has a less than, equal to, or greater level than the specified one.
	 */
	public int compare(Element el, int level) {
		if(subset == null)
			return el.getLevel();
		
		int elLevel = -1;
		// get indent of subset:
		switch (subset.getIndent()) {
//		case 0: //NONE
//		case 1: //INDENT
//			int indent = el.getDepth() + 1;
//			return compare(indent, level);
		case 2: //LEVEL
			elLevel = el.getLevel();
			break;
		case 3: //DEPTH
			elLevel = el.getDepth();
			break;
		default: //NONE or //INDENT => "0" or "1"
			elLevel = el.getDepth() + 1;
		}
		return compare(elLevel, level);
	}
	
	private final int compare(int l1, int l2) {
		if(l1<l2)
			return -1;
		else if(l1>l2)
			return 1;
		return 0;
	}
}
