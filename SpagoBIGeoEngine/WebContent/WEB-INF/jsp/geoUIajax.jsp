<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%-- 
author: Andrea Gioia (andrea.gioia@eng.it)
--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<% // lucky for us no code yet %>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

<html>

	<head>
		<%@include file="commons/includeExtJS.jspf" %>
   		<%@include file="commons/includeSbiGeoJS.jspf" %>
		
		<%-- START SCRIPT FOR DOMAIN DEFINITION (MUST BE EQUAL BETWEEN SPAGOBI AND EXTERNAL ENGINES) -->
		<script type="text/javascript">
			document.domain='<%= EnginConf.getInstance().getSpagoBiDomain() %>';
		</script>
		<!-- END SCRIPT FOR DOMAIN DEFINITION --%>
	</head>
	
	<body>
		<script type="text/javascript">   
	    	var url = {
				host: '<%= request.getServerName()%>',
		        port: '<%= request.getServerPort()%>',
		        contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
		        				  request.getContextPath().substring(1):
		        				  request.getContextPath()%>',
		        execId: '<%= request.getParameter("SBI_EXECUTION_ID")%>'
	    	};
	    	
	    	
	    	Sbi.geo.app.serviceRegistry = new Sbi.commons.ServiceRegistry({baseUrl: url});

	    	Ext.onReady(Sbi.geo.app.init, Sbi.geo.app.app);    
    	</script>
    	
    	
    	<div id="tabs"></div>
		<div id="menuTreePane1"></div>
		<div id="menuTreePane2"></div>
		<form id="form" 
			  method="post"
			  action=""
			  target=""></form>
	</body>
</html>

	

	

	
	
    
    
   
	




