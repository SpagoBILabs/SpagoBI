/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.geo");

Sbi.geo.MainPanel = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	
	var defaultSettings = {
		mapName: 'Sbi.geo.mappanel.title'
		, controlPanelConf: {
			analysisPanelEnabled: true
			, measurePanelEnabled: false
			, earthPanelEnabled: true
		} 
		, toolbarConf: {
			enabled: true,
			zoomToMaxButtonEnabled: true,
			mouseButtonGroupEnabled: true,
			measureButtonGroupEnabled: true,
			wmsGroupEnabled: true,
			drawButtonGroupEnabled: true,
			historyButtonGroupEnabled: true
		},
		hideBorders: true
	};
		
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.georeportPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.georeportPanel);
	}
	
	
		
	var c = Ext.apply(defaultSettings, config || {});
	
	c.toolbarConf.enabled = false;
	
	Ext.apply(this, c);
	
	this.controlPanelConfOrignal = Ext.apply({}, this.controlPanelConf);
	this.toolbarConfOrignal = Ext.apply({}, this.toolbarConf);
	
	this.initServices();
	this.initStore();
	
	// enable disable debug panel
	this.controlPanelConf.debugPanelEnabled = true;
	this.controlPanelConf.debugPanelConf = {
			store: this.store
		};
	
	

	
	this.init();
	
	Ext.layout.BorderLayout.Region.prototype.getCollapsedEl = Ext.layout.BorderLayout.Region.prototype.getCollapsedEl.createSequence(function(){
		if(this.collapseMode == 'none'){
            this.collapsedEl.enableDisplayMode('none');
        }
    });
	
	c = Ext.apply(c, {
         layout   : 'border',
         hideBorders: true,
         items    : [this.controlPanel, this.mapPanel, this.controlPanel2]
	});

	// constructor
	Sbi.geo.MainPanel.superclass.constructor.call(this, c);
	
	this.on('render', function() {
		this.setCenter();
		if(this.controlPanelConf.earthPanelEnabled === true) {
			this.init3D.defer(500, this);
		}
		if(this.toolbarConf.enabled) {
			this.toolbar.initButtons.defer(500, this.toolbar);
		}
	}, this);	
};

/**
 * @class Sbi.geo.MainPanel
 * @extends Ext.Panel
 * 
 * ...
 */
