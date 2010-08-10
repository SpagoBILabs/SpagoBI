/*
*
* @file EditorTab.java
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
* @version $Id: EditorTab.java,v 1.11 2010/04/15 09:55:22 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>EditorTab</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: EditorTab.java,v 1.11 2010/04/15 09:55:22 PhilippBouillon Exp $
 **/
public abstract class EditorTab extends TabItem {
	public EditorTab(String name) {
		super(name);
	}
	public abstract void set(XObject input);
	public abstract boolean save(XObject input);	
	public abstract void saveAs(String name, XObject input);
	
	public void hideHeader() {
		getHeader().hide();
		getHeader().setIntStyleAttribute("height", 0);
//		remove(getHeader());
		getHeader().setStyleName("invisible");
		getHeader().getElement().setAttribute("height", "0px");
		getHeader().getElement().setAttribute("borders", "none");
	}
//TODO we should validate input before saving!	public abstract boolean validate(XObject input);
}
