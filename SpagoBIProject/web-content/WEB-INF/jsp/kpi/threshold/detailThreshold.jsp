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

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%@page import="java.util.Map,java.util.HashMap"%>
<%@page import="it.eng.spago.navigation.LightNavigationManager"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page	import="it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService"%>
<%@page import="it.eng.spagobi.kpi.threshold.bo.Threshold"%>
<%
	String title = "";
	String id = "";
	String name = "";
	String code = "";
	String description = "";
	Integer threshold_type_id = null;
	
    String messageBunle = "component_kpi_messages"; 

    ConfigSingleton configure = ConfigSingleton.getInstance();
	SourceBean moduleBean = (SourceBean) configure
			.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					"DetailThresholdModule");

	if (moduleBean.getAttribute("CONFIG.TITLE") != null)
		title = (String) moduleBean.getAttribute("CONFIG.TITLE");
	
	String messageIn = (String) aServiceRequest.getAttribute("MESSAGE");
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
		.getAttribute("DetailThresholdModule");
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
				.getAttribute("DetailThresholdModule");
		Threshold threshold = (Threshold) moduleResponse.getAttribute("THRESHOLD");
		if(threshold.getId() != null) {
			id = threshold.getId().toString();
			messageIn = (String) moduleResponse.getAttribute("MESSAGE");
			messageSave = DelegatedDetailService.DETAIL_UPDATE;
		} else {
			messageIn = DelegatedDetailService.DETAIL_SELECT;
			messageSave = DelegatedDetailService.DETAIL_INSERT;
		}
	}

	if (messageIn != null
			&& messageIn
					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
		SourceBean moduleResponse = (SourceBean) aServiceResponse
				.getAttribute("DetailThresholdModule");
		Threshold threshold = (Threshold) moduleResponse.getAttribute("THRESHOLD");
		if (threshold != null) {
			if(threshold.getId()!=null)
				id = threshold.getId().toString();
			if(threshold.getName() != null)
				name = threshold.getName();
			if(threshold.getCode() != null)
				code = threshold.getCode();
			if(threshold.getDescription() != null)
				description = threshold.getDescription();
			if(threshold.getThresholdTypeId()!= null)
				threshold_type_id = threshold.getThresholdTypeId();
			else
				threshold_type_id = null;
		}
	}
	
	Map formUrlPars = new HashMap();
//	if(ChannelUtilities.isPortletRunning()) {
		formUrlPars.put("PAGE", "ThresholdPage");
		formUrlPars.put("MODULE", "DetailThresholdModule");
		formUrlPars.put("MESSAGE", messageSave);
		formUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
//	}
	
	String formUrl = urlBuilder.getUrl(request, formUrlPars);
	
	Map backUrlPars = new HashMap();
	backUrlPars.put("PAGE", "ThresholdPage");
	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
	String backUrl = urlBuilder.getUrl(request, backUrlPars);
%>


<%@page import="java.util.List"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.commons.bo.Domain"%><table class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
			style='vertical-align: middle; padding-left: 5px;'>
			<spagobi:message key="<%=title%>" bundle="<%=messageBunle%>" /></td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		<td class='header-button-column-portlet-section'><a
			href="javascript:document.getElementById('thresholdForm').submit()"> <img
			class='header-button-image-portlet-section'
			title='<spagobi:message key = "sbi.kpi.button.save.title" bundle="<%=messageBunle%>" />'
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/save.png", currTheme)%>'
			alt='<spagobi:message key = "sbi.kpi.button.save.title" bundle="<%=messageBunle%>" />' /> </a></td>
		<td class='header-button-column-portlet-section'><a
			href='<%=backUrl%>'> <img
			class='header-button-image-portlet-section'
			title='<spagobi:message key = "sbi.kpi.button.back.title" bundle="<%=messageBunle%>"/>'
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme)%>'
			alt='<spagobi:message key = "sbi.kpi.button.back.title" bundle="<%=messageBunle%>"/>' /> </a></td>
	</tr>
</table>

<form method='POST' action='<%=formUrl%>' id='thresholdForm' name='thresholdForm'>
<input type='hidden' value='<%=id%>' name='id' />

<div class="div_detail_area_forms">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.name" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="name" size="50"
	value="<%=StringEscapeUtils.escapeHtml(name)%>" maxlength="200"> &nbsp;*</div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.code" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="code" size="50"
	value="<%=StringEscapeUtils.escapeHtml(code)%>" maxlength="200"></div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.description" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height: 150px;">
<textarea
  class='portlet-text-area-field'  cols="40" style='height: 110px;' name="description"><%=StringEscapeUtils.escapeHtml(description)%></textarea></div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.thresholdType" bundle="<%=messageBunle%>"/> </span></div>
<% 
String readonly = "";
if(messageSave.trim().equals(DelegatedDetailService.DETAIL_UPDATE)) {
	readonly = "disabled";
}%>
<div class='div_detail_form'>
<select class='portlet-form-field' name="threshold_type_id" <%= readonly %>>
<%
	List thresholdTypes = DAOFactory.getDomainDAO().loadListDomainsByType("THRESHOLD_TYPE");
	Iterator itt = thresholdTypes.iterator();
	while (itt.hasNext()){
		Domain domain = (Domain)itt.next();
		String selected = "";
		if (threshold_type_id != null && threshold_type_id.intValue() == domain.getValueId().intValue()){
			selected = "selected='selected'";
		}
		%>    			 		
		<option value="<%= domain.getValueId() %>" label="<%= StringEscapeUtils.escapeHtml(domain.getTranslatedValueName(locale)) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(domain.getTranslatedValueName(locale)) %>	
		</option>
		<%
	}
%>
</select >

</div>
<% 
if(messageSave.trim().equals(DelegatedDetailService.DETAIL_UPDATE)) { %>

<input type="hidden" name="threshold_type_id" value="<%=threshold_type_id %>">
<%} %>
</form>

<spagobi:error/>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>