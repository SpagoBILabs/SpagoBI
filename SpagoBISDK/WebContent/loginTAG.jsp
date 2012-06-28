<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%
/**
Login page: user must fill form with his credentials.
Authentication works properly only if 
it.eng.spagobi.services.security.service.ISecurityServiceSupplier.checkAuthentication(String userId, String psw) 
method is implemented, therefore it should work if SpagoBI is installed as a web application.
If SpagoBI is installed into a portal environment, the above method should be implemented for the portal in use (eXo, Liferay, ...).
The form points to documentsList.jsp.
*/
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Login</title>
	<style>
	body, p { font-family:Tahoma; font-size:10pt; padding-left:30; }
	pre { font-size:8pt; }
	</style>
</head>
</head>
<body>
<h2>Welcome to SpagoBI SDK - Web Services / TAG library demo</h2>
<br/>
<span><b>Login with biuser/biuser</b></span>
<form action="documentsList.jsp" method="post">
Name: <input type="text" name="user" size="30"/><br/>
Password: <input type="password" name="password" size="30"/><br/>
<input type="submit" value="Login" />
</form>
</body>
</html>