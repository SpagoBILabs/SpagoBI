Ext.ns("Sbi.settings");

Sbi.settings.georeport = {
	
	georeportPanel: {
	
		controlPanelConf: {
			layerPanelEnabled: true
			, analysisPanelEnabled: true
			, legendPanelEnabled: true
			, logoPanelEnabled: true
			, earthPanelEnabled: false
		}	
	
		, toolbarConf: {
			enabled: true
			, zoomToMaxButtonEnabled: true
			, mouseButtonGroupEnabled: true
			, drawButtonGroupEnabled: false
			, historyButtonGroupEnabled: true
		}
	
	
			
		/**
		 * base map configuration's options passed to the constructor of OpenLayers Map Object 
		 * 
		 * @see http://dev.openlayers.org/docs/files/OpenLayers/Map-js.html#OpenLayers.Map.Constructor
		 */
	
		, baseMapOptions: {
	        
			/**
			 * {String} Set in the map options to override the default projection string this map - also set maxExtent, maxResolution, 
			 * and units if appropriate.  Default is �EPSG:4326�.
			 */
			projection: 'EPSG:900913',
	        
			/**
			 * {String} Requires proj4js support.Projection used by several controls to display data to user.  
			 * If this property is set, it will be set on any control which has a null displayProjection property at 
			 * the time the control is added to the map.
			 */
			displayProjection: 'EPSG:4326',
	        
			/**
			 * {String} The map units.  Defaults to �degrees�.  Possible values are �degrees� (or �dd�), �m�, �ft�, �km�, �mi�, �inches�.
			 */
			units: "m",
			
			/**
			 * {Float} Default max is 360 deg / 256 px, which corresponds to zoom level 0 on gmaps.  
			 * Specify a different value in the map options if you are not using a geographic projection and displaying the whole world.
			 */
	        maxResolution: 156543.0339,
	        
	        /**
	         * {Object} The maximum extent for the map.  Defaults to the whole world in decimal degrees (-180, -90, 180, 90).  
	         * Specify a different extent in the map options if you are not using a geographic projection and displaying the whole world.
	         */
	        
			maxExtent: {
				left: -20037508, 
				bottom: -20037508,
				right:20037508, 
				top: 20037508.34
			}  
			
		}
	
		
		/**
	 	 * configurations of layers that must be loaded at startup
	 	 * 
	 	 * layer configuration is an object composed by the following attributes ...
	 	 * 	- name {String} A name for the layer (can be localized)
	 	 * 	- url {String} Base url for the WMS (e.g.  http://wms.jpl.nasa.gov/wms.cgi) 
	 	 * 	- params {Object} An object with key/value pairs representing the GetMap query string parameters and parameter values. 
	 	 * 	- options {Ojbect} Hashtable of extra options to tag onto the layer 
	 	 * 
	 	 * for more informations see:
	 	 * 		http://dev.openlayers.org/releases/OpenLayers-2.7/doc/apidocs/files/OpenLayers/Layer/WMS-js.html#OpenLayers.Layer.WMS.OpenLayers.Layer.WMS
	     */ 
		
		, baseLayersConf: [
		    {
		    	type: 'WMS',
		    	name: "OpenLayers WMS",
     	        url: "http://labs.metacarta.com/wms/vmap0",
     	        params: {layers: 'basic'},
     	        options: {singleTile: false}, 
				enabled: true
     	    }, 
     	    {
     	    	type: 'WMS',
     	    	name: "NASA Global Mosaic",
     	    	url: "http://hypercube.telascience.org/cgi-bin/landsat7?",
     	    	params: {layers: "landsat7"},
     	    	options: {'isBaseLayer': true}, 
				enabled: false
     	    }, 
     	    {
     	    	type: 'WMS',
     	    	name: "Satellite",
     	    	url: "http://labs.metacarta.com/wms-c/Basic.py?",
     	    	params: {layers: 'satellite', format: 'image/png'}, 
				enabled: false
     	    }, 
     	    {
     	    	type: 'TMS',
     	    	name: 'OpenStreetMap',
     	    	url:  "http://tile.openstreetmap.org/",
     	    	options: {
     	    		type: 'png', 
     	    		//getURL: this.osm_getTileURL,
    		        displayOutsideMaxExtent: true
    		    }, 
				enabled: true
    		},
    		{
    			type: 'Google',
    			name: 'GoogleMap',
    			options: {'sphericalMercator': true}, 
				enabled: true
    		}, 
    		{
    			type: 'Google',
    			name: 'Google Satellite',
    			options: {type: G_SATELLITE_MAP, 'sphericalMercator': true, numZoomLevels: 22},
    			enabled: true
    		}, 
    		{
    			type: 'Google',
    			name: 'Google Hybrid',
    			options: {type: G_HYBRID_MAP, 'sphericalMercator': true}, 
				enabled: true
    		},
    		{
    			type: 'OSM', 
				enabled: false
    		}
    		
     	]
     	
     	/**
		 * Controls affect the display or behavior of the map.  They allow everything from panning and zooming 
		 * to displaying a scale indicator.
		 */
		, baseControlsConf: [
			
			/**
			 * The MousePosition control displays geographic coordinates of the mouse pointer, as it is moved about the map.
			 */
			{
				type: 'MousePosition', 
				enabled: true
			},
			
			/**
			 * The OverMap control creates a small overview map, useful to display the extent of a zoomed map and your main map and provide 
			 * additional navigation options to the User. 
			 * By default the overview map is drawn in the lower right corner of the main map.
			 */
			{
				type: 'OverviewMap', 
				enabled: true
			}, 
			
			/**
			 * The navigation control handles map browsing with mouse events (dragging, double-clicking, and scrolling the wheel)
			 */
			{
				type: 'Navigation', 
				enabled: true
			},
			
			/**
			 * The PanZoomBar is a visible control. By default it is displayed in the upper left corner of the map as 4 
			 * directional arrows above a vertical slider.
			 */
			{
				type: 'PanZoomBar', 
				enabled: false
			}	
		]     	
		
	}
};
// no overrides yet

