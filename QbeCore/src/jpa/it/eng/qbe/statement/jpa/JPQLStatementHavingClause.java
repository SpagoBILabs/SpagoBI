/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.jpa.JPQLStatementConditionalOperators.IConditionalOperator;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementHavingClause extends JPQLStatementFilteringClause {
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementHavingClause.class);
	
	protected JPQLStatementHavingClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	protected String buildHavingClause(Query query, Map entityAliasesMaps) {
		
		StringBuffer buffer = new StringBuffer();
		
		if( query.getHavingFields().size() > 0) {
			buffer.append("HAVING ");
			Iterator it = query.getHavingFields().iterator();
			while (it.hasNext()) {
				HavingField field = (HavingField) it.next();
								
				if(field.getLeftOperand().values[0].contains("expression")){
					IConditionalOperator conditionalOperator = null;
					conditionalOperator = (IConditionalOperator)JPQLStatementConditionalOperators.getOperator( field.getOperator() );
					Assert.assertNotNull(conditionalOperator, "Unsopported operator " + field.getOperator() + " used in query definition");

					String havingClauseElement =  buildInLineCalculatedFieldClause(field.getOperator(), field.getLeftOperand(), field.isPromptable(), field.getRightOperand(), query, entityAliasesMaps, conditionalOperator);
					buffer.append(havingClauseElement);
				}else{
						buffer.append( buildHavingClauseElement(field, query, entityAliasesMaps) );
				}
				
				if (it.hasNext()) {
					buffer.append(" " + field.getBooleanConnector() + " ");
				}
			}
		}
		
		return buffer.toString().trim();
	}
	
	private String buildHavingClauseElement(HavingField havingField, Query query, Map entityAliasesMaps) {
		
		String havingClauseElement;
		String[] leftOperandElements;
		String[] rightOperandElements;
				
		logger.debug("IN");
		
		try {
			IConditionalOperator conditionalOperator = null;
			conditionalOperator = (IConditionalOperator)JPQLStatementConditionalOperators.getOperator( havingField.getOperator() );
			Assert.assertNotNull(conditionalOperator, "Unsopported operator " + havingField.getOperator() + " used in query definition");
			
			leftOperandElements = buildOperand(havingField.getLeftOperand(), query, entityAliasesMaps);
			
			if (parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getRightOperand().type) 
					&& havingField.isPromptable()) {
				// get last value first (the last value edited by the user)
				rightOperandElements = havingField.getRightOperand().lastValues;
			} else {
				rightOperandElements = buildOperand(havingField.getRightOperand(), query, entityAliasesMaps);
			}
			
			if (parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getLeftOperand().type) )  {
				leftOperandElements = getTypeBoundedStaticOperand(havingField.getRightOperand(), havingField.getOperator(), leftOperandElements);
			}
			
			if (parentStatement.OPERAND_TYPE_STATIC.equalsIgnoreCase(havingField.getRightOperand().type) )  {
				rightOperandElements = getTypeBoundedStaticOperand(havingField.getLeftOperand(), havingField.getOperator(), rightOperandElements);
			}
			
			havingClauseElement = conditionalOperator.apply(leftOperandElements[0], rightOperandElements);
			logger.debug("Having clause element value [" + havingClauseElement + "]");
		} finally {
			logger.debug("OUT");
		}
		
		
		return  havingClauseElement;
	}
}
