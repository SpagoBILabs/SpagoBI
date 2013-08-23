/*
*
* @file VerticalScroller.java
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
* @version $Id: VerticalScroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.ToggleIcon;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>VScroller</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: VerticalScroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
public class VerticalScroller extends Scroller {
	
	public VerticalScroller(XObjectContainer container) {
		super(container);
	}

	protected final Panel getContent() {
		HorizontalPanel content = new HorizontalPanel();
		content.setSpacing(5);
		return content;
	}

	public Point getScrollOffset() {
		Point offset = new Point();
		Widget widget = getContainer().getFirstWidget();
		offset.y = widget.getOffsetHeight();
		return offset;
	}

	public ToggleIcon getBackwardIcon() {
		return new ToggleIcon("images/down_on.gif", "images/down_off.gif", 14, 14);
	}

	public ToggleIcon getForwardIcon() {
		return new ToggleIcon("images/up_on.gif", "images/up_off.gif", 14, 14);
	}
}
