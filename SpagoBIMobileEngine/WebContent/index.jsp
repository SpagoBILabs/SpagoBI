<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>
 
<%@ page language="java"
         extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"
         contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"
         session="true" 
%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPagePortlet"%>
<% 
	String contextName = ChannelUtilities.getSpagoBIContextName(request);
	String redirectURL = contextName + "/servlet/AdapterHTTP?ACTION_NAME=START_ACTION";
    response.sendRedirect(redirectURL);
%>

<HTML>
<HEAD>
<TITLE>Redirect...</TITLE> 

</HEAD>
<BODY>
Redirect in corso...
</BODY>
</HTML>