<%--

LICENSE: see LICENSE.html file 

--%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.Node"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="sun.misc.BASE64Decoder"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="it.eng.spagobi.jpivotaddins.bean.TemplateBean"%>
<%@page import="com.tonbeller.wcf.form.FormComponent"%>
<%@page import="com.tonbeller.jpivot.olap.model.OlapModel"%>
<%@page import="java.util.HashMap"%>
<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>

<html>
<head>
  <title>Initial query creation</title>
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
// retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

// puts in session the spagobi content repository servlet url 
// and the document path for TemplateBean.saveTemplate method
// and other objects avoiding unuseful http reuqest parameters
String biobjectPath = request.getParameter("biobject_path");
if (biobjectPath != null) session.setAttribute("biobject_path", biobjectPath);
String spagobiurl = request.getParameter("spagobiurl");
if (spagobiurl != null) session.setAttribute("spagobiurl", spagobiurl);
String templateName = request.getParameter("templateName");
if (templateName != null) session.setAttribute("templateName", templateName);
String template = request.getParameter("template");
if (template != null) session.setAttribute("template", template);

SAXReader readerConfigFile = new SAXReader();
Document documentConfigFile = readerConfigFile.read(getClass().getResourceAsStream("/engine-config.xml"));

String connection = request.getParameter("connection");
if (connection == null) connection = (String) session.getAttribute("connection");

