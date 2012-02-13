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
		var typeCode= rec.typeCode;
		var imageClass ="preview-item";
		if(engine != null && engine !== undefined && (engine == 'TableMobileEngine' || engine == 'Table Mobile Engine')){
			imageClass ="preview-item-table";

		}else if(engine != null && engine !== undefined && (engine == 'ChartMobileEngine' || engine == 'Chart Mobile Engine')){
			imageClass ="preview-item-chart";
		}else if(engine != null && engine !== undefined && (engine == 'ComposedMobileEngine' || engine == 'Composed Mobile Engine')){
			imageClass ="preview-item-composed";
		}else{
			return;
		}
		
		var documentTpl = '<div class="preview-item" id="preview-'+id+'" '+
		'onClick="javascript: executeDocument('+id+',\''+label+'\', \''+engine+'\', \''+typeCode+'\');">' +
		'<div class="'+imageClass+'">' +			
		'<img src="' + Ext.BLANK_IMAGE_URL + '" ></img>' +
		'</div>' +
	    '<div class="item-desc">' +name+ '</div>'+
	    '<div class="item-desc"><b>engine: </b>' +engine+ '</div>'+
	    '<div class="item-desc"><b>description: </b>' +descr+ '</div>'+
	    '<div class="item-desc">' +date+ '</div>'+
	    '</div>';
		app.views.preview.showPreview( documentTpl);
	}
	
	, executeDocument: function(options) {
		
		Ext.Ajax.request({
            url: this.services['loadDocumentService'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: options.id},
            success: function(response, opts) {
            	this.getRoles(options.id, options.label, options.engine, options.typeCode);
            }
	    }); 

	  }
	
	, getRoles: function(id, label, engine, typeCode){
		
		Ext.Ajax.request({
            url: this.services['getRolesForExecutionService'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
                    var roleName = responseJson.root[0].name;
                    this.startNewExecutionProcess(id, label, roleName, engine, typeCode);
            	}
          	}
	    }); 
	}
	
	, startNewExecutionProcess: function(id, label, roleName, engine, typeCode){

		Ext.Ajax.request({
            url: this.services['startNewExecutionProcess'],
            scope: this,
            method: 'post',
            params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName},
            success: function(response, opts) {
            	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
            		var responseJson = Ext.decode(response.responseText);
            		var execContextId = responseJson.execContextId;
            		this.getParametersForExecutionAction(id, label, roleName, execContextId, engine, typeCode);
            	}
            }
	    }); 
	}
	
	, getParametersForExecutionAction: function(id, label, roleName, sbiExecutionId, engine, typeCode){
				
		  Ext.dispatch({
			  controller: app.controllers.parametersController,
			  action: 'getParametersForExecutionAction',
			  id: id,
			  label: label,
			  roleName : roleName, 
			  sbiExecutionId : sbiExecutionId,
			  engine: engine, 
			  typeCode: typeCode
		  });
	}

	, backToBrowser: function(){
		if(app.views.table != undefined && app.views.table != null){
			app.views.table.destroy();
		}
	    app.views.viewport.setActiveItem(app.views.main, { type: 'fade' });
	    
  	}

});
