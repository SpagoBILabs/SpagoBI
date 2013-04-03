/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
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

Ext.ns("Sbi.georeport");

Sbi.georeport.ControlPanel = function(config) {
	
	var defaultSettings = {
		title       : LN('sbi.georeport.controlpanel.title'),
		region      : 'west',
		split       : true,
		width       : 315,
		collapsible : true,
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
	Sbi.georeport.ControlPanel.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.georeport.ControlPanel
 * @extends Ext.Panel
 * 
 * ...
 */
Ext.extend(Sbi.georeport.ControlPanel, Ext.Panel, {
    
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
				title: LN('sbi.georeport.earthpanel.title'),
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
	        	title: LN('sbi.georeport.layerpanel.title'),
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
	        	title: LN('sbi.georeport.analysispanel.title'),
	            collapsible: true,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
	            items: [this.geostatistic]
	        }, this.analysisPanelConf));
			
			this.geostatistic.on('ready', function(){
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
			}, this);
			
			
			this.controlPanelItemsConfig.push(this.analysisControlPanel);
		}
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
		           title: LN('sbi.georeport.legendpanel.title'),
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
			
			this.debugControlPanel = new Ext.Panel({
		           title: 'Debug',
		           collapsible: true,
		           height: 85,
		           items: [new Ext.Button({
				    	text: 'Debug',
				        width: 30,
				        handler: function() {
		        	   		this.init3D();
		        	   		/*
		        	   		var size = this.controlPanel.getSize();
		        	   		size.width += 1;
		        	   		this.controlPanel.refreshSize(size);
		        	   		*/
		           		},
		           		scope: this
				    })]
		    });
			
			
			this.controlPanelItemsConfig.push(this.debugControlPanel);
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