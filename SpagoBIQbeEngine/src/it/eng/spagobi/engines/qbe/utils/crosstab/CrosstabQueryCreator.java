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
package it.eng.spagobi.engines.qbe.utils.crosstab;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Column;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Row;
import it.eng.spagobi.engines.qbe.worksheet.bo.Attribute;
import it.eng.spagobi.engines.qbe.worksheet.bo.Measure;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.sql.SqlUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Creates the crosstab query 
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class CrosstabQueryCreator {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CrosstabQueryCreator.class);
	
    public static final String QBE_SMARTFILTER_COUNT = "qbe_smartfilter_count"; 
    
	public static String getCrosstabQuery(
			CrosstabDefinition crosstabDefinition,
			IDataSetTableDescriptor descriptor,
			List<WhereField> whereFields) {
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer();
		
		putSelectClause(buffer, crosstabDefinition, descriptor);
			
		buffer.append(" FROM " + descriptor.getTableName() + " ");
		
		if (whereFields == null) {
			whereFields = new ArrayList<WhereField>();
		}
		addColumnsValuesToWhereClause(crosstabDefinition.getColumns(), whereFields);
		addRowsValuesToWhereClause(crosstabDefinition.getRows(), whereFields);
		
		putWhereClause(buffer, whereFields, descriptor);
		
		putGroupByClause(buffer, crosstabDefinition, descriptor);
		
		String toReturn = buffer.toString();
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

    
    
//	public static String getCrosstabQuery(CrosstabDefinition crosstabDefinition, Query baseQuery, List<WhereField> whereFields, String sqlQuery, IStatement stmt) {
//		logger.debug("IN");
//		StringBuffer buffer = new StringBuffer();
//		
//		List baseQuerySelectedFields = SqlUtils.getSelectFields(sqlQuery);
//		
//		putSelectClause(buffer, crosstabDefinition, baseQuery, baseQuerySelectedFields);
//			
//		buffer.append(" FROM TEMPORARY_TABLE ");
//		
//		if (whereFields == null) {
//			whereFields = new ArrayList<WhereField>();
//		}
//		addColumnsValuesToWhereClause(crosstabDefinition.getColumns(), whereFields);
//		addRowsValuesToWhereClause(crosstabDefinition.getRows(), whereFields);
//		
//		putWhereClause(buffer, whereFields, baseQuery, baseQuerySelectedFields, (AbstractStatement)stmt);
//		
//		putGroupByClause(buffer, crosstabDefinition, baseQuery, baseQuerySelectedFields);
//		
//		String toReturn = buffer.toString();
//		logger.debug("OUT: returning " + toReturn);
//		return toReturn;
//	}
	
	private static void addColumnsValuesToWhereClause(List<Column> columns,
			List<WhereField> whereFields) {
		Iterator<CrosstabDefinition.Column> it = columns.iterator();
		while (it.hasNext()) {
			CrosstabDefinition.Column aColumn = it.next();
			addAttributeToWhereClause(aColumn, whereFields);
		}
	}
	
	private static void addRowsValuesToWhereClause(List<Row> rows,
			List<WhereField> whereFields) {
		Iterator<CrosstabDefinition.Row> it = rows.iterator();
		while (it.hasNext()) {
			CrosstabDefinition.Row aRow = it.next();
			addAttributeToWhereClause(aRow, whereFields);
		}
	}

	private static void addAttributeToWhereClause(Attribute attribute,
			List<WhereField> whereFields) {
		String valuesStr = attribute.getValues();
		JSONArray valuesJSON = null;
		try {
			valuesJSON = new JSONArray(valuesStr);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		if (valuesJSON.length() > 0) {
			WhereField whereField = buildWhereField(attribute, valuesJSON);
			whereFields.add(whereField);
		}
		
	}

	private static WhereField buildWhereField(Attribute attribute,
			JSONArray valuesJSON) {
		String operator = valuesJSON.length() > 1 ? CriteriaConstants.IN : CriteriaConstants.EQUALS_TO;
		Operand leftOperand = new Operand(new String[] {attribute.getEntityId()}, attribute.getAlias(), AbstractStatement.OPERAND_TYPE_FIELD, null, null);
		String[] values = new String[valuesJSON.length()];
		for (int i = 0; i < valuesJSON.length(); i++) {
			try {
				values[i] = valuesJSON.getString(i);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		Operand rightOperand = new Operand(values, attribute.getAlias(), AbstractStatement.OPERAND_TYPE_STATIC, null, null);
		WhereField whereField = new WhereField(attribute.getAlias(), attribute.getAlias(), false, leftOperand, operator, rightOperand, "AND");
		return whereField;
	}

	private static void putSelectClause(StringBuffer toReturn,
			CrosstabDefinition crosstabDefinition, IDataSetTableDescriptor descriptor) {
		logger.debug("IN");
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
		List<Measure> measures = crosstabDefinition.getMeasures(); 
		
		toReturn.append("SELECT ");
		
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String columnName = descriptor.getColumnName(aColumn.getEntityId()); // TODO change to aColumn.getName()
			toReturn.append(columnName);
			toReturn.append(", ");
		}
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = descriptor.getColumnName(aRow.getEntityId()); // TODO change to aRow.getName()
			toReturn.append(columnName);
			toReturn.append(", ");
		}
		
		// appends measures
		Iterator<Measure> measuresIt = measures.iterator();
		while (measuresIt.hasNext()) {
			Measure aMeasure = measuresIt.next();
			IAggregationFunction function = aMeasure.getAggregationFunction();
			String columnName = descriptor.getColumnName(aMeasure.getEntityId()); // TODO change to aMeasure.getName()
			if (columnName == null) {
				// when defining a crosstab inside the SmartFilter document, an additional COUNT field with id QBE_SMARTFILTER_COUNT
				// is automatically added inside query fields, therefore the alias is not found on base query selected fields
				if (aMeasure.getEntityId().equals(QBE_SMARTFILTER_COUNT)) {
					toReturn.append(AggregationFunctions.COUNT_FUNCTION.apply("*"));
				} else {
					logger.error("Alias " + aMeasure.getAlias() + " not found on the base query!!!!");
					throw new RuntimeException("Alias " + aMeasure.getAlias() + " not found on the base query!!!!");
				}
			} else {
				if (function != AggregationFunctions.NONE_FUNCTION) {
					toReturn.append(function.apply(columnName));
				} else {
					toReturn.append(columnName);
				}
			}
			if (measuresIt.hasNext()) {
				toReturn.append(", ");
			}
		}

		logger.debug("OUT");
	}
	
	private static void putGroupByClause(StringBuffer toReturn,
			CrosstabDefinition crosstabDefinition, IDataSetTableDescriptor descriptor) {
		logger.debug("IN");
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
		
		toReturn.append(" GROUP BY ");
		
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String columnName = descriptor.getColumnName(aColumn.getEntityId());
			toReturn.append(columnName);
			if (columsIt.hasNext()) {
				toReturn.append(", ");
			}
		}
		
		// append an extra comma between grouping on columns and grouping on rows, if necessary
		if (colums.size() > 0 && rows.size() > 0) {
			toReturn.append(", ");
		}
		
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String columnName = descriptor.getColumnName(aRow.getEntityId());
			toReturn.append(columnName);
			if (rowsIt.hasNext()) {
				toReturn.append(", ");
			}
		}
		logger.debug("OUT");
		
	}
	
