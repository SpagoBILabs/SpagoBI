/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.qbe.datasource;

import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.ModelAccessModality;
import it.eng.qbe.model.properties.i18n.ModelI18NProperties;
import it.eng.qbe.model.properties.i18n.ModelI18NPropertiesCache;
import it.eng.qbe.model.structure.ModelStructure;
import it.eng.qbe.model.structure.builder.DataMartStructureBuilderFactory;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;
import it.eng.qbe.statement.StatementFactory;

import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Gioia
 */
public abstract class AbstractDataSource implements IDataSource {
	
	protected String name;
	protected IDataSourceConfiguration configuration;
	
	protected ModelAccessModality dataMartModelAccessModality;
	protected ModelStructure dataMartModelStructure;

		
	
	public IDataSourceConfiguration getConfiguration() {
		return configuration;
	}

	public ModelStructure getModelStructure() {
		IDataMartStructureBuilder structureBuilder;
		if(dataMartModelStructure == null) {			
			structureBuilder = DataMartStructureBuilderFactory.getDataMartStructureBuilder(this);
			dataMartModelStructure = structureBuilder.build();
		}
		
		return dataMartModelStructure;
	}
	
	public IStatement createStatement(Query query) {
		return StatementFactory.createStatement(this, query);
	}
	
	public ModelAccessModality getModelAccessModality() {
		return dataMartModelAccessModality;
	}

	public void setDataMartModelAccessModality(
			ModelAccessModality dataMartModelAccessModality) {
		this.dataMartModelAccessModality = dataMartModelAccessModality;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public ModelI18NProperties getModelI18NProperties(Locale locale) {
		ModelI18NProperties properties;
		
		properties = ModelI18NPropertiesCache.getInstance().getProperties(this, locale);
		if(properties == null) {			
			properties = getConfiguration().loadModelI18NProperties(locale);
			ModelI18NPropertiesCache.getInstance().putProperties(this, properties, locale);
		}
		return properties;
	}
	
}
