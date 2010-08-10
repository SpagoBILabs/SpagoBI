<!--
/**
 *	LICENSE: see COPYING file
**/
-->

<%@ page language="java"
		 import="it.eng.spago.error.EMFErrorHandler"
		 extends="it.eng.spago.dispatching.httpchannel.AbstractHttpJspPage"
		 contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"
		 session="true"
		 errorPage="/WEB-INF/jsp/error.jsp"
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	EMFErrorHandler errorHandler = getErrorHandler(request);
%>

<HTML>
	<HEAD>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<TITLE>Security Error</TITLE>
	</HEAD>
	<BODY>
		<center>
			<span style="color:red;font-size:16pt;">
				Security Error
			</span>
		</center>
	</BODY>
</HTML>

