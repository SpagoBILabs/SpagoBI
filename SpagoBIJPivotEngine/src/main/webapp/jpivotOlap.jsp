<%--

LICENSE: see LICENSE.txt file 

--%>
<%--

  JPivot / WCF comes with its own "expression language", which simply
  is a path of properties. E.g. #{customer.address.name} is
  translated into:
    session.getAttribute("customer").getAddress().getName()
  WCF uses jakarta commons beanutils to do so, for an exact syntax
  see its documentation.

  With JSP 2.0 you should use <code>#{}</code> notation to define
  expressions for WCF attributes and <code>\${}</code> to define
  JSP EL expressions.

  JSP EL expressions can not be used with WCF tags currently, all
  tag attributes have their <code>rtexprvalue</code> set to false.
  There may be a twin library supporting JSP EL expressions in
  the future (similar to the twin libraries in JSTL, e.g. core
  and core_rt).

  Check out the WCF distribution which contains many examples on
  how to use the WCF tags (like tree, form, table etc).

--%>

<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%--
The following directive catches exceptions thrown by jsps, must be commented in development environment
--%>
<%@page errorPage="/error.jsp"%>

<%@page import="it.eng.spagobi.utilities.messages.EngineMessageBundle"%>
<%@page import="com.tonbeller.wcf.controller.RequestContext"%>
<%@page import="java.util.Locale"%>
<%@page import="it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils"%>
<%@page import="it.eng.spago.security.IEngUserProfile"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spagobi.commons.bo.UserProfile"%>
<%@page import="it.eng.spagobi.jpivotaddins.crossnavigation.SpagoBICrossNavigationConfig"%>
<%@page import="it.eng.spagobi.jpivotaddins.bean.ToolbarBean"%>
<%@page import="it.eng.spagobi.services.common.EnginConf"%>

<%@ taglib uri="http://www.tonbeller.com/jpivot" prefix="jp" %>
<%@ taglib uri="http://www.tonbeller.com/wcf" prefix="wcf" %>
<%@ taglib uri="http://spagobi.eng.it/" prefix="spagobi" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %> 



<html>
<head>
  <title>JPivot Page</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="jpivot/table/mdxtable.css">
  <link rel="stylesheet" type="text/css" href="jpivot/navi/mdxnavi.css">
  <link rel="stylesheet" type="text/css" href="wcf/form/xform.css">
  <link rel="stylesheet" type="text/css" href="wcf/table/xtable.css">
  <link rel="stylesheet" type="text/css" href="wcf/tree/xtree.css">
  
  <!-- javascript and css files for context menu (used for cross navigation) -->
  <script type="text/javascript" src="contextMenu/contextMenu.js"></script>
  <link rel="stylesheet" type="text/css" href="contextMenu/contextMenu.css">
</head>
<body bgcolor="white" lang="en">

<%-- START SCRIPT FOR DOMAIN DEFINITION (MUST BE EQUAL BETWEEN SPAGOBI AND EXTERNAL ENGINES) -->
<script type="text/javascript">
	document.domain='<%= EnginConf.getInstance().getSpagoBiDomain() %>';
</script>
<!-- END SCRIPT FOR DOMAIN DEFINITION --%>

<%
Logger logger = Logger.getLogger(this.getClass());
logger.debug("Reading a user profile...");
String userId = null;
IEngUserProfile profile = (IEngUserProfile)session.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
if (profile!=null){
   userId = (String)profile.getUserUniqueIdentifier();
	 //userId = (String)((UserProfile)profile).getUserId();
}
if(profile==null || userId==null) {
    logger.debug("User profile is null");
	throw new ServletException("User profile not found !");
}

// AUDIT UPDATE
String auditId = request.getParameter("SPAGOBI_AUDIT_ID");
logger.debug("auditId="+auditId);
AuditAccessUtils auditAccessUtils = 
	(AuditAccessUtils) request.getSession().getAttribute("SPAGOBI_AUDIT_UTILS");
