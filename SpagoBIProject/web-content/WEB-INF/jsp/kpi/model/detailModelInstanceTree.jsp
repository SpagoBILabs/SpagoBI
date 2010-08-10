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

<%@ page import="java.util.Map,java.util.HashMap,java.util.List,java.util.ArrayList"%>
<%@page import="it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.kpi.model.bo.ModelInstance"%>
<%@page import="it.eng.spagobi.kpi.model.bo.Model"%>
<%@page import="it.eng.spagobi.commons.bo.Domain"%>
<%@page import="it.eng.spagobi.kpi.config.bo.Periodicity"%>
<%@page import="it.eng.spagobi.kpi.model.utils.DetailModelInstanceUtil"%>
<%@page import="it.eng.spagobi.kpi.config.bo.Kpi"%>
<%@page import="it.eng.spagobi.kpi.threshold.bo.Threshold"%>
<%@page import="it.eng.spagobi.kpi.config.bo.KpiInstance"%>
<%@page import="it.eng.spagobi.commons.utilities.SpagoBIUtilities"%>
<%@page import="it.eng.spagobi.kpi.model.utils.DetailModelInstanceUtil"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.UUID"%>

<%
	String messageIn = (String) aServiceRequest.getAttribute("MESSAGE");
	String modelId = (String) aServiceRequest.getAttribute("MODEL_ID");
	String parentId = (String) aServiceRequest.getAttribute("ID");
	
	String modelInstanceName = "";
	String modelInstanceDescription = "";
	String modelInstanceLabel = "";
	
	String modelName = "";
	String modelDescription = "";
	String modelCode = "";
	String typeName = "";
	String typeDescription = "";
	List attributeList = null;
	
	Integer kpiId = null;
	Integer thresholdId = null;
	Integer chartTypeId = null;
	Integer periodicityId = null;
	String weight = "";
	String target = "";
	
	String startDateS = "";
	String endDateS = "";

	String dojoFormat ="MM/dd/yyyy";

	String title = "";
	
	String dateFormat = GeneralUtilities.getLocaleDateFormat(aSessionContainer);
	
	String modelInstanceUUID = "";
	String displayModelInstanceDiv = "display:none;";
	String radioModelCheked = "";

	String displayKpiDiv = "display:block;";
	String radioKpiCheked = "checked=\"checked\"";

    ConfigSingleton configure = ConfigSingleton.getInstance();
	SourceBean moduleBean = (SourceBean) configure
	.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
			"DetailModelInstanceTreeModule");
	
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
		.getAttribute("DetailModelInstanceTreeModule");
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
		.getAttribute("DetailModelInstanceTreeModule");
		ModelInstance modelInst = (ModelInstance) moduleResponse.getAttribute("MODELINSTANCE");
		
		if (modelInst != null){
	modelId = modelInst.getId().toString();
	messageIn = (String) moduleResponse.getAttribute("MESSAGE");
	if (messageIn == null) {
		messageIn = DelegatedDetailService.DETAIL_NEW;
	    messageSave = DelegatedDetailService.DETAIL_INSERT;
	} else {
		messageSave = DelegatedDetailService.DETAIL_UPDATE;
	}
	} else {
		messageIn = DelegatedDetailService.DETAIL_SELECT;
		messageSave = DelegatedDetailService.DETAIL_INSERT;
	}
	}

	if (messageIn != null
	&& messageIn
			.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
		SourceBean moduleResponse = (SourceBean) aServiceResponse
		.getAttribute("DetailModelInstanceTreeModule");
		ModelInstance modelInstance = (ModelInstance) moduleResponse.getAttribute("MODELINSTANCE");
		if (modelInstance != null) {
	modelInstanceName = modelInstance.getName();
	modelInstanceDescription = modelInstance.getDescription();
	modelInstanceLabel = modelInstance.getLabel();
	Date startDate = modelInstance.getStartDate();
	Date endDate = modelInstance.getEndDate();
	Format formatter = new SimpleDateFormat(dojoFormat);
	modelInstanceUUID = modelInstance.getModelUUID();
	
	if (modelInstanceUUID != null && (!modelInstanceUUID.trim().equals(""))){
		displayModelInstanceDiv = "display:block;";
		radioModelCheked = "checked=\"checked\"";

		displayKpiDiv = "display:none;";
		radioKpiCheked = "";
	}

	if (startDate != null){
		startDateS =formatter.format(startDate);
	}
	
	if (endDate != null){
		endDateS = formatter.format(endDate);
	}
	
	
	Model aModel = modelInstance.getModel();
	
	if (aModel != null){
		modelName = aModel.getName();
		modelDescription = aModel.getDescription();
		modelCode = aModel.getCode();
		typeName = aModel.getTypeName();
		typeDescription = aModel.getTypeDescription();
		attributeList = aModel.getModelAttributes();
	}
	KpiInstance aKpiInstance = modelInstance.getKpiInstance();
	
	if (aKpiInstance != null){
		kpiId = aKpiInstance.getKpi();
		thresholdId = aKpiInstance.getThresholdId();
		chartTypeId = aKpiInstance.getChartTypeId();
		periodicityId = aKpiInstance.getPeriodicityId();
		Double aWeight = aKpiInstance.getWeight();
		if (aWeight != null)
			weight = aWeight.toString();
		Double aTarget = aKpiInstance.getTarget();
		if (aTarget !=null)
			target = aTarget.toString();
	}
	
		}
	}

	Map formUrlPars = new HashMap();
	formUrlPars.put("PAGE", "ModelInstanceTreePage");
	formUrlPars.put("MODULE", "DetailModelInstanceTreeModule");
	formUrlPars.put("MESSAGE", messageSave);
	formUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
	String formUrl = urlBuilder.getUrl(request, formUrlPars);

	Map backUrlPars = new HashMap();
	backUrlPars.put("PAGE", "ModelInstanceTreePage");
	backUrlPars.put("ID", parentId);
	backUrlPars.put("MODULE","ListModelInstanceTreeModule");
	
	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_RESET, "");
	String backUrl = urlBuilder.getUrl(request, backUrlPars);

	String messageBundle = "component_kpi_messages";
	String messageBundle2 = "messages";
