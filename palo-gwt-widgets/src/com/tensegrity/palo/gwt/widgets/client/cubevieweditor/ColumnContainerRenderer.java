/*
*
* @file ColumnContainerRenderer.java
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
* @version $Id: ColumnContainerRenderer.java,v 1.6 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.cubevieweditor;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerWidget;
import com.tensegrity.palo.gwt.widgets.client.container.HorizontalScrollableRenderer;
import com.tensegrity.palo.gwt.widgets.client.container.HorizontalScroller;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;

/**
 * <code>HierarchySmallContainerRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ColumnContainerRenderer.java,v 1.6 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
class ColumnContainerRenderer extends HorizontalScrollableRenderer {

	private static final int HEIGHT = 41;
	
	private XObjectContainer container;
	private final HierarchyWidgetListener widgetListener;
	
	public ColumnContainerRenderer(HierarchyWidgetListener widgetListener) {
		this.widgetListener = widgetListener;
	}
	
	public HorizontalScroller createScroller(XObjectContainer container) {
		HorizontalScroller smallScroller = new HorizontalScroller(container);
		smallScroller.setForwardIcons("images/small_left_on.png", "images/small_left_off.png", 9, 8);
		smallScroller.setBackwardIcons("images/small_right_on.png", "images/small_right_off.png", 9, 8);
		return smallScroller;
	}

	public ContainerWidget createWidget(XObject forModel) {
		HierarchyWidget widget = new ColumnHierarchyWidget(container, (XAxisHierarchy) forModel);
		widget.addWidgetListener(widgetListener);
		return widget;
	}
	
	public final int getMinHeight() {
		return HEIGHT;
	}
	
	public final int getMinWidth() {
		return 0;
	}	
}
