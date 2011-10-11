
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
<%@ include file="/WEB-INF/jsp/commons/importSbiJS.jspf"%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="it.eng.spagobi.commons.utilities.GeneralUtilities"%>
<%@page import="org.safehaus.uuid.UUIDGenerator"%>
<%@page import="org.safehaus.uuid.UUID"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="it.eng.spagobi.engines.drivers.EngineURL"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="java.util.HashMap"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.engines.config.bo.Engine"%>

<%@page import="java.net.URLEncoder"%><script type="text/javascript"
	src='<%=urlBuilder.getResourceLink(request, "/js/lib/ext-2.0.1/ux/miframe/miframe-min.js")%>'></script>

<!-- 
<script type="text/javascript" src="<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/execution/worksheet/WorksheetToolbar.js")%>"></script>
 -->

<script type="text/javascript">



<%
	UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
	UUID uuid = uuidGen.generateTimeBasedUUID();
	String requestIdentity = "request" + uuid.toString();
//    SourceBean moduleResponse = (SourceBean) aServiceResponse.getAttribute("DocumentTemplateBuildModule");
//    BIObject obj = (BIObject) moduleResponse.getAttribute("biobject");

	Object dataset_labelO = aResponseContainer.getServiceResponse().getAttribute("dataset_label");
	String dataSetLabel = null; 
	if(dataset_labelO != null){
			dataSetLabel = dataset_labelO.toString();
	}
	
	String title ="";
   	Map backUrlPars = new HashMap();
   	backUrlPars.put(SpagoBIConstants.PAGE, "DetailBIObjectPage");
   	backUrlPars.put(SpagoBIConstants.MESSAGEDET, ObjectsTreeConstants.DETAIL_SELECT);
   	//backUrlPars.put(ObjectsTreeConstants.OBJECT_ID, obj.getId().toString());
   	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
    String backUrl = urlBuilder.getUrl(request, backUrlPars);


	String context=GeneralUtilities.getSpagoBiContext();
	String param2="?"+SpagoBIConstants.SBI_CONTEXT+"="+context;
	String host=GeneralUtilities.getSpagoBiHost();
	String param3="&"+SpagoBIConstants.SBI_HOST+"="+host;
	
    SourceBean moduleResponse = (SourceBean) aServiceResponse.getAttribute("DocumentTemplateBuildModule");
	Engine engineWs = null;
	
	// only one engine for WORKSHEET type 
    List<Engine> engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType("WORKSHEET");
	for (Iterator iterator = engines.iterator(); iterator.hasNext();) {
		Engine object = (Engine) iterator.next();
		engineWs = object;
	}
	
	
	
	StringBuffer urlToCall= new StringBuffer(engineWs.getUrl());
	EngineURL engineurl = new EngineURL(urlToCall.toString(), null);
//    EngineURL engineurl = (EngineURL) moduleResponse.getAttribute(ObjectsTreeConstants.CALL_URL);
//	StringBuffer urlToCall= new StringBuffer(engineurl.getMainURL());
	//urlToCall+=param1;
	urlToCall.append(param2);
	urlToCall.append(param3);
	urlToCall.append("&"+SpagoBIConstants.SBI_LANGUAGE+"="+locale.getLanguage());
	urlToCall.append("&"+SpagoBIConstants.SBI_COUNTRY+"="+locale.getCountry());
	urlToCall.append("&NEW_SESSION=TRUE");
	//urlToCall.append("&ACTION_NAME=QBE_ENGINE_START_ACTION");
	urlToCall.append("&ACTION_NAME=WORKSHEET_WITH_DATASET_START_EDIT_ACTION");
	urlToCall.append("&SBI_EXECUTION_ROLE=/spagobi/admin");
	urlToCall.append("&user_id=biadmin");
	urlToCall.append("&dataset_label="+dataSetLabel);
	//urlToCall.append("&document=103");
	
	// get parameters, those passed can be recognize by prefix PAR_
	List atts= aResponseContainer.getServiceResponse().getContainedAttributes();
	for (Iterator iterator = atts.iterator(); iterator.hasNext();) {
		SourceBeanAttribute sba = (SourceBeanAttribute)iterator.next();
		String key = sba.getKey();
		if(key.startsWith("PAR_")){
			Object value = sba.getValue();
			String name = key.substring(4);
			
			String valueEnc = URLEncoder.encode(value.toString(), "UTF-8");
			urlToCall.append("&"+name+"="+valueEnc);			
		}
	}
	
	String encodedUrl =	urlToCall.toString(); //URLEncoder.encode(urlToCall.toString(), "UTF-8");
 
%>


var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
    	   				  request.getContextPath().substring(1):
    	   				  request.getContextPath()%>'
    	    
};

var params = {
    	SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
};

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
	    	    	if(template == null){
	    	    		return;
	    	    	}
	    	    	var templateJSON = Ext.util.JSON.decode(template);
	    			var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
	    			var documentWindowsParams = {
	    					//'OBJECT_ID': this.executionInstance.OBJECT_ID,
	    					'OBJECT_TYPE': 'WORKSHEET',
	    					//'template': wkDefinition,
	    					'OBJECT_WK_DEFINITION': wkDefinition,
	    					'MESSAGE_DET': 'DOC_SAVE_FROM_DATASET',
	    					'dataset_label': '<%=dataSetLabel%>',
	    					'typeid': 'WORKSHEET' 
	    					//,'OBJECT_DATA_SOURCE': this.executionInstance.document.datasource
	    				};
	    			//if(this.executionInstance.document.typeCode == 'DATAMART'){
	    			//	documentWindowsParams = templateJSON.OBJECT_QUERY;
	    			//}else if(this.executionInstance.document.typeCode == 'SMART_FILTER'){
	    			//	documentWindowsParams=templateJSON.OBJECT_FORM_VALUES;
	    			//	documentWindowsParams = Ext.apply(this.executionInstance, params);
	    			//}

	    			
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
		items = ['->', saveButton, backButton];
	} 

	var toolbar = new Ext.Toolbar({
		  items: items
	});

	this.templateEditIFrame = new Ext.ux.ManagedIframePanel({
		title: '<%= StringEscapeUtils.escapeJavaScript(title) %>'
		, defaultSrc: '<%= StringEscapeUtils.escapeJavaScript(GeneralUtilities.getUrl(encodedUrl, engineurl.getParameters())) %>'
		, autoLoad: true
        , loadMask: true
       , disableMessaging: true
        , tbar: toolbar
        , renderTo: Sbi.user.ismodeweb ? undefined : 'edit_template_<%=requestIdentity%>'  
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
<!-- ERROR TAG -->
<spagobi:error />