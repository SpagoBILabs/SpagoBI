<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%
/**
This page retrieves the role choosen by the user on chooseRole.jsp and retrieves information about document parameters.
If the document has no parameters, user is redirected automatically on execution.jsp, otherwise a form pointing to execution.jsp is shown.
For each parameter, all admissible values are retrieved and shown into a combobox.
If the parameter is manual input, a manul input appears.
*/
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.*"%>
<%@page import="it.eng.spagobi.sdk.proxy.DocumentsServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocumentParameterValue"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Choose parameters</title>
	<style>
	body, p { font-family:Tahoma; font-size:10pt; padding-left:30; }
	pre { font-size:8pt; }
	</style>
</head>
<body>
<%
String user = (String) session.getAttribute("spagobi_user");
String password = (String) session.getAttribute("spagobi_pwd");
if (user != null && password != null) {
	DocumentsServiceProxy proxy = new DocumentsServiceProxy(user, password);
	proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/DocumentsService");
	Integer documentId = (Integer) session.getAttribute("spagobi_documentId");
	String role = request.getParameter("role");
	session.setAttribute("spagobi_role", role);
	SDKDocumentParameter[] parameters = proxy.getDocumentParameters(documentId, role);
	session.setAttribute("spagobi_document_parameters", parameters);
	if (parameters == null || parameters.length == 0) {
		response.sendRedirect("execution.jsp");
	} else {
		%>
		<span><b>Choose parameters</b></span>
		<form action="execution.jsp" method="post">
			<%
			for (int i = 0; i < parameters.length; i++) {
				SDKDocumentParameter aDocParameter = parameters[i];
				%>
				<%= aDocParameter.getLabel() %>
				<%
				// admissible values for parameter
				SDKDocumentParameterValue[] values = proxy.getAdmissibleValues(aDocParameter.getId(), role);
				if (values == null || values.length == 0) {
					%>
					<input type="text" name="<%= aDocParameter.getUrlName() %>" size="30"/>
					<%
				} else {
					%>
					<select name="<%= aDocParameter.getUrlName() %>">
					<%
					for (int j = 0; j < values.length; j++) {
						SDKDocumentParameterValue aValue = values[j];
						%>
						<option value="<%= aValue.getValue() %>"><%= aValue.getDescription() %></option>
						<%
					}
					%>
					</select><br/>
					<%
				}
			}
			%>
		<input type="submit" value="Execute" />
		</form>
		<%
	}
} else {
	response.sendRedirect("login.jsp");
}
%>
</body>
</html>