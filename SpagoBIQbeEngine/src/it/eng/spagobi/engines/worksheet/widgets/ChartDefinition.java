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

import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Field;
import it.eng.spagobi.engines.worksheet.bo.Measure;
import it.eng.spagobi.engines.worksheet.bo.SheetContent;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class ChartDefinition extends SheetContent {
	
	private Attribute category = null;
	private List<Measure> series = null;
	private JSONObject config = null;
	
	public ChartDefinition() {}

	public Attribute getCategory() {
		return category;
	}

	public void setCategory(Attribute category) {
		this.category = category;
	}

	public List<Measure> getSeries() {
		return series;
	}

	public void setSeries(List<Measure> series) {
		this.series = series;
	}

	public JSONObject getConfig() {
		return config;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}

	@Override
	public List<Attribute> getFilters() {
		List<Attribute> toReturn = new ArrayList<Attribute>();
		Attribute category = getCategory();
		String values = category.getValues();
		if (values != null && !values.equals(new JSONArray().toString())) {
			toReturn.add(category);
		}
		return toReturn;
	}

	@Override
	public List<Field> getAllFields() {
		List<Field> toReturn = new ArrayList<Field>();
		toReturn.add(getCategory());
		toReturn.addAll(getSeries());
		return toReturn;
	}

}
