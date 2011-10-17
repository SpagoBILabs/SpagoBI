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

import it.eng.qbe.model.structure.ModelCalculatedField.Slot;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class Query {
	String id;
	String name;
	String description;
	
	boolean distinctClauseEnabled;
	
	List<ISelectField> selectFields;	
	List<WhereField> whereClause;
	List<HavingField> havingClause;
	
	ExpressionNode whereClauseStructure;
	boolean nestedExpression;

	Map whereFieldMap;
	Map havingFieldMap;
	
	Query parentQuery;
	Map subqueries;
	
	public Query() {
		selectFields = new ArrayList();		
		whereClause = new ArrayList();
		havingClause = new ArrayList();
		whereFieldMap = new HashMap();
		havingFieldMap = new HashMap();
		subqueries  = new HashMap();
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	
	public boolean isEmpty() {
		int selectedFieldsCount;
		List fields, calculatedFields, inlineCalculatedFields;
		
		fields = getSimpleSelectFields(true);
		Assert.assertNotNull(fields, "getDataMartSelectFields method cannot return a null value");
		calculatedFields = getCalculatedSelectFields(true);
		Assert.assertNotNull(fields, "getCalculatedSelectFields method cannot return a null value");
		inlineCalculatedFields = getInLineCalculatedSelectFields(true);
		Assert.assertNotNull(fields, "getInLineCalculatedSelectFields method cannot return a null value");
		
		selectedFieldsCount = fields.size() + calculatedFields.size() + inlineCalculatedFields.size();
		
		return (selectedFieldsCount == 0);
	}
	
	public void addSelectFiled(String fieldUniqueName, String function, String fieldAlias, boolean include, boolean visible,
			boolean groupByField, String orderType, String pattern) {
		selectFields.add( new SimpleSelectField(fieldUniqueName, function, fieldAlias, include, visible, groupByField, orderType, pattern) );
	}
	
	public void addCalculatedFiled(String fieldAlias, String expression, String type, boolean included, boolean visible) {
		selectFields.add( new CalculatedSelectField(fieldAlias, expression, type, included, visible) );
	}
	
	public void addInLineCalculatedFiled(String fieldAlias, String expression, String slots, String type, boolean included, boolean visible, boolean groupByField, String orderType, String funct) {
		selectFields.add( new InLineCalculatedSelectField(fieldAlias, expression, slots, type, included, visible, groupByField, orderType, funct) );
	}

	
	public WhereField addWhereField(String name, String description, boolean promptable,
			it.eng.qbe.query.WhereField.Operand leftOperand, String operator, it.eng.qbe.query.WhereField.Operand rightOperand,
			String booleanConnector) {
		
		WhereField whereField = new WhereField(name, description, promptable,  leftOperand, operator, rightOperand, booleanConnector);
		
		whereClause.add( whereField );
		whereFieldMap.put("$F{" + name + "}", whereField);
		return whereField;  
	}
	
	public HavingField addHavingField(String name, String description, boolean promptable, 
			it.eng.qbe.query.HavingField.Operand leftOperand, String operator, it.eng.qbe.query.HavingField.Operand rightOperand,
			String booleanConnector) {
		
		HavingField havingField = new HavingField(name, description, promptable, leftOperand, operator, rightOperand, booleanConnector);
		
		havingClause.add( havingField );
		havingFieldMap.put("$F{" + name + "}", havingField);
		return havingField;
	}
	
	public WhereField getWhereFieldByName(String fname) {
		return (WhereField)whereFieldMap.get(fname.trim());
	}
	
	public HavingField getHavingFieldByName(String fname) {
		return (HavingField)havingFieldMap.get(fname.trim());
	}
	

	/**
	 * @param onlyIncluded true to return all the select fields. 
	 * false to include only the select fields actually included in the select clause of the generated statemet (i.e
	 * it is possible for a select field to be used only in 'order by' or in 'group by' clause of the statement)
	 * 
	 * @return a List of all selected fields (ISelectField). All the field types are included (i.e. simple fields, calculated fields
	 * and inline calculated fields). Never returns null. If there are no selected fields in the query it returns an empty list.
	 */
	public List<ISelectField> getSelectFields(boolean onlyIncluded) {
		List<ISelectField> fields;
		if(onlyIncluded == false) {
			fields = new ArrayList<ISelectField>(selectFields);
		} else {
			fields = new ArrayList<ISelectField>();
			for(ISelectField field : selectFields) {
				if(field.isIncluded()) {
					fields.add(field);
				}
			}
		}
		return fields;
	}
	
	public List getSelectSimpleFieldsByUniqueName(String uniqueName) {
	List<SimpleSelectField> matchingSimpleSelectFields;
		
		matchingSimpleSelectFields = new ArrayList<SimpleSelectField>();
		List<SimpleSelectField> simpleSelectFields = getSimpleSelectFields(false);
		for(SimpleSelectField simpleSelectField : simpleSelectFields) {
			if(simpleSelectField.getUniqueName().equalsIgnoreCase(uniqueName)) {
				matchingSimpleSelectFields.add(simpleSelectField);
			}
		}
		
		return matchingSimpleSelectFields;
	}
	
	public List<SimpleSelectField> getSelectSimpleFieldsByAlias(String alias) {
		List<SimpleSelectField> matchingSimpleSelectFields;
		
		matchingSimpleSelectFields = new ArrayList<SimpleSelectField>();
		List<SimpleSelectField> simpleSelectFields = getSimpleSelectFields(false);
		for(SimpleSelectField simpleSelectField : simpleSelectFields) {
			if(simpleSelectField.getAlias().equalsIgnoreCase(alias)) {
				matchingSimpleSelectFields.add(simpleSelectField);
			}
		}
		
		return matchingSimpleSelectFields;
	}
	
	public void removeSelectField(int fieldIndex) {
		Assert.assertTrue(fieldIndex >= 0 && fieldIndex < selectFields.size(), "Index [" + fieldIndex + "] out of bound for select fields list (0 - " + selectFields.size() + ")");
		selectFields.remove(fieldIndex);
	}
	
	public void removeWhereField(int fieldIndex) {
		Assert.assertTrue(fieldIndex >= 0 && fieldIndex < whereClause.size(), "Index [" + fieldIndex + "] out of bound for select fields list (0 - " + whereClause.size() + ")");
		whereClause.remove(fieldIndex);
	}
	
	public void removeHavingField(int fieldIndex) {
		Assert.assertTrue(fieldIndex >= 0 && fieldIndex < havingClause.size(), "Index [" + fieldIndex + "] out of bound for select fields list (0 - " + havingClause.size() + ")");
		havingClause.remove(fieldIndex);
	}
	
	public ISelectField getSelectFieldByIndex(int fieldIndex) {
		Assert.assertTrue(fieldIndex >= 0 && fieldIndex < selectFields.size(), "Index [" + fieldIndex + "] out of bound for select fields list (0 - " + selectFields.size() + ")");
		return (ISelectField)selectFields.get(fieldIndex);
	}
	
	public int getSelectFieldIndex(String uniqueName) {
		int index;
		
		index = -1;
				
		for(int i = 0; i < selectFields.size(); i++) {
			ISelectField f = (ISelectField)selectFields.get(i);
			if(f.isSimpleField()) {
				SimpleSelectField field = (SimpleSelectField)f;
				if(field.getUniqueName().equalsIgnoreCase(uniqueName)) {
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	

	/**
	 * Returns a list of of simple select fields (no inlineCalculatedSelectField & calculatedSelectField)
	 * 
	 * @param onlyIncluded if true the returned list will include only the simple select fields actually included in the select statement. All the simple
	 * select fields will be returned otherwise.
	 * 
	 * @return a list of SimpleSelectField. It never returns null. If there are not fields in select clause it will return an empty list.
	 */
	public List<SimpleSelectField> getSimpleSelectFields(boolean onlyIncluded) {
		List<SimpleSelectField> simpleSelectFields;
				
		simpleSelectFields = new ArrayList<SimpleSelectField>();
		for(ISelectField selectField :  selectFields) {			
			if(selectField.isSimpleField()) {
				if( onlyIncluded == false || (onlyIncluded == true && selectField.isIncluded()) ) {
					simpleSelectFields.add((SimpleSelectField)selectField);
				}				
			}
		}
		
		return simpleSelectFields;
	}
	
	public List getCalculatedSelectFields(boolean onlyIncluded) {
		List calculatedSelectFields;
		Iterator it;
		ISelectField field;
		
		calculatedSelectFields = new ArrayList();
		it = getSelectFields(false).iterator();
		while(it.hasNext()) {
			field = (ISelectField)it.next();
			if(field.isCalculatedField()) {
				if( onlyIncluded == false || (onlyIncluded == true && field.isIncluded()) ) {
					calculatedSelectFields.add(field);
				}
			}
		}
		
		return calculatedSelectFields;
	}
	
	/**
	 * Returns the list of inline calculated fields included in select clause (no simpleSelectField & calculatedSelectField)
	 * 
	 * @param onlyIncluded if true the returned list will include only the inline calculated fields actually included in the select statement. All the
	 * inline calculated fields will be returned otherwise.
	 * 
	 * @return a list of InLineCalculatedSelectField. It never returns null. If there are not inline calculated fields in select 
	 * clause it will return an empty list.
	 */
	public List getInLineCalculatedSelectFields(boolean onlyIncluded) {
		List<InLineCalculatedSelectField> inLineCalculatedSelectFields;
		List<ISelectField>  selectFields;
		
		selectFields = getSelectFields(false);
		inLineCalculatedSelectFields = new ArrayList<InLineCalculatedSelectField>();
		
		for(ISelectField field : selectFields) {
			if(field.isInLineCalculatedField()) {
				if( onlyIncluded == false || (onlyIncluded == true && field.isIncluded()) ) {
					inLineCalculatedSelectFields.add( (InLineCalculatedSelectField)field);
				}
			}
		}
		
		return inLineCalculatedSelectFields;
	}
	
	public List<WhereField> getWhereFields() {
		return whereClause;
	}
	
	public List<HavingField> getHavingFields() {
		return havingClause;
	}

	public boolean isDistinctClauseEnabled() {
		return distinctClauseEnabled;
	}
	
	public void setDistinctClauseEnabled(boolean distinctClauseEnabled) {
		this.distinctClauseEnabled = distinctClauseEnabled;
	}
	
	/**
	 * Get all the fields in order by clause (i.e. SimpleSelectField + InLineCalculatedSelectedField). Note: CalculatedField cannot
	 * be used in order by clause. If some CalculateField has been erroneously added to order by clause it will be ignored
	 * by this method.
	 * 
	 * @return The list of ISelectField  included in order by clause (except CalculatedSelectField). It never returns null. If there are not
	 * fields in order by clause it will return an empty list.
	 */
	public List<ISelectField> getOrderByFields() {
		List<ISelectField> orderByFields = new ArrayList<ISelectField>();
		List<ISelectField> selectFields = new ArrayList<ISelectField>();
		
		List<SimpleSelectField> simpleSelectField = getSimpleSelectFields(false);
		selectFields.addAll(simpleSelectField);
		
		List<SimpleSelectField> inlineCalculatedSelectField = this.getInLineCalculatedSelectFields(false);
		selectFields.addAll(inlineCalculatedSelectField);
		
		for( ISelectField selectField : selectFields ) {
			if(selectField.isOrderByField()) {
				orderByFields.add(selectField);
			}
		}
		
		return orderByFields;
	}
	
	
	public List<ISelectField> getGroupByFields() {
		List<ISelectField> groupByFields = new ArrayList();
		Iterator it = this.getSimpleSelectFields(false).iterator();
		while( it.hasNext() ) {
			SimpleSelectField selectField = (SimpleSelectField)it.next();
			if(selectField.isGroupByField()) {
				groupByFields.add(selectField);
			}
		}
		
		Iterator<InLineCalculatedSelectField> it2 = this.getInLineCalculatedSelectFields(false).iterator();
		while( it2.hasNext() ) {
			InLineCalculatedSelectField selectField = (InLineCalculatedSelectField)it2.next();
			if(selectField.isGroupByField()) {
				groupByFields.add(selectField);
			}
		}
		
		return groupByFields;
	}
	
	
	public ExpressionNode getWhereClauseStructure() {
		return whereClauseStructure;
	}

	public void setWhereClauseStructure(ExpressionNode whereClauseStructure) {
		this.whereClauseStructure = whereClauseStructure;
	}
	
	/*
	 * true iff it is an expression built using the client side expression wizard
	 */
	public boolean isNestedExpression() {
		return nestedExpression;
	}

	public void setNestedExpression(boolean nestedExpression) {
		this.nestedExpression = nestedExpression;
	}
	
	
	public Query getParentQuery() {
		return parentQuery;
	}

	public void setParentQuery(Query parentQuery) {
		this.parentQuery = parentQuery;
	}
	
	public boolean hasParentQuery() {
		return getParentQuery() != null;
	}
	
	public void addSubquery(Query subquery) {
		subqueries.put(subquery.getId(), subquery);
		subquery.setParentQuery(this);
	}
	
	public Query getSubquery(String id) {
		return (Query)subqueries.get(id);
	}
	
	public Set getSubqueryIds() {
		return new HashSet(subqueries.keySet());
	}
	
	public Query removeSubquery(String id) {
		Query subquery = (Query)subqueries.remove(id);
		if(subquery != null) subquery.setParentQuery(null);
		return subquery;
	}

	public void clearSelectedFields(){
		if(selectFields!=null){
			selectFields.clear();
		}
	}
	
	public void clearWhereFields(){
		if(whereClause!=null){
			whereClause.clear();
		}
		if(whereFieldMap!=null){
			whereFieldMap.clear();
		}
		whereClauseStructure = null;
	}
	
	public void clearHavingFields(){
		if(havingClause!=null){
			havingClause.clear();
		}
		if(havingFieldMap!=null){
			havingFieldMap.clear();
		}
	}

}
