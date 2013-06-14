package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.JDBCDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.JDBCStandardDataReader;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.dataset.utils.DatasetMetadataParser;
import it.eng.spagobi.tools.datasource.bo.DataSourceFactory;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.json.JSONUtils;
import it.eng.spagobi.utilities.sql.SQLStatementConditionalOperators;
import it.eng.spagobi.utilities.sql.SQLStatementConditionalOperators.IConditionalOperator;
import it.eng.spagobi.utilities.sql.SqlUtils;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public abstract class AbstractJDBCDataset extends ConfigurableDataSet {
	
	public static String DS_TYPE = "SbiQueryDataSet";
	private static final String QUERY = "query";
	private static final String QUERY_SCRIPT = "queryScript";
	private static final String QUERY_SCRIPT_LANGUAGE = "queryScriptLanguage";
	private static final String DATA_SOURCE = "dataSource";
	
	private static transient Logger logger = Logger.getLogger(AbstractJDBCDataset.class);
    
	
	/**
     * Instantiates a new empty JDBC data set.
     */
    public AbstractJDBCDataset() {
		super();
		setDataProxy( new JDBCDataProxy() );
		setDataReader( new JDBCStandardDataReader() );
		addBehaviour( new QuerableBehaviour(this) );
	}
    
    // cannibalization :D
    public AbstractJDBCDataset(JDBCDataSet jdbcDataset) {
    	this(jdbcDataset.toSpagoBiDataSet());
    }
    
    public AbstractJDBCDataset(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		
		setDataProxy( new JDBCDataProxy() );
		setDataReader( new JDBCStandardDataReader() );
		
		try{
			setDataSource( DataSourceFactory.getDataSource( dataSetConfig.getDataSource() ) );
		} catch (Exception e) {
			throw new RuntimeException("Missing right exstension", e);
		}
		try{
    		String config = JSONUtils.escapeJsonString(dataSetConfig.getConfiguration());		
    		JSONObject jsonConf  = ObjectUtils.toJSONObject(config);
			setQuery((jsonConf.get(QUERY) != null) ? jsonConf.get(QUERY).toString() : "");
			setQueryScript((jsonConf.get(QUERY_SCRIPT) != null) ? jsonConf.get(QUERY_SCRIPT).toString() : "");
			setQueryScriptLanguage((jsonConf.get(QUERY_SCRIPT_LANGUAGE) != null) ? jsonConf.get(QUERY_SCRIPT_LANGUAGE).toString() : "");
		} catch (Exception e) {
			logger.error("Error while defining dataset configuration. Error:", e);
			throw new SpagoBIRuntimeException("Error while defining dataset configuration", e);	
		}
		
		addBehaviour( new QuerableBehaviour(this) );
	}
    
    
    
    /**
     * Redefined for set schema name
     * 
     */
	public void setUserProfileAttributes(Map userProfile)  {
		this.userProfileParameters = userProfile;
		if (getDataSource().checkIsMultiSchema()){
			String schema=null;
			try {
				schema = (String)userProfile.get(getDataSource().getSchemaAttribute());
				((JDBCDataProxy)dataProxy).setSchema(schema);
				logger.debug("Set UP Schema="+schema);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("An error occurred while reading schema name from user profile", t);	
			}		
		}
	}
	

	
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet toReturn;
		JDBCDataProxy dataProxy;
		
		toReturn = super.toSpagoBiDataSet();
		
		toReturn.setType( DS_TYPE );
			
		dataProxy = (JDBCDataProxy)this.getDataProxy();
		toReturn.setDataSource(dataProxy.getDataSource().toSpagoBiDataSource());
		
		try {
			JSONObject jsonConf  = new JSONObject();
			jsonConf.put(QUERY, (getQuery() == null) ? "" : getQuery());
			jsonConf.put(QUERY_SCRIPT, (getQueryScript() == null) ? "" : getQueryScript());
			jsonConf.put(QUERY_SCRIPT_LANGUAGE, (getQueryScriptLanguage() == null) ? "" : getQueryScriptLanguage());
			toReturn.setConfiguration(jsonConf.toString());
		} catch (Exception e) {
			logger.error("Error while defining dataset configuration. Error:", e);
			throw new SpagoBIRuntimeException("Error while defining dataset configuration. Error:", e);
		}
		
		return toReturn;
	}

	
	public JDBCDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new JDBCDataProxy() );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  JDBCDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in JDBCDataSet");
		
		return (JDBCDataProxy)dataProxy;
	}
	
	public void setDataSource(IDataSource dataSource) {
		getDataProxy().setDataSource(dataSource);
	}
	
	public IDataSource getDataSource() {
		return getDataProxy().getDataSource();
	}
	
	@Override
	public IMetaData getMetadata() {
		IMetaData metadata = null;
		try {
			DatasetMetadataParser dsp = new DatasetMetadataParser();
			metadata =  dsp.xmlToMetadata( getDsMetadata() );
		} catch (Exception e) {
			logger.error("Error loading the metadata",e);
			throw new SpagoBIEngineRuntimeException("Error loading the metadata",e);
		}
		return metadata;
	}

	public void setMetadata(IMetaData metadata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDataStore test() {
		loadData();
		return getDataStore();
	}

	/**
	 * Returns the signature of the dataset.
	 * The signature composed by query signature (i.e. the SQL query where parameters and profile attributes are already replaced) 
	 * plus the datasource signature (i.e. the JNDI url (with eventual schema name) or the JDBC connection string + JDBC user)
	 * @return the signature of the dataset
	 */
	@Override
	public String getSignature() {
		logger.debug("IN");
		String toReturn = null;
		
		// get the query with parameters and profile attributes substituted by their values
		QuerableBehaviour querableBehaviour = (QuerableBehaviour) this.getBehaviour(QuerableBehaviour.class.getName()) ;
		String statement = querableBehaviour.getStatement();
		
		// puts also the data-source signature
		String datasourceSignature = null;
		IDataSource dataSource = this.getDataSource();
		if (dataSource.getJndi() != null && !dataSource.getJndi().trim().equals("")) {
			String schema = dataSource.checkIsMultiSchema() ? this.getDataProxy().getSchema() : null;
			datasourceSignature = dataSource.getJndi() + (schema != null ? schema : "");
		} else {
			datasourceSignature = dataSource.getUrlConnection() + "_" + dataSource.getUser();
		}
		
		toReturn = statement + "_" + datasourceSignature;
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName, IDataSource dataSource) {
		IDataSource datasetDataSource = getDataSource();
		if (datasetDataSource.getLabel().equals(dataSource.getLabel())) {
			logger.debug("Specified datasource is the dataset's datasource; using CREATE TABLE AS SELECT tecnique");
			try{
				List<String> fields = this.getFieldsList();
				return TemporaryTableManager.createTable(fields, (String)query, tableName, getDataSource());
			} catch (Exception e) {
				logger.error("Error peristing the temporary table", e);
				throw new SpagoBIEngineRuntimeException("Error peristing the temporary table", e);
			}
		} else {
			logger.debug("Specified datasource is NOT the dataset's datasource");
			return super.persist(tableName, dataSource);
		}
	}
	
	@Override
	public IDataStore getDomainValues(String fieldName, Integer start, Integer limit, IDataStoreFilter filter) {
		IDataStore toReturn = null;
		try{
			String tableName = this.getTemporaryTableName();
			logger.debug("Temporary table name : [" + tableName + "]");
			if (tableName == null) {
				logger.error("Temporary table name not set, cannot proceed!!");
				throw new SpagoBIEngineRuntimeException("Temporary table name not set");
			}
			IDataSetTableDescriptor tableDescriptor = null;
			if (((String)query).equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
				// signature matches: no need to create a TemporaryTable
				tableDescriptor = TemporaryTableManager.getLastDataSetTableDescriptor(tableName);
			} else {
//				List<String> fields = getDataSetSelectedFields((String)query);
				tableDescriptor = TemporaryTableManager.createTable(null, ((String)query), tableName, getDataSource());
			}
			String filterColumnName = tableDescriptor.getColumnName(fieldName);
			StringBuffer buffer = new StringBuffer("Select DISTINCT(" + encapsulateColumnName(filterColumnName, getDataSource()) + ") FROM " + tableName);
			manageFilterOnDomainValues(buffer, fieldName, tableDescriptor, filter);
			String sqlStatement = buffer.toString();
			toReturn = TemporaryTableManager.queryTemporaryTable(sqlStatement, getDataSource(), start, limit);
	
		} catch (Exception e) {
			logger.error("Error loading the domain values for the field " + fieldName, e);
			throw new SpagoBIEngineRuntimeException("Error loading the domain values for the field "+fieldName, e);
		}
		return toReturn;
	}
	
	private void manageFilterOnDomainValues(StringBuffer buffer,
			String fieldName, IDataSetTableDescriptor tableDescriptor, IDataStoreFilter filter) {
		if (filter != null) {
			String filterColumnName = tableDescriptor.getColumnName(fieldName);
			if (filterColumnName == null) {
				throw new SpagoBIRuntimeException("Field name [" + fieldName + "] not found");
			}
			String columnName = tableDescriptor.getColumnName(fieldName);
			Class clazz = tableDescriptor.getColumnType(fieldName);
			String value = getFilterValue(filter.getValue(), clazz);
			IConditionalOperator conditionalOperator = SQLStatementConditionalOperators.getOperator(filter.getOperator());
			String temp = conditionalOperator.apply(encapsulateColumnName(columnName, getDataSource()), new String[] { value });
			buffer.append(" WHERE " + temp);
		}
	}
	
	private String getFilterValue(String value, Class clazz) {
		String toReturn = null;
		if ( String.class.isAssignableFrom(clazz) ) {
			value = StringUtils.escapeQuotes(value);
			toReturn = StringUtils.bound(value, "'");
		} else if ( Number.class.isAssignableFrom(clazz) ) {
			toReturn = value;
		} else if ( Boolean.class.isAssignableFrom(clazz) ) {
			toReturn = value;
		} else {
			// TODO manage other types, such as date and timestamp
			throw new SpagoBIRuntimeException("Unsupported operation: cannot filter on a fild type " + clazz.getName());
		}
		return toReturn;
	}

	private List<String> getDataSetSelectedFields(String query) {
		List<String> toReturn= new ArrayList<String>();
		String alias = null;
		List<String[]> selectFileds = SqlUtils.getSelectFields(query, true);
		for(int i=0; i<selectFileds.size(); i++){
			alias = null;
			alias =selectFileds.get(i)[1];
			if(alias==null){
				alias = selectFileds.get(i)[0];
			}
			toReturn.add(alias);
		}
		return toReturn;
	}

	private String getUserId() {
		Map userProfileAttrs = getUserProfileAttributes();
		String userId = null;
		if (userProfileAttrs != null) {
			userId = (String) userProfileAttrs.get(SsoServiceInterface.USER_ID);
		}
		return userId;
	}
	
	public static String encapsulateColumnName (String columnName, IDataSource dataSource) {
		logger.debug("IN");
		String toReturn = null;
		if (columnName != null) {
			String aliasDelimiter = TemporaryTableManager.getAliasDelimiter(dataSource);
			logger.debug("Alias delimiter is [" + aliasDelimiter + "]");
			toReturn = aliasDelimiter + columnName + aliasDelimiter;
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
		
}

