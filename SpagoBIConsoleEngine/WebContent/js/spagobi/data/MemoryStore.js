/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.data");

Sbi.data.MemoryStore = function(config) {
	this.alias2NameMap = {};
	this.dsLabel = config.datasetLabel;
	if(!config.url) {
		var serviceConfig;
		if(config.serviceName) {
			serviceConfig = {serviceName: config.serviceName};
			if(config.baseParams) {
				serviceConfig.baseParams = config.baseParams;
				delete config.baseParams;
			}
			delete config.serviceName;
			
			config.url = Sbi.config.serviceRegistry.getServiceUrl( serviceConfig );
		} else if(config.datasetLabel)	{
			serviceConfig = {serviceName: 'GET_CONSOLE_DATA_ACTION'};
				
			config.baseParams = config.baseParams || {};
			config.baseParams.ds_label = config.datasetLabel;
			config.baseParams.ds_rowsLimit = config.rowsLimit || -1;
			config.baseParams.ds_memoryPagination = config.memoryPagination;
			this.dsLabel = config.baseParams.ds_label;
			serviceConfig.baseParams = config.baseParams;			
			delete config.datasetLabel;
			delete config.baseParams;			
			config.url = Sbi.config.serviceRegistry.getServiceUrl( serviceConfig );
		}	
	}
	
	this.refreshTime = config.refreshTime;	
	delete config.refreshTime;
	
	Sbi.data.MemoryStore.superclass.constructor.call(this, config);
};

Ext.extend(Sbi.data.MemoryStore, Ext.ux.data.PagingJsonStore, {
	    
	alias2FieldMetaMap: null
	, refreshTime: null
	, dsLabel: null
	
    // -- public methods ----------------------------------------------------------------
	
	/*
	, refresh: function() {
		alert('Super refresh: '  + Sbi.data.MemoryStore.superclass.onRender.refresh);
	 	delete this.store.lastParams;
	 	this.doLoad(this.cursor);   
	}
	*/
	
	, getFieldMetaByAlias: function(alias) {
		// assert
		if(!this.alias2FieldMetaMap) {			
			Sbi.Msg.showError('Impossible to [getFieldMetaByAlias of '+ alias+' ]. Store '+this.dsLabel+' has not loaded yet.', 'Wrong function call');
			//return null;
		}
	
		var m = this.alias2FieldMetaMap[alias];
		if(m){
			if(m.length === 0) {
				m = undefined;
			} else if(m.length === 1) {
				m = m[0];
			} else {
				m = m[0];
				alert('Warning: there are [' + m.length + '] fields whose alias is [' + alias + ']. Only the first one will be used');
			}
		}
		return m;
	}

	, getFieldNameByAlias: function(alias) {
		var fname;
		var fmeta = this.getFieldMetaByAlias(alias);
		if(fmeta) {
			fname = fmeta.name;
		}
		return fname;
	}
	
	, loadStore: function(){
		this.load({
			params: {}, 
			callback: function(){this.ready = true;}, 
			scope: this, 
			add: false
		});
	}
    
	, getDsLabel: function(){
		return this.dsLabel;
	}
	
    // -- private methods ----------------------------------------------------------------
   
    , onMetaChange : function(meta){
		this.alias2FieldMetaMap = {};
		var fields = meta.fields;
		for(var i = 0, l = fields.length, f; i < l; i++) {
			f = fields[i];
			if( typeof f === 'string' ) {
				f = {name: f};
			}
			f.header = f.header || f.name;
			if(!this.alias2FieldMetaMap[f.header]) {
				this.alias2FieldMetaMap[f.header] = new Array();
			}
			this.alias2FieldMetaMap[f.header].push(f);
		}
		
		Sbi.data.MemoryStore.superclass.onMetaChange.call(this, meta);
    }

   
    
});