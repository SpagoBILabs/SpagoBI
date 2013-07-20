/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/


Ext.ns("Sbi.geo.stat");


Sbi.geo.stat.Classifier = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);

	this.initialize(config);

	Sbi.geo.stat.Classifier.superclass.constructor.call(this, config);
};
	
Sbi.geo.stat.Classifier.CLASSIFY_WITH_BOUNDS = 0;
Sbi.geo.stat.Classifier.CLASSIFY_BY_EQUAL_INTERVALS = 1;
Sbi.geo.stat.Classifier.CLASSIFY_BY_QUANTILS =  2;

/**
 * @class Sbi.geo.stat.Classifier
 * @extends Ext.util.Observable
 * 
 * Ditribution class
 */
Ext.extend(Sbi.geo.stat.Classifier, Ext.util.Observable, {
	
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
	 * @property {String} values
	 * values of the series
	 */
    values: null

    /**
	 * @property {String} nbVal
	 * number of  value 
	 */
    , nbVal: null

    /**
	 * @property {String} minVal
	 * max value 
	 */
    , minVal: null

    /**
	 * @property {String} maxVal
	 * min value 
	 */
    , maxVal: null
    
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
	 * @param {OpenLayers.Map} values values of the indicator
	 * @param {Object} options of extra options
	 */
    , initialize: function(values, options) {
        //OpenLayers.Util.extend(this, values);
        this.setValues(values);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , setValues: function(values) {
    	this.values = values || [];
        this.nbVal = values.length;
        this.minVal = null;
        this.maxVal = null;
    }
    
    /**
     * @method 
     * the max value.
     */
    , getMax: function() {
    	if(this.minVal == null) {
    		this.minVal = this.nbVal ? Math.max.apply({}, this.values): 0;
    	}
        return this.minVal;
    }

    /**
     * @method 
     * @return {Number} the min value.
     */
    , getMin: function() {
    	if(this.maxVal == null) {
    		this.maxVal = this.nbVal ? Math.min.apply({}, this.values): 0;
    	}
        return this.maxVal;
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    /**
     * @method 
     * This function calls the appropriate classifyBy... function.
     * The name of classification methods are defined by class constants
     *
     * @param {Integer} method Method name constant as defined in this class
     * @param {Integer} nbBins Number of classes
     * @param {Array(Integer)} bounds Array of bounds to be used for by bounds method
     *
     * @return {Sbi.geo.stat.Classification} Classification
     */
    , classify: function(method, nbBins, bounds) {
    	Sbi.trace("[Classifier.classify] : IN");
    	
    	Sbi.debug("[Classifier.classify] : Input parameter [method] is equal to [" + method + "]");
    	Sbi.debug("[Classifier.classify] : Input parameter [nbBins] is equal to [" + nbBins + "]");
    	Sbi.debug("[Classifier.classify] : Input parameter [bounds] is equal to [" + bounds + "]");
    	
    	var classification = null;
        if (!nbBins) {
            nbBins = this.sturgesRule();
        }
        switch (method) {
        case Sbi.geo.stat.Classifier.CLASSIFY_WITH_BOUNDS:
            classification = this.classifyWithBounds(bounds);
            break;
        case Sbi.geo.stat.Classifier.CLASSIFY_BY_EQUAL_INTERVALS :
            classification = this.classifyByEqIntervals(nbBins);
            break;
        case Sbi.geo.stat.Classifier.CLASSIFY_BY_QUANTILS :
            classification = this.classifyByQuantils(nbBins);
            break;
        default:
           alert("Unsupported or invalid classification method [" + method + "]");
        }
        
        Sbi.trace("[Classifier.classify] : OUT");
        
        return classification;
    }

    , classifyByEqIntervals: function(nbBins) {
    	Sbi.trace("[Classifier.classifyByEqIntervals] : IN");
    	
    	Sbi.debug("[Classifier.classifyByEqIntervals] : min val equal to [" + this.getMin() + "]");
    	Sbi.debug("[Classifier.classifyByEqIntervals] : max val equal to [" + this.getMax() + "]");
    	
    	var binSize = (this.getMax() - this.getMin()) / nbBins;
    	Sbi.debug("[Classifier.classifyByEqIntervals] : Each one of the [" + nbBins + "] bins will have a size of [" + binSize + "]");
    	
        var bounds = [];
        var boundsStr = '';
        
        for(var i = 0; i <= nbBins; i++) {
            bounds[i] = this.getMin() + (i*binSize);
            boundsStr +=  bounds[i] + "; ";
        }
        Sbi.debug("[Classifier.classifyByEqIntervals] : Bounds array is equal to [" + boundsStr.trim() + "];");
        
        Sbi.trace("[Classifier.classifyByEqIntervals] : OUT");

        return this.classifyWithBounds(bounds);
    }

    
    , arrayToString: function(a) {
    	 var s ='';
         for (i = 0; i < a.length; i++) {
         	s += a[i] + "; ";
         }
         return s.trim();
    }
    
    , classifyByQuantils: function(nbBins) {
    	Sbi.trace("[Classifier.classifyByQuantils] : IN");
        
    	var values = this.values;
        values.sort(function(a,b) {a = a || 0; b = b || 0; return a-b;});
        
       
        Sbi.debug("[Classifier.classifyByEqIntervals] : Sorted values array is equal to [" + this.arrayToString(values) + "];");
        
        var binSize = Math.round(this.values.length / nbBins);
        Sbi.debug("[Classifier.classifyByEqIntervals] : Each one of the [" + nbBins + "] bins will contain [" + binSize + "] values");

        var bounds = [];
        var boundsStr = '';
        var binLastValPos = (binSize == 0) ? 0 : binSize;

        if (values.length > 0) {
            bounds[0] = values[0];
            boundsStr +=  values[0] + "; ";
            for (i = 1; i < nbBins; i++) {
                bounds[i] = values[binLastValPos];
                boundsStr +=  values[binLastValPos] + "; ";
                binLastValPos += binSize;
            }
            bounds.push(values[values.length - 1]);
            boundsStr +=  values[values.length - 1] + "; ";
        }
        Sbi.debug("[Classifier.classifyByEqIntervals] : Bounds array is equal to [" + boundsStr.trim() + "];");
        
        Sbi.trace("[Classifier.classifyByQuantils] : OUT");
        
        return this.classifyWithBounds(bounds);
    }
    
    , classifyWithBounds: function(bounds) {
    	Sbi.trace("[Classifier.classifyWithBounds] : IN");
    	
        var bins = [];
        var binCount = [];
        var sortedValues = [];
        for (var i = 0; i < this.values.length; i++) {
            sortedValues.push(this.values[i]);
        }
        sortedValues.sort(function(a,b) {a = a || 0; b = b || 0; return a-b;});
        Sbi.debug("[Classifier.classifyByEqIntervals] : Sorted values array is equal to [" + this.arrayToString(sortedValues) + "];");
        
        
        var nbBins = bounds.length - 1;
        for (var i = 0; i < nbBins; i++) {
            binCount[i] = 0;
        }

        for (var i = 0; i < nbBins - 1; i) {
            if (sortedValues[0] < bounds[i + 1]) {
            	Sbi.debug("[Classifier.classifyByEqIntervals] : Added value [" + sortedValues[0] + "] of type [" + (typeof sortedValues[0]) + "] to bin [" + (i+1) + "] becuase it is less than bin ub [" + bounds[i + 1]+ "]");
                binCount[i] = binCount[i] + 1;
                sortedValues.shift();
            } else {
                i++;
                Sbi.trace("[Classifier.classifyWithBounds] : Increment to bin [" + (i+1)+ "] because value [" + sortedValues[0] + "] is greater then lb [" + bounds[i + 1] + "] of type [" + (typeof bounds[i + 1]) + "]");
            }
        }

        binCount[nbBins - 1] = this.nbVal - mapfish.Util.sum(binCount);

        for (var i = 0; i < nbBins; i++) {
        	
        	bins[i] = new Sbi.geo.stat.Bin({
        		nbVal: binCount[i]
        		, lowerBound: bounds[i]
        		, upperBound: bounds[i + 1]
        		, isLast: i == (nbBins - 1)
        	});
        	Sbi.trace("[Classifier.classifyWithBounds] : Bin [" + (i+1) + "] is equal to [" + bounds[i]+ " - " + bounds[i + 1] + "]and contains [" + binCount[i] + "] values");
          
            //var labelGenerator = this.labelGenerator || this.defaultLabelGenerator;
            bins[i].label = this.labelGenerator(bins[i], i, nbBins);
        }
        
        Sbi.trace("[Classifier.classifyWithBounds] : OUT");
        
        return new Sbi.geo.stat.Classification(bins);
    }
    
   
    
    
    /**
	 * @method
	 * Generator for bin labels
	 */
    , labelGenerator: function(bin, binIndex, nbBins) {
        return this.defaultLabelGenerator(bin, binIndex, nbBins);
    }
    /**
     * @method
     * Generator for bin labels
     *
     * Parameters:
     *   bin - {<mapfish.GeoStat.Bin>} Lower bound limit value
     *   binIndex - {Integer} Current bin index
     *   nBins - {Integer} Total number of bins
     */
    , defaultLabelGenerator: function(bin, binIndex, nbBins) {
       //.toFixed(3)
        return Sbi.commons.Format.number(bin.lowerBound, '0.000,00')  + ' - ' + Sbi.commons.Format.number(bin.upperBound, '0.000,00')  + ' (' + bin.nbVal + ')'
    }
    
    /**
     * Returns:
     * {Number} Maximal number of classes according to the Sturge's rule
     */
    , sturgesRule: function() {
        return Math.floor(1 + 3.3 * Math.log(this.nbVal, 10));
    }



});

