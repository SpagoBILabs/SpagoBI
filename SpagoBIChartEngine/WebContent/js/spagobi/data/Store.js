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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.define('Sbi.extjs.chart.data.Store', {
    extend: 'Ext.data.Store',
    require: 'Sbi.extjs.chart.data.Model',
    model: 'Sbi.extjs.chart.data.Model',
    filterOnLoad: false,
    filters: null,
    
    alias2FieldMetaMap: null,
	refreshTime: null,
	dsLabel: null,
	proxy: null

	, constructor: function(config) {
			this.init(config);

			config.groupers = {};
			config.filters = {};
			config.sorters = {};
			
			Ext.apply(this, config);		
	
			this.callParent(arguments);

			return this;
	    }

	, init: function(config){
		this.dsLabel = config.datasetLabel;
		var serviceConfig;
		var params = config.baseParams || {};		
		params.id = config.storeId;
		params.ds_label = config.datasetLabel;		

		var pars =  config.dsPars;
		var separator = '';
		var arParams = [];
		
		if(Ext.isArray(pars)) {		
			for(var i = 0; i < pars.length; i++) {
				var strParams = {};
				var elem = pars[i];
				for(e in elem) {
					strParams[e] = encodeURIComponent(elem[e]);							
					separator = ',';
				}
				arParams.push(strParams);
			}
		}

		//params.pars = Ext.util.JSON.encode(arParams) || [];
		params.pars = arParams || [];
		
		delete config.storeId;
		delete config.datasetLabel;	
		delete config.dsTypeCd;	
		delete config.dsPars;	
		
		var proxyUrl = Sbi.config.serviceRegistry.getServiceUrl({serviceName:  'GET_CHART_DATA_ACTION'
			   , baseParams:params
			    });
		
		this.proxy =  Ext.apply(this.createProxy(proxyUrl),{});

		this.refreshTime = config.refreshTime;	
		delete config.refreshTime;		
		return;
	}

	  /*
		, logRecord: function(r, depth) {
		    if (!depth) { depth = ''; }
		    alert(depth + Ext.encode(r.data));
		
		    Ext.each(r.childNodes, function(record, index){
		        this.logRecord(record, depth + '    ');
		    }, this);
		}
		
		, listeners: {
		    load: function(sender, node, records) {
		        Ext.each(records, function(record, index){
		            this.logRecord(record);
		        }, this);
		    }
		}
*/
	  , getFieldMetaByAlias: function(alias) {
			if (Ext.isArray(alias)){
				//alias = alias[0].toUpperCase();
				alias = alias[0];
			}else{
				//alias = alias.toUpperCase();
				alias = alias;
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
			var fields = meta.reader.metaData.fields;
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
		}
		
		, createProxy: function(url){
			
			var proxy = new Ext.data.proxy.Ajax({			
						 url: url,
						 format: 'json',
						 method: 'POST', 
					     extraParams: {start: -1, limit: -1},
						 reader: {
							 type:'json'
							, root: 'rows'
							, idProperty: 'id'
							, totalProperty:  'results'
						    , successProperty: 'success'							
						  }		          					       
						});
			return proxy;			
		}
});