package it.eng.spagobi.tools.utils;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IBIObjectParameterDAO;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.tools.dataset.bo.GuiGenericDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.DataSource;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class CreationUtilities {

	static private Logger logger = Logger.getLogger(CreationUtilities.class);



	public  Integer creatDataSet(GuiGenericDataSet dataSet) {
		logger.debug("IN");
		Integer toReturn = null;
		if (dataSet == null) {
			logger.error("Dataset is null");
			return null;
		}

		try{

			validateDataSet(dataSet);

			// validate
			IDataSetDAO dataSetDao = DAOFactory.getDataSetDAO();
			toReturn = dataSetDao.insertDataSet(dataSet);
			if (toReturn != null) {
				logger.info("DataSet "+dataSet.getLabel()+" saved with id = " + toReturn);
			} else {
				logger.error("DataSet not saved: check error log");
			}

		}
		catch (ValidationException e) {
			logger.error("Failed validation of dataset "+dataSet.getLabel()+" with cause: "+e.getValidationMessage());
			throw new RuntimeException(e.getValidationMessage());
		}
		catch (EMFUserError e) {
			logger.error("EmfUserError ",e);
			throw new RuntimeException(e.getMessage());
		}

		logger.debug("OUT");
		return toReturn;
	}

	private boolean validateDataSet(GuiGenericDataSet dataSet) throws ValidationException, EMFUserError{
		logger.debug("IN");

		logger.debug("check the dataset not alreaduy present with same label");

		IDataSet datasetLab = DAOFactory.getDataSetDAO().loadActiveDataSetByLabel(dataSet.getLabel());

		if(datasetLab != null){
			throw new ValidationException("Dataset with label "+dataSet.getLabel()+" already found");
		}

		logger.debug("OUT");
		return true;

	}








	public  Integer creatBiObject(BIObject biObject) throws NotAllowedOperationException{
		logger.debug("IN");
		Integer toReturn = null;

		if (biObject == null) {
			logger.error("BiObject is null");
			return null;
		}

		logger.debug("validation of object");

		try{
			validateBiObject(biObject);

			logger.debug("object validated");


			logger.debug("validation of parameters");
			List biObjectParameters = biObject.getBiObjectParameters();
			if(biObjectParameters != null){
				for (Iterator iterator = biObjectParameters.iterator(); iterator.hasNext();) {
					BIObjectParameter parameter = (BIObjectParameter) iterator.next();
					try{
						validateBiObjectParameter(parameter);
						logger.debug("parameter "+parameter.getParameterUrlName()+ " validated" );
					}
					catch (ValidationException e) {
						logger.error("Failed validation of parameter "+parameter.getParameterUrlName()+" with cause: "+e.getValidationMessage());
						throw new RuntimeException(e.getValidationMessage());
					}
				}
			}


			IBIObjectDAO biObjDAO = DAOFactory.getBIObjectDAO();
			//biObjDAO.setUserProfile(profile);

			biObjDAO.insertBIObject(biObject, biObject.getActiveTemplate(), true);

			logger.debug("object inserted with id "+biObject.getId());

			toReturn = biObject.getId();

			logger.debug("Insert parameters");

			if(biObjectParameters != null){
				for (Iterator iterator = biObjectParameters.iterator(); iterator.hasNext();) {
					BIObjectParameter parameter = (BIObjectParameter) iterator.next();
					parameter.setBiObjectID(toReturn);
					creatBiObjectParameter(parameter);
				}
			}

		}
		catch (ValidationException e) {
			logger.error("Failed validation of objject "+biObject.getLabel()+" with cause: "+e.getValidationMessage());
			throw new RuntimeException(e.getValidationMessage());
		}
		catch (EMFUserError e) {
			logger.error("EmfUserError ",e);
			throw new RuntimeException(e.getMessage());
		}


		logger.debug("OUT");
		return toReturn;


	}


	private  Integer creatBiObjectParameter(BIObjectParameter biObjectParameter) throws NotAllowedOperationException, EMFUserError{
		logger.debug("IN");
		Integer toReturn = null;

		IBIObjectParameterDAO biObjParameterDAO = DAOFactory.getBIObjectParameterDAO();
		biObjParameterDAO.insertBIObjectParameter(biObjectParameter);
		logger.debug("Inserted parameter with parameter url "+biObjectParameter.getParameterUrlName());

		logger.debug("OUT");
		return toReturn;
	}



	private boolean validateBiObjectParameter(BIObjectParameter objectPar) throws ValidationException, EMFUserError{
		logger.debug("IN");
		logger.debug("check the parameter exists");
		if (objectPar.getParID() != null ) {
			// if engine id is not specified take the first engine for the biobject type
			Integer parId = objectPar.getParID();
			Parameter isThere = null;
			try{
				isThere = DAOFactory.getParameterDAO().loadForDetailByParameterID(parId);
			}
			catch (EMFUserError e) {
				throw new ValidationException("Parameter with id "+parId+" was not found");

			}
			if(isThere == null){
				throw new ValidationException("Parameter with id "+parId+" was not found");
			}
		}

		logger.debug("OUT");
		return true;


	}

	private boolean validateBiObject(BIObject object) throws ValidationException, EMFUserError{
		logger.debug("IN");

		logger.debug("check the engine");
		if (object.getEngine() != null ) {
			// if engine id is not specified take the first engine for the biobject type
			Engine eng = object.getEngine();
			Engine isThere = null;
			try{
				isThere = DAOFactory.getEngineDAO().loadEngineByLabel(eng.getLabel());
			}
			catch (EMFUserError e) {
				throw new ValidationException("Datasource with id "+eng.getId()+" was not found");

			}
			if(isThere == null){
				throw new ValidationException("Engine with label "+eng.getLabel()+" was not found");
			}

		}

		logger.debug("check the datasource");
		if (object.getDataSourceId() != null ) {
			// if engine id is not specified take the first engine for the biobject type
			Integer dSourceId = object.getDataSourceId();
			DataSource isThere = null;
			try{
				isThere = DAOFactory.getDataSourceDAO().loadDataSourceByID(dSourceId);
			}
			catch (EMFUserError e) {
				throw new ValidationException("Datasource with id "+dSourceId+" was not found");

			}
			if(isThere == null){
				throw new ValidationException("Datasource with id "+dSourceId+" was not found");
			}	
		}

		logger.debug("check the datasource");
		if (object.getDataSetId() != null ) {
			// if engine id is not specified take the first engine for the biobject type
			Integer dSetId = object.getDataSetId();
			GuiGenericDataSet isThere = null;
			try{
				isThere = DAOFactory.getDataSetDAO().loadDataSetById(dSetId);
			}
			catch (EMFUserError e) {
				throw new ValidationException("Datasource with id "+dSetId+" was not found");

			}
			if(isThere == null){
				throw new ValidationException("Datase with id "+dSetId+" was not found");
			}	
		}

		logger.debug("check functionality");
		List functionalities = object.getFunctionalities();
		if(functionalities == null || functionalities.size() == 0){
			throw new ValidationException("Define at least one functionality where to insert biobject");
		}

		if(object.getBiObjectTypeID() == null || object.getBiObjectTypeCode() == null){
			throw new ValidationException("Object type and Object type code must be inserted");
		}
		if(object.getStateID() == null){
			throw new ValidationException("state must be set");
		}
		if(object.getVisible() == null){
			throw new ValidationException("visible flag must be set");
		}

		Domain stateDom = DAOFactory.getDomainDAO().loadDomainById(object.getStateID());
		object.setStateID(stateDom.getValueId());
		object.setStateCode(stateDom.getValueCd());


		// hceck the template (not blocking anyway)

		logger.debug("check template is present");
		if(object.getActiveTemplate() == null){
			logger.warn("No template definition for object: not blocking");
		}

		// CHECK IF THE LABEL IS ALREADY ASSIGNED TO AN EXISTING OBJECT
		BIObject aBIObject = DAOFactory.getBIObjectDAO().loadBIObjectByLabel(object.getLabel());
		if (aBIObject != null) {
			throw new ValidationException("Document with same label "+object.getLabel()+" alreay present");
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
		public String getValidationMessage(){
			return this.validationMessage;
		}

	}
}
