/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.worksheet.widgets;

import it.eng.spagobi.engines.qbe.worksheet.SheetContent;
import it.eng.spagobi.engines.qbe.worksheet.bo.Attribute;
import it.eng.spagobi.engines.qbe.worksheet.bo.Field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class TableDefinition extends SheetContent {
	
	private List<Field> fields;
	
	public TableDefinition() {
		fields = new ArrayList<Field>();
	}
	
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public void addField(Field field) {
		this.fields.add(field);
	}

	@Override
	public List<Attribute> getFilters() {
		List<Attribute> toReturn = new ArrayList<Attribute>();
		List<Field> fields = getFields();
		Iterator<Field> it = fields.iterator();
		while (it.hasNext()) {
			Field field = it.next();
			if (field instanceof Attribute) {
				Attribute attribute = (Attribute) field;
				String values = attribute.getValues();
				if (values != null && !values.equals(new JSONArray().toString())) {
					toReturn.add(attribute);
				}
			}
		}
		return toReturn;
	}

	@Override
	public List<Field> getAllFields() {
		return getFields();
	}
}
