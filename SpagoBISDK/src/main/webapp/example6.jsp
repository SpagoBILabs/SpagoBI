<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style>
	table, td {
	    border: 1px solid black;
	}
	
	th {
		background-color: gray;
	}
	</style>
	<script type="text/javascript" src="js/sbisdk-all-production.js"></script>
	<!--  script type="text/javascript" src="http://localhost:8080/SpagoBI/js/src/sdk/sbisdk-all-production.js"></script -->

	<script type="text/javascript">

		Sbi.sdk.services.setBaseUrl({
	        protocol: 'http'     
	        , host: 'localhost'
	        , port: '8080'
	        , contextPath: 'SpagoBI' 
	    });
		
 		execTest6 = function() {
		    Sbi.sdk.api.getDataSetList({
		    	callback: function( json, args, success ) {
		    		if (success){
		    			var str = "";
		    			
		    			for (var key in json){
			    			str += "<tr><td>" + json[key].label + "</td><td>" + json[key].name + "</td><td>" + json[key].description + "</td></tr>";
		    			}
		    			
		    			document.getElementById('datasets').innerHTML = str;
		    		}
				}});
		};
	</script>
</head>


<body>
<h2>Example 6 : getDataSetList</h2>
<hr>
<b>Description: </b> Use <i>getDataSetList</i> function to retrieve the list of all datasets
<p>
<b>Code: </b>
<p>
<BLOCKQUOTE>
<PRE>
execTest6 = function() {
    Sbi.sdk.api.getDataSetList({
    	callback: function( json, args, success ) {
    		if (success){
    			var str = "";
    			
    			for (var key in json){
	    			str += "&lt;tr&gt;&lt;td&gt;" + json[key].label + "&lt;/td&gt;&lt;td&gt;" + json[key].name + "&lt;/td&gt;&lt;td&gt;" + json[key].description + "&lt;/td&gt;&lt;/tr&gt;";
    			}
    			
    			document.getElementById('datasets').innerHTML = str;
    		}
		}});
};
</PRE>
</BLOCKQUOTE>
<hr>
<table>
	<th>Label</th>
	<th>Name</th>
	<th>Description</th>
	
	<tbody id="datasets">
	</tbody>
</table>

<script type="text/javascript">
	execTest6();
</script>
<hr>

</body>
</html>