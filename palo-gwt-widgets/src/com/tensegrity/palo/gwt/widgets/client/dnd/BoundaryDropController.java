/*
*
* @file BoundaryDropController.java
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
* @version $Id: BoundaryDropController.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.VetoDragException;

/**
 * A {@link DropController} for the {@link com.google.gwt.user.client.ui.Panel}
 * which contains a given draggable widget.
 */
public class BoundaryDropController extends AbsolutePositionDropController {

  private boolean allowDroppingOnBoundaryPanel = true;

  public BoundaryDropController(AbsolutePanel dropTarget, boolean allowDroppingOnBoundaryPanel) {
    super(dropTarget);
    dropTarget.addStyleName("dragdrop-boundary");
    this.allowDroppingOnBoundaryPanel = allowDroppingOnBoundaryPanel;
  }

  /**
   * Whether or not dropping on the boundary panel is permitted.
   * 
   * @return <code>true</code> if dropping on the boundary panel is allowed
   */
  public boolean getBehaviorBoundaryPanelDrop() {
    return allowDroppingOnBoundaryPanel;
  }

  @Override
  public void onPreviewDrop(DragContext context) throws VetoDragException {
    if (!allowDroppingOnBoundaryPanel) {
      throw new VetoDragException();
    }
    super.onPreviewDrop(context);
  }

  /**
   * Set whether or not widgets may be dropped anywhere on the boundary panel.
   * Set to <code>false</code> when you only want explicitly registered drop
   * controllers to accept drops. Defaults to <code>true</code>.
   * 
   * @param allowDroppingOnBoundaryPanel <code>true</code> to allow dropping
   */
  public void setBehaviorBoundaryPanelDrop(boolean allowDroppingOnBoundaryPanel) {
    this.allowDroppingOnBoundaryPanel = allowDroppingOnBoundaryPanel;
  }

  @Override
  Widget makePositioner(Widget reference) {
    if (allowDroppingOnBoundaryPanel) {
      return super.makePositioner(reference);
    } else {
      return new SimplePanel();
    }
  }
}
