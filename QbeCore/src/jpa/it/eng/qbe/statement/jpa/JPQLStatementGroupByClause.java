/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.DataMartSelectField;
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
public class JPQLStatementGroupByClause  extends JPQLStatementClause {
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementGroupByClause.class);
	
	protected JPQLStatementGroupByClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	protected String buildGroupByClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer = new StringBuffer();
		List groupByFields = query.getGroupByFields();
		String fieldName; 
		if(groupByFields == null ||groupByFields.size() == 0) {
			return "";
		}
		
		buffer.append("GROUP BY");
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
		
		Iterator<AbstractSelectField> it = groupByFields.iterator();
		while( it.hasNext() ) {
			AbstractSelectField abstractSelectedField = it.next();
			
			if(abstractSelectedField.isInLineCalculatedField()){
				InLineCalculatedSelectField icf = (InLineCalculatedSelectField)abstractSelectedField;
				fieldName = parseInLinecalculatedField(icf.getExpression(), query, entityAliasesMaps);
			}else{
			
				DataMartSelectField groupByField = (DataMartSelectField)abstractSelectedField;
				IModelField datamartField = parentStatement.getDataSource().getModelStructure().getField(groupByField.getUniqueName());
				
						
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
				fieldName = entityAlias + "." +queryName;
			}
			buffer.append(" " + fieldName);
			if( it.hasNext() ) {
				buffer.append(",");
			}
		}
		
		return buffer.toString().trim();
	}
	
}
