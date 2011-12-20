<%--
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
Given username and password from login.jsp form, they are used to invoke each SpagoBI web service.
Authentication works properly only if 
it.eng.spagobi.services.security.service.ISecurityServiceSupplier.checkAuthentication(String userId, String psw) 
method is implemented, therefore it should work if SpagoBI is installed as a web application.
If SpagoBI is installed into a portal environment, the above method should be implemented for the portal in use (eXo, Liferay, ...).
When the authentication succeeds, the user can choose the document he wants to execute; this form points to chooseRole.jsp.
*/
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page errorPage="error.jsp"%>
<%@page import="it.eng.spagobi.sdk.proxy.DocumentsServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocument"%>
<%@page import="it.eng.spagobi.sdk.proxy.TestConnectionServiceProxy"%>
<%@page import="org.apache.axis.AxisFault"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Choose document</title>
	<style>
	body, p { font-family:Tahoma; font-size:10pt; padding-left:30; }
	pre { font-size:8pt; }
	</style>
</head>
<body>
<%
// retrieving username and password
String user = request.getParameter("user");
String password = request.getParameter("password");
if (user != null && password != null) {
	TestConnectionServiceProxy proxy = new TestConnectionServiceProxy(user, password);
    proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/TestConnectionService");
	boolean result = proxy.connect();
	if (result) {
		// connection successful
		session.setAttribute("spagobi_user", user);
		session.setAttribute("spagobi_pwd", password);
	} else {
		response.sendRedirect("login.jsp?connectionFailed=true");
		return;
	}
}
user = (String) session.getAttribute("spagobi_user");
password = (String) session.getAttribute("spagobi_pwd");

// display a form for document selection
if (user != null && password != null) {
%>
<span><b>Choose a document</b></span>
<form action="chooseRole.jsp" method="post">
	Document: 
	<select name="documentId">
	<%
	// gets all visible documents list
	DocumentsServiceProxy proxy = new DocumentsServiceProxy(user, password);
	proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/DocumentsService");
	SDKDocument[] documents = proxy.getDocumentsAsList(null, null, null);
	session.setAttribute("spagobi_documents", documents);
	for (int i = 0; i < documents.length; i++) {
		SDKDocument aDoc = documents[i];
		%>
		<option value="<%= aDoc.getId() %>"><%= aDoc.getName() %></option>
		<%
	}
	%>
	</select>
	<input type="submit" value="Go on" />
</form>
<%
} else {
	response.sendRedirect("login.jsp");
}
%>
</body>
</html>