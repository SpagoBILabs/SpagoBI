/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset;

/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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



import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;

import org.apache.log4j.Logger;


/** The class gives two method to create a biobject (with parameters) or a dataset
 * 
 * @author gavardi
 *
 */

public class DatasetManagementAPI {

	static private Logger logger = Logger.getLogger(DatasetManagementAPI.class);

	public  Integer creatDataSet(IDataSet dataSet) {
		logger.debug("IN");
		Integer toReturn = null;
		if (dataSet == null) {
			logger.error("Dataset is null");
			return null;
		}

		try{

			validateDataSet(dataSet);

			// validate
			logger.debug("Getting the data set dao..");
			IDataSetDAO dataSetDao = DAOFactory.getDataSetDAO();
			logger.debug("DatasetDAo loaded");
			logger.debug("Inserting the data set wit the dao...");
			toReturn = dataSetDao.insertDataSet(dataSet);
			logger.debug("Data Set inserted");
			if (toReturn != null) {
				logger.info("DataSet "+dataSet.getLabel()+" saved with id = " + toReturn);
			} else {
				logger.error("DataSet not saved: check error log");
			}

		}
		catch (ValidationException e) {
			logger.error("Failed validation of dataset "+dataSet.getLabel()+" with cause: "+e.getValidationMessage());
			throw new RuntimeException(e.getValidationMessage(), e);
		}
		catch (EMFUserError e) {
			logger.error("EmfUserError ",e);
			throw new RuntimeException("EmfUserError ",e);
		}


		logger.debug("OUT");
		return toReturn;
	}

	  
	private boolean validateDataSet(IDataSet dataSet) throws ValidationException, EMFUserError{
		logger.debug("IN");

		logger.debug("check the dataset not alreaduy present with same label");

		IDataSet datasetLab = DAOFactory.getDataSetDAO().loadActiveDataSetByLabel(dataSet.getLabel());

		if(datasetLab != null){
			throw new ValidationException("Dataset with label "+dataSet.getLabel()+" already found");
		}

		logger.debug("OUT");
		return true;

	}

	public class ValidationException extends Exception{
		private String validationMessage;
		ValidationException(String _validationMessage) {
			super();
			this.validationMessage = _validationMessage;
		}
		
		ValidationException(String _validationMessage, Throwable e) {
			super(e);
			this.validationMessage = _validationMessage;
		}
		public String getValidationMessage(){
			return this.validationMessage;
		}

	}
}
