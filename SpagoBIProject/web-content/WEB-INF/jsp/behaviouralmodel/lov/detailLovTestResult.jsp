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

<%@ page import="javax.portlet.PortletURL,
			it.eng.spagobi.commons.constants.SpagoBIConstants,
			it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue,
			it.eng.spagobi.behaviouralmodel.lov.bo.ScriptDetail,
			it.eng.spagobi.behaviouralmodel.lov.bo.JavaClassDetail,
			it.eng.spagobi.behaviouralmodel.lov.bo.FixedListDetail,
			it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse,
			it.eng.spagobi.commons.dao.DAOFactory,
			it.eng.spago.navigation.LightNavigationManager,
			java.util.List,
			java.util.ArrayList,
			java.util.Iterator"%>
<%@page import="it.eng.spagobi.behaviouralmodel.lov.bo.LovDetailFactory"%>
<%@page import="it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail"%>
<%@page import="it.eng.spagobi.commons.utilities.GeneralUtilities"%>
<%@page import="it.eng.spagobi.behaviouralmodel.lov.handlers.LovManager"%>
<%@page import="it.eng.spagobi.commons.utilities.PortletUtilities"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<script type="text/javascript" src="<%=linkProto%>"></script>
<script type="text/javascript" src="<%=linkProtoWin%>"></script>
<script type="text/javascript" src="<%=linkProtoEff%>"></script>
<link href="<%=linkProtoDefThem%>" rel="stylesheet" type="text/css"/>
<link href="<%=linkProtoAlphaThem%>" rel="stylesheet" type="text/css"/>

<%
	SourceBean detailMR = (SourceBean) aServiceResponse.getAttribute("DetailModalitiesValueModule"); 
	SourceBean listLovMR = (SourceBean) aServiceResponse.getAttribute("ListTestLovModule"); 

	String lovProviderModified = (String)aSessionContainer.getAttribute(SpagoBIConstants.LOV_MODIFIED);
	if (lovProviderModified == null) 
		lovProviderModified = "false";
	
	String modality = null;
	if (detailMR != null) modality = (String) detailMR.getAttribute("modality");
	if (modality == null) modality = (String) aSessionContainer.getAttribute(SpagoBIConstants.MODALITY);
  	String messagedet = "";
  	if (modality.equals(SpagoBIConstants.DETAIL_INS))
		messagedet = SpagoBIConstants.DETAIL_INS;
	else messagedet = SpagoBIConstants.DETAIL_MOD;
	
  	Map saveUrlPars = new HashMap();
  	saveUrlPars.put("PAGE", "DetailModalitiesValuePage");
  	saveUrlPars.put(SpagoBIConstants.MESSAGEDET, messagedet);
  	// saveUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "2");
  	saveUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
  	saveUrlPars.put("RETURN_FROM_TEST_MSG","SAVE");
    String saveUrl = urlBuilder.getUrl(request, saveUrlPars);
  	
    Map backUrlPars = new HashMap();
    backUrlPars.put("PAGE", "DetailModalitiesValuePage");
    backUrlPars.put(SpagoBIConstants.MESSAGEDET, messagedet);
    backUrlPars.put("modality", modality);
    //backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
    backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
    backUrlPars.put("RETURN_FROM_TEST_MSG", "DO_NOT_SAVE");
    if(!lovProviderModified.trim().equals(""))
    	backUrlPars.put("lovProviderModified", lovProviderModified);
    String backUrl = urlBuilder.getUrl(request, backUrlPars);
  	
  	ModalitiesValue modVal = (ModalitiesValue) aSessionContainer.getAttribute(SpagoBIConstants.MODALITY_VALUE_OBJECT);
  	String lovProv = modVal.getLovProvider();
  	ILovDetail lovDet = LovDetailFactory.getLovFromXML(lovProv);
    String readonly = "" ;
    boolean isreadonly = true;
    if (userProfile.isAbleToExecuteAction(SpagoBIConstants.PARAMETER_MANAGEMENT)){
   	isreadonly = false;
   	readonly = "readonly";
   }
%>


 

<!--  SCRIPTS  -->

<script type="text/javascript">

<%
	// get the labels of all documents related to the lov
	List docLabels = LovManager.getLabelsOfDocumentsWhichUseLov(modVal);
	String confirmMessage = null;
	boolean askConfirm = false;
	if(docLabels.size() > 0) {
		askConfirm = true;
		String documentsStr = docLabels.toString();
		confirmMessage += msgBuilder.getMessage("SBIDev.predLov.savePreamble", "messages", request);
		confirmMessage += " ";
		confirmMessage += documentsStr;
		confirmMessage += ". ";
		confirmMessage += "\\n\\n";
		confirmMessage += msgBuilder.getMessage("SBIDev.predLov.saveConfirm", "messages", request);
	}	
	
%>

function askForConfirmIfNecessary() {
<%
	if(askConfirm && lovProviderModified.equalsIgnoreCase("true")) {
		String documentsStr = docLabels.toString();
%>
		if (confirm('<spagobi:message key = "SBIDev.predLov.savePreamble" />' + ' ' + '<%=documentsStr%>' + '. ' + '<spagobi:message key = "SBIDev.predLov.saveConfirm" />')) {
			document.getElementById('formTest').submit();
		}
<%
	} else {
%>
		document.getElementById('formTest').submit();
<%
	}
%>
}
</script>


