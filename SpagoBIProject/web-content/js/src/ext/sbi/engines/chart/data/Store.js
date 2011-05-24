/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.ns("Sbi.engines.chart.data");

Sbi.engines.chart.data.Store = function(config) {
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

			var params = {};
			params.MESSAGE_DET ="DATASET_EXEC";
			params.id = config.storeId;
			params.label = config.datasetLabel;
			params.dsTypeCd = config.dsTypeCd || "";
			params.pars = config.dsPars || [];
			params.trasfTypeCd = config.dsTransformerType || "";
			params.start =-1; // to get all values
			params.limit =-1; // to get all values
			
			delete config.id;
			delete config.label;	
			delete config.dsTypeCd;	
			delete config.pars;	
			delete config.trasfTypeCd;	
			
			config.url = Sbi.config.serviceRegistry.getServiceUrl({serviceName:  'MANAGE_DATASETS_ACTION'
																 , baseParams:params
																   });
		}	
	}
	
	this.refreshTime = config.refreshTime;	
	delete config.refreshTime;

	Sbi.engines.chart.data.Store.superclass.constructor.call(this, config);
};

//Ext.extend(Sbi.engines.chart.data.Store, Ext.data.JsonStore, {
Ext.extend(Sbi.engines.chart.data.Store, Ext.ux.data.PagingJsonStore, {	
	
    
	alias2FieldMetaMap: null
	, refreshTime: null
	, dsLabel: null
	
    // -- public methods ----------------------------------------------------------------
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

		Sbi.engines.chart.data.Store.superclass.onMetaChange.call(this, meta);
    }

});