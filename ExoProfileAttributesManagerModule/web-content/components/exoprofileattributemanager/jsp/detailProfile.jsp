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
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="it.eng.spago.base.*"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>

<!-- IMPORT TAG LIBRARY  -->
<%@ taglib uri='http://java.sun.com/portlet' prefix='portlet'%>


<portlet:defineObjects/>

<%
	SourceBean moduleResponse = (SourceBean)aServiceResponse.getAttribute("ExoProfileAttributeManagerModule"); 
	Map attributes = (Map)moduleResponse.getAttribute("attributes");
	Map attributeKeys = (Map)moduleResponse.getAttribute("attributeKeys");
	Set keySet = attributes.keySet();
	Iterator iterAttr = keySet.iterator();
	String username = (String)moduleResponse.getAttribute("UserName");
	String lastname = (String)moduleResponse.getAttribute("LastName");
	String firstname = (String)moduleResponse.getAttribute("FirstName");
	String email = (String)moduleResponse.getAttribute("Email");
 
   	PortletURL saveUrl = renderResponse.createActionURL();
   	saveUrl.setParameter("PAGE", "ExoProfileAttributeManagerPage");
   	saveUrl.setParameter("MESSAGE", "SAVE_PROFILE");
   	saveUrl.setParameter(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED, "true");
   	
   	PortletURL backUrl = renderResponse.createActionURL();
   	backUrl.setParameter(LightNavigationManager.LIGHT_NAVIGATOR_BACK_TO, "1");
%>

