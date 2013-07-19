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

<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/js/lib/ext-4.1.1a/overrides/resources/css/spagobi.css" type="text/css" />
<link id="spagobi-ext-4" rel="styleSheet" href ="/SpagoBI/themes/geobi/css/home40/layout.css" type="text/css" />
<script type="text/javascript">
    Ext.BLANK_IMAGE_URL = '/SpagoBI/js/lib/ext-4.1.1a/resources/themes/images/default/tree/s.gif';
  
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
	   	    dock: 'top',
	   	    items: itemsM
    	}]
    });
    
    Ext.create('Ext.Viewport', {
    	
        layout: 'top',
        items: [this.mainpanel]
    });
    
    
});

	
</script>
