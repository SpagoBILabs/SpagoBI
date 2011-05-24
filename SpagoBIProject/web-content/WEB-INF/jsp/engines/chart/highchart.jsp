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

	//gets the json template
	JSONObject template = (JSONObject)sbModuleResponse.getAttribute("template");
	System.out.println("template in jsp: " + template.toString());
	//String template = "Bar Chart";
%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML CODE       														--%>
<%-- ---------------------------------------------------------------------- --%>

	<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>
	<script type="text/javascript" src="http://highcharts.com/js/testing.js"></script>
	
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
			
			var chartPanel;
			switch (config.chart.type) {	        
		        case 'bar' || 'column':
		        	chartPanel =  new Sbi.engines.chart.BarChartPanel({'chartConfig':config});
		        	break;
		        case 'line':
		        	chartPanel = new Sbi.engines.chart.LineChartPanel({'chartConfig':config});
		        	break;
		        case 'pie':
		        	chartPanel = new Sbi.engines.chart.PieChartPanel({'chartConfig':config});
		        	break;
		        default: 
		        	alert('Unknown widget!');
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
	<div id="pippo"></div>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>