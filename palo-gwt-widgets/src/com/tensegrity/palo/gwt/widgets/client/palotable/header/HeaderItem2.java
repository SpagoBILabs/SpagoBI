/*
*
* @file HeaderItem2.java
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
* @version $Id: HeaderItem2.java,v 1.4 2010/02/12 13:50:48 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code>HeaderItem</code> TODO DOCUMENT ME
 * 
 * @version $Id: HeaderItem2.java,v 1.4 2010/02/12 13:50:48 PhilippBouillon Exp $
 **/
class HeaderItem2 extends Widget { // implements SourcesClickEvents {
	/**
	 * suggested css: - item-font -
	 * 
	 */
	private static final int INDENT = 2;

	private final Element icon;
	private final Element caption;
	private boolean expanded;
	// private final boolean hasChildren;
	private ClickListenerCollection clickListeners = new ClickListenerCollection();

	public HeaderItem2(String name, boolean hasChildren) {
		// this.hasChildren = hasChildren;

		// create content:
		setElement(DOM.createDiv()); // the main element
		// sinkEvents(Event.ONCLICK);

		// DOM.setStyleAttribute(getElement(), "className", "item");
		// setStyleName("item");
		// DOM.setStyleAttribute(getElement(), "display", "inline");
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "position", "absolute");
		DOM.setStyleAttribute(getElement(), "border", "1px solid black");

		if (hasChildren) {
			icon = DOM.createImg();
			// DOM.setStyleAttribute(icon, "className", "plus visible");
			// icon.set
			// setStyleName(style)
			// DOM.setStyleAttribute(icon, "display", "inline");
			DOM.setElementProperty(icon, "className", "item-icon-plus");
			// DOM.setStyleAttribute(icon, "display", "inline");
			DOM.setStyleAttribute(icon, "position", "absolute");
			// DOM.setStyleAttribute(icon, "width", "9px");
			// DOM.setStyleAttribute(icon, "height", "9px");
			getElement().appendChild(icon);

			DOM.sinkEvents(icon, Event.ONCLICK | DOM.getEventsSunk(icon));
			DOM.setEventListener(icon, this);

			// CSS
		} else
			icon = null;

		caption = DOM.createSpan();
		// DOM.setElementProperty(elem.<com.google.gwt.user.client.Element>
		// cast(),
		// "className", styleName);
		// DOM.setElementProperty(caption, "className", "item");
		DOM.setStyleAttribute(caption, "position", "absolute");
		// FONT:
		DOM.setStyleAttribute(caption, "fontFamily", "Tahoma");
		DOM.setStyleAttribute(caption, "fontSize", "11px");
		DOM.setStyleAttribute(caption, "whiteSpace", "nowrap");
		caption.setInnerText(name);
		getElement().appendChild(caption);

	}

	// public void onAttach() {
	// //we register icon as eventlistener and sink
	// if(icon != null) {
	// DOM.sinkEvents(icon, Event.ONCLICK | DOM.getEventsSunk(icon));
	// DOM.setEventListener(icon, this);
	// }
	// super.onAttach();
	// }
	public void onDetach() {
		if (icon != null) {
			DOM.setEventListener(icon, null);
		}
		super.onDetach();
	}

	public void onLoad() {
		// // Reset the position attribute of the parent element
		// DOM.setStyleAttribute(getElement(), "position", "relative");
		// redraw();
		layout();
	}

	public final void layout() {
		int x = INDENT;
		int y = INDENT;
		int height = Math
				.max(icon.getOffsetHeight(), caption.getOffsetHeight())
				+ 2 * INDENT;
		if (icon != null) {
			// place icon:
			setPosition(icon, x, y + (height - icon.getOffsetHeight()) / 2 - 1);
			x += icon.getOffsetWidth() + INDENT;
		}
		setPosition(caption, x, y);
		x += caption.getOffsetWidth() + INDENT;
		// y += height + INDENT;
		setPixelSize(x, height);
		// Size size = Ruler.measureMe(getName());
	}

	public final void setPosition(int x, int y) {
		setPosition(getElement(), x, y);
	}

	private final void setPosition(Element elem, int x, int y) {
		DOM.setStyleAttribute(elem, "left", x + "px");
		DOM.setStyleAttribute(elem, "top", y + "px");
	}

	public final String getName() {
		return caption.getInnerText();
	}

	public final int getStyle() {
		return 0;
		/*
		 * On 9 avr, 00:56, tomoucb <tomo...@gmail.com> wrote:
		 * 
		 * > Hi folks,
		 * 
		 * > Many thanks to the GWT team for a great set of tools!
		 * 
		 * > I'm trying to access a value set in a CSS, but getStyleAttribute >
		 * doesn't seem to be able to access the CSS portion of the DOM (as of >
		 * 1.5M2).
		 * 
		 * That has nothing to do with GWT: getStyleAttribute looks at the
		 * .style. object from the element, which reflects its inline style
		 * (style= attribute on this element in markup).
		 * 
		 * Internet Explorer has runtimeStyle while others have the DOM-Style's
		 * getComputedStyle(): elt.runtimeStyle.color vs.
		 * window.getComputedStyle(elt, null).style.color
		 * 
		 * There's nothing in GWT yet for those, but you could do it yourself
		 * with some JSNI (and eventually rebinding to make it really clean).
		 * 
		 * > Note, all this is ultimately to allow UI/CSS designers to set the >
		 * TabIndex by setting a CSS value that GWT will get (from CSS) and set
		 * > (in GWT-JS) so if there's a better way to do this, that would be a
		 * > great help!
		 * 
		 * Which CSS property would you use for the tab index???
		 * 
		 * > Here's some sample code that tries to read the style set in the
		 * HTML - > the first pass at getting the "left" value fails, but the
		 * second > attempt which uses the GWT setStyleAttribute works fine.
		 * 
		 * For those (left and top), use getOffsetLeft/getOffsetTop.
		 */
	}

	public void addClickListener(ClickListener listener) {
		clickListeners.add(listener);
	}

	public void removeClickListener(ClickListener listener) {
		clickListeners.remove(listener);
	}

	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() == Event.ONCLICK) {
			clickListeners.fireClick(this);
			expanded = !expanded;
			DOM.setElementProperty(icon, "className",
					expanded ? "item-icon-minus" : "item-icon-plus");
		}
	}

}
