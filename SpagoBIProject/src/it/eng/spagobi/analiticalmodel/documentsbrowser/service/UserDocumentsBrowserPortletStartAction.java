/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.documentsbrowser.service;


import java.sql.Connection;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.JSONObject;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.services.PortletLoginAction;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.commons.utilities.HibernateUtil;
import it.eng.spagobi.commons.utilities.PortletUtilities;
import it.eng.spagobi.commons.utilities.UserUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIException;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class UserDocumentsBrowserPortletStartAction extends PortletLoginAction {
	
	// logger component
	private static Logger logger = Logger.getLogger(UserDocumentsBrowserPortletStartAction.class);
	
	public static final String LABEL_SUBTREE_NODE = "PATH_SUBTREE";
	public static final String HEIGHT = "HEIGHT";
	public static final String PORTLET = "PORTLET";
	

	public void service(SourceBean request, SourceBean response) throws Exception {
		
		String labelSubTreeNode = null;
		String height = null;
		String channelType;
		
		logger.debug("IN");
		//Start writing log in the DB
		Session aSession =null;
		try {
			aSession = HibernateUtil.currentSession();
			//Connection jdbcConnection = aSession.connection();
			Connection jdbcConnection = HibernateUtil.getConnection(aSession);
			IEngUserProfile profile = UserUtilities.getUserProfile();
			AuditLogUtilities.updateAudit(jdbcConnection,  profile, "activity.DocumentsBrowserMenu", null);
		} catch (HibernateException he) {
			throw new EMFUserError(EMFErrorSeverity.ERROR, 100);
		} finally {
			if (aSession!=null){
				if (aSession.isOpen()) aSession.close();
			}
		}
		//End writing log in the DB
		
		try {
			super.service(request, response);
			
			channelType = getRequestContainer().getChannelType();
			
			logger.info("[DAJS]:: channelType: " + channelType);
			
			if( PORTLET.equalsIgnoreCase(channelType) ) {
				logger.info("[DAJS]:: mode: " + PORTLET);
				PortletRequest portReq = PortletUtilities.getPortletRequest();
				logger.info("[DAJS]:: portReq: " + portReq);
				PortletPreferences prefs = portReq.getPreferences();
				logger.info("[DAJS]:: prefs: " + prefs);
				labelSubTreeNode = (String)prefs.getValue(LABEL_SUBTREE_NODE, "");
				logger.info("[DAJS]:: labelSubTreeNode: " + labelSubTreeNode);
				height = (String)prefs.getValue(HEIGHT, "600");
				logger.info("[DAJS]:: height: " + height);
				if (labelSubTreeNode != null && !labelSubTreeNode.trim().equals("")) {
					response.setAttribute("labelSubTreeNode", labelSubTreeNode);
					logger.info("[DAJS]:: attribute [labelSubTreeNode] set equals to " + labelSubTreeNode);
				}
				if (height != null && !height.trim().equals("")) {
					response.setAttribute("height", height);
					logger.info("[DAJS]:: attribute [height] set equals to " + height);
				}else{
					response.setAttribute("height", "600");
					logger.info("[DAJS]:: attribute [height] set equals to 600");
				}
			} else {
				logger.info("[DAJS]:: mode: " + channelType);
				DocumentsBrowserConfig config = DocumentsBrowserConfig.getInstance();
				JSONObject jsonObj  = config.toJSON();
				// read value from db
				//labelSubTreeNode = ...;
				//jsonObj.put("labelSubTreeNode", labelSubTreeNode);
				response.setAttribute("metaConfiguration", jsonObj);
			}			
			
		} catch (Throwable t) {
			logger.error("[DAJS]:: error", t);
			throw new SpagoBIException("An unexpected error occured while executing UserDocumentsBrowserPortletStartAction", t);
		} finally {
			logger.debug("OUT");
		}
	}	
}
