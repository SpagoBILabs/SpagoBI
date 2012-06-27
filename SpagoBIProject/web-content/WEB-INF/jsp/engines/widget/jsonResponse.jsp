<%-- SpagoBI, the Open Source Business Intelligence suite

 © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java"
         contentType="text/json; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" %>

<%@page import="it.eng.spagobi.commons.utilities.GeneralUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.PortletUtilities"%>
<%@page import="it.eng.spago.base.*"%>



<%
	ResponseContainer responseContainer = ResponseContainerPortletAccess.getResponseContainer(request);
	if (responseContainer == null) {
		responseContainer = ResponseContainer.getResponseContainer();
	}
	SourceBean serviceResponse = responseContainer.getServiceResponse();
	SourceBean sbResponse = (SourceBean) serviceResponse.getAttribute("ExecuteBIObjectModule");
%>


<%= sbResponse.getAttribute("content") %>