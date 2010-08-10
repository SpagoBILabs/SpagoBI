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
<%@tag language="java" pageEncoding="UTF-8" %>
<%@tag import="java.net.URLEncoder"%>
<%@tag import="java.util.Set"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="it.eng.spagobi.services.common.SsoServiceInterface"%>
<%@tag import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>

<%@attribute name="spagobiContext" required="true" type="java.lang.String"%>
<%@attribute name="userId" required="true" type="java.lang.String"%>
<%@attribute name="documentId" required="false" type="java.lang.String"%>
<%@attribute name="documentLabel" required="false" type="java.lang.String"%>
<%@attribute name="executionRole" required="false" type="java.lang.String"%>
<%@attribute name="parametersStr" required="false" type="java.lang.String"%>
<%@attribute name="parametersMap" required="false" type="java.util.Map"%>
<%@attribute name="displayToolbar" required="false" type="java.lang.Boolean"%>
<%@attribute name="displaySliders" required="false" type="java.lang.Boolean"%>
<%@attribute name="iframeStyle" required="false" type="java.lang.String"%>
<%@attribute name="theme" required="false" type="java.lang.String"%>
<%@attribute name="authenticationTicket" required="false" type="java.lang.String"%>

<%
StringBuffer iframeUrl = new StringBuffer();
iframeUrl.append(spagobiContext + "/servlet/AdapterHTTP?NEW_SESSION=true");
iframeUrl.append("&ACTION_NAME=EXECUTE_DOCUMENT_ACTION");
iframeUrl.append("&" + SsoServiceInterface.USER_ID + "=" + userId);

if (documentId == null && documentLabel == null) {
	throw new Exception("Neither document id nor document label are specified!!");
}
if (documentId != null) {
	iframeUrl.append("&OBJECT_ID=" + documentId);
} else {
	iframeUrl.append("&OBJECT_LABEL=" + documentLabel);
}
if (parametersStr != null) iframeUrl.append("&PARAMETERS=" + URLEncoder.encode(parametersStr));
if (parametersMap != null && !parametersMap.isEmpty()) {
	Set keys = parametersMap.keySet();
	Iterator keysIt = keys.iterator();
	while (keysIt.hasNext()) {
		String urlName = (String) keysIt.next();
		Object valueObj = parametersMap.get(urlName);
		if (valueObj != null) {
			iframeUrl.append("&" + URLEncoder.encode(urlName) + "=" + URLEncoder.encode(valueObj.toString()));
		}
	}
}
if (executionRole != null) iframeUrl.append("&ROLE=" + URLEncoder.encode(executionRole));
if (displayToolbar != null) iframeUrl.append("&TOOLBAR_VISIBLE=" + displayToolbar.toString());
if (displaySliders != null) iframeUrl.append("&SLIDERS_VISIBLE=" + displaySliders.toString());
if (theme != null)	iframeUrl.append("&theme=" + theme);
if (authenticationTicket != null) iframeUrl.append("&auth_ticket=" + URLEncoder.encode(authenticationTicket));
%>

<iframe src="<%= iframeUrl.toString() %>" style="<%= iframeStyle != null ? iframeStyle : "" %>"></iframe>
