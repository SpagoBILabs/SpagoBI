app.controllers.MobileController = Ext.extend(Ext.Controller,{
	
	init: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = new Array();
		this.services['loadDocumentService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'LOAD_MOBILE_DOCUMENT_ACTION'
		});   
		
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
		var viewport = app.views.viewport;
		viewport.setActiveItem(app.views.main, { type: 'slide', direction: 'left' });
		
/*		var active = app.views.browser.getActiveItem();

		active.store.filter([
		              {filterFn: function(item) { 
		            	  var ite = item.get("id");
		            	  return ite == 0; }}
		          ]);*/

/*		app.views.browser.store.load({params:{node: 0}});
		app.views.browser.store.sync();
		app.views.browser.update();*/
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
		if(engine != null && engine !== undefined && engine == 'Mobile Engine'){

			//onClick="executeDocument();"
			var documentTpl = '<div class="preview-item" id="preview-'+id+'" '+
			'onClick="javascript: executeDocument('+id+',\''+label+'\');">' +
			'<div class="document-item-icon">' +			
			'<img src="' + Ext.BLANK_IMAGE_URL + '" ></img>' +
			'</div>' +
		    '<div class="item-desc">' +name+ '</div>'+
		    '<div class="item-desc"><b>engine: </b>' +engine+ '</div>'+
		    '<div class="item-desc"><b>description: </b>' +descr+ '</div>'+
		    '<div class="item-desc">' +date+ '</div>'+
		    '</div>';
			app.views.preview.showPreview( documentTpl);

		}else{
			app.views.preview.showPreview( '');
		}
	}
	
	, executeDocument: function(options) {
		
		Ext.Ajax.request({
            url: this.services['loadDocumentService'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: options.id},
            success: function(response, opts) {
            	this.getRoles(options.id, options.label);
            }
	    }); 

	  }
	
	, getRoles: function(id, label){
		
		Ext.Ajax.request({
            url: this.services['getRolesForExecutionService'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
                    var roleName = responseJson.root[0].name;
                    this.startNewExecutionProcess(id, label, roleName);
            	}
          	}
	    }); 
	}
	
	, startNewExecutionProcess: function(id, label, roleName){

		Ext.Ajax.request({
            url: this.services['startNewExecutionProcess'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
            		var execContextId = responseJson.execContextId;
            		this.getParametersForExecutionAction(id, label, roleName, execContextId);
            	}
            }
	    }); 
	}
	
	, getParametersForExecutionAction: function(id, label, roleName, sbiExecutionId){
				
		  Ext.dispatch({
			  controller: app.controllers.parametersController,
			  action: 'getParametersForExecutionAction',
			  id: id,
			  label: label,
			  roleName : roleName, 
			  sbiExecutionId : sbiExecutionId
		  });
	}
	
	, executeTemplate: function(option){

		var id = option.id;
		var label = option.label;
		var roleName = option.roleName;
		var sbiExecutionId = option.sbiExecutionId;
		
		Ext.Ajax.request({
            url: this.services['executeMobileTableAction'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName, SBI_EXECUTION_ID: sbiExecutionId},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var resp = Ext.decode(response.responseText);
            		this.createTableExecution(resp);
            	}
            }
	    }); 
	}
	
	, createTableExecution: function(resp){
		
		//these are settings for table object
		app.views.execView = new app.views.ExecutionView(resp);
	    //adds execution view directly to viewport
	    var viewport = app.views.viewport;
	    viewport.add(app.views.execView);
	    app.views.execView.setWidget(resp, 'table');
	    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
  	}
	, backToBrowser: function(){
		app.views.table.destroy();
	    app.views.viewport.setActiveItem(app.views.main, { type: 'fade' });
	    
  	}

});
