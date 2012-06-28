<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	
	<script type="text/javascript" src="js/sbisdk-all-production.js"></script>
	<!--  script type="text/javascript" src="http://localhost:8080/SpagoBI/js/src/sdk/sbisdk-all-production.js"></script -->

	<script type="text/javascript">

		Sbi.sdk.services.setBaseUrl({
	        protocol: 'http'     
	        , host: 'localhost'
	        , port: '8080'
	        , contextPath: 'SpagoBI'
	        , controllerPath: 'servlet/AdapterHTTP'  
	    });
		
		execTest4 = function() {
		    Sbi.sdk.api.injectDocument({
				documentLabel: 'RPT_WAREHOUSE_PROF'
				, executionRole: '/spagobi/user'
				, parameters: {'PARAMETERS': 'warehouse_id=19'}
				, displayToolbar: false
				, displaySliders: false
				, target: 'targetDiv'
				, iframe: {
					style: 'border: 0px;'
				}
				, useExtUI: true
			});
		};

	</script>
</head>


<body>
<h2>Example 4 : injectDocument into existing div using ExtJs UI</h2>
<hr>
<b>Description:</b> Use <i>injectDocument</i> function to inject into an existing div an html string that contains the definition of an iframe 
pointing to the execution service. The html string is generated internally using <i>getDocumentHtml</i> function. In this example 
differently from the previous the new execution module, fully based on ajax technology, is invoked.

<p>
<b>Code: </b>
<p>
<BLOCKQUOTE>
<PRE>
example4Function = function() {
	Sbi.sdk.api.injectDocument({
		documentLabel: 'RPT_WAREHOUSE_PROF'
		, executionRole: '/spagobi/user'
		, parameters: {'PARAMETERS': 'warehouse_id=19'}
		, displayToolbar: false
		, displaySliders: false
		, target: 'targetDiv'
		, iframe: {
			style: 'border: 0px;'
		}
		, useExtUI: true
	});
};
</PRE>
</BLOCKQUOTE>
<hr>
<div height="500px" width="100%" id='targetDiv'></div>
<hr>

<script type="text/javascript">
	execTest4();
</script>
</body>
</html>