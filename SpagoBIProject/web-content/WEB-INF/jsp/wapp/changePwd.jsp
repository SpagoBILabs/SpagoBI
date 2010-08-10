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
<%@ page language="java"
 		 extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true"
          import="it.eng.spago.base.*,
                 it.eng.spagobi.commons.constants.SpagoBIConstants" 
%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.IMessageBuilder"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.IUrlBuilder"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.utilities.themes.ThemesManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>

<%      
	String userId = (request.getParameter("user_id")==null)?"":request.getParameter("user_id");
	String startUrl = request.getParameter("start_url");
	String contextName = ChannelUtilities.getSpagoBIContextName(request);
	String authFailed = (request.getAttribute(SpagoBIConstants.AUTHENTICATION_FAILED_MESSAGE) == null)?"":
						(String)request.getAttribute(SpagoBIConstants.AUTHENTICATION_FAILED_MESSAGE);
	
	ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
	RequestContainer requestContainer = RequestContainer.getRequestContainer();
	
	String currTheme=ThemesManager.getDefaultTheme();
	if (requestContainer != null){
		currTheme=ThemesManager.getCurrentTheme(requestContainer);
		if(currTheme==null)currTheme=ThemesManager.getDefaultTheme();
	
		if(responseContainer!=null) {
			SourceBean aServiceResponse = responseContainer.getServiceResponse();
			if(aServiceResponse!=null) {
				SourceBean loginModuleResponse = (SourceBean)aServiceResponse.getAttribute("LoginModule");
				if(loginModuleResponse!=null) {
					userId = (String)loginModuleResponse.getAttribute("user_id");
					startUrl = (String)loginModuleResponse.getAttribute("start_url");
          			String authFailedMessage = (String)loginModuleResponse.getAttribute(SpagoBIConstants.AUTHENTICATION_FAILED_MESSAGE);
  					if(authFailedMessage!=null) authFailed = authFailedMessage;
				}
			}
		}
	
		
	}

	//IMessageBuilder msgBuilder = MessageBuilderFactory.getMessageBuilder();

	String sbiMode = "WEB";
	IUrlBuilder urlBuilder = null;
	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
%>

<html>
  <head>
    <title>SpagoBI</title>
    <style>
      body {
	       padding: 0;
	       margin: 0;
      }
    </style> 
  </head>


	
  <body>
	<% 
	String url="";
	if(ThemesManager.resourceExists(currTheme,"/html/banner.html")){
		url = "/themes/"+currTheme+"/html/banner.html";	
	}
	else {
		url = "/themes/sbi_default/html/banner.html";	
	}
	
	%>	
  <LINK rel='StyleSheet' 
      href='<%=urlBuilder.getResourceLinkByTheme(request, "css/spagobi_shared.css",currTheme)%>' 
      type='text/css' />
      
  <jsp:include page='<%=url%>' />
	
	<form action="<%=contextName%>/ChangePwdServlet" method="POST" >
		<input type="hidden" id="MESSAGE" name="MESSAGE" value="CHANGE_PWD" />
		<input type="hidden" id="user_id" name="user_id" value="<%=userId%>" />
		<input type="hidden" id="start_url" name="start_url" value="<%=startUrl%>" />
	    
    <div style="width:100%;height:50px;border:0px solid gray;">
	    	<table >
      			<tr>
      				<td class='header-title-column-portlet-section-nogrey' width = "100px">
      				 <H1> It is necessary to change your password... </H1>
      				</td>
      			</tr>
      	</table>
	  </div>
    <div id="content" style="width:100%;">
      	<div style="width:500px;height:250px;border:0px solid gray;margin-left:50px;" >
      
      		<table >
      			<tr>
      				<td class='header-title-column-portlet-section-nogrey' width = "100px">
      				   <img src="<%=urlBuilder.getResourceLinkByTheme(request, "/img/wapp/loginUser64.png", currTheme)%>"/>
      				</td>
      				<td>
  				    <br/> 
  				    <table>		
                  <% if (("").equals(userId)) { %>        				  
        				     <tr class='header-row-portlet-section'>
        				    		<td class='header-title-column-portlet-section-nogrey' width="150px">
        						      Username : 
        							</td>
        							<td class='header-title-column-portlet-section-nogrey' width="30px">&nbsp;</td>
        							<td class='header-title-column-portlet-section-nogrey'>
        								<input name="username" type="text" size="30" />
        							</td>	
        						</tr> 
      						<% } %>
      						<tr class='header-row-portlet-section'>
      				    	<td class='header-title-column-portlet-section-nogrey' width="150px">
      								Old Password:
      							</td>
      							<td class='header-title-column-portlet-section-nogrey' width="30px">&nbsp;</td>
      							<td>
      								<input name="oldPassword" type="password" size="30" />
      							</td>	
      						</tr>
      						<tr class='header-row-portlet-section'>
      				    	<td class='header-title-column-portlet-section-nogrey' width="150px">
      								New Password:
      							</td>
      							<td class='header-title-column-portlet-section-nogrey' width="30px">&nbsp;</td>
      							<td>
      								<input name="NewPassword" type="password" size="30" />
      							</td>	
      						</tr>
      						<tr class='header-row-portlet-section'>
      				    	<td class='header-title-column-portlet-section-nogrey' width="150px">
      								Retype New Password:
      							</td>
      							<td class='header-title-column-portlet-section-nogrey' width="30px">&nbsp;</td>
      							<td>
      								<input name="NewPassword2" type="password" size="30" />
      							</td>	
      						</tr>
      					</table>	
      				</td>
      			</tr>
      			<tr>
      				<td class='header-title-column-portlet-section-nogrey'>&nbsp;</td>
      				<td class='header-title-column-portlet-section-nogrey' style='color:red;font-size:11pt;'><br/><%=authFailed%></td>
      			</tr>
      			<tr><td>&nbsp;</td></tr>
      			<tr>
      			 <td class='header-title-column-portlet-section-nogrey'>&nbsp;</td>
      			 <td align="center"> 					    		        					      
     						<input type="image" border="0" width="22px" height="22px" title='Confirm'
     							 src="<%=urlBuilder.getResourceLinkByTheme(request, "/img/button_ok.gif", currTheme)%>" 
     							alt='Confirm' />                  				
      			
    					&nbsp;
    			        	 <a href='<%=startUrl%>'>
             						<img border="0" width="22px" height="22px" title='Cancel'
             							 src="<%=urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme)%>" 
             							alt='Cancel' />
             				</a>
      				</td>
      			</tr>

      		</table>
      	</div>
      </div>
	</form>
	<spagobi:error/>
	<%
	String url2="";
  if(ThemesManager.resourceExists(currTheme,"/html/footer.html")){
  	url2 = "/themes/"+currTheme+"/html/footer.html";	
  }
  else {
  	url2 = "/themes/sbi_default/html/footer.html";	
  }
  %>
  	<jsp:include page='<%=url2%>' />
  	
  	
  </body>
  

</html>
