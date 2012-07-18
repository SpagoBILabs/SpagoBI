/*
*
* @file DimensionRepositoryRenderer.java
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
* @version $Id: DimensionRepositoryRenderer.java,v 1.8 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.cubeview;

import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerWidget;
import com.tensegrity.palo.gwt.widgets.client.container.VerticalContainerRenderer;
import com.tensegrity.palo.gwt.widgets.client.cubevieweditor.HierarchySelectionWidget;
import com.tensegrity.palo.gwt.widgets.client.util.Point;


/**
 * <code>DimensionRepositoryRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: DimensionRepositoryRenderer.java,v 1.8 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class DimensionRepositoryRenderer extends VerticalContainerRenderer {

	private static final int WIDTH = 15;
	private static final String STYLE = "empty-label";
	private final boolean hide;
	
	public DimensionRepositoryRenderer(boolean hide) {
		this.hide = hide;
	}
	
	public final int getMinWidth() {
		return WIDTH;
	}

	public final ContainerWidget createWidget(XObject forModel) {
		return new HierarchySelectionWidget(container,
				(XAxisHierarchy) forModel);
	}

	public final int getMinHeight() {
		return 0;
	}
	
	protected String getStyle() {
		return STYLE;
	}
	
	public Point render(int width, int height) {
		if (hide) {
			return new Point(0,0);
		}
		int left = INDENT, top = INDENT;
		if(width < getMinWidth())
			width = getMinWidth();
		// renderer empty label if visible
		Widget emptyLabel = container.getEmptyLabel();
		if (emptyLabel != null && emptyLabel.isVisible()) {
			emptyLabel.setWidth(width + "px");
			container.setWidgetPosition(emptyLabel, 0, top);
		}
		ContainerWidget[] widgets = container.getWidgets();
		for (int i = 0; i < widgets.length; i++) {
			ContainerWidget widget = widgets[i];
			if (i < firstVisible) {
				widget.setVisible(false);
			} else {
				widget.setVisible(true);
				container.setWidgetPosition(widget, left, top);
				top += widget.getOffsetHeight() + INDENT;
			}
		}
		return new Point(width, top);
	}

}
