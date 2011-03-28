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
				 it.eng.spagobi.tools.datasource.bo.*,
				 java.util.ArrayList,
				 java.util.List,
				 org.json.JSONArray" %>
<%
    List dsTypesList = (List) aSessionContainer.getAttribute("dsTypesList");
    List catTypesCd = (List) aSessionContainer.getAttribute("catTypesList");
    List dataSourceList = (List) aSessionContainer.getAttribute("dataSourceList");
    List scriptLanguageList = (List) aSessionContainer.getAttribute("scriptLanguageList");
    List trasfTypesList = (List) aSessionContainer.getAttribute("trasfTypesList");
	
%>


<%@page import="it.eng.spagobi.tools.udp.bo.Udp"%>
<%@page import="it.eng.spagobi.chiron.serializer.UdpJSONSerializer"%>
<%@page import="org.json.JSONObject"%><script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>

<script type="text/javascript">

	<%	
	JSONArray dsTypesArray = new JSONArray();
	if(dsTypesList != null){
		for(int i=0; i< dsTypesList.size(); i++){
			Domain domain = (Domain)dsTypesList.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			dsTypesArray.put(temp);
		}
	}	
	String dsTypes = dsTypesArray.toString();
	dsTypes = dsTypes.replaceAll("\"","'");
	
	JSONArray catTypesArray = new JSONArray();
	if(catTypesCd != null){
		for(int i=0; i< catTypesCd.size(); i++){
			Domain domain = (Domain)catTypesCd.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			catTypesArray.put(temp);
		}
	}	
	String catTypes = catTypesArray.toString();
	catTypes = catTypes.replaceAll("\"","'");
	
	JSONArray dataSourcesArray = new JSONArray();
	if(dataSourceList != null){
		for(int i=0; i< dataSourceList.size(); i++){
			DataSource datasource = (DataSource)dataSourceList.get(i);
			JSONArray temp = new JSONArray();
			temp.put(datasource.getLabel());
			dataSourcesArray.put(temp);
		}
	}	
	String dataSourceLabels = dataSourcesArray.toString();
	dataSourceLabels = dataSourceLabels.replaceAll("\"","'");
	
	JSONArray scriptLanguagesArray = new JSONArray();
	if(scriptLanguageList != null){
		for(int i=0; i< scriptLanguageList.size(); i++){
			Domain domain = (Domain)scriptLanguageList.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			scriptLanguagesArray.put(temp);
		}
	}	
	String scriptTypes = scriptLanguagesArray.toString();
	scriptTypes = scriptTypes.replaceAll("\"","'");
	
	JSONArray trasfTypesArray = new JSONArray();
	if(trasfTypesList != null){
		for(int i=0; i< trasfTypesList.size(); i++){
			Domain domain = (Domain)trasfTypesList.get(i);
			JSONArray temp = new JSONArray();
			temp.put(domain.getValueCd());
			trasfTypesArray.put(temp);
		}
	}	
	String trasfTypes = trasfTypesArray.toString();
	trasfTypes = trasfTypes.replaceAll("\"","'");
    
    %>

    var config = {};  
    config.dsTypes = <%= dsTypes%>;
    config.catTypeCd = <%= catTypes%>;
    config.dataSourceLabels = <%= dataSourceLabels%>;
    config.scriptTypes = <%= scriptTypes%>;
    config.trasfTypes = <%= trasfTypes%>;    
	
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

	Ext.onReady(function(){
		Ext.QuickTips.init();
		var manageDatasets = new Sbi.tools.ManageDatasets(config);
		var viewport = new Ext.Viewport({
			layout: 'border'
			, items: [
			    {
			       region: 'center',
			       layout: 'fit',
			       items: [manageDatasets]
			    }
			]
	
		});
	   	
	});


</script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>