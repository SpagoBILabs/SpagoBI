/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.datamart.provider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;
import it.eng.spagobi.engines.geo.dataset.provider.Link;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.transformer.IDataStoreTransformer;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class AddLinkFieldsTransformer implements IDataStoreTransformer {
	String[] measureColumnNames;
	Hierarchy.Level level;
	Map env;
	
	public static transient Logger logger = Logger.getLogger(AddLinkFieldsTransformer.class);
	
	public AddLinkFieldsTransformer(String[] measureColumnNames, Hierarchy.Level level, Map env){
		this.measureColumnNames = measureColumnNames;
		this.level = level;
		this.env = env;
	}
	
	public void transform(IDataStore dataStore) {
		List fieldsMeta = dataStore.getMetaData().findFieldMeta("ROLE", "MEASURE");
		logger.debug("found " + fieldsMeta.size() + " measure column in dataset");
		for(int i = 0; i < fieldsMeta.size(); i++) {
			
			IFieldMetaData fieldMeta = (IFieldMetaData)fieldsMeta.get(i);
			String measureFiledName = fieldMeta.getName();
		
			String linkFiledName = measureFiledName + "_LINK";
			Link link = level.getLink(measureFiledName);
			addLinkField(linkFiledName, link, dataStore);
		}
		
	}
	
	
	public void addLinkField(String fieldName, Link link, IDataStore dataStore) {
		
		IMetaData dataStoreMeta;
		FieldMetadata fieldMeta;
		IRecord record;
		IField field;
		
		
		try {
			Assert.assertNotNull(fieldName, "Input parametr [fieldName] cannot be null");
			//Link parameter can be null; in that case Link.DEFAULT_BASE_URL will be used
			Assert.assertNotNull(dataStore, "Input parametr [dataStore] cannot be null");
			
			try {
				logger.debug("Adding link column [" + fieldName + ": " + link + "] ...");
				
				dataStoreMeta = dataStore.getMetaData();
				fieldMeta = new FieldMetadata();
					
				fieldMeta.setName(fieldName);
				fieldMeta.setType(String.class);
				fieldMeta.setProperty("ROLE", "CROSSNAVLINK");
				
				dataStoreMeta.addFiedMeta(fieldMeta);
				
				logger.debug("Link column [" + fieldName + ": " + link + "] added succesfully");
			} catch(Throwable t) {
				throw new GeoEngineException("Impossible to add link column [" + fieldName + ": " + link + "] to datastore", t);
			}
			
			record = null;
			try {
				logger.debug("Valorizing link column [" + fieldName + ": " + link + "] for each record in the dataset ...");
				Iterator it = dataStore.iterator();
				while(it.hasNext()) {
					record = (IRecord)it.next();
					if(link != null) {
						logger.debug("Added link value [" + link.toXString(record, env) + "]");
						field = new Field( link.toXString(record, env) );	
					} else {
						field = new Field( Link.DEFAULT_BASE_URL );		
						logger.debug("Added link value [" + Link.DEFAULT_BASE_URL + "]");
					}
							
					record.appendField( field );
				}
				logger.debug("Link column [" + fieldName + ": " + link + "] has been succesfully valorized for each record in the dataset ");
			} catch(Throwable t) {
				throw new GeoEngineException("Impossible to valorize link column for record [" + record + "]", t);
			}
		} catch(Throwable t) {
			GeoEngineException e;
			if(t instanceof GeoEngineException) {
				e = (GeoEngineException)t;
			} else {
				e = new GeoEngineException("An unpredicted error occurred while adding link fields to datastore", t);
			}
			
			throw e;
		}
	}

}
