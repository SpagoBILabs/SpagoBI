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

	}

	,
	initServices : function(baseParams) {
		this.services = [];
		this.services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName : 'selfservicedataset',
			baseParams : baseParams
		});
		this.services["getCategories"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'domains/listValueDescriptionByType',
			baseParams: baseParams
		});
		this.services["save"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset/save',
			baseParams: baseParams
		});
		this.services["delete"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset/delete',
			baseParams: baseParams
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
		this.viewPanel.on('detail', this.modifyDataset, this);
		this.viewPanel.on('delete', this.deleteDataset, this);
	}

	,
	onRender : function(obj, opt) {
		Sbi.widgets.dataview.DataViewPanel.superclass.onRender.call(this, opt);
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
	
	,
	addNewDataset : function() {		 
		var config =  {};
		config.categoriesStore = this.categoriesStore;
		config.isNew = true;
		this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);	
		this.wizardWin.on('save', this.saveDataset, this);
    	this.wizardWin.show();
	}
	
	, 
	modifyDataset: function(rec){
		if (rec != undefined){
			var config =  {};
			config.categoriesStore = this.categoriesStore;
			config.record = rec;
			config.isNew = false;
			this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);	
			this.wizardWin.on('save', this.saveDataset, this);
			this.viewPanel.on('delete', this.deleteDataset, this);
	    	this.wizardWin.show();
		}
	}
	
	,
	saveDataset: function(values){
		Ext.Ajax.request({
			url: this.services["save"],
			params: values,
			success : function(response, options) {
				if(response !== undefined && response.statusText !== undefined) {
					var responceText = Ext.decode(response.responseText);
					if(responceText.errors){
						Sbi.exception.ExceptionHandler.showErrorMessage(responceText.error, 'Service Error');
					}else{
						Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.saved'));
					}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			},
			scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
	}
	
	,
	deleteDataset: function(values){
		Ext.MessageBox.confirm(
				LN('sbi.generic.pleaseConfirm'),
				LN('sbi.generic.confirmDelete'),
				function(btn, text){
					if (btn=='yes') {
						Ext.Ajax.request({
							url: this.services["delete"],
							params: values,
							success : function(response, options) {
								if(response !== undefined && response.statusText !== undefined) {
									var responceText = Ext.decode(response.responseText);
									if(responceText.errors){
										Sbi.exception.ExceptionHandler.showErrorMessage(responceText.error, 'Service Error');
									}else{
										Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.deleted'));
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
								}
							},
							scope: this,
							failure: Sbi.exception.ExceptionHandler.handleFailure      
						})
					}
				},
				this
			);
	}

});
