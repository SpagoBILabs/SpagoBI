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
<%@ page import="it.eng.spagobi.commons.bo.Config,
				 java.util.ArrayList,
				 java.util.List,
				 org.json.JSONArray" %>
<%
    List thrTypesCd = (List) aSessionContainer.getAttribute("thrTypesList");
    List thrSeverityTypesCd = (List) aSessionContainer.getAttribute("thrSeverityTypes");
	List kpiTypesCd = (List) aSessionContainer.getAttribute("kpiTypesList");
	List measureTypesCd = (List) aSessionContainer.getAttribute("measureTypesList");
	List udpListCd = (List) aSessionContainer.getAttribute("udpKpiList");

	List metricScaleTypesCd = (List) aSessionContainer.getAttribute("metricScaleTypesList");

%>


<%@page import="it.eng.spagobi.tools.udp.bo.Udp"%>
<%@page import="it.eng.spagobi.chiron.serializer.UdpJSONSerializer"%>
<%@page import="org.json.JSONObject"%>

<script type="text/javascript" src='<%=urlBuilder.getResourceLink(request, "/js/src/ext/sbi/service/ServiceRegistry.js")%>'></script>

<script type="text/javascript">


	
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
		//var manageKpis = new Sbi.kpi.ManageKpis(config);
		var manageKpis = new Sbi.config.ManageConfig({});
		var viewport = new Ext.Viewport({
			layout: 'border'
			, items: [
			    {
			       region: 'center',
			       layout: 'fit',
			       items: [manageKpis]
			    }
			]
	
		});
	   	
	});


</script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>