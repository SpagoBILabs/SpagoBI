/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.services.core.datamart;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.dao.ICalculatedFieldsDAO;
import it.eng.qbe.model.structure.DataMartCalculatedField;
import it.eng.qbe.model.structure.DataMartEntity;
import it.eng.qbe.model.structure.DataMartModelStructure;
import it.eng.qbe.query.serializer.json.QuerySerializationConstants;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;


/**
 * The Class ExecuteQueryAction.
 */
public class SaveTreeAction extends AbstractQbeEngineAction {	
	
	public static final String SERVICE_NAME = "SAVE_TREE_ACTION";
	public String getActionName(){return SERVICE_NAME;}
	
	
	// INPUT PARAMETERS
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(SaveTreeAction.class);
   
    
	
	public void service(SourceBean request, SourceBean response)  {				
		
		ICalculatedFieldsDAO calculatedFieldsDAO;
		String dataMartName;
		Map calculatedFields;
				
		logger.debug("IN");
		
		try {
		
			super.service(request, response);		
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
						
			calculatedFieldsDAO = DAOFactory.getCalculatedFieldsDAO();
			Assert.assertNotNull(calculatedFieldsDAO, "Impossible to retrive a valid instance of CalculatedFieldsDAO");
			
			
			dataMartName = getDataSource().getName();
			logger.debug("DataMart name [" + dataMartName +"]");
			Assert.assertNotNull(dataMartName, "Datamart name cannot be null in order to execute " + this.getActionName() + " service");
			
			
			calculatedFields = getDataSource().getDataMartModelStructure().getCalculatedFields();
			Assert.assertNotNull(calculatedFields, "Calculated field map cannot be null in order to execute " + this.getActionName() + " service");
			
			List datamartsName = getDataSource().getDatamartNames();
			if (datamartsName.size() == 1) {
				String datamartName = (String) datamartsName.get(0);
				logger.debug("Saving calculated fields into datamart [" + datamartName + "] ...");
				calculatedFieldsDAO.saveCalculatedFields(datamartName, calculatedFields);
				logger.debug("Calculated fileds saved succesfully into datamart [" + datamartName + "].");
			} else {
				for (int i = 0; i < datamartsName.size(); i++) {
					String datamartName = (String) datamartsName.get(i);
					Map datamartCalcultedField = getCalculatedFieldsForDatamart(getDataSource().getDataMartModelStructure(), calculatedFields, datamartName);
					logger.debug("Saving calculated fields into datamart [" + datamartName + "]...");
					calculatedFieldsDAO.saveCalculatedFields(datamartName, datamartCalcultedField);
					logger.debug("Calculated fileds saved succesfully into datamart [" + datamartName + "].");
				}
			}
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}			
	}


	/**
	 * The input map contains all the calculated fields defined into the entire datamart model structure. 
	 * This method returns the calculated field defined for a single datamart (used in case of composite datasource, i.e. more than 1 datamart).
	 * @param dataMartModelStructure The datamart model structure
	 * @param calculatedFields All the calculated fields defined into the entire datamart model structure
	 * @param datamartName The datamart for which the calculated fields should be retrieved
	 * @return the calculated field defined for the specified datamart 
	 */
	private Map getCalculatedFieldsForDatamart(DataMartModelStructure dataMartModelStructure, Map calculatedFields, String datamartName) {
		Map toReturn = new HashMap();
		Set keys = calculatedFields.keySet();
		Iterator keysIt = keys.iterator();
		while (keysIt.hasNext()) {
			String entityUniqueName = (String) keysIt.next();
			DataMartEntity dataMartEntity = dataMartModelStructure.getEntity(entityUniqueName);
			DataMartEntity dataMartRootEntity = dataMartEntity.getRoot();
			List rootEntities = dataMartModelStructure.getRootEntities(datamartName);
			if (rootEntities.contains(dataMartRootEntity)) {
				toReturn.put(entityUniqueName, calculatedFields.get(entityUniqueName));
			}
		}
		return toReturn;
	}



	private DataMartCalculatedField deserialize(JSONObject fieldJSON) {
		DataMartCalculatedField field;
		String alias;
		String fieldType;
		
		String fieldUniqueName;		
		String group;
		String order;
		String funct;
		
		JSONObject fieldClaculationDescriptor;
		String type;
		String expression;
		
		boolean visible;
		boolean included;
		
		
		try {
			alias = fieldJSON.getString(QuerySerializationConstants.FIELD_ALIAS);
			fieldType = fieldJSON.getString(QuerySerializationConstants.FIELD_TYPE);
						
			fieldClaculationDescriptor = fieldJSON.getJSONObject("calculationDescriptor");
			type = fieldClaculationDescriptor.getString(QuerySerializationConstants.FIELD_TYPE);
			expression = fieldClaculationDescriptor.getString(QuerySerializationConstants.FIELD_EXPRESSION);
			
			field = new DataMartCalculatedField(alias, type, expression);
		} catch (Throwable t) {
			throw new SpagoBIEngineServiceException(getActionName(), "impossible to deserialize calculated field [" + fieldJSON.toString() + "]", t);
		}					
		
		
		return field;
	}
	
}