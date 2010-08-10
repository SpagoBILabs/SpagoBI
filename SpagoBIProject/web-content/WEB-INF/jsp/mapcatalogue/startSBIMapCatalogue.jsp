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
<%@page language="java"
        contentType="text/html; charset=ISO-8859-1"
        pageEncoding="ISO-8859-1" %>
         
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="it.eng.spagobi.commons.utilities.urls.UrlBuilderFactory"%>
<%@page import="it.eng.spago.base.RequestContainer"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%
	Map backUrlPars = new HashMap();
	backUrlPars.put("ACTION_NAME", "START_ACTION");
	backUrlPars.put("PUBLISHER_NAME", "LoginSBICataloguePublisher");
	//backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_RESET, "true");
	String backUrl = urlBuilder.getUrl(request, backUrlPars);

	Map listMapUrlPars = new HashMap();
	listMapUrlPars.put("PAGE", "ListMapsPage");
	String listMapUrl = urlBuilder.getUrl(request, listMapUrlPars);
	
	Map listFeatUrlPars = new HashMap();
	listFeatUrlPars.put("PAGE", "ListFeaturesPage");
	String listFeatUrl = urlBuilder.getUrl(request, listFeatUrlPars);
%>
<% if (userProfile.isAbleToExecuteAction(SpagoBIConstants.MAPCATALOGUE_MANAGEMENT)) {%>

<portlet:defineObjects/>

	
<div class="div_background">
	<br/>
	<table>
		<tr class="portlet-font">
			<td width="100" align="center">
				<img height="80px" width="80x" src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/mapcatalogue/maps.png", currTheme)%>' />
			</td>
			<td width="20">
				&nbsp;
			</td>
			<td vAlign="middle">
			    <br/> 
				<a href='<%=listMapUrl%>' 
					class="link_main_menu" >
					<spagobi:message key="SBIMapCatalogue.linkMaps" bundle="component_mapcatalogue_messages" /></a>
			</td>
		</tr>
		<tr class="portlet-font">
			<td width="100" align="center">
				<img height="80px" width="80px" src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/mapcatalogue/featureManagement.png", currTheme)%>' />
			</td>
			<td width="20">
				&nbsp;
			</td>
			<td vAlign="middle">
			    <br/> 
				<a href='<%=listFeatUrl%>' 
					class="link_main_menu" >
					<spagobi:message key="SBIMapCatalogue.linkFeatures" bundle="component_mapcatalogue_messages" /></a>
			</td>
		</tr>				
	</table>
	<%} %>
	<br/>
</div>
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>