%>



<%@page import="it.eng.spago.navigation.LightNavigationManager"%>

<%@page import="java.util.ArrayList"%>
<%@page import="it.eng.spagobi.kpi.model.bo.ModelAttribute"%><table
	class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
			style='vertical-align: middle; padding-left: 5px;'><spagobi:message
			key="<%=title%>" bundle="<%=messageBundle%>" /></td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		<td class='header-button-column-portlet-section'>
 			<a href='#' id="openInfo"> 
				<img class='header-button-image-portlet-section'
				src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/info22.png", currTheme)%>' 
				title='<spagobi:message key = "sbi.kpi.button.info.title" bundle="<%=messageBundle%>" />'
				alt='<spagobi:message key = "sbi.kpi.button.info.title" bundle="<%=messageBundle%>"/>'
				/> 
			</a>
		</td>
		
		<td class='header-button-column-portlet-section'><a
			href="javascript:document.getElementById('ModelForm').submit()">
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


<table width="100%"
  style="margin-top: 3px; margin-left: 3px; margin-right: 3px; margin-bottom: 5px;">
  <tr height="20">
    <td class="td_tree" valign="top"><!-- Column with the navigation tree --> <%
  // drawing the tree
  
 %> <spagobi:treeObjects moduleName="DetailModelInstanceTreeModule"
		htmlGeneratorClass="it.eng.spagobi.kpi.model.presentation.ModelInstanceStructureTreeHtmlGenerator" /></td>
    <td class='td_form' valign="top">

<form method='post' action='<%=formUrl%>' id='ModelForm'
	name='ModelForm'>
	<div id="tabs1" >
	
	<input type="hidden" name="MODEL_ID" value="<%=modelId%>">
	<input type="hidden" name="ID" value="<%=parentId%>">
	

<div id="modelInstanceB" class="x-hide-display">	
<%

	String mandatory = "&nbsp;*";
 	if (messageIn != null
 			&& messageIn
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_NEW)) {
 		mandatory = "";
 %>

<div class="div_detail_area_forms" style="width: 670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.select.model" bundle="<%=messageBundle%>" /> </span></div>
 
<select class='portlet-form-field' name="KPI_MODEL_ID">
	<%
		List modelList = DetailModelInstanceUtil.getCandidateModelChildren(new Integer(modelId));
			Iterator itt = modelList.iterator();
			while (itt.hasNext()) {
				Model model = (Model) itt.next();
				String selected = "";
	%>
	<option value="<%=model.getId()%>"
		label="<%=StringEscapeUtils.escapeHtml(model.getName())%>" <%=selected%>><%=StringEscapeUtils.escapeHtml(model.getName())%>
	</option>
	<%
		}
	%>
</select>
</div>
<div class="div_detail_area_forms" style="width:670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.create.children" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<input type="checkbox" name="CREATE_CHILDREN" />
</div>
</div>

<%
	}
