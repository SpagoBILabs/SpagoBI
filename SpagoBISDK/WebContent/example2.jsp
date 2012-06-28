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
		
		execTest2 = function() {
		    var html = Sbi.sdk.api.getDocumentHtml({
				documentLabel: 'RPT_WAREHOUSE_PROF'
				, executionRole: '/spagobi/user'
				, parameters: {warehouse_id: 19}
				, displayToolbar: false
				, displaySliders: false
				, iframe: {
		    		height: '500px'
		    		, width: '100%'
					, style: 'border: 0px;'
				}
			});
		    document.getElementById('targetDiv').innerHTML = html;
		};
	</script>
</head>


<body>
<h2>Example 2 : getDocumentHtml</h2>
<hr>
<b>Description: </b> Use <i>getDocumentHtml</i> function to get an html string that contains the definition of an iframe 
	pointing to the execution service. The src property of the iframe is internally populated using <i>getDocumentUrl</i> function.
<p>
<b>Code: </b>
<p>
<BLOCKQUOTE>
<PRE>
example2Function = function() {
	var html = Sbi.sdk.api.getDocumentHtml({
		documentLabel: 'RPT_WAREHOUSE_PROF'
		, executionRole: '/spagobi/user'
		, parameters: {warehouse_id: 19}
		, displayToolbar: false
		, displaySliders: false
		, iframe: {
			height: '500px'
		    , width: '100%'
			, style: 'border: 0px;'
		}
	});
	document.getElementById('targetDiv').innerHTML = html;
};
</PRE>
</BLOCKQUOTE>
<hr>
<div height="300px" width="800px" id='targetDiv'></div>
<hr>

<script type="text/javascript">
	execTest2();
</script>
</body>
</html>