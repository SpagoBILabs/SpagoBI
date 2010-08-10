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
		 contentType="text/html; charset=UTF-8" 
		 pageEncoding="UTF-8"%>
<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%	
	Locale locale;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	String formDocumentId;
	String language, country;
	
	language = request.getParameter(SpagoBIConstants.SBI_LANGUAGE); 
	country = request.getParameter(SpagoBIConstants.SBI_COUNTRY);
	try {
		locale = new Locale(language, country);
	} catch (Exception e) {
		locale = Locale.UK;
	}
	
	formDocumentId = request.getParameter("document");
	spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
    spagobiSpagoController = "/servlet/AdapterHTTP"; //request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
%>
<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

<html>
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeSbiQbeJS.jspf"%>
		
		<title>Template Builder</title>
	</head>
	
	<body>
	<script type="text/javascript">  
		Sbi.config = {};
	
		var url = {
	    	host: '<%= request.getServerName()%>'
	    	, port: '<%= request.getServerPort()%>'
	    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
	    	   				  request.getContextPath().substring(1):
	    	   				  request.getContextPath()%>'
	    	    
	    };

	    var params = {
	    	SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
	    	, "<%= SpagoBIConstants.SBI_LANGUAGE %>" : "<%= locale.getLanguage() %>"
	    	, "<%= SpagoBIConstants.SBI_COUNTRY %>" : "<%= locale.getCountry() %>"
	    };

	    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
	    	baseUrl: url
	        , baseParams: params
	    });

	    var remoteUrl = {
			completeUrl: '<%= spagobiServerHost + spagobiContext + spagobiSpagoController %>'
		};

	    var remoteServiceRegistryParams = {
		    	SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
		    	, LIGHT_NAVIGATOR_DISABLED: 'TRUE'
		    	, "NEW_SESSION" : "TRUE"
		}; 
		Sbi.config.remoteServiceRegistry = new Sbi.service.ServiceRegistry({
			baseUrl: remoteUrl
		    , baseParams: remoteServiceRegistryParams
		    , defaultAbsolute: true
		});

	

		var documentTemplateBuilderPanel = new Sbi.formtemplate.DocumentTemplateBuilder({
			formDocumentId: <%= formDocumentId %>
		});
	   	var viewport = new Ext.Viewport(documentTemplateBuilderPanel);  
	 </script>
	</body>
</html>