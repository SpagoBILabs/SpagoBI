/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 /**
 * @authors
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 * - Monica Franceschini (monica.franceschini@eng.it)  
  */
  
 
Ext.define('app.controllers.MobileController',{
	extend: 'Ext.app.Controller',
	
	config:{
	},
	
	constructor: function(){
		console.log("Mobile controller");
		this.services =new Array();
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		

		this.services['getRolesForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_ROLES_FOR_EXECUTION_ACTION'
			, baseParams: params
		});
		
		this.services['startNewExecutionProcess'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'START_EXECUTION_PROCESS_ACTION'
			, baseParams: params
		});


		this.services['executeMobileTableAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_TABLE_ACTION'
			, baseParams: params
		});
//		//this.callParent(arguments);
	}
	

	, login: function(options){
		console.log('MobileController: Received event of login successfull');
		app.views.viewport.addMain();
		//app.views.main.reloadPanel();
		app.views.viewport.goHome();
	}
	
//	, showDetail: function(record) {
//		var id = record.record.id;
//		//determines if it is a document available for mobile presentetation
//		var rec = record.record.data;
//		var engine = rec.engine;
//		var name= rec.name;
//		var label= rec.label;
//		var descr= rec.description;
//		var date= rec.creationDate;
//		var typeCode= rec.typeCode;
//		var imageClass ="preview-item";
//		if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.report)){
//			imageClass ="preview-item-table";
//		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.chart)){
//			imageClass ="preview-item-chart";
//		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.cockpit)){
//			imageClass ="preview-item-composed";
//		}else{
//			return;
//		}
//
//		app.views.preview.showPreview( imageClass, rec);
//	}
//		
	, getRoles: function(options){

		Ext.Ajax.request({
            url: this.services['getRolesForExecutionService'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: options.id, OBJECT_LABEL: options.label, isFromCross:false},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
                    var roleName = responseJson.root[0].name;
                    this.startNewExecutionProcess(options.id, 
                    								options.label, 
                    								roleName,  
                    								options.engine, 
                    								options.typeCode, 
                    								options.isFromCross,
                    								options.parameters//filled only from cross navigation 
                    								);
            	}
          	}
            ,failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}
	    }); 
	}
	
	, startNewExecutionProcess: function(id, label, roleName, engine, typeCode, isFromCross, params){

		Ext.Ajax.request({
            url: this.services['startNewExecutionProcess'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
            		var execContextId = responseJson.execContextId;
            		this.getParametersForExecutionAction(id, label, roleName, execContextId, engine, typeCode, isFromCross, params);
            	}
            }
            ,failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}
	    }); 
	}
	//params filled only from cross navigation
	, getParametersForExecutionAction: function(id, label, roleName, sbiExecutionId, engine, typeCode, isFromCross, params){
				
		  app.controllers.parametersController.getParametersForExecutionAction({
			  id: id,
			  label: label,
			  roleName : roleName, 
			  sbiExecutionId : sbiExecutionId,
			  engine: engine, 
			  typeCode: typeCode,
			  isFromCross : isFromCross,
			  params: params //filled only from cross navigation
		  });
	}

	, backToBrowser: function(opt){
		this.destroyExecutionView();
		console.log("Go Home");
		
		//DA RIVEDERE QUANDO REINTRODURREMO LA BREADCRUMB...
/*		try{
			app.views.execView.bottomTools.clearNavigation();
		}catch(error){
			app.views.execution.bottomTools.clearNavigation();
		}*/
		app.views.browser.goToRoot();	
		app.views.viewport.goHome();	
		
//		app.views.viewport.doLayout();
  	}

	, backToParametersView: function(option){
		this.destroyExecutionView();
		//app.views.execView.clearNavigation();
		app.views.viewport.goParameters();
  	}


	//Destroy the execution panel
	, destroyExecutionView: function(){
		if(app.views.execView){
			app.views.execView.removeAll(true);
		}
	}
	
    , logout : function () {
		var func = function(answer) {
	        if (answer === "yes") {
	        	Ext.Ajax.request({
                     url : Sbi.env.invalidateSessionURL
                     , method : 'POST'
                     , success : function(response, opts) {
                    	 // refresh page
                    	 localStorage.removeItem('app.views.launched');
                    	 localStorage.removeItem('app.views.browser');
                    	 window.location.href = Sbi.env.contextPath;
                     }
                     , failure : Sbi.exception.ExceptionHandler.handleFailure
                     , scope : this
                });
	        }
		};
		Sbi.exception.ExceptionHandler.showConfirmMessage(null, 'Are you sure you want to logout?', func);
	}
});
