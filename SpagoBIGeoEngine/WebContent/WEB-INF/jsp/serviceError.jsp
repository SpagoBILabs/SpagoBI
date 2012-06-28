<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java"
		 import="it.eng.spago.error.*,java.util.*, it.eng.spagobi.engines.geo.*"
		 extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPage"
		 contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"
		 session="true"
		 errorPage="/WEB-INF/jsp/error.jsp"
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	EMFErrorHandler errorHandler = getErrorHandler(request);
	Iterator it = errorHandler.getErrors().iterator();
	EMFInternalError error = (EMFInternalError)it.next();	
	Exception exception = error.getNativeException();
	String description="no description available";
	List hints = null;
	if(exception instanceof GeoEngineException) {
		GeoEngineException geoException = (GeoEngineException)exception;
		description = geoException.getDescription();
		hints = geoException.getHints();
	}
%>

<HTML>
	<HEAD>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<TITLE>Service Error</TITLE>
	</HEAD>
	<body>

		<table cellspacing="20px">
		  <tr>
		    <td width="20%" valign="top">
		      <image height="150px"  src="../img/error.gif"/>
		    </td>
		    
		    <td width="80%" valign="top">
		    
		    <H1>Error</H1>
		    <hr>
		    <H2><%=exception.getMessage() %></H2>
		    <hr>
		    <br/>
		    <b>Description:</b> <%=description %> 
		    
		    <br/><br/>
		    <b>How to fix it:</b> <br>
		    <ul>
		    <% if (hints == null) {%>
		    
		    <%} else { 
		    	for(int i = 0; i < hints.size(); i++) {
		    		String hint = (String)hints.get(i);
		    %>
		    <li><%= hint%>
		    <%  }
		      }
		    %>
		    </ul>
		    
		    <br>
		    If none of these possible fixes work, please ask on <a href="http://forge.objectweb.org/forum/forum.php?forum_id=862">Spagobi Forum</a> for futher help
		    
		    </td>
		  </tr>
		</table>
		
	</body>
</HTML>
