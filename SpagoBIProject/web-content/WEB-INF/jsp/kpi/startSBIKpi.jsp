<%--
    Copyright 2008 Engineering Ingegneria Informatica S.p.A.

    This file is part of Spago4Q.

    Spago4Q is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published 
    by the Free Software Foundation; either version 3 of the License, or
    any later version.

    Spago4Q is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
--%>

<%@ taglib uri='http://java.sun.com/portlet' prefix='portlet'%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<portlet:defineObjects/>

<% String messageBunle = "component_kpi_messages"; %>

<table class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section-no-buttons'
			style='vertical-align: middle; padding-left: 5px;'>
			<spagobi:message key="sbi.kpi.kpiConfiguration.title" bundle="<%=messageBunle%>"/></td>
	</tr>
</table>


<div class="div_background">
<table>

  <tr class="portlet-font">
    <td width="100" align="center">
      <img src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/kpi/kpi.png", currTheme)%>' />
    </td>
    <td width="20">&nbsp;</td>
    <td vAlign="middle">
      <a href='<portlet:actionURL><portlet:param name="PAGE" value="KpiPage"/>
								  <portlet:param name="FIELD_ORDER" value="NAME"/></portlet:actionURL>'
         class="link_main_menu"> 
        <spagobi:message key="sbi.kpi.kpiDefinition.label" bundle="<%=messageBunle%>"/>
      </a>
    </td>
  </tr>

  <tr class="portlet-font">
    <td width="100" align="center">
      <img src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/kpi/thresholds.png", currTheme)%>' />
    </td>
	<td width="20">&nbsp;</td>
	<td vAlign="middle">
	  <a href='<portlet:actionURL><portlet:param name="PAGE" value="ThresholdPage"/>
								  <portlet:param name="FIELD_ORDER" value="NAME"/></portlet:actionURL>'
	    class="link_main_menu">
		<spagobi:message key="sbi.kpi.thresholdDefinition.label" bundle="<%=messageBunle%>" />
  	  </a>
    </td>
  </tr>

  <tr class="portlet-font">
		<td width="100" align="center"><img
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/kpi/model-def.png", currTheme)%>' />
		</td>
		<td width="20">&nbsp;</td>
		<td vAlign="middle">
		  <a href='<portlet:actionURL><portlet:param name="PAGE" value="ModelPage"/>
		  							  <portlet:param name="FIELD_ORDER" value="NAME"/>
									  </portlet:actionURL>'
			class="link_main_menu">
			<spagobi:message key="sbi.kpi.modelDefinition.label" bundle="<%=messageBunle%>" />
		  </a>
		</td>
	</tr>
	
	  <tr class="portlet-font">
		<td width="100" align="center"><img
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/kpi/model-inst.png", currTheme)%>' />
		</td>
		<td width="20">&nbsp;</td>
		<td vAlign="middle">
		  <a href='<portlet:actionURL><portlet:param name="PAGE" value="ModelInstancePage"/>
		  							  <portlet:param name="FIELD_ORDER" value="NAME"/>
									  </portlet:actionURL>'
			class="link_main_menu">
			<spagobi:message key="sbi.kpi.modelInstanceDefinition.label" bundle="<%=messageBunle%>" />
		  </a>
		</td>
	</tr>
	
	<tr class="portlet-font">
		<td width="100" align="center"><img
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/kpi/resources.png", currTheme)%>' />
		</td>
		<td width="20">&nbsp;</td>
		<td vAlign="middle">
		  <a href='<portlet:actionURL><portlet:param name="PAGE" value="ResourcesPage"/>
		  							  <portlet:param name="FIELD_ORDER" value="NAME"/>
									  </portlet:actionURL>'
			class="link_main_menu">
			<spagobi:message key="sbi.kpi.resourcesDefinition.label" bundle="<%=messageBunle%>" />
		  </a>
		</td>
	</tr>
	
	<%--
	 
	  <tr class="portlet-font">
    <td width="100" align="center"><img
      src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/img/userKpi.png")%>' />
    </td>
    <td width="20">&nbsp;</td>
    <td vAlign="middle"><br />
    <a
      href='<portlet:actionURL><portlet:param name="PAGE" value="UserKpiPage"/></portlet:actionURL>'
      class="link_main_menu"> <spagobi:message key="s4q.wz7.userKpi" /> </a>
    </td>
  </tr>
	<tr class="portlet-font">
		<td width="100" align="center"><img
			src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/img/measureIcon.png")%>' />
		</td>
		<td width="20">&nbsp;</td>
		<td vAlign="middle"><br />
		<a
			href='<portlet:actionURL><portlet:param name="PAGE" value="MeasureModelPage"/></portlet:actionURL>'
			class="link_main_menu"> <spagobi:message key="s4q.wz1.measureModel" /> </a></td>
	</tr>
	<tr class="portlet-font">
		<td width="100" align="center"><img
			src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/img/assessmentIcon.png")%>' />
		</td>
		<td width="20">&nbsp;</td>
		<td vAlign="middle"><br />
		<a
			href='<portlet:actionURL><portlet:param name="PAGE" value="AssessmentModelPage"/></portlet:actionURL>'
			class="link_main_menu"> <spagobi:message key="s4q.wz2.assessmentModel" /> </a></td>
	</tr>
	
	
	--%>	
</table>

</div>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>
