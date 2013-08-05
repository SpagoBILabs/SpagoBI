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
     * Property: classificationApplied
     * {Boolean} true if the classify was applied
     */
    , classificationApplied: false

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
     * Perform classification
     * 
     * @param {Boolean} exception If true show a message box to user if either
     * the widget isn't ready, or no indicator is specified, or no
     * method is specified.
     */
    , classify: function(exception) {
        if (!this.ready) {
            if (exception) {
                Ext.MessageBox.alert('Error', 'Component init not complete');
            }
            return;
        }
        var options = {};
        this.indicator = this.form.findField('indicator').getValue();
        this.indicatorText = this.form.findField('indicator').getRawValue();
        options.indicator = this.indicator;
        if (!options.indicator) {
            if (exception) {
                Ext.MessageBox.alert('Error', 'You must choose an indicator');
            }
            return;
        }
        options.method = this.form.findField('method').getValue();
        if (!options.method) {
            if (exception) {
                Ext.MessageBox.alert('Error', 'You must choose a method');
            }
            return;
        }
        
        options.method = Sbi.geo.stat.Classifier[options.method];
        options.numClasses = this.form.findField('numClasses').getValue();
        options.colors = this.getColors();
        this.thematizer.updateOptions(options);
        this.thematizer.thematize();
        this.classificationApplied = true;
    }
	
    // -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
    
	, setFormState: function(formState, riseEvents) {
		Sbi.trace("[ChoropletControlPanel.setFormState] : IN");
		
		var options = {};
		
		if(formState.method) {
			var m = Sbi.geo.stat.Classifier[formState.method];
			if(m) {
				this.form.findField('method').setValue(formState.method);
				options.method = m;
			} else {
				Sbi.warn("[ChoropletControlPanel.setFormState] : Classification method [" + formState.method + "] is not valid");
			}	
		}
		
		if(formState.classes) {
			this.form.findField('numClasses').setValue(formState.classes);
			options.numClasses = formState.classes;
		} 
		
		if(formState.fromColor || formState.toColor) {
			options.colors = this.getColors();
		}
		
		if(formState.fromColor) {
			this.form.findField('colorA').setValue(formState.fromColor);
			options.colors[0] = new mapfish.ColorRgb();
			options.colors[0].setFromHex(formState.fromColor);
		}
		
		if(formState.toColor) {
			this.form.findField('colorB').setValue(formState.toColor);
			options.colors[1] = new mapfish.ColorRgb();
			options.colors[1].setFromHex(formState.toColor);
		}
		
		if(formState.indicator) {
			this.form.findField('indicator').setValue(formState.indicator);
			options.indicator = formState.indicator;
		}
		
		this.thematizer.setOptions(options);
		
		Sbi.trace("[ChoropletControlPanel.setFormState] : OUT");
	}
	
	, getFormState: function() {
		var formState = {};
		
		formState.method = this.form.findField('method').getValue();
		formState.classes = this.form.findField('numClasses').getValue();
		formState.fromColor = this.form.findField('colorA').getValue();
		formState.toColor = this.form.findField('colorB').getValue();
		formState.indicator = this.form.findField('indicator').getValue();
		
		return formState;
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
	
	, setIndicator: function(indicatorName, riseEvents) {
		Sbi.trace("[ChoropletControlPanel.setIndicator] : IN");
		Sbi.trace("[ChoropletControlPanel.setIndicator] : Looking for indicator [" + indicatorName + "] ...");
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
			if(riseEvents === true) {
				// TODO: this is a workaround. We call the callback but the select event is never
				// fired. Try to overrides this behaviour.
				 this.onConfigurationChange();
				//this.indicatorSelectionField.showOnPending(indicator[0]);
			}
			this.indicatorSelectionField.setValue(indicator[0]);
			this.indicator = indicator[0][0];
			this.indicatorText = indicator[0][1];
		} else {
			Sbi.warn("[ChoropletControlPanel.setIndicator] : Impossible to find indicator [" + indicatorName + "]");
		}
		
		Sbi.trace("[ChoropletControlPanel.setIndicator] : OUT");
	}
    
    /**
     * Method: getColors
     *    Retrieves the colors from form elements
     *
     * Returns:
     * {Array(<mapfish.Color>)} an array of two colors (start, end)
     */
    , getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(this.form.findField('colorA').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(this.form.findField('colorB').getValue());
        return [colorA, colorB];
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
                    	alert("indicator change");
                    	this.classify(false);
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
                    	alert("method change");
                    	this.classify(false);
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
                    	alert("numClasses change");
                    	this.classify(false);
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
                    	this.classify(false);
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
                    	alert("colorB change");
                    	this.classify(false);
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
    	alert("Classification change");
    	this.classify(false);
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
