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
<%@ page language="java"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" 
%>
<%@page session="false" %>

<%@page import="it.eng.spagobi.commons.utilities.SpagoBIUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.GeneralUtilities"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
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
	<%-- 
	SpagoBI Web Application can have different nested iframes. 
	If the session expires, the user would see SpagoBI start page on the nested iframe, that is not so good... 
	The top window contains a javascript variable which name is 'sessionExpiredSpagoBIJS' (see home.jsp), so the following javascript 
	looks for the parent window (using recursion) that contains that variable, and redirects that window.
	If this window is not found, than the current window is redirect to SpagoBI start page.
	--%>
	
	
<script>
	var sessionExpiredSpagoBIJSFound = false;
	try {
		var currentWindow = window;
		var parentWindow = parent;
		while (parentWindow != currentWindow) {
			if (parentWindow.sessionExpiredSpagoBIJS) {
				parentWindow.location = '<%= GeneralUtilities.getSpagoBiContext() %>';
				sessionExpiredSpagoBIJSFound = true;
				break;
			} else {
				currentWindow = parentWindow;
				parentWindow = currentWindow.parent;
			}
		}
	} catch (err) {}
	
	if (!sessionExpiredSpagoBIJSFound) {
		window.location = '<%= GeneralUtilities.getSpagoBiContext() %>';
	}
	</script>
	<%
}
%>