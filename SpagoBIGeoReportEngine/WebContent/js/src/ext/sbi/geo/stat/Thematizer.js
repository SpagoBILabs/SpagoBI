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
	
	this.addEvents('indicatorsChanged');
	this.addEvents('filtersChanged');

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
    , indicatorContainer: 'store' //'layer' - 'store'
    	
	/**
     * @property {OpenLayers.Layer.Vector} layer
     * The vector layer containing the features that are styled based on statistical values. 
     * If none is provided, one will be created.
     */
    , layer: null
    
	/**
	 * @property {String} layerId
	 * Defines the name of the property of the feature that contains its the unique identifier
	 */
    , layerId: null //'NAME_3'
    
    /**
     * @property {Ext.data.Store} store
     * The store containing the values of the indicator used to thematize the map. Only apply if 
     * #indicatorContainer is equal to 'store'
     */
    , store: null
    
    /**
	 * @property {String} storeId
	 * Defines the name of the field of the record that contains its the unique identifier. Only apply if 
     * #indicatorContainer is equal to 'store'
	 */
    , storeId: null //'COMUNE_ITA'
    
    /**
     * @property {Boolean} storeReload
     * Define if the store must be reloaded at the end of thematizer initialization or not. 
     * Defalut to true. Only apply if #indicatorContainer is equal to 'store'
     */
    , storeReload: true
    
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
		
		Sbi.trace("[Thematizer.validateConfigObject] : IN");
		
		if(!config) {
			throw "Impossible to build thematizer. Config object is undefined";
		}
		
		if(!config.layerId) {
			throw "Impossible to build thematizer. Config property [layerId] is undefined";
		}
		
		if(config.indicatorContainer == 'store') {
			
			if(!config.storeType) {
				throw "Impossible to build thematizer. Configuration property [indicatorContainer] is equal to [store] but property [storeType] is undefined ";
			}
			
			if(config.storeType === "physicalStore") {
				if(!config.store) {
					throw "Impossible to build thematizer. Configuration property [indicatorContainer] is equal to [store] " +
							"and configuration property [storeType] is equal to [physicalStore] but property [store] is undefined ";
				}
				
				if(!config.storeId) {
					throw "Impossible to build thematizer. Configuration property [indicatorContainer] is equal to [store] " +
					"and configuration property [storeType] is equal to [physicalStore] but property [storeId] is undefined ";
				}
			} else if(config.storeType === "virtualStore"){
				if(!config.storeConfig) {
					config.storeConfig = {};
					Sbi.warn("Configuration property [indicatorContainer] is equal to [store] " +
					"and configuration property [storeType] is equal to [visrtualStore] but property [storeConfig] is undefined. " +
					"The thematizer will be initialized but the thematization cannot be performed until the virtual store's config wont be set properly");
				}
			} else {
				throw "Value [" + config.storeType + "] is not valid for property [storeType]";
			}
			
		}
		
		Sbi.trace("[Thematizer.validateConfigObject] : Config object passed to constructor has been succesfully validated");
		
		Sbi.trace("[Thematizer.validateConfigObject] : OUT");
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
//		if(indicatorContainer === "layer" && config.storeId) {
//			config.storeId = config.storeId.toUpperCase();
//		}
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // abstract methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
     * @method
     * Creates the classification that will be used for map thematization. 
     * It must be properly implemented by subclasses
     */
    , setClassification: function() {
    	// implement this in subclasses
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
    
    /**
     * TODO: move this method in a proper utilities file
     */
    , convertToNumber: function(value) {
    	if(typeof value == "Number") return value;
    	return parseFloat(value);
    }
    
	// -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
	
    
    /**
     * @method
     * Method used to update the properties. It call #setOptions the check if some properties value is changed. If so
     * regenerate the classification on which the thematization is built on calling #setClassification method. It should
     * be overwritten by subclasses if not all the properties contained in options object can trigger a classification recalculation
     * but only a subset of them.
     * 
     * @param {Object} options object
     */
    , updateOptions: function(newOptions) {
        var oldOptions = Ext.apply({}, this.options);
        this.setOptions(newOptions);
        if (newOptions) {
        	var isSomethingChanged = false;
            for(o in newOptions) {
            	if(newOptions[o] !== oldOptions[o]) {
            		isSomethingChanged = true;
            		break;
            	}
            }
            if(isSomethingChanged) {
            	this.setClassification();
            }            
        }
    } 
    
    /**
     * @method 
     * 
     * @param {Object} newOptions an object containing the value of one
     * or more property that influence the generated thematization. This
     * method does not replace the old options just update them. To substitute
     * the old object with the one passed in use instead the #resetOption method. Finally
     * this method just update the value of the options without forcing also the update
     * of classification on which the thematization is built on. 
     * In order to update options and then also update the classification use #updateOptions method.
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
    
    
    
    /**
     * @method 
     *
     * @param {Ext.data.Store} store the store that contains data used to generate the new thematization
     * @param {Object} meta the store's metadata as returned from measure catalogue service
     */
    , setData: function(store, meta) {
    	Sbi.trace("[Thematizer.setData] : IN");
    	
    	// field metadata contained in metadata object are generated by method buildJoinedFieldMetdata of class InMemoryMaterializer
    	// and enriched by method join of class MeasureCatalogueCRUD
    	Sbi.trace("[Thematizer.setData] : store's metadata is equal to: " + Sbi.toSource(meta));
    	
    	if(!store) {
    		Sbi.warn("[Thematizer.setData] : Store input parameter is not defined");
    		return null;
    	}
    	
    	this.indicatorContainer = 'store';
    	this.store = store;
    	Sbi.debug("[Thematizer.setData] : The new store contains [" + store.getCount() + "] records");
    	
    	if(!meta) {
    		Sbi.warn("[Thematizer.setData] : Metadata input parameter is not specified so all other thematization property will be keep unchanged");
    		return null;
    	}
    	
    	
    	if(meta.geoId) {
    		this.storeId = meta.geoId;
    		Sbi.debug("[Thematizer.setData] : The new store id is equal to [" + this.storeId + "]");
    	} else {
    		Sbi.debug("[Thematizer.setData] : Property [storeId] is not specified in store's metadata so it will be used as [storeId] the old one that is equal to [" + this.storeId + "]");
    	}
    	
    	if(meta.geoIdHierarchyLevel) {
    		Sbi.warn("[Thematizer.setData] : For the moment we are not able to dinamicaly load the [layerId] associated to hierarchy level [" + meta.geoIdHierarchyLevel + "] so it will remain equal to [" + this.layerId + "]");
    	} else {
    		Sbi.debug("[Thematizer.setData] : Property [geoIdHierarchyLevel] is not specified in store's metadata it will be used as [layerId] the old one that is equal to [" + this.layerId + "]");
    	}
    	
    	var indicators = [];
    	var selectedIndictaor = null;
    	for(var i = 0; i < meta.fields.length; i++) {
    		var field = meta.fields[i];
    		Sbi.debug("[Thematizer.setData] : Scanning field [" + Sbi.toSource(field) + "]");
    		Sbi.debug("[Thematizer.setData] : Property role is equal to [" + field.role + "]");
    		if(field.role == 'MEASURE')  {
    			Sbi.debug("[Thematizer.setData] : new indicator found. It is equal to [" + field.header + "]");
    			selectedIndictaor = field.header;
    			indicators.push([field.header, field.header]);
    		}
    	}
    	if(selectedIndictaor != null) {
    		Sbi.debug("[Thematizer.setData] : The indicator will be set equal to [" + selectedIndictaor + "]");
    		this.indicator = selectedIndictaor;
    	} else {
    		Sbi.debug("[Thematizer.setData] : Indicator not specified in store's metadata so the ld one will be used [" + this.indicator + "]");
    	}
    	
    	var filters = this.getAttributeFilters(store, meta);
    	
    	this.thematize({resetClassification: true});
    	Sbi.trace("[Thematizer.setData] : OUT");
    	
    	this.fireEvent('indicatorsChanged', this, indicators, this.indicator);
    	this.fireEvent('filtersChanged', this, filters);
    }
    
    /**
     * Apply the filters to the store
     */
    , filterStore: function(filters){
    	Sbi.debug("[Thematizer.filterStore] : IN");
    	if(filters){
    		Sbi.debug("[Thematizer.filterStore] :  filtering the store for"+filters);
    		
    		var filterFunction = function(record, id){
    			for(var i=0; i<filters.length; i++){
	        		var field = filters[i].field;
	        		var value = filters[i].value;
	        		if(field && value!=null && value!=undefined && value!=""){
	        			if(record.data[field]!=value){
	        				return false;
	        			}
	        		}else{
	        			Sbi.debug("[Thematizer.filterStore] : there are filters with no field defined");
	        		}
	    		}
    			
    			return true;
    		};
    		
    		this.store.filterBy(filterFunction, this);
    	}
    	Sbi.debug("[Thematizer.filterStore] : OUT");
    }
    
    , getAttributeFilters: function(store, meta){
    	Sbi.debug("[Thematizer.getAttributeFilters] :IN");
    	
    	var filtersNames = new Array();//array with the name of the filters: all the attributes except the geoId
    	var filtersValueMap = new Array();//array with the values of the filters
    	var filters = new Array();//array with filters: contains definitions and values
    	//the order of the 3 arrays is the same. The values of the filter with name filtersNames[j] stay in filtersValueMap[j]
    	
    	Sbi.debug("[Thematizer.getAttributeFilters] : Building the array of filters positions");
    	for(var i = 0; i < meta.fields.length; i++) {
    		var field = meta.fields[i];
    		Sbi.debug("[Thematizer.getAttributeFilters] : Scanning field [" + Sbi.toSource(field) + "]");
    		Sbi.debug("[Thematizer.getAttributeFilters] : Property role is equal to [" + field.role + "]");
    		if(field.role == 'ATTRIBUTE' && !((meta.geoId) && (meta.geoId==field.header)) )  {
    			Sbi.debug("[Thematizer.getAttributeFilters] : new filter found. It is equal to [" + field.header + "]");
    			filtersNames.push(field.name);
    			filtersValueMap.push(new Array());
    			filters.push(field);
    		}
    	}
    	
    	Sbi.debug("[Thematizer.getAttributeFilters] : Building the map of the filters values");
    	for(var i = 0; i < store.data.length; i++) {
    		var row = store.data.items[i].data;
        	for(var j = 0; j < filtersNames.length; j++) {
        		var value = row[filtersNames[j]];
        		var filterValues = filtersValueMap[j];
        		if(filterValues.indexOf(value)<0){
        			filterValues.push(value);
        		}
        	}
    	}
    	Sbi.debug("[Thematizer.getAttributeFilters] : Built the map of the filters values");
    	
    	Sbi.debug("[Thematizer.getAttributeFilters] : Building the filters");
    	for(var j = 0; j < filtersNames.length; j++) {

    		filters[j].values = new Array();
    		
        	for(var i = 0; i < filtersValueMap[j].length; i++) {
        		var value = filtersValueMap[j][i];
        		filters[j].values.push([value]);
        	}
    		
//    		filters[j].values = filtersValueMap[j];
    	}
    	Sbi.debug("[Thematizer.getAttributeFilters] : Built the filters");
    	
    	return filters;
    }
   
    
    /**
     * @method 
     *  
     * @param {String} indicator the name of indicator whose values we want to extract
     * 
     * @return {Sbi.geo.stat.Distribution} the extracted distribution
     */
    , getDistribution: function(indicator) {
    	
    	Sbi.trace("[Thematizer.getDistribution] : IN");
    	
    	Sbi.trace("[Thematizer.getDistribution] : Extract values for indicator [" + indicator + "] from [" + this.indicatorContainer + "]");
    	
   	 	var values;
   	 	if(this.indicatorContainer === 'layer') {
   	 		if(!this.layerId)  {
	 			throw "Impossible to get distribution from layer. Configuration property [layerId] not defined";
	 		}
   	 		values = this.getDistributionFromLayer(indicator, this.layerId);
   	 	} else if(this.indicatorContainer === 'store') {
   	 		if(!this.storeId)  {
   	 			throw "Impossible to get distribution from store. Configuration property [storeId] not defined";
   	 		}
   	 		values = this.getDistributionFromStore(indicator, this.storeId);
   	 	} else {
   	 		Sbi.error("[Thematizer.getDistribution] : Impossible to extract indicators from a container of type [" + indicatorContainer + "]");
   	 	}
   	 	
   	 	Sbi.trace("[Thematizer.getDistribution] : Extracted [" + values.length + "] values for indicator [" + indicator + "]");
        
   	 	Sbi.trace("[Thematizer.getDistribution] : OUT");
   	 
        return values;
    }
    
    , getDistributionFromLayer: function(indicator, id) {
    	var distribution = new Sbi.geo.stat.Distribution();;
    	
    	var features = this.layer.features;
   	 	Sbi.trace("[Thematizer.getDistributionFromLayer] : Features number is equal to [" + features.length + "]");
        for (var i = 0; i < features.length; i++) {
        	var idValue = features[i].attributes[id];
        	var indicatorValue = features[i].attributes[indicator];
        	var numericIndicatorValue = this.convertToNumber(indicatorValue);
        	if(isNaN(numericIndicatorValue) == true) {
				Sbi.trace("[Thematizer.getDistributionFromLayer] : Value [" + indicatorValue + "] will be discarded because it is not a number");
			} else {
				var dataPoint = new Sbi.geo.stat.DataPoint({
					coordinates: [idValue]
					, value: numericIndicatorValue
				});
				distribution.addDataPoint( dataPoint );
			}
        }
        
        return distribution;
    }
    
    , getDistributionFromStore: function(indicator, id) {
    	var distribution = new Sbi.geo.stat.Distribution();
    	var records = this.store.getRange();
	 	Sbi.trace("[Thematizer.getDistributionFromStore] : Records number is equal to [" + records.length + "]");
   	 	
	 	var indicatorFiledName, idFiledName;
	 	for(var n = 0; n < records[0].fields.getCount(); n++) {
	 		var field = records[0].fields.itemAt(n);
	 		if(field.header == indicator) {
	 			indicatorFiledName = field.name;
	 		}
	 		if(field.header == id) {
	 			idFiledName = field.name;
	 		}
	 		
	 		if(indicatorFiledName && idFiledName) break;
	 	}
	 	
	 	if(!idFiledName) {
	 		alert("Impossible to find a column was header is equal to [" + id + "]");
	 	}
	 	
	 	for (var i = 0; i < records.length; i++) {	
	 		var idValue = records[i].get(idFiledName);
   	 		var indicatorValue = records[i].get(indicatorFiledName);
   	 		var numericIndicatorValue = this.convertToNumber(indicatorValue);
        	if(isNaN(numericIndicatorValue) == true) {
				Sbi.trace("[Thematizer.getDistributionFromStore] : Value [" + indicatorValue + "] will be discarded because it is not a number");
			} else {
				Sbi.trace("[Thematizer.getDistributionFromStore] : Add datapoint [" + indicatorValue + " - " + idValue + "] to distribution");
				var dataPoint = new Sbi.geo.stat.DataPoint({
					coordinates: [idValue]
					, value: numericIndicatorValue
				});
				distribution.addDataPoint( dataPoint );
			}
        }
    	return distribution;
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
    	Sbi.trace("[Thematizer.initialize]: IN");
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
        	Sbi.debug("[Thematizer.initialize]: Url attribute has been valorized to [" + Sbi.toSource(url) + "]. Features will be loaded from it");
            OpenLayers.loadURL(
                this.url, '', this, this.onSuccess, this.onFailure);
        } else {
        	Sbi.debug("[Thematizer.initialize]: Url attribute has not been valorized");
        }
        
        this.legendDiv = Ext.get(options.legendDiv);
        
        if(this.indicatorContainer == 'store') {
        	
        	Sbi.debug("[Thematizer.initialize]: Property [indicatorContainer] is equal to [store]");
        	
        	if(this.storeType === 'physicalStore') {
        		this.store.on('load', this.onPhysicalStoreLoaded, this);
            	if(this.storeReload == true) {
            		this.loadPhysicalStore();
            	}
        	} else if(this.storeType === 'virtualStore') {
        		if(this.storeConfig.params) {
	        		this.loadVirtualStore();
	        	}
        	} else {
        		Sbi.debug("[Thematizer.initialize]: Property [storeType] value [" + this.storeType + "] is not valid");
        	}
        } else if(this.indicatorContainer == 'layer') {
        	Sbi.debug("[Thematizer.initialize]: Property [indicatorContainer] is equal to [layer]");
        } else {
        	Sbi.warn("[Thematizer.initialize]: Property [indicatorContainer] is equal to [" + this.indicatorContainer + "]");
        }
        
        Sbi.trace("[Thematizer.initialize]: OUT");
     }
     
     , loadPhysicalStore: function() {
    	 Sbi.debug("[Thematizer.loadPhysicalStore]: IN");
    	 this.store.load({});
    	 Sbi.debug("[Thematizer.loadPhysicalStore]: OUT");
     }
     
     , loadVirtualStore: function() {
    	 
    	 Sbi.debug("[Thematizer.loadPhysicalStore]: OUT");
    	 
    	 if(!this.storeConfig.params) {
    		 Sbi.warn("Impossible to load virtual store because property [storeConfig.params] is undefined");
    	 }
    	 
    	 if(!this.storeConfig.url) {
    		 Sbi.warn("Impossible to load virtual store because property [storeConfig.url] is undefined");
    	 }
    	 
    	 Ext.Ajax.request({
			url: this.storeConfig.url
			, params: this.storeConfig.params
			, success : this.onVirtualStoreLoaded
			, failure: Sbi.exception.ExceptionHandler.handleFailure
			, scope: this
    	 });
    	 
    	 Sbi.debug("[Thematizer.loadPhysicalStore]: OUT");
     }
     
     /**
      * @method 
      *
      * @param {Object} request
      */
     , onPhysicalStoreLoaded: function(store, records, options) {
    	Sbi.trace("[Thematizer.onPhysicalStoreLoaded]: IN");
    	Sbi.trace("[Thematizer.onPhysicalStoreLoaded]: OUT");
     }
     
     , onVirtualStoreLoaded: function(response, options) {
    	 Sbi.trace("[Thematizer.onVirtualStoreLoaded]: IN");
    	 if(response !== undefined && response.responseText !== undefined && response.statusText=="OK") {
    		 if(response.responseText!=null && response.responseText!=undefined) {
				if(response.responseText.indexOf("error.mesage.description")>=0){
					Sbi.exception.ExceptionHandler.handleFailure(response);
				} else {
					//Sbi.debug(response.responseText);
					var r = Ext.util.JSON.decode(response.responseText);
			
					var store = new Ext.data.JsonStore({
					    fields: r.metaData.fields
					});
					store.loadData(r.rows);
					this.setData(store, r.metaData);
				}
			}
		} else {
			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		}
    	 
    	Sbi.trace("[Thematizer.onVirtualStoreLoaded]: OUT");
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
     
     // -----------------------------------------------------------------------------------------------------------------
     // private methods
 	 // -----------------------------------------------------------------------------------------------------------------
 	
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
});





