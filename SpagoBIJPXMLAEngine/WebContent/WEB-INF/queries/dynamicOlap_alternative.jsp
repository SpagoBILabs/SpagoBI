<%--
/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
--%>
<%@ page session="true" 
         contentType="text/html; charset=UTF-8" 
		 import="org.dom4j.Document,
				 org.dom4j.Node,
				 java.io.InputStreamReader,
				 java.util.List,
				 com.thoughtworks.xstream.XStream,it.eng.spagobi.jpivotaddins.bean.AnalysisBean,it.eng.spagobi.jpivotaddins.util.SessionObjectRemoval,it.eng.spagobi.utilities.SpagoBIAccessUtils,it.eng.spagobi.jpivotaddins.bean.SaveAnalysisBean,com.tonbeller.wcf.form.FormComponent,mondrian.olap.*" %>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%-- 
	private String substituteQueryParameters(String queryStr, List parameters, javax.servlet.http.HttpServletRequest request, String connectionStr) {
		Connection connection = DriverManager.getConnection(connectionStr, getServletContext(), true);
		Query queryObj = connection.parseQuery(queryStr);	
		if (parameters != null && parameters.size() > 0) {
	    	for (int i = 0; i < parameters.size(); i++) {
	    		Node parameter = (Node) parameters.get(i);
	    		String name = "";
	    		String as = "";
	    		if (parameter != null) {
	    			name = parameter.valueOf("@name");
	    			as = parameter.valueOf("@as");
	    		}
	    		String parameterValue = request.getParameter(name);
	    		if (parameterValue == null || parameterValue.trim().equals("")) continue;
				queryObj.setParameter(as, parameterValue);
	    	}
	    }
		queryStr = queryObj.toMdx();
		connection.close();
	    return queryStr;
	}
--%>



<%
SessionObjectRemoval.removeSessionObjects(session);

