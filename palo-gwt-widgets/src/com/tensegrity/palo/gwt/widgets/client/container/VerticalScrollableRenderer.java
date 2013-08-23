/*
*
* @file VerticalScrollableRenderer.java
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
* @version $Id: VerticalScrollableRenderer.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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
 * <code>VerticalScrollableRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: VerticalScrollableRenderer.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public abstract class VerticalScrollableRenderer extends VerticalContainerRenderer
		implements ScrollableContainerRenderer {

	public final boolean isVisible(Widget w, Rectangle bounds) {
		if (w != null) {
			int wy = w.getAbsoluteTop();
			int wh = wy + w.getOffsetHeight();
			return wy >= bounds.y && wh <= (bounds.y + bounds.height);
		}
		return false;
	}

	public Point getViewportSize(int width, int height, Point scrollerSize) {
		int h = height - scrollerSize.y - 30;
		return new Point(getMinWidth(), h);
	}

	public final Point arrange(AbsolutePanel content, AbsolutePanel viewport, Scroller scroller) {
		Point size = 
			new Point(viewport.getOffsetWidth(), viewport.getOffsetHeight());
		content.setWidgetPosition(viewport, 0, 10);
		Point scrSize = scroller.getSize();
		size.y += 20;
		content.setWidgetPosition(scroller, (size.x - scrSize.x)/2, size.y);
		size.y += scrSize.y;
		return size;
	}
}
