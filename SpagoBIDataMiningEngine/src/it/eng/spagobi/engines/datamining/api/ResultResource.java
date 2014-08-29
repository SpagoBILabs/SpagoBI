/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.api;

import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.bo.DataMiningResult;
import it.eng.spagobi.engines.datamining.common.AbstractDataMiningEngineService;
import it.eng.spagobi.engines.datamining.compute.DataMiningScriptExecutor;
import it.eng.spagobi.engines.datamining.compute.DatasetFileUtils;
import it.eng.spagobi.engines.datamining.serializer.SerializationException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

@Path("/1.0/result")
public class ResultResource extends AbstractDataMiningEngineService {
	public static transient Logger logger = Logger.getLogger(ResultResource.class);

	/**
	 * Service to get Result
	 * 
	 * @return
	 * 
	 */
	@GET
	@Produces("text/html; charset=UTF-8")
	public String getResult() {
		logger.debug("IN");

		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();
		String outputOfExecution = null;
		// /if (dataMiningEngineInstance.getDatasets() == null ||
		// dataMiningEngineInstance.getDatasets().isEmpty()) {
		DataMiningScriptExecutor executor = new DataMiningScriptExecutor();
		List<DataMiningResult> results = executor.executeScript(dataMiningEngineInstance);
		try {
			outputOfExecution = serialize(results);
		} catch (SerializationException e) {
			logger.error("Error serializing the result", e);
			throw new SpagoBIEngineRuntimeException("Error serializing the result", e);
		}
		// }

		if (!isNullOrEmpty(outputOfExecution)) {
			logger.debug("Returning result");
		} else {
			logger.debug("No result found");
		}

		logger.debug("OUT");
		return outputOfExecution;

	}

	/**
	 * Checks whether the result panel has to be displyed ad first execution
	 * 
	 */
	@GET
	@Path("/needsResultAtForstExec")
	@Produces("text/html; charset=UTF-8")
	public String needsResultAtForstExec() {
		logger.debug("IN");
		Boolean resNeeded = true;
		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();

		resNeeded = DatasetFileUtils.areDatasetsProvided(dataMiningEngineInstance);
		if (!resNeeded) {
			return getJsonKo();
		}
		logger.debug("OUT");
		return getJsonOk();

	}

}
