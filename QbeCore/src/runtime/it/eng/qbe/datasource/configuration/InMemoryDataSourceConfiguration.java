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

import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.structure.IModelViewEntityDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

/**
 * This class allow to manage the configuration in memory. Nothing is loaded. It is the caller to injects 
 * values at runtime into the configuration. 
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class InMemoryDataSourceConfiguration extends AbstractDataSourceConfiguration {

	IModelProperties modelProperties;
	
	IModelProperties modelI18NProperties;
	Map<Locale, IModelProperties> i18nMap;
	
	Map<String, Object> dataSourceProperties;
	List<IModelViewEntityDescriptor> views;
	Map<String, List<ModelCalculatedField>> calculatedFields;
	
	public InMemoryDataSourceConfiguration(String modelName) {
		super(modelName);
	}

	public IModelProperties loadModelProperties() {
		if(modelProperties == null) modelProperties = super.loadModelProperties();
		return modelProperties;
	}

	public IModelProperties loadModelI18NProperties() {
		if(modelI18NProperties == null) modelI18NProperties = super.loadModelI18NProperties();
		return modelI18NProperties;
	}

	public IModelProperties loadModelI18NProperties(Locale locale) {
		if(i18nMap == null) {
			i18nMap = new HashMap<Locale, IModelProperties>();
		}
		
		IModelProperties p = i18nMap.get(locale);
		if(p == null) {
			i18nMap.put(locale, super.loadModelI18NProperties(locale));
		}
		return i18nMap.get(locale);
	}

	public Map<String, Object> loadDataSourceProperties() {
		if(dataSourceProperties == null) dataSourceProperties = super.loadDataSourceProperties();
		return dataSourceProperties;
	}

	public List<IModelViewEntityDescriptor> loadViews() {
		if(views == null) views = super.loadViews();
		return views;
	}

	public Map<String, List<ModelCalculatedField>> loadCalculatedFields() {
		if(calculatedFields == null) calculatedFields = super.loadCalculatedFields();
		return calculatedFields;
	}

	public void saveCalculatedFields(
			Map<String, List<ModelCalculatedField>> calculatedFields) {
		// do nothing	
		super.saveCalculatedFields(calculatedFields);
	}

}
