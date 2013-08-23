/*
*
* @file HorizontalContainerRenderer.java
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
* @version $Id: HorizontalContainerRenderer.java,v 1.13 2010/04/15 09:54:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>HorizontalContainerRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HorizontalContainerRenderer.java,v 1.13 2010/04/15 09:54:50 PhilippBouillon Exp $
 **/
public abstract class HorizontalContainerRenderer extends AbstractContainerRenderer {
	
	public abstract int getMinHeight();
	private PaloInsert insert = null;
	private PaloReplace replace = null;
	
	public final int [] getInsertIndex(int x, int y) {
		int index = 0;
		int horizontalCorrection = 0;
		for (Widget widget : container.getWidgets()) {
			if (widget.getStyleName().indexOf("dragdrop-dragging") != -1) {
//				horizontalCorrection = widget.getOffsetWidth() + 10;
				continue;
			}			
			int areaWidth = widget.getOffsetWidth() / 2;			
			int leftThreshold = widget.getAbsoluteLeft() + (areaWidth / 2) - horizontalCorrection;
			int rightThreshold = widget.getAbsoluteLeft() + (areaWidth + areaWidth / 2) - horizontalCorrection;
			if (x < leftThreshold) {
				return new int [] {index, 0};
			} else if (x >= leftThreshold && x < rightThreshold) {
				return new int [] {index, 1};
			}
			index++;
		}
		return new int [] {index, 0};
	}
	
	public final Point [] getAdjustedDragMarkPosition(int x, int y) {
		int newX = 0, newY = 0, width = 0, height = 0;
		int horizontalCorrection = 0;
		for (Widget widget : container.getWidgets()) {
			if (widget.getStyleName().indexOf("dragdrop-dragging") != -1) {
				//horizontalCorrection = widget.getOffsetWidth() + 10;
				continue;
			}
			int areaWidth = widget.getOffsetWidth() / 2;			
			int leftThreshold = widget.getAbsoluteLeft() + (areaWidth / 2) - horizontalCorrection;
			int rightThreshold = widget.getAbsoluteLeft() + (areaWidth + areaWidth / 2) - horizontalCorrection;
//			int threshold = 
//					widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2 - horizontalCorrection;
			if (x < leftThreshold) {
				return new Point [] {new Point(widget.getAbsoluteLeft() - 10, widget.getAbsoluteTop()),
						             new Point(widget.getOffsetWidth(), container.getMinHeight() - 20)};
			}
			if (x >= leftThreshold && x < rightThreshold) {
				return new Point [] {new Point(widget.getAbsoluteLeft() + areaWidth - 16, widget.getAbsoluteTop() + (widget.getOffsetHeight() / 2) - 16),
						             new Point(widget.getOffsetWidth(), container.getMinHeight() - 20),
						             new Point(0, 0)};
			}
			newX = widget.getAbsoluteLeft() + widget.getOffsetWidth();
			newY = widget.getAbsoluteTop();
			width = widget.getOffsetWidth();
			height = container.getMinHeight() - 20;
		}
		return new Point [] {new Point(newX, newY),
				             new Point(width, height)};
	}
	
	public Point render(int width, int height) {
		int left = INDENT, top = INDENT;
		if(height < getMinHeight())
			height = getMinHeight();
		// renderer empty label if visible
		Widget emptyLabel = container.getEmptyLabel();
		if (emptyLabel.isVisible()) {
			int y = (height - emptyLabel.getOffsetHeight()) / 2;
			container.setWidgetPosition(emptyLabel, left, y);
		}
		ContainerWidget[] widgets = container.getWidgets();
		for (int i = 0; i < widgets.length; i++) {
			ContainerWidget widget = widgets[i];
			if (i < firstVisible) {
				widget.setVisible(false);
			} else {
				widget.setVisible(true);
//				DOM.setStyleAttribute(widget.getElement(), "top", top + "px");
				container.setWidgetPosition(widget, left, top);
				left += widget.getOffsetWidth() + INDENT;
			}
		}
		return new Point(left, height);
	}
	
	public void renderDragMark() {
		Point p = container.getDragMark();
		if (p.x == -1 || p.y == -1) {
			if (insert != null) {
				insert.setVisible(false);
				insert.remove();
				insert = null;
			}
			if (replace != null) {
				replace.setVisible(false);
				replace.remove();
				replace = null;
			}
			return;
		}		
		Point [] ps = getAdjustedDragMarkPosition(p.x, p.y);
		if (ps[0].x == 0 && ps[0].y == 0) {
			if (insert != null) {
				insert.setVisible(false);
				insert.remove();
				insert = null;				
			}
			if (replace != null) {
				replace.setVisible(false);
				replace.remove();
				replace = null;
			}
			return;			
		}		
	    if (insert != null) {
	    	insert.setVisible(false);
	    	insert.remove();
	    	insert = null;
	    }
		if (replace != null) {
			replace.setVisible(false);
			replace.remove();
			replace = null;
		}
		if (ps.length == 3) {
			replace = new PaloReplace(32);
			replace.setVisible(true);
			replace.el().setBounds(ps[0].x, ps[0].y, 32, 32);
		} else {
			insert = new PaloInsert(ps[1].y);
			insert.setVisible(true);	  
			insert.el().setBounds(ps[0].x, ps[0].y, 10, ps[1].y);
		}
	}
}
