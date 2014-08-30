/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.compute;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.datamining.DataMiningEngineConfig;
import it.eng.spagobi.engines.datamining.DataMiningEngineInstance;
import it.eng.spagobi.engines.datamining.model.DataMiningDataset;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class DataMiningDatasetUtils {
	static private Logger logger = Logger.getLogger(DataMiningDatasetUtils.class);

	public static final String UPLOADED_FILE_PATH = DataMiningEngineConfig.getInstance().getEngineConfig().getResourcePath() + "\\datamining\\";

	public static Boolean areDatasetsProvided(DataMiningEngineInstance dataminingInstance) {
		Boolean areProvided = true;

		if (dataminingInstance.getDatasets() != null && !dataminingInstance.getDatasets().isEmpty()) {
			for (Iterator dsIt = dataminingInstance.getDatasets().iterator(); dsIt.hasNext();) {
				DataMiningDataset ds = (DataMiningDataset) dsIt.next();
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

	public static String getFileFromSpagoBIDataset(DataMiningDataset ds, IEngUserProfile profile) {
		String filePath = "";
		IDataSetDAO dataSetDao;
		try {
			dataSetDao = DAOFactory.getDataSetDAO();
			dataSetDao.setUserProfile(profile);
			IDataSet spagobiDataset = dataSetDao.loadDataSetByLabel(ds.getSpagobiLabel());
			spagobiDataset.loadData();
			DataStore dataStore = (DataStore) spagobiDataset.getDataStore();

			filePath = UPLOADED_FILE_PATH + "temp\\" + ds.getName() + "\\" + ds.getSpagobiLabel() + ".csv";
			File csvFile = new File(filePath);
			csvFile.createNewFile();

			CSVWriter writer = new CSVWriter(new FileWriter(filePath), ',');
			writeColumns(dataStore, writer);
			writeFields(dataStore, writer);
			writer.flush();
			writer.close();

			filePath = filePath.replaceAll("\\\\", "/");
			return filePath;

		} catch (Exception e) {
			logger.error(e);
		}
		return filePath;

	}

	public static void writeColumns(DataStore dataStore, CSVWriter writer) {
		logger.debug("IN");
		String col = "";
		List<String> fieldsList = new ArrayList<String>();

		for (int j = 0; j < dataStore.getMetaData().getFieldCount(); j++) {
			IFieldMetaData fieldMetaData = dataStore.getMetaData().getFieldMeta(j);
			String fieldHeader = fieldMetaData.getAlias() != null ? fieldMetaData.getAlias() : fieldMetaData.getName();
			col += fieldHeader + ",";
		}
		writer.writeNext(col.split(","));

	}

	public static void writeFields(DataStore dataStore, CSVWriter writer) {
		Iterator records = dataStore.iterator();
		while (records.hasNext()) {
			IRecord record = (IRecord) records.next();
			String row = "";
			for (int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
				IField field = record.getFieldAt(i);

				if (field.getValue() != null) {
					row += field.getValue().toString() + ",";
				} else {
					row += "" + ",";
				}
			}

			writer.writeNext(row.split(","));
		}

	}
}
