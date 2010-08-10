<%--

LICENSE: see LICENSE.html file 

--%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@page import="mondrian.olap.MondrianDef"%>
<%@page import="mondrian.olap.MondrianDef.Schema"%>
<%@page import="mondrian.olap.MondrianDef.Cube"%>
<%@page import="org.eigenbase.xom.XOMUtil"%>
<%@page import="org.eigenbase.xom.Parser"%>
<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.Node"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.net.URL"%>
<%@page import="mondrian.olap.MondrianDef.VirtualCube"%>
<%@page import="it.eng.spagobi.jpivotaddins.bean.TemplateBean"%>
<%@page import="com.tonbeller.wcf.form.FormComponent"%>
<%@page import="com.tonbeller.jpivot.olap.model.*"%>
<%@page import="com.tonbeller.jpivot.xmla.*"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>

<html>
<head>
  <title>Initial query creation</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="jpivot/table/mdxtable.css">
  <link rel="stylesheet" type="text/css" href="jpivot/navi/mdxnavi.css">
  <link rel="stylesheet" type="text/css" href="wcf/form/xform.css">
  <link rel="stylesheet" type="text/css" href="wcf/table/xtable.css">
  <link rel="stylesheet" type="text/css" href="wcf/tree/xtree.css">
</head>

<body bgcolor=white lang="en">

<form action="initialQueryCreator.jsp" method="post" name="initialQueryForm" id="initialQueryForm">
<input type="hidden" name="action" id="action" value="" />

<%
//retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

//puts in session the spagobi content repository servlet url 
//and the document path for TemplateBean.saveTemplate method
String biobjectPath = request.getParameter("biobject_path");
if (biobjectPath != null) session.setAttribute("biobject_path", biobjectPath);
String spagobiurl = request.getParameter("spagobiurl");
if (spagobiurl != null) session.setAttribute("spagobiurl", spagobiurl);

String action = request.getParameter("action");
if (action != null && !action.equals("")) {
	if (action.equalsIgnoreCase("selectConnection")) {
		session.removeAttribute("MondrianCubes");
		session.removeAttribute("MondrianCubez");
		session.removeAttribute("MondrianVirtualCubes");
		session.removeAttribute("query01");
		session.removeAttribute("navi01");
		session.removeAttribute("table01");
		session.removeAttribute("selectedSchema");
		session.removeAttribute("selectedCube");
		String connectionSelected = request.getParameter("connection");
		session.setAttribute("selectedConnection", connectionSelected);
	}
	if (action.equalsIgnoreCase("selectSchema")) {
		session.removeAttribute("MondrianCubes");
		session.removeAttribute("MondrianCubez");
		session.removeAttribute("MondrianVirtualCubes");
		session.removeAttribute("query01");
		session.removeAttribute("navi01");
		session.removeAttribute("table01");
		session.removeAttribute("selectedCube");
		String schemaSelected = request.getParameter("schema");
		session.setAttribute("selectedSchema", schemaSelected);
	}
	if (action.equalsIgnoreCase("selectCube")) {
		session.removeAttribute("query01");
		session.removeAttribute("navi01");
		session.removeAttribute("table01");
		String cubeSelected = request.getParameter("cube");
		session.setAttribute("selectedCube", cubeSelected);
	}
}

List schemas = (List) session.getAttribute("schemas");
List connections = (List) session.getAttribute("connections");
if (connections == null) {
	SAXReader readerConfigFile = new SAXReader();
	Document documentConfigFile = readerConfigFile.read(getClass().getResourceAsStream("/engine-config.xml"));
	connections = documentConfigFile.selectNodes("//ENGINE-CONFIGURATION/CONNECTIONS-CONFIGURATION/CONNECTION");
	session.setAttribute("connections", connections);
}

