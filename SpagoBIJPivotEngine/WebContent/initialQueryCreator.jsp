<%--

LICENSE: see LICENSE.txt file 

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
<%@page import="java.util.Iterator"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="mondrian.olap.MondrianDef.VirtualCube"%>
<%@page import="it.eng.spagobi.jpivotaddins.bean.TemplateBean"%>
<%@page import="com.tonbeller.wcf.form.FormComponent"%>
<%@page import="com.tonbeller.jpivot.olap.model.OlapModel"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>
<%@page import="it.eng.spagobi.services.proxy.DataSourceServiceProxy"%>
<%@page import="it.eng.spagobi.services.datasource.bo.SpagoBiDataSource"%>
<%@page import="it.eng.spagobi.tools.datasource.bo.*"%>
<%@page import="it.eng.spagobi.services.common.EnginConf" %>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spago.security.*" %>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>
<%@page import="it.eng.spagobi.services.artifact.bo.SpagoBIArtifact"%>
<%@page import="it.eng.spagobi.services.proxy.ArtifactServiceProxy"%>
<%@page import="it.eng.spagobi.jpivotaddins.schema.MondrianSchemaManager"%>

<%!
    private Logger logger = Logger.getLogger("it.eng.spagobi.initialQueryCreator_jsp");
%>

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
String userId = (String) session.getAttribute("userId");
String documentId = (String) session.getAttribute("document");
ArtifactServiceProxy artifactProxy = new ArtifactServiceProxy(userId, session);
MondrianSchemaManager schemaManager = new MondrianSchemaManager(artifactProxy);

//retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

String action = request.getParameter("action");
if (action != null && !action.equals("")) {
	if (action.equalsIgnoreCase("selectSchema")) {
		session.removeAttribute("MondrianCubes");
		session.removeAttribute("MondrianVirtualCubes");
		session.removeAttribute("query01");
		session.removeAttribute("navi01");
		session.removeAttribute("table01");
		session.removeAttribute("selectedCube");
		String schemaSelected = request.getParameter("schema");
		session.setAttribute("reference", schemaSelected);
	}
	if (action.equalsIgnoreCase("selectCube")) {
		session.removeAttribute("query01");
		session.removeAttribute("navi01");
		session.removeAttribute("table01");
		String cubeSelected = request.getParameter("cube");
		session.setAttribute("selectedCube", cubeSelected);
	}
}

SpagoBIArtifact[] schemas = (SpagoBIArtifact[]) session.getAttribute("schemas");
if (schemas == null) {
    schemas = artifactProxy.getArtifactsByType(SpagoBIConstants.MONDRIAN_SCHEMA);
    session.setAttribute("schemas", schemas);
}

if (schemas == null || schemas.length == 0) {
    out.write("No schemas defined in Mondrian schemas' catalogue.");
    return;
}

%>
<p>
<%

