/*
*
* @file DragContext.java
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
* @version $Id: DragContext.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for context information about the current drag operation.
 */
public class DragContext {

  /**
   * The boundary panel for this drag controller.
   * 
   * TODO replace context.dragController.getBoundaryPanel() with context.boundaryPanel
   */
  public final AbsolutePanel boundaryPanel;

  /**
   * Desired x coordinate of draggable due to mouse dragging.
   */
  public int desiredDraggableX;

  /**
   * Desired y coordinate of draggable due to mouse dragging.
   */
  public int desiredDraggableY;

  /**
   * The DragController for which this context exists.
   */
  public final DragController dragController;

  /**
   * The primary widget currently being dragged or <code>null</code> when not dragging.
   */
  public Widget draggable;

  /**
   * The currently engaged drop controller or <code>null</code> when not dragging,
   * or when the drag controller does not utilize drop controllers.
   */
  public DropController dropController;

  /**
   * The drop controller which participated in the final drop, or <code>null</code>
   * before the final drop has occurred, or when the drag controller does not utilize
   * drop controllers.
   * 
   * TODO use setter methods to handle the finalDropController/vetoException dependencies
   */
  public DropController finalDropController;

  /**
   * Current mouse x coordinate.
   */
  public int mouseX;

  /**
   * Current mouse y coordinate.
   */
  public int mouseY;

  /**
   * List of currently selected widgets. List will contain at most
   * one widget when {@link DragController#setBehaviorMultipleSelection(boolean)}
   * is disabled.
   */
  public List<Widget> selectedWidgets = new ArrayList<Widget>();

  /**
   * At the end of a drag operation this fields will contain either the
   * {@link VetoDragException} which caused the drag to be cancelled, or
   * <code>null</code> if the drag was successful. Note that while the
   * value of this field will still be <code>null</code> in
   * {@link DragHandler#onPreviewDragEnd(DragEndEvent)}, the value will
   * be available in {@link DragHandler#onDragEnd(DragEndEvent)}.
   */
  public Exception vetoException;

  DragContext(DragController dragController) {
    this.dragController = dragController;
    boundaryPanel = dragController.getBoundaryPanel();
  }

  /**
   * Called by {@link MouseDragHandler#dragEndCleanup} at the end of a drag operation
   * to cleanup state.
   */
  public void dragEndCleanup() {
    dropController = null;
    draggable = null;
  }

  /**
   * Called by {@link MouseDragHandler#startDragging} at the start of a drag operation
   * to initialize state.
   */
  public void dragStartCleanup() {
    assert dropController == null;
    finalDropController = null;
    vetoException = null;
  }
}
