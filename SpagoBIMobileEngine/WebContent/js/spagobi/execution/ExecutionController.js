app.controllers.ExecutionController = Ext.extend(Ext.Controller,{
	
	init: function()  {
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
			  			Ext.dispatch({
							  controller: app.controllers.mobileController,
							  action: 'getRoles',
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
		        		this.createWidgetExecution(resp, 'table', documentContainerPanel, executionInstance);
		        	}
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
		        		this.createWidgetExecution(resp, 'chart', documentContainerPanel, executionInstance);
		        	}
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
		        		this.createWidgetExecution(resp,  'composed', null, executionInstance);
		        	}
		        }
		    }); 
		}
	}
/*	, crossNavigationManagement: function(resp, type, executionInstance){
		
		app.controllers.mobileController.destroyExecutionView();
		var exe = new app.views.ExecutionView({parameters: executionInstance.PARAMETERS});
		
		if(app.views.crossExecView == undefined || app.views.crossExecView == null){
			//when executing back to home this will be destroyed
			app.views.crossExecView = new app.views.CrossExecutionView();
		}
		exe.hideBottomToolbar();
		exe.title= executionInstance.OBJECT_LABEL;
		exe.setWidget(resp, type, true);
		app.views.crossExecView.setBreadCrumb(executionInstance.OBJECT_LABEL, 
				executionInstance.OBJECT_ID,
				executionInstance.TYPE_CODE,
				executionInstance.PARAMETERS);
		
		app.views.crossExecView.add(exe);

		app.views.viewport.add(app.views.crossExecView);
		
		
		app.views.viewport.setActiveItem(app.views.crossExecView, { type: 'slide', direction: 'left' });
		app.views.crossExecView.setActiveItem(exe , { type: 'fade'});
	}*/
	, simpleNavigationManagement: function(resp, type, executionInstance){
		app.controllers.mobileController.destroyExecutionView();
		if(app.views.execView == undefined || app.views.execView == null){
			app.views.execView = new app.views.ExecutionView({parameters: executionInstance.PARAMETERS});
		}
	    var viewport = app.views.viewport;	    
	    viewport.add(app.views.execView);	
	    app.views.execView.showBottomToolbar();
	    app.views.execView.setWidget(resp, type);
		app.views.execView.bottomTools.setBreadCrumb(executionInstance.OBJECT_LABEL, 
				executionInstance.OBJECT_ID,
				executionInstance.TYPE_CODE,
				executionInstance.PARAMETERS);
	    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
	}

	, createWidgetExecution: function(resp, type, documentContainerPanel, executionInstance){

		if (documentContainerPanel == undefined || documentContainerPanel == null) {

			if(executionInstance.isFromCross){
				//cross navigation
				//this.crossNavigationManagement(resp, type, executionInstance);

				this.simpleNavigationManagement(resp, type, executionInstance);

			}else{
				//default navigation
				this.simpleNavigationManagement(resp, type, executionInstance);
			}
			app.views.execView.setExecutionInstance(executionInstance);
		} else {
			app.views.execView.setWidgetComposed(resp, type, documentContainerPanel);
			documentContainerPanel.setExecutionInstance(executionInstance);
		}
		
		
	}

});