%>



<div class="div_detail_area_forms" style="width: 670;">

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.name" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="modelInstanceName" size="50"
	value="<%=StringEscapeUtils.escapeHtml(modelInstanceName)%>" maxlength="200"> <%=mandatory %></div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.description" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form' style='height: 150px;'>
	<textarea name="modelInstanceDescription" cols="40" style='height: 110px;' class='portlet-text-area-field'><%=StringEscapeUtils.escapeHtml(modelInstanceDescription)%></textarea>
</div>

<% if(parentId != null && modelId != null && parentId.equals(modelId)){ %>

<div class='div_detail_form'>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.start.date" bundle="<%=messageBundle%>" /> </span></div>
<input type='text' 
name='startDate' 
dojoType='dropdowndatepicker'
saveFormat='<%=DetailModelInstanceUtil.DATE_FORMAT %>'
displayFormat='<%= dateFormat %>'
widgetId='searchDate'
value='<%= StringEscapeUtils.escapeHtml(startDateS) %>'/> </div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.end.date" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<input type='text' 
name='endDate' 
dojoType='dropdowndatepicker'
saveFormat='<%=DetailModelInstanceUtil.DATE_FORMAT %>'
displayFormat='<%= dateFormat %>'
widgetId='searchDate'
value='<%= StringEscapeUtils.escapeHtml(endDateS) %>'/> </div>
<% } %>

</div>
</div>


