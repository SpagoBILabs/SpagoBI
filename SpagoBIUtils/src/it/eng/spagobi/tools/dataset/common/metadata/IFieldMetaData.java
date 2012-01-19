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
package it.eng.spagobi.tools.dataset.common.metadata;

import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *         Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public interface IFieldMetaData {

	public static final String DECIMALPRECISION = "decimalPrecision";
	public static final String ORDERTYPE = "oprdertype";
	
	public static final String PROPERTY_ATTRIBUTE_PRESENTATION = "attributePresentation";
	public static final String PROPERTY_ATTRIBUTE_PRESENTATION_CODE = "code";
	public static final String PROPERTY_ATTRIBUTE_PRESENTATION_DESCRIPTION = "description";
	public static final String PROPERTY_ATTRIBUTE_PRESENTATION_CODE_AND_DESCRIPTION = "both";
	
	public enum FieldType {ATTRIBUTE, MEASURE}

	String getName();
	String getAlias();
	Class getType();
	FieldType getFieldType();
	Object getProperty(String propertyName);

	void setName(String name);
	void setAlias(String alias);
	void setType(Class type);
	void setProperty(String propertyName, Object propertyValue);
	void setFieldType(FieldType fieldType);
	Map getProperties();

}
