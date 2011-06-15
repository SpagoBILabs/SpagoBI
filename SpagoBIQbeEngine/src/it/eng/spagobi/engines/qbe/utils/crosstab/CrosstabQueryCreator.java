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

import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.qbe.statement.IStatement;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.utilities.sql.SqlUtils;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


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
    
	public static String getCrosstabQuery(CrosstabDefinition crosstabDefinition, Query baseQuery, List<WhereField> whereField, String sqlQuery, IStatement stmt) {
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer();
		
		List baseQuerySelectedFields = SqlUtils.getSelectFields(sqlQuery);
		
		putSelectClause(buffer, crosstabDefinition, baseQuery, baseQuerySelectedFields);
			
		buffer.append(" FROM TEMPORARY_TABLE ");
		
		putWhereClause(buffer, whereField, baseQuery, baseQuerySelectedFields, (AbstractStatement)stmt);
		
		putGroupByClause(buffer, crosstabDefinition, baseQuery, baseQuerySelectedFields);
		
		String toReturn = buffer.toString();
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	private static void putSelectClause(StringBuffer toReturn,
			CrosstabDefinition crosstabDefinition, Query baseQuery, List baseQuerySelectedFields) {
		logger.debug("IN");
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
		List<CrosstabDefinition.Measure> measures = crosstabDefinition.getMeasures(); 
		
		toReturn.append("SELECT ");
		
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String alias = getSQLAlias(aColumn.getAlias(), baseQuery, baseQuerySelectedFields);
			toReturn.append(alias);
			toReturn.append(", ");
		}
		// appends rows
		Iterator<CrosstabDefinition.Row> rowsIt = rows.iterator();
		while (rowsIt.hasNext()) {
			CrosstabDefinition.Row aRow = rowsIt.next();
			String alias = getSQLAlias(aRow.getAlias(), baseQuery, baseQuerySelectedFields);
			toReturn.append(alias);
			toReturn.append(", ");
		}
		
		// appends measures
		Iterator<CrosstabDefinition.Measure> measuresIt = measures.iterator();
		while (measuresIt.hasNext()) {
			CrosstabDefinition.Measure aMeasure = measuresIt.next();
			IAggregationFunction function = aMeasure.getAggregationFunction();
			String alias = getSQLAlias(aMeasure.getAlias(), baseQuery, baseQuerySelectedFields);
			if (alias == null) {
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
					toReturn.append(function.apply(alias));
				} else {
					toReturn.append(alias);
				}
			}
			if (measuresIt.hasNext()) {
				toReturn.append(", ");
			}
		}

		logger.debug("OUT");
	}
	
	private static void putGroupByClause(StringBuffer toReturn,
			CrosstabDefinition crosstabDefinition, Query baseQuery, List baseQuerySelectedFields) {
		logger.debug("IN");
		List<CrosstabDefinition.Row> rows = crosstabDefinition.getRows();
		List<CrosstabDefinition.Column> colums = crosstabDefinition.getColumns();
		
		toReturn.append(" GROUP BY ");
		
		// appends columns
		Iterator<CrosstabDefinition.Column> columsIt = colums.iterator();
		while (columsIt.hasNext()) {
			CrosstabDefinition.Column aColumn = columsIt.next();
			String alias = getSQLAlias(aColumn.getAlias(), baseQuery, baseQuerySelectedFields);
			toReturn.append(alias);
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
			String alias = getSQLAlias(aRow.getAlias(), baseQuery, baseQuerySelectedFields);
			toReturn.append(alias);
			if (rowsIt.hasNext()) {
				toReturn.append(", ");
			}
		}
		logger.debug("OUT");
		
	}
	
	private static String getSQLAlias(String elementElias, Query baseQuery, List baseQuerySelectedFields) {
		logger.debug("IN");
		String toReturn = null;
		
		List qbeQueryFields = baseQuery.getSelectFields(true);
		int index = -1;
		for (int i = 0; i < qbeQueryFields.size(); i++) {
			ISelectField field = (ISelectField) qbeQueryFields.get(i);
			if (field.getAlias().equals(elementElias)) {
				index = i;
				break;
			}
		}
		
		if (index > -1) {
			String[] sqlField = (String[]) baseQuerySelectedFields.get(index);
			toReturn = sqlField[1] != null ? sqlField[1] : sqlField[0];
		}
		
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
	
	private static void putWhereClause(StringBuffer toReturn, List<WhereField> whereFields, Query baseQuery, List baseQuerySelectedFields, AbstractStatement stmt) {
		String boundedValue,leftValue,alias;
		String[] rightValues;
		IModelField datamartField;
		
		logger.debug("IN");
		if(whereFields!=null && whereFields.size()>0){
			toReturn.append(" WHERE ");
			for(int i=0; i<whereFields.size(); i++){
				leftValue = whereFields.get(i).getLeftOperand().values[0];
				datamartField = stmt.getDataSource().getModelStructure().getField(leftValue);
				
				rightValues = whereFields.get(i).getRightOperand().values;
				
				alias = getSQLAliasByUniqueName(datamartField.getUniqueName(), baseQuery, baseQuerySelectedFields);
				if(rightValues.length==1){
					boundedValue = stmt.getValueBounded(rightValues[0], datamartField.getType());
					toReturn.append(alias+" = "+boundedValue);
				}else{
					toReturn.append(alias+" IN (");
					for(int j=0; j<rightValues.length; j++){
						boundedValue = stmt.getValueBounded(rightValues[j], datamartField.getType());
						toReturn.append(boundedValue);
						if (j<rightValues.length-1) {
							toReturn.append(", ");
						}
					}
					toReturn.append(") ");
				}				
				if (i<whereFields.size()-1) {
					toReturn.append(" AND ");
				}
			}
		}
		logger.debug("OUT: returning " + toReturn);
	}
	
	
	private static String getSQLAliasByUniqueName(String elementUniqueName, Query baseQuery, List baseQuerySelectedFields) {
		logger.debug("IN");
		String toReturn = null;
		
		List qbeQueryFields = baseQuery.getSelectFields(true);
		int index = -1;
		for (int i = 0; i < qbeQueryFields.size(); i++) {
			DataMartSelectField field = (DataMartSelectField) qbeQueryFields.get(i);
			if (field.getUniqueName().equals(elementUniqueName)) {
				index = i;
				break;
			}
		}
		
		if (index > -1) {
			String[] sqlField = (String[]) baseQuerySelectedFields.get(index);
			toReturn = sqlField[1] != null ? sqlField[1] : sqlField[0];
		}
		
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

}
