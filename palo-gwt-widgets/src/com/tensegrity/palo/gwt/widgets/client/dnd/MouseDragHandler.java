/*
*
* @file MouseDragHandler.java
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
* @version $Id: MouseDragHandler.java,v 1.5 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * Copyright 2008 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tensegrity.palo.gwt.widgets.client.dnd;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;

/**
 * Implementation helper class which handles mouse events for all
 * draggable widgets for a given {@link DragController}.
 */
class MouseDragHandler implements MouseListener {

  private static final int ACTIVELY_DRAGGING = 3;

  private static final int DRAGGING_NO_MOVEMENT_YET = 2;

  private static final int NOT_DRAGGING = 1;

  private FocusPanel capturingWidget;

  private final DragContext context;

  private DeferredMoveCommand deferredMoveCommand = new DeferredMoveCommand(this);

  private int dragging = NOT_DRAGGING;

  private HashMap<Widget, Widget> dragHandleMap = new HashMap<Widget, Widget>();

  private boolean mouseDown;

  private int mouseDownOffsetX;

  private int mouseDownOffsetY;

  private Widget mouseDownWidget;

  MouseDragHandler(DragContext context) {
    this.context = context;
    initCapturingWidget();
  }

  public void onMouseDown(Widget sender, int x, int y) {
    if (dragging == ACTIVELY_DRAGGING || dragging == DRAGGING_NO_MOVEMENT_YET) {
      // Ignore additional mouse buttons depressed while still dragging
      return;
    }

    Event event = DOM.eventGetCurrentEvent();
    int button = DOM.eventGetButton(event);
    if (button != Event.BUTTON_LEFT) {
      return;
    }

    // mouse down (not first mouse move) determines draggable widget
    mouseDownWidget = sender;
    context.draggable = dragHandleMap.get(mouseDownWidget);
    assert context.draggable != null;

    if (!toggleKey(event) && !context.selectedWidgets.contains(mouseDownWidget)) {
      context.dragController.clearSelection();
      context.dragController.toggleSelection(context.draggable);
    }
    if (context.dragController.getBehaviorCancelDocumentSelections()) {
      DeferredCommand.addCommand(new Command() {
        public void execute() {
          DOMUtil.cancelAllDocumentSelections();
        }
      });
    }

    mouseDown = true;
    DOM.eventPreventDefault(event);

    mouseDownOffsetX = x;
    mouseDownOffsetY = y;
    WidgetLocation loc1 = new WidgetLocation(mouseDownWidget, null);
    if (mouseDownWidget != context.draggable) {
      WidgetLocation loc2 = new WidgetLocation(context.draggable, null);
      mouseDownOffsetX += loc1.getLeft() - loc2.getLeft();
      mouseDownOffsetY += loc1.getTop() - loc2.getTop();
    }
    if (context.dragController.getBehaviorDragStartSensitivity() == 0 && !toggleKey(event)) {
      // set context.mouseX/Y before startDragging() is called
      context.mouseX = x + loc1.getLeft();
      context.mouseY = y + loc1.getTop();
      startDragging();
      if (dragging == NOT_DRAGGING) {
        return;
      }
      actualMove(context.mouseX, context.mouseY);
    }
  }

  public void onMouseEnter(Widget sender) {
  }

  public void onMouseLeave(Widget sender) {
    if (mouseDown && dragging == NOT_DRAGGING) {
      // TODO DOMUtil.cancelAllDocumentSelections(); ?
      startDragging();
    }
  }

  public void onMouseMove(Widget sender, int x, int y) {
    if (dragging == ACTIVELY_DRAGGING || dragging == DRAGGING_NO_MOVEMENT_YET) {
      // TODO remove Safari workaround after GWT issue 1807 fixed
      if (sender != capturingWidget) {
        // In Safari 1.3.2 MAC, other mouse events continue to arrive even when capturing
        return;
      }
      dragging = ACTIVELY_DRAGGING;
    } else {
      if (mouseDown) {
        if (Math.max(Math.abs(x - mouseDownOffsetX), Math.abs(y - mouseDownOffsetY)) >= context.dragController.getBehaviorDragStartSensitivity()) {
          if (context.dragController.getBehaviorCancelDocumentSelections()) {
            DOMUtil.cancelAllDocumentSelections();
          }
          if (!context.selectedWidgets.contains(context.draggable)) {
            context.dragController.toggleSelection(context.draggable);
          }
          startDragging();

          // adjust (x,y) to be relative to capturingWidget at (0,0)
          Location location = new WidgetLocation(mouseDownWidget, null);
          x += location.getLeft();
          y += location.getTop();
        } else {
          // prevent IE image drag when drag sensitivity > 5
          DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
        }
      }
      if (dragging == NOT_DRAGGING) {
        return;
      }
    }
    // proceed with the actual drag
    DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
    deferredMoveCommand.scheduleOrExecute(x, y);
  }

