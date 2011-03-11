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
package it.eng.qbe.cache;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.i18n.ModelI18NProperties;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeCacheManager {
	QbeCache cache;
	
	public static QbeCacheManager instance;
	
	public static QbeCacheManager getInstance() {
		if( instance == null ) {
			instance = new QbeCacheManager();
		}
		return instance;
	}
	
	private QbeCacheManager( ) {
		cache = QbeCache.getInstance();
	}
	
	
	public void putLabels(IDataSource dataSource, ModelI18NProperties labels, Locale locale) {
		cache.putLabels(dataSource, labels, locale);
	}
	
	public ModelI18NProperties getLabels(IDataSource dataSource, Locale locale) {
		ModelI18NProperties labels;
		
		labels = cache.getLabels(dataSource, locale);
		if(labels == null) {
			
			labels = dataSource.getConfiguration().loadModelI18NProperties(locale);
			
			cache.putLabels(dataSource, labels, locale);
		}
		
		return labels;
	}

}
