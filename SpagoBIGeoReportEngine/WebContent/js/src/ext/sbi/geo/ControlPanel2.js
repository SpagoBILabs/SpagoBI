/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 
Ext.ns("Sbi.geo");


/**
 * Every time you create a new class add it to the following files:
 *  - importSbiJS.jspf
 *  - ant-files/SpagoBI-2.x-source/SpagoBIProject/ant/build.xml
 */
Sbi.geo.ControlPanel2 = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	// init properties...
	var defaultSettings = {
		// set default values here
		title       : LN('sbi.geo.controlpanel.title'),
		region      : 'east',
		split       : true,
		width       : 380,
		collapsible : true,
		collapsed   : false,
		margins     : '3 0 3 3',
		cmargins    : '3 3 3 3',
		autoScroll	 : true
	};
	
	if (Sbi.settings && Sbi.settings.geo && Sbi.settings.geo.ControlPanel2) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.geo.ControlPanel2);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	// init events...
	//this.addEvents();
	
	this.initServices();
	this.init();

	c = Ext.apply(c, {
	     items: this.innerPanel 
	});
	
	// constructor
    Sbi.geo.ControlPanel2.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.geo.ControlPanel2
 * @extends Ext.Panel
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.geo.ControlPanel2, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
   
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
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - none
	 */
	, initServices: function() {
//		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
//		
//		this.services = this.services || new Array();
//		
//		this.services['exampleService'] = this.services['exampleService'] || Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXAMPLE_ACTION'
//			, baseParams: params
//		});	
	}


	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.innerPanel = new Ext.Panel({
			height: 900,
			html: ' <main class="main main-map" id="main"> ' +
					    '<div class="panel">' +
					    	'<form class="panel-form" action="#" method="post">' +
					            '<div class="scroll" id="scroll">' +
					               '<div class="scroll-content">' +
					                    '<div class="map-description">' +
					                        '<h1 class="titleButton">Numero di letti nel turismo</h1>' +
					                        '<p>Variazione del numero di letti nelle strutture ricettive dell\'Alto Adige tra il 2001 e il 2011.</p>' +
					                		'<p id="author" class="published">Pubblicata da <a id="authorButton" class="authorButton" href="#">ASTAT</a> <span class="separator">/</span> <a href="#" class="feedback">invia feedback</a></p>' +
					                    '</div>' +
					                    '<ul class="map-type">' +
					                    	'<li class="map-zone active"><a href="#">Mappa a <span>zone</span><span class="arrow"></span></a></li>' +
					                        '<li class="map-comparation"><a href="#">Mappa di <span>comparazione</span></a></li>' +
					                        '<li class="map-point"><a href="#">Mappa <span>puntiforme</span></a></li>' +
					                        '<li class="map-heat last"><a href="#">Mappa di <span>calore</span></a></li>' +
					                    '</ul>' +
					                    '<div class="indicators">' +
					                    	'<h2>Indicatori</h2>' +
					                        '<ul class="group">' +
					                        	'<li class="first">' +
					                            	'<span class="button"><a href="#" class="tick"></a>Numero letti 2001<span class="arrow"></span></span>' +
					                                '<div class="slider">' +
					                                	'<p>Dati relativi al numero di letti presenti nelle strutture ricettive dell\'Alto Adige.</p>' +
					                                	'<p class="published">Pubblicata da <a href="#">ASTAT</a> <span class="separator">/ aggiornati il 05/12/12</span></p>' +
					                                	'<div class="select">' +
					                                    	'<label for="select-1">Anno</label>' +
					                                        '<select id="select-1" name="select-1">' +
					                                        	'<option value="1">Tutti</option>' +
					                                            '<option value="2">Hotel</option>' +
					                                            '<option value="3">Agritur</option>' +
					                                        '</select>' +
					                                    '</div>' +
					                                '</div>	' +
					                            '</li>' +
					                            '<li class="disabled">' +
					                            	'<span class="button"><a href="#" class="tick"></a>Numero letti 2005<span class="arrow"></span></span>' +
					                                '<div class="slider">' +
					                                	'<p>Dati relativi al numero di letti presenti nelle strutture ricettive dell\'Alto Adige.</p>' +
					                                	'<p class="published">Pubblicata da <a href="#">ASTAT</a> <span class="separator">/ aggiornati il 05/12/12</span></p>' +
					                                	'<div class="select">' +
					                                    	'<label for="select-2">Anno</label>' +
					                                        '<select id="select-2" name="select-2">' +
					                                        	'<option value="1">Tutti</option>' +
					                                            '<option value="2">Hotel</option>' +
					                                            '<option value="3">Agritur</option>' +
					                                        '</select>' +
					                                    '</div>' +
					                                '</div>	' +
					                            '</li>' +
					                            '<li class="locked last">' +
					                            	'<span class="button"><a href="#" class="tick"></a>Numero letti 2011<span class="arrow"></span></span>' +
					                                '<div class="slider">' +
					                                	'<p>Dati relativi al numero di letti presenti nelle strutture ricettive dell\'Alto Adige.</p>' +
					                                	'<p class="published">Pubblicata da <a href="#">ASTAT</a> <span class="separator">/ aggiornati il 05/12/12</span></p>' +
					                                	'<div class="select">' +
					                                    	'<label for="select-3">Anno</label>' +
					                                        '<select id="select-3" name="select-3">' +
					                                        	'<option value="1">Tutti</option>' +
					                                            '<option value="2">Hotel</option>' +
					                                            '<option value="3">Agritur</option>' +
					                                        '</select>' +
					                                    '</div>' +
					                                '</div>' +
					                                '<span class="lock"></span>	' +
					                            '</li>' +
					                        '</ul>' +
					                        '<span class="btn-2">Aggiungi</span>' +
					                    '</div>' +
										'<div class="map-permissions">' +
					                    	'<div class="radio">' +
					                        	'<span class="label">Questa mappa Ã¨:</span>' +
					                            '<div class="radio-option checked">' +
					                            	'<input id="permissions-1" type="radio" name="permissions" value="1" />' +
					                                '<label for="permissions-1">Privata</label>' +
					                            '</div>' +
					                            '<div class="radio-option">' +
					                            	'<input id="permissions-2" type="radio" name="permissions" value="1" />' +
					                                '<label for="permissions-2">Pubblica</label>' +
					                            '</div>' +
					                        '</div>' +
					                    '</div>' +
					                '</div>' +
					            '</div>' +
					            '<!-- // Mapper new map' +
					                '<div class="panel-buttons-container">' +
					                    '<div class="panel-buttons">	' +
					                        '<input type="submit" class="btn-1" value="salva" />' +
					                    '</div>' +
					                '</div>' +
					            '-->' +
					            '<!-- // Mapper modify own map -->' +
					               '<div class="panel-buttons-container map-owner">' +
					                    '<div class="panel-buttons">' +
					                        '<a href="#" class="btn-2">Annulla</a>' +
					                        '<input type="submit" class="btn-1" value="Aggiorna" />' +
					                    '</div>' +
					                    '<p>salva <a href="#">nuova mappa</a></p>' +
					                '</div>' +
					           
					            '<!-- // Mapper modify sombody else map ' +
					            '<div class="panel-buttons-container">' +
					                '<div class="panel-buttons">' +
					                    '<a href="#" class="btn-2">Annulla</a>' +
					                    '<input type="submit" class="btn-1" value="Salva nuova mappa" />' +
					                '</div>' +
					            '</div>-->' +
					            '<div class="map-tools">' +
					                '<div class="map-tools-element legend">' +
					                	'<div class="tools-content overlay">' +
					                    	'<h3>Legenda</h3>' +
					                        '<img src="images/placeholders/img-legenda.jpg" alt=" " />' +
					                        '<span class="btn-close"></span>' +
					                    '</div>' +
					                	'<span class="icon"></span>' +
					                '</div>' +
					                '<div class="map-tools-element layers">' +
					                	'<div class="tools-content overlay">' +
					                    	'<h3>Livelli</h3>' +
					                        '<div class="select">' +
					                        	'<label for="cartografia">Cartografia</label>' +
					                            '<select name="cartografia" id="cartografia">' +
					                            	'<option value="cart-1">Google Maps</option>' +
					                                '<option value="cart-2">Map 2</option>' +
					                                '<option value="cart-3">Map 3</option>' +
					                            '</select>' +
					                        '</div>' +
					                        '<div class="check-group">' +
					                        	'<div class="checkbox">' +
					                            	'<input type="checkbox" name="check-1" id="check-1" />' +
					                                '<label for="check-1">Fermate bus</label>' +
					                            '</div>' +
					                            '<div class="checkbox right">' +
					                            	'<input type="checkbox" name="check-2" id="check-2" />' +
					                                '<label for="check-2">Comuni e città </label>' +
					                            '</div>' +
					                            '<div class="checkbox">' +
					                            	'<input type="checkbox" name="check-3" id="check-3" />' +
					                                '<label for="check-3">Strade</label>' +
					                            '</div>' +
					                            '<div class="checkbox right">' +
					                            	'<input type="checkbox" name="check-4" id="check-4" />' +
					                                '<label for="check-4">Icone</label>' +
					                            '</div>' +
					                            '<div class="checkbox">' +
					                            	'<input type="checkbox" name="check-5" id="check-5" />' +
					                                '<label for="check-5">Fiumi</label>' +
					                            '</div>' +
					                        '</div>' +
					                        '<span class="btn-close"></span>' +
					                    '</div>' +
					                	'<span class="icon"></span>' +
					                '</div>' +
					            '</div>' +
					        '</form>' +
					        '<ul class="panel-actions">' +
					        	'<li class="btn-toggle open first"><span></span></li>' +
					            '<li class="btn-print"><span>Print this map</span></li>' +
					            '<li class="btn-share"><span>Share this map</span></li>' +
					            '<li class="btn-download"><a href="#">Download this map</a></li>' +
					            '<li class="btn-favourite last"><a href="#">Make this map favourite</a></li>' +
					        '</ul>' +
					    '</div>' +
					'</main>'
		});
		
		this.innerPanel.on('render', function() {
			this.initInnerPannelCallbacks.defer(2000, this);
		}, this);
	}
	
	, initInnerPannelCallbacks: function() {
		//alert('initInnerPannelCallbacks');
		var el = Ext.get("authorButton");
		if(el && el !== null) {
			el.on('click', function() {
				alert('xxx');
			});
			//alert('Registered handler on element [authorButton]');
		} else {
			//alert('Impossible to find element [authorButton]');
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
	//this.addEvents(
	/**
     * @event eventone
     * Fired when ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Ext.Toolbar} ...
     */
	//'eventone'
	/**
     * @event eventtwo
     * Fired before ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Object} ...
     */
	//'eventtwo'
	//);	
});