<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  

<%-- 
author: Antonella Giachino (antonella.giachino@eng.it)
--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engines.console.ConsoleEngineConfig"%>
<%@page import="it.eng.spagobi.engines.console.ConsoleEngineInstance"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	ConsoleEngineConfig consoleEngineConfig;
	ConsoleEngineInstance consoleEngineInstance;	
	UserProfile profile;
	Locale locale;	
	String documentLabel;
	
	String engineContext;
	String engineServerHost;
	String enginePort;
	String executionId;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	
	
	consoleEngineConfig = ConsoleEngineConfig.getInstance();
	consoleEngineInstance = (ConsoleEngineInstance)ResponseContainerAccess.getResponseContainer(request).getServiceResponse().getAttribute("ENGINE_INSTANCE");
	
	profile = (UserProfile)consoleEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);	
	locale = (Locale)consoleEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	documentLabel = (String)consoleEngineInstance.getEnv().get(EngineConstants.ENV_DOCUMENT_LABEL);
	    
	// used in remote ServiceRegistry
    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
    spagobiContext = spagobiContext.substring(1);
    spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
    
 	// used in local ServiceRegistry
 	engineServerHost = request.getServerName();
 	enginePort = "" + request.getServerPort();
    engineContext = request.getContextPath();
    if( engineContext.startsWith("/") || engineContext.startsWith("\\") ) {
    	engineContext = request.getContextPath().substring(1);
    }
    
    executionId = request.getParameter("SBI_EXECUTION_ID");
    if(executionId != null) {
    	executionId = "'" + request.getParameter("SBI_EXECUTION_ID") + "'";
    } else {
    	executionId = "null";
    }   
    // gets analytical driver
    Map analyticalDrivers  = consoleEngineInstance.getAnalyticalDrivers();

//    String localeResourcePath = consoleEngineConfig.getEngineResourcePath() + System.getProperty("file.separator") + "user_messages_"+locale.getLanguage()+".js";

%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeSbiConsoleJS.jspf"%>
		 
		<!-- Active TEST  -->
		<!--  %@include file="tests/template.jspf"% -->
		<!-- Active TEST  -->
		
		<%-- START SCRIPT FOR DOMAIN DEFINITION (MUST BE EQUAL BETWEEN SPAGOBI AND EXTERNAL ENGINES) --
		<script type="text/javascript">
		document.domain='<%= EnginConf.getInstance().getSpagoBiDomain() %>';
		</script>
		-- END SCRIPT FOR DOMAIN DEFINITION --%>
	
	</head>

	<body  >
		<!--  workaround for cas case...  -->
		<iframe id='invalidSessionCommonJ'
                 name='invalidSessionCommonJ'
                 src='/SpagoBICommonJEngine/servlet/AdapterHTTP?ACTION_NAME=START_WORK'
                 height='0'
                 width='0'
                 frameborder='0' >
		</iframe>
		
		<script>

			var template = Sbi.template || <%= consoleEngineInstance.getTemplate().toString()  %>;
			
			Sbi.config = {};

			Sbi.chart.SpagoBIChart.CHART_BASE_URL =  '/<%= engineContext %>/swf/spagobichart/';
			Sbi.chart.OpenFlashChart.CHART_URL = '/<%= engineContext %>/swf/openflashchart/open-flash-chart.swf';
			Sbi.chart.FusionFreeChart.CHART_URL = '/<%= engineContext %>/swf/fusionchartfree/FCF_Column3D.swf';
			
			var url = {
				host: '<%= engineServerHost %>'
				, port: '<%= enginePort %>'
				, contextPath: '<%= engineContext %>'
			};
		
			var params = {
				SBI_EXECUTION_ID: <%=executionId %>
				, LOCALE: '<%=locale%>'
				, DOCUMENT_LABEL: '<%=documentLabel%>'
				, LIGHT_NAVIGATOR_DISABLED: 'TRUE'
			};
		
			Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
				baseUrl: url
			    , baseParams: params
			});

			Sbi.config.commonjServiceRegistry = new Sbi.service.ServiceRegistry({
				baseUrl: {
					contextPath: 'SpagoBICommonJEngine'
				}
			    , baseParams: {NEW_SESSION: 'TRUE', LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
			});


			
			Sbi.config.spagobiServiceRegistry = new Sbi.service.ServiceRegistry({
				baseUrl: {
					contextPath: '<%= spagobiContext %>'
				}
			    , baseParams: {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
			});
			

			// javascript-side user profile object
	         Ext.ns("Sbi.user");
	        Sbi.user.userId = "<%= profile.getUserId() %>";
	        Sbi.user.locale = "<%= locale%>";

	       var executionContext = {};
	       <% Iterator it = analyticalDrivers.keySet().iterator();
			  while(it.hasNext()) {
				String parameterName = (String)it.next();
				//System.out.println("parameterName: " + parameterName);
				String parameterValue = (String)analyticalDrivers.get(parameterName);
				//if (parameterValue.indexOf("'")>=0) parameterValue = parameterValue.replaceAll("'","");				
				//System.out.println("parameterValue: " + parameterValue);
				if (parameterValue != null && !parameterValue.equals("")){
					if  (parameterValue.startsWith("'")){
						if ( parameterValue.indexOf(",") >= 0){					
			%>
							executionContext ['<%=parameterName%>'] = [<%=parameterValue%>];
			<%			}else{
			%>
							executionContext ['<%=parameterName%>'] = <%=parameterValue%>;
			<%
						}
					}else{
						if ( parameterValue.indexOf(",") >= 0){	
		   %>
		   					executionContext ['<%=parameterName%>'] = ['<%=parameterValue%>']';
		   <%			}else{ %>
							executionContext ['<%=parameterName%>'] = '<%=parameterValue%>';
		   <%			}
			    	}
				} //if
        	  } //while
	       %>
	       template.executionContext = executionContext;

			Ext.onReady(function() { 
				Ext.QuickTips.init();				
				var consolePanel = new Sbi.console.ConsolePanel(template);
				var viewport = new Ext.Viewport(consolePanel);  
			});
		</script>
		
		
		 
	</body>
 
</html>




	

	
	
	
	
	
	
	
	
	
	
	
	
    