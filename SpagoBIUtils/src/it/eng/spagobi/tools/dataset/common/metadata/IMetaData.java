/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/

package it.eng.spagobi.tools.dataset.common.metadata;

import java.util.List;
import java.util.Map;

/**
 * @authors Angelo Bernabei (angelo.bernabei@eng.it)       
 *          Andrea Gioia (andrea.gioia@eng.it)
 *          Davide Zerbetto (davide.zerbetto@eng.it)
 */
public interface IMetaData {
	
	public static final String DECIMALPRECISION = "decimalPrecision";
	
	/**
	 * @return Returns the index of identfier field if any. -1 otherwaise. 
	 */
	int getIdFieldIndex();
	
	void setIdField(int fieldIndex);
	
	/**
	 * @return Returns the number of fields in this DataStore object. 
	 */
	int getFieldCount();
	
	/**
	 * Get the designated field's index. 
	 * 
	 * @param columnIndex the first column is 0, the second is 1, ... 
	 * 
	 * @return column index 
	 */
	int getFieldIndex(String fieldName);
	
	/**
	 * Get the designated column's name. 
	 * 
	 * @param columnIndex the first column is 0, the second is 1, ... 
	 * 
	 * @return column name 
	 */
	String getFieldName(int fieldIndex);
	
	/**
	 * Retrieves the designated column's Class type
	 * 
	 * @param columnIndex
	 * 
	 * @return Java class
	 */
	Class getFieldType(int fieldIndex);
	
	void addFiedMeta(IFieldMetaData fieldMetaData);
	
	IFieldMetaData getFieldMeta(int fieldIndex);
	
	List findFieldMeta(String propertyName, Object propertyValue);
	
	Object getProperty(String propertyName);

	void setProperty(String propertyName, Object propertyValue);
	
	Map<String, Object> getProperties();

	void deleteFieldMetaDataAt(int pivotFieldIndex); 
	
	void changeFieldAlias(int fieldIndex, String newAlias);

}