<script>

	var listAttributes;			//the list of all values visualized
	var selectedValue; 			//the value selected from user
	var generalValueAttr;		//the all valus
	var formChanged;			//flag that indicates if a value is changed into the form
	


    //fills the detail's attributes with the selected element
	function fillAttribute(keyAttr, valueAttr){
		checkChanges();
		var outputVal = valueAttr;
		RE = new RegExp("{'", "ig");
		outputVal = outputVal.replace(RE, "");
		RE = new RegExp("'}", "ig");
		outputVal = outputVal.replace(RE, "");
		RE = new RegExp("'", "ig");
		outputVal = outputVal.replace(RE, "");
		RE = new RegExp("{,", "ig");
		outputVal = outputVal.replace(RE, "");
		RE = new RegExp("}", "ig");
		outputVal = outputVal.replace(RE, "");
		//RE = new RegExp("'}", "ig");
		//outputVal = outputVal.replace(RE, "")
		//RE = new RegExp(",", "ig");
		if (outputVal.substr(0,1) == ",")	
			outputVal = outputVal.substr(1);
		
		document.forms.detailAttributesForm.keyDetail.value = keyAttr;
		document.forms.detailAttributesForm.valueDetail.value = outputVal;
		
		generalValueAttr = outputVal;
		drawListValues(outputVal);
		formChanged  = 'false';
		selectedValue = "";
		return;
	}
	
	
	//draws the attribute's list
	function drawListValues(valueAttr){
		
	   listAttributes = document.getElementById("tableAllValues");     

	   var alternate = new Boolean("false");
	   var rowClass = "";
	   var rowElement;
	   var tdElement;
	   var labelElement;
	   var imgElement;
	   var aElement;
	   var tbdy;
	   var  nameForm = "detailAttributesForm";
	   
	   removeElements();
	   if (valueAttr.substr(valueAttr.length-1,1) == ",")
			valueAttr = valueAttr.substr(0, valueAttr.length-1);
	   
	   if (valueAttr != ""){
	   	   var tmpValue = valueAttr.split(",");
		   for (i = 0; i < tmpValue.length; i++){
		  		 if(document.all && !window.opera && document.createElement) {
		  		 	tbdy = document.createElement("tbody")
		  		    //field number 1
		  		    rowElement = document.createElement("<tr>");
		  		 	tdElement = document.createElement("<td style='width:10%' class='portlet-form-field-label'>");
					rowElement.appendChild(tdElement);		
					
					//field number 2
					if (alternate) {
							rowClass = "portlet-section-body";
							alternate = false;
					}
					else {
							rowClass = "portlet-section-alternate";
							alternate = true;
					}
					tdElement = document.createElement("<td style='width:70%' class='" + rowClass + "'>");
					labelElement = document.createTextNode(tmpValue[i]);
					tdElement.appendChild(labelElement);				
					rowElement.appendChild(tdElement);	
					tbdy.appendChild(rowElement);		
					
					//field number 3
					tdElement = document.createElement("<td name='tdValue_"+i+"' style='width:5%' class='portlet-form-field-label'>");
					labelElement = document.createTextNode(" ");
					aElement = document.createElement("<a href=\"javascript:setValueForModify(\'"+tmpValue[i]+"\')\" >");
					imgElement = document.createElement("<IMG src='<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/signature.png")%>' >" );
					aElement.appendChild(imgElement);
					tdElement.appendChild(aElement);
					rowElement.appendChild(tdElement);	
					tbdy.appendChild(rowElement);
					
					//field number 4
					tdElement = document.createElement("<td style='width:5%' class='portlet-form-field-label'>");
					labelElement = document.createTextNode(" ");
					aElement = document.createElement("<a href=\"javascript:deleteValue(\'"+tmpValue[i]+"\')\" >");
					imgElement = document.createElement("<IMG src='<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/edit_remove.png")%>' >");
					aElement.appendChild(imgElement);
					tdElement.appendChild(aElement);
					rowElement.appendChild(tdElement);		
					tbdy.appendChild(rowElement);
					listAttributes.appendChild(tbdy);
					
		  		 }
		  		 else {
					rowElement = document.createElement("tr");
					//field number 1
					tdElement = document.createElement("td");
					tdElement.style.width="10%";
					tdElement.setAttribute("class", "portlet-form-field-label");
					labelElement = document.createTextNode(" ");
					tdElement.appendChild(labelElement);
					rowElement.appendChild(tdElement);		
					
					//field number 2
					if (alternate) {
							rowClass = "portlet-section-alternate";
							alternate = false;
					}
					else {
							rowClass = "portlet-section-body";
							alternate = true;
					}
					tdElement = document.createElement("td");
					tdElement.style.width="70%";
					tdElement.setAttribute("class", rowClass);
					tdElement.setAttribute("name", "tdValue_"+i);
					labelElement = document.createTextNode(tmpValue[i]);
					tdElement.appendChild(labelElement);				
					rowElement.appendChild(tdElement);			
					
					//field number 3
					tdElement = document.createElement("td");
					tdElement.style.width="5%";
					tdElement.setAttribute("class", "portlet-form-field-label");
					labelElement = document.createTextNode(" ");
					aElement = document.createElement("a");
					aElement.setAttribute("href", "javascript:setValueForModify('"+tmpValue[i]+"')");
					imgElement = document.createElement("IMG");
					imgElement.setAttribute("src","<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/signature.png")%>");
					imgElement.setAttribute("alt","<spagobi:message key = "detailAdd"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />");
					aElement.appendChild(imgElement);
					tdElement.appendChild(aElement);
					rowElement.appendChild(tdElement);	
					
					//field number 4
					tdElement = document.createElement("td");
					tdElement.style.width="5%";
					tdElement.setAttribute("class", "portlet-form-field-label");
					labelElement = document.createTextNode(" ");
					aElement = document.createElement("a");
					aElement.setAttribute("href", "javascript:deleteValue('"+tmpValue[i]+"')");
					imgElement = document.createElement("IMG");
					imgElement.setAttribute("src","<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/edit_remove.png")%>");
					imgElement.setAttribute("alt","<spagobi:message key = "detailDelete"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />");
					aElement.appendChild(imgElement);
					tdElement.appendChild(aElement);
					rowElement.appendChild(tdElement);	
					listAttributes.appendChild(rowElement);
				}
			}
		}
		return;
	}
	
	//remove elements from list
	function removeElements(){
		if(this.listAttributes) {
			child = listAttributes.firstChild;		
			while(child) {
		       var nextChild = child.nextSibling;
			   listAttributes.removeChild(child);
			   child = nextChild;
			}
		}
		return;
	}
	
	//copy the seleted value into textbox for modify
	function setValueForModify(valueAttr){
		document.forms.detailAttributesForm.valueDetail.value = valueAttr;
		selectedValue = valueAttr;
		return;
	}
	
	//deletes a row from attribute's table
	function deleteValue(valueAttr){
		var tmpValue = generalValueAttr.split(",");
		var tmpStr = "";
		
		for (i = 0; i < tmpValue.length; i++){
			if (tmpValue[i] != valueAttr){
				tmpStr = tmpStr + tmpValue[i];
				if (i < tmpValue.length -1 ) 
					tmpStr = tmpStr +  ",";
			}
		}
		generalValueAttr = tmpStr;
		if (generalValueAttr.substr(generalValueAttr.length-1,1) == ",")
			generalValueAttr = generalValueAttr.substr(0, generalValueAttr.length-1);

		drawListValues(generalValueAttr);
		formChanged='true';
		selectedValue = "";
		return;
	}
	
	//adds new value to the list
	function addValue(){
		if (document.forms.detailAttributesForm.keyDetail.value ==  null ||
			document.forms.detailAttributesForm.keyDetail.value == "" ) {
			var msg = "<%=msgBuilder.getMessage("1002", "it.eng.spagobi.exoaddins.component_exoprofman_messages", request) %>";
			alert(msg);
			return;
		}
		var newValue = document.forms.detailAttributesForm.valueDetail.value;
		//if selectedValue is imposted, it means that the user want to modify a value and not adding it...
		var tmpVal = generalValueAttr;
		if (selectedValue != null && selectedValue != ""){
			tmpVal = tmpVal.replace(selectedValue, newValue);
			generalValueAttr = tmpVal;
		}
		else {		
			if (generalValueAttr != "")
		    	generalValueAttr = generalValueAttr + "," + newValue;
		    else 
		    	generalValueAttr = newValue;
		}
	   
	    drawListValues(generalValueAttr);
	    formChanged='true';
	    selectedValue = "";
		return;
	}
	
	function saveAttributes(){
		document.forms.attributesForm.keys.value =  document.forms.detailAttributesForm.keyDetail.value;
		document.forms.attributesForm.attributes.value =  generalValueAttr;
		selectedValue = "";
		
		if (document.forms.detailAttributesForm.keyDetail.value ==  null ||
			document.forms.detailAttributesForm.keyDetail.value == "" || generalValueAttr == null) {
			var msg = "<%=msgBuilder.getMessage("1001", "it.eng.spagobi.exoaddins.component_exoprofman_messages", request) %>";
			alert(msg);
		}
		else{
			formChanged = false;
			document.forms.attributesForm.submit();
		}
		return;
	}
	
	function checkChanges(){
		if (formChanged == 'true') {
			var msg = "<%=msgBuilder.getMessage("1000", "it.eng.spagobi.exoaddins.component_exoprofman_messages", request) %>";
			if (confirm(msg)){
				document.forms.attributesForm.keys.value =  document.forms.detailAttributesForm.keyDetail.value;
				document.forms.attributesForm.attributes.value =  generalValueAttr;
		
				formChanged = 'false';
				document.forms.attributesForm.submit();
			}
		}
		
		return;
	} 
	
	function clearValue(){
		document.forms.detailAttributesForm.valueDetail.value = "";
		selectedValue = "";
		formChanged = 'false';
		return;
	} 
