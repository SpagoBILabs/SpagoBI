/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.ns("Sbi.geo");

Sbi.geo.ControlPanel = function(config) {
	
	var defaultSettings = {
		title       : LN('Sbi.geo.controlpanel.title'),
		region      : 'east',
		split       : true,
		width       : 315,
		collapsible : true,
		collapsed   : true,
		margins     : '3 0 3 3',
		cmargins    : '3 3 3 3',
		autoScroll	 : true,
		earthPanelConf: {},
		layerPanelConf: {},
		analysisPanelConf: {},
		measurePanelConf: {},
		logoPanelConf: {},
		legendPanelConf: {},
		debugPanelConf: {}
		
	};
	
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.controlPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.controlPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.initControls();
	
	c = Ext.apply(c, {
	     items: this.controlPanelItemsConfig 
	});
		
	// constructor
	Sbi.geo.ControlPanel.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.geo.ControlPanel
 * @extends Ext.Panel
 * 
 * ...
 */
Ext.extend(Sbi.geo.ControlPanel, Ext.Panel, {
    
	controlPanelItemsConfig: null
	
	, earthPanelEnabled: false
	, layerPanelEnabled: false
	, analysisPanelEnabled: false
	, measurePanelEnabled: false
	, logoPanelEnabled: false
	, legendPanelEnabled: false
	, debugPanelEnabled: false
	
	, earthPanelConf: null
	, layerPanelConf: null
	, analysisPanelConf: null
	, measurePanelConf: null
	, logoPanelConf: null
	, legendPanelConf: null
	, debugPanelConf: null
   
	, layersControlPanel: null
	, analysisControlPanel: null
	, measureControlPanel: null
	, legendControlPanel: null
	, logoControlPanel: null
	, debugControlPanel: null
   
    // public methods
    
    // private methods
	
	, initControls: function() {
		
		this.controlPanelItemsConfig = [];
	
		this.initEarthControlPanel();
		this.initLayersControlPanel();
		this.initMeasureControlPanel();
		this.initAnalysisControlPanel();
		this.initLegendControlPanel();
		this.initLogoControlPanel();
		this.initDebugControlPanel();

	}
	
	, initEarthControlPanel: function() {
		if(this.earthPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
				title: LN('Sbi.geo.earthpanel.title'),
				collapsible: false,
				split: true,
				height: 300,
				minSize: 300,
				maxSize: 500,
				html: '<center id="map3dContainer"></center>'
			});
		}
	}

	, initLayersControlPanel: function() {
		
		if(this.layerPanelEnabled === true) {			
			
			this.layersControlPanel = new mapfish.widgets.LayerTree(Ext.apply({
	        	title: LN('Sbi.geo.layerpanel.title'),
	            collapsible: true,
	            collapsed: false,
	            autoHeight: true,
	            rootVisible: false,
	            separator: '!',
	            model: this.extractModel(),
	            map: this.map,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF'
	        }, this.layerPanelConf));
			
			this.map.layerTree = this.layersControlPanel;
			
			this.controlPanelItemsConfig.push(this.layersControlPanel);
		}
	}
	
	, initAnalysisControlPanel: function() {
		if(this.analysisPanelEnabled === true) {
			
			this.analysisControlPanel = new Ext.Panel(Ext.apply({
	        	title: LN('Sbi.geo.analysispanel.title'),
	            collapsible: true,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
	            items: [this.geostatistic]
	        }, this.analysisPanelConf));
			
			this.geostatistic.on('ready', function(){
				
				this.setAnalysisConf( this.geostatistic.analysisConf );
				
				/*
				var analysisConf = this.geostatistic.analysisConf || {};
				
				var method = analysisConf.method || 'CLASSIFY_BY_QUANTILS';
				this.geostatistic.form.findField('method').setValue(method);
				//this.geostatistic.form.findField('method').setValue('CLASSIFY_BY_EQUAL_INTERVALS');
				//this.geostatistic.form.findField('method').setValue('CLASSIFY_BY_QUANTILS');
				
				var classes =  analysisConf.classes || 5;
				this.geostatistic.form.findField('numClasses').setValue(classes);
				
				var fromColor =  analysisConf.fromColor || '#FFFF99';
				this.geostatistic.form.findField('colorA').setValue(fromColor);
				var toColor =  analysisConf.toColor || '#FF6600';
				this.geostatistic.form.findField('colorB').setValue(toColor);
				
				if(analysisConf.indicator) analysisConf.indicator = analysisConf.indicator.toUpperCase();
				var indicator = analysisConf.indicator || this.geostatistic.indicators[0][0];
				this.geostatistic.form.findField('indicator').setValue(indicator);
				
				this.geostatistic.classify();
				*/
			}, this);
			
			
			this.controlPanelItemsConfig.push(this.analysisControlPanel);
		}
	}
	
	, setAnalysisConf: function(analysisConf) {
		
		analysisConf = analysisConf || {};
		
		var method = analysisConf.method || 'CLASSIFY_BY_QUANTILS';
		this.geostatistic.form.findField('method').setValue(method);
		//this.geostatistic.form.findField('method').setValue('CLASSIFY_BY_EQUAL_INTERVALS');
		//this.geostatistic.form.findField('method').setValue('CLASSIFY_BY_QUANTILS');
		
		var classes =  analysisConf.classes || 5;
		this.geostatistic.form.findField('numClasses').setValue(classes);
		
		var fromColor =  analysisConf.fromColor || '#FFFF99';
		this.geostatistic.form.findField('colorA').setValue(fromColor);
		var toColor =  analysisConf.toColor || '#FF6600';
		this.geostatistic.form.findField('colorB').setValue(toColor);
		
		if(analysisConf.indicator) analysisConf.indicator = analysisConf.indicator.toUpperCase();
		var indicator = analysisConf.indicator || this.geostatistic.indicators[0][0];
		this.geostatistic.form.findField('indicator').setValue(indicator);
		
		this.geostatistic.classify();
	}
	
	, getAnalysisConf: function() {
		var analysisConf = {};
		
		analysisConf.method = this.geostatistic.form.findField('method').getValue();
		analysisConf.classes = this.geostatistic.form.findField('numClasses').getValue();
		analysisConf.fromColor = this.geostatistic.form.findField('colorA').getValue();
		analysisConf.toColor = this.geostatistic.form.findField('colorB').getValue();
		analysisConf.indicator = this.geostatistic.form.findField('indicator').getValue();
		
		return analysisConf;
	}
	
	, initMeasureControlPanel: function() {
		
		
		if(this.measurePanelEnabled === true) {
			
			this.measureControlPanel = new Ext.Panel(Ext.apply({
				 id: 'mapOutput',
	             title: 'Misurazione',
	             collapsible: true,
	             collapsed: false,
	             height: 50,
	             html: '<center></center>'
			 }, this.measurePanelConf));
				
			this.controlPanelItemsConfig.push(this.measureControlPanel);
		}
	}
	
	, initLegendControlPanel: function() {
		if(this.legendPanelEnabled === true) {
			
			this.legendControlPanel = new Ext.Panel(Ext.apply({
		           title: LN('Sbi.geo.legendpanel.title'),
		           collapsible: true,
		           bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
		           height: 180,
		           autoScroll: true,
		           html: '<center id="myChoroplethLegendDiv"></center>'
		     },this.legendPanelConf));
					
			this.controlPanelItemsConfig.push(this.legendControlPanel);
		}
	}
	
	, initLogoControlPanel: function() {
		if(this.logoPanelEnabled === true) {
			
			this.logoControlPanel = new Ext.Panel(Ext.apply({
		           title: 'Logo',
		           collapsible: true,
		           height: 85,
		           html: '<center><img src="/SpagoBIGeoReportEngine/img/georeport.jpg" alt="GeoReport"/></center>'
			 },this.logoPanelConf));
				
			this.controlPanelItemsConfig.push(this.logoControlPanel);
		}
	}
	
	, initDebugControlPanel: function() {
		if(this.debugPanelEnabled === true) {
			
			this.store = this.debugPanelConf.store;
			this.initWin();
			
			this.debugControlPanel = new Ext.Panel({
		           title: 'Debug',
		           collapsible: true,
		           height: 85,
		           items: [new Ext.Button({
				    	text: 'Debug',
				        width: 30,
				        handler: function() {
				        	this.win.show();
				        	var p = Ext.apply({}, this.params, {
				    			//start: this.start
				    			//, limit: this.limit
				    		});
				    		this.store.load({params: p});
		           		},
		           		scope: this
				    })]
		    });
			
			
			this.controlPanelItemsConfig.push(this.debugControlPanel);
		}
	}
	
    // private methods
    , initWin: function() {
		
    	this.store.on('loadexception', function(store, options, response, e) {
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		});
    	
    	this.store.on('metachange', function( store, meta ) {
    		this.updateMeta( meta );
    	}, this);
    	
    	if(!this.cm){
			this.cm = new Ext.grid.ColumnModel([
			   new Ext.grid.RowNumberer(),
		       {
		       	  header: "Data",
		          dataIndex: 'data',
		          width: 75
		       }
		    ]);
    	}
    	
    	var pagingBar = null;
    	pagingBar = new Ext.PagingToolbar({
    	        pageSize: this.limit,
    	        store: this.store,
    	        displayInfo: true,
    	        displayMsg: '', //'Displaying topics {0} - {1} of {2}',
    	        emptyMsg: "No topics to display",
    	        
    	        items:[
    	               '->'
    	               , {
    	            	   text: LN('sbi.lookup.Annulla')
    	            	   , listeners: {
    		           			'click': {
    		                  		fn: this.onCancel,
    		                  		scope: this
    		                	} 
    	               		}
    	               } , {
    	            	   text: LN('sbi.lookup.Confirm')
    	            	   , listeners: {
    		           			'click': {
    		                  		fn: this.onOk,
    		                  		scope: this
    		                	} 
    	               		}
    	               }
    	        ]
    	});
  
		
    	this.grid = new Ext.grid.GridPanel({
			store: this.store
   	     	, cm: this.cm
   	     	, frame: false
   	     	, border:false  
   	     	, collapsible:false
   	     	, loadMask: true
   	     	, viewConfig: {
   	        	forceFit:false
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	}	
	        //, bbar: pagingBar
		});
		
		this.win = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 580,
            height      : 300,
            closeAction :'hide',
            plain       : true,
            items       : [this.grid]
		});
	}
    
    , updateMeta: function(meta) {
    	if(this.grid){		
    		meta.fields[0] = new Ext.grid.RowNumberer();
			this.grid.getColumnModel().setConfig(meta.fields);
		} else {
		   alert('ERROR: store meta changed before grid instatiation')
		}
	}
	
	
	
	
	
	
	, extractModel: function() {
		
		var model = null;
		
		var bLayers = new Array();
		var oLayers = new Array();
	
		var mapLayers = this.map.layers.slice();
		
		for (var i = 0; i < mapLayers.length; i++) {
			 var layer = mapLayers[i];
			 
			 var className = '';
	         if (!layer.displayInLayerSwitcher) {
	        	 className = 'x-hidden';
	         }
	         
	         var layerNode = {
	        	 text: layer.name, // TODO: i18n
                 checked: layer.selected, //layer.getVisibility(),
                 cls: className,
                 layerName: layer.name
             };
	         
	         if(layer.isBaseLayer) {
	        	 layerNode.checked = layer.selected;
	        	 bLayers.push(layerNode);
	         } else {
	        	 layerNode.checked = layer.getVisibility();
	        	 oLayers.push(layerNode);
	         }
		}
		
		model = [
		{
			text: 'Background layers',
		    expanded: true,
		    children: bLayers
		}, {
		    text: 'Overlays',
		    expanded: true,
		    children: oLayers
		}
		];
		
		return model;
	}
    
   
});