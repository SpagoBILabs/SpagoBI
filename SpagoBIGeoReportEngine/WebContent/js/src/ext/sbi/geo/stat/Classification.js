/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 **/

Ext.ns("Sbi.geo.stat");


// =====================================================================================
// Bin Class
//======================================================================================
Sbi.geo.stat.Bin = function(config) {
	this.initialize(config.nbVal, config.lowerBound, config.upperBound, config.isLast);
	Sbi.geo.stat.Bin.superclass.constructor.call(this, config);
};

/**
 * @class Sbi.geo.stat.Bin
 * @extends Ext.util.Observable
 * 
 *  Bin is category of the Classification.
 *  When they are defined, lowerBound is within the class
 *  and upperBound is outside the class.
 */
Ext.extend(Sbi.geo.stat.Bin, Ext.util.Observable, {
    label: null
    , nbVal: null
    , lowerBound: null
    , upperBound: null
    , isLast: false

    , initialize: function(nbVal, lowerBound, upperBound, isLast) {
        this.nbVal = nbVal;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.isLast = isLast;
    }
});

//=====================================================================================
//Classification Class
//======================================================================================
Sbi.geo.stat.Classification = function(bins) {
	this.initialize(bins);
	Sbi.geo.stat.Classification.superclass.constructor.call(this, bins);
};

/**
 * @class Sbi.geo.stat.Classification
 * @extends Ext.util.Observable
 * Classification summarizes a Distribution by regrouping data within several Bins.
 */
Ext.extend(Sbi.geo.stat.Classification, Ext.util.Observable, {
    bins: []

    , initialize: function(bins) {
        this.bins = bins;
    }

    , getBoundsArray: function() {
        var bounds = [];
        for (var i = 0; i < this.bins.length; i++) {
            bounds.push(this.bins[i].lowerBound);
        }
        if (this.bins.length > 0) {
            bounds.push(this.bins[this.bins.length - 1].upperBound);
        }
        return bounds;
    }
});

