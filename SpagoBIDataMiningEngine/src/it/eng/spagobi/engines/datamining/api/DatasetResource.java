package it.eng.spagobi.engines.datamining.api;

import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.common.AbstractDataMiningEngineService;
import it.eng.spagobi.engines.datamining.model.FileDataset;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

@Path("/1.0/dataset")
public class DatasetResource extends AbstractDataMiningEngineService {
	public static transient Logger logger = Logger.getLogger(DatasetResource.class);

	@GET
	@Produces("text/html; charset=UTF-8")
	public String getDatasets() {
		logger.debug("IN");

		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();
		String datasetsJson = null;
		List<FileDataset> datasets = null;
		if (dataMiningEngineInstance.getDatasets() != null && !dataMiningEngineInstance.getDatasets().isEmpty()) {
			datasets = dataMiningEngineInstance.getDatasets();
			datasetsJson = serializeDatasetsList(datasets);
		}

		if (!isNullOrEmpty(datasetsJson)) {
			logger.debug("Returning dataset list");
		} else {
			logger.debug("No dataset list found");
		}

		logger.debug("OUT");
		return datasetsJson;
	}
}
