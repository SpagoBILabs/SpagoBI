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

<%@page import="it.eng.spagobi.profiling.bean.SbiAttribute"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>
<%@page import="it.eng.spagobi.commons.bo.Domain,
				 it.eng.spagobi.tools.datasource.bo.*,
				 java.util.ArrayList,
				 java.util.List,
				 java.util.Map,
				 org.json.JSONArray" %>
<%@page import="org.json.JSONObject"%>
<%@page import="it.eng.spagobi.engines.config.bo.Engine"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.tools.dataset.bo.IDataSet"%>
<%@page import="it.eng.spagobi.tools.dataset.constants.DataSetConstants"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%

	String executionId = request.getParameter("SBI_EXECUTION_ID");
	

	if(executionId != null) {
		executionId = "'" + request.getParameter("SBI_EXECUTION_ID") + "'";
	} else {
		executionId = "null";
	}  
	
	
	
	SourceBean sbModuleResponse = (SourceBean) aServiceResponse.getAttribute("ExecuteBIObjectModule");
	
	//gets the metadata of dataset
	String dsId =  String.valueOf(sbModuleResponse.getAttribute(DataSetConstants.ID));
	String dsLabel =  String.valueOf(sbModuleResponse.getAttribute(DataSetConstants.LABEL));
	String dsTypeCd =  (String) sbModuleResponse.getAttribute(DataSetConstants.DS_TYPE_CD);
	JSONArray dsPars =  (JSONArray) sbModuleResponse.getAttribute(DataSetConstants.PARS);
	String dsTransformerType =  (String) sbModuleResponse.getAttribute(DataSetConstants.TRASFORMER_TYPE_CD);
	
	String divId = (executionId != null)?executionId:"highchartDiv";
	String divWidth = (String) sbModuleResponse.getAttribute("divWidth");
	String divHeight = (String) sbModuleResponse.getAttribute("divHeight");
	String theme = (String) sbModuleResponse.getAttribute("themeHighchart");
	Integer numCharts = (Integer) sbModuleResponse.getAttribute("numCharts");
	String subType = (String) sbModuleResponse.getAttribute("subType");
	
	//gets the json template
	JSONObject template = (JSONObject)sbModuleResponse.getAttribute("template");
	String docLabel = (String)sbModuleResponse.getAttribute("documentLabel");
	
	//only for test... delete with production
	
	//System.out.println("template in jsp: " + template.toString());
	//System.out.println("dsPars in jsp: " + dsPars.toString());
	//System.out.println("theme in jsp: " + theme);
	//System.out.println("numCharts in jsp: " + numCharts);
	
%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML CODE       														--%>
<%-- ---------------------------------------------------------------------- --%>

	<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>
	<% if (theme != null && !theme.equals("") ) { %>
		<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/engines/chart/themes/"+theme+".js")%>'></script>
	<% }%>
	<script type="text/javascript">
		var template =  <%= template.toString()  %>;
		Sbi.config = {};

		Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			baseUrl: url
		  , baseParams: params
		});

		var url = {
	    	host: '<%= request.getServerName()%>'
	    	, port: '<%= request.getServerPort()%>'
	    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
	    	   				  request.getContextPath().substring(1):
	    	   				  request.getContextPath()%>'
	    	    
	    };

		var params = {
				  SBI_EXECUTION_ID: <%= executionId %>
				, LIGHT_NAVIGATOR_DISABLED: 'TRUE'
			};
	
		
		Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			  baseUrl: url
		    , baseParams: params
		});

		Ext.onReady(function() { 					
			Ext.QuickTips.init();		
			
			var config = <%=template%>;
			config.dsId = <%=dsId%>;
			config.dsLabel = "<%=dsLabel%>";
			config.dsTypeCd = "<%=dsTypeCd%>";
			config.dsPars =  <%=dsPars%>;
			config.dsTransformerType = "<%=dsTransformerType%>";
			config.divId = "<%=divId%>";
			config.docLabel ="<%=docLabel%>";
			config.theme = "<%=theme%>";
			config.numCharts = <%=numCharts%>;

			
			var chartPanel = {};
			if (config.chart && config.chart.subType && config.chart.subType === 'MasterDetail') {
				chartPanel = new Sbi.engines.chart.MasterDetailChartPanel({'chartConfig':config});
			}else{
				chartPanel = new Sbi.engines.chart.HighchartsPanel({'chartConfig':config});
			}
			
			var viewport = new Ext.Viewport({
				layout: 'border'
				, items: [
				    {
				       region: 'center',
				       layout: 'fit',
				       items: [chartPanel]
				    }
				]
	
			});
		});
	</script>
	
	<%if (subType != null && subType.equalsIgnoreCase("MasterDetail")) {%>
		<div id="<%=divId%>__detail" style="height:<%=divHeight%>; width:<%=divWidth%>; float:left;"></div>
		<div id="<%=divId%>__master" style="height:<%=divHeight%>; width:<%=divWidth%>; float:left;"></div>
	<% }else{
		  for (int i=0; i<numCharts; i++ ) { %>
			<div id="<%=divId%>__<%=i%>" style="height:<%=divHeight%>; width:<%=divWidth%>; float:left;"></div>
	<%	  }
	   } %>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>