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
       
    	OpenLayers.Control.prototype.initialize.apply(this, [options]);
    	// ovveride the main div class automatically generated 
    	// by parent's initialize method
    	this.displayClass = "map-tools"; 
    	this.id = "MapTools"; 
    	
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

        
    /**
     * Method: draw
     * Render the control in the browser.
     */    

    draw: function(px) {    	
    	Sbi.trace("[Legend.draw] : IN");
    	
    	this.div = document.getElementById("MapTools");
    	if(this.div != null) {
    		Sbi.trace("[Legend.draw] : a div with id equal to [MapTools] already exist");
    	} else {
    		Sbi.trace("[Legend.draw] : a div with id equal to [MapTools] does not exist");
    	}
    	
    	OpenLayers.Control.prototype.draw.apply(this, arguments);
    	//this.parentDraw(px);
    	
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
    

    CLASS_NAME: 'Sbi.geo.control.Legend'
});
