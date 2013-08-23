/*
*
* @file XObjectContainer.java
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
* @version $Id: XObjectContainer.java,v 1.8 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.widgets.client.dnd.PickupDragController;
import com.tensegrity.palo.gwt.widgets.client.util.Point;


/**
 * <code>XObjectContainer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XObjectContainer.java,v 1.8 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public interface XObjectContainer {

	//TODO if needed add methods
	//	- remove(XObject)
	//	- XObject[] getModel();
	//	- ...
	
	
	public void reset();
	public ContainerWidget add(XObject model);
	public ContainerWidget add(XObject model, int atIndex);
	public ContainerWidget replace(int atIndex, ContainerWidget widget);
	public void addContainerListener(ContainerListener listener);
	public ContainerWidget[] getWidgets();
	public int getWidgetCount();
//	public Widget getWidgetAt(int index);
	public ContainerWidget getFirstWidget();
	public ContainerWidget getLastWidget();
	public Point layout(int wHint, int hHint);
	/** force subclasses to override */
	public void setWidgetPosition(Widget widget, int left, int top);
	public AbsolutePanel getDropTarget();
	public void register(PickupDragController dragController);
//	/** force subclasses to override */
//	public boolean remove(Widget widget);
	public boolean remove(ContainerWidget widget);
	public void removeListener(ContainerListener listener);
	public Widget getEmptyLabel();	
	public void setEmptyLabel(Widget label);
	public void setEmptyLabel(String label);
	public ContainerRenderer getRenderer();
	public int getMinWidth();
	public int getMinHeight();
	public XObject[] getXObjects();
	public void notifyDrop(ContainerWidget widget, int atIndex);
	public void clearDragMark();
	public void showDragMark(int x, int y);
	public Point getDragMark();	
	public int indexOf(ContainerWidget widget);
}
