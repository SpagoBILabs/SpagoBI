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

/**
 * Object name
 * 
 * ManageConfig
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors
 * 
 * Monia Spinelli (monia.spinelli@eng.it)
 */
package it.eng.spagobi.commons.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.x.AbstractSpagoBIAction;
import it.eng.spagobi.chiron.serializer.SerializerFactory;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

public class ManageConfigService extends AbstractSpagoBIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// logger component
	private static Logger logger = Logger.getLogger(ManageDomainService.class);

	// Service parameter
	private final String MESSAGE_DET = "MESSAGE_DET";

	private static final String CONFIG_LIST = "CONFIG_LIST";
	private static final String CONFIG_DELETE = "CONFIG_DELETE";
	private static final String CONFIG_SAVE = "CONFIG_SAVE";

	protected IEngUserProfile profile = null;

	@Override
	public void doService() {
		IConfigDAO configDao;
		String serviceType;

		logger.debug("IN");

		try {
			configDao = DAOFactory.getSbiConfigDAO();
		} catch (EMFUserError e1) {
			logger.error(e1.getMessage(), e1);
			throw new SpagoBIServiceException(SERVICE_NAME, "Error occurred");
		}

		serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Parameter [" + MESSAGE_DET + "] is equal to ["
				+ serviceType + "]");

		if (serviceType != null) {
			if (serviceType.equalsIgnoreCase(CONFIG_LIST)) {
				doConfigList();
			} else if (serviceType.equalsIgnoreCase(CONFIG_DELETE)) {
				doDelete();
			} else if (serviceType.equalsIgnoreCase(CONFIG_SAVE)) {
				doSave();
			} else {
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Unable to execute service [" + serviceType + "]");
			}
		}

		logger.debug("OUT");

	}

	public void doSave() {

		logger.debug("IN");

		try {

			logger.debug("Save config");

			Config config = this.setConfig();
			DAOFactory.getSbiConfigDAO().setUserProfile(profile);
			DAOFactory.getSbiConfigDAO().saveConfig(config);
			JSONObject response = new JSONObject();
			response.put("ID", config.getId());
			writeBackToClient(new JSONSuccess(response));

		} catch (Throwable e) {
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Impossible to save config", e);
		} finally {
			logger.debug("OUT");
		}
	}

	public void doDelete() {
		try {
			logger.debug("Delete config");
			Integer id = this.getAttributeAsInteger("ID");
			DAOFactory.getSbiConfigDAO().delete(id);
			JSONObject response = new JSONObject();
			response.put("ID", id);
			writeBackToClient(new JSONSuccess(response));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving config data", e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Impossible to delete config", e);
		}
	}

	public void doConfigList() {
		try {
			logger.debug("Loaded config list");

			List<Config> configList = DAOFactory.getSbiConfigDAO().loadAllConfigParameters();

			JSONArray configListJSON = (JSONArray) SerializerFactory
					.getSerializer("application/json").serialize(configList,
							this.getLocale());

			// PaginatorIFace paginator = new GenericPaginator();

			// int numRows = 20;
			/*
			 * try { ConfigSingleton spagoconfig =
			 * ConfigSingleton.getInstance(); String lookupnumRows = (String)
			 * spagoconfig.getAttribute("SPAGOBI.LOOKUP.numberRows"); if
			 * (lookupnumRows != null) { numRows =
			 * Integer.parseInt(lookupnumRows); } } catch (Exception e) {
			 * numRows = 20;
			 * logger.error("Error while recovering number rows for " +
			 * "lookup from configuration, usign default 10", e); }
			 */
			// paginator.setPageSize(numRows);
			// logger.debug("setPageSize="+numRows);
			// ListIFace list = new GenericList();
			// list.setPaginator(paginator);

			JSONObject response = new JSONObject();
			response.put("response", configListJSON);

			writeBackToClient(new JSONSuccess(response));

		} catch (Throwable e) {
			logger.error("Exception occurred while retrieving config data", e);
			throw new SpagoBIServiceException(SERVICE_NAME,
					"Exception occurred while retrieving config data", e);
		}
	}

	public Config setConfig() {
		Config config = new Config();
		if(this.requestContainsAttribute("ID")){
			config.setId(this.getAttributeAsInteger("ID"));
		}
		config.setLabel(this.getAttributeAsString("LABEL"));
		config.setName(this.getAttributeAsString("NAME"));
		config.setDescription(this.getAttributeAsString("DESCRIPTION"));
		config.setActive(this.getAttributeAsBoolean("IS_ACTIVE"));
		config.setValueCheck(this.getAttributeAsString("VALUE_CHECK"));
		config.setValueTypeId(this.getAttributeAsInteger("VALUE_TYPE"));

		return config;

	}

}
