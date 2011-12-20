<%--
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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