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
		
 		execTest7 = function() {
		    Sbi.sdk.cors.api.executeDataSet({
		    	datasetLabel: 'DS_DEMO_EXTCHART'
		    	, parameters: {
		    		par_year: 1998,
		    		par_family: 'Food'
		    	}
		    	, basicAuthentication: {
		    		userName: 'biadmin'
		    		, password: 'biadmin'
		    	}
		    	, callbackOk: function(obj) {
		    		var str = "<th>Id</th>";
		    		
	    			var fields = obj.metaData.fields;
	    			for(var fieldIndex in fields) {
	    				if (fields[fieldIndex].hasOwnProperty('header'))
	    					str += '<th>' + fields[fieldIndex]['header'] + '</th>';
	    			}
	    			
	    			str += '<tbody>';
	    			
	    			var rows = obj.rows;
	    			for (var rowIndex in rows){
	    				str += '<tr>';
	    				for (var colIndex in rows[rowIndex]) {
	    					str += '<td>' + rows[rowIndex][colIndex] + '</td>';
	    				}
	    				str += '</tr>';
	    			}
	    			
	    			str += '</tbody>';
	    			
	    			document.getElementById('results').innerHTML = str;
				}});
		};
	</script>
</head>


<body>
<h2>Example 7 : executeDataSet</h2>
<hr>
<b>Description: </b> Use <i>executeDataSet</i> function to get the content of a specific dataset
<p>
<b>Code: </b>
<p>
<BLOCKQUOTE>
<PRE>
execTest7 = function() {
    Sbi.sdk.cors.api.executeDataSet({
    	datasetLabel: 'DS_DEMO_EXTCHART'
    	, parameters: {
    		par_year: 1998,
    		par_family: 'Food'
    	}
    	, basicAuthentication: {
    		userName: 'biadmin'
    		, password: 'biadmin'
    	}
    	, callbackOk: function(obj) {
    		var str = "&lt;th&gt;Id&lt;/th&gt;";
    		
   			var fields = obj.metaData.fields;
   			for(var fieldIndex in fields) {
   				if (fields[fieldIndex].hasOwnProperty('header'))
   					str += '&lt;th&gt;' + fields[fieldIndex]['header'] + '&lt;/th&gt;';
   			}
   			
   			str += '&lt;tbody&gt;';
   			
   			var rows = obj.rows;
   			for (var rowIndex in rows){
   				str += '&lt;tr&gt;';
   				for (var colIndex in rows[rowIndex]) {
   					str += '&lt;td&gt;' + rows[rowIndex][colIndex] + '&lt;/td&gt;';
   				}
   				str += '&lt;/tr&gt;';
   			}
   			
   			str += '&lt;/tbody&gt;';
   			
   			document.getElementById('results').innerHTML = str;
		}});
};
</PRE>
</BLOCKQUOTE>
<hr>
<table id="results">

</table>

<script type="text/javascript">
	execTest7();
</script>
<hr>

</body>
</html>