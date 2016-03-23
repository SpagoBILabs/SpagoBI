<%--

LICENSE: see LICENSE.txt file 

--%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@page import="com.tonbeller.jpivot.olap.model.OlapModel"%>
<%@page import="com.tonbeller.jpivot.olap.navi.MdxQuery"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>

<html>
<head>
  <title>Mdx query edit</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="jpivot/table/mdxtable.css">
  <link rel="stylesheet" type="text/css" href="jpivot/navi/mdxnavi.css">
  <link rel="stylesheet" type="text/css" href="wcf/form/xform.css">
  <link rel="stylesheet" type="text/css" href="wcf/table/xtable.css">
  <link rel="stylesheet" type="text/css" href="wcf/tree/xtree.css">
  <link rel="stylesheet" type="text/css" href="css/stili.css">
</head>

<body bgcolor=white lang="en">

<%
//retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

// retrieves the Mondrian query
OlapModel om = (OlapModel) session.getAttribute("query01");
MdxQuery query = (MdxQuery) om.getExtension("mdxQuery");
String mondrianQuery = query.getMdxQuery();

// retrieves the query with parameters from the form below
String queryWithParameters = request.getParameter("queryWithParameters");
// if it is not null the form below was submitted
if (queryWithParameters == null) {
	String initialQueryWithParameters = (String) session.getAttribute("initialQueryWithParameters");
	String initialMondrianQuery = (String) session.getAttribute("initialMondrianQuery");
	if (initialQueryWithParameters != null && initialMondrianQuery != null) {
		if (mondrianQuery.trim().equalsIgnoreCase(initialMondrianQuery.trim())) {
			// the initial Mondrian query was not modified
			queryWithParameters = initialQueryWithParameters;
		} else {
			// the initial Mondrian query was not modified
			queryWithParameters = mondrianQuery;
		}
	} else {
		queryWithParameters = mondrianQuery;
	}
}
// puts in session the queryWithParameters so the TemplateBean.saveTemplate can retrieve it
session.setAttribute("queryWithParameters", queryWithParameters);

String parameterName = request.getParameter("parameterName");
String parameterUrlName = request.getParameter("parameterUrlName");
HashMap parameters = (HashMap) session.getAttribute("parameters");
if (parameters == null) {
	parameters = new HashMap();
	session.setAttribute("parameters", parameters);
}
String action = request.getParameter("action");
if (action != null && action.trim().equalsIgnoreCase("addParameter")) {
	String ALPHANUMERIC_STRING_REGEXP="^([a-zA-Z0-9\\s\\-\\_])*$";
	int maxLength = 30;
	if (GenericValidator.isBlankOrNull(parameterName) || GenericValidator.isBlankOrNull(parameterUrlName)) {
		%>
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: red;font-size: 8pt;font-weight: bold;">
		<%=EngineMessageBundle.getMessage("error.no.parameter.name.or.urlname", locale)%>
		</span>
		<%
	} else if (!GenericValidator.maxLength(parameterName, maxLength) 
			|| !GenericValidator.maxLength(parameterUrlName, maxLength)) {
		%>
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: red;font-size: 8pt;font-weight: bold;">
		<%=EngineMessageBundle.getMessage("error.parameter.name.and.urlname.exceed.max", locale, new String[] {new Integer(maxLength).toString()})%>
		</span>
		<%
	} else if (!GenericValidator.matchRegexp(parameterName, ALPHANUMERIC_STRING_REGEXP) 
			|| !GenericValidator.matchRegexp(parameterUrlName, ALPHANUMERIC_STRING_REGEXP)) {
		%>
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: red;font-size: 8pt;font-weight: bold;">
		<%=EngineMessageBundle.getMessage("error.parameter.name.and.urlname.alphanumeric", locale)%>
		</span>
		<%
	} else {
		parameterName = parameterName.trim();
		parameterUrlName = parameterUrlName.trim();
		parameters.put(parameterName, parameterUrlName);
		parameterName = null;
		parameterUrlName = null;
	}
}

if (action != null && action.trim().equalsIgnoreCase("deleteParameter")) {
	String parameterNameToRemove = request.getParameter("parameterNameToRemove");
	if (parameterNameToRemove != null && parameters.containsKey(parameterNameToRemove)) {
		parameters.remove(parameterNameToRemove);
		parameterName = null;
	}
}
%>

