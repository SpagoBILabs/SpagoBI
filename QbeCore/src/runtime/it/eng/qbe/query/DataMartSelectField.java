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
package it.eng.qbe.query;

import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataMartSelectField extends AbstractSelectField {
	
	private String uniqueName;
	private IAggregationFunction function;
	private boolean groupByField;
	private String orderType;
	private String pattern;
	


	public DataMartSelectField(String uniqueName, String function, String alias, boolean include, boolean visible,
		boolean groupByField, String orderType, String pattern ) {
		
		super(alias, ISelectField.DATAMART_FIELD, include, visible);
				
		setUniqueName(uniqueName);
		setFunction( AggregationFunctions.get(function) );		
		setGroupByField(groupByField);
		setOrderType(orderType);
		setPattern(pattern);
	}
	
	public DataMartSelectField(DataMartSelectField field) {
			
			this(field.getUniqueName(), 
				field.getFunction().getName(), 
				field.getAlias(), 
				field.isIncluded(), 
				field.isVisible(),
				field.isGroupByField(), 
				field.getOrderType(), 
				field.getPattern());					
	}


	
	public IAggregationFunction getFunction() {
		return function;
	}

	public void setFunction(IAggregationFunction function) {
		this.function = function;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public boolean isGroupByField() {
		return groupByField;
	}


	public void setGroupByField(boolean groupByField) {
		this.groupByField = groupByField;
	}

	public boolean isOrderByField() {
		return "ASC".equalsIgnoreCase( getOrderType() )
			|| "DESC".equalsIgnoreCase( getOrderType() );
	}

	public boolean isAscendingOrder() {
		return "ASC".equalsIgnoreCase( getOrderType() );
	}
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public ISelectField copy() {
		return new DataMartSelectField( this );
	}
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
