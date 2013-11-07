<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

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
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
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
	String docLabel;
	String docVersion;
	String docAuthor;
	String docName;
	String docDescription;
	String docIsPublic;
	String docIsVisible;
	String docPreviewFile;
	String[] docCommunities;
	String docCommunity;
	List docFunctionalities;
	String docDatasetLabel;
	String userId;
	List<String> includes;
	
	engineInstance = (GeoReportEngineInstance)request.getSession().getAttribute(EngineConstants.ENGINE_INSTANCE);
	env = engineInstance.getEnv();
	locale = engineInstance.getLocale();

	executionRole = (String)env.get(EngineConstants.ENV_EXECUTION_ROLE);
	userId = (engineInstance.getDocumentUser()==null)?"":engineInstance.getDocumentUser().toString();
	template = engineInstance.getGuiSettings().toString();
	docLabel = (engineInstance.getDocumentLabel()==null)?"":engineInstance.getDocumentLabel().toString();
	docVersion = (engineInstance.getDocumentVersion()==null)?"":engineInstance.getDocumentVersion().toString();
	docAuthor = (engineInstance.getDocumentAuthor()==null)?"":engineInstance.getDocumentAuthor().toString();
	docName = (engineInstance.getDocumentName()==null)?"":engineInstance.getDocumentName().toString();
	docDescription = (engineInstance.getDocumentDescription()==null)?"":engineInstance.getDocumentDescription().toString();
	docIsPublic= (engineInstance.getDocumentIsPublic()==null)?"":engineInstance.getDocumentIsPublic().toString();
	docIsVisible= (engineInstance.getDocumentIsVisible()==null)?"":engineInstance.getDocumentIsVisible().toString();
	docPreviewFile= (engineInstance.getDocumentPreviewFile()==null)?"":engineInstance.getDocumentPreviewFile().toString();	
	docDatasetLabel = (engineInstance.getDataSet()==null)?"":engineInstance.getDataSet().getLabel();
	docCommunities= (engineInstance.getDocumentCommunities()==null)?null:engineInstance.getDocumentCommunities();
	docCommunity = (docCommunities == null && docCommunities.length > 0) ? "": docCommunities[0];
	docFunctionalities= (engineInstance.getDocumentFunctionalities()==null)?new ArrayList():engineInstance.getDocumentFunctionalities();
	
	includes = engineInstance.getIncludes();
	
%>

<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

<html>

	<head>
		<title>SpagoBIGeoReportEngine</title>
		
		<%@include file="commons/includeGeoExt.jspf" %>
		
		
		
		<%@include file="commons/includeExtensionsJS.jspf" %>	
		<script language="javascript" type="text/javascript">
			//alert('includeExtensionsJS.jspf');
		</script>	
		
		<%@include file="commons/includeSpagoBIGeoReportJS.jspf" %>
		<script language="javascript" type="text/javascript">
			//alert('includeSpagoBIGeoReportJS.jspf');
		</script>
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

		    Sbi.config.docLabel ="<%=docLabel%>";
		    Sbi.config.docVersion = "<%=docVersion%>";
		    Sbi.config.userId = "<%=userId%>";
		    Sbi.config.docAuthor = "<%=docAuthor%>";
		    Sbi.config.docName = "<%=docName%>";
		    Sbi.config.docDescription = "<%=docDescription%>";
		    Sbi.config.docIsPublic= "<%=docIsPublic%>";
		    Sbi.config.docIsVisible= "<%=docIsVisible%>";
		    Sbi.config.docPreviewFile= "<%=docPreviewFile%>";
		    Sbi.config.docCommunities= "<%=docCommunity%>";
		    Sbi.config.docFunctionalities= <%=docFunctionalities%>;
		    Sbi.config.docDatasetLabel= "<%=docDatasetLabel%>";
		    
		    var geoReportPanel = null;
		    
			Ext.onReady(function(){
			
				Ext.QuickTips.init();   
				geoReportPanel = new Sbi.geo.MainPanel(Sbi.template);	    
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

