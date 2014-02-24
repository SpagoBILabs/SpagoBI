/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.core");

/**
 * @class Sbi.cockpit.core.WidgetContainer
 * @extends Ext.util.Observable
 * 
 * It manage the widget layout. At the moment it support only white board layout.
 * In the future it should be extended in order to a support different layouts (ex.
 * table, portal, ecc ...). The layout should be managed as an extension point and new
 * layouts should be plugged at any time.
 */

/**
 * @cfg {Object} config The configuration object passed to the cnstructor
 */
Sbi.cockpit.core.WidgetContainer = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	
	// init properties...
	var defaultSettings = {
		// set default values here
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.core', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.init();
	
	
	// constructor
	Sbi.cockpit.core.WidgetContainer.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.cockpit.core.WidgetContainer, Sbi.cockpit.core.WidgetRuntime, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.core.WidgetManager} widgetManager
     * The container that manages the all the widgets rendered within this panel
     */
	widgetManager: null

	/**
	 * @property {Ext.Window} widgetEditorWizard
	 * The wizard that manages the single widget definition
	 */
	, widgetEditorWizard: null
  
	/**
	 * @property {Object} defaultRegion
	 * The region of the container to  which all new widgets will be added if not explicitly specified otherwise
	 */
	, defaultRegion: {
		width : 0.5
	   	, height : 0.5
	   	, x : 0.5
	   	, y: 0.5
	}
	 
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
		if(config.widgetManager === undefined) {
			config.widgetManager = new Sbi.cockpit.core.WidgetManager({storeManager: config.storeManager});
			if(config.storeManager) {
				delete config.storeManager;
			}
		}
			
		if(config.items !== undefined) {
			config.widgetManager.register(config.items);
			delete config.items;
		}	
	}
    
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method
	 */
    , addWidget: function(widget, layoutConf) {	

		Sbi.trace("[WidgetContainer.addWidget]: IN");
    	
		widget = Sbi.cockpit.core.WidgetExtensionPointManager.getWidgetRuntime(widget);
    	if(widget) {
    		this.getWidgetManager().register(widget);
    		var storeManager = this.getWidgetManager().getStoreManager();
			if(storeManager.containsStore(widget.getStoreId()) === false) {
    			var store = this.createStore(widget.getStoreId());
    			storeManager.addStore(store);
			}
    		
    		if(Sbi.isValorized(layoutConf)) {
        		Sbi.trace("[WidgetContainer.addWidget]: Input parameter [layoutConf] is valorized");
        		widget.setLayoutConfiguration(layoutConf);
        	} else {
        		Sbi.trace("[WidgetContainer.addWidget]: Input parameter [layoutConf] is not valorized so it will e replaced with the [wlayout] property of the widget]");
        		layoutConf = widget.getLayoutConfiguration();
        	}
    	}
    	 	
    	Sbi.trace("[WidgetContainer.addWidget]: [layoutConf] is equal to [" + Sbi.toSource(layoutConf) + "]");
    	
    	var component = this.addComponent(widget, layoutConf);

		Sbi.trace("[WidgetContainer.addWidget]: OUT");
		
		return widget;
	}
    
    , getComponentRegion: function(component, relative) {
    	Sbi.trace("[WidgetContainer.getComponentRegion]: IN");
    	var region = null;
    	if( this.components.contains(component) ) {
    		var box = component.getBox();
    		region = {};
    		region.x = box.x;
    		region.y = box.y;
    		region.width = box.width;
    		region.height = box.height;
    		
    		if(relative === true) {
    			region = this.convertToRelativeRegion(region);
    		}
    	}
    	Sbi.trace("[WidgetContainer.getComponentRegion]: OUT");
    	return region;
    }
    
    , setComponentRegion: function(component, region) {
    	
    }
    
    , getWidgetManager: function() {
    	return this.widgetManager;
    }
    
    /**
     * TODO: integrate ace-extjs editor to have the configuration not only pretty printed 
     * but also highlighted
     */
    , showWidgetConfiguration: function(component) {
    	
    	Sbi.trace("[WidgetContainer.showWidgetConfiguration]: IN");
    	
    	if( Sbi.isNotValorized(component) ) {
    		Sbi.trace("[WidgetContainer.showWidgetConfiguration]: component not defined");
    	}
    	
    	var widget = component.getWidget();
    	if(widget) {
    		// to be sure to have the conf pretty printed also on old browser that dont support
        	// JSON object natively it is possible to include json2.jd by Douglas Crockford (
        	// https://github.com/douglascrockford/JSON-js)
        	var confStr = (typeof JSON === 'object')
        					? JSON.stringify(widget.getConfiguration(), null, 2)
        					: Ext.util.JSON.encode(widget.getConfiguration());
        	    		
        		
        	
        	var win = new Ext.Window({
        		id: 'configuration',
                layout:'fit',
                width:500,
                height:300,
                //closeAction:'hide',
                plain: true,
                title: "Widget [" + component.getWidgetId() + "] configuration",
                items: new Ext.form.TextArea({
                	border: false
                	, value: confStr
                    , name: 'configuration'
                }),

                buttons: [
//              {
//                	text:'Copy to clipboard',
//                  	handler: function(){
//                    		...
//                		}
//              },
                {
                	text: 'Close',
                    handler: function(){
                    	win.close();
                    }
                }]
            });
        	win.show();
    	} else {
    		alert("widget not defined");
    	}
    	
    	
    	
    	Sbi.trace("[WidgetContainer.showWidgetConfiguration]: OUT");
    }
    
    
    , createStore: function(storeId) {
    	var proxy = new Ext.data.HttpProxy({
			url: Sbi.config.serviceRegistry.getServiceUrl({
				serviceName : 'api/1.0/dataset/' + storeId + '/data'
				, baseParams: new Object()
			})
//	    	, timeout : this.timeout
//	    	, failure: this.onStoreLoadException
	    });
		
		var store = new Ext.data.Store({
			storeId: storeId,
	        proxy: this.proxy,
	        reader: new Ext.data.JsonReader(),
	        remoteSort: true
	    });
		
		return store;
    }
    
    , showWidgetEditorWizard: function(component) {    	
    	
    	Sbi.trace("[WidgetContainer.showWidgetEditorWizard]: IN");
    	
    	if(this.widgetEditorWizard === null) {
    		
    		Sbi.trace("[WidgetContainer.showWidgetEditorWizard]: instatiating the editor");

    		this.widgetEditorWizard = new Sbi.cockpit.editor.WidgetEditorWizard();
    		this.widgetEditorWizard.on("submit", function(wizard) {
    			Sbi.trace("[WidgetContainer.onSubmit]: IN");
    			wizard.hide();
    			var component = wizard.getWizardTargetComponent();
    			var wizardState = wizard.getWizardState();
    			
    			// TODO manage datasetSelection
    			wizardState.storeId = wizardState.selectedDatasetLabel;
    			var storeManager = this.getWidgetManager().getStoreManager();
    			
    			if(storeManager.containsStore(wizardState.storeId) === false) {
        			var store = this.createStore(wizardState.storeId);
        			storeManager.addStore(store);
    			}
    			storeManager.removeStore(wizardState.unselectedDatasetLabel);
    			alert(storeManager.getStoreIds().join(";"));
    			
    			delete wizardState.selectedDatasetLabel;
    			delete wizardState.unselectedDatasetLabel;
    			    			
    			component.setWidgetConfiguration( wizardState );
    			Sbi.trace("[WidgetContainer.onSubmit]: OUT");
    		}, this);
    		this.widgetEditorWizard.on("cancel", function(wizard) {
    			Sbi.trace("[WidgetContainer.onCancel]: IN");
    			wizard.hide();
    			Sbi.trace("[WidgetContainer.onCancel]: OUT");
    		}, this);
    		this.widgetEditorWizard.on("apply", function(wizard) {
    			Sbi.trace("[WidgetContainer.onApply]: IN");
    			var component = wizard.getWizardTargetComponent();
    			var wizardState = wizard.getWizardState();
    			
    			// TODO manage datasetSelection
    			wizardState.storeId = wizardState.selectedStoreId;
    			delete wizardState.selectedStoreId;
    			delete wizardState.unselectedStoreId;
    			
    			component.setWidgetConfiguration( wizardState );
    			Sbi.trace("[WidgetContainer.onApply]: OUT");
    		}, this);
    		
	    	Sbi.trace("[WidgetContainer.showWidgetEditorWizard]: editor succesfully instantiated");
    	}
    	
    	// TODO implement setTitle method
    	//this.widgetEditorWizard.setTitle("Widget [" + widget.id + "] editor");
    	this.widgetEditorWizard.getDatasetBrowserPage().setUsedDatasets(this.getWidgetManager().getStoreManager().getStoreIds());
    	this.widgetEditorWizard.setWizardTargetComponent(component);
    	
    	
    	this.widgetEditorWizard.show();
    	
    	Sbi.trace("[WidgetContainer.showWidgetEditorWizard]: OUT");
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
    , init: function() {
    	this.components = new Ext.util.MixedCollection();
    }
    
    , onRender : function(ct, position){
    	Sbi.trace("[WidgetContainer.onRender]: IN");
    	Sbi.cockpit.core.WidgetContainer.superclass.onRender.call(this, ct, position);
    	if( Sbi.isValorized(this.widgets)) {
    		Sbi.trace("[WidgetContainer.onRender]: There are [" + this.widgets.length + "] widget(s) to render");
    		for(var i = 0; i < this.widgets.length; i++) {
    			var widgetConf = this.widgets[i];
    			var w = this.addWidget(widgetConf);
    			//w.setStoreId(widgetConf.storeId);
    		}
    	} else {
    		Sbi.trace("[WidgetContainer.onRender]: There are no widget to render");
    	}
    	Sbi.trace("[WidgetContainer.onRender]: OUT");
    }
    
   
    // -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , getContainerSize: function() {
    	return Ext.getBody().getViewSize();
    }
    
    , getContainerWidth: function() {
    	return this.getContainerSize().width;
    }
     
    , getContainerHeight: function() {
    	return this.getContainerSize().height;
    }
    
    , convertToAbsoluteWidth: function(relativeWidth) {
    	return Math.ceil(this.getContainerWidth() * relativeWidth);
    }
    
    , convertToAbsoluteX: function(relativeX) {
    	return this.convertToAbsoluteWidth(relativeX);
    }
    
    , convertToAbsoluteHeight: function(relativeHeight) {
    	return Math.ceil(this.getContainerHeight() * relativeHeight);
    }
    
    , convertToAbsoluteY: function(relativeY) {
    	return this.convertToAbsoluteHeight(relativeY);
    }
    
    /**
     * @method
     * Returns the region received as argument with all measures converted from relative unit (i.e. %)) to absolute unit (i.e. %). The 
     * original object is not modified
     * 
     * @param {Objcet} relativeRegion The region to convert in relative units
     * @param {Number} relativeRegion.x The region x position in percentage
     * @param {Number} relativeRegion.y The region y position in percentage
     * @param {Number} relativeRegion.width The region width in percentage
     * @param {Number} relativeRegion.height The region height in percentage   
     * 
     * @return {Object} The region with all measure express in absolute units (i.e. px)
     */
    , convertToAbsoluteRegion: function(relativeRegion) {
    	var absoluteRegion = {};
    	
    	Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: IN");
    	
    	Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: Input relative region is equal to [" + Sbi.toSource(relativeRegion) + "]");
    	
    	if(Sbi.isNotValorized(relativeRegion)) {
    		Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: Input parameter [relativeRegion] is not defined");
    		Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: OUT");
    		return null;
    	}
    	
    	if(Sbi.isValorized(relativeRegion.width)) {
    		absoluteRegion.width = this.convertToAbsoluteWidth(relativeRegion.width);
    	} else {
    		Sbi.warn("[WidgetContainer.convertToAbsoluteRegion]: attribute [width] is not defined in the region to convert");
    	}
    	
    	if(Sbi.isValorized(relativeRegion.height)) {
    		absoluteRegion.height = this.convertToAbsoluteHeight(relativeRegion.height);
    	} else {
    		Sbi.warn("[WidgetContainer.convertToAbsoluteRegion]: attribute [height] is not defined in the region to convert");
    	}
    	
    	if(Sbi.isValorized(relativeRegion.x)) {
    		absoluteRegion.x = this.convertToAbsoluteX(relativeRegion.x);
    	} else {
    		Sbi.warn("[WidgetContainer.convertToAbsoluteRegion]: attribute [x] is not defined in the region to convert");
    	}
    	
    	
    	if(Sbi.isValorized(relativeRegion.y)) {
    		absoluteRegion.y = this.convertToAbsoluteY(relativeRegion.y);
    	} else {
    		Sbi.warn("[WidgetContainer.convertToAbsoluteRegion]: attribute [y] is not defined in the region to convert");
    	}
    	
    	
    	Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: Output absolute region is equal to [" + Sbi.toSource(absoluteRegion) + "]");
    	
    	Sbi.trace("[WidgetContainer.convertToAbsoluteRegion]: OUT");
    	
    	return absoluteRegion;
    }
    
    , convertToRelativeWidth: function(absoluteWidth) {
    	var relativeWidth =  absoluteWidth / this.getContainerWidth();
    	relativeWidth = relativeWidth.toFixed(2);
    	return relativeWidth;
    }
    
    , convertToRelativeX: function(absoluteX) {
    	return this.convertToRelativeWidth(absoluteX);
    }
    
    , convertToRelativeHeight: function(absoluteHeight) {
    	var relativeHeight =  absoluteHeight / this.getContainerHeight();
    	relativeHeight = relativeHeight.toFixed(2);
    	return relativeHeight;
    }
    
    , convertToRelativeY: function(absoluteY) {
    	return this.convertToRelativeHeight(absoluteY);
    }
    
    , convertToRelativeRegion: function(absoluteRegion) {
    	var relativeRegion = {};
    	
    	relativeRegion.width = this.convertToRelativeWidth(absoluteRegion.width);
    	relativeRegion.height = this.convertToRelativeHeight(absoluteRegion.height);
    	relativeRegion.x = this.convertToRelativeX(absoluteRegion.x);
    	relativeRegion.y = this.convertToRelativeY(absoluteRegion.y);
    	
    	return relativeRegion;
    }
   
    /**
     * @method
     * 
     * @return  The default region of the container to  which all new widgets will 
     * be added if not explicitly specified otherwise
     */
    , getDefaultRegion: function() {
    	var r = Ext.apply({}, this.defaultRegion || {});
    	Sbi.trace("[WidgetContainer.getDefaultRegion]: default region is equal to: [" + Sbi.toSource(r) + "]");
    	return r;
    }
    
    , addComponent: function(widget, layoutConf) {
    	
    	Sbi.trace("[WidgetContainer.addComponent]: IN");
    	
    	var componentConf = {};
    	if(widget) {
    		Sbi.trace("[WidgetContainer.addComponent]: add a component with an alredy embedded widget");
    		componentConf.widget = widget;
    	}
    	
    	if( Sbi.isNotValorized(layoutConf) ) {
    		Sbi.trace("[WidgetContainer.addComponent]: input parameter [layoutConf] is not defined");
    		layoutConf = {};
    	}
    	if( Sbi.isNotValorized(layoutConf.region) ) {
    		Sbi.trace("[WidgetContainer.addComponent]: attribute [region] of input parameter [layoutConf] is not defined");
    		layoutConf.region = this.getDefaultRegion();
    	}
    	
    	layoutConf.region = this.convertToAbsoluteRegion(layoutConf.region);
    	Sbi.trace("[WidgetContainer.addComponent]: the new component will be added to region: [" + Sbi.toSource(layoutConf.region) + "]");
    	
    	Ext.apply(componentConf, layoutConf);
    	var component = new Sbi.cockpit.core.WidgetContainerComponent(componentConf);
    	
    	component.on('move', this.onComponentMove, this);
    	component.on('resize', this.onComponentResize, this);
    	component.on("performaction", this.onComponentAction, this);
    	
    	this.components.add(component.getId(), component);
    	component.setParentContainer(this);
    	
    	if(widget) {
    		widget.setParentComponent(component);
    	}
    	
    	component.show();
    	
    	Sbi.trace("[WidgetContainer.addComponent]: OUT");
    	
    	return component;
    }
    
    , getComponents: function() {
    	return this.components.getRange();
    }
    
 
    
