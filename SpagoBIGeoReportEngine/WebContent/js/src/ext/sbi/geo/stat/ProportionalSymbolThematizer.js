/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/

Ext.ns("Sbi.geo.stat");

/**
 * @requires core/GeoStat.js
 */

Sbi.geo.stat.ProportionalSymbolThematizer = function(map, config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	// constructor
	Sbi.geo.stat.ProportionalSymbolThematizer.superclass.constructor.call(this, map, config);
};

/**
 * @class Sbi.geo.stat.Choropleth
 * @extends Sbi.geo.stat.Thematizer
 * 
 * Use this class to create proportional symbols on a map.
 *
 * Mandatory options are :
 * - features
 * 
 * Example usage :
 * > new mapfish.Sbi.geo.stat.ProportionalSymbolThematizer(this.map, {
 * >     minRadiusSize: 5,
 * >     maxRadiusSize: 15,
 * >     idAttribute:
 * > });
 */
Ext.extend(Sbi.geo.stat.ProportionalSymbolThematizer, Sbi.geo.stat.Thematizer, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
    
	/**
	 * @property {Sbi.geo.stat.Classification} classification
	 * Defines the different classification to use
	 */
    classification: null
    
    /**
     * @property {Integer} minRadiusSize
     * The minimum radius size
     */
    , minRadiusSize: 2

    /**
     * APIProperty: maxRadiusSize
     * {Integer} The maximum radius size
     */
    , maxRadiusSize: 20


    // =================================================================================================================
	// METHODS
	// =================================================================================================================
    
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
   
    /**
     * Constructor: OpenLayers.Layer
     *
     * Parameters:
     * map - {<OpenLayers.Map>} OpenLayers map object
     * options - {Object} Hashtable of extra options
     */
    , initialize: function(map, options) {
    	Sbi.geo.stat.Thematizer.prototype.initialize.apply(this, arguments);
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    
    /**
     * @method 
     * This function loops over the layer's features and applies already given classification.
     *
     * @param {Object} options object with a single {Boolean} property: resetClassification.
     */
    , thematize: function(options) {
        
    	if (options) {
    		if(options.resetClassification) {
    			this.setClassification();
    		} else {
    			this.updateOptions(options);
    		}
        }
        
        var calculateRadius = OpenLayers.Function.bind(
            function(feature) {
                var value = feature.attributes[this.indicator];
                var size = (value - this.classification.minVal) / ( this.classification.maxVal - this.classification.minVal) *
                           (this.maxRadiusSize - this.minRadiusSize) + this.minRadiusSize;
                return size;
            }, this
        );
        
        this.extendStyle(null,
            {'pointRadius': '${calculateRadius}'},
            {'calculateRadius': calculateRadius}
        );
        
        Sbi.geo.stat.Thematizer.prototype.thematize.apply(this, arguments);
    }
    
    
    /**
     * @method
     * Creates the classification that will be used for map thematization
     */  
    , setClassification: function() {
    	var values = this.getValues(this.indicator);
        var dist = new Sbi.geo.stat.Classifier(values);
        this.classification = {};
        this.classification.minVal = dist.minVal;
        this.classification.maxVal = dist.maxVal;
    }

   
    /**
     * @method
     * Method used to update the properties 
     * 	- indicator, 
     *  - minRadiusSize, 
     *  - maxRadiusSize.
     *
     * @param {Object} options object
     */
    , updateOptions: function(newOptions) {
        var oldOptions = OpenLayers.Util.extend({}, this.options);
        this.setOptions(newOptions);
        if (newOptions && newOptions.indicator != oldOptions.indicator) {
            this.setClassification();
        }
    }    

    
    
    
    
    
    
    
    
    /**
     * Method: updateLegend
     *    Update the legendDiv content with new bins label
     */
    , updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }
        this.legendDiv.innerHTML = "Needs to be done !";
        // TODO use css classes instead
    }
});
