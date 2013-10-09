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
        
    	//add theme file     	
        var cssNode = document.createElement('link');
        cssNode.setAttribute('rel', 'stylesheet');
        cssNode.setAttribute('type', 'text/css');
        cssNode.setAttribute('href', './css/standard.css');
        document.getElementsByTagName('head')[0].appendChild(cssNode);
               
        this.layers = [];
        this.handlers = {};
        OpenLayers.Control.prototype.initialize.apply(this, [options]);
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
//    draw: function(px) {
    draw: function() {    	
//    	alert('this.map.size: ' + this.map.size);

    	var x = this.map.size.w*2;
    	var y =  this.map.size.h+100;
    	this.position = new OpenLayers.Pixel(x, y);
    	
    	OpenLayers.Control.prototype.draw.apply(this, arguments);
//        px = this.position;
//        
//        var sz = new OpenLayers.Size(18,18);
//        var centered = new OpenLayers.Pixel(px.x+sz.w/2, px.y);

        // create overview map DOM elements
        this.createButton();
        
//        this.update();

        return this.div;
    },
   
    
    /**
     * Method: createElementsAction
     * Defines the action elements on the map
     */
    createButton: function(){
        this.element = document.createElement('div');
        this.element.className = 'map-tools';

        this.mapDiv = document.createElement('div');
      
        this.mapDiv.id = OpenLayers.Util.createUniqueID('SbiLegendMap'); 
        this.mapDiv.className = 'map-tools-element legend';
        
        this.mapDivContent = document.createElement('div');
        this.mapDivContent.className = "tools-content overlay"; 
        var mapDivContentSpan = document.createElement('span');
        mapDivContentSpan.className = "btn-close";
        this.mapDivContent.appendChild(mapDivContentSpan);
        this.mapDivContent.id = OpenLayers.Util.createUniqueID('legendContent');   
        OpenLayers.Event.observe(mapDivContentSpan, "click", 
	    		OpenLayers.Function.bindAsEventListener(this.closeLegend, this, 'close'));
       
        var divDet = document.createElement('div'),
        	divDetSpan = document.createElement('span');

        divDetSpan.className = 'icon';
        divDet.appendChild(divDetSpan);

    	this.mapDiv.id = OpenLayers.Util.createUniqueID('legend');   
	          
	    this.mapDiv.appendChild(divDet);

	    this.mapDiv.appendChild(this.mapDivContent); 
	    OpenLayers.Event.observe(divDetSpan, "click", 
	    		OpenLayers.Function.bindAsEventListener(this.openLegend, this));
	    
	    
        this.element.appendChild(this.mapDiv);  
        
        this.div.appendChild(this.element);
    },
    
    
    /**
     * Method: execClick
     * Executes the specific action
     */
    openLegend: function(el){
    	this.mapDivContent.style.height = '200px';
    	this.mapDivContent.style.width = '180px';
    	this.mapDivContent.style.display = 'block';
    	this.mapDivContent.opened = true;
    
    	
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
		
    	pippo.show();
    },
    
    closeLegend: function(el){
    	this.mapDivContent.style.height = '0px';
    	this.mapDivContent.style.width = '0px';
    	this.mapDivContent.style.display = 'none';
    	this.mapDivContent.closed = true;
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
