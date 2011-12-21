/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
