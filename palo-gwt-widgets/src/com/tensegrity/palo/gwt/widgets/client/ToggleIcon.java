/*
*
* @file ToggleIcon.java
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
* @version $Id: ToggleIcon.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

/**
 * <code>EnableableImage</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ToggleIcon.java,v 1.3 2010/03/12 12:49:13 PhilippBouillon Exp $
 **/
public class ToggleIcon extends Image {
	
	private boolean enabled;
	private String on_url;
	private String off_url;
	
	public ToggleIcon(String on_url, String off_url, int width, int height) {
		super(on_url, 0, 0, width, height);
		enabled = true;
		this.on_url = on_url;
		this.off_url = off_url;
	}

	public final void setOnIcon(String url, int width, int height) {
		this.on_url = url;
		setWidth(width + "px");
		setHeight(height + "px");
		if(isEnabled())
			setUrl(url);
	}
	public final void setOffIcon(String url, int width, int height) {
		this.off_url = url;
		setWidth(width + "px");
		setHeight(height + "px");
		if(!isEnabled())
			setUrl(url);
	}
	
	public final boolean isEnabled() {
		return enabled;
	}
	
//	public final boolean isOn() {
//		return enabled;
//	}
//	
//	public final void setState(boolean on) {
//		setEnabled(on);
//	}
	
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setUrl(enabled ? on_url : off_url);
	}

	public void onBrowserEvent(Event event) {
		if(!isEnabled())
			return;
		super.onBrowserEvent(event);
	}

	
}
