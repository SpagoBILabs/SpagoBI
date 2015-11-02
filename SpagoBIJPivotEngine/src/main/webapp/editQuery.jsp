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
<%@page import="it.eng.spagobi.services.proxy.ContentServiceProxy"%>
<%@page import="it.eng.spagobi.services.content.bo.Content"%>
<%@page import="it.eng.spagobi.tools.datasource.bo.*"%>
<%@page import="org.apache.log4j.Logger" %>
<%@page import="it.eng.spagobi.services.proxy.ArtifactServiceProxy"%>
<%@page import="it.eng.spagobi.jpivotaddins.schema.MondrianSchemaManager"%>
<%@page import="it.eng.spagobi.services.artifact.bo.SpagoBIArtifact"%>
<%@page import="it.eng.spagobi.commons.constants.SpagoBIConstants"%>

<%!
    private Logger logger = Logger.getLogger("it.eng.spagobi.editQuery_jsp");

    private Integer getArtifactVersionId(HttpServletRequest request) {
        try {
            if (request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID) == null 
                    || request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID).trim().equals("")) {
                throw new Exception("Request is missing artifact version id missing");
            }
            Integer id = new Integer(request.getParameter(SpagoBIConstants.SBI_ARTIFACT_VERSION_ID));
            return id;
        } catch (Exception e) {
            logger.error("Error while getting artifact version id", e);
            throw new RuntimeException("Error while getting artifact version id", e);
        }
    }
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
  <link rel="stylesheet" type="text/css" href="css/stili.css">
</head>

<body bgcolor=white lang="en">

<%
// retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();

String documentId = request.getParameter("document");
if (documentId != null) session.setAttribute("document", documentId);
if (documentId == null) documentId = (String) session.getAttribute("document");

String userId = (String) session.getAttribute("userId");
ContentServiceProxy contentProxy = new ContentServiceProxy(userId, session);
ArtifactServiceProxy artifactProxy = new ArtifactServiceProxy(userId, session);
MondrianSchemaManager schemaManager = new MondrianSchemaManager(artifactProxy);

%>
<form action="editQuery.jsp" method="post">
<%
	OlapModel om = (OlapModel) session.getAttribute("query01");
	if (om == null) {
		BASE64Decoder bASE64Decoder = new BASE64Decoder();		
		Content template = contentProxy.readTemplate(documentId, new HashMap());
		byte[] templateContent = bASE64Decoder.decodeBuffer(template.getContent());
		ByteArrayInputStream is = new java.io.ByteArrayInputStream(templateContent);

		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		
		// read schema and put it on session
		Node cubeNode = document.selectSingleNode("//olap/cube");
		String reference = cubeNode.valueOf("@reference");
		session.setAttribute("reference", reference);
		
	    // Read data access information and put it in session...
	    Node dataAccessNode = document.selectSingleNode("//olap/DATA-ACCESS");
	    String filters = null;
	    if (dataAccessNode != null) {
	    	filters = dataAccessNode.getStringValue();
	    }
	    session.setAttribute("filters", filters);
	    
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
			<%= EngineMessageBundle.getMessage("edit.query.parameters.warning", locale) %>
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

		Integer artifactVersionId = this.getArtifactVersionId(request);
		String catalogUri = schemaManager.getMondrianSchemaURI(artifactVersionId);
         // puts the catalogUri in session for TemplateBean.saveTemplate() method
           session.setAttribute("catalogUri", catalogUri);
         // adjust reference
           if (!catalogUri.startsWith("file:")) {
               catalogUri = "file:" + catalogUri;
               logger.debug("Reference changed to " + catalogUri);
           }

		//gets datasource
		DataSourceServiceProxy proxyDS = new DataSourceServiceProxy(userId, session);		
		//String userId=request.getParameter("user");
		IDataSource datasource = proxyDS.getDataSource(documentId);		
		if (datasource == null) {
			out.write("Connection not defined as data source in table SBI_DATA_SOURCE .");
			return;
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
<jp:navigator id="navi01" query="#{query01}" visible="false"/>
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
<%-- 
The following test is commented since it may hide some other errors' details
If there was an overflow, show error message 
<c:if test="${query01.result.overflowOccured}">
	<p>
		<strong style="color:red">Resultset overflow occured</strong>
	<p>
</c:if>
--%>
<p>
<wcf:render ref="navi01" xslUri="/WEB-INF/jpivot/navi/navigator.xsl" xslCache="true"/>
<p>
<wcf:render ref="table01" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>
<p>


</body>
</html>