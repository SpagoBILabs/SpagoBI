/*
*
* @file ReverseVerticalLayouter.java
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
* @version $Id: ReverseVerticalLayouter.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import com.tensegrity.palo.gwt.widgets.client.util.Point;


/**
 * <code>ReverseVerticalLayouter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReverseVerticalLayouter.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class ReverseVerticalLayouter extends VerticalLayouter {
	
	
	protected int layout(HeaderItem item, int x, int y, boolean isVisible) {
		Point iSize = new Point(item.getOffsetWidth(), item.getOffsetHeight()); //item.getSize();
		int tmpX = x + iSize.x;
		int tmpY = y;
		
		if(item.hasChildren()) {
			for(HeaderItem child : item.getChildren())
				tmpY = layout(child, x + INDENT, tmpY, isVisible && item.isExpanded());
		}

		int _h = tmpY; //have to set tmpY later if necessary
		if(item.hasRootsInNextLevel()) {
			int _x = x + getMaxLevelWidth(item.getLevel()) - (INDENT * (item.getDepth() + 1));
			for(HeaderItem root : item.getRootsInNextLevel()) {
				_h = layout(root, _x, _h, isVisible);
//				if(isVisible && _h > tmpY)
//					tmpY = _h;
//				else
//					root.setHeight(iSize.height+"px");
			}
		} else {
			//we count leaf indizes...
			item.setLeafIndex(leaf++);
			leafListener.visitedLeaf(item);
		}
		item.setVisible(isVisible);
		header.setWidgetPosition(item, x, tmpY);
		tmpY = isVisible ? tmpY + iSize.y : y;
		if(_h > tmpY) tmpY = _h;
		if(tmpX > size.x)
			size.x = tmpX;
		return tmpY;
	}
}
