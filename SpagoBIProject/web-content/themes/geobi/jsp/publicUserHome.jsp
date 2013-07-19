<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
%>
    
<!-- Include Ext stylesheets here: -->
<link id="extall"     rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all.css" type="text/css" />
<link id="theme-gray" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/resources/css/ext-all-gray.css" type="text/css" />
<script type="text/javascript" src="/SpagoBI/js/lib/ext-4.1.1a/overrides/overrides.js"></script>
<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '/SpagoBI/js/lib/ext-4.1.1a/resources/themes/geobi/default/tree/s.gif';
</script>

<%-- Javascript object useful for session expired management (see also sessionExpired.jsp) --%>
<% 
   
   String userAgent = request.getHeader("user-agent");
   String subsString = "";
   String info[] = null;
   String browsername ="";
   String browserversion ="";
   
   if (userAgent.contains("MSIE")) {
	   subsString = userAgent.substring( userAgent.indexOf("MSIE"));
	   info = (subsString.split(";")[0]).split(" ");	  
   }else if(userAgent.contains("Firefox")){
       subsString = userAgent.substring( userAgent.indexOf("Firefox"));
       info = (subsString.split(" ")[0]).split("/");
   }else if(userAgent.contains("Chrome")){
       subsString = userAgent.substring( userAgent.indexOf("Chrome"));
       info = (subsString.split(" ")[0]).split("/");
   }
   browsername = info[0];
   browserversion = info[1];
   String mapsUrl="#";
   String datasetUrl = "#";
   String loginUrl = "#";
   String langIT = "#";
   String langEN = "#";
   String langDE = "#";

   for(int i=0; i< jsonMenuList.length(); i++){
	   Object menuObj = jsonMenuList.get(i);
	   if(menuObj instanceof JSONObject) {
			JSONObject menuItem = jsonMenuList.getJSONObject(i);
			
			if(menuItem.has("iconCls") && menuItem.getString("iconCls").equalsIgnoreCase("folder_open")){
				mapsUrl = menuItem.getString("href").replace("'","\\'");
			}else if(menuItem.has("iconCls") && menuItem.getString("iconCls").equalsIgnoreCase("my_data")){
				datasetUrl = menuItem.getString("href").replace("'","\\'");
			}else if(menuItem.has("iconCls") && menuItem.getString("iconCls").equalsIgnoreCase("login")){
				loginUrl = menuItem.getString("href").replace("'","\\'");
			}else if(menuItem.has("itemLabel") && menuItem.getString("itemLabel") == "LANG"){
				langEN = "javascript:execUrl(\\'/SpagoBI/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&LANGUAGE_ID=en&COUNTRY_ID=US\\')";
				langIT = "javascript:execUrl(\\'/SpagoBI/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&LANGUAGE_ID=itn&COUNTRY_ID=IT\\')";
				langDE = "javascript:execUrl(\\'/SpagoBI/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&LANGUAGE_ID=en&COUNTRY_ID=US\\')";
			}	
	   }		
	}
