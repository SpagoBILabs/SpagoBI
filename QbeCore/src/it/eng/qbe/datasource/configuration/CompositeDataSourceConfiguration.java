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

import it.eng.qbe.model.properties.i18n.ModelI18NProperties;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.model.structure.ModelCalculatedField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class CompositeDataSourceConfiguration implements IDataSourceConfiguration {

	String modelName;
	Map<String,Object> dataSourceProperties;
	
	List<IDataSourceConfiguration> subConfigurations;
	
	public CompositeDataSourceConfiguration(String modelName, Map<String,Object> dataSourceProperties) {
		this.modelName = modelName;
		this.dataSourceProperties = dataSourceProperties;
		
		this.subConfigurations = new ArrayList<IDataSourceConfiguration>();
	}
	
	public CompositeDataSourceConfiguration() {
		this(null);
	}
	
	public CompositeDataSourceConfiguration(String modelName) {
		this.modelName = modelName;
		this.dataSourceProperties = new HashMap<String,Object>();
		
		this.subConfigurations = new ArrayList<IDataSourceConfiguration>();
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#getDataSourceProperties()
	 */
	public Map<String, Object> loadDataSourceProperties() {
		return dataSourceProperties;
	}
	
	public void addSubConfiguration(IDataSourceConfiguration configuration){
		subConfigurations.add(configuration);
	}
	
	public List<IDataSourceConfiguration> getSubConfigurations(){
		return subConfigurations;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#getModelProperties()
	 */
	public Properties loadModelProperties() {
		Properties properties = new Properties();
		Iterator<IDataSourceConfiguration> it = subConfigurations.iterator();
		while (it.hasNext()) {
			IDataSourceConfiguration configuration = it.next();
			Properties props = configuration.loadModelProperties();
			properties.putAll(props);
		}
		
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#getModelLabels()
	 */
	public ModelI18NProperties loadModelI18NProperties() {
		return loadModelI18NProperties(null);
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#getModelLabels(java.util.Locale)
	 */
	public ModelI18NProperties loadModelI18NProperties(Locale locale) {
		ModelI18NProperties labels = new ModelI18NProperties();
		Iterator<IDataSourceConfiguration> it = subConfigurations.iterator();
		while (it.hasNext()) {
			IDataSourceConfiguration configuration = it.next();
			ModelI18NProperties modelLabels = configuration.loadModelI18NProperties(locale);
			if(locale != null && labels == null) {
				modelLabels = configuration.loadModelI18NProperties();
			}
			labels.addDatamartLabels(modelLabels);
		}
		
		return labels;
	}
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#getCalculatedFields()
	 */
	public Map<String, List<ModelCalculatedField>> loadCalculatedFields() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.configuration.IDataSourceConfiguration#setCalculatedFields(java.util.Map)
	 */
	public void saveCalculatedFields(Map<String, List<ModelCalculatedField>> calculatedFields) {
		
		Iterator<List<ModelCalculatedField>> it = calculatedFields.values().iterator();
		if(!it.hasNext()) return; // if NO calculated fields to add return
		IModelStructure structure = it.next().get(0).getStructure();
		
		Iterator<IDataSourceConfiguration> subConfigurationIterator = subConfigurations.iterator();
		while(it.hasNext()) {
			IDataSourceConfiguration subConfiguration = subConfigurationIterator.next();
			Map<String, List<ModelCalculatedField>> datamartCalcultedField = getCalculatedFieldsForDatamart(structure, subConfiguration.getModelName());

			subConfiguration.saveCalculatedFields(datamartCalcultedField);
		}
	

	}
	
	/**
	 * The input map contains all the calculated fields defined into the entire datamart model structure. 
	 * This method returns the calculated field defined for a single datamart (used in case of composite datasource, i.e. more than 1 datamart).
	 * @param structure The datamart model structure
	 * @param calculatedFields All the calculated fields defined into the entire datamart model structure
	 * @param datamartName The datamart for which the calculated fields should be retrieved
	 * @return the calculated field defined for the specified datamart 
	 */
	private Map<String, List<ModelCalculatedField>> getCalculatedFieldsForDatamart(IModelStructure structure, String datamartName) {
		Map<String, List<ModelCalculatedField>> toReturn = new HashMap<String, List<ModelCalculatedField>>();
		Map<String, List<ModelCalculatedField>> calculatedFields = structure.getCalculatedFields();
		Set keys = calculatedFields.keySet();
		Iterator keysIt = keys.iterator();
		while (keysIt.hasNext()) {
			String entityUniqueName = (String) keysIt.next();
			IModelEntity dataMartEntity = structure.getEntity(entityUniqueName);
			IModelEntity dataMartRootEntity = dataMartEntity.getRoot();
			List rootEntities = structure.getRootEntities(datamartName);
			if (rootEntities.contains(dataMartRootEntity)) {
				toReturn.put(entityUniqueName, calculatedFields.get(entityUniqueName));
			}
		}
		return toReturn;
	}






}
