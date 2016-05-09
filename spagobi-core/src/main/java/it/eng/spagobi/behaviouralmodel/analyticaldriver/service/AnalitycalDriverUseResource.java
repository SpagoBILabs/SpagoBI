/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.behaviouralmodel.analyticaldriver.service;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IParameterUseDAO;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.constants.AdmintoolsConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.rest.RestUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Lazar Kostic (lazar.kostic@mht.net)
 */
@Path("/analyticalDriverUse")
public class AnalitycalDriverUseResource {

	static private Logger logger = Logger.getLogger(AnalitycalDriverUseResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@QueryParam("adid") Integer adid) {

		logger.debug("IN");

		IParameterUseDAO parametersUseDAO = null;
		List<ParameterUse> parametersUse;
		JSONObject parametersUseJSON = new JSONObject();

		IRoleDAO rolesDAO = null;
		List<Role> allRoles;
		List<Role> freeRoles;

		try {
			parametersUseDAO = DAOFactory.getParameterUseDAO();
			parametersUse = parametersUseDAO.loadParametersUseByParId(adid);

			rolesDAO = DAOFactory.getRoleDAO();
			allRoles = rolesDAO.loadAllRoles();
			freeRoles = rolesDAO.loadAllFreeRolesForInsert(adid);

			parametersUseJSON = serializeParametersUse(parametersUse, allRoles, freeRoles);

			logger.debug("OUT: Returned analitical driver uses");

		} catch (Exception e) {

			logger.debug("OUT: Exception in returning analitical driver uses");
			throw new SpagoBIServiceException("analyticalDriverUse", "There is the problem with get", e);
		}

		return parametersUseJSON.toString();

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req) {

		logger.debug("IN");

		ParameterUse parameterUse = null;

		try {
			JSONObject requestBodyJSON = RestUtilities.readBodyAsJSONObject(req);
			String id = (String) requestBodyJSON.opt("USEID");
			int intId = Integer.parseInt(id);
			parameterUse = DAOFactory.getParameterUseDAO().loadByUseID(intId);

			DAOFactory.getParameterUseDAO().eraseParameterUse(parameterUse);
			logger.debug("OUT: Deleted analitical driver use");

			return Response.ok().build();

		} catch (Exception e) {

			logger.debug("OUT: Exception in deleting analitical driver use");
			throw new SpagoBIServiceException("analyticalDriverUse", "There is the problem with delete", e);

		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String post(@Context HttpServletRequest req) {

		logger.debug("IN");

		try {
			JSONObject reqJsonObject = RestUtilities.readBodyAsJSONObject(req);
			IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();

			ParameterUse parameterUse = recoverParameterUseDetails(reqJsonObject);
			parameterUseDAO.insertParameterUse(parameterUse);

			parameterUse = reloadParameterUse(parameterUse.getLabel(), parameterUse.getId());

			logger.debug("OUT: Posted analitical driver use");

			JSONObject parameterUseIdentifier = new JSONObject();
			parameterUseIdentifier.put("USEID", parameterUse.getUseID());

			return parameterUseIdentifier.toString();
		} catch (Exception e) {

			logger.debug("OUT: Exception in posting analitical driver use");
			throw new SpagoBIServiceException("analyticalDriverUse", "There is the problem with post", e);

		}

	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String put(@Context HttpServletRequest req) {

		logger.debug("IN");

		try {
			JSONObject requestBodyJSON = RestUtilities.readBodyAsJSONObject(req);
			IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();

			ParameterUse parameterUse = recoverParameterUseDetails(requestBodyJSON);
			parameterUseDAO.modifyParameterUse(parameterUse);

			parameterUse = reloadParameterUse(parameterUse.getLabel(), parameterUse.getId());

			logger.debug("OUT: Put analitical driver use");

			JSONObject parameterUseIdentifier = new JSONObject();
			parameterUseIdentifier.put("USEID", parameterUse.getUseID());

			return parameterUseIdentifier.toString();
		} catch (Exception e) {

			logger.debug("OUT: Exception in putting analitical driver use");
			throw new SpagoBIServiceException("analyticalDriverUse", "There is the problem with put", e);

		}

	}

	private JSONObject serializeParametersUse(List<ParameterUse> parametersUse, List<Role> allRoles, List<Role> freeRoles) {

		logger.debug("IN");

		JSONObject parametersUseJSON = new JSONObject();
		JSONArray parametersUseJSONArray = new JSONArray();
		JSONArray rolesJSONArray = new JSONArray();
		JSONArray freeRolesJSONArray = new JSONArray();

		try {
			parametersUseJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(parametersUse, null);
			parametersUseJSON.put("ADUSE", parametersUseJSONArray);

			rolesJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(allRoles, null);
			parametersUseJSON.put("ROLES", rolesJSONArray);

			freeRolesJSONArray = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(freeRoles, null);
			parametersUseJSON.put("FREEROLES", freeRolesJSONArray);

			logger.debug("OUT: Serialized analitical driver use");

		} catch (Exception e) {

			logger.debug("OUT: Exception in serializing analitical driver use");
			throw new SpagoBIServiceException("analyticalDriverUse", "There is a serialize problem", e);

		}

		return parametersUseJSON;
	}

	private ParameterUse recoverParameterUseDetails(JSONObject requestBodyJSON) throws EMFUserError {

		logger.debug("IN");

		ParameterUse parameterUse = new ParameterUse();
		Integer id = -1;
		String idStr = (String) requestBodyJSON.opt("USEID");
		if (idStr != null && !idStr.equals("")) {

			id = new Integer(idStr);

		}

		String idd = (String) requestBodyJSON.opt("ID");
		String lovid = (String) requestBodyJSON.opt("LOVID");
		String label = (String) requestBodyJSON.opt("LABEL");
		String name = (String) requestBodyJSON.opt("NAME");
		String desc = (String) requestBodyJSON.opt("DESCRIPTION");
		String pickup = (String) requestBodyJSON.opt("DEFAULTFORMULA");
		Boolean manualInput = requestBodyJSON.optBoolean("MANUALINPUT");
		String selectionType = (String) requestBodyJSON.opt("SELECTIONTYPE");
		Boolean expendableBool = requestBodyJSON.optBoolean("EXPENDABLE");
		String defaultlovid = (String) requestBodyJSON.opt("DEFAULTLOVID");
		JSONArray roleslist = requestBodyJSON.optJSONArray("ROLESLIST");
		JSONArray constlist = requestBodyJSON.optJSONArray("CONSTLIST");

		Domain selection = DAOFactory.getDomainDAO().loadDomainByCodeAndValue("SELECTION_TYPE", selectionType);

		Assert.assertNotNull(id, "Id cannot be null");
		Assert.assertNotNull(idd, "Idd cannot be null");
		Assert.assertNotNull(label, "Label cannot be null");
		Assert.assertNotNull(name, "Name cannot be null");
		Assert.assertNotNull(desc, "Description cannot be null");
		Assert.assertNotNull(selectionType, "Selection type cannot be null");

		parameterUse.setId(new Integer(idd));
		parameterUse.setUseID(id);

		parameterUse.setLabel(label);
		parameterUse.setName(name);
		parameterUse.setDescription(desc);
		parameterUse.setMultivalue(false);

		if (defaultlovid == "") {

			parameterUse.setIdLovForDefault(-1);

		} else {

			parameterUse.setIdLovForDefault(new Integer(defaultlovid));

		}

		if (pickup == "null" || pickup == "") {

			parameterUse.setDefaultFormula(null);

		} else {

			parameterUse.setDefaultFormula(pickup);

		}

		if (expendableBool) {

			parameterUse.setMaximizerEnabled(true);

		} else {

			parameterUse.setMaximizerEnabled(false);

		}

		if (selectionType == "") {

			parameterUse.setSelectionType(null);

		} else {

			parameterUse.setSelectionType(selection.getValueCd());
			if (selection.getValueCd().equals("LIST") || selection.getValueCd().equals("COMBOBOX")) {

				parameterUse.setMultivalue(false);

			} else {

				parameterUse.setMultivalue(true);

			}
		}

		if (!manualInput) {

			parameterUse.setManualInput(new Integer(0));

		} else {

			parameterUse.setManualInput(new Integer(1));
		}

		if (lovid == null || lovid == "") {

			parameterUse.setIdLov(new Integer(-1));

		} else {

			parameterUse.setIdLov(new Integer(lovid));

		}

		List<Role> roles = new ArrayList();

		for (int i = 0; i < roleslist.length(); i++) {

			try {
				JSONObject obj = (JSONObject) roleslist.get(i);
				String roleName = obj.getString("name");
				roles.add(DAOFactory.getRoleDAO().loadByName(roleName));

			} catch (JSONException e) {
				logger.error("Cannot fill response container", e);
				throw new SpagoBIRuntimeException("Cannot fill response container", e);
			}

		}

		List<Check> checks = new ArrayList();

		for (int i = 0; i < constlist.length(); i++) {

			try {
				JSONObject obj = (JSONObject) constlist.get(i);
				String constcd = obj.getString("VALUE_CD");
				List<Check> existingChecks = DAOFactory.getChecksDAO().loadAllChecks();

				for (Check c : existingChecks) {

					if (constcd.equals(c.getValueTypeCd()))
						checks.add(c);

				}

			} catch (JSONException e) {
				logger.error("Cannot fill response container", e);
				throw new SpagoBIRuntimeException("Cannot fill response container", e);
			}

		}

		parameterUse.setAssociatedRoles(roles);
		parameterUse.setAssociatedChecks(checks);

		logger.debug("OUT: Recovered analitical driver use");

		return parameterUse;

	}

	private ParameterUse reloadParameterUse(String label, int adid) throws EMFInternalError {
		if (label == null || label.trim().equals(""))
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Invalid input data for method relaodParameter in DetailParameterModule");
		ParameterUse parameterUse = null;
		try {
			IParameterUseDAO parameterUseDAO = DAOFactory.getParameterUseDAO();
			List parameterUses = parameterUseDAO.loadParametersUseByParId(adid);
			Iterator it = parameterUses.iterator();
			while (it.hasNext()) {
				ParameterUse aParameterUse = (ParameterUse) it.next();
				if (aParameterUse.getLabel().equals(label)) {
					parameterUse = aParameterUse;
					break;
				}
			}
		} catch (EMFUserError e) {
			SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DetailParameterModule", "reloadParameter", "Cannot reload Parameter", e);
		}
		/*
		 * if (parameter == null) { SpagoBITracer.major(AdmintoolsConstants.NAME_MODULE, "DetailParameterModule", "reloadParameter", "Parameter with label '" +
		 * label + "' not found."); parameter = createNewParameter(); }
		 */
		return parameterUse;
	}
}
