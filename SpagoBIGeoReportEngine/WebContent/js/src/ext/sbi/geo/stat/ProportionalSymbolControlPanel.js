/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/

Ext.ns("Sbi.geo.stat");

/**
 * The ProportionalSymbol class create a widget allowing to set up and display proportional symbol
 * thematization on a map.
 * 
 * ## Further Reading
 * 
 * The proportional symbol thematization technique uses symbols of different sizes to represent data associated 
 * with different areas or locations within the map. For example, a disc may be shown at the location 
 * of each city in a map, with the area of the disc being proportional to the population of the city.
 *
 * @author Andrea Gioia
 */
Sbi.geo.stat.ProportionalSymbolControlPanel = Ext.extend(Ext.FormPanel, {

	/**
	 * @property {OpenLayers.Layer.Vector} layer
	 * The vector layer containing the features that
	 * are styled based on statistical values. If none is provided, one will
	 * be created.
	 */
    layer: null

    /**
	 * @property {OpenLayers.Format} format
	 * The OpenLayers format used to get features from the HTTP request response. 
	 * GeoJSON is used if none is provided.
	 */
    , format: null

    /**
	 * The service name to call in order to load target layer. If none is provided, the features
     * found in the provided vector layer will be used.
	 */
    , loadLayerServiceName: null

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
	 * The indicator currently chosen
	 */
    , indicator: null
    
    /**
     * @property {String} indicator
     * The raw value of the currently chosen indicator (ie. human readable)
     */
    , indicatorText: null

    /**
     * @property {Sbi.geo.stat.ProportionalSymbolThematizer} thematizer
     * The core thematizer object.
     */
    , thematizer: null
    
    /**
     * @property {boolean} thematizationApplied
     * true if the thematization was applied
     */
    , thematizationApplied: false
   
    /**
     * @property {Boolean} ready
     * true if the widget is ready to accept user commands.
     */
    , ready: false

    /**
     * @property {Boolean} border
     * Styling border
     */
    , border: false
    

    // =================================================================================================================
	// METHODS
	// =================================================================================================================
    
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    /**
     * Method: classify
     *    Reads the features to get the different value for
     *    the field given for indicator
     *    Creates a new Distribution and related Classification
     *    Then creates an new ProportionalSymbols and applies classification
     */
    , thematize: function(exception, additionaOptions) {
    	
    	Sbi.trace("[ProportionalSymbolControlPanel.thematize] : IN");
    	
    	var doThematization = true;
    	
        if (!this.ready) {
            if (exception) {
                Ext.MessageBox.alert('Error', 'Component init not complete');
            }
            return;
        }
        
        var options = this.getThemathizerOptions();
        
        if (!options.indicator) {
            if (exception === true) {
                Ext.MessageBox.alert('Error', 'You must choose an indicator');
            }
            doThematization = false;
        }
        
        options = Ext.apply(options, additionaOptions||{});
        
        if(doThematization) {
        	this.thematizer.thematize(options);
            this.thematizationApplied = true;
        } else {
        	this.thematizer.setOptions(options);
        } 
        
        Sbi.trace("[ProportionalSymbolControlPanel.thematize] : IN");
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------
    
	, getThemathizerOptions: function() {
		Sbi.trace("[ProportionalSymbolControlPanel.getThemathizerOptions] : IN");
		var formState = this.getFormState();
		var options = {};
		options.indicator = formState.indicator;
		options.minRadiusSize = formState.minRadiusSize;
		options.maxRadiusSize = formState.maxRadiusSize;
		
		Sbi.trace("[ProportionalSymbolControlPanel.getThemathizerOptions] : OUT");
		
		return options;
	}
	
	, getFormState: function() {
		var formState = {};
		

		formState.indicator = this.getIndicator();
		formState.minRadiusSize = this.getMinRadiusSize();
		formState.maxRadiusSize = this.getMaxRadiusSize();
		
		return formState;
	}
	
	, setFormState: function(formState, riseEvent) {
		Sbi.trace("[ProportionalSymbolControlPanel.setFormState] : IN");
	
//		this.setMethod(formState.method);
//		this.setNumberOfClasses(formState.classes);
//		this.setFromColor(formState.fromColor);
//		this.setToColor(formState.toColor);
//		this.setIndicator(formState.indicator);
//		this.setFiltersDefaultValues(formState.filtersDefaultValues);
//		if(riseEvent === true) { this.onConfigurationChange(); }
		
		Sbi.trace("[ProportionalSymbolControlPanel.setFormState] : OUT");
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected classification indicator
	 */
	, getIndicator: function() {
		return this.form.findField('indicator').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected classification min radius size
	 */
	, getMinRadiusSize: function() {
		return this.form.findField('minSize').getValue();
	}
	
	/**
	 * @method
	 * 
	 * @return {String} the selected classification max radius size
	 */
	, getMaxRadiusSize: function() {
		return this.form.findField('maxSize').getValue();
	}
    
    // -----------------------------------------------------------------------------------------------------------------
    // init method
	// -----------------------------------------------------------------------------------------------------------------
    
	  /**
     * Method: onRender
     * Called by EXT when the component is rendered.
     */
    , onRender: function(ct, position) {
    	Sbi.geo.stat.ProportionalSymbolControlPanel.superclass.onRender.apply(this, arguments);
        
        var thematizerOptions = {
        	'layer': this.layer,
            'layerName': this.layerName,
            'layerId' : this.geoId,
          	'loadLayerServiceName': this.loadLayerServiceName,
          	'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            	
            'format': this.format,
            'featureSourceType': this.featureSourceType,
            'featureSource': this.featureSource,
        		
        	'featureSelection': this.featureSelection,
        	'nameAttribute': this.nameAttribute,
        	        
        	'indicatorContainer': this.indicatorContainer,
        	'storeType': this.storeType,
        	'storeConfig': this.storeConfig,
        	'store': this.store,
        	'storeId' : this.businessId,
        		       
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator      	
        };
        
        
       
       
        
        
        this.thematizer = new Sbi.geo.stat.ProportionalSymbolThematizer(this.map, thematizerOptions);
    }
	
    /**
     * @private
     * Called by EXT when the component is initialized.
     */
    , initComponent : function() {
        this.items = [
            this.initIndicatorSelectionField()
            , this.initMinRadiusSizeField()
            , this.initMaxRadiusSizeField()
        ];
         
        this.buttons = [{
            text: 'OK',
            handler: this.thematize,
            scope: this
        }];
        Sbi.geo.stat.ProportionalSymbolControlPanel.superclass.initComponent.apply(this);
    }

    /**
     * @private
     * Initialize the indicators' selection field
     */
    , initIndicatorSelectionField: function() {
    	this.indicatorSelectionField = new Ext.form.ComboBox({
    		fieldLabel: LN('sbi.geo.analysispanel.indicator'),
            name: 'indicator',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: LN('sbi.geo.analysispanel.emptytext'),
            valueNotFoundText: LN('sbi.geo.analysispanel.emptytext'),
            triggerAction: 'all',
            store: new Ext.data.SimpleStore({
            	fields: ['value', 'text'],
            	data : this.indicators
            }),
            listeners: {
                'select': {
                    fn: function() {
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
     * Initialize the minRadiusSize' selection field
     */
    , initMinRadiusSizeField: function() {
    	this.minRadiuSize = new Ext.form.NumberField({
    		fieldLabel:'Min Size',
            name: 'minSize',
            width: 30,
            value: 2,
            maxValue: 20
    	});
    	
    	return this.minRadiuSize;
    }
    
    /**
     * @private
     * Initialize the minRadiusSize' selection field
     */
    , initMaxRadiusSizeField: function() {
    	this.maxRadiuSize = new Ext.form.NumberField({
             fieldLabel:'Max Size',
             name: 'maxSize',
             width: 30,
             value: 20,
             maxValue: 50
    	});
    	
    	return this.maxRadiuSize;
    }
    
    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    , requestSuccess: function(request) {
        this.ready = true;
        this.fireEvent('ready', this);
    }

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    , requestFailure: function(response) {
    	var message = response.responseXML;
        if (!message || !message.documentElement) {
            message = response.responseText;
        }
        Sbi.exception.ExceptionHandler.showErrorMessage(message, 'Service Error');
    }   
});
Ext.reg('proportionalsymbol', Sbi.geo.stat.ProportionalSymbolControlPanel);
