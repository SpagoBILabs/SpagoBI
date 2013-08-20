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

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 * 
 */
@Path("/certificateddatasets")
public class GetCertificatedDatasets {

	static private Logger logger = Logger
			.getLogger(GetCertificatedDatasets.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDataSet(@Context HttpServletRequest req) {
		IDataSetDAO dataSetDao = null;
		List<IDataSet> dataSets;
		IEngUserProfile profile = (IEngUserProfile) req.getSession()
				.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		JSONObject JSONReturn = new JSONObject();
		JSONArray datasetsJSONArray = new JSONArray();
		try {
			dataSetDao = DAOFactory.getDataSetDAO();
			dataSetDao.setUserProfile(profile);
			String isPublic = req.getParameter("isPublic");
			Boolean isPubl = true;
			if(isPublic != null && isPublic.equals("false")){
				isPubl = false;
			}
			dataSets = dataSetDao.loadPagedDatasetList(0,
					Integer.MAX_VALUE, profile.getUserUniqueIdentifier()
							.toString(), isPubl);

			datasetsJSONArray = (JSONArray) SerializerFactory.getSerializer(
					"application/json").serialize(dataSets, null);

			JSONReturn.put("root", datasetsJSONArray);

		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while instatiating the dao", t);
		}
		return JSONReturn.toString();

	}

}
