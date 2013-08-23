/*
*
* @file HorizontalScrollableRenderer.java
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
* @version $Id: HorizontalScrollableRenderer.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Rectangle;

/**
 * <code>HorizontalScrollableContainerRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HorizontalScrollableRenderer.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public abstract class HorizontalScrollableRenderer extends HorizontalContainerRenderer
		implements ScrollableContainerRenderer {

	public final boolean isVisible(Widget w, Rectangle bounds) {
		if (w != null) {
			int wx = w.getAbsoluteLeft();
			int ww = wx + w.getOffsetWidth();
			return wx >= bounds.x && ww <= (bounds.x + bounds.width);
		}
		return false;
	}

	public final Point getViewportSize(int width, int height, Point scrollerSize) {		
		int w = width - scrollerSize.x - 30;
		return new Point(w, getMinHeight());
	}
	
	public final Point arrange(AbsolutePanel content, AbsolutePanel viewport, Scroller scroller) {
		Point size = 
			new Point(viewport.getOffsetWidth(), viewport.getOffsetHeight());
		content.setWidgetPosition(viewport, 10, 0);
		Point scrSize = scroller.getSize();
		size.x += 20;
		content.setWidgetPosition(scroller, size.x, (size.y-scrSize.y)/2);
		size.x += scrSize.x;
		return size;
	}
}
