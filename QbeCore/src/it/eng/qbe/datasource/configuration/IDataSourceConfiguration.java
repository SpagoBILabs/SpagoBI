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
package it.eng.qbe.datasource.configuration;

import it.eng.qbe.model.i18n.ModelI18NProperties;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartModelStructure;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Classes that implement this interface act as proxy toward all the resources needed in order to create
 * a new IDataSource. Actual needed resources depend on the type of IDataSource.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IDataSourceConfiguration {
	/**
	 * 
	 * @return the name of the model. Must be unique. It is used as model identifier 
	 * (ex. while caching model)
	 */
	String getModelName();
	
	/**
	 * Load the properties associated to the model. These properties are usually
	 * injected into the model by a proper implementation of interface IModelPropertiesInitializer.
	 * 
	 * NOTE: model properties can be only read. It is not possible to change them and persist the
	 * modification
	 * 
	 * @return the properties associated to the model. 
	 */
	Properties getModelProperties();
	
	
	Map<String, List<DataMartCalculatedField>> getCalculatedFields();
	void setCalculatedFields(Map<String, List<DataMartCalculatedField>> calculatedFields);
	
	ModelI18NProperties getModelI18NProperties();
	ModelI18NProperties getModelI18NProperties(Locale locale);
	
	/**
	 * 
	 * @return the properties associated to the specific data source type. The numeber and type of
	 * these properties depend on the DataSource implementation
	 */
	Map<String,Object> getDataSourceProperties();
}
