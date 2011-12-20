/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.qbe.datasource.configuration.dao;

import it.eng.qbe.model.properties.SimpleModelProperties;

import java.util.Locale;

/**
 * The Interface IModelI18NPropertiesDAO.
 * 
 * @author Andrea Gioia
 */
public interface IModelI18NPropertiesDAO {
	
	/**
	 * Load i18n properties for the default locale. Equals to loadProperties(null)
	 * 
	 * @param locale the target locale
	 * 
	 * @return the loaded i18n properties
	 */
	SimpleModelProperties loadProperties();
	
	/**
	 * Load i18n properties for the given locale.
	 *
	 * @param locale the target locale
	 * 
	 * @return the loaded i18n properties
	 */
	SimpleModelProperties loadProperties(Locale locale);
}
