<%@ page language="java"
         extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" 
         import="it.eng.spago.base.*,
                 it.eng.spagobi.commons.constants.SpagoBIConstants"
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

 	
 	String sbiMode = "WEB";
 	IUrlBuilder urlBuilder = null;
 	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
   	
%>

<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>
<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/analiticalmodel/browser/standard.css",currTheme)%>'/>

<script type="text/javascript">

Ext.ns("Sbi.config");
Sbi.config.loginUrl = "";
function cancel(){
	
	Ext.MessageBox.confirm(
	  //LN('sbi.generic.pleaseConfirm'),
	  //LN('sbi.generic.confirmDelete'),
	  'Confirm',
	  'Confirm delete?',
	  function(btn, text){
		  if (btn=='yes') {
			var form = document.myForm;
			form.method = 'get';
			form.action = '${pageContext.request.contextPath}/restful-services/signup/delete';
			form.submit();
			
		  }
		}
	);
}

function changefield(el){
    document.getElementById(el+"box").innerHTML = "<input id=\""+el+"\" type=\"password\" name=\""+el+"\" />";
    document.getElementById(el).focus();
 
}
	
function modify() {

	
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
this.services["update"]= Sbi.config.serviceRegistry.getRestServiceUrl({
	serviceName: 'signup/update',
	baseParams: {}
});

    var form             = document.myForm;
	
    var nome             = document.getElementById("nome").value;
    var cognome          = document.getElementById("cognome").value;
    var username         = document.getElementById("username").value;
	var password         = document.getElementById("password").value;
	var confermaPassword = document.getElementById("confermaPassword").value;
    var email            = document.getElementById("email").value;
    var azienda          = document.getElementById("azienda").value;
	
	var params = new Object();
	params.useCaptcha = "false";
	params.nome        = nome;
	params.cognome     = cognome;
	params.username    = username;
	params.password    = password;
	params.confermaPassword = confermaPassword;
	params.email       = email;
	params.azienda     = azienda;
	
	//params.modify      = true;
	
     Ext.Ajax.request({
	url: this.services["update"],
	method: "POST",
	params: params,			
	success : function(response, options) {	
		
	    if(response != undefined  && response.responseText != undefined ) {
			if( response.responseText != null && response.responseText != undefined ){
		    var jsonData = Ext.decode( response.responseText );
		    if( jsonData.message != undefined && jsonData.message != null && jsonData.message == 'validation-error' ){
		      Sbi.exception.ExceptionHandler.handleFailure(response);
		    }else{
		      Sbi.exception.ExceptionHandler.showInfoMessage('Saved', 'Saved OK', {});
		    }		
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
</script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/src/ext/sbi/service/ServiceRegistry.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/src/ext/sbi/exception/ExceptionHandler.js'/></script>

<link id="extall"     rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />
<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/home40/standard.css",currTheme)%>'/>
<html>

  <body>
		<% 
     		//IUrlBuilder urlBuilder = null;
         /*
     		IMessageBuilder msgBuilder = MessageBuilderFactory.getMessageBuilder();
    		urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
         	String defaultOrganization = msgBuilder.getMessage("profileattr.company"); 
         	String defaultName = msgBuilder.getMessage("profileattr.firstname");
         	String defaultSurname = msgBuilder.getMessage("profileattr.lastname");
         	String defaultUsername = msgBuilder.getMessage("username");
         	String defaultPassword = msgBuilder.getMessage("password");
         	String defaultEmail = msgBuilder.getMessage("profileattr.email");
         	String defaultConfirmPwd = msgBuilder.getMessage("confirmPwd");         
         	*/
        	String defaultOrganization = "Company"; 
         	String defaultName = "Name";
         	String defaultSurname = "Surname";
         	String defaultUsername = "Username";
         	String defaultPassword = "Password";
         	String defaultEmail = "Email";
         	String defaultConfirmPwd = "Confirm Password";         	
         %>
    <main class="main main-maps-list main-list" id="main">
        	<div class="aux">
            	<div class="reserved-area-container">
            		<h1>Modify account</h1>
                    <form name="myForm" method="post"  class="reserved-area-form">
                        <fieldset>
                            <div class="field organization">
                                <label for="organization">Company</label>
                                <input type="text" name="azienda" id="azienda" value="${data['company']}" onfocus="if(value=='<%=defaultOrganization%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultOrganization%>'"/>
                            </div>
                            <div class="field name">
                                <label for="name">Name</label>
                                <input type="text" name="nome" id="nome" value="${data['name']}" onfocus="if(value=='<%=defaultName%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultName%>'"/>
                            </div>
                            <div class="field surname">
                                <label for="surname">Cognome</label>
                                <input type="text" name="cognome" id="cognome" value="${data['surname']}" onfocus="if(value=='<%=defaultSurname%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultSurname%>'"/>
                            </div>
                            <div class="field username">
                                <label for="username">Username</label>
                                <input type="text" name="username" id="username" value="${data['username']}" readonly />
                            </div>
                            <div class="field email">
                                <label for="email">Email</label>
                                <input type="text" name="email" id="email" value="${data['email']}" onfocus="if(value=='<%=defaultEmail%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultEmail%>'"/>
                            </div>
                            <div class="field password" id="passwordbox">
                                <label for="password">Password</label>
                                <input type="text" name="password" id="password" value="Password" onfocus="changefield('password');" onblur="if (this.value=='') this.value = '<%=defaultPassword%>'" />
                            </div>
                            <div class="field confirm" id="confermaPasswordbox">
                                <label for="confirm">Confirm Password</label>
                                <input type="text" name="confermaPassword" id="confermaPassword" value="Confirm Password" onfocus="changefield('confermaPassword');" onblur="if (this.value=='') this.value = '<%=defaultConfirmPwd%>'" />
                            </div>
                            <div class="submit">
                                <input type="text" value="Modify" onclick="javascript:modify();"/>
                                <p class="delete">Do you want delete this account? <a href="#" onclick="javascript:cancel();">Delete</a></p>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </main>
  </body>
</html>