if (connections == null || connections.size() == 0) {
	out.write("No connections defined in engine-config.xml file.");
	return;
}
%>
<p>
<div style="margin: 0 0 5 5;">
	<div style="float:left;clear:left;width:150px;height:25px;">
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
			<%=EngineMessageBundle.getMessage("query.creation.select.connection", locale)%>
		</span>
	</div>
	<div style="height:25px;">
		<select name="connection" id="connection" style="width:200px" 
			onchange="document.getElementById('action').value='selectConnection';document.getElementById('initialQueryForm').submit()">
			<%
				String selectedConnection = (String) session.getAttribute("selectedConnection");
				Node selectedConnectionNode = null;
				
				Iterator connectionsIt = connections.iterator();
				
				while (connectionsIt.hasNext()) {
					Node aConnection = (Node) connectionsIt.next();
					if(selectedConnectionNode == null) {
						selectedConnectionNode = aConnection;
					}
					String aConnectionName = aConnection.valueOf("@name");
					String isConnectionSelected = "";
					if (selectedConnection == null) {
						if (aConnection.valueOf("@isDefault").trim().equalsIgnoreCase("true")) {
					selectedConnectionNode = aConnection;
					isConnectionSelected = "selected='selected'";
						}
					} else {
						if (aConnectionName.equalsIgnoreCase(selectedConnection)) {
					selectedConnectionNode = aConnection;
					isConnectionSelected = "selected='selected'";
						}
					}
			%>
				<option value="<%=aConnectionName%>" <%=isConnectionSelected%>><%=aConnectionName%></option>
				<%
					}
					session.setAttribute("selectedConnectionNode", selectedConnectionNode);
				%>
		</select>
	</div>
</div>
<%
	String selectedSchema = (String) session.getAttribute("selectedSchema");
	Node selectedSchemaNode = null;
	String type = selectedConnectionNode.valueOf("@type");
	if (schemas == null) {
		if(type.equalsIgnoreCase("xmla")) {
	XMLA_SOAP olapServer = new XMLA_SOAP(selectedConnectionNode.valueOf("@xmlaServerUrl"), "", "");
	schemas = olapServer.discoverCat();
		} else {	
		SAXReader readerConfigFile = new SAXReader();
		Document documentConfigFile = readerConfigFile.read(getClass().getResourceAsStream("/engine-config.xml"));
		schemas = documentConfigFile.selectNodes("//ENGINE-CONFIGURATION/SCHEMAS/SCHEMA");
		session.setAttribute("schemas", schemas);
		}		
	}
	
	if (schemas == null || schemas.size() == 0) {
		out.write("Cannot retrive schemas");
		return;
	}
%>


<div style="margin: 0 0 5 5;">
	<div style="float:left;clear:left;width:150px;height:25px;">
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
			<%=EngineMessageBundle.getMessage("query.creation.select.schema", locale)%>
		</span>
	</div>
	<div style="height:25px;">
		<select name="schema" id="schema" style="width:200px" 
			onchange="document.getElementById('action').value='selectSchema';document.getElementById('initialQueryForm').submit()">
			<%
			if (selectedSchema == null) {
			%>
				<option value="" selected="selected">&nbsp;</option>
				<%
					}
					String selectedSchemaName = null;
					String selectedCatalogUri = null;
					Iterator it = schemas.iterator();
					while (it.hasNext()) {
						String aSchemaName = "";
						String isSchemaSelected = "";
						if(type.equalsIgnoreCase("xmla")) {
							OlapItem aSchema = (OlapItem) it.next();
							aSchemaName = aSchema.getName();
							if (aSchemaName.equalsIgnoreCase(selectedSchema)) {
						selectedSchemaName = aSchemaName;
						isSchemaSelected = "selected='selected'";
							}
						} else {	
							Node aSchema = (Node) it.next();
							aSchemaName = aSchema.valueOf("@name");
							if (aSchemaName.equalsIgnoreCase(selectedSchema)) {
						selectedSchemaName = aSchemaName;
						selectedCatalogUri = aSchema.valueOf("@catalogUri");
						isSchemaSelected = "selected='selected'";
							}
						}
				%>
				<option value="<%=aSchemaName%>" <%=isSchemaSelected%>><%=aSchemaName%></option>
				<%
				}
				%>
		</select>
	</div>
