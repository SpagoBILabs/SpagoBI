/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.registry.serializer;

import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.type.SerializationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 * 
 */
public class RegistryConfigurationJSONSerializer {

	public static transient Logger logger = Logger
			.getLogger(RegistryConfigurationJSONSerializer.class);

	public static String ENTITY = "entity";
	public static String FILTERS = "filters";
	public static String COLUMNS = "columns";

	public static String TITLE = "title";
	public static String FIELD = "field";
	public static String PRESENTATION = "presentation";
	
	public static String EDITABLE = "editable";
	public static String VISIBLE = "visible";
	public static String EDITOR_TYPE = "editor";
	public static String SUBENTITY = "subEntity";
	public static String FOREIGNKEY = "foreignKey";
	public static String MANDATORY_COLUMN = "mandatoryColumn";
	public static String MANDATORY_VALUE = "mandatoryValue";

	public JSONObject serialize(RegistryConfiguration conf) {
		logger.debug("IN");
		JSONObject toReturn = null;
		try {
			toReturn = new JSONObject();
			String entity = conf.getEntity();
			toReturn.put(ENTITY, entity);
			JSONArray filtersJSON = serializeFilters(conf);
			toReturn.put(FILTERS, filtersJSON);
			JSONArray columnsJSON = serializeColumns(conf);
			toReturn.put(COLUMNS, columnsJSON);
		} catch (Exception e) {
			throw new SerializationException("Error while serializating RegistryConfiguration", e);
		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}


	private JSONArray serializeFilters(RegistryConfiguration conf) throws JSONException {
		List<RegistryConfiguration.Filter> filters = conf.getFilters();
		JSONArray filtersJSON = new JSONArray();
		Iterator<RegistryConfiguration.Filter> it = filters.iterator();
		while (it.hasNext()) {
			RegistryConfiguration.Filter filter = it.next();
			JSONObject filterJSON = new JSONObject();
			String title = filter.getTitle();
			String field = filter.getField();
			String presentationType = filter.getPresentationType();
			filterJSON.put(TITLE, title);
			filterJSON.put(FIELD, field);
			filterJSON.put(PRESENTATION, presentationType);
			filtersJSON.put(filterJSON);
		}
		return filtersJSON;
	}

	private JSONArray serializeColumns(RegistryConfiguration conf) throws JSONException {
		List<RegistryConfiguration.Column> columns = conf.getColumns();
		JSONArray columnsJSON = new JSONArray();
		Iterator<RegistryConfiguration.Column> it = columns.iterator();
		while (it.hasNext()) {
			RegistryConfiguration.Column column = it.next();
			JSONObject columnJSON = new JSONObject();
			String field = column.getField();
			String subentity = column.getSubEntity();
			String foreignKey = column.getForeignKey();
			boolean isEditable = column.isEditable();
			boolean isVisible = column.isVisible();
			String editorType = column.getEditorType();
			columnJSON.put(FIELD, field);
			if (subentity != null) {
				columnJSON.put(SUBENTITY, subentity);
				columnJSON.put(FOREIGNKEY, foreignKey);
			}
			columnJSON.put(EDITABLE, isEditable);
			columnJSON.put(VISIBLE, isVisible);
			columnJSON.put(EDITOR_TYPE, editorType);
			String mandatoryCol = column.getMandatoryColumn();
			if(mandatoryCol != null){
				columnJSON.put(MANDATORY_COLUMN, mandatoryCol);
			}
			String mandatoryVal = column.getMandatoryValue();
			if(mandatoryVal != null){
				columnJSON.put(MANDATORY_VALUE, mandatoryVal);
			}
			columnJSON.put(EDITOR_TYPE, editorType);
			columnsJSON.put(columnJSON);
		}
		return columnsJSON;
	}
	
}
