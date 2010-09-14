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

import java.util.List;

import it.eng.qbe.bo.DatamartProperties;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.builder.DataMartStructureBuilderFactory;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.StatementFactory;

/**
 * @author Andrea Gioia
 */
public abstract class AbstractDataSource implements IDataSource {
	
	private String name;
	// if it is a single jar qbe
	protected String datamartName = null;
	// if it is a multi jar qbe
	protected List datamartNames = null;
	private int type;
	private DatamartProperties properties = null;	
	private DataMartModelAccessModality dataMartModelAccessModality;
	private DataMartModelStructure dataMartModelStructure = null;
    
	public DataMartModelStructure getDataMartModelStructure() {
		IDataMartStructureBuilder builder;
		if(dataMartModelStructure == null) {			
			builder = DataMartStructureBuilderFactory.getDataMartStructureBuilder(this);
			dataMartModelStructure = builder.build();
		}
		
		return dataMartModelStructure;
	}
	
	public IStatement createStatement(Query query) {
		return StatementFactory.createStatement(this, query);
	}
	
	public DataMartModelAccessModality getDataMartModelAccessModality() {
		return dataMartModelAccessModality;
	}

	public void setDataMartModelAccessModality(
			DataMartModelAccessModality dataMartModelAccessModality) {
		this.dataMartModelAccessModality = dataMartModelAccessModality;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getDatamartName()
	 */
	public String getDatamartName() {
		return datamartName;
	}

	/**
	 * Sets the datamart name.
	 * 
	 * @param datamartName the new datamart name
	 */
	public void setDatamartName(String datamartName) {
		this.datamartName = datamartName;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getDatamartNames()
	 */
	public List getDatamartNames() {
		return datamartNames;
	}


	/**
	 * Sets the datamart names.
	 * 
	 * @param datamartNames the new datamart names
	 */
	public void setDatamartNames(List datamartNames) {
		this.datamartNames = datamartNames;
	}
	
	protected void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public DatamartProperties getProperties() {
		return properties;
	}
	
	public void setProperties(DatamartProperties properties) {
		this.properties = properties;
	}
	
}
