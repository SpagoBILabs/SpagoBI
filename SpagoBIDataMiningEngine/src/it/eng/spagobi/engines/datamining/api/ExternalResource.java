package it.eng.spagobi.engines.datamining.api;

import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.common.AbstractDataMiningEngineService;
import it.eng.spagobi.engines.datamining.compute.DataMiningExecutor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

@Path("/1.0/execute")
public class ExternalResource extends AbstractDataMiningEngineService {
	public static transient Logger logger = Logger.getLogger(ExternalResource.class);
	/**
	 * Service to execute an external script * 
	 * @return
	 * 
	 */
	@GET
	@Path("/{fileName}")
	@Produces("text/html; charset=UTF-8")
	public String executeScript(@PathParam("fileName") String fileName) {
		logger.debug("IN");

		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();
		
		DataMiningExecutor executor = new DataMiningExecutor(dataMiningEngineInstance, getUserProfile());
		try {
			executor.externalExecution(fileName, getUserProfile());
		} catch (Exception e) {
			logger.error(e);
			return getJsonKo();
		}

		logger.debug("OUT");
		return getJsonOk();
	}
}
