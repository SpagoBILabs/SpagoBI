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
package it.eng.qbe.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.model.IDataMartModel;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.spagobi.commons.utilities.StringUtilities;

// TODO: Auto-generated Javadoc
/**
 * The Class DatamartProperties.
 * 
 * @author Andrea Gioia
 */
public class DatamartProperties {

	/** The qbe properties. */
	private Map qbeProperties = null;
	
	
	/** The Constant CLASS_TYPE_TABLE. */
	public static final int CLASS_TYPE_CUBE = 1;
	
	/** The Constant CLASS_TYPE_RELATION. */
	public static final int CLASS_TYPE_DIMENSION = 2;
	
	/** The Constant CLASS_TYPE_VIEW. */
	public static final int CLASS_TYPE_VIEW = 3;
	
	
	
	/** The Constant FIELD_TYPE_MEASURE. */
	public static final int FIELD_TYPE_MEASURE = 1;
	
	/** The Constant FIELD_TYPE_DIMENSION. */
	public static final int FIELD_TYPE_ATTRIBUTE = 2;
	
	/** The Constant FIELD_TYPE_GEOREF. */
	public static final int FIELD_TYPE_GEOREF = 3;
	
	
	
	public DatamartProperties() {
		qbeProperties = new Properties();
	}
	
	public DatamartProperties(Properties properties) {
		qbeProperties = properties;
	}

	public DatamartProperties(IDataMartModel dm) {
		qbeProperties = dm.getDataMartProperties();
	}
	
	public void addDatamartProperties(DatamartProperties datamartProperties) {
		if (datamartProperties != null && datamartProperties.qbeProperties != null && !datamartProperties.qbeProperties.isEmpty()) {
			qbeProperties.putAll(datamartProperties.qbeProperties);
		}
	}
	
	private String getEntityUniqueNameInFile( DataMartEntity entity ) {
		return entity.getUniqueName().replaceAll(":", "/");
	}
	
	private String getFieldUniqueNameInFile( DataMartField field ) {
		return field.getUniqueName().replaceAll(":", "/");
	}
	
	private String getPropertyUniqueNameInFile(DataMartEntity entity, String propertyName) {
		return getEntityUniqueNameInFile( entity ) + "." + propertyName.trim();
	}
	
	private String getPropertyUniqueNameInFile(DataMartField field, String propertyName) {
		return getFieldUniqueNameInFile( field ) + "." + propertyName.trim();
	}
	
	public Object getProperty(String prpertyName) {
		return qbeProperties.get( prpertyName );
	}
	
	public String getProperty(Object datamartItem, String propertyName) {
		String propertyValue;
		String propertyUniqueNameInFile;
		
		if(qbeProperties == null) {
			return null;
		}
		
		if( datamartItem instanceof DataMartEntity ) {
			propertyUniqueNameInFile = getPropertyUniqueNameInFile( (DataMartEntity)datamartItem, propertyName );
		} else if( datamartItem instanceof DataMartField ) {
			propertyUniqueNameInFile = getPropertyUniqueNameInFile( (DataMartField)datamartItem, propertyName );
		} else {
			// fail fast
			throw new IllegalArgumentException("[datamartItem] is an instance of class " + datamartItem.getClass().getName() + ".[datamartItem] can be only an instance of class DataMartEntity or of class DataMartField");
		}
		propertyValue = (String)qbeProperties.get( propertyUniqueNameInFile );		
		return StringUtilities.isNull( propertyValue )? null: propertyValue.trim();
	}
	
	private boolean getPropertyAsBoolean(Object datamartItem, String propertyName, boolean defaultValue) {
		String propertyValue;		
		
		propertyValue = getProperty(datamartItem, propertyName);	
		if( "TRUE".equalsIgnoreCase( propertyValue ) ) {
			return true;
		} else if( "FALSE".equalsIgnoreCase( propertyValue ) ) {
			return false;
		} 		
		
		return defaultValue;
	}
		
	
	/**
	 * Checks if is table visible.
	 * 
	 * @param className the class name
	 * 
	 * @return true, if is table visible
	 */
	public boolean isEntityVisible( DataMartEntity entity ) {
		return getPropertyAsBoolean(entity, "visible", true);
	}
	