Ext.extend(Sbi.geo.MainPanel, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
    services: null
    
    , baseLayersConf: null
    , layers: null
    
    , map: null
    , lon: null
    , lat: null
    , zoomLevel: null
    
    , showPositon: null
    , showOverview: null
    , mapName: null
    , mapPanel: null
    , controlPanel: null
    
    , analysisType: null
    , PROPORTIONAL_SYMBOLS:'proportionalSymbols'
    , CHOROPLETH:'choropleth'
    , GRAPHIC:'graphic'
    
    , targetLayer: null
    , thematizerControlPanel: null
    
    , controlPanel2: null
    
    , nWLayer: null
    
    , wmsLayerUrl: null
 
    , btnAddWms: null
 
    , winWmsForm: null
    
    , Form: null
    
    , wmsLayerGrid: null
    
    , model: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	/**
	 * @method 
	 * 
	 * Controls that the configuration object passed in to the class constructor contains all the compulsory properties. 
	 * If it is not the case an exception is thrown. Use it when there are properties necessary for the object
	 * construction for whom is not possible to find out a valid default value.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the config object received as input
	 */
	, validateConfigObject: function(config) {
		
	}

	/**
	 * @method 
	 * 
	 * Modify the configuration object passed in to the class constructor adding/removing properties. Use it for example to 
	 * rename a property or to filter out not necessary properties.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the modified version config object received as input
	 * 
	 */
	, adjustConfigObject: function(config) {
		// patch for an old typo
		if(config.feautreInfo) {
			config.featureInfo = config.feautreInfo;
			delete config.feautreInfo;
		}
	}
	
	, validate: function (successHandler, failureHandler, scope) {
		Sbi.trace("[MainPanel.validate]: IN");
		
		var thematizationControlPanel = this.controlPanel.thematizerControlPanel;
		
		var template = {};
		
		template.mapName = this.mapName;
		template.analysisType = this.analysisType;
	
		template.indicatorContainer = thematizationControlPanel.indicatorContainer;
		template.storeType = thematizationControlPanel.storeType;
		
		if(template.storeType === 'virtualStore') {
			template.storeConfig = thematizationControlPanel.storeConfig;
		} else { // it's a physicalStore
			template.feautreInfo = this.feautreInfo;
			template.indicators = this.indicators;
			template.businessId = this.businessId;	
		}
		
		
		template.geoId = this.geoId;
				
		template.analysisConf = this.controlPanel.getAnalysisConf();
				
		template.selectedBaseLayer = this.selectedBaseLayer;
		for(var i=0; i < this.map.getNumLayers(); i++) {
			var layer = this.map.getLayerIndex(i);
			if(layer.isBaseLayer && layer.selected) {
				template.selectedBaseLayer = layer.name;
			}
		}
		
		template.targetLayerConf = this.targetLayerConf;

		template.controlPanelConf = this.controlPanelConfOrignal;		
		template.toolbarConf = this.toolbarConfOrignal;	
		
//		var mapCenterPoint = this.map.getCenter();
//		template.lon = mapCenterPoint.lon;
//		template.lat = mapCenterPoint.lat;
//		template.zoomLevel = this.map.getZoom();
		
		template.lon = this.lon;
		template.lat = this.lat;
		template.zoomLevel = this.zoomLevel;
		
		var templeteStr = Ext.util.JSON.encode(template);
		Sbi.trace("[MainPanel.validate]: template = " + templeteStr);
		Sbi.trace("[MainPanel.validate]: IN");
		return templeteStr;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
    , setCenter: function(center) {
      	
		center = center || {};
      	this.lon = center.lon || this.lon;
      	this.lat = center.lat || this.lat;
      	this.zoomLevel = center.zoomLevel || this.zoomLevel;
        
        if(this.map.projection == 'EPSG:900913'){            
            this.centerPoint = Sbi.geo.utils.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat));
            this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else if(this.map.projection == 'EPSG:4326') {
        	this.centerPoint = new OpenLayers.LonLat(this.lon, this.lat);
        	this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else{
        	alert('Map Projection [' + this.map.projection + '] not supported yet');
        }
         
     }

	//-----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - MapOl: ...
	 *    - GetTargetDataset: ...
	 *    - GetTargetLayer: ...
	 *    
	 */
	, initServices: function() {
		this.services = this.services || new Array();	
		
		var params = {
			layer: this.targetLayerConf.name
			, businessId: this.businessId
			, geoId: this.geoId
		};
		
		if(this.targetLayerConf.url) {
			params.featureSourceType = 'wfs';
			params.featureSource = this.targetLayerConf.url;
		} else {
			params.featureSourceType = 'file';
			params.featureSource = this.targetLayerConf.data;
		}
		
		this.services['MapOl'] = this.services['MapOl'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MapOl'
			, baseParams: params
		});
		
		this.services['GetTargetDataset'] = this.services['GetTargetDataset'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GetTargetDataset'
			, baseParams: params
		});
		
		this.services['GetTargetLayer'] = this.services['GetTargetLayer'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GetTargetLayer'
			, baseParams: params
		});
	}
	
	/**
	 * @method 
	 * 
	 * initialize store
	 */
	, initStore: function() { 
		Sbi.trace("[MainPanel.initStore]: IN");
		if(this.indicatorContainer === "store") {
			if(this.storeType === "physicalStore") {
				Sbi.debug("[MainPanel.initStore]: Store will be loaded using service [GetTargetDataset] because " +
						"property [indicatorContainer] is equal to [" + this.indicatorContainer+ "] " +
						"and property [storeType] is equal to [physicalStore");
				
				this.store = new Ext.data.JsonStore({
					url: this.services['GetTargetDataset']
					, autoLoad: false
				})
			} else if(this.storeType === "virtualStore") {
				Sbi.debug("[MainPanel.initStore]: Store will be loaded using service [MeasureJoin] because " +
						"property [indicatorContainer] is equal to [" + this.indicatorContainer+ "] " +
						"and property [storeType] is equal to [physicalStore");
				
				this.store = null;
			} else {
				Sbi.warn("Impossible to load initialize store because the value [" + this.storeType + "] of property [storeType] is not valid");
				this.store = null;
			}
		} else {
			Sbi.debug("[MainPanel.initStore]: Store will be loaded using service [MapOl] because property [indicatorContainer] is equal to [" + this.indicatorContainer+ "]");
			this.store = new Ext.data.JsonStore({
				url: this.services['MapOl']
				, autoLoad: false
			})
		}
		Sbi.trace("[MainPanel.initStore]: OUT");
	}
	
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initMap();
		this.initMapPanel();
		this.initControlPanel();
	}
	
	
	
	, initMap: function() {  
		var o = this.baseMapOptions;
	
		if(o.projection !== undefined && typeof o.projection === 'string') {
			o.projection = new OpenLayers.Projection( o.projection );
		}
		
		if(o.displayProjection !== undefined && typeof o.displayProjection === 'string') {
			o.displayProjection = new OpenLayers.Projection( o.displayProjection );
		}
		
		if(o.maxExtent !== undefined && typeof o.maxExtent === 'object') {
			o.maxExtent = new OpenLayers.Bounds(
					o.maxExtent.left, 
					o.maxExtent.bottom,
					o.maxExtent.right,
					o.maxExtent.top
            );
		}
		
		
		this.map = new OpenLayers.Map('map', this.baseMapOptions);
		this.map.addControlToMap = function (control, px) {
			Sbi.debug("[Map.addControlToMap]: IN");
			
			
			if(control.div == null) {
				Sbi.debug("[Map.addControlToMap]: div is null");
			} else {
				Sbi.debug("[Map.addControlToMap]: div is not null");
			}
			
	        // If a control doesn't have a div at this point, it belongs in the viewport.
	        control.outsideViewport = (control.div != null);
	        
	        // If the map has a displayProjection, and the control doesn't, set 
	        // the display projection.
	        if (this.displayProjection && !control.displayProjection) {
	            control.displayProjection = this.displayProjection;
	        }    
	        
	        control.setMap(this);
	        var div = control.draw(px);
	        if (div) {
	            if(!control.outsideViewport) {
	                div.style.zIndex = this.Z_INDEX_BASE['Control'] +
	                                    this.controls.length;
	                this.viewPortDiv.appendChild( div );
	                Sbi.debug("[Map.addControlToMap]: control [" + control.CLASS_NAME + "] added to viewport");
	            }
	        }
	        Sbi.debug("[Map.addControlToMap]: OUT");
	    };
		
		
		this.initLayers();
		this.initControls();  
		this.initAnalysis();
		
		
    }

	, initLayers: function(c) {
		this.layers = new Array();
				
		if(this.baseLayersConf && this.baseLayersConf.length > 0) {
			for(var i = 0; i < this.baseLayersConf.length; i++) {
				if(this.baseLayersConf[i].enabled === true) {
					var l = Sbi.geo.utils.LayerFactory.createLayer( this.baseLayersConf[i] );
					if(l.name === this.selectedBaseLayer) {
						l.selected = true;
					} else {
						l.selected = false;
					}
					this.layers.push( l	);
				}
			}			
		}
		
		this.map.addLayers(this.layers);
	}
	
	, initControls: function() {
		if(this.baseControlsConf && this.baseControlsConf.length > 0) {
			for(var i = 0; i < this.baseControlsConf.length; i++) {
				if(this.baseControlsConf[i].enabled === true) {
					this.baseControlsConf[i].mapOptions = this.baseMapOptions;
					var c = Sbi.geo.utils.ControlFactory.createControl( this.baseControlsConf[i] );
					if(c != null) {
						Sbi.trace("[MainPanel.initControls] : adding control [" + Sbi.toSource(this.baseControlsConf[i]) + "] ...");
						if(c.div == null) {
							Sbi.trace("[MainPanel.initControls] : div is null");
						} else {
							Sbi.trace("[MainPanel.initControls] : div is not null");
						}
						if(c.CLASS_NAME == 'Sbi.geo.control.InlineToolbar') {
							c.mainPanel = this;
						}
						this.map.addControl( c );
						Sbi.trace("[MainPanel.initControls] : control [" + Sbi.toSource(this.baseControlsConf[i]) + "] succesfully added to the map");
					}
					
				}
			}			
		}
	}
	
	
	, init3D: function(center){
		center = center || {};
		this.lon = center.lon || this.lon;
		this.lat = center.lat || this.lat;
	    
	    if(this.map.projection == "EPSG:900913"){
	        
	    	var earth = new mapfish.Earth(this.map, 'map3dContainer', {
	    		lonLat: Sbi.geo.utils.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat)),
	            altitude: 50, //da configurare
	            heading: -60, //da configurare
	            tilt: 70,     //da configurare
	            range: 700}
	    	); //da configurare
	    	
	    } else if(this.map.projection == "EPSG:4326"){
	    	
	    	var earth = new mapfish.Earth(this.map, 'map3dContainer', {
	    		lonLat: new OpenLayers.LonLat(this.lon, this.lat),
	            altitude: 50,//da configurare
	            heading: -60,//da configurare
	            tilt: 70,//da configurare
	            range: 700
	       
	    	}); //da configurare	    
	    } else{
	    	alert('Map projection [' + this.map.projection + '] not supported yet!');
	    }
	  
	  }
	
	, isStoreVirtual: function() {
		return this.indicatorContainer === "store" && this.storeType === "virtualStore";
	}
	
	, initAnalysis: function() {
		
		if(this.indicatorContainer === "layer")  {
			for (var i = 0; i < this.indicators.length; i++){
				this.indicators[i][0] = this.indicators[i][0].toUpperCase();
			}
		}
		
		this.indicatorContainer = this.indicatorContainer  || 'layer';
		
		var loadLayerUrl = null
		if(this.indicatorContainer == 'layer') {
			loadLayerUrl = this.services['MapOl'];
		} else if(this.indicatorContainer == 'store') {
			loadLayerUrl = this.services['GetTargetLayer'];
		}
			
		var geostatConf = {
			map: this.map,
			layer: null, // this.targetLayer not yet defined here
			indicatorContainer: this.indicatorContainer || 'layer',
			storeType: this.storeType || 'physicalStore',
			storeConfig: this.storeConfig,
			indicators: this.indicators,			
			url: loadLayerUrl,
			loadMask : {msg: 'Analysis...', msgCls: 'x-mask-loading'},
			//legendDiv : 'myChoroplethLegendDiv',
			legendDiv : 'LegendBody',
			featureSelection: false,
			listeners: {}
		};
		
		if(this.map.projection == "EPSG:900913") {
			 geostatConf.format = new OpenLayers.Format.GeoJSON({
				 externalProjection: new OpenLayers.Projection("EPSG:4326"),
			     internalProjection: new OpenLayers.Projection("EPSG:900913")
			 });
		}
		
		
		if (this.analysisType === this.PROPORTIONAL_SYMBOLS) {
			this.initProportionalSymbolsAnalysis();
			geostatConf.layer = this.targetLayer;
			this.thematizerControlPanel = new Sbi.geo.stat.ProportionalSymbolControlPanel(geostatConf);
			this.thematizerControlPanel.analysisConf = this.analysisConf;
		} else if(this.analysisType === this.GRAPHIC) {
			this.initGraphicAnalysis();
			geostatConf.layer = this.targetLayer;
			this.map.PieDefinition = this.PieDefinition;			
			this.map.analysisType = this.analysisType;
			this.map.colors = this.color;
			this.map.chartType = this.chartType;
			this.map.totalField= this.totalField;
			this.map.fieldsToShow= this.fieldsToShow;
			this.thematizerControlPanel = new Sbi.geo.stat.ChoroplethControlPanel(geostatConf);
			this.thematizerControlPanel.analysisConf = this.analysisConf;
			this.thematizerControlPanel.analysisConf = this.analysisConf;
		} else if (this.analysisType === this.CHOROPLETH) {
			this.initChoroplethAnalysis();
			geostatConf.layer = this.targetLayer;
			geostatConf.businessId = this.businessId;
			geostatConf.geoId = this.geoId;
			geostatConf.store = this.store;
			this.thematizerControlPanel = new Sbi.geo.stat.ChoroplethControlPanel(geostatConf);
			this.thematizerControlPanel.analysisConf = this.analysisConf;
		} else {
			alert('error: unsupported analysis type [' + this.analysisType + ']');
		}
		
		this.initAnalysislayerSelectControl();
		this.map.addControl(this.analysisLayerSelectControl); 
		this.analysisLayerSelectControl.activate();
		
		
	}
	
	, initProportionalSymbolsAnalysis: function() {
	
		this.targetLayer = new OpenLayers.Layer.Vector(this.targetLayerConf.text, {
				'visibility': false  ,
				'styleMap': new OpenLayers.StyleMap({
	   				'select': new OpenLayers.Style(
	       				{'strokeColor': 'red', 'cursor': 'pointer'}
	   				)
				})
		});

		this.map.addLayer(this.targetLayer);		
	}
	
	, initChoroplethAnalysis: function() {
		this.targetLayer = new OpenLayers.Layer.Vector(this.targetLayerConf.text, {
        	'visibility': true,
          	'styleMap': new OpenLayers.StyleMap({
            	'default': new OpenLayers.Style(
                	OpenLayers.Util.applyDefaults(
                      {'fillOpacity': 0.5},
                      OpenLayers.Feature.Vector.style['default']
                  	)
              	),
              	'select': new OpenLayers.Style(
                  {'strokeColor': 'red', 'cursor': 'pointer'}
              	)
          	})
      	});
     
    
    	this.map.addLayer(this.targetLayer);                                    
	}
	
	, initGraphicAnalysis: function() {

		var context = Sbi.geo.utils.ContextFactory.createContext();

        var template = {
			fillColor: "${getColor}",
            fillOpacity: 0.2,
            graphicOpacity: 1,
            externalGraphic: "${getChartURLOriginal}",
            pointRadius: 20,
            graphicWidth:  "${getWidth}",
            graphicHeight: "${getHeight}"
        };
            
        var style = new OpenLayers.Style(template, {context: context});
        
		var styleMap = new OpenLayers.StyleMap({'default': style, 'select': {fillOpacity: 0.3}});
		    	
		    
		    /************************************ **********/
		this.targetLayer = new OpenLayers.Layer.Vector( this.targetLayerConf.text,
                                                { format: OpenLayers.Format.GeoJSON,
                                                  styleMap: styleMap,
                                                  isBaseLayer: false,
                                                  projection: new OpenLayers.Projection("EPSG:4326")} );

           // this.map.addLayers(vectors);
            this.setCenter(new OpenLayers.LonLat(20, 38), 4);


            //map.addControl(new OpenLayers.Control.LayerSwitcher());
            //map.addControl(new OpenLayers.Control.MouseDefaults());
            // map.addControl(new OpenLayers.Control.PanZoomBar());

        function serialize() {
            var Msg = "<strong>" + vectors.selectedFeatures[0].attributes["name"] + "</strong><br/>";
            Msg    += "Population: " + vectors.selectedFeatures[0].attributes["population"] + "<br/>";
            Msg    += "0-14 years: " + vectors.selectedFeatures[0].attributes["pop_0_14"] + "%<br/>";
            Msg    += "15-59 years: " + vectors.selectedFeatures[0].attributes["pop_15_59"] + "%<br/>";
            Msg    += "60 and over: " + vectors.selectedFeatures[0].attributes["pop_60_above"] + "%<br/>";
            document.getElementById("info").innerHTML = Msg;
        }

            var options = {
               hover: true
               //,onSelect: serialize
            };
            
            var select = new OpenLayers.Control.SelectFeature(this.targetLayer, options);
            this.map.addControl(select);
            select.activate();
			
     
    
    	this.map.addLayer(this.targetLayer);                                    
	}
	
	
	// --------------------------------------------------------------------------------------------------
	// SELECTION Control
	// --------------------------------------------------------------------------------------------------
	
	, initAnalysislayerSelectControl: function() {
		this.analysisLayerSelectControl = new OpenLayers.Control.SelectFeature(
        		this.targetLayer
        		, {
        			multiple: true
        			, toggle: true
        			, box: true
        		}
        );

		this.featureHandler = new OpenLayers.Handler.Feature(
				this, this.targetLayer, {click: this.onTargetFeatureClick}
	    );
		
		this.targetLayer.events.register("beforefeaturesadded", this, function(o) { 
			this.map.xfeatures = o.features;
		}); 
		
		this.targetLayer.events.register("featureselected", this, function(o) { 
			this.onTargetFeatureSelect(o.feature);
		}); 
        
        this.targetLayer.events.register("featureunselected", this, function(o) { 
			this.onTargetFeatureUnselect(o.feature);
		}); 
	}
	
	, onTargetFeatureClick: function(feature) {
		if(Ext.isEmpty(this.detailDocumentConf)) {
			this.detailDocumentConf = [];
		} 
		if(!Ext.isArray( this.detailDocumentConf )) {
			this.detailDocumentConf = [this.detailDocumentConf];
		}
		
		if(!this.toolbar.selectMode){
			this.openPopup(feature);
		}
	}
	
	
	, onTargetFeatureSelect: function(feature) {
		if(this.toolbar.selectMode){
			
		}	
	}
	
	, onTargetFeatureUnselect: function(feature) {
		if(this.toolbar.selectMode){
			
		}
	}
	
	// --------------------------------------------------------------------------------------------------

	, initMapPanel: function() {
		
		this.mapComponent = new Sbi.geo.MapComponent({map: this.map});
		var mapPanelConf = {
			title: LN(this.mapName),
			layout: 'fit',
			margins     : '0 0 0 0',
			cmargins    : '0 0 0 0',
			hideCollapseTool : true,
			hideBorders: true,
			border		: false,
			frame: false,
	       	items: [this.mapComponent]
	    };
		
		if(this.toolbarConf.enabled) {
			this.toolbarConf.map = this.map;
			this.toolbarConf.analysisLayerSelectControl = this.analysisLayerSelectControl;
			this.toolbarConf.featureHandler = this.featureHandler;
			alert("why?!");
			//this.toolbar = new Sbi.geo.Toolbar(this.toolbarConf);
			mapPanelConf.tbar = this.toolbar;
		}
	 
		if(this.detailDocumentConf) {
			
			this.mapPanel = new Ext.TabPanel({
			    region    : 'center',
			    margins   : '0 0 0 0', 
			    activeTab : 0,
			    defaults  : {
					autoScroll : true
				},
		       	items: [
		       	   new Ext.Panel(mapPanelConf), 
		       	   {
			            title    : 'Info',
			            html: '<div id="info"</div>',
			            id: 'infotable',
			            autoScroll: true
			        }
		       	]
			});
		} else {
			delete mapPanelConf.title;
			var m = new Ext.Panel(mapPanelConf);
			
			this.mapPanel = new Ext.Panel({
			    region    : 'center',
			    margins     : '0 0 0 0',
				cmargins    : '0 0 0 0',
				hideCollapseTool : true,
				hideBorders: true,
				border		: false,
				frame: false,
			    defaults  : {
					autoScroll : true
				},
		       	items: [m]
			});
			
			this.map.mapComponent = this.mapComponent;

		}
		
		
	}
	
	, initControlPanel: function() {		
		this.controlPanelConf.map = this.map;
		this.controlPanelConf.thematizerControlPanel = this.thematizerControlPanel;
		this.controlPanelConf.controlledPanel = this;
		this.controlPanel = new Sbi.geo.ControlPanel(this.controlPanelConf);
		this.controlPanel2 = new Sbi.geo.ControlPanel2(this.controlPanelConf);
	}
	

	
	, addSeparator: function(){
          this.toolbar.add(new Ext.Toolbar.Spacer());
          this.toolbar.add(new Ext.Toolbar.Separator());
          this.toolbar.add(new Ext.Toolbar.Spacer());
    } 

	, wmsAddLayer: function(){   
		  
		   var controlPanel = this.controlPanel;
		   var wmsData= Array();  
		   var reader= new Ext.data.JsonReader({}, [
		        {name: 'id'},
		        {name: 'layername'},
		        {name: 'srs'},
		   ]);
		      
		   var store= new Ext.data.Store({
		        reader: reader,
		        data: wmsData
		   }); 
		   
		   var sm= new Ext.grid.CheckboxSelectionModel({}); 
		   var map = this.map;
		   var winWmsForm = this.winWmsForm;
		   var model = this.model;
		   var addWmsLayer= function(nWLayer, urlWLayer){
		  
		   this.nWLayer = nWLayer;
			   var generaLayer = new OpenLayers.Layer.WMS(nWLayer, urlWLayer, {
			   		layers: nWLayer,                    
			   		srs: 'EPSG:4326',
			   		format: 'image/png',
			   		transparent: true
			   }, {
			   		singleTile: true, 
			   		ratio: 1,
			   		visibility: true, 
			   		'isBaseLayer': false, 
			   		opacity: 0.5
			   } 
			   );
			  
			   map.addLayer(generaLayer);
			   /*
			    var newLay = {
			                              layerName: nWLayer,
			                              text: nWLayer,
			                              leaf: true,
			                              checked: true
			                             }
			    
			    model.push(newLay);
			    //alert(model.length);
			   */
			   /*
			    addGr = -1;
			  gruppo = 'WMS';
			  alias = nWLayer;
			  for(ii=0;ii<model.length;ii++){
			  if (model[ii].text == gruppo){
			   addGr = ii;
			   }
			  }
			 
			  if  (addGr == -1){
			  //crea gruppo e aggiungi il layer
			   var l =  model.length;
			      //alert(h);
			   var gruppoLayer = {
			        text: gruppo,
			        leaf: false,
			        expanded: false,
			        children: [{
			         layerName:  alias,
			          text:  alias,
			          leaf: true,
			         checked: false
			         }]};
			   model[l] =  gruppoLayer;    
			   
			  }else{
			   var l = model[addGr].children.length; 
			   var gruppoLayer = {
			        layerName:  alias,
			                text:  alias,
			        leaf: true,
			        checked: false
			         };
			   model[addGr].children[l] =  gruppoLayer;
			  //aggiungi il layer al grupppo  
	
			  }
			   */
	
			  //Ext.getCmp('view').items.items[0].remove(Ext.getCmp('laytr'));
			   
			  /*
			  var layertree = {
			       title: 'Layer',
			       xtype: "layertree",
			       region: "center",
			       map: map,
			       border:false,
			       enableDD: true,
			              id:'laytr',
			       model: model
			       
			  };
				*/
		   
		    //Ext.getCmp('view').items.items[0].add(layertree);
		    //Ext.getCmp('view').items.items[0].doLayout();
		    
		  };

		  var wmsLayerUrl= new Ext.form.TextField({
			  fieldLabel:'WMS Url', 
			  name:'urlWms', 
			  width:540, 
			  value: 'http://localhost:8080/geoserver/wms'
		  });
		    
		    // simple array store
		    /*var storeCombo = Ext.data.SimpleStore({

		        fields: ['urlWms'],
		        data : dataSel
		    });
		    var wmsLayerUrl = new Ext.form.ComboBox({
		        store: storeCombo,
		        width:540,
		        displayField:'urlWms',
		        typeAhead: true,
		        mode: 'local',
		        forceSelection: true,
		        triggerAction: 'all',
		        emptyText:'Select a wms service...',
		        selectOnFocus:true,
		        applyTo: 'local-states'
		    });

		    var dataSel = [
		        ['http://localhost:8080/geoserver/wms']
		    ];
		    */
		    
		  var btnAddWms= new Ext.form.Hidden({name: 'btnAddWms', value: 'AddWms'});
		  this.wmsLayerGrid= new Ext.grid.GridPanel({
		        id:'button-grid',
		        store: store,
		        cm: new Ext.grid.ColumnModel([
		            sm,
		            //expander,
		            {id:'id',header: 'id', width: 10, sortable: true, dataIndex: 'id'},
		            {header: 'layername', width: 20, sortable: true, dataIndex: 'layername'},
		            {header: 'srs', width: 20, sortable: true, dataIndex: 'srs'}
		            //{header: 'imglegend', width: 20, sortable: true, dataIndex: 'imglegend'}
		        ]),
		        sm: sm,

		        viewConfig: {
		            forceFit:true
		        },
		        columnLines: true,
		        
		        tbar:[
		              wmsLayerUrl
		              , '-', 
		              {
		            	  tooltip:'Add a new wms layer',
		            	  iconCls:'addWms',
		            	  wmsLayerUrl: wmsLayerUrl,
		            	  btnAddWms: btnAddWms,
		            	  handler: function() {
		  		                Ext.Ajax.request({
		  		                	url :  'LayerWms', 
		  		                	params : {urlWms:this.wmsLayerUrl.getValue(), btnAddWms:this.btnAddWms.getValue()},
		  		                	method: 'POST',
		  		                	timeout: '300000', 
		  		                	waitMsg:'Loading',
		  		                	//scope: wmsAddLayer.wmsLayerGrid, 
		  		                	success: function (result,request) {
		  		                		if(result.status == 200){
		  		                			//alert(result.responseText);
		  		                			var stringData = result.responseText;
		  		                			var jsonData = Ext.util.JSON.decode(stringData);
		  		                			wmsData = jsonData;
		  		                			store.loadData(wmsData);
		  		                		}
		  		                	},
		  		                	failure: function (result,request) { 
		  		                		Ext.MessageBox.alert('Failed', 'Error '); 
		  		                	} 
		  		                });
		              	  }
		    
		              }
		        ],
		        width:600,
		        height:300,
		        //plugins: expander,
		        iconCls:'icon-grid',
		        scope: this
		  });
		 
		  this.Form = new Ext.FormPanel({
		    width:'auto',
		    height:'auto',
		  autoHeight: true,
		  autoWidth: true,
		  border: false,
		        
		  items: [btnAddWms,this.wmsLayerGrid], 
		      buttons: [{
		    	 text: 'Add to map' ,
		    	 formBind: true,
		    	 handler: function(){
		    	  	//alert("Add to map");
		    	  	for(i = 0; i < sm.selections.getCount(); i++){
		    	  		a = sm.selections.items[i].get('id');
		    	  		//a = sm.selections.items[0].get('id');
		    	  		//alert(a);
		    	  		name = wmsData[a-1].layername;
		    	  		//alert(name);
		    	  		//alert(wmsLayerUrl.getValue());
		    	  		addWmsLayer(name,wmsLayerUrl.getValue());
		    	  	}
		    	  	winWmsForm.destroy();
		    	  	winWmsForm.close();
		      	}
		      },{
		  
		    	text: 'Close',
		    	handler: function(){
		    	  //statusForm = 0;
		    	  winWmsForm.destroy();
		    	  winWmsForm.close();
		      	},
		      	scope: this
		  }]
		});

		winWmsForm = new Ext.Window({
			title: 'Aggiunge un layer WMS',
		    layout:'fit',
		    autoScroll: true,
		  
		    width:612,
		    
		    closable: false,
		    closeAction: "hide",
		    constrainHeader:true,
		  
		    plain: true,
		    border: false,
		    items: [this.Form],
		    scope: this
		 });
		 winWmsForm.show();
	}
	
	
	// ==========================================================================================
	//	Utility methods used to create contextual popup win
	// ==========================================================================================
	
	, openPopup: function(feature) {
		var content = '';
		content += this.getFeatureInfoHtmlFragment(feature);
		content += this.getDetailDocHtmlFragment(feature);
		content += this.getInlineDocHtmlFragment(feature);

		var onPopupCloseFn = function(evt) {
			this.closePopup(feature);
        }.createDelegate(this, []);
        
        if( Ext.isEmpty(feature.popup) === false ) {
        	this.closePopup(feature);
        }
        
        feature.popup = new OpenLayers.Popup.AnchoredBubble( //new OpenLayers.Popup.FramedCloud(
        		Ext.id(), 
                feature.geometry.getBounds().getCenterLonLat(),
                new OpenLayers.Size(200, 150),
                content,
                null, 
                true, 
                onPopupCloseFn
        );
        
        this.map.addPopup(feature.popup);
	}
	
	, closePopup: function(feature) {
		if(feature.popup){
			this.map.removePopup(feature.popup);
			feature.popup.destroy();
			feature.popup = null;
		}
        var infoPanel = Ext.getCmp('infotable');
        if(infoPanel.body){
        	infoPanel.body.dom.innerHTML = '';
        }
	}
	
	
	// -----------------------------
	// Feature info part
	// -----------------------------
	
	, getFeatureInfoHtmlFragment: function(feature) {
		var info = "<div style='font-size:.8em'>";
	    for(var i=0; i<this.featureInfo.length; i++){
	    	info = info+"<b>"+ this.featureInfo[i][0] +"</b>: " + feature.attributes[this.featureInfo[i][1]] + "<br />";    
	    } 
	    info += "</div>";
	    return info;
	}
	
	// -----------------------------
	// Detail part
	// -----------------------------
	
	, getDetailDocHtmlFragment: function(feature) {
		var content  = '';
		
		for(var i = 0, l = this.detailDocumentConf.length; i < l; i++) {
			var params = this.getDetailDocParams(this.detailDocumentConf[i], feature);
			      
	        var execDetailFn =  this.getDetailDocExecFn(this.detailDocumentConf[i], params);
	       
	        this.detailDocumentConf[i].text = this.detailDocumentConf[i].text || 'Details';
	        
	        var link = this.getDetailDocExecLink(this.detailDocumentConf[i], execDetailFn);
	        
	        content += link;
		}
		
		return content;
	}
	
	, getDetailDocParams: function(detailDocumentConf, feature) {
		var params;
		
		params = Ext.apply({}, detailDocumentConf.staticParams);
		for(p in detailDocumentConf.dynamicParams) {
			var attrName = detailDocumentConf.dynamicParams[p].toUpperCase();			
			params[p] = feature.attributes[attrName];
		}
		
		return params;
	}
	
	, getDetailDocExecFn: function(detailDocumentConf, detailDocParams) {
		var execDetailFn = "execDoc(";
        execDetailFn += '"' + detailDocumentConf.label + '",'; // documentLabel
        execDetailFn += '"' + this.role + '",'; // execution role
        execDetailFn += Ext.util.JSON.encode( detailDocParams ) + ','; // parameters
        execDetailFn += detailDocumentConf.displayToolbar + ','; // displayToolbar
        execDetailFn += detailDocumentConf.displaySliders + ','; // displaySliders
        execDetailFn += '"' + detailDocumentConf.label + '"'; // frameId
        execDetailFn += ")";
        
        return execDetailFn;
	} 
	
	, getDetailDocExecLink: function(detailDocumentConf, detailDocFn) {
		var link = '';
        
		link += '<center>';
        link += '<font size="1" face="Verdana">';
        link += '<a href="#" onclick=\'Ext.getCmp("' + this.mapPanel.getId() + '").setActiveTab("infotable");';
        link += 'Ext.getCmp("infotable").body.dom.innerHTML=';
        link += detailDocFn + '\';>';
        link += detailDocumentConf.text + '</a></font></center>';
        
        return link;
	}
	
	// -----------------------------
	// Inline doc part
	// -----------------------------
	
	, getInlineDocHtmlFragment: function(feature) {
		var content = '';
		
		if(Ext.isEmpty(this.inlineDocumentConf)) return content;
		
		var params = Ext.apply({}, this.inlineDocumentConf.staticParams);
		for(p in this.inlineDocumentConf.dynamicParams) {
			var attrName = this.inlineDocumentConf.dynamicParams[p].toUpperCase();
			params[p] = feature.attributes[attrName];
		}
		
        content += execDoc(
        		this.inlineDocumentConf.label, 
        		this.role, 
        		params, 
        		this.inlineDocumentConf.displayToolbar, 
        		this.inlineDocumentConf.displaySliders, 
        		this.inlineDocumentConf.label,
        		'300'
        );
        
        return content;
	}
});