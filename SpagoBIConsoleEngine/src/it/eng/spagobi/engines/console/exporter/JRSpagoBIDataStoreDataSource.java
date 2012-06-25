/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.exporter;

import java.sql.Date;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.apache.log4j.Logger;

import it.eng.spago.dbaccess.sql.DateDecorator;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JRSpagoBIDataStoreDataSource implements JRRewindableDataSource {
	
	
	private IDataStore dataStore = null;
	private Iterator<IRecord> records = null;
	private IRecord currentRecord = null;
	
	private static transient Logger logger = Logger.getLogger(JRSpagoBIDataStoreDataSource.class);
	
	public JRSpagoBIDataStoreDataSource(IDataStore ds) {
		dataStore = ds;
		if (dataStore != null) {
			records = dataStore.iterator();
		}
	}
	
	public boolean next()
	{
		boolean hasNext = false;
		
		if (records != null) {
			hasNext = records.hasNext();
			
			if (hasNext) {
				currentRecord = records.next();
				logger.debug("Go to nect record ...");
			}
		}
		
		return hasNext;
	}
	
	public Object getFieldValue(JRField field)
	{
		Object value = null;
		int  fieldIndex;
		
		if (currentRecord != null) {
			fieldIndex = dataStore.getMetaData().getFieldIndex(field.getName());
			value = currentRecord.getFieldAt(fieldIndex).getValue();
			
			if(value instanceof DateDecorator) {
				DateDecorator dateDecorator = (DateDecorator)value;
				value = new Date(dateDecorator.getTime());
			}
		}
		logger.debug(field.getName() + ": " + value);
		return value;
	}

	
	public void moveFirst()
	{
		if (dataStore != null)
		{
			records = dataStore.iterator();
		}
	}

	/**
	 * Returns the underlying map dataStore used by this data source.
	 * 
	 * @return the underlying dataStore
	 */
	public IDataStore getDataStore()
	{
		return dataStore;
	}

	/**
	 * Returns the total number of records/maps that this data source
	 * contains.
	 * 
	 * @return the total number of records of this data source
	 */
	public int getRecordCount()
	{
		return dataStore == null ? 0 : (int)dataStore.getRecordsCount();
	}
	
	/**
	 * Clones this data source by creating a new instance that reuses the same
	 * underlying map collection.
	 * 
	 * @return a clone of this data source
	 */
	public JRSpagoBIDataStoreDataSource cloneDataSource()
	{
		return new JRSpagoBIDataStoreDataSource(dataStore);
	}

}
