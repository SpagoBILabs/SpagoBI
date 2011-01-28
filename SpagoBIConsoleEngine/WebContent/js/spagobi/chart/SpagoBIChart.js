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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.chart");


Sbi.chart.SpagoBIChart = function(config) {	
	
	this.bindStoreBeforeSwfInit = config.bindStoreBeforeSwfInit || false;
	
	// because swfInit do not work for spago chart...
	/*
	if(this.bindStoreBeforeSwfInit) {
		this.bindStore(config.store, true);
	} else {
		this.store = config.store;
	}
	*/
	//... always bind store in constructor
	this.bindStore(config.store, true);
	
	if(config.bindStoreBeforeSwfInit) { delete config.bindStoreBeforeSwfInit; }
	if(config.store) { delete config.store; }
	
	
	
	// encode
	var c = Ext.apply({}, config);

	if (c.storeManager){
		this.storeManager = c.storeManager;
		delete c.storeManager;
	}
	
	if(c.ownerCt) {
		delete c.ownerCt;
	}
		
	for(p in c) {
		if( (typeof c[p]) === 'object') {
			c[p] = Ext.util.JSON.encode(c[p]);
			c[p] = c[p].replace(new RegExp('"','g'), '|');
		}
	}
	
	this.flashVars = Ext.applyIf(c, this.CHART_DEFAULT_CONFIG);
	

	this.flashVars.scale = 'exactfit'; 
	this.flashParams = this.flashParams || {};
	this.flashParams.scale = 'exactfit';
	this.flashVars.isIE = Ext.isIE;

	Sbi.chart.SpagoBIChart.superclass.constructor.call(this, config);
	/*
	if(c.xtype === 'chart.sbi.livelines') {
		alert(this.includeFields + ' - ' +  config.includeFields);
	}
	*/
};

Ext.extend(Sbi.chart.SpagoBIChart, Ext.FlashComponent, {
    
	store: null
	, storeMeta: null
	, bindStoreBeforeSwfInit: null
	, url: null
	, isLastWidget: null
	
	// if it is good for ext chart it is also good for us :)
	//, disableCaching: Ext.isIE || Ext.isOpera
	, disableCaching: false //the cache is ever active
    , disableCacheParam: '_dc'
	
	
	
    // -- public methods -------------------------------------------------------------------
    
   
    
    
    // -- private methods ------------------------------------------------------------------
    
	, initComponent : function(){
		Sbi.chart.SpagoBIChart.superclass.initComponent.call(this);
    	if(!this.url){
        	this.url = Sbi.chart.SpagoBIChart.CHART_BASE_URL + this.CHART_SWF;
    	}
    	if(this.disableCaching){
            this.url = Ext.urlAppend(this.url, String.format('{0}={1}', this.disableCacheParam, new Date().getTime()));
        }
    	   	
    	this.addEvents(
    		'beforerefresh'
    		, 'refresh'
    	);

    	this.autoScroll = true;  	
	}
	
	, isSwfReady: function() {
		return true;
	}

	// never invoked for the moment
	, onSwfReady : function(isReset){
        Ext.chart.Chart.superclass.onSwfReady.call(this, isReset);
        alert('onSwfReady');
        if(!isReset && !this.bindStoreBeforeSwfInit){
        	alert('Bind store');
            this.bindStore(this.store, true);
        }
        
        this.refresh.defer(10, this);
    }


	, refresh: function() {
		if( !this.isSwfReady() ) {
			if(this.bindStoreBeforeSwfInit) {
				// some charts can queue pending data refresh and then apply 
				// them as soon as the swf object is initialized
				this.onPendingRefresh();
			}
			return;
		}
		
		if(this.fireEvent('beforerefresh', this) !== false){
			this.onRefresh();			
			this.fireEvent('refresh', this);
		}
    	
    }
	
	, onRefresh: Ext.emptyFn
	
	, onPendingRefresh: function() {
		alert('Chart is unable to handle incoming data before inizialization');
	}
	
    , bindStore : function(store, initial){
        if(!initial && this.store){
        	this.unbindStore(store !== this.store && this.store.autoDestroy);
        }
        
        if(store){
        	store.on("datachanged", this.refresh, this);
            store.on("add", this.refresh, this);
            store.on("remove", this.refresh, this);
            store.on("update", this.refresh, this);
            store.on("clear", this.refresh, this);
            store.on('metachange', this.onStoreMetaChange, this);
        }
        
        this.store = store;
        if(store && !initial){
            this.refresh();
        }
    }
    
    , unbindStore: function(destroy) {
    	destroy = destroy || false;
    	if(destroy){
            this.store.destroy();
        }else{
            this.store.un("datachanged", this.refresh, this);
            this.store.un("add", this.refresh, this);
            this.store.un("remove", this.refresh, this);
            this.store.un("update", this.refresh, this);
            this.store.un("clear", this.refresh, this);
            this.store.un('metachange', this.onStoreMetaChange, this);
        }
    }
	
	, onStoreMetaChange: function(s, m) {
		this.storeMeta = m;
	}
	
});

