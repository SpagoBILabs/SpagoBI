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
package it.eng.qbe.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.qbe.bo.DatamartProperties;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.StatementFactory;


public class DataMartModel implements IDataMartModel {
	

	private String name;	
	private String label;	
	private String description;	
	private IDataSource dataSource; 
	//private DataMartModelAccessModality dataMartModelAccessModality;
	private Map dataMartProperties;
	
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(DataMartModel.class);
	

	public DataMartModel(IDataSource dataSource){
		this.dataSource = dataSource;
		this.name = getDataSource().getDatamartName();
		this.description = getDataSource().getDatamartName();
		this.label = getDataSource().getDatamartName();
		
		//this.dataMartModelAccessModality = new DataMartModelAccessModality();
		dataSource.setDataMartModelAccessModality(new DataMartModelAccessModality());
		this.dataMartProperties = new HashMap();		
	}
	

	public DatamartProperties getProperties() {
		return  dataSource.getProperties();
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////
	/// Access metohds
	/////////////////////////////////////////////////////////////////////////
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}		
		
	public DataMartModelStructure getDataMartModelStructure() {
		return getDataSource().getDataMartModelStructure();
	}

	public IDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public IStatement createStatement(Query query) {
		return StatementFactory.createStatement(getDataSource(), query);
	}

	public DataMartModelAccessModality getDataMartModelAccessModality() {
		//return this.dataMartModelAccessModality;
		return getDataSource().getDataMartModelAccessModality();
	}

	public void setDataMartModelAccessModality(
			DataMartModelAccessModality dataMartModelAccessModality) {
		//this.dataMartModelAccessModality = dataMartModelAccessModality;
		getDataSource().setDataMartModelAccessModality(dataMartModelAccessModality);
	}

	public Map getDataMartProperties() {
		return dataMartProperties;
	}
	
	public void setDataMartProperties(Map dataMartProperties) {
		this.dataMartProperties = dataMartProperties;
	}
	
	
}
