/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 app.views.MainContainer = Ext.extend(Ext.Panel,

		{

	    fullscreen: true,
	    autoRender: true,

		initComponent: function ()	{
	
			this.title = 'Main container';
			this.cls = 'card4',
			this.layout = {
		        type: 'hbox',
		        align: 'stretch'
		    };
			console.log('init main container');
			
			app.views.MainContainer.superclass.initComponent.apply(this, arguments);

			
		}
		, setItems: function(){
			
			this.fullscreen= true;
			if(app.views.browser == undefined || app.views.browser == null){
			    Ext.apply(app.views, {
			    	browser: 	  new app.views.DocumentBrowser(),
			    	preview:      new app.views.DocumentPreview()
	
			    });
			    this.add(app.views.browser);
			    this.add(app.views.preview);
			}
		    /*PAY ATTENTION TO INVOKE DO LAYOUT METHOD OF MAIN CONTAINER...otherwise no child item is displayed!!!!*/
		    this.doLayout();  

		}
		

	});