Sbi.chart.SpagoBIChart.CHART_BASE_URL =  '/SpagoBIConsoleEngine/swf/spagobichart/';


Sbi.chart.Multileds = Ext.extend(Sbi.chart.SpagoBIChart, {
	
	CHART_SWF: 'multileds.swf'
	, CHART_DEFAULT_CONFIG: {
		//title:'SpagoBI Multileds'
			fields: Ext.util.JSON.encode([
		    {
		    	header: 'EffortIdx',
		    	name:'EffortIndex', 
		    	descValue:'descEffortIndex',
		    	rangeMaxValue: 100, 
		    	secondIntervalUb: 66, 
		    	firstIntervalUb: 10, 
		    	rangeMinValue: 0
		    }, {
		    	header: 'Compet.',
		    	name:'Competitiveness', 
		    	descValue:'descCompetitiveness',
		    	rangeMaxValue: 100, 
		    	secondIntervalUb: 66, 
		    	firstIntervalUb: 33, 
		    	rangeMinValue: 0
		    }, {
		    	header: 'CostOpt.',
		    	name:'CostOptimization', 
		    	descValue:'descCostOptimization',
		    	rangeMaxValue: 100, 
		    	secondIntervalUb: 66, 
		    	firstIntervalUb: 33, 
		    	rangeMinValue: 0
		    }, {
		    	header: 'Health',
		    	name:'Health', 
		    	descValue:'descHealth',
		    	rangeMaxValue: 100, 
		    	secondIntervalUb: 66, 
		    	firstIntervalUb: 33, 
		    	rangeMinValue: 0
		    }
		])
	}
		
	, isSwfReady: function() {
		return !!this.swf.loadData;
	}
	
	, onRefresh: function() {
			var data = {};
			var rec = this.store.getAt(0);
			if(rec) {
				var fields = this.storeMeta.fields;
				for(var i = 0, l = fields.length, f; i < l; i++) {
					f = fields[i];
					//alert("f: " + f.toSource());
					if( (typeof f) === 'string') {
						f = {name: f};
					}
					var alias = f.header || f.name;
					if(alias === 'recNo') continue;
					//data[alias] = rec.get(f.name);
					
					var tmpDescValue = this.getDescriptionColumn(alias);
					if (tmpDescValue!== undefined && tmpDescValue != ''){											
						data[alias] = rec.get(f.name) + '|' + rec.get(this.store.getFieldNameByAlias(tmpDescValue));
					}else{
						data[alias] = rec.get(f.name);
					}
				}
				this.swf.loadData(data);
			}
	}
	
	// checks if the column is configurated as visible into template
	, getDescriptionColumn: function(alias){
		if (this.fields === undefined) return '';
		
		var desc = '';
		for (var i = 0; i < this.fields.length; i++){
			if (alias === this.fields[i].name ){
				desc = this.fields[i].descValue;
				break;
			}				
		}
		return desc;	
		
	}
});
Ext.reg('chart.sbi.multileds', Sbi.chart.Multileds);


