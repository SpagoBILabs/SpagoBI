/*
*
* @file HierarchySelectionWidget.java
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
* @version $Id: HierarchySelectionWidget.java,v 1.12 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.cubevieweditor;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;

/**
 * <code>HierarchySelectionWidget</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HierarchySelectionWidget.java,v 1.12 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class HierarchySelectionWidget extends ColumnHierarchyWidget {

	private static final String STYLE = "palo-gwt-hierarchy-widget-selection";
	private final Image dimIcon = new Image("images/dimension.gif", 0, 0, 12, 14);
	private final Image selectIcon = new Image("images/arrow.gif", 0, 0, 11, 17);
	private final Image elementIcon = new Image("images/element.gif", 0, 0, 12, 12);
	private final Label elementName = new Label();

	public HierarchySelectionWidget(XObjectContainer container, XAxisHierarchy hierarchy) {
		super(container, hierarchy);
		initComponents();
		initEventHandling();
	}

	public final void setSelectedElement(XElement selectedElement) {
		if(selectedElement != null) {
			hierarchy.setSelectedElement(selectedElement);
			elementName.setText(selectedElement.getName());
			if (elementName.getOffsetWidth() > 97) {
				shorten(elementName, 97);
			}			
		}
	}
	protected final void layout() {
		String hName = hierarchyName.getText();
		super.layout();		
		hierarchyName.setText(hName);
		content.setWidgetPosition(dimIcon, 2, 4);
		content.setWidgetPosition(hierarchyName, 18, 3);
		if (hierarchyName.getOffsetWidth() > 97) {
			shorten(hierarchyName, 97);
		}
		content.setWidgetPosition(elementIcon, 2, 25);
		content.setWidgetPosition(elementName, 18, 24);
		if (elementName.getOffsetWidth() > 97) {
			shorten(elementName, 97);
		}
		placeFilterWidget(123, 4);
		content.setWidgetPosition(selectIcon, 122, 23);
	}

	protected final void initComponents() {
		content.add(dimIcon);
		content.add(elementIcon);
		content.add(elementName);
		content.add(selectIcon);
		
		setStyleName(STYLE);
		DOM.setStyleAttribute(selectIcon.getElement(), "cursor", "pointer");
		setSelectedElement(hierarchy.getSelectedElement());
	}
		
	protected void setLabel(String hierName) {
		hierarchyName.setText(hierName);
		if (hierarchyName.getOffsetWidth() > 97) {
			shorten(hierarchyName, 97);
		}
		// Don't set the width here, otherwise, FF will not return
		// getOffsetWidth properly
//		hierarchyName.setWidth(97 + "px");
	}

	private final void initEventHandling() {
		selectIcon.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				fireSelectElementPressed();
			}
		});
	}
	
	private final void fireSelectElementPressed() {
		for(HierarchyWidgetListener listener : listeners)
			listener.pressedSelectElement(this);
	}

}
