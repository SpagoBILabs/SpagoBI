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

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>

<%@ page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>    
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.BIObject"%>
<%@page import="it.eng.spagobi.commons.dao.DAOFactory"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spagobi.analiticalmodel.execution.service.ExecuteDocumentAction"%>
<%@page import="it.eng.spagobi.commons.bo.Domain"%>
<%@page import="it.eng.spagobi.commons.dao.IDomainDAO"%>
<%@page import="it.eng.spagobi.engines.config.dao.IEngineDAO"%>
<%@page import="it.eng.spagobi.engines.config.bo.Engine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="it.eng.spagobi.commons.metadata.SbiDomains"%>
<%@page import="it.eng.spagobi.engines.config.metadata.SbiExporters"%>
<%@page import="it.eng.spagobi.engines.config.bo.Exporters"%>
<%@page import="java.util.List"%>
<%@page import="it.eng.spagobi.analiticalmodel.document.bo.SubObject"%>
<%@page import="it.eng.spagobi.commons.serializer.SerializerFactory"%>

<%! private static transient Logger logger = Logger.getLogger(ExecuteDocumentAction.class);%>

	<%@ include file="/WEB-INF/jsp/commons/importSbiJS.jspf"%>
	
    
	<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '<%=urlBuilder.getResourceLink(request, "/js/lib/ext-2.0.1/resources/images/default/s.gif")%>';
    
    Sbi.config = {};

    // the user language
    Sbi.config.language = '<%= locale.getLanguage() %>';
	// the user country
    Sbi.config.country = '<%= locale.getCountry() %>';
    // the date format localized according to user language and country
    Sbi.config.localizedDateFormat = '<%= GeneralUtilities.getLocaleDateFormatForExtJs(permanentSession) %>';
    // the date format to be used when communicating with server
    Sbi.config.clientServerDateFormat = '<%= GeneralUtilities.getServerDateFormatExtJs() %>';
    // the timestamp format to be used when communicating with server
    Sbi.config.clientServerTimestampFormat = '<%= GeneralUtilities.getServerTimestampFormatExtJs() %>';
    
    var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
    	   				  request.getContextPath().substring(1):
    	   				  request.getContextPath()%>'
    	    
    };

    var params = {
    	SBI_EXECUTION_ID: <%= request.getParameter("SBI_EXECUTION_ID")!=null?"'" + request.getParameter("SBI_EXECUTION_ID") +"'": "null" %>
    	, LIGHT_NAVIGATOR_DISABLED: 'TRUE'
    };

    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
        , baseParams: params
    });

    <%
    BIObject obj = (BIObject) aServiceResponse.getAttribute(SpagoBIConstants.OBJECT);
	SubObject subObject = (SubObject) aServiceResponse.getAttribute(SpagoBIConstants.SUBOBJECT);
    String parameters = (String) aServiceRequest.getAttribute(ObjectsTreeConstants.PARAMETERS);
    logger.debug("Document parameters in request are [" + parameters + "]");
    String subobjectName = (String) aServiceRequest.getAttribute(SpagoBIConstants.SUBOBJECT_NAME);
    logger.debug("Subobject name in request is [" + subobjectName + "]");
    String snapshotName = (String) aServiceRequest.getAttribute(SpagoBIConstants.SNAPSHOT_NAME);
    logger.debug("Snapshot name in request is [" + snapshotName + "]");
    String snapshotHistoryNumber = (String) aServiceRequest.getAttribute(SpagoBIConstants.SNAPSHOT_HISTORY_NUMBER);
    logger.debug("Snapshot history number in request is [" + snapshotHistoryNumber + "]");
    
    String toolbarVisible = (String) aServiceRequest.getAttribute(SpagoBIConstants.TOOLBAR_VISIBLE);
    boolean toolbarHidden = (toolbarVisible == null || toolbarVisible.trim().equals("")) ? false : !Boolean.parseBoolean(toolbarVisible);
    String shortcutsVisible = (String) aServiceRequest.getAttribute(SpagoBIConstants.SLIDERS_VISIBLE);
    boolean shortcutsHidden = (shortcutsVisible == null || shortcutsVisible.trim().equals("")) ? false : !Boolean.parseBoolean(shortcutsVisible);
    
    Integer engineId = null;
	Engine engineObj = (obj == null) ? null : obj.getEngine();
	String exportersJSArray = "";
	if(engineObj!=null){
	
		IEngineDAO engineDao=DAOFactory.getEngineDAO();
		List exporters=new ArrayList();
		exporters=engineDao.getAssociatedExporters(engineObj);			
		if(!exporters.isEmpty()){
			exportersJSArray = "[" ;
			for (Iterator iterator = exporters.iterator(); iterator.hasNext();) {
				
				 Exporters exp = (Exporters) iterator.next();
				 Integer domainId=exp.getDomainId();
				 
				 IDomainDAO domainDao=DAOFactory.getDomainDAO();
				 Domain domain=domainDao.loadDomainById(domainId);
				 if(domain!=null){
					 String value_cd=domain.getValueCd();
					 String urlExporter=null;	
					 if (value_cd!=null){
						 if(iterator.hasNext()){
							 exportersJSArray +="'"+value_cd+"'," ;
						 }else{
							 exportersJSArray +="'"+value_cd+"']" ;
						 }
					 }
				 }
			}
		}
	}
	// 20100505: check if in request there is parameter
		boolean comingFromDocOrTreeList = false;
		if(request.getParameter("BIOBJECT_TREE_LIST") != null){
			comingFromDocOrTreeList = true;
		}
	
    %>
    //var menuConfig = <%= aServiceResponse.getAttribute("metaConfiguration")%>;
    
	<%
	if (obj == null) {
		%>
		var object = undefined;
		<%
	} else {
		%>
		var object = <%= SerializerFactory.getSerializer("application/json").serialize( obj ,locale).toString() %>
		<%
	}
	%>

	var parameters = <%= (parameters != null  && !parameters.trim().equals("")) ? ("'" + parameters.replace("'", "\\\'") + "'") : "undefined" %>;
	<% if (subObject != null) { %>
	var subobject = {id: <%= subObject.getId() %>, 'name': '<%= subObject.getName().replace("'", "\\\'") %>'};
	<% } else { %>
	var subobject = undefined;
	<% } %>
	var snapshotName = <%= (snapshotName != null && !snapshotName.trim().equals("")) ? ("'" + snapshotName.replace("'", "\\\'") + "'") : "undefined" %>;
	var snapshotHistoryNumber = <%= (snapshotHistoryNumber != null && !snapshotHistoryNumber.trim().equals("")) ? snapshotHistoryNumber : "0" %>;
	var snaphost = {'name': snapshotName, 'historyNumber': snapshotHistoryNumber};
	var shortcutsHidden = <%= shortcutsHidden %>;
	var toolbarHidden = <%= toolbarHidden %>;

// 20100505, added fromDOcTreeOrList parameter	
    var config = {
    	document: object
    	, preferences: {
			parameters: parameters
			, subobject: subobject
			, snapshot: snaphost
			, toolbarHidden: toolbarHidden
			, shortcutsHidden: shortcutsHidden
			, fromDocTreeOrList : <%=comingFromDocOrTreeList%>
	    }
	};
	
	Ext.onReady(function(){
		Ext.QuickTips.init();
		if (object === undefined) {
        	Ext.MessageBox.show({
           		title: 'Error'
           		, msg: 'Required document was not found or you cannot see it'
           		, buttons: Ext.MessageBox.OK     
           		, icon: Ext.MessageBox.ERROR
           		, modal: false
       		});
		} else {
			var executionPanel = new Sbi.execution.ExecutionPanel(config, object);
			var viewport = new Ext.Viewport({
				layout: 'border'
				, items: [
				    {
				       region: 'center',
				       layout: 'fit',
				       items: [executionPanel]
				    }
				]
	
			});
			executionPanel.execute();
		}
	});
    
    </script>

<%@ include file="/WEB-INF/jsp/commons/footer.jsp"%>