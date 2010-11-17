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

<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
		import="it.eng.spago.base.*,
                 java.util.List,
                 java.util.ArrayList"%>
<%@page import="it.eng.spagobi.commons.utilities.ChannelUtilities"%>
<%@page import="it.eng.spagobi.commons.services.LoginModule"%>
<%@page import="it.eng.spagobi.wapp.util.MenuUtilities"%>
<%@page import="it.eng.spagobi.chiron.serializer.MenuListJSONSerializer"%>
<%@page import="it.eng.spagobi.chiron.serializer.MenuThemesListJSONSerializer"%>
<%@page import="it.eng.spagobi.wapp.services.DetailMenuModule"%>
<%@page import="it.eng.spagobi.wapp.bo.Menu"%>
<%@page import="org.json.JSONObject"%>
<%@page import="it.eng.spagobi.commons.constants.ObjectsTreeConstants"%>
<%@page import="it.eng.spagobi.chiron.serializer.MenuListJSONSerializer"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="it.eng.spagobi.services.common.SsoServiceInterface"%>

<%! private static transient Logger logger = Logger.getLogger("it.eng.spagobi.homebis_jsp");%>

<%@ include file="/WEB-INF/jsp/commons/portlet_base.jsp"%>
<%@ include file="/WEB-INF/jsp/commons/importSbiJS.jspf"%>

<%-- START CHECK USER PROFILE EXISTENCE
	This Ajax call is usefull to find out if a user profile object is in session, i.e. if a user has logged in.
	In case the user profile object is not found, the browser is redirected to the login page.
	
	N.B.  TODO con il CAS da problemi perchè non valida il ticket ...
	--%>



<script type="text/javascript" src="/SpagoBI/js/src/ext/sbi/overrides/overrides.js"></script>
	
<script type="text/javascript" src="<%=linkSbijs%>"></script>
<script type="text/javascript" src="<%=linkProto%>"></script>
<script type="text/javascript" src="<%=linkProtoWin%>"></script>
<script type="text/javascript" src="<%=linkProtoEff%>"></script>
<link href="<%=linkProtoDefThem%>" rel="stylesheet" type="text/css" />
<link href="<%=linkProtoAlphaThem%>" rel="stylesheet" type="text/css" />

<%-- END CHECK USER PROFILE EXISTENCE 	
<script type="text/javascript">
	Ext.onReady(function(){
		Ext.Ajax.request({
			url: '<%= request.getContextPath() + GeneralUtilities.getSpagoAdapterHttpUrl() %>?ACTION_NAME=CHECK_USER_PROFILE_EXISTENCE&LIGHT_NAVIGATOR_DISABLED=true',
			method: 'get',
			params: '',
			success: function (result, request) {
				response = result.responseText || "";
				if (response == '' || response == 'userProfileNotFound') {
					window.location.href="<%= request.getContextPath() %>";
				}
			},
			failure: somethingWentWrongWhileCheckingUserProfileExistence,
			disableCaching: true
		});
	});
	
	function somethingWentWrongWhileCheckingUserProfileExistence() {}
	</script>
 END CHECK USER PROFILE EXISTENCE --%>


<%  
	String contextName = ChannelUtilities.getSpagoBIContextName(request);

	String characterEncoding = response.getCharacterEncoding();
	if (characterEncoding == null) {
		logger.warn("Response characterEncoding not found!!! Using UTF-8 as default.");
		characterEncoding = "UTF-8";
	}

	SourceBean moduleResponse = (SourceBean)aServiceResponse.getAttribute("LoginModule"); 

	if(moduleResponse==null) moduleResponse=aServiceResponse;
	
	List lstMenu = new ArrayList();
	
	if (moduleResponse.getAttribute(MenuUtilities.LIST_MENU) != null){
		lstMenu = (List)moduleResponse.getAttribute(MenuUtilities.LIST_MENU);
	}
	List filteredMenuList = MenuUtilities.filterListForUser(lstMenu, userProfile);
	MenuListJSONSerializer m = new MenuListJSONSerializer();
	JSONObject jsonMenuList = (JSONObject)m.serialize(filteredMenuList,locale);
	if(jsonMenuList == null) jsonMenuList= new JSONObject();	
	ConfigSingleton spagoconfig = ConfigSingleton.getInstance(); 
	// get mode of execution
	String viewTrack = (String)spagoconfig.getAttribute("SPAGOBI.MENU.pathTracked");   
	boolean viewTrackPath=false;	
	if(viewTrack!=null && viewTrack.equalsIgnoreCase("TRUE")){
	viewTrackPath=true;	
	}
	
	Boolean userHasChanged = (Boolean) moduleResponse.getAttribute("USER_HAS_CHANGED"); 
%>

<%-- Javascript object useful for session expired management (see also sessionExpired.jsp) --%>
<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';
</script>
<%-- End javascript object useful for session expired management (see also sessionExpired.jsp) --%>

