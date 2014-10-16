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

<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         session="true"
%>	


<%-- ---------------------------------------------------------------------- --%>
<%-- HTML	 																--%>
<%-- ---------------------------------------------------------------------- --%>
<html>
	
	<head>
	
		<%@include file="commons/includeExtJS.jspf" %>
		<%@include file="commons/includeSbiSocialAnalysisJS.jspf"%>
	
	</head>
	
	<body>
	
    	<script type="text/javascript">  
    	

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




	

	
	
	
	
	
	
	
	
	
	
	
	
    