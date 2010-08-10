<%--

LICENSE: see LICENSE.txt file 

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
<%@page import="java.util.ArrayList"%>
<%@page import="sun.misc.BASE64Decoder"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="it.eng.spagobi.jpivotaddins.bean.TemplateBean"%>
<%@page import="com.tonbeller.wcf.form.FormComponent"%>
<%@page import="com.tonbeller.jpivot.olap.model.OlapModel"%>
<%@page import="java.util.HashMap"%>
<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.services.proxy.DataSourceServiceProxy"%>
<%@page import="it.eng.spagobi.services.datasource.bo.SpagoBiDataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="it.eng.spagobi.services.proxy.ContentServiceProxy"%>
<%@page import="it.eng.spagobi.services.content.bo.Content"%>
<%@page import="it.eng.spagobi.tools.datasource.bo.*"%>
<%@page import="it.eng.spagobi.services.common.EnginConf" %>
<%@page import="org.apache.log4j.Logger" %>

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
Logger logger = Logger.getLogger(this.getClass());
// retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

// puts in session the spagobi content repository servlet url 
// and the document path for TemplateBean.saveTemplate method
// and other objects avoiding unuseful http reuqest parameters
String biobjectPath = request.getParameter("biobject_path");
if (biobjectPath != null) session.setAttribute("biobject_path", biobjectPath);
//String spagobiurl = request.getParameter("spagobiurl");
//if (spagobiurl != null) session.setAttribute("spagobiurl", spagobiurl);
String documentId = request.getParameter("document");
if (documentId != null) session.setAttribute("document", documentId);
if (documentId == null) documentId = (String) session.getAttribute("document");


SAXReader readerConfigFile = new SAXReader();
Document documentConfigFile = readerConfigFile.read(getClass().getResourceAsStream("/engine-config.xml"));
String userId = (String)session.getAttribute("userId");
ContentServiceProxy contentProxy = new ContentServiceProxy(userId, session);

String schema = request.getParameter("schema");
if (schema == null) schema = (String) session.getAttribute("schema");

if (schema != null && !schema.trim().equals("")) {
	session.setAttribute("schema", schema);
	session.setAttribute("selectedSchema", schema);
	
//gets the schema node from engine-config.xml
List schemas = (List) session.getAttribute("schemas");
Node selectedSchemaNode = null;
if (schemas == null) {
	schemas = documentConfigFile.selectNodes("//ENGINE-CONFIGURATION/SCHEMAS/SCHEMA");
	session.setAttribute("schemas", schemas);
	
	Iterator it = schemas.iterator();
	while (it.hasNext()) {
		Node aSchema = (Node) it.next();
		String aSchemaName = aSchema.valueOf("@name");
		if (aSchemaName.equalsIgnoreCase(schema)) {
			selectedSchemaNode = aSchema;
		}
	}
}



%>
	<form action="editQuery.jsp" method="post">
	<%
		OlapModel om = (OlapModel) session.getAttribute("query01");
		if (om == null) {			
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			//String templateBase64Coded = (String) session.getAttribute("template");
			//byte[] templateContent = bASE64Decoder.decodeBuffer(templateBase64Coded);
			//ByteArrayInputStream is = new ByteArrayInputStream(templateContent);			
			Content template = contentProxy.readTemplate(documentId, new HashMap());
			byte[] templateContent = bASE64Decoder.decodeBuffer(template.getContent());
			ByteArrayInputStream is = new java.io.ByteArrayInputStream(templateContent);

			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			
		    // Read data access information and put it in session...
		    Node dataAccessNode = document.selectSingleNode("//olap/DATA-ACCESS");
		    String filters = null;
		    if (dataAccessNode != null) filters = dataAccessNode.getStringValue();
		    session.setAttribute("filters",filters);
		    
		    
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
			//String catalogUri = cube.valueOf("@reference");
			String catalogUri = EnginConf.getInstance().getResourcePath() + 
								selectedSchemaNode.valueOf("@catalogUri").replace("/", System.getProperty("file.separator"));
			
			// puts the catalogUri in session for TemplateBean.saveTemplate() method
			session.setAttribute("catalogUri", catalogUri);
			/*
			TemplateBean templateBean = new TemplateBean();
			templateName = (String) session.getAttribute("templateName");
			templateBean.setTemplateName(templateName);
			session.setAttribute("saveTemplate01", templateBean);
			*/
			//gets datasource
			DataSourceServiceProxy proxyDS = new DataSourceServiceProxy(userId, session);		
			//String userId=request.getParameter("user");
			IDataSource datasource = proxyDS.getDataSource(documentId);		
			if (datasource == null) {
				out.write("Connection not defined as data source in table SBI_DATA_SOURCE .");
				return;
			}
			
			// adjust reference
			if (!catalogUri.startsWith("file:")) {
				catalogUri = "file:" + catalogUri;
				logger.debug("Reference changed to " + catalogUri);
			}
			
			String resName = datasource.getJndi();
			if (resName != null && !resName.equals("")) {
				//resName = resName.replace("java:comp/env/","");
			    String connectionStr = "Provider=mondrian;"+resName+";Catalog="+catalogUri+";";
			%>
			<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=catalogUri%>">
				<%=mdxQuery%>
			</jp:mondrianQuery>
			<%
			} else {
				String driver = datasource.getDriver();
				String url = datasource.getUrlConnection();
				String usr = datasource.getUser();
				String pwd = datasource.getPwd();
				String connectionStr = "Provider=mondrian;JdbcDrivers="+driver+";Jdbc="+url+";JdbcUser="+usr+";JdbcPassword="+pwd+";Catalog="+catalogUri+";";				
				%>
			    <jp:mondrianQuery id="query01" jdbcDriver="<%=driver%>" jdbcUrl="<%=url%>" jdbcUser="<%=usr%>" jdbcPassword="<%=pwd%>" catalogUri="<%=catalogUri%>" >
					<%=mdxQuery%>
				</jp:mondrianQuery>	
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
	<form action="editQuery.jsp" method="post" name="chooseSchemaForm" id="chooseSchemaForm">
		<%
		String selectedSchema = (String) session.getAttribute("selectedSchema");
		List schemas = documentConfigFile.selectNodes("//ENGINE-CONFIGURATION/SCHEMAS/SCHEMA");
		if (schemas == null || schemas.size() == 0) {
			out.write("No schemas defined in engine-config.xml file.");
			return;
		}		
		%>
		<div style="float:left;clear:left;width:150px;height:25px;margin:5px;">
			<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
				<%=EngineMessageBundle.getMessage("edit.query.select.schema", locale)%>
			</span>
		</div>
		<div style="height:25px;margin:5px;">
			<select name="schema" id="schema">
				<%				
				Iterator schemasIt = schemas.iterator();
				Node selectedSchemaNode = null;
				while (schemasIt.hasNext()) {
					Node aSchema = (Node) schemasIt.next();
					String aSchemaName = aSchema.valueOf("@name");
					String isSchemaSelected = "";
					if (aSchemaName.equalsIgnoreCase(selectedSchema)) {
						selectedSchemaNode = aSchema;
						isSchemaSelected = "selected='selected'";
					}					
					%>
					<option value="<%=aSchemaName%>" <%=isSchemaSelected%>><%=aSchemaName%></option>
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