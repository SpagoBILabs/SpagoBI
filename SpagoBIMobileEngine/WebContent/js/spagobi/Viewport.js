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
			if(loggedGlobal && loggedGlobal=="true"){
				app.views.form.hide();
				localStorage.setItem('app.views.launched', 'true');
				this.addMain();
				
				this.goHome();
			}else{
				this.goLogIn();
			}

		},this);

	    this.callParent(arguments);

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
	
	,goExecution: function(config){
		app.views.customTopToolbar.setViewModality("execution");
		app.views.customBottomToolbar.setViewModality("execution");
		if(config && config.noParametersPageNeeded ){
			app.views.customTopToolbar.hideItem("params");
			app.views.customBottomToolbar.hideItem("params");
		}
		this.setActiveItem(app.views.executionContainer, { type: 'fade' });	
	}
	
	,goParameters: function(){
		app.views.customTopToolbar.setViewModality("parameters");
		app.views.customBottomToolbar.setViewModality("parameters");
		this.setActiveItem(app.views.parameters, { type: 'fade' });	
	}
	
	,goHome: function(submodality){
		app.views.customTopToolbar.setViewModality("main",submodality);
		app.views.customBottomToolbar.setViewModality("main",submodality);
		//cleans the execution 
		app.views.executionContainer.clearExecutions();
		this.setActiveItem(app.views.main, { type: 'fade' });	
	}
	
	,addToolbarEvents: function(aToolbar){
		aToolbar.on("documentbrowserback",function(toolbar){
			if(toolbar.modality=="main"){
				app.views.main.documentBrowserBack();
			}
		},this);
		aToolbar.on("gotoparameters",function(toolbar){
			var activeExecutionInstance = app.views.executionContainer.getActiveExecutionInstance();
			if(activeExecutionInstance && activeExecutionInstance.isFromCross){
				var option = {
						docName:activeExecutionInstance.OBJECT_NAME,
						id: activeExecutionInstance.OBJECT_ID,
						label: activeExecutionInstance.OBJECT_LABEL,
						roleName: activeExecutionInstance.ROLE,
						sbiExecutionId: activeExecutionInstance.SBI_EXECUTION_ID,
						typeCode : activeExecutionInstance.TYPE_CODE,
						engine : activeExecutionInstance.ENGINE,
						isFromCross: activeExecutionInstance.isFromCross,
						params: activeExecutionInstance.paramsFromCross//filled only from cross navigation
				};

				app.controllers.parametersController.getParametersForExecutionAction(option, true);
			}else{
				this.goParameters();	
			}
				
		},this);
		aToolbar.on("refreshDoc",function(toolbar){
			app.views.executionContainer.refresh();
		},this);
		aToolbar.on("gohome",function(toolbar){
			app.controllers.mobileController.backToBrowser();
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
		