/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.datasource;

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
				logger.debug("Go to next record ...");
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
