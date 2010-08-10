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
	
	<%@ page import="it.eng.spagobi.tools.datasource.bo.DataSource,
	 				         it.eng.spago.navigation.LightNavigationManager,
	 				         java.util.Map,java.util.HashMap,java.util.List,
	 				         java.util.Iterator,
	 				         it.eng.spagobi.commons.bo.Domain,
	 				         it.eng.spagobi.services.dataset.bo.SpagoBiDataSet,
	 				         it.eng.spagobi.tools.dataset.bo.*,
	 				         it.eng.spagobi.tools.dataset.service.DetailDataSetModule" %>
<%@page import="it.eng.spagobi.utilities.scripting.ScriptManager"%>
<%@page import="it.eng.spagobi.utilities.scripting.ScriptUtilities"%>
<%@page import="it.eng.spagobi.commons.utilities.SpagoBIUtilities"%>
<%@page import="java.io.File"%>
<script type="text/javascript" src="<%=linkProto%>"></script>
	 				         
	 				         
	<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
	
	<%
		SourceBean moduleResponse = (SourceBean)aServiceResponse.getAttribute("DetailDataSetModule"); 
		SpagoBiDataSet ds = (SpagoBiDataSet)moduleResponse.getAttribute(DetailDataSetModule.DATASET);
		if(ds==null) ds = (SpagoBiDataSet)session.getAttribute(DetailDataSetModule.DATASET); 
		if(ds==null)  ds=new SpagoBiDataSet();
		
		List listTransformerType = (List) moduleResponse.getAttribute(DetailDataSetModule.LIST_TRANSFORMER);
		String message=(String)aServiceRequest.getAttribute("MESSAGEDET");
		String modality = (String)moduleResponse.getAttribute(SpagoBIConstants.MODALITY);
		if(modality==null) modality=SpagoBIConstants.DETAIL_NEW;
		String subMessageDet = ((String)moduleResponse.getAttribute("SUBMESSAGEDET")==null)?"":(String)moduleResponse.getAttribute("SUBMESSAGEDET");
		String msgWarningSave = msgBuilder.getMessage("8002", request);
		
		Map formUrlPars = new HashMap();
		if(ChannelUtilities.isPortletRunning()) {
			formUrlPars.put("PAGE", "DetailDataSetPage");	
  			formUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");	
		}
		String formUrl = urlBuilder.getUrl(request, formUrlPars);
		
		Map backUrlPars = new HashMap();
		//backUrlPars.put("PAGE", "detailMapPage");
		backUrlPars.put("PAGE", "ListDataSetPage");
	    backUrlPars.put("MESSAGEDET", "EXIT_FROM_DETAIL");
		backUrlPars.put(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
		String backUrl = urlBuilder.getUrl(request, backUrlPars);		
	%>
	
	

<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>

<script type="text/javascript" src="<%=linkProto%>"></script>
<script type="text/javascript" src="<%=linkProtoWin%>"></script>
<script type="text/javascript" src="<%=linkProtoEff%>"></script>
<link href="<%=linkProtoDefThem%>" rel="stylesheet" type="text/css"/>
<link href="<%=linkProtoAlphaThem%>" rel="stylesheet" type="text/css"/>



<form method='POST' action='<%=formUrl%>' id='dsForm' name='dsForm' enctype="multipart/form-data" >

	<% if(ChannelUtilities.isWebRunning()) { %>
		<input type='hidden' name='PAGE' value='DetailDataSetPage' />
		<input type='hidden' name='<%=LightNavigationManager.LIGHT_NAVIGATOR_DISABLED%>' value='true' />
	<% } %>

	<input type='hidden' value='<%=modality%>' name='MESSAGEDET' />	
	<input type='hidden' value='<%=subMessageDet%>' name='SUBMESSAGEDET' />
	<input type='hidden' value='<%=ds.getDsId()%>' name='id' />
	<input type='hidden' name='parametersXMLModified' value='' id='parametersXMLModified' />
	
	
	<table width="100%" cellspacing="0" border="0" class='header-table-portlet-section'>		
		<tr class='header-row-portlet-section'>
			<td class='header-title-column-portlet-section' 
			    style='vertical-align:middle;padding-left:5px;'>
				<spagobi:message key = "SBISet.ListDataSet.TitleDetail"  />
			</td>
			<td class='header-button-column-portlet-section' id='testButton'>
			<input type='image' class='header-button-image-portlet-section' id='testButtonImage'
						onclick='setParametersXMLModifiedField();'
						name="testDataSetBeforeSave" value="testDataSetBeforeSave"  
						src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/test.png", currTheme)%>' 
						title='<spagobi:message key = "SBIDev.DetailDataSet.TestBeforeSaveLbl" />'  
						alt='<spagobi:message key = "SBIDev.DetailDataSet.TestBeforeSaveLbl" />' 
		/>
		</td>
			<td class='header-button-column-portlet-section'>
				<a href="javascript:saveDS('SAVE')"> 
	      			<img class='header-button-image-portlet-section' 
	      				 title='<spagobi:message key = "SBISet.ListDataSet.saveButton" />' 
	      				 src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/save.png", currTheme)%>' 
	      				 alt='<spagobi:message key = "SBISet.ListDataSet.saveButton"/>' 
	      			/> 
				</a>
			</td>		 
			<td class='header-button-column-portlet-section'>
				<input type='image' name='saveAndGoBack' id='saveAndGoBack' onClick="javascript:saveDS('SAVEBACK')" class='header-button-image-portlet-section'
				       src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/saveAndGoBack.png", currTheme)%>' 
      				   title='<spagobi:message key = "SBISet.ListDataSet.saveBackButton" />'  
                       alt='<spagobi:message key = "SBISet.ListDataSet.saveBackButton" />' 
			   /> 

			</td>
			<td class='header-button-column-portlet-section'>
				<a href='javascript:goBack("<%=msgWarningSave%>", "<%=backUrl%>")'> 
	      			<img class='header-button-image-portlet-section' 
	      				 title='<spagobi:message key = "SBISet.ListDataSet.backButton"  />' 
	      				 src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme)%>' 
	      				 alt='<spagobi:message key = "SBISet.ListDataSet.backButton" />' 
	      			/>
				</a>
			</td>		
		</tr>
	</table>
	
			<spagobi:error/>
	
				<% 
				
			
				String disableFile="disabled";
				String disableQuery="disabled";
				String disableWs="disabled";
				String disableScript="disabled";
				String disableLanguageScript="disabled";				
				String disableJClass="disabled";
			
				String hideFile="style=\"display: none;\"";
				String hideQuery="style=\"display: none;\"";
				String hideWs="style=\"display: none;\"";
				String hideScript="style=\"display: none;\"";
				String hideLanguageScript="style=\"display: none;\"";
				String hideJClass="style=\"display: none;\"";
				
		
				String type="";
				
   	       if(FileDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
					type="file";
					disableFile="";
					hideFile="";
   	       		}
				else if(JDBCDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
					type="query";
					disableQuery="";
					hideQuery="";
				} 
				else if(WebServiceDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
					type="ws";
					disableWs="";
					hideWs="";
				
				} 
				else if(ScriptDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
					type="script";
					disableScript="";
					disableLanguageScript="";
					hideScript="";
					hideLanguageScript="";
				
				} 
				else if(JavaClassDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
					type="javaclass";
					disableJClass="";
					hideJClass="";
				
				} 
   	       
			String datasetDisplay = "none";
			 
			DataSetParametersList dataSetParametersList=null;
			 if(type.equals("query") ||
					 type.equals("script") ||
					 type.equals("javaclass") ||
					 type.equals("ws")
			 ){
					dataSetParametersList = new DataSetParametersList();
						datasetDisplay = "inline";
						String parametersXML = ds.getParameters();
					  	if (parametersXML != null  &&  !parametersXML.equals("")){
					  		dataSetParametersList = DataSetParametersList.fromXML(parametersXML);
					}
				 
				 }
      	            	     
      	            	     
      	            	     
      	            	     %> 
	
	<div class='div_background' style='padding-top:5px;padding-left:5px;'>
	
	<table width="100%" cellspacing="0" border="0" id = "fieldsTable" >
	<tr>
	  <td>
		<div class='div_detail_label'>
			<span class='portlet-form-field-label'>
				<spagobi:message key = "SBISet.ListDataSet.columnLabel" />
			</span>
		</div>
		<%
			  String isReadonly = "";
			  if (modality.equalsIgnoreCase("DETAIL_MOD")){
			  		isReadonly = "readonly";
			  }
			  String label = ds.getLabel();
			   if((label==null) || (label.equalsIgnoreCase("null"))  ) {
				   label = "";
			   }
		%>
		<div class='div_detail_form'>
			<input class='portlet-form-input-field' type="text" <%=isReadonly %> id="LABEL"s
				   name="LABEL" size="50" value="<%=StringEscapeUtils.escapeHtml(label)%>" maxlength="50" />
			&nbsp;*
		</div>
		<div class='div_detail_label'>
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.columnName" />
			</span>
		</div>
		<div class='div_detail_form'>
		<%
			   String name = ds.getName();
			   if((name==null) || (name.equalsIgnoreCase("null"))  ) {
			   	   name = "";
			   }
		%>
			<input class='portlet-form-input-field' type="text" name="NAME"  id="NAME"
				   size="50" value="<%=StringEscapeUtils.escapeHtml(name)%>" maxlength="160" />
				   		&nbsp;*
		</div>
			
		<div class='div_detail_label'>
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.columnDescr" />
			</span>
		</div>
		<div class='div_detail_form'>
		<%
			   String desc = ds.getDescription();
			   if((desc==null) || (desc.equalsIgnoreCase("null"))  ) {
			   	   desc = "";
			   }
		%>
			<input class='portlet-form-input-field' type="text" name="DESCR" 
				   size="50" value="<%= StringEscapeUtils.escapeHtml(desc) %>" maxlength="160" />
		</div>
			   <!-- transformation type combo -->
	 
	   <div class='div_detail_label'>
				<span class='portlet-form-field-label'>
					<spagobi:message key = "SBISet.ListDataSet.transformer" />
				</span>
		</div>
	   <div class='div_detail_form'>
      		<select class='portlet-form-input-field' style='width:250px;' name="TRANSFORMERNAME" id="TRANSFORMERNAME" onchange="javascript:EnableTransformerDiv(this.value)">
				<option value="">&nbsp;</option>
				
			<% String hideTrasnformer="style=\"display: none;\""; 
			if (ds.getTransformerId()!=null) hideTrasnformer="style=\"display: inline;\"";
			if (listTransformerType != null){
				Iterator iterTransformer= listTransformerType.iterator();
			   
      			while(iterTransformer.hasNext()) {
      				Domain transformer = (Domain)iterTransformer.next();
      				Integer objTransformer = ds.getTransformerId();
      				Integer currTransformer = transformer.getValueId();
                    boolean isTransformer = false;
      		    	if(objTransformer != null && objTransformer.intValue() == currTransformer.intValue()){
      		    		isTransformer = true;   
      		    	}
      		%>
      			<option value="<%=transformer.getValueCd()%>"  <%if(isTransformer) out.print(" selected='selected' ");  %>><%=StringEscapeUtils.escapeHtml(transformer.getTranslatedValueName(locale))%></option>
      		<% 	
      			}
			}
      		%>
      		</select>
		</div> 
		<div class='div_detail_form' id='transformer_pivot' <%=hideTrasnformer%>>
			<%  
				   String pivotColumnName = ds.getPivotColumnName();
				   if((pivotColumnName==null) || (pivotColumnName.equalsIgnoreCase(""))  ) {
					   pivotColumnName = "";
				   }	
				   String pivotRowName  = ds.getPivotRowName();
				   if((pivotRowName==null) || (pivotRowName.equalsIgnoreCase(""))  ) {
					   pivotRowName = "";
				   }	
				   String pivotColumnValue  = ds.getPivotColumnValue();
				   if((pivotColumnValue==null) || (pivotColumnValue.equalsIgnoreCase(""))  ) {
					   pivotColumnValue = "";
				   }
				   boolean pivotNumRows  = ds.isNumRows();
				   String strChecked = "";  
				   if(!pivotNumRows ) {
					   strChecked ="";
				   }
				   else{
					   strChecked ="checked='checked'";
				   }
			%>
				<span class='portlet-form-field-label'>	
					<spagobi:message key = "SBISet.ListDataSet.pivotColumn" />
				</span>
				<input class='portlet-form-input-field' type="text" name="PIVOTCOLUMNNAME" 
					   size="25" value="<%= StringEscapeUtils.escapeHtml(pivotColumnName) %>" maxlength="50" />
			    <span class='portlet-form-field-label'>	
					<spagobi:message key = "SBISet.ListDataSet.pivotRow" />
				</span>
				<input class='portlet-form-input-field' type="text" name="PIVOTROWNAME" 
					   size="25" value="<%= StringEscapeUtils.escapeHtml(pivotRowName) %>" maxlength="50" />
			   <span class='portlet-form-field-label'>	
					<spagobi:message key = "SBISet.ListDataSet.pivotValue" />
				</span>
				<input class='portlet-form-input-field' type="text" name="PIVOTCOLUMNVALUE" 
					   size="25" value="<%= StringEscapeUtils.escapeHtml(pivotColumnValue) %>" maxlength="50" />
				<span class='portlet-form-field-label'>	
					<spagobi:message key = "SBISet.ListDataSet.numRows" />
				</span>
				<input class='portlet-form-input-field' name="PIVOTNUMROWS" value="true" 
					   type="checkbox" <%=strChecked%> />
					
		</div>
		
					
					<%	
			if(message.equalsIgnoreCase("DETAIL_SELECT") || message.equalsIgnoreCase("DETAIL_MOD")){ 
		String mess="";
			     if(type.equals("file")){
						mess="0";
				}
				else if(type.equals("query")){
						mess="1";
				} 
				else if(type.equals("ws")){
						mess="2";
				} 
				else if(type.equals("script")){
						mess="3";
				} 
				else if(type.equals("javaclass")){
						mess="4";
				} 
			     

		%>
			   	<input type="hidden" name="typeDataSet" value="<%=mess%>"/>
			   	
			   	<div class='div_detail_label'>
					<span class='portlet-form-field-label'>
						<spagobi:message key = "SBISet.ListDS.TypeDs" />
					</span>
				</div>	
				    <div class='div_detail_form'>		
						<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;font-size: 8pt;"><%=type%></span>
				</div>
				
			<%}
			else
				{%>
				<br>
				<br>
   	<div class='div_detail_label'>
			<span class='portlet-form-field-label'>
				<spagobi:message key = "SBISet.ListDS.TypeDs" />
			</span>
		</div>
		<div class='div_detail_form'>

      	   	
      	   	<input type="radio" name="typeDataSet" value="0" <% if(type.equalsIgnoreCase("file")) { out.println(" checked='checked' "); } %> onClick="DisableFields('file')">
					<span class="portlet-font"><spagobi:message key = "SBISet.ListDataSet.fileType" /></span>
			</input>
      	   	<input type="radio" name="typeDataSet" value="1" <% if(type.equalsIgnoreCase("query")) { out.println(" checked='checked' "); } %> onClick="DisableFields('query')">
					<span class="portlet-font"><spagobi:message key = "SBISet.ListDataSet.queryType" /></span>
			</input>
			<input type="radio" name="typeDataSet" value="2" <% if(type.equalsIgnoreCase("ws")) { out.println(" checked='checked' "); } %> onClick="DisableFields('ws')">
					<span class="portlet-font"><spagobi:message key = "SBISet.ListDataSet.wsType" /></span> 
			</input>
			<input type="radio" name="typeDataSet" value="3" <% if(type.equalsIgnoreCase("script")) { out.println(" checked='checked' "); } %> onClick="DisableFields('script')">
					<span class="portlet-font"><spagobi:message key = "SBISet.ListDataSet.scriptType" /></span> 
			</input>
			<input type="radio" name="typeDataSet" value="4" <% if(type.equalsIgnoreCase("javaclass")) { out.println(" checked='checked' "); } %> onClick="DisableFields('javaclass')">
					<span class="portlet-font"><spagobi:message key = "SBISet.ListDataSet.jClassType" /></span> 
			</input>
		</div>
		<%} %>



