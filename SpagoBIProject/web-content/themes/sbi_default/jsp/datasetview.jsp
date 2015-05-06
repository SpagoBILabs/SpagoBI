<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  
<%@page import="java.util.HashMap"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="org.json.JSONObject"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile" %>
<%@ include file="/WEB-INF/jsp/commons/rest_base410.jsp"%>

<%
	ObjectMapper mapper = new ObjectMapper();
	String datasetLabel = request.getParameter("datasetLabel"); 
	String pageSize = request.getParameter("pageSize"); 
	if(pageSize==null || pageSize.length()==0){
		pageSize = "10";
	}
	
	String[] privateParameters = {"datasetLabel","pageSize"};
	Map<String, String[]> parameters = request.getParameterMap();
	Map<String, Object> parametersMap = new HashMap<String, Object>();
	String parametersString = "";
	
	//remove the metadata parameters
	for(int i=0; i<privateParameters.length; i++){
		parameters.remove(privateParameters[i]);
	}
	
	
	//if a parameter contains only a value transform the type from string to single value
	Iterator<String> paramIter = parameters.keySet().iterator();
	while(paramIter.hasNext()){
		String key = paramIter.next();
		String[] value = (String[])parameters.get(key);
		if(value.length==1){
			parametersMap.put(key, value[0]);
		}else{
			parametersMap.put(key, value);
		}
	}
	
	

	
	if(parametersMap.size()==0){
		parametersString="{}";
	}else{
		parametersString= mapper.writeValueAsString(parametersMap);
	}
	parametersString= parametersString.substring(1,parametersString.length()-1);
	


%>


<!-- spagobi:list moduleName="ListDataSourceModule" /-->


<script type="text/javascript">


	
	var datasetLabel = '<%= datasetLabel %>';
	var pageSize = <%= pageSize %>;
	var parameters =  '<%= parametersString %>';

    Ext.onReady(function(){
    	var datasetDetail = Ext.create('Sbi.tools.dataset.DataSetServiceView',{datasetLabel:datasetLabel, pageSize:pageSize, params: {dataSetParameters: parameters} }); //by alias
    	var panel = Ext.create("Ext.Panel",{
    		layout:'border',
    		items:[datasetDetail]
    	})
    	
		
		var datasetViewport = Ext.create('Ext.container.Viewport', {
			layout:'fit',
	     	items: [panel]
	    });
    });
	

</script>
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>