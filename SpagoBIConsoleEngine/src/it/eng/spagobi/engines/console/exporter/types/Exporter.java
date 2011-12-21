/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */

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