if (auditId != null) {
	if (auditAccessUtils != null) auditAccessUtils.updateAudit(session,userId,auditId, new Long(System.currentTimeMillis()), null, 
			"EXECUTION_STARTED", null, null);

}
logger.debug("Started...");
try {
%>

<form action="jpivotOlap.jsp" method="post">

<spagobi:saveAnalysis id="save01"/>

<%-- include query and title, so this jsp may be used with different queries --%>
<wcf:include id="include01" httpParam="query" prefix="/WEB-INF/queries/" suffix=".jsp"/>
<c:if test="${query01 == null}">
  <%
	// AUDIT UPDATE
	if (auditId != null) {
		if (auditAccessUtils != null) auditAccessUtils.updateAudit(session,userId,auditId, null, new Long(System.currentTimeMillis()), 
				"EXECUTION_FAILED", "Error executing query", null);
	}
  %>
  <jsp:forward page="/index.jsp"/>
</c:if>
<%
	ToolbarBean tb= new ToolbarBean();
	if(session.getAttribute("toolbarButtonsVisibility")!=null){
		tb =(ToolbarBean) session.getAttribute("toolbarButtonsVisibility");
		session.removeAttribute("toolbarButtonsVisibility");
	}
	
%>
<%-- define table, navigator and forms --%>
<jp:table id="table01" query="#{query01}"/>
<jp:navigator id="navi01" query="#{query01}" visible="false"/>

<wcf:form id="mdxedit01" xmlUri="/WEB-INF/jpivot/table/mdxedit.xml" model="#{query01}" visible="false"/>
<wcf:form id="sortform01" xmlUri="/WEB-INF/jpivot/table/sortform.xml" model="#{table01}" visible="false"/>
<wcf:form id="saveAnalysis01" xmlUri="/WEB-INF/jpivot/table/saveAnalysisTable.xml" model="#{save01}" visible="false"/>
<jp:print id="print01"/>
<wcf:form id="printform01" xmlUri="/WEB-INF/jpivot/print/printpropertiesform.xml" model="#{print01}" visible="false"/>
<jp:chart id="chart01" query="#{query01}" visible="false"/>
<wcf:form id="chartform01" xmlUri="/WEB-INF/jpivot/chart/chartpropertiesform.xml" model="#{chart01}" visible="false"/>

<wcf:table id="query01.drillthroughtable" visible="false" selmode="none" editable="true"/>

<h2><c:out value="${title01}"/></h2>

<%-- define a toolbar --%>
<wcf:toolbar id="toolbar01" bundle="com.tonbeller.jpivot.toolbar.resources">
  <% if(tb.getButtonCubeVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="cubeNaviButton" tooltip="toolb.cube" img="cube" model="#{navi01.visible}"/>
  <% } %>
  
  <% if(tb.getButtonMDXVisibleB().booleanValue()){%>
	<wcf:scriptbutton id="mdxEditButton" tooltip="toolb.mdx.edit" img="mdx-edit" model="#{mdxedit01.visible}"/>
  <% } %>

  <% if(tb.getButtonOrderVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="sortConfigButton" tooltip="toolb.table.config" img="sort-asc" model="#{sortform01.visible}"/>
  <% } %>
  
  <%
  if (profile.getFunctionalities().contains("SaveSubobjectFunctionality")) {
  %>
	  <% if(tb.getButtonSaveAnalysisVisibleB().booleanValue()){%>
	  <wcf:scriptbutton id="saveAnalysis" tooltip="toolb.save" img="save" model="#{saveAnalysis01.visible}"/>
	  <% } %>
  <%
  }
  %>
  <wcf:separator/>
  <% if(tb.getButtonFatherMembVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="levelStyle" tooltip="toolb.level.style" img="level-style" model="#{table01.extensions.axisStyle.levelStyle}"/>
  <% } %>
  
  <% if(tb.getButtonHideSpansVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="hideSpans" tooltip="toolb.hide.spans" img="hide-spans" model="#{table01.extensions.axisStyle.hideSpans}"/>
  <% } %>
  
  <% if(tb.getButtonShowPropertiesVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="propertiesButton" tooltip="toolb.properties"  img="properties" model="#{table01.rowAxisBuilder.axisConfig.propertyConfig.showProperties}"/>
  <% } %>
  
  <% if(tb.getButtonHideEmptyVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="nonEmpty" tooltip="toolb.non.empty" img="non-empty" model="#{table01.extensions.nonEmpty.buttonPressed}"/>
  <% } %>
  
  <% if(tb.getButtonShiftAxisVisibleB().booleanValue()){%>
  	<wcf:scriptbutton id="swapAxes" tooltip="toolb.swap.axes"  img="swap-axes" model="#{table01.extensions.swapAxes.buttonPressed}"/>
  <% } %>
  
  <wcf:separator/>
  <% if(tb.getButtonDrillMemberVisibleB().booleanValue()){%>
  	<wcf:scriptbutton model="#{table01.extensions.drillMember.enabled}"	 tooltip="toolb.navi.member" radioGroup="navi" id="drillMember"   img="navi-member"/>
  <% } %>
  
  <% if(tb.getButtonDrillPositionVisibleB().booleanValue()){%>
  	<wcf:scriptbutton model="#{table01.extensions.drillPosition.enabled}" tooltip="toolb.navi.position" radioGroup="navi" id="drillPosition" img="navi-position"/>
  <% } %>
  
  <% if(tb.getButtonDrillReplaceVisibleB().booleanValue()){%>
  	<wcf:scriptbutton model="#{table01.extensions.drillReplace.enabled}"	 tooltip="toolb.navi.replace" radioGroup="navi" id="drillReplace"  img="navi-replace"/>
  <% } %>
  
  <% if(tb.getButtonDrillThroughVisibleB().booleanValue()){%>
  	<wcf:scriptbutton model="#{table01.extensions.drillThrough.enabled}"  tooltip="toolb.navi.drillthru" id="drillThrough01"  img="navi-through"/>
  <% } %>
  <%
  if (session.getAttribute(SpagoBICrossNavigationConfig.ID) != null) {
	%>
	<wcf:scriptbutton model="#{table01.extensions.crossNavigation.enabled}"  tooltip="toolb.navi.crossNavigation" id="crossNavigation01"  img="cross-navigation"/>  
	<%
  }
  %>
  <wcf:separator/>
   <% if(tb.getButtonShowChartVisibleB().booleanValue()){%>
  		<wcf:scriptbutton id="chartButton01" tooltip="toolb.chart" img="chart" model="#{chart01.visible}"/>
   <% } %>
   <% if(tb.getButtonConfigureChartVisibleB().booleanValue()){%>
  		<wcf:scriptbutton id="chartPropertiesButton01" tooltip="toolb.chart.config" img="chart-config" model="#{chartform01.visible}"/>
   <% } %>
  <wcf:separator/>
   <% if(tb.getButtonConfigurePrintVisibleB().booleanValue()){%>
  		<wcf:scriptbutton id="printPropertiesButton01" tooltip="toolb.print.config" img="print-config" model="#{printform01.visible}"/>
   <% } %>
  <%--
  <wcf:imgbutton id="printpdf" tooltip="toolb.print" img="print" href="./Print?cube=01&type=1"/>
  <wcf:imgbutton id="printxls" tooltip="toolb.excel" img="excel" href="./Print?cube=01&type=0"/>
  --%>
  <wcf:separator/>
   <% if(tb.getButtonFlushCacheVisibleB().booleanValue()){%>
  	<wcf:imgbutton id="flushCache" tooltip="toolb.cache.flush" img="reload" href="./FlushCacheServlet"/>
   <% } %>
</wcf:toolbar>

<%-- render toolbar --%>
<wcf:render ref="toolbar01" xslUri="/WEB-INF/jpivot/toolbar/htoolbar.xsl" xslCache="true"/>
<p>


<wcf:render ref="saveAnalysis01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
<%

//retrieves the locale
RequestContext context = RequestContext.instance();
Locale locale = context.getLocale();
String message = (String) session.getAttribute("saveSubObjectMessage");
if (message != null && !message.trim().equals("")) {
	if (message.toUpperCase().startsWith("KO - ")) {
		%>
		<p>
			<strong style="color:red">
			<%=EngineMessageBundle.getMessage("save.subobject.ko", locale)%><br>
			<%=EngineMessageBundle.getMessage("save.subobject.ko.message", locale)%><br>
			<%=message.substring(5)%>
			</strong>
		<p>
		<%
	} else if (message.toUpperCase().startsWith("OK - ")) {
		%>
		<p>
			<strong style="color:black">
			<%=EngineMessageBundle.getMessage("save.subobject.ok", locale)%>
			</strong>
			<%
			String subObjId = message.substring("OK - ".length());
			%>
			<script type="text/javascript">
			// for old execution interface
			try {
				parent.loadSubObject(window.name, <%= subObjId %>);
			} catch (ex) {
			}
			// for new ExtJs-based execution interface, call metadata window first
			try {
				var id = <%= subObjId %>;
				var msgToSend = 'Sub Object Saved!!';
				//sendMessage({'id': id, 'msg': msgToSend},'subobjectsaved');
				
				window.onLoad = setTimeout('try {sendMessage({\'id\': id, \'msg\': msgToSend},"subobjectsaved")} catch (ex) {}', 1000);
			} catch (err) {
			}
			</script>
		<p>
		<%
	} else {
		%>
		<p>
			<strong style="color:black"><%=message%></strong>
		<p>
		<%
	}
	session.removeAttribute("saveSubObjectMessage");
}
%>
<%-- if there was an overflow, show error message 
<c:if test="${query01.result.overflowOccured}">
  <%
	// AUDIT UPDATE
	if (auditId != null) {
		if (auditAccessUtils != null) auditAccessUtils.updateAudit(session,userId,auditId, null, new Long(System.currentTimeMillis()), 
				"EXECUTION_FAILED", "Overflow occurred", null);
	}
  %>
  <p>
  <strong style="color:red">Resultset overflow occured</strong>
  </p>
</c:if>
--%>

<%-- render navigator --%>
<wcf:render ref="navi01" xslUri="/WEB-INF/jpivot/navi/navigator.xsl" xslCache="true"/>

<%-- edit mdx --%>
<c:if test="${mdxedit01.visible}">
  <h3>MDX Query Editor</h3>
  <wcf:render ref="mdxedit01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
</c:if>

<%-- sort properties --%>
<c:if test="${sortform01.visible}">
<wcf:render ref="sortform01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>
</c:if>

<%-- chart properties --%>
<wcf:render ref="chartform01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>

<%-- print properties --%>
<wcf:render ref="printform01" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>

<!-- render the table -->

<wcf:render ref="table01" xslUri="/WEB-INF/jpivot/table/mdxtable.xsl" xslCache="true"/>

Slicer:
<wcf:render ref="table01" xslUri="/WEB-INF/jpivot/table/mdxslicer.xsl" xslCache="true"/>

<!-- drill through table -->
<wcf:render ref="query01.drillthroughtable" xslUri="/WEB-INF/wcf/wcf.xsl" xslCache="true"/>

<!-- render chart -->
<wcf:render ref="chart01" xslUri="/WEB-INF/jpivot/chart/chart.xsl" xslCache="true"/>

<%-- 
<a href="index.jsp">back to index</a>
--%>
</form>
<%
	// AUDIT UPDATE
	if (auditId != null && auditAccessUtils != null) 
		auditAccessUtils.updateAudit(session,userId,auditId, null, new Long(System.currentTimeMillis()), 
			"EXECUTION_PERFORMED", null, null);
} catch (Exception e) {
	// AUDIT UPDATE
	if (auditId != null && auditAccessUtils != null) 
		auditAccessUtils.updateAudit(session,userId,auditId, null, new Long(System.currentTimeMillis()), 
			"EXECUTION_FAILED", e.getMessage(), null);
	throw e;
}
%>

</body>
</html>
