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
public class JPQLStatementSelectClause extends JPQLStatementClause {
	
	String[] idsForQuery;
	int index;
	Map entityAliases;;
	
	
	public static final String SELECT = "SELECT";
	public static final String DISTINCT = "DISTINCT";
	 
	public static transient Logger logger = Logger.getLogger(JPQLStatementSelectClause.class);
	
	protected JPQLStatementSelectClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	protected String buildSelectClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		List selectFields;
		List allSelectFields;
		List<InLineCalculatedSelectField> selectInLineCalculatedFields = new ArrayList<InLineCalculatedSelectField>();
		AbstractSelectField selectAbstractField;
		DataMartSelectField selectField;
		InLineCalculatedSelectField selectInLineField;

		logger.debug("IN");
		
		buffer = new StringBuffer();
		try {
			selectFields = query.getSelectFields(true);
			
			if(selectFields == null ||selectFields.size() == 0) {
				return "";
			}
			
			entityAliases = (Map)entityAliasesMaps.get(query.getId());
						
			buffer.append(SELECT);		
			if (query.isDistinctClauseEnabled()) {
				buffer.append(" " + DISTINCT);
			}
			
			idsForQuery = new String[selectFields.size()-query.getCalculatedSelectFields(true).size()]; 
			index=0;
			
			Iterator it = selectFields.iterator();
			while(it.hasNext()){
				selectAbstractField = (AbstractSelectField)it.next();
										
				if(selectAbstractField.isDataMartField()){
					addDatamartField((DataMartSelectField)selectAbstractField, entityAliasesMaps); 
				}else if(selectAbstractField.isInLineCalculatedField()){
					selectInLineCalculatedFields.add((InLineCalculatedSelectField)selectAbstractField);
					index++;
				}
			}
				

			for(int k=0; k< selectInLineCalculatedFields.size(); k++){
					selectInLineField = selectInLineCalculatedFields.get(k);
					
					String expr = selectInLineField.getExpression();//.replace("\'", "");			
					expr = parseInLinecalculatedField(expr, query, entityAliasesMaps);
					expr = selectInLineField.getFunction().apply(expr);
					
					for(int y= 0; y<idsForQuery.length; y++){
						if(idsForQuery[y]==null){
							idsForQuery[y]=" " +expr;
							index = y;
							break;
						}
					}

					logger.debug("select clause element succesfully added to select clause");
			}
				
				
			for(int y= 0; y<idsForQuery.length-1; y++){
					buffer.append(idsForQuery[y]+",");
			}
			buffer.append(idsForQuery[idsForQuery.length-1]);
				
			
		
		}
		
		finally {
			logger.debug("OUT");
		}
		
		return buffer.toString().trim();
	}
	
	private void addDatamartField(DataMartSelectField selectField, Map entityAliasesMaps) {
		
		IModelField datamartField;
		String queryName;
		IModelEntity rootEntity;
		String rootEntityAlias;
		String selectClauseElement; // rootEntityAlias.queryName
	
		
		logger.debug("select field unique name [" + selectField.getUniqueName() + "]");
		
		datamartField = parentStatement.getDataSource().getModelStructure().getField(selectField.getUniqueName());
		
		Couple queryNameAndRoot = datamartField.getQueryName();
		
		queryName = (String) queryNameAndRoot.getFirst();
		logger.debug("select field query name [" + queryName + "]");
		
		if(queryNameAndRoot.getSecond()!=null){
			rootEntity = (IModelEntity)queryNameAndRoot.getSecond(); 	
		}else{
			rootEntity = datamartField.getParent().getRoot(); 	
		}
		
			
		logger.debug("select field root entity unique name [" + rootEntity.getUniqueName() + "]");
		
		rootEntityAlias = (String)entityAliases.get(rootEntity.getUniqueName());
		if(rootEntityAlias == null) {
			rootEntityAlias = parentStatement.getNextAlias(entityAliasesMaps);
			entityAliases.put(rootEntity.getUniqueName(), rootEntityAlias);
		}
		logger.debug("select field root entity alias [" + rootEntityAlias + "]");
		
		
		//selectClauseElement = rootEntityAlias + "." + queryName.substring(0,1).toLowerCase()+queryName.substring(1);
		selectClauseElement = rootEntityAlias + "." + queryName;
		logger.debug("select clause element before aggregation [" + selectClauseElement + "]");
		
		selectClauseElement = selectField.getFunction().apply(selectClauseElement);
		logger.debug("select clause element after aggregation [" + selectClauseElement + "]");
		
		
		idsForQuery[index] = " " + selectClauseElement;
		index++;
		logger.debug("select clause element succesfully added to select clause");
	}
	
}
