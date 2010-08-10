<%-- 
SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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

<%-- 
author: Andrea Gioia (andrea.gioia@eng.it)
--%>
<%@ page language="java" 
	     contentType="text/html; charset=ISO-8859-1" 
	     pageEncoding="ISO-8859-1"%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA IMPORTS															--%>
<%-- ---------------------------------------------------------------------- --%>
<%@page import="it.eng.spago.configuration.*"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.utilities.engines.EngineConstants"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="it.eng.spagobi.engines.commonj.*"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>
<%
	UserProfile profile;
	Locale locale;
	String isFromCross;
	boolean isPowerUser;
	Integer resultLimit;
	boolean isMaxResultLimitBlocking;
	boolean isQueryValidationEnabled;
	boolean isQueryValidationBlocking;
	String spagobiServerHost;
	String spagobiContext;
	String spagobiSpagoController;
	String docId;
	
	
	CommonjEngineConfig qcommonJEngineConfig = CommonjEngineConfig.getInstance();

    spagobiServerHost = request.getParameter(SpagoBIConstants.SBI_HOST);
    spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
    spagobiSpagoController = request.getParameter(SpagoBIConstants.SBI_SPAGO_CONTROLLER);
    docId = request.getParameter("document");

        
    
	Map parsMap=request.getParameterMap();
	Map<String,String> paramsMapToSend = new HashMap<String, String>();
	String parametersString="";
	for (Iterator iterator = parsMap.keySet().iterator(); iterator.hasNext();) {
		String url= (String) iterator.next();
		if(url.equals("ACTION_NAME")) continue;
		Object val=parsMap.get(url);
		// take only String or numbers
		if(val!=null && val instanceof String[]){
			String[] strs=(String[])val;
			String toAdd="";			
			if(strs.length==1){
				paramsMapToSend.put(url, strs[0].toString());
				parametersString+="&"+url+"="+strs[0];	
			}
			else{
			parametersString+="&"+url+"=[";
			toAdd+="&"+url+"=[";
			for(int i=0; i< strs.length;i++){
				String valS=strs[i];
				if(i==0){
					parametersString+=valS;
					toAdd+=valS;
				}
			else{
				parametersString+=","+valS;				
				toAdd+=","+valS;
			}
			}
				parametersString+="]";
				toAdd+="]";
				paramsMapToSend.put(url, toAdd.toString());
			}
		}
		else
		if(val!=null && (val instanceof String || val instanceof Integer)) {
			parametersString+="&"+url+"="+val.toString();
			paramsMapToSend.put(url, val.toString());
		}
	}
	if(parametersString.indexOf('&')==0){
		parametersString=parametersString.substring(1,parametersString.length());
	}

	// Check if I am in scheduler mode!	in that case call directly the start Engine
	Object userIdO = parsMap.get("user_id");
	String userIdNow = null;
	if(userIdO != null){
	 if(userIdO instanceof String[]){
		 String[] userIdArray = (String[])userIdO;
		 userIdNow = userIdArray[0];
		 }
	 else if(userIdO instanceof String){
		userIdNow = userIdO.toString();	 
	 }
	
	 if(userIdNow.equalsIgnoreCase("scheduler")){
			// call directly startWorkEngineService
			// TODO: separate logic code from action so it can be use as a class		
			//auditProxy = new AuditServiceProxy(getAuditId(), getUserIdentifier(), getHttpSession());
		   //EventServiceProxy eventProxy = new EventServiceProxy(userIdNow, ses);

		   // I have to convert the parmeters MAP to a Map of strings
		   
			StartWorkAction	startWorkAction = new StartWorkAction();
	     	 startWorkAction.serviceStart(userIdNow, docId, paramsMapToSend, session, request, false);
	 }
	
	}
	
	

%>


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>

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