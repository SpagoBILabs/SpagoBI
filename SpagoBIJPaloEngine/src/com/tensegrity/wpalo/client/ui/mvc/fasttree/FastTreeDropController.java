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
* @version $Id: FastTreeDropController.java,v 1.1 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.mvc.fasttree;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.dnd.AbsolutePositionDropController;
import com.tensegrity.palo.gwt.widgets.client.dnd.DragContext;

/**
 * <code>ContainerDropController</code> TODO DOCUMENT ME
 * 
 * @version $Id: FastTreeDropController.java,v 1.1 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class FastTreeDropController extends AbsolutePositionDropController {

	private final FastMSTree tree;

	public FastTreeDropController(AbsolutePanel dropTarget, FastMSTree tree) {
		super(dropTarget);
		this.tree = tree;
	}

	public void onDrop(final DragContext context) {
		Widget widget = context.draggable;
//		if (widget instanceof ContainerWidget) {
//			ContainerWidget _widget = (ContainerWidget) widget;
//			// remove from previous container:
//			XObjectContainer _container = _widget.getContainer();
//			_container.remove(_widget);
//			_container.layout(0, 0);
//			int dropIndex = container.getRenderer().getInsertIndex(
//					context.desiredDraggableX, context.desiredDraggableY);
//			_widget = container.add(_widget.getModel(), dropIndex);
//			container.layout(0, 0);
//			container.notifyDrop(_widget, dropIndex);					
//		} else
//			super.onDrop(context);
	}
}
