/*
*
* @file DragHandlerCollection.java
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
* @version $Id: DragHandlerCollection.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * Helper class for controllers that accept
 * {@link com.allen_sauer.gwt.dnd.client.DragHandler DragHandlers}. This
 * subclass of ArrayList assumes that all items added to it will be of type
 * {@link com.allen_sauer.gwt.dnd.client.DragHandler}.
 */
@SuppressWarnings("serial")
public class DragHandlerCollection extends ArrayList<DragHandler> {

  /**
   * Fires a {@link DragHandler#onDragEnd(DragEndEvent)} on all handlers in the
   * collection.
   * 
   * @param dragEndEvent the event
   */
  public void fireDragEnd(DragEndEvent dragEndEvent) {
    for (DragHandler handler : this) {
      handler.onDragEnd(dragEndEvent);
    }
  }

  /**
   * Fires a {@link DragHandler#onDragStart(DragStartEvent)} on all handlers in
   * the collection.
   * 
   * @param dragStartEvent the event
   */
  public void fireDragStart(DragStartEvent dragStartEvent) {
    for (DragHandler handler : this) {
      handler.onDragStart(dragStartEvent);
    }
  }

  /**
   * Fires a {@link DragHandler#onPreviewDragEnd(DragEndEvent)} on all handlers
   * in the collection.
   * 
   * @param dragEndEvent the event
   * @throws VetoDragException if the proposed operation is unacceptable
   */
  public void firePreviewDragEnd(DragEndEvent dragEndEvent) throws VetoDragException {
    for (DragHandler handler : this) {
      handler.onPreviewDragEnd(dragEndEvent);
    }
  }

  /**
   * Fires a {@link DragHandler#onPreviewDragStart(DragStartEvent)} on all
   * handlers in the collection.
   * 
   * @param dragStartEvent the event
   * @throws VetoDragException if the proposed operation is unacceptable
   */
  public void firePreviewDragStart(DragStartEvent dragStartEvent) throws VetoDragException {
    for (DragHandler handler : this) {
      handler.onPreviewDragStart(dragStartEvent);
    }
  }
}