<!--  FILE SECTION -->
<%boolean hasFile=false;
if(ds.getFileName()!=null)hasFile=true; %>
	<div id="filecontainer" <%=hideFile%>>
   	<div class='div_detail_label' id="WHAT_FILE">
			<span class='portlet-form-field-label'>
				<spagobi:message key = "SBIDev.DetailDataSet.getFile" />
			</span>
		</div>
		<div class='div_detail_form'>
			<input type="radio" name="howGetFile" value="0" checked onclick="changeFileFields('exists')">
					<span class="portlet-font">
						<spagobi:message key = "SBIDev.DetailDataSet.ChooseExistingFile" />
					</span> 
			</input>
			<input type="radio" name="howGetFile" value="1" onClick="changeFileFields('new')">
					<span class="portlet-font">
						<spagobi:message key = "SBIDev.DetailDataSet.ChooseUploadNewFile" />
					</span> 
			</input>
		</div>

		<div class='div_detail_label' id="FILENAMELABEL" >
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.fileName" />
			</span>
		</div>
	    <div class='div_detail_form'>
		<%
			   String fileName =""; 
		       if(FileDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
			   	fileName = ds.getFileName();
		       }
			   if((fileName==null) || (fileName.equalsIgnoreCase("null"))  ) {
				   fileName = "";
			   }
		%>
		<%  // Get all files in resource dir!
			ConfigSingleton configSingleton = ConfigSingleton.getInstance();
			SourceBean sb = (SourceBean)configSingleton.getAttribute("SPAGOBI.RESOURCE_PATH_JNDI_NAME");
			String pathh = (String) sb.getCharacters();
			String filePath= SpagoBIUtilities.readJndiResource(pathh);
			filePath += "/dataset/files";
			File dir=new File(filePath);
    
   			String[] children = dir.list();
   			if (children == null) {
				children=new String[0];
			} else {
        		for (int i=0; i<children.length; i++) {
            	// Get filename of file or directory
            	String filename = children[i];
			        }
    		}
			int i=0;
