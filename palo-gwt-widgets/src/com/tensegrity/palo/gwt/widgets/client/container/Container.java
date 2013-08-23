/*
*
* @file Container.java
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
* @version $Id: Container.java,v 1.20 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.widgets.client.dnd.PickupDragController;
import com.tensegrity.palo.gwt.widgets.client.util.Point;



/**
 * <code>Container</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: Container.java,v 1.20 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public class Container extends AbsolutePanel implements XObjectContainer {
	
	private static final String STYLE = "palo-gwt-container";
	
//	private Label emptyLabel;
	private Widget emptyLabel;
	private final List<ContainerWidget> widgets = new ArrayList<ContainerWidget>();
	private final List<ContainerListener> listeners = new ArrayList<ContainerListener>();
	private final ContainerRenderer renderer;	
	private final ContainerDropController dropController;
	private PickupDragController dragController;
	private int dragMarkX = -1;
	private int dragMarkY = -1;
	
	public Container(ContainerRenderer renderer) {
		this.renderer = renderer;
		this.dropController = new ContainerDropController(this);
		initComponents();
		initEventHandling();
	}
	
	public final void reset() {
		super.clear();
		widgets.clear();
		if(emptyLabel != null)
			add(emptyLabel);
	}
	
	public final void addContainerListener(ContainerListener listener) {
		removeListener(listener);
		listeners.add(listener);
	}
	
	public final void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}

	public final AbsolutePanel getDropTarget() {
		return this;
	}
		
	public final void register(PickupDragController dragController) {
		if (this.dragController != null) {
			this.dragController.unregisterDropController(dropController);
		}
		this.dragController = dragController;
		this.dragController.setBehaviorDragStartSensitivity(4);
		if (isAttached()) {
			for (ContainerWidget cont: widgets) {
				makeDraggable(cont);
			}
			if (dragController != null) {
				dragController.registerDropController(dropController);
			}
		}
	}
	
	public final Widget getEmptyLabel() {
		return emptyLabel;
	}
	public final void setEmptyLabel(Widget label) {
		if(emptyLabel != null)
			remove(emptyLabel);
		emptyLabel = label;
		add(emptyLabel);
		emptyLabel.setVisible(false);
	}
	public final void setEmptyLabel(String label) {
		emptyLabel = renderer.createEmptyLabel(label);
		add(emptyLabel);
	}
	
	public final Point layout(int width, int height) {
		if(emptyLabel != null)
			emptyLabel.setVisible(widgets.isEmpty());
		if(width <= 0)
			width = getOffsetWidth();
		if(height <= 0)
			height = getOffsetHeight();
		return renderer.render(width, height);
	}
	
	
	
	protected void onAttach() {
		super.onAttach();
		if(dragController != null)
			dragController.registerDropController(dropController);
	}

	protected void onDetach() {
		super.onDetach();
		if(dragController != null)
			dragController.unregisterDropController(dropController);
	}

	public final ContainerWidget add(XObject model) {
		return add(model, widgets.size());
	}
	public final ContainerWidget add(XObject model, int atIndex) {
		ContainerWidget widget = getContainerWidget(model);
		if (widget == null) {
			widget = renderer.createWidget(model);
			widget.setContainer(this);
			super.add(widget);
			makeDraggable(widget);
			widgets.add(atIndex, widget);
//			notifyAdded(widget, atIndex);
		}
		return widget;
	}
	public final ContainerWidget replace(int atIndex, ContainerWidget widget) {
		ContainerWidget _widget = widgets.get(atIndex);
		ContainerWidget result = null;
		if (_widget != null) {
			if (widget.getContainer().equals(this) ||
				(widget.getContainer() instanceof ScrollableContainer &&
				 ((ScrollableContainer) widget.getContainer()).getContainer().equals(this))) {
				int i1 = widgets.indexOf(_widget);
				int i2 = widgets.indexOf(widget);
				if (i1 < i2) {
					widgets.remove(i2);
					widget.getContainer().add(widget.getModel(), i1);
					widgets.remove(i1 + 1);
					if (i2 >= widgets.size()) {
						i2 = widgets.size();
					}
					result = add(_widget.getModel(), i2);
				} else {
					widgets.remove(i1);
					result = widget.getContainer().add(_widget.getModel(), i2);
					widgets.remove(i2 + 1);
					if (i1 >= widgets.size()) {
						i1 = widgets.size();
					}
					add(widget.getModel(), i1);
				}				
			} else {
				int index = widgets.indexOf(_widget);
				remove(_widget);
				XObjectContainer cont = widget.getContainer();
				int i2 = cont.indexOf(widget);
				cont.remove(widget);
				if (index > getWidgetCount()) {
					index = getWidgetCount();
				}
				result = add(widget.getModel(), index);
				if (i2 > cont.getWidgetCount()) {
					i2 = cont.getWidgetCount();
				}
				cont.add(_widget.getModel(), i2);
			}
		}
		return result;
	}
	public int indexOf(ContainerWidget widget) {
		return widgets.indexOf(widget);
	}
	public final void removeAll() {
		Iterator<ContainerWidget> allWidgets = widgets.iterator();
		while(allWidgets.hasNext()) {
			ContainerWidget widget = allWidgets.next();
			widget.removeFromParent();
			renderer.setFirstVisibleIndex(renderer.getFirstVisibleIndex() - 1);
			allWidgets.remove();
		}
	}
	public final boolean remove(ContainerWidget widget) {
		if(widgets.remove(widget)) {
			//update first visible index:
			renderer.setFirstVisibleIndex(renderer.getFirstVisibleIndex() - 1);
			notifyRemoved(widget);
			return true;
		}
		return false;
	}
	
	public final void notifyDrop(ContainerWidget widget, int atIndex) {
		for (ContainerListener listener : listeners)
			listener.dropped(widget, atIndex);
	}
	
	public final XObject[] getXObjects() {
		XObject[] xObjects = new XObject[widgets.size()];
		for (int i = 0; i < xObjects.length; ++i)
			xObjects[i] = widgets.get(i).getModel();
		return xObjects;
	}
	
	public final ContainerWidget[] getWidgets() {
		return widgets.toArray(new ContainerWidget[widgets.size()]);
	}

	public final int getWidgetCount() {
		return widgets.size();
	}
	
	public final ContainerWidget getFirstWidget() {
		if(!widgets.isEmpty())
			return widgets.get(0);
		return null;
	}
	public final ContainerWidget getLastWidget() {
		if(!widgets.isEmpty())
			return widgets.get(widgets.size() - 1);
		return null;
	}
	
	public final ContainerRenderer getRenderer() {
		return renderer;
	}

	public final int getMinHeight() {
		return renderer.getMinHeight();
	}

	public final int getMinWidth() {
		return renderer.getMinWidth();
	}

	
	private final ContainerWidget getContainerWidget(XObject model) {
		for(ContainerWidget widget : widgets) {
			if(widget.getModel().equals(model))
				return widget;
		}
		return null;
	}

	private final void makeDraggable(ContainerWidget widget) {
		if(dragController != null) {
			dragController.makeDraggable(widget, widget);
		}
	}

	private final void notifyRemoved(ContainerWidget widget) {
		for(ContainerListener listener : listeners)
			listener.removed(widget);
	}

	private final void initComponents() {
		renderer.init(this);
		setStyleName(STYLE);
	}
	private final void initEventHandling() {
		
	}

	public void clearDragMark() {
		if (dragMarkX != -1) {
			dragMarkX = -1;
			dragMarkY = -1;
			renderer.renderDragMark();
		}
	}

	public void showDragMark(int x, int y) {
		if (dragMarkX != x || dragMarkY != y) {
			dragMarkX = x;
			dragMarkY = y;
			renderer.renderDragMark();
		}
	}
	
	public Point getDragMark() {
		return new Point(dragMarkX, dragMarkY);
	}
}
