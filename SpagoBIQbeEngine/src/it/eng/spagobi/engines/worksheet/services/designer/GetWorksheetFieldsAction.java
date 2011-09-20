/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.engines.worksheet.services.designer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.worksheet.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class GetWorksheetFieldsAction  extends AbstractWorksheetEngineAction {	

	private static final long serialVersionUID = -5874137232683097175L;
	
	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(GetWorksheetFieldsAction.class);

	public void service(SourceBean request, SourceBean response)  {

		JSONObject resultsJSON;
		
		logger.debug("IN");

		try {		
			super.service(request, response);	

			WorksheetEngineInstance engineInstance = this.getEngineInstance();
			IDataSet dataset = engineInstance.getDataSet();
			IMetaData metadata = dataset.getMetadata();
			
			JSONArray fieldsJSON = writeFields(metadata);
			resultsJSON = new JSONObject();
			resultsJSON.put("results", fieldsJSON);

			try {
				writeBackToClient( new JSONSuccess( resultsJSON ) );
			} catch (IOException e) {
				throw new SpagoBIEngineServiceException(getActionName(), "Impossible to write back the responce to the client [" + resultsJSON.toString(2)+ "]", e);
			}

		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	public JSONArray writeFields(IMetaData metadata) throws Exception {
		
		// field's meta
		JSONArray fieldsMetaDataJSON = new JSONArray();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			IFieldMetaData fieldMetaData = metadata.getFieldMeta(i);
			
			Object propertyRawValue = fieldMetaData.getProperty("visible");
			if (propertyRawValue != null
					&& (propertyRawValue instanceof Boolean)
					&& ((Boolean) propertyRawValue).booleanValue() == false) {
				continue;
			}
			
			String fieldName = getFieldName(fieldMetaData);
			String fieldHeader = getFieldAlias(fieldMetaData);
			
			JSONObject fieldMetaDataJSON = new JSONObject();
			fieldMetaDataJSON.put("id", fieldName);						
			fieldMetaDataJSON.put("alias", fieldHeader);
			
			FieldType type = fieldMetaData.getFieldType();
			switch (type) {
				case ATTRIBUTE:
					Boolean isSegmentAttribute = (Boolean) fieldMetaData.getProperty("isSegmentAttribute");
					fieldMetaDataJSON.put("nature", 
							isSegmentAttribute != null && isSegmentAttribute.booleanValue() ? "segment_attribute" : "attribute");
					fieldMetaDataJSON.put("funct", AggregationFunctions.NONE);
					fieldMetaDataJSON.put("iconCls", "attribute");
					break;
				case MEASURE:
					Boolean isMandatoryMeasure = (Boolean) fieldMetaData.getProperty("isMandatoryMeasure");
					fieldMetaDataJSON.put("nature", 
							isMandatoryMeasure != null && isMandatoryMeasure.booleanValue() ? "mandatory_measure" : "measure");
					String aggregationFunction = (String) fieldMetaData.getProperty("aggregationFunction");
					fieldMetaDataJSON.put("funct", AggregationFunctions.get(aggregationFunction).getName());
					fieldMetaDataJSON.put("iconCls", "measure");
					break;
			}
			fieldsMetaDataJSON.put(fieldMetaDataJSON);
		}
		
		return fieldsMetaDataJSON;
	
	}
	
	protected String getFieldAlias(IFieldMetaData fieldMetaData) {
		String fieldAlias = fieldMetaData.getAlias() != null? fieldMetaData.getAlias(): fieldMetaData.getName();
		return fieldAlias;
	}

	protected String getFieldName(IFieldMetaData fieldMetaData) {
		String fieldName = fieldMetaData.getName();
		return fieldName;
	}
	
}
