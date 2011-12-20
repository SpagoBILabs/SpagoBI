
<%@page import="java.io.InputStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent"%>
<%@page import="it.eng.spagobi.sdk.proxy.DocumentsServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocument"%><%--
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
This page invokes a SpagoBI web services in order to execute the current document and retrive the result (a PDF file).
This functionality is available only for REPORT and KPI documents.
*/
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
String user = (String) session.getAttribute("spagobi_user");
String password = (String) session.getAttribute("spagobi_pwd");
String html = "";
if (user != null && password != null) {
	InputStream is = null;
	try {
		SDKDocument document = (SDKDocument) session.getAttribute("spagobi_current_document");
		String role = (String) session.getAttribute("spagobi_role");
		SDKDocumentParameter[] parameters = (SDKDocumentParameter[]) session.getAttribute("spagobi_document_parameters"); 
		DocumentsServiceProxy proxy = new DocumentsServiceProxy(user, password);
		proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/DocumentsService");
		SDKExecutedDocumentContent export = proxy.executeDocument(document, parameters, role,"text/html");
		is = export.getContent().getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		//response.setContentType("text/html");
		//response.setHeader("content-disposition", "attachment; filename=" + export.getFileName());
		//ServletOutputStream os = response.getOutputStream();
		int c = 0;
		byte[] b = new byte[1024];
		while ((c = is.read(b)) != -1) {
			if (c == 1024)
				os.write(b);
			else
				os.write(b, 0, c);
		}
		html = os.toString();
		os.flush();
		os.close();
	} finally {
		if (is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

	}
} else {
	response.sendRedirect("login.jsp");
}
%>
<%= html %>