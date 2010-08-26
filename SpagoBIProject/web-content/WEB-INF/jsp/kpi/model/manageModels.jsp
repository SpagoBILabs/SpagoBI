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

<%@ include file="/WEB-INF/jsp/commons/portlet_base311.jsp"%>
<%@ page import="it.eng.spagobi.commons.bo.Domain,
				 java.util.ArrayList,
				 java.util.List,
				 org.json.JSONArray, 
				 org.json.JSONObject" %>
<%

	List nodeTypesCd = (List) aSessionContainer.getAttribute("nodeTypesList");
    List thrSeverityTypesCd = (List) aSessionContainer.getAttribute("thrSeverityTypes");
	List thrTypesCd = (List) aSessionContainer.getAttribute("thrTypesList");
	List kpiTypesCd = (List) aSessionContainer.getAttribute("kpiTypesList");
	List measureTypesCd = (List) aSessionContainer.getAttribute("measureTypesList");
	List metricScaleTypesCd = (List) aSessionContainer.getAttribute("metricScaleTypesList");

%>

<LINK rel='StyleSheet' 
      href='<%=urlBuilder.getResourceLinkByTheme(request, "css/kpi/kpi.css",currTheme)%>' 
      type='text/css' />
      
<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>

<script type="text/javascript">
	<%	
	JSONArray thrTypesArray = new JSONArray();
	if(thrTypesCd != null){
		for(int i=0; i< thrTypesCd.size(); i++){
			Domain domain = (Domain)thrTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			thrTypesArray.put(temp);
		}
	}	
	String thrTypes = thrTypesArray.toString();
	thrTypes = thrTypes.replaceAll("\"","'");
	
	JSONArray severityTypesArray = new JSONArray();
	if(thrSeverityTypesCd != null){
		for(int i=0; i< thrSeverityTypesCd.size(); i++){
			Domain domain = (Domain)thrSeverityTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			severityTypesArray.put(temp);
		}
	}	
	String severityTypes = severityTypesArray.toString();
	severityTypes = severityTypes.replaceAll("\"","'");	
	
	JSONArray kpiTypesArray = new JSONArray();
	if(kpiTypesCd != null){
		for(int i=0; i< kpiTypesCd.size(); i++){
			Domain domain = (Domain)kpiTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			kpiTypesArray.put(temp);
		}
	}	
	String kpiTypes = kpiTypesArray.toString();
	kpiTypes = kpiTypes.replaceAll("\"","'");
	
	JSONArray measureTypesArray = new JSONArray();
	if(measureTypesCd != null){
		for(int i=0; i< measureTypesCd.size(); i++){
			Domain domain = (Domain)measureTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			measureTypesArray.put(temp);
		}
	}	
	String measureTypes = measureTypesArray.toString();
	measureTypes = measureTypes.replaceAll("\"","'");
	
	JSONArray metricScaleTypesArray = new JSONArray();
	if(metricScaleTypesCd != null){
		for(int i=0; i< metricScaleTypesCd.size(); i++){
			Domain domain = (Domain)metricScaleTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			metricScaleTypesArray.put(temp);
		}
	}	
	String metricScalesTypes = metricScaleTypesArray.toString();
	metricScalesTypes = metricScalesTypes.replaceAll("\"","'");	
	
	
	JSONArray nodeTypesArray = new JSONArray();
	if(nodeTypesCd != null){
		
		for(int i=0; i< nodeTypesCd.size(); i++){
			Domain domain = (Domain)nodeTypesCd.get(i);	
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueId());
			temp.put(domain.getValueCd());
			temp.put(domain.getValueDescription());
			temp.put(domain.getDomainCode());
			nodeTypesArray.put(temp);
		}
	}	
	String nodeTypes = nodeTypesArray.toString();
	nodeTypes = nodeTypes.replaceAll("\"","'");
	
	

	%>

	var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
    	   				  request.getContextPath().substring(1):
    	   				  request.getContextPath()%>'
    	    
    };

    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
    });
    
    var config = {};
	config.kpiTypesCd = <%= kpiTypes%>;
	config.thrSeverityTypesCd = <%= severityTypes%>;
	config.measureTypesCd = <%= measureTypes%>;
	config.metricScaleTypesCd = <%= metricScalesTypes%>;
	config.thrTypes = <%= thrTypes%>;
    config.nodeTypesCd = <%= nodeTypes%>;
    
Ext.onReady(function(){
	Ext.QuickTips.init();
	var manageModelsViewPort = new Sbi.kpi.ManageModelsViewPort(config);
   	
});


</script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>