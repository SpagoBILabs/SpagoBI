Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
    extend: 'Sbi.widgets.compositepannel.ListDetailPanel'

    ,config: {
    	stripeRows: true
    }

	, constructor: function(config) {
		
		this.initServices();
		this.detailPanel =  Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services: this.services});
		this.columns = [{dataIndex:"DATASOURCE_LABEL", header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
		this.fields = ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
		this.detailPanel.on("save",this.onFormSave,this);
		this.detailPanel.on("test",this.onFormTest,this);
		this.filteredProperties = ["DATASOURCE_LABEL","DESCRIPTION"];
		this.buttonToolbarConfig = {
			newButton: true
			//,cloneButton: true
		};
		this.buttonColumnsConfig ={
			deletebutton:true
			//,selectbutton: true
		};
		
    	this.callParent(arguments);
    }
	
	, initServices: function(baseParams){
		this.services["getAllValues"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			    							serviceName: 'datasources/listall'
			    							, baseParams: baseParams
			    						});
		this.services["delete"]= Sbi.config.serviceRegistry.getRestServiceUrl({
											serviceName: 'datasources/delete'
											, baseParams: baseParams
		});
		this.services["save"]= Sbi.config.serviceRegistry.getRestServiceUrl({
											serviceName: 'datasources/save'
											, baseParams: baseParams
		});
		this.services["test"]= Sbi.config.serviceRegistry.getRestServiceUrl({
											serviceName: 'datasources/test'
											, baseParams: baseParams
		});
		this.services["getDialects"]= Sbi.config.serviceRegistry.getRestServiceUrl({
											serviceName: 'domains/listValueDescriptionByType'
											, baseParams: baseParams
										});
		    	
	}
	
	, onDeleteRow: function(record){
		Ext.Ajax.request({
  	        url: this.services["delete"],
  	        params: {DATASOURCE_ID: record.get('DATASOURCE_ID')},
  	        success : function(response, options) {
	      		if(response !== undefined && response.responseText !== undefined) {
	      			Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.deleted'));
	      			this.grid.store.remove(record);
	      			this.grid.store.commitChanges();
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
  	        },
  	        scope: this,
  			failure: Sbi.exception.ExceptionHandler.handleFailure      
  		});
	}
	
	, onFormSave: function(record){
		Ext.Ajax.request({
  	        url: this.services["save"],
  	        params: record,
  	        success : function(response, options) {
	      		if(response !== undefined && response.statusText !== undefined && response.statusText=="OK") {
	      			if(response.responseText!=null && response.responseText!=undefined){
	      				if(response.responseText.indexOf("error.mesage.description")>=0){
	      					Sbi.exception.ExceptionHandler.handleFailure(response);
	      				}
	      			}else{
	      				Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.saved'));
		      			var selectedRow = this.grid.getSelectionModel().getSelection();
		      			Ext.apply(selectedRow[0].data,record);
		      			this.grid.store.commitChanges();	
	      			}
	      			
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
  	        },
  	        scope: this,
  			failure: Sbi.exception.ExceptionHandler.handleFailure      
  		});
	}
	
	, onFormTest: function(record){
		Ext.Ajax.request({
  	        url: this.services["test"],
  	        params: record,
  	        success : function(response, options) {
	      		if(response !== undefined && response.statusText !== undefined && response.statusText=="OK") {
	      			Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.saved'));
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
  	        },
  	        scope: this,
  			failure: Sbi.exception.ExceptionHandler.handleFailure      
  		});
	}
	
	, onGridSelect: function(selectionrowmodel, record, index, eOpts){
		this.detailPanel.show();
		this.detailPanel.setFormState(record.data);
	}
});
    