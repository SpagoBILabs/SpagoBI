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

<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.monitoring.dao.AuditManager"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>
<%@page import="java.net.URLEncoder"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<% 
	SourceBean sbModuleResponse = (SourceBean) aServiceResponse.getAttribute("ExecuteBIObjectModule");
	Boolean isImage = (sbModuleResponse.getAttribute("isImage") != null )? 
			(Boolean)sbModuleResponse.getAttribute("isImage"):false; 
	ExecutionInstance instanceO = contextManager.getExecutionInstance(ExecutionInstance.class.getName());
	String execContext = instanceO.getExecutionModality();

	Integer executionAuditId_office = null;
	String spagobiContext = request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
				 			 	request.getContextPath().substring(1):request.getContextPath();
	// identity string for object of the page
	String strUuid = instanceO.getExecutionId();
	BIObject biObj = instanceO.getBIObject();
	
	AuditManager auditManager = AuditManager.getInstance();
	String modality = instanceO.getExecutionModality();
	String executionRole = instanceO.getExecutionRole();
	executionAuditId_office = auditManager.insertAudit(biObj, null, userProfile, executionRole, modality);
	
	//if (!isImage){
		//get the url for document retrieval
		String officeDocUrl = GeneralUtilities.getSpagoBIProfileBaseUrl(userUniqueIdentifier);
		officeDocUrl += "&ACTION_NAME=GET_OFFICE_DOC&documentId=" + biObj.getId().toString() + "&" + LightNavigationManager.LIGHT_NAVIGATOR_DISABLED + "=TRUE";
		// adding parameters for AUDIT updating
		if (executionAuditId_office != null) {
			officeDocUrl += "&" + AuditManager.AUDIT_ID + "=" + executionAuditId_office.toString();
		}
	if (!isImage){
		response.sendRedirect(officeDocUrl);
	}else{
		
	String src = "/" + spagobiContext + "/swf/keenerview.swf?image_url=" + URLEncoder.encode(officeDocUrl);
// HTML CODE FOR THE FLASH COMPONENT %>
		 <div align="center" style="width:100%; height:98%; overflow:auto;"> 
		   <object  	classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"  
		   				style="width:100%; height:98%;"
		                codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0"
		                type="application/x-shockwave-flash"
		                width="100%" 
                		height="98%" >
		       	<param name="src" value="<%=src%>" />
		       	<param name="wmode" value="transparent">
		        <EMBED  src="<%=src%>"  	                 
		                wmode="transparent" 
		                width="100%" 
                		height="98%" 
		   			    TYPE="application/x-shockwave-flash">
		   		</EMBED>
			</object>    
		</div> 
<%} %>
