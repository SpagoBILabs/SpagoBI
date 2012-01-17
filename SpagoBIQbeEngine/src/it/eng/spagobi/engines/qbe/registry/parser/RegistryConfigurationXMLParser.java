/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.qbe.registry.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Filter;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;

import com.fdsapi.parser.FDSMathParser.ParseException;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 * 
 */
public class RegistryConfigurationXMLParser {

	public static transient Logger logger = Logger
			.getLogger(RegistryConfigurationXMLParser.class);

	public static String TAG_ENTITY = "ENTITY";
	public static String TAG_FILTERS = "FILTERS";
	public static String TAG_FILTER = "FILTER";
	public static String TAG_COLUMNS = "COLUMNS";
	public static String TAG_COLUMN = "COLUMN";

	public static String ATTRIBUTE_NAME = "name";
	public static String ATTRIBUTE_TITLE = "title";
	public static String ATTRIBUTE_FIELD = "field";
	public static String ATTRIBUTE_PRESENTATION = "presentation";
	
	public static String ATTRIBUTE_EDITOR = "editor";
	public static String ATTRIBUTE_EDITABLE = "editable";
	public static String ATTRIBUTE_VISIBLE = "visible";
	public static String ATTRIBUTE_SUBENTITY = "subEntity";
	public static String ATTRIBUTE_FOREIGNKEY = "foreignKey";
	public static String ATTRIBUTE_MANDATORY_COLUMN = "mandatoryColumn";
	public static String ATTRIBUTE_MANDATORY_VALUE = "mandatoryValue";
	public static String ATTRIBUTE_COLUMNS_MAX_SIZE = "maxSize";
	public static String ATTRIBUTE_COLUMN_SIZE = "size";
	public static String ATTRIBUTE_SORTER = "sorter";
	public static String ATTRIBUTE_UNSIGNED = "unsigned";

	public static String PRESENTATION_TYPE_MANUAL = "MANUAL";
	public static String PRESENTATION_TYPE_COMBO = "COMBO";
	
	public static final String EDITOR_TYPE_TEXT = "TEXT";
	public static final String EDITOR_TYPE_COMBO = "COMBO";

	public RegistryConfiguration parse(SourceBean registryConf) {
		logger.debug("IN");
		RegistryConfiguration toReturn = null;
		try {
			toReturn = new RegistryConfiguration();
			SourceBean entitySB = (SourceBean) registryConf
					.getAttribute(TAG_ENTITY);
			Assert.assertNotNull(entitySB, "TAG " + TAG_ENTITY + " not found");
			String entity = (String) entitySB.getAttribute(ATTRIBUTE_NAME);
			logger.debug("Entity name is " + entity);
			Assert.assertNotNull(entity, "Entity " + ATTRIBUTE_NAME + " attribute not specified.");
			toReturn.setEntity(entity);
			List<RegistryConfiguration.Filter> filters = parseFilters(entitySB, toReturn);
			List<RegistryConfiguration.Column> columns = parseColumns(entitySB, toReturn);
			toReturn.setFilters(filters);
			toReturn.setColumns(columns);
		} finally {
			logger.debug("OUT");
		}
		return toReturn;
	}

	private List<Filter> parseFilters(SourceBean entitySB, RegistryConfiguration toReturn) {
		List<RegistryConfiguration.Filter> list = new ArrayList<RegistryConfiguration.Filter>();
		SourceBean filtersSB = (SourceBean) entitySB.getAttribute(TAG_FILTERS);
		List filters = filtersSB == null ? null : filtersSB.getAttributeAsList(TAG_FILTER);
		if (filters != null && filters.size() > 0) {
			Iterator it = filters.iterator();
			while (it.hasNext()) {
				SourceBean aFilter = (SourceBean) it.next();
				RegistryConfiguration.Filter filter = toReturn.new Filter();
				String field = (String) aFilter
						.getAttribute(ATTRIBUTE_FIELD);
				String title = (String) aFilter
						.getAttribute(ATTRIBUTE_TITLE);
				String presentationType = PRESENTATION_TYPE_COMBO
						.equalsIgnoreCase((String) aFilter
								.getAttribute(ATTRIBUTE_PRESENTATION)) ? Filter.PRESENTATION_TYPE_COMBO
						: Filter.PRESENTATION_TYPE_MANUAL;
				logger.debug("Filter: title " + title + ", field " + field + ", presentation " + presentationType + "");
				Assert.assertTrue(field != null && title != null, "A filter must contain at least attributes " + ATTRIBUTE_TITLE + " and " + ATTRIBUTE_FIELD);
				filter.setField(field);
				filter.setTitle(title);
				filter.setPresentationType(presentationType);
				list.add(filter);
			}
		}
		return list;
	}
	
