/*
*
* @file RadioTableColumn.java
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
* @version $Id: RadioTableColumn.java,v 1.4 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.table.CellRenderer;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableItem;

/**
 * <code>CheckboxTableColumn</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: RadioTableColumn.java,v 1.4 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class RadioTableColumn extends TableColumn {
	private boolean enabled = true;
	
	public RadioTableColumn(String id) {
		super(id, "", 30);
		setMinWidth(16);
		setMaxWidth(44);
		setAlignment(HorizontalAlignment.CENTER);
		setRenderer(new CellRenderer<TableItem>() {
			public String render(TableItem item, String property, Object value) {
				Radio cb = new Radio();
				if (value instanceof Boolean) {
					cb.setValue((Boolean) value);
				} else if (value instanceof Radio) {
					cb.setValue(((Radio) value).getValue());
				}
				cb.setEnabled(enabled);
				
				// cb.add
				// CheckBox cb = new CheckBox();
				// if(value instanceof Boolean)
				// cb.setChecked(((Boolean)value).booleanValue());
				//				
				// cb.addClickListener(new ClickListener() {
				// public void onClick(Widget arg0) {
				// }
				// });
				item.setWidget(0, cb);
				return "";
			}
		});
	}

	public void setEnabled(boolean en) {
		this.enabled = en;
	}
}
