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
				HashMap values = proxy.getAdmissibleValues(aDocParameter.getId(), role);
				if (values == null || values.isEmpty()) {
					%>
					<input type="text" name="<%= aDocParameter.getUrlName() %>" size="30"/>
					<%
				} else {
					%>
					<select name="<%= aDocParameter.getUrlName() %>">
					<%
					Set entries = values.entrySet();
					Iterator it = entries.iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						%>
						<option value="<%= entry.getKey() %>"><%= entry.getValue() %></option>
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