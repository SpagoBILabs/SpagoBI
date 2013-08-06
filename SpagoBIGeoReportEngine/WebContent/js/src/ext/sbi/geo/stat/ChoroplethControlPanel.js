/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/
Ext.ns("Sbi.geo.stat");

/**
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */


/**
 * The Choropleth class create a widget allowing to set up and display choropleth
 * thematization on a map
 *
 * ## Further Reading
 *
 * A choropleth map is a thematic map in which areas are shaded or patterned in 
 * proportion to the measurement of the statistical variable being displayed on 
 * the map, such as population density or per-capita income.
 * 
 * The choropleth map provides an easy way to visualize how a measurement varies 
 * across a geographic area or it shows the level of variability within a region.
 *
 *   - {@link http://en.wikipedia.org/wiki/Choropleth_map} - Wikipedia entry on Choroplet maps
 *   - {@link http://leafletjs.com/examples/choropleth.html} - An example of interactive choroplet map
 *
 * @author Andrea Gioia
 */
Sbi.geo.stat.ChoroplethControlPanel = Ext.extend(Ext.FormPanel, {
	
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
	 * @property {String} indicatorContainer
	 * The object that contains data points used by the thematizer. It is equal to 'store' if data points are 
	 * contained into an Ext.data.Store, it is equalt to 'layer' otherwise if datapoint are contained into an
	 * OpenLayers.Layer.Vector. By default it is equal to store.
	 */
	 indicatorContainer: 'store'
		 
	
	 /**
	  * @property {String} storeType
	  * The type of store that contains data points used by thematizer. Only apply if property #indicatorContainer is
	  * equal to 'store'. It is equal to 'physicalStore' if the store is feeded by a SpagoBI' dataset, it is equal to
	  * 'virtualStore' if the store is feeded by SpagoBI's measure catalogue.
	  */
     , storeType: 'physicalStore'
    	
     /**
      * @property {String} storeConfig
      * An object containing configuration used to generate the store. If property #storeType is equal to 'virtualStore' contains
      * for example the ids of the mesure to join in the generated dataset
      */
     , storeConfig: null
     
	/**
	 * @property {OpenLayers.Layer.Vector} layer
	 * The vector layer containing the features that
	 * are styled based on statistical values. If none is provided, one will
	 * be created.
	 */
    , layer: null

    /**
	 * @property {OpenLayers.Format} format
	 * The OpenLayers format used to get features from the HTTP request response. 
	 * GeoJSON is used if none is provided.
	 */
    , format: null

    /**
	 * The URL to the web service. If none is provided, the features
     * found in the provided vector layer will be used.
	 */
    , url: null

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
	 * @property {Array} indicators
	 * An array of selectable indicators. Each item of the array is an array composed by two element. The first is the name
	 * of the indictor the secon one is the indicator text (ie. human readable).
	 */
    , indicators: null
    
    /**
	 * @property {String} indicator
	 * The feature attribute currently chosen. Useful if callbacks are registered 
	 * on 'featureselected' and 'featureunselected' events
	 */
    , indicator: null

    
    /**
	 * @property {String} indicatorText
	 * The raw value of the currently chosen indicator (ie. human readable). Useful if callbacks are registered on 'featureselected'
	 * and 'featureunselected' events
	 */
    , indicatorText: null

    
    /**
     * Property: thematizer
     * {<mapfish.GeoStat.ProportionalSymbol>} The core component object.
     */
    , thematizer: null

    /**
     * Property: thematizationApplied
     * {Boolean} true if the thematization was applied
     */
    , thematizationApplied: false

    /**
     * Property: ready
     * {Boolean} true if the widget is ready to accept user commands.
     */
    , ready: false

    /**
     * Property: border
     *     Styling border
     */
    
    /**
     * @cfg {Boolean} [border=false]
     * `true` if the border should be ..., false if it is ...
     */
    , border: false

    /**
     * @property {Boolean} loadMask
     * `true` to mask the widget while loading (defaults to false).
     */
    , loadMask : false

    /**
     * APIProperty: labelGenerator
     * Generator for bin labels
     */
    , labelGenerator: null

    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
    
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    /**
     * Perform thematization
     * 
     * @param {Boolean} exception If true show a message box to user if either
     * the widget isn't ready, or no indicator is specified, or no
     * method is specified.
     */
    , thematize: function(exception) {
    	Sbi.trace("[ChoropletControlPanel.thematize] : IN");
    	
    	var doThematization = true;
    	
        if (!this.ready) {
            if (exception === true) {
                Ext.MessageBox.alert('Error', 'Component init not complete');
            }
            doThematization = false;
        }
        var options = this.getThemathizerOptions();
       
        if (!options.indicator) {
            if (exception === true) {
                Ext.MessageBox.alert('Error', 'You must choose an indicator');
            }
            doThematization = false;
        }
        if (!options.method) {
            if (exception === true) {
                Ext.MessageBox.alert('Error', 'You must choose a method');
            }
            doThematization = false;
        }

        if(doThematization) {
        	this.thematizer.thematize(options);
            this.thematizationApplied = true;
        } else {
        	this.thematizer.setOptions(options);
        }
        
        
        Sbi.trace("[ChoropletControlPanel.thematize] : OUT");
    }
	
    // -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
    
	, getThemathizerOptions: function() {
		Sbi.trace("[ChoropletControlPanel.getThemathizerOptions] : IN");
		var formState = this.getFormState();
		var options = {};
		
		options.method = Sbi.geo.stat.Classifier[formState.method];
		options.numClasses = formState.classes;
		options.colors = new Array(2);
		options.colors[0] = new mapfish.ColorRgb();
		options.colors[0].setFromHex(formState.fromColor);
		options.colors[1] = new mapfish.ColorRgb();
		options.colors[1].setFromHex(formState.toColor);
		options.indicator = formState.indicator;
		
		Sbi.trace("[ChoropletControlPanel.getThemathizerOptions] : OUT");
		
		return options;
	}

	, getFormState: function() {
		var formState = {};
		
		formState.method = this.getMethod();
		formState.classes = this.getNumberOfClasses();
		formState.fromColor = this.getToColor();
		formState.toColor = this.getFromColor();
		formState.indicator = this.getIndicator();
		
		return formState;
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected classification method
	 */
	, getMethod: function() {
		return this.form.findField('method').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected number of classes 
	 */
	, getNumberOfClasses: function() {
		return this.form.findField('numClasses').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected lowerBoundColor
	 */
	, getToColor: function() {
		return this.form.findField('colorA').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected upper bound color
	 */
	, getFromColor: function() {
		return this.form.findField('colorB').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected indicator
	 */
	, getIndicator: function() {
		return this.form.findField('indicator').getValue();
	}
	
	, setFormState: function(formState, riseEvent) {
		Sbi.trace("[ChoropletControlPanel.setFormState] : IN");
	
		this.setMethod(formState.method);
		this.setNumberOfClasses(formState.classes);
		this.setFromColor(formState.fromColor);
		this.setToColor(formState.toColor);
		this.setIndicator(formState.indicator);
		
		if(riseEvent === true) { this.onConfigurationChange(); }
		
		Sbi.trace("[ChoropletControlPanel.setFormState] : OUT");
	}
	
	, setMethod: function(method, riseEvent) {
		if(method) {
			var m = Sbi.geo.stat.Classifier[method];
			if(m) {
				this.form.findField('method').setValue(method);	
			} else {
				Sbi.warn("[ChoropletControlPanel.setMethod] : Classification method [" + formState.method + "] is not valid");
			}	
			if(riseEvent === true) {this.onConfigurationChange();}
		}
	}
	
	, setNumberOfClasses: function(classes, riseEvent) {
		if(classes) {
			this.form.findField('numClasses').setValue(classes);
			if(riseEvent === true) {this.onConfigurationChange();}
		} 
	}
	
	, setFromColor: function(color, riseEvent) {
		if(color) {
			this.form.findField('colorA').setValue(color);
			if(riseEvent === true) {this.onConfigurationChange();}
		} 
	}
	
	, setToColor: function(color, riseEvent) {
		if(color) {
			this.form.findField('colorB').setValue(color);
			if(riseEvent === true) {this.onConfigurationChange();}
		} 
	}
	
	, setIndicator: function(indicatorName, riseEvents) {
		Sbi.trace("[ChoropletControlPanel.setIndicator] : IN");
		Sbi.trace("[ChoropletControlPanel.setIndicator] : Looking for indicator [" + indicatorName + "] ...");
		
		if(indicatorName) {
			var indicator = null;
			for(var i = 0; i < this.indicators.length; i++) {
				Sbi.trace("[ChoropletControlPanel.setIndicator] : Comparing indicator [" + indicatorName + "] with indicator [" + this.indicators[i][0] +"]");
				if (indicatorName == this.indicators[i][0]) {
					indicator = this.indicators[i];
					break;
				}
	        }
			if(indicator != null) {
				Sbi.trace("[ChoropletControlPanel.setIndicator] : Indicator [" + indicatorName + "] succesfully found");
				this.indicatorSelectionField.setValue(indicator[0]);
				this.indicator = indicator[0][0];
				this.indicatorText = indicator[0][1];
				
				if(riseEvents === true) { this.onConfigurationChange(); }
			} else {
				Sbi.warn("[ChoropletControlPanel.setIndicator] : Impossible to find indicator [" + indicatorName + "]");
			}
		}
		
		Sbi.trace("[ChoropletControlPanel.setIndicator] : OUT");
	}
    
	/**
	 * @method 
	 * Set a new list of indicators usable to generate the thematization
	 * 
	 * @param {Array} indicators new indicators list. each element is an array of two element:
	 * the first is the indicator name while the second one is the indicator text
	 * @param {String} indicator the name of the selected indicator. Must be equal to one of the 
	 * names of the indicators passed in as first parameter. It is optional. If not specified
	 * the first indicators of the list will be selected.
	 * @param {boolean} riseEvents true to rise an event in order to regenerate the thematization, false 
	 * otherwise. Optional. By default false.
	 */
	, setIndicators: function(indicators, indicator, riseEvents) {
		Sbi.trace("[ChoropletControlPanel.setIndicators] : IN");
		
		Sbi.trace("[ChoropletControlPanel.setIndicators] : New indicators number is equal to [" + indicators.length + "]");
		
    	this.indicators = indicators;
    	var newStore = new Ext.data.SimpleStore({
            fields: ['value', 'text'],
            data : this.indicators
        });
        this.indicatorSelectionField.bindStore(newStore);
        
        if(Ext.isArray(indicator)) indicator = indicator[0];
        indicator = indicator || indicators[0][0];
        this.setIndicator(indicator, riseEvents);
        
        Sbi.trace("[ChoropletControlPanel.setIndicators] : OUT");      
    }
	
	
    
    //-----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------
    
    /**
     * @private
     * Called by EXT when the component is rendered.
     */
    , onRender: function(ct, position) {
    	Sbi.geo.stat.ChoroplethControlPanel.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator,
            'indicatorContainer': this.indicatorContainer,
            'storeType': this.storeType,
            'storeConfig': this.storeConfig,
            'store': this.store,
            'layerId' : this.geoId,
            'storeId' : this.businessId
        };

        this.thematizer = new Sbi.geo.stat.ChoroplethThematizer(this.map, coreOptions);
        
        this.thematizer.on('indicatorsChanged', function(thematizer, indicators, selectedIndicator){
			this.setIndicators(indicators, selectedIndicator, false);
		}, this);
    }
    
    /**
     * @private
     * Called by EXT when the component is initialized.
     */
    , initComponent : function() {
        this.items = [
            this.initIndicatorSelectionField()
            , this.initMethodSelectionField()
            , this.initClassesNumberSelectionField()
            , this.initFromColorSelectionField()
            , this.initToColorSelectionField()
        ];

        Sbi.geo.stat.ChoroplethControlPanel.superclass.initComponent.apply(this);
    }
    
    /**
     * @private
     * Initialize the indicators' selection field
     */
    , initIndicatorSelectionField: function() {
    	this.indicatorSelectionField = new Ext.form.ComboBox  ({
            fieldLabel: 'Indicator',
            name: 'indicator',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: 'Select an indicator',
            valueNotFoundText: 'Select an indicator',
            triggerAction: 'all',
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data : this.indicators
            }),
            listeners: {
                'select': {
                    fn: function() {
                    	//alert("indicator change");
                    	this.thematize(false);
                    },
                    scope: this
                }
            }
        });
    	
    	return this.indicatorSelectionField;
    }
    
    /**
     * @private
     * Initialize the method's selection field
     */
    , initMethodSelectionField: function() {
    	this.methodSelectionField = new Ext.form.ComboBox  ({
            xtype: 'combo',
            fieldLabel: 'Method',
            name: 'method',
            hiddenName: 'method',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: 'Select a method',
            triggerAction: 'all',
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data : [['CLASSIFY_BY_EQUAL_INTERVALS', 'Equal Intervals'],
                        ['CLASSIFY_BY_QUANTILS', 'Quantils']]
            }),
            listeners: {
                'select': {
                	fn: function() {
                    	//alert("method change");
                    	this.thematize(false);
                    },
                    scope: this
                }
            }
        });
    	
    	return this.methodSelectionField;
    }
    
    /**
     * @private
     * Initialize the classes number's selection field
     */
    , initClassesNumberSelectionField: function() {
    	this.classesNumberSelectionField = new Ext.form.ComboBox  ({
            xtype: 'combo',
            fieldLabel: 'Number of classes',
            name: 'numClasses',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: 5,
            triggerAction: 'all',
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[0], [1], [2], [3], [4], [5], [6], [7], [8], [9]]
            }),
            listeners: {
                'select': {
                	fn: function() {
                    	//alert("numClasses change");
                    	this.thematize(false);
                    },
                    scope: this
                }
            }
        });
    	
    	return this.classesNumberSelectionField;
    }
    
    /**
     * @private
     * Initialize the method's selection field
     */
    , initFromColorSelectionField: function() {
    	this.fromColorSelectionField = new Ext.ux.ColorField({
            fieldLabel: 'From color',
            name: 'colorA',
            width: 100,
            allowBlank: false,
            value: "#FFFF00",
            listeners: {
                'select': {
                    fn: function() {
                    	alert("colorA change");
                    	this.thematize(false);
                    },
                    scope: this
                }
            }
        });
    	return this.fromColorSelectionField;
    }
    
    /**
     * @private
     * Initialize the method's selection field
     */
    , initToColorSelectionField: function() {
    	this.toColorSelectionField = new Ext.ux.ColorField({
            xtype: 'colorfield',
            fieldLabel: 'To color',
            name: 'colorB',
            width: 100,
            allowBlank: false,
            value: "#FF0000",
            listeners: {
                'select': {
                    fn: function() {
                    	//alert("colorB change");
                    	this.thematize(false);
                    },
                    scope: this
                }
            }
        });
    	
    	return this.toColorSelectionField;
    }
    
    //-----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , onConfigurationChange: function() {
    	//alert("Classification change");
    	this.thematize(false);
    }
    
    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    , requestSuccess: function(request) {
        this.ready = true;

        // if widget is rendered, hide the optional mask
        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
        
        this.fireEvent('ready', this);
    }

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    , requestFailure: function(request) {
        OpenLayers.Console.error('Ajax request failed');
    }   
});


Ext.reg('choropleth', Sbi.geo.stat.ChoroplethControlPanel);
