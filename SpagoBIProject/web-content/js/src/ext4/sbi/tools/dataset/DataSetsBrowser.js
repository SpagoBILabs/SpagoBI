Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		autoLoad : true,
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		tbar : null
	}

	,
	constructor : function(config) {
		
		this.initServices();
		this.initStore();
		this.initViewPanel();
	
		this.callParent(arguments);
	}

	,
	initServices : function(baseParams) {
		this.services = [];
		this.services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName : 'selfservicedataset',
			baseParams : baseParams
		});
	}
	
	,
	initStore : function(baseParams) {
		this.columns = [ {
			dataIndex : "label",
			header : "Name"
		}, {
			dataIndex : "description",
			header : "Description"
		} ];
	
		// set the proxy of the model.. Is STATIC
		var model = Ext.ModelMgr.getModel(this.getModelName());
		model.setProxy({
			type : 'rest',
			url : Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName : 'selfservicedataset'
			}),
			reader : {
				type : 'json',
				root : 'root'
			}
		});
		
		this.fields = [ "id", "label", "name", "description" ];
		this.filteredProperties = [ "label", "name" ];
		
		Sbi.debug('DataViewPanel bulding the store...');

		this.storeConfig = Ext.apply({
			model : this.getModelName(),
			filteredProperties : [ "label", "name" ]
		}, {});

		// creates and returns the store
		Sbi.debug('DataViewPanel store built.');

		this.store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);
				
		this.store.load({});
	}
	
	
	
	,
	initViewPanel: function() {
		var config = {};
		config.services = this.services;
		config.store = this.store;
		this.viewPanel = Ext.create('Sbi.tools.dataset.DataSetsView', config);
	}

	,
	onRender : function(obj, opt) {
		Sbi.widgets.dataview.DataViewPanel.superclass.onRender.call(this, opt);
	}

	,
	addNewDataset : function() {
		alert("new dataset, chiamare wizard...");
	}

	
	



});