/* Copyright (c) 2006-2011 by OpenLayers Contributors (see authors.txt for 
 * full list of contributors). Published under the Clear BSD license.  
 * See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @requires OpenLayers/Filter.js
 */

/**
 * Class: OpenLayers.Filter.Function
 * This class represents a filter function.
 * We are using this class for creation of complex 
 * filters that can contain filter functions as values.
 * Nesting function as other functions parameter is supported.
 * 
 * Inherits from
 * - <OpenLayers.Filter>
 */
OpenLayers.Filter.Function = OpenLayers.Class(OpenLayers.Filter, {

    /**
     * APIProperty: name
     * {String} Name of the function.
     */
    name: null,
    
    /**
     * APIProperty: params
     * {Array(<OpenLayers.Filter.Function> || String || Number)} Function parameters
     * For now support only other Functions, String or Number
     */
    params: null,  
    
    /** 
     * Constructor: OpenLayers.Filter.Function
     * Creates a filter function.
     *
     * Parameters:
     * options - {Object} An optional object with properties to set on the
     *     function.
     * 
     * Returns:
     * {<OpenLayers.Filter.Function>}
     */
    initialize: function(options) {
        OpenLayers.Filter.prototype.initialize.apply(this, [options]);
    },

    CLASS_NAME: "OpenLayers.Filter.Function"
});