<script type="text/javascript">

	function showStacktrace(){
		document.getElementById("stacktrace").style.display = 'inline';
		document.getElementById("showStacktraceDiv").style.display = 'none';
		document.getElementById("hideStacktraceDiv").style.display = 'inline';
	}
					
	function hideStacktrace(){
		document.getElementById("stacktrace").style.display = 'none';
		document.getElementById("showStacktraceDiv").style.display = 'inline';
		document.getElementById("hideStacktraceDiv").style.display = 'none';
	}
</script>





<!-- TITLE -->

<table class='header-table-portlet-section'>		
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
		    style='vertical-align:middle;padding-left:5px;'>
			<spagobi:message key = "SBIDev.predLov.testPageTitle" />
		</td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		<td class='header-button-column-portlet-section'>
			<a href= 'javascript:askForConfirmIfNecessary();' >
				<img class='header-button-image-portlet-section'
					src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/save.png", currTheme)%>' 
					title='<spagobi:message key = "SBIDev.predLov.saveButt" />'  
					alt='<spagobi:message key = "SBIDev.predLov.saveButt" />' 
				/>
			</a>
		</td>
		<td class='header-button-column-portlet-section'>
			<a href="<%=backUrl%>"> 
      				<img class='header-button-image-portlet-section' 
      				     title='<spagobi:message key = "SBISet.Funct.backButt" />' 
      				     src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme)%>' 
      				     alt='<spagobi:message key = "SBISet.Funct.backButt" />' />
			</a>
		</td>
	</tr>
</table>




<form id="formTest" method="post" action="<%=saveUrl%>" >

<!-- BODY -->


<div class='div_background_no_img' >


   <!-- ERROR TAG --> 
	<spagobi:error/>



<%
	String errorMessage = (String) listLovMR.getAttribute("errorMessage");	
	String stack = (String) listLovMR.getAttribute("stacktrace");
	if (errorMessage != null) {				  
%>
		<br/>
		<div style="left:10%;width:80%" class='portlet-form-field-label' >
			<spagobi:message key = "SBIDev.predLov.testExecNotCorrect" />
		</div>	
<%
		if (!errorMessage.trim().equals("")) { 
%>					  
			<br/>
		 	<div style="left:10%;width:80%" class='portlet-form-field-label' >
		 		<spagobi:message key = "SBIDev.predLov.testErrorMessage" />
		 	</div>
			<% if (errorMessage.equalsIgnoreCase("Invalid_XML_Output"))  { %>
				<div style="left:10%;width:70%" class='portlet-section-alternate'>
					<spagobi:message key = "SBIDev.predLov.testScriptInvalidXMLOutput" />
				</div>
			<% } else { %>
				<div style="left:10%;width:70%" class='portlet-section-alternate'>
					<%= errorMessage %>
				</div>
			<% } %>
			<br/>
<% 
		}

		if (stack != null) { 
%>
			<div id='errorDescriptionJS' style='display:inline;'>
			  	<br/>
			  	<div style="left:10%;width:80%;display:inline;" class='portlet-form-field-label' id='showStacktraceDiv'>
			  		<spagobi:message key = "SBIDev.predLov.testErrorShowStacktrace1" />
			  		<a href='javascript:showStacktrace()'>
			 	 		<spagobi:message key = "SBIDev.predLov.testErrorShowStacktrace2" />
					</a>
			 		.
			 	</div>
			    <div style="left:10%;width:80%;display:none;" class='portlet-form-field-label' id='hideStacktraceDiv'>
			 		<spagobi:message key = "SBIDev.predLov.testErrorHideStacktrace1" />
			 		<a href='javascript:hideStacktrace()'>
				 		<spagobi:message key = "SBIDev.predLov.testErrorHideStacktrace2" />
			 		</a>
			 		.
			 	</div>
				<br/>	
				<div id='stacktrace' style="left:10%;width:70%;display:none;" class='portlet-section-alternate'>
					<%= stack %>
				</div>
			 </div>
<%
		}		
		String result = (String) listLovMR.getAttribute("result");
		if(result != null) { 				  
	  		result = result.replaceAll(">", "&gt;");
	  		result = result.replaceAll("<", "&lt;");
	  		result = result.replaceAll("\"", "&quot;");					  
%>			  
			<div width="100%">
				<br/>
				<div style="position:relative;left:10%;width:80%" class='portlet-form-field-label' >
					<spagobi:message key = "SBIDev.predLov.testScriptNonCorrectResult" />
				</div>
				<br/>	
				<div style="position:relative;left:10%;width:70%" class='portlet-section-alternate'>
					<%= result %>
				</div>
			</div>
			<br/>					  
<% 
		} 
	}else {
%>
		
		<div width="100%">
			<spagobi:LovColumnsSelector moduleName="ListTestLovModule" 
			                           visibleColumns="<%=GeneralUtilities.fromListToString(lovDet.getVisibleColumnNames(),\",\")%>"
			                            valueColumn="<%=lovDet.getValueColumnName()%>" 
			                            descriptionColumn="<%=lovDet.getDescriptionColumnName()%>" 
			                            invisibleColumns="<%=GeneralUtilities.fromListToString(lovDet.getInvisibleColumnNames(),\",\")%>" />
		</div>
		

<%
	}%>
	
	</div>			
</form>
	
	<% 

	if(errorMessage == null){
%>
<br/>
		<div width="100%">
			<spagobi:list moduleName="ListTestLovModule"/>
		</div>
	<%} %>
				 