<%-- Javascript function for document composition cross navigation (workaround for ie)
On ie svg plugin, the parent variable seems to return top window, so this function here calls the execCrossNavigation at the correct level
--%>
<script>
function execCrossNavigation(windowName, label, parameters) {
	document.getElementById('iframeDoc').contentWindow.execCrossNavigation(windowName, label, parameters);
}
</script>
<%-- End javascript function for document composition cross navigation (workaround for ie) --%>
	
<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';
var activesso = false;
var jsonMenuThemesList;
var drawSelectTheme = false;
var logoutUrl = '';
var themesIcon;
var themesViewName;
</script>

<!-- I want to execute if there is an homepage, only for user!-->
<%
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
			String pathInit=MenuUtilities.getMenuPath(firtsItem);
			Integer objId=firtsItem.getObjId();
			
			if(objId!=null){
				firstUrlToCall = contextName+"/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+firtsItem.getMenuId();
			}else if(firtsItem.getStaticPage()!=null){
				firstUrlToCall = contextName+"/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+firtsItem.getMenuId();
			}else if(firtsItem.getFunctionality()!=null){
				firstUrlToCall = DetailMenuModule.findFunctionalityUrl(firtsItem, contextName);
			}else if(firtsItem.getExternalApplicationUrl()!=null && !firtsItem.getExternalApplicationUrl().equals("")){
				firstUrlToCall = firtsItem.getExternalApplicationUrl();
			}
		}
		
	}
	
 %>
	<script type="text/javascript">
	activesso = false;
	logoutUrl =  Sbi.config.contextName+"/servlet/AdapterHTTP?ACTION_NAME=LOGOUT_ACTION&LIGHT_NAVIGATOR_DISABLED=TRUE";
	</script>

<%
// BANNER AND FOOTER CONFIGURATION
SourceBean b = (SourceBean)spagoconfig.getAttribute("SPAGOBI.HOME.BANNER");
String banner = (String)b.getAttribute("view");
boolean showbanner = true;
if (banner!=null && !banner.equals("") && banner.equalsIgnoreCase("false")){
	showbanner = false;
}
SourceBean f = (SourceBean)spagoconfig.getAttribute("SPAGOBI.HOME.FOOTER");
String footer =(String) f.getAttribute("view");
boolean showfooter = true;
if (footer!=null && !footer.equals("") && footer.equalsIgnoreCase("false")){
	showfooter =  false;
}



String themesIcon="";
String themeI="img/theme.png";
if(ThemesManager.resourceExists(currTheme,themeI)){
	themesIcon=contextName+"/themes/"+currTheme+"/"+themeI;
}
else
{
	themesIcon=contextName+"/themes/sbi_default/"+themeI;
}

//recover all themes
	List themes=spagoconfig.getAttributeAsList("SPAGOBI.THEMES.THEME");
	boolean drawSelectTheme=ThemesManager.drawSelectTheme(themes);
	
	//keep track of current theme view name
	String currThemeView="";
	
	if(drawSelectTheme){
		MenuThemesListJSONSerializer m2 = new MenuThemesListJSONSerializer();
		JSONObject jsonMenuThemesList = (JSONObject)m2.serialize(themes,locale);
	%>
	<script type="text/javascript">
	drawSelectTheme = true;
	themesIcon = '<%=themesIcon%>';
	themesViewName = '<%=currViewThemeName%>';
	jsonMenuThemesList = <%=jsonMenuThemesList%>;
	</script>
 	<%} // end if(draw_select_combo)%>
<% 
String url="";
if(ThemesManager.resourceExists(currTheme,"/html/banner.html")){
	url = "/themes/"+currTheme+"/html/banner.html";	
}
else {
	url = "/themes/sbi_default/html/banner.html";	
}

if(showbanner){%>
<div id='Banner'>
	<jsp:include page='<%=url%>' />
</div>
<%} %>

<% 
String url2="";
if(ThemesManager.resourceExists(currTheme,"/html/footer.html")){
	url2 = "/themes/"+currTheme+"/html/footer.html";	
}
else {
	url2 = "/themes/sbi_default/html/footer.html";	
}

if(showfooter){%>
<div id='Footer' >
	<jsp:include page='<%=url2%>' />
</div>
<% } %>

