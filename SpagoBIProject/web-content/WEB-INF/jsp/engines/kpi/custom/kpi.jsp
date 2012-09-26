<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  
 
<%@page import="it.eng.spagobi.engines.kpi.utils.KpiGUIUtil"%>
<%@page import="org.json.JSONObject, 
				 java.util.ArrayList,
				 java.util.List,
				 org.json.JSONArray,
				 it.eng.spagobi.analiticalmodel.document.handlers.*"%>
<%@ page import="java.util.Map" %>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.safehaus.uuid.UUID"%>
<%@page import="org.jfree.chart.entity.StandardEntityCollection"%>
<%@page import="it.eng.spago.error.EMFErrorHandler"%>
<%@page import="java.util.Vector"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="org.jfree.data.category.DefaultCategoryDataset"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spagobi.engines.kpi.utils.StyleLabel"%>
<%@page import="it.eng.spagobi.engines.kpi.bo.ChartImpl"%>
<%@page import="it.eng.spagobi.engines.kpi.bo.KpiResourceBlock"%>
<%@page import="it.eng.spagobi.engines.kpi.bo.KpiLine"%>
<%@page import="it.eng.spagobi.engines.kpi.bo.KpiLineVisibilityOptions"%>
<%@page import="java.util.Date"%>



<%@page import="it.eng.spagobi.analiticalmodel.document.handlers.ExecutionManager"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.handlers.BIObjectNotesManager"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.service.ExecuteBIObjectModule"%>
<%@page import="it.eng.spagobi.commons.utilities.ParameterValuesEncoder"%>
<%@page import="it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base311.jsp"%>
<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>
<LINK rel='StyleSheet' 
      href='<%=urlBuilder.getResourceLinkByTheme(request, "css/kpi/kpi.css",currTheme)%>' 
      type='text/css' />

<%	//START ADDING TITLE AND SUBTITLE

	List resources = new ArrayList();
	
	SourceBean sbModuleResponse = (SourceBean) aServiceResponse.getAttribute("ExecuteBIObjectModule");
	Integer executionAuditId_chart = null;
		EMFErrorHandler errorHandler=aResponseContainer.getErrorHandler();
	if(errorHandler.isOK()){    
		SessionContainer permSession = aSessionContainer.getPermanentContainer();
	
		if(userProfile==null){
			userProfile = (IEngUserProfile) permSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			userId=(String)((UserProfile)userProfile).getUserId();
		}
	}
	String crossNavigationUrl = "";
	ExecutionInstance instanceO = contextManager.getExecutionInstance(ExecutionInstance.class.getName());
	String execContext = instanceO.getExecutionModality();
	String title = (String)sbModuleResponse.getAttribute("title");
	String subTitle = (String)sbModuleResponse.getAttribute("subName");

	//END ADDING TITLE AND SUBTITLE

	String metadata_publisher_Name =(String)sbModuleResponse.getAttribute("metadata_publisher_Name");
	String trend_publisher_Name =(String)sbModuleResponse.getAttribute("trend_publisher_Name");
	String customChartName =(String)sbModuleResponse.getAttribute("custom_chart_name");

	List kpiRBlocks =(List)sbModuleResponse.getAttribute("kpiRBlocks");
	KpiLineVisibilityOptions options = new KpiLineVisibilityOptions();
	
	//START creating resources list
	if(!kpiRBlocks.isEmpty()){
		Iterator blocksIt = kpiRBlocks.iterator();
		while(blocksIt.hasNext()){
			KpiResourceBlock block = (KpiResourceBlock) blocksIt.next();
			if(block.getR()!=null){
				resources.add( block.getR());
			}
		}
	}
	
	ExecutionInstance instance = contextManager.getExecutionInstance(ExecutionInstance.class.getName());
	String EXECUTION_ID = instance.getExecutionId();
	/*String parsToDetailDocs = "";
	   if(instance!=null && instance.getBIObject()!=null){
	   List pars = instance.getBIObject().getBiObjectParameters();			
		if(pars!=null && !pars.isEmpty()){
			Iterator ite=pars.iterator();
			while(ite.hasNext()){
				BIObjectParameter p = (BIObjectParameter)ite.next();
				String url = p.getParameterUrlName();
				String value = p.getParameterValuesAsString();
				parsToDetailDocs += url+"="+value+"&";
			}		
		}
	}*/

	
	JSONArray kpiRowsArray = new JSONArray();
	KpiGUIUtil util = new KpiGUIUtil();
	util.setExecutionInstance(instance, locale);

	if(!kpiRBlocks.isEmpty()){		
		
		Iterator blocksIt = kpiRBlocks.iterator();

		while(blocksIt.hasNext()){			
			KpiResourceBlock block = (KpiResourceBlock) blocksIt.next();
			String resourceName = null;
			if(block.getR() != null){
				resourceName = block.getR().getName();
			}
			KpiLine root = block.getRoot();
			JSONObject modelInstJson =  util.recursiveGetJsonObject(root);
			modelInstJson.put("resourceName", resourceName);
			kpiRowsArray.put(modelInstJson);	
		}			
	}
	SessionContainer permSession = aSessionContainer.getPermanentContainer();
	String localeExtDateFormat = GeneralUtilities.getLocaleDateFormatForExtJs(permSession);
	String serverExtTimestampFormat = GeneralUtilities.getServerTimestampFormatExtJs();
	String serverDateFormat = GeneralUtilities.getServerDateFormatExtJs();
	String engineContext = request.getContextPath();
    if( engineContext.startsWith("/") || engineContext.startsWith("\\") ) {
    	engineContext = request.getContextPath().substring(1);
    }
	//determines execution instance for each detail document

%>		
<script type="text/javascript">
		var url = {
			host: 'localhost'
			, port: '8080'
			, contextPath: 'SpagoBI'
			

		};

		Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			baseUrl: url
			
		});
		
		var grid = {
			subtitle: '<%= subTitle%>',		
			autoScroll	:true,
			json: <%=kpiRowsArray%>
		};
		var accordion ={SBI_EXECUTION_ID: '<%=EXECUTION_ID%>', 
						customChartName: '<%=customChartName%>',
						localeExtDateFormat: '<%=localeExtDateFormat%>',
						serverExtTimestampFormat: '<%=serverExtTimestampFormat%>',
						serverDateFormat: '<%=serverDateFormat%>',
						chartBaseUrl: '/<%= engineContext %>/js/lib/ext-3.1.1/resources/charts.swf'
						};
		
		var config ={grid: grid, accordion: accordion};

		Ext.onReady(function(){

			var item = new Sbi.kpi.KpiGUILayout(config);

		    var viewport = new Ext.Viewport({
		        layout:'fit',
		        items:[item]
		    });

		});

</script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>

		