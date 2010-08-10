<!--
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
-->


<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%@page import="org.safehaus.uuid.UUIDGenerator"%>
<%@page import="org.safehaus.uuid.UUID"%>
<%@page import="it.eng.spago.base.SourceBean"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>               
<%@page import="it.eng.spagobi.engines.drivers.EngineURL"%>
<%@page import="java.util.Map" %>
<%@page import="java.util.HashMap" %>

<%
	UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
	UUID uuid = uuidGen.generateTimeBasedUUID();
	String requestIdentity = "request" + uuid.toString();
    // get module response
    SourceBean moduleResponse = (SourceBean) aServiceResponse.getAttribute("DocumentTemplateBuildModule");
	// get the BiObject from the response
    BIObject obj = (BIObject) moduleResponse.getAttribute("biobject");
	// get the url of the engine
	EngineURL engineurl = (EngineURL) moduleResponse.getAttribute(ObjectsTreeConstants.CALL_URL);
    String operation = (String) moduleResponse.getAttribute("operation");
	
	// build the string of the title
    String title = "";
	if (operation != null && operation.equalsIgnoreCase("newDocumentTemplate")) {
		title = msgBuilder.getMessage("SBIDev.docConf.templateBuild.newTemplateTitle", "messages", request);
	} else {
		title = msgBuilder.getMessage("SBIDev.docConf.templateBuild.editTemplateTitle", "messages", request);
	}
    title += " : " + obj.getName();

   	// try to get from the session the heigh of the output area
   	boolean heightSetted = false;
   	String heightArea = (String) aSessionContainer.getAttribute(SpagoBIConstants.HEIGHT_OUTPUT_AREA);
   	if (heightArea == null || heightArea.trim().equals("")) {
   		heightArea = "500";
   	} else {
   		heightSetted = true;
   	}
   	
    
	//String backEndContext=GeneralUtilities.getSpagoBiHostBackEnd();
	//String param1="?"+SpagoBIConstants.SBI_BACK_END_HOST+"="+backEndContext;
String context=GeneralUtilities.getSpagoBiContext();
String param2="?"+SpagoBIConstants.SBI_CONTEXT+"="+context;
String host=GeneralUtilities.getSpagoBiHost();
String param3="&"+SpagoBIConstants.SBI_HOST+"="+host;

	String urlToCall=engineurl.getMainURL();
	//urlToCall+=param1;
	urlToCall+=param2;
	urlToCall+=param3;	
	
   	
   	// build the back link
   	Map backUrlPars = new HashMap();
   	backUrlPars.put(SpagoBIConstants.PAGE, "DetailBIObjectPage");
   	backUrlPars.put(SpagoBIConstants.MESSAGEDET, ObjectsTreeConstants.DETAIL_SELECT);
   	backUrlPars.put(ObjectsTreeConstants.OBJECT_ID, obj.getId().toString());
   	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
    String backUrl = urlBuilder.getUrl(request, backUrlPars);

%>
<%@page import="it.eng.spagobi.commons.utilities.GeneralUtilities"%>
<table class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
    	<td class='header-title-column-portlet-section' style='vertical-align:middle;padding-left:5px;'>
           <%=title%>
       </td>
       <td class='header-empty-column-portlet-section'>&nbsp;</td>
       <td class='header-button-column-portlet-section'>
           <a href='<%= backUrl %>'>
                 <img title='<spagobi:message key = "SBIDev.docConf.templateBuild.backButton" />' 
                      class='header-button-image-portlet-section'
                      src='<%= urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme) %>' 
                      alt='<spagobi:message key = "SBIDev.docConf.templateBuild.backButton" />' />
           </a>
       </td>
   </tr>
</table>


<!-- ***************************************************************** -->
<!-- ***************************************************************** -->
<!-- **************** START BLOCK IFRAME ***************************** -->
<!-- ***************************************************************** -->
<!-- ***************************************************************** -->


<script>
		function adaptSize() {
			iframe = window.frames['iframeexec<%=requestIdentity%>'];
			navigatorname = navigator.appName;
			height = 0;
			navigatorname = navigatorname.toLowerCase();
			if(navigatorname.indexOf('explorer')) {
				height = iframe.document.body.offsetHeight;
			} else {
				height = iframe.innerHeight;
			}
			iframeEl = document.getElementById('iframeexec<%=requestIdentity%>');
			height = height + 100;
			if(height < 300){
				height = 300;
			}
			iframeEl.style.height = height + 100 + 'px';
		}
</script>

<div id="divIframe<%=requestIdentity%>" style="width:100%">
           
           <%
           		String onloadStr = " ";
           		if(!heightSetted)
           			onloadStr = " onload='adaptSize();' ";
           		String heightStr = "height:400px;";
           		if(heightSetted)
           			heightStr = "height:"+heightArea+"px;";
           %> 
             
           <iframe <%=onloadStr%> 
				   style='display:inline;<%=heightStr%>' 
				   id='iframeexec<%=requestIdentity%>' 
                   name='iframeexec<%=requestIdentity%>'  
				   src=""
                   frameborder=0  
			       width='100%' >
         	</iframe>       
                                
         	<form name="formexecution<%=requestIdentity%>" id='formexecution<%=requestIdentity%>' method="post" 
         	      action="<%=urlToCall%>" 
         	      target='iframeexec<%=requestIdentity%>'>
         	<%
         		Map mapPars = engineurl.getParameters();
         		java.util.Set keys = mapPars.keySet();
         	    Iterator iterKeys = keys.iterator();
         	    while(iterKeys.hasNext()) {
         	    	String key = iterKeys.next().toString();
         	    	String value = mapPars.get(key).toString();
         	%>
         		<input type="hidden" name="<%=key%>" value="<%=value%>" />
         	<%     	
         	    }
         	%> 
         	<center>
         	<input id="button<%=requestIdentity%>" type="submit" value="View Output"  style='display:inline;'/>
			</center>
			</form>
         
            <script>
              button = document.getElementById('button<%=requestIdentity%>');
              button.style.display='none';
              button.click();               
            </script>
                
</div>
       


<!-- ***************************************************************** -->
<!-- ***************************************************************** -->
<!-- **************** END BLOCK IFRAME ******************************* -->
<!-- ***************************************************************** -->
<!-- ***************************************************************** -->