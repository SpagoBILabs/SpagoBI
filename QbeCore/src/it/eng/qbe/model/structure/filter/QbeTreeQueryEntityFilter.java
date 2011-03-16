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
package it.eng.qbe.model.structure.filter;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;
import it.eng.qbe.model.structure.filter.IQbeTreeEntityFilter;
import it.eng.qbe.model.structure.filter.QbeTreeWhiteListEntityFilter;

import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class QbeTreeOrderEntityFilter.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QbeTreeQueryEntityFilter extends QbeTreeWhiteListEntityFilter {

	private Query query;
	
	/**
	 * Instantiates a new qbe tree order entity filter.
	 */
	public QbeTreeQueryEntityFilter() {
		super();
	}
	
	public QbeTreeQueryEntityFilter(IQbeTreeEntityFilter parentFilter, Query query) {
		super(parentFilter, null);
		this.setQuery( query );
	}
	
	public List filter(IDataSource dataSource, List entities) {
		List list = null;
		
		IStatement statement = dataSource.createStatement(getQuery());
		Set whiteList = statement.getSelectedEntities();
		setWhiteList(whiteList);
		list = super.filter(dataSource, entities);
		return list;
	}
	
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	
	
}
