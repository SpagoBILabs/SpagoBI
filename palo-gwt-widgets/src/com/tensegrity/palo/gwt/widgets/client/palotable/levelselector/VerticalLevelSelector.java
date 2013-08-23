/*
*
* @file VerticalLevelSelector.java
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
* @version $Id: VerticalLevelSelector.java,v 1.4 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable.levelselector;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tensegrity.palo.gwt.widgets.client.separator.HorizontalSeparator;
import com.tensegrity.palo.gwt.widgets.client.separator.Separator;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;

/**
 * <code>VerticalLevelButtonPanel</code> TODO DOCUMENT ME
 * 
 * @version $Id: VerticalLevelSelector.java,v 1.4 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class VerticalLevelSelector extends LevelSelector {

	public VerticalLevelSelector() {
	}
	
	protected Separator createSeparator() {
		return new HorizontalSeparator();
	}
	
	public final Point layout(int level, int atX, int atY) {
		Point size = new Point(ICON_SIDE_X + SPACING, atY);
//		// separator:
//		Separator separator = getSeparator(level);
//		if (atX > 0)
//			atX -= separator.getWidth() / 2;		
//		Ruler.setClientWidth(separator, ICON_SIDE);
//		content.setWidgetPosition(separator, atX, size.y);
//		size.y += separator.getHeight() + SPACING;
		
		Label selectorIcon = getSelectorIcon(level);
		content.setWidgetPosition(selectorIcon, atX, size.y);
		size.y += ICON_SIDE_Y;
		return size;
	}

}
