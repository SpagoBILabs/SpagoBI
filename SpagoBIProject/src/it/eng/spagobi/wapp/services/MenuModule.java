/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
/*
 * Created on 21-apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.spagobi.wapp.services;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.module.AbstractHttpModule;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.AdmintoolsConstants;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.wapp.bo.Menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 * @deprecated (use LoginModule)
 * 
 */
public class MenuModule extends AbstractHttpModule {

    public static final String CREATE_MENU = "CREATE_MENU";
    public static final String MODULE_PAGE = "MenuPage";

    static Logger logger = Logger.getLogger(MenuModule.class);
    IEngUserProfile profile = null;

    /**
     * Service.
     * 
     * @param request
     *                the request
     * @param response
     *                the response
     * 
     * @throws Exception
     *                 the exception
     * 
     * @see it.eng.spago.dispatching.action.AbstractHttpAction#service(it.eng.spago.base.SourceBean,
     *      it.eng.spago.base.SourceBean)
     */
    public void service(SourceBean request, SourceBean response) throws Exception {
	logger.debug("IN");
	RequestContainer reqCont = RequestContainer.getRequestContainer();
	SessionContainer sessCont = reqCont.getSessionContainer();
	SessionContainer permSess = sessCont.getPermanentContainer();
	profile = (IEngUserProfile) permSess.getAttribute(IEngUserProfile.ENG_USER_PROFILE);

	String message = (String) request.getAttribute("MESSAGEDET");
	logger.debug("Message =" + message);

	EMFErrorHandler errorHandler = getErrorHandler();

	try {
	    if (message == null) {
		EMFUserError userError = new EMFUserError(EMFErrorSeverity.ERROR, 101);
		logger.debug("The message parameter is null");
		throw userError;
	    }

	    if (message.trim().equalsIgnoreCase(CREATE_MENU)) {
		getMenuItems(request, response);
	    }
	} catch (EMFUserError eex) {
	    errorHandler.addError(eex);
	    logger.error("EMFUserError", eex);
	    return;
	} catch (Exception ex) {
	    EMFInternalError internalError = new EMFInternalError(EMFErrorSeverity.ERROR, ex);
	    errorHandler.addError(internalError);
	    logger.error("Exception", ex);
	    return;
	}
	// fill response attributes
	response.setAttribute(SpagoBIConstants.PUBLISHER_NAME, "userhome");
	logger.debug("OUT");
    }

    /**
     * Gets the elements of menu relative by the user logged. It reaches the
     * role from the request and asks to the DB all detail menu information, by
     * calling the method <code>loadMenuByRoleId</code>.
     * 
     * @param request
     *                The request Source Bean
     * @param response
     *                The response Source Bean
     * @throws EMFUserError
     *                 If an exception occurs
     */
    private void getMenuItems(SourceBean request, SourceBean response) throws EMFUserError {
	logger.debug("IN");
	try {
	    List lstFinalMenu = new ArrayList();
	    Collection lstRolesForUser = ((UserProfile)profile).getRolesForUse();
	    Object[] arrRoles = lstRolesForUser.toArray();
	    for (int i = 0; i < arrRoles.length; i++) {
			Integer roleId = (Integer) arrRoles[i];
			if (roleId != null)
			    logger.debug("Reading menu items for roleId:" + roleId.toString());
			List lstMenuItems = DAOFactory.getMenuRolesDAO().loadMenuByRoleId(roleId);
			List lstMenuChildren = new ArrayList();
			for (int j = 0; j < lstMenuItems.size(); j++) {
			    if (!lstFinalMenu.contains((Menu) lstMenuItems.get(j))) {
					Menu tmpElement = (Menu) lstMenuItems.get(j);
					logger.debug("Add Menu:" + tmpElement.getName());
					List tmpChildren = (DAOFactory.getMenuDAO().getChildrenMenu(tmpElement.getMenuId(),roleId));
					//merge children of different roles
					/*
					if (!lstMenuChildren.containsAll(tmpChildren)){
						for (int k=0; k<tmpChildren.size(); k++){
							if (!lstMenuChildren.contains(tmpChildren.get(k)))
									lstMenuChildren.add(tmpChildren.get(k));
						}
					}*/
					//boolean tmpHasCHildren = (tmpChildren.size() == 0) ? false : true;
					boolean tmpHasCHildren = (lstMenuChildren.size() == 0) ? false : true;
					tmpElement.setHasChildren(tmpHasCHildren);
					tmpElement.setLstChildren(lstMenuChildren);
					lstFinalMenu.add(tmpElement);
			    }
			}
	    }
	    response.setAttribute("LIST_MENU", lstFinalMenu);
	} catch (Exception ex) {
	    logger.error("Cannot fill response container" + ex.getLocalizedMessage());
	    HashMap params = new HashMap();
	    params.put(AdmintoolsConstants.PAGE, MenuModule.MODULE_PAGE);
	    throw new EMFUserError(EMFErrorSeverity.ERROR, 7000, new Vector(), params);
	}
	logger.debug("OUT");
    }
}
