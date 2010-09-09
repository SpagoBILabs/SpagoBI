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
				 org.json.JSONObject,
				 it.eng.spagobi.kpi.config.bo.Periodicity" %>
<%

	List thrTypesCd = (List) aSessionContainer.getAttribute("thrTypesList");
	List kpiChartTypesCd = (List) aSessionContainer.getAttribute("kpiChartTypesList");	
%>

<LINK rel='StyleSheet' 
      href='<%=urlBuilder.getResourceLinkByTheme(request, "css/kpi/kpi.css",currTheme)%>' 
      type='text/css' />
      
<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>

<script type="text/javascript"><!--
	<%	
	JSONArray thrTypesArray = new JSONArray();
	if(thrTypesCd != null){
		for(int i=0; i< thrTypesCd.size(); i++){
			Domain domain = (Domain)thrTypesCd.get(i);
			JSONArray temp = new JSONArray();
			//temp.put(domain.getValueId());
			temp.put(domain.getValueCd());			
			thrTypesArray.put(temp);
		}
	}	
	String thrTypes = thrTypesArray.toString();
	thrTypes = thrTypes.replaceAll("\"","'");
	
	//chart types
	JSONArray kpiChartTypesArray = new JSONArray();
	if(kpiChartTypesCd != null){
		for(int i=0; i< kpiChartTypesCd.size(); i++){
			Domain domain = (Domain)kpiChartTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueId());
			temp.put(domain.getValueCd());			
			kpiChartTypesArray.put(temp);
		}
	}	
	String chartTypes = kpiChartTypesArray.toString();
	chartTypes = chartTypes.replaceAll("\"","'");

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
	config.thrTypes = <%= thrTypes%>;
	config.kpiChartTypes = <%= chartTypes%>;
    
Ext.onReady(function(){
	Ext.QuickTips.init();
	var manageModelInstancesViewPort = new Sbi.kpi.ManageModelInstancesViewPort(config);
   	
});


--></script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>