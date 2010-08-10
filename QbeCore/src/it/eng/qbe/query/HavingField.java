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
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class HavingField {
	
	private String name;
	private String description;
	
	private boolean promptable;
	
	private Operand leftOperand;
	private String operator;
	private Operand rightOperand;
	
	private String booleanConnector;

	
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
	
	
	
	public HavingField(String name, String description, boolean promptable,
			Operand leftOperand, String operator, Operand rightOperand,
			String booleanConnector) {
		
		setName(name);
		setDescription(description);
		setPromptable(promptable);
		setLeftOperand(leftOperand);
		setOperator(operator);
		setRightOperand(rightOperand);
		setBooleanConnector(booleanConnector);
	}
	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public boolean isPromptable() {
		return promptable;
	}



	public void setPromptable(boolean promptable) {
		this.promptable = promptable;
	}



	public Operand getLeftOperand() {
		return leftOperand;
	}



	public void setLeftOperand(Operand leftOperand) {
		this.leftOperand = leftOperand;
	}



	public String getOperator() {
		return operator;
	}



	public void setOperator(String operator) {
		this.operator = operator;
	}



	public Operand getRightOperand() {
		return rightOperand;
	}



	public void setRightOperand(Operand rightOperand) {
		this.rightOperand = rightOperand;
	}



	public String getBooleanConnector() {
		return booleanConnector;
	}



	public void setBooleanConnector(String booleanConnector) {
		this.booleanConnector = booleanConnector;
	}



	public static class Operand extends it.eng.qbe.query.Operand {
		
		public IAggregationFunction function;
		
		public Operand(String[] values, String description, String type,
				String[] defaulttValues, String[] lastValues, IAggregationFunction function) {
			super(values, description, type, defaulttValues, lastValues);
			this.function = function;
		}
	}
	
}