Sbi.chart.Livelines = Ext.extend(Sbi.chart.SpagoBIChart, {
	
	CHART_SWF: 'livelines.swf'
	, CHART_DEFAULT_CONFIG: {
		rangeMinValue: 0
		, rangeMaxValue: 120 
		, stepY: 40
		, domainValueNumber: 18
		, title:'SpagoBI Liveline'
	}
	
	, isSwfReady: function() {
		return !!this.swf.loadData;
	}
	
	, onRefresh: function() {
			var data = {};
			var rec = this.store.getAt(0);
			if(rec) {
				var fields = this.storeMeta.fields;
				for(var i = 0, l = fields.length, f; i < l; i++) {
					f = fields[i];
					if( (typeof f) === 'string') {
						f = {name: f};
					}
					var alias = f.header || f.name;
					if(alias === 'recNo' || !this.isVisible(alias)) continue;
					
					data[alias] = rec.get(f.name);				
				}
				this.swf.loadData(data);
			}
			
	}
	
	// checks if the column is configurated as visible into template
	, isVisible: function(alias){
		if (this.fields === undefined) return true;
		
		var isVisible = false;
		for (var i = 0; i < this.fields.length; i++){
			if (alias === this.fields[i] ){
				isVisible = true;
				break;
			}				
		}
		
		if(this.includeFields !== undefined && this.includeFields === false) {
			isVisible = !isVisible;
		}
		
		return isVisible;	
		
	}
});
Ext.reg('chart.sbi.livelines', Sbi.chart.Livelines);


Sbi.chart.Speedometer = Ext.extend(Sbi.chart.SpagoBIChart, {
	
	CHART_SWF: 'speedometer.swf'
	, CHART_DEFAULT_CONFIG: {
		minValue: 0
		, maxValue: 100
		, lowValue: 33
		, highValue: 66
		, field: 'EffortIndex'
	}
		
	, isSwfReady: function() {
		return !!this.swf.setValue;
	}

	, onRender : function(ct, position) {
		//this.flashVars.paramWidth = ct.getWidth();
		//this.flashVars.paramHeight = ct.getHeight();
		Sbi.chart.SpagoBIChart.superclass.onRender.call(this, ct, position);
	}
	
	
	, onRefresh: function() {
		var value;
		var rec = this.store.getAt(0);
		if(rec) {
			var fName = this.store.getFieldNameByAlias(this.flashVars.field);			
			var tmpDescValue = this.flashVars.descValue;
			if (tmpDescValue!== undefined && tmpDescValue != ''){											
				value = rec.get(fName) + '|' + rec.get(this.store.getFieldNameByAlias(tmpDescValue));
			}else{
				value = rec.get(fName);
			}
			//value = rec.get(fName);
		}
		this.swf.setValue(value);			
	}
	
});
Ext.reg('chart.sbi.speedometer', Sbi.chart.Speedometer);

Sbi.chart.Semaphore = Ext.extend(Sbi.chart.SpagoBIChart, {
	
	CHART_SWF: 'semaphore.swf'
	, CHART_DEFAULT_CONFIG: {
	//	title: 'Title',
  	    header: 'Effort',
    	field:'EffortIndex', 
    	orientation:'vertical',
    	rangeMaxValue: 100, 
    	rangeSecondInterval: 66, 
    	rangeFirstInterval: 10, 
    	rangeMinValue: 0
	}
		
	, isSwfReady: function() {
		//return this.swf.setValue;
		return !!this.swf.setValue;
	}

	, onRender : function(ct, position) {		
		Sbi.chart.SpagoBIChart.superclass.onRender.call(this, ct, position);
	}
	
	, onRefresh: function() {
		var value;
		var rec = this.store.getAt(0);
		if(rec) {
			var fName = this.store.getFieldNameByAlias(this.flashVars.field);
			var tmpDescValue = this.flashVars.descValue;
			if (tmpDescValue!== undefined && tmpDescValue != ''){											
				value = rec.get(fName) + '|' + rec.get(this.store.getFieldNameByAlias(tmpDescValue));
			}else{
				value = rec.get(fName);
			}
		//	value = rec.get(fName);
		}
		
		this.swf.setValue(value);			
	}
});
Ext.reg('chart.sbi.semaphore', Sbi.chart.Semaphore);


