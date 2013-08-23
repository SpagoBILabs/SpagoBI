/*
*
* @file ScrollableContainer.java
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
* @version $Id: ScrollableContainer.java,v 1.10 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.widgets.client.dnd.PickupDragController;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Rectangle;

/**
 * <code>ScrollableContainer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ScrollableContainer.java,v 1.10 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public class ScrollableContainer extends Composite implements XObjectContainer, ScrollerListener {

	private Scroller scroller;
	private final Container container;
	private final ScrollableContainerRenderer renderer;
	private final AbsolutePanel viewport = new AbsolutePanel();
	private final AbsolutePanel content = new AbsolutePanel();
	
	public ScrollableContainer(ScrollableContainerRenderer renderer) {
		this.renderer = renderer;
		this.container = new Container(renderer);	
		this.scroller = renderer.createScroller(this);
		initComponents();
		initEventHandling();
	}

	//FIXME remove this: its only there because dropcontroller is registered to container
	//and hence, the container widget points to internal used container...
	public final Container getContainer() {
		return container;
	}
	
	public final ContainerWidget add(XObject model) {
		return add(model, container.getWidgetCount());
	}
	public final ContainerWidget add(XObject model, int atIndex) {
		ContainerWidget widget = container.add(model, atIndex);
		widget.setContainer(this);
		return widget;
	}
	public final ContainerWidget replace(int atIndex, ContainerWidget widget) {
		ContainerWidget _widget = container.replace(atIndex, widget);
		_widget.setContainer(this);
		return _widget;
	}
	public int indexOf(ContainerWidget widget) {
		return container.indexOf(widget);
	}
	public final void removeAll() {
		container.removeAll();
	}
	
	public final void addContainerListener(ContainerListener listener) {
		container.addContainerListener(listener);
	}

	public final void reset() {
		container.reset();
	}

	public final AbsolutePanel getDropTarget() {
		return container;
	}
	
	public final Widget getEmptyLabel() {
		return container.getEmptyLabel();
	}
	public final void setEmptyLabel(Widget label) {
		container.setEmptyLabel(label);
	}
	public final void setEmptyLabel(String label) {
		container.setEmptyLabel(label);
	}

	public final ContainerWidget getFirstWidget() {
		return container.getFirstWidget();
	}

	public final ContainerWidget getLastWidget() {
		return container.getLastWidget();
	}
	public final int getWidgetCount() {
		return container.getWidgetCount();
	}

	public final XObject[] getXObjects() {
		return container.getXObjects();
	}
	
	public final ContainerWidget[] getWidgets() {
		return container.getWidgets();
	}

	public final void setWidgetPosition(Widget widget, int left, int top) {
		container.setWidgetPosition(widget, left, top);
	}
	
	public final ContainerRenderer getRenderer() {
		return container.getRenderer();
	}
	
	public final void register(PickupDragController dragController) {
		container.register(dragController);
	}

	public final Point layout(int width, int height) {
		if(width <= 0) width = getOffsetWidth();
		if(height <= 0) height = getOffsetHeight();
		Point conSize = container.layout(0, 0);
		Point scrSize = scroller.getSize();
		Point vpSize = renderer.getViewportSize(width, height, scrSize);
		//container should be at least as big as the viewport... 
		if(conSize.x < vpSize.x)
			conSize.x = vpSize.x;
		if(conSize.y < vpSize.y)
			conSize.y = vpSize.y;
		
		viewport.setPixelSize(vpSize.x, vpSize.y);
		container.setPixelSize(conSize.x, conSize.y);
		
		//finally arrange them:
		Point size = renderer.arrange(content, viewport, scroller);
		updateScrollButtons();		
		return size;
	}

	public final void notifyDrop(ContainerWidget widget, int atIndex) {
		container.notifyDrop(widget, atIndex);
	}

//	public final void register(PickupDragController dragController) {
//		container.register(dragController);
//	}

	public final boolean remove(ContainerWidget widget) {
		return container.remove(widget);
	}

	public final void removeListener(ContainerListener listener) {
		container.removeListener(listener);
	}

	public final void scroll(int direction) {
		//we scroll one widget to the left or right:		
//		Point scrollOffset = scroller.getScrollOffset(); //renderer.getScrollOffset();
		int dir = direction == Scroller.FORWARD ? -1 : +1;
		renderer.setFirstVisibleIndex(renderer.getFirstVisibleIndex() + dir);
//		int x = viewport.getWidgetLeft(container) + dir * scrollOffset.x;
//		int y = viewport.getWidgetTop(container) + dir * scrollOffset.y;
//		viewport.setWidgetPosition(container, x, y);
		container.layout(0, 0);
		updateScrollButtons();
	}
	
	private final void initComponents() {
		initWidget(content);	
		renderer.init(this);
		viewport.add(container);		
		content.add(viewport);
		content.add(scroller);
		
		//styles:
		DOM.setStyleAttribute(viewport.getElement(), "overflow", "hidden");
	}
	private final void initEventHandling() {
		scroller.addListener(this);
	}
	
	public final void updateScrollButtons() {
//		int firstVisible = renderer.getFirstVisibleIndex();
		Widget firstWidget = container.getFirstWidget();
		Widget lastWidget = container.getLastWidget();
		boolean empty = firstWidget == null && lastWidget == null;
		Rectangle bounds = new Rectangle(viewport.getAbsoluteLeft(), viewport.getAbsoluteTop(), viewport.getOffsetWidth(), viewport.getOffsetHeight());
		scroller.enable(Scroller.BACKWARD, !empty && !renderer.isVisible(lastWidget, bounds));
		scroller.enable(Scroller.FORWARD, !empty  && !firstWidget.isVisible()); //!empty && !renderer.isVisible(firstWidget, bounds));
	}

	public final int getMinHeight() {
		return renderer.getMinHeight();
	}

	public final int getMinWidth() {
		return renderer.getMinWidth();
	}

	public void clearDragMark() {
		container.clearDragMark();
	}

	public void showDragMark(int x, int y) {
		container.showDragMark(x, y); 
	}
	
	public Point getDragMark() {
		return container.getDragMark();
	}
}
