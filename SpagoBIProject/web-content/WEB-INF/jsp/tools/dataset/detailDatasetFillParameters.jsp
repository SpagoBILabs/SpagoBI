<!--
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
-->


<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%@ page import="it.eng.spago.navigation.LightNavigationManager,
			java.util.List,
			java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>


   <!-- ERROR TAG --> 
	<spagobi:error/>

<%

	SourceBean detailMR = (SourceBean) aServiceResponse.getAttribute("DetailDatasetModule"); 
	List parametersToFill= (List)detailMR.getAttribute("parameters");	

	List profAttrToFill = (List)detailMR.getAttribute(SpagoBIConstants.PROFILE_ATTRIBUTES_TO_FILL);

    String modality = null;
	if (detailMR != null) modality = (String) detailMR.getAttribute("modality");
	if (modality == null) modality = (String) aSessionContainer.getAttribute(SpagoBIConstants.MODALITY);
  	String messagedet = "";
  	if (modality.equals(SpagoBIConstants.DETAIL_INS))
		messagedet = SpagoBIConstants.DETAIL_INS;
	else messagedet = SpagoBIConstants.DETAIL_MOD;
		
  	
  	
  	Map backUrlPars = new HashMap();
  	backUrlPars.put("PAGE", "DetailDatasetPage");
  	backUrlPars.put(SpagoBIConstants.MESSAGEDET, messagedet);
  	backUrlPars.put("modality", modality);
  	backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
  	backUrlPars.put(DetailDataSetModule.RETURN_FROM_TEST_MSG, "DO_NOT_SAVE");
    String backUrl = urlBuilder.getUrl(request, backUrlPars);
  	
    Map testUrlPars = new HashMap();
    testUrlPars.put("PAGE", "DetailDatasetPage");
    testUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
    testUrlPars.put(SpagoBIConstants.MESSAGEDET, DetailDataSetModule.TEST_AFTER_PARAMETERS_FILLED);
    String testUrl = urlBuilder.getUrl(request, testUrlPars);	
    
    %>


<%@page import="it.eng.spagobi.tools.dataset.bo.DataSetParameterItem"%>
<%@page import="it.eng.spagobi.tools.dataset.service.DetailDataSetModule"%>
<form id="formTest" method="post" action="<%=testUrl%>" >



<!-- TITLE -->

<table class='header-table-portlet-section'>		
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section'
		    style='vertical-align:middle;padding-left:5px;'>
			<spagobi:message key = "SBIDev.predLov.profileAttrToFill" />
		</td>
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		
		<td class='header-button-column-portlet-section' id='testButton'>
			<input type='image' class='header-button-image-portlet-section' id='testButtonImage'
					name="testLovBeforeSave" value="testLovBeforeSave" 
					src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/test.png", currTheme)%>' 
					title='<spagobi:message key = "SBIDev.predLov.TestBeforeSaveLbl" />'  
					alt='<spagobi:message key = "SBIDev.predLov.TestBeforeSaveLbl" />' 
			/>
		</td>
		
		<td class='header-empty-column-portlet-section'>&nbsp;</td>
		
		<td class='header-button-column-portlet-section'>
			<a href="<%=backUrl%>"> 
      				<img class='header-button-image-portlet-section' 
      				     title='<spagobi:message key = "SBISet.Funct.backButt" />' 
      				     src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme)%>' 
      				     alt='<spagobi:message key = "SBISet.Funct.backButt" />' />
			</a>
		</td>
	</tr>
</table>




<!-- BODY -->


<div style="padding-left:10px;" class='div_background_no_img' >

		
		<div class="div_detail_area_forms" >
		
		  <div class='portlet-form-field-label' >
		     <spagobi:message key = "SBIDev.DataSet.needPars" /> 
			 <br/>
			 <spagobi:message key = "SBIDev.DataSet.assignValToPars" /> 			
		  </div> 
		  
		  <br/>
		  <br/>
		
		<%
			Iterator iterProfAttr = parametersToFill.iterator();
		    while(iterProfAttr.hasNext()) {
		    	DataSetParameterItem item=(DataSetParameterItem)iterProfAttr.next();
		    	String name = item.getName();
		    	String type= item.getType();
		%>
			<div class='div_detail_label' style="width:200px;">
				<span class='portlet-form-field-label'>
					<%= name %>
				</span>
			</div>
			<div class='div_detail_form'>
				<input class='portlet-form-input-field' type="text" name="<%=name%>att" size="35" >
	    		&nbsp;*
			</div>
		<%    	
		    }
		%>
		</div>
		
		<br/>

</div>
				
</form>


