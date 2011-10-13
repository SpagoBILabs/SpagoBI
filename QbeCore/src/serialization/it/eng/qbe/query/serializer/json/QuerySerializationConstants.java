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
package it.eng.qbe.query.serializer.json;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QuerySerializationConstants {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
		
	public static final String FIELDS = "fields";
	public static final String FIELD_ID = "id";
	public static final String FIELD_TYPE = "type";	
	public static final String FIELD_ENTITY = "entity";
	public static final String FIELD_NAME = "field";
	public static final String FIELD_LONG_DESCRIPTION = "longDescription";
	public static final String FIELD_ALIAS = "alias";
	public static final String FIELD_GROUP = "group";
	public static final String FIELD_ORDER = "order";
	public static final String FIELD_AGGREGATION_FUNCTION = "funct";
	public static final String FIELD_VISIBLE = "visible";
	public static final String FIELD_INCLUDE = "include";
	public static final String FIELD_INITIAL_VALUE = "initialValue";
	public static final String FIELD_EXPRESSION = "expression";
	public static final String FIELD_SLOTS = "slots";
	public static final String FIELD_NATURE = "nature";
	public static final String FIELD_NATURE_MEASURE = "measure";
	public static final String FIELD_NATURE_ATTRIBUTE = "attribute";
	public static final String FIELD_NATURE_MANDATORY_MEASURE = "mandatory_measure";
	public static final String FIELD_NATURE_SEGMENT_ATTRIBUTE = "segment_attribute";
	public static final String FIELD_NATURE_POST_LINE_CALCULATED = "postLineCalculated";
	public static final String FIELD_ICON_CLS = "iconCls";
	
	
	public static final String DISTINCT = "distinct";
	public static final String IS_NESTED_EXPRESSION = "isNestedExpression";
	
	public static final String FILTERS = "filters";
	public static final String HAVINGS = "havings";
	
	public static final String FILTER_ID = "filterId";	
	public static final String FILTER_DESCRIPTION = "filterDescripion";
	public static final String FILTER_PROMPTABLE = "promptable";	
	public static final String FILTER_LO_VALUE = "leftOperandValue";
	public static final String FILTER_LO_DESCRIPTION = "leftOperandDescription";
	public static final String FILTER_LO_LONG_DESCRIPTION = "leftOperandLongDescription";
	public static final String FILTER_LO_TYPE = "leftOperandType";
	public static final String FILTER_LO_FUNCTION = "leftOperandAggregator";
	public static final String FILTER_LO_DEFAULT_VALUE = "leftOperandDefaultValue";
	public static final String FILTER_LO_LAST_VALUE = "leftOperandLastValue";
	public static final String FILTER_OPERATOR = "operator";
	public static final String FILTER_RO_VALUE = "rightOperandValue";
	public static final String FILTER_RO_DESCRIPTION = "rightOperandDescription";
	public static final String FILTER_RO_LONG_DESCRIPTION = "rightOperandLongDescription";
	public static final String FILTER_RO_TYPE = "rightOperandType";
	public static final String FILTER_RO_FUNCTION = "rightOperandAggregator";
	public static final String FILTER_RO_DEFAULT_VALUE = "rightOperandDefaultValue";
	public static final String FILTER_RO_LAST_VALUE = "rightOperandLastValue";
	public static final String FILTER_BOOLEAN_CONNETOR = "booleanConnector";

	

	public static final String EXPRESSION = "expression";
	public static final String EXPRESSION_TYPE = "type";
	public static final String EXPRESSION_VALUE = "value";
	public static final String EXPRESSION_CHILDREN = "childNodes";
	
	public static final String SUBQUERIES = "subqueries";
	
	public static final String DIALECT_MYSQL = "org.hibernate.dialect.MySQLInnoDBDialect";
	public static final String DIALECT_POSTGRES = "org.hibernate.dialect.PostgreSQLDialect";
	public static final String DIALECT_ORACLE = "org.hibernate.dialect.OracleDialect";
	public static final String DIALECT_HSQL = "org.hibernate.dialect.HSQLDialect";
	public static final String DIALECT_ORACLE9i10g = "org.hibernate.dialect.Oracle9Dialect";
	public static final String DIALECT_SQLSERVER = "org.hibernate.dialect.SQLServerDialect";
	public static final String DIALECT_INGRES = "org.hibernate.dialect.IngresDialect";
	
	//Date functions
	public static final String FUNC_GG_BETWEEN_DATES = "GG-between-dates";
	public static final String FUNC_MM_BETWEEN_DATES = "MM-between-dates";
	public static final String FUNC_YY_BETWEEN_DATES = "YY-between-dates";
	public static final String FUNC_GG_UP_TODAY = "gg_up_today";
	public static final String FUNC_MM_UP_TODAY = "mm_up_today";
	public static final String FUNC_YY_UP_TODAY = "yy_up_today";
	
	
	
}
