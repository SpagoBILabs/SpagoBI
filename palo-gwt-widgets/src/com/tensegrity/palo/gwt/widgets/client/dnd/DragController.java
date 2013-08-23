/*
*
* @file DragController.java
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
* @version $Id: DragController.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Common interface which all drag controllers must implement.
 */
public interface DragController extends FiresDragEvents {

  /**
   * Register a drag handler which will listen for
   * {@link DragStartEvent DragStartEvents} and
   * and {@link DragEndEvent DragEndEvents}.
   * 
   * @see #removeDragHandler(DragHandler)
   */
  void addDragHandler(DragHandler handler);

  /**
   * All currently selected widgets are deselected.
   */
  void clearSelection();

  /**
   * Callback method for {@link MouseDragHandler}.
   */
  void dragEnd();

  /**
   * Callback method for {@link MouseDragHandler}.
   */
  void dragMove();

  /**
  * Callback method for {@link MouseDragHandler} when a drag operation
  * is initiated for this drag controller.
  */
  void dragStart();

  /**
   * Whether or not any selected regions should be unselected
   * by dragging.
   * 
   * @return <code>true</code> if cancel selections behavior in on
   */
  boolean getBehaviorCancelDocumentSelections();

  /**
   * Determine whether or not drag operations are constrained to the boundary panel.
   * 
  * @return <code>true</code> if drags are constrained to the boundary panel
  */
  boolean getBehaviorConstrainedToBoundaryPanel();

  /**
   * Gets the number of pixels the mouse must be moved to initiate a drag operation.
   * 
   * @return number of pixels or <code>0</code> (zero) if mouse down starts the drag
   */
  int getBehaviorDragStartSensitivity();

  /**
   * Determines whether multiple widget selection behavior is enabled.
   * 
   * @return <code>true</code> if multiple widget selection behavior is enabled
   */
  boolean getBehaviorMultipleSelection();

  /**
   * Get the boundary panel provided in the constructor.
   * 
   * @return the AbsolutePanel provided in the constructor
   */
  AbsolutePanel getBoundaryPanel();

  /**
   * Enable dragging on widget. Call this method for each widget that
   * you would like to make draggable under this drag controller.
   * 
   * @param draggable the widget to be made draggable
   */
  void makeDraggable(Widget draggable);

  /**
   * Enable dragging on widget, specifying the child widget serving as a
   * drag handle.
   * 
   * @param draggable the widget to be made draggable
   * @param dragHandle the widget by which widget can be dragged
   */
  void makeDraggable(Widget draggable, Widget dragHandle);

  /**
   * Performs the reverse of {@link #makeDraggable(Widget)}, making the widget
   * no longer draggable by this drag controller.
   * 
   * @param widget the widget which should no longer be draggable
   */
  void makeNotDraggable(Widget widget);

  /**
   * Callback method for {@link MouseDragHandler}.
   * 
   * @throws VetoDragException if the proposed operation is unacceptable
   */
  void previewDragEnd() throws VetoDragException;

  /**
   * Callback method for {@link MouseDragHandler}.
   * 
   * @throws VetoDragException if the proposed operation is unacceptable
   */
  void previewDragStart() throws VetoDragException;

  /**
   * Unregister drag handler.
   * 
   * @see #addDragHandler(DragHandler)
   */
  void removeDragHandler(DragHandler handler);

  /**
   * Reset the internal drop controller (drop target) cache which is initialized
   * primarily by {@link AbstractDragController#dragStart()}. This method should
   * be called when a drop target's size and/or location changes, or when drop
   * target eligibility changes.
   */
  void resetCache();

  /**
   * Set whether or not document selections should be canceled by dragging.
   * The default is <code>true</code>.
   * 
   * @param cancelDocumentSelections <code>true</code> if dragging should cancel document selections
   */
  void setBehaviorCancelDocumentSelections(boolean cancelDocumentSelections);

  /**
   * Set whether or not movable widget is to be constrained to the boundary panel
   * during dragging. The default is not to constrain the draggable or drag proxy.
   * 
   * @param constrainedToBoundaryPanel whether or not to constrain to the boundary panel
   */
  void setBehaviorConstrainedToBoundaryPanel(boolean constrainedToBoundaryPanel);

  /**
   * Sets the number of pixels the mouse must be moved in either horizontal or vertical
   * direction in order to initiate a drag operation. Defaults to <code>0</code> (zero).
   * Use a value of at least <code>1</code> (one) in order to allow registered click
   * listeners to receive click events.
   * 
   * @param pixels number of pixels or <code>0</code> (zero) to start dragging on mouse down
   */
  void setBehaviorDragStartSensitivity(int pixels);

  /**
   * Sets whether multiple widgets can be selected for dragging at one time via
   * <code>CTRL</code>/<code>META</code>-click. Defaults to <code>true</code>.
   * 
   * @param multipleSelectionAllowed whether multiple selections are enabled
   */
  void setBehaviorMultipleSelection(boolean multipleSelectionAllowed);

  /**
   * Toggle the selection of the specified widget.
   * 
   * @param draggable the widget whose selection is to be toggled
   */
  void toggleSelection(Widget draggable);
}