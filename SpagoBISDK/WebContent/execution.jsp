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
This page use the SpagoBI execution tag, that displays an iframe pointing to SpagoBI context with all information about document execution 
(document identifier, role to be used, values for parameters).
*/
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spagobi" tagdir="/WEB-INF/tags/spagobi" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.*"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocument"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Document execution</title>
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
	Integer documentId = (Integer) session.getAttribute("spagobi_documentId");
	SDKDocument document = null;
	SDKDocument[] documents = (SDKDocument[]) session.getAttribute("spagobi_documents");
	for (int i = 0; i < documents.length; i++) {
		SDKDocument aDocument = documents[i];
		if (aDocument.getId().equals(documentId)) {
			document = aDocument;
		}
	}
	session.setAttribute("spagobi_current_document", document);
	String userId = (String) session.getAttribute("spagobi_user");
	String role = (String) session.getAttribute("spagobi_role");
	SDKDocumentParameter[] parameters = (SDKDocumentParameter[]) session.getAttribute("spagobi_document_parameters"); 
	StringBuffer parameterValues = new StringBuffer();
	if (parameters != null && parameters.length > 0) {
		for (int i = 0; i < parameters.length; i++) {
			SDKDocumentParameter aParameter = parameters[i];
			String value = request.getParameter(aParameter.getUrlName());
			if (value != null) {
				aParameter.setValues(new String[]{value});
				if (parameterValues.length() > 0) {
					parameterValues.append("&");
				}
				parameterValues.append(aParameter.getUrlName() + "=" + value);
			} else {
				aParameter.setValues(null);
			}
		}
	}
	%>
	<spagobi:execution 
			spagobiContext="http://localhost:8080/SpagoBI/"
			userId="<%= userId %>" 
	        documentId="<%= documentId.toString() %>"
	        iframeStyle="height:500px; width:100%" 
	        executionRole="<%= role %>"
	        parametersStr="<%= parameterValues.toString() %>"
	        displayToolbar="<%= Boolean.TRUE %>"
	        displaySliders="<%= Boolean.TRUE %>" />

	<%
	String documentType = document.getType();
	if (documentType.equals("REPORT") || documentType.equals("KPI")) {
		%>
		<a href="export.jsp">Export to PDF</a><br/>
		<%
	}else if(documentType.equals("ACCESSIBLE_HTML")){
		%>
		<a href="viewAccessible.jsp">View ACCESSIBLE HTML</a><br/>
		<a href="exportAccessible.jsp">Export to ACCESSIBLE HTML</a><br/>
		<%		
	}
	%>
	<a href="documentsList.jsp">Back to documents list</a>
	<%
} else {
	response.sendRedirect("login.jsp");
}
%>
</body>
</html>