<%-- SpagoBI, the Open Source Business Intelligence suite

 © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
<%@ page isErrorPage="true" 
		 language="java" 
		 contentType="text/html; charset=ISO-8859-1"
    	 pageEncoding="ISO-8859-1" 
    	 session="false"%>
<%@page import="org.apache.log4j.Logger"%>
<%
// The exception variable may be null since this jsp is used as general error page but also as Spago service exception publisher
// In case of Spago trapped exception, this should be logged elsewhere...
if (exception != null) {
	exception.printStackTrace();
	Logger logger = Logger.getLogger("it.eng.spagobi");
	logger.error(exception);
}
response.setStatus(500);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<title>Error</title>
</head>
<body>

An error has occurred. Retry later.<br/>
If the problems persists, contact the system administrator.

</body>
</html>