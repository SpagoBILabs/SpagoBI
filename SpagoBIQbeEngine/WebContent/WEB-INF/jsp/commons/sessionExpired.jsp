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
<%@ page language="java"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" 
%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page session="false" %>

<%
String header = request.getHeader("Powered-By");
if (header != null && header.equals("Ext")) {
	response.setStatus(500);
	JSONObject sessionExpiredError = new JSONObject();
	sessionExpiredError.put("message", "session-expired");
	JSONArray array = new JSONArray();
	array.put(sessionExpiredError);
	JSONObject jsonObject = new JSONObject();
	jsonObject.put("errors", array);
	out.clear();
	out.write(jsonObject.toString());
	out.flush();
} else {
	%>
	Session has expired. Try re-executing the document
	<%
}
%>