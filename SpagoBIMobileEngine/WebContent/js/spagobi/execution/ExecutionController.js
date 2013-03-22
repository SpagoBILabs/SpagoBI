/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.controllers.ExecutionController',{

	extend:'Ext.app.Controller',
	config:{
	},
	constructor: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = new Array();
		this.services['executeMobileTableAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_TABLE_ACTION'
			, baseParams: params
		});
		
		this.services['executeMobileChartAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_CHART_ACTION'
			, baseParams: params
		});
		
		this.services['executeMobileComposedAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_COMPOSED_ACTION'
			, baseParams: params
		});
		this.services['getDocumentInfoAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_DOCUMENT_INFO_ACTION'
			, baseParams: params
		});
	}
	, getDocumentInfoForCrossNavExecution: function(options){
		var targetDoc = options.targetDoc;
		var params = options.params;
		Ext.Ajax.request({
	        url: this.services['getDocumentInfoAction'],
	        scope: this,
	        method: 'post',
	        params: {OBJECT_LABEL: targetDoc},
	        success: function(response, opts) {
	        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
	        		var resp = Ext.decode(response.responseText);
	        		var doc = resp['document'];
	        		if(doc != undefined && doc != null){
		        		var type = doc.typeCode;
			  			app.controllers.mobileController.getRoles({
							  id: doc.id,
							  label: doc.label, 
							  engine: doc.engine, 
							  typeCode: doc.typeCode,
							  parameters: params,
							  isFromCross: true
						});
	        		}
	        	}
	        }
	        ,failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			} 
	    });
	}
	, executeTemplate: function(option, documentContainerPanel){

		var executionInstance = option.executionInstance;
		var typeCode =  executionInstance.TYPE_CODE;
		var engine =  executionInstance.ENGINE;
		
		var params = Ext.apply({}, executionInstance);
		params.PARAMETERS =  Ext.encode(executionInstance.PARAMETERS);

		if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.report)){

			Ext.Ajax.request({
		        url: this.services['executeMobileTableAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        timeout : ajaxReqGlobalTimeout,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp, 'table', documentContainerPanel, executionInstance, option);
		        	}
		        }
				,failure: function(response, options) {
					Sbi.exception.ExceptionHandler.handleFailure(response, options);
				}
		    }); 
		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.chart)){
			Ext.Ajax.request({
		        url: this.services['executeMobileChartAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        timeout : ajaxReqGlobalTimeout,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp, 'chart', documentContainerPanel, executionInstance, option);
		        	}
		        }
				,failure: function(response, options) {
					Sbi.exception.ExceptionHandler.handleFailure(response, options);
				}
		    }); 
		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.cockpit)){
			
			Ext.Ajax.request({
		        url: this.services['executeMobileComposedAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		resp.executionInstance = params;
		        		this.createWidgetExecution(resp,  'composed', null, executionInstance, option);
		        	}
		        }
				,failure: function(response, options) {
					Sbi.exception.ExceptionHandler.handleFailure(response, options);
				}
		    }); 
		}
	}

	, simpleNavigationManagement: function(resp, type, executionInstance){
		app.controllers.mobileController.destroyExecutionView();
		if(app.views.execView == undefined || app.views.execView == null){
			app.views.execView = Ext.create("app.views.ExecutionView",{parameters: executionInstance.PARAMETERS});
		}
	    var viewport = app.views.viewport;	    
	    viewport.add(app.views.execView);	
//	    app.views.execView.showBottomToolbar();
	    app.views.execView.setWidget(resp, type);
//		app.views.execView.bottomTools.setBreadCrumb(executionInstance.OBJECT_LABEL, 
//				executionInstance.OBJECT_ID,
//				executionInstance.TYPE_CODE,
//				executionInstance.PARAMETERS);
	    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
	}

	, createWidgetExecution: function(resp, type, documentContainerPanel, executionInstance, composedComponentOptions){

		if (documentContainerPanel == undefined || documentContainerPanel == null) {

			this.simpleNavigationManagement(resp, type, executionInstance);
			app.views.execView.setExecutionInstance(executionInstance);
		} else {
			documentContainerPanel.addWidgetComposed(resp, type, composedComponentOptions);
			//documentContainerPanel.setExecutionInstance(executionInstance);
		}
		
		
	}

});