</div>
<%
	if (selectedSchema != null) {
	List cubes = (List) session.getAttribute("MondrianCubes");
	MondrianDef.Cube[] cubez = (MondrianDef.Cube[]) session.getAttribute("MondrianCubez");
	MondrianDef.VirtualCube[] virtualcubes = (MondrianDef.VirtualCube[]) 
				session.getAttribute("MondrianVirtualCubes");
	String catalogUri = (String)session.getAttribute("catalogUri");
	
	boolean cubeWasSelected = true;
	if (cubes == null) {
		cubeWasSelected = false;
		
		cubes = new ArrayList();
		
		if(type.equalsIgnoreCase("xmla")) {
	catalogUri = selectedConnectionNode.valueOf("@xmlaServerUrl");
	XMLA_SOAP olapServer = new XMLA_SOAP(catalogUri, "", "");
	List olapItems = olapServer.discoverCube(selectedSchema);
	for(int i = 0; i < olapItems.size(); i++) {
		OlapItem oi = (OlapItem)olapItems.get(i);
		cubes.add(oi.getName());
	}
		} else {	
	catalogUri = selectedCatalogUri;
	Parser xmlParser = XOMUtil.createDefaultParser();
	URL catalogURL = this.getServletContext().getResource(catalogUri);
	MondrianDef.Schema schema = new MondrianDef.Schema(xmlParser.parse(catalogURL));
		    cubez = schema.cubes;
	for(int i = 0; i < cubez.length; i++) {
		String name = cubez[i].name;
		cubes.add(name);
	}
	virtualcubes = schema.virtualCubes;		
		}
	}
	
	session.setAttribute("MondrianCubes", cubes);
	session.setAttribute("MondrianCubez", cubez);
	session.setAttribute("MondrianVirtualCubes", virtualcubes);
	
	if ((cubes == null || cubes.size() == 0) && (virtualcubes == null || virtualcubes.length == 0)) {
		out.write("No cubes defined in " + selectedSchema.valueOf("@name") + " schema.");
		return;
	}
%>
	<div style="margin: 0 0 5 5;">
		<div style="float:left;clear:left;width:150px;height:25px;">
			<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
				<%=EngineMessageBundle.getMessage("query.creation.select.cube", locale)%>
			</span>
		</div>
		<div style="height:25px;">
			<select name="cube" id="cube" style="width:200px" 
				onchange="document.getElementById('action').value='selectCube';document.getElementById('initialQueryForm').submit()">
				<%
				if (!cubeWasSelected) {
				%>
					<option value="" selected="selected">&nbsp;</option>
					<%
							}
							String selectedCubeName = (String) session.getAttribute("selectedCube");
							Object selectedCube = null;
							for (int i = 0; i < cubes.size(); i++) {
								String cube = (String)cubes.get(i);
								String isCubeSelected = "";
								if (cubeWasSelected && cube.equalsIgnoreCase(selectedCubeName)) {
							if(type.equalsIgnoreCase("xmla")) {
								selectedCube = selectedCubeName;
							} else {
								selectedCube = cubez[i];
							}
							
							isCubeSelected = "selected='selected'";
								}
					%>
					<option value="<%=cube%>" <%=isCubeSelected%>><%=cube%></option>
					<%
							}
							MondrianDef.VirtualCube selectedVirtualCube = null;
							if(virtualcubes != null) {
								for (int i = 0; i < virtualcubes.length; i++) {
							MondrianDef.VirtualCube aVirtualCube = virtualcubes[i];
							String isVirtualCubeSelected = "";
							if (cubeWasSelected && aVirtualCube.name.equalsIgnoreCase(selectedCubeName)) {
								selectedVirtualCube = aVirtualCube;
								isVirtualCubeSelected = "selected='selected'";
							}
					%>
						<option value="<%=aVirtualCube.name%>" <%=isVirtualCubeSelected%>><%=aVirtualCube.name%></option>
						<%
								}
								}
						%>
			</select>
		</div>
	</div>
	<%
		OlapModel om = (OlapModel) session.getAttribute("query01");
		if ((selectedCube != null || selectedVirtualCube != null) && om == null) {
			// creates initial mdx query
			String mdxQuery = null;
			if (selectedCube != null) {
		if(type.equalsIgnoreCase("xmla")) {
			catalogUri = selectedConnectionNode.valueOf("@xmlaServerUrl");
			XMLA_SOAP olapServer = new XMLA_SOAP(catalogUri, "", "");
			List olapItems = olapServer.discoverDim(selectedSchema, (String)selectedCube);
			OlapItem dimension = (OlapItem)olapItems.get(1);
			
			olapServer = new XMLA_SOAP(catalogUri, "", "");
			olapItems = olapServer.discoverMem(selectedSchema, (String)selectedCube, "Measures", null, null);
			if(olapItems.size()>0) {
				OlapItem measure = (OlapItem)olapItems.get(0);
				mdxQuery = "select {[Measures].[" + measure.getName() + "]} on columns, {([" + dimension.getName() + "])} on rows from [" +  (String)selectedCube + "]";
			} else {
				mdxQuery = "select {[Measures].DefaultMember} on columns, {([" + dimension.getName() + "])} on rows from [" +  (String)selectedCube + "]";
			}
		} else {
			MondrianDef.CubeDimension dimension = ((MondrianDef.Cube)selectedCube).dimensions[0];
			MondrianDef.Measure measure = ((MondrianDef.Cube)selectedCube).measures[0];
			mdxQuery = "select {[Measures].[" + measure.name + "]} on columns, {([" + dimension.name + "])} on rows from [" + ((MondrianDef.Cube)selectedCube).name + "]";
		
		}
			} else {
		MondrianDef.VirtualCubeDimension dimension = selectedVirtualCube.dimensions[0];
		MondrianDef.VirtualCubeMeasure measure = selectedVirtualCube.measures[0];
		String virtualCubeMeasureName = measure.name;
		String temp = virtualCubeMeasureName.toLowerCase();
		if (temp.startsWith("[measures].[")) {
			virtualCubeMeasureName = virtualCubeMeasureName.substring(12, virtualCubeMeasureName.length() - 1);
		}
		mdxQuery = "select {[Measures].[" + virtualCubeMeasureName + "]} on columns, {([" + dimension.name + "])} on rows from [" + selectedVirtualCube.name + "]";
			}
			
			// puts the catalogUri in session for TemplateBean.saveTemplate() method
			session.setAttribute("catalogUri", catalogUri);
			
			// execute initial query
			if (type.equalsIgnoreCase("jndi")) {
			    String iniCont = selectedConnectionNode.valueOf("@initialContext");
			    String resName = selectedConnectionNode.valueOf("@resourceName");
			    String connectionStr = "Provider=mondrian;DataSource="+iniCont+"/"+resName+";Catalog="+catalogUri+";";
			    catalogUri = selectedCatalogUri;
	%>
			<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=catalogUri%>">
				<%=mdxQuery%>
			</jp:mondrianQuery>
			<%
				} else if (type.equalsIgnoreCase("jdbc")) {
				String driver = selectedConnectionNode.valueOf("@driver");
				String url = selectedConnectionNode.valueOf("@jdbcUrl");
				String usr = selectedConnectionNode.valueOf("@user");
				String pwd = selectedConnectionNode.valueOf("@password");
					    String connectionStr = "Provider=mondrian;JdbcDrivers="+driver+";Jdbc="+url+";JdbcUser="+usr+";JdbcPassword="+pwd+";Catalog="+catalogUri+";";
					    catalogUri = selectedCatalogUri;
			%>
		    <jp:mondrianQuery id="query01" jdbcDriver="<%=driver%>" jdbcUrl="<%=url%>" jdbcUser="<%=usr%>" jdbcPassword="<%=pwd%>" catalogUri="<%=catalogUri%>" >
				<%=mdxQuery%>
			</jp:mondrianQuery>	
			<%
					} else if(type.equalsIgnoreCase("xmla")) {
					catalogUri = selectedConnectionNode.valueOf("@xmlaServerUrl");
				%>
			<jp:xmlaQuery id="query01"
		    		uri="<%=catalogUri%>" 
		    		catalog="<%=selectedSchemaName%>" >
				<%=mdxQuery%>
			</jp:xmlaQuery>
		<%
			}
			}
			om = (OlapModel) session.getAttribute("query01");
			if (om != null) {
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
		<div style="clear:left">
		<p>
		<jp:table id="table01" query="#{query01}"/>
		<jp:navigator id="navi01" query="#{query01}" visible="true"/>
		<wcf:form id="saveTemplateForm01" xmlUri="/WEB-INF/jpivot/table/saveTemplateTable.xml" model="#{saveTemplate01}" visible="false"/>
		<wcf:toolbar id="toolbar01" bundle="com.tonbeller.jpivot.toolbar.resources">
			<wcf:scriptbutton id="cubeNaviButton" tooltip="toolb.cube" img="cube" model="#{navi01.visible}"/>
			<wcf:scriptbutton id="tableButton" tooltip="toolb.table" img="table" model="#{table01.visible}"/>
			<wcf:scriptbutton id="saveTemplate" tooltip="toolb.saveTemplate" img="save" model="#{saveTemplateForm01.visible}"/>
			<wcf:imgbutton id="addParameters" tooltip="toolb.addParameters" img="mdx-edit" href="./addParameters.jsp"/>
		</wcf:toolbar>
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
		</div>
		<%
	}
}
%>
</form>
</body>
</html>