//	public static String getColumnName(String elementElias, Query baseQuery, List baseQuerySelectedFields) {
//		logger.debug("IN");
//		String toReturn = null;
//		
//		List qbeQueryFields = baseQuery.getSelectFields(true);
//		int index = -1;
//		for (int i = 0; i < qbeQueryFields.size(); i++) {
//			ISelectField field = (ISelectField) qbeQueryFields.get(i);
//			if (field.getAlias().equals(elementElias)) {
//				index = i;
//				break;
//			}
//		}
//		
//		if (index > -1) {
//			String[] sqlField = (String[]) baseQuerySelectedFields.get(index);
//			toReturn = sqlField[1] != null ? sqlField[1] : sqlField[0];
//		}
//		
//		logger.debug("OUT: returning " + toReturn);
//		return toReturn;
//	}
	
	
	private static void putWhereClause(StringBuffer toReturn, List<WhereField> whereFields, IDataSetTableDescriptor descriptor) {
		String boundedValue, leftValue, columnName;
		String[] rightValues;
		
		logger.debug("IN");
		if (whereFields != null && whereFields.size() > 0) {
			toReturn.append(" WHERE ");
			for (int i = 0; i < whereFields.size(); i++) {
				leftValue = whereFields.get(i).getLeftOperand().values[0];
				columnName = descriptor.getColumnName(leftValue);

				rightValues = whereFields.get(i).getRightOperand().values;
				if (rightValues.length == 1) {
					boundedValue = getValueBounded(rightValues[0],
							descriptor.getColumnType(leftValue));
					toReturn.append(columnName + " = " + boundedValue);
				} else {
					toReturn.append(columnName + " IN (");
					for (int j = 0; j < rightValues.length; j++) {
						boundedValue = getValueBounded(rightValues[j],
								descriptor.getColumnType(leftValue));
						toReturn.append(boundedValue);
						if (j < rightValues.length - 1) {
							toReturn.append(", ");
						}
					}
					toReturn.append(") ");
				}
				if (i < whereFields.size() - 1) {
					toReturn.append(" AND ");
				}
			}
		}
		logger.debug("OUT: returning " + toReturn);
	}
	
	public static String getValueBounded(String operandValueToBound, Class clazz) {
		String boundedValue;
		
		boundedValue = operandValueToBound;
		
		if ( String.class.isAssignableFrom(clazz) ) {
			// if the value is already surrounded by quotes, does not neither add quotes nor escape quotes 
			if ( StringUtils.isBounded(operandValueToBound, "'") ) {
				boundedValue = operandValueToBound;
			} else {
				operandValueToBound = StringUtils.escapeQuotes(operandValueToBound);
				return StringUtils.bound(operandValueToBound, "'");
			}
		} 
		
		// TODO manage dates and timestamps
//		Date operandValueToBoundDate;

//		else if(operandType.equalsIgnoreCase("TIMESTAMP") || operandType.equalsIgnoreCase("DATE") || operandType.equalsIgnoreCase("java.sql.TIMESTAMP") || operandType.equalsIgnoreCase("java.sql.date") || operandType.equalsIgnoreCase("java.util.date")){
//
//			ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
//			String dbDialect = connection.getDialect();
//			
//			String userDateFormatPattern = (String)getParameters().get("userDateFormatPattern");
//			DateFormat userDataFormat = new SimpleDateFormat(userDateFormatPattern);		
//			try{
//				operandValueToBoundDate = userDataFormat.parse(operandValueToBound);
//			} catch (ParseException e) {
//				logger.error("Error parsing the date "+operandValueToBound);
//				throw new SpagoBIRuntimeException("Error parsing the date "+operandValueToBound+". Check the format, it should be "+userDateFormatPattern);
//			}
//			
//			boundedValue = composeStringToDt(dbDialect, operandValueToBoundDate);
//		}
		
		return boundedValue;
	}
	
	
