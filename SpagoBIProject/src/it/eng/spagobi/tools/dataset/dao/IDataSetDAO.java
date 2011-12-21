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
package it.eng.spagobi.tools.dataset.dao;


/*
 * Created on 13-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.tools.dataset.bo.GuiDataSetDetail;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;

import java.util.List;

/**
 * Defines the interfaces for all methods needed to insert, modify and deleting an engine.
 */
public interface IDataSetDAO extends ISpagoBIDao{
	
	/*****************USED by new GUI******/
	/**
	 * Delete data set.
	 * @param dsID the a data set ID
	 * @throws EMFUserError the EMF user error
	 */
	public void deleteDataSet(Integer dsID) throws EMFUserError ;
	
	/**
	 * Delete the inactive dataset version.
	 * @param dsVerionID the a data set version ID
	 * @throws EMFUserError the EMF user error
	 */
	public boolean deleteInactiveDataSetVersion(Integer dsVerionID) throws EMFUserError ;
	
	/**
	 * Delete all inactive dataset versions.
	 * @param dsID the a data set fo which all old versions need to eb deleted
	 * @throws EMFUserError the EMF user error
	 */
	public void deleteAllInactiveDataSetVersions(Integer dsID) throws EMFUserError ;
	
	/**
	 * Insert data set.
	 * @param dataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#insertDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public Integer insertDataSet(GuiGenericDataSet dataSet) throws EMFUserError;
	
	/**
	 * Restore an Older Version of the dataset
	 * @param dsId the a data set ID
	 * @param dsVersion the a data set Version
	 * @throws EMFUserError the EMF user error
	 */
	public GuiGenericDataSet restoreOlderDataSetVersion(Integer dsId, Integer dsVersion) throws EMFUserError ;
	
	/**
	 * Returns the Higher Version Number of a selected DS
	 * @param dsId the a data set ID
	 * @throws EMFUserError the EMF user error
	 */
	public Integer getHigherVersionNumForDS(Integer dsId) throws EMFUserError;
	
	/**
	 * Modify data set.
	 * @param aDataSet the a data set
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataset.dao.IDataSetDAO#modifyDataSet(it.eng.spagobi.tools.dataset.bo.AbstractDataSet)
	 */
	public void modifyDataSet(GuiGenericDataSet dataSet) throws EMFUserError;
	
	/**
	 * Returns List of all existent SbiDataSetConfig elements (NO DETAIL, only name, label, descr...).
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent SbiDataSetConfig
	 * @throws EMFUserError the EMF user error
	 */
	public List<SbiDataSetConfig> loadPagedSbiDatasetConfigList(Integer offset, Integer fetchSize)
			throws EMFUserError;
	
	public List<GuiGenericDataSet> loadFilteredDatasetList(String hsql,Integer offset, Integer fetchSize) throws EMFUserError;
	
	/**
	 * Returns List of all existent IDataSets with current active version
	 * @param offset starting element
	 * @param fetchSize number of elements to retrieve
	 * @return List of all existent IDataSets with current active version
	 * @throws EMFUserError the EMF user error
	 */
	public List<GuiGenericDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize)
		throws EMFUserError ;
	
	/**
	 * Counts number of BIObj associated.
	 * @param dsId the ds id
	 * @return Integer, number of BIObj associated
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countBIObjAssociated (Integer dsId) throws EMFUserError;
	
	/**
	 * Counts number of existent DataSets
	 * @return Integer, number of existent DataSets
	 * @throws EMFUserError the EMF user error
	 */
	public Integer countDatasets() throws EMFUserError ;
	
	/*****************USED by OLD GUI******/
	/**
	 * Checks for bi obj associated.
	 * @param dsId the ds id
	 * @return true, if checks for bi obj associated
	 * @throws EMFUserError the EMF user error
	 * @see it.eng.spagobi.tools.dataSet.dao.IDataSetDAO#hasBIObjAssociated(java.lang.String)
	 */
	public boolean hasBIObjAssociated (String dsId) throws EMFUserError;
	
	/*****************USED many times but not in new GUI******/
	/**
	 * Load data set by id.
	 * @param dsID the ds id
	 * @return the data set
	 * @throws EMFUserError the EMF user error*/
	public IDataSet loadActiveIDataSetByID(Integer dsId) throws EMFUserError;
	
	/**
	 * Load data set by label.
	 * @param label the label
	 * @return the data set
	 * @throws EMFUserError the EMF user error
	 */	
	public IDataSet loadActiveDataSetByLabel(String label) throws EMFUserError ;
	
	/**
	 * Load data set by id.
	 * @param datasetId datasetConfig
	 * @return the data set
	 * @throws EMFUserError the EMF user error
	 */	
	public GuiGenericDataSet loadDataSetById(Integer dsId) throws EMFUserError ;
	
	/**
	 * Load data set by label.
	 * @param datasetId datasetConfig
	 * @return the data set
	 * @throws EMFUserError the EMF user error
	 */	
	public GuiGenericDataSet loadDataSetByLabel(String dsLabel) throws EMFUserError ;
	
	
	/**
	 * Load all active data sets.
	 * @return the list
	 * @throws EMFUserError the EMF user error
	 */
	public List loadAllActiveDataSets() throws EMFUserError;
	
	/**
	 * From the hibernate DataSet as input, gives the corrispondent <code>DataSet</code> object.
	 * 
	 * @param hibDataSet The hybernate data set
	 * @return The corrispondent <code>DataSet</code> object
	 * @throws EMFUserError 
	 */
	public IDataSet toIDataSet(SbiDataSetHistory hibDataSet) throws EMFUserError;
	
	/**
	 * From the IDataSet as input, return the corrispondent <code>GuiGenericDataSet</code> object.
	 * 
	 * @param iDataSet The IDataSet 
	 * @return The corrispondent <code>GuiGenericDataSet</code> object
	 * @throws EMFUserError 
	 */
	public GuiGenericDataSet toDataSet(IDataSet iDataSet) throws EMFUserError;
	
	
	public SbiDataSetHistory copyDataSetHistory(SbiDataSetHistory hibDataSet) throws EMFUserError;
}
