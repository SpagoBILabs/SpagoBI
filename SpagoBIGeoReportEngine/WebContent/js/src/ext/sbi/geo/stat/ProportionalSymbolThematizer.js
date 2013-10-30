/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/

Ext.ns("Sbi.geo.stat");


Sbi.geo.stat.ProportionalSymbolThematizer = function(map, config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	// constructor
	Sbi.geo.stat.ProportionalSymbolThematizer.superclass.constructor.call(this, map, config);
};

/**
 * @class Sbi.geo.stat.ProportionalSymbolThematizer
 * @extends Sbi.geo.stat.Thematizer
 * 
 * Use this class to create proportional symbols on a map.
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
    
    , thematyzerType: "proportionalSymbols"


    // =================================================================================================================
	// METHODS
	// =================================================================================================================
    
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
   

    , initialize: function(map, options) {
    	Sbi.geo.stat.ProportionalSymbolThematizer.superclass.initialize.call(this, map, options);
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
        
    	Sbi.trace("[ProportionalSymbolThematizer.thematize] : IN");
    	
    	Sbi.trace("[ProportionalSymbolThematizer.thematize] : layer [" + this.layer + "]");
    	if(this.layer){
    		Sbi.trace("[ProportionalSymbolThematizer.thematize] : layer [" + this.layer.features.length + "]");
    	}
    	
    	if (options) {
    		if(options.resetClassification) {
    			this.setClassification();
    		} else {
    			this.updateOptions(options);
    		}
        }
        
        var calculateRadius = OpenLayers.Function.bind(
            function(feature) {
            	var size;
            	
            	var dataPoint = this.distribution.getDataPoint([ feature.attributes[this.layerId] ]);
            	if(dataPoint) {
            		 var value = dataPoint.getValue();
                     var minValue = this.distribution.getMinDataPoint().getValue();
                     var maxValue = this.distribution.getMaxDataPoint().getValue();
                     
                     
                     size = (value - minValue) / ( maxValue - minValue) *
                                (this.maxRadiusSize - this.minRadiusSize) + this.minRadiusSize;
                     
                     Sbi.trace("[ProportionalSymbolThematizer.calculateRadius] : radius for feature [" + feature.attributes[this.layerId] + "]is equal to [" + size + "]");
            	} else {
            		size = 0;
            	}
               
                return size;
            }, this
        );
        
        this.extendStyle(null,
            {'pointRadius': '${calculateRadius}'},
            {'calculateRadius': calculateRadius}
        );
        
        Sbi.geo.stat.Thematizer.prototype.thematize.apply(this, arguments);
        
        Sbi.trace("[ProportionalSymbolThematizer.thematize] : OUT");
    }
    
    
    /**
     * @method
     * Creates the classification that will be used for map thematization
     */  
    , classify: function() {
    	Sbi.trace("[ProportionalSymbolThematizer.classify] : IN");
        
    	this.distribution = this.getDistribution(this.indicator);
    	Sbi.debug("[ChoroplethThematizer.setClassification] : Extracted [" + this.distribution.getSize() + "] values for indicator [" + this.indicator + "]");
            
        Sbi.trace("[ProportionalSymbolThematizer.classify] : OUT");
    }
    
    /**
     * @method
     * @deperecated use #classify instead
     */
    , setClassification: function() {
    	Sbi.trace("[ProportionalSymbolThematizer.setClassification] : IN");
    	Sbi.warn("[ProportionalSymbolThematizer.setClassification] : Method [setClassification] is deprecated. Use method [classify] instead");
    	this.classify();
    	Sbi.trace("[ProportionalSymbolThematizer.setClassification] : IN");
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
    	Sbi.trace("[ProportionalSymbolThematizer.updateOptions] : IN");
    	var oldOptions = Ext.apply({}, this.options);
        this.setOptions(newOptions);
        if(newOptions) {
        	 if (newOptions.indicator != oldOptions.indicator
        			 || newOptions.minRadiusSize != newOptions.minRadiusSize
        			 || newOptions.maxRadiusSize != newOptions.maxRadiusSize) {
        		 this.classify();
             }
        }
       
        Sbi.trace("[ProportionalSymbolThematizer.updateOptions] : IN");
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

Sbi.geo.stat.Thematizer.addSupportedType("proportionalSymbols", Sbi.geo.stat.ProportionalSymbolThematizer, Sbi.geo.stat.ProportionalSymbolControlPanel);