//	private static String getSQLAliasByUniqueName(String elementUniqueName, Query baseQuery, List baseQuerySelectedFields) {
//		logger.debug("IN");
//		String toReturn = null;
//		
//		List qbeQueryFields = baseQuery.getSelectFields(true);
//		int index = -1;
//		for (int i = 0; i < qbeQueryFields.size(); i++) {
//			DataMartSelectField field = (DataMartSelectField) qbeQueryFields.get(i);
//			if (field.getUniqueName().equals(elementUniqueName)) {
//				index = i;
//				break;
//			}
//		}
//		
//		if (index > -1) {
//			String[] sqlField = (String[]) baseQuerySelectedFields.get(index);
//			toReturn = sqlField[1] != null ? sqlField[1] : sqlField[0];
//		}
//		
//		logger.debug("OUT: returning " + toReturn);
//		return toReturn;
//	}
	
	public static String getTableQuery(List<String> fieldsName,
			IDataSetTableDescriptor descriptor, List<WhereField> whereFields) {
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer();
		
		putSelectClause(buffer, fieldsName, descriptor);
			
		buffer.append(" FROM TEMPORARY_TABLE ");
		
		if (whereFields == null) {
			whereFields = new ArrayList<WhereField>();
		}
		
		putWhereClause(buffer, whereFields, descriptor);
		
		String toReturn = buffer.toString();
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	private static void putSelectClause(StringBuffer buffer, 
			List<String> fieldsName,
			IDataSetTableDescriptor descriptor) {
		
		logger.debug("IN");
		
		buffer.append("SELECT ");

		for (int i = 0; i < fieldsName.size(); i++) {
			String fieldName = fieldsName.get(i);
			String columnName = descriptor.getColumnName(fieldName);
			buffer.append(columnName);
			if (i < fieldsName.size() - 1) {
				buffer.append(", ");
			}
		}

		logger.debug("OUT");
		
	}

}
