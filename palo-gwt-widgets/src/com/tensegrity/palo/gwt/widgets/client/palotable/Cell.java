/*
*
* @file Cell.java
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
* @version $Id: Cell.java,v 1.26 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellFormat;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;


public class Cell extends Composite {

	private static final String STYLE = "cell";
	private Image ruleMarker;
	private final Label value = new Label();
	private final AbsolutePanel content = new AbsolutePanel();
	private int width = -1;
	
	private final XCell xCell;
	
	Cell(XCell xCell) {
		this.xCell = xCell;
		setValue(xCell.value);
		initComponents();
	}
		
	public final void setInternalWidth(int width) {
		this.width = width;
	}
	
	public final XCell getXCell() {
		return xCell;
	}
	public final int getColumn() {
		return xCell.col;
	}
	public final void setColumn(int col) {
		xCell.col = col;
	}
	public final int getRow() {
		return xCell.row;
	}
	public final void setRow(int row) {
		xCell.row = row;
	}
		
	public final String getValue() {
		return value.getText();
	}
	
	public final Label getValueLabel() {
		return value;
	}
	
	public final void setValue(String value) {
		this.value.setText(value);
		this.value.setTitle(value);
		xCell.value = value;
	}
	
	public final void setValue(String value, boolean shorten) {
		this.value.setText(value);
		this.value.setTitle(value);
		xCell.value = value;
		if (shorten) {
			shorten(this.value, Content.MAX_COLUMN_WIDTH);
		}
	}

	public final boolean isEmpty() {
		String value = getValue();
		return value == null || value.equals("");
	}

	public final void markRule(boolean doIt) {
		if(xCell.isRuleBased) {
			createAndAddRuleMarker();
			ruleMarker.setVisible(doIt);
		}
	}
	private final void createAndAddRuleMarker() {
		if(ruleMarker != null)
			return;
		ruleMarker = new Image();
		content.add(ruleMarker);
		content.setWidgetPosition(ruleMarker, 0, 0);
		ruleMarker.setUrl("icons/rule_marker.png");
		ruleMarker.setPixelSize(9, 9);
	}
	
	protected final void shorten(Label name, int max) {
		if (width == -1) {
			width = name.getOffsetWidth();
		}
		if (width > max) {
			name.setText(Content.FILL_STRING);
//	        String txt = name.getText();
//			if (txt.length() > 40) {
//				txt = txt.substring(0, 40);
//			}
//			// TODO is there a better way to do this??
//			while (name.getOffsetWidth() > max && txt.length() > 2) {			
//				txt = txt.substring(0, txt.length() - 1);
//				name.setText(txt + "...");
//			}
		}
	}		
	void layout() {
		//ensure gap between text and right border
        DOM.setStyleAttribute(value.getElement(), "position", "absolute");
        DOM.setStyleAttribute(value.getElement(), "right", "1px");
        shorten(value, Content.MAX_COLUMN_WIDTH);
	}
	private final void initComponents() {
		initWidget(content);
		content.add(value);
		//styles:
		setStyleName(STYLE);
		//set formats:
//		setFormats(xCell.format);
		if (!xCell.isConsolidated)  {
			DOM.setStyleAttribute(getElement(), "backgroundColor", "white");
		}
	}
	private final void setFormats(XCellFormat format) {
		if(format == null)
			return;
		Element cellEl = getElement();
		DOM.setStyleAttribute(cellEl, "color", format.foregroundColor);
		DOM.setStyleAttribute(cellEl, "backgroundColor", format.backgroundColor);
		DOM.setStyleAttribute(cellEl, "font", format.font);
		DOM.setStyleAttribute(cellEl, "textDecoration", format.textDecoration);
		DOM.setStyleAttribute(cellEl, "borderTop", format.borderTop);
		DOM.setStyleAttribute(cellEl, "borderLeft", format.borderLeft);
		DOM.setStyleAttribute(cellEl, "borderRight", format.borderRight);
		DOM.setStyleAttribute(cellEl, "borderBottom", format.borderBottom);
	}
}