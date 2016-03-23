/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.hibernate.datasource.hibernate;

import it.eng.qbe.runtime.datasource.IDataSource;
import it.eng.qbe.runtime.datasource.IDriver;
import it.eng.qbe.runtime.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.runtime.datasource.naming.SimpleDataSourceNamingStrategy;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class HibernateDriver implements IDriver {

	protected boolean dataSourceCacheEnabled;
	protected int openedDataSource;
	protected int maxDataSource;

	public static final String DRIVER_ID = "hibernate";
	protected static final Map<String, IDataSource> cache = new HashMap<String, IDataSource>();
	protected static final SimpleDataSourceNamingStrategy namingStrategy = new SimpleDataSourceNamingStrategy();

	public HibernateDriver() {
		dataSourceCacheEnabled = true;
		openedDataSource = 0;
		maxDataSource = -1;
	}

	@Override
	public String getName() {
		return DRIVER_ID;
	}

	@Override
	public IDataSource getDataSource(IDataSourceConfiguration configuration) {
		IDataSource dataSource;
		String dataSourceName;

		if (maxDataSource > 0 && openedDataSource == maxDataSource) {
			throw new SpagoBIRuntimeException("Maximum  number of open data source reached");
		}

		dataSource = null;
		dataSourceName = namingStrategy.getDataSourceName(configuration);
		if (dataSourceCacheEnabled) {
			dataSource = cache.containsKey(dataSourceName) ? cache.get(dataSourceName) : new HibernateDataSource(dataSourceName, configuration);
			cache.put(dataSourceName, dataSource);
		} else {
			dataSource = new HibernateDataSource(dataSourceName, configuration);
		}

		openedDataSource++;

		return dataSource;
	}

	@Override
	public void setDataSourceCacheEnabled(boolean enabled) {
		dataSourceCacheEnabled = enabled;
	}

	@Override
	public boolean isDataSourceCacheEnabled() {
		return dataSourceCacheEnabled;
	}

	@Override
	public void setMaxDataSource(int n) {
		maxDataSource = n;
	}

	@Override
	public boolean acceptDataSourceConfiguration() {
		// TODO Auto-generated method stub
		return true;
	}

}
