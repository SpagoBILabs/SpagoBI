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

<%@ page import="java.util.Map,java.util.HashMap"%>
<%@ page	import="it.eng.spago.dispatching.service.detail.impl.DelegatedDetailService"%>
<%@ page import="it.eng.spagobi.kpi.config.bo.Kpi"%>
<%@page import="it.eng.spagobi.commons.bo.Domain"%>
<%
	String title = "";
	String id = "";
	String name = "";
	String documentLabel = "";
	String code = "";
	String metric = "";
	String description = "";
	String weight = "";
	Integer ds_id = null;
	Integer threshold_id = null;
	
	Integer kpiTypeId = null;
	Integer metricScaleId = null;
	Integer measureTypeId = null;
	
	String interpretation = "";
	String inputAttribute = "";  
	String modelReference = "";
	String targetAudience = "";  

    String messageBunle = "component_kpi_messages"; 

    ConfigSingleton configure = ConfigSingleton.getInstance();
	SourceBean moduleBean = (SourceBean) configure
			.getFilteredSourceBeanAttribute("MODULES.MODULE", "NAME",
					"DetailKpiModule");
	
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
		.getAttribute("DetailKpiModule");
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
				.getAttribute("DetailKpiModule");
		Kpi kpi = (Kpi) moduleResponse.getAttribute("KPI");
		if(kpi.getKpiId() != null) {
			id = kpi.getKpiId().toString();
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
				.getAttribute("DetailKpiModule");
		Kpi kpi = (Kpi) moduleResponse.getAttribute("KPI");
		if (kpi != null) {
			if(kpi.getKpiId()!=null)
				id = kpi.getKpiId().toString();
			if(kpi.getKpiName()!= null)
				name = kpi.getKpiName();
			if(documentLabel!= null)
				documentLabel = kpi.getDocumentLabel();
			if(code!=null)
				code = kpi.getCode();
			if(metric != null)
				metric = kpi.getMetric();
			if(description != null)
				description = kpi.getDescription();
			if (kpi.getStandardWeight() != null)
				weight = kpi.getStandardWeight().toString();
			if(kpi.getKpiDsId()!=null)
				ds_id = kpi.getKpiDsId();
			else
				ds_id = null;
			if (kpi.getThreshold()!=null)
				threshold_id = kpi.getThreshold().getId();
			else
				threshold_id = null;
			if (kpi.getKpiTypeId()!= null)
				kpiTypeId = kpi.getKpiTypeId();
			if (kpi.getMetricScaleId()!=null)
				metricScaleId = kpi.getMetricScaleId();
			if (kpi.getMeasureTypeId() != null)
				measureTypeId = kpi.getMeasureTypeId();
			
			if (kpi.getInterpretation() != null)
				interpretation = kpi.getInterpretation();
			if (kpi.getInputAttribute() != null)
				inputAttribute = kpi.getInputAttribute();
			if (kpi.getModelReference() != null)
				modelReference = kpi.getModelReference();
			if (kpi.getTargetAudience() != null)
				targetAudience = kpi.getTargetAudience();
		}
	}
	
	
	Map formUrlPars = new HashMap();
//	if(ChannelUtilities.isPortletRunning()) {
		formUrlPars.put("PAGE", "KpiPage");
		formUrlPars.put("MODULE", "DetailKpiModule");
		formUrlPars.put("MESSAGE", messageSave);
		formUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
//	}
	
	String formUrl = urlBuilder.getUrl(request, formUrlPars);

	
	
	
	Map backUrlPars = new HashMap();
	backUrlPars.put("PAGE", "KpiPage");
	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
	String backUrl = urlBuilder.getUrl(request, backUrlPars);
%>

<%@page import="it.eng.spago.navigation.LightNavigationManager"%>

<%@page import="java.util.List"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.tools.dataset.bo.IDataSet"%> 
<%@page import="it.eng.spagobi.kpi.threshold.bo.Threshold"%><table class='header-table-portlet-section'>

	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
			style='vertical-align: middle; padding-left: 5px;'>
			<spagobi:message key="<%=title%>" bundle="<%=messageBunle%>" /></td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		<td class='header-button-column-portlet-section'>
			<a href='#' id="openInfo"> 
			<img class='header-button-image-portlet-section'
			width='22px'
			height='22px'
			src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/info22.png",currTheme)%>'
			name='info'
			alt='<spagobi:message key = "sbi.kpi.button.info.title" bundle="<%=messageBunle%>"/>'
			title='<spagobi:message key = "sbi.kpi.button.info.title" bundle="<%=messageBunle%>"/>'/>
			</a>
		</td>
		
		
		<td class='header-button-column-portlet-section'><a
			href="javascript:document.getElementById('kpiForm').submit()"> <img
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

