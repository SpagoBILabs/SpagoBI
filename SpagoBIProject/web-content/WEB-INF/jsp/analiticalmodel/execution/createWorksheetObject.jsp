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

<%@page import="it.eng.spagobi.tools.datasource.bo.DataSource"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>
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
<%@page import="java.net.URLEncoder"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>
<%@ include file="/WEB-INF/jsp/commons/importSbiJS.jspf"%>

<script type="text/javascript"
	src='<%=urlBuilder.getResourceLink(request, "/js/lib/ext-2.0.1/ux/miframe/miframe-min.js")%>'></script>

<%
	UUIDGenerator uuidGen  = UUIDGenerator.getInstance();
	UUID uuidObj = uuidGen.generateTimeBasedUUID();
	String executionId = uuidObj.toString();
	executionId = executionId.replaceAll("-", "");
	String dataSetLabel = (String) aResponseContainer.getServiceResponse().getAttribute("dataset_label");
	
	// only one engine for WORKSHEET type 
	Engine engineWs = null;
    List<Engine> engines = DAOFactory.getEngineDAO().loadAllEnginesForBIObjectType("WORKSHEET");
	if (engines == null || engines.size() == 0) {
		throw new RuntimeException("No engines for WORKSHEET documents found");
	} else {
		engineWs = (Engine) engines.get(0);
	}
	
	HashMap<String, String> parameters = new HashMap<String, String>();
	parameters.put(SpagoBIConstants.SBI_CONTEXT, GeneralUtilities.getSpagoBiContext());
	parameters.put(SpagoBIConstants.SBI_HOST, GeneralUtilities.getSpagoBiHost());
	parameters.put(SpagoBIConstants.SBI_LANGUAGE, locale.getLanguage());
	parameters.put(SpagoBIConstants.SBI_COUNTRY, locale.getCountry());
	parameters.put("NEW_SESSION", "TRUE");
	parameters.put("ACTION_NAME", "WORKSHEET_WITH_DATASET_START_EDIT_ACTION");
	parameters.put(SsoServiceInterface.USER_ID, userId);
	parameters.put("dataset_label" , dataSetLabel);
	Integer datasourceId = engineWs.getDataSourceId();
	if (datasourceId == null) {
		throw new RuntimeException("Worksheet engine [" + engineWs.getLabel() + "] has no datasource.");
	}
	DataSource dataSource = DAOFactory.getDataSourceDAO().loadDataSourceByID(datasourceId);
	parameters.put("datasource_label" , dataSource.getLabel());
	
	// get parameters, those passed can be recognize by prefix PAR_
	List atts= aResponseContainer.getServiceResponse().getContainedAttributes();
	for (Iterator iterator = atts.iterator(); iterator.hasNext();) {
		SourceBeanAttribute sba = (SourceBeanAttribute)iterator.next();
		String key = sba.getKey();
		if(key.startsWith("PAR_")){
			Object value = sba.getValue();
			String name = key.substring(4);
			String valueEnc = URLEncoder.encode(value.toString(), "UTF-8");
			parameters.put(name, valueEnc);			
		}
	}

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
	    					'MESSAGE_DET': 'DOC_SAVE_FROM_DATASET',
	    					'dataset_label': '<%=dataSetLabel%>',
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
		, defaultSrc: '<%= StringEscapeUtils.escapeJavaScript(GeneralUtilities.getUrl(engineWs.getUrl(), parameters)) %>'
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