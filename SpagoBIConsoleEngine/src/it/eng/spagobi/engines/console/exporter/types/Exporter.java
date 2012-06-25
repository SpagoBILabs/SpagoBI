/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.console.exporter.types;

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class Exporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(Exporter.class);

	IDataStore dataStore = null;
	Vector extractedFields = null;
	List<IFieldMetaData> extractedFieldsMetaData = null;
    
	public abstract Object export();
	
	public void setExtractedFields(Vector extractedFields) {
		this.extractedFields = extractedFields;
	}
	
	public IDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(IDataStore dataStore) {
		this.dataStore = dataStore;
	}
	
	public List<IFieldMetaData> getExtractedFieldsMetaData() {
		return extractedFieldsMetaData;
	}

	public void setExtractedFieldsMetaData(List<IFieldMetaData> extractedFieldsMetaData) {
		this.extractedFieldsMetaData = extractedFieldsMetaData;
	}
	
}
