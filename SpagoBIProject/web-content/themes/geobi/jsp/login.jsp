<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

  
<%@ page language="java"
         extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" 
         import="it.eng.spago.base.*,
                 it.eng.spagobi.commons.constants.SpagoBIConstants"
%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.IMessageBuilder"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.IUrlBuilder"%>
<%@page import="it.eng.spago.base.SourceBean"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.utilities.themes.ThemesManager"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Enumeration"%>


<%
	String contextName = ChannelUtilities.getSpagoBIContextName(request);	
    

	String authFailed = "";
	String startUrl = "";
	ResponseContainer aResponseContainer = ResponseContainerAccess
			.getResponseContainer(request);
	//RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request); 
	RequestContainer requestContainer = RequestContainer
			.getRequestContainer();
	//SessionContainer sessionContainer = requestContainer.getSessionContainer();

	SingletonConfig serverConfig = SingletonConfig.getInstance();
	String strInternalSecurity = serverConfig
			.getConfigValue("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS.className");
	boolean isInternalSecurity = (strInternalSecurity
			.indexOf("InternalSecurity") > 0) ? true : false;
	String roleToCheckLbl = SingletonConfig.getInstance()
			.getConfigValue("SPAGOBI.SECURITY.ROLE_LOGIN");
	String roleToCheckVal = "";
	if (!("").equals(roleToCheckLbl)) {
		roleToCheckVal = (request.getParameter(roleToCheckLbl) != null) ? request
				.getParameter(roleToCheckLbl) : "";
		if (("").equals(roleToCheckVal)) {
			//			roleToCheckVal = ( sessionContainer.getAttribute(roleToCheckLbl)!=null)?
			//						(String)sessionContainer.getAttribute(roleToCheckLbl):"";
			roleToCheckVal = (session.getAttribute(roleToCheckLbl) != null) ? (String) session
					.getAttribute(roleToCheckLbl) : "";
		}
	}

	String currTheme = ThemesManager.getCurrentTheme(requestContainer);
	if (currTheme == null)
		currTheme = ThemesManager.getDefaultTheme();

	if (aResponseContainer != null) {
		SourceBean aServiceResponse = aResponseContainer
				.getServiceResponse();
		if (aServiceResponse != null) {
			SourceBean loginModuleResponse = (SourceBean) aServiceResponse
					.getAttribute("LoginModule");
			if (loginModuleResponse != null) {
				String authFailedMessage = (String) loginModuleResponse
						.getAttribute(SpagoBIConstants.AUTHENTICATION_FAILED_MESSAGE);
				startUrl = (loginModuleResponse.getAttribute("start_url") == null) ? ""	: (String) loginModuleResponse.getAttribute("start_url");
				if (authFailedMessage != null) {
					authFailed = authFailedMessage;
				}
			}
		}
	}

	IMessageBuilder msgBuilder = MessageBuilderFactory
			.getMessageBuilder();

	String sbiMode = "WEB";
	IUrlBuilder urlBuilder = null;
	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
%>






