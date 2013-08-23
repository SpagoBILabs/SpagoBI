/*
*
* @file ColumnHierarchyWidget.java
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
* @version $Id: ColumnHierarchyWidget.java,v 1.9 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.cubevieweditor;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.widgets.client.container.XObjectContainer;

/**
 * <code>HierachyWidget</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ColumnHierarchyWidget.java,v 1.9 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
class ColumnHierarchyWidget extends HierarchyWidget {

	private static final String STYLE = "palo-gwt-hierarchy-widget";

	public ColumnHierarchyWidget(XObjectContainer container, XAxisHierarchy hierarchy) {
		super(container, hierarchy);
	}

	protected final String getStyle() {
		return STYLE;
	}

	protected void layout() {
		content.setWidgetPosition(hierarchyName, 3, 3);
		if (hierarchyName.getOffsetWidth() > 97) {
			shorten(hierarchyName, 97);
		}		
		placeFilterWidget(104, 4);
	}
	
	protected void setLabel(String hierName) {
		hierarchyName.setText(hierName);
		// Don't set the width here, otherwise, FF will not return
		// getOffsetWidth properly
//		hierarchyName.setWidth(97 + "px");
		if (hierarchyName.getOffsetWidth() > 97) {
			shorten(hierarchyName, 97);
		}		
	}
	
	protected final void shorten(Label name, int max) {
		String txt = name.getText();
		if (txt.length() > 40) {
			txt = txt.substring(0, 40);
		}
		// TODO is there a better way to do this??
		while (name.getOffsetWidth() > max && txt.length() > 2) {			
			txt = txt.substring(0, txt.length() - 1);
			name.setText(txt + "...");
		}
	}	
}
