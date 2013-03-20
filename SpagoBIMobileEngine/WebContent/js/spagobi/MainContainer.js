/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
Ext.define('app.views.MainContainer',{
		extend: 'Ext.Panel',
		config:{
		    fullscreen: true,
		    autoRender: true,
			title: 'Main container',
			cls: 'card4',
			layout: {
				type: 'hbox',
	        	align: 'stretch'
	        }
		},
		
		initialize: function(){		
			this.callParent(arguments);
			app.views.browser = Ext.create("app.views.DocumentBrowser");
			//app.views.preview = Ext.create('Ext.Panel', {html: "preview"});
		   this.add(app.views.browser);
		    //this.add(app.views.preview);
		    //refresh management
		    localStorage.setItem('app.views.browser', 'true');
		},
		
		reloadPanel: function(){
			app.views.browser.reloadPanel();
		}
	

});