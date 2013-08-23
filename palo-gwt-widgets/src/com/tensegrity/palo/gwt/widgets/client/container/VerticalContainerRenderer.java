/*
*
* @file VerticalContainerRenderer.java
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
* @version $Id: VerticalContainerRenderer.java,v 1.12 2010/04/15 09:54:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.container;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>VerticalRenderer</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: VerticalContainerRenderer.java,v 1.12 2010/04/15 09:54:50 PhilippBouillon Exp $
 **/
public abstract class VerticalContainerRenderer extends AbstractContainerRenderer {

//	protected static final String STYLE_EMPTY_LABEL_VERTICAL = "empty-label-vertical";
	
	public abstract int getMinWidth();
	private PaloInsert insert = null;
	private PaloReplace replace = null;
	
	public Label createEmptyLabel(String label) {
		Label _label = new Label();
		_label.getElement().setInnerHTML(toVertical(label));
		_label.setStyleName(getStyle());		
		return _label;
	}

	public int [] getInsertIndex(int x, int y) {
		int index = 0;
		int verticalCorrection = 0;
		for (Widget hierarchy : container.getWidgets()) {
			if (hierarchy.getStyleName().indexOf("dragdrop-dragging") != -1) {
//				verticalCorrection = hierarchy.getOffsetHeight() + 10;
				continue;
			}			
			int areaHeight = hierarchy.getOffsetHeight() / 2;			
			int topThreshold = hierarchy.getAbsoluteTop() + (areaHeight / 2) - verticalCorrection;
			int bottomThreshold = hierarchy.getAbsoluteTop() + (areaHeight + areaHeight / 2) - verticalCorrection;
//			int threshold = hierarchy.getAbsoluteTop()
//					+ hierarchy.getOffsetHeight() / 2;
			if (y < topThreshold) {
				return new int [] {index, 0};
			} else if (y >= topThreshold && y < bottomThreshold) {
				return new int [] {index, 1};
			}
			index++;
		}
		return new int [] {index, 0};
	}
	
	public final Point [] getAdjustedDragMarkPosition(int x, int y) {
		int newX = 0, newY = 0, width = 0, height = 0;
		int verticalCorrection = 0;
		for (Widget widget : container.getWidgets()) {
			if (widget.getStyleName().indexOf("dragdrop-dragging") != -1) {
//				verticalCorrection = widget.getOffsetHeight() + 10;
				continue;
			}
			int areaHeight = widget.getOffsetHeight() / 2;			
			int topThreshold = widget.getAbsoluteTop() + (areaHeight / 2) - verticalCorrection;
			int bottomThreshold = widget.getAbsoluteTop() + (areaHeight + areaHeight / 2) - verticalCorrection;
			
			if (y < topThreshold) {
				return new Point [] {new Point(widget.getAbsoluteLeft(), widget.getAbsoluteTop() - 10),
                        new Point(widget.getOffsetWidth(), widget.getOffsetHeight())};
			}
			if (y >= topThreshold && y < bottomThreshold) {
				return new Point [] {new Point(widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2 - 16, widget.getAbsoluteTop() + areaHeight - 16),
						             new Point(widget.getOffsetWidth(), widget.getOffsetHeight()),
						             new Point(0,0)};
			}

			newX = widget.getAbsoluteLeft();
			newY = widget.getAbsoluteTop() + widget.getOffsetHeight();
			width = widget.getOffsetWidth();
			height = widget.getOffsetHeight();
		}
		return new Point [] {new Point(newX, newY),
				             new Point(width, height)};
	}
	
	
	public Point render(int width, int height) {
		int left = INDENT, top = INDENT;
		if(width < getMinWidth())
			width = getMinWidth();
		// renderer empty label if visible
		Widget emptyLabel = container.getEmptyLabel();
		if (emptyLabel != null && emptyLabel.isVisible()) {
			int labelW = emptyLabel.getOffsetWidth();
			int x = (width - labelW)/2;
			if(x < 1)
				x = labelW / 2;
			container.setWidgetPosition(emptyLabel, x, top);
		}
		ContainerWidget[] widgets = container.getWidgets();
		for (int i = 0; i < widgets.length; i++) {
			ContainerWidget widget = widgets[i];
			if (i < firstVisible) {
				widget.setVisible(false);
			} else {
				widget.setVisible(true);
				container.setWidgetPosition(widget, left, top);
				top += widget.getOffsetHeight() + INDENT;
			}
		}
		return new Point(width, top);
	}
	
	protected String toVertical(String txt) {
		char[] letters = txt.toCharArray();
		String style = getStyle();
		StringBuilder html = new StringBuilder();
		html.append("<div text-align=\"center\">");
		for (char c : letters) {
			html.append("<div class=\"" + style + "\" align=\"center\">");
			html.append(c == ' ' ? "<br>" : c);
			html.append("</div>");
		}		
		html.append("</div>");
		return html.toString();
	}
	
	protected String getStyle() {
		return STYLE_EMPTY_LABEL;
	}
	
	public void renderDragMark() {
		Point p = container.getDragMark();
		if (p.x == -1 || p.y == -1) {
		    if (insert != null) {
		    	insert.remove();
		    	insert = null;
		    }
		    if (replace != null) {
		    	replace.remove();
		    	replace = null;
		    }	    		    
			return;
		}		
		Point [] ps = getAdjustedDragMarkPosition(p.x, p.y);
		if (ps[0].x == 0 && ps[0].y == 0) {
		    if (insert != null) {		    	
		    	insert.remove();
		    	insert = null;
		    }	    
		    if (replace != null) {
		    	replace.remove();
		    	replace = null;
		    }	    		    
			return;			
		}
	    if (insert != null) {
	    	insert.remove();
	    	insert = null;
	    }	  
	    if (replace != null) {
	    	replace.remove();
	    	replace = null;
	    }
	    if (ps.length == 3) {
			replace = new PaloReplace(32);
			replace.setVisible(true);
			replace.el().setBounds(ps[0].x, ps[0].y, 32, 32);	    	
	    } else {
	    	insert = new PaloInsert(10);
	    	insert.setVisible(true);	  
	    	insert.el().setBounds(ps[0].x, ps[0].y, ps[1].x, 10);
	    }
	}
}