<form method='POST' action='<%=formUrl%>' id='kpiForm' name='kpiForm'>
<input type='hidden' value='<%=id%>' name='id' />


<div id="tabs1">
<div id="tab1" class="x-hide-display">
<div class="div_detail_area_forms" style="width: 670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.name" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form'><input
	class='portlet-form-input-field' type="text" name="name" size="50"
	value="<%=StringEscapeUtils.escapeHtml(name)%>" maxlength="200"> &nbsp;*</div>
	
	<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.description" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style='height: 150px;'>
<textarea id="description" name="description" cols="40" style='height: 110px;' class='portlet-text-area-field'><%=StringEscapeUtils.escapeHtml(description)%></textarea>
</div>

<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.code" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form'><input
  class='portlet-form-input-field' type="text" name="code" size="50"
  value="<%=StringEscapeUtils.escapeHtml(code)%>" maxlength="200">&nbsp;*</div>

<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.metric" bundle="<%=messageBunle%>"/> </span></div>
<div style='height: 150px;' class='div_detail_form'><textarea
  style='height: 110px;' class='portlet-text-area-field' name='metric'
  cols='40'>
<%=metric%></textarea></div>

<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.kpi.interpretation" bundle="<%=messageBunle%>"/> </span></div>
<div style='height: 150px;' class='div_detail_form'>
<textarea
  style='height: 110px;' class='portlet-text-area-field' name='interpretation'
  cols='40'>
<%=interpretation%></textarea></div>


<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.weight" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;"><input
  class='portlet-form-input-field' type="text" name="weight" size="50"
  value="<%=StringEscapeUtils.escapeHtml(weight)%>" maxlength="200" ></div>
  
  <div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.thresholdName" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;">
<select class='portlet-form-field' name="threshold_id" >
<option value="" label=""></option>

<%
	List thresholds = DAOFactory.getThresholdDAO().loadThresholdList();
	Iterator thresholdsIt = thresholds.iterator();
	while (thresholdsIt.hasNext()){
		Threshold threshold = (Threshold)thresholdsIt.next();
		String selected = "";
		String label = "[" + threshold.getCode() + "] "+ threshold.getName();
		
		if (threshold_id != null && threshold_id.equals(threshold.getId())) {
			selected = "selected='selected'";
		}
		%>    			 		
		<option value="<%= threshold.getId() %>" label="<%= StringEscapeUtils.escapeHtml(label) %>" <%= selected %>>
			<%= label %>	
		</option>
		<%
	}
%>

</select>
</div>


<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.documentLabel" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form'>
<select class='portlet-form-field' name="document_label" >
<option value="" label=""></option>
<%
	List sbiDocs = DAOFactory.getBIObjectDAO().loadAllBIObjects();
	Iterator sbiDocsIt = sbiDocs.iterator();
	while (sbiDocsIt.hasNext()){
		BIObject bio = (BIObject)sbiDocsIt.next();
		String selected = "";
		if (documentLabel!=null && documentLabel.equals(bio.getLabel())) {
			selected = "selected='selected'";										
		}	
		%>    			 		
		<option value="<%= StringEscapeUtils.escapeHtml(bio.getLabel()) %>" label="<%= StringEscapeUtils.escapeHtml(bio.getLabel()) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(bio.getLabel()) %>	
		</option>
		<%
	}
%>
</select>
</div>


<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.dataSet" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;">
<select class='portlet-form-field' name="ds_id" >
<%if(ds_id == null) { %>
		<option value="-1"
			label="" selected>
		</option>
	<%	}
	else {  %>
	<option value="-1"
		label="" selected>
	</option>
	<%	} 

	List dataSets = DAOFactory.getDataSetDAO().loadAllDataSets();
	Iterator dataSetsIt = dataSets.iterator();
	while (dataSetsIt.hasNext()){
		IDataSet dataSet = (IDataSet)dataSetsIt.next();
		String selected = "";
		if (ds_id != null && ds_id.intValue() == dataSet.getId()) {
			selected = "selected='selected'";
		}
		%>
		    			 		
		<option value="<%= dataSet.getId() %>" label="<%= StringEscapeUtils.escapeHtml(dataSet.getLabel()) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(dataSet.getLabel()) %>	
		</option>
		<%
	}
%>

</select>
</div>
</div>
</div>

