Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
	extend: 'Sbi.widgets.compositepannel.ListDetailPanel'

	,config: {
		stripeRows: true,
		modelName: "Sbi.tools.datasource.DataSourceModel"
	}

	, constructor: function(config) {
		this.services =[];
		this.initServices();
		this.detailPanel =  Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services: this.services});
		this.columns = [{dataIndex:"DATASOURCE_LABEL", header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
		this.fields = ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
		this.detailPanel.on("save",this.onFormSave,this);
		this.detailPanel.on("test",this.onFormTest,this);
		this.filteredProperties = ["DATASOURCE_LABEL","DESCRIPTION"];
		this.buttonToolbarConfig = {
				newButton: true
		};
		this.buttonColumnsConfig ={
				deletebutton:true
		};
	
		//set the proxy of the model.. Is STATIC
		var model = Ext.ModelMgr.getModel(this.getModelName());
		model.setProxy({
			type: 'rest',
			url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'datasources'}),
			reader: {
				type: 'json',
				root: 'root'
			}
	
		});
	
		this.callParent(arguments);
	}
	
	, initServices: function(baseParams){
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
		var recordToDelete = Ext.create("Sbi.tools.datasource.DataSourceModel",record.data);
		recordToDelete.destroy({
			success : function(object, response, options) {
				if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") {
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
	
		var recordToSave = Ext.create("Sbi.tools.datasource.DataSourceModel",record);
		recordToSave.save({
			success : function(object, response, options) {
	
				if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") {
					response = response.response ;
					if(response.responseText!=null && response.responseText!=undefined){
						if(response.responseText.indexOf("error.mesage.description")>=0){
							Sbi.exception.ExceptionHandler.handleFailure(response);
						}else{
							var respoceJSON = Ext.decode(response.responseText);
							if(respoceJSON.DATASOURCE_ID){
								record.DATASOURCE_ID = respoceJSON.DATASOURCE_ID;
							}
							Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.saved'));
							var selectedRow = this.grid.getSelectionModel().getSelection();
							
							
							//unused.. Its a workaround because it doesn't update the values in the grids...
							selectedRow[0].set("DESCRIPTION",selectedRow.DESCRIPTION);
							
							
							selectedRow[0].data = Ext.apply(selectedRow[0].data,record);
							this.grid.store.commitChanges();	
							this.detailPanel.setFormState(record);
						}
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
				if(response !== undefined && response.statusText !== undefined) {
					var responceText = Ext.decode(response.responseText);
					if(responceText.error){
						Sbi.exception.ExceptionHandler.showErrorMessage(responceText.error, 'Service Error');
					}else{
						Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.saved'));
					}
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