<%
 	if (messageIn != null
 			&& messageIn
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
 %>
<div id="modelDefinitionB" class="x-hide-display">
<div class="div_detail_area_forms" style="width: 670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.name" bundle="<%=messageBundle%>" /> </span></div>
	
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="modelName" size="50"
	value="<%=StringEscapeUtils.escapeHtml(modelName)%>" maxlength="200" readonly disabled="disabled"></div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.description" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form' style='height: 150px;'>
	<textarea name="modelDescription" cols="40" style='height: 110px;' class='portlet-text-area-field' disabled="disabled"><%=StringEscapeUtils.escapeHtml(modelDescription)%></textarea>
</div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.code" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="modelCode" size="50"
	value="<%=StringEscapeUtils.escapeHtml(modelCode)%>" maxlength="200" readonly disabled="disabled"></div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.model.typeName" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>

<input class='portlet-form-input-field' type="text" name="typeName"
	size="50" value='<spagobi:message
	key='<%=StringEscapeUtils.escapeHtml(typeName)%>' bundle="<%=messageBundle2%>" />'
	maxlength="200" readonly disabled="disabled"></div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.model.typeDescription" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form' style='height: 150px;'>
	<textarea name="typeDescription" cols="40" style='height: 110px;' class='portlet-text-area-field' disabled="disabled"><spagobi:message key="<%=StringEscapeUtils.escapeHtml(typeDescription)%>" bundle="<%=messageBundle2%>" /> </textarea>
</div>
</div>
</div>

<div id="modelAttributeB" class="x-hide-display">
<div class="div_detail_area_forms" style="width: 670;">
<%
	List modelAttributesName = new ArrayList();
		for (int i = 0; attributeList != null
				&& i < attributeList.size(); i++) {
			String attributeName = "";
			String attributeValue = "";
			Integer attributeId;
			ModelAttribute modelAttribute = (ModelAttribute) attributeList
					.get(i);
			attributeName = modelAttribute.getName();
			attributeValue = modelAttribute.getValue();
			attributeId = modelAttribute.getId();
			modelAttributesName.add(attributeId);
%>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="<%=attributeName%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text"
	name='<%="M_ATTR" + attributeId.toString()%>' size="50"
	value="<%=StringEscapeUtils.escapeHtml(attributeValue)%>" maxlength="200" readonly disabled="disabled"></div>
<%
	}
%>
</div>
</div>

<div id="kpiInstanceB" class="x-hide-display">
<div class="div_detail_area_forms" style="width:670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.kpi.type" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>

<input type="radio" name="typeKpi" value="model" onClick="DisableFields('model')" <%=radioModelCheked %>>
	<span class="portlet-font"><spagobi:message
	key="sbi.kpi.label.kpi.modelUUIDType" bundle="<%=messageBundle%>" /></span>
</input>
<input type="radio" name="typeKpi" value="kpi" onClick="DisableFields('kpi')" <%=radioKpiCheked %> >
	<span class="portlet-font"><spagobi:message
	key="sbi.kpi.label.kpi.kpiType" bundle="<%=messageBundle%>" /></span>
</input>
</div>
</div>
<div id="kpi-div" class="div_detail_area_forms" style="width: 670;<%=displayKpiDiv%>">

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.kpi.name" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<select class='portlet-form-field' name="KPI_ID">
<%
 
	if(kpiId == null) { %>
		<option value="-1"
			label="" selected>
		</option>
<%	}
	else {  %>
		<option value="-1"
			label="" selected>
		</option>
<%	} %>

	<%
	List kpiList = DAOFactory.getKpiDAO().loadKpiList();
	for (java.util.Iterator iterator = kpiList.iterator(); iterator
			.hasNext();) {
		String selected = "";
		Kpi kpi = (Kpi) iterator.next();
		if(kpi.getKpiId().equals(kpiId)){
			selected = "selected";
		}
		else {
			selected = "";
		}
	%>
	<option value="<%=kpi.getKpiId()%>"
				label="<%=StringEscapeUtils.escapeHtml(kpi.getComboBoxLabel(true))%>"
				title="<%=StringEscapeUtils.escapeHtml(kpi.getComboBoxLabel(false))%>"
				 <%=selected%>><%=StringEscapeUtils.escapeHtml(kpi.getComboBoxLabel(true))%>
	</option>
	<%
	}
	%>

</select>
</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.restoreKpiValue" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<input type="checkbox" name="RESTORE_DEFAULT" >
</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.saveToKpiHistory" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<input type="checkbox" name="SAVE_TO_KPI_HISTORY" >
</div>

<%if (kpiId != null) {%>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.thresholdName" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
<select class='portlet-form-field' name="THRESHOLD_ID">

<%
	if(thresholdId == null) { %>
		<option value="-1"
			label="" selected>
		</option>
	<%	}
	else {  %>
		<option value="-1"
			label="" selected>
		</option>
<%	} %>

<%
	List thresholdList = DAOFactory.getThresholdDAO().loadThresholdList();
	for (java.util.Iterator iterator = thresholdList.iterator(); iterator
			.hasNext();) {
		Threshold threshold = (Threshold) iterator.next();
		String selected = "";
		if(threshold.getId().equals(thresholdId)){
			selected = "selected";
		}
		else {
			selected = "";
	}
	%>
	<option value="<%=threshold.getId()%>"
		label="[<%=StringEscapeUtils.escapeHtml(threshold.getCode())%>] <%=StringEscapeUtils.escapeHtml(threshold.getName())%>" <%=selected%>>[<%=StringEscapeUtils.escapeHtml(threshold.getCode())%>] <%=StringEscapeUtils.escapeHtml(threshold.getName())%>
	</option>
	<%
	}
	%>

</select>

</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.weight" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="weight"
	size="10" value="<%=StringEscapeUtils.escapeHtml(weight)%>" maxlength="200" ></div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.target" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="target"
	size="10" value="<%=StringEscapeUtils.escapeHtml(target)%>" maxlength="200" ></div>

<hr>


<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.chartType" bundle="<%=messageBundle%>" /> </span></div>

<div class='div_detail_form'>	
<select  class='portlet-form-field' name="CHART_TYPE_ID">

<%
	if(chartTypeId == null) { %>
		<option value="-1"
			label="" selected>
		</option>
<%	}
	else {  %>
		<option value="-1"
			label="" selected>
		</option>
<%	} %>

	<%
		List ChartType = DAOFactory.getDomainDAO()
					.loadListDomainsByType("KPI_CHART");
	for (Iterator iterator = ChartType.iterator(); iterator.hasNext();) {
		Domain domain = (Domain) iterator.next();
		String selected = "";
		if(domain.getValueId().equals(chartTypeId)){
			selected = "selected";
		}
		else {
			selected = "";
		}
	%>
	<option value="<%=domain.getValueId()%>"
		label="<%=StringEscapeUtils.escapeHtml(domain.getValueName())%>" <%=selected%>><%=StringEscapeUtils.escapeHtml(domain.getValueName())%>
	</option>
	<%
		}
	%>
</select>
</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.periodicity" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'>
	<select class='portlet-form-field' name="ID_KPI_PERIODICITY">
	<%
	if(periodicityId == null) { %>
		<option value="-1"
			label="" selected>
		</option>
<%	}
	else {  %>
		<option value="-1"
			label="" selected>
		</option>
<%	} %>
	
	<%
		List periodicityList = DAOFactory.getPeriodicityDAO().loadPeriodicityList();
		for (Iterator iterator = periodicityList.iterator(); iterator.hasNext();) {
		Periodicity periodicity = (Periodicity) iterator.next();
		String selected = "";
		if(periodicity.getIdKpiPeriodicity().equals(periodicityId)){
			selected = "selected";
		}
		else {
			selected = "";
		}
	%>
	<option value="<%=periodicity.getIdKpiPeriodicity()%>"
		label="<%=StringEscapeUtils.escapeHtml(periodicity.getName())%>" <%=selected%>><%=StringEscapeUtils.escapeHtml(periodicity.getName())%>
	</option>
	<%
		}
	%>
</select>
</div>

<%} %>
</div>

<div id="model-label-div" style="width:670;<%=displayModelInstanceDiv %>">
<div class="div_detail_area_forms" style="width:670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.kpi.modelUUID" bundle="<%=messageBundle%>" /></span></div>
<%
String sModelInstanceUUID ="";
if(modelInstanceUUID != null){
	sModelInstanceUUID = modelInstanceUUID;
} %>	
	
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="modelUUID" size="50"
	value="<%=sModelInstanceUUID %>" maxlength="200"></div>
</div>
</div>

</div>
<%
 	}
 %>
 
 <div id="labelB" class="x-hide-display">
 <div class="div_detail_area_forms" style="width: 670;">
 
 <%
	String readOnly ="";
 	if (messageIn != null
 			&& messageIn
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {
 		readOnly = "readonly";
 	}
 %>


<%
 if (messageIn != null
 			&& messageIn
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_NEW)) {
	 	modelInstanceLabel =  java.util.UUID.randomUUID().toString();
	}	 
