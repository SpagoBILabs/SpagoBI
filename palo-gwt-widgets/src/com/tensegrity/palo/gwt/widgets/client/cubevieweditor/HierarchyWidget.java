/*
*
* @file HierarchyWidget.java
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
* @version $Id: HierarchyWidget.java,v 1.6 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.cubevieweditor;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.widgets.client.container.ContainerWidget;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;

/**
 * <code>HierarchyWidget</code> TODO DOCUMENT ME
 * 
 * @version $Id: HierarchyWidget.java,v 1.6 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public abstract class HierarchyWidget extends ContainerWidget {

	// TODO RENAME CSS CLASSES TOO!!!!

	protected final Image filterIcon = new Image("images/filter.gif", 0, 0, 12, 12);
	protected final Image filterIconLocalFilter = new Image("images/filterLocal.gif", 0, 0, 12, 12);
	protected final Image filterIconSubset = new Image("images/filterSubset.gif", 0, 0, 12, 12);
	protected final Label hierarchyName = new Label();
	protected final AbsolutePanel content = new AbsolutePanel();
	protected final XAxisHierarchy hierarchy;
	protected final ArrayList<HierarchyWidgetListener> listeners = new ArrayList<HierarchyWidgetListener>();

	public HierarchyWidget(XObjectContainer container, XAxisHierarchy hierarchy) {
		super(container);
		this.hierarchy = hierarchy;
		initComponents();
		initEventHandling();
	}

	public XObject getModel() {
		return hierarchy;
	}

	public XAxisHierarchy getHierarchy() {
		return hierarchy;
	}
	
	public Widget getDragHandle() {
		return hierarchyName;
	}
	
	public final void addWidgetListener(HierarchyWidgetListener listener) {
		removeWidgetListener(listener);
		listeners.add(listener);
	}
	public final void removeWidgetListener(HierarchyWidgetListener listener) {
		listeners.remove(listener);
	}
	
	protected abstract String getStyle();
	protected abstract void layout();
	protected abstract void setLabel(String hierName);

	
	protected void onLoad() {
		layout();
	}

	private final void initComponents() {
		initWidget(content);
		content.add(hierarchyName);
		Image icon;
		if (hierarchy.getVisibleElements() != null &&
				   hierarchy.getVisibleElements().length != 0) {
			content.add(filterIconLocalFilter);
			icon = filterIconLocalFilter;
		} else if (hierarchy.getActiveSubset() != null) {
			content.add(filterIconSubset);
			icon = filterIconSubset;
		} else {
			content.add(filterIcon);
			icon = filterIcon;
		}
		setLabel(hierarchy.getName());
		//styles:
		setStyleName(getStyle());
		DOM.setStyleAttribute(icon.getElement(), "cursor", "pointer");
	}
	
	protected final void placeFilterWidget(int left, int top) {
		int fis = content.getWidgetIndex(filterIconSubset);
		int fil = content.getWidgetIndex(filterIconLocalFilter);
		int fi  = content.getWidgetIndex(filterIcon);
		
		if (hierarchy.getVisibleElements() != null &&
				   hierarchy.getVisibleElements().length != 0) {
			if (fil == -1) {
				int index = Math.max(fi, fis);
				if (index != -1) {
					content.remove(index);
				}
				content.add(filterIconLocalFilter);
			}
			content.setWidgetPosition(filterIconLocalFilter, left, top);
		} else if (hierarchy.getActiveSubset() != null) {
			if (fis == -1) {
				int index = Math.max(fi, fil);
				if (index != -1) {
					content.remove(index);
				}
				content.add(filterIconSubset);
			}
			content.setWidgetPosition(filterIconSubset, left, top);
		} else {
			if (fi == -1) {
				int index = Math.max(fil, fis);
				if (index != -1) {
					content.remove(index);
				}
				content.add(filterIcon);
			}
			content.setWidgetPosition(filterIcon, left, top);
		}				
	}

	private final void initEventHandling() {
		// add event listeners:
		filterIcon.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				fireFilterPressed();
			}
		});
		filterIconLocalFilter.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				fireFilterPressed();
			}
		});		
		filterIconSubset.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				fireFilterPressed();
			}
		});
	}
	
	private final void fireFilterPressed() {
		for(HierarchyWidgetListener listener : listeners)
			listener.pressedFilter(this);
	}
	
	public void update() {
		layout();
	}
}
