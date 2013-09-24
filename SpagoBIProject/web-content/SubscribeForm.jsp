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


<%@ page language="java"
         extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" %>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities,
                it.eng.spagobi.commons.constants.SpagoBIConstants,
                it.eng.spagobi.commons.SingletonConfig"%>
                
<script type="text/javascript" src='js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>
    
<script type="text/javascript" src='js/src/ext/sbi/service/ServiceRegistry.js'/></script>
    
                
<% 
	SingletonConfig serverConfig = SingletonConfig.getInstance();

	//default url
	String contextName = ChannelUtilities.getSpagoBIContextName(request);
	
	String actionURL = contextName + "/restful-services/subscribe/getUserInfo";

%>

<script type="text/javascript">
function submitform()
{
	//Service Registry creation
	var url = {
	    	host: '<%= request.getServerName()%>'
	    	, port: '<%= request.getServerPort()%>'
	    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?request.getContextPath().substring(1): request.getContextPath()%>'
	    	, controllerPath: null // no cotroller just servlets   
	    };
	Sbi.config = {};
	Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
        , baseParams: params
    });
	
	
	this.services = [];
	
	//Adding a new service to the registry
	this.services["getUserInfo"]= Sbi.config.serviceRegistry.getRestServiceUrl({
		serviceName: 'subscribe/getUserInfo',
		baseParams: {}
	});
	
	
	alert("Submit function");
 	var form = document.myForm;
 	var firstname = document.getElementById("firstName").value;
 	var lastname = document.getElementById("lastName").value;
 	
 	var params = new Object();
 	params.firstname = firstname;
 	params.lastname = lastname;
 	
	Ext.Ajax.request({
		url: this.services["getUserInfo"],
		method: "POST",
		params: params,			
		success : function(response, options) {	
			alert("Service invoked ok");
			if(response.responseText!=null && response.responseText!=undefined){
				var jsonData = Ext.decode(response.responseText);
				alert("User registered: "+jsonData.firstname+" "+jsonData.lastname);
			}
            

		},
		scope: this,
		failure: function ( result, request ) {
            alert("Error invoking rest service");

		}     
	});
}
</script>


<HTML>
<HEAD>
  
<TITLE>Example user subscribe page</TITLE> 

</HEAD>
<BODY>
<h2>Example user subscribe page</h2> 
<div width="100%" style="float:left; width:100%;">
<p style="float:left; width:100%;">Please insert information</p>

<span style="float:left; width: 60%; text-align:left;">
<form name="myForm" method="post"   
action="javascript: submitform()">

First name: <input type="text" name="firstname" id="firstName"><br>
Last name: <input type="text" name="lastname" id="lastName"><br>
<input type="submit" value="Submit">
</form>

</span>

</div>
</BODY>
