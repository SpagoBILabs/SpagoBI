/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.services.dataset.service;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class DataSetSupplier {
    
	static private Logger logger = Logger.getLogger(DataSetSupplier.class);

    /**
     * Gets the data set.
     * 
     * @param documentId the document id
     * 
     * @return the data set
     */
    public SpagoBiDataSet getDataSet(String documentId) {
		
    	SpagoBiDataSet datasetConfig = null;
    	BIObject obj;
    	IDataSet dataSet;
    	
    	logger.debug("IN");
		
		logger.debug("Requested the datasource associated to document [" + documentId + "]");

		if (documentId == null) {
		    return null;
		}
	
		// gets data source data from database
		try {
		    obj = DAOFactory.getBIObjectDAO().loadBIObjectById( Integer.valueOf(documentId) );
		    if (obj == null) {
				logger.warn("The object with id " + documentId + " deoes not exist on database.");
				return null;
		    }
		    
		    
	    	if (obj.getDataSetId() == null) {
	    		logger.warn("Dataset is not configured for this document:"+documentId);
	    		return null;
	    	}
	    	
	    	dataSet = DAOFactory.getDataSetDAO().loadActiveIDataSetByID( obj.getDataSetId() );
		    if (dataSet == null) {
				logger.warn("The dataSet with id " + obj.getDataSetId() + " deoes not exist on database.");
				return null;
		    }
		    
		    datasetConfig = dataSet.toSpagoBiDataSet();
	
		} catch (Exception e) {
		    logger.error("The dataset is not correctly returned", e);	
		} finally {
			logger.debug("OUT");
		}
		
		return datasetConfig;
    }

    /**
     * Gets the data set by label.
     * 
     * @param label the ds label
     * 
     * @return the data set by label
     */
    public SpagoBiDataSet getDataSetByLabel(String label) {
    	SpagoBiDataSet datasetConfig = null;
    	IDataSet ds = null;
    
    	logger.debug("IN");

		try {
			ds = DAOFactory.getDataSetDAO().loadActiveDataSetByLabel( label );	
			datasetConfig = ds.toSpagoBiDataSet();
		} catch (EMFUserError e) {
			e.printStackTrace();
		} finally {
			logger.debug("OUT");
		}
		
		return datasetConfig;
    }

    /**
     * Gets the all data source.
     * 
     * @return the all data source
     */
    public SpagoBiDataSet[] getAllDataSet() {
    	SpagoBiDataSet[] dataSetsConfig = null;;
    	List datasets;
    	ArrayList tmpList;
    	
    	
    	logger.debug("IN");

		// gets all data source from database
		try {
		    datasets = DAOFactory.getDataSetDAO().loadAllActiveDataSets();
		    if (datasets == null) {
		    	logger.warn("There are no datasets defined on the database.");
		    	return null;
		    }
	
		    Iterator it = datasets.iterator();
		    tmpList = new ArrayList();
		    while (it.hasNext()) {
		    	IDataSet dataset = (IDataSet) it.next();
				SpagoBiDataSet sbds = dataset.toSpagoBiDataSet();			    
				tmpList.add(sbds);
		    }
		    
		    dataSetsConfig = (SpagoBiDataSet[])tmpList.toArray(new SpagoBiDataSet[tmpList.size()]);
			
		} catch (Exception e) {
		    logger.error("The data sources are not correctly returned", e);
		} finally {
			logger.debug("OUT");
		}		
		
		return dataSetsConfig;
    }
}
