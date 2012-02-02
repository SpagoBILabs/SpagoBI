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
package it.eng.spagobi.tools.massiveExport.services;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.execution.service.GetParametersForExecutionAction;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.massiveExport.bo.ProgressThread;
import it.eng.spagobi.tools.massiveExport.dao.IProgressThreadDAO;
import it.eng.spagobi.tools.massiveExport.utils.Utilities;
import it.eng.spagobi.tools.massiveExport.work.MassiveExportWork;
//import it.eng.spagobi.tools.massiveExport.work.MassiveExportWorkListener;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.threadmanager.WorkManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import commonj.work.WorkEvent;
import commonj.work.WorkItem;

import de.myfoo.commonj.work.FooRemoteWorkItem;

public class StartMassiveExportThreadAction extends GetParametersForExecutionAction {
	private final String SERVICE_NAME = "START_MASSIVE_EXPORT_THREAD_ACTION";

	// logger component
	private static Logger logger = Logger.getLogger(StartMassiveExportThreadAction.class);

	private final String FUNCTIONALITY_ID = "functId";
	private final String PARAMETER_VALUES = "parameterValues";
	private final String ROLE = "selectedRole";
	private final String TYPE = "type";  
	private final String SPLITTING_FILTER = "splittingFilter";  

	@Override
	public void doService() {
		logger.debug("IN");
		ILowFunctionalityDAO funcDao;
		List selObjects = null;
		Integer progressThreadId = null;
		
		Integer folderId = this.getAttributeAsInteger(FUNCTIONALITY_ID);	

		Assert.assertNotNull(folderId, "Functionality id cannot be null");

		String execRole = this.getAttributeAsString(ROLE);
		String state = this.getAttributeAsString(PARAMETER_VALUES);
		String documentType = this.getAttributeAsString(TYPE);
		String cycleOnFilters = this.getAttributeAsString(SPLITTING_FILTER);
		boolean splittingFilter = false;
		if(cycleOnFilters != null) splittingFilter = Boolean.valueOf(cycleOnFilters);
		logger.debug(ROLE+": "+execRole);;
		logger.debug(PARAMETER_VALUES+": "+state);;
		logger.debug(TYPE+": "+documentType);;
		logger.debug(SPLITTING_FILTER+": "+cycleOnFilters);;
		
		LowFunctionality funct = null;


		JSONObject parValuesJSON = null;
		try {
			parValuesJSON = new JSONObject(state);
			logger.debug("parValuesJSON");
		} catch (JSONException e) {
			logger.error("Could not parse JSON of parameters values: "+state);
			throw new SpagoBIServiceException("Could not parse JSON of parameters values: "+state, e);
		} 


		try {
			funcDao = DAOFactory.getLowFunctionalityDAO();

			// Get all the documents
			logger.debug("Search folder "+folderId+ " for documents of type "+TYPE);		
			funct = funcDao.loadLowFunctionalityByID(folderId, true);
			Assert.assertNotNull(funct, "functionality with id "+folderId);
			selObjects = Utilities.getContainedObjFilteredbyType(funct, documentType );


			logger.debug("Check if userid "+getUserProfile().getUserUniqueIdentifier()+ " and functionality "+funct.getCode()+ " has already a work in execution");

			IProgressThreadDAO threadDAO = DAOFactory.getProgressThreadDAO();
			// search if already exists
			ProgressThread pT = threadDAO.loadActiveProgressThreadByUserIdAndFuncCd(getUserProfile().getUserUniqueIdentifier().toString(), funct.getCode());
			if(pT != null){
				logger.warn("A massive export process is still opened for userId "+getUserProfile().getUserUniqueIdentifier()+" on functionality "+funct.getCode());
				throw new SpagoBIServiceException(SERVICE_NAME, "A massive export process is still opened for userId "+getUserProfile().getUserUniqueIdentifier()+" on functionality "+funct.getCode());
			}

			String randomName = getRandomName();
			
			ProgressThread progressThread = new ProgressThread(getUserProfile().getUserUniqueIdentifier().toString(), selObjects.size(), funct.getCode(), null, randomName);
			progressThreadId= threadDAO.insertProgressThread(progressThread);

			fillDriverValues(selObjects, parValuesJSON);
			// Object has parameters values set

			// cycle on Objects, fill parameters and call thread
			Config config = DAOFactory.getSbiConfigDAO().loadConfigParametersByLabel(SpagoBIConstants.JNDI_THREAD_MANAGER);

			WorkManager wm = new WorkManager(config.getValueCheck());
			//MassiveExportWorkListener mewListener = new MassiveExportWorkListener(getUserProfile(), funct, progressThreadId);
			
			MassiveExportWork mew = new MassiveExportWork(selObjects, getUserProfile(), funct , progressThreadId, randomName, splittingFilter);
			//FooRemoteWorkItem fooRemoteWorkItem=wm.buildFooRemoteWorkItem(mew, mewListener);
			FooRemoteWorkItem fooRemoteWorkItem=wm.buildFooRemoteWorkItem(mew, null);
			
			// Check if work was accepted
			if(fooRemoteWorkItem.getStatus()==WorkEvent.WORK_ACCEPTED){
				logger.debug("run work item");
				//WorkItem workItem=(WorkItem)wm.runWithReturnWI(mew, mewListener);
				WorkItem workItem=(WorkItem)wm.runWithReturnWI(mew, null);
				int statusWI=workItem.getStatus();


			}
			else{
				int statusWI=fooRemoteWorkItem.getStatus();
				logger.error("Massive export Work thread was rejected with status "+statusWI);
				if(progressThreadId != null){
					deleteDBRowInCaseOfError(progressThreadId);
				}
				throw new SpagoBIServiceException(SERVICE_NAME, "Massive export Work thread was rejected with status "+statusWI);
			}

		} 
		catch (JSONException e1) {
			logger.error("Error in reading parameters values",e1);
			if(progressThreadId != null){
				deleteDBRowInCaseOfError(progressThreadId);
			}
			throw new SpagoBIServiceException(SERVICE_NAME, "Error in reading parameters values", e1);
		}
		catch (Exception e) {
			logger.error("error in starting export thread",e);
			if(progressThreadId != null){
				deleteDBRowInCaseOfError(progressThreadId);
			}
			throw new SpagoBIServiceException(SERVICE_NAME, "error in starting export thread: \n"+e.getLocalizedMessage(), e);
		}
		logger.debug("OUT");

	}

