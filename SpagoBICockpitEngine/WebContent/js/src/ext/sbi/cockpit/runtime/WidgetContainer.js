/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.runtime");

Sbi.cockpit.runtime.WidgetContainer = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	var defaultSettings = {

	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widgetPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widgetPanel);
	}	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.regions = {};

	// constructor
	Sbi.cockpit.runtime.WidgetContainer.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.cockpit.runtime.WidgetContainer
 * @extends Ext.util.Observable
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.runtime.WidgetContainer, Sbi.cockpit.runtime.Widget, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.runtime.WidgetManager} widgetManager
     * The container that manages the all the widgets rendered within this panel
     */
	widgetManager: null

  /**
   * @property { Sbi.cockpit.editor.WidgetEditorWizardPanel} wizard
   * The wizard that manages the single widget definition
   */
  , wizard: null
	 
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
			config.widgetManager = new Sbi.cockpit.runtime.WidgetManager({storeManager: config.storeManager});
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
	
    , addWidget: function(widget, region) {	
    	
//    	alert("Widget: " + (widget instanceof Sbi.cockpit.runtime.Widget));
//    	alert("DummyWidget: " + (widget instanceof Sbi.cockpit.widgets.dummy.DummyWidget));
//    	alert("TableWidget: " + (widget instanceof Sbi.cockpit.widgets.table.TableWidget));
		Sbi.trace("[WidgetContainer.addWidget]: IN");
    	
    	if(!widget) {
    		// what we do?
    	}
    	
    	if( (widget instanceof Sbi.cockpit.runtime.Widget) === false) {
    		if(typeof widget === 'object' && !(widget instanceof Ext.util.Observable) === false) {
    			// ok is a conf object. Use a factory method to instatiate a new widget object
    		} else {
    			// it's not a valid config object
    		}
    	} 
    	
		this.widgetManager.register(widget);
		this.setWidgetRegion(widget, region);
		var containerComponent = this.renderWidget(widget);
		// update region object properly when container component is moved or resized
		containerComponent.on('move', this.onMoveComponent, this);
		containerComponent.on('resize', this.onResizeComponent, this);
		widget.setParentContainer(this);
		
		Sbi.trace("[WidgetContainer.addWidget]: OUT");
	}
    
    , getWidgetRegion: function(widget) {
    	return this.regions[widget.id];
    }
    
    , setWidgetRegion: function(widget, region) {
    	this.regions[widget.id] = region;
    }
    
    , getWidgetManager: function() {
    	return this.widgetManager;
    }
    
    /**
     * TODO: integrate ace-extjs editor to have the configuration not only pretty printed 
     * but also highlighted
     */
    , showWidgetConfiguration: function(widget) {
    	
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
            title: "Widget [" + widget.id + "] configuration",
            items: new Ext.form.TextArea({
            	border: false
            	, value: confStr
                , name: 'configuration'
            }),

            buttons: [
//          {
//            	text:'Copy to clipboard',
//              	handler: function(){
//                		...
//            		}
//          },
            {
            	text: 'Close',
                handler: function(){
                	win.close();
                }
            }]
        });
    	win.show();
    }
    
    , showWidgetEditor: function(widget) {    	
    	var config = {};
    	config.widgetManager = this.getWidgetManager();
    	config.widget = widget;
    	
    	var widgetWizard = new Sbi.cockpit.editor.WidgetEditorWizardPanel(config);
    	widgetWizard.on('close', this.closeWizard, this);
    	widgetWizard.on('confirm', this.defineTemplate, this);
    	
    	this.wizard = new Ext.Window({
    		id: 'wizard',
            layout:'fit',
            width:1000, //800,
            height: 510,
            plain: true,
            modal: true,
            title: "Widget [" + widget.id + "] editor",
    		items: widgetWizard
        });
    	
    	this.wizard.show();
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , onRender: function(ct, position) {	
    	Sbi.trace("[WidgetContainer.onRender]: IN");
    	
		Sbi.cockpit.runtime.WidgetContainer.superclass.onRender.call(this, ct, position);
	
		this.clearContent();
		this.renderContent();
		Sbi.trace("[WidgetContainer.onRender]: OUT");
	}
     
    , clearContent: function() {
//    	 this.items.each( function(item) {
// 			this.items.remove(item);
// 	        item.destroy();           
// 	    }, this); 
    }
    
    , renderContent: function() {
    	var widgets = this.widgetManager.getWidgets();
		widgets.each(function(widget, index, length) {
			this.renderWidget(widget);
		}, this);	
    }
    
    , renderWidget: function(widget) {
    	
    	Sbi.trace("[WidgetContainer.renderWidget]: IN");
    	
    	var componentConf = {widget: widget};
    	
    	var region = this.regions[widget.id];
    	if(region == undefined) {
    		region = {
    			width : 0.5
    	    	, height : 0.5
    	    	, x : '50%'
    	    	, y: '50%'
    		};
    		this.regions[widget.id] = region;
    	}
    	
    	Sbi.trace("[WidgetContainer.renderWidget]: region is equal to: [" + Sbi.toSource(region) + "]");
    	
    	Ext.apply(componentConf, region);
    	var vpSize = Ext.getBody().getViewSize();
    	componentConf.width = Math.ceil(vpSize.width * componentConf.width);
    	componentConf.height = Math.ceil(vpSize.height * componentConf.height);
    	
    	var component = new Sbi.cockpit.runtime.WidgetContainerComponent(componentConf);
    	component.on("performaction", function(component, widget, action) {
    		if(action === 'showEditor') {
    			this.showWidgetEditor(widget);
    		} else if(action === 'showConfiguration') {
    			this.showWidgetConfiguration(widget);
    		}
    	}, this);
    	this.components = this.components || new Array();
    	this.components.push(component);
    	component.show();
    	
    	Sbi.trace("[WidgetContainer.renderWidget]: OUT");
    	
    	return component;
    }
    
    , onShowWidgetConfiguration: function(widget) {
    	this.showWidgetConfiguration(widget);
    	//Ext.Msg.alert('Message', 'The CONFIG tool was clicked.');
    } 
    
    , onShowWidgetEditor: function(widget) {
    	this.showWidgetEditor(widget);
    	//Ext.Msg.alert('Message', 'The CONFIG tool was clicked.');
    } 
    
    , closeWizard: function(){
    	this.wizard.close();
    }
    
    , defineTemplate: function(wEditor){
    	Sbi.trace("[WidgetContainer.defineTemplate]: IN");

    	//gets the containerComponent to update  (is Dummy for default)
    	var myWidget = this.components[0];
    	var newWidget = myWidget;
    	//Recupero della tipologia : 
    	//si potrebbe ottimizzare  sfruttando l'evento drug dalla palette?
    	var editor = wEditor.getComponent(1).getComponent(1);
    	var widgetEditorMainPanel = editor.getComponent(0);
    	var designer = widgetEditorMainPanel.getComponent(0);  
//
//	    if (designer instanceof Sbi.cockpit.widgets.table.TableWidgetDesigner){
//	    	//gets informations about table widget
//	    	var state = designer.getFormState();
////	    	newWidget = new Sbi.cockpit.widgets.table.TableWidget(); NO PERCHé E? IL  RUNTIME
//	    	if (newWidget.setFormState)
//	    		newWidget.setFormState(state);
//	    	
//	    }
	    
//	    myWidget.setWidget(newWidget);
	    
//	    var template = (typeof JSON === 'object')
//		? JSON.stringify(myWidget.getCustomConfiguration(), null, 2)
//		: Ext.util.JSON.encode(myWidget.getCustomConfiguration());
    	
		var state = {};
    	state.fields =  designer.getFormState().visibleselectfields || [];
    	state.type = 'Table';
		var template = (typeof JSON === 'object')
		? JSON.stringify(state, null, 2)
		: Ext.util.JSON.encode(state);
		
		alert(template);
	    	
	    Sbi.trace("[WidgetContainer.defineTemplate]: OUT");
	    return template;
    }
    
    , onMoveComponent: function(c){
    	//refresh xy informations of region obj
    	Sbi.trace("[WidgetContainer.onMoveComponent]: IN");

    	var container = c.getWidget().getParentContainer();		
    	var r = container.getRegion();
    	
		r.x = c.getWidget().getPosition()[0];
		r.y = c.getWidget().getPosition()[1];
		
		this.setWidgetRegion(c.getWidget(),r);
		
		Sbi.trace("[WidgetContainer.onMoveComponent]: OUT");
    	
    }
    
    , onResizeComponent: function(c){
    	//refresh size informations of region obj
    	Sbi.trace("[WidgetContainer.onResizeComponent]: IN");
    	
    	var r =  c.getWidget().getParentContainer().getRegion();
    	r.width = c.getWidget().getWidth();
    	r.height = c.getWidget().getHeight();
//    	r.width = c.width;
//    	r.height = c.height;
    	
    	if (Sbi.settings.cockpit && Sbi.settings.cockpit.layout && 
    			Sbi.settings.cockpit.layout.useRelativeDimensions == true){
    		//get realtive dimensions if necessary
    		var vpSize = Ext.getBody().getViewSize();
	    	var newW = ((100*c.getWidget().getWidth()) / vpSize.width)/100;
			var newH = ((100*c.getWidget().getHeight()) / vpSize.height)/100;
	    	r.width = Ext.util.Format.number(newW, '0.00');
	    	r.height =Ext.util.Format.number(newH, '0.00');
    	}
    	
		this.setWidgetRegion(c.getWidget(),r);
		
		Sbi.trace("[WidgetContainer.onResizeComponent]: OUT");
    }
}); 