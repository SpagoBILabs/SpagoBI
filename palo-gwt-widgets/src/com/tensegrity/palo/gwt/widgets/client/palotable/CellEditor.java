/*
*
* @file CellEditor.java
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
* @version $Id: CellEditor.java,v 1.10 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code>CellEditor</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CellEditor.java,v 1.10 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public class CellEditor implements KeyboardListener {

	private final TextBox cellEditor = new TextBox();
	private CellChangedListener listener;
	private Cell editCell;
	
	public CellEditor() {
		initComponent();
		initEventHandling();
	}

	public Widget getComponent() {
		return cellEditor;
	}
	
	public final void addCellChangedListener(CellChangedListener listener) {
		this.listener = listener;
	}
	
	public final void edit(Cell cell) {
		editCell = cell;
		String title = cell.getValueLabel().getTitle();
		cellEditor.setText((title == null || title.isEmpty()) ? cell.getValue() : title);		
		cellEditor.setVisible(true);		
		cellEditor.setFocus(true);
		cellEditor.selectAll();
	}

	public final void setSize(int width, int height) {
		cellEditor.setPixelSize(width, height);
	}
	
	private final void initComponent() {
		cellEditor.setVisible(false);
		DOM.setStyleAttribute(cellEditor.getElement(), "backgroundColor", "white");
		DOM.setStyleAttribute(cellEditor.getElement(), "zIndex", "10000");
	}
	
	private final void initEventHandling() {
		cellEditor.addKeyboardListener(this);
		cellEditor.addFocusListener(new FocusListener() {
			public void onFocus(Widget sender) {
			}
			public void onLostFocus(Widget sender) {
				cellEditor.setVisible(false);
			}
		});
//GWT 1.6:		
//		cellEditor.addBlurHandler(new BlurHandler() {
//			public void onBlur(BlurEvent event) {
//				cellEditor.setVisible(false);
//			}
//		});
	}

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
	}
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	}
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		String newValue = "";
		switch(keyCode) {
		case KEY_ENTER:			
			newValue = cellEditor.getText();
			String oldValue = editCell.getValueLabel().getTitle();
			if(!newValue.equals(oldValue)) {
				editCell.setValue(newValue);
				if(listener != null)
					listener.changed(editCell, oldValue);
			}
		case KEY_TAB:
			cellEditor.setVisible(false);
		}
	}
	
	
}
