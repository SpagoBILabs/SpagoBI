<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page isErrorPage="true" language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="org.apache.axis.AxisFault"%>
<html>
<head>
	<title>Error!</title>
	<style>
	body, p { font-family:Tahoma; font-size:10pt; padding-left:30; }
	pre { font-size:8pt; }
	</style>
</head>
<body>

<%-- Exception Handler --%>
<font color="red">
<%
exception.printStackTrace();
if (exception instanceof AxisFault) {
	AxisFault axisFault = (AxisFault) exception;
	if (axisFault.getFaultString().startsWith("WSDoAllReceiver")) {
		%>
		Authenticated failed!!
		<%
	} else {
		%>
		Error while connecting to server!!
		<%
	}
} else {
	%>
	Error while connecting to server!!
	<%
}
%><br>
</font>
<a href="login.jsp">Click to retry</a>
</body>
</html>