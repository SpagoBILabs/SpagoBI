<%-- SpagoBI, the Open Source Business Intelligence suite

 © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
<%-- 
author: Andrea Gioia (andrea.gioia@eng.it)
--%>
<%@ page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

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
		<%-- START SCRIPT FOR DOMAIN DEFINITION (MUST BE EQUAL BETWEEN SPAGOBI AND EXTERNAL ENGINES) -->
	  	<script type="text/javascript">
	  		document.domain='<%= EnginConf.getInstance().getSpagoBiDomain() %>';
	  	</script>
	  	<!-- END SCRIPT FOR DOMAIN DEFINITION --%>
	</head>
	
	<body>
	
		<iframe id="iframe_1"
		        name="iframe_1"
		        src='AdapterHTTP?ACTION_NAME=DRAW_MAP_ACTION&SBI_EXECUTION_ID=<%= request.getParameter("SBI_EXECUTION_ID")%>'
		        width="100%"
		        height="100%"
		        frameborder="0"
		        style="background-color:white;">
		</iframe>
		
	</body>
</html>
