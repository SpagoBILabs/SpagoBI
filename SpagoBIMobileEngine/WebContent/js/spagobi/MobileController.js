app.controllers.MobileController = Ext.extend(Ext.Controller,{
	
	init: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = new Array();

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
	}

	, login: function(options){
		console.log('MobileController: Received event of login successfull');

		app.views.main.setItems();

		app.views.viewport.setActiveItem(app.views.main, { type: 'slide', direction: 'left' });
	}
	
	, showDetail: function(record) {
		var id = record.record.id;
		//determines if it is a document available for mobile presentetation
		var rec = record.record.attributes.record.data;
		var engine = rec.engine;
		var name= rec.name;
		var label= rec.label;
		var descr= rec.description;
		var date= rec.creationDate;
		var typeCode= rec.typeCode;
		var imageClass ="preview-item";
		if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.report)){
			imageClass ="preview-item-table";
		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.chart)){
			imageClass ="preview-item-chart";
		}else if(typeCode != null && typeCode !== undefined && (typeCode == Sbi.constants.documenttype.cockpit)){
			imageClass ="preview-item-composed";
		}else{
			return;
		}

		app.views.preview.showPreview( imageClass, rec);
	}
		
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
	    }); 
	}
	//params filled only from cross navigation
	, getParametersForExecutionAction: function(id, label, roleName, sbiExecutionId, engine, typeCode, isFromCross, params){
				
		  Ext.dispatch({
			  controller: app.controllers.parametersController,
			  action: 'getParametersForExecutionAction',
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
		try{
			app.views.execView.bottomTools.clearNavigation();
		}catch(error){
			app.views.execution.bottomTools.clearNavigation();
		}
		app.views.viewport.setActiveItem(app.views.main, { type: 'fade' });	
		app.views.viewport.doLayout();
  	}

	, backToParametersView: function(option){
		this.destroyExecutionView();
		//app.views.execView.clearNavigation();

	    app.views.viewport.setActiveItem(app.views.parameters, { type: 'fade' });
  	}


	//Destroy the execution panel
	, destroyExecutionView: function(){
		if(app.views.table != undefined && app.views.table != null){
			app.views.table.destroy();
		}
		if(app.views.chart != undefined && app.views.chart != null){
			app.views.chart.destroy();
		}
		if(app.views.composed != undefined && app.views.composed != null){
			app.views.composed.destroy();
		}
	}
});