	/**
	 * Checks if is field visible.
	 * 
	 * @param fieldName the field name
	 * 
	 * @return true, if is field visible
	 */
	public boolean isFieldVisible( DataMartField field ) {
		return getPropertyAsBoolean(field, "visible", true);
	}
	
	
	private int getEntityType(DataMartEntity entity, int defaultType) {		
		String type;
		
		type = getProperty(entity, "type");		
		if( "CUBE".equalsIgnoreCase( type ) ) {
			return CLASS_TYPE_CUBE;
		} else if( "DIMENSION".equalsIgnoreCase( type ) ) { 
			return CLASS_TYPE_DIMENSION;
		} else if( "VIEW".equalsIgnoreCase( type ) ) { 
			return CLASS_TYPE_VIEW;
		}
		
		return defaultType;
	}
	
	public int getEntityType(DataMartEntity entity) {		
		return getEntityType(entity, CLASS_TYPE_DIMENSION);
	}
	
	public String getEntityIconClass( DataMartEntity entity ) {
		String iconCls;
		int entityType;
		
		entityType = getEntityType( entity );
		if(entityType == DatamartProperties.CLASS_TYPE_CUBE) {
			iconCls = "cube";
		} else if(entityType == DatamartProperties.CLASS_TYPE_DIMENSION) {
			iconCls = "dimension";
		} else if(entityType == DatamartProperties.CLASS_TYPE_VIEW) {
			iconCls = "view";
		} else {
			// fail fast
			throw new RuntimeException("Internal errror. Function getEntityType return value [" + entityType + "] is not valid.");
		}
				
		return iconCls;
	}
	
	/**
	 * Gets the field type.
	 * 
	 * @param className the class name
	 * 
	 * @return the field type
	 */
	public int getFieldType(DataMartField filed, int defaultType) {
		String type;
		
		type = getProperty(filed, "type");		
		if( "ATTRIBUTE".equalsIgnoreCase( type ) ) {
			return FIELD_TYPE_ATTRIBUTE;
		} else if( "MEASURE".equalsIgnoreCase( type ) ) { 
			return FIELD_TYPE_MEASURE;
		} else if( "GEOREF".equalsIgnoreCase( type ) ) { 
			return FIELD_TYPE_GEOREF;
		}
		
		return defaultType;
	}
	
	public int getFieldType(DataMartField filed) {
		return getFieldType(filed, FIELD_TYPE_ATTRIBUTE);
	}
	
	public String getFieldIconClass( DataMartField field ) {
		String iconCls;
		int fieldType;
		
		fieldType = getFieldType( field );
		if(fieldType == DatamartProperties.FIELD_TYPE_ATTRIBUTE) {
			iconCls = "attribute";
		} else if(fieldType == DatamartProperties.FIELD_TYPE_MEASURE) {
			iconCls = "measure";
		} else if(fieldType == DatamartProperties.FIELD_TYPE_GEOREF) {
			iconCls = "georef";
		} else {
			// fail fast
			throw new RuntimeException("Internal errror. Function getFieldType return value [" + fieldType + "] is not valid.");
		}
		
		return iconCls;
	}
	
	/**
	 * Returns the format to be applied to the input field.
	 * If there is no format for the input field, it retrieves the format defined for the relevant field in the root entity
	 * @param field The datamart field
	 * @param datamartModel The datamart model: it is required in order to retrieve the input field's root entity
	 * @return the format to be applied to the input field
	 */
	public String getFormat(DataMartField field, DataMartModel datamartModel) {
		String toReturn = null;
		
		// try first with entity unique name
		toReturn = getProperty(field, "format");
		if (toReturn == null) {
			// then try with relevant field on first level entity 
			DataMartField rootField = null;
			DataMartEntity rootEntity = datamartModel.getDataMartModelStructure().getRootEntity(field.getParent());
			List fields = rootEntity.getAllFields();
			Iterator it = fields.iterator();
			while (it.hasNext()) {
				DataMartField aField = (DataMartField) it.next();
				if (aField.getName().equals(field.getName())) {
					rootField = aField;
					break;
				}
			}
			toReturn = getProperty(rootField, "format");
		}
		
		return toReturn;
	}
}
