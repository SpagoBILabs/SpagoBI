Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		tbar : null,
		height: 600,
		user : ''
	}

	,
	constructor : function(config) {

		this.user = '';
		this.initServices();
		this.initStore();
		this.initToolbar();
		this.initViewPanel();
		this.layout='fit';
		this.items = [this.viewPanel];
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
			serviceName: 'domainsforfinaluser/listValueDescriptionByType',
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
	
		
//		this.fields = [ "id", "label", "name", "description","catTypeVn", "dataSource" ];
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

		this.scopeStore = Ext.create('Ext.data.Store', {
		    fields: ['field', 'value'],
		    data : [
		        {"field":"true", "value":"Public"},
		        {"field":"false", "value":"Private"}
		    ]
		});
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
		config.user = this.user;
		config.autoScroll = true;
		this.viewPanel = Ext.create('Sbi.tools.dataset.DataSetsView', config);
		this.viewPanel.on('detail', this.modifyDataset, this);
		this.viewPanel.on('delete', this.deleteDataset, this);
		this.viewPanel.on('executeDocument',function(docType, inputType,  record){
			this.fireEvent('executeDocument',docType, inputType,  record);
		},this);
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
		config.scopeStore = this.scopeStore;
		config.user = this.user;
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
			config.scopeStore = this.scopeStore;
			config.user = this.user;
			config.record = rec;
			config.isNew = false;
			this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);	
			this.wizardWin.on('save', this.saveDataset, this);
			this.wizardWin.on('delete', this.deleteDataset, this);
	    	this.wizardWin.show();
		}
	}
	
	,
	saveDataset: function(values){
		var metaConfiguration = values.meta || [];
		delete values.meta;
		var params = values;
		params.meta = Ext.JSON.encode(metaConfiguration) ;
		Ext.Ajax.request({
			url: this.services["save"],
			params: params,			
			success : function(response, options) {				
				if(response !== undefined  && response.responseText !== undefined && response.statusText=="OK") {
					if(response.responseText!=null && response.responseText!=undefined){
						if(response.responseText.indexOf("error.mesage.description")>=0){
							Sbi.exception.ExceptionHandler.handleFailure(response);
						}else{						
							this.store.load({reset:true});
							this.wizardWin.destroy();						
							this.viewPanel.refresh();
							Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.saved'));
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
								if(response !== undefined  && response.responseText !== undefined && response.statusText=="OK") {
									if(response.responseText!=null && response.responseText!=undefined){
										if(response.responseText.indexOf("error.mesage.description")>=0){
											Sbi.exception.ExceptionHandler.handleFailure(response);
										}else{						
											this.store.load({reset:true});										
											this.viewPanel.refresh();			
											Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.deleted'));
										}
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
	
	/**
	 * Opens the loading mask 
	*/
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {    		
    		this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg: "  Wait...  "});
    	}
    	if (this.loadMask){
    		this.loadMask.show();
    	}
    }

	/**
	 * Closes the loading mask
	*/
	, hideMask: function() {
    	if (this.loadMask && this.loadMask != null) {	
    		this.loadMask.hide();
    	}
	} 

});