	private List<Column> parseColumns(SourceBean entitySB,
			RegistryConfiguration toReturn) {
		List<RegistryConfiguration.Column> list = new ArrayList<RegistryConfiguration.Column>();
		SourceBean filtersSB = (SourceBean) entitySB.getAttribute(TAG_COLUMNS);
		//columns max size
		String columnsMaxSize = (String)filtersSB.getAttribute(ATTRIBUTE_COLUMNS_MAX_SIZE);
		toReturn.setColumnsMaxSize(columnsMaxSize);
		List filters = filtersSB == null ? null : filtersSB
				.getAttributeAsList(TAG_COLUMN);
		if (filters != null && filters.size() > 0) {
			Iterator it = filters.iterator();
			while (it.hasNext()) {
				SourceBean aColumn = (SourceBean) it.next();
				RegistryConfiguration.Column column = toReturn.new Column();
				String field = (String) aColumn.getAttribute(ATTRIBUTE_FIELD);
				String subEntity = (String) aColumn
						.getAttribute(ATTRIBUTE_SUBENTITY);
				if (subEntity != null && subEntity.trim().equals("")) {
					subEntity = null;
				}
				String size = (String) aColumn.getAttribute(ATTRIBUTE_COLUMN_SIZE);
				Integer intSize = null;
				try{
					intSize = Integer.parseInt(size);
				}catch(NumberFormatException e){
					logger.debug("Column size not integer");
				}
				String sorter = (String) aColumn.getAttribute(ATTRIBUTE_SORTER);
				boolean unsigned = false;
				if(aColumn.getAttribute(ATTRIBUTE_UNSIGNED) != null){
					try{
						unsigned = Boolean.parseBoolean((String)aColumn.getAttribute(ATTRIBUTE_UNSIGNED));
					}catch(Exception e){
						logger.debug("Column unsigned not boolean");
					}
				}

				String foreignKey = (String) aColumn
						.getAttribute(ATTRIBUTE_FOREIGNKEY);
				boolean isEditable = !"false".equalsIgnoreCase((String) aColumn
						.getAttribute(ATTRIBUTE_EDITABLE));
				boolean isVisible = !"false".equalsIgnoreCase((String) aColumn
						.getAttribute(ATTRIBUTE_VISIBLE));
				String editorType = EDITOR_TYPE_COMBO
						.equalsIgnoreCase((String) aColumn
								.getAttribute(ATTRIBUTE_EDITOR)) ? Column.EDITOR_TYPE_COMBO
						: Column.EDITOR_TYPE_TEXT;
				logger.debug("Column: field " + field + ", subEntity "
						+ subEntity + ", isEditable " + isEditable
						+ ", isVisible " + isVisible + ", editor " + editorType);
				Assert.assertTrue(field != null,
						"A column must contain at least attributes "
								+ ATTRIBUTE_FIELD);
				Assert.assertTrue(subEntity == null || foreignKey != null,
						"If a " + ATTRIBUTE_SUBENTITY
								+ " attribute is specified, the attribute "
								+ ATTRIBUTE_FOREIGNKEY + " is also requested.");
				column.setField(field);
				column.setSize(intSize);
				column.setSorter(sorter);
				column.setUnsigned(unsigned);
				column.setSubEntity(subEntity);
				column.setForeignKey(foreignKey);
				column.setEditable(isEditable);
				column.setVisible(isVisible);
				column.setEditorType(editorType);
				String mandatoryColumn = (String) aColumn.getAttribute(ATTRIBUTE_MANDATORY_COLUMN);
				if(mandatoryColumn != null){
					column.setMandatoryColumn(mandatoryColumn)	;
				}
				String mandatoryValue = (String) aColumn.getAttribute(ATTRIBUTE_MANDATORY_VALUE);
				if(mandatoryValue != null){
					column.setMandatoryValue(mandatoryValue);
				}
				if (subEntity != null) { // if a column is a subEntity reference, the editor is a combo-box
					if (editorType != null && !editorType.trim().equals(EDITOR_TYPE_COMBO)) {
						logger.warn("For sub-entity references, only " + EDITOR_TYPE_COMBO + " is admissible as editor type");
					}
					column.setEditorType(EDITOR_TYPE_COMBO);
				}
				
				list.add(column);
			}
		}
		return list;
	}


}
