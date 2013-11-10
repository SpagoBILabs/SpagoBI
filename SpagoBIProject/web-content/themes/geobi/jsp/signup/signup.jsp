<%@ page language="java"
         extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" 
         import="it.eng.spago.base.*,
                 it.eng.spagobi.commons.constants.SpagoBIConstants,
         		 it.eng.spagobi.commons.utilities.messages.IMessageBuilder"
%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.MessageBuilder"%>
<%@page import="it.eng.spagobi.commons.utilities.messages.MessageBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.IUrlBuilder"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spago.base.SourceBean"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.utilities.themes.ThemesManager"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Enumeration"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>  

<% 
	String defaultOrganization = msgBuilder.getMessage("profileattr.company",locale); 
	String defaultName = msgBuilder.getMessage("profileattr.firstname",locale);
	String defaultSurname = msgBuilder.getMessage("profileattr.lastname",locale);
	String defaultUsername = "Username";
	String defaultPassword = "Password";
	String defaultEmail = msgBuilder.getMessage("profileattr.email",locale);
	String defaultConfirmPwd = msgBuilder.getMessage("confirmPwd",locale);  
	
	String registrationSuccessMsg = msgBuilder.getMessage("signup.msg.success",locale);

%> 

<script type="text/javascript">

Ext.ns("Sbi.config");
Sbi.config.loginUrl = "";


function nascondi(){
  var optDiv = document.getElementById("optional");
  var a = document.getElementById("nascondi");
  if (optDiv.style.display == 'none') {
    optDiv.style.display = '';
    a.innerHTML = 'hide optional field';
  }
  else {
    optDiv.style.display = 'none';
    a.innerHTML = 'display optional field';
  }
}

function register() {
	//callLoadingDiv();
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
	this.services["create"]= Sbi.config.serviceRegistry.getRestServiceUrl({
		serviceName: 'signup/create',
		baseParams: {}
	});

    var form             = document.myForm;	
    var nome             = document.getElementById("nome").value;
	var cognome          = document.getElementById("cognome").value;
	var username         = document.getElementById("username").value;
	var password         = document.getElementById("password").value;
	var confermaPassword = document.getElementById("confermaPassword").value;
	var email            = document.getElementById("email").value;
	var azienda          = ( document.getElementById("azienda").value == "<%=defaultOrganization%>" )?"":document.getElementById("azienda").value;
	
	var registrationSuccessMsg = "<%=registrationSuccessMsg%>";
	
	var params = new Object();
	params.useCaptcha = "false";
	params.nome     = nome;
	params.cognome  = cognome;
	params.username = username;
	params.password = password;
	params.confermaPassword = confermaPassword;
	params.azienda     = azienda;	
	params.email    = email;

    Ext.Ajax.request({
		url: this.services["create"],
		method: "POST",
		params: params,			
		success : function(response, options) {	
		    if(response != undefined  && response.responseText != undefined ) {
				if( response.responseText != null && response.responseText != undefined ){
			    var jsonData = Ext.decode( response.responseText );
			    if( jsonData.message != undefined && jsonData.message != null && jsonData.message == 'validation-error' ){
			      Sbi.exception.ExceptionHandler.handleFailure(response);
			    }else{
			      Sbi.exception.ExceptionHandler.showInfoMessage(registrationSuccessMsg, "Success", {});
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


function changefield(el){
    document.getElementById(el+"box").innerHTML = "<input id=\""+el+"\" type=\"password\" name=\""+el+"\" />";
    document.getElementById(el).focus();
 
}

</script>

<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/home40/standard.css",currTheme)%>'/>

<html>

  <body>
 		<main class="loginPage main-maps-list main-list" id="main">
        	<div class="aux">
            	<div class="reserved-area-container">            		
            		<h1><%=msgBuilder.getMessage("registration",locale)%></h1>
                    <form name="myForm" method="post" action="${pageContext.request.contextPath}/" class="reserved-area-form">
                        <fieldset>
                            <div class="field organization">
                                <label for="organization">Company</label>
                                <input type="text" name="azienda" id="azienda" value="<%=defaultOrganization%>" onfocus="if(value=='<%=defaultOrganization%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultOrganization%>'" />
                            </div>
                            <div class="field name">
                                <label for="name">Name</label>
                                <input type="text" name="nome" id="nome" value="<%=defaultName%>" onfocus="if(value=='<%=defaultName%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultName%>'" />
                            </div>
                            <div class="field surname">
                                <label for="surname">Surname</label>
                                <input type="text" name="cognome" id="cognome" value="<%=defaultSurname%>" onfocus="if(value=='<%=defaultSurname%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultSurname%>'" />
                            </div>
                            <div class="field username">
                                <label for="username">Username</label>
                                <input type="text" name="username" id="username" value="Username" onfocus="if(value=='<%=defaultUsername%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultUsername%>'"/>
                            </div>
                            <div class="field email">
                                <label for="email">Email</label>
                                <input type="text" name="email" id="email" value="<%=defaultEmail%>" onfocus="if(value=='<%=defaultEmail%>') value = ''" onblur="if (this.value=='') this.value = '<%=defaultEmail%>'" />
                            </div>
                            <div class="field password" id="passwordbox">
                                <label for="password">Password</label>
                                <input type="text" name="password" id="password" value="Password"  onfocus="changefield('password');" onblur="if (this.value=='') this.value = '<%=defaultPassword%>'" />
                            </div>
                            <div class="field confirm" id="confermaPasswordbox">
                                <label for="confirm">Confirm Password</label>
                                <input type="text" name="confermaPassword" id="confermaPassword" value="<%=defaultConfirmPwd%>" onfocus="changefield('confermaPassword');"  onblur="if (this.value=='') this.value = '<%=defaultConfirmPwd%>'"/>
                            </div>

                            <div class="submit">
                                <input type="text" value="<%=msgBuilder.getMessage("signup",locale)%>" onclick="javascript:register();" />
                                <p><%=msgBuilder.getMessage("yesAccount",locale)%> <a href="${pageContext.request.contextPath}/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE">Login</a></p>                                
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </main>
   
  </body>
</html>
