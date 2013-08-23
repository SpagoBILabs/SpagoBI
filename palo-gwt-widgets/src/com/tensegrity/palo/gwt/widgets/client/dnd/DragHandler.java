/*
*
* @file DragHandler.java
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
* @version $Id: DragHandler.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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

import java.util.EventListener;

import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * {@link EventListener} interface for drag-and-drop events.
 */
public interface DragHandler extends EventListener {

  /**
   * Fired when drag operation terminates.
   * 
   * @param event an event object containing information about the drag
   *            operation
   */
  void onDragEnd(DragEndEvent event);

  /**
   * Fired when drag is initiated.
   * 
   * @param event an event object containing information about the drag
   *            operation
   */
  void onDragStart(DragStartEvent event);

  /**
   * Fired before {@link #onDragEnd(DragEndEvent)} is fired and provides an
   * opportunity for any registered DragHandler to throw
   * {@link VetoDragException} to prevent the operation.
   * 
   * @param event an event object containing information about the drag
   *            operation
   * @throws VetoDragException when the drag operation is unacceptable
   */
  void onPreviewDragEnd(DragEndEvent event) throws VetoDragException;

  /**
   * Fired before {@link #onDragStart(DragStartEvent)} is fired and provides an
   * opportunity for any registered DragHandler to throw
   * {@link VetoDragException} to prevent the operation.
   * 
   * @param event an event object containing information about the drag
   *            operation
   * @throws VetoDragException when the drag operation is unacceptable
   */
  void onPreviewDragStart(DragStartEvent event) throws VetoDragException;
}
