<%-- 
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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

	

	

	
	
    
    
   
	




