/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.dao;

import it.eng.spagobi.commons.dao.ISpagoBIDao;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetHistory;

import java.util.List;

/**
 * Defines the interfaces for all methods needed to create, 
 * read, update and delete a dataset (CRUD operations).
 */
public interface IDataSetDAO extends ISpagoBIDao {
	
	// ========================================================================================
	// CEATE operations (Crud)
	// ========================================================================================
	public Integer insertDataSet(GuiGenericDataSet dataSet);
	
	
	// ========================================================================================
	// READ operations (cRud)
	// ========================================================================================
	
	public List<IDataSet> loadAllActiveDataSets();
	public IDataSet loadActiveDataSetByLabel(String label);
	public IDataSet loadActiveIDataSetByID(Integer id);
	
	public List<GuiGenericDataSet> loadFilteredDatasetList(String hsql, Integer offset, Integer fetchSize);
	public List<GuiGenericDataSet> loadPagedDatasetList(Integer offset, Integer fetchSize);
	public GuiGenericDataSet loadDataSetById(Integer dsId) ;
	public GuiGenericDataSet loadDataSetByLabel(String dsLabel);
	
	public List<SbiDataSetConfig> loadPagedSbiDatasetConfigList(Integer offset, Integer fetchSize);
	
	public Integer countBIObjAssociated(Integer dsId);
	public Integer countDatasets();
	public boolean hasBIObjAssociated (String dsId);
	
	// ========================================================================================
	// UPDATE operations (crUd)
	// ========================================================================================
	
	public void modifyDataSet(GuiGenericDataSet dataSet);
	public GuiGenericDataSet restoreOlderDataSetVersion(Integer dsId, Integer dsVersion);
	public Integer getHigherVersionNumForDS(Integer dsId);
	
	
	// ========================================================================================
	// DELETE operations (cruD)
	// ========================================================================================
	public void deleteDataSet(Integer dsID);
	public boolean deleteInactiveDataSetVersion(Integer dsVerionID);
	public boolean deleteAllInactiveDataSetVersions(Integer dsID);

	// ========================================================================================
	// UTILITY methods
	// ========================================================================================
	/**
	 * @deprecated
	 */
	public GuiGenericDataSet toGuiGenericDataSet(IDataSet iDataSet);
	public SbiDataSetHistory copyDataSetHistory(SbiDataSetHistory hibDataSet);
}
