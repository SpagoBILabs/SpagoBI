/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.model.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataMartProperties {
	
	public final static DataMartProperty[] globalProperties = new DataMartProperty[]{
		new DataMartProperty("recursiveFiltering", false, true, "enabled")
	};
	
	public final static DataMartProperty[] entityProperties = new DataMartProperty[]{
		new DataMartProperty("visible", false, true, "true"),
		new DataMartProperty("type", false, true, "dimension"),
		new DataMartProperty("position", false, true, "" + Integer.MAX_VALUE)
	};
	
	public final static DataMartProperty[] fieldProperties = new DataMartProperty[]{
		new DataMartProperty("visible", false, true, "true"),
		new DataMartProperty("type", false, true, "attribute"),
		new DataMartProperty("position", false, true, "" + Integer.MAX_VALUE),
		new DataMartProperty("format", false, true, null)
	};
	
	static Map<String, DataMartProperty> globalPropertiesMap;
	static Map<String, DataMartProperty> entityPropertiesMap;
	static Map<String, DataMartProperty> fieldPropertiesMap;
	
	static {
		globalPropertiesMap = new HashMap<String, DataMartProperty>();
		entityPropertiesMap = new HashMap<String, DataMartProperty>();
		fieldPropertiesMap = new HashMap<String, DataMartProperty>();
		
		for(int i = 0; i < globalProperties.length; i++) globalPropertiesMap.put(globalProperties[i].getName(), globalProperties[i]);
		for(int i = 0; i < entityProperties.length; i++) entityPropertiesMap.put(entityProperties[i].getName(), entityProperties[i]);
		for(int i = 0; i < fieldProperties.length; i++) fieldPropertiesMap.put(fieldProperties[i].getName(), fieldProperties[i]);
	}
	
	public DataMartProperty getGlobalProperty(String name) {return globalPropertiesMap.get(name);}
	public DataMartProperty getEntityProperties(String name) {return entityPropertiesMap.get(name);}
	public DataMartProperty getFieldProperties(String name) {return fieldPropertiesMap.get(name);}
}
