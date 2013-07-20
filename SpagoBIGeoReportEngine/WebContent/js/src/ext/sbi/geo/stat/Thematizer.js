/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/

Ext.ns("Sbi.geo.stat");



/**
 * @requires OpenLayers/Layer/Vector.js
 * @requires OpenLayers/Popup/AnchoredBubble.js
 * @requires OpenLayers/Feature/Vector.js
 * @requires OpenLayers/Format/GeoJSON.js
 * @requires OpenLayers/Control/SelectFeature.js
 * @requires OpenLayers/Ajax.js
 */

/**
 * Class: Sbi.geo.stat.Thematizer
 * Base class for geo-statistics. This class is not meant to be used directly, it serves
 * as the base for specific geo-statistics implementations.
 */
Sbi.geo.stat.Thematizer = function(map, config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);

	this.initialize(map, config);
//	
//	// init properties...
//	var defaultSettings = {
//		// set default values here
//	};
//	
//	if (Sbi.settings && Sbi.settings.xxx && Sbi.settings.xxx.xxxx) {
//		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.xxx.xxxx);
//	}
//	
//	var c = Ext.apply(defaultSettings, config || {});	
//	Ext.apply(this, c);
//	
//	// init events...
//	this.addEvents();
//	
//	this.initServices();
//	this.init();
	
	// constructor
	Sbi.geo.stat.Thematizer.superclass.constructor.call(this, config);
};

