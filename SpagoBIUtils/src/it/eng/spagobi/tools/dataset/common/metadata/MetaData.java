/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class MetaData implements IMetaData {
	
	int idFieldIndex;
	List fieldsMeta;
	Map name2IndexMap;
	Map properties;

	public MetaData() {
		idFieldIndex = -1;
		name2IndexMap = new HashMap();
		fieldsMeta = new ArrayList();
		properties = new HashMap();
	}
	
	public int getIdFieldIndex() {
		return idFieldIndex;
	}
	
	public void setIdField(int fieldIndex) {
		this.idFieldIndex = fieldIndex;
	}

	public int getFieldCount() {
		return fieldsMeta.size();
	}
	

	public int getFieldIndex(String fieldName) {
		Integer columnIndex = null;
		
		columnIndex = (Integer)name2IndexMap.get(fieldName.toUpperCase());
		
		return columnIndex == null? -1: columnIndex.intValue();
	}

	public IFieldMetaData getFieldMeta(int fieldIndex) {
		IFieldMetaData fieldMeta = null;

		fieldMeta = (IFieldMetaData)fieldsMeta.get( fieldIndex );
		
		return fieldMeta;
	}
	
	public List findFieldMeta(String propertyName, Object propertyValue) {
		List results;
		Iterator it;
		
		results = new ArrayList();
		it = fieldsMeta.iterator();
		while(it.hasNext()) {
			IFieldMetaData fieldMeta = (IFieldMetaData)it.next();
			if(fieldMeta.getProperty(propertyName) != null 
					&& fieldMeta.getProperty(propertyName).equals(propertyValue)) {
				results.add(fieldMeta);
			}
		}
		
		return results;
	}
	
	public String getFieldName(int fieldIndex) {
		String fieldName = null;
		IFieldMetaData fieldMeta;
		
		fieldMeta = getFieldMeta(fieldIndex);		
		if(fieldMeta != null) {
			String alias = fieldMeta.getAlias();
			if(alias!=null && !alias.equals("")){
				fieldName = alias;
			}else{
				fieldName = fieldMeta.getName();
			}
		}
		
		return fieldName;
	}

	public Class getFieldType(int fieldIndex) {
		Class fieldType = null;
		IFieldMetaData fieldMeta;
		
		fieldMeta = getFieldMeta(fieldIndex);		
		if(fieldMeta != null) {
			fieldType = fieldMeta.getType();
		}
		
		return fieldType;
	}

	
	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void setProperty(String propertyName, Object proprtyValue) {
		properties.put(propertyName, proprtyValue);
		
	}
	
	public void addFiedMeta(IFieldMetaData fieldMetaData) {
		Integer fieldIndex = new Integer(fieldsMeta.size());
		fieldsMeta.add(fieldMetaData);
		String fieldKey = fieldMetaData.getAlias() != null ? fieldMetaData.getAlias() : fieldMetaData.getName();
		name2IndexMap.put(fieldKey.toUpperCase(), fieldIndex);
	}

	public String toString() {
		return fieldsMeta.toString();
	}

	public void deleteFieldMetaDataAt(int pivotFieldIndex) {
		name2IndexMap.remove( getFieldMeta(pivotFieldIndex) );
		fieldsMeta.remove( pivotFieldIndex );	
	}

	public Map getProperties() {
		return properties;
	}
	
	public List getFieldsMeta() {
		return fieldsMeta;
	}

	public void changeFieldAlias(int fieldIndex, String newAlias) {
		IFieldMetaData m = this.getFieldMeta(fieldIndex);
		String previousAlias = m.getAlias() != null ? m.getAlias() :  m.getName();
		m.setAlias(newAlias);
		name2IndexMap.remove(previousAlias);
		name2IndexMap.put(newAlias.toUpperCase(), fieldIndex);
	}

}