	public void deleteDBRowInCaseOfError(Integer progressThreadId){
		logger.debug("IN");
		try {
			IProgressThreadDAO threadDAO = DAOFactory.getProgressThreadDAO();
			logger.error("delete row progress thread with id "+progressThreadId);
			threadDAO.deleteProgressThread(progressThreadId);
		} catch (EMFUserError e1) {
			logger.error("Error in deleting the row with the progress id "+progressThreadId);
		}
		logger.debug("OUT");

	}
	
	
	
	
	

	void fillDriverValues(List<BIObject> obj, JSONObject values) throws JSONException{
		logger.debug("IN");
		// for all objects
		for (Iterator iterator = obj.iterator(); iterator.hasNext();) {
			BIObject biObject = (BIObject) iterator.next();
			logger.debug("fill values of object "+biObject.getLabel());
			List pars = biObject.getBiObjectParameters();
			for (Iterator iterator2 = pars.iterator(); iterator2.hasNext();) {
				BIObjectParameter par = (BIObjectParameter) iterator2.next();
				logger.debug("search value for obj par with id  "+par.getId());
				// get the label passed
				String parLabel = values.getString(par.getId().toString()+"_objParameterId");
				List valuesToInsert = null;
				if(parLabel != null){

					try{
						JSONArray arr = values.getJSONArray(parLabel);
						valuesToInsert = new ArrayList();
						for (int i = 0; i < arr.length(); i++) {
							String ob = arr.getString(i);
							valuesToInsert.add(ob);
						}
					}
					catch (JSONException e) {
						// to catch case it is not multivalue
					}
					if(valuesToInsert == null){
						String value = values.getString(parLabel);
						if(value != null){
							valuesToInsert = new ArrayList();
							valuesToInsert.add(value);
						}

					}

				}
				else{
					logger.warn("parameter value not defined  "+par.getLabel());
				}

				logger.debug("insert for "+par.getLabel()+" value"+ valuesToInsert.toString());
				par.setParameterValues(valuesToInsert);

			}


		}


		logger.debug("OUT");
	}


	private String getRandomName(){
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss.SSS");
		String randomName = formatter.format(new Date());			
		randomName=randomName.replaceAll(" ", "_");
		randomName=randomName.replaceAll(":", "-");
		//randomName = "Massive_Export_"+randomName;
		return randomName;

	}
	
}
