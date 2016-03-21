/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.qbe.sql.statement.sql;

import it.eng.qbe.jpa.statement.jpa.JPQLStatementWhereClause;
import it.eng.qbe.runtime.query.Query;
import it.eng.qbe.runtime.statement.AbstractStatementWhereClause;
import it.eng.qbe.runtime.statement.IConditionalOperator;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */

public class SQLStatementWhereClause extends AbstractStatementWhereClause {

	public static final String WHERE = "WHERE";

	public static transient Logger logger = Logger.getLogger(JPQLStatementWhereClause.class);

	public static String build(SQLStatement parentStatement, Query query, Map<String, Map<String, String>> entityAliasesMaps) {
		SQLStatementWhereClause clause = new SQLStatementWhereClause(parentStatement);
		return clause.buildClause(query, entityAliasesMaps);
	}

	protected SQLStatementWhereClause(SQLStatement statement) {
		parentStatement = statement;
	}

	@Override
	public IConditionalOperator getOperator(String operator) {
		return (IConditionalOperator) SQLStatementConditionalOperators.getOperator(operator);
	}

}
