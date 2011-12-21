/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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


