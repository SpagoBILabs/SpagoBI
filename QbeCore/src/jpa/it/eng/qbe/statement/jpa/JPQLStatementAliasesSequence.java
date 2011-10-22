/**
 * 
 */
package it.eng.qbe.statement.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementAliasesSequence {
	
	// one map of entity aliases for each queries (master query + subqueries)
	// each map is indexed by the query id
	Map<String, Map<String, String>> entityAliasesMaps;
	
	
	protected JPQLStatementAliasesSequence() {
		entityAliasesMaps = new HashMap<String, Map<String, String>>();
	}
	
	public void clear() {
		entityAliasesMaps = new HashMap<String, Map<String, String>>();
	}
	
	public String getNextAlias() {
		int aliasesCount = 0;
		Iterator it = entityAliasesMaps.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			Map entityAliases = (Map)entityAliasesMaps.get(key);
			aliasesCount += entityAliases.keySet().size();
		}
		
		return "t_" + aliasesCount;
	}
}
