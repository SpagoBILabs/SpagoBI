<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
  
 
  

<%@page import="it.eng.spagobi.engines.commonj.services.StartWorkAction"%>
<%@page import="it.eng.spagobi.utilities.engines.AuditServiceProxy"%>
<%@page import="it.eng.spagobi.services.proxy.EventServiceProxy"%>
<%@page import="java.util.HashMap"%><html>
	
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
	    <%@include file="commons/includeSrcJS.jspf" %>
	</head>
	
	<body>
	
    	<script type="text/javascript">  
	     
	     var generalPanel=null;
	        
	        Ext.onReady(function(){
	        	Ext.QuickTips.init();   
	        	
	        	Sbi.config = {};
	        	
	        	var url = {
			    	host: '<%= request.getServerName()%>'
			    	, port: '<%= request.getServerPort()%>'
			    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
			    	   				  request.getContextPath().substring(1):
			    	   				  request.getContextPath()%>'
			    	    
		    	};
		
			    Sbi.config.serviceRegistry = new Sbi.commons.ServiceRegistry({
			    	baseUrl: url
			    });
	        	//alert('prima');
	        	generalPanel= new Sbi.commons.ExecutionPanel({document_id:<%=docId%>, parameters:'<%=parametersString%>'});
	        	//alert('dopo');
	        	//generalPanel.monitorStatus();
		        
		        var viewport = new Ext.Viewport({
	           		items: [generalPanel]
	           	});  
			        
	           //	setTimeout("timer()", 5000);
	           	timer();
	           	
	      	});
	      	
	      	var val;
	     function timer(){
			//alert(generalPanel);	
			generalPanel.statusProcess(<%=docId%>);
			
			if(generalPanel.status==2 || generalPanel.status==4)
			{
			}
			else{
			setTimeout("timer()", 3000);	
			}
	}
	
		
	      	
	      	
	    </script>
	
	</body>

</html>