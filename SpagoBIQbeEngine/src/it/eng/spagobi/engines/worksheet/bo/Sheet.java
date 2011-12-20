/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.spagobi.engines.worksheet.bo;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *          Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class Sheet {
	
	public enum FiltersPosition {TOP, LEFT};
	
	private String name;
	private String layout;
	private JSONObject header;
	private List<Filter> filters;
	private FiltersPosition filtersPosition;
	private SheetContent content;
	private List<Attribute> filtersOnDomainValues;
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
			List<Filter> filters, SheetContent content, List<Attribute> filtersOnDomainValues, JSONObject footer) {
		super();
		this.name = name;
		this.header = header;
		this.layout = layout;
		this.filters = filters;
		this.content = content;
		this.filtersOnDomainValues = filtersOnDomainValues;
		this.footer = footer;
		this.filtersPosition = FiltersPosition.TOP;
	}
	public Sheet(String name, String layout, JSONObject header,
			List<Filter> filters, FiltersPosition filtersPosition, SheetContent content, 
			List<Attribute> filtersOnDomainValues, JSONObject footer) {
		this(name, layout, header, filters, content, filtersOnDomainValues, footer);
		this.filtersPosition = filtersPosition;
	}
	public JSONObject getHeader() {
		return header;
	}
	public void setHeader(JSONObject header) {
		this.header = header;
	}
	public List<Filter> getFilters() {
		return filters;
	}
	public void setFilters(List<Filter> filters) {
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
	public FiltersPosition getFiltersPosition() {
		return filtersPosition;
	}
	public void setFiltersPosition(FiltersPosition filtersPosition) {
		this.filtersPosition = filtersPosition;
	}
	public List<Attribute> getFiltersOnDomainValues() {
//		List<Attribute> toReturn = new ArrayList<Attribute>();
//		WorkSheetDefinition.addDomainValuesFilters(toReturn, getFilters());
//		WorkSheetDefinition.addDomainValuesFilters(toReturn, getContent().getFiltersOnDomainValues());
//		return toReturn;
		return this.filtersOnDomainValues;
	}
	public void setFiltersOnDomainValues(List<Attribute> filtersOnDomainValues) {
		this.filtersOnDomainValues = filtersOnDomainValues;
	}
	public List<Field> getAllFields() {
		List<Field> toReturn = new ArrayList<Field>();
		List<Filter> filters = this.getFilters();
		toReturn.addAll(filters);
		SheetContent content = this.getContent();
		List<Field> fields = content.getAllFields();
		toReturn.addAll(fields);
		return toReturn;
	}
}