<div id="tab2" class="x-hide-display">
<div class="div_detail_area_forms" style="width: 670;">
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.kpi.type" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;">
<select class='portlet-form-field' name="kpi_type_id" >
<%
	if(kpiTypeId == null) { %>
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
	List kpiTypeLevels = DAOFactory.getDomainDAO().loadListDomainsByType("KPI_TYPE");
	Iterator itKpiType = kpiTypeLevels.iterator();
	while (itKpiType.hasNext()){
		Domain domain = (Domain)itKpiType.next();
		String selected = "";
		if (kpiTypeId != null && kpiTypeId.intValue() == domain.getValueId().intValue()){
			selected = "selected='selected'";		
		}
		%>    			 		
		<option value="<%= domain.getValueId() %>" label="<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>	
		</option>
		<%
	}
%>
</select>


</div>

<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.metric.scale.type" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;">
<select class='portlet-form-field' name="metric_scale_type_id" >
<%
	if(metricScaleId == null) { %>
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
	List MetricScaleTypeLevels = DAOFactory.getDomainDAO().loadListDomainsByType("METRIC_SCALE_TYPE");
	Iterator itMetricScaleType = MetricScaleTypeLevels.iterator();
	while (itMetricScaleType.hasNext()){
		Domain domain = (Domain)itMetricScaleType.next();
		String selected = "";
		if (metricScaleId != null && metricScaleId.intValue() == domain.getValueId().intValue()){
			selected = "selected='selected'";		
		}
		%>    			 		
		<option value="<%= domain.getValueId() %>" label="<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>	
		</option>
		<%
	}
%>

</select>
</div>
<div class='div_detail_label'><span
	class='portlet-form-field-label'> <spagobi:message
	key="sbi.kpi.label.measure.type" bundle="<%=messageBunle%>"/> </span></div>
<div class='div_detail_form' style="height:40px;">
<select class='portlet-form-field' name="mesure_type_id" >
<%
	if(measureTypeId == null) { %>
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
	List MeasureTypeLevels = DAOFactory.getDomainDAO().loadListDomainsByType("MEASURE_TYPE");
	Iterator itMeasureType = MeasureTypeLevels.iterator();
	while (itMeasureType.hasNext()){
		Domain domain = (Domain)itMeasureType.next();
		String selected = "";
		if (measureTypeId != null && measureTypeId.intValue() == domain.getValueId().intValue()){
			selected = "selected='selected'";		
		}
		%>    			 		
		<option value="<%= domain.getValueId() %>" label="<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>" <%= selected %>>
			<%= StringEscapeUtils.escapeHtml(domain.getValueName()) %>	
		</option>
		<%
	}
%>
</select>
</div>

<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.input.attribute" bundle="<%=messageBunle%>"/> </span></div>
<div style='height: 150px;' class='div_detail_form'>
<textarea
  style='height: 110px;' class='portlet-text-area-field' name='inputAttribute'
  cols='40'>
<%=inputAttribute%></textarea></div>

<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.model.reference" bundle="<%=messageBunle%>"/> </span></div>
<div style='height: 150px;' class='div_detail_form'>
<textarea
  style='height: 110px;' class='portlet-text-area-field' name='modelReference'
  cols='40'>
<%=StringEscapeUtils.escapeHtml(modelReference)%></textarea></div>


<div class='div_detail_label'><span
  class='portlet-form-field-label'> <spagobi:message
  key="sbi.kpi.label.target.audience" bundle="<%=messageBunle%>"/> </span></div>
<div style='height: 150px;' class='div_detail_form'>
<textarea
  style='height: 110px;' class='portlet-text-area-field' name='targetAudience'
  cols='40'>
<%=StringEscapeUtils.escapeHtml(targetAudience)%></textarea></div>

</div> 
</div>
</div>


</form>


<spagobi:error/>
 
<spagobi:infoTag fileName="kpiwizzardinfo" infoTitle="Kpi informations" buttonId="openInfo"/>
  
<script type="text/javascript">

Ext.onReady(function(){
    var tabs = new Ext.TabPanel({
        renderTo: 'tabs1',
        width:690,
        activeTab: 0,
        frame:true,
        defaults:{autoHeight: true},
        items:[
            {contentEl:'tab1', title: '<spagobi:message key="sbi.kpi.label.main.tab" bundle="<%=messageBunle%>"/>'},
            {contentEl:'tab2', title: '<spagobi:message key="sbi.kpi.label.advanced.tab" bundle="<%=messageBunle%>"/>'}
        ]
    });

});

</script>
 
<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>