/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * Singleton object that handle all errors generated on the client side
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */


Ext.ns("Sbi.exception.ExceptionHandler");

Sbi.exception.ExceptionHandler = function(){
	// do NOT access DOM from here; elements don't exist yet
 
    // private variables
 
    // public space
	return {
	
		init : function() {
			//alert("init");
		},
		
		
        handleFailure : function(response, options) {
        	
        	var errMessage = null;
        	if(response !== undefined) {        		
        		if(response.responseText !== undefined) {
        			var content = Ext.util.JSON.decode( response.responseText );
        			if(content.errors !== undefined && content.errors.length > 0) {
        				errMessage = '';
        				for(var i = 0; i < content.errors.length; i++) {
        					errMessage += content.errors[i].message + '<br>';
        				}
        			} 
        		} 
        		if(errMessage === null)	errMessage = 'An unspecified error occurred on the server side';
        	} else {
        		errMessage = 'Request has been aborted due to a timeout trigger';
        	}
        		
        	errMessage = errMessage || 'An error occurred while processing the server error response';
        	
        	Sbi.exception.ExceptionHandler.showErrorMessage(errMessage, 'Service Error');
       	
        },
        
        showErrorMessage : function(errMessage, title) {
        	var m = errMessage || 'Generic error';
        	var t = title || 'Error';
        	
        	Ext.MessageBox.show({
           		title: t
           		, msg: m
           		, buttons: Ext.MessageBox.OK     
           		, icon: Ext.MessageBox.ERROR
           		, modal: false
       		});
        },
        
        showWarningMessage : function(errMessage, title) {
        	var m = errMessage || 'Generic warning';
        	var t = title || 'Warning';
        	
        	Ext.MessageBox.show({
           		title: t
           		, msg: m
           		, buttons: Ext.MessageBox.OK     
           		, icon: Ext.MessageBox.WARNING
           		, modal: false
       		});
        }

	};
}();/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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
  * - Andrea Gioia (adrea.gioia@eng.it)
  */

Ext.ns("Sbi.qbe.commons");

