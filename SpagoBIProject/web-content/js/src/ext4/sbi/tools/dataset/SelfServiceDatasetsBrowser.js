Ext.define('Sbi.tools.dataset.SelfServiceDatasetsBrowser', {
	extend: 'Sbi.widgets.dataview.DataViewPanel'

	,config: {
		autoLoad: true,
		modelName: "Sbi.tools.dataset.DataSetModel",
		viewPanel: null
	}

	, constructor: function(config) {
		this.services =[];	
		this.initServices();	
		this.columns = [{dataIndex:"label", header:"Name"}, {dataIndex:"description", header:"Description"}];		
		//this.columns = [{dataIndex:"label", header:"Name"}, {dataIndex:"description", header:"Description"},{dataIndex:"CATEGORY_ID", header:"Category"}];
		this.fields = ["id","label","name","description"];
		this.filteredProperties = ["label","name"];
		
		this.buttonToolbarConfig = {newButton: true};
		this.buttonColumnsConfig ={deletebutton:true};
		
	
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
		
		this.store = this.buildStore( this.getModelName());
		this.store.load({});
		
		this.tpl = this.buildTpl({src:Ext.LEAF_IMAGE_URL});
		config.services = this.services;	
		config.store = this.store;
		config.tpl = this.tpl;
		this.viewPanel =  Ext.create('Sbi.widgets.dataview.DataViewPanel',config);
		//this.viewPanel.tbar.on("click",this.addNewDataset,this);
		//this.callParent(arguments);
	}
	
	, initServices: function(baseParams){
		//da personalizzare per le operazioni di dettaglio , inserimento , cancellazione,..
		this.services["list"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset'
		  , baseParams: baseParams
		});
	}
	, addNewDataset: function(){
		alert('SelfServiceDataSetBROWSER: addNewDataset! ');
	}	
	, onClick : function(obj, rec, item, idx, e, opt){		
		  alert("*** SelfServiceDatasetBrowser: click of id: " + idx);
	 }
});
