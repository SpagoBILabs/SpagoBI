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
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.services.AbstractWorksheetEngineAction;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.utilities.assertion.Assert;
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

	// PROPERTIES TO LOOK FOR INTO THE FIELDS
	public static final String PROPERTY_VISIBLE = "visible";
	public static final String PROPERTY_IS_SEGMENT_ATTRIBUTE = "isSegmentAttribute";
	public static final String PROPERTY_IS_MANDATORY_MEASURE = "isMandatoryMeasure";
	public static final String PROPERTY_AGGREGATION_FUNCTION = "aggregationFunction";

	public void service(SourceBean request, SourceBean response)  {

		JSONObject resultsJSON;

		logger.debug("IN");

		try {		
			super.service(request, response);	

			WorksheetEngineInstance engineInstance = this.getEngineInstance();
			Assert.assertNotNull(engineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			IDataSet dataset = engineInstance.getDataSet();
			Assert.assertNotNull(dataset, "The engine instance is missing the dataset!!");
			IMetaData metadata = dataset.getMetadata();
			Assert.assertNotNull(metadata, "No metadata retrieved by the dataset");

			JSONArray fieldsJSON = writeFields(metadata);
			logger.debug("Metadata read:");
			logger.debug(fieldsJSON);
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

		int fieldCount = metadata.getFieldCount();
		logger.debug("Number of fields = " + fieldCount);
		Assert.assertTrue(fieldCount > 0, "Dataset has no fields!!!");

		for (int i = 0; i < fieldCount; i++) {
			IFieldMetaData fieldMetaData = metadata.getFieldMeta(i);
			Assert.assertNotNull(fieldMetaData, "Field metadata for position " + i + " not found.");
			logger.debug("Evaluating field with name [" + fieldMetaData.getName() + "], alias [" + fieldMetaData.getAlias() + "] ...");

			Object propertyRawValue = fieldMetaData.getProperty(PROPERTY_VISIBLE);
			logger.debug("Read property " + PROPERTY_VISIBLE + ": its value is [" + propertyRawValue + "]");
			if (propertyRawValue != null
					&& (propertyRawValue instanceof Boolean)
					&& ((Boolean) propertyRawValue).booleanValue() == false) {
				logger.debug("The field is not visible");
				continue;
			} else {
				logger.debug("The field is visible");
			}

			String fieldName = getFieldName(fieldMetaData);
			String fieldHeader = getFieldAlias(fieldMetaData);

			JSONObject fieldMetaDataJSON = new JSONObject();
			fieldMetaDataJSON.put("id", fieldName);						
			fieldMetaDataJSON.put("alias", fieldHeader);

			FieldType type = fieldMetaData.getFieldType();
			logger.debug("The field type is " + type.name());
			switch (type) {
			case ATTRIBUTE:
				Object isSegmentAttributeObj = fieldMetaData.getProperty(PROPERTY_IS_SEGMENT_ATTRIBUTE);
				logger.debug("Read property " + PROPERTY_IS_SEGMENT_ATTRIBUTE + ": its value is [" + propertyRawValue + "]");
				String attributeNature = (isSegmentAttributeObj != null
						&& (isSegmentAttributeObj instanceof Boolean)
						&& ((Boolean) isSegmentAttributeObj).booleanValue()) ? "segment_attribute" : "attribute";
				logger.debug("The nature of the attribute is recognized as " + attributeNature);
				fieldMetaDataJSON.put("nature", attributeNature);
				fieldMetaDataJSON.put("funct", AggregationFunctions.NONE);
				fieldMetaDataJSON.put("iconCls", attributeNature);
				break;
			case MEASURE:
				Object isMandatoryMeasureObj = fieldMetaData.getProperty(PROPERTY_IS_MANDATORY_MEASURE);
				logger.debug("Read property " + PROPERTY_IS_MANDATORY_MEASURE + ": its value is [" + isMandatoryMeasureObj + "]");
				String measureNature = (isMandatoryMeasureObj != null
						&& (isMandatoryMeasureObj instanceof Boolean)
						&& ((Boolean) isMandatoryMeasureObj).booleanValue()) ? "mandatory_measure" : "measure";
				logger.debug("The nature of the measure is recognized as " + measureNature);
				fieldMetaDataJSON.put("nature", measureNature);
				String aggregationFunction = (String) fieldMetaData.getProperty(PROPERTY_AGGREGATION_FUNCTION);
				logger.debug("Read property " + PROPERTY_AGGREGATION_FUNCTION + ": its value is [" + aggregationFunction + "]");
				fieldMetaDataJSON.put("funct", AggregationFunctions.get(aggregationFunction).getName());
				fieldMetaDataJSON.put("iconCls", measureNature);
				String decimalPrecision= (String) fieldMetaData.getProperty(IMetaData.DECIMALPRECISION);
				if(decimalPrecision!=null){
					fieldMetaDataJSON.put("precision", decimalPrecision);
				}else{
					fieldMetaDataJSON.put("precision", "2");
				}
				break;
			}
			fieldsMetaDataJSON.put(fieldMetaDataJSON);
		}

		return fieldsMetaDataJSON;

	}

	protected String getFieldAlias(IFieldMetaData fieldMetaData) {
		String fieldAlias = fieldMetaData.getAlias() != null ? fieldMetaData.getAlias() : fieldMetaData.getName();
		return fieldAlias;
	}

	protected String getFieldName(IFieldMetaData fieldMetaData) {
		String fieldName = fieldMetaData.getName();
		return fieldName;
	}

}
