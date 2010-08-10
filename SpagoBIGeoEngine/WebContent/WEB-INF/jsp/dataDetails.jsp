<%-- 

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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

<%@ page contentType="text/html; charset=ISO-8859-1"%>
<%@ page language="java" %>

<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="it.eng.spago.base.*"%>








<% 
	RequestContainer requestContainer = null;
	ResponseContainer responseContainer = null;
	SessionContainer sessionContainer = null;   
	
	requestContainer = RequestContainerAccess.getRequestContainer(request);
	responseContainer = ResponseContainerAccess.getResponseContainer(request);
	sessionContainer = requestContainer.getSessionContainer();
	
	SourceBean dataDetailsSB ;
	String featureDesc;
	
	dataDetailsSB = (SourceBean)responseContainer.getServiceResponse().getAttribute("RESULT_SET");
	featureDesc = (String)responseContainer.getServiceResponse().getAttribute("FEATURE_DESC");
	
	//SourceBean resultSetSB = dataDetailsSB.getAttribute()
	List rows = dataDetailsSB.getAttributeAsList("ROWS.ROW");
	SourceBean firstRowSB = (SourceBean)rows.get(0);
	List firstRowColumnsSB = firstRowSB.getContainedAttributes();
	List columnLabels = new ArrayList();
	for(int i = 0; i < firstRowColumnsSB.size(); i++) {
		SourceBeanAttribute attr = (SourceBeanAttribute)firstRowColumnsSB.get(i);
		columnLabels.add(attr.getKey());
	}
	

	
	
%>



<html>

<head>
	<link rel="stylesheet" href ="../css/spagobi.css" type="text/css"/>
	<link rel="stylesheet" href ="../css/jsr168.css" type="text/css"/>
	<link rel="stylesheet" href ="../../../css/external.css" type="text/css"/>
</head>

<body>

<script>	
	
	
</script>

<!-- ============================================================================================================== -->

<table class='header-table-portlet-section'>
	<tr class='header-row-portlet-section'>
		<td class='header-title-column-portlet-section' 
		    style='vertical-align:middle;padding-left:5px;'>
			Data Details: <%=featureDesc %>
		</td>
	</tr>
</table>

<div class='div_background_no_img'>

<table class="object-details-table" style="margin:5px;width:100%;">
	<tr>
	<%
	for (int i=0; i<columnLabels.size(); i++) {
	%>
	   <td class="portlet-section-header" style="text-align:left;"><%=(String)columnLabels.get(i)%></td>
	<%
	}
	%>
	</tr>
	
	<%
	boolean alternate = false;
	String rowClass;
	for (int j=0; j<rows.size(); j++) {
		SourceBean rowSB = (SourceBean)rows.get(j);
		
		if (alternate) rowClass = "portlet-section-alternate";
	    else rowClass = "portlet-section-body";      
	    alternate = !alternate;
	%>
	<tr>
	<%
		for (int i=0; i <columnLabels.size(); i++) {
			String columnName = (String)columnLabels.get(i);
			String columValue = "";
			if(rowSB.getAttribute(columnName) instanceof String) {
				columValue = (String)rowSB.getAttribute(columnName);
			} else {
				List list = (List)rowSB.getAttribute(columnName);
				columValue = (String)list.get(0);
			}
			 
	%>
		<td class="<%=rowClass%>"><%=columValue %></td>
	<%
		}
	%>
	</tr>	
	<%
	}
	%>
	
	
</table>




<!-- ============================================================================================================== -->





</body>

</html>
