/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementGroupByClause  extends AbstractJPQLStatementClause {
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementGroupByClause.class);
	
	public static String build(JPQLStatement parentStatement, Query query, Map<String, Map<String, String>> entityAliasesMaps){
		JPQLStatementGroupByClause clause = new JPQLStatementGroupByClause(parentStatement);
		return clause.buildClause(query, entityAliasesMaps);
	}
	
	protected JPQLStatementGroupByClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	public String buildClause(Query query, Map<String, Map<String, String>> entityAliasesMaps) {
		StringBuffer buffer;	
		String fieldName;
		
		buffer = new StringBuffer();
		
		List<ISelectField> groupByFields = query.getGroupByFields();
		if(groupByFields.size() == 0) return buffer.toString();
		
		buffer.append(JPQLStatementConstants.STMT_KEYWORD_GROUP_BY);
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		
		String fieldSeparator = "";
		
		for( ISelectField groupByField : groupByFields ) {
			Assert.assertTrue(groupByField.isGroupByField(), "Field [" + groupByField.getAlias() +"] is not an groupBy filed");
			
			buffer.append(fieldSeparator);
			
			fieldName = null;			
			if(groupByField.isInLineCalculatedField()){
				InLineCalculatedSelectField inlineCalculatedField = (InLineCalculatedSelectField)groupByField;
				fieldName = parseInLinecalculatedField(inlineCalculatedField.getExpression(), inlineCalculatedField.getSlots(), query, entityAliasesMaps);
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
				fieldName = entityAlias + "." + queryName;
			} else {
				// TODO throw an exception here
			}
			
			buffer.append(" " + fieldName);
			
			fieldSeparator = ", ";
			
		}
		
		return buffer.toString().trim();
	}
	
}