/**
 * @class Sbi.geo.stat.Thematizer
 * @extends Ext.util.Observable
 * 
 * Base class for geo-statistics. This class is not meant to be used directly, it serves
 * as the base for specific geo-statistics implementations.
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.geo.stat.Thematizer, Ext.util.Observable, {
	
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
	 * @property {String} indicator
	 * Defines the name of indicator used to create the thematization
	 */
    indicator: null
    
    
    /**
     * @property {String} indicatorContainer
     * Defines the object that contain the values of the specified indicator. It can be equal to:
     *  - 'store' if the values are taken from a store. In this case the value are taken directly from the
     * dataset's column whose name is equal to the #indicator;
     *  - 'layer' if the values are taken from the features contained in the target layer. In this case the 
     *  values are taken directly from the feature's property whose name is equal to the #indicator;
     *  
     * By default it is equal to 'layer'
     */
    , indicatorContainer: 'layer' //'layer' - 'store'
    	
	/**
     * @property {OpenLayers.Layer.Vector} layer
     * The vector layer containing the features that are styled based on statistical values. 
     * If none is provided, one will be created.
     */
    , layer: null
    
    /**
     * @property {Ext.data.Store} store
     * The store containing the values of the indicator used to thematize the map. Only apply if 
     * #indicatorContainer is equal to 'store'
     */
    , store: null
    

    /**
     * @property {OpenLayers.Format} format
     * The OpenLayers format used to get features from
     * the HTTP request response. GeoJSON is used if none is provided.
     */
    , format: null

    /**
     * @property {String} url
     * The URL to the web service. If none is provided, the features
     * found in the provided vector layer will be used.
     */
    , url: null

    /**
     * @property {Function} requestSuccess
     * Function called upon success with the HTTP request.
     */
    , requestSuccess: function(request) {}

   
	/**
	 * @property {Function} requestFailure
	 * Function called upon failure with the HTTP request.
	 */
    , requestFailure: function(request) {}

    /**
	 * @property {Boolean} featureSelection
	 * A boolean value specifying whether feature selection must
     * be put in place. If true a popup will be displayed when the
     * mouse goes over a feature.
	 */
    , featureSelection: true
    
    /**
	 * @property {String} nameAttribute
	 * The feature attribute that will be used as the popup title.
     * Only applies if featureSelection is true.
	 */
    , nameAttribute: null

    
 
    /**
	 * @property {Object} defaultSymbolizer
	 * This symbolizer is used in the constructor to define
     * the default style in the style object associated with the
     * "default" render intent. This symbolizer is extended with
     * OpenLayers.Feature.Vector.style['default']. It can be
     * overridden in subclasses.
	 */
    , defaultSymbolizer: {}
    
    /**
	 * @property {Object} selectSymbolizer
	 * This symbolizer is used in the constructor to define
     * the select style in the style object associated with the
     * "select" render intent. When rendering selected features
     * it is extended with the default symbolizer. It can be
     * overridden in subclasses.
	 */
    , selectSymbolizer: {'strokeColor': '#000000'} // neutral stroke color

    /**
     * Property: legendDiv
     * {Object} Reference to the DOM container for the legend to be
     *     generated.
     */
    
    /**
	 * @property {String} legendDiv
	 * Reference to the DOM container for the legend to be generated.
	 */
    , legendDiv: null

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
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Called by the constructor to initialize this object
	 * 
	 * @param {OpenLayers.Map} OpenLayers map object
	 * @param {Object} Hashtable of extra options
	 */

     , initialize: function(map, options) {
        this.map = map;
        this.setOptions(options);
        if (!this.layer) {
        	Sbi.debug("[Thematizer.initialize]: target layer not specified. A new one will be created");
        	this.initLayer();
        } else {
        	Sbi.debug("[Thematizer.initialize]: target layer already defined");
        }
        
        if (this.featureSelection) {
        	Sbi.debug("[Thematizer.initialize]: feature selection enabled. A feature selection control will be created");
        	this.initFeatureSelectionControl();
        } else {
        	Sbi.debug("[Thematizer.initialize]: feature selection disabled");
        }
        
        // get features from web service if a url is specified
        if (this.url) {
        	Sbi.debug("[Thematizer.initialize]: Url attribute has been valorized to [" + Sbi.toSource(url) + "]. Features will be loded from it");
            OpenLayers.loadURL(
                this.url, '', this, this.onSuccess, this.onFailure);
        }
        this.legendDiv = Ext.get(options.legendDiv);
    }

     /**
      * @method 
      * create layer that will contains thematized features
      */
     , initLayer: function() {
    	 var styleMap = new OpenLayers.StyleMap({
             'default': new OpenLayers.Style(
                 OpenLayers.Util.applyDefaults(
                     this.defaultSymbolizer,
                     OpenLayers.Feature.Vector.style['default']
                 )
             ),
             'select': new OpenLayers.Style(this.selectSymbolizer)
         });
         var layer = new OpenLayers.Layer.Vector('geostat', {
             'displayInLayerSwitcher': false,
             'visibility': false,
             'styleMap': styleMap
         });
         map.addLayer(layer);
         this.layer = layer;
     }

     /**
      * @method 
      * create select feature control so that popups can
      * be displayed on feature selection
      */
     , initFeatureSelectionControl: function() {
    	 this.layer.events.on({
             'featureselected': this.showDetails,
             'featureunselected': this.hideDetails,
             scope: this
         });
         var selectFeature = new OpenLayers.Control.SelectFeature(
             this.layer,
             {'hover': true}
         );
         map.addControl(selectFeature);
         selectFeature.activate();
     }
     
     
    /**
     * @method 
     *
     * @param {Object} request
     */
    , onSuccess: function(request) {
        var doc = request.responseXML;
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }
        var format = this.format || new OpenLayers.Format.GeoJSON()
        this.layer.addFeatures(format.read(doc));
        this.requestSuccess(request);
    }

    /**
     * @method 
     *
     * @param {Object} request
     */
    , onFailure: function(request) {
        this.requestFailure(request);
    }

	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
    
    /**
     * @method
     * To be overriden by subclasses.
     *
     * @param {Object} options
     */
    , thematize: function(options) {
    	Sbi.trace("[Thematizer.thematize] : IN");
        this.layer.renderer.clear();
        this.layer.redraw();
        this.updateLegend();
        this.layer.setVisibility(true);
        Sbi.trace("[Thematizer.thematize] : OUT");
    }
    
	// -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
	
    
    /**
     * @method 
     * 
     * @param {Object} newOptions an object containing the value of one
     * or more property that influence the generated thematization. This
     * method does not replace the old options just update them. To substitute
     * the old object with the one passed in use instead the #resetOption method. Finally
     * this method just update the value of the options without forcing also the update
     * of thematization. In order to update options and then also update the thematization 
     * if needed use #updateOptions method .
     * 
     */
    , setOptions: function(newOptions) {
        if (newOptions) {
            if (!this.options) {
                this.options = {};
            }
            // update our copy for clone
            Ext.apply(this.options, newOptions);
            // add new options to this
            Ext.apply(this, newOptions);
        }  
    }
    
    /**
     * @method 
     *
     * @return {Object} the option an object containing the value of all the properties that influence
     * the generated thematization. The returned object is the same set with the last call of the method
     * #setOptions. The content of the object change depending on the actual implementation of the thematizer 
     */
    , getOptions: function() {
    	return this.options || {};
    }
    
    , convertToNumber: function(value) {
    	if(typeof value == "Number") return value;
    	return parseFloat(value);
    }
    
    
    /**
     * @method 
     *  
     * @param {String} indicator the name of indicator whose values we want to extract
     */
    , getValues: function(indicator) {
    	
    	Sbi.trace("[Thematizer.getValues] : IN");
    	
    	Sbi.trace("[Thematizer.getValues] : Extract values for indicator [" + indicator + "] from [" + this.indicatorContainer + "]");
    	
   	 	var values;
   	 	if(this.indicatorContainer === 'layer') {
   	 		values = this.getValuesFromLayer(indicator);
   	 	} else if(this.indicatorContainer === 'store') {
   	 		values = this.getValuesFromStore(indicator);
   	 	} else {
   	 		Sbi.error("[Thematizer.getValues] : Impossible to extract indicators from a container of type [" + indicatorContainer + "]");
   	 	}
   	 	
   	 	Sbi.trace("[Thematizer.getValues] : Extracted [" + values.length + "] values for indicator [" + indicator + "]");
        
   	 	Sbi.trace("[Thematizer.getValues] : OUT");
   	 
        return values;
    }
    
    , getValuesFromLayer: function(indicator) {
    	var values = [];
    	
    	var features = this.layer.features;
   	 	Sbi.trace("[Thematizer.getValues] : Features number is equal to [" + features.length + "]");
        for (var i = 0; i < features.length; i++) {
        	var value = features[i].attributes[indicator];
        	var numericValue = this.convertToNumber(value);
        	if(isNaN(numericValue) == true) {
				Sbi.trace("[Thematizer.getValues] : Value [" + value + "] will be discarded because it is not a number");
			} else {
				values.push(numericValue);
			}
        }
        
        return values;
    }
    
    , getValuesFromStore: function(indicator) {
    	var values = [];
    	var records = this.store.getRange();
	 	Sbi.trace("[Thematizer.getValues] : Records number is equal to [" + records.length + "]");
   	 	
	 	var indicatorFiledName;
	 	for(var n = 0; n < records[0].fields.getCount(); n++) {
	 		var field = records[0].fields.itemAt(n);
	 		if(field.header == indicator) {
	 			indicatorFiledName = field.name;
	 			break;
	 		}
	 		//Sbi.trace("[Thematizer.getValues] : Field [" + Sbi.toSource(records[i].fields.itemAt(n)) + "]");
	 	}
	 	
	 	for (var i = 0; i < records.length; i++) {	
   	 		var value = records[i].get(indicatorFiledName);
   	 		var numericValue = this.convertToNumber(value);
        	if(isNaN(numericValue) == true) {
				Sbi.trace("[Thematizer.getValues] : Value [" + value + "] will be discarded because it is not a number");
			} else {
				values.push(numericValue);
			}
        }
    	return values;
    }
    
    

    /**
     * @method 
     * Extend layer style for the default render intent and
     * for the select render intent if featureSelection is
     * set.
     *
     * @param {Array({<OpenLayers.Rule>})} rules Array of new rules to add
     * @param {Object} symbolizer Object with new styling options
     * @param {Object} context Object representing the new context
     */
    , extendStyle: function(rules, symbolizer, context) {
        var style = this.layer.styleMap.styles['default'];
        // replace rules entirely - the geostat object takes control
        // on the style rules of the "default" render intent
        if (rules) {
            style.rules = rules;
        }
        if (symbolizer) {
            style.setDefaultStyle(
                OpenLayers.Util.applyDefaults(
                    symbolizer,
                    style.defaultStyle
                )
            );
        }
        if (context) {
            if (!style.context) {
                style.context = {};
            }
            OpenLayers.Util.extend(style.context, context);
        }
    }

    /**
     * @method
     *
     * @param {Object} obj
     */
    , showDetails: function(obj) {
        var feature = obj.feature;
        // popup html
        var html = typeof this.nameAttribute == 'string' ?
            '<h4 style="margin-top:5px">'
                + feature.attributes[this.nameAttribute] +'</h4>' : '';
        html += this.indicator + ": " + feature.attributes[this.indicator];
        // create popup located in the bottom right of the map
        var bounds = this.layer.map.getExtent();
        var lonlat = new OpenLayers.LonLat(bounds.right, bounds.bottom);
        var size = new OpenLayers.Size(200, 100);
        var popup = new OpenLayers.Popup.AnchoredBubble(
            feature.attributes[this.nameAttribute],
            lonlat, size, html, 0.5, false);
        var symbolizer = feature.layer.styleMap.createSymbolizer(feature, 'default');
        popup.setBackgroundColor(symbolizer.fillColor);
        this.layer.map.addPopup(popup);
    }

    /**
     * @method
     *
     * @param {Object} obj
     */
    , hideDetails: function(obj) {
        //remove all other popups from screen
        var map= this.layer.map;
        for (var i = map.popups.length - 1; i >= 0; --i) {
            map.removePopup(map.popups[i]);
        }
    }
});




