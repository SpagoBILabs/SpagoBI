/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.AbstractSelectField;
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
public class JPQLStatementOrderByClause  extends JPQLStatementClause {
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementOrderByClause.class);
	
	protected JPQLStatementOrderByClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	protected String buildOrderByClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		Iterator it;
		SimpleSelectField selectField;
		
		it = getOrderByFields(query).iterator();		
		if(!it.hasNext()) {
			return "";
		}
		
		buffer = new StringBuffer();	
		buffer.append("ORDER BY");
		
		Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
					
		while( it.hasNext() ) {
			selectField = (SimpleSelectField)it.next();
			
			Assert.assertTrue(selectField.isOrderByField(), "Field [" + selectField.getUniqueName() +"] is not an orderBy filed");
			
			IModelField datamartField = parentStatement.getDataSource().getModelStructure().getField(selectField.getUniqueName());
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
			String fieldName = entityAlias + "." + queryName;
			buffer.append(" " + selectField.getFunction().apply(fieldName));
			buffer.append(" " + (selectField.isAscendingOrder()?"ASC": "DESC") );
						
			if( it.hasNext() ) {
				buffer.append(",");
			}
		}
		
		return buffer.toString().trim();
	}
	
	private List getOrderByFields(Query query) {
		List orderByFields = new ArrayList();
		Iterator it = query.getSimpleSelectFields(false).iterator();
		while( it.hasNext() ) {
			SimpleSelectField selectField = (SimpleSelectField)it.next();
			if(selectField.isOrderByField()) {
				orderByFields.add(selectField);
			}
		}
		return orderByFields;
	}
	
}
