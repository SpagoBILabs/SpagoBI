Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		tbar : null,
		height: 600,
		user : '',
		datasetsServicePath: '',
		displayToolbar: true,
		PUBLIC_USER: 'public_user'
	}

	,
	constructor : function(config) {
		this.initConfig(config);
//		this.user = '';
		this.initServices();
		this.initStore();
		this.initToolbar();
		this.initViewPanel();
		this.layout='fit';
		this.items = [this.viewPanel];
		this.callParent(arguments);
		
		this.addEvents('order');
	}

	,
	initServices : function(baseParams) {
		this.services = [];

		this.services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName : this.datasetsServicePath,
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
		this.services["testDataSet"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset/testDataSet',
			baseParams: baseParams
		});
	}
	
	,
	initStore : function(baseParams) {
		Sbi.debug('DataViewPanel bulding the store...');
		
		this.filteredProperties = [ "label", "name","description","fileName","fileType", "catTypeCd","owner" ];
		
		this.sorters = [{property : 'dateIn', direction: 'DESC', description: LN('sbi.ds.moreRecent')}, 
		                {property : 'label', direction: 'ASC', description:  LN('sbi.ds.label')}, 
		                {property : 'name', direction: 'ASC', description: LN('sbi.ds.name')}, 
		                {property : 'fileName', direction: 'ASC', description:  LN('sbi.ds.fileName')},	
		                {property : 'fileType', direction: 'ASC', description: LN('sbi.ds.file.type')}, 
		                {property : 'catTypeCd', direction: 'ASC', description: LN('sbi.ds.catType')},						
						{property : 'owner', direction: 'ASC', description: LN('sbi.ds.owner')}];
		

		this.storeConfig = Ext.apply({
			model : this.getModelName(),
			filteredProperties : this.filteredProperties, 
			sorters: [],
			proxy: {
		        type: 'ajax'
		        , url: this.services["list"]
	         	, reader : {
	        		type : 'json',
	        		root : 'root'
	        	}
		     }
		}, {});

		// creates and returns the store
		Sbi.debug('DataViewPanel store built.');

		this.store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);				
		this.store.load({});
		
		this.categoriesStore = this.createCategoriesStore();
		this.datasetPropertiesStore = this.createDatasetMetadataPropertiesStore();
		this.datasetValuesStore = this.createDatasetMetadataValuesStore();


		this.scopeStore = Ext.create('Ext.data.Store', {
		    fields: ['field', 'value'],
		    data : [
		        {"field":"true", "value":"Public"},
		        {"field":"false", "value":"Private"}
		    ]
		});
		
		this.sortersCombo = this.createSortersStore({sorters: this.sorters});		
		
	}
	
	, initToolbar: function() {
		
		if (this.displayToolbar) {
			if (this.user !== '' && this.user !== this.PUBLIC_USER){
				//the button add isn't able for public user
				var newDatasetButton = new Ext.button.Button({
			    	text : LN('sbi.generic.add'),
					iconCls:'icon-add',
					width:70,
					listeners: {
						'click': {
			          		fn: this.addNewDataset,
			          		scope: this
			        	} 
					}
			    });
			     
				var additionalButtons = [];
				additionalButtons.push(newDatasetButton);
			}
			var ordersCombo = new Ext.form.ComboBox({
	//			fieldLabel: LN('sbi.ds.orderComboLabel') ,
				store : this.sortersCombo,
				name : 'ordersCombo',			
				width : 'auto',
				margin: '2 0 0 10',
				displayField : 'description', 
				valueField : 'property',
	//			labelStyle:'font-weight:bold;', 
				emptyText:LN('sbi.ds.orderComboLabel'),
				typeAhead : true, forceSelection : true,
				mode : 'local',
				triggerAction : 'all',
				selectOnFocus : true, editable : false,		   
				xtype : 'combo'	
			});
			
			var additionalSorters = [];
			additionalSorters.push(ordersCombo);
			
			var config = Ext.apply({store: this.store, additionalButtons:additionalButtons, additionalSorters:additionalSorters});
			config.alignToRight = true;
			config.emptyLabel = LN('sbi.ds.filterLabel');
			var toolbar =  Ext.create('Sbi.widgets.toolbar.InLineFilterAndOrder',config);
			toolbar.on("filter",function(filterConfig){
	      		this.filterString = filterConfig.filterString;
	      	},this);	
			toolbar.on("order",function(renderConfig){			
				this.store.sort(renderConfig.property, renderConfig.direction);
				this.viewPanel.refresh();
	      	},this);
	
			this.tbar = toolbar;
		}
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
	
	, createDatasetMetadataPropertiesStore: function(){
		Ext.define("DatasetMetadataPropertiesModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var datasetPropertiesStore=  Ext.create('Ext.data.Store',{
    		model: "DatasetMetadataPropertiesModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"DS_META_PROPERTY"},
    			url:  this.services['getCategories'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	datasetPropertiesStore.load();
    	
    	return datasetPropertiesStore;
	}
	
	, createDatasetMetadataValuesStore: function(){
		Ext.define("DatasetMetadataValuesModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var datasetValuesStore=  Ext.create('Ext.data.Store',{
    		model: "DatasetMetadataValuesModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"DS_META_VALUE"},
    			url:  this.services['getCategories'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	datasetValuesStore.load();
    	
    	return datasetValuesStore;
	}
	
	, createSortersStore: function(config){		
		var ordersStore = Ext.create('Ext.data.Store', {
		    fields: ["property","direction","description"],
		    data : config.sorters
		});
    	
		ordersStore.load();
    	
    	return ordersStore;
	}
	
	,
	addNewDataset : function() {		 
		var config =  {};
		config.categoriesStore = this.categoriesStore;
		config.datasetPropertiesStore = this.datasetPropertiesStore;
		config.datasetValuesStore = this.datasetValuesStore;
		config.scopeStore = this.scopeStore;
		config.user = this.user;
		config.isNew = true;
		this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);	
		this.wizardWin.on('save', this.saveDataset, this);
		this.wizardWin.on('getMetaValues', this.getMetaValues, this);
    	this.wizardWin.show();
	}
	
	, 
	modifyDataset: function(rec){
		if (rec != undefined){
			var config =  {};
			config.categoriesStore = this.categoriesStore;
			config.datasetPropertiesStore = this.datasetPropertiesStore;
			config.datasetValuesStore = this.datasetValuesStore;
			config.scopeStore = this.scopeStore;
			config.user = this.user;
			config.record = rec;
			config.isNew = false;
			this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',config);	
			this.wizardWin.on('save', this.saveDataset, this);
			this.wizardWin.on('delete', this.deleteDataset, this);
			this.wizardWin.on('getMetaValues', this.getMetaValues, this);
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
	
	,
	getMetaValues: function(values){
		var metaConfiguration = values.meta || [];
		delete values.meta;
		var params = values;
		params.meta = Ext.JSON.encode(metaConfiguration) ;
		Ext.Ajax.request({
			url: this.services["testDataSet"],
			params: params,			
			success : function(response, options) {				
				if(response !== undefined  && response.responseText !== undefined && response.statusText=="OK") {
					if(response.responseText!=null && response.responseText!=undefined){
						if(response.responseText.indexOf("error.mesage.description")>=0){
							this.wizardWin.disableButton('confirm');
							this.wizardWin.goBack(1);
							Sbi.exception.ExceptionHandler.handleFailure(response);
						}else{			
							var newMeta = response.responseText;
							var newMetaDecoded =  Ext.decode(newMeta);				 
							this.wizardWin.metaInfo.updateData(newMetaDecoded.datasetColumns);
							this.wizardWin.metaInfo.updateGridData(newMetaDecoded.meta);
							this.wizardWin.enableButton('confirm');
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
  