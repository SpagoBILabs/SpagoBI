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

import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.model.structure.builder.DataMartStructureBuilderFactory;
import it.eng.qbe.model.structure.builder.IDataMartStructureBuilder;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.StatementFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Gioia
 */
public abstract class AbstractDataSource implements IDataSource {
	
	protected String name;
	protected List<IDataSourceConfiguration> configurations;
	
	
	protected DataMartModelAccessModality dataMartModelAccessModality;
	protected DataMartModelStructure dataMartModelStructure;
	protected DBConnection connection;
	protected Map dblinkMap;

		
	
	public List<IDataSourceConfiguration> getConfigurations() {
		return configurations;
	}

	public DataMartModelStructure getDataMartModelStructure() {
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
	 * @see it.eng.qbe.datasource.IJPADataSource#getConnection()
	 */
	public DBConnection getConnection() {
		return connection;
	}


	/**
	 * Sets the connection.
	 * 
	 * @param connection the new connection
	 */
	public void setConnection(DBConnection connection) {
		this.connection = connection;
	}
	

	/**
	 * Gets the dblink map.
	 * 
	 * @return the dblink map
	 */
	public Map getDblinkMap() {
		return dblinkMap;
	}


	/**
	 * Sets the dblink map.
	 * 
	 * @param dblinkMap the new dblink map
	 */
	public void setDblinkMap(Map dblinkMap) {
		this.dblinkMap = dblinkMap;
	}
	
}
