Ext.define('Sbi.tools.dataset.SelfServiceDatasetsBrowser', {
	extend: 'Sbi.widgets.dataview.DataViewPanel'

	,config: {
		autoLoad: true,
		modelName: "Sbi.tools.dataset.DataSetModel",
		viewPanel: null
	}

	, constructor: function(config) {
		this.services =[];	
		this.columns = [{dataIndex:"LABEL", header:"Name"}, {dataIndex:"DESCR", header:"Description"},{dataIndex:"CATEGORY_ID", header:"Category"}];		
		this.fields = ["DS_ID","VERSION_NUM","ACTIVE","LABEL","NAME","DESCR","OBJECT_TYPE","DS_METADATA","PARAMS","CATEGORY_ID","TRANSFORMER_ID","PIVOT_COLUMN", 
		               "PIVOT_ROW","PIVOT_VALUE","NUM_ROWS","IS_PERSISTED","DATA_SOURCE_PERSIST_ID","IS_FLAT_DATASET","FLAT_TABLE_NAME",
		               "DATA_SOURCE_FLAT_ID","CONFIGURATION","USER"];
		this.filteredProperties = ["LABEL","DESCR", "CATEGORY_ID"];
		
		/*	this.buttonToolbarConfig = {
				newButton: true
		};
		this.buttonColumnsConfig ={
				deletebutton:true
		};
		*/
	
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
		
		this.initServices();		
		this.store = this.buildStore(this.getModelName());
		this.store.load({});
		this.tpl = this.buildTpl({src:Ext.LEAF_IMAGE_URL},this.store);
		config.services = this.services;	
		config.store = this.store;
		config.tpl = this.tpl;
		this.viewPanel =  Ext.create('Sbi.widgets.dataview.DataViewPanel',config);
	
		this.callParent(arguments);
	}
	
	, initServices: function(baseParams){
		//da personalizzare per le operazioni di dettaglio , inserimento , cancellazione,..
		this.services["list"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset'
		  , baseParams: baseParams
		});
	}
	
});
