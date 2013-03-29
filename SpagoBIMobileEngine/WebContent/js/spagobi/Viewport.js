/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * @authors
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 * - Monica Franceschini (monica.franceschini@eng.it)
 * 
 */
 
  
 
  
 
Ext.define('app.views.Viewport',{
	extend: 'Ext.Panel',
	config:{
		fullscreen: true,
		layout: 'card',
		cardSwitchAnimation: 'slide'
	}


	,initialize: function(){
		
		
		app.views.loginView = Ext.create('app.views.LoginView');
		app.views.parameters = Ext.create("app.views.ParametersView");
		app.views.customTopToolbar = Ext.create("app.views.CustomToolbar",{toolbarConfiguration:Sbi.settings.top.toolbar, docked: 'top'});
		app.views.customBottomToolbar = Ext.create("app.views.CustomToolbar",{toolbarConfiguration:Sbi.settings.bottom.toolbar, docked: 'bottom'});
		app.views.executionContainer = Ext.create("app.views.ExecutionContainerView",{containerToolbars: [app.views.customTopToolbar,app.views.customBottomToolbar]});
		this.add(app.views.loginView);
		this.add(app.views.parameters);
		this.add(app.views.executionContainer);
		this.add(app.views.customTopToolbar);
		this.add(app.views.customBottomToolbar);
		this.addToolbarEvents(app.views.customTopToolbar);
		this.addToolbarEvents(app.views.customBottomToolbar);
		
		this.on("activate",function(){
			app.views.customTopToolbar.setViewModality("login");
			this.goLogIn();
		},this);


		
//	    //put instances of login into app.views namespace
//	    Ext.apply(app.views, {
//	        loginView: new app.views.LoginView(),
//	        main:      new app.views.MainContainer(),
//	        parameters:new app.views.ParametersView(),
//	        execution: new app.views.ExecutionView ()
//
//	    });
//	    //put instances of loginView into viewport
//	    Ext.apply(this, {
//	        items: [
//	            app.views.loginView,
//	            app.views.main,
//	            app.views.parameters,
//	            app.views.execution
//	        ]
//	    });
	    
	    this.callParent(arguments);
//	    this.goLogIn();

	  }
	
	, addMain: function(){
		app.views.main = Ext.create('app.views.MainContainer',{containerToolbar: app.views.customTopToolbar});
		this.add(app.views.main);
	}
	
	,goLogIn: function(){
		app.views.customTopToolbar.setViewModality("login");
		app.views.customBottomToolbar.setViewModality("login");
		this.setActiveItem(app.views.loginView, { type: 'fade' });	
	}
	
	,goExecution: function(){
		app.views.customTopToolbar.setViewModality("execution");
		app.views.customBottomToolbar.setViewModality("execution");
		this.setActiveItem(app.views.executionContainer, { type: 'fade' });	
	}
	
	,goParameters: function(){
		app.views.customTopToolbar.setViewModality("parameters");
		app.views.customBottomToolbar.setViewModality("parameters");
		this.setActiveItem(app.views.parameters, { type: 'fade' });	
	}
	
	,goHome: function(){
		app.views.customTopToolbar.setViewModality("main");
		app.views.customBottomToolbar.setViewModality("main");
		this.setActiveItem(app.views.main, { type: 'fade' });	
	}
	
	,addToolbarEvents: function(aToolbar){
		aToolbar.on("documentbrowserback",function(toolbar){
			if(toolbar.modality=="main"){
				app.views.main.documentBrowserBack();
			}
		},this);
		aToolbar.on("gotoparameters",function(toolbar){
			this.goParameters();
		},this);
		aToolbar.on("refreshDoc",function(toolbar){
			if(app.views.executionContainer && app.views.executionContainer.getActiveExecutionInstance()){
				app.controllers.executionController.executeTemplate( { executionInstance: app.views.executionContainer.getActiveExecutionInstance()});				
			}
		},this);
		aToolbar.on("gohome",function(toolbar){
			app.controllers.mobileController.backToBrowser();
		},this);
		aToolbar.on("logout",function(toolbar){
			app.controllers.mobileController.logout();
		},this);
		aToolbar.on("logout",function(toolbar){
			app.controllers.mobileController.logout();
		},this);
		aToolbar.on("previous",function(toolbar){
			app.views.executionContainer.goToPreviousExecutions();
		},this);
		aToolbar.on("navigationbuttonclicked",function(toolbar, selectedItemPos){
			app.views.executionContainer.changeActiveDocument(selectedItemPos);
		},this);

	}
	

    
   
    	

	
    
});
		