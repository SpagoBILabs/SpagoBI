<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKExecutedDocumentContent"%>
<%@page import="it.eng.spagobi.sdk.proxy.DocumentsServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocumentParameter"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKDocument"%>
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