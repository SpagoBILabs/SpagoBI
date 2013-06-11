package it.eng.spagobi.analiticalmodel.execution.service;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.List;

import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

public class ExecuteAdHocUtility {

	// logger component
	private static Logger logger = Logger.getLogger(ExecuteAdHocUtility.class);
	
	public static Engine getWorksheetEngine() {
		return getEngineByDocumentType(SpagoBIConstants.WORKSHEET_TYPE_CODE);
	}
	
	public static Engine getQbeEngine() {
		return getEngineByDocumentType(SpagoBIConstants.DATAMART_TYPE_CODE);
	}
	
	public static Engine getEngineByDocumentType(String type) {
		Engine engine;
		List<Engine> engines;
		
		engine = null;
		try {
			Assert.assertNotNull(DAOFactory.getEngineDAO(), "EngineDao cannot be null");
			engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType(type);
			if (engines == null || engines.size() == 0) {
				throw new SpagoBIRuntimeException("There are no engines for documents of type [" + type + "] available");
			} else {
				engine = (Engine) engines.get(0);
				LogMF.warn(logger, "There are more than one engine for document of type [" + type + "]. We will use the one whose label is equal to [{0}]", engine.getLabel());
			}
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException( "Impossible to load a valid engine for document of type [" + type + "]", t);				
		} finally {
			logger.debug("OUT");
		}
		
		return engine;
	}
	
	public static String createNewExecutionId() {
		String executionId;
		
		logger.debug("IN");
		
		executionId = null;
		try {
			UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
			UUID uuidObj = uuidGen.generateTimeBasedUUID();
			executionId = uuidObj.toString();
			executionId = executionId.replaceAll("-", "");
		} catch(Throwable t) {
			
		} finally {
			logger.debug("OUT");
		}
		
		return executionId;
	}

}
