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
    // RequestContainer requestContainer = RequestContainer.getRequestContainer();
    String currTheme = (String)request.getAttribute("currTheme");
   	if (currTheme == null)
  		currTheme = ThemesManager.getDefaultTheme();

   	System.out.println("currTheme: " + currTheme);
 	
 	String sbiMode = "WEB";
 	IUrlBuilder urlBuilder = null;
 	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
   	
%>

<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>

<script type="text/javascript">
  Ext.ns("Sbi.config");
  Sbi.config.loginUrl = "";
  
  function active(accountId) {
  
  //Service Registry creation
  var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?request.getContextPath().substring(1): request.getContextPath()%>'
    	, controllerPath: null // no cotroller just servlets   
    };
  
  Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
	baseUrl: url
    , baseParams: params
  });
  
  this.services = [];

  //Adding a new service to the registry
  this.services["active"]= Sbi.config.serviceRegistry.getRestServiceUrl({
	serviceName: 'signup/active',
	baseParams: {}
  });

  var params = new Object();
  params.accountId = accountId;
	
  Ext.Ajax.request({
	   url: this.services["active"],
	   method: "POST",
	   params: params,			
	   success : function(response, options) {	
		
	    if(response != undefined  && response.responseText != undefined ) {
			if( response.responseText != null && response.responseText != undefined ){
		      var jsonData = Ext.decode( response.responseText );
		      Sbi.exception.ExceptionHandler.showInfoMessage(jsonData.message, 'Attivazione account', {});
		    }		
		}
		else {
		  Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		}
      },
	  scope: this,
	  failure: Sbi.exception.ExceptionHandler.handleFailure
    });
  }
  
  function changefield(){
      document.getElementById("passwordbox").innerHTML = "<input id=\"password\" type=\"password\" name=\"password\" title=\"Password\" />";
      document.getElementById("password").focus();
   
  }
  
</script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/src/ext/sbi/service/ServiceRegistry.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/src/ext/sbi/exception/ExceptionHandler.js'/></script>
  
 <link id="extall" rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" /> 
	
<html>
  <body onload="javascript:active('<%= request.getParameter("accountId") %>')"> 
	 <link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/home40/standard.css",currTheme)%>'/>   
	  
    <% 
   	String userDefaultValue = "Username";
   	String pwdDefaultValue = "Password";
 	%>
      <main class="loginPage main-maps-list main-list" id="main">
      	<div class="aux">
          	<div class="reserved-area-container">
          		<h1>Login</h1>
          		  <form id="formId" name="login" action="${pageContext.request.contextPath}/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE" method="POST" onsubmit="return escapeUserName()"  class="reserved-area-form login">	        		          
                    <fieldset>                    	 
                          <div class="field username">
                              <label for="username">Username</label>
                              <input type="text" name="userID" id="userID" value="Username" onfocus="if(value=='<%=userDefaultValue%>') value = ''" onblur="if (this.value=='') this.value = '<%=userDefaultValue%>'"  />                              
                          </div>
                          <div class="field password" id="passwordbox">
                              <label for="password">Password</label>
                              <input type="text" name="password" id="password" value="Password" onfocus="changefield();" onblur="if (this.value=='') this.value = '<%=pwdDefaultValue%>'"/>
                          </div>
                          <div class="submit">
                              <input type="submit" value="Login" />                                                     
                          </div>
                      </fieldset>
                      <input type="hidden" id="currTheme" name="currTheme" value="<%=currTheme%>" /> 	
                  </form>
              </div>
          </div>
      </main>
    
  </body>
</html>
