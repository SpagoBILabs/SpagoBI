<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  
 
 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    
    <%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
    <%@page import="it.eng.spagobi.commons.SingletonConfig"%>

<%

boolean backUrlB=false;
String backUrl="";
if(session.getAttribute(SpagoBIConstants.BACK_URL)!=null){
	backUrl=(String)session.getAttribute(SpagoBIConstants.BACK_URL);
	backUrlB=true;
}


session.invalidate();


//Check if SSO is active

String active = SingletonConfig.getInstance().getConfigValue("SPAGOBI_SSO.ACTIVE");

if ((active == null || active.equalsIgnoreCase("false")) && backUrlB==false) {
	String context = request.getContextPath();
	response.sendRedirect(context);
}
else if (active != null && active.equalsIgnoreCase("true")) {

	String urlLogout =  SingletonConfig.getInstance().getConfigValue("SPAGOBI_SSO.SECURITY_LOGOUT_URL");
	if(backUrlB==true){
		response.sendRedirect(backUrl); 
	}
	response.sendRedirect(urlLogout);

} %>
 

<%if (active != null && active.equalsIgnoreCase("true")) { %>
	<script>window.close();</script>
<% } %>


