Ext.define('Sbi.tools.dataset.SelfServiceDatasetsBrowser', {
	extend: 'Sbi.widgets.dataview.DataViewPanel'

	,config: {
    	//store: null,
    	//tpl: null ,
    	//itemSelector: null,    
    	//dataView: null,
		modelName: "Sbi.tools.dataset.DataSetModel"
	}

	, constructor: function(config) {
		this.services =[];
		this.initServices();
		//config.services = this.services;
		this.detailPanel =  Ext.create('Sbi.widgets.dataview.DataViewPanel',config);
		this.columns = [{dataIndex:"LABEL", header:"Name"}, {dataIndex:"DESCR", header:"Description"},{dataIndex:"CATEGORY_ID", header:"Category"}];
		this.fields = ["DS_ID","VERSION_NUM","ACTIVE","LABEL","NAME","DESCR","OBJECT_TYPE","DS_METADATA","PARAMS","CATEGORY_ID","TRANSFORMER_ID","PIVOT_COLUMN", 
		               "PIVOT_ROW","PIVOT_VALUE","NUM_ROWS","IS_PERSISTED","DATA_SOURCE_PERSIST_ID","IS_FLAT_DATASET","FLAT_TABLE_NAME",
		               "DATA_SOURCE_FLAT_ID","CONFIGURATION","USER"];
		this.detailPanel.on("save",this.onFormSave,this);
		this.filteredProperties = ["LABEL","DESCR", "CATEGORY_ID"];
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
			url : Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'selfservicedataset'}),
			reader: {
				type: 'json',
				root: 'root'
			}
	
		});
	
		this.callParent(arguments);
	}
	
	, initServices: function(baseParams){
		//da personalizzare per le operazioni di dettaglio , inserimento , cancellazione,..
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
});