%>
<div style="margin: 0 0 5 5;">
	<div style="float:left;clear:left;width:150px;height:25px;">
		<span style="font-family: Verdana,Geneva,Arial,Helvetica,sans-serif;color: #074B88;font-size: 8pt;">
			<%= EngineMessageBundle.getMessage("query.creation.select.schema", locale) %>
		</span>
	</div>
	<div style="height:25px;">
		<select name="schema" id="schema" style="width:200px" 
			onchange="document.getElementById('action').value='selectSchema';document.getElementById('initialQueryForm').submit()">
			    <%
				String selectedSchema = (String) session.getAttribute("reference");
				if (selectedSchema == null) {
			        %>
				    <option value="" selected="selected">&nbsp;</option>
				    <%
				}
				
				SpagoBIArtifact artifact = null;
				for (int i = 0; i < schemas.length; i++) {
					SpagoBIArtifact anArtifact = schemas[i];
					String aSchemaName = anArtifact.getName();
					String isSchemaSelected = "";
                    if (aSchemaName.equalsIgnoreCase(selectedSchema)) {
                    	artifact = anArtifact;
                        isSchemaSelected = "selected='selected'";
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
	String catalogUri = schemaManager.getMondrianSchemaURI(artifact.getContentId());
	MondrianDef.Cube[] cubes = (MondrianDef.Cube[]) session.getAttribute("MondrianCubes");
	MondrianDef.VirtualCube[] virtualcubes = (MondrianDef.VirtualCube[]) 
				session.getAttribute("MondrianVirtualCubes");
	boolean cubeWasSelected = true;
	if (cubes == null) {
		cubeWasSelected = false;
		Parser xmlParser = XOMUtil.createDefaultParser();
		//URL catalogURL = (this.getServletContext().getResource(catalogUri));
		//URL catalogURL = (this.getServletContext().getResource(catalogURLStr));
		//MondrianDef.Schema schema = new MondrianDef.Schema(xmlParser.parse(catalogURL));
		File tmpFile = new File(catalogUri);
		FileInputStream fis = new FileInputStream(tmpFile);
		MondrianDef.Schema schema = new MondrianDef.Schema(xmlParser.parse(fis));
		cubes = schema.cubes;
		virtualcubes = schema.virtualCubes;
		session.setAttribute("MondrianCubes", cubes);
		session.setAttribute("MondrianVirtualCubes", virtualcubes);
	}
	if ((cubes == null || cubes.length == 0) && (virtualcubes == null || virtualcubes.length == 0)) {
		out.write("No cubes defined in " + artifact.getName() + " schema.");
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
							MondrianDef.Cube selectedCube = null;
							for (int i = 0; i < cubes.length; i++) {
								MondrianDef.Cube aCube = cubes[i];
								String isCubeSelected = "";
								if (cubeWasSelected && aCube.name.equalsIgnoreCase(selectedCubeName)) {
							selectedCube = aCube;
							isCubeSelected = "selected='selected'";
								}
					%>
					<option value="<%=aCube.name%>" <%=isCubeSelected%>><%=aCube.name%></option>
					<%
							}
							MondrianDef.VirtualCube selectedVirtualCube = null;
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
				MondrianDef.CubeDimension dimension = selectedCube.dimensions[0];
				MondrianDef.Measure measure = selectedCube.measures[0];
				mdxQuery = "select {[Measures].[" + measure.name + "]} on columns, {([" + dimension.name + "])} on rows from [" + selectedCube.name + "]";
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
			
			DataSourceServiceProxy proxyDS = new DataSourceServiceProxy(userId, session);
			IDataSource ds = proxyDS.getDataSource( documentId );
			if (ds == null || (ds.getJndi()==null && (ds.getDriver()==null || 
				ds.getUrlConnection() == null || ds.getUser()==null || ds.getPwd()==null))){
			%>
			<p>
  				<strong style="color:red">Data Source is not correctly defined</strong>
  			<p>
			<%
			}
			else {
					// execute initial query
					String resName =(ds.getJndi()==null)?"":ds.getJndi();
					if (ds.checkIsMultiSchema()){
						String schema=null;
						try {
								String attrname=ds.getSchemaAttribute();
								IEngUserProfile profile = (IEngUserProfile) session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
								if (attrname!=null) schema = (String)profile.getUserAttribute(attrname);
								if (schema==null) logger.error("Cannot retrive ENTE");
								else resName=resName+schema;
						} catch (Exception e) {
							logger.error("Cannot retrive ENTE", e);
						}
					}					
					// adjust reference
					if (!catalogUri.startsWith("file:")) {
						catalogUri = "file:" + catalogUri;
						logger.debug("Reference changed to " + catalogUri);
					}
					if (!resName.equals("")) {
					    //resName = resName.replace("java:comp/env/","");
			%>
					<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=catalogUri%>">
						<%=mdxQuery%>
					</jp:mondrianQuery>
					<%
						} else {
							String driver = (ds.getDriver()==null)?"":ds.getDriver();
							String url = (ds.getUrlConnection()==null)?"":ds.getUrlConnection();
							String usr = (ds.getUser()==null)?"":ds.getUser();
							String pwd = (ds.getPwd()==null)?"":ds.getPwd();
					%>
				    <jp:mondrianQuery id="query01" jdbcDriver="<%=driver%>" jdbcUrl="<%=url%>" jdbcUser="<%=usr%>" jdbcPassword="<%=pwd%>" catalogUri="<%=catalogUri%>" >
						<%=mdxQuery%>
					</jp:mondrianQuery>	
			<%
				}
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