<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@page import="it.eng.spagobi.tools.dataset.service.SelfServiceDatasetStartAction"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>
<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/tools/dataset/main.css", currTheme)%>'/>
<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/tools/dataset/listview.css", currTheme)%>'/>

<%
String isMyData =((String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.IS_FROM_MYDATA)!=null)?(String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.IS_FROM_MYDATA):"FALSE";
if (isMyData.equalsIgnoreCase("FALSE")) {%>
	<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/tools/dataset/catalogue-item-small.css",currTheme)%>'/>
<%}else{%>
	<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/tools/dataset/catalogue-item-big.css",currTheme)%>'/>	
<%} %>
<!--  <link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/home40/standard.css",currTheme)%>'/>-->
<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/analiticalmodel/browser/standard.css",currTheme)%>'/>

<%
	String worksheetEditActionUrl = (String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.OUTPUT_PARAMETER_WORKSHEET_EDIT_SERVICE_URL);
    String qbeEditFromDataSetActionUrl = (String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.OUTPUT_PARAMETER_QBE_EDIT_FROM_DATA_SET_SERVICE_URL);
	String datasetLabel = (String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.DATASET_LABEL);
    String typeDoc = (String) aResponseContainer.getServiceResponse().getAttribute(SelfServiceDatasetStartAction.TYPE_DOC);
%>

<script type="text/javascript">

    Ext.onReady(function(){
    	
    	var documentexecution = Ext.create('Sbi.selfservice.SelfServiceExecutionIFrame',{});
    	
		var datasetListViewport = Ext.create('Ext.container.Viewport', {
			layout:'fit',
	     	items: [documentexecution]	     	
	    });

		var datasetLabel = '<%=datasetLabel%>';
		var type = '<%=typeDoc%>';
		
		var url = null;
		if (type == 'QBE') {
			url = '<%= StringEscapeUtils.escapeJavaScript(qbeEditFromDataSetActionUrl) %>';
		} else if (type == 'WORKSHEET') {
			url = '<%= StringEscapeUtils.escapeJavaScript(worksheetEditActionUrl) %>';
		}
		
		if (url === null){
			alert('Impossible to execute document of type [' + docType + ']');
		}
		else {
			url += '&dataset_label=' + datasetLabel;
			
			documentexecution.load(url);
			documentexecution.datasetLabel = datasetLabel;
		}
    });
	
</script>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>