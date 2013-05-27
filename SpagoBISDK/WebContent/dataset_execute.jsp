<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
 <%@page import="java.io.InputStream"%>
<%@page import="java.util.Random"%>
<%@page import="it.eng.spagobi.sdk.proxy.DataSetsSDKServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.datasets.bo.SDKDataSet"%>

<%
/**
This page invokes a SpagoBI web services in order to execute the dataset's methods.
It's a JSP for ONLY case tests.
To call it the url must be: http://localhost:8080/SpagoBISDK/dataset_execute.jsp
*/
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
/*
	String user = "biadmin";
	String password = "biadmin";
	try {
	 DataSetsSDKServiceProxy proxy = new DataSetsSDKServiceProxy(user, password);
	 proxy.setEndpoint("http://localhost:8080/SpagoBI/sdk/DataSetsSDKService");
	 SDKDataSet[] datasets = proxy.getDataSets();
	 System.out.println("*** dataset: " + datasets.length);
	}  catch (Exception e) {
	 e.printStackTrace();
}
*/
String user = "biadmin";
String password = "biadmin";
String message = "Il dataset è stato eseguito correttamente ";

if (user != null && password != null) {
	InputStream is = null;
	try { 
		DataSetsSDKServiceProxy proxy = new DataSetsSDKServiceProxy(user, password);
		proxy.setEndpoint("http://localhost:38080/SpagoBI/sdk/DataSetsSDKService");		
		

		String dataset = null;

			//dataset = proxy.executeDataSet("prova",null);
			dataset = proxy.executeDataSet("testQBe",null);
			
			System.out.println("*** dataset: " + dataset);

	}  catch (Exception e) {
		message = "L'esecuzione del dataset è terminata con errori. Guardare il log!";
		e.printStackTrace();
			
	}
} else {
	response.sendRedirect("login.jsp");
}
%>
<body>
<h2><%= message%></h2>
<%= new java.util.Date() %>
</body></html> 
