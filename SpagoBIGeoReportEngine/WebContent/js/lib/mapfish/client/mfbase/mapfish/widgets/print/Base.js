/*
 * Copyright (C) 2007-2008  Camptocamp
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
 * @requires core/PrintProtocol.js
 * @requires OpenLayers/Control/DragFeature.js
 * @requires OpenLayers/Layer/Vector.js
 * @requires OpenLayers/Feature/Vector.js
 * @requires OpenLayers/Geometry/Polygon.js
 */

Ext.namespace('mapfish.widgets');
Ext.namespace('mapfish.widgets.print');

/**
 * Class: mapfish.widgets.print.Base
 * Base class for the Ext panels used to communicate with the print module,
 * automatically take the layers from the given {<OpenLayers.Map>} instance.
 *
 * Inherits from:
 * - {Ext.Panel}
 */

/**
 * Constructor: mapfish.widgets.print.Base
 *
 * Parameters:
 * config - {Object} Config object
 */

mapfish.widgets.print.Base = Ext.extend(Ext.Panel, {
    /**
     * APIProperty: map
     * {<OpenLayers.Map>} - The OpenLayers Map object.
     */
    map: null,

    /**
     * APIProperty: overrides
     * {Object} the map that specify the print module overrides for each layers.
     *    They can be used of changing the OL layer's bahaviors for the print
     *   module. See the documentation in {<mapfish.PrintProtocol>}.
     */
    overrides: null,

    /**
     * APIProperty: configUrl
     * {String} - The URL to access .../config.json. Either this property or
     *            config must be set.
     */
    configUrl: null,

    /**
     * APIProperty: config
     * {Object} - The response from .../config.json. Either this property or
     *            configUrl must be set.
     */
    config: null,

    /**
     * APIProperty: layerTree
     * {<mapfish.widgets.LayerTree>} - An optional layer tree. Needed only if you
      *                                want to display legends.
     */
    layerTree: null,

    /**
     * APIProperty: grids
     * {Object} - An optional dictionary of {Ext.grid.GridPanel}. Needed only
     *            if you want to display search results. Can be function
     *            (returning the dictionary) that will be called each time the
     *            information is needed.
     */
    grids: null,

    /**
     * Property: pageDrag
     * {<OpenLayers.Control.DragFeature>} - The control to move the extent.
     */
    pageDrag: null,

    /**
     * Property: rotateHandle
     * {<OpenLayers.Feature.Vector>} - The handle used to rotate the page.
     */
    rotateHandle: null,

    /**
     * Property: layer
     * {<OpenLayers.Layer.Vector>} - The layer to draw the extent
     */
    layer: null,

    /**
     * Property: mask
     * {Ext.LoadingMask} - The mask used when loading the configuration or
     *                     when generating the PDF
     */
    mask: null,

    layout: 'fit',

    /**
     * Method: initComponent
     * Overrides super-class initComponent method. Put in place the handlers
     * for the showing/hidding of the panel. Then call the createComponent
     *  method
     */
    initComponent: function() {
        mapfish.widgets.print.Base.superclass.initComponent.call(this);

        //for accordion
        this.on('expand', this.onShowEvent, this);
        this.on('collapse', this.onHideEvent, this);

        //for tabs
        this.on('activate', this.onShowEvent, this);
        this.on('deactivate', this.onHideEvent, this);

        if (this.overrides == null) {
            this.overrides = {};
        }

        this.on('render', function() {
            var mask = this.mask = new Ext.LoadMask(this.getEl(), {
                msg: OpenLayers.Lang.translate('mf.print.loadingConfig')
            });
            if (this.config == null) {
                mask.show();
            }
        }, this);

        if (this.config == null) {
            mapfish.PrintProtocol.getConfiguration(this.configUrl,
                    this.configReceived, this.configFailed, this);
        } else {
            this.fillComponent();
        }
    },

    configReceived: function(config) {
        if (this.mask) {
            this.mask.hide();
        }
        this.config = config;
        this.fillComponent();
        this.doLayout();
    },

    configFailed: function() {
        this.add({
            border: false,
            region: 'center',
            html: OpenLayers.Lang.translate('mf.print.serverDown')
        });
        this.doLayout();
        if (this.mask) {
            this.mask.hide();
        }
        this.config = false;
    },

    /**
     * Method: onShowEvent
     *
     * Called when the panel is activated.
     */
    onShowEvent: function() {
        if (this.config) {
            this.map.addLayer(this.getOrCreateLayer());
            this.pageDrag.activate();
        }
    },

    /**
     * Method: onHideEvent
     *
     * Called when the panel is de-activated.
     */
    onHideEvent: function() {
        if (this.config && this.pageDrag && this.layer) {
            this.pageDrag.destroy();
            this.pageDrag = null;

            this.removeRotateHandle();
            this.layer.removeFeatures(this.layer.features);
            this.layer.destroy();
            this.layer = null;
        }
    },

    /**
     * Method: getOrCreateLayer
     *
     * If not already done, creates the layer used to represent the pages to
     * print.
     * Returns:
     * {<OpenLayers.Layer.Vector>}  
     */
    getOrCreateLayer: function() {
        if (!this.layer) {
            var self = this;

            this.layer = new OpenLayers.Layer.Vector("Print" + this.getId(), {
                displayInLayerSwitcher: false,
                calculateInRange: function() {
                    return true;
                }
            });

            this.pageDrag = new OpenLayers.Control.DragFeature(this.layer);
            this.map.addControl(this.pageDrag);
            var curFeature = null;
            this.pageDrag.onStart = function(feature) {
                OpenLayers.Control.DragFeature.prototype.onStart.apply(this, arguments);
                curFeature = feature;
                if (feature.attributes.rotate) {
                    self.pageRotateStart(feature);
                } else {
                    self.pageDragStart(feature);
                }
            }
            this.pageDrag.onDrag = function(feature) {
                OpenLayers.Control.DragFeature.prototype.onDrag.apply(this, arguments);
                if (!feature) feature = curFeature;
                if (feature.attributes.rotate) {
                    self.pageRotated(feature);
                }
            }
            this.pageDrag.onComplete = function(feature) {
                OpenLayers.Control.DragFeature.prototype.onComplete.apply(this, arguments);
                if (!feature) feature = curFeature;
                if (feature.attributes.rotate) {
                    self.pageRotateComplete(feature);
                } else {
                    self.pageDragComplete(feature);
                }
                curFeature = null;
            }

            this.afterLayerCreated();
        }
        return this.layer;
    },

    /**
     * Method: pageRotateStart
     *
     * Called when the user starts to move the rotate handle.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The rotate handle.
     */
    pageRotateStart: function(feature) {
    },

    /**
     * Method: pageRotated
     *
     * Called when rotate handle is being moved.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The rotate handle.
     */
    pageRotated: function(feature) {
        var center = feature.attributes.center;
        var pos = feature.geometry;
        var angle = Math.atan2(pos.x - center.x, pos.y - center.y) * 180 / Math.PI;
        var page = feature.attributes.page;
        page.attributes.rotation = angle;
        page.geometry.rotate(feature.attributes.prevAngle - angle, center);
        this.layer.drawFeature(page);
        this.setCurRotation(Math.round(angle));
        feature.attributes.prevAngle = angle;
    },

    /**
     * Method: pageRotateComplete
     *
     * Called when rotate handle is being released.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The rotate handle.
     */
    pageRotateComplete: function(feature) {
        //put back the rotate handle at the page's edge
        this.createRotateHandle(feature.attributes.page);
    },

    /**
     * Method: pageDragStart
     *
     * Called when we start editing a page.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The selected page.
     */
    pageDragStart: function(feature) {
        this.removeRotateHandle();
    },

    /**
     * Method: removeRotateHandle
     *
     * Remove the rotation handle, if any.
     */
    removeRotateHandle: function() {
        if (this.rotateHandle) {
            this.rotateHandle.destroy();
            this.rotateHandle = null;
        }
    },

    /**
     * Method: pageDragComplete
     *
     * Called when we stop editing a page.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The selected page.
     */
    pageDragComplete: function(feature) {
        if (this.getCurLayout().rotation) {
            this.createRotateHandle(feature);
        }
    },

    /**
     * Method: createRotateHandle
     *
     * Create the handle used to rotate the page.
     *
     * Parameters:
     * feature - {<OpenLayers.Feature.Vector>} The selected page.
     */
    createRotateHandle: function(feature) {
        this.removeRotateHandle();

        var firstPoint = feature.geometry.components[0].components[2];
        var secondPoint = feature.geometry.components[0].components[3];
        var lon = (firstPoint.x + secondPoint.x) / 2;
        var lat = (firstPoint.y + secondPoint.y) / 2;
        var rotatePoint = new OpenLayers.Geometry.Point(lon, lat);
        var center = this.getCenterRectangle(feature);
        this.rotateHandle = new OpenLayers.Feature.Vector(rotatePoint, {
            rotate: true,
            page: feature,
            center: {x: center[0], y: center[1]},
            prevAngle: feature.attributes.rotation
        });
        this.layer.addFeatures(this.rotateHandle);
    },

    /**
     * Method: createRectangle
     *
     * Create the feature representing a page to print.
     *
     * Parameters:
     * center - {<OpenLayers.LonLat>} The center of the rectangle.
     * scale - {Integer} The page's scale
     * layout - {Object} The current layout object from the configuration.
     * rotation - {float} The current rotation in degrees.
     *
     * Returns:
     * {<OpenLayers.Feature.Vector>}
     */
    createRectangle: function(center, scale, layout, rotation) {
        var extent = this.getExtent(center, scale, layout);
        var rect = extent.toGeometry();
        rect.rotate(-rotation, {x:center.lon, y:center.lat});
        var feature = new OpenLayers.Feature.Vector(rect, {rotation: rotation});
        this.layer.addFeatures(feature);

        return feature;
    },

    /**
     * Method: getCenterRectangle
     *
     * Parameters:
     * rectangle - {<OpenLayers.Feature.Vector>}
     *
     * Returns:
     * {<OpenLayers.LonLat>} The center of the rectangle.
     */
    getCenterRectangle: function(rectangle) {
        var center = rectangle.geometry.getBounds().getCenterLonLat();
        return [center.lon, center.lat];
    },

    /**
     * Method: getExtent
     *
     * Compute the page's extent.
     *
     * Parameters:
     * center - {<OpenLayers.LonLat>} The center of the rectangle.
     * scale - {Integer} The page's scale
     * layout - {Object} The current layout object from the configuration.
     *
     * Returns:
     * {<OpenLayers.Bounds>}
     */
    getExtent: function(center, scale, layout) {
        var unitsRatio = OpenLayers.INCHES_PER_UNIT[this.map.baseLayer.units];

        var size = layout.map;
        var w = size.width / 72.0 / unitsRatio * scale / 2.0;
        var h = size.height / 72.0 / unitsRatio * scale / 2.0;

        return new OpenLayers.Bounds(
                center.lon - w,
                center.lat - h,
                center.lon + w,
                center.lat + h);
    },

    /**
     * Finds the best scale to use for the given layout in function of the map's
     * extent.
     *
     * Parameters:
     * layout - {Object} The current layout object from the configuration.
     *
     * Returns:
     * {Integer} The best scale
     */
    fitScale: function(layout) {
        var availsTxt = this.config.scales;
        if (availsTxt.length == 0) return;
        var avails = [];
        for (var i = 0; i < availsTxt.length; ++i) {
            avails.push(parseFloat(availsTxt[i].value));
        }
        avails.sort(function(a, b) {
            return a - b
        });

        var bounds = this.map.getExtent();
        var unitsRatio = OpenLayers.INCHES_PER_UNIT[this.map.baseLayer.units];
        var size = layout.map;

        var targetScale = Math.min(bounds.getWidth() / size.width * 72.0 * unitsRatio,
                bounds.getHeight() / size.height * 72.0 * unitsRatio);

        var nearestScale = avails[0];
        for (var j = 1; j < avails.length; ++j) {
            if (avails[j] <= targetScale) {
                nearestScale = avails[j];
            } else {
                break;
            }
        }

        return nearestScale;
    },

    /**
     * Method: print
     *
     * Do the actual printing.
     */
    print: function() {
        //we don't want the layer used for the extent to be printed, don't we?
        this.overrides[this.layer.name] = {visibility: false};

        var printCommand = new mapfish.PrintProtocol(this.map, this.config,
                this.overrides, this.getCurDpi());
        if (this.layerTree) {
            this.addLegends(printCommand.spec);
        }
        if (this.grids) {
            this.addGrids(printCommand.spec);
        }
        this.fillSpec(printCommand);

        this.mask.msg = OpenLayers.Lang.translate('mf.print.generatingPDF');
        this.mask.show();
        printCommand.createPDF(
            function() { //success
                this.mask.hide();
            },
            function(request) { //failure
                if(request.getURL) {
                    Ext.Msg.alert(OpenLayers.Lang.translate('mf.information'),
                        OpenLayers.Lang.translate('mf.print.popupBlocked') +
                        '<br />' +
                        '<a href="' + request.getURL + '" target="_blanc">' +
                        request.getURL+'</a>');
                } else {
                    Ext.Msg.alert(OpenLayers.Lang.translate('mf.error'),
                        OpenLayers.Lang.translate('mf.print.unableToPrint'));
                }
                this.mask.hide();
            }, this);
    },

    /**
     * Method: addGrids
     *
     * Add the grids' data to the given spec.
     *
     * Parameters:
     * spec - {Object} The print spec to fill.
     */
    addGrids: function(spec) {
        var grids = this.grids;
        if (grids && typeof grids == "function") {
            grids = grids();
        }
        if (grids) {
            for (var name in grids) {
                var grid = grids[name];
                if (!grid) {
                    continue;
                }
                spec[name] = {};
                var specData = spec[name].data = [];
                var specCols = spec[name].columns = [];
                var columns = grid.getColumnModel();
                var store = grid.getStore();
                for (var j = 0; j < columns.getColumnCount(); ++j) {
                    if (!columns.isHidden(j)) {
                        specCols.push(columns.getDataIndex(j));
                    }
                }
                store.each(function(record) {
                    var hash = {};
                    for (var key in record.data) {
                        var val = record.data[key];
                        if (val != null) {
                            if (val.CLASS_NAME && val.CLASS_NAME == 'OpenLayers.Feature.Vector') {
                                val = new OpenLayers.Format.WKT().write(val);
                            }
                            hash[key] = val;
                        }
                    }
                    specData.push(hash);
                }, this);
            }
        }
    },

    /**
     * Method: addLegends
     *
     * Add the layerTree's legends to the given spec.
     *
     * Parameters:
     * spec - {Object} The print spec to fill.
     */
    addLegends: function(spec) {
        var legends = spec.legends = [];

        function addLayer(layerNode) {
            var layerInfo = {
                name: layerNode.attributes.text,
                icon:  mapfish.Util.relativeToAbsoluteURL(layerNode.attributes.icon)
            };
            var classesInfo = layerInfo.classes = [];
            layerNode.eachChild(function(classNode) {
                classesInfo.push({
                    name: classNode.attributes.text,
                    icon:  mapfish.Util.relativeToAbsoluteURL(classNode.attributes.icon)
                });
            }, this);
            if(classesInfo.length>0) {
                legends.push(layerInfo);
            }
        }

        function goDeep(root) {
            root.eachChild(function(node) {
                var attr = node.attributes;
                if (attr.checked && attr.layerNames) {
                    addLayer(node);
                } else {
                    goDeep(node);
                }
            }, this);
        }
        goDeep(this.layerTree.getRootNode());
    },

    /**
     * Method: getLayoutForName
     *
     * Finds the layout object from the configuration by it's name.
     *
     * Parameters:
     * layoutName - {String}
     *
     * Returns:
     * {Object}  
     */
    getLayoutForName: function(layoutName) {
        var layouts = this.config.layouts;
        for (var i = 0; i < layouts.length; ++i) {
            var cur = layouts[i];
            if (cur.name == layoutName) {
                return cur;
            }
        }
    },

    /**
     * Method: createScaleCombo
     */
    createScaleCombo: function() {
        var scaleStore = new Ext.data.JsonStore({
            root: "scales",
            fields: ['name', 'value'],
            data: this.config
        });

        return new Ext.form.ComboBox({
            fieldLabel: OpenLayers.Lang.translate('mf.print.scale'),
            store: scaleStore,
            displayField: 'name',
            valueField: 'value',
            typeAhead: false,
            mode: 'local',
            id: 'scale_' + this.getId(),
            hiddenId: 'scaleId_' + this.getId(),
            hiddenName: "scale",
            name: "scale",
            editable: false,
            triggerAction: 'all',
            value: this.config.scales[this.config.scales.length - 1].value
        });
    },

    /**
     * Method: createDpiCombo
     */
    createDpiCombo: function(name) {
        if (this.config.dpis.length > 1) {
            var dpiStore = new Ext.data.JsonStore({
                root: "dpis",
                fields: ['name', 'value'],
                data: this.config
            });

            return {
                fieldLabel: OpenLayers.Lang.translate('mf.print.dpi'),
                xtype: 'combo',
                store: dpiStore,
                displayField: 'name',
                valueField: 'value',
                typeAhead: false,
                mode: 'local',
                id: 'dpi_' + this.getId(),
                hiddenId: 'dpiId_' + this.getId(),
                hiddenName: name,
                name: name,
                editable: false,
                triggerAction: 'all',
                value: this.config.dpis[0].value
            };
        } else {
            return {
                xtype: 'hidden',
                name: name,
                value: this.config.dpis[0].value
            };
        }
    },

    /**
     * Method: createLayoutCombo
     */
    createLayoutCombo: function(name) {
        if (this.config.layouts.length > 1) {
            var layoutStore = new Ext.data.JsonStore({
                root: "layouts",
                fields: ['name'],
                data: this.config
            });

            return new Ext.form.ComboBox({
                fieldLabel: OpenLayers.Lang.translate('mf.print.layout'),
                store: layoutStore,
                displayField: 'name',
                valueField: 'name',
                typeAhead: false,
                mode: 'local',
                id: 'layout_' + this.getId(),
                hiddenId: 'layoutId_' + this.getId(),
                hiddenName: name,
                name: name,
                editable: false,
                triggerAction: 'all',
                value: this.config.layouts[0].name
            });
        } else {
            return new Ext.form.Hidden({
                name: name,
                value: this.config.layouts[0].name
            });
        }
    },

    /**
     * Method: createRotationTextField
     *
     * Creates a text field for editing the rotation. Only if the config
     * has at least one layout allowing rotations, otherwise, returns null.
     */
    createRotationTextField: function() {
        var layouts = this.config.layouts;
        var hasRotation = false;
        for (var i = 0; i < layouts.length && ! hasRotation; ++i) {
            hasRotation = layouts[i].rotation;
        }
        if (hasRotation) {
            var num = /^-?[0-9]+$/;
            return new Ext.form.TextField({
                fieldLabel: OpenLayers.Lang.translate('mf.print.rotation'),
                name: 'rotation',
                value: '0',
                maskRe: /^[-0-9]$/,
                msgTarget: 'side',
                validator: function(v) {
                    return num.test(v) ? true : "Not a number";
                }
            });
        } else {
            return null;
        }
    },

    /**
     * Method: fillComponent
     *
     * Called by initComponent to create the component's sub-elements. To be
     * implemented by child classes.
     */
    fillComponent: null,

    /**
     * Method: afterLayerCreated
     *
     * Called just after the layer has been created. To be implemented by child
     * classes.
     */
    afterLayerCreated: null,

    /**
     * Method: getCurDpi
     *
     * Returns the user selected DPI. To be implemented by child classes.
     */
    getCurDpi: null,

    /**
     * Method: fillSpec
     * Add the page definitions and set the other parameters. To be implemented
     * by child classes.
     *
     * Parameters:
     * printCommand - {<mapfish.PrintProtocol>} The print definition to fill.
     */
    fillSpec: null,

    /**
     * Method: getCurLayout
     *
     * Returns:
     * {Object} - The current layout config object
     */
    getCurLayout: null,

    /**
     * Method: setCurRotation
     *
     * Called when the rotation of the current page has been changed.
     *
     * Parameters:
     * rotation - {float}
     */
    setCurRotation: null
});
