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
package it.eng.qbe.datasource;

import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.transaction.ITransaction;
import it.eng.qbe.model.accessmodality.IModelAccessModality;
import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;

import java.util.Locale;

import org.json.JSONObject;

/**
 * @author Andrea Gioia
 */
public interface IDataSource {
	
	String getName();
	IDataSourceConfiguration getConfiguration();
	
	IModelStructure getModelStructure();
	IModelAccessModality getModelAccessModality();
	void setDataMartModelAccessModality(IModelAccessModality modelAccessModality) ;
	IModelProperties getModelI18NProperties(Locale locale);
	
	void open();
	boolean isOpen();
	void close();
	
	IStatement createStatement(Query query);
	ITransaction getTransaction();
	IPersistenceManager getPersistenceManager();
}
