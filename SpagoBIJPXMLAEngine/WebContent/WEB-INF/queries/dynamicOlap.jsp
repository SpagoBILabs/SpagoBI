<%--
/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
--%>
<%@ page session="true" 
         contentType="text/html; charset=UTF-8" 
		 import="org.dom4j.Document,org.dom4j.Node,java.io.InputStreamReader,java.util.List,com.thoughtworks.xstream.XStream,it.eng.spagobi.jpivotaddins.bean.AnalysisBean,it.eng.spagobi.utilities.SpagoBIAccessUtils,it.eng.spagobi.jpivotaddins.util.ParameterSetter,it.eng.spagobi.jpivotaddins.util.ParameterHandler,it.eng.spagobi.jpivotaddins.engines.jpivotxmla.conf.EngineXMLAConf,it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection.*,it.eng.spagobi.jpivotaddins.bean.SaveAnalysisBean,it.eng.spagobi.tools.datasource.bo.*,it.eng.spagobi.services.proxy.DataSourceServiceProxy,com.tonbeller.wcf.form.FormComponent,java.io.InputStream,mondrian.olap.*" %>
<%@page import="sun.misc.BASE64Decoder"%>
<%@page import="java.util.Map"%>
<%@page import="it.eng.spagobi.utilities.ParametersDecoder"%>
<%@page import="com.tonbeller.jpivot.mondrian.ScriptableMondrianDrillThrough"%>
<%@page import="com.tonbeller.jpivot.olap.model.OlapModel"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Set"%>
<%@page import="com.tonbeller.jpivot.mondrian.MondrianDimension"%>
<%@page import="com.tonbeller.jpivot.mondrian.MondrianHierarchy"%>
<%@page import="it.eng.spagobi.services.proxy.ContentServiceProxy"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="it.eng.spagobi.services.content.bo.Content"%>


<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%!private static String CONNECTION_NAME = "connectionName";%>

