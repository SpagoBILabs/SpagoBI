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
  * - Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.MainPanel = function(config) {
	
	var defaultSettings = {
		mapName: 'sbi.georeport.mappanel.title'
		, controlPanelConf: {
			layerPanelEnabled: true
			, analysisPanelEnabled: true
			, measurePanelEnabled: true
			, legendPanelEnabled: true
			, logoPanelEnabled: true
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
		}
	};
		
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.georeportPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.georeportPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
		
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
	
	
	
	
	//this.addEvents('customEvents');
		
		
	this.initMap();
	this.initMapPanel();
	this.initControlPanel();
	
	c = Ext.apply(c, {
         layout   : 'border',
         items    : [this.controlPanel, this.mapPanel]
	});

	// constructor
	Sbi.georeport.MainPanel.superclass.constructor.call(this, c);
	
	this.on('render', function() {
		this.setCenter();
		if(this.controlPanelConf.earthPanelEnabled === true) {
			this.init3D.defer(500, this);
		}
		if(this.toolbarConf.enabled) {
			this.initToolbarContent.defer(500, this);	
		}
	}, this);
	
	
	
};

Ext.extend(Sbi.georeport.MainPanel, Ext.Panel, {
    
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
    
    , targetLayer: null
    , geostatistic: null
    
    // --- modifica fabio
    
    , controlPanel2: null
    
    , nWLayer: null
    
    , wmsLayerUrl: null
 
    , btnAddWms: null
 
    , winWmsForm: null
    
    , Form: null
    
    , wmsLayerGrid: null
    
    , model: null
    
    // --- modifica fabio
    
    
    // -- public methods ------------------------------------------------------------------------
    
    
    , setCenter: function(center) {
      	
		center = center || {};
      	this.lon = center.lon || this.lon;
      	this.lat = center.lat || this.lat;
      	this.zoomLevel = center.zoomLevel || this.zoomLevel;
        
        if(this.map.projection == "EPSG:900913"){            
            this.centerPoint = Sbi.georeport.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat));
            this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else if(this.map.projection == "EPSG:4326") {
        	this.centerPoint = new OpenLayers.LonLat(this.lon, this.lat);
        	this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else{
        	alert("Map Projection not supported yet!");
        }
         
     }

	 // -- private methods ------------------------------------------------------------------------
	
	
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
		this.initLayers();
		this.initControls();  
		this.initAnalysis();
    }

	, initLayers: function(c) {
		this.layers = new Array();
				
		if(this.baseLayersConf && this.baseLayersConf.length > 0) {
			for(var i = 0; i < this.baseLayersConf.length; i++) {
				if(this.baseLayersConf[i].enabled === true) {
					var l = Sbi.georeport.LayerFactory.createLayer( this.baseLayersConf[i] );
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
					var c = Sbi.georeport.ControlFactory.createControl( this.baseControlsConf[i] );
					this.map.addControl( c );
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
	    		lonLat: Sbi.georeport.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat)),
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
	
	
	
	, initAnalysis: function() {
		
		var geostatConf = {
			map: this.map,
			layer: null, // this.targetLayer not yet defined here
			indicators:  this.indicators,
			url: this.services['MapOl'],
			loadMask : {msg: 'Analysis...', msgCls: 'x-mask-loading'},
			legendDiv : 'myChoroplethLegendDiv',
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
			this.geostatistic = new mapfish.widgets.geostat.ProportionalSymbol(geostatConf);
			
		} else if (this.analysisType === this.CHOROPLETH) {
			this.initChoroplethAnalysis();
			geostatConf.layer = this.targetLayer;
			this.geostatistic = new mapfish.widgets.geostat.Choropleth(geostatConf);
		} else {
			alert('error: unsupported analysis type [' + this.analysisType + ']');
		}
		
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
        this.analysisLayerSelectControl = new OpenLayers.Control.SelectFeature(this.targetLayer, {} );

        this.targetLayer.events.register("featureselected", this, function(o) { 
			//alert('select -> ' + this.getInfo(o.feature));
			this.onTargetFeatureSelect(o.feature);
		}); 
        
        this.targetLayer.events.register("featureunselected", this, function(o) { 
			//alert('unselect -> ' + this.getInfo(o.feature));
			this.onTargetFeatureUnselect(o.feature);
		}); 
		
	}
	
	, initChoroplethAnalysis: function() {
		this.targetLayer = new OpenLayers.Layer.Vector(this.targetLayerConf.text, {
        	'visibility': false,
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
    	this.analysisLayerSelectControl = new OpenLayers.Control.SelectFeature(this.targetLayer, {});
        
        this.targetLayer.events.register("featureselected", this, function(o) { 
			//alert('select -> ' + this.getInfo(o.feature));
			this.onTargetFeatureSelect(o.feature);
		}); 
        
        this.targetLayer.events.register("featureunselected", this, function(o) { 
			//alert('unselect -> ' + this.getInfo(o.feature));
			this.onTargetFeatureUnselect(o.feature);
		}); 
	}
	
	, onTargetFeatureSelect: function(feature) {
		this.selectedFeature = feature;
		
		if(!Ext.isArray( this.detailDocumentConf )) {
			this.detailDocumentConf = [this.detailDocumentConf];
		}
		
		var content = "<div style='font-size:.8em'>" + this.getInfo(feature);
		
		var params;
        
		for(var i = 0, l = this.detailDocumentConf.length; i < l; i++) {

			params = Ext.apply({}, this.detailDocumentConf[i].staticParams);
			for(p in this.detailDocumentConf[i].dynamicParams) {
				var attrName = this.detailDocumentConf[i].dynamicParams[p];
				params[p] = feature.attributes[attrName];
			}
			
			//alert(params.toSource());
			
	        var execDetailFn = "execDoc(";
	        execDetailFn += '"' + this.detailDocumentConf[i].label + '",'; // documentLabel
	        execDetailFn += '"' + this.role + '",'; // execution role
	        execDetailFn += Ext.util.JSON.encode(params) + ','; // parameters
	        execDetailFn += this.detailDocumentConf[i].displayToolbar + ','; // displayToolbar
	        execDetailFn += this.detailDocumentConf[i].displaySliders + ','; // displaySliders
	        execDetailFn += '"' + this.detailDocumentConf[i].label + '"'; // frameId
	        execDetailFn += ")";
	       
	        this.detailDocumentConf[i].text = this.detailDocumentConf[i].text || 'Details';
	        
	        var link = '';
	        link += '<center>';
	        link += '<font size="1" face="Verdana">';
	        link += '<a href="#" onclick=\'Ext.getCmp("' + this.mapPanel.getId() + '").setActiveTab("infotable");';
	        link += 'Ext.getCmp("infotable").body.dom.innerHTML=';
	        link += execDetailFn + '\';>';
	        link += this.detailDocumentConf[i].text + '</a></font></center>';
        
	        content += link;
		}

        params = Ext.apply({}, this.inlineDocumentConf.staticParams);
		for(p in this.inlineDocumentConf.dynamicParams) {
			var attrName = this.inlineDocumentConf.dynamicParams[p];
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
       
        popup = new OpenLayers.Popup.FramedCloud("chicken", 
                feature.geometry.getBounds().getCenterLonLat(),
                null,
                content,
                null, 
                true, 
                function(evt) {
        			this.analysisLayerSelectControl.unselect(this.selectedFeature);    
                }.createDelegate(this, [])
        );
        
        feature.popup = popup;
        this.map.addPopup(popup);
	}
	
	, onTargetFeatureUnselect: function(feature) {
		this.map.removePopup(feature.popup);
        feature.popup.destroy();
        feature.popup = null;
        var infoPanel = Ext.getCmp('infotable');
        if(infoPanel.body){
        	infoPanel.body.dom.innerHTML = '';
        }
	}
	
	, getInfo: function(feature) {
		//alert(feature.attributes.toSource());
		var info = "";
	    for(var i=0; i<this.feautreInfo.length; i++){
	    	info = info+"<b>"+ this.feautreInfo[i][0] +"</b>: " + feature.attributes[this.feautreInfo[i][1]] + "<br />";    
	    } 
	    return info;
	}
	
	
	
	
	

	
	
	, initMapPanel: function() {
		
		var mapPanelConf = {
			title: LN(this.mapName),
			layout: 'fit',
	       	items: {
		        xtype: 'mapcomponent',
		        map: this.map
		    }
	    };
		
		if(this.toolbarConf.enabled) {
			this.loadingButton = new Ext.Toolbar.Button({
	            tooltip: 'Please wait',
	            iconCls: "x-tbar-loading"
	        });
			
			this.toolbar = new mapfish.widgets.toolbar.Toolbar({
		    	map: this.map, 
		        configurable: false,
		        items: [this.loadingButton, ' Loading toolbar...']
			});
			
			this.loadingButton.disable();
			
			mapPanelConf.tbar = this.toolbar;
		}
	 
	 
		this.mapPanel = new Ext.TabPanel({
		    region    : 'center',
		    margins   : '3 3 3 0', 
		    activeTab : 0,
		    defaults  : {
				autoScroll : true
			},

	       	items: [
		       	new Ext.Panel(mapPanelConf), {
		            title    : 'Info',
		            html: '<div id="info"</div>',
		            id: 'infotable',
		            autoScroll: true
		        }
		    ]
		});
	}
	
	, initControlPanel: function() {		
		this.controlPanelConf.map = this.map;
		this.controlPanelConf.geostatistic = this.geostatistic;
		this.controlPanel = new Sbi.georeport.ControlPanel(this.controlPanelConf);
	}
	

	
	, addSeparator: function(){
          this.toolbar.add(new Ext.Toolbar.Spacer());
          this.toolbar.add(new Ext.Toolbar.Separator());
          this.toolbar.add(new Ext.Toolbar.Spacer());
    } 

	, initToolbarContent: function() {
			
		this.toolbar.items.each( function(item) {
			this.toolbar.items.remove(item);
            item.destroy();           
        }, this); 
		
		var vectorLayer = new OpenLayers.Layer.Vector("vector", { 
	    	displayInLayerSwitcher: false
	    });
	    this.map.addLayer(vectorLayer);
	    
	    if(this.toolbarConf.zoomToMaxButtonEnabled === true) {
		    this.toolbar.addControl(
		        new OpenLayers.Control.ZoomToMaxExtent({
		        	map: this.map,
		            title: 'Zoom to maximum map extent'
		        }), {
		            iconCls: 'zoomfull', 
		            toggleGroup: 'map'
		        }
		    );
			
		    this.addSeparator();
	    }
	    
	    if(this.toolbarConf.mouseButtonGroupEnabled === true) {
		    this.toolbar.addControl(
		    	new OpenLayers.Control.ZoomBox({
		    		title: 'Zoom in: click in the map or use the left mouse button and drag to create a rectangle'
	            }), {
		    		iconCls: 'zoomin', 
	                toggleGroup: 'map'
		    	}
	        );
	      
			this.toolbar.addControl(
				new OpenLayers.Control.ZoomBox({
					out: true,
	                title: 'Zoom out: click in the map or use the left mouse button and drag to create a rectangle'
	            }), {
					iconCls: 'zoomout', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DragPan({
					isDefault: true,
	                title: 'Pan map: keep the left mouse button pressed and drag the map'
	            }), {
	                iconCls: 'pan', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.addSeparator();
	    }
	          
	    if(this.toolbarConf.drawButtonGroupEnabled === true) {
	    	this.toolbar.addControl(
	    		new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Point, {
	    			title: 'Draw a point on the map'
	            }), {
	    			iconCls: 'drawpoint', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Path, {
					title: 'Draw a linestring on the map'
				}), {
	                iconCls: 'drawline', 
	                toggleGroup: 'map'
				}
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Polygon, {
					title: 'Draw a polygon on the map'
	            }), {
	                iconCls: 'drawpolygon', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.addSeparator();
	    }
	      
	    if(this.toolbarConf.historyButtonGroupEnabled === true) {
	    	var nav = new OpenLayers.Control.NavigationHistory();
	        this.map.addControl(nav);
	        nav.activate();
	          
	        this.toolbar.add(
	        	new Ext.Toolbar.Button({ 
	        		iconCls: 'back',
	                tooltip: 'Previous view', 
	                handler: nav.previous.trigger
	            })
	         );
	          
	         this.toolbar.add(
	        	new Ext.Toolbar.Button({
	        		iconCls: 'next',
	                tooltip: 'Next view', 
	                handler: nav.next.trigger
	            })
	         );
	          
	         this.addSeparator();
	    }
	    
	    
	    // -- Modifica Fabio 
	    if(this.toolbarConf.wmsGroupEnabled === true) {
		   this.toolbar.addControl(
				   new OpenLayers.Control.Button({
					   isDefault: false,
					   title: 'Add WMS Layer'
				   })
				   , {
					   iconCls: 'wmsAdd', 
					   toggleGroup: 'map',
					   handler: this.wmsAddLayer,
					   scope: this
				   }
		   );   
		   
		   this.toolbar.addControl(
				   new OpenLayers.Control.Button({
					   isDefault: false,
					   title: 'Add WMS Layer (REFACTORED)'
				   })
				   , {
					   iconCls: 'wmsAdd', 
					   toggleGroup: 'map',
					   handler: this.wmsAddLayerRefactored,
					   scope: this
				   }
		   );   
	    }
	    
	    this.addSeparator(toolbar);

	    if(this.toolbarConf.measureButtonGroupEnabled === true) {
	    	// SPECIFICHE GRAFICISMI PER MISURAZIONI
	    	var style = new OpenLayers.Style();

	    	var styleMap = new OpenLayers.StyleMap({"default": style});
	    	function handleMeasurements(event) {
	            var geometry = event.geometry;
	            var units = event.units;
	            var order = event.order;
	            var measure = event.measure;
	            var element = Ext.getCmp('mapOutput'); 
	            var out = "";
	            if(order == 1) {
	                out += "Distanza: " + measure.toFixed(3) + " " + units;
	                if (this.map.getProjection() == "EPSG:4326") {
	                    out += ", Great Circle Distance: " + 
	                        calcVincenty(geometry).toFixed(3) + " km"; 
	                }        
	            } else {
	                out += "<span class='mapAreaOutput'>Area: " + measure.toFixed(3) + " " + units + "<sup style='font-size:6px'>2</" + "sup></span>";
	            }
	            element.body.dom.innerHTML = out;
	        };
	        
	        function calcVincenty(geometry) {
	        	/*Note: this function assumes geographic coordinates and  will fail otherwise.  OpenLayers.Util.distVincenty takes two objects representing points with geographic coordinates
		  		and returns the geodesic distance between them (shortest  distance between the two points on an ellipsoid) in *kilometers*.It is important to realize that the segments drawn on the map
		  		are *not* geodesics (or "great circle" segments).  This means that in general, the measure returned by this function  will not represent the length of segments drawn on the map. */
		        var dist = 0;
	            for (var i = 1; i < geometry.components.length; i++) {
	                var first = geometry.components[i-1];
	                var second = geometry.components[i];
	                dist += OpenLayers.Util.distVincenty(
	                    {lon: first.x, lat: first.y},
	                    {lon: second.x, lat: second.y}
	                );
	            }
	            return dist;
	        };  
	        
	        var optionsLine = {
	        	handlerOptions: {
	        		style: "default", // this forces default render intent
	        		layerOptions: {styleMap: styleMap},
	        		persist: true
	        	},
	        	displayClass: "olControlMeasureDistance"          
	        };

	        var optionsPolygon = {
	        	handlerOptions: {
	        		style: "default", // this forces default render intent
	        		layerOptions: {styleMap: styleMap},
	        		persist: true
	        	},
	        	displayClass: "olControlMeasureArea"     
	        };

	        measureControls = {
	        	line: new OpenLayers.Control.Measure(
	        		OpenLayers.Handler.Path, 
	        		optionsLine
	        	),
	        	polygon: new OpenLayers.Control.Measure(
	        		OpenLayers.Handler.Polygon, 
	        		optionsPolygon
	        	)
	        };
	                
	        for(var key in measureControls) {
	        	control = measureControls[key];
	        	control.events.on({
	        		"measure": handleMeasurements,
	        		"measurepartial": handleMeasurements
	        	});
	        }  
	   
	        this.toolbar.addControl(            
	        	measureControls.line,{
	        	tooltip: 'Misura Distanze',
	        	iconCls: 'meaLinee', 
	        	toggleGroup: 'map'    
	        }
	        );
	  
	        this.toolbar.addControl(            
	        	measureControls.polygon,{                
	        	tooltip: 'Misura Area',
	        	iconCls: 'meaArea', 
	        	toggleGroup: 'map'}
	        );
	   
	        this.addSeparator(toolbar);  
	    }

	    // -- Modifica Fabio 
	    
	    
	    /*
	    var printConfigUrl = mapfish.SERVER_BASE_URL + 'pdf/info.json';
	    this.toolbar.add(
	    	new mapfish.widgets.print.PrintAction({
	            map: this.map,
	            overrides: this.layers,
	            configUrl: printConfigUrl
	            
	    	})
		);
		*/
	          
	    this.toolbar.activate();
	    
      }
	
	  , wmsAddLayerRefactored: function() {
		  var winWmsForm = new Ext.Window({
				title: 'Aggiunge un layer WMS (REFACTORED)',
			    layout:'fit',
			    autoScroll: true,
			  
			    width:612,
			    
			    closable: false,
			    closeAction: "hide",
			    constrainHeader:true,
			  
			    plain: true,
			    border: false,
			    items: [new Sbi.georeport.ImportWMSLayerForm()],
			    scope: this
		});
		winWmsForm.show();
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
	
});