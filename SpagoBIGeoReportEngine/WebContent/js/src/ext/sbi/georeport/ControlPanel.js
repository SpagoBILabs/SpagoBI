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

Ext.ns("Sbi.georeport");

Sbi.georeport.ControlPanel = function(config) {
	
	var defaultSettings = {
		title       : LN('sbi.georeport.controlpanel.title'),
		region      : 'west',
		split       : true,
		width       : 300,
		collapsible : true,
		margins     : '3 0 3 3',
		cmargins    : '3 3 3 3',
		autoScroll	 : true
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

Ext.extend(Sbi.georeport.ControlPanel, Ext.Panel, {
    
	controlPanelItemsConfig: null
	
	, earthPanelEnabled: false
	, layerPanelEnabled: false
	, analysisPanelEnabled: false
	, measurePanelEnabled: false
	, logoPanelEnabled: false
	, legendPanelEnabled: false
	, debugPanelEnabled: false
   
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
			
			this.layersControlPanel = new mapfish.widgets.LayerTree({
	        	title: LN('sbi.georeport.layerpanel.title'),
	            collapsible: true,
	            autoHeight: true,
	            rootVisible: false,
	            separator: '!',
	            model: this.extractModel(),
	            map: this.map,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF'
	        });
			
			this.map.layerTree = this.layersControlPanel;
			
			this.controlPanelItemsConfig.push(this.layersControlPanel);
		}
	}
	
	, initAnalysisControlPanel: function() {
		if(this.analysisPanelEnabled === true) {
			
			this.analysisControlPanel = new Ext.Panel({
	        	title: LN('sbi.georeport.analysispanel.title'),
	            collapsible: true,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
	            items: [this.geostatistic]
	        });
			
			this.controlPanelItemsConfig.push(this.analysisControlPanel);
		}
	}
	
	, initMeasureControlPanel: function() {
		
		
		if(this.measurePanelEnabled === true) {
			
			this.measureControlPanel = new Ext.Panel({
				 id: 'mapOutput',
	             title: 'Misurazione',
	             collapsible: true,
	             height: 50,
	             html: '<center></center>'
			});
				
			this.controlPanelItemsConfig.push(this.measureControlPanel);
		}
	}
	
	, initLegendControlPanel: function() {
		if(this.legendPanelEnabled === true) {
			
			this.legendControlPanel = new Ext.Panel({
		           title: LN('sbi.georeport.legendpanel.title'),
		           collapsible: true,
		           bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
		           height: 150,
		           autoScroll: true,
		           html: '<center id="myChoroplethLegendDiv"></center>'
		     });
					
			this.controlPanelItemsConfig.push(this.legendControlPanel);
		}
	}
	
	, initLogoControlPanel: function() {
		if(this.logoPanelEnabled === true) {
			
			this.logoControlPanel = new Ext.Panel({
		           title: 'Logo',
		           collapsible: true,
		           height: 85,
		           html: '<center><img src="/SpagoBIGeoReportEngine/img/georeport.jpg" alt="GeoReport"/></center>'
		    });
				
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
                 checked: layer.getVisibility(),
                 cls: className,
                 layerName: layer.name
             };
	         
	         if(layer.isBaseLayer) {
	        	 bLayers.push(layerNode);
	         } else {
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