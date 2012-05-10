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
