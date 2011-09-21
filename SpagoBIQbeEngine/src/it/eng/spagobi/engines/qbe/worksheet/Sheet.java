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
package it.eng.spagobi.engines.qbe.worksheet;

import it.eng.spagobi.engines.qbe.worksheet.bo.Attribute;
import it.eng.spagobi.engines.qbe.worksheet.bo.Field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *          Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class Sheet {
	
	private String name;
	private String layout;
	private JSONObject header;
	private List<Attribute> filters;
	private SheetContent content;
	private JSONObject footer;
	

	/**
	 * @param name
	 * @param layout
	 * @param header
	 * @param filters
	 * @param content
	 * @param footer
	 */
	public Sheet(String name, String layout, JSONObject header,
			List<Attribute> filters, SheetContent content, JSONObject footer) {
		super();
		this.name = name;
		this.header = header;
		this.layout = layout;
		this.filters = filters;
		this.content = content;
		this.footer = footer;
	}
	public JSONObject getHeader() {
		return header;
	}
	public void setHeader(JSONObject header) {
		this.header = header;
	}
	public List<Attribute> getFilters() {
		return filters;
	}
	public void setFilters(List<Attribute> filters) {
		this.filters = filters;
	}
	public SheetContent getContent() {
		return content;
	}
	public void setContent(SheetContent content) {
		this.content = content;
	}
	public JSONObject getFooter() {
		return footer;
	}
	public void setFooter(JSONObject footer) {
		this.footer = footer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public List<Attribute> getAllFilters() {
		List<Attribute> toReturn = new ArrayList<Attribute>();
		WorkSheetDefinition.addFilters(toReturn, getFilters());
		WorkSheetDefinition.addFilters(toReturn, getContent().getFilters());
		return toReturn;
	}
	public List<Field> getAllFields() {
		List<Field> toReturn = new ArrayList<Field>();
		List<Attribute> filters = this.getFilters();
		toReturn.addAll(filters);
		SheetContent content = this.getContent();
		List<Field> fields = content.getAllFields();
		toReturn.addAll(fields);
		return toReturn;
	}
}
