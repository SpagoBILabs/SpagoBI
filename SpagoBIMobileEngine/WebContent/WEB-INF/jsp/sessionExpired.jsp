<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  
 
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

%>