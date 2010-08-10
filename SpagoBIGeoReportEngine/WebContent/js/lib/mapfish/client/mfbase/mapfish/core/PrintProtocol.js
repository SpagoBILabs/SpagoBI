/*
 * Copyright (C) 2008 Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * @requires OpenLayers/Console.js
 * @requires OpenLayers/Format/JSON.js
 * @requires OpenLayers/Request/XMLHttpRequest.js
 */

/**
 * Class: mapfish.PrintProtocol
 * Class to communicate with the print module.
 *
 * This class will automatically pick the layers from the OL map and create the
 * configuration structure accordingly.
 *
 * As we often want a sligtly different styling or minScale/maxScale, an
 * override functionallity is provided that allows to override the OL layers'
 * configuration, this can be used as well if a layer's URL points to a
 * TileCache service to allow the print module to access the WMS service
 * directly.
 */
mapfish.PrintProtocol = OpenLayers.Class({
    /**
     * APIProperty: config
     * {String} the configuration as returned by the MapPrinterServlet.
     */
    config: null,

    /**
     * APIProperty: spec
     * {Object} The complete spec to send to the servlet. You can use it to add
     * custom parameters.
     */
    spec: null,

    /**
     * Constructor: OpenLayers.Layer
     *
     * Parameters:
     * map - {<OpenLayers.Map>} The OL MAP.
     * config - {Object} the configuration as returned by the MapPrinterServlet.
     * overrides - {Object} the map that specify the print module overrides for
     *                      each layers.
     * dpi - {Integer} the DPI resolution  
     */
    initialize: function(map, config, overrides, dpi) {
        this.config = config;
        this.spec = {pages: []};
        this.addMapParams(overrides, map, dpi);
    },

    //TODO: we'll need some cleaner way to add pages and others to the spec.

    /**
     * APIMethod: getAllInOneUrl
     *
     * Creates the URL string for generating a PDF using the print module. Using
     * the direct method.
     *
     * WARNING: this method has problems with accents and requests with lots of
     *          pages. But it has the advantage to work without proxy.
     */
    getAllInOneUrl: function() {
        var json = new OpenLayers.Format.JSON();
        return this.config.printURL + "?spec=" +
               json.write(this.encodeForURL(this.spec));
    },

    /**
     * APIMethod: createPDF
     *
     * Uses AJAX to create the PDF on the server and then gets it from the
     * server. If it doesn't work (different URL and OpenLayers.ProxyHost not
     * set), try the GET direct method.   
     *
     * Parameters:
     * success - {Function} The function to call in case of success.
     * failure - {Function} The function to call in case of failure. Gets the
     *                      request object in parameter. If getURL is defined,
     *                      the popup where blocked and the PDF can still be
     *                      recovered using this URL.
     * context - {Object} The context to use to call the success of failure
     *                    method.
     */
    createPDF: function(success, failure, context) {
        var specTxt = new OpenLayers.Format.JSON().write(this.spec);
        OpenLayers.Console.info(specTxt);

        try {
            //The charset seems always to be UTF-8, regardless of the page's
            var charset = "UTF-8";
            /*+document.characterSet*/
            OpenLayers.Request.POST({
                url: this.config.createURL,
                data: specTxt,
                params: {
                    url: this.config.createURL
                },
                headers: {
                    'CONTENT-TYPE': "application/json; charset=" + charset
                },
                callback: function(request) {
                    if (request.status >= 200 && request.status < 300) {
                        var json = new OpenLayers.Format.JSON();
                        var answer = json.read(request.responseText);
                        if (answer && answer.getURL) {
                            this.openPdf(answer, success, failure, context);
                        } else {
                            failure.call(context, request);
                        }
                    } else {
                        failure.call(context, request);
                    }
                },
                scope: this
            });
        } catch (err) {
            OpenLayers.Console.warn(
                    "Cannot request the print service by AJAX. You must set " +
                    "the 'OpenLayers.ProxyHost' variable. Fallback to GET method");
            //try the other method
            window.open(this.getAllInOneUrl());
            success.call(context, err);
        }
    },

    /**
     * Method: openPdf
     *
     * Work around the browsers security "features" and open the given PDF
     * document.
     *
     * answer - {Object} The answer for the AJAX call to the print service.
     * success - {Function} The function to call in case of success.
     * failure - {Function} The function to call in case of failure. Gets the
     *                      request object in parameter. If getURL is defined,
     *                      the popup where blocked and the PDF can still be
     *                      recovered using this URL.
     * context - {Object} The context to use to call the success of failure
     *                    method
     */
    openPdf: function(answer, success, failure, context) {
        OpenLayers.Console.info(answer.getURL);
        var popup = window.open(answer.getURL, '_blank');
        if (popup) {
            // OK, we can at least open the popup
            if (mapfish.Util.isIE7()) {
                // IE7's anti-popup system tends to close the popup window
                // afterwards. We have to check if it has not done that.
                function checkWindowStillOpen() {
                    if (!popup.closed) {
                        success.call(context);
                    } else {
                        failure.call(context, answer);
                    }
                }
                window.setTimeout(checkWindowStillOpen, 300);
            } else {
                // we can assume the user received his PDF
                success.call(context);
            }
        } else {
            // we can say for sure that popups are blocked
            failure.call(context, answer);
        }
    },

    /**
     * Method: addMapParams
     *
     * Takes an OpenLayers Map and build the configuration needed for
     * the print module.
     */
    addMapParams: function(overrides, map, dpi) {
        var spec = this.spec;
        spec.dpi = dpi;
        spec.units = map.baseLayer.units;
        spec.srs = map.baseLayer.projection.getCode();
        var layers = spec.layers = [];
        for (var i = 0; i < map.layers.length; ++i) {
            var olLayer = map.layers[i];
            var layerOverrides = OpenLayers.Util.extend({}, overrides[olLayer.name]);

            //allows to have some attributes overriden in fct of the resolution
            OpenLayers.Util.extend(layerOverrides, layerOverrides[dpi]);

            if (olLayer.getVisibility() && layerOverrides.visibility != false) {
                var type = olLayer.CLASS_NAME;
                var handler = mapfish.PrintProtocol.SUPPORTED_TYPES[type];
                if (handler) {
                    var layer = handler.call(this, olLayer);
                    if (layer) {
                        this.applyOverrides(layer, layerOverrides);

                        if (olLayer.isBaseLayer) {
                            // base layers are always first
                            layers.unshift(layer);
                        } else {
                            layers.push(layer);
                        }
                    }
                } else if (!handler) {
                    OpenLayers.Console.error(
                            "Don't know how to print a layer of type " + type +
                            " (" + olLayer.name + ")");
                }
            }
        }
    },

    /**
     * Method: applyOverrides
     *
     * Change the layer config according to the overrides.
     */
    applyOverrides: function(layer, overrides) {
        for (var key in overrides) {
            if (isNaN(parseInt(key))) {
                var value = overrides[key];
                if (key == 'layers' || key == 'styles') {
                    value = this.fixArray(value);
                }
                if (layer[key] != null) {
                    layer[key] = value;
                } else {
                    layer.customParams[key] = value;
                }
            }
        }
    },

    /**
     * Method: convertLayer
     *
     * Handles the common parameters of all supported layer types.
     *
     * Parameters:
     * olLayer - {<OpenLayers.Layer>} The OL layer.
     *
     * Returns:
     * {Object} The config for this layer
     */
    convertLayer: function(olLayer) {
        var url = olLayer.url;
        if (url instanceof Array) {
            url = url[0];
        }
        return {
            baseURL: mapfish.Util.relativeToAbsoluteURL(url),
            opacity: (olLayer.opacity != null) ? olLayer.opacity : 1.0,
            singleTile: olLayer.singleTile,
            customParams: {}
        };
    },

    /**
     * Method: convertWMSLayer
     *
     * Builds the layer configuration from an {<OpenLayers.Layer.WMS>} layer.
     * The structure expected from the print module is:
     * (start code)
     * {
     *   type: 'WMS'
     *   baseURL: {String}
     *   layers: [{String}]
     *   styles: [{String}]
     *   format: {String}
     *   opacity: {Float}
     *   singleTile: {boolean}
     *   customParams: {
     *     {String}: {String}
     *   }
     * }
     * (end)
     *
     * Parameters:
     * olLayer - {<OpenLayers.Layer.WMS>} The OL layer.
     *
     * Returns:
     * {Object} The config for this layer
     */
    convertWMSLayer: function(olLayer) {
        var layer = OpenLayers.Util.extend(this.convertLayer(olLayer),
        {
            type: 'WMS',
            layers: this.fixArray(olLayer.params.LAYERS),
            format: olLayer.params.FORMAT || olLayer.DEFAULT_PARAMS.format,
            styles: this.fixArray(olLayer.params.STYLES ||
                                  olLayer.DEFAULT_PARAMS.styles)
        });
        for (var paramName in olLayer.params) {
            var paramNameLow = paramName.toLowerCase();
            if (olLayer.DEFAULT_PARAMS[paramNameLow] == null &&
                paramNameLow != 'layers' &&
                paramNameLow != 'width' &&
                paramNameLow != 'height' &&
                paramNameLow != 'srs') {
                layer.customParams[paramName] = olLayer.params[paramName];
            }
        }
        return layer;
    },

    /**
     * Method: convertTileCacheLayer
     *
     * Builds the layer configuration from an {<OpenLayers.Layer.TileCache>} layer.
     * The structure expected from the print module is:
     * (start code)
     * {
     *   type: 'TileCache'
     *   baseURL: {String}
     *   layer: {String}
     *   opacity: {Float}
     *   maxExtent: [minx, miny]
     *   tileSize: [width, height]
     *   extension: {String}
     *   resolutions: [{Float}]
     * }
     * (end)
     *
     * Parameters:
     * olLayer - {<OpenLayers.Layer.TileCache>} The OL layer.
     *
     * Returns:
     * {Object} The config for this layer
     */
    convertTileCacheLayer: function(olLayer) {
        return OpenLayers.Util.extend(this.convertLayer(olLayer), {
            type: 'TileCache',
            layer: olLayer.layername,
            maxExtent: olLayer.maxExtent.toArray(),
            tileSize: [olLayer.tileSize.w, olLayer.tileSize.h],
            extension: olLayer.extension,
            resolutions: olLayer.resolutions
        });
    },

    /**
     * Method: fixArray
     *
     * In some fields, OpenLayers allows to use a coma separated string instead
     * of an array. This method make sure we end up with an array.
     *
     * Parameters:
     * subs - {String/Array}
     *
     * Returns:
     * {Array}
     */
    fixArray: function(subs) {
        if (subs == '' || subs == null) {
            return [];
        } else if (subs instanceof Array) {
            return subs;
        } else {
            return subs.split(',');
        }
    },

    /**
     * Method: encodeForURL
     *
     * Takes a JSON structure and encode it so it can be added to an HTTP GET
     * URL. Does a better job with the accents than encodeURIComponent().
     * @param cur
     */
    encodeForURL: function(cur) {
        if (cur == null) return null;
        var type = typeof cur;
        Ext.type(cur);
        if (type == 'string') {
            return escape(cur.replace(/[\n]/g, "\\n"));
        } else if (type == 'object' && cur.constructor == Array) {
            var array = [];
            for (var i = 0; i < cur.length; ++i) {
                var val = this.encodeForURL(cur[i]);
                if (val != null) array.push(val);
            }
            return array;
        } else if (type == 'object' && cur.CLASS_NAME &&
                   cur.CLASS_NAME == 'OpenLayers.Feature.Vector') {
            return new OpenLayers.Format.WKT().write(cur);
        } else if (type == 'object') {
            var hash = {};
            for (var j in cur) {
                var val2 = this.encodeForURL(cur[j]);
                if (val2 != null) hash[j] = val2;
            }
            return hash;
        } else {
            return cur;
        }
    },

    CLASS_NAME: "mapfish.PrintProtocol"
});


