<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>


<!-- spagobi:list moduleName="ListDataSourceModule" /-->



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
		var datasourceDetail = Ext.create('Sbi.tools.datasource.DataSourceListDetailPanel',{}); //by alias
		var datasourceDetailViewport = Ext.create('Ext.container.Viewport', {
			layout:'fit',
	     	items: [datasourceDetail]
	    });
    });
	

</script>
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>