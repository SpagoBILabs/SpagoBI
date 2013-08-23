/*
*
* @file Ruler.java
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
* @version $Id: Ruler.java,v 1.7 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Ruler extends Widget {

	private static int borderOffset;
	static {
		new Ruler();
	}

	private Ruler() {
		init();
		determineOffsets();
	}

//	public static final native void setSize(Element elem, int w, int h)/*-{
//		if (w >= 0) {
//		  elem.style['width']=w;
//		}
//		if (h >= 0) {
//		  elem.style['height']=h;
//		}
// 	}-*/;
//	
//	public static final native void setPosition(Element elem, int x, int y)/*-{
//		elem.style['position']='absolute';
//		if (x >= 0) {
//		  elem.style['left']=x;
//		}
//		if (y >= 0) {
//		  elem.style['top']=y;
//		}
//	}-*/;
	
	public static final void setSize(Widget widget, int w, int h) {
		Style style = widget.getElement().getStyle();		
		if (w >= 0)
			style.setPropertyPx("width", w);
		if (h >= 0)
			style.setPropertyPx("height", h);
	}

	public static final void setPosition(Widget widget, int x, int y) {
		Style style = widget.getElement().getStyle();
		style.setProperty("position", "absolute");
		if (x >= 0)
			style.setPropertyPx("left", x);
		if (y >= 0)
			style.setPropertyPx("top", y);
	}
	
	public static final int getBorderOffset() {
		return borderOffset;
	}
	
	public static final int getClientHeight(Widget widget) {
		return getClientHeight(widget.getElement());
	}
	public static final void setClientHeight(Widget widget, int height) {
//	    assert height >= 0 : "CSS heights should not be negative";
		if(height < 0) height = 0;
	    widget.getElement().getStyle().setPropertyPx("height", height);
	}
	
	public static final int getClientWidth(Widget widget) {
		return getClientWidth(widget.getElement());
	}
	public static final void setClientWidth(Widget widget, int width) {
//	    assert width >= 0 : "CSS widths should not be negative";
		if(width < 0) width= 0;
	    widget.getElement().getStyle().setPropertyPx("width", width);
	}

	public static final void setClientSize(Widget widget, int width, int height) {
		setClientWidth(widget, width);
		setClientHeight(widget, height);
	}

	private static final native int getClientWidth(Element elem) /*-{
		return elem.clientWidth;
	}-*/;

	private static final native int getClientHeight(Element elem) /*-{
		return elem.clientHeight;
	}-*/;

	private final void init() {
		setElement(DOM.createDiv());
		DOM.setStyleAttribute(getElement(), "borderLeft", "1px solid white");
		RootPanel.get().add(this);
	}

	private final void determineOffsets() {
		int a = 40;
		setPixelSize(a, a);
		setVisible(true);
		borderOffset = getOffsetWidth() - a;
		setVisible(false);
		RootPanel.get().remove(this);
	}
}