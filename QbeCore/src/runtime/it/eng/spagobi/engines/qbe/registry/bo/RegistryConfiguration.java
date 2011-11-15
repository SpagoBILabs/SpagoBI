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
	
	private List<Filter> filters = null;
	private List<Column> columns = null;

	private String entity = null;
	
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

	public class Filter {
		
		public static final String PRESENTATION_TYPE_MANUAL = "MANUAL";
		
		public static final String PRESENTATION_TYPE_COMBO = "COMBO";
		
		private String title = null;

		private String presentationType = PRESENTATION_TYPE_MANUAL;
		
		private String field = null;
		
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
