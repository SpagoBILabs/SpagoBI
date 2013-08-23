/*
*
* @file TableHeader.java
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
* @version $Id: TableHeader.java,v 1.12 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.Header;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderLayouter;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.ItemVisitor;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>TableHeader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TableHeader.java,v 1.12 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class TableHeader extends AbsolutePanel {
	
	private static final String STYLE = "header";
	
	private final Header header;
	private final Point size = new Point();
	
	public TableHeader(HeaderLayouter layouter) {
		this.header = new Header(layouter);
		initComponent();
	}

	/** cause to layout the inner content widget */
	public final Point layout() {
		//we should be at least as width/height as inner header!
//		Point hSize = header.layout();
//		if(size.x < hSize.x)
//			size.x = hSize.x;
//		if(size.y < hSize.y)
//			size.y = hSize.y;
//		return size;
		return header.layout();
	}
	
	public final void reset() {
		size.x = 0;
		size.y = 0;
		header.reset();
	}

	public final void initWithCurrentState() {
		header.initWithCurrentState();
	}
	
	public final Header getHeader() {
		return header;
	}
	
	public final XAxisItem[] getExpandedItems() {
		final List<XAxisItem> items = new ArrayList<XAxisItem>();
		header.traverse(new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				if (item.isExpanded())
					items.add(item.getModel());
				return true;
			}
		});
		return items.toArray(new XAxisItem[0]);
	}
	
//	public final void setLayouter(HeaderLayouter layouter) {
//		header.setLayouter(layouter);
//	}
	
	public final void placeContent(int left, int top) {
		setWidgetPosition(header, left, top);
	}
	
	private final void initComponent() {
		add(header);		
		setStyleName(STYLE);
	}
}
