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
package it.eng.spagobi.tools.role.services;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IRoleDAO;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * @author Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class ModifyRolesAction extends AbstractHttpAction {
	
	private static final long serialVersionUID = 1L;
	static private Logger logger = Logger.getLogger(ModifyRolesAction.class);
	
	/**
	 * This action is invoked to save modifications on one or more than one roles.
	 * Required request attributes:
	 * FIELDS_ORDER : a string with the fields order declaration (ex.: 'Type,Snapshost,Subobjects,....')
	 * MODIFIED_ROLES : a string with all roles modifications with the syntax: {role id}:{comma separated values for the fields
	 * in the same order declared by FIELDS_ORDER}(ex: '4:ADMIN,false,true....')
	 * 
	 * @param serviceRequest the service request
	 * @param serviceResponse the service response
	 * 
	 * @throws Exception the exception
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		logger.debug("IN");
		String message = null;
		freezeHttpResponse();
		HttpServletResponse httResponse = getHttpResponse();
		try {
			IRoleDAO roleDAO = DAOFactory.getRoleDAO();
			String modifiedRoles = (String) serviceRequest.getAttribute("MODIFIED_ROLES");
			if (modifiedRoles == null || modifiedRoles.trim().equals("")) {
				logger.warn("No roles to save");
				return;
			}
			String fieldsOrder = (String) serviceRequest.getAttribute("FIELDS_ORDER");
			int roleTypeIndex = 0, saveSubojectsIndex = 0, subojectsIndex = 0, snapshotsIndex = 0, viewpointsIndex = 0, notesIndex = 0, seemetadataIndex = 0, savemetadataIndex = 0, sendMailIndex = 0, rememberMeIndex = 0, personalFolderIndex = 0, buildQbeQueryIndex = 0;
			String[] fields = fieldsOrder.split(",");
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				if (field.equalsIgnoreCase("Type")) {
					roleTypeIndex = i;
				} else if (field.equalsIgnoreCase("SaveSubojects")) {
					saveSubojectsIndex = i;
				} else if (field.equalsIgnoreCase("Subojects")) {
					subojectsIndex = i;
				} else if (field.equalsIgnoreCase("Snapshots")) {
					snapshotsIndex = i;
				} else if (field.equalsIgnoreCase("Viewpoints")) {
					viewpointsIndex = i;
				} else if (field.equalsIgnoreCase("Notes")) {
					notesIndex = i;
				} else if (field.equalsIgnoreCase("SeeMetadata")) {
					seemetadataIndex = i;
				} else if (field.equalsIgnoreCase("SaveMetadata")) {
					savemetadataIndex = i;					
				} else if (field.equalsIgnoreCase("SendMail")) {
					sendMailIndex = i;
				} else if (field.equalsIgnoreCase("RememberMe")) {
					rememberMeIndex = i;
				} else if (field.equalsIgnoreCase("PersonalFolder")) {
					personalFolderIndex = i;
				} else if (field.equalsIgnoreCase("BuildQbeQuery")) {
					buildQbeQueryIndex = i;
				}
			}
			
			String[] rolesStr = modifiedRoles.split(";");
			for (int i = 0; i < rolesStr.length; i++) {
				String roleStr = rolesStr[i];
				String roleIdStr = roleStr.substring(0, roleStr.indexOf(":"));
				Integer id = new Integer(roleIdStr);
				Role role = roleDAO.loadByID(id);
				String valuesStr = roleStr.substring(roleStr.indexOf(":") + 1);
				String[] values = valuesStr.split(",");
				String roleTypeStr = values[roleTypeIndex];
				Domain roleType = DAOFactory.getDomainDAO().loadDomainByCodeAndValue("ROLE_TYPE", roleTypeStr);
				role.setRoleTypeID(roleType.getValueId());
				role.setRoleTypeCD(roleType.getValueCd());
				role.setIsAbleToSaveSubobjects(Boolean.parseBoolean(values[saveSubojectsIndex]));
				role.setIsAbleToSeeSubobjects(Boolean.parseBoolean(values[subojectsIndex]));
				role.setIsAbleToSeeSnapshots(Boolean.parseBoolean(values[snapshotsIndex]));
				role.setIsAbleToSeeViewpoints(Boolean.parseBoolean(values[viewpointsIndex]));
				role.setIsAbleToSeeNotes(Boolean.parseBoolean(values[notesIndex]));
				role.setIsAbleToSeeMetadata(Boolean.parseBoolean(values[seemetadataIndex]));
				role.setIsAbleToSaveMetadata(Boolean.parseBoolean(values[savemetadataIndex]));
				role.setIsAbleToSendMail(Boolean.parseBoolean(values[sendMailIndex]));
				role.setIsAbleToSaveRememberMe(Boolean.parseBoolean(values[rememberMeIndex]));
				role.setIsAbleToSaveIntoPersonalFolder(Boolean.parseBoolean(values[personalFolderIndex]));
				role.setIsAbleToBuildQbeQuery(Boolean.parseBoolean(values[buildQbeQueryIndex]));
				roleDAO.modifyRole(role);
			}
			message = "SBISet.ListRoles.saveOk";
		} catch (Exception e) {
			logger.debug("Error while saving roles: " + e);
			message = "SBISet.ListRoles.errorWhileSaving";
		} finally {
			httResponse.getOutputStream().write(message.getBytes());
			httResponse.getOutputStream().flush();
			logger.debug("OUT");
		}
	}	

}
