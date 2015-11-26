/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.rest.publishers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * PublisherService Rest service can be used to display a jsp. It can be called passing the request parameter "PUBLISHER" containing the uri to the requested
 * resource. i.e. PUBLISHER=PUBLISHER=/WEB-INF/jsp/community/XXX.jsp
 *
 * @author franceschini
 *
 */

@Path("/publish")
public class PublisherService {
	@Context
	private HttpServletResponse servletResponse;

	private static Logger logger = Logger.getLogger(PublisherService.class);
	private static String PUBLISHER = "PUBLISHER";
	private static Map<String, String> urls;

	@GET
	public void publish(@Context HttpServletRequest req) {

		try {

			urls = new HashMap<String, String>();

			urls.put("MULTITENANT_MANAGEMENT_HOME", "/WEB-INF/jsp/tools/multitenant/multitenantManagement.jsp");
			urls.put("LIST_DATASOURCE_HOME", "/WEB-INF/jsp/tools/datasource/listDataSource.jsp");
			urls.put("MEASURES_CATALOGUE_HOME", "/WEB-INF/jsp/tools/measure/measuresCatalogue.jsp");
			urls.put("LAYER_CATALOGUE_HOME", "/WEB-INF/jsp/tools/layer/layerCatalogue.jsp");
			urls.put("LIST_OF_VALUES_HOME", "/WEB-INF/jsp/behaviouralmodel/lov/listOfValues.jsp");
			urls.put("ANALYTICAL_DRIVERS_HOME", "/WEB-INF/jsp/behaviouralmodel/analyticaldriver/analyticalDriver.jsp");
			urls.put("MANAGE_COMMUNITY_HOME", "/WEB-INF/jsp/community/ManageCommunity.jsp");
			urls.put("HIERARCHIES_EDITOR_HOME", "/WEB-INF/jsp/tools/hierarchieseditor/hierarchiesEditor.jsp");

			HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);

			HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

			String publisher = request.getParameter(PUBLISHER);
			if (publisher != null) {
				request.getRequestDispatcher(urls.get(publisher)).forward(request, response);
			}

		} catch (Exception e) {
			logger.error("Error forwarding request", e);
		}
	}
}
