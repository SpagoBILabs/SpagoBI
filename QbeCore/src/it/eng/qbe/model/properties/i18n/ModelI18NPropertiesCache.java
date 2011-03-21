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
package it.eng.qbe.model.properties.i18n;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.properties.SimpleModelProperties;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ModelI18NPropertiesCache {
	
	private Map cache;
	
	public static ModelI18NPropertiesCache instance;
	
	public static ModelI18NPropertiesCache getInstance() {
		if( instance == null ) {
			instance = new ModelI18NPropertiesCache();
		}
		return instance;
	}
	
	private ModelI18NPropertiesCache() {
		cache = new HashMap();
	}
	
	public Object getResource(String resourceName) {
		return cache.get(resourceName);
	}
	
	public void putResource(String resourceName, Object resource) {
		cache.put(resourceName, resource);
	}
	
	public void putProperties(IDataSource dataSource, SimpleModelProperties labels, Locale locale) {
		String resourceName = dataSource.getName() + ":" + "labels";
		if(locale != null) {
			resourceName += "_" + locale.getLanguage();
		}
		putResource(resourceName, labels);
	}
	
	public SimpleModelProperties getProperties(IDataSource dataSource, Locale locale) {
		String resourceName = dataSource.getName() + ":" + "labels";
		if(locale != null) {
			resourceName += "_" + locale.getLanguage();
		}
		return (SimpleModelProperties)getResource(resourceName);
	}
}
