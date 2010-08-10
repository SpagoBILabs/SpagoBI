/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.common.query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class FilterQueryTransformer extends AbstractQueryTransformer{
	
	List<String> selectColumnNames;
	List<String> selectColumnAliases;
	List<FilterQueryTransformer.Filter> filters;
	
	public FilterQueryTransformer() {
		this(null);
	}
	
	public FilterQueryTransformer(IQueryTransformer previousTransformer) {
		super(previousTransformer);
		selectColumnNames = new ArrayList();
		selectColumnAliases = new ArrayList();
		filters = new ArrayList();
	}
	
	public Object execTransformation(Object statement) {
	
		String transformedStatment = null;
		String alias;
		
		String subQueryAlias = "t" + System.currentTimeMillis();
    	
		transformedStatment = "SELECT ";
		for(int i = 0; i < selectColumnNames.size(); i++) {
    		transformedStatment += (i>0)? ", ": "";
    		String columnName = selectColumnNames.get(i);
    		String columnAlias = selectColumnAliases.get(i);
    		
    		columnAlias = columnAlias.trim();
			if( !(columnAlias.startsWith("'") || columnAlias.startsWith("\"")) ) {
				columnAlias = "\"" + columnAlias + "\"";
			}
    		
    		if(columnName.equalsIgnoreCase("*")) {
    			transformedStatment += "*";
    		} else {
    			transformedStatment += subQueryAlias + "." + columnName + " AS " + columnAlias;
    		}
    	}
		
		transformedStatment += " \nFROM ( " + statement + ") " + subQueryAlias;
    	transformedStatment += " \nWHERE ";
    	for(int i = 0; i < filters.size(); i++) {
    		Filter f = filters.get(i);
    		transformedStatment += (i>0)? " AND ": "";
    		transformedStatment += subQueryAlias + "." + f.leftOperand + " " + f.operator + " " + f.rightOperand;
    	}
    	
		return transformedStatment;
	}
	
	public void addFilter(String columnName, Number value) {
		filters.add( new Filter(columnName, value) );
	}
	
	public void addFilter(String columnName, String value) {
		filters.add( new Filter(columnName, value) );
	}
	
	public void addColumn(String name) {
		addColumn(name, null);
	}
	
	public void addColumn(String name, String alias) {
		selectColumnNames.add(name);
		selectColumnAliases.add( alias == null? name: alias);
	}
	
	private static class Filter {
		String leftOperand;
		String operator;
		String rightOperand;
		
		public Filter(String columnName, Number value) {
			leftOperand = columnName;
			operator = "=";
			rightOperand = value.toString();
			
		}
		
		public Filter(String columnName, String value) {
			leftOperand = columnName;
			operator = "=";
			rightOperand = "'" + value.toString() + "'";
		}
		
		
	}
	
	
}
