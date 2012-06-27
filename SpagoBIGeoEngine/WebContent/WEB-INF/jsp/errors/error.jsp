<%-- SpagoBI, the Open Source Business Intelligence suite

 © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this file,
 You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@ page language="java"
		 import="java.io.PrintWriter,
    			 java.io.StringWriter,
    			 java.lang.Exception"
		 contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="UTF-8"
		 isErrorPage="true"
		 session="true"
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
		<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<TITLE>Error Page</TITLE>
	</HEAD>
	<BODY>

<%
	String messageExcept = "";	
    if(exception != null) {
		StringWriter exStringWriter = new StringWriter();
		PrintWriter exPrintWriter = new PrintWriter(exStringWriter);
		exception.printStackTrace(exPrintWriter);
		messageExcept = exStringWriter.toString();
    }
%>
		<CENTER>
			<span style="color:red;font-size:16pt;">
				Error
			</span>
			<br/>
			<span style="font-size:13pt;">
				Sorry, an internal error has occurred
			</span>
			<br/><br/>
			<TEXTAREA rows="22" name="error_message" cols="80"><%=messageExcept%></TEXTAREA>
		</CENTER>
		
	</BODY>
</HTML>
