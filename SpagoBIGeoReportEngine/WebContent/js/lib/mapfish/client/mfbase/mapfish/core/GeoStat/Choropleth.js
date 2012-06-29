/*
* SpagoBI, the Open Source Business Intelligence suite
* 
* Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This file is part of MapFish Client, Copyright (C) 2007 Camptocamp 
* This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the “Incompatible With Secondary Licenses” notice, according to the ExtJS Open Source License Exception for Development, version 1.03, January 23rd, 2012 http://www.sencha.com/legal/open-source-faq/open-source-license-exception-for-development/ 
* If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
* This file is an extension to Ext JS Library that is distributed under the terms of the GNU GPL v3 license. For any information, visit: http://www.sencha.com/license.
* 
* The original copyright notice of this file follows.*/
/*
 * Copyright (C) 2007  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat.js
 */

/**
 * Class: mapfish.GeoStat.Choropleth
 * Use this class to create choropleths on a map.
 *
 * Inherits from:
 * - <mapfish.GeoStat>
 */
mapfish.GeoStat.Choropleth = OpenLayers.Class(mapfish.GeoStat, {

    /**
     * APIProperty: colors
     * {Array(<mapfish.Color>}} Array of 2 colors to be applied to features
     *     We should use styles instead
     */
    colors: [
        new mapfish.ColorRgb(120, 120, 0),
        new mapfish.ColorRgb(255, 0, 0)
    ],

    /**
     * APIProperty: method
     * {Integer} Specifies the distribution method to use. Possible
     *      values are:
     *      mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS and
     *      mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS
     */
    method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,

    /**
     * APIProperty: numClasses
     * {Integer} Number of classes
     */
    numClasses: 5,

    /**
     * Property: defaultSymbolizer
     * {Object} Overrides defaultSymbolizer in the parent class
     */
    defaultSymbolizer: {'fillOpacity': 1},

    /**
     * Property: classification
     * {<mapfish.GeoStat.Classification>} Defines the different classification to use
     */
    classification: null,

    /**
     * Property: colorInterpolation
     * {Array({<mapfish.Color>})} Array of {<mapfish.Color} resulting from the
     *      RGB color interpolation
     */
    colorInterpolation: null,

    /**
     * Constructor: mapfish.GeoStat.Choropleth
     *
     * Parameters:
     * map - {<OpenLayers.Map>} OpenLayers map object
     * options - {Object} Hashtable of extra options
     */
    initialize: function(map, options) {
        mapfish.GeoStat.prototype.initialize.apply(this, arguments);
    },

    /**
     * APIMethod: updateOptions
     *      Method used to update the properties method, indicator,
     *      numClasses and colors.
     *
     * Parameters:
     * newOptions - {Object} options object
     */
    updateOptions: function(newOptions) {
        var oldOptions = OpenLayers.Util.extend({}, this.options);
        this.addOptions(newOptions);
        if (newOptions) {
            if (newOptions.method != oldOptions.method ||
                newOptions.indicator != oldOptions.indicator ||
                newOptions.numClasses != oldOptions.numClasses) {
                this.setClassification();
            } else if (newOptions.colors && (
                       !newOptions.colors[0].equals(oldOptions.colors[0]) ||
                       !newOptions.colors[1].equals(oldOptions.colors[1]))) {
                this.createColorInterpolation();
            }
        }
    },

    /**
     * Method: createColorInterpolation
     *      Generates color interpolation in regard to classification.
     */
    createColorInterpolation: function() {
        var initialColors = this.colors;
        var numColors = this.classification.bins.length;
        this.colorInterpolation =
            mapfish.ColorRgb.getColorsArrayByRgbInterpolation(
                initialColors[0], initialColors[1], numColors
            );
    },

    /**
     * Method: setClassification
     *      Creates a classification with the features.
     */
    setClassification: function() {
        var values = [];
        var features = this.layer.features;
        for (var i = 0; i < features.length; i++) {
            values.push(features[i].attributes[this.indicator]);
        }

        var distOptions = {
            'labelGenerator' : this.options.labelGenerator
        };
        var dist = new mapfish.GeoStat.Distribution(values, distOptions);
        this.classification = dist.classify(
            this.method,
            this.numClasses,
            null
        );
        this.createColorInterpolation();
    },

    /**
     * APIMethod: applyClassification
     *      Style the features based on the classification
     *
     * Parameters:
     * options - {Object}
     */
    /**
     * APIMethod: applyClassification
     *      Style the features based on the classification
     *
     * Parameters:
     * options - {Object}
     */
    applyClassification: function(options) {
      
    	//alert('applyClassification');
    	function Geometry(symbol, maxSize, maxValue){
		    this.symbol = symbol;
		    this.maxSize = maxSize;
		    this.maxValue = maxValue;		
		    this.getSize = function(value){
		        switch(this.symbol) {
		            case 'circle': // Returns radius of the circle
		            case 'square': // Returns length of a side
		                return Math.sqrt(value/this.maxValue)*this.maxSize;
		            case 'bar': // Returns height of the bar
		                return (value/this.maxValue)*this.maxSize;
		            case 'sphere': // Returns radius of the sphere
		            case 'cube': // Returns length of a side
		                return Math.pow(value/this.maxValue, 1/3)*this.maxSize;
		        }
		    }
		}
        
         var symbol = new Geometry('circle', 20, 1312978855);

            var context = {
                getSize: function(feature) {
                    return 100;
                },
                getColor: function(feature){
					
						return 'white';
					
				},
				// getChartURL:this.targetLayerConf.myFunc;
				getWidth:function(feature){
					if( feature.layer.map.analysisType=='graphic'){
					  function isIN(array, value){
						 for(current in array){
							 if (array[current] == value){
								 return true;
							 }
						 }
						 return false;
					 }
						if(!feature.layer.map.pieDimensions){
							var mapObj = feature.layer.map;
							mapObj.pieDimensions = new Object();
							var data = mapObj.features;
							for(row in data){
								if(typeof(data[row])!="function"){
								var rowTotal=0;
									if(mapObj.totalField){
										rowTotal = data[row].data[mapObj.totalField];
									}
									else{
									for(field in data[row].data){
										  var currentValue = data[row].data[field];
												if(! isNaN(currentValue) && (isIN(mapObj.fieldsToShow, field))){
													rowTotal+=currentValue;
												}
											}
									}
								feature.layer.map.pieDimensions[data[row].fid] = rowTotal;
							}
						}
							var min;
							var max;
							for(value in feature.layer.map.pieDimensions){
								if(value != 'undefined'){
									var current = feature.layer.map.pieDimensions[value];
									if(!min){min=current;}
									if( current<min){
										min=current;
									}
									if(!max){max=current;}
									if(current>max){
										max=current;
									}
								}
							}
							
							var delta = max-min;
							for(fid in feature.layer.map.pieDimensions){
								var factorK = (feature.layer.map.pieDimensions[fid] - min) / delta;
								size = (30 + Math.round(70 * factorK));
	                            feature.layer.map.pieDimensions[fid+'_size'] =size;
	                            console.log("size: " +size );
							}
						}
						//if(feature.layer.map.zoom>6){
						//return 600;
				//	}
					//else{
						console.log("size:" +  feature.layer.map.pieDimensions[feature.fid+'_size'] );
						return feature.layer.map.pieDimensions[feature.fid+'_size'] ;
					//}
				}
				},
				getHeight:function(feature){
					if( feature.layer.map.analysisType=='graphic'){
//					if(feature.layer.map.zoom>6){
//						return 200;
//					}
//					else{
					
						return feature.layer.map.pieDimensions[feature.fid+'_size'] ;
					}
					//}
				},
				//getChartURL:this.targetLayerConf.myFunc,
                getChartURLOriginal: function(feature) {
                	
               	 function isIN(array, value){
					 for(current in array){
						 if (array[current] == value){
							 return true;
						 }
					 }
					 return false;
				 }
               	if( feature.layer.map.analysisType=='graphic'){
            	var mapObj = feature.layer.map;
            	var values="";
            	var count  = 0;
            	var length = mapObj.fieldsToShow.length;
//            	var total = 0;
//            	for(var p in feature.data){
//            		length++;
//            		if(! isNaN(feature.data[p])){
//            			if(p!="AREA" && p!="descriptionid"){
//            			total+=feature.data[p];
//            			}
//            		}
//            	}
            	
            	var label ='';
            
            	for(var p in feature.data){                		
            		       		
            		if(! isNaN(feature.data[p]) && (isIN(mapObj.fieldsToShow, p))){
            			                count=count+1;         
                						values += feature.data[p];
				                		label += p;                		
				                		if(count<length){
									                	   label +='|';
									                	   values+=",";
				                		}
            		
            		}
            	}
            	 var esize = 50 + (10*feature.layer.map.zoom);
               if(esize>540){
            	   esize=540;
               }
               var charturl='';
               var color = "&chco=" + feature.layer.map.colors;
               var chartType = feature.layer.map.chartType;
                if(feature.layer.map.zoom>6){
//                	var valuesArray = values.split(",");
//                	var labelsArray = label.split("|");
//                	for(m=0; m<valuesArray.length;m++){
//                		var perc = (valuesArray[m]/total) * 100;
//                		labelsArray[m] = labelsArray[m] +" " + perc.toFixed(2) + "%";
//                	}
//                	label="";
//                	for(m=0;m<labelsArray.length;m++){
//                		label += labelsArray[m];
//                		if(m<labelsArray.length-1){
//                			label+="|";
//                		}
//                	}
//                	var chdl = '&chdl='+label;
//                	charturl = 'http://chart.apis.google.com/chart?cht='+chartType+'&chd=t:' + values + '&chs=' +810 + 'x' +300 + '&chf=bg,s,ffffff00'+chdl+color+'&chdls=000000,20';
//                
                	var charturl = 'http://chart.apis.google.com/chart?cht=p&chd=t:' + values + '&chds=a&chs=' + esize + 'x' + esize + '&chf=bg,s,ffffff00'+color;
                	}
                else{
                var charturl = 'http://chart.apis.google.com/chart?cht=p&chd=t:' + values + '&chds=a&chs=' + esize + 'x' + esize + '&chf=bg,s,ffffff00'+color;
                }
                return charturl;
               	}
            }
            };

            var template = {
				fillColor: "${getColor}",
                fillOpacity: 0.6,
                graphicOpacity: 1,
                externalGraphic: "${getChartURLOriginal}",
                pointRadius: 20,
                graphicWidth:  "${getWidth}",
                graphicHeight: "${getHeight}"
                 };
            
            var style = new OpenLayers.Style(template, {context: context});            
		    var styleMap = new OpenLayers.StyleMap({'default': style, 'select': {fillOpacity: 0.3 }});
        this.options.layer.styleMap = styleMap;
        this.options.layer.options.styleMap = styleMap;
        this.updateOptions(options);
        var boundsArray = this.classification.getBoundsArray();
        var rules = new Array(boundsArray.length - 1);
        for (var i = 0; i < boundsArray.length -1; i++) {
            var rule = new OpenLayers.Rule({
                symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.BETWEEN,
                    property: this.indicator,
                    lowerBoundary: boundsArray[i],
                    upperBoundary: boundsArray[i + 1]
                })
            });
            rules[i] = rule;
        }
        this.extendStyle(rules);
        mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
    },

    /**
     * Method: updateLegend
     *    Update the legendDiv content with new bins label
     */
    updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }

        // TODO use css classes instead
        this.legendDiv.update("");
        for (var i = 0; i < this.classification.bins.length; i++) {
            var element = document.createElement("div");
            element.style.backgroundColor = this.colorInterpolation[i].toHexString();
            element.style.width = "30px";
            element.style.height = "15px";
            element.style.cssFloat = "left";
            element.style.marginRight = "10px";
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.innerHTML = this.classification.bins[i].label;
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.style.clear = "left";
            this.legendDiv.appendChild(element);
        }
    }, 

    /**
     * Method: updateLegend
     *    Update the legendDiv content with new bins label
     */
    updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }

        // TODO use css classes instead
        this.legendDiv.update("");
        for (var i = 0; i < this.classification.bins.length; i++) {
            var element = document.createElement("div");
            element.style.backgroundColor = this.colorInterpolation[i].toHexString();
            element.style.width = "30px";
            element.style.height = "15px";
            element.style.cssFloat = "left";
            element.style.marginRight = "10px";
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.innerHTML = this.classification.bins[i].label;
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.style.clear = "left";
            this.legendDiv.appendChild(element);
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Choropleth"
});