<%
	IEngUserProfile userProfile = (IEngUserProfile) session
			.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
	String userUniqueIdentifier = "";

	if (userProfile != null) {
		userUniqueIdentifier = (String) userProfile
				.getUserUniqueIdentifier();
	}
	Logger logger = Logger
			.getLogger("it.eng.spagobi.jpivotaddins.engines.jpivotxmla");

	List parameters = null;
	InputStream is = null;
	String reference = null;
	String name = null;
	String nameConnection = null;
	String query = null;
	AnalysisBean analysis = null;
	String user = null;
	String documentId = null;
	try {
		SaveAnalysisBean analysisBean = (SaveAnalysisBean) session
				.getAttribute("save01");
		String nameSubObject = request.getParameter("nameSubObject");
		user = (String) session.getAttribute("user");
		documentId = (String) session.getAttribute("document");
		if (user == null) {
			user = (String) session.getAttribute("userId");
		}
		// if into the request is defined the attribute "nameSubObject" the engine must run a subQuery
		if (nameSubObject != null) {
			String jcrPath = (String) session
					.getAttribute("templatePath");
			String spagoBIBaseUrl = (String) session
					.getAttribute("spagobiurl");
			// if subObject execution in the request there are the description and visibility
			String descrSO = request
					.getParameter("descriptionSubObject");
			if (descrSO == null) {
				descrSO = "";
			}
			String visSO = request.getParameter("visibilitySubObject");
			if (visSO == null) {
				visSO = "Private";
			}

			analysisBean.setAnalysisName(nameSubObject);
			analysisBean.setAnalysisDescription(descrSO);
			// the possible values of the visibility are (Private/Public)
			analysisBean.setAnalysisVisibility(visSO);

			//calls service for gets data source object		
			String requestConnectionName = (String) request
					.getParameter(CONNECTION_NAME);
			DataSourceServiceProxy proxyDS = new DataSourceServiceProxy(
					user, session);
			IDataSource ds = null;
			if (requestConnectionName != null) {
				ds = proxyDS
						.getDataSourceByLabel(requestConnectionName);
			} else {
				ds = proxyDS.getDataSource(documentId);
			}
			ContentServiceProxy proxy = new ContentServiceProxy(
					userUniqueIdentifier, session);
			String subObjectId = request.getParameter("subobjectId");
			Content subObject = proxy.readSubObjectContent(subObjectId);
			String subobjdata64Coded = subObject.getContent();
			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			byte[] subobjBytes = bASE64Decoder
					.decodeBuffer(subobjdata64Coded);
			is = new java.io.ByteArrayInputStream(subobjBytes);
			InputStreamReader isr = new InputStreamReader(is);
			XStream dataBinder = new XStream();
			try {
				analysis = (AnalysisBean) dataBinder.fromXML(isr,
						new AnalysisBean());
				isr.close();
				query = analysis.getMdxQuery();
				nameConnection = analysis.getConnectionName();
				name = analysis.getCatalog();
				//sets the datasource of document
				if (ds != null)
					nameConnection = ds.getLabel();
				//nameConnection = analysis.getConnectionName();
				reference = analysis.getCatalogUri();
				logger.debug("Reference: " + reference);
			} catch (Throwable t) {
				t.printStackTrace();
			}

		} else {
			// normal execution (no subObject)	

			logger.debug("Starting a new execution ...");

			BASE64Decoder bASE64Decoder = new BASE64Decoder();
			HashMap requestParameters = ParametersDecoder
					.getDecodedRequestParameters(request);
			logger.debug("Loading template ...");
			ContentServiceProxy contentProxy = new ContentServiceProxy(
					user, session);
			Content template = contentProxy.readTemplate(documentId,
					requestParameters);
			logger.debug("... template loaded succesfully");

			logger.debug("Parsing template ...");
			byte[] templateContent = bASE64Decoder
					.decodeBuffer(template.getContent());
			is = new java.io.ByteArrayInputStream(templateContent);
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			Document document = reader.read(is);
			logger.debug("... template parsed succesfully");

			if (request.getParameter(CONNECTION_NAME) != null) {
				nameConnection = request.getParameter(CONNECTION_NAME);

			} else {

				Node connectionString = document.selectSingleNode("//olap/connection");

				if (connectionString != null) {
					nameConnection = connectionString.valueOf("@"+ CONNECTION_NAME);
				} else {
					nameConnection = null;
				}

			}

			logger.debug("Connection name [" + nameConnection + "]");

			// nameConnection = request.getParameter(CONNECTION_NAME);
			// logger.debug("Connection name [" + nameConnection + "]");

			query = document.selectSingleNode("//olap/MDXquery")
					.getStringValue();
			logger.debug("Query [" + query + "]");

			Node cube = document.selectSingleNode("//olap/cube");
			reference = cube.valueOf("@reference");
			logger.debug("Cube reference [" + reference + "]");
			name = cube.valueOf("@name");
			logger.debug("Cube name [" + name + "]");

			logger.debug("Reading parameters ...");
			parameters = document
					.selectNodes("//olap/MDXquery/parameter");
			logger.debug("... parameters readed succesfully");

			analysis = new AnalysisBean();
			analysis.setConnectionName(nameConnection);
			analysis.setCatalogUri(reference);
			analysis.setCatalog(name);
			session.setAttribute("analysisBean", analysis);
		}

		logger.debug("Replacing parameters in query ...");
		ParameterHandler parameterHandler = ParameterHandler
				.getInstance();
		query = parameterHandler.substituteQueryParameters(query,
				parameters, request);
		logger.debug("... parameters replacesd succesfully");

		IConnection connection = null;
		if (nameConnection != null) {
			connection = EngineXMLAConf.getInstance().getConnection(
					nameConnection);
		} else {
			connection = EngineXMLAConf.getInstance()
					.getDefaultConnection();
		}

		logger.debug("executing query ...");

		if (connection.getType() == IConnection.XMLA_CONNECTION) {
			XMLAConnection xmlaConnection = (XMLAConnection) connection;
%>				 
	
	<jp:xmlaQuery id="query01"
			      uri="<%=xmlaConnection.getXmlaServerUrl()%>" 
				  catalog="<%=reference%>" >
					<%=query%>
	</jp:xmlaQuery>
			
<%
				} else {
						logger
								.debug("The engine is able to handle only XMLA connection");
					}

					if (nameSubObject != null) {
						session.setAttribute("analysisBean", analysis);
			%>
				<jsp:include page="customizeAnalysis.jsp"/>
			<%
				}

				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
			%>