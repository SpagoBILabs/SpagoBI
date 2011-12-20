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