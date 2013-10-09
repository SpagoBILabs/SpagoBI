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

<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ext-all-debug.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/examples/ux/IFrame.js'/></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/js/lib/ext-4.1.1a/ux/RowExpander.js'/></script>

<script type="text/javascript">

Ext.ns("Sbi.config");
Sbi.config.loginUrl = "";
function cancel(){
	
	Ext.MessageBox.confirm(
	  //LN('sbi.generic.pleaseConfirm'),
	  //LN('sbi.generic.confirmDelete'),
	  'Conferma cancellazione',
	  'Vuoi cancellare',
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
	var password         = document.getElementById("password").value;
    var email            = document.getElementById("email").value;
	var dataNascita      = document.getElementById("dataNascita").value;
	var indirizzo        = document.getElementById("indirizzo").value;
	var azienda          = document.getElementById("azienda").value;
	var biografia        = document.getElementById("biografia").value;
	var lingua           = document.getElementById("lingua").value;
	
	
	var params = new Object();
	
	params.nome        = nome;
	params.cognome     = cognome;
	params.password    = password;
	params.email       = email;
	params.dataNascita = dataNascita;
	params.indirizzo   = indirizzo;
	params.azienda     = azienda;
	params.biografia   = biografia;
	params.lingua      = lingua;
	params.modify      = true;
	
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

<%
    String sbiMode = "WEB";
	IUrlBuilder urlBuilder = null;
	urlBuilder = UrlBuilderFactory.getUrlBuilder(sbiMode);
%>

<link id="extall"     rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="${pageContext.request.contextPath}/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />

<html>
  <head>
  <style media="screen" type="text/css">

	
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
  
  <link rel="shortcut icon" href="<%=urlBuilder.getResourceLink(request, "img/favicon.ico")%>" />
  <title>SpagoBI signup</title>
  <LINK rel='StyleSheet' 
    href='${pageContext.request.contextPath}/themes/sbi_default/css/spagobi_shared.css' 
    type='text/css' />
  
  <style>
      body {
	       padding: 0;
	       margin: 0;
      }
  </style> 
  </head>

  <body>

    <form name="myForm" method="post">
        <div id="content" style="height:100%">
		        	<div style="padding: 80px " >
		        	<!--
		        	DO NOT DELETE THIS COMMENT
		        	If you change the tag table with this one  you can have the border of the box with the shadow via css
		        	the problem is that it doesn't work with ie	
		     		
		     		<table style="background: none repeat scroll 0 0 #fff; border-radius: 10px 10px 10px 10px;  box-shadow: 0 0 10px #888; color: #009DC3; display: block; font-size: 14px; line-height: 18px; padding: 20px;">
		        	 -->

				<table border="0" align="center" style="border-collapse:separate; background: none repeat scroll 0 0; border-radius: 5px 5px 5px 5px;  box-shadow: 0px 0px 10px #888;  -webkit-box-shadow:  0px 0px 10px #888;  -moz-box-shadow:  0px 0px 10px #888; color: #009DC3; display: block; font-size: 14px; line-height: 18px; padding: 20px;">
					<tr>
						<td></td>
						<td><img
							src='${pageContext.request.contextPath}/themes/sbi_default/img/wapp/spagobi40logo.png'
							width='180px' height='51px' style="margin: 20px 0px"/>
						</td>
						<td width='50px'></td>
						<td></td>
					</tr>
					<tr valign="top">
						<td width="120px">&nbsp;</td>
						<td width="350px">

							<table border="0">
							    <tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">*&nbsp;Name:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
                                <tr>
									<td><input id="nome" name="nome" type="text"
										size="25" class="login" value="${data['name']}"></td>
									<td></td>

								</tr>
							
							    <tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">New Password:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
                                <tr>
									<td><input id="password" name="password" type="password"
										size="25" class="login"></td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">*&nbsp;Email:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
                                <tr>
									<td><input id="email" name="email" type="text"
										size="25" class="login" value="${data['email']}"></td>
									<td></td>

								</tr>
                                <tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">Birthday (dd/mm/yyyy):
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td><input id="dataNascita" name="dataNascita" type="text" size="25"
										class="login" value="${data['birth_date']}"/>
									</td>
									<td></td>

								</tr>
								
								<tr>
									<td colspan="2" height="30px">&nbsp;</td>
								</tr>
								
								<tr>
									<td colspan="2" height="30px">&nbsp;</td>
								</tr>

						   </table>
						</td>
						<td width='50px'></td>
						<td width="350px">
						  <table border="0">
						    
						        <tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">*&nbsp;Surname:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
                                <tr>
									<td><input id="cognome" name="cognome" type="text"
										size="25" class="login" value="${data['surname']}"></td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">Location:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td><input id="indirizzo" name="indirizzo" type="text" size="25"
										class="login" value="${data['location']}"/>
									</td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">Company:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td><input id="azienda" name="azienda" type="text" size="25"
										class="login" value="${data['company']}"/>
									</td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">Short biography:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td>
									<textarea class="login" rows="5" cols="35" name="biografia" id="biografia" >${data['short_bio']}</textarea>
									</td>
									<td></td>

								</tr>
								<tr class='header-row-portlet-section'>
									<td class='login-label' width="90px" align="left">Language:
									</td>
									<td width="25px">&nbsp;</td>

								</tr>
								<tr>
									<td>
									  <select class="login" name="lingua" id="lingua">
									    <c:choose>
									      <c:when test="${data['language'] == ''}">
									        <option value="" selected="selected"></option>
									      </c:when>
									      <c:otherwise>
									        <option value=""></option>
									      </c:otherwise>
									    </c:choose>
									    <c:choose>
									      <c:when test="${data['language'] == 'it_IT'}">
									        <option value="it_IT" selected="selected">Italian</option>
									      </c:when>
									      <c:otherwise>
									        <option value="it_IT">Italian</option>
									      </c:otherwise>
									    </c:choose>
									    <c:choose>
									      <c:when test="${data['language'] == 'en_US'}">
									        <option value="en_US" selected="selected">English</option>
									      </c:when>
									      <c:otherwise>
									        <option value="en_US">English</option>
									      </c:otherwise>
									    </c:choose>
									    <c:choose>
									      <c:when test="${data['language'] == 'fr_FR'}">
									        <option value="fr_FR" selected="selected">French</option>
									      </c:when>
									      <c:otherwise>
									        <option value="fr_FR">French</option>
									      </c:otherwise>
									    </c:choose>
									    <c:choose>
									      <c:when test="${data['language'] == 'es_ES'}">
									        <option value="es_ES" selected="selected">Spanish</option>
									      </c:when>
									      <c:otherwise>
									        <option value="es_ES">Spanish</option>
									      </c:otherwise>
									    </c:choose>
									    
									 </select>
									</td>
									<td></td>
								</tr>
														  
						  </table>
						</td>
					</tr>
					<tr>
					  <td colspan="4" align="center">
									  
					    <a href="#" onclick="javascript:modify();">
						  <img src='${pageContext.request.contextPath}/themes/geobi/img/wapp/confirm_button.png' title="aggiorna" alt="aggiorna"/>
						</a>
						<a href="#" onclick="javascript:cancel();" >
						  <img src='${pageContext.request.contextPath}/themes/geobi/img/wapp/cancel_button.png' title="elimina" alt="elimina"/>
						</a>			  	
					  </td>
					  
					</tr>
					

				</table>
			</div>
	        </div>
        </form>

   
  </body>
</html>
