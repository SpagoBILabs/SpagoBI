/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */


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
 *  Monica Franceschini
 *  	   monica.franceschini@eng.it *  
 */
public class JDBCDataSet extends AbstractJDBCDataset {
	
public static String DS_TYPE = "SbiQueryDataSet";
	
	private static transient Logger logger = Logger.getLogger(JDBCDataSet.class);
    
	
	/**
     * Instantiates a new empty JDBC data set.
     */
    public JDBCDataSet() {
		super();
		setDataReader( new JDBCStandardDataReader() );
	}
 
}
