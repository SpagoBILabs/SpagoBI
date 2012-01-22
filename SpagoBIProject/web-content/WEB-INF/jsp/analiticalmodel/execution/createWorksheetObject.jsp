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

var params = {};

Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
	baseUrl: url
    , baseParams: params
});

Ext.onReady(function(){

	var saveButton = new Ext.Toolbar.Button({
				iconCls: 'icon-saveas' 
				, scope: this
	    	    , handler : function() {
	    	    	var thePanel = this.templateEditIFrame.getFrame().getWindow().workSheetPanel;
	    	    	var template = thePanel.validate();	
	    	    	if (template == null){
	    	    		return;
	    	    	}
	    	    	var templateJSON = Ext.util.JSON.decode(template);
	    			var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
	    			var documentWindowsParams = {
	    					'OBJECT_TYPE': 'WORKSHEET',
	    					//'template': wkDefinition,
	    					'OBJECT_WK_DEFINITION': wkDefinition,
	    					'business_metadata': <%= businessMetadata %>,
	    					'MESSAGE_DET': 'DOC_SAVE_FROM_DATASET',
	    					'dataset_label': '<%=datasetLabel%>',
	    					'typeid': 'WORKSHEET' 
	    				};
	    			this.win_saveDoc = new Sbi.execution.SaveDocumentWindow(documentWindowsParams);
	    			this.win_saveDoc.show();
	    	    
	    	    }	
	});

    var backButton = new Ext.Toolbar.Button({
    	iconCls: 'icon-back'
		, scope: this
		, handler : function() {this.fireEvent('backToAdmin');}
    });

	var items;
	if (Sbi.user.ismodeweb) {
		//items = ['->', saveButton, backButton];
		items = ['->', saveButton];
	} 

	var toolbar = new Ext.Toolbar({
		  items: items
	});

	this.templateEditIFrame = new Ext.ux.ManagedIframePanel({
		title: '<%= StringEscapeUtils.escapeJavaScript(title) %>'
		, defaultSrc: '<%= StringEscapeUtils.escapeJavaScript(worksheetEditActionUrl) %>'
		, autoLoad: true
        , loadMask: true
        , disableMessaging: true
        , tbar: toolbar
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