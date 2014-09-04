/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.api;

import it.eng.spagobi.engines.datamining.DataMiningEngineConfig;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.common.AbstractDataMiningEngineService;
import it.eng.spagobi.engines.datamining.common.utils.DataMiningConstants;
import it.eng.spagobi.engines.datamining.compute.DataMiningDatasetUtils;
import it.eng.spagobi.engines.datamining.compute.DataMiningExecutor;
import it.eng.spagobi.engines.datamining.model.DataMiningDataset;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/1.0/dataset")
public class DatasetResource extends AbstractDataMiningEngineService {

	public static transient Logger logger = Logger.getLogger(DatasetResource.class);
	private final String UPLOADED_FILE_PATH = DataMiningEngineConfig.getInstance().getEngineConfig().getResourcePath() + DataMiningConstants.DATA_MINING_PATH_SUFFIX;

	@GET
	@Produces("text/html; charset=UTF-8")
	public String getDatasets() {
		logger.debug("IN");

		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();
		String datasetsJson = "";
		List<DataMiningDataset> datasets = null;
		if (dataMiningEngineInstance.getDatasets() != null && !dataMiningEngineInstance.getDatasets().isEmpty()) {
			datasets = dataMiningEngineInstance.getDatasets();
			// finds existing files for datasets

			for (Iterator dsIt = dataMiningEngineInstance.getDatasets().iterator(); dsIt.hasNext();) {
				DataMiningDataset ds = (DataMiningDataset) dsIt.next();
				if (ds.getType().equalsIgnoreCase(DataMiningConstants.DATASET_TYPE_FILE)) {
					File fileDSDir = new File(DataMiningDatasetUtils.UPLOADED_FILE_PATH + ds.getName());
					// /find file in dir
					File[] dsfiles = fileDSDir.listFiles();
					String fileName = dsfiles[0].getName();
					ds.setFileName(fileName);
				}

			}
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

	@GET
	@Path("/updateDataset/{fileName}/{dsName}")
	@Produces("text/html; charset=UTF-8")
	public String updateDataset(@PathParam("fileName") String fileName, @PathParam("dsName") String dsName) {
		logger.debug("IN");

		DataMiningEngineInstance dataMiningEngineInstance = getDataMiningEngineInstance();
		String datasetsJson = "";
		List<DataMiningDataset> datasets = null;
		if (dataMiningEngineInstance.getDatasets() != null && !dataMiningEngineInstance.getDatasets().isEmpty()) {
			datasets = dataMiningEngineInstance.getDatasets();
			// finds existing files for datasets

			for (Iterator dsIt = dataMiningEngineInstance.getDatasets().iterator(); dsIt.hasNext();) {
				DataMiningDataset ds = (DataMiningDataset) dsIt.next();
				if (ds.getType().equalsIgnoreCase(DataMiningConstants.DATASET_TYPE_FILE) && ds.getName().equals(dsName)) {
					ds.setFileName(fileName);
					// and update its content in R workspace!
					DataMiningExecutor executor = new DataMiningExecutor(dataMiningEngineInstance);
					try {
						executor.updateDatasetInWorkspace(ds, getUserProfile());
					} catch (IOException e) {
						throw new SpagoBIEngineRuntimeException("Error updating file dataset", e);
					}
				}

			}
		}

		if (!isNullOrEmpty(datasetsJson)) {
			logger.debug("Returning dataset list");
		} else {
			logger.debug("No dataset list found");
		}

		logger.debug("OUT");
		return getJsonSuccess();
	}

	@POST
	@Path("/loadDataset/{fieldName}")
	@Consumes("multipart/form-data")
	@Produces("text/html; charset=UTF-8")
	public String loadDataset(@Context HttpServletRequest req, MultipartFormDataInput input, @PathParam("fieldName") String fieldName) {

		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		List<InputPart> inputParts = uploadForm.get(fieldName);

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				File dirToSaveDS = new File(UPLOADED_FILE_PATH + fieldName);// remember
																			// to
																			// create
																			// datamining
																			// folder
																			// inside
																			// resources
				dirToSaveDS.mkdir();
				// leave just une file per dataset
				File[] dsfiles = dirToSaveDS.listFiles();
				if (dsfiles.length >= 1) {
					for (int i = 0; i < dsfiles.length; i++) {
						File olFile = dsfiles[i];
						olFile.delete();
					}
				}

				// // constructs upload file path
				fileName = dirToSaveDS.getPath() + "\\" + fileName;

				writeFile(bytes, fileName);

			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SpagoBIEngineRuntimeException("Error loading file dataset", e);
			}

		}

		return getJsonSuccessTrue();

	}

	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}

}
