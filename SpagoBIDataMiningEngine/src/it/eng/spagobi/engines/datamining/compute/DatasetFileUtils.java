/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spagobi.engines.datamining.DataMiningEngineConfig;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.model.FileDataset;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DatasetFileUtils {
	static private Logger logger = Logger.getLogger(DatasetFileUtils.class);

	public static final String UPLOADED_FILE_PATH = DataMiningEngineConfig.getInstance().getEngineConfig().getResourcePath() + "\\datamining\\";

	public static Boolean areDatasetsProvided(DataMiningEngineInstance dataminingInstance) {
		Boolean areProvided = true;

		if (dataminingInstance.getDatasets() != null && !dataminingInstance.getDatasets().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getDatasets().iterator(); dsIt.hasNext();) {
				FileDataset ds = (FileDataset) dsIt.next();
				File fileDSDir = new File(UPLOADED_FILE_PATH + ds.getName());
				if (fileDSDir != null) {
					File[] dsfiles = fileDSDir.listFiles();
					if (dsfiles == null || dsfiles.length == 0) {
						areProvided = false;
					}
				} else {
					areProvided = false;
				}
			}

		}
		return areProvided;
	}
}
