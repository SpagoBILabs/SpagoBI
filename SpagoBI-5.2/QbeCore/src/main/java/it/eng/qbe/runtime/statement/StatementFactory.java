/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.runtime.statement;

import it.eng.qbe.dataset.datasource.dataset.DataSetDataSource;
import it.eng.qbe.hibernate.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.hibernate.statement.hibernate.HQLStatement;
import it.eng.qbe.jpa.datasource.jpa.JPADataSource;
import it.eng.qbe.jpa.statement.jpa.JPQLStatement;
import it.eng.qbe.runtime.datasource.IDataSource;
import it.eng.qbe.runtime.query.Query;

import java.lang.reflect.Constructor;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class StatementFactory {

	public static IStatement createStatement(IDataSource dataSource, Query query) {
		IStatement statement;

		statement = null;

		if (dataSource instanceof IHibernateDataSource) {
			statement = new HQLStatement(dataSource, query);
		} else if (dataSource instanceof JPADataSource) {
			statement = new JPQLStatement((JPADataSource) dataSource, query);
		} else if (dataSource instanceof DataSetDataSource) {
			DataSetDataSource ds = (DataSetDataSource) dataSource;
			Constructor c = null;
			Object object = null;
			try {
				c = ds.getStatementType().getConstructor(IDataSource.class, Query.class);
				object = c.newInstance(dataSource, query);
				statement = (IStatement) object;
				// statement = new SQLStatement((DataSetDataSource)dataSource, query);
			} catch (Exception e) {
				throw new RuntimeException("Impossible to create statement from a datasource of type DataSetDataSource [" + ds.getStatementType() + "]");
			}

		} else {
			throw new RuntimeException("Impossible to create statement from a datasource of type [" + dataSource.getClass().getName() + "]");
		}

		return statement;
	}
}
