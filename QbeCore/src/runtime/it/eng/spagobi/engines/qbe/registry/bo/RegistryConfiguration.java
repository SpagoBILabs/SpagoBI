/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.registry.bo;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class RegistryConfiguration {

	public static transient Logger logger = Logger.getLogger(RegistryConfiguration.class);
	
	private String keyField = null;
	
	private List<Filter> filters = null;
	private List<Column> columns = null;
	private List<Configuration> configurations = null;

	private String entity = null;
	private String columnsMaxSize = null;
	
	public String getColumnsMaxSize() {
		return columnsMaxSize;
	}

	public void setColumnsMaxSize(String columnsMaxSize) {
		this.columnsMaxSize = columnsMaxSize;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public List<Filter> getFilters() {
		return filters;
	}


	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	
	
	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public Column getColumnConfiguration(String fieldName) {
		if (this.columns == null || this.columns.size() == 0) {
			logger.warn("No columns are defined. Column for field " + fieldName + " not found");
			return null;
		}
		Iterator<Column> it = columns.iterator();
		while (it.hasNext()) {
			Column c = it.next();
			if (c.getField().equals(fieldName)) {
				return c;
			}
		}
		logger.warn("Column for field " + fieldName + " not found");
		return null;
	}
	
	
	
	public String getConfiguration(String name) {
		String toReturn = null;
		boolean found = false;
		for (Iterator iterator = configurations.iterator(); iterator.hasNext() && !found;) {
			Configuration conf = (Configuration) iterator.next();
			if(conf.getName().equalsIgnoreCase(name)){
				found = true;
				toReturn = conf.getValue();
			}
		}
		return toReturn;
	}
	
	
	

	public class Filter {
		
		public static final String PRESENTATION_TYPE_MANUAL = "MANUAL";
		
		public static final String PRESENTATION_TYPE_COMBO = "COMBO";

		public static final String PRESENTATION_TYPE_DRIVER = "DRIVER";

		
		private String title = null;

		private String presentationType = PRESENTATION_TYPE_MANUAL;
		
		private String field = null;
		
		private String driverName = null;
		
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPresentationType() {
			return presentationType;
		}

		public void setPresentationType(String presentationType) {
			this.presentationType = presentationType;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		
	}
	
	public class Configuration {
		
		public static final String  ENABLE_BUTTONs= "enableButtons";
		
		public static final String IS_PK_AUTO_LOAD = "isPkAutoLoad";

		
		private String name = null;

		private String value = null;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	
	
	public class Column {
		
		public static final String EDITOR_TYPE_TEXT = "TEXT";
		
		public static final String EDITOR_TYPE_COMBO = "COMBO";
		
		private String field = null;
		
		private String subEntity = null;
		
		private String foreignKey = null;

		private String editorType = EDITOR_TYPE_TEXT;

		private boolean isEditable = true;
		
		private boolean isVisible = true;
		//mandatory depending on another column value
		private String mandatoryColumn = null;
		//sets the column width
		private Integer size = null;
		//sets if the result set is ordered by this column and can assume values "asc" or "desc"
		private String sorter = null;
		//sets if the column of type number must be signed or unsigned (only positive numbers) by false or true values
		private boolean unsigned = false;
		

		public boolean isUnsigned() {
			return unsigned;
		}

		public void setUnsigned(boolean unsigned) {
			this.unsigned = unsigned;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getSorter() {
			return sorter;
		}

		public void setSorter(String sorter) {
			this.sorter = sorter;
		}

		
		public String getMandatoryColumn() {
			return mandatoryColumn;
		}

		public void setMandatoryColumn(String mandatoryColumn) {
			this.mandatoryColumn = mandatoryColumn;
		}

		public String getMandatoryValue() {
			return mandatoryValue;
		}

		public void setMandatoryValue(String mandatoryValue) {
			this.mandatoryValue = mandatoryValue;
		}

		private String mandatoryValue = null;
		
		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getEditorType() {
			return editorType;
		}

		public void setEditorType(String editorType) {
			this.editorType = editorType;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public void setEditable(boolean isEditable) {
			this.isEditable = isEditable;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}
		
		public String getSubEntity() {
			return subEntity;
		}

		public void setSubEntity(String subEntity) {
			this.subEntity = subEntity;
		}
		
		public String getForeignKey() {
			return foreignKey;
		}

		public void setForeignKey(String foreignKey) {
			this.foreignKey = foreignKey;
		}
		
	}
	
	
	
	
	
	
	
}
