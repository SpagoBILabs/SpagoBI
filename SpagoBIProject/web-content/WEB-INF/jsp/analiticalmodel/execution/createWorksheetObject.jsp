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

<%@page import="java.net.URLEncoder"%><script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/lib/ext-2.0.1/ux/miframe/miframe-min.js")%>'></script>

<!-- 
<script type="text/javascript" src="<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/execution/worksheet/WorksheetToolbar.js")%>"></script>
 -->

<script>



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

Ext.onReady(function(){

	var saveButton = new Ext.Toolbar.Button({
				iconCls: 'icon-saveas' 
				    , scope: this
	    	    , handler : function() {window.location.href = '<%= backUrl %>';}	
	});

    var backButton = new Ext.Toolbar.Button({
    	iconCls: 'icon-back'
		, scope: this
		, handler : function() {window.location.href = '<%= backUrl %>';}
    });

var items;
if (Sbi.user.ismodeweb) {
		items = ['->', saveButton, backButton];
	} else {
		//items = ['->', backButton];
	}

var toolbar = new Ext.Toolbar({
	  items: items
});


//var toolbar = new Sbi.execution.worksheet.WorksheetToolbar();


//var templateEditIFrame = new Ext.ux.ManagedIframePanel({
//	title: 'ciao'
//	, defaultSrc: 'http://www.google.it'
//	, autoLoad: true
//    , loadMask: true
//    , disableMessaging: true
 //   , tbar: toolbar
//});

	var templateEditIFrame = new Ext.ux.ManagedIframePanel({
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
	<spagobi:error/>