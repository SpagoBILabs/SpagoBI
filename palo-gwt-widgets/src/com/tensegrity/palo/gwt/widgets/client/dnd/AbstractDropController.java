/*
*
* @file AbstractDropController.java
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
* @version $Id: AbstractDropController.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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

import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * Base class for typical drop controllers. Contains some basic functionality
 * like adjust widget styles.
 */
public abstract class AbstractDropController implements DropController {

  /**
   * CSS style name applied to drop targets.
   */
  private static final String CSS_DROP_TARGET = "dragdrop-dropTarget";

  /**
   * CSS style name which is applied to drop targets which are being actively
   * engaged by the current drag operation.
   */
  private static final String PRIVATE_CSS_DROP_TARGET_ENGAGE = "dragdrop-dropTarget-engage";

  /**
   * The drop target.
   */
  private Widget dropTarget;

  public AbstractDropController(Widget dropTarget) {
    this.dropTarget = dropTarget;
    dropTarget.addStyleName(CSS_DROP_TARGET);
  }

  public Widget getDropTarget() {
    return dropTarget;
  }

  public void onDrop(DragContext context) {
  }

  public void onEnter(DragContext context) {
    dropTarget.addStyleName(PRIVATE_CSS_DROP_TARGET_ENGAGE);
  }

  public void onLeave(DragContext context) {
    dropTarget.removeStyleName(PRIVATE_CSS_DROP_TARGET_ENGAGE);
  }

  public void onMove(DragContext context) {
  }

  public void onPreviewDrop(DragContext context) throws VetoDragException {	  
  }
}