  public void onMouseUp(Widget sender, int x, int y) {
    Event event = DOM.eventGetCurrentEvent();
    int button = DOM.eventGetButton(event);
    if (button != Event.BUTTON_LEFT) {
      return;
    }
    mouseDown = false;

    // in case mouse down occurred elsewhere
    if (mouseDownWidget == null) {
      return;
    }

    if (context.dragController.getBehaviorCancelDocumentSelections()) {
      DOMUtil.cancelAllDocumentSelections();
    }
    if (dragging == NOT_DRAGGING) {
      doSelectionToggle(event);
      return;
    }

    // TODO Remove Safari workaround after GWT issue 1807 fixed
    if (sender != capturingWidget) {
      // In Safari 1.3.2 MAC does not honor capturing widget for mouse up
      Location location = new WidgetLocation(sender, null);
      x += location.getLeft();
      y += location.getTop();
    }
    // Proceed with the drop
    try {
      drop(x, y);
      if (dragging != ACTIVELY_DRAGGING) {
        doSelectionToggle(event);
      }
    } finally {
      dragEndCleanup();
    }
  }

  void actualMove(int x, int y) {
    context.mouseX = x;
    context.mouseY = y;
    context.desiredDraggableX = x - mouseDownOffsetX;
    context.desiredDraggableY = y - mouseDownOffsetY;

    context.dragController.dragMove();
  }

  void makeDraggable(Widget draggable, Widget dragHandle) {
    if (dragHandle instanceof SourcesMouseEvents) {
      ((SourcesMouseEvents) dragHandle).addMouseListener(this);
      dragHandleMap.put(dragHandle, draggable);
    } else {
      throw new RuntimeException("dragHandle must implement SourcesMouseEvents to be draggable");
    }
  }

  void makeNotDraggable(Widget dragHandle) {
    if (dragHandleMap.remove(dragHandle) == null) {
      throw new RuntimeException("dragHandle was not draggable");
    }
    ((SourcesMouseEvents) dragHandle).removeMouseListener(this);
  }

  private void doSelectionToggle(Event event) {
    Widget widget = dragHandleMap.get(mouseDownWidget);
    assert widget != null;
    if (!toggleKey(event)) {
      context.dragController.clearSelection();
    }
    context.dragController.toggleSelection(widget);
  }

  private void dragEndCleanup() {
    DOM.releaseCapture(capturingWidget.getElement());
    capturingWidget.removeFromParent();
    dragging = NOT_DRAGGING;
    context.dragEndCleanup();
  }

  private void drop(int x, int y) {
    actualMove(x, y);

    // Does the DragController allow the drop?
    try {
      context.dragController.previewDragEnd();
    } catch (VetoDragException ex) {
      context.vetoException = ex;
    }

    context.dragController.dragEnd();
  }

  private void initCapturingWidget() {
    capturingWidget = new FocusPanel();
    capturingWidget.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
//    RootPanel.get().add(capturingWidget, 0, 0);
    capturingWidget.addMouseListener(this);
//    capturingWidget.getElement().getStyle().setProperty("visibility", "hidden");
//    capturingWidget.getElement().getStyle().setProperty("margin", "0px");
//    capturingWidget.getElement().getStyle().setProperty("border", "none");
    Style style = capturingWidget.getElement().getStyle();
    style.setProperty("filter", "alpha(opacity=0)");
    style.setProperty("opacity", "0");
    style.setProperty("margin", "0px");
    style.setProperty("borderStyle", "none");
    style.setProperty("backgroundColor", "blue");
  }

  private void startDragging() {
    context.dragStartCleanup();
    try {
      context.dragController.previewDragStart();
    } catch (VetoDragException ex) {
      context.vetoException = ex;
      mouseDown = false;
      return;
    }
    context.dragController.dragStart();

    RootPanel.get().add(capturingWidget, 0, 0);
    
    DOM.setCapture(capturingWidget.getElement());
    dragging = DRAGGING_NO_MOVEMENT_YET;
  }

  private boolean toggleKey(Event event) {
    return DOM.eventGetCtrlKey(event) || DOM.eventGetMetaKey(event);
  }
}
