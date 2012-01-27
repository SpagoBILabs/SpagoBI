<%--
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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

<%@page import="org.json.JSONObject"%>
<%@page import="it.eng.spagobi.analiticalmodel.execution.service.CreateDatasetForWorksheetAction"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>
<%@ include file="/WEB-INF/jsp/commons/importSbiJS.jspf"%>

<script type="text/javascript"
	src='<%=urlBuilder.getResourceLink(request, "/js/lib/ext-2.0.1/ux/miframe/miframe-min.js")%>'></script>

<%
	String executionId = (String) aResponseContainer.getServiceResponse().getAttribute(CreateDatasetForWorksheetAction.OUTPUT_PARAMETER_EXECUTION_ID);
	String worksheetEditActionUrl = (String) aResponseContainer.getServiceResponse().getAttribute(CreateDatasetForWorksheetAction.OUTPUT_PARAMETER_WORKSHEET_EDIT_SERVICE_URL);
	String datasetLabel = (String) aResponseContainer.getServiceResponse().getAttribute(CreateDatasetForWorksheetAction.OUTPUT_PARAMETER_DATASET_LABEL);
	Map<String, String> datasetParameterValuesMap = (Map<String, String>) aResponseContainer.getServiceResponse().getAttribute(CreateDatasetForWorksheetAction.OUTPUT_PARAMETER_DATASET_PARAMETERS);
	String businessMetadata = (String) aResponseContainer.getServiceResponse().getAttribute(CreateDatasetForWorksheetAction.OUTPUT_PARAMETER_BUSINESS_METADATA);
	
	String title ="";
%>

<script type="text/javascript">

	var url = {
	    	host: '<%= request.getServerName()%>'
	    	, port: '<%= request.getServerPort()%>'
	    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
	    	   				  request.getContextPath().substring(1):
	    	   				  request.getContextPath()%>'
	    	    
	};
	
	var params = {
	    SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
	    , LIGHT_NAVIGATOR_DISABLED: 'TRUE'
	};
	
	Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
		baseUrl: url
	    , baseParams: params
	});
	
	Ext.onReady(function(){
	
		var templateEditIFrame = new Sbi.worksheet.WorksheetEditorIframePanel({
			title: '<%= StringEscapeUtils.escapeJavaScript(title) %>'
			, defaultSrc: '<%= StringEscapeUtils.escapeJavaScript(worksheetEditActionUrl) %>'
			, businessMetadata : <%= businessMetadata %>
			, datasetLabel : '<%= datasetLabel %>'
			, datasetParameters : <%= new JSONObject(datasetParameterValuesMap).toString() %>
		});
	
		
		if (Sbi.user.ismodeweb) {
			var viewport = new Ext.Viewport({
				layout: 'border'
				, items: [
				    {
				       region: 'center',
				       layout: 'fit',
				       items: [templateEditIFrame]
				    }
				]
			});
		}
			
	});

</script>
 
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>