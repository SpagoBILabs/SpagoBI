/*
*
* @file ContainerDropController.java
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
* @version $Id: ContainerDropController.java,v 1.11 2010/04/15 09:54:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.dnd.AbsolutePositionDropController;
import com.tensegrity.palo.gwt.widgets.client.dnd.DragContext;

/**
 * <code>ContainerDropController</code> TODO DOCUMENT ME
 * 
 * @version $Id: ContainerDropController.java,v 1.11 2010/04/15 09:54:50 PhilippBouillon Exp $
 **/
public class ContainerDropController extends AbsolutePositionDropController {

	private final XObjectContainer container;

	public ContainerDropController(XObjectContainer container) {
		super(container.getDropTarget());
		this.container = container;
	}

	public void onDrop(final DragContext context) {
		Widget widget = context.draggable;
		if (widget instanceof ContainerWidget) {
			ContainerWidget _widget = (ContainerWidget) widget;
			// remove from previous container:
			XObjectContainer _container = _widget.getContainer();
			int [] dropIndex = container.getRenderer().getInsertIndex(
					context.mouseX, context.mouseY); 
			if (dropIndex[1] == 0) {
				_container.remove(_widget);
				_container.layout(0, 0);
				_widget = container.add(_widget.getModel(), dropIndex[0]);
			} else {				
				if (container.equals(_widget.getContainer()) ||
					(_widget.getContainer() instanceof ScrollableContainer &&
					 ((ScrollableContainer) _widget.getContainer()).getContainer().equals(container))) {
					int i1 = container.indexOf(_widget);
					if (dropIndex[0] >= i1) {
						dropIndex[0]++;
					}
				}
				_widget = container.replace(dropIndex[0], _widget);
				_container.layout(0, 0);
				
			}
			
			container.layout(0, 0);
			container.notifyDrop(_widget, dropIndex[0]);									
		} else
			super.onDrop(context);
	}

	private final void clearAllDragMarks() {
		RootPanel rp = RootPanel.get();
		int widgetCount = rp.getWidgetCount();
		List <PaloInsert> dragMarks = new ArrayList<PaloInsert>();
		Widget w;
		for (int i = 0; i < widgetCount; i++) {
			if ((w = rp.getWidget(i)) instanceof PaloInsert) {
				dragMarks.add((PaloInsert) w);
			}
		}
		for (PaloInsert pi: dragMarks) {
			pi.remove();
		}
		List <PaloReplace> replaceMarks = new ArrayList<PaloReplace>();
		widgetCount = rp.getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			if ((w = rp.getWidget(i)) instanceof PaloReplace) {
				replaceMarks.add((PaloReplace) w);
			}
		}
		for (PaloReplace pr: replaceMarks) {
			pr.remove();
		}				
	}
	
	 public void onEnter(DragContext context) {
		 super.onEnter(context);
		 clearAllDragMarks();
	 }
	 
	 public void onLeave(DragContext context) {
		super.onLeave(context);
		clearAllDragMarks();
	 }
	 
	 public void onMove(DragContext context) {
//		container.showDragMark(context.desiredDraggableX, 
//				context.desiredDraggableY);
		container.showDragMark(context.mouseX, 
				context.mouseY);		 
	 }
}
