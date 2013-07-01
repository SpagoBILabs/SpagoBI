<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>
<%@page import="it.eng.spagobi.commons.utilities.UserUtilities"%>
<%@page import="org.json.JSONObject"%>
<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
		import="it.eng.spago.base.*,
                 java.util.List,
                 java.util.ArrayList"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.services.LoginModule"%>
<%@page import="it.eng.spagobi.wapp.util.MenuUtilities"%>
<%@page import="it.eng.spagobi.commons.serializer.MenuThemesListJSONSerializer"%>
<%@page import="it.eng.spagobi.wapp.services.DetailMenuModule"%>
<%@page import="it.eng.spagobi.wapp.bo.Menu"%>
<%@page import="org.json.JSONArray"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="it.eng.spagobi.commons.serializer.MenuListJSONSerializer"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>

<%!private static transient Logger logger = Logger.getLogger("it.eng.spagobi.publicUserHome.jsp");%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base410.jsp"%>
<%@ include file="/WEB-INF/jsp/commons/importSbiJS410.jspf"%>
    
<!-- Include Ext stylesheets here: -->
<link id="extall"     rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />
<script type="text/javascript" src="/SpagoBI/js/lib/ext-4.1.1a/overrides/overrides.js"></script>

<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/sbi_default/css/home40/layout.css" type="text/css" />
<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '/SpagoBI/js/lib/ext-4.1.1a/resources/themes/images/default/tree/s.gif';
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
	MenuListJSONSerializer serializer = new MenuListJSONSerializer(userProfile);
	JSONArray jsonMenuList = (JSONArray) serializer.serialize(filteredMenuList,locale);
	//System.out.println(jsonMenuList);
%>
<%-- Javascript object useful for session expired management (see also sessionExpired.jsp) --%>
<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';
</script>
<%-- End javascript object useful for session expired management (see also sessionExpired.jsp) --%>
<!-- I want to execute if there is an homepage, only for user!-->
<%
	String characterEncoding = response.getCharacterEncoding();
	if (characterEncoding == null) {
		logger.warn("Response characterEncoding not found!!! Using UTF-8 as default.");
		characterEncoding = "UTF-8";
	}
    String firstPublicUrlToCall = "";
    String firstUrlToCall = "";
    if (jsonMenuList.get(0) != null && jsonMenuList.get(0) instanceof JSONObject){
    	JSONObject firstItem = (JSONObject)jsonMenuList.get(0);
	    if (firstItem != null && firstItem.getString("firstUrl") != null){
	    	firstUrlToCall =  firstItem.getString("firstUrl");
	    } else{			
	    	firstUrlToCall = contextName+"/themes/" + currTheme + "/html/publicUserIntro.html";	
		}
    }else{
    	firstUrlToCall = contextName+"/themes/" + currTheme + "/html/publicUserIntro.html";
    }
   
 %>
<script type="text/javascript">

var firstUrlTocallvar;
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
  var languageUrl = "javascript:execUrl('"+Sbi.config.contextName+"/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&LANGUAGE_ID="+config.language+"&COUNTRY_ID="+config.country+"&IS_PUBLIC_USER=TRUE')";
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
		frame: false,
		style:"background-color: white",
		autoLoad: {url: Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+'/html/infos.jsp'},             				
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
	var url;
	if(!html){
		url = firstUrlTocallvar;
	}else{
		url = Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+html;
	}
	if(url){
		execDirectUrl(url, path);
	}
	
}


  
Ext.onReady(function () {
	var firstPublicUrl =  '<%= StringEscapeUtils.escapeJavaScript(firstUrlToCall) %>';  
	firstUrlTocallvar = firstPublicUrl;
    Ext.tip.QuickTipManager.init();
    this.mainframe = Ext.create('Ext.ux.IFrame', 
    			{ xtype: 'uxiframe'
  	  			, src: firstPublicUrl
  	  			, height: '100%'
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
	}
	function hideItem( menu, e, eOpts){
        console.log('bye bye ');
        menu.hide();
    }
    this.mainpanel =  Ext.create("Ext.panel.Panel",{
    	autoScroll: true,
    	height: '100%',
    	items: [
			//this.titlePath	,		
    	    mainframe]
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
