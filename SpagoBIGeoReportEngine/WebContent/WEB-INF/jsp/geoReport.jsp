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
<%@page import="java.util.Locale"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="it.eng.spagobi.engines.georeport.GeoReportEngineInstance"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<% 
	GeoReportEngineInstance engineInstance;
	Map env;
	String executionRole;
	Locale locale;
	String template;
	List<String> includes;
	
	engineInstance = (GeoReportEngineInstance)request.getSession().getAttribute(EngineConstants.ENGINE_INSTANCE);
	env = engineInstance.getEnv();
	locale = engineInstance.getLocale();
	
	executionRole = (String)env.get(EngineConstants.ENV_EXECUTION_ROLE);
	template = engineInstance.getGuiSettings().toString();
	includes = engineInstance.getIncludes();
	
	
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

<html>

	<head>
		<title>SpagoBIGeoReportEngine</title>
		
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeMapFishJS.jspf" %>
		<%@include file="commons/includeExtensionsJS.jspf" %>		
		<%@include file="commons/includeSpagoBIGeoReportJS.jspf" %>
	</head>
	
	<body>
	
		<!-- Include template here  -->
		<!--   %@include file="tests/capoluoghiPropRemoteTemplate.jsp" % --> 
		<!--  %@include file="tests/usastateChorLocalTemplate.jsp" % -->
		
		
		
		<script language="javascript" type="text/javascript">

			Sbi.template = <%= template %>;

			if(Sbi.template.role) {
				Sbi.template.role = Sbi.template.role.charAt(0) == '/'? 
									Sbi.template.role.charAt(0): 
									'/' + Sbi.template.role.charAt(0);
			}
			var executionRole = '<%= executionRole%>';
			Sbi.template.role = executionRole || Sbi.template.role;
			
			execDoc = function(docLab, role, params, dispToolbar, dispSlide,frameId, height) {

				var h = height || '100%';
				
				var html = Sbi.sdk.api.getDocumentHtml({
					documentLabel: docLab
					, executionRole: role // "/" + role
					, parameters: params 
			      	, displayToolbar: dispToolbar
					, displaySliders: dispSlide
					, useExtUI: false
					, iframe: {
			        	id: frameId
			          	, height: h
				    	, width: '100%'
						, style: 'border: 0px;'
					}
				});
				
				//var html = '<h1>Prova provata ' + docLab + ' </h1>'
			    return html;
			};		
		</script>
		
		<script language="javascript" type="text/javascript">

			Sbi.config = {};

			var url = {
		    	host: '<%= request.getServerName()%>'
		    	, port: '<%= request.getServerPort()%>'
		    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?request.getContextPath().substring(1): request.getContextPath()%>'
		    	, controllerPath: null // no cotroller just servlets   
		    };

			Sbi.sdk.services.setBaseUrl({
		        protocol: '<%= request.getScheme()%>'     
		        , host: url.host
		        , port: url.port
		        //, contextPath: 'SpagoBI'
		        //, controllerPath: 'servlet/AdapterHTTP'  
		    });
	
		    var params = { };
	
		    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
		    	baseUrl: url
		        , baseParams: params
		    });

		
			Ext.onReady(function(){
			
				Ext.QuickTips.init();   
				var geoReportPanel = new Sbi.georeport.MainPanel(Sbi.template);	    
	      		var viewport = new Ext.Viewport({
	      			id:    'view',
		      		layout: 'fit',
		            items: [geoReportPanel]
		        });
			});
	
	
	</script>
		
		
		
		
		<div style="width: 600px; height: 200px; z-index:0;">&nbsp;
	  
		<div id="buttonbar"></div>
		<div id="map"></div>
	
		</div>
		
		<center id="error"></center>
		
	 
	</body>

</html>

