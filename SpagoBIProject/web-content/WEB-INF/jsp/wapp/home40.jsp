<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
<%@page import="org.json.JSONObject"%>
<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
		import="it.eng.spago.base.*,
                 java.util.List,
                 java.util.ArrayList"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.services.LoginModule"%>
<%@page import="it.eng.spagobi.wapp.util.MenuUtilities"%>
<%@page import="it.eng.spagobi.commons.serializer.MenuListJSONSerializer"%>
<%@page import="it.eng.spagobi.commons.serializer.MenuThemesListJSONSerializer"%>
<%@page import="it.eng.spagobi.wapp.services.DetailMenuModule"%>
<%@page import="it.eng.spagobi.wapp.bo.Menu"%>
<%@page import="org.json.JSONArray"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="it.eng.spagobi.commons.serializer.MenuListJSONSerializer"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>

<%!private static transient Logger logger = Logger.getLogger("it.eng.spagobi.homebis_jsp");%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>
<%@ include file="/WEB-INF/jsp/commons/importSbiJS410.jspf"%>
    
<!-- Include Ext stylesheets here: -->
<link id="extall"     rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />


<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/sbi_default/css/home40/layout.css" type="text/css" />
<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '/SpagoBI/js/lib/ext-2.0.1/resources/images/default/s.gif';
</script>

<%
	String contextName = ChannelUtilities.getSpagoBIContextName(request);
	SourceBean moduleResponse = (SourceBean)aServiceResponse.getAttribute("LoginModule"); 

	if(moduleResponse==null) moduleResponse=aServiceResponse;
	
	List lstMenu = new ArrayList();
	
	if (moduleResponse.getAttribute(MenuUtilities.LIST_MENU) != null){
		lstMenu = (List)moduleResponse.getAttribute(MenuUtilities.LIST_MENU);
	}
	List filteredMenuList = MenuUtilities.filterListForUser(lstMenu, userProfile);
	MenuListJSONSerializer m = new MenuListJSONSerializer();
	JSONArray jsonMenuList = (JSONArray)m.serialize(filteredMenuList,locale);
	//System.out.println(jsonMenuList);

%>

