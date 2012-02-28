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

<html manifest="mobilespagobi.manifest">

	<head>
		<link rel="stylesheet" href="../css/add2home.css" type="text/css">
		<link rel="apple-touch-icon" href="../img/iOS-57.png" /> 
		<link rel="apple-touch-icon" sizes="72x72" href="../img/iOS-72.png" /> 
		<link rel="apple-touch-icon" sizes="114x114" href="../img/iOS-114.png" /> 
		<link rel="apple-touch-startup-image" href="../img/startup.png">
		
		<script type="text/javascript">var addToHomeConfig = { touchIcon:true };</script> 
		<script type="text/javascript" src="../js/add2home/add2home.js"></script> 

		<link rel="stylesheet" href="../css/sencha-touch-debug.css" type="text/css">
		<link rel="stylesheet" href="../css/spagobi-mobile.css" type="text/css">
		<link rel="stylesheet" href="../css/Ext.ux.TouchGridPanel.css" type="text/css">
		<link rel="stylesheet" href="../css/touch-charts-demo.css" type="text/css">
		

		<script type="text/javascript" src="../js/sencha/sencha-touch-debug.js"></script>
		<script type="text/javascript" src="../js/sencha/touch-charts-debug.js"></script>
		<script type="text/javascript" src="../js/add2home/add2home.js"></script>
		
		<script type="text/javascript" src="../js/sencha/Ext.ux.TouchGridPanel.js"></script>
		<script type="text/javascript" src="../js/sencha/Ext.ux.touch.PagingToolbar.js"></script>
		<script type="text/javascript" src="../js/spagobi/service/ServiceRegistry.js"></script>
		<script type="text/javascript" src="../js/spagobi/app.js"></script>		
		<script type="text/javascript" src="../js/spagobi/MobileModels.js"></script>
		<script type="text/javascript" src="../js/spagobi/MobileController.js"></script>
		<script type="text/javascript" src="../js/spagobi/Viewport.js"></script>
		<script type="text/javascript" src="../js/spagobi/Login.js"></script>
		<script type="text/javascript" src="../js/spagobi/MainContainer.js"></script>
		<script type="text/javascript" src="../js/spagobi/DocumentBrowser.js"></script>
		<script type="text/javascript" src="../js/spagobi/DocumentPreview.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/WidgetPanel.js"></script>		
		<script type="text/javascript" src="../js/spagobi/execution/table/TableExecutionPanel.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/chart/ChartExecutionPanel.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/composed/ComposedExecutionPanel.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/composed/ComposedExecutionController.js"></script>
		<!-- script type="text/javascript" src="../js/spagobi/Utils.js"></script-->
		<script type="text/javascript" src="../js/spagobi/execution/BottomToolbar.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/ExecutionView.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/ExecutionController.js"></script>
		
		<script type="text/javascript" src="../js/spagobi/execution/CrossExecutionView.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/ParametersView.js"></script>
		<script type="text/javascript" src="../js/spagobi/execution/ParametersController.js"></script>

		
		
	</head>

	<body>

		 <script>
			var hostGlobal= '<%= request.getServerName()%>';
			var portGlobal= '<%= request.getServerPort()%>';
		 </script>
	</body>
 
</html>