/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 ﻿app.views.Viewport = Ext.extend(Ext.Panel,
	{
	fullscreen: true,
	layout: 'card',
	cardSwitchAnimation: 'slide',
	initComponent: function() 
	
	  {
	    //put instances of login into app.views namespace
	    Ext.apply(app.views, {
	        loginView: new app.views.LoginView(),
	        main:      new app.views.MainContainer(),
	        parameters:new app.views.ParametersView(),
	        execution: new app.views.ExecutionView ()

	    });
	    //put instances of loginView into viewport
	    Ext.apply(this, {
	        items: [
	            app.views.loginView,
	            app.views.main,
	            app.views.parameters,
	            app.views.execution
	        ]
	    });
	    
	    app.views.Viewport.superclass.initComponent.apply(this, arguments);
	    
		if(app.views.execution.loadingMaskForExec == null){
			app.views.execution.loadingMaskForExec = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});              
		}

        // invokes before each ajax request 
        Ext.Ajax.on('beforerequest', function(){        
                // showing the loadding mask
                app.views.execution.loadingMaskForExec.show();
        });

        // invokes after request completed 
        Ext.Ajax.on('requestcomplete', function(){      
                // showing the loadding mask
                app.views.execution.loadingMaskForExec.hide();
        });             

        // invokes if exception occured 
        Ext.Ajax.on('requestexception', function(){         
                //TODO: need to handle the server exceptions
        });
	  }

	});
		