<form action="addParameters.jsp" method="post" name="addParametersForm" id="addParametersForm">
	<wcf:form id="saveTemplateForm01" xmlUri="/WEB-INF/jpivot/table/saveTemplateTable.xml" model="#{saveTemplate01}" visible="false"/>
	<wcf:toolbar id="toolbar02" bundle="com.tonbeller.jpivot.toolbar.resources">
		<wcf:scriptbutton id="saveTemplate" tooltip="toolb.saveTemplate" img="save" model="#{saveTemplateForm01.visible}"/>
	</wcf:toolbar>
	<p>
	<wcf:render ref="toolbar02" xslUri="/WEB-INF/jpivot/toolbar/htoolbar.xsl" xslCache="true"/>
	<p>
	<wcf:render ref="saveTemplateForm01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
	<p>
	<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
	<b><%=EngineMessageBundle.getMessage("add.parameters.type.query", locale)%></b>
	<br>
	<textarea style="width:48%;height:100;" name="queryWithParameters" /><%=queryWithParameters%></textarea>
	<p>
	<b><%=EngineMessageBundle.getMessage("add.parameters.type.parameters", locale)%></b>
	<br>
	<input type="hidden" name="action" id="action" value="" />
	<table cellpadding="5" cellspacing="0" width="48%" style="border:1px solid #7f9db9;font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
		<tr>
			<td style="width: 47%;"><%=EngineMessageBundle.getMessage("add.parameters.parameter.name", locale)%></td>
			<td style="width: 47%;"><input type="text" name="parameterName" value="<%=(parameterName != null) ? parameterName : ""%>" size="30" /></td>
			<td rowspan="2" align="center" style="width: 6%;">
				<input type="image" onclick="document.getElementById('action').value='addParameter';document.getElementById('addParametersForm').submit();"
						title="<%=EngineMessageBundle.getMessage("add.parameters.add", locale)%>" 
						alt="<%=EngineMessageBundle.getMessage("add.parameters.add", locale)%>" 
						src="jpivot/table/drill-position-expand.gif" />
			</td>
		</tr>
		<tr>
			<td><%=EngineMessageBundle.getMessage("add.parameters.parameter.urlname", locale)%></td>
			<td><input type="text" name="parameterUrlName" value="<%=(parameterUrlName != null) ? parameterUrlName : ""%>" size="30" /></td>
		</tr>
	</table>
	</span>
</form>
<p>
<%
if (parameters.size() > 0) {
	%>
	<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
	<b><%=EngineMessageBundle.getMessage("add.parameters.defined.parameters", locale)%></b>
	<br>
	<form action="addParameters.jsp" method="post" name="deleteParametersForm" id="deleteParametersForm">
		<input type="hidden" name="action" value="deleteParameter" />
		<input type="hidden" name="parameterNameToRemove" id="parameterNameToRemove" value="" />
		<input type="hidden" name="queryWithParameters" value="<%=queryWithParameters%>" />
		
		<table cellpadding="5" cellspacing="0" width="48%" style="border:1px solid #7f9db9;font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
			<tr>
				<th style="background-color: #DEE3EF;color: Black;text-align: left;width: 47%;"><%=EngineMessageBundle.getMessage("add.parameters.parameter.name.lbl", locale)%></th>
				<th style="background-color: #DEE3EF;color: Black;text-align: left;width: 47%;"><%=EngineMessageBundle.getMessage("add.parameters.parameter.urlname.lbl", locale)%></th>
				<th style="background-color: #DEE3EF;color: Black;text-align: left;width: 6%;">&nbsp;</th>
			</tr>
			<%
			Set keys = parameters.keySet();
			Iterator keyIt = keys.iterator();
			while (keyIt.hasNext()) {
				String aParameterName = (String) keyIt.next();
				String aParameterUrlName = (String) parameters.get(aParameterName);
				%>
				<tr>
					<td><%=aParameterName%></td>
					<td><%=aParameterUrlName%></td>
					<td align="center">
						<input type="image" onclick="document.getElementById('parameterNameToRemove').value='<%=aParameterName%>';document.getElementById('deleteParametersForm').submit();"
							title="<%=EngineMessageBundle.getMessage("add.parameters.remove", locale)%>" 
							alt="<%=EngineMessageBundle.getMessage("add.parameters.remove", locale)%>" 
							src="jpivot/table/drill-position-collapse.gif" />
					</td>
				</tr>
				<%
			}
			%>
		</table>
	</form>
	</span>
	<%
}
%>
</body>
</html>