List parameters = null;
try{
	
	java.io.InputStream is = null;
	String reference = null, nameConnection = null, query= null;
	AnalysisBean analysis = null;
	SaveAnalysisBean analysisBean = new SaveAnalysisBean();
	// if into the request is defined the attribute "nameSubObject" the engine must run a subQuery
	String nameSubObject = request.getParameter("nameSubObject");
	if (nameSubObject != null) {
		String jcrPath = (String)session.getAttribute("templatePath");
		String spagoBIBaseUrl = (String)session.getAttribute("spagobiurl");
		String user = (String)session.getAttribute("user");
		// if subObject execution in the request there are the description and visibility
		String descrSO = request.getParameter("descriptionSubObject");
		if(descrSO==null)
	descrSO = "";
		String visSO = request.getParameter("visibilitySubObject");
		if(visSO==null)
	visSO = "Private";
		analysisBean.setAnalysisName(nameSubObject);
		analysisBean.setAnalysisDescription(descrSO);
		// the possible values of the visibility are (Private/Public)
		analysisBean.setAnalysisVisibility(visSO);
		// get content from cms
		SpagoBIAccessUtils sbiUtil = new SpagoBIAccessUtils();
		byte[] jcrContent = sbiUtil.getSubObjectContent(spagoBIBaseUrl, jcrPath, nameSubObject, user);
		is = new java.io.ByteArrayInputStream(jcrContent);
		InputStreamReader isr = new InputStreamReader(is);
		XStream dataBinder = new XStream();
		try {
	analysis = (AnalysisBean) dataBinder.fromXML(isr, new AnalysisBean());
	isr.close();
	query = analysis.getMdxQuery();
	nameConnection = analysis.getConnectionName();
	reference = analysis.getCatalogUri();
		} catch (Throwable t) {
	t.printStackTrace();
		}
	// normal execution (no subObject)	
	} else {
	
		String jcrPath = request.getParameter("templatePath");
		String spagoBIBaseUrl = ( String )request.getParameter("spagobiurl");
		byte[] jcrContent = new it.eng.spagobi.utilities.SpagoBIAccessUtils().getContent(spagoBIBaseUrl,jcrPath);
		is = new java.io.ByteArrayInputStream(jcrContent);
		org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
	    Document document = reader.read(is);
		Node connection = document.selectSingleNode("//olap/connection");
		if(connection != null) {
	nameConnection = connection.valueOf("@name");
		}
		query = document.selectSingleNode("//olap/MDXquery").getStringValue();
		Node cube = document.selectSingleNode("//olap/cube");
		reference = cube.valueOf("@reference");
		parameters = document.selectNodes("//olap/MDXquery/parameter");
		analysis = new AnalysisBean();
		analysis.setConnectionName(nameConnection);
		analysis.setCatalogUri(reference);
		
		session.setAttribute("analysisBean",analysis);
	}
	
	// put in session the analysis file information bean
	session.setAttribute("save01", analysisBean);
	Object formObj = session.getAttribute("saveAnalysis01");
	if (formObj != null) {
		FormComponent form = (FormComponent) formObj;
		form.setBean(analysisBean);
	}	
	
	org.dom4j.io.SAXReader readerConFile = new org.dom4j.io.SAXReader();
	Document documentConFile = readerConFile.read(getClass().getResourceAsStream("/engine-config.xml"));
	Node connectionDef = null;
	if(nameConnection!=null) {
		connectionDef = documentConFile.selectSingleNode("//ENGINE-CONFIGURATION/CONNECTIONS-CONFIGURATION/CONNECTION[@name='"+nameConnection+"'");
	} else {
		connectionDef = documentConFile.selectSingleNode("//ENGINE-CONFIGURATION/CONNECTIONS-CONFIGURATION/CONNECTION[@isDefault='true'");
	}
	
	String jndi = connectionDef.valueOf("@isJNDI");
	
	if(jndi.equalsIgnoreCase("true")) { 
	    String iniCont = connectionDef.valueOf("@initialContext");
	    String resName = connectionDef.valueOf("@resourceName");
	    String connectionStr = "Provider=mondrian;DataSource="+iniCont+"/"+resName+";Catalog="+reference+";";
	    //query = substituteQueryParameters(query, parameters, request, connectionStr);
%>
    	<jp:mondrianQuery id="query01" dataSource="<%=resName%>"  catalogUri="<%=reference%>">
			<%=query%>
		</jp:mondrianQuery>
		<%
	} else {
		String driver = connectionDef.valueOf("@driver");
		String url = connectionDef.valueOf("@jdbcUrl");
		String usr = connectionDef.valueOf("@user");
		String pwd = connectionDef.valueOf("@password");
	    String connectionStr = "Provider=mondrian;JdbcDrivers="+driver+";Jdbc="+url+";JdbcUser="+usr+";JdbcPassword="+pwd+";Catalog="+reference+";";
	    //query = substituteQueryParameters(query, parameters, request, connectionStr);
		%>
	    <jp:mondrianQuery id="query01" jdbcDriver="<%=driver%>" jdbcUrl="<%=url%>" jdbcUser="<%=usr%>" jdbcPassword="<%=pwd%>" catalogUri="<%=reference%>" >
			<%=query%>
		</jp:mondrianQuery>	
		<%
	}
	
	if (parameters != null && parameters.size() > 0) {
    	for (int i = 0; i < parameters.size(); i++) {
    		Node parameter = (Node) parameters.get(i);
    		String name = "";
    		String as = "";
    		if (parameter != null) {
    			name = parameter.valueOf("@name");
    			as = parameter.valueOf("@as");
    		}
    		//String parameterValue = request.getParameter(name);
    		//if (parameterValue == null || parameterValue.trim().equals("")) continue;
    		%>
    		<jp:setParam query="query01" httpParam="<%=name%>" mdxParam="<%=as%>"/>
    		<%
    	}
    }
	
	if (nameSubObject != null) {
		session.setAttribute("analysisBean", analysis);
		%>
			<jsp:include page="customizeAnalysis.jsp"/>
		<%
	}
	
} catch (Exception e){
	e.printStackTrace();
}%>