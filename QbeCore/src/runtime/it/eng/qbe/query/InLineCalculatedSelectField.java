/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
 */
public class InLineCalculatedSelectField extends AbstractSelectField {
	
	private String expression;
	private String type;
//	private Object initialValue;
	private boolean groupByField;
	private IAggregationFunction function;
	private String orderType;
//	private int resetType;
//	private int incrementType;
	
	public InLineCalculatedSelectField(String alias, String expression, String type, boolean included, boolean visible, boolean groupByField, String orderType, String function ) {
		super(alias, ISelectField.CALCULATED_FIELD, included, visible);
		this.expression = expression;
		this.type = type;
		this.groupByField = groupByField;
		setOrderType(orderType);
		setFunction( AggregationFunctions.get(function) );
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public ISelectField copy() {
		return null;
	}
	
	public boolean isInLineCalculatedField() {
		return true;
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
	
	public IAggregationFunction getFunction() {
		return function;
	}

	public void setFunction(IAggregationFunction function) {
		this.function = function;
	}
	
}
