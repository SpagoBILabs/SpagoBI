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
author: Davide Zerbetto (davide.zerbetto@eng.it)
--%>

<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spagobi.commons.QbeEngineStaticVariables"%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engines.qbe.QbeEngineConfig"%>
<%@page import="it.eng.spagobi.engines.qbe.QbeEngineInstance"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="java.util.Locale"%>
<%@page import="org.json.JSONObject"%>
<%@page import="it.eng.spagobi.engines.qbe.services.initializers.RegistryEngineStartAction"%>
<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	SourceBean serviceResponse;
	QbeEngineInstance qbeEngineInstance;
	QbeEngineConfig qbeEngineConfig;
	UserProfile profile;
	Locale locale;
	JSONObject registryConfiguration;
	
	serviceResponse = ResponseContainerAccess.getResponseContainer(request).getServiceResponse();
	qbeEngineInstance = (QbeEngineInstance) serviceResponse.getAttribute("ENGINE_INSTANCE");
	profile = (UserProfile)qbeEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);
	locale = (Locale)qbeEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	qbeEngineConfig = QbeEngineConfig.getInstance();
	registryConfiguration = (JSONObject) serviceResponse.getAttribute(RegistryEngineStartAction.REGISTRY_CONFIGURATION);
%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

<html>
	
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeSbiQbeJS.jspf"%>
	</head>
	
	<body>
	
    	<script type="text/javascript">  
			Sbi.config = {};
	
			Sbi.config.queryVersion = <%= QbeEngineStaticVariables.CURRENT_QUERY_VERSION %>;
	  	
			var url = {
		    	host: '<%= request.getServerName()%>'
		    	, port: '<%= request.getServerPort()%>'
		    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
		    	   				  request.getContextPath().substring(1):
		    	   				  request.getContextPath()%>'
		    	    
		    };
	
		    var params = {
		    	SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
		    };
	
		    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
		    	baseUrl: url
		        , baseParams: params
		    });
	
	      	var qbeConfig = {
	      		region: 'center'
	      	};
	      	qbeConfig.registryConfiguration = <%= registryConfiguration.toString() %>;
	
	        var registry = null;
	        
	        Ext.onReady(function(){
	        	Ext.QuickTips.init();   
	       		
	        	registry = new Sbi.registry.RegistryPanel(qbeConfig);
	           	//var viewport = new Ext.Viewport({items: [registry]});
	        	var viewport = new Ext.Viewport(registry);
	      	});
	    </script>
	
	</body>

</html>