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


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class WhereFieldBkp {
	private String fname;
	private String fdesc;
	private String uniqueName;
	private String operator;
	private Object operand;
	private String operandType;
	private String operandDesc;
	private String boperator;
	private boolean isFree;
	private String defaultValue;
	private String lastValue;
	
	public static final String EQUALS_TO = "EQUALS TO";
	public static final String NOT_EQUALS_TO = "NOT EQUALS TO";
	public static final String GREATER_THAN = "GREATER THAN";
	public static final String EQUALS_OR_GREATER_THAN = "EQUALS OR GREATER THAN";
	public static final String LESS_THAN = "LESS THAN";
	public static final String EQUALS_OR_LESS_THAN = "EQUALS OR LESS THAN";
	public static final String STARTS_WITH = "STARTS WITH";
	public static final String NOT_STARTS_WITH = "NOT STARTS WITH";
	public static final String ENDS_WITH = "ENDS WITH";	
	public static final String NOT_ENDS_WITH = "NOT ENDS WITH";	
	public static final String NOT_NULL = "NOT NULL";	
	public static final String IS_NULL = "IS NULL";	
	public static final String CONTAINS = "CONTAINS";	
	public static final String NOT_CONTAINS = "NOT CONTAINS";	
	public static final String BETWEEN = "BETWEEN";	
	public static final String NOT_BETWEEN = "NOT BETWEEN";	
	public static final String IN = "IN";	
	public static final String NOT_IN = "NOT IN";	
	
	
	
	public WhereFieldBkp(String fname, String fdesc, 
			String uniqueName, String operator, Object operand, String type, String desc, String boperator, boolean isFree, String defaultValue, String lastValue) {
		
		setUniqueName(uniqueName);
		setOperator( operator );
		setOperand( operand );
		setOperandType(type);
		setOperandDesc(desc);
		setFname(fname);
		setFdesc(fdesc);
		setBoperator(boperator);
		setIsFree(isFree);
		setDefaultValue(defaultValue);
		setLastValue(lastValue);
	}
	
	public WhereFieldBkp(String fname, String fdesc,  String uniqueName, String operator, String boperator) {
		setUniqueName(uniqueName);
		setOperator( operator );
		setOperand( null );
		setOperandType( null );
		setFname(fname);
		setFdesc(fdesc);
		setBoperator(boperator);
		setIsFree(false);
		setDefaultValue(null);
		setLastValue(null);
	}
	
	

	public Object getOperand() {
		return operand;
	}

	public void setOperand(Object operand) {
		this.operand = operand;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getOperandType() {
		return operandType;
	}

	public void setOperandType(String operandType) {
		this.operandType = operandType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperandDesc() {
		return operandDesc;
	}

	public void setOperandDesc(String operandDesc) {
		this.operandDesc = operandDesc;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFdesc() {
		return fdesc;
	}

	public void setFdesc(String fdesc) {
		this.fdesc = fdesc;
	}

	public String getBoperator() {
		return boperator;
	}

	public void setBoperator(String boperator) {
		this.boperator = boperator;
	}
	
	public boolean isFree() {
		return isFree;
	}

	public void setIsFree(boolean isFree) {
		this.isFree = isFree;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}
}
