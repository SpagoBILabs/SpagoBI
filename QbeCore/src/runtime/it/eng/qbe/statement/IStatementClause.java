/**
 * 
 */
package it.eng.qbe.statement;

import it.eng.qbe.query.Query;

import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface IStatementClause {
	String buildClause(Query query, Map<String, Map<String, String>> entityAliasesMaps);
}
