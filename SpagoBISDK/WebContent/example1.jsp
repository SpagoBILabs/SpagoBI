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
		
		execTest1 = function() {
		    var url = Sbi.sdk.api.getDocumentUrl({
				documentLabel: 'RPT_WAREHOUSE_PROF'
				, executionRole: '/spagobi/user'
				, parameters: {warehouse_id: 19}
				, displayToolbar: false
				, displaySliders: false
				, iframe: {
					style: 'border: 0px;'
				}
			});
		    document.getElementById('execiframe').src = url;
		};
	</script>
</head>


<body>
<h2>Example 1 : getDocumentUrl</h2>
<hr>
<b>Description: </b> Use <i>getDocumentUrl</i> function to create the invocation url used to call execution service asking for a 
specific execution (i.e. document + execution role + parameters) 
<p>
<b>Code: </b>
<p>
<BLOCKQUOTE>
<PRE>
example1Function = function() {
	var url = Sbi.sdk.api.getDocumentUrl({
		documentLabel: 'RPT_WAREHOUSE_PROF'
		, executionRole: '/spagobi/user'
		, parameters: {warehouse_id: 19}
		, displayToolbar: false
		, displaySliders: false
		, iframe: {
			style: 'border: 0px;'
		}
	});

	document.getElementById('execiframe').src = url;
};
</PRE>
</BLOCKQUOTE>
<hr>
<iframe id="execiframe" src='' height="400px" width="100%"></iframe>
<hr>

<script type="text/javascript">
	execTest1();
</script>
</body>
</html>