Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		autoLoad : true,
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		tbar : null,
		height: 600
	}

	,
	constructor : function(config) {
		
		this.initServices();
		this.initStore();
		this.initToolbar();
		this.initViewPanel();
		this.layout='fit';
		
		this.callParent(arguments);
		
		this.addListener('detail', this.openDetail, this);
	}

	,
	initServices : function(baseParams) {
		this.services = [];
		this.services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName : 'selfservicedataset',
			baseParams : baseParams
		});
		this.services["getCategories"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'domains/listValueDescriptionByType'
				, baseParams: baseParams
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
		
		this.fields = [ "id", "label", "name", "description","catTypeVn" ];
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
		
		this.categoriesStore = this.createCategoriesStore();
	}
	
	, initToolbar: function() {
		var newDatasetButton = new Ext.button.Button({
	    	tooltip: LN('sbi.generic.add'),
			iconCls:'icon-add',
			width:50,
			listeners: {
				'click': {
	          		fn: this.addNewDataset,
	          		scope: this
	        	} 
			}
	    });
	     
		var toolbar =  Ext.create('Ext.toolbar.Toolbar',{renderTo: Ext.getBody(),height:30});
		toolbar.add('->');
		toolbar.add(' ');
		toolbar.add(newDatasetButton);
	
		this.tbar = toolbar;
	}
	
	,
	initViewPanel: function() {
		var config = {};
		config.services = this.services;
		config.store = this.store;
		config.actions = this.actions;
		this.viewPanel = Ext.create('Sbi.tools.dataset.DataSetsView', config);
		this.viewPanel.on('detail', this.openDetail(rec), this);
	}

	,
	onRender : function(obj, opt) {
		Sbi.widgets.dataview.DataViewPanel.superclass.onRender.call(this, opt);
	}

	,
	addNewDataset : function() {		 
		var config =  {};
		config.categoriesStore = this.categoriesStore;
		this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);		
    	this.wizardWin.show();
	}
	
	, createCategoriesStore: function(){
		Ext.define("CategoriesModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var categoriesStore=  Ext.create('Ext.data.Store',{
    		model: "CategoriesModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"CATEGORY_TYPE"},
    			url:  this.services['getCategories'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	categoriesStore.load();
    	
    	return categoriesStore;
	}
	
	, openDetail: function(rec){
		alert("openDetail del dataset Browser!!");
	}

});