Sbi.qbe.commons.Format = function(){
 
	return {
		/**
         * Cut and paste from Ext.util.Format
         */
        date : function(v, format){
			
		
		
		
			format = format || "m/d/Y";
			
			if(typeof format === 'string') {
				format = {
					dateFormat: format,
			    	nullValue: ''
				};
			}
			
			
            if(!v){
                return format.nullValue;
            }
            
            if(!(v instanceof Date)){
                v = new Date(Date.parse(v));
            }
          
            
            v = v.dateFormat(format.dateFormat);
         
            return v;
        }

        /**
         * Cut and paste from Ext.util.Format
         */
        , dateRenderer : function(format){
            return function(v){
                return Sbi.qbe.commons.Format.date(v, format);
            };
        }
        
        
        /**
         * thanks to Condor: http://www.extjs.com/forum/showthread.php?t=48600
         */
        , number : function(v, format)  {
    		
        	format = Ext.apply({}, format || {}, {
	    		decimalSeparator: '.',
	    		decimalPrecision: 2,
	    		groupingSeparator: ',',
	    		groupingSize: 3,
	    		currencySymbol: '',
	    		nullValue: ''
	    		
    		});

        	if(v === undefined || v === null) {
        		 return format.nullValue;
        	}
        	
        	if (typeof v !== 'number') {
    			v = String(v);
    			if (format.currencySymbol) {
    				v = v.replace(format.currencySymbol, '');
    			}
    			if (format.groupingSeparator) {
    				v = v.replace(new RegExp(format.groupingSeparator, 'g'), '');
    			}
    			if (format.decimalSeparator !== '.') {
    				v = v.replace(format.decimalSeparator, '.');
    			}
    			v = parseFloat(v);
    		}
    		var neg = v < 0;
    		v = Math.abs(v).toFixed(format.decimalPrecision);
    		var i = v.indexOf('.');
    		if (i >= 0) {
    			if (format.decimalSeparator !== '.') {
    				v = v.slice(0, i) + format.decimalSeparator + v.slice(i + 1);
    			}
    		} else {
    			i = v.length;
    		}
    		if (format.groupingSeparator) {
    			while (i > format.groupingSize) {
    				i -= format.groupingSize;
    				v = v.slice(0, i) + format.groupingSeparator + v.slice(i);
    			}
    		}
    		if (format.currencySymbol) {
    			v = format.currencySymbol + v;
    		}
    		if (neg) {
    			v = '-' + v;
    		}
    		return v;
        }   
        
        , numberRenderer : function(format){
            return function(v){
                return Sbi.qbe.commons.Format.number(v, format);
            };
        }
        
        , string : function(v, format) {
        	format = Ext.apply({}, format || {}, {
	    		trim: true,
	    		maxLength: null,
	    		ellipsis: true,
	    		changeCase: null, // null | 'capitalize' | 'uppercase' | 'lowercase'
	    		prefix: '',
	    		suffix: '',
	    		nullValue: ''
    		});
        	
        	if(!v){
                return format.nullValue;
            }
        	
        	if(format.trim) v = Ext.util.Format.trim(v);
        	if(format.maxLength) {
        		if(format.ellipsis === true) {
        			v = Ext.util.Format.ellipsis(v, format.maxLength);
        		} else {
        			v = Ext.util.Format.substr(v, 0, format.maxLength);
        		}
        	}
        	if(format.changeCase){
        		if(format.changeCase === 'capitalize') {
        			v = Ext.util.Format.capitalize(v);
        		} else if(format.changeCase === 'uppercase') {
        			v = Ext.util.Format.uppercase(v);
        		} else if(format.changeCase === 'lowercase') {
        			v = Ext.util.Format.lowercase(v);
        		}        		
        	}
        	if(format.prefix) v = format.prefix+ v;
        	if(format.suffix) v =  v + format.suffix;
        	
        	return v;
        }
        
        , stringRenderer : function(format){
            return function(v){
                return Sbi.qbe.commons.Format.string(v, format);
            };
        }
        
        , 'boolean': function(v, format) {
        	format = Ext.apply({}, format || {}, {
	    		trueSymbol: 'true',
	    		falseSymbol: 'false',
	    		nullValue: ''
    		});
        	
        	if(v === true){
        		 v = format.trueSymbol;
            } else if(v === true){
            	 v = format.falseSymbol;
            } else {
            	 v = format.nullValue;
            }
        	
        	return v;
        }
        
        , booleanRenderer : function(format){
            return function(v){
                return Sbi.qbe.commons.Format.boolean(v, format);
            };
        }
        
        , html : function(v, format) {
        	// format is not used yet but it is reserve for future use
        	// ex. format.cls, format.style
        	v = Ext.util.Format.htmlDecode(v);
        	return v;
        }
        
        , htmlRenderer : function(format){
            return function(v){
                return Sbi.qbe.commons.Format.html(v, format);
            };
        }
        
	};
	
}();







	Ext.ns("Sbi.locale");

Sbi.locale.dummyFormatter = function(v){return v;};
Sbi.locale.formatters = {
	//number: Sbi.locale.dummyFormatter,
	int: Sbi.locale.dummyFormatter,
	float: Sbi.locale.dummyFormatter,
	string: Sbi.locale.dummyFormatter,		
	date: Sbi.locale.dummyFormatter,		
	boolean: Sbi.locale.dummyFormatter,
	html: Sbi.locale.dummyFormatter
};


if(Sbi.qbe.commons.Format){
	if(Sbi.locale.formats) {
		Sbi.locale.formatters.int  = Sbi.qbe.commons.Format.numberRenderer(Sbi.locale.formats['int']);		
		Sbi.locale.formatters.float  = Sbi.qbe.commons.Format.numberRenderer(Sbi.locale.formats['float']);		
		Sbi.locale.formatters.string  = Sbi.qbe.commons.Format.stringRenderer(Sbi.locale.formats['string']);		
		Sbi.locale.formatters.date    = Sbi.qbe.commons.Format.dateRenderer(Sbi.locale.formats['date']);		
		Sbi.locale.formatters.boolean = Sbi.qbe.commons.Format.booleanRenderer(Sbi.locale.formats['boolean']);
		Sbi.locale.formatters.html    = Sbi.qbe.commons.Format.htmlRenderer();
	} else {
		Sbi.locale.formatters.int  = Sbi.qbe.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.float  = Sbi.qbe.commons.Format.numberRenderer( );	
		Sbi.locale.formatters.string  = Sbi.qbe.commons.Format.stringRenderer( );		
		Sbi.locale.formatters.date    = Sbi.qbe.commons.Format.dateRenderer( );		
		Sbi.locale.formatters.boolean = Sbi.qbe.commons.Format.booleanRenderer( );
		Sbi.locale.formatters.html    = Sbi.qbe.commons.Format.htmlRenderer();
	}
};


Sbi.locale.localize = function(key) {
	if(!Sbi.locale.ln) return key;
	return Sbi.locale.ln[key] || key;
};

// alias
LN = Sbi.locale.localize;
FORMATTERS = Sbi.locale.formatters;




/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * ServiceRegistry - short description
  * 
  * Object documentation ...
  * 
  * by Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.service");