%>
<% 
if (browsername.contains("MSIE")){
	if (browserversion.contains("7")){ %>
	 <link href="/SpagoBI/themes/geobi/css/home40/ie7.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }if (browserversion.contains("8")){ %>
	<link href="/SpagoBI/themes/geobi/css/home40/ie8.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }else{ %>
	<link href="/SpagoBI/themes/geobi/css/home40/ie9.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }	
}else{
%>	
	<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/geobi/css/home40/standard.css" media="screen,projection,print" type="text/css" />
<%} %>
<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';

Ext.onReady(function () {
	
	var firstUrl =  '<%= StringEscapeUtils.escapeJavaScript(firstUrlToCall) %>';  
	firstUrlTocallvar = firstUrl;
    Ext.tip.QuickTipManager.init();
    this.mainframe = Ext.create('Ext.ux.IFrame', 
    			{ xtype: 'uxiframe'
  	  			, src: firstUrl
  	  		//	, height: '100%'
  	  			});
    
    this.titlePath = Ext.create("Ext.Panel",{title :'Home'});
    
	function hideItem( menu, e, eOpts){
        console.log('bye bye ');
        menu.hide();
    }
	

 	var bannerHTML = 
 		'	<header class="header" id="header"> '+
		'		<div class="aux"> '+
		'			<a href="#" id="logo">GeoBI - Geographic Business Intelligence</a> '+
		'	        <nav class="main-buttons"> '+
		'	        	<ul> '+
		'	            	<li class="btn-maps active"><a href="<%=mapsUrl%>">Mappe<span></span></a></li> '+
		'	                <li class="btn-datasets"><a href="<%=datasetUrl%>">Dataset<span></span></a></li> '+
		'	            </ul> '+
		'	        </nav> '+
		'	    </div> '+
		'			<div class="top-bar" id="top-bar"> '+
		'	        <nav class="aux"> '+
		'	            <ul class="top-menu" id="top-menu"> '+
		'	                <li class="first"><a href="#">GeoBI Project</a></li> '+
		'	                <li><a href="#">Tutorial</a></li> '+
		'	                <li><a href="#">Termini e condizioni</a></li> '+
		'	                <li class="reserved last"><a href="<%=loginUrl%>">Area riservata</a></li> '+		
		//'	                <li class="reserved last"> '+
		//'	                	<span>Area riservata</span> '+		
		//'	                    <div class="login-panel"> '+
		//'	                    	<form action="<%=loginUrl%>" method="post"> '+
		//'	                        	<fieldset> '+
		//'	                            	<p class="title">Accedi</p> '+
		//'	                                <p class="not-registered">Non sei registrato? <a href="#">Registrati</a></p> '+
		//'	                                <div class="field"> '+
		//'	                                	<label for="l-username">Username</label> '+
		//'	                                    <input type="text" name="l-username" id="l-username" value="Username" /> '+
		//'	                                </div> '+
		//'	                                <div class="field last"> '+
		//'	                                	<label for="l-password">Password</label> '+
		//'	                                    <input type="text" name="l-password" id="l-password" value="Password" /> '+
		//'	                                </div> '+
		//'	                                <div class="submit"> '+
		//'	                                	<input type="submit" value="Login" /> '+
		//'	                                </div> '+
		//'	                                <a href="#" class="forgot-password">Password dimenticata?</a> '+
		//'	                            </fieldset> '+
		//'	                        </form> '+
		//'	                    </div> '+
		
		//'	                </li> '+
		'	            </ul> '+
		'	            <ul class="language-switcher"> '+
		'	                <li class="active"><a href="#">IT</a></li> '+
		'	                <li><a href="#">DE</a></li> '+
		'	                <li><a href="#">EN</a></li> '+
		'	            </ul> '+
		'	        </nav> '+
		'	    </div> '+
		'	</header>';
		
		
		
	var bannerBar = Ext.create("Ext.toolbar.Toolbar",{
		/*layout: {
		    //type: 'vbox',
		    align: 'left'
		},*/
    	autoScroll: false,
    	height: 130,
    	html: bannerHTML
    });
 
	var footerHtml = 
			'<footer class="footer" id="footer"> '+
		    	'<div class="aux">' +
					'<div class="left">' +
				    '	<p>Geobi.info geographic business intelligence | TIS innovation park | Siemensstr.19 - 39100 Bozen</p>' +
				    '	<p><a href="mailto:info@geobi.info">info@geobi.info</a> | +39 0471 068000 | MwSt.Nr. IT 01677580217</p>' +
				    ' 	<p><a href="#">Impressum & Privacy</a> | <a href="#">Forum & FAQs</a></p>' +
				    '</div>' +
				    '<ul class="logos">' +
				    '	<li class="tis"><a href="#">TIS - Innovation Park</a></li>' +
				    '   <li class="pab"><a href="#">Provincia Autonoma di Bolzano Alto Adige - Autonome Provinz Bozen Sudtirol</a></li>' +
				    '    <li class="ue"><a href="#">Unione Europea - Provincia Autonoma di Bolzano - Alto Adige</a></li>' +
				    '</ul>' +
				'</div>' +
			'</footer>';
		
	var footerBar = Ext.create("Ext.toolbar.Toolbar",{
		/*layout: {
		//    type: 'vbox',
		    align: 'left'
		},*/
    	autoScroll: false,
    	height:100,
    	html: footerHtml
    });
	
    this.mainpanel =  Ext.create("Ext.panel.Panel",{
    	autoScroll: false,
    	height: 700,
    	items: [mainframe],
    	fbar:footerBar,
    	tbar: bannerBar    	
    });
    
    Ext.create('Ext.Viewport', {    	
        layout: 'fit',
        items: [this.mainpanel]
    });
    
    
});

</script>
 