<!-- I want to execute if there is an homepage, only for user!-->
<%
	String characterEncoding = response.getCharacterEncoding();
	if (characterEncoding == null) {
		logger.warn("Response characterEncoding not found!!! Using UTF-8 as default.");
		characterEncoding = "UTF-8";
	}
    String firstUrlToCall = "";
	// if a document execution is required, execute it
	if (aServiceRequest.getAttribute(ObjectsTreeConstants.OBJECT_LABEL) != null) {
		String label = (String) aServiceRequest.getAttribute(ObjectsTreeConstants.OBJECT_LABEL);
	    String subobjectName = (String) aServiceRequest.getAttribute(SpagoBIConstants.SUBOBJECT_NAME);
		
	    StringBuffer temp = new StringBuffer();
	    temp.append(contextName + "/servlet/AdapterHTTP?ACTION_NAME=EXECUTE_DOCUMENT_ACTION&");
	    temp.append(ObjectsTreeConstants.OBJECT_LABEL + "=" + label);
	    if (subobjectName != null && !subobjectName.trim().equals("")) {
	    	temp.append("&" + SpagoBIConstants.SUBOBJECT_NAME + "=" + URLEncoder.encode(subobjectName, characterEncoding));
	    }
	    
	    // propagates other request parameters than PAGE, NEW_SESSION, OBJECT_LABEL and SUBOBJECT_NAME
	    Enumeration parameters = request.getParameterNames();
	    while (parameters.hasMoreElements()) {
	    	String aParameterName = (String) parameters.nextElement();
	    	if (aParameterName != null 
	    			&& !aParameterName.equalsIgnoreCase("PAGE") && !aParameterName.equalsIgnoreCase("NEW_SESSION") 
	    			&& !aParameterName.equalsIgnoreCase(ObjectsTreeConstants.OBJECT_LABEL)
        	    	&& !aParameterName.equalsIgnoreCase(SpagoBIConstants.SUBOBJECT_NAME) 
	    			&& request.getParameterValues(aParameterName) != null) {
	    		String[] values = request.getParameterValues(aParameterName);
	    		
	    		for (int i = 0; i < values.length; i++) {
	    			temp.append("&" + URLEncoder.encode(aParameterName, characterEncoding) + "=" 
	    					+ URLEncoder.encode(values[i], characterEncoding));
	    		}
	    	}
	    }
	    
		firstUrlToCall = temp.toString();
		
	} else {
	
		if (filteredMenuList.size() > 0) {
			//DAO method returns menu ordered by parentId, but null values are higher or lower on different database:
			//PostgreSQL - Nulls are considered HIGHER than non-nulls.
			//DB2 - Higher
			//MSSQL - Lower
			//MySQL - Lower
			//Oracle - Higher
			//Ingres - Higher
			// so we must look for the first menu item with null parentId
			Menu firtsItem = null;
			Iterator it = filteredMenuList.iterator();
			while (it.hasNext()) {
				Menu aMenuElement = (Menu) it.next();
				if (aMenuElement.getParentId() == null) {
					firtsItem = aMenuElement;
					break;
				}
			}
			String pathInit=MenuUtilities.getMenuPath(firtsItem, locale);
			Integer objId=firtsItem.getObjId();
			
			if(objId!=null){
				firstUrlToCall = contextName+"/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+firtsItem.getMenuId();
			}else if(firtsItem.getStaticPage()!=null){
				firstUrlToCall = contextName+"/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+firtsItem.getMenuId();
			}else if(firtsItem.getFunctionality()!=null){
				firstUrlToCall = DetailMenuModule.findFunctionalityUrl(firtsItem, contextName);
			}else if(firtsItem.getExternalApplicationUrl()!=null && !firtsItem.getExternalApplicationUrl().equals("")){
				firstUrlToCall = firtsItem.getExternalApplicationUrl();
				if (!GeneralUtilities.isSSOEnabled()) {
					if (firstUrlToCall.indexOf("?") == -1) {
						firstUrlToCall += "?" + SsoServiceInterface.USER_ID + "=" + userUniqueIdentifier;
					} else {
						firstUrlToCall += "&" + SsoServiceInterface.USER_ID + "=" + userUniqueIdentifier;
					}
				}
			} else {
				firstUrlToCall = contextName+"/themes/" + currTheme + "/html/technicalUserIntro.html";
			}
		}
		
	}
	
 %>
<script type="text/javascript">
var win_info_1;
Ext.require([
             'Ext.panel.*',
             'Ext.toolbar.*',
             'Ext.button.*',
             'Ext.container.ButtonGroup',
             'Ext.layout.container.Table',
             'Ext.tip.QuickTipManager'
         ]);
         


function execDirectDoc(btn){
	var url = "";
	var idMenu = btn.id;
	var path=btn.path;
<%-- 	var viewTrackPath='<%=viewTrackPath%>';
	if( viewTrackPath=='true' && path!=null)
	{document.getElementById('trackPath').innerHTML=path;} --%>
	
	if (idMenu != null && idMenu != 'null'){
		url =   "'<%=contextName%>/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+idMenu+"'" ;
		this.mainframe.load(url);
	}
	return;
 }

function getFunctionality(btn){
	var url = btn.url;
	var path=btn.path;
	execDirectUrl(url, path);
	return;
 }
 
function readHtmlFile(btn){
	var url = "";
 	var idMenu = btn.id;
 	var path = btn.path;
<%-- 	var viewTrackPath='<%=viewTrackPath%>';
	if( viewTrackPath=='true' && path!=null)
	{document.getElementById('trackPath').innerHTML=path;} --%>

 	 if (idMenu != null && idMenu != 'null'){
		url =  "'<%=contextName%>/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+idMenu+"'";
		this.mainframe.load(url);

	}
	return;
}
 	 
