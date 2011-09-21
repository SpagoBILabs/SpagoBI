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
package it.eng.qbe.dataset;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.catalogue.QueryCatalogue;
import it.eng.qbe.statement.AbstractQbeDataSet;
import it.eng.qbe.statement.QbeDatasetFactory;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.ConfigurableDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.DataSourceFactory;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class QbeDataSet extends ConfigurableDataSet {
	
public static String DS_TYPE = "SbiQbeDataSet";
	
	private static transient Logger logger = Logger.getLogger(QbeDataSet.class);
	
	protected IDataSet ds = null;
	protected String jsonQuery = null;
	protected String datamarts = null;
	protected Map attributes = null;
	protected Map params = null;
	
	protected IDataSource dataSource = null;
	
	public QbeDataSet() {}
	
    public QbeDataSet(SpagoBiDataSet dataSetConfig) {

    	this.setDatamarts(dataSetConfig.getDatamarts());
    	this.setJsonQuery(dataSetConfig.getJsonQuery());
    	
		IDataSource dataSource = DataSourceFactory.getDataSource( dataSetConfig.getDataSource() ) ;
		this.setDataSource(dataSource);
		
	}
    
    private void init() {
    	if (ds == null) {
    		it.eng.qbe.datasource.IDataSource qbeDataSource = getQbeDataSource();
    		QueryCatalogue catalogue = getCatalogue(jsonQuery, qbeDataSource);
    		Query query = catalogue.getFirstQuery();
    		
    		ds = QbeDatasetFactory.createDataSet(qbeDataSource.createStatement(query));
    		ds.setUserProfileAttributes(attributes);
    		ds.setParamsMap(params);
    		ds.setTransformerId(transformerId);
    		ds.setTransformerCd(transformerCd);
    		ds.setPivotColumnName(pivotColumnName);
    		ds.setPivotColumnValue(pivotColumnValue);
    		ds.setPivotRowName(pivotRowName);
    		ds.setNumRows(numRows);
    		ds.setDataStoreTransformer(dataSetTransformer);
    	}
    }
    
    public void loadData(int offset, int fetchSize, int maxResults) {
    	init();
    	ds.loadData(offset, fetchSize, maxResults);
    }
    
    public void loadData() {
    	init();
    	ds.loadData();
    }
    
    public void setUserProfileAttributes(Map attributes) {
    	this.attributes = attributes;
    }
    
    public void setParamsMap(Map params) {
    	this.params = params;
    }
    
    public IDataStore getDataStore() {
    	return ds.getDataStore();
    }
    
    public String getJsonQuery() {
    	return this.jsonQuery;
    }
    
    public void setJsonQuery(String jsonQuery) {
    	this.jsonQuery = jsonQuery;
    }
    
    public String getDatamarts() {
    	return this.datamarts;
    }
    public void setDatamarts(String datamarts) {
    	this.datamarts = datamarts;
    }
    
	public void setDataSource(IDataSource dataSource) {
		
		this.dataSource = dataSource;
	}
	
	public IDataSource getDataSource() {
		return this.dataSource;
	}
    
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;

		sbd = super.toSpagoBiDataSet();

		sbd.setType(DS_TYPE);
		if(getDataSource() != null) {
			sbd.setDataSource(getDataSource().toSpagoBiDataSource());
		}
		sbd.setJsonQuery(getJsonQuery());
		sbd.setDatamarts(getDatamarts());

		return sbd;
	}

	public it.eng.qbe.datasource.IDataSource getQbeDataSource() {

        Map<String, Object> dataSourceProperties = new HashMap<String, Object>();
       
        String modelName = getDatamarts();
        List<String> modelNames = new ArrayList<String>();
        modelNames.add( modelName );
   
        ConnectionDescriptor connection = new ConnectionDescriptor();
        connection.setName( modelName );
        connection.setDialect( dataSource.getHibDialectClass() );           
        connection.setJndiName( dataSource.getJndi() );           
        connection.setDriverClass( dataSource.getDriver() );           
        connection.setPassword( dataSource.getPwd() );
        connection.setUrl( dataSource.getUrlConnection() );
        connection.setUsername( dataSource.getUser() );   

        dataSourceProperties.put("connection", connection);
        dataSourceProperties.put("dblinkMap", new HashMap());

	    File modelJarFile = null;
	    List<File> modelJarFiles = new ArrayList<File>();
	    CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration();
	    compositeConfiguration.loadDataSourceProperties().putAll( dataSourceProperties);
	    
	    String resourcePath = getResourcePath();
	    modelJarFile = new File(resourcePath+File.separator+"qbe" + File.separator + "datamarts" + File.separator + modelNames.get(0)+File.separator+"datamart.jar");
	    modelJarFiles.add(modelJarFile);
	    compositeConfiguration.addSubConfiguration(new FileDataSourceConfiguration(modelNames.get(0), modelJarFile));
	
	    logger.debug("OUT: Finish to load the data source for the model names "+modelNames+"..");
	    return DriverManager.getDataSource(getDriverName(modelJarFile), compositeConfiguration);
	}
	
    /**
     * Get the driver name (hibernate or jpa). It checks if the passed jar file contains the persistence.xml
     * in the META-INF folder
     * @param jarFile a jar file with the model definition
     * @return jpa if the persistence provder is JPA o hibernate otherwise
     */
    private static String getDriverName(File jarFile){
        logger.debug("IN: Check the driver name. Looking if "+jarFile+" is a jpa jar file..");
        JarInputStream zis;
        JarEntry zipEntry;
        String dialectName = null;
        boolean isJpa = false;
           
        try {
            FileInputStream fis = new FileInputStream(jarFile);
            zis = new JarInputStream(fis);
            while((zipEntry=zis.getNextJarEntry())!=null){
                logger.debug("Zip Entry is [" + zipEntry.getName() + "]");
                if(zipEntry.getName().equals("META-INF/persistence.xml") ){
                    isJpa = true;
                    break;
                }
                zis.closeEntry();
            }
            zis.close();
            if(isJpa){
                dialectName = "jpa";
            } else{
                dialectName = "hibernate";
            }
        } catch (Throwable t) {
            logger.error("Impossible to read jar file [" + jarFile + "]",t);
            throw new SpagoBIRuntimeException("Impossible to read jar file [" + jarFile + "]", t);
        }


        logger.debug("OUT: "+jarFile+" has the dialect: "+dialectName);
        return dialectName;
    }
    
    
	public QueryCatalogue getCatalogue(String json, it.eng.qbe.datasource.IDataSource dataSource) {
		QueryCatalogue catalogue;
		JSONObject catalogueJSON;
		JSONArray queriesJSON;
		JSONObject queryJSON;
		Query query;
		
		catalogue = new QueryCatalogue();
		try {
			catalogueJSON = new JSONObject(json).getJSONObject("catalogue");
			queriesJSON = catalogueJSON.getJSONArray("queries");
		
			for(int i = 0; i < queriesJSON.length(); i++) {
				queryJSON = queriesJSON.getJSONObject(i);
				query = it.eng.qbe.query.serializer.SerializerFactory.getDeserializer("application/json").deserializeQuery(queryJSON, dataSource);
								
				catalogue.addQuery(query);
			}
		} catch (Throwable e) {
			throw new SpagoBIEngineRuntimeException("Impossible to deserialize catalogue", e);
		}
		
		return catalogue;
	}
	
	@Override
	public IDataSetTableDescriptor persist(String tableName, Connection connection) {
		return((AbstractQbeDataSet)ds).persist(tableName, connection);
	}

	@Override
	public IDataStore getDomainValues(String fieldName, Integer start, Integer limit, IDataStoreFilter filter ) {
		return ((AbstractQbeDataSet)ds).getDomainValues(fieldName, start, limit, filter);
	}
	
	@Override
	public String getSignature() {
		return ((AbstractQbeDataSet)ds).getSQLQuery();
	}
	
	
}
