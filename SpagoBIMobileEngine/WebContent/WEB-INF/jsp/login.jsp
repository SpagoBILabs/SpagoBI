<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%-- 
authors: Monica Franceschini
		 Davide Zerbetto

--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>

<html>



	<head>
			<script>
				function backButtonOverride()
				{
				  // Work around a Safari bug
				  // that sometimes produces a blank page
				  setTimeout("backButtonOverrideBody()", 1);
				
				}
				
				function backButtonOverrideBody()
				{
				  // Works if we backed up to get here
				  try {
				    history.forward();
				  } catch (e) {
				    // OK to ignore
				  }
				  // Every quarter-second, try again. The only
				  // guaranteed method for Opera, Firefox,
				  // and Safari, which don't always call
				  // onLoad but *do* resume any timers when
				  // returning to a page
				  setTimeout("backButtonOverrideBody()", 500);
				}
			</script>
		<%@ include file="/WEB-INF/jsp/importSenchaJSLibrary.jspf" %>
		<%@ include file="/WEB-INF/jsp/constants.jspf" %>
		<%@ include file="/WEB-INF/jsp/env.jspf" %>
		<%@ include file="/WEB-INF/jsp/importSbiJS.jspf" %>
		
	</head>



	<body onLoad="backButtonOverride()">

		 <script>
		 	var ajaxReqGlobalTimeout = 120000;
			var hostGlobal= '<%= request.getServerName()%>';
			var portGlobal= '<%= request.getServerPort()%>';
		 </script>
	</body>
 
</html>