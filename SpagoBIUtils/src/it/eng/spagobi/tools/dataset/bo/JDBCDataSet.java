/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/


package it.eng.spagobi.tools.dataset.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.sql.SqlUtils;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

/**
 * @authors
 * Angelo Bernabei
 *         angelo.bernabei@eng.it
 * Giulio Gavardi
 *     giulio.gavardi@eng.it
 *  Andrea Gioia
 *         andrea.gioia@eng.it
 *  Alberto Ghedin
 *         alberto.ghedin@eng.it
 */
public class JDBCDataSet extends ConfigurableDataSet {
	
public static String DS_TYPE = "SbiQueryDataSet";
	
	private static transient Logger logger = Logger.getLogger(JDBCDataSet.class);
    
	
	/**
     * Instantiates a new empty JDBC data set.
     */
    public JDBCDataSet() {
		super();
		setDataProxy( new JDBCDataProxy() );
		setDataReader( new JDBCStandardDataReader() );
		addBehaviour( new QuerableBehaviour(this) );
	}
    
    // cannibalization :D
    public JDBCDataSet(JDBCDataSet jdbcDataset) {
    	this(jdbcDataset.toSpagoBiDataSet());
    }
    
    public JDBCDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		
		setDataProxy( new JDBCDataProxy() );
		setDataReader( new JDBCStandardDataReader() );
		
		try{
			setDataSource( DataSourceFactory.getDataSource( dataSetConfig.getDataSource() ) );
		} catch (Exception e) {
			throw new RuntimeException("Missing right exstension", e);
		}
	
		setQuery( dataSetConfig.getQuery() );
		
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
		SpagoBiDataSet sbd;
		JDBCDataProxy dataProxy;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );
			
		dataProxy = (JDBCDataProxy)this.getDataProxy();
		sbd.setDataSource(dataProxy.getDataSource().toSpagoBiDataSource());
		if(query!=null){
		sbd.setQuery(query.toString());
		}
		return sbd;
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
			metadata =  dsp.xmlToMetadata(dsMetadata);
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

	@Override
	public String getSignature() {
		return (String)query;
	}

	@Override
	public IDataSetTableDescriptor persist(String tableName, Connection connection) {
		List<String> fields = getDataSetSelectedFields((String)query);
		try{
			return TemporaryTableManager.createTable(fields, (String)query, tableName, getDataSource());
		} catch (Exception e) {
			logger.error("Error peristing the temporary table", e);
			throw new SpagoBIEngineRuntimeException("Error peristing the temporary table", e);
		}
	}
	
	@Override
	public IDataStore getDomainValues(String fieldName, Integer start, Integer limit, IDataStoreFilter filter) {
		IDataStore toReturn = null;
		try{
			String userId = getUserId();
			String tableName = TemporaryTableManager.getTableName(userId);
			IDataSetTableDescriptor tableDescriptor = null;
			if (((String)query).equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
				// signature matches: no need to create a TemporaryTable
				tableDescriptor = TemporaryTableManager.getLastDataSetTableDescriptor(tableName);
			} else {
				List<String> fields = getDataSetSelectedFields((String)query);
				tableDescriptor = TemporaryTableManager.createTable(fields, ((String)query), tableName, getDataSource());
			}
			String filterColumnName = tableDescriptor.getColumnName(fieldName);
			StringBuffer buffer = new StringBuffer("Select DISTINCT(" + filterColumnName + ") FROM " + tableName);
			String sqlStatement = buffer.toString();
			toReturn = TemporaryTableManager.queryTemporaryTable(sqlStatement, getDataSource(), start, limit);
	
		} catch (Exception e) {
			logger.error("Error loading the domain values for the field " + fieldName, e);
			throw new SpagoBIEngineRuntimeException("Error loading the domain values for the field "+fieldName, e);
		}
		return toReturn;
	}

	/**
	 * @param query
	 * @return
	 */
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
	
	
		
}
