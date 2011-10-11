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

import it.eng.qbe.datasource.configuration.dao.ICalculatedFieldsDAO;
import it.eng.qbe.datasource.configuration.dao.IInLineFunctionsDAO;
import it.eng.qbe.datasource.configuration.dao.IModelI18NPropertiesDAO;
import it.eng.qbe.datasource.configuration.dao.IModelPropertiesDAO;
import it.eng.qbe.datasource.configuration.dao.IViewsDAO;
import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.properties.SimpleModelProperties;
import it.eng.qbe.model.structure.IModelViewEntityDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DelegatingDataSourceConfiguration extends InMemoryDataSourceConfiguration {
	
	IModelPropertiesDAO modelPropertiesDAO;
	ICalculatedFieldsDAO calculatedFieldsDAO;
	IModelI18NPropertiesDAO modelLabelsDAOFileImpl;
	IViewsDAO viewsDAO;
	IInLineFunctionsDAO functionsDAO;
	
	

	public DelegatingDataSourceConfiguration(String modelName) {
		super(modelName);
	}
	
	// overrides
	
	// datasource properties are managed in memory -> no delegation here
	// public Map<String, Object> loadDataSourceProperties() { ...
	
	public IModelProperties loadModelProperties() {
		return modelPropertiesDAO.loadModelProperties();
	}
	
	public IModelProperties loadModelI18NProperties() {
		return loadModelI18NProperties(null);
	}
	public IModelProperties loadModelI18NProperties(Locale locale) {
		SimpleModelProperties properties = modelLabelsDAOFileImpl.loadProperties(locale);
		return properties;
	}

	public Map loadCalculatedFields() {
		return calculatedFieldsDAO.loadCalculatedFields();
	}

	public void saveCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		calculatedFieldsDAO.saveCalculatedFields( calculatedFields );
	}


	public List<IModelViewEntityDescriptor> loadViews() {
		return viewsDAO.loadModelViews();
	}
	
	public List loadInLineFunctions(String dialect) {
		return functionsDAO.loadInLineFunctions(dialect);
	}
	
	// Accessor methods	

	public IModelPropertiesDAO getModelPropertiesDAO() {
		return modelPropertiesDAO;
	}

	public void setModelPropertiesDAO(IModelPropertiesDAO modelPropertiesDAO) {
		this.modelPropertiesDAO = modelPropertiesDAO;
	}

	public ICalculatedFieldsDAO getCalculatedFieldsDAO() {
		return calculatedFieldsDAO;
	}

	public void setCalculatedFieldsDAO(ICalculatedFieldsDAO calculatedFieldsDAO) {
		this.calculatedFieldsDAO = calculatedFieldsDAO;
	}

	public IModelI18NPropertiesDAO getModelLabelsDAOFileImpl() {
		return modelLabelsDAOFileImpl;
	}

	public void setModelLabelsDAOFileImpl(
			IModelI18NPropertiesDAO modelLabelsDAOFileImpl) {
		this.modelLabelsDAOFileImpl = modelLabelsDAOFileImpl;
	}

	public IViewsDAO getViewsDAO() {
		return viewsDAO;
	}

	public void setViewsDAO(IViewsDAO viewsDAO) {
		this.viewsDAO = viewsDAO;
	}
	
	public IInLineFunctionsDAO getFunctionsDAO() {
		return functionsDAO;
	}

	public void setFunctionsDAO(IInLineFunctionsDAO functionsDAO) {
		this.functionsDAO = functionsDAO;
	}
}