//    , onCloseWidgetEditorWizard: function(){
//    	this.widgetEditorWizard.resetState();
//    	this.widgetEditorWizard.hide();
//    }
 
    , getConfiguration: function(){
    	Sbi.trace("[WidgetContainer.getConfiguration]: IN");

    	var conf = {};
    	conf.widgets = [];
    	
    	var components = this.components.getRange();
    	for(var i = 0; i < components.length; i++) {
    		if(components[i].isNotEmpty()) {
    			conf.widgets.push( components[i].getWidgetConfiguration() );
    		}
    	}
    	
    	Sbi.trace("[WidgetContainer.getConfiguration]: OUT");
    	
    	return conf;
    }
    
    , onComponentMove: function(component){

    }
    
    , onComponentResize: function(component){

    }
    
    , onComponentAction: function(component, action) {
    	Sbi.trace("[WidgetContainer.onComponentAction]: IN");
		
    	if(!component) {
    		Sbi.warn("[WidgetContainer.onComponentAction]: component not defined");
    	}
    	
    	if(action === 'showEditor') {
			this.onShowWidgetEditorWizard(component);
		} else if(action === 'showConfiguration') {
			this.onShowWidgetConfiguration(component);
		} else {
			Sbi.warn("[WidgetContainer.onComponentAction]: action [" + action + "] not recognized");
		}
		Sbi.trace("[WidgetContainer.onComponentAction]: OUT");
	}
    
    , onShowWidgetConfiguration: function(component) {
    	this.showWidgetConfiguration(component);
    } 
    
    , onShowWidgetEditorWizard: function(component) {
    	this.showWidgetEditorWizard(component);
    }
}); 