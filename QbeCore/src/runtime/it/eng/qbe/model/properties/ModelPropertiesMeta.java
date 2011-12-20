/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
public class ModelPropertiesMeta {
	
	public final static ModelPropertyMeta[] globalProperties = new ModelPropertyMeta[]{
		new ModelPropertyMeta("recursiveFiltering", false, true, "enabled")
	};
	
	public final static ModelPropertyMeta[] entityProperties = new ModelPropertyMeta[]{
		new ModelPropertyMeta("visible", false, true, "true"),
		new ModelPropertyMeta("type", false, true, "dimension"),
		new ModelPropertyMeta("position", false, true, "" + Integer.MAX_VALUE)
	};
	
	public final static ModelPropertyMeta[] fieldProperties = new ModelPropertyMeta[]{
		new ModelPropertyMeta("visible", false, true, "true"),
		new ModelPropertyMeta("type", false, true, "attribute"),
		new ModelPropertyMeta("position", false, true, "" + Integer.MAX_VALUE),
		new ModelPropertyMeta("format", false, true, null)
	};
	
	static Map<String, ModelPropertyMeta> globalPropertiesMap;
	static Map<String, ModelPropertyMeta> entityPropertiesMap;
	static Map<String, ModelPropertyMeta> fieldPropertiesMap;
	
	static {
		globalPropertiesMap = new HashMap<String, ModelPropertyMeta>();
		entityPropertiesMap = new HashMap<String, ModelPropertyMeta>();
		fieldPropertiesMap = new HashMap<String, ModelPropertyMeta>();
		
		for(int i = 0; i < globalProperties.length; i++) globalPropertiesMap.put(globalProperties[i].getName(), globalProperties[i]);
		for(int i = 0; i < entityProperties.length; i++) entityPropertiesMap.put(entityProperties[i].getName(), entityProperties[i]);
		for(int i = 0; i < fieldProperties.length; i++) fieldPropertiesMap.put(fieldProperties[i].getName(), fieldProperties[i]);
	}
	
	public ModelPropertyMeta getGlobalProperty(String name) {return globalPropertiesMap.get(name);}
	public ModelPropertyMeta getEntityProperties(String name) {return entityPropertiesMap.get(name);}
	public ModelPropertyMeta getFieldProperties(String name) {return fieldPropertiesMap.get(name);}
}
