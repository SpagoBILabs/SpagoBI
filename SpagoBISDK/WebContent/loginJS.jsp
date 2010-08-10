<%--
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Login</title>
	<style>
	body, p { font-family:Tahoma; font-size:10pt; padding-left:30; }
	pre { font-size:8pt; }
	</style>
	
	<script type="text/javascript" src="js/sbisdk-all-production.js"></script>
	<!--  script type="text/javascript" src="http://localhost:8080/SpagoBI/js/src/sdk/sbisdk-all-production.js"></script -->

	<script type="text/javascript">

		/*
		 *  setup some basic informations in order to invoke SpagoBI server's services
		 */
		Sbi.sdk.services.setBaseUrl({
	        protocol: 'http'     
	        , host: 'localhost'
	        , port: '8080'
	        , contextPath: 'SpagoBI'
	        , controllerPath: 'servlet/AdapterHTTP'  
	    });

		
		doLogin = function() {
			var userEl = document.getElementById('user');
			var passwordEl = document.getElementById('password');
			var user = userEl.value;
			var password = passwordEl.value;

		    /*
		    * the callback invoked uppon request termination
		    *
			* @param result the server response
			* @param args parameters sent to the server with the original request
			* @param seccess true if the service has been executed by the server in a succesfull way
		    */ 
		    var cb = function(result, args, success) {
		        
				if(success === true) {
					var authenticationEl =  document.getElementById('authentication');
					var examplesEl =  document.getElementById('examples');
					authenticationEl.style.display = "none";
					examplesEl.style.display = "inline";
				} else {
					alert('ERROR: Wrong username or password');
				}
		    };

		   /*
		    * authentication function
		    *
			* @param params the list of parameters to pass to the authentication servics (i.e. user & password)
			* @param callback the callback definition (i.e. fn: the function to call; scope: the scope of invocation; 
			*        args: parameters to append to the callback invocation)
		    */ 
		    Sbi.sdk.api.authenticate({ 
				params: {
					user: user
					, password: password
				}
				
				, callback: {
					fn: cb
					, scope: this
					//, args: {arg1: 'A', arg2: 'B', ...}
				}
			});
		}
	</script>
</head>


<body>
<h2>Welcome to SpagoBI SDK - JS API demo</h2>
<br/>

<div id="authentication" style="display:inline">
<span><b>Login with biuser/biuser</b></span>
<form  id="authenticationForm">
Name: <input type="text" id='user' name="user" size="30" value="biuser"/><br/>
Password: <input type="password" id="password" name="password" size="30" value="biuser"/><br/>
<input type="button" value="Login" onclick="javascript:doLogin()"/>
</form>
</div>

<div id="examples" style="display:none">
<b>Examples</b>
<dl>
	<dt> <a target="_blank" href="example1.jsp">Example 1 : getDocumentUrl</a>
	<dd> Use <i>getDocumentUrl</i> function to create the invocation url used to call execution service asking for a 
	specific execution (i.e. document + execution role + parameters) 
	<p>
	<dt> <a target="_blank" href="example2.jsp">Example 2 : getDocumentHtml</a>
	<dd> Use <i>getDocumentHtml</i> function to get an html string that contains the definition of an iframe 
	pointing to the execution service. <br>The src property of the iframe is internally populated using <i>getDocumentUrl</i> function.
	<p>
	<dt> <a target="_blank" href="example3.jsp">Example 3 : injectDocument into existing div</a>
	<dd>  Use <i>injectDocument</i> function to inject into an existing div an html string that contains the definition of an iframe 
	pointing to the execution service. <br>The html string is generated internally using <i>getDocumentHtml</i> function.
	<p>
	<dt> <a target="_blank" href="example4.jsp">Example 4 : injectDocument into existing div using ExtJs UI</a>
	<dd>  Use <i>injectDocument</i> function to inject into an existing div an html string that contains the definition of an iframe 
	pointing to the execution service. <br>The html string is generated internally using <i>getDocumentHtml</i> function. In this example 
	differently from the previous the new execution module, fully based on ajax technology, is invoked.
	<p>
	<dt> <a target="_blank" href="example5.jsp">Example 5 : injectDocument into non-existing div</a>
	<dd>  Use <i>injectDocument</i> function to inject into a div an html string that contains the definition of an iframe 
	pointing to the execution service. <br>In this example the specified target div does not exist so it is created on the fly by the function
	
</dl>
</div>





</body>
</html>