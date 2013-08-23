/*
*
* @file Scroller.java
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
* @version $Id: Scroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.ToggleIcon;
import com.tensegrity.palo.gwt.widgets.client.util.Point;


/**
 * <code>Scroller</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: Scroller.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
public abstract class Scroller extends Composite implements ClickListener {

	public static final int FORWARD = 1;
	public static final int BACKWARD = 2;
	
	private ToggleIcon forward;
	private ToggleIcon backward;
	private final XObjectContainer container;
	private Panel content;
	
	
	private final List<ScrollerListener> listeners = new ArrayList<ScrollerListener>();

	public Scroller(XObjectContainer container) {
		this.container = container;
		initComponents();
		initEventhandling();
	}

	public final XObjectContainer getContainer() {
		return container;
	}
	
	public final Point getSize() {
		return new Point(getOffsetWidth(), getOffsetHeight());
	}
	
	protected abstract Panel getContent();
	public abstract Point getScrollOffset();	
	public abstract ToggleIcon getForwardIcon();
	public abstract ToggleIcon getBackwardIcon();
	
	public final void setForwardIcons(String on_url, String off_url, int width, int height) {
		forward.setOnIcon(on_url, width, height);
		forward.setOffIcon(off_url, width, height);
	}
	public final void setBackwardIcons(String on_url, String off_url, int width, int height) {
		backward.setOnIcon(on_url, width, height);
		backward.setOffIcon(off_url, width, height);
	}

	public final void addListener(ScrollerListener listener) {
		removeListener(listener);
		listeners.add(listener);
	}
	public final void removeListener(ScrollerListener listener) {
		listeners.remove(listener);
	}

	public void enable(int direction, boolean enabled) {
		if(direction == FORWARD) {
			forward.setEnabled(enabled);
		} else if(direction == BACKWARD)
			backward.setEnabled(enabled);
	}
	
	public boolean isEnabled(int direction) {
		if(direction == FORWARD) {
			return forward.isEnabled();
		} else
			return backward.isEnabled();
	}
	
	public void onClick(Widget sender) {
		if(!((ToggleIcon)sender).isEnabled())
			return;
		int direction = -1;
		if(sender == forward)
			direction = FORWARD;
		else if(sender == backward)
			direction = BACKWARD;
		notifyListeners(direction);
	}
	
	
	private final void initComponents() {
		//get scroller parts:
		content = getContent();
		forward = getForwardIcon();
		backward = getBackwardIcon();
		//init:
		initWidget(content);		
		content.add(forward);
		content.add(backward);
	}
	
	private final void initEventhandling() {
		forward.addClickListener(this);
		backward.addClickListener(this);
	}
	
	protected final void notifyListeners(int direction) {
		if(direction == -1)
			return;
		for(ScrollerListener listener : listeners)
			listener.scroll(direction);			
	}
}
