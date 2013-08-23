/*
*
* @file ContainerWidget.java
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
* @version $Id: ContainerWidget.java,v 1.3 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>ContainerWidget</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ContainerWidget.java,v 1.3 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public abstract class ContainerWidget extends Composite implements SourcesMouseEvents {
	
	private final MouseListenerCollection mouseListeners = new MouseListenerCollection();
	
	private XObjectContainer container;
	
	public ContainerWidget(XObjectContainer container) {
		this.container = container;
	}
	
	public abstract XObject getModel();
	public abstract Widget getDragHandle();
	
	public final XObjectContainer getContainer() {
		return container;
	}
	
	public final void setContainer(XObjectContainer container) {
		this.container = container;
	}
	
	public final void addMouseListener(MouseListener listener) {
		mouseListeners.add(listener);
	}

	public final void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);
	}

	public final void initWidget(Widget widget) {
		super.initWidget(widget);
		sinkEvents(Event.MOUSEEVENTS);
	}
	
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEOVER:
		case Event.ONMOUSEOUT:
			if (mouseListeners != null) {
				mouseListeners.fireMouseEvent(this, event);
			}
			break;
		}
	}

}