if (connection != null && !connection.trim().equals("")) {
	session.setAttribute("connection", connection);
%>
	<form action="editQuery.jsp" method="post">
	<%
		OlapModel om = (OlapModel) session.getAttribute("query01");
		if (om == null) {
			Node connectionDef = documentConfigFile.selectSingleNode("//ENGINE-CONFIGURATION/CONNECTIONS-CONFIGURATION/CONNECTION[@name='"+connection+"'");
			if (connectionDef == null) {
		out.write("Connection '" + connection + "' not defined in engine-config.xml file.");
		return;
			}
			// the connection is specified so proceed with query execution
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			String templateBase64Coded = (String) session.getAttribute("template");
			byte[] templateContent = bASE64Decoder.decodeBuffer(templateBase64Coded);
			ByteArrayInputStream is = new ByteArrayInputStream(templateContent);
			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			String mdxQuery = null;
			String queryWithParameters = document.selectSingleNode("//olap/MDXquery").getStringValue();
			// loads parameters
			HashMap parameters = new HashMap();
			List parametersNodes = document.selectNodes("//olap/MDXquery/parameter");
			Iterator parametersNodesIt = parametersNodes.iterator();
			while (parametersNodesIt.hasNext()) {
		Node parameterNode = (Node) parametersNodesIt.next();
		String aParameterName = parameterNode.valueOf("@as");
		String aParameterUrlName = parameterNode.valueOf("@name");
		parameters.put(aParameterName, aParameterUrlName);
			}
			session.setAttribute("parameters", parameters);
			Node mdxMondrianQueryNode = document.selectSingleNode("//olap/MDXMondrianQuery");
			if (queryWithParameters.indexOf("${") != -1) {
		// Parameters with SpagoBI sintax were inserted so the query with parameters cannot be executed
	%>
			<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;font-weight: bold;">
			<%=EngineMessageBundle.getMessage("edit.query.parameters.warning", locale)%>
			<br>
			</span>
			<%
			if (mdxMondrianQueryNode == null) {
			%>
				<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;font-weight: bold;">
				<%=EngineMessageBundle.getMessage("edit.query.parameters.no.mondrian.query", locale)%>
				</span>
				<%
					return;
					} else {
						session.setAttribute("initialQueryWithParameters", queryWithParameters.trim());
						String mondrianQuery = mdxMondrianQueryNode.getStringValue();
						session.setAttribute("initialMondrianQuery", mondrianQuery.trim());
						mdxQuery = mondrianQuery;
						queryWithParameters = mondrianQuery;
					}
						} else {
					// Parameters with SpagoBI sintax were not inserted so the query can be executed
					mdxQuery = queryWithParameters;
						}
						Node cube = document.selectSingleNode("//olap/cube");
						String catalogUri = cube.valueOf("@reference");
						String schema = cube.valueOf("@name");
						
						// puts the catalogUri in session for TemplateBean.saveTemplate() method
						session.setAttribute("catalogUri", catalogUri);
						session.setAttribute("selectedSchema", schema);
						
						TemplateBean templateBean = new TemplateBean();
						templateName = (String) session.getAttribute("templateName");
						templateBean.setTemplateName(templateName);
						session.setAttribute("saveTemplate01", templateBean);
						
						String type = connectionDef.valueOf("@type");
						if (type.equalsIgnoreCase("jndi")) {
						    String iniCont = connectionDef.valueOf("@initialContext");
						    String resName = connectionDef.valueOf("@resourceName");
						    String connectionStr = "Provider=mondrian;DataSource="+iniCont+"/"+resName+";Catalog="+catalogUri+";";
				%>
			<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=catalogUri%>">
				<%=mdxQuery%>
			</jp:mondrianQuery>
			<%
				} else if (type.equalsIgnoreCase("jdbc")){
				String driver = connectionDef.valueOf("@driver");
				String url = connectionDef.valueOf("@jdbcUrl");
				String usr = connectionDef.valueOf("@user");
				String pwd = connectionDef.valueOf("@password");
				String connectionStr = "Provider=mondrian;JdbcDrivers="+driver+";Jdbc="+url+";JdbcUser="+usr+";JdbcPassword="+pwd+";Catalog="+catalogUri+";";
			%>
		    <jp:mondrianQuery id="query01" jdbcDriver="<%=driver%>" jdbcUrl="<%=url%>" jdbcUser="<%=usr%>" jdbcPassword="<%=pwd%>" catalogUri="<%=catalogUri%>" >
				<%=mdxQuery%>
			</jp:mondrianQuery>	
			<%
					} else if(type.equalsIgnoreCase("xmla")) {
					catalogUri = connectionDef.valueOf("@xmlaServerUrl");
					session.setAttribute("catalogUri", catalogUri);
				%>
				<jp:xmlaQuery id="query01"
			    		uri="<%=catalogUri%>" 
			    		catalog="<%=schema%>" >
					<%=mdxQuery%>
				</jp:xmlaQuery>
			<%
				}
				}
				TemplateBean templateBean = (TemplateBean) session.getAttribute("saveTemplate01");
				if (templateBean == null) {
					templateBean = new TemplateBean();
					session.setAttribute("saveTemplate01", templateBean);
				}
				Object formObj = session.getAttribute("saveTemplateForm01");
				if (formObj != null) {
					FormComponent form = (FormComponent) formObj;
					form.setBean(templateBean);
				}
			%>
	
	<jp:table id="table01" query="#{query01}"/>
	<jp:navigator id="navi01" query="#{query01}" visible="true"/>
	<wcf:form id="saveTemplateForm01" xmlUri="/WEB-INF/jpivot/table/saveTemplateTable.xml" model="#{saveTemplate01}" visible="false"/>
	<wcf:toolbar id="toolbar01" bundle="com.tonbeller.jpivot.toolbar.resources">
		<wcf:scriptbutton id="cubeNaviButton" tooltip="toolb.cube" img="cube" model="#{navi01.visible}"/>
		<wcf:scriptbutton id="tableButton" tooltip="toolb.table" img="table" model="#{table01.visible}"/>
		<wcf:scriptbutton id="saveTemplate" tooltip="toolb.saveTemplate" img="save" model="#{saveTemplateForm01.visible}"/>
		<wcf:imgbutton id="addParameters" tooltip="toolb.addParameters" img="mdx-edit" href="./addParameters.jsp"/>
	</wcf:toolbar>
	<p>
	<wcf:render ref="toolbar01" xslUri="/WEB-INF/jpivot/toolbar/htoolbar.xsl" xslCache="true"/>
	<p>
	<wcf:render ref="saveTemplateForm01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
	<%-- if there was an overflow, show error message --%>
	<c:if test="${query01.result.overflowOccured}">
 			<p>
 				<strong style="color:red">Resultset overflow occured</strong>
 			<p>
	</c:if>
	<p>
	<wcf:render ref="navi01" xslUri="/WEB-INF/jpivot/navi/navigator.xsl" xslCache="true"/>
	<p>
	<wcf:render ref="table01" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>
	<p>

	<%--
	<jsp:forward page="/initialQuery.jsp"/>
	--%>
	<%
} else {
	%>
	<form action="editQuery.jsp" method="post" name="chooseConnectionForm" id="chooseConnectionForm">
		<%
		List connections = documentConfigFile.selectNodes("//ENGINE-CONFIGURATION/CONNECTIONS-CONFIGURATION/CONNECTION");
		if (connections == null || connections.size() == 0) {
			out.write("No connections defined in engine-config.xml file.");
			return;
		}
		%>
		<div style="float:left;clear:left;width:150px;height:25px;margin:5px;">
			<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
				<%=EngineMessageBundle.getMessage("edit.query.select.connection", locale)%>
			</span>
		</div>
		<div style="height:25px;margin:5px;">
			<select name="connection" id="connection">
				<%
				Iterator connectionsIt = connections.iterator();
				Node selectedConnectionNode = null;
				while (connectionsIt.hasNext()) {
					Node aConnection = (Node) connectionsIt.next();
					String aConnectionName = aConnection.valueOf("@name");
					String isConnectionSelected = "";
					if (aConnection.valueOf("@isDefault").trim().equalsIgnoreCase("true")) {
						selectedConnectionNode = aConnection;
						isConnectionSelected = "selected='selected'";
					}
					%>
					<option value="<%=aConnectionName%>" <%=isConnectionSelected%>><%=aConnectionName%></option>
					<%
				}
				%>
			</select>&nbsp;&nbsp;&nbsp;<input type="image" src="wcf/form/ok.png" title="Ok" alt="Ok" />
		</div>
	</form>
	<%
}
%>

</body>
</html>