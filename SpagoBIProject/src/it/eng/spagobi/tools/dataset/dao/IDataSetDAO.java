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
package it.eng.spagobi.tools.dataset.dao;


/*
 * Created on 13-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.tools.dataset.bo.IDataSet;

import java.util.List;

/**
 * Defines the interfaces for all methods needed to insert, modify and deleting an engine.
 */
public interface IDataSetDAO {
	
	
	/**
	 * Loads all detail information for a data set identified by its <code>dsID</code>.
	 * All these information,  achived by a query to the DB, are stored
	 * into a <code>dataset</code> object, which is returned.
	 * 
	 * @param dsID The id for the dataset to load
	 * 
	 * @return A <code>dataset</code> object containing all loaded information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public IDataSet loadDataSetByID(Integer dsID) throws EMFUserError;
	
	/**
	 * Loads all detail information for data Set whose label is equal to <code>label</code>.
	 * 
	 * @param label The label for the data Set to load
	 * 
	 * @return An <code>dataset</code> object containing all loaded information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public IDataSet loadDataSetByLabel(String label) throws EMFUserError;
	
	/**
	 * Loads all detail information for all data Sets. For each of them, detail
	 * information is stored into a <code>dataset</code> object. After that, all data Sets
	 * are stored into a <code>List</code>, which is returned.
	 * 
	 * @return A list containing all dataset objects
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	
	public List loadAllDataSets() throws EMFUserError;
	
	public List loadPagedDatasetList(Integer offset, Integer fetchSize)throws EMFUserError ;
	
	public Integer countDatasets()throws EMFUserError ;

	/**
	 * Implements the query to modify a data Set. All information needed is stored
	 * into the input <code>dataset</code> object.
	 * 
	 * @param aDataSet The object containing all modify information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	
	public void modifyDataSet(IDataSet aDataSet) throws EMFUserError;
	
	/**
	 * Implements the query to insert a data Set. All information needed is stored
	 * into the input <code>dataset</code> object.
	 * 
	 * @param aDataSet The object containing all insert information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */
	public void insertDataSet(IDataSet aDataSet) throws EMFUserError;
	
	/**
	 * Implements the query to erase a data Set. All information needed is stored
	 * into the input <code>dataset</code> object.
	 * 
	 * @param aDataSet The object containing all delete information
	 * 
	 * @throws EMFUserError If an Exception occurred
	 */	
	public void eraseDataSet(IDataSet aDataSet) throws EMFUserError;

	/**
	 * Tells if a data Set is associated to any
	 * BI Engines. It is useful because a data Set cannot be deleted
	 * if it is used by one or more BI Engines.
	 * 
	 * @param dsId The dataset identifier
	 * 
	 * @return True if the dataset is used by one or more
	 * objects, else false
	 * 
	 * @throws EMFUserError If any exception occurred
	 */
	//public boolean hasBIEngineAssociated (String dsId) throws EMFUserError;

	
	/**
	 * Tells if a data Set is associated to any
	 * BI Object. It is useful because a data Set cannot be deleted
	 * if it is used by one or more BI Objects.
	 *
	 * @param dsId The dataset identifier
	 * @return True if the dataset is used by one or more 
	 * 		    objects, else false 
	 * @throws EMFUserError If any exception occurred 
	 */
	public boolean hasBIObjAssociated (String dsId) throws EMFUserError;

}
