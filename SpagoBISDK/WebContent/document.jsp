<%@page import="java.io.InputStream"%>
<%@page import="java.util.Random"%>
<%@page import="it.eng.spagobi.sdk.proxy.DocumentsServiceProxy"%>
<%@page import="it.eng.spagobi.sdk.documents.bo.SDKTemplate"%>
<%@page import="javax.activation.DataHandler"%>
<%@page import="it.eng.spagobi.commons.utilities.SpagoBIUtilities"%>

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
<%
/**
This page invokes a SpagoBI web services in order to execute the document's methods.
It's a JSP for ONLY case tests.
To call it the url must be: http://localhost:8080/SpagoBISDK/document.jsp?doUpload=true&folderUpload=<nome_folder>
By default a foodmart/datamart.jar is ever downloaded.
The parameter 'doUpload' force an upload operation after the previous download operation.
The parameter 'folderUpload' gives the name of the folder to upload the file. If 'doUpload' is true and 'folderUpload' is null
a random folder name is created.
*/
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
String user = "biadmin";
String password = "biadmin";
String message = "Il documento è stato ";
String doUpload = "false";
String folderUpload = null;
String bodyCode = "";
if (user != null && password != null) {
	try { 
		DocumentsServiceProxy proxy = new DocumentsServiceProxy(user, password);
		proxy.setEndpoint("http://localhost:38080/SpagoBI/sdk/DocumentsService");		
		//gets request variables
		doUpload = (String)request.getParameter("doUpload");
		folderUpload = (String)request.getParameter("folderUpload");
		
		SDKTemplate template = new SDKTemplate();
		//test download datamart.jar
		template = proxy.downloadDatamartTemplate("foodmart", "datamart.jar");
		if (template != null){ 
			if (doUpload == null || doUpload.equalsIgnoreCase("false")) {
				DataHandler dh = template.getContent();
				InputStream is = dh.getInputStream();
				String fileName = template.getFileName();
				java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
				java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(baos);
	
				int c = 0;
				byte[] b = new byte[1024];
				while ((c = is.read(b)) != -1) {
					if (c == 1024)
						bos.write(b);
					else
						bos.write(b, 0, c);
				}
				bos.flush();
				byte[] content = baos.toByteArray();
				bos.close();
				
				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\";");
				response.setContentLength(content.length);
				response.setContentType("application/x-zip-compressed");
				response.getOutputStream().write(content);
			    //response.getOutputStream().flush();
				message += "scaricato con successo!";
			}
		}else{
			message += "scaricato con errori! Verifica i logs.";
			bodyCode += "<body> <h2> " + message + " </h2> " + new java.util.Date() +" </body></html>";
		}
		//test modifica dataset esistente
		
		if(doUpload != null && doUpload.equalsIgnoreCase("true")){
			//test upload datamart.jar
			if (folderUpload == null){
				Random randomGenerator = new Random();
				folderUpload = "foodmartSDK_"+ randomGenerator.nextInt(100);
			}
			template.setFileName("datamart.jar");
			template.setFolderName(folderUpload);
			template.setContent(template.getContent());
			proxy.uploadDatamartTemplate(template);
			message += "aggiornato con successo!";
			bodyCode += "<body> <h2> " + message + " </h2> " + new java.util.Date() +" </body></html>";
		}
	}  catch (Exception e) {
		e.printStackTrace();
			
	}
} else {
	response.sendRedirect("login.jsp");
}

%>
<%=bodyCode%>
