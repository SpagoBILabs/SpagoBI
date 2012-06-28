<%-- SpagoBI, the Open Source Business Intelligence suite

 Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.  If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java"
		 import="it.eng.spago.error.EMFErrorHandler"
		 extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPage"
		 contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"
		 session="true"
		 errorPage="/jsp/error.jsp"
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	EMFErrorHandler errorHandler = getErrorHandler(request);
%>

<HTML>
	<HEAD>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<TITLE>Session Expired</TITLE>
	</HEAD>
	<BODY>
		<center>
			<span style="color:red;font-size:16pt;">
				Session Expired
			</span>
		</center>
	</BODY>
</HTML>
