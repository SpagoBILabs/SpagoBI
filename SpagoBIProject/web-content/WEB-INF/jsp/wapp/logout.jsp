<%-- SpagoBI, the Open Source Business Intelligence suite

 © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<iframe id='invalidSessionJasper'
                 name='invalidSessionJasper'
                 src='<%=GeneralUtilities.getSpagoBiHost()%>/SpagoBIJasperReportEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe>  

<iframe id='invalidSessionJPivot'
                 name='invalidSessionJPivot'
                 src='<%=GeneralUtilities.getSpagoBiHost()%>/SpagoBIJPivotEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe>  

<iframe id='invalidSessionQbe'
                 name='invalidSessionQbe'
                 src='<%=GeneralUtilities.getSpagoBiHost()%>/SpagoBIQbeEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe>  

<iframe id='invalidSessionGeo'
                 name='invalidSessionGeo'
                 src='<%=GeneralUtilities.getSpagoBiHost()%>/SpagoBIGeoEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe>  

<iframe id='invalidSessionBirt'
                 name='invalidSessionBirt'
                 src='<%=GeneralUtilities.getSpagoBiHost()%>/SpagoBIBirtReportEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe>  

<iframe id='invalidSessionTalend'
                 name='invalidSessionTalend'
                 src='<%=GeneralUtilities.getSpagoBiHost()	%>/SpagoBITalendEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe> 

<iframe id='invalidSessionChart'
                 name='invalidSessionChart'
                 src='<%=GeneralUtilities.getSpagoBiHost()	%>/SpagoBIChartEngine/invalidateSession.jsp'
                 height='0'
                 width='0'
                 frameborder='0' >
</iframe> 
<%

boolean backUrlB=false;
String backUrl="";
if(session.getAttribute(SpagoBIConstants.BACK_URL)!=null){
	backUrl=(String)session.getAttribute(SpagoBIConstants.BACK_URL);
	backUrlB=true;
}


session.invalidate();


//Check if SSO is active

String active = SingletonConfig.getInstance().getConfigValue("SPAGOBI_SSO.ACTIVE");

if ((active == null || active.equalsIgnoreCase("false")) && backUrlB==false) {
	String context = request.getContextPath();
	response.sendRedirect(context);
}
else if (active != null && active.equalsIgnoreCase("true")) {

	String urlLogout =  SingletonConfig.getInstance().getConfigValue("SPAGOBI_SSO.SECURITY_LOGOUT_URL");
	if(backUrlB==true){
		response.sendRedirect(backUrl); 
	}
	response.sendRedirect(urlLogout);

} %>
 

<%if (active != null && active.equalsIgnoreCase("true")) { %>
	<script>window.close();</script>
<% } %>