</script>



<form method='POST' action='<%=saveUrl%>' id='attributesForm' name='attributesForm'>
	<div style="float:left;width:99%;" class="div_detail_area_forms">
	<input type='hidden' value='<%=username%>' name='UserName' />
	<input type='hidden' value='' name='keys' />
	<input type='hidden' value='' name='attributes' />
	
	<table class='header-table-portlet-section'>		
		<tr class='header-row-portlet-section'>
			<td class='header-title-column-portlet-section' 
			    style='vertical-align:middle;padding-left:5px;'>
				<spagobi:message key = "profileDetail"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
			</td>
			<td class='header-empty-column-portlet-section'>&nbsp;</td>
			<td class='header-button-column-portlet-section'>
				<a href="javascript:saveAttributes()"> 
	      			<img class='header-button-image-portlet-section' 
	      				 title='<spagobi:message key = "SBISet.eng.saveButt" />' 
	      				  src='<%=urlBuilder.getResourceLinkByTheme(request, "/img/save.png", currTheme) %>' 
	      				 alt='<spagobi:message key = "SBISet.eng.saveButt" />' /> 
				</a>
			</td>
			<td class='header-button-column-portlet-section'>
				<a href='<%= backUrl.toString() %>'> 
	      			<img class='header-button-image-portlet-section' title='<spagobi:message key = "SBISet.eng.backButt" />' 
	      			src='<%= urlBuilder.getResourceLinkByTheme(request, "/img/back.png", currTheme) %>'
	      			alt='<spagobi:message key = "SBISet.eng.backButt" />' />
				</a>
			</td>
		</tr>
	</table>
	<div style="float:left;width:50%;" class="div_detail_area_sub_forms">
		<!--  <div class='div_background_no_img' style='padding:5px;'>-->
	     
		    <table>
		    	<tr>
		    		<td class='portlet-section-header'><spagobi:message key = "profileattr.username" /></td>
		    		<td class='portlet-section-header'><spagobi:message key = "profileattr.firstname" /></td>
		    		<td class='portlet-section-header'><spagobi:message key = "profileattr.lastname" /></td>
		    		<td class='portlet-section-header'><spagobi:message key = "profileattr.email" /></td>
		    	</tr>
		    	<tr>
		    	  <td style="border-bottom: 1px solid #cccccc;background-color:#fafafa;"><%=username%></td>
		    		<td style="border-bottom: 1px solid #cccccc;background-color:#fafafa;"><%=firstname%></td>
		    		<td style="border-bottom: 1px solid #cccccc;background-color:#fafafa;"><%=lastname%></td>
		    		<td style="border-bottom: 1px solid #cccccc;background-color:#fafafa;"><%=email%></td>    	
		      </tr>
		    </table> 
	     
	     
	    	<br/>
    
			<div class="div_detail_area_sub_forms" style="width:90%;padding:10px;">    
		    <%
		    	if(attributes.isEmpty()) {
		    %>
		    	<table>
		    		<tr>
		    			<td>
		    				<span style='font-size:11px;'>
		    				<spagobi:message key = "noAttributeToSet"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
		    				</span>
		    			</td>
		    		</tr>
		    	</table>
		    <%  } else { %>
		    <table>
  		    <tr>
  				  	<td>
  				  	 <span class='portlet-form-field-label'>
  		    				<spagobi:message key = "selectKey"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
  		    			</span>
  				  	</td>
  				</tr>
  				<tr><td>&nbsp;</td></tr>
		    </table>

			  <table>
			  	<%
			  		while(iterAttr.hasNext()) {
			  			String key = (String)iterAttr.next();
			  			String value = (String)attributes.get(key);
			   	%>
					<tr>
			   			<td width="25%">
			   				<span class='portlet-form-field-label'>
								<%=key%>
							</span>
			   			</td>
			   			<td  >
			   				<input class='portlet-form-input-field' type="text" name="<%=attributeKeys.get(key)%>" 
					      	       size="40" value="<%=value%>" onClick='fillAttribute("<%=key %>", "<%=value.replace("'", "&#39;") %>")' readonly="readonly">
			   			</td>  
			   		</tr> 
			    <%  } %>
			  </table>  
	
			<%  } %>
			
			</div>
		</div>
	<!-- </div> -->

