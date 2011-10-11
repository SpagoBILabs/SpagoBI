/**
 * 
 */
package it.eng.qbe.statement.jpa;

import it.eng.qbe.datasource.jpa.IJpaDataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelField;
import it.eng.qbe.query.AbstractSelectField;
import it.eng.qbe.query.SimpleSelectField;
import it.eng.qbe.query.InLineCalculatedSelectField;
import it.eng.qbe.query.Query;
import it.eng.spagobi.utilities.objects.Couple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class JPQLStatementFromClause extends JPQLStatementClause {
	
	public static final String FROM = "FROM";
	
	public static transient Logger logger = Logger.getLogger(JPQLStatementFromClause.class);
	
	protected JPQLStatementFromClause(JPQLStatement statement) {
		parentStatement = statement;
	}
	
	protected String buildFromClause(Query query, Map entityAliasesMaps) {
		StringBuffer buffer;
		
		
		logger.debug("IN");
		buffer = new StringBuffer();
		try {
			Map entityAliases = (Map)entityAliasesMaps.get(query.getId());
			
			
			if(entityAliases == null || entityAliases.keySet().size() == 0) {
				return "";
			}
			
			buffer.append(" " + FROM + " ");
			
			
			// outer join are not supported by jpa ?? check it! 
			// so this method is expected to return always an empty string
			//buffer.append( buildJoinClause(query, entityAliases) );
			
			Iterator it = entityAliases.keySet().iterator();
			while( it.hasNext() ) {
				String entityUniqueName = (String)it.next();
				logger.debug("entity [" + entityUniqueName +"]");
				
				String entityAlias = (String)entityAliases.get(entityUniqueName);
				logger.debug("entity alias [" + entityAlias +"]");
				
				IModelEntity datamartEntity =  parentStatement.getDataSource().getModelStructure().getEntity(entityUniqueName);
				
				
				String fromClauseElement = datamartEntity.getName() + " " + entityAlias;
				logger.debug("from clause element [" + fromClauseElement +"]");
				
				buffer.append(" " + fromClauseElement);
				if( it.hasNext() ) {
					buffer.append(",");
				}
			}
		} finally {
			logger.debug("OUT");
		}
		
		return buffer.toString().trim();
	}
	
	
}
