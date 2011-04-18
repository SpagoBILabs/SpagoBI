package it.eng.qbe.datasource.hibernate;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

public class HibernateDriverWithClassLoader extends HibernateDriver{

	public static final String DRIVER_ID = "hibernate_with_cl";
	
	public HibernateDriverWithClassLoader(){
		super();
	}
	
	@Override
	public String getName() {
		return DRIVER_ID;
	}

	@Override
	public IDataSource getDataSource(IDataSourceConfiguration configuration) {
		IDataSource dataSource;
		String dataSourceName;
		
		if(maxDataSource > 0 && openedDataSource == maxDataSource) {
			throw new SpagoBIRuntimeException("Maximum  number of open data source reached");
		}
		
		dataSource = null;
		dataSourceName = namingStrategy.getDataSourceName(configuration);
		if(dataSourceCacheEnabled) {
			dataSource = cache.containsKey(dataSourceName)? 
						 cache.get(dataSourceName): 
					     new HibernateDataSourceWithClassLoader(dataSourceName, configuration);
		} else {
			dataSource = new HibernateDataSourceWithClassLoader(dataSourceName, configuration);
		}
		
		openedDataSource++;
		
		return dataSource;
	}
	
}
