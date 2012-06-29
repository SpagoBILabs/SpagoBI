/*
* SpagoBI, the Open Source Business Intelligence suite
* 
* Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This file is part of MapFish Client, Copyright (C) 2007 Camptocamp 
* This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the “Incompatible With Secondary Licenses” notice, according to the ExtJS Open Source License Exception for Development, version 1.03, January 23rd, 2012 http://www.sencha.com/legal/open-source-faq/open-source-license-exception-for-development/ 
* If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
* This file is an extension to Ext JS Library that is distributed under the terms of the GNU GPL v3 license. For any information, visit: http://www.sencha.com/license.
* 
* The original copyright notice of this file follows.*/
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

/**
 * @requires OpenLayers/Map.js
 */

Ext.namespace('mapfish.widgets');

/**
 * Class: mapfish.widgets.Shortcuts
 * Shorcuts to get the map zoomed to a preconfigured locations.
 *
 * Typical usage:
 * (start code)
 * var shortcuts = new mapfish.widgets.Shortcuts({
 *    el: 'myDiv',
 *    map: map,
 *    store: store,
 *    templates: {
 *        header: new Ext.Template("Choose a continent in the list"),
 *        footer: new Ext.Template("The map will automatically center to this location")
 *    }
 * });
 * (end)
 *
 * Inherits from:
 * - {Ext.Container}
 */

/**
 * Constructor: mapfish.widgets.Shortcuts
 *
 * Parameters:
 * config - {Object} The config object
 */
mapfish.widgets.Shortcuts = function(config) {
    Ext.apply(this, config);
    mapfish.widgets.Shortcuts.superclass.constructor.call(this);
}

Ext.extend(mapfish.widgets.Shortcuts, Ext.Container, {

    initComponent: function() {
        var combo = new Ext.form.ComboBox({
            name: 'shortcuts',
            hiddenName: '',
            store: this.store,
            valueField: 'value',
            displayField:'text',
            editable: false,
            mode: 'local',
            triggerAction: 'all',
            emptyText:'Select a value ...',
            lazyRender: true,
            width: 150
        });
        combo.on('select', this.recenter, this);

        this.items = combo;
        
        mapfish.widgets.Shortcuts.superclass.initComponent.call(this);
    },

    // private
    onRender: function(container, position) {
        if (!this.el) {
            this.el = document.createElement('div');
        }
        
        mapfish.widgets.Shortcuts.superclass.onRender.apply(this, arguments);
        
        this.initTemplates();
        this.applyTemplates();
    },

    // private
    initTemplates: function() {
        var ts = this.templates || {};
        
        if (!ts.header) {
            ts.header = new Ext.Template('some text before');
        }
        
        if (!ts.footer) {
            ts.footer = new Ext.Template('some text after');
        }

        this.templates = ts;
    },
    
    // private
    applyTemplates: function() {
        for (var i in this.templates) {
            var template = this.templates[i];
            
            var el = document.createElement("div");
            template.overwrite(el);

            switch (i) {
                case 'header':
                    template.insertBefore(this.el);
                    break;
                case 'footer':
                    template.insertAfter(this.el);
                    break;
            }
        }
    },
    
    // private
    recenter: function(combo, record) {
        this.map.zoomToExtent(record.get('bbox'));
    }
});
Ext.reg('shortcuts', mapfish.widgets.Shortcuts);
