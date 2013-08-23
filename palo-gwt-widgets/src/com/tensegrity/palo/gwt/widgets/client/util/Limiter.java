/*
*
* @file Limiter.java
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
* @version $Id: Limiter.java,v 1.10 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.util;

import com.google.gwt.user.client.ui.UIObject;

/**
 * <code>Limiter</code>
 * TODO REMOVE ME!!!
 * @deprecated WILL BE REMOVED SOON! Please use {@link Ruler} instead.
 * @version $Id: Limiter.java,v 1.10 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class Limiter {

//	public static final void setClientSize(UIObject obj, int width, int height,
//			BorderSize border) {
//		setClientWidth(obj, width, border);
//		setClientHeight(obj, height, border);
//	}
//
//	public static final void setClientWidth(UIObject obj, int width,
//			BorderSize border) {
//		if (UserAgent.isFirefox()) {
//			width -= border.left - border.right;
//		}
//		if (width < 0)
//			width = 0;
//		obj.setWidth(width + "px");
//	}
//	public static final void setClientHeight(UIObject obj, int height,
//			BorderSize border) {
//		if (UserAgent.isFirefox()) {
//			height -= border.top - border.bottom;
//		}
//		if (height < 0)
//			height = 0;
//		obj.setHeight(height + "px");
//	}

	public static final int setClientWidth(UIObject obj, int max) {
		if(max <= 0) return 0;
		// TODO check that object is attached or mention this requirement in
		// javadoc!!
		obj.setWidth(max + "px");
		int offset = obj.getOffsetWidth();
		int width = adjust(max, offset);
		obj.setWidth(width + "px");
		return width;
	}
	
	public static final int setClientHeight(UIObject obj, int max) {
		if(max <= 0) return 0;
		obj.setHeight(max + "px");
		int offset = obj.getOffsetHeight();
		int height = adjust(max, offset);
		obj.setHeight(height + "px");
		return height;
	}
	
	public static final int setClientWidth(int max) {
		if(max <= 0) return 0;
		// TODO check that object is attached or mention this requirement in
		// javadoc!!
		int width = adjust(max, 0);
		return width;
	}
	
	public static final int setClientHeight(int max) {
		if(max <= 0) return 0;
		int height = adjust(max, 0);
		return height;
	}

	public static final void setClientSize(UIObject obj, int maxW, int maxH) {
		setClientWidth(obj, maxW);
		setClientHeight(obj, maxH);
	}
	
	private static final int adjust(int max, int offset) {
		if(offset <= 0) offset = max;
		int size = 2 * max - offset;
		if(size < 0) size = max;
		if (size > max) size = max;
		return size;
	}
}
