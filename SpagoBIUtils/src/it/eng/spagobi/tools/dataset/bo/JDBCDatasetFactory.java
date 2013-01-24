package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.tools.datasource.bo.DataSourceFactory;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import org.apache.log4j.Logger;

public class JDBCDatasetFactory {
	private static transient Logger logger = Logger.getLogger(DataSourceFactory.class);
	
	public static IDataSet getJDBCDataSet( IDataSource dataSource ) {
		IDataSet dataSet = null;
				
		if (dataSource == null) {

			throw new IllegalArgumentException("datasource parameter cannot be null");
		}
		String dialect = dataSource.getHibDialectClass();
		if(dialect.contains("hbase")){
			dataSet = new JDBCHBaseDataSet();
		}else if(dialect.contains("hive")){
			dataSet = new JDBCHiveDataSet();
		}else{
			dataSet = new JDBCDataSet();
		}
		
		return dataSet;
	}
}