%>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.label" bundle="<%=messageBundle%>" /> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="modelInstanceLabel" size="50"
	value="<%=StringEscapeUtils.escapeHtml(modelInstanceLabel)%>" maxlength="100" <%=readOnly %>>&nbsp;*</div>
 
</div>
</div>
</div>
</form>
</td>
</tr>
</table>

<spagobi:infoTag fileName="modelInstinfo" infoTitle="Model Instance Informations" buttonId="openInfo"/>
<spagobi:error />

<script type="text/javascript">

function DisableFields(type){
	if (type == 'model'){
		document.getElementById("kpi-div").style.display = "none";
		document.getElementById("model-label-div").style.display = "block";
		document.getElementById("kpi-value").value="";
	} 
	if (type == 'kpi'){
		document.getElementById("model-label-div").style.display = "none";
		document.getElementById("kpi-div").style.display = "block";
	}
}

Ext.onReady(function(){
    var tabs = new Ext.TabPanel({
        renderTo: 'tabs1',
        width:690,
        activeTab: 0,
        frame:true,
        defaults:{autoHeight: true},
        items:[
            {contentEl:'modelInstanceB', title: '<spagobi:message
            	key="sbi.kpi.modelInstanceDefinition.label" bundle="<%=messageBundle%>" /> '}
			<% if (messageIn != null
 			&& messageIn
 					.equalsIgnoreCase(DelegatedDetailService.DETAIL_SELECT)) {%>
 			,{contentEl:'kpiInstanceB', title: '<spagobi:message
 		        key="sbi.kpi.label.kpiInstance" bundle="<%=messageBundle%>" /> '}
        	,{contentEl:'modelDefinitionB', title: '<spagobi:message
            	key="sbi.kpi.label.sourcemodel.tab" bundle="<%=messageBundle%>" /> '}
			<% if (!(attributeList == null || attributeList.size() == 0 )) { %>
            ,{contentEl:'modelAttributeB', title: '<spagobi:message
            	key="sbi.kpi.model.attributes" bundle="<%=messageBundle%>" /> '}
			<%}%>
            
        	<%}%>
        	,{contentEl:'labelB', title: '<spagobi:message
            	key="sbi.kpi.label.advanced.tab" bundle="<%=messageBundle%>" /> '}
        ]
    });

});

</script>

<%
String dojoUrl = urlBuilder.getResourceLink(request,"/js/dojo/dojo.js");
%>
 
<script type='text/javascript' src='<%= dojoUrl %>'></script>


<script type='text/javascript'>
	dojo.require('dojo.widget.DropdownDatePicker');
</script>


<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>