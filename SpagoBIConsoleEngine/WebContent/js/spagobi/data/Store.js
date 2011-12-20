/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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

Sbi.data.Store = function(config) {
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
			config.baseParams.ds_limitSS = config.limitSS || -1;
			config.baseParams.rowsLimit = config.rowsLimit || -1;
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
	
	Sbi.data.Store.superclass.constructor.call(this, config);
};

Ext.extend(Sbi.data.Store, Ext.data.JsonStore, {
//Ext.extend(Sbi.data.Store, Ext.ux.data.PagingJsonStore, {	
	
    
	alias2FieldMetaMap: null
	, refreshTime: null
	, dsLabel: null
	
    // -- public methods ----------------------------------------------------------------
	
	/*
	, refresh: function() {
		alert('Super refresh: '  + Sbi.data.Store.superclass.onRender.refresh);
	 	delete this.store.lastParams;
	 	this.doLoad(this.cursor);   
	}
	*/
	
	, getFieldMetaByAlias: function(alias) {
		// assert
		if(!this.alias2FieldMetaMap) {
			Sbi.Msg.showError('Impossible to [getFieldMetaByAlias]. Store has not loaded yet.', 'Wrong function call');
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
		
		Sbi.data.Store.superclass.onMetaChange.call(this, meta);
    }

   
    
});