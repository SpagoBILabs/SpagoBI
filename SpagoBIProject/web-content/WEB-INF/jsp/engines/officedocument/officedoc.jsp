<%--
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
--%>

<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.monitoring.dao.AuditManager"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<% 
SourceBean sbModuleResponse = (SourceBean) aServiceResponse.getAttribute("ExecuteBIObjectModule");
ExecutionInstance instanceO = contextManager.getExecutionInstance(ExecutionInstance.class.getName());
String execContext = instanceO.getExecutionModality();

Integer executionAuditId_office = null;

// identity string for object of the page
String strUuid = instanceO.getExecutionId();
//SourceBean moduleResponse = (SourceBean) aServiceResponse.getAttribute("ExecuteBIObjectModule");
BIObject biObj = instanceO.getBIObject();

//get the url for document retrieval
String officeDocUrl = GeneralUtilities.getSpagoBIProfileBaseUrl(userUniqueIdentifier);
officeDocUrl += "&ACTION_NAME=GET_OFFICE_DOC&documentId=" + biObj.getId().toString() + "&" + LightNavigationManager.LIGHT_NAVIGATOR_DISABLED + "=TRUE";
// adding parameters for AUDIT updating
if (executionAuditId_office != null) {
	officeDocUrl += "&" + AuditManager.AUDIT_ID + "=" + executionAuditId_office.toString();
}

response.sendRedirect(officeDocUrl);

%>
