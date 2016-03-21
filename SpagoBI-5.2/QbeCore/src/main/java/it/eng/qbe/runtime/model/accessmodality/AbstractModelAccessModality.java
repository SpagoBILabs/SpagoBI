/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.runtime.model.accessmodality;

import it.eng.qbe.runtime.datasource.IDataSource;
import it.eng.qbe.runtime.model.structure.IModelEntity;
import it.eng.qbe.runtime.model.structure.IModelField;
import it.eng.qbe.runtime.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class AbstractModelAccessModality implements IModelAccessModality {

	Boolean recursiveFiltering = Boolean.TRUE;

	public static final String ATTR_RECURSIVE_FILTERING = "recursiveFiltering";

	@Override
	public boolean isEntityAccessible(IModelEntity entity) {
		return true;
	}

	@Override
	public boolean isFieldAccessible(IModelField field) {
		return true;
	}

	@Override
	public List getEntityFilterConditions(String entityName) {
		return new ArrayList();
	}

	@Override
	public List getEntityFilterConditions(String entityName, Properties parameters) {
		return new ArrayList();
	}

	@Override
	public Boolean getRecursiveFiltering() {
		return recursiveFiltering;
	}

	@Override
	public void setRecursiveFiltering(Boolean recursiveFiltering) {
		this.recursiveFiltering = recursiveFiltering;
	}

	@Override
	public Query getFilteredStatement(Query query, IDataSource iDataSource, Map userProfileAttributes) {
		return query;
	}

}