<%@page import="it.eng.spagobi.commons.SingletonConfig"%>
<html>
  <head>
  <style media="screen" type="text/css">

	input.login    {
	display:block;
	border: 1px solid #a9a9a9; 
	color: #7b7575;
	background: #d4d4d4; 
	height: 25px;
	width: 300px;
	-webkit-box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.3);
	-moz-box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.3);
	box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.3);
	}
	body {
background: #dedede; /* Old browsers */
background: -moz-linear-gradient(top,  #dedede 0%, #efefef 100%); /* FF3.6+ */
background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#dedede), color-stop(100%,#efefef)); /* Chrome,Safari4+ */
background: -webkit-linear-gradient(top,  #dedede 0%,#efefef 100%); /* Chrome10+,Safari5.1+ */
background: -o-linear-gradient(top,  #dedede 0%,#efefef 100%); /* Opera 11.10+ */
background: -ms-linear-gradient(top,  #dedede 0%,#efefef 100%); /* IE10+ */
background: linear-gradient(to bottom,  #dedede 0%,#efefef 100%); /* W3C */
filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#dedede', endColorstr='#efefef',GradientType=0 ); /* IE6-9 */
height: 100%;
margin: 0;
background-repeat: no-repeat;
background-attachment: fixed;
	}
	td.login-label{
 	font-family: Tahoma,Verdana,Geneva,Helvetica,sans-serif;
	font-size: 10 px;
	color: #7d7d7d;
}

a:link{
 	font-family: Tahoma,Verdana,Geneva,Helvetica,sans-serif;
	font-size: 9px;
	color: #7d7d7d;
}
a:visited{
 	font-family: Tahoma,Verdana,Geneva,Helvetica,sans-serif;
	font-size: 9px;
	color: #7d7d7d;
}
a:hover{
 	font-family: Tahoma,Verdana,Geneva,Helvetica,sans-serif;
	font-size: 9px;
	color: #7d7d7d;
}

 </style>
  
  
  <script type="text/javascript">
    function signup(){
    	var form = document.getElementById('formId');
    	var act = '${pageContext.request.contextPath}/restful-services/signup/prepare';
    	form.action = act;
    	form.submit();
    	
    }
	function escapeUserName(){
		
		userName = document.login.userID.value;

		if (userName.indexOf("<")>-1 || userName.indexOf(">")>-1 || userName.indexOf("'")>-1 || userName.indexOf("\"")>-1 || userName.indexOf("%")>-1)
			{
			alert('Invalid username');
			return false;
			}
		else
			{
				/*var form = document.getElementById('formId');
				alert(form.action);
				form.submit();*/
				return true;
			}
		
	 }
	
	 function setUser(userV, pswV){
		var password = document.getElementById('password');
		var user = document.getElementById('userID');
		password.value = pswV;
		user.value = userV;
	 //	document.forms[0].submit();
	 }
	 
	  function changefield(){
	        document.getElementById("passwordbox").innerHTML = "<input id=\"password\" type=\"password\" name=\"password\" title=\"Password\" />";
	        document.getElementById("password").focus();
	     
	    }
	</script>
	<link rel="shortcut icon" href="<%=urlBuilder.getResourceLink(request, "img/favicon.ico")%>" />
    <title>SpagoBI</title>
  </head>

  <body>

	<LINK rel='StyleSheet' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/spagobi_shared.css",currTheme)%>' type='text/css' />
	<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/analiticalmodel/browser/standard.css",currTheme)%>'/>

	        
   <% 
   	String userDefaultValue = "Username";
   	String pwdDefaultValue = "Password";
   %>
   <main class="loginPage main-maps-list main-list" id="main">
      	<div class="aux">
          	<div class="reserved-area-container">
          		<h1>Login</h1>
                  <form  id="formId" name="login" action="<%=contextName%>/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE" method="POST" onsubmit="return escapeUserName()" class="reserved-area-form login">
                   <input type="hidden" id="isInternalSecurity" name="isInternalSecurity" value="<%=isInternalSecurity %>" />        	
	        		<input type="hidden" id="<%=roleToCheckLbl%>" name="<%=roleToCheckLbl%>" value="<%=roleToCheckVal%>" />
		        	<%	
			        	//manages backUrl after login
			        	String backUrl = (String)request.getAttribute(SpagoBIConstants.BACK_URL);
			        	if (backUrl != null && !backUrl.equals("")) {
					%>
				 	
					<input type="hidden" name="<%= SpagoBIConstants.BACK_URL %>" value="<%= backUrl %>" />
					<input type="hidden" name="fromLogin" value="true" />
					<%
		        	}
		        	// propagates parameters (if any) for document execution
		        	if (request.getParameter(ObjectsTreeConstants.OBJECT_LABEL) != null) {
		        		String label = request.getParameter(ObjectsTreeConstants.OBJECT_LABEL);
		        	    String subobjectName = request.getParameter(SpagoBIConstants.SUBOBJECT_NAME);
		        	    %>
		        	    <input type="hidden" name="<%= ObjectsTreeConstants.OBJECT_LABEL %>" value="<%= StringEscapeUtils.escapeHtml(label) %>" />
		        	    <% if (subobjectName != null && !subobjectName.trim().equals("")) { %>
		        	    	<input type="hidden" name="<%= SpagoBIConstants.SUBOBJECT_NAME %>" value="<%= StringEscapeUtils.escapeHtml(subobjectName) %>" />
		        	    <% } %>
		        	    <%
		        	    // propagates other request parameters than PAGE, NEW_SESSION, OBJECT_LABEL and SUBOBJECT_NAME
		        	    Enumeration parameters = request.getParameterNames();
		        	    while (parameters.hasMoreElements()) {
		        	    	String aParameterName = (String) parameters.nextElement();
		        	    	if (aParameterName != null 
		        	    			&& !aParameterName.equalsIgnoreCase("PAGE") && !aParameterName.equalsIgnoreCase("NEW_SESSION") 
		        	    			&& !aParameterName.equalsIgnoreCase(ObjectsTreeConstants.OBJECT_LABEL)
		        	    			&& !aParameterName.equalsIgnoreCase(SpagoBIConstants.SUBOBJECT_NAME) 
		        	    			&& request.getParameterValues(aParameterName) != null) {
		        	    		String[] values = request.getParameterValues(aParameterName);
		        	    		for (int i = 0; i < values.length; i++) {
		        	    			%>
		        	    			<input type="hidden" name="<%= StringEscapeUtils.escapeHtml(aParameterName) %>" 
		        	    								 value="<%= StringEscapeUtils.escapeHtml(values[i]) %>" />
		        	    			<%
		        	    		}
		        	    	}
		        	    }
		        	} 
		        	%>
     	
                      <fieldset>
                          <div class="field username" style="width:250px;">
                              <label for="username"><%=msgBuilder.getMessage("username")%></label>
                              <input type="text" name="userID" id="userID" value="Username" onfocus="if(value=='<%=userDefaultValue%>') value = ''" onblur="if (this.value=='') this.value = '<%=userDefaultValue%>'"  />                              
                          </div>
                          <div class="field password" id="passwordbox"  style="width:250px;">
                              <label for="password"><%=msgBuilder.getMessage("password")%></label>
                              <!-- <input type="text" name="password" id="password" value="" onfocus="if(value=='<%=pwdDefaultValue%>') value = ''" onblur="if (this.value=='') this.value = '<%=pwdDefaultValue%>'"/> -->
                              <input type="text" name="password" id="password" value="Password" onfocus="changefield();" onblur="if (this.value=='') this.value = '<%=pwdDefaultValue%>'"/>
                          </div>
                          <div class="submit">
                              <input type="submit" value="Login" />                              
                              <p><%=msgBuilder.getMessage("noAccount")%> <a href="#" onclick="signup();"><%=msgBuilder.getMessage("signup")%></a></p>
                          </div>
                      </fieldset>
                  </form>
              </div>
          </div>
      </main>
	   
  </body>
</html>
