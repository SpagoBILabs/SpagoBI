/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package it.eng.qbe.statement;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.List;
import java.util.Map;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public abstract class AbstractStatementGroupByClause extends AbstractStatementClause {
	
	public static final String GROUP_BY = "GROUP BY";
	
	public String buildClause(Query query, Map<String, Map<String, String>> entityAliasesMaps) {
		StringBuffer buffer;	
		String fieldName;
		
		buffer = new StringBuffer();
		
		List<ISelectField> groupByFields = query.getGroupByFields();
		if(groupByFields.size() == 0) return buffer.toString();
		
		buffer.append(GROUP_BY);
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		
		String fieldSeparator = "";
		
		for( ISelectField groupByField : groupByFields ) {
			Assert.assertTrue(groupByField.isGroupByField(), "Field [" + groupByField.getAlias() +"] is not an groupBy filed");
			
			buffer.append(fieldSeparator);
			
			fieldName = null;			
			if(groupByField.isInLineCalculatedField()){
				InLineCalculatedSelectField inlineCalculatedField = (InLineCalculatedSelectField)groupByField;
				fieldName = parseInLinecalculatedField(inlineCalculatedField, inlineCalculatedField.getSlots(), query, entityAliasesMaps);
			} else if(groupByField.isSimpleField()){			
				SimpleSelectField simpleField = (SimpleSelectField)groupByField;
				IModelField datamartField = parentStatement.getDataSource().getModelStructure().getField(simpleField.getUniqueName());
				
						
				Couple queryNameAndRoot = datamartField.getQueryName();
				IModelEntity root;
				String queryName = (String) queryNameAndRoot.getFirst();
				logger.debug("select field query name [" + queryName + "]");
				
				if(queryNameAndRoot.getSecond()!=null){
					root = (IModelEntity)queryNameAndRoot.getSecond(); 	
				}else{
					root = datamartField.getParent().getRoot(); 	
				}
				
				
				if(!entityAliases.containsKey(root.getUniqueName())) {
					entityAliases.put(root.getUniqueName(), parentStatement.getNextAlias(entityAliasesMaps));
				}
				String entityAlias = (String)entityAliases.get( root.getUniqueName() );
				fieldName =  parentStatement.getFieldAlias(entityAlias, queryName);
			} else {
				// TODO throw an exception here
			}
			
			buffer.append(" " + fieldName);
			
			fieldSeparator = ", ";
			
		}
		
		return buffer.toString().trim();
	}
}