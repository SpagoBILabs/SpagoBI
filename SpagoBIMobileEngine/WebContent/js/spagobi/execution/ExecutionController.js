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

		var typeCode =  option.executionInstance.TYPE_CODE;
		var engine =  option.executionInstance.ENGINE;
		
		var params = Ext.apply({PARAMETERS: Ext.encode(option.parameters)},option.executionInstance);
		
		if((engine == 'TableMobileEngine' || engine == 'Table Mobile Engine') && typeCode =='MOBILE'){
			Ext.Ajax.request({
		        url: this.services['executeMobileTableAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp, params.PARAMETERS, 'table', documentContainerPanel);
		        	}
		        }
		    }); 
		}else if((engine == 'ChartMobileEngine' || engine == 'Chart Mobile Engine') && typeCode =='MOBILE'){
			Ext.Ajax.request({
		        url: this.services['executeMobileChartAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		this.createWidgetExecution(resp,params.PARAMETERS, 'chart', documentContainerPanel);
		        	}
		        }
		    }); 
		}else if((engine == 'ComposedMobileEngine' || engine == 'Composed Mobile Engine') && typeCode =='MOBILE'){
			Ext.Ajax.request({
		        url: this.services['executeMobileComposedAction'],
		        scope: this,
		        method: 'post',
		        params: params,
		        success: function(response, opts) {
		        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
		        		var resp = Ext.decode(response.responseText);
		        		resp.executionInstance = params;
		        		this.createWidgetExecution(resp, params.PARAMETERS, 'composed');
		        	}
		        }
		    }); 
		}
	}
	, createWidgetExecution: function(resp, parameters, type, documentContainerPanel){

		if(documentContainerPanel==undefined || documentContainerPanel==null){
			app.views.execView = new app.views.ExecutionView({parameters: parameters});

		    var viewport = app.views.viewport;
		    viewport.add(app.views.execView);
		    app.views.execView.setWidget(resp, type);

		    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
	
		}else{
			app.views.execView.setWidgetComposed(resp, type, documentContainerPanel);
		}
		
	}

});