%>

				<select class='portlet-form-input-field' style='width:230px;' 
							name="FILENAME" id="FILENAME" <%=disableFile%>>
		      		<% 
					for(int ind=0;ind<children.length;ind++){
						String file=children[ind];
		      			%>
		      			<option value="<%=file%>"<%if(file.equals(fileName)) out.print(" selected='selected' "); %>><%=file%>
		      			</option>
		      		<% 
		      		    }
		      		%>
				</select>
	   	</div>
	   				<!-- DISPLAY FORM FOR NEW FILES UPLOAD -->
				<div id="FILE_UPLOAD">
					<div class='div_detail_label'>
						<span class='portlet-form-field-label'>
							<spagobi:message key = "SBIDev.DetailDataSet.UploadNewFile" />
						</span>
					</div>
					<div class='div_detail_form'>
						<input class='portlet-form-input-field' type="file" disabled
			      		       name="UPLOAD_FILE" id="UPLOAD_FILE" />
			<span class='portlet-form-field-label'>
				<spagobi:message key = "SBIDev.DetailDataSet.saveBeforeTest" />
			</span>		
					</div>
				</div>	
	   </div>

	   
<script type="text/javascript">
<!--
function changeFileFields(message){
	var divNew=document.getElementById("UPLOAD_FILE");
	var divEx=document.getElementById("FILENAME");
	if(message=='new'){
			divEx.disabled=true;	
			divNew.disabled=false;	
		}
	if(message=="exists"){
			divEx.disabled=false;	
			divNew.disabled=true;			
	}	
}
-->
</script>

	   
	   
	   
	   <div id="querycontainer" <%=hideQuery%>>
	   		<div class='div_detail_label' id="QUERYLABEL">
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.query" />
			</span>
		</div>
	    <div style="height:150px;">
		<%
			   String query =""; 
				if(JDBCDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){		
					query=ds.getQuery();
					}
			   if((query==null) || (query.equalsIgnoreCase("null"))  ) {
				   query = "";
			   }
		%>
		
		<textarea id="QUERY" rows="8" cols="80" name="QUERY" style="font-size:9pt" <%=disableQuery%>><%=StringEscapeUtils.escapeHtml(query)%></textarea>
		<BR>
	   
	   </div>
	   
	   	<div class='div_detail_label' id="DATASOURCELABEL">
			<span class='portlet-form-field-label'>
				<spagobi:message key = "SBISet.eng.dataSource" />
			</span>
		</div>	
	<div class='div_detail_form'>
		<select class='portlet-form-field' name="DATASOURCE" onchange= "changeEngineType(this.options[this.selectedIndex].label)" id="DATASOURCE" <%=disableQuery%> >			
			<option></option>
			<%

			java.util.List dataSources = DAOFactory.getDataSourceDAO().loadAllDataSources();
			java.util.Iterator dataSourceIt = dataSources.iterator();
	
			String actualDsId=""; 
			if(JDBCDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){
				if(ds.getDataSource()!=null){
				int id=DAOFactory.getDataSourceDAO().loadDataSourceByLabel(ds.getDataSource().getLabel()).getDsId();
				actualDsId=Integer.valueOf(id).toString();
				}
			}
			
			while (dataSourceIt.hasNext()) {
				DataSource dataSourceD = (DataSource) dataSourceIt.next();
				String dsId = String.valueOf(dataSourceD.getDsId());
					
				String selected = "";
				if (dsId.equalsIgnoreCase(actualDsId)) {
					selected = "selected='selected'";										
					}				
			 	%>    			 		
    				<option value="<%= dsId  %>" label="<%= StringEscapeUtils.escapeHtml(dataSourceD.getLabel()) %>" <%= selected %>>
    					<%=StringEscapeUtils.escapeHtml(dataSourceD.getLabel()) %>	
    				</option>
    				<%				
			  	}
			
			%>
		</select>
	</div>
	</div>
	<div id="wscontainer" <%=hideWs%>>		
		<div class='div_detail_label' id="ADDRESSLABEL">
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.address" />
			</span>
		</div>
	    <div class='div_detail_form'>
		<%
			   String address =""; 
		       if(WebServiceDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
		    	   address = ds.getAdress();
		       }
			   if((address==null) || (address.equalsIgnoreCase("null"))  ) {
				   address = "";
			   }
		%>
			<input class='portlet-form-input-field' type="text" name="ADDRESS" id="ADDRESS"
				   size="50" value="<%=StringEscapeUtils.escapeHtml(address)%>" maxlength="150" <%=disableWs%> />
	   </div>
	   
	   		<div class='div_detail_label' id="OPERATION">
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.operation" />
			</span>
		</div>
	    <div class='div_detail_form'>
		<%
			   String operation =""; 
		       if(WebServiceDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
		    	   operation = ds.getOperation();
		       }
			   if((operation==null) || (operation.equalsIgnoreCase("null"))  ) {
				   operation = "";
			   }
		%>
			<input class='portlet-form-input-field' type="text" name="OPERATION" id="OPERATION"
				   size="50" value="<%=StringEscapeUtils.escapeHtml(operation)%>" maxlength="50" <%=operation%> <%=disableWs%>/>
	   </div>		
			
	</div>
	
	
	
	<div id="scriptcontainer" <%=hideScript%>>
		<div class='div_detail_label' id="SCRIPTNAMELABEL" >
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.script" />
			</span>
		</div>
	    <div class='div_detail_form' style="height:150px;">
		<%
			   String script = "" ; 
		       if(ScriptDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
		    	   script = ds.getScript();
		       }
			   if((script==null) || (script.equalsIgnoreCase("null"))  ) {
				   script = "";
			   }
		%>
		  <textarea id="SCRIPT" rows="8" cols="80" name="SCRIPT" style="font-size:9pt" <%=disableScript%>><%=script%></textarea>

	   </div>
	   </div>
	   
	   <!-- LANGUAGE SCRIPT  -->
		<div id="languagescriptcontainer" <%=hideLanguageScript%>>
		<div class='div_detail_label' id="LANGUAGESCRIPTNAMELABEL" >
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.languageScript" />
			</span>
		</div>
	    <div class='div_detail_form'>
		<%
			   String languageScript = "" ; 
		       if(ScriptDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
		    	   languageScript = ds.getLanguageScript();
		       }
			   if((languageScript==null) || (languageScript.equalsIgnoreCase("null"))  ) {
				   languageScript = "";
			   }
		%>
		<select class='portlet-form-field' name="LANGUAGESCRIPT" id="LANGUAGESCRIPT" <%=disableScript%> >			
			<%

			Map engineNames=ScriptUtilities.getEngineFactoriesNames();
			String selected="";
			for(Iterator it=engineNames.keySet().iterator();it.hasNext();){
				String engName=(String)it.next(); 
				String alias=(String)engineNames.get(engName);
			    selected="";
			    if(languageScript.equalsIgnoreCase(alias)){
			    
					selected="selected='selected'";
			    }		
			    String aliasName=ScriptUtilities.bindAliasEngine(alias);
	%>
	    	<option value="<%=StringEscapeUtils.escapeHtml(alias)%>" label="<%=StringEscapeUtils.escapeHtml(alias)%>" <%= selected%>>
    					<%=StringEscapeUtils.escapeHtml(aliasName)%>	
    		</option>
	<%
		}
			     