function execDirectUrl(url, path){
	this.mainframe.load(url);
	this.titlePath.setTitle(path);
	return;
}

function callExternalApp(url, path){
	url = getExternalAppUrl(url);
	this.mainframe.load(url);
	this.titlePath.setTitle(path);
	return;
}

function getExternalAppUrl(url){
	if (!Sbi.config.isSSOEnabled) {
		if (url.indexOf("?") == -1) {
			url += '?<%= SsoServiceInterface.USER_ID %>=' + Sbi.user.userUniqueIdentifier;
		} else {
			url += '&<%= SsoServiceInterface.USER_ID %>=' + Sbi.user.userUniqueIdentifier;
		}
	}
	return url;
}


function execUrl(url){
	document.location.href=url;
	return;
}
//returns the language url to be called in the language menu
function getLanguageUrl(config){
  var languageUrl = "javascript:execUrl('"+Sbi.config.contextName+"/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&LANGUAGE_ID="+config.language+"&COUNTRY_ID="+config.country+"')";
  return languageUrl;
}
function role(){
	if(Sbi.user.roles && Sbi.user.roles.length > 1){
		this.win_roles = new Sbi.home.DefaultRoleWindow({'SBI_EXECUTION_ID': ''});
		this.win_roles.show();
	}

}
function info(){

	if(!win_info_1){
		win_info_1= new Ext.Window({
		id:'win_info_1',
		autoLoad: {url: Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+'/html/infos.html'},             				
		layout:'fit',
		width:210,
		height:180,
		closeAction:'hide',
		//closeAction:'close',
		buttonAlign : 'left',
		plain: true,
		title: LN('sbi.home.Info')
		});
	}		
	win_info_1.show();
  }
function goHome(html, path){
	var url = Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+html;
	execDirectUrl(url, path);
}
  
Ext.onReady(function () {
	
	var firstUrl =  '<%= StringEscapeUtils.escapeJavaScript(firstUrlToCall) %>';  
    Ext.tip.QuickTipManager.init();
    this.mainframe = Ext.create('Ext.ux.IFrame', 
    			{ xtype: 'uxiframe'
  	  			, src: firstUrl
  	  			});
    
    this.titlePath = Ext.create("Ext.Panel",{title :'Home'});
    var itemsM = <%=jsonMenuList%>;
	for(i=0; i< itemsM.length; i++){
		var menuItem = itemsM[i];
		if(menuItem.itemLabel != null && menuItem.itemLabel == "LANG"){
	 		var languagesMenuItems = [];
	 		for (var j = 0; j < Sbi.config.supportedLocales.length ; j++) {
	 			var aLocale = Sbi.config.supportedLocales[j];
 				var aLanguagesMenuItem = new Ext.menu.Item({
					id: '',
					text: aLocale.language,
					iconCls:'icon-' + aLocale.language,
					href: this.getLanguageUrl(aLocale)
				})
 				languagesMenuItems.push(aLanguagesMenuItem);
	 		}
	 		menuItem.menu= languagesMenuItems;
		}else if(menuItem.itemLabel != null && menuItem.itemLabel == "ROLE"){
			if(Sbi.user.roles && Sbi.user.roles.length == 1){
				menuItem.hidden=true;
			}
		}
// 		else if(menuItem.itemLabel != null && menuItem.itemLabel == "HOME"){
// 			if(Sbi.user.roles){
// 				for(j=0; j < Sbi.user.roles.length; j++){
// 					var role = Sbi.user.roles[j];
// 				}

// 			}
// 		}
	}
    this.mainpanel =  Ext.create("Ext.Panel",{
    	height:'100%',
    	items: [
			this.titlePath			
    	    , mainframe]
    	, dockedItems: [{
	   	    xtype: 'toolbar',
	   	    dock: 'left',
	   	    items: itemsM
    	}]
    });
    
    Ext.create('Ext.Viewport', {
        layout: 'fit',
        items: [this.mainpanel]
    });
    
    
});

</script>
 


