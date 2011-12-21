/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.ui.widgets;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.table.CellRenderer;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.table.TableItem;

/**
 * <code>CheckboxTableColumn</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CheckBoxTableColumn.java,v 1.4 2010/02/12 13:49:50 PhilippBouillon Exp $
 **/
public class CheckBoxTableColumn extends TableColumn {
	private boolean enabled = true;
	
	public CheckBoxTableColumn(String id) {
		super(id, "", 30);
		setMinWidth(16);
		setMaxWidth(44);
		setAlignment(HorizontalAlignment.CENTER);
		setRenderer(new CellRenderer<TableItem>() {
			public String render(TableItem item, String property, Object value) {
				com.extjs.gxt.ui.client.widget.form.CheckBox cb = new com.extjs.gxt.ui.client.widget.form.CheckBox();
				if (value instanceof Boolean) {
					cb.setValue((Boolean) value);
				} else if (value instanceof CheckBox) {
					cb.setValue(((CheckBox) value).getValue());
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
