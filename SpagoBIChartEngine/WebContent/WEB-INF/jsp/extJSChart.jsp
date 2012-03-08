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
<%@page import="it.eng.spagobi.engines.chart.ChartEngineConfig"%>
<%@page import="it.eng.spagobi.engines.chart.ChartEngineInstance"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.tools.dataset.bo.IDataSet"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	ChartEngineInstance chartEngineInstance;
	UserProfile profile;
	Locale locale;
	IDataSet ds;
	String dsLabel;
	String dsTypeCd;
	String transformerType;
	String isFromCross;
	String engineContext;
	String engineServerHost;
	String enginePort;
	String executionId;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	
	chartEngineInstance = (ChartEngineInstance)ResponseContainerAccess.getResponseContainer(request).getServiceResponse().getAttribute("ENGINE_INSTANCE");
	profile = (UserProfile)chartEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);
	locale = (Locale)chartEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	ds =  (IDataSet)chartEngineInstance.getDataSet();
	dsLabel = (ds != null) ? ds.getLabel() : "";
	dsTypeCd = (ds != null) ? ds.getDsType() : "";
	transformerType = (ds != null) ? ds.getTransformerCd() : "";
	
	isFromCross = (String)chartEngineInstance.getEnv().get("isFromCross");
	if (isFromCross == null) {
		isFromCross = "false";
	}
	
	ChartEngineConfig chartEngineConfig = ChartEngineConfig.getInstance();
    
    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
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
    
    String chartTemplate = chartEngineInstance.getTemplate().toString();
    // gets analytical driver
    //Map analyticalDrivers  = chartEngineInstance.getAnalyticalDrivers();

%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeExtJS_410.jspf" %> 
		<%@include file="commons/includeSbiChartJS.jspf"%>
	</head>
	
	<body>		
    	<script type="text/javascript">  
	    	var template =  <%= chartTemplate  %>;	    	
			
	    	Sbi.config = {};
			
			var url = {
				  host: '<%= engineServerHost %>'
				, port: '<%= enginePort %>'
				, contextPath: '<%= engineContext %>'
			};
		
			var params = {
				SBI_EXECUTION_ID: <%=executionId %>			
			  , LIGHT_NAVIGATOR_DISABLED: 'TRUE'
			};
			Sbi.config.serviceRegistry = Ext.create('Sbi.service.ServiceRegistry',{ baseUrl: url
    																			  , baseParams: params
    									  }); 
			Sbi.config.spagobiServiceRegistry = Ext.create('Sbi.service.ServiceRegistry',{
															baseUrl: {contextPath: '<%= spagobiContext %>'}
														  , baseParams: {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
												});
	        //add to executionContext all parameters... it's really necessary??
	        //var executionContext = {};	       
	        //template.executionContext = executionContext;
	        var config ={};
	        config.template = template
	        config.divId = "<%=executionId%>";
	        config.dsLabel = "<%=dsLabel%>";
	        config.dsTypeCd = "<%=dsTypeCd%>";
	        config.dsTransformerType= "<%=transformerType%>";
	        config.dsPars = []; //temporaneo
			Ext.onReady(function() { 
				Ext.QuickTips.init();
				var chartPanel = Ext.widget('ExtJSChartPanel',config); //by alias
				var viewport = new Ext.Viewport(chartPanel);
			});

	    </script>
	    <div id="<%=executionId%>_title" align="center" style="width:90%;"></div>
	    <div id="<%=executionId%>_subtitle" align="center" style="width:90%;"></div>
	    <div id="<%=executionId%>" align="center" style="width:90%;"></div>    
	</body>

</html>
