/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		user : '',
		datasetsServicePath: '',
		autoScroll:true,
		displayToolbar: true,
		PUBLIC_USER: 'public_user',
	    //id:'this',
	    isTech: false, //for only certified datasets
	    qbeEditDatasetUrl : ''
	}

	,
	constructor : function(config) {
		this.initConfig(config);
		this.initServices();
		this.initStore();
		this.initToolbar();
		this.initViewPanel();
		this.items = [this.bannerPanel,this.viewPanel];
		this.callParent(arguments);
//		this.doLayout();
		
		this.addEvents('order');
	}

	,
	initServices : function(baseParams) {
		this.services = [];
		
		if(baseParams == undefined){
			baseParams ={};
		}
		baseParams.isTech = this.config.isTech;
		baseParams.showOnlyOwner = Sbi.settings.mydata.showOnlyOwner;

		
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
		/*
		this.services["getDataStore"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'selfservicedataset/getDataStore',
			baseParams: baseParams
		});
		*/
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
		this.datasetGenericPropertiesStore = this.createDatasetGenericPropertiesStore();
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
			
			var bannerHTML = this.createBannerHtml({});
			this.bannerPanel = new Ext.Panel({
				height: 105,
				border:0,
			   	autoScroll: false,
			   //	style:"position:'absolute';z-index:800000;float:left;width:100%;",
			   	html: bannerHTML
			});			
		}
	}
	
	,
	initViewPanel: function() {
		var config = {};
		config.services = this.services;
		config.store = this.store;
		config.actions = this.actions;
		config.user = this.user;
		config.fromMyDataCtx = this.displayToolbar;
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
	
	, createDatasetGenericPropertiesStore: function(){
		Ext.define("DatasetMetadataGenericPropertiesModel", {
    		extend: 'Ext.data.Model',
            fields: ["VALUE_NM","VALUE_DS","VALUE_ID"]
    	});
    	
    	var datasetGenericPropertiesStore=  Ext.create('Ext.data.Store',{
    		model: "DatasetMetadataGenericPropertiesModel",
    		proxy: {
    			type: 'ajax',
    			extraParams : {DOMAIN_TYPE:"DS_GEN_META_PROPERTY"},
    			url:  this.services['getCategories'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	datasetGenericPropertiesStore.load();
    	
    	return datasetGenericPropertiesStore;
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
		config.datasetGenericPropertiesStore = this.datasetGenericPropertiesStore;
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
			config.datasetGenericPropertiesStore = this.datasetGenericPropertiesStore;
			config.datasetPropertiesStore = this.datasetPropertiesStore;
			config.datasetValuesStore = this.datasetValuesStore;
			config.scopeStore = this.scopeStore;
			config.user = this.user;
			config.record = rec;
			config.isNew = false;
			config.qbeEditDatasetUrl = this.qbeEditDatasetUrl;
			switch (rec.dsTypeCd) {
				case 'File' : 
					this.wizardWin = Ext.create('Sbi.tools.dataset.DataSetsWizard', config);	
					this.wizardWin.on('save', this.saveDataset, this);
					this.wizardWin.on('delete', this.deleteDataset, this);
					this.wizardWin.on('getMetaValues', this.getMetaValues, this);
			    	this.wizardWin.show();
					break;
				case 'Qbe' :
					config.width = this.getWidth() - 50,
					config.height = this.getHeight() - 50,
					this.wizardWin = Ext.create('Sbi.tools.dataset.QbeDataSetsWizard', config);	
					this.wizardWin.on('save', this.saveDataset, this);
					this.wizardWin.on('delete', this.deleteDataset, this);
					this.wizardWin.on('getMetaValues', this.getMetaValues, this);
			    	this.wizardWin.show();
			    	break;
			}
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
							if (this.wizardWin.isOwner){
								this.wizardWin.enableButton('confirm');
							}
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
	
	, filterStore: function(filterString) {
		this.store.load({filterString: filterString});
	}
	
	, sortStore: function(value) {			
		var sortEls = Ext.get('sortList').dom.childNodes;
		//move the selected value to the first element
		for(var i=0; i< sortEls.length; i++){
			if (sortEls[i].id == value){					
				sortEls[i].className = 'active';
				break;
			} 
		}
		//append others elements
		for(var i=0; i< sortEls.length; i++){
			if (sortEls[i].id !== value){
				sortEls[i].className = '';		
			}
		}
		

		for (sort in this.sorters){
			var s = this.sorters[sort];
			if (s.property == value){
				this.store.sort(s.property, s.direction);
				break;
			}
		}
		
		this.viewPanel.refresh();
	}	

	, createBannerHtml: function(communities){
    	var communityString = '';
    	//hidden 'ALL' button for favourites (until they aren't managed)
//        for(i=0; i< communities.root.length; i++){
//        	var funct = communities.root[i].functId;
//        	communityString += '<li><a href="#" onclick="javascript:Ext.getCmp(\'this\').loadFolder('+funct+', null)">';
//        	communityString += communities.root[i].name;
//        	communityString +='</a></li>';
//        }
//        
        var createButton = '';
        if (this.user !== '' && this.user !== this.PUBLIC_USER){
        	createButton += ' <a id="newDataset" href="#" onclick="javascript:Ext.getCmp(\'this\').addNewDataset(\'\')" class="btn-add"><span class="highlighted">'+LN('sbi.generic.create')+'</span> '+LN('sbi.browser.document.dataset')+'<span class="plus">+</span></a> ';
        }
        
        var bannerHTML = ''+
//     		'<div class="aux"> '+
     		'<div class="main-datasets-list"> '+
    		'    <div class="list-actions-container"> '+ //setted into the container panel
//    		'		<ul class="list-tab"> '+
//    		'	    	<li class="active first"><a href="#" onclick="javascript:Ext.getCmp(\'this\').loadFolder(null, null, \'ALL\')">'+LN('sbi.generic.all')+'</a></li> '+
//    					communityString+
////    		'	        <li class="favourite last"><a href="#">'+LN('sbi.browser.document.favourites')+'</a></li> '+
//    		'		</ul> '+
    		'	    <div class="list-actions"> '+
    					createButton +
    		'	        <form action="#" method="get" class="search-form"> '+
    		'	            <fieldset> '+
    		'	                <div class="field"> '+
    		'	                    <label for="search">'+LN('sbi.browser.document.searchDatasets')+'</label> '+
    		'	                    <input type="text" name="search" id="search" onclick="this.value=\'\'" onkeyup="javascript:Ext.getCmp(\'this\').filterStore(this.value)" value="'+LN('sbi.browser.document.searchKeyword')+'" /> '+
    		'	                </div> '+
    		'	                <div class="submit"> '+
    		'	                    <input type="text" value="Cerca" /> '+
    		'	                </div> '+
    		'	            </fieldset> '+
    		'	        </form> '+
    		'	         <ul class="order" id="sortList">'+
    		'	            <li id="dateIn" class="active"><a href="#" onclick="javascript:Ext.getCmp(\'this\').sortStore(\'dateIn\')">'+LN('sbi.ds.moreRecent')+'</a> </li> '+
//    		'	            <li id="label"><a href="#" onclick="javascript:Ext.getCmp(\'this\').sortStore(\'label\')">'+LN('sbi.ds.label')+'</a></li> '+
    		'	            <li id="name"><a href="#" onclick="javascript:Ext.getCmp(\'this\').sortStore(\'name\')">'+LN('sbi.ds.name')+'</a></li> '+
    		'	            <li id="owner"><a href="#" onclick="javascript:Ext.getCmp(\'this\').sortStore(\'owner\')">'+LN('sbi.ds.owner')+'</a></li> '+
    		'	        </ul> '+
    		'	    </div> '+
    		'	</div> '+
    		'</div>' ;
//        var dh = Ext.DomHelper;
//        var b = this.bannerPanel.getEl().update(bannerHTML);

        return bannerHTML;
    }
	

});
  