/**
 * APIFunction: getConfiguration
 *
 * Does an AJAX call to get the print configuration from the server.
 *
 * Parameters:
 * url - {String} the URL to access .../config.json
 * success - {Function} the function that will be called with the
 *           configuration object when/if its received.
 * failure - {Function} the function that is called in case of error.
 * context - {Object} The context to use when calling the callbacks.
 */
mapfish.PrintProtocol.getConfiguration = function(url, success,
                                                  failure, context) {
    try {
        OpenLayers.Request.GET({
            url: url,
            params: {
                url: url
            },
            callback: function(request) {
                if (request.status >= 200 && request.status < 300) {
                    var json = new OpenLayers.Format.JSON();
                    var answer = json.read(request.responseText);
                    if (answer) {
                        success.call(context, answer);
                    } else {
                        failure.call(context, request);
                    }
                } else {
                    failure.call(context, request);
                }
            }
        });
    } catch(err) {
        failure.call(context, err);
    }
}


mapfish.PrintProtocol.IGNORED = function() {
    return null;
};

mapfish.PrintProtocol.SUPPORTED_TYPES = {
    'OpenLayers.Layer': mapfish.PrintProtocol.IGNORED,
    'OpenLayers.Layer.WMS': mapfish.PrintProtocol.prototype.convertWMSLayer,
    'OpenLayers.Layer.WMS.Untiled': mapfish.PrintProtocol.prototype.convertWMSLayer,
    'OpenLayers.Layer.TileCache': mapfish.PrintProtocol.prototype.convertTileCacheLayer
};