Sbi.service.ServiceRegistry = function(config) {
	
	config = config || {};
	
	this.baseUrl = Ext.apply({}, config.baseUrl || {}, {
		protocol: 'http'     
		, host: 'localhost'
	    , port: '8080'
	    , contextPath: 'SpagoBI'
	    , controllerPath: 'servlet/AdapterHTTP'    
	});
	
	this.baseParams = Ext.apply({}, config.baseParams || {}, {
		
	});
	
	this.defaultAbsolute = config.defaultAbsolute !== undefined?  config.defaultAbsolute: false; 
	this.defaultServiceType = config.defaultServiceType !== undefined?  config.defaultServiceType: 'action'; 
		
	//this.addEvents();	
	
	// constructor
    Sbi.service.ServiceRegistry.superclass.constructor.call(this);
};

Ext.extend(Sbi.service.ServiceRegistry, Ext.util.Observable, {
    
    // static contens and methods definitions
	baseUrl: null
	, baseParams: null
	, defaultAbsolute: null
	, defaultServiceType: null 
	
   
    // public methods
    
    , setBaseUrl : function(url) {
       Ext.apply(this.baseUrl, url); 
    }
        
    , getServiceUrl : function(s){
    	var serviceUrl;
    	
    	var baseUrlStr;
    	var serviceType;
    	var params;
               
        if(typeof s == 'string') {
        	s = {serviceName: s};
        }
        
        serviceType = s.serviceType || this.defaultServiceType;
        params = Ext.apply({}, s.baseParams || {}, this.baseParams);
                
        serviceUrl = this.getBaseUrlStr(s);
        serviceUrl += '/' + s.serviceName;
        serviceUrl += '?';
    
        for(var p in params){
        	if(params[p] !== null) {
        		serviceUrl += '&' + p + '=' + params[p];
        	}
        }
        
        return serviceUrl;
    }     
    
    , getBaseUrlStr: function(s) {
    	var baseUrlStr;

    	if (this.baseUrl.completeUrl !== undefined) {
    		baseUrlStr = this.baseUrl.completeUrl;
    	} else {
        	var isAbsolute = s.isAbsolute || this.defaultAbsolute;
        	var url = Ext.apply({}, s.baseUrl || {}, this.baseUrl);
        	
        	if(isAbsolute) {
        		baseUrlStr = url.protocol + '://' + url.host + ":" + url.port + '/' + url.contextPath;
        	} else {
        		baseUrlStr = '/' + url.contextPath;
        	}
        	
        	if(url.controllerPath) {
        		baseUrlStr += '/' + url.controllerPath;
        	}
    	}
    	return  baseUrlStr;
    }
});/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * this is just a staging area for utilities function waiting to be factored somewhere else
  * 
  * 
  * Public Functions
  * 
  *  [list]
  * 
  * 
  * Authors
  * 
  * - Andrea Gioia (adrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.GeoReportUtils = function(){
 
	return {
		
		/**
		 * computes mercator coordinates from latitude,longitude coordinates, 
		 * for map unsing mercator's projection (EPSG:900913)
		 */
		lonLatToMercator: function(ll) {
			var lon = ll.lon * 20037508.34 / 180;
			var lat = Math.log(Math.tan((90 + ll.lat) * Math.PI / 360)) / (Math.PI / 180);
			lat = lat * 20037508.34 / 180;
			return new OpenLayers.LonLat(lon, lat);
		}

		/**
		 * loads tile using google standard
		 */
		, osm_getTileURL: function(bounds) {
			var res = this.map.getResolution();
			var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
			var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
			var z = this.map.getZoom();
			var limit = Math.pow(2, z);

			if (y < 0 || y >= limit) {
				return OpenLayers.Util.getImagesLocation() + "404.png";
			} else {
				x = ((x % limit) + limit) % limit;
				return this.url + z + "/" + x + "/" + y + "." + this.type;
			}
		}
	};
	
}();







	/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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
  * - Andrea Gioia (adrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.ControlFactory = function(){
 
	return {
		
		createControl : function( controlConf ){
			var control;
			
			if(controlConf.type === 'MousePosition') {
				control =  new OpenLayers.Control.MousePosition();
			} else if(controlConf.type === 'OverviewMap') {
				control =  new OpenLayers.Control.OverviewMap({
					mapOptions: controlConf.mapOptions
				});
			} else if(controlConf.type === 'Navigation') {
				control =  new OpenLayers.Control.Navigation();
			} else if(controlConf.type === 'PanZoomBar') {
				control =  new OpenLayers.Control.PanZoomBar();
			} else {
				Sbi.exception.ExceptionHandler.showErrorMessage(
					'Control type [' + controlConf.type + '] not supported'
				);
			}
			
			return control;
		}
	};
	
}();







	/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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
  * - Andrea Gioia (adrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.LayerFactory = function(){
 
	return {
		
		createLayer : function( layerConf ){
			var layer;
			if(layerConf.type === 'WMS') {
				layer = new OpenLayers.Layer.WMS(
					layerConf.name, layerConf.url, 
					layerConf.params, layerConf.options
				);
			} else if(layerConf.type === 'TMS') {
				layerConf.options.getURL = Sbi.georeport.GeoReportUtils.osm_getTileURL;
				layer = new OpenLayers.Layer.TMS(
					layerConf.name, layerConf.url, layerConf.options
				);
			} else if(layerConf.type === 'Google') {
				layer = new OpenLayers.Layer.Google(
					layerConf.name, layerConf.options
				);
			} else if(layerConf.type === 'OSM') { 
				layer = new OpenLayers.Layer.OSM.Mapnik('OSM');
			}else {
				Sbi.exception.ExceptionHandler.showErrorMessage(
					'Layer type [' + layerConf.type + '] not supported'
				);
			}
			return layer;
		}
	};
	
}();







	/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.GeoReportPanel = function(config) {
	
	var defaultSettings = {
		mapName: 'sbi.georeport.mappanel.title'
		, controlPanelConf: {
			layerPanelEnabled: true
			, analysisPanelEnabled: true
			, legendPanelEnabled: true
			, logoPanelEnabled: true
			, earthPanelEnabled: true
		}	
		, toolbarConf: {
			enabled: true,
			zoomToMaxButtonEnabled: true,
			mouseButtonGroupEnabled: true,
			drawButtonGroupEnabled: true,
			historyButtonGroupEnabled: true
		}
	};
		
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.georeportPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.georeportPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
		
	this.services = this.services || new Array();	
	
	var params = {
		layer: this.targetLayerConf.name
		, businessId: this.businessId
		, geoId: this.geoId
	};
	if(this.targetLayerConf.url) {
		params.featureSourceType = 'wfs';
		params.featureSource = this.targetLayerConf.url;
	} else {
		params.featureSourceType = 'file';
		params.featureSource = this.targetLayerConf.data;
	}
	
	this.services['MapOl'] = this.services['MapOl'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MapOl'
		, baseParams: params
	});
	
	
	
	
	//this.addEvents('customEvents');
		
		
	this.initMap();
	this.initMapPanel();
	this.initControlPanel();
	
	c = Ext.apply(c, {
         layout   : 'border',
         items    : [this.controlPanel, this.mapPanel]
	});

	// constructor
	Sbi.georeport.GeoReportPanel.superclass.constructor.call(this, c);
	
	this.on('render', function() {
		this.setCenter();
		if(this.controlPanelConf.earthPanelEnabled === true) {
			this.init3D.defer(500, this);
		}
		if(this.toolbarConf.enabled) {
			this.initToolbarContent.defer(500, this);	
		}
	}, this);
	
	
	
};

Ext.extend(Sbi.georeport.GeoReportPanel, Ext.Panel, {
    
    services: null
    
    , baseLayersConf: null
    , layers: null
    
    , map: null
    , lon: null
    , lat: null
    , zoomLevel: null
    
    , showPositon: null
    , showOverview: null
    , mapName: null
    , mapPanel: null
    , controlPanel: null
    
    , analysisType: null
    , PROPORTIONAL_SYMBOLS:'proportionalSymbols'
    , CHOROPLETH:'choropleth'
    
    , targetLayer: null
    , geostatistic: null
    
    // -- public methods ------------------------------------------------------------------------
    
    
    , setCenter: function(center) {
      	
		center = center || {};
      	this.lon = center.lon || this.lon;
      	this.lat = center.lat || this.lat;
      	this.zoomLevel = center.zoomLevel || this.zoomLevel;
        
        if(this.map.projection == "EPSG:900913"){            
            this.centerPoint = Sbi.georeport.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat));
            this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else if(this.map.projection == "EPSG:4326") {
        	this.centerPoint = new OpenLayers.LonLat(this.lon, this.lat);
        	this.map.setCenter(this.centerPoint, this.zoomLevel);
        } else{
        	alert("Map Projection not supported yet!");
        }
         
     }

	 // -- private methods ------------------------------------------------------------------------
	
	
	, initMap: function() {  
		var o = this.baseMapOptions;
	
		if(o.projection !== undefined && typeof o.projection === 'string') {
			o.projection = new OpenLayers.Projection( o.projection );
		}
		
		if(o.displayProjection !== undefined && typeof o.displayProjection === 'string') {
			o.displayProjection = new OpenLayers.Projection( o.displayProjection );
		}
		
		if(o.maxExtent !== undefined && typeof o.maxExtent === 'object') {
			o.maxExtent = new OpenLayers.Bounds(
					o.maxExtent.left, 
					o.maxExtent.bottom,
					o.maxExtent.right,
					o.maxExtent.top
            );
		}
		
		
		this.map = new OpenLayers.Map('map', this.baseMapOptions);
		this.initLayers();
		this.initControls();  
		this.initAnalysis();
    }

	, initLayers: function(c) {
		this.layers = new Array();
				
		if(this.baseLayersConf && this.baseLayersConf.length > 0) {
			for(var i = 0; i < this.baseLayersConf.length; i++) {
				if(this.baseLayersConf[i].enabled === true) {
					var l = Sbi.georeport.LayerFactory.createLayer( this.baseLayersConf[i] );
					this.layers.push( l	);
				}
			}			
		}
		
		this.map.addLayers(this.layers);
	}
	
	, initControls: function() {
		
		if(this.baseControlsConf && this.baseControlsConf.length > 0) {
			for(var i = 0; i < this.baseControlsConf.length; i++) {
				if(this.baseControlsConf[i].enabled === true) {
					this.baseControlsConf[i].mapOptions = this.baseMapOptions;
					var c = Sbi.georeport.ControlFactory.createControl( this.baseControlsConf[i] );
					this.map.addControl( c );
				}
			}			
		}
	}
	
	
	, init3D: function(center){
		center = center || {};
		this.lon = center.lon || this.lon;
		this.lat = center.lat || this.lat;
	    
	    if(this.map.projection == "EPSG:900913"){
	        
	    	var earth = new mapfish.Earth(this.map, 'map3dContainer', {
	    		lonLat: Sbi.georeport.GeoReportUtils.lonLatToMercator(new OpenLayers.LonLat(this.lon, this.lat)),
	            altitude: 50, //da configurare
	            heading: -60, //da configurare
	            tilt: 70,     //da configurare
	            range: 700}
	    	); //da configurare
	    	
	    } else if(this.map.projection == "EPSG:4326"){
	    	
	    	var earth = new mapfish.Earth(this.map, 'map3dContainer', {
	    		lonLat: new OpenLayers.LonLat(this.lon, this.lat),
	            altitude: 50,//da configurare
	            heading: -60,//da configurare
	            tilt: 70,//da configurare
	            range: 700
	       
	    	}); //da configurare	    
	    } else{
	    	alert('Map projection [' + this.map.projection + '] not supported yet!');
	    }
	  
	  }
	
	
	
	, initAnalysis: function() {
		
		var geostatConf = {
			map: this.map,
			layer: null, // this.targetLayer not yet defined here
			indicators:  this.indicators,
			url: this.services['MapOl'],
			loadMask : {msg: 'Analysis...', msgCls: 'x-mask-loading'},
			legendDiv : 'myChoroplethLegendDiv',
			featureSelection: false,
			listeners: {}
		};

		if(this.map.projection == "EPSG:900913") {
			 geostatConf.format = new OpenLayers.Format.GeoJSON({
				 externalProjection: new OpenLayers.Projection("EPSG:4326"),
			     internalProjection: new OpenLayers.Projection("EPSG:900913")
			 });
		}
		
		
		if (this.analysisType === this.PROPORTIONAL_SYMBOLS) {
			this.initProportionalSymbolsAnalysis();
			geostatConf.layer = this.targetLayer;
			this.geostatistic = new mapfish.widgets.geostat.ProportionalSymbol(geostatConf);
			
		} else if (this.analysisType === this.CHOROPLETH) {
			this.initChoroplethAnalysis();
			geostatConf.layer = this.targetLayer;
			this.geostatistic = new mapfish.widgets.geostat.Choropleth(geostatConf);
		} else {
			alert('error: unsupported analysis type [' + this.analysisType + ']');
		}
		
		this.map.addControl(this.analysisLayerSelectControl); 
		this.analysisLayerSelectControl.activate();
		
		
	}
	
	, initProportionalSymbolsAnalysis: function() {
	
		this.targetLayer = new OpenLayers.Layer.Vector(this.targetLayerConf.text, {
				'visibility': false  ,
				'styleMap': new OpenLayers.StyleMap({
	   				'select': new OpenLayers.Style(
	       				{'strokeColor': 'red', 'cursor': 'pointer'}
	   				)
				})
		});

		this.map.addLayer(this.targetLayer);
        this.analysisLayerSelectControl = new OpenLayers.Control.SelectFeature(this.targetLayer, {} );

        this.targetLayer.events.register("featureselected", this, function(o) { 
			//alert('select -> ' + this.getInfo(o.feature));
			this.onTargetFeatureSelect(o.feature);
		}); 
        
        this.targetLayer.events.register("featureunselected", this, function(o) { 
			//alert('unselect -> ' + this.getInfo(o.feature));
			this.onTargetFeatureUnselect(o.feature);
		}); 
		
	}
	
	, initChoroplethAnalysis: function() {
		this.targetLayer = new OpenLayers.Layer.Vector(this.targetLayerConf.text, {
        	'visibility': false,
          	'styleMap': new OpenLayers.StyleMap({
            	'default': new OpenLayers.Style(
                	OpenLayers.Util.applyDefaults(
                      {'fillOpacity': 0.5},
                      OpenLayers.Feature.Vector.style['default']
                  	)
              	),
              	'select': new OpenLayers.Style(
                  {'strokeColor': 'red', 'cursor': 'pointer'}
              	)
          	})
      	});
     
    
    	this.map.addLayer(this.targetLayer);                                    
    	this.analysisLayerSelectControl = new OpenLayers.Control.SelectFeature(this.targetLayer, {});
        
        this.targetLayer.events.register("featureselected", this, function(o) { 
			//alert('select -> ' + this.getInfo(o.feature));
			this.onTargetFeatureSelect(o.feature);
		}); 
        
        this.targetLayer.events.register("featureunselected", this, function(o) { 
			//alert('unselect -> ' + this.getInfo(o.feature));
			this.onTargetFeatureUnselect(o.feature);
		}); 
	}
	
	, onTargetFeatureSelect: function(feature) {
		this.selectedFeature = feature;
		
        
       

		var params = Ext.apply({}, this.detailDocumentConf.staticParams);
		for(p in this.detailDocumentConf.dynamicParams) {
			var attrName = this.detailDocumentConf.dynamicParams[p];
			params[p] = feature.attributes[attrName];
		}
		
		//alert(params.toSource());
		
        var execDetailFn = "execDoc(";
        execDetailFn += '"' + this.detailDocumentConf.label + '",'; // documentLabel
        execDetailFn += '"' + this.role + '",'; // execution role
        execDetailFn += Ext.util.JSON.encode(params) + ','; // parameters
        execDetailFn += this.detailDocumentConf.displayToolbar + ','; // displayToolbar
        execDetailFn += this.detailDocumentConf.displaySliders + ','; // displaySliders
        execDetailFn += '"' + this.detailDocumentConf.label + '"'; // frameId
        execDetailFn += ")";
       
        var link = '';
        link += '<center>';
        link += '<font size="1" face="Verdana">';
        link += '<a href="#" onclick=\'Ext.getCmp("' + this.mapPanel.getId() + '").setActiveTab("infotable");';
        link += 'Ext.getCmp("infotable").body.dom.innerHTML=';
        link += execDetailFn + '\';>';
        link += 'Dettagli</a></font></center>';

        params = Ext.apply({}, this.inlineDocumentConf.staticParams);
		for(p in this.inlineDocumentConf.dynamicParams) {
			var attrName = this.inlineDocumentConf.dynamicParams[p];
			params[p] = feature.attributes[attrName];
		}
		
		//alert(params.toSource());
        
        var content = "<div style='font-size:.8em'>" + this.getInfo(feature);
        content += link;
        content += execDoc(
        		this.inlineDocumentConf.label, 
        		this.role, 
        		params, 
        		this.inlineDocumentConf.displayToolbar, 
        		this.inlineDocumentConf.displaySliders, 
        		this.inlineDocumentConf.label,
        		'300'
        );
       
        popup = new OpenLayers.Popup.FramedCloud("chicken", 
                feature.geometry.getBounds().getCenterLonLat(),
                null,
                content,
                null, 
                true, 
                function(evt) {
        			this.analysisLayerSelectControl.unselect(this.selectedFeature);    
                }.createDelegate(this, [])
        );
        
        feature.popup = popup;
        this.map.addPopup(popup);
	}
	
	, onTargetFeatureUnselect: function(feature) {
		this.map.removePopup(feature.popup);
        feature.popup.destroy();
        feature.popup = null;
        var infoPanel = Ext.getCmp('infotable');
        if(infoPanel.body){
        	infoPanel.body.dom.innerHTML = '';
        }
	}
	
	, getInfo: function(feature) {
		//alert(feature.attributes.toSource());
		var info = "";
	    for(var i=0; i<this.feautreInfo.length; i++){
	    	info = info+"<b>"+ this.feautreInfo[i][0] +"</b>: " + feature.attributes[this.feautreInfo[i][1]] + "<br />";    
	    } 
	    return info;
	}
	
	
	
	
	

	
	
	, initMapPanel: function() {
		
		var mapPanelConf = {
			title: LN(this.mapName),
			layout: 'fit',
	       	items: {
		        xtype: 'mapcomponent',
		        map: this.map
		    }
	    };
		
		if(this.toolbarConf.enabled) {
			this.loadingButton = new Ext.Toolbar.Button({
	            tooltip: 'Please wait',
	            iconCls: "x-tbar-loading"
	        });
			
			this.toolbar = new mapfish.widgets.toolbar.Toolbar({
		    	map: this.map, 
		        configurable: false,
		        items: [this.loadingButton, ' Loading toolbar...']
			});
			
			this.loadingButton.disable();
			
			mapPanelConf.tbar = this.toolbar;
		}
	 
		
	 
		this.mapPanel = new Ext.TabPanel({
		    region    : 'center',
		    margins   : '3 3 3 0', 
		    activeTab : 0,
		    defaults  : {
				autoScroll : true
			},

	       	items: [
		       	new Ext.Panel(mapPanelConf), {
		            title    : 'Info',
		            html: '<div id="info"</div>',
		            id: 'infotable',
		            autoScroll: true
		        }
		    ]
		});
	}
	
	, initControlPanel: function() {
		
		
		var controlPanelItems = [];
		
		if(this.controlPanelConf.earthPanelEnabled === true) {
			controlPanelItems.push({
				title: LN('sbi.georeport.earthpanel.title'),
                html: '<center id="map3dContainer"></center>',
                split: true,
                height: 300,
                minSize: 300,
                maxSize: 500,
                collapsible: false                
	        });
		}
		
		if(this.controlPanelConf.layerPanelEnabled === true) {
			controlPanelItems.push({
	        	title: LN('sbi.georeport.layerpanel.title'),
	            collapsible: true,
	            autoHeight: true,
	            xtype: 'layertree',
	            map: this.map
	        });
		}
		
		if(this.controlPanelConf.analysisPanelEnabled === true) {
			controlPanelItems.push({
	        	title: LN('sbi.georeport.analysispanel.title'),
	            collapsible: true,
	            items: [this.geostatistic]
	        });
		}
		
		
		
		if(this.controlPanelConf.legendPanelEnabled === true) {
			controlPanelItems.push({
		           title: LN('sbi.georeport.legendpanel.title'),
		           collapsible: true,
		           height: 150,
		           html: '<center id="myChoroplethLegendDiv"></center>'
		     });
		}
		
		if(this.controlPanelConf.logoPanelEnabled === true) {
			controlPanelItems.push({
		           title: 'Logo',
		           collapsible: true,
		           height: 85,
		           html: '<center><img src="/SpagoBIGeoReportEngine/img/georeport.jpg" alt="GeoReport"/></center>'
		    });
		}
		
		if(this.controlPanelConf.logoPanelEnabled === false) {
			controlPanelItems.push({
		           title: 'Debug',
		           collapsible: true,
		           height: 85,
		           items: [new Ext.Button({
				    	text: 'Debug',
				        width: 30,
				        handler: function() {
		        	   		this.init3D();
		        	   		/*
		        	   		var size = this.controlPanel.getSize();
		        	   		size.width += 1;
		        	   		this.controlPanel.refreshSize(size);
		        	   		*/
		           		},
		           		scope: this
				    })]
		    });
		}
		
		this.controlPanel = new Ext.Panel({
			 title       : LN('sbi.georeport.controlpanel.title'),
		     region      : 'west',
		     split       : true,
		     width       : 300,
		     collapsible : true,
		     margins     : '3 0 3 3',
		     cmargins    : '3 3 3 3',
		     autoScroll	 : true,
		     items		 : controlPanelItems 
		}); 
		 
		 	
	}
	

	
	, addSeparator: function(){
          this.toolbar.add(new Ext.Toolbar.Spacer());
          this.toolbar.add(new Ext.Toolbar.Separator());
          this.toolbar.add(new Ext.Toolbar.Spacer());
    } 

	, initToolbarContent: function() {
			
		this.toolbar.items.each( function(item) {
			this.toolbar.items.remove(item);
            item.destroy();           
        }, this); 
		
		var vectorLayer = new OpenLayers.Layer.Vector("vector", { 
	    	displayInLayerSwitcher: false
	    });
	    this.map.addLayer(vectorLayer);
	    
	    if(this.toolbarConf.zoomToMaxButtonEnabled === true) {
		    this.toolbar.addControl(
		        new OpenLayers.Control.ZoomToMaxExtent({
		        	map: this.map,
		            title: 'Zoom to maximum map extent'
		        }), {
		            iconCls: 'zoomfull', 
		            toggleGroup: 'map'
		        }
		    );
			
		    this.addSeparator();
	    }
	    
	    if(this.toolbarConf.mouseButtonGroupEnabled === true) {
		    this.toolbar.addControl(
		    	new OpenLayers.Control.ZoomBox({
		    		title: 'Zoom in: click in the map or use the left mouse button and drag to create a rectangle'
	            }), {
		    		iconCls: 'zoomin', 
	                toggleGroup: 'map'
		    	}
	        );
	      
			this.toolbar.addControl(
				new OpenLayers.Control.ZoomBox({
					out: true,
	                title: 'Zoom out: click in the map or use the left mouse button and drag to create a rectangle'
	            }), {
					iconCls: 'zoomout', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DragPan({
					isDefault: true,
	                title: 'Pan map: keep the left mouse button pressed and drag the map'
	            }), {
	                iconCls: 'pan', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.addSeparator();
	    }
	          
	    if(this.toolbarConf.drawButtonGroupEnabled === true) {
	    	this.toolbar.addControl(
	    		new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Point, {
	    			title: 'Draw a point on the map'
	            }), {
	    			iconCls: 'drawpoint', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Path, {
					title: 'Draw a linestring on the map'
				}), {
	                iconCls: 'drawline', 
	                toggleGroup: 'map'
				}
	        );
	          
			this.toolbar.addControl(
				new OpenLayers.Control.DrawFeature(vectorLayer, OpenLayers.Handler.Polygon, {
					title: 'Draw a polygon on the map'
	            }), {
	                iconCls: 'drawpolygon', 
	                toggleGroup: 'map'
	            }
	        );
	          
			this.addSeparator();
	    }
	      
	    if(this.toolbarConf.historyButtonGroupEnabled === true) {
	    	var nav = new OpenLayers.Control.NavigationHistory();
	        this.map.addControl(nav);
	        nav.activate();
	          
	        this.toolbar.add(
	        	new Ext.Toolbar.Button({
	        		iconCls: 'back',
	                tooltip: 'Previous view', 
	                handler: nav.previous.trigger
	            })
	         );
	          
	         this.toolbar.add(
	        	new Ext.Toolbar.Button({
	        		iconCls: 'next',
	                tooltip: 'Next view', 
	                handler: nav.next.trigger
	            })
	         );
	          
	         this.addSeparator();
	    }
	          
	    this.toolbar.activate();
	    
      }
});