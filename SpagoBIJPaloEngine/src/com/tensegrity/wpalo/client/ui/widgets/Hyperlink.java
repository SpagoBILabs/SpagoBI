/*
*
* @file Hyperlink.java
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
* @version $Id: Hyperlink.java,v 1.5 2009/12/17 16:14:21 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import java.util.HashSet;
import java.util.Set;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.Element;

/**
 * <code>Hyperlink</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: Hyperlink.java,v 1.5 2009/12/17 16:14:21 PhilippBouillon Exp $
 **/
public class Hyperlink extends Html {

	private Set<OnClickListener> listeners = new HashSet<OnClickListener>();
	
	public Hyperlink() {
		this("");
	}
	public Hyperlink(String text) {
		setText(text);
		setStyleName("main-font");
		addListener(Events.OnClick, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				fireEvent(ce);
			}
		});
	}
	
	public void setText(String text) {
		createLink(text);
	}
	
	public void addListener(OnClickListener listener) {
		listeners.add(listener);
	}
	public void removeListener(OnClickListener listener) {
		listeners.remove(listener);
	}
	
	protected final void onRender(Element target, int index) {
		super.onRender(target, index);
		sinkEvents(Events.OnClick);
	}

	private final void fireEvent(ComponentEvent ce) {
		for(OnClickListener listener : listeners)
			listener.clicked(ce);
	}
	private final void createLink(String text) {
		StringBuilder html = new StringBuilder();
		html.append("<span class='x-nodrag' style='text-decoration: underline; cursor: pointer'>");
		html.append(text);
		html.append("</span>&nbsp;");
		setHtml(html.toString());
	}
}


