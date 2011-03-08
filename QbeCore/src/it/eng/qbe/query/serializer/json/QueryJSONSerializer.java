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

import it.eng.qbe.bo.DatamartLabels;
import it.eng.qbe.bo.DatamartProperties;
import it.eng.qbe.cache.QbeCacheManager;
import it.eng.qbe.commons.serializer.SerializationException;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartField;
import it.eng.qbe.query.CalculatedSelectField;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.ExpressionNode;
import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.json.JSONUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class QueryJSONSerializer {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QueryJSONSerializer.class);
    
    
	public Object serialize(Query query, IDataSource dataSource, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		JSONArray recordsJOSN;
		JSONArray filtersJSON;
		JSONArray havingsJSON;
		JSONObject filterExpJOSN;
		boolean distinctClauseEnabled = false;		
		JSONArray subqueriesJSON;
		JSONObject subqueryJSON;
		Iterator subqueriesIterator;
		Query subquery;
		
		Assert.assertNotNull(query, "Query cannot be null");
		Assert.assertNotNull(query.getId(), "Query id cannot be null");
		Assert.assertNotNull(dataSource, "DataMartModel cannot be null");
		
		try {
			
			
			recordsJOSN = serializeFields(query, dataSource, locale);			
			filtersJSON = serializeFilters(query, dataSource, locale);
			filterExpJOSN = encodeFilterExp( query.getWhereClauseStructure() );
			havingsJSON = serializeHavings(query, dataSource, locale);
			
			subqueriesJSON = new JSONArray();
			subqueriesIterator = query.getSubqueryIds().iterator();		
			while(subqueriesIterator.hasNext()) {
				String id = (String)subqueriesIterator.next();
				subquery = query.getSubquery(id);
				subqueryJSON = (JSONObject)serialize(subquery, dataSource, locale);
				subqueriesJSON.put(subqueryJSON);
			} 
			
			
			result = new JSONObject();
			result.put(QuerySerializationConstants.ID, query.getId());
			result.put(QuerySerializationConstants.NAME, query.getName());
			result.put(QuerySerializationConstants.DESCRIPTION, query.getDescription());
			result.put(QuerySerializationConstants.DISTINCT, query.isDistinctClauseEnabled());
			result.put(QuerySerializationConstants.IS_NESTED_EXPRESSION, query.isNestedExpression());
			
			result.put(QuerySerializationConstants.FIELDS, recordsJOSN);
			
			result.put(QuerySerializationConstants.FILTERS, filtersJSON);
			result.put(QuerySerializationConstants.EXPRESSION, filterExpJOSN);
			
			result.put(QuerySerializationConstants.HAVINGS, havingsJSON);
			
			result.put(QuerySerializationConstants.SUBQUERIES, subqueriesJSON);
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + query, t);
		} finally {
			
		}
		
		return result;
	}
	
	
	/*
	 { 
	  "id" : "it.eng.spagobi.ProductClass:productClassId",
	  "entity" : "ProductClass",
	  "field"  : "productClassId",
	  "alias"  : "",
	  "group"  : "undefined",
	  "order"  : "",
	  "funct"  : "",
	  "visible" : "si"
	 }
	 */
	private JSONArray serializeFields(Query query, IDataSource dataSource, Locale locale) throws SerializationException {
		JSONArray result;
		
		List fields;
		ISelectField field;
		String fieldUniqueName;
		DataMartField datamartField;
		JSONObject fieldJSON;
		Iterator it;
		DatamartLabels datamartLabels;
		String label, longDescription;
		
		logger.debug("IN");
		
		try {
			datamartLabels = null;
			if(locale != null) {
				datamartLabels =  QbeCacheManager.getInstance().getLabels( dataSource , locale );
			}
			
			fields = query.getSelectFields(false);
			Assert.assertNotNull(fields, "Fields cannot be null");
			logger.debug("Query [" + query.getId() + "] have [" + fields.size() + "] field/s to serialize");
			
			result = new JSONArray();
			it = fields.iterator();
			while( it.hasNext() ) {
				field = (ISelectField)it.next();
				logger.debug("Serializing filed [" + field.getAlias() + "]");
				try {
					fieldJSON = new JSONObject();
					
					fieldJSON.put(QuerySerializationConstants.FIELD_ALIAS, field.getAlias());
					
					fieldJSON.put(QuerySerializationConstants.FIELD_VISIBLE, field.isVisible());
					fieldJSON.put(QuerySerializationConstants.FIELD_INCLUDE, field.isIncluded());					
					
					// field nature can me "measure" or "attribute"
					String nature = null;
					
					if (field.isDataMartField()) {
						DataMartSelectField dataMartSelectField = (DataMartSelectField)field;
						
						fieldJSON.put(QuerySerializationConstants.FIELD_TYPE, field.DATAMART_FIELD);
						
						fieldUniqueName = dataMartSelectField.getUniqueName();
						datamartField = dataSource.getDataMartModelStructure().getField( fieldUniqueName );
						Assert.assertNotNull(datamartField, "A filed named [" + fieldUniqueName + "] does not exist in the datamart model");
						
						fieldJSON.put(QuerySerializationConstants.FIELD_ID, datamartField.getUniqueName());
												
						// localize entity name
						label = null;
						if(datamartLabels != null) {
							label = datamartLabels.getLabel( datamartField.getParent() );
						}
						label = StringUtilities.isEmpty(label)? datamartField.getParent().getName(): label;
						fieldJSON.put(QuerySerializationConstants.FIELD_ENTITY, label);
						
						// localize field name
						label = null;
						if(datamartLabels != null) {
							label = datamartLabels.getLabel( datamartField );
						}
						label = StringUtilities.isEmpty(label)? datamartField.getName(): label;
						fieldJSON.put(QuerySerializationConstants.FIELD_NAME, label);
						longDescription = getFieldLongDescription(datamartField, datamartLabels);
						fieldJSON.put(QuerySerializationConstants.FIELD_LONG_DESCRIPTION, longDescription);
						
						if( dataMartSelectField.isGroupByField() ) {
							fieldJSON.put(QuerySerializationConstants.FIELD_GROUP, "true");
						} else {
							fieldJSON.put(QuerySerializationConstants.FIELD_GROUP, "");
						}
						fieldJSON.put(QuerySerializationConstants.FIELD_ORDER, dataMartSelectField.getOrderType());
						fieldJSON.put(QuerySerializationConstants.FIELD_AGGREGATION_FUNCTION, dataMartSelectField.getFunction().getName());
						
						DatamartProperties datamartProperties = dataSource.getDataMartProperties();
						String iconCls = datamartProperties.getFieldIconClass( datamartField );	
						fieldJSON.put(QuerySerializationConstants.FIELD_ICON_CLS, iconCls);
						
						// if an aggregation function is defined or if the field is declared as "measure" into property file,
						// then it is a measure, elsewhere it is an attribute
						if (
								(dataMartSelectField.getFunction() != null 
								&& !dataMartSelectField.getFunction().equals(AggregationFunctions.NONE_FUNCTION))
								|| iconCls.equals("measure")) {
							nature = QuerySerializationConstants.FIELD_NATURE_MEASURE;
						} else {
							nature = QuerySerializationConstants.FIELD_NATURE_ATTRIBUTE;
						}
						
					} else if (field.isCalculatedField()){
						CalculatedSelectField calculatedSelectField = (CalculatedSelectField)field;
						
						fieldJSON.put(QuerySerializationConstants.FIELD_TYPE, field.CALCULATED_FIELD);
						
						JSONObject fieldClaculationDescriptor = new JSONObject();
						fieldClaculationDescriptor.put(QuerySerializationConstants.FIELD_TYPE, calculatedSelectField.getType());
						fieldClaculationDescriptor.put(QuerySerializationConstants.FIELD_EXPRESSION, calculatedSelectField.getExpression());
						fieldJSON.put(QuerySerializationConstants.FIELD_ID, fieldClaculationDescriptor);
						
						fieldJSON.put(QuerySerializationConstants.FIELD_ICON_CLS, "calculation");
						
						nature = QuerySerializationConstants.FIELD_NATURE_POST_LINE_CALCULATED;
						
					} else if (field.isInLineCalculatedField()) {
						InLineCalculatedSelectField calculatedSelectField = (InLineCalculatedSelectField)field;
						
						fieldJSON.put(QuerySerializationConstants.FIELD_TYPE, field.IN_LINE_CALCULATED_FIELD);
						
						JSONObject fieldClaculationDescriptor = new JSONObject();
						fieldClaculationDescriptor.put(QuerySerializationConstants.FIELD_ALIAS, calculatedSelectField.getAlias());
						fieldClaculationDescriptor.put(QuerySerializationConstants.FIELD_TYPE, calculatedSelectField.getType());
						fieldClaculationDescriptor.put(QuerySerializationConstants.FIELD_EXPRESSION, calculatedSelectField.getExpression());
						fieldJSON.put(QuerySerializationConstants.FIELD_ID, fieldClaculationDescriptor);
						fieldJSON.put(QuerySerializationConstants.FIELD_LONG_DESCRIPTION, calculatedSelectField.getExpression());

						if ( calculatedSelectField.isGroupByField() ) {
							fieldJSON.put(QuerySerializationConstants.FIELD_GROUP, "true");
						} else {
							fieldJSON.put(QuerySerializationConstants.FIELD_GROUP, "");
						}
						
						fieldJSON.put(QuerySerializationConstants.FIELD_AGGREGATION_FUNCTION, calculatedSelectField.getFunction().getName());
						fieldJSON.put(QuerySerializationConstants.FIELD_ORDER, calculatedSelectField.getOrderType());
						
						//fieldJSON.put(SerializationConstants.FIELD_GROUP, "");
						fieldJSON.put(QuerySerializationConstants.FIELD_ORDER, "");
						//fieldJSON.put(SerializationConstants.FIELD_AGGREGATION_FUNCTION, "");
						
						fieldJSON.put(QuerySerializationConstants.FIELD_ICON_CLS, "calculation");
						
						/*
						 * We should understand if the calculated field is an attribute (i.e. a composition of attributes)
						 * or a measure (i.e. a composition of measures).
						 * The easiest way to understand this it to see if it is a grouping field.
						 * TODO manage queries without any aggregation and grouping.
						 * At the time being this information is used only in crosstab definition, and crosstab base query SHOULD 
						 * make aggregation.
						 */
						if ( calculatedSelectField.isGroupByField() ) {
							nature = QuerySerializationConstants.FIELD_NATURE_ATTRIBUTE;
						} else {
							nature = QuerySerializationConstants.FIELD_NATURE_MEASURE;
						}
						
					}
					
					fieldJSON.put(QuerySerializationConstants.FIELD_NATURE, nature);	
					
				} catch(Throwable t) {
					throw new SerializationException("An error occurred while serializing field: " + field.getAlias(), t);
				}
				logger.debug("Filed [" + field.getAlias() + "] serialized succesfully: [" + fieldJSON.toString() + "]");
				result.put(fieldJSON);
			}
			
		}catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing select clause of query: " + query.getId(), t);
		} finally {
			logger.debug("OUT");
		}
		
		return result;
	}	
	
	public static String getFieldLongDescription(DataMartField field, DatamartLabels datamartLabels) {
		String label = field.getName();
		if (datamartLabels != null) {
			label = datamartLabels.getLabel(field);
		}
		String extendedLabel = StringUtilities.isEmpty(label)? field.getName(): label;
		DataMartEntity parent = field.getParent();
		if (parent == null) return extendedLabel;
		else return getEntityLongDescription(parent, datamartLabels) + " : " + extendedLabel;
	}
	
	public static String getEntityLongDescription(DataMartEntity entity, DatamartLabels datamartLabels) {
		String label = entity.getName();
		if (datamartLabels != null) {
			label = datamartLabels.getLabel(entity);
		}
		String extendedLabel = StringUtilities.isEmpty(label)? entity.getName(): label;
		DataMartEntity parent = entity.getParent();
		if (parent == null) return extendedLabel;
		else return getEntityLongDescription(parent, datamartLabels) + " / " + extendedLabel;
	}
	
	/*
	 
				Iterator it = query.getSelectFields().iterator();
				while( it.hasNext() ) {
					SelectField selectField = (SelectField)it.next();
					DataMartField datamartField = getDatamartModel().getDataMartModelStructure().getField(selectField.getUniqueName());
					String label;
					label = datamartLabels.getLabel(datamartField);
					label =  StringUtilities.isEmpty(label)? datamartField.getName(): label;
				} 
	 */
	
	/*
	{
	  "id" : "it.eng.spagobi.ProductClass:productClassId",
	  "entity" : "ProductClass",
	  "field"  : "productClassId",
	  //"alias"  : "",
	  "operator"  : "GREATER THAN",
	  "value"  : "5",
	  "type"  : "Static Value"
	  }
	 */
	private JSONArray serializeFilters(Query query, IDataSource datamartModel, Locale locale) throws SerializationException {
		JSONArray filtersJOSN = new JSONArray();
		
		List filters;
		WhereField filter;
		WhereField.Operand operand;
		JSONObject filterJSON;
		DataMartField datamartFilter;
		String fieldUniqueName;
		Iterator it;
		DatamartLabels datamartLabels;
		DataMartField datamartField;
		
		filters = query.getWhereFields();
		Assert.assertNotNull(filters, "Filters cannot be null");
		
		datamartLabels = null;
		if(locale != null) {
			datamartLabels =  QbeCacheManager.getInstance().getLabels( datamartModel , locale );
		}
		
		it = filters.iterator();
		while( it.hasNext() ) {
			filter = (WhereField)it.next();
			
			filterJSON = new JSONObject();
			try {
				filterJSON.put(QuerySerializationConstants.FILTER_ID, filter.getName());
				filterJSON.put(QuerySerializationConstants.FILTER_DESCRIPTION, filter.getDescription());
				filterJSON.put(QuerySerializationConstants.FILTER_PROMPTABLE, filter.isPromptable());
				
				operand = filter.getLeftOperand();
				filterJSON.put(QuerySerializationConstants.FILTER_LO_VALUE, operand.values[0]);
				if(operand.type.equalsIgnoreCase("Field Content")) {
					if(operand.values[0].contains("\"expression\":\"")){
						filterJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description );
						String description = operand.values[0].substring(operand.values[0].indexOf("\"expression\":\"")+14);
						description.substring(0, description.indexOf("\""));
						filterJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, description);						
					}else{
						datamartField = datamartModel.getDataMartModelStructure().getField( operand.values[0] );
						
						String labelF, labelE;
						labelE = null;
						if(datamartLabels != null) {
							labelE = datamartLabels.getLabel( datamartField.getParent() );
						}
						labelE = StringUtilities.isEmpty(labelE)? datamartField.getParent().getName(): labelE;
						
						
						labelF = null;
						if(datamartLabels != null) {
							labelF = datamartLabels.getLabel( datamartField );
						}
						labelF = StringUtilities.isEmpty(labelF)? datamartField.getName(): labelF;
						
						filterJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, labelE  + " : " + labelF );
						
						String loLongDescription = getFieldLongDescription(datamartField, datamartLabels);
						filterJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
					}
				} else if(operand.type.equalsIgnoreCase("Subquery")) {
					String loLongDescription = "Subquery " + operand.description;
					filterJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
					
					filterJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				} else if(operand.type.equalsIgnoreCase("Parent Field Content")) {
					String[] chunks = operand.values[0].split(" ");
					String parentQueryId = chunks[0];
					String fieldName = chunks[1];
					datamartField = datamartModel.getDataMartModelStructure().getField( fieldName );
					String datamartFieldLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					String loLongDescription = "Query " + parentQueryId + ", " + datamartFieldLongDescription;
					filterJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
					
					filterJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				} else {
					filterJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				}
				
				
				
				filterJSON.put(QuerySerializationConstants.FILTER_LO_TYPE, operand.type);
				filterJSON.put(QuerySerializationConstants.FILTER_LO_DEFAULT_VALUE, operand.defaulttValues[0]);
				filterJSON.put(QuerySerializationConstants.FILTER_LO_LAST_VALUE, operand.lastValues[0]);
				
				filterJSON.put(QuerySerializationConstants.FILTER_OPERATOR, filter.getOperator());
				
				operand = filter.getRightOperand();
				filterJSON.put(QuerySerializationConstants.FILTER_RO_VALUE, JSONUtils.asJSONArray(operand.values));
				if(operand.type.equalsIgnoreCase("Field Content")) {
					datamartField = datamartModel.getDataMartModelStructure().getField( operand.values[0] );
					
					String labelF, labelE;
					labelE = null;
					if(datamartLabels != null) {
						labelE = datamartLabels.getLabel( datamartField.getParent() );
					}
					labelE = StringUtilities.isEmpty(labelE)? datamartField.getParent().getName(): labelE;
					
					
					labelF = null;
					if(datamartLabels != null) {
						labelF = datamartLabels.getLabel( datamartField );
					}
					labelF = StringUtilities.isEmpty(labelF)? datamartField.getName(): labelF;
					
					filterJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, labelE  + " : " + labelF );
					
					String roLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					filterJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, roLongDescription);
				} else if(operand.type.equalsIgnoreCase("Subquery")) {
					String roLongDescription = "Subquery " + operand.description;
					filterJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, roLongDescription);
					
					filterJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				} else if(operand.type.equalsIgnoreCase("Parent Field Content")) {
					String[] chunks = operand.values[0].split(" ");
					String parentQueryId = chunks[0];
					String fieldName = chunks[1];
					datamartField = datamartModel.getDataMartModelStructure().getField( fieldName );
					String datamartFieldLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					String loLongDescription = "Query " + parentQueryId + ", " + datamartFieldLongDescription;
					filterJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, loLongDescription);
					
					filterJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				} else {
					filterJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				}
				filterJSON.put(QuerySerializationConstants.FILTER_RO_TYPE, operand.type);
				filterJSON.put(QuerySerializationConstants.FILTER_RO_DEFAULT_VALUE, JSONUtils.asJSONArray(operand.defaulttValues));
				filterJSON.put(QuerySerializationConstants.FILTER_RO_LAST_VALUE, JSONUtils.asJSONArray(operand.lastValues));
				
				filterJSON.put(QuerySerializationConstants.FILTER_BOOLEAN_CONNETOR, filter.getBooleanConnector());
				
			} catch(JSONException e) {
				throw new SerializationException("An error occurred while serializing filter: " + filter.getName(), e);
			}
			filtersJOSN.put(filterJSON);
		}
		
		return filtersJOSN;
	}
	
	private JSONArray serializeHavings(Query query, IDataSource dataSource, Locale locale) throws SerializationException {
		JSONArray havingsJSON = new JSONArray();
		
		List havings;
		HavingField filter;
		HavingField.Operand operand;
		JSONObject havingJSON;
		DataMartField datamartFilter;
		String fieldUniqueName;
		Iterator it;
		DatamartLabels datamartLabels;
		DataMartField datamartField;
		
		havings = query.getHavingFields();
		Assert.assertNotNull(havings, "Filters cannot be null");
		
		datamartLabels = null;
		if(locale != null) {
			datamartLabels =  QbeCacheManager.getInstance().getLabels( dataSource , locale );
		}
		
		it = havings.iterator();
		while( it.hasNext() ) {
			filter = (HavingField)it.next();
			
			havingJSON = new JSONObject();
			try {
				havingJSON.put(QuerySerializationConstants.FILTER_ID, filter.getName());
				havingJSON.put(QuerySerializationConstants.FILTER_DESCRIPTION, filter.getDescription());
				havingJSON.put(QuerySerializationConstants.FILTER_PROMPTABLE, filter.isPromptable());
				
				operand = filter.getLeftOperand();
				havingJSON.put(QuerySerializationConstants.FILTER_LO_VALUE, operand.values[0]);
				if(operand.type.equalsIgnoreCase("Field Content")) {
					
					if(operand.values[0].contains("\"expression\":\"")){
						havingJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description );
						String description = operand.values[0].substring(operand.values[0].indexOf("\"expression\":\"")+14);
						description.substring(0, description.indexOf("\""));
						havingJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, description);						
					}else{
					
						datamartField = dataSource.getDataMartModelStructure().getField( operand.values[0] );
						
						String labelF, labelE;
						labelE = null;
						if(datamartLabels != null) {
							labelE = datamartLabels.getLabel( datamartField.getParent() );
						}
						labelE = StringUtilities.isEmpty(labelE)? datamartField.getParent().getName(): labelE;
						
						
						labelF = null;
						if(datamartLabels != null) {
							labelF = datamartLabels.getLabel( datamartField );
						}
						labelF = StringUtilities.isEmpty(labelF)? datamartField.getName(): labelF;
						
						havingJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, labelE  + " : " + labelF );
						
						String loLongDescription = getFieldLongDescription(datamartField, datamartLabels);
						havingJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
	
					}	
					
				} else if(operand.type.equalsIgnoreCase("Subquery")) {
					String loLongDescription = "Subquery " + operand.description;
					havingJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
					
					havingJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				} else if(operand.type.equalsIgnoreCase("Parent Field Content")) {
					String[] chunks = operand.values[0].split(" ");
					String parentQueryId = chunks[0];
					String fieldName = chunks[1];
					datamartField = dataSource.getDataMartModelStructure().getField( fieldName );
					String datamartFieldLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					String loLongDescription = "Query " + parentQueryId + ", " + datamartFieldLongDescription;
					havingJSON.put(QuerySerializationConstants.FILTER_LO_LONG_DESCRIPTION, loLongDescription);
					
					havingJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				} else {
					havingJSON.put(QuerySerializationConstants.FILTER_LO_DESCRIPTION, operand.description);
				}
				
				
				
				havingJSON.put(QuerySerializationConstants.FILTER_LO_TYPE, operand.type);
				havingJSON.put(QuerySerializationConstants.FILTER_LO_FUNCTION, operand.function.getName());
				havingJSON.put(QuerySerializationConstants.FILTER_LO_DEFAULT_VALUE, operand.defaulttValues[0]);
				havingJSON.put(QuerySerializationConstants.FILTER_LO_LAST_VALUE, operand.lastValues[0]);
				
				havingJSON.put(QuerySerializationConstants.FILTER_OPERATOR, filter.getOperator());
				
				operand = filter.getRightOperand();
				havingJSON.put(QuerySerializationConstants.FILTER_RO_VALUE, JSONUtils.asJSONArray(operand.values));
				if(operand.type.equalsIgnoreCase("Field Content")) {
					datamartField = dataSource.getDataMartModelStructure().getField( operand.values[0] );
					
					String labelF, labelE;
					labelE = null;
					if(datamartLabels != null) {
						labelE = datamartLabels.getLabel( datamartField.getParent() );
					}
					labelE = StringUtilities.isEmpty(labelE)? datamartField.getParent().getName(): labelE;
					
					
					labelF = null;
					if(datamartLabels != null) {
						labelF = datamartLabels.getLabel( datamartField );
					}
					labelF = StringUtilities.isEmpty(labelF)? datamartField.getName(): labelF;
					
					havingJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, labelE  + " : " + labelF );
					
					String roLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					havingJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, roLongDescription);
				} else if(operand.type.equalsIgnoreCase("Subquery")) {
					String roLongDescription = "Subquery " + operand.description;
					havingJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, roLongDescription);
					
					havingJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				} else if(operand.type.equalsIgnoreCase("Parent Field Content")) {
					String[] chunks = operand.values[0].split(" ");
					String parentQueryId = chunks[0];
					String fieldName = chunks[1];
					datamartField = dataSource.getDataMartModelStructure().getField( fieldName );
					String datamartFieldLongDescription = getFieldLongDescription(datamartField, datamartLabels);
					String loLongDescription = "Query " + parentQueryId + ", " + datamartFieldLongDescription;
					havingJSON.put(QuerySerializationConstants.FILTER_RO_LONG_DESCRIPTION, loLongDescription);
					
					havingJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				} else {
					havingJSON.put(QuerySerializationConstants.FILTER_RO_DESCRIPTION, operand.description);
				}
				havingJSON.put(QuerySerializationConstants.FILTER_RO_TYPE, operand.type);
				havingJSON.put(QuerySerializationConstants.FILTER_RO_FUNCTION, operand.function.getName());
				havingJSON.put(QuerySerializationConstants.FILTER_RO_DEFAULT_VALUE, JSONUtils.asJSONArray(operand.defaulttValues));
				havingJSON.put(QuerySerializationConstants.FILTER_RO_LAST_VALUE, JSONUtils.asJSONArray(operand.lastValues));
				
				havingJSON.put(QuerySerializationConstants.FILTER_BOOLEAN_CONNETOR, filter.getBooleanConnector());
				
			} catch(JSONException e) {
				throw new SerializationException("An error occurred while serializing filter: " + filter.getName(), e);
			}
			havingsJSON.put(havingJSON);
		}
		
		return havingsJSON;
	}
		
	private JSONObject encodeFilterExp(ExpressionNode filterExp) throws SerializationException {
		JSONObject exp = new JSONObject();
		JSONArray childsJSON = new JSONArray();
		
		if(filterExp == null) return exp;
		
		try {
			exp.put(QuerySerializationConstants.EXPRESSION_TYPE, filterExp.getType()) ;
			exp.put(QuerySerializationConstants.EXPRESSION_VALUE, filterExp.getValue());
			
			for(int i = 0; i < filterExp.getChildNodes().size(); i++) {
				ExpressionNode child = (ExpressionNode)filterExp.getChildNodes().get(i);
				JSONObject childJSON = encodeFilterExp(child);
				childsJSON.put(childJSON);
			}		
			
			exp.put(QuerySerializationConstants.EXPRESSION_CHILDREN, childsJSON);
		} catch(JSONException e) {
			throw new SerializationException("An error occurred while serializing filter expression", e);
		}		
		 
		return exp;
	}
	
}
