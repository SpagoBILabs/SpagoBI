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

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>
<%@ page
	import="java.util.Map,java.util.HashMap,java.util.List,java.util.ArrayList"%>
<%@page
	import="it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.kpi.model.bo.Resource"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="java.util.ArrayList"%>
<%@page import="it.eng.spagobi.commons.bo.Domain"%>

<%
	String messageIn = (String) aServiceRequest.getAttribute("MESSAGE");
	String id = (String) aServiceRequest.getAttribute("ID");
	String tableName = "";
	String columnName = "";
	String resourceName = "";
	String resourceCode = "";
	String resourceDescription = "";
	String resourceTypeDescription = "";
	Integer resourceTypeId = null;

	String title = "";
	
    ConfigSingleton configure = ConfigSingleton.getInstance();
	SourceBean moduleBean = (SourceBean) configure
			.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					"DetailResourcesModule");
	
	if (moduleBean.getAttribute("CONFIG.TITLE") != null)
		title = (String) moduleBean.getAttribute("CONFIG.TITLE");
	
	String messageSave = "";

	// DETAIL_SELECT
	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
		messageSave = DelegatedDetailService.DETAIL_UPDATE;
	}
	// DETAIL_UPDATE
	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_UPDATE)) {
		SourceBean moduleResponse = (SourceBean) aServiceResponse
		.getAttribute("DetailResourcesModule");
		messageIn = (String) moduleResponse.getAttribute("MESSAGE");
		messageSave = DelegatedDetailService.DETAIL_UPDATE;
	}
	
	//DETAIL_NEW
	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_NEW)) {
		messageSave = DelegatedDetailService.DETAIL_INSERT;
	}
	//DETAIL_INSERT
	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_INSERT)) {
		SourceBean moduleResponse = (SourceBean) aServiceResponse
				.getAttribute("DetailResourcesModule");
		Resource resource = (Resource) moduleResponse.getAttribute("RESOURCE");
		
		if(resource.getId()!= null){
			id = resource.getId().toString();
			messageIn = (String) moduleResponse.getAttribute("MESSAGE");
			messageSave = DelegatedDetailService.DETAIL_UPDATE;
		} else { // if it has a validation error
			messageIn = DelegatedDetailService.DETAIL_SELECT;
			messageSave = DelegatedDetailService.DETAIL_INSERT;
		}
		
	}

	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
		SourceBean moduleResponse = (SourceBean) aServiceResponse
				.getAttribute("DetailResourcesModule");
		Resource resource = (Resource) moduleResponse.getAttribute("RESOURCE");
		if (resource != null) {
			tableName = resource.getTable_name();
			columnName = resource.getColumn_name();
			resourceName = resource.getName();
			resourceCode = resource.getCode();
			resourceDescription = resource.getDescr();
			resourceTypeDescription = resource.getType();
			resourceTypeId = resource.getTypeId();
		}
	}

	Map formUrlPars = new HashMap();
	formUrlPars.put("PAGE", "ResourcesPage");
	formUrlPars.put("MODULE", "DetailResourcesModule");
	formUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
	formUrlPars.put("MESSAGE", messageSave);
	String formUrl = urlBuilder.getUrl(request, formUrlPars);

	Map backUrlPars = new HashMap();
	backUrlPars.put("PAGE", "ResourcesPage");
	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
	String backUrl = urlBuilder.getUrl(request, backUrlPars);

	String messageBundle = "component_kpi_messages";
%>


<table
	class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
			style='vertical-align: middle; padding-left: 5px;'><spagobi:message
			key="<%=title%>" bundle="<%=messageBundle%>" /></td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		<td class='header-button-column-portlet-section'><a
			href="javascript:document.getElementById('ResourceForm').submit()">
		<img class='header-button-image-portlet-section'
			title='<spagobi:message key = "sbi.kpi.button.save.title" bundle="<%=messageBundle%>" />'
			src='<%=urlBuilder.getResourceLinkByTheme(request,
									"/img/save.png", currTheme)%>'
			alt='<spagobi:message key = "sbi.kpi.button.save.title" bundle="<%=messageBundle%>"/>' />
		</a></td>
		<td class='header-button-column-portlet-section'><a
			href='<%=backUrl%>'> <img
			class='header-button-image-portlet-section'
			title='<spagobi:message key = "sbi.kpi.button.back.title" bundle="<%=messageBundle%>" />'
			src='<%=urlBuilder.getResourceLinkByTheme(request,
									"/img/back.png", currTheme)%>'
			alt='<spagobi:message key = "sbi.kpi.button.back.title" bundle="<%=messageBundle%>"/>' />
		</a></td>
	</tr>
</table>

<form method='post' action='<%=formUrl%>' id='ResourceForm'
	name='ModelForm'><input type="hidden" name="ID" value="<%=id%>">
<div class="div_detail_area_forms">

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.name" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="resourceName" size="50"
	value="<%=StringEscapeUtils.escapeHtml(resourceName)%>" maxlength="200"> &nbsp;*</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.code" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="resourceCode" size="50"
	value="<%=StringEscapeUtils.escapeHtml(resourceCode)%>" maxlength="200"> &nbsp;*</div>	

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.description" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form' style='height: 150px;'>
	<textarea name="resourceDescription" cols="40" style='height: 110px;' class='portlet-text-area-field'><%=StringEscapeUtils.escapeHtml(resourceDescription)%></textarea>
</div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.tableName" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="tableName" size="50"
	value="<%=StringEscapeUtils.escapeHtml(tableName)%>" maxlength="200"></div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.columnName" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="columnName" size="50"
	value="<%=StringEscapeUtils.escapeHtml(columnName)%>" maxlength="200"></div>

<%
 	if (messageIn != null
 			&& messageSave
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_UPDATE)) {
 %>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.model.typeName" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="resourceTypeDesc" size="50"
	value="<%=StringEscapeUtils.escapeHtml(resourceTypeDescription)%>" maxlength="200" readonly></div>	
<input type="hidden" name="resourceTypeId" value="<%=resourceTypeId %>">
<%}%>

<%
 	if (messageIn != null
 			&& messageSave
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_INSERT)) {
 %>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.resource.resource.type" bundle="<%=messageBundle%>" /> </span></div>

<div class='div_detail_form'>	
<select  class='portlet-form-field' name="resourceTypeId">
<% List ChartType = DAOFactory.getDomainDAO().loadListDomainsByType("RESOURCE");
   for (Iterator iterator = ChartType.iterator(); iterator.hasNext();) {
		Domain domain = (Domain) iterator.next();
	%>
	<option value="<%=domain.getValueId()%>"
		label="<%=StringEscapeUtils.escapeHtml(domain.getTranslatedValueName(locale))%>" ><%=StringEscapeUtils.escapeHtml(domain.getTranslatedValueName(locale))%>
	</option>
	<%
		}
	%>
</select>
</div>	
<%} %>
</div>

</form>

<spagobi:error />
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>