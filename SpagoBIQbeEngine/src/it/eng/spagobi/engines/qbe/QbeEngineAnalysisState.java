/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.qbe.catalogue.QueryCatalogue;
import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.QuerySerializerFactory;
import it.eng.spagobi.engines.qbe.analysisstateloaders.IQbeEngineAnalysisStateLoader;
import it.eng.spagobi.engines.qbe.analysisstateloaders.QbeEngineAnalysisStateLoaderFactory;
import it.eng.spagobi.utilities.engines.EngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeEngineAnalysisState extends EngineAnalysisState {
	
	// property name
	public static final String CATALOGUE = "CATALOGUE";
	public static final String DATAMART_MODEL = "DATAMART_MODEL";
	
	public static final String CURRENT_VERSION = "6";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(QbeEngineAnalysisState.class);
	
	
	
	public QbeEngineAnalysisState( DataMartModel datamartModel ) {
		super( );
		setDatamartModel( datamartModel );
	}

	public void load(byte[] rowData) throws SpagoBIEngineException {
		String str = null;
		JSONObject catalogueJSON = null;
		JSONObject rowDataJSON = null;
		String encodingFormatVersion;
		
		logger.debug("IN");

		try {
			str = new String( rowData );
			logger.debug("loading analysis state from row data [" + str + "] ...");
			
			rowDataJSON = new JSONObject(str);
			try {
				encodingFormatVersion = rowDataJSON.getString("version");
			} catch (JSONException e) {
				encodingFormatVersion = "0";
			}
			
			logger.debug("Row data encoding version  [" + encodingFormatVersion + "]");
			
			
			
			if(encodingFormatVersion.equalsIgnoreCase(CURRENT_VERSION)) {				
				catalogueJSON = rowDataJSON;
			} else {
				logger.warn("Row data encoding version [" + encodingFormatVersion + "] does not match with the current version used by the engine [" + CURRENT_VERSION + "] ");
				logger.debug("Converting from encoding version [" + encodingFormatVersion + "] to encoding version [" + CURRENT_VERSION + "]....");
				IQbeEngineAnalysisStateLoader analysisStateLoader;
				analysisStateLoader = QbeEngineAnalysisStateLoaderFactory.getInstance().getLoader(encodingFormatVersion);
				if(analysisStateLoader == null) {
					throw new SpagoBIEngineException("Unable to load data stored in format [" + encodingFormatVersion + "] ");
				}
				catalogueJSON = (JSONObject)analysisStateLoader.load(str);
				logger.debug("Encoding conversion has been executed succesfully");
			}
			
			catalogueJSON = catalogueJSON.getJSONObject("catalogue");
			setProperty( CATALOGUE,  catalogueJSON);
			logger.debug("analysis state loaded succsfully from row data");
		} catch (JSONException e) {
			throw new SpagoBIEngineException("Impossible to load analysis state from raw data", e);
		} finally {
			logger.debug("OUT");
		}
	}

	public byte[] store() throws SpagoBIEngineException {
		JSONObject catalogueJSON = null;
		JSONObject rowDataJSON = null;
		String rowData = null;	
		
		catalogueJSON = (JSONObject)getProperty( CATALOGUE );
				
		try {
			rowDataJSON = new JSONObject();
			rowDataJSON.put("version", CURRENT_VERSION);
			rowDataJSON.put("catalogue", catalogueJSON);
			
			rowData = rowDataJSON.toString();
		} catch (Throwable e) {
			throw new SpagoBIEngineException("Impossible to store analysis state from catalogue object", e);
		}
		
		return rowData.getBytes();
	}

	public QueryCatalogue getCatalogue() {
		QueryCatalogue catalogue;
		JSONObject catalogueJSON;
		JSONArray queriesJSON;
		JSONObject queryJSON;
		Query query;
		
		catalogue = new QueryCatalogue();
		catalogueJSON = (JSONObject)getProperty( CATALOGUE );
		try {
			queriesJSON = catalogueJSON.getJSONArray("queries");
		
			for(int i = 0; i < queriesJSON.length(); i++) {
				queryJSON = queriesJSON.getJSONObject(i);
				query = QuerySerializerFactory.getDeserializer("application/json").deserialize(queryJSON, getDatamartModel());
								
				catalogue.addQuery(query);
			}
		} catch (Throwable e) {
			throw new SpagoBIEngineRuntimeException("Impossible to deserialize analysis state", e);
		}
		
		return catalogue;
	}

	public void setCatalogue(QueryCatalogue catalogue) {
		Set queries;
		Query query;
		JSONObject queryJSON;
		JSONArray queriesJSON;
		JSONObject catalogueJSON;
		
		catalogueJSON = new JSONObject();
		queriesJSON = new JSONArray();
		
		try {
			queries = catalogue.getAllQueries(false);
			Iterator it = queries.iterator();
			while(it.hasNext()) {
				query = (Query)it.next();
				queryJSON =  (JSONObject)QuerySerializerFactory.getSerializer("application/json").serialize(query, getDatamartModel(), null);
				queriesJSON.put( queryJSON );
			}
			
			catalogueJSON.put("queries", queriesJSON);
		} catch (Throwable e) {
			throw new SpagoBIEngineRuntimeException("Impossible to deserialize analyziz state", e);
		}
		
		setProperty( CATALOGUE, catalogueJSON );
	}

	public DataMartModel getDatamartModel() {
		return (DataMartModel)getProperty( DATAMART_MODEL );
	}

	public void setDatamartModel(DataMartModel datamartModel) {
		setProperty( DATAMART_MODEL, datamartModel );
	}
	
	
}