<script type="text/javascript">
    
   // 20100511
   var url = {
    	host: '<%= request.getServerName()%>'
    	, port: '<%= request.getServerPort()%>'
    	, contextPath: '<%= request.getContextPath().startsWith("/")||request.getContextPath().startsWith("\\")?
    	   				  request.getContextPath().substring(1):
    	   				  request.getContextPath()%>'
    };

    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
    	baseUrl: url
    });
    // END 20100511
    
    var northFrame;
    var centerFrame;
    var southFrame;
    var showBanner = <%=showbanner%>;
    var showFooter = <%=showfooter%>;  
    
    var browserWidth = 1024;
		
		if (Ext.isIE) {
	      	browserWidth = document.body.clientWidth;
	  	} else {
	     	browserWidth = document.documentElement.clientWidth;
  		}
     	
    function execDirectDoc(btn){
		var url = "";
		var idMenu = btn.id;
		var path=btn.path;
		var viewTrackPath='<%=viewTrackPath%>';
		if( viewTrackPath=='true' && path!=null)
		{document.getElementById('trackPath').innerHTML=path;}
		
		if (idMenu != null && idMenu != 'null'){
			url =   "'<%=contextName%>/servlet/AdapterHTTP?ACTION_NAME=MENU_BEFORE_EXEC&MENU_ID="+idMenu+"'" ;
			centerFrame.getFrame().setSrc(url);
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
		var viewTrackPath='<%=viewTrackPath%>';
		if( viewTrackPath=='true' && path!=null)
		{document.getElementById('trackPath').innerHTML=path;}

	 	 if (idMenu != null && idMenu != 'null'){
			url =  "'<%=contextName%>/servlet/AdapterHTTP?ACTION_NAME=READ_HTML_FILE&MENU_ID="+idMenu+"'";
			centerFrame.getFrame().setSrc(url);
		}
		return;
	}
     	 
	function execDirectUrl(url, path){
		centerFrame.getFrame().setSrc(url);
		return;
	}
	
	function callExternalApp(url, path){
		if (!Sbi.config.isSSOEnabled) {
			if (url.indexOf("?") == -1) {
				url += '?<%= SsoServiceInterface.USER_ID %>=' + Sbi.user.userUniqueIdentifier;
			} else {
				url += '&<%= SsoServiceInterface.USER_ID %>=' + Sbi.user.userUniqueIdentifier;
			}
		}
		centerFrame.getFrame().setSrc(url);
		return;
	}
	
	function execUrl(url){
		document.location.href=url;
		return;
	}

	Ext.onReady(function(){
      Ext.QuickTips.init();              
      var menuList = <%=jsonMenuList%>;
      var firstUrl =  '<%= StringEscapeUtils.escapeJavaScript(firstUrlToCall) %>';
     northFrame = new Sbi.home.Banner({bannerMenu: menuList,themesMenu: jsonMenuThemesList});
      centerFrame = new  Ext.ux.ManagedIframePanel({
      					region: 'center'
      					,xtype: 'panel'
						,frameConfig:{
						        autoCreate:{id: 'iframeDoc', name:'iframeDoc'},
	        					disableMessaging :false}
		                ,defaultSrc : 'about:blank' // see comment below for miframe-1.2.5
		                ,border		: false 
						,collapseMode: 'mini'
						,loadMask  : true
						,scrolling  : 'auto'
						, fitToParent: true  	
						, disableMessaging :false
						,listeners: {'message:collapse2':  {
				        		fn: function(srcFrame, message) {	
				        			if(showFooter){				        				        			        		
						        		if(northFrame.collapsed && southFrame.collapsed){
						        			northFrame.expand(false);
						        			southFrame.expand(false);
						        			
						        		}else{
						        			northFrame.collapse(false);
						        			southFrame.collapse(false);		
						        			
						        		}
					        		}else{
					        			if(northFrame.collapsed){
						        			northFrame.expand(false);
						        			
						        		}else{
						        			northFrame.collapse(false);
						        			
						        		}
					        		}
						        }
	        					, scope: this}
    					}
						
	  });
	  
	  // this setTimeout is a workaround for miframe-1.2.5: 
	  // when setting the iframe url with defaultSrc, the 'sendMessage' method isn't injected into the iframe, therephore the Expand
	  // button does not work.
	  // TODO: remove this instruction and use defaultSrc when updating miframe library
	  setTimeout("centerFrame.getFrame().setSrc('" + firstUrl + "');", 500);
	  
	  if (Ext.isIE) {
						centerFrame.on('resize', 
	  					function() {
	  					centerFrame.getFrame().dom.style.height = centerFrame.getSize().height;
	  					},
	  				this);
	}
	
	var viewport;
	if(showFooter){
      southFrame = new Sbi.home.Footer({});
      viewport = new Ext.Viewport({
	    layout: 'border',
	    items: [northFrame, centerFrame, southFrame],
	    forceLayout: true,
	    autoHeight: true
	    });	  
	  }else{
	    viewport = new Ext.Viewport({
	    layout: 'border',
	    items: [northFrame, centerFrame],
	    forceLayout: true,
	    autoHeight: true
	    });	  
	  }
	  
	  viewport.render();
	  viewport.doLayout(true,true);


	  <% if (userHasChanged != null && userHasChanged.booleanValue()) { %>
	  // reset parameters stored in session
	  Sbi.execution.SessionParametersManager.reset();
	  <% } %>
    });
    

    
    </script>
<%-- END CHECK USER PROFILE EXISTENCE --%>




