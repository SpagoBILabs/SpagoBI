/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