%>			
		</select>
	</div>
	   </div>
	   
	   
	   <div id="jclasscontainer" <%=hideJClass%>>
		<div class='div_detail_label' id="JCLASSLABEL" >
			<span class='portlet-form-field-label'>	
				<spagobi:message key = "SBISet.ListDataSet.jClassName" />
			</span>
		</div>
	    <div class='div_detail_form'>
		<%
			   String javaClassName =""; 
		       if(JavaClassDataSet.DS_TYPE.equalsIgnoreCase(ds.getType())){	
		    	   javaClassName = ds.getJavaClassName();
		       }
			   if((javaClassName==null) || (javaClassName.equalsIgnoreCase("null"))  ) {
				   javaClassName = "";
			   }
		%>
			<input class='portlet-form-input-field' type="text" name="JCLASSNAME" id="JCLASSNAME"
				   size="100" value="<%=StringEscapeUtils.escapeHtml(javaClassName)%>" maxlength="100" <%=disableJClass%> />
	   </div>
	   </div>
	   

	</td><!-- CLOSE COLUMN WITH DATA FORM  -->
	
			<!-- START DIV FIX LIST WIZARD --> 
        <table id="tag" style="display:<%=datasetDisplay%>;">
  		<tr><td>
		<spagobi:datasetWizard parametersXML='<%= dataSetParametersList!= null ? dataSetParametersList.toXML() : "" %>' /> 	
		</td></tr></table>	
		
		
		
		<!-- DIV FIX LIST WIZARD CLOSED -->
	</tr>
	</table>   <!-- CLOSE TABLE FORM ON LEFT AND VERSION ON RIGHT  -->
	
	 <!--</div>  background -->
	 
	 
	 
	
	<script>
		function EnableTransformerDiv(type){
			if (type == "")
				document.all['transformer_pivot'].style.display = 'none';
			else if (type == "PIVOT_TRANSFOMER")
				document.all['transformer_pivot'].style.display = 'inline';
		}
	<!--
	
	<%
		String datasetModified = (String)aSessionContainer.getAttribute(SpagoBIConstants.DATASET_MODIFIED);
		if (datasetModified != null && !datasetModified.trim().equals("")) {
	%>
		var datasetModified = <%=datasetModified%>;
	<%
		} else {
	%>
		var datasetModified = false;
	<%
		}
	%>

	function isDsFormChanged () {
	
	var bFormModified = 'false';
		
	var label = document.dsForm.LABEL.value;
	var description = document.dsForm.DESCR.value;	
	
	if ((label != '<%=StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(ds.getLabel()))%>')
		|| (description != '<%=(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(ds.getDescription()))==null)?"":StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(ds.getDescription()))%>'))
	{			
		bFormModified = 'true';
	}
	
	return bFormModified;
	
	}

	
	function goBack(message, url) {
	  
	  var bFormModified = isDsFormChanged();
	  
	  if (bFormModified == 'true'){
	  	  if (confirm(message)) {
	  	      document.getElementById('saveAndGoBack').click(); 
	  	  } else {
			location.href = url;	
    	  }	         
       } else {
			location.href = url;
       }	  
	}
	
	function saveDS(type) {
	
		if(!document.dsForm.QUERY.disabled){	
			if(document.getElementById('DATASOURCE').selectedIndex == 0){
			 alert('<%=msgBuilder.getMessage("9212", "messages",  request) %>');
			 return;
			}
		}
		
		if(document.getElementById('LABEL').value == ''){
			 alert('<%=msgBuilder.getMessage("9214", "messages",  request) %>');
			 return;
}
  	  	  document.dsForm.SUBMESSAGEDET.value=type;
  	  	  if (type == 'SAVE'){
      		  document.getElementById('dsForm').submit();}
	}
	
	
		function DisableFields(type){

		if (type == 'file'){
			document.dsForm.FILENAME.disabled=false;
			document.dsForm.ADDRESS.disabled=true;
			document.dsForm.QUERY.disabled=true;
			document.dsForm.OPERATION.disabled=true;
			document.dsForm.DATASOURCE.disabled=true;
			document.dsForm.SCRIPT.disabled=true;
			document.dsForm.LANGUAGESCRIPT.disabled=true;			
			document.dsForm.JCLASSNAME.disabled=true;
			document.getElementById("tag").style.display = "none";
			document.getElementById("filecontainer").style.display = "inline";
			document.getElementById("querycontainer").style.display = "none";
			document.getElementById("wscontainer").style.display = "none";
			document.getElementById("scriptcontainer").style.display = "none";
			document.getElementById("languagescriptcontainer").style.display = "none";
			document.getElementById("jclasscontainer").style.display = "none";
	
			
		}
		else 
		if (type == 'query'){
			document.dsForm.FILENAME.disabled=true;
			document.dsForm.ADDRESS.disabled=true;
			document.dsForm.QUERY.disabled=false;
			document.dsForm.OPERATION.disabled=true;
			document.dsForm.DATASOURCE.disabled=false;
			document.dsForm.SCRIPT.disabled=true;
			document.dsForm.LANGUAGESCRIPT.disabled=true;
			document.dsForm.JCLASSNAME.disabled=true;
			document.getElementById("tag").style.display = "inline";
			document.getElementById("filecontainer").style.display = "none";
			document.getElementById("querycontainer").style.display = "inline";
			document.getElementById("wscontainer").style.display = "none";
			document.getElementById("scriptcontainer").style.display = "none";
			document.getElementById("languagescriptcontainer").style.display = "none";
			document.getElementById("jclasscontainer").style.display = "none";
			
		}
	    else 
	    if (type == 'ws'){
			document.dsForm.FILENAME.disabled=true;
			document.dsForm.ADDRESS.disabled=false;
			document.dsForm.QUERY.disabled=true;
			document.dsForm.OPERATION.disabled=false;
			document.dsForm.DATASOURCE.disabled=true;
			document.dsForm.SCRIPT.disabled=true;
			document.dsForm.LANGUAGESCRIPT.disabled=true;
			document.dsForm.JCLASSNAME.disabled=true;
			document.getElementById("tag").style.display = "inline";
			document.getElementById("filecontainer").style.display = "none";
			document.getElementById("querycontainer").style.display = "none";
			document.getElementById("wscontainer").style.display = "inline";
			document.getElementById("scriptcontainer").style.display = "none";
			document.getElementById("languagescriptcontainer").style.display = "none";
			document.getElementById("jclasscontainer").style.display = "none";
	
		}
		else
		if (type == 'script') {
			document.dsForm.FILENAME.disabled=true;
			document.dsForm.ADDRESS.disabled=true;
			document.dsForm.QUERY.disabled=true;
			document.dsForm.OPERATION.disabled=true;
			document.dsForm.DATASOURCE.disabled=true;
			document.dsForm.SCRIPT.disabled=false;
			document.dsForm.LANGUAGESCRIPT.disabled=false;
			document.dsForm.JCLASSNAME.disabled=true;
			document.getElementById("tag").style.display = "inline";
			document.getElementById("filecontainer").style.display = "none";
			document.getElementById("querycontainer").style.display = "none";
			document.getElementById("wscontainer").style.display = "none";
			document.getElementById("scriptcontainer").style.display = "inline";
			document.getElementById("languagescriptcontainer").style.display = "inline";
			document.getElementById("jclasscontainer").style.display = "none";
		}
		else
		if (type == 'javaclass') {
			document.dsForm.FILENAME.disabled=true;
			document.dsForm.ADDRESS.disabled=true;
			document.dsForm.QUERY.disabled=true;
			document.dsForm.OPERATION.disabled=true;
			document.dsForm.DATASOURCE.disabled=true;
			document.dsForm.SCRIPT.disabled=true;
			document.dsForm.LANGUAGESCRIPT.disabled=true;
			document.dsForm.JCLASSNAME.disabled=false;
			document.getElementById("tag").style.display = "inline";
			document.getElementById("filecontainer").style.display = "none";
			document.getElementById("querycontainer").style.display = "none";
			document.getElementById("wscontainer").style.display = "none";
			document.getElementById("scriptcontainer").style.display = "none";
			document.getElementById("languagescriptcontainer").style.display = "none";
			document.getElementById("jclasscontainer").style.display = "inline";
		}
	}
	
			function setParametersXMLModified(newValue) {
	   			 <%if(modality.equals(SpagoBIConstants.DETAIL_MOD)) { %>
					datasetModified = newValue;
				<%}%>
				}
		
			function setParametersXMLModifiedField(){
					if (datasetModified) {
						document.getElementById("parametersXMLModified").value = 'true';
					} else {
						document.getElementById("parametersXMLModified").value = 'false';
						}
				}
		
	var profattrwinopen = false;
	var winPA = null;
	
	function opencloseProfileAttributeWin() {
		if(!profattrwinopen){
			profattrwinopen = true;
			openProfileAttributeWin();
		}
	}
	
	function openProfileAttributeWin(){
		if(winPA==null) {
			winPA = new Window('winPAId', {className: "alphacube", title: "<%=msgBuilder.getMessage("SBIDev.lov.avaiableProfAttr", "messages", request)%>", width:400, height:300, destroyOnClose: true});
	      	winPA.setContent('profileattributeinfodiv', false, false);
	      	winPA.showCenter(false);
	    } else {
	      	winPA.showCenter(false);
	    }
	}
	
	observerWPA = { 
		onClose: function(eventName, win) {
			if (win == winPA) {
				profattrwinopen = false;
			}
		}
	}
	
	Windows.addObserver(observerWPA);
--></script>

<div id='profileattributeinfodiv' style='display:none;'>	
	<hr/>
	<br/>
	<ul>
    <%
    List nameAttrs = (List) moduleResponse.getAttribute(SpagoBIConstants.PROFILE_ATTRS);
    if(nameAttrs!=null && nameAttrs.size()>0){
		Iterator profAttrsIter = nameAttrs.iterator();
		while(profAttrsIter.hasNext()) {
			String profAttrName = (String)profAttrsIter.next();
	%>
	 	<li><%=profAttrName%></li>
	<% 	
		}
    }
	%>
	</ul>
	<br/>
</div>	
</form>		
		
		
		
	
