<%-- SpagoBI, the Open Source Business Intelligence suite

Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. --%>

<%@page language="java" 
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
%>
<%@ include file="/WEB-INF/jsp/wapp/homeBase.jsp"%>

    
<!-- Include Ext stylesheets here: 
<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/geobi/css/home40/layout.css" type="text/css" />
-->
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
   
   Map langUrls = new HashMap();

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
				List localesList = GeneralUtilities.getSupportedLocales();				
				for (int j = 0; j < localesList.size() ; j++) {
					String langUrl = "javascript:execUrl(\\'"+contextName+"/servlet/AdapterHTTP?ACTION_NAME=CHANGE_LANGUAGE&IS_PUBLIC_USER=TRUE&THEME_NAME="+currTheme;
					Locale aLocale = (Locale)localesList.get(j);
					langUrl += "&LANGUAGE_ID="+aLocale.getLanguage()+"&COUNTRY_ID="+aLocale.getCountry();
					langUrl += "\\')";
	 				langUrls.put( aLocale.getLanguage(), langUrl);
				}
			}	
	   }		
	}
%>

<link rel='stylesheet' type='text/css' href='<%=urlBuilder.getResourceLinkByTheme(request, "css/home40/standard.css",currTheme)%>'/>

<% 
if (browsername.contains("MSIE")){
	if (browserversion.contains("7")){ %>
	 <link href="<%=contextName %>/themes/geobi/css/home40/ie7.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }if (browserversion.contains("8")){ %>
	<link href="<%=contextName %>/themes/geobi/css/home40/ie8.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }else{ %>
	<link href="<%=contextName %>/themes/geobi/css/home40/ie9.css" rel="stylesheet" media="screen,projection,print" type="text/css" />
<%  }	
}
%>



<script>
sessionExpiredSpagoBIJS = 'sessionExpiredSpagoBIJS';

Ext.onReady(function () {
	//alert('<%=browsername%> - <%=browserversion%>');
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
		'	            	<li class="btn-maps"><a href="<%=mapsUrl%>">'+LN('sbi.generic.maps')+'<span></span></a></li> '+
		'	                <li class="btn-datasets"><a href="<%=datasetUrl%>">'+LN('sbi.browser.document.dataset')+'<span></span></a></li> '+
		'	            </ul> '+
		'	        </nav> '+
		'	    </div> '+
		'			<div class="top-bar" id="top-bar"> '+
		'	        <nav class="aux"> '+
		'	            <ul class="top-menu" id="top-menu"> '+
		'	                <li class="first"><a href="#">'+LN('home.header.geoBIProject')+'</a></li> '+
		'	                <li><a href="#">'+LN('home.header.tutorial')+'</a></li> '+
		'	                <li><a href="#">'+LN('home.herader.conditions')+'</a></li> '+
		'	                <li class="reserved last"><a href="<%=loginUrl%>">'+LN('home.header.reservedArea')+'</a></li> '+		
		'	            </ul> '+
		'	            <ul class="language-switcher"> '+
						<%java.util.Set keys = langUrls.keySet();
						  Iterator iterKeys = keys.iterator();
						  while(iterKeys.hasNext()) {
								String key = iterKeys.next().toString();
								String value = langUrls.get(key).toString();
								String activeClass = "";
								//if (key.equalsIgnoreCase(curr_language)){
								if (key.equalsIgnoreCase(locale.getLanguage())){
									activeClass = "class=\"active\"";
								}
						%>
		'					 <li <%=activeClass%>><a href="<%=value%>"><%=key%></a></li> '+
						<%}%>
		'	            </ul> '+
		'	        </nav> '+
		'	    </div> '+
		'	</header>';
		
	var footerHtml = 
			'<footer class="footer" id="footer"> '+
		    	'<div class="aux">' +
					'<div class="left">' +
				    '	<p>Geobi.info geographic business intelligence | TIS innovation park | Siemensstr.19 - 39100 Bozen</p>' +
				    '	<p><a href="mailto:info@geobi.info">info@geobi.info</a> | +39 0471 068000 | MwSt.Nr. IT 01677580217</p>' +
				    ' 	<p><a href="#">Impressum & Privacy</a> | <a href="#">Forum & FAQs</a></p>' +
				    '</div>' +
				    '<ul class="logos">' +
				    '	<li class="tis"><a href="#">'+LN('home.footer.logo.tis')+'</a></li>' +
				    '   <li class="pab"><a href="#">'+LN('home.footer.logo.pab')+'</a></li>' +
				    '    <li class="ue"><a href="#">'+LN('home.footer.logo.ue')+'</a></li>' +
				    '</ul>' +
				'</div>' +
			'</footer>';
		
	var bannerPanel = Ext.create("Ext.panel.Panel",{
		region: 'north',
	   	autoScroll: false,
	   	height: 142,
	   	html: bannerHTML
	});
			
	var footerPanel = Ext.create("Ext.panel.Panel",{
		region: 'south',
    	autoScroll: false,
    	height:60,
    	html: footerHtml
    });
	
    var mainPanel =  Ext.create("Ext.panel.Panel",{
    	autoScroll: false,
    	region: 'center',
    	height: 700,
    	items: [mainframe]  	
    });
    
    this.pagePanel =  Ext.create("Ext.panel.Panel",{
    	layout: 'border',
    	autoScroll: false,
    	items: [bannerPanel, mainPanel, footerPanel]	
    });
    
    Ext.create('Ext.Viewport', {    	
        layout: 'fit',
        items: [this.pagePanel]
    });
    
    
});

</script>
 