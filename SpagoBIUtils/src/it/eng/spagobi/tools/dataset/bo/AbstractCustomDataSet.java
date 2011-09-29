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

import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreFilter;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.functionalities.temporarytable.DatasetTempTable;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class AbstractCustomDataSet extends AbstractDataSet implements IDataSet {

	private IMetaData metadata;	

	private static transient Logger logger = Logger.getLogger(AbstractCustomDataSet.class);

	public AbstractCustomDataSet() {
		super();
		addBehaviour( new FilteringBehaviour(this) );
		addBehaviour( new SelectableFieldsBehaviour(this) );
	}

	public IMetaData getMetadata(){
		return this.metadata;
	}

	public void setMetadata(IMetaData metadata){
		this.metadata = metadata;
	}

	public IDataSetTableDescriptor createTemporaryTable(String tableName
			, MetaData metadata
			, Connection connection){
		logger.debug("IN");
		IDataSetTableDescriptor descriptor = DatasetTempTable.createTemporaryTable(connection, metadata, tableName);
		logger.debug("Temporary table created successfully");
		logger.debug("OUT");
		return descriptor;
	}


	// *********** Abstract methods **************


	// no implement
	public abstract IDataStore test();
	public abstract String getSignature();
	public abstract IDataStore getDomainValues(String attributeName, Integer start, Integer limit, IDataStoreFilter filter);
	public abstract Map<String, List<String>> getDomainDescriptions(Map<String, List<String>> codes); 
	public abstract IDataSetTableDescriptor persist(String tableName, Connection connection);



	public Map getUserProfileAttributes() {
		logger.error("This method is not implemented. It should not be invoked");
		return null;
	}

	public void setUserProfileAttributes(Map attributes) {
		logger.error("This method is not implemented. It should not be invoked");

	}

	public IDataStore getDataStore() {
		logger.error("This method is not implemented. It should not be invoked");
		throw new SpagoBIRuntimeException("This method is not implemented. It should not be invoked");
	}

	public Object getQuery() {
		logger.error("This method is not implemented. It should not be invoked");
		return null;
	}

	public void setQuery(Object query) {
		logger.error("This method is not implemented. It should not be invoked");

	}

	public void setAbortOnOverflow(boolean abortOnOverflow) {
		logger.error("This method is not implemented. It should not be invoked");

	}

	public void addBinding(String bindingName, Object bindingValue) {
		logger.error("This method is not implemented. It should not be invoked");

	}


}
