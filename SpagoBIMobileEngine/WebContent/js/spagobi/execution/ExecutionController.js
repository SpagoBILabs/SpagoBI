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
		
	}

	, executeTemplate: function(option, documentContainerPanel){

		var executionInstance = option.executionInstance;
		var typeCode =  executionInstance.TYPE_CODE;
		var engine =  executionInstance.ENGINE;
		
		var params = Ext.apply({}, executionInstance);
		params.PARAMETERS =  Ext.encode(executionInstance.PARAMETERS);
		
		if(typeCode != null && typeCode !== undefined && (typeCode == 'MOBILE_TABLE')){
			Ext.Ajax.request({
		        url: this.services['executeMobileTableAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp, 'table', documentContainerPanel, executionInstance);
		        	}
		        }
		    }); 
		}else if(typeCode != null && typeCode !== undefined && (typeCode == 'MOBILE_CHART')){
			Ext.Ajax.request({
		        url: this.services['executeMobileChartAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp, 'chart', documentContainerPanel, executionInstance);
		        	}
		        }
		    }); 
		}else if(typeCode != null && typeCode !== undefined && (typeCode == 'MOBILE_COMPOSED')){
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
	, createWidgetExecution: function(resp, type, documentContainerPanel, executionInstance){

		if (documentContainerPanel == undefined || documentContainerPanel == null) {
			app.views.execView = new app.views.ExecutionView({parameters: executionInstance.PARAMETERS});

		    var viewport = app.views.viewport;
		    viewport.add(app.views.execView);
		    app.views.execView.setWidget(resp, type);

		    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
	
		    app.views.execView.setExecutionInstance(executionInstance);
		    
		} else {
			app.views.execView.setWidgetComposed(resp, type, documentContainerPanel);
			documentContainerPanel.setExecutionInstance(executionInstance);
		}
		
		
	}

});