/*
*
* @file HorizontalScroller.java
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
* @version $Id: HorizontalScroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.ToggleIcon;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>Scroller</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HorizontalScroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
public class HorizontalScroller extends Scroller {
	
	public HorizontalScroller(XObjectContainer container) {
		super(container);
	}

	protected final Panel getContent() {
		VerticalPanel content = new VerticalPanel();
		content.setSpacing(5);
		return content;
	}

	public Point getScrollOffset() {
		Point offset = new Point();
		// we scroll one widget to the right:
		Widget widget = getContainer().getFirstWidget();
		offset.x = widget.getOffsetWidth();
		return offset;
	}

	public ToggleIcon getBackwardIcon() {
		return new ToggleIcon("images/right_on.gif", "images/right_off.gif", 14, 14);
	}

	public ToggleIcon getForwardIcon() {
		return new ToggleIcon("images/left_on.gif", "images/left_off.gif", 14, 14);
	}
}
