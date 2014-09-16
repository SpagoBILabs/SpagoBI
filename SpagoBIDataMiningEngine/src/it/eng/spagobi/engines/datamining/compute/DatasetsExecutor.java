/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.model.DataMiningDataset;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class DatasetsExecutor {

	static private Logger logger = Logger.getLogger(DatasetsExecutor.class);

	private Rengine re;

	DataMiningEngineInstance dataminingInstance;
	IEngUserProfile profile;

	public DatasetsExecutor(DataMiningEngineInstance dataminingInstance, IEngUserProfile profile) {
		this.dataminingInstance = dataminingInstance;
		this.profile = profile;
	}

	public Rengine getRe() {
		return re;
	}

	public void setRe(Rengine re) {
		this.re = re;
	}

	// protected void evalDatasets(HashMap paramsFilled) throws IOException {
	// if (dataminingInstance.getDatasets() != null &&
	// !dataminingInstance.getDatasets().isEmpty()) {
	// for (Iterator dsIt = dataminingInstance.getDatasets().iterator();
	// dsIt.hasNext();) {
	// DataMiningDataset ds = (DataMiningDataset) dsIt.next();
	// if (ds.getType().equalsIgnoreCase("file")) {
	// // tries to get it from user workspace
	// REXP datasetNameInR = re.eval(ds.getName());
	// if (datasetNameInR == null) {
	// File fileDSDir = new
	// File(DataMiningDatasetUtils.getUserResourcesPath(profile) +
	// ds.getName());
	// // /find file in dir
	// File[] dsfiles = fileDSDir.listFiles();
	// String fileDSPath = dsfiles[0].getPath();
	//
	// fileDSPath = fileDSPath.replaceAll("\\\\", "/");
	//
	// String stringToEval = ds.getName() + "<-read." + ds.getReadType() + "(\""
	// + fileDSPath + "\"," + ds.getOptions() + ");";
	// re.eval(stringToEval);
	// REXP dataframe = re.eval(ds.getName());
	// } else {
	// // use it!
	// logger.debug("dataset " + ds.getName() +
	// " already loaded in user workspace!");
	// }
	// } else if (ds.getType().equalsIgnoreCase("spagobi_ds")) {
	// // spagobi dataset content could change independently from
	// // the engine, so it must be recalculated every time
	// try {
	//
	// String csvToEval =
	// DataMiningDatasetUtils.getFileFromSpagoBIDataset(paramsFilled, ds,
	// profile);
	// String stringToEval = ds.getName() + "<-read.csv(\"" + csvToEval +
	// "\",header = TRUE, sep = \",\");";
	// re.eval(stringToEval);
	//
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// throw e;
	// }
	//
	// }
	// }
	// }
	// }

	protected void evalDatasetsNeeded(HashMap paramsFilled) throws IOException {
		if (dataminingInstance.getDatasets() != null && !dataminingInstance.getDatasets().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getDatasets().iterator(); dsIt.hasNext();) {
				DataMiningDataset ds = (DataMiningDataset) dsIt.next();
				String options = ds.getOptions();
				if (options == null || options.equals("")) {
					options = "header = TRUE, sep = \",\"";
				}
				if (ds.getType().equalsIgnoreCase("file")) {
					// tries to get it from user workspace
					REXP datasetNameInR = re.eval(ds.getName());
					if (datasetNameInR == null) {
						File fileDSDir = new File(DataMiningDatasetUtils.getUserResourcesPath(profile) + ds.getName());
						// /find file in dir
						File[] dsfiles = fileDSDir.listFiles();
						if (dsfiles != null && dsfiles.length != 0) {
							String fileDSPath = dsfiles[0].getPath();

							fileDSPath = fileDSPath.replaceAll("\\\\", "/");

							String stringToEval = ds.getName() + "<-read." + ds.getReadType() + "(\"" + fileDSPath + "\"," + options + ");";
							re.eval(stringToEval);
							REXP dataframe = re.eval(ds.getName());
						}
					} else {
						// use it!
						logger.debug("dataset " + ds.getName() + " already loaded in user workspace!");
					}
				} else if (ds.getType().equalsIgnoreCase("spagobi_ds")) {
					// spagobi dataset content could change independently from
					// the engine, so it must be recalculated every time
					try {

						String csvToEval = DataMiningDatasetUtils.getFileFromSpagoBIDataset(paramsFilled, ds, profile);
						String stringToEval = ds.getName() + "<-read.csv(\"" + csvToEval + "\",header = TRUE, sep = \",\");";
						re.eval(stringToEval);

					} catch (IOException e) {
						logger.error(e.getMessage());
						throw e;
					}

				}
			}
		}
	}

	protected void updateDataset(DataMiningDataset ds) throws IOException {

		File fileDSDir = new File(DataMiningDatasetUtils.getUserResourcesPath(profile) + ds.getName());
		// /find file in dir
		File[] dsfiles = fileDSDir.listFiles();

		String fileDSPath = dsfiles[0].getPath();

		fileDSPath = fileDSPath.replaceAll("\\\\", "/");

		String stringToEval = ds.getName() + "<-read." + ds.getReadType() + "(\"" + fileDSPath + "\"," + ds.getOptions() + ");";
		re.eval(stringToEval);// updated!!!
	}
}
