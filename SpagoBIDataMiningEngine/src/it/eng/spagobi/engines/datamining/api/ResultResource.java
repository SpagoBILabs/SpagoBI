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
		DataMiningResult dmRes = new DataMiningResult();
		// /if (dataMiningEngineInstance.getDatasets() == null ||
		// dataMiningEngineInstance.getDatasets().isEmpty()) {
		dmRes = performScriptExecution(dataMiningEngineInstance);
		try {
			outputOfExecution = serialize(dmRes);
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

	// private void resolveDataMiningEngineBehaviour(DataMiningEngineInstance
	// dataMiningEngineInstance) {
	// if (dataMiningEngineInstance.getDatasets() != null &&
	// dataMiningEngineInstance.getDatasets().size() != 0) {
	// servletRequest.setAttribute(DO_UPLOAD_DATASETS, true);
	// } else {
	// String outputOfExecution =
	// performScriptExecution(dataMiningEngineInstance);
	// servletRequest.setAttribute(EXECUTION_OUTPUT, outputOfExecution);
	// }
	// }

	private DataMiningResult performScriptExecution(DataMiningEngineInstance dataMiningEngineInstance) {
		DataMiningResult dmRes = new DataMiningResult();
		// execute script
		DataMiningScriptExecutor executor = new DataMiningScriptExecutor();
		String result = executor.executeScript(dataMiningEngineInstance);
		if (dataMiningEngineInstance.getOutputType().equalsIgnoreCase("plot")) {
			result = executor.getPlotImageAsBase64(dataMiningEngineInstance.getOutputName());
			dmRes.setResult(result);
			dmRes.setOutputType("plot");
		} else {
			result = executor.executeScript(dataMiningEngineInstance);
			dmRes.setResult(result);
			dmRes.setOutputType("video");
		}
		return dmRes;
	}
}
