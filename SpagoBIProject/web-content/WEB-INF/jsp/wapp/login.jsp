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
	ResponseContainer aResponseContainer = ResponseContainerAccess.getResponseContainer(request);
	//RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request); 
	RequestContainer requestContainer = RequestContainer.getRequestContainer();
	//SessionContainer sessionContainer = requestContainer.getSessionContainer();
	
	SingletonConfig serverConfig = SingletonConfig.getInstance();
	String strInternalSecurity = serverConfig.getConfigValue("SPAGOBI.SECURITY.PORTAL-SECURITY-CLASS.className");
	boolean isInternalSecurity = (strInternalSecurity.indexOf("InternalSecurity")>0)?true:false;
	String roleToCheckLbl  =  SingletonConfig.getInstance().getConfigValue("SPAGOBI.SECURITY.ROLE_LOGIN");
	String roleToCheckVal = "";
	if (!("").equals(roleToCheckLbl)){
		roleToCheckVal = (request.getParameter(roleToCheckLbl)!=null)?request.getParameter(roleToCheckLbl):"";	
		if (("").equals(roleToCheckVal)){
//			roleToCheckVal = ( sessionContainer.getAttribute(roleToCheckLbl)!=null)?
//						(String)sessionContainer.getAttribute(roleToCheckLbl):"";
			roleToCheckVal = ( session.getAttribute(roleToCheckLbl)!=null)?
					(String)session.getAttribute(roleToCheckLbl):"";
		}
	}
	
	
	
	String currTheme=ThemesManager.getCurrentTheme(requestContainer);
	if(currTheme==null)currTheme=ThemesManager.getDefaultTheme();
	 
	if(aResponseContainer!=null) {
		SourceBean aServiceResponse = aResponseContainer.getServiceResponse();
		if(aServiceResponse!=null) {
			SourceBean loginModuleResponse = (SourceBean)aServiceResponse.getAttribute("LoginModule");
			if(loginModuleResponse!=null) {
				String authFailedMessage = (String)loginModuleResponse.getAttribute(SpagoBIConstants.AUTHENTICATION_FAILED_MESSAGE);
				startUrl = (loginModuleResponse.getAttribute("start_url")==null)?"":(String)loginModuleResponse.getAttribute("start_url");
				if(authFailedMessage!=null) {
					authFailed = authFailedMessage;
				}				
			}
		}
	}

	
	IMessageBuilder msgBuilder = MessageBuilderFactory.getMessageBuilder();
	
	String sbiMode = "WEB";
	IUrlBuilder urlBuilder = null;
	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
%>






<%@page import="it.eng.spagobi.commons.SingletonConfig"%><html>
  <head>
  <style media="screen" type="text/css">

	input.login    {
	display:block;
	border: 1px solid #a9a9a9; 
	color: #908989;
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
	function escapeUserName(){
	
	userName = document.login.userID.value;
	
		if (userName.indexOf("<")>-1 || userName.indexOf(">")>-1 || userName.indexOf("'")>-1 || userName.indexOf("\"")>-1 || userName.indexOf("%")>-1)
			{
			alert('Invalid username');
			return false;
			}
		else
			{return true;}
	}
	
	function setUser(userV, pswV){
		var password = document.getElementById('password');
		var user = document.getElementById('userID');
		password.value = pswV;
		user.value = userV;
	//	document.forms[0].submit();
	}
	</script>
	<link rel="shortcut icon" href="<%=urlBuilder.getResourceLink(request, "img/favicon.ico")%>" />
    <title>SpagoBI</title>
    <style>
      body {
	       padding: 0;
	       margin: 0;
      }
    </style> 
  </head>

  <body>

	
	<LINK rel='StyleSheet' 
    href='<%=urlBuilder.getResourceLinkByTheme(request, "css/spagobi_shared.css",currTheme)%>' 
    type='text/css' />


	
        <form name="login" action="<%=contextName%>/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE" method="POST" onsubmit="return escapeUserName()">
        	<input type="hidden" id="isInternalSecurity" name="isInternalSecurity" value="<%=isInternalSecurity %>" />        	
        	<input type="hidden" id="<%=roleToCheckLbl%>" name="<%=roleToCheckLbl%>" value="<%=roleToCheckVal%>" />
        	<%
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
        	
	        <div id="content" style="width:100%;height:100%">
		        	<div style="float:left;!important;width:570px;height:310px;margin-top:80px;margin-left:50px; " >
		        	<!--
		        	DO NOT DELETE THIS COMMENT
		        	If you change the tag table with this one  you can have the border of the box with the shadow via css
		        	the problem is that it doesn't work with ie	
		     		
		     		<table style="background: none repeat scroll 0 0 #fff; border-radius: 10px 10px 10px 10px;  box-shadow: 0 0 10px #888; color: #009DC3; display: block; font-size: 14px; line-height: 18px; padding: 20px;">
		        	 -->

				<table border=0>
					<tr>
						<td></td>
						<td><img src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/wapp/spagobi40logo.png", currTheme)%>' width='150px' height='51px'/></td>
						<td></td>
					</tr>
					<tr>
						<td width="120px">&nbsp;</td>
						<td width="350px"> 
						
							<table border=0>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left"><%=msgBuilder.getMessage("username")%>:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td><input id="userID" name="userID" type="text" size="25"
										class="login"></td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left"><%=msgBuilder.getMessage("password")%>:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<% if (isInternalSecurity) {%>
								<tr>
									<td><input id="password" name="password" type="password"
										size="25" class="login">
									</td>
									<td></td>

								</tr>
								<tr>
									<td colspan=3 height="30px">&nbsp;</td>
								</tr>
								<tr>
									<td><input type="image" align="right"
										src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/wapp/login40.png", currTheme)%>'
										title='<%=msgBuilder.getMessage("login")%>'
										alt='<%=msgBuilder.getMessage("login")%>'></td>
									<td></td>

								</tr>
								<tr>
									<td colspan=3 height="30px">&nbsp;</td>
								</tr>
								
								<tr>
									<td class='login-label'><a
										href="<%=contextName %>/ChangePwdServlet?start_url=<%=startUrl%>">
											<%=msgBuilder.getMessage("changePwd")%> </a></td>
									<td>&nbsp;</td>
									<td class='login-label' width="150px"></td>
								</tr>
								<% } %>
							</table></td>
						<td style="padding-top: 20px">&nbsp;&nbsp;</td>
					</tr>
							<tr>
								<td>&nbsp;</td>
								<td class='header-title-column-portlet-section-nogrey'>
									<div class="header-row-portlet-section" style = "line-height: 130%; margin-top: 10px; font-size:9pt;">														
										SpagoBI Demo users' credentials:<br/>
										&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onclick="setUser('biuser','biuser')"><b>biuser/biuser</b></a>(business user)<br/>
										&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onclick="setUser('bidemo','bidemo')"><b>bidemo/bidemo</b></a> (showcase user)<br/>
										&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onclick="setUser('biadmin','biadmin')"><b>biadmin/biadmin</b></a> (administrator)
									</div>
								</td>
							</tr>
					<tr>
						<td>&nbsp;</td>
						<td style='color: red; font-size: 11pt;'><br /><%=authFailed%></td>
						<td>&nbsp;</td>
					</tr>

				</table>
			</div>
					<div style="float:right;background-image:url('/SpagoBI/themes/sbi_default/img/wapp/sfodno_login.png');width:330px;height:310px;margin-top:110px;margin-right:20px;"></div>
	        </div>
        </form>
        <spagobi:error/>

   
  </body>
</html>
