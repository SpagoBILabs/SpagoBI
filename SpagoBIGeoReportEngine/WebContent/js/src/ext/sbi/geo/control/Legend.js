/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Functions
  * 
  *  [list]
  * 
  * 
  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

/** 
 * @requires OpenLayers/Control.js
 * @requires OpenLayers/BaseTypes.js
 * @requires OpenLayers/Events.js
 */

/**
 * Class: Sbi.geo.control.Legend
 * Create an overview map to display the extent of your main map and provide
 * additional navigation control.  Create a new overview map with the
 * <Sbi.geo.control.Legend> constructor.
 *
 * Inerits from:
 *  - <OpenLayers.Control>
 */

Ext.ns("Sbi.geo.control");

Sbi.geo.control.Legend = OpenLayers.Class(OpenLayers.Control, {
	
	 /**
     * Property: TYPE
     * {String} The TYPE of the control 
     * (values should be OpenLayers.Control.TYPE_BUTTON, 
     *  OpenLayers.Control.TYPE_TOGGLE or OpenLayers.Control.TYPE_TOOL)
     */
//	TYPE: OpenLayers.Control.TYPE_BUTTON,

    /**
     * Property: element
     * {DOMElement} The DOM element that contains the overview map
     */
    element: null,
    
    /**
     * APIProperty: ovmap
     * {<OpenLayers.Map>} A reference to the overview map itself.
     */
    ovmap: null,

    /**
     * APIProperty: size
     * {<OpenLayers.Size>} The overvew map size in pixels.  Note that this is
     * the size of the map itself - the element that contains the map (default
     * class name olControlSbiLegendMapElement) may have padding or other style
     * attributes added via CSS.
     */
    size: new OpenLayers.Size(35, 35),
        
    /**
     * APIProperty: mapOptions
     * {Object} An object containing any non-default properties to be sent to
     * the overview map's map constructor.  These should include any non-default
     * options that the main map was constructed with.
     */
    mapOptions: null,

    /** 
     * Property: position
     * {<OpenLayers.Pixel>} 
     */
    position: null,
    
    /**
     * popup windows for share the map url
     * */
    shareMapWindow: null,
    
    /**
     * Property: action clicked from the user
     * */
    action: null,
    
    /**
     * Property: legend configuration
     * */
    legendPanelConf: {},
    /**
     * Property: panel with the legend
     * */
    legendControlPanel: null,
    
    name: "legend",

    parentInitialize: function (options) {
    	Sbi.trace("[Legend.parentInitialize] : IN");
    	
        // We do this before the extend so that instances can override
        // className in options.
        this.displayClass = "map-tools"; 
        //"olControlLegend";
        //"olControlMousePosition"; 
        //this.CLASS_NAME.replace("OpenLayers.", "ol").replace(/\./g, "");
        
        OpenLayers.Util.extend(this, options);
        
        this.events = new OpenLayers.Events(this, null, this.EVENT_TYPES);
        if(this.eventListeners instanceof Object) {
            this.events.on(this.eventListeners);
        }
        if (this.id == null) {
            this.id = OpenLayers.Util.createUniqueID(this.CLASS_NAME + "_");
        }
        Sbi.trace("[Legend.parentInitialize] : OUT");
    },
    
    /**
     * Constructor: Sbi.geo.control.Legend
     * Create a new options map
     *
     * Parameters:
     * object - {Object} Properties of this object will be set on the overview
     * map object.  Note, to set options on the map object contained in this
     * control, set <mapOptions> as one of the options properties.
     */
    initialize: function(options) {  
        
    	Sbi.trace("[Legend.initialize] : IN");
    	
    	Sbi.trace("[Legend.initialize] : options are equal to [" + Sbi.toSource(options) + "]");    	
    	
    	//add theme file     	
//        var cssNode = document.createElement('link');
//        cssNode.setAttribute('rel', 'stylesheet');
//        cssNode.setAttribute('type', 'text/css');
//        cssNode.setAttribute('href', './css/standard.css');
//        document.getElementsByTagName('head')[0].appendChild(cssNode);
//               
//        this.layers = [];
//        this.handlers = {};
        
    	this.parentInitialize(options);
    	//OpenLayers.Control.prototype.initialize.apply(this, [options]);
    	
    	if (this.div == null) {
    		Sbi.trace("[Legend.initialize] : div is null");
    	} else {
    		Sbi.trace("[Legend.initialize] : div is not null");
    	}
    	
        
        Sbi.trace("[Legend.initialize] : OUT");
    },
    
    /**
     * APIMethod: destroy
     * Deconstruct the control
     */
    destroy: function() {
        if (!this.mapDiv) { // we've already been destroyed
            return;
        }
        this.ovmap.destroy();
        this.ovmap = null;
        
        this.element.removeChild(this.mapDiv);
        this.mapDiv = null;

        this.div.removeChild(this.element);
        this.element = null;

        OpenLayers.Control.prototype.destroy.apply(this, arguments);    
    },

    parentDraw: function (px) {
    	Sbi.trace("[Legend.parentDraw] : IN");
        
    	if (this.div == null) {
    		
    		Sbi.trace("[Legend.parentDraw] : div is null");
    		 
            this.div = OpenLayers.Util.createDiv(this.id);
            this.div.className = this.displayClass;
            
            if (!this.allowSelection) {
                this.div.className += " olControlNoSelect";
                this.div.setAttribute("unselectable", "on", 0);
                this.div.onselectstart = function() { return(false); }; 
            }    
            if (this.title != "") {
                this.div.title = this.title;
            }
        } else {
        	Sbi.trace("[Legend.parentDraw] : div is not null");
        }
    	
    	Sbi.trace("[Legend.parentDraw] : div calss is equal to [" + this.displayClass + "]");
    	
        if (px != null) {
            this.position = px.clone();
        }
        this.moveTo(this.position);
        
        Sbi.trace("[Legend.parentDraw] : OUT");
        
        return this.div;
    },
    
    /**
     * Method: draw
     * Render the control in the browser.
     */    

    draw: function(px) {    	
    	Sbi.trace("[Legend.draw] : IN");
    	
//    	var x = this.map.size.w*2;
//    	var y =  this.map.size.h+100;
//    	this.position = new OpenLayers.Pixel(x, y);
    	
    	//OpenLayers.Control.prototype.draw.apply(this, arguments);
    	this.parentDraw(px);
    	
    	
    	
        // create overview map DOM elements
        this.createContents();
    	
        
        Sbi.trace("[Legend.draw] : OUT");
        
        return this.div;
    },
   
   
    
    /**
     * Method: createElementsAction
     * Defines the action elements on the map
     */
    createContents: function(){

    	// create main legend element
        this.legendElement = document.createElement('div');
        this.legendElement.id = 'Legend'; //OpenLayers.Util.createUniqueID('Legend');   
        this.legendElement.className = 'map-tools-element legend';
        
        // create legend popup window
        this.legendContentElement = document.createElement('div');
        this.legendContentElement.id = 'LegendContent'; // OpenLayers.Util.createUniqueID('LegendContent');   
        this.legendContentElement.className = "tools-content overlay"; 
        
        var legendContentCloseBtnElement = document.createElement('span');
        legendContentCloseBtnElement.className = "btn-close";
        this.legendContentElement.appendChild(legendContentCloseBtnElement);
        OpenLayers.Event.observe(legendContentCloseBtnElement, "click", 
	    		OpenLayers.Function.bindAsEventListener(this.closeLegend, this, 'close'));
       
        var legendContentBodyElement = document.createElement('div');
        legendContentBodyElement.id = 'LegendBody'; // OpenLayers.Util.createUniqueID('LegendContent');   
        this.legendContentElement.appendChild(legendContentBodyElement);
        
        // create legend button
        var	legendButtonElement = document.createElement('span');
        legendButtonElement.className = 'icon';
        OpenLayers.Event.observe(legendButtonElement, "click", 
	    		OpenLayers.Function.bindAsEventListener(this.openLegend, this));
    	
	          
        // put everythings together
        this.legendElement.appendChild(legendButtonElement);
	    this.legendElement.appendChild(this.legendContentElement); 
	   
        this.div.appendChild(this.legendElement);
    },
    
    
    /**
     * Method: execClick
     * Executes the specific action
     */
    openLegend: function(el){
    	this.legendContentElement.style.height = '200px';
    	this.legendContentElement.style.width = '180px';
    	this.legendContentElement.style.display = 'block';
    	this.legendContentElement.opened = true;
    
    	
    	this.initLegendControlPanel();
    	var pippo = new Ext.Window({
            layout      : 'fit',
	        width		: 700,
	        height		: 350,
            closeAction :'destroy',
            plain       : true,
//	            title		: OpenLayers.Lang.translate('sbi.tools.catalogue.measures.window.title'),
            title		: 'test for the legend...',
            items       : [this.legendControlPanel]
		});
		
    	//pippo.show();
    },
    
    closeLegend: function(el){
    	this.legendContentElement.style.height = '0px';
    	this.legendContentElement.style.width = '0px';
    	this.legendContentElement.style.display = 'none';
    	this.legendContentElement.closed = true;
    },
    
    /**
     * Method: createMap
     * Construct the map that this control contains
     */
    createMap: function() {
        // create the overview map
        var options = OpenLayers.Util.extend(
                        {controls: [], maxResolution: 'auto', 
                         fallThrough: false}, this.mapOptions);

        this.ovmap = new OpenLayers.Map(this.mapDiv, options);
        
        // prevent ovmap from being destroyed when the page unloads, because
        // the SbiLegendMap control has to do this (and does it).
//        OpenLayers.Event.stopObserving(window, 'click', this.ovmap.unloadDestroy);
        
    },
    
	initLegendControlPanel: function() {

		this.legendPanelConf = null;
		this.legendControlPanel = new Ext.Panel(Ext.apply({
	           title: LN('sbi.geo.legendpanel.title'),
	           collapsible: true,
	           bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
	           height: 180,
	           autoScroll: true,
	           html: '<center id="myChoroplethLegendDiv"></center>'
	     },this.legendPanelConf));
					

	},
    

    CLASS_NAME: 'Sbi.geo.control.Legend'
});
