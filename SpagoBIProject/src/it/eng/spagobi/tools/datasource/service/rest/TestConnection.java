/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.datasource.service.rest;


import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONFailure;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 */
@Path("/datasources")
public class TestConnection {

	static private String testDataSourceError = "Test data source fieled. SPAGOBIERRORCODE0015";

	
	static private Logger logger = Logger.getLogger(TestConnection.class);
	
	@POST
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String testDataSource(@javax.ws.rs.core.Context HttpServletRequest req) throws Exception {
		
		logger.debug("IN");
	
		String jndi = (String)req.getParameter("JNDI_URL");
		String url = (String)req.getParameter("CONNECTION_URL");
		String user = (String)req.getParameter("USER");
		String pwd = (String)req.getParameter("PASSWORD");
		String driver = (String)req.getParameter("DRIVER");
		String schemaAttr = (String)req.getParameter("CONNECTION_URL");


		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		String schema=(String)profile.getUserAttribute(schemaAttr);
		logger.debug("schema:"+ schema);
		Connection connection = null;

		try {
			if (jndi!=null){
					String jndiName = schema == null ? jndi : jndi + schema;
					logger.debug("Lookup JNDI name:"+ jndiName);
					Context ctx = new InitialContext();
				    DataSource ds = (DataSource) ctx.lookup(jndiName);
				    connection = ds.getConnection();
			}else{			
				    Class.forName(driver);
				    connection = DriverManager.getConnection(url, user, pwd);
			}
		 if (connection != null){//test ok
			 return (new JSONAcknowledge()).toString();
		 }else{
			 return (new JSONFailure(new Exception(testDataSourceError))).toString();
		 }
		} catch (Exception ex) {
			logger.error("Cannot fill response container", ex);

			try {
				return (new JSONFailure(ex)).toString();
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}
	}	
}