</form>
<form method='POST' action='' id='detailAttributesForm' name='detailAttributesForm'>
	<div style="float:right;width:45%;" class="div_detail_area_forms" >
		
		<div class="div_detail_area_sub_forms"  style="width:90%">   
			<table>			 
				<tr>
					<td >						
						<span class='portlet-form-field-label'>
							<spagobi:message key = "detailAttribute.key"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
						</span>&nbsp;
					</td>		
					<td width="70%">		
						<input class='portlet-form-input-field' type="text"	name="keyDetail" id="keyDetail" value=""  size="40"  readonly="readonly"/>						
					</td>
					<td >
						<span class='portlet-form-field-label'>&nbsp;</span>
					</td>
					<td >						
						<span class='portlet-form-field-label'>&nbsp;</span>
					</td>	
				</tr>
				<tr>
					<td >						
						<span class='portlet-form-field-label'>
							<spagobi:message key = "detailAttribute.value"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />
						</span>&nbsp;
					</td>
					<td width="70%">	
							<input class='portlet-form-input-field' type="text" name="valueDetail" id="valueDetail" value=""  size="40"  onClick="javascript:checkChanges();"/>
					</td>
					<td class="portlet-form-field-label" style='width:5%' >&nbsp;
						<a href="javascript:clearValue()"> 
			      			<img src='<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/editclear.png")%>' 
			      				 alt='<spagobi:message key = "detailClear"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />' /> 
						</a>
					</td>
					<td class="portlet-form-field-label" style='width:5%' >&nbsp;
						<a href="javascript:addValue()"> 
			      			<img src='<%= renderResponse.encodeURL(renderRequest.getContextPath() + "/components/exoprofileattributemanager/img/edit_add.png")%>' 
			      				 alt='<spagobi:message key = "detailAdd"  bundle="it.eng.spagobi.exoaddins.component_exoprofman_messages" />' /> 
						</a>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
			</table>
			<table id="tableAllValues">			 
			</table>
		 </div> 
	</div>
</form>

