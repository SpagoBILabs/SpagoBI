<%--

LICENSE: see LICENSE.txt file 

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