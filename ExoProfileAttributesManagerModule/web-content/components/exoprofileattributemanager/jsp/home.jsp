
<%--

Copyright 2005 Engineering Ingegneria Informatica S.p.A.

This file is part of SpagoBI.

SpagoBI is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

SpagoBI is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Spago; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

--%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%@ page import="javax.portlet.PortletURL,
				 it.eng.spago.navigation.LightNavigationManager" %>

<!-- IMPORT TAG LIBRARY  -->
<%@ taglib uri='http://java.sun.com/portlet' prefix='portlet'%>

<portlet:defineObjects/>

<%
	SourceBean moduleResponse = (SourceBean)aServiceResponse.getAttribute("ExoProfileAttributeManagerModule"); 
	
   	PortletURL listUserUrl = renderResponse.createActionURL();
   	listUserUrl.setParameter("PAGE", "ExoProfileAttributeManagerListUserPage");
   
	PortletURL synchAttrUrl = renderResponse.createActionURL();
	synchAttrUrl.setParameter("PAGE", "ExoProfileAttributeManagerPage");
	synchAttrUrl.setParameter("MESSAGE", "SYNCH_ATTRIBUTES");
	synchAttrUrl.setParameter(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true"); 
   	
%>

<table class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section-no-buttons' 
		    style='vertical-align:middle;padding-left:5px;'>
			<spagobi:message key="homeTitle"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
		</td>
	</tr>
</table>

<div class="div_background">
    <br/>	
	<table>
		<tr class="portlet-font">
			<td width="100" align="center">
				<img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/UserProfile32.png")%>' />
			</td>
			<td width="20">
				&nbsp;
			</td>
			<td vAlign="middle">
			    <br/> 
				<a href='<%=listUserUrl.toString()%>' class="link_main_menu" >
					<spagobi:message key = "changeProfSingleUser"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
				</a>
			</td>
		</tr>
		<tr class="portlet-font" vAlign="middle">
			<td width="100" align="center">
				<img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/Synch32.png")%>' />
			</td>
			<td width="20">
				&nbsp;
			</td>
			<td vAlign="middle">
			    <br/> 
				<a href='<%=synchAttrUrl.toString()%>' class="link_main_menu" >
					<spagobi:message key = "synchProf"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
				</a>
			</td>
		</tr>
	</table>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
</div>		
	
	




 



