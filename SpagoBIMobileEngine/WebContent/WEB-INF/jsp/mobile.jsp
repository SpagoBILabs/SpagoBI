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
author: Monica Franceschini

--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engine.mobile.*"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	MobileEngineConfig mobileEngineConfig;
	MobileEngineInstance mobileEngineInstance;	
	UserProfile profile;
	Locale locale;	
	
	String engineContext;
	String engineServerHost;
	String enginePort;
	String executionId;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	
	
	mobileEngineConfig = MobileEngineConfig.getInstance();
	mobileEngineInstance = (MobileEngineInstance)ResponseContainerAccess.getResponseContainer(request).getServiceResponse().getAttribute("ENGINE_INSTANCE");
	///title template configuration
	JSONObject title = mobileEngineInstance.getTitle();
	String titleValue= title.getString("value");
	String titleStyle= title.getString("style");
	//columns template configuration
	JSONArray columns = mobileEngineInstance.getColumns();
	String colMod = "[]";
	String colFields = "[]";
	String conditions = "[]";
	if(columns != null){
		colMod = columns.toString();
	}
	JSONArray fieldsJSON = mobileEngineInstance.getFields();
	if(fieldsJSON != null){
		colFields = fieldsJSON.toString();
	}
	JSONArray conditionsJSON = mobileEngineInstance.getConditions();
	if(conditionsJSON != null){
		conditions = conditionsJSON.toString();
	}
	profile = (UserProfile)mobileEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);	
	locale = (Locale)mobileEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	    
	// used in remote ServiceRegistry
    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);

    spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
    
 	// used in local ServiceRegistry
 	engineServerHost = request.getServerName();
 	enginePort = "" + request.getServerPort();
    engineContext = request.getContextPath();
    if( engineContext.startsWith("/") || engineContext.startsWith("\\") ) {
    	engineContext = request.getContextPath().substring(1);
    }
    String documentId = request.getParameter("document");
    executionId = request.getParameter("SBI_EXECUTION_ID");
    if(executionId != null) {
    	executionId = "'" + request.getParameter("SBI_EXECUTION_ID") + "'";
    } else {
    	executionId = "null";
    }   
%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>

	<head>
		<link rel="stylesheet" href="../css/sencha-touch-debug.css" type="text/css">

		<link rel="stylesheet" href="../css/Ext.ux.TouchGridPanel.css" type="text/css">

		<script type="text/javascript" src="../js/sencha/sencha-touch-debug.js"></script>

		<script type="text/javascript" src="../js/sencha/Ext.ux.TouchGridPanel.js"></script>
		<script type="text/javascript" src="../js/sencha/Ext.ux.touch.PagingToolbar.js"></script>
		<script type="text/javascript" src="../js/spagobi/service/ServiceRegistry.js"></script>
		
	</head>

	<body>

		
		<script>


	      Ext.setup({
	    	    icon: 'icon.png',
	    	    tabletStartupScreen: 'tablet_startup.png',
	    	    phoneStartupScreen: 'phone_startup.png',
	    	    glossOnIcon: true,
	    	    fullscreen: true,
	    	    
	    	    onReady: function() {			

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
			    };
		

		        var executionContext = {};

				var paramsList = {MESSAGE_DET: "GET_DATA", document: <%= documentId%>};


			    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			    	baseUrl: url
			        , baseParams: params
	        
			    });
				var getColumns = Sbi.config.serviceRegistry.getServiceUrl({
					serviceName: 'TABLE_ACTION'
					, baseParams: paramsList
				});	
		      var store = new Ext.data.Store({
		     		root: 'columns'
		     		, fields: <%=colFields%>
		      		, pageSize: 10
					//, autoLoad: true
		     		, proxy: {
			              type: 'ajax',
			              url: getColumns,		              
			              reader: {
			                  type: 'json',
			                  root: 'columns',	 
			                  totalProperty: "total",                 
			                  totalCount: 'total'
			              }
		          }
		      });
	      
		      
		      store.load();
	    	  		Ext.ux.TouchGridPanel = new Ext.ux.TouchGridPanel({
						fullscreen  : true,
						store       : store,
			            plugins    : new Ext.ux.touch.PagingToolbar({
			                store : store
			            }),
						multiSelect : false,
						dockedItems : [{
							xtype : "toolbar",
							dock  : "top",
							title : '<%=titleValue %>',
							style:  '<%=titleStyle %>'
						}],
						conditions  : <%= conditions%>,
						colModel    : <%= colMod%>
					});
	    	  
	    	    }
	    	});

		</script>
		
		
		 
	</body>
 
</html>




	

	
	
	
	
	
	
	
	
	
	
	
	
    