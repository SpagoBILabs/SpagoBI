<%-- 
 SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
--%>

<%-- 
@author
Giorgio Federici (giorgio.federici@eng.it)
--%>

<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="java.util.Locale"%>

<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         session="true"
%>	

<%-- ---------------------------------------------------------------------- --%>
<%-- JAVA CODE 																--%>
<%-- ---------------------------------------------------------------------- --%>

<%

	String language = (String) request.getSession().getAttribute(SpagoBIConstants.SBI_LANGUAGE);
	String country = (String) request.getSession().getAttribute(SpagoBIConstants.SBI_COUNTRY);
	
	if(language == null || language.equals(""))
	{
		language = "en";
	}
	
	if(country == null || country.equals(""))
	{
		country = "US";
	}
	
	Locale locale = new Locale(language, country);
	
	UserProfile profile = (UserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
	
	String profileJSONStr = new ObjectMapper().writeValueAsString(profile);	
	String createSocialAnalysis = new ObjectMapper().writeValueAsString(SpagoBIConstants.CREATE_SOCIAL_ANALYSIS);
	
%>
<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeMessageResource.jspf" %>
		<%@include file="commons/includeSbiSocialAnalysisJS.jspf"%>
	
	</head>
	
	<body>
	
    	<script type="text/javascript">  
    	
    	Sbi.user = {};
    	
    	Sbi.user = <%= profileJSONStr %>;
    	Sbi.createsocialanalysis = <%= createSocialAnalysis %>;

    	Ext.onReady(function () {
    		
    		Ext.QuickTips.init();
    		
    		var socialAnalysisPanel = Ext.create('Sbi.social.analysis.SocialAnalysisPanel',{});
    		var socialAnalysisPanelViewport = Ext.create('Ext.container.Viewport', {
    			layout:'fit',
    	     	items: [socialAnalysisPanel]
    	    });
    	});
        
        </script>
	
	</body>

</html>




	

	
	
	
	
	
	
	
	
	
	
	
	
    