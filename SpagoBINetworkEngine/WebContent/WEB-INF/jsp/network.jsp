<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%-- 
author: Alberto Ghedin
--%>



<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="it.eng.spagobi.engines.network.bean.INetwork"%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.engines.network.NetworkEngineInstance"%>
<%@page import="it.eng.spagobi.engines.network.NetworkEngineConfig"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.engines.network.bean.JSONNetwork"%>
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
	NetworkEngineInstance networkEngineInstance;
	UserProfile profile;
	Locale locale;
	String isFromCross;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	
	networkEngineInstance = (NetworkEngineInstance)ResponseContainerAccess.getResponseContainer(request).getServiceResponse().getAttribute("ENGINE_INSTANCE");
	profile = (UserProfile)networkEngineInstance.getEnv().get(EngineConstants.ENV_USER_PROFILE);
	locale = (Locale)networkEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
	
	isFromCross = (String)networkEngineInstance.getEnv().get("isFromCross");
	if (isFromCross == null) {
		isFromCross = "false";
	}
	
	NetworkEngineConfig networkEngineConfig = NetworkEngineConfig.getInstance();
    
    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
    spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
    INetwork net = networkEngineInstance.getNet();
%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeCytoscapeJS.jspf" %>
		<%@include file="commons/includeSbiNetworkJS.jspf"%>
		
		<%-- START SCRIPT FOR DOMAIN DEFINITION (MUST BE EQUAL BETWEEN SPAGOBI AND EXTERNAL ENGINES) -->
		<script type="text/javascript">
		document.domain='<%= EnginConf.getInstance().getSpagoBiDomain() %>';
		</script>
		<-- END SCRIPT FOR DOMAIN DEFINITION --%>
	
	
        <script type="text/javascript">
            window.onload=function() {
                // id of Cytoscape Web container div
                var div_id = "cytoscapeweb";

                // initialization options
                var options = {
                    // where you have the Cytoscape Web SWF
                    swfPath: "../swf/CytoscapeWeb",
                    // where you have the Flash installer SWF
                    flashInstallerPath: "../swf/playerProductInstall"
                };
                
                // init and draw
                var vis = new org.cytoscapeweb.Visualization(div_id, options);

                var networkEscaped = <%= net.getNetworkType().equals("json")?net.getNetworkAsString():("\""+StringEscapeUtils.escapeJavaScript( net.getNetworkAsString() )+"\"")	%>;
                var networkLink = <%= net.getNetworkCrossNavigation()	%>;

                if(<%= net.getNetworkType().equals("json") %>){
              	   var network = {
                     		dataSchema: {
                     			nodes: networkEscaped.nodeMetadata
                     		}
              	   };
              	  	network.data = {};
              	  	var options = <%= (net instanceof JSONNetwork)?((JSONNetwork)net).getNetworkOptions():"\"\"" %>;
              	  	network.data.edges= networkEscaped.edges;
              	  	network.data.nodes= networkEscaped.nodes;
              	 	vis.draw(Ext.apply({ network: network},options ||{}));

                 }else{
                	 vis.draw({ network: networkEscaped});
                 }

                
                

                vis.addListener("click", "edges", function(evt) {
                    var edge = evt.target;
                    var parametersString="";

                    var fixedParameters = networkLink.fixedParameters;
                    if(fixedParameters!=null && fixedParameters!=undefined){
                    	for(var parameter in fixedParameters){
                    		parametersString = parametersString+"&"+parameter+'='+fixedParameters[parameter];
                    	}
                    }

                    var dynamicParameters = networkLink.dynamicParameters;
                    if(dynamicParameters!=null && dynamicParameters!=undefined){
                    	var edgeParameters = dynamicParameters.EDGE; 
                        if(edgeParameters!=null && edgeParameters!=undefined){
                        	for(var parameter in edgeParameters){
                        		parametersString = parametersString+"&"+edgeParameters[parameter]+'='+edge.data[parameter];
                        	}
                        }
                    }

                    alert("Edge " +parametersString + " was clicked");
                });
            };
        </script>
        
        <style>
            /* The Cytoscape Web container must have its dimensions set. */
            html, body { height: 100%; width: 100%; padding: 0; margin: 0; }
            #cytoscapeweb { width: 100%; height: 100%; }
        </style>
    </head>
    
    <body>
    SSSSSSSSSSS
        <div id="cytoscapeweb">
            Cytoscape Web will replace the contents of this div with your graph.
        </div>
    </body>
	


</html>




	

	
	
	
	
	
	
	
	
	
	
	
	
    