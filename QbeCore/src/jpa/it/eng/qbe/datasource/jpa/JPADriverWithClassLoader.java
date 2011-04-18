package it.eng.qbe.datasource.jpa;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

public class JPADriverWithClassLoader extends JPADriver{

	public static final String DRIVER_ID = "jpa_with_cl";
	
	public JPADriverWithClassLoader(){
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
			throw new SpagoBIRuntimeException("Maximum  number of open data sources reached");
		}
		
		dataSource = null;
		dataSourceName = namingStrategy.getDataSourceName(configuration);
		
		if(dataSourceCacheEnabled) {
			logger.debug("The Data source cache is enabled");
			dataSource = cache.containsKey(dataSourceName)? 
						 cache.get(dataSourceName): 
					     new JPADataSourceWithClassLoader(dataSourceName, configuration);
		} else {
			logger.debug("The Data source cache is not enabled");
			dataSource = new JPADataSourceWithClassLoader(dataSourceName, configuration);
		}
		
		openedDataSource++;
		
		return dataSource;
	}
	
}
