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

import java.io.Serializable;
import java.util.Map;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;


// TODO: Auto-generated Javadoc
/**
 * The Interface IDataMartModel.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IDataMartModel extends Serializable {
	
	String getName();
	

	public IStatement createStatement(Query query);
	

	public DataMartModelStructure getDataMartModelStructure();
	
	public IDataSource getDataSource();
	

	public DataMartModelAccessModality getDataMartModelAccessModality();
	

	public void setDataMartModelAccessModality(DataMartModelAccessModality dataMartModelAccessModality);	
	

	public Map getDataMartProperties();
	
	
	public void setDataMartProperties(Map dataMartProperties);
}
