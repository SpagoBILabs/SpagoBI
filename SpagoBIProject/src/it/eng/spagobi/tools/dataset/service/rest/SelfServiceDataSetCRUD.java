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
package it.eng.spagobi.tools.dataset.service.rest;

import it.eng.spago.base.SourceBeanException;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.rest.RestUtilities;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Antonella Giachino (antonella.giachino@eng.it)
 * 
 */
@Path("/selfservicedataset")
public class SelfServiceDataSetCRUD {

	static private Logger logger = Logger.getLogger(SelfServiceDataSetCRUD.class);
	static private String deleteNullIdDataSourceError = "error.mesage.description.data.source.cannot.be.null";
	static private String deleteInUseDSError = "error.mesage.description.data.source.deleting.inuse";
	static private String canNotFillResponseError = "error.mesage.description.generic.can.not.responce";
	static private String saveDuplicatedDSError = "error.mesage.description.data.source.saving.duplicated";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDataSet(@Context HttpServletRequest req) {
		IDataSetDAO dataSetDao = null;
		IDomainDAO domaindao = null;
		List<IDataSet> dataSets;
		List<Domain> categories = null;
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		JSONObject JSONReturn = new JSONObject();
		JSONArray datasetsJSONArray = new JSONArray();
		try {
			dataSetDao = DAOFactory.getDataSetDAO();
			dataSetDao.setUserProfile(profile);
			dataSets = dataSetDao.loadAllActiveDataSets();		
			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(dataSets, null);
			
			//sets action to modify dataset
			JSONArray actions = new JSONArray();
			JSONObject detailAction = new JSONObject();
			detailAction.put("name", "detail");
			detailAction.put("description", "Dataset detail");
			actions.put(detailAction);
			JSONObject deleteAction = new JSONObject();
			deleteAction.put("name", "delete");
			deleteAction.put("description", "Delete dataset");
			actions.put(deleteAction);
//			JSONObject worksheetAction = new JSONObject();
//			worksheetAction.put("name", "worksheet");
//			worksheetAction.put("description", "Show Worksheet");
//			actions.put(worksheetAction);
//			JSONObject geoAction = new JSONObject();
//			geoAction.put("name", "worksheet");
//			geoAction.put("description", "Show Geo");
//			actions.put(geoAction);
			JSONArray datasetsJSONReturn = new JSONArray();
	
			
			for(int i = 0; i < datasetsJSONArray.length(); i++) {
				JSONObject datasetJSON = datasetsJSONArray.getJSONObject(i);				
				datasetJSON.put("actions", actions);
				datasetsJSONReturn.put(datasetJSON);
			}
			/*categories are getted with domains/listValueDescriptionByType service
			domaindao = DAOFactory.getDomainDAO();
			categories = domaindao.loadListDomainsByType("CATEGORY_TYPE");
			JSONArray categoriesJSONArray = new JSONArray();
			if (categories != null) {
				categoriesJSONArray = (JSONArray) SerializerFactory.getSerializer(
						"application/json").serialize(categories, null);
				JSONReturn.put("categories", categoriesJSONArray);
			}
			*/

			JSONReturn.put("root", datasetsJSONReturn);


		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while instatiating the dao", t);
		}
		return JSONReturn.toString();

	}


	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteDataSource(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		HashMap<String, String> logParam = new HashMap();

		try {
			JSONObject requestBodyJSON = RestUtilities.readBodyAsJSONObject(req);
			String id = (String) requestBodyJSON.opt("DATASOURCE_ID");
			Assert.assertNotNull(id,deleteNullIdDataSourceError );
			// if the ds is associated with any BIEngine or BIObjects, creates
			// an error
			boolean bObjects = DAOFactory.getDataSourceDAO().hasBIObjAssociated(id);
			boolean bEngines = DAOFactory.getDataSourceDAO().hasBIEngineAssociated(id);
			if (bObjects || bEngines) {
				HashMap params = new HashMap();
				logger.debug(deleteInUseDSError);
				updateAudit(req, profile, "DATA_SOURCE.DELETE", null, "ERR");
				return ( ExceptionUtilities.serializeException(deleteInUseDSError,null));
			}

			IDataSource ds = DAOFactory.getDataSourceDAO().loadDataSourceByID(new Integer(id));
			DAOFactory.getDataSourceDAO().eraseDataSource(ds);
			logParam.put("TYPE", ds.getJndi());
			logParam.put("NAME", ds.getLabel());
			updateAudit(req, profile, "DATA_SOURCE.DELETE", null, "OK");
			return ("");
		} catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SOURCE.DELETE", null, "ERR");
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String saveDataSource(@Context HttpServletRequest req) {
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		try {
			
			JSONObject requestBodyJSON = RestUtilities.readBodyAsJSONObject(req);
			
			
			
			IDataSourceDAO dao=DAOFactory.getDataSourceDAO();
			dao.setUserProfile(profile);
			DataSource dsNew = recoverDataSourceDetails(requestBodyJSON);

			HashMap<String, String> logParam = new HashMap();
			logParam.put("JNDI",dsNew.getJndi());
			logParam.put("NAME",dsNew.getLabel());
			logParam.put("URL",dsNew.getUrlConnection());



			if (dsNew.getDsId()==-1) {
				//if a ds with the same label not exists on db ok else error
				if (DAOFactory.getDataSourceDAO().loadDataSourceByLabel(dsNew.getLabel()) != null){
					updateAudit(req, profile, "DATA_SOURCE.ADD", logParam, "KO");
					throw new SpagoBIRuntimeException(saveDuplicatedDSError);
				}	 		
				dao.insertDataSource(dsNew);
							
				IDataSource tmpDS = dao.loadDataSourceByLabel(dsNew.getLabel());
				dsNew.setDsId(tmpDS.getDsId());
				updateAudit(req, profile, "DATA_SOURCE.ADD", logParam, "OK");
			} else {				
				//update ds
				dao.modifyDataSource(dsNew);
				updateAudit(req, profile, "DATA_SOURCE.MODIFY", logParam, "OK");
			}  
					
			return ("{DATASOURCE_ID:"+dsNew.getDsId()+" }");
		} catch (SpagoBIRuntimeException ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SOURCE.DELETE", null, "ERR");
			logger.debug(ex.getMessage());
			try {
				return ( ExceptionUtilities.serializeException(ex.getMessage(),null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		} catch (Exception ex) {
			logger.error("Cannot fill response container", ex);
			updateAudit(req, profile, "DATA_SOURCE.DELETE", null, "ERR");
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}
	}


	private static void updateAudit(HttpServletRequest request,
			IEngUserProfile profile, String action_code,
			HashMap<String, String> parameters, String esito) {
		try {
			AuditLogUtilities.updateAudit(request, profile, action_code,
					parameters, esito);
		} catch (Exception e) {
			logger.debug("Error writnig audit", e);
		}
	}
	
	private JSONObject serializeDatasets(List<IDataSet> dataSets) throws SerializationException, JSONException {
		JSONObject dataSetsJSON = new JSONObject();
		JSONArray dataSetsJSONArray = new JSONArray();
		if (dataSets != null) {
			dataSetsJSONArray = (JSONArray) SerializerFactory.getSerializer(
					"application/json").serialize(dataSets, null);
			dataSetsJSON.put("root", dataSetsJSONArray);
		}
		return dataSetsJSON;
	}
	
	private DataSource recoverDataSourceDetails (JSONObject requestBodyJSON) throws EMFUserError, SourceBeanException, IOException  {
		DataSource ds  = new DataSource();
		Integer id=-1;
		String idStr = (String)requestBodyJSON.opt("DATASOURCE_ID");
		if(idStr!=null && !idStr.equals("")){
			id = new Integer(idStr);
		}
		Integer dialectId = Integer.valueOf((String)requestBodyJSON.opt("DIALECT_ID"));	
		String description = (String)requestBodyJSON.opt("DESCRIPTION");	
		String label = (String)requestBodyJSON.opt("DATASOURCE_LABEL");
		String jndi = (String)requestBodyJSON.opt("JNDI_URL");
		String url = (String)requestBodyJSON.opt("CONNECTION_URL");
		String user = (String)requestBodyJSON.opt("USER");
		String pwd = (String)requestBodyJSON.opt("PASSWORD");
		String driver = (String)requestBodyJSON.opt("DRIVER");
		String schemaAttr = (String)requestBodyJSON.opt("CONNECTION_URL");
		String multiSchema = (String)requestBodyJSON.opt("MULTISCHEMA");
		Boolean isMultiSchema = false;
		if(multiSchema!=null && multiSchema.equals("true")){
			isMultiSchema = true;
		}
		
		ds.setDsId(id.intValue());
		ds.setDialectId(dialectId);
		ds.setLabel(label);
		ds.setDescr(description);
		ds.setJndi(jndi);
		ds.setUrlConnection(url);
		ds.setUser(user);
		ds.setPwd(pwd);
		ds.setDriver(driver);
		ds.setSchemaAttribute(schemaAttr);
		ds.setMultiSchema(isMultiSchema);
				
		return ds;
	}
	

	
	private String serializeException(Exception e) throws JSONException{
		return ExceptionUtilities.serializeException(e.getMessage(),null);
	}


}
