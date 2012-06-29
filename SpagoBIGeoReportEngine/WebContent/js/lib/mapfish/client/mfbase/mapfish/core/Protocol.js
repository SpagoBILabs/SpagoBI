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
 * Copyright (C) 2007  Camptocamp
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
 * Namespace: mapfish.Protocol
 * Contains convenience methods for protocol manipulation.
 */
mapfish.Protocol = {

    /**
     * APIFunction: decorateProtocol
     * Decorate a protocol.
     *
     * Example of use:
     * (start code)
     * var protocol = mapfish.Protocol.decorateProtocol({
     *     protocol: protocol,
     *     TriggerEventDecorator: {
     *         eventListeners: {
     *             crudfinished: function() {
     *                 alert("CRUD operation completed");
     *             }
     *         }
     *     },
     *     MergeFilterDecorator: null
     * });
     * (end)
     *
     * Parameters:
     * config - {Object} Config object specifying how protocol must be
     *     decorated, see the above the example.
     *
     * Returns:
     * {<OpenLayers.Protocol>} The resulting protocol.
     * */
    decorateProtocol: function(config) {
        var protocol = config.protocol;
        for (var key in config) {
            if (key != "protocol") {
                if (!mapfish.Protocol[key]) {
                    OpenLayers.Console.error(
                        "mapfish.Protocol." + key + " does not exist");
                } else {
                    protocol = new mapfish.Protocol[key](
                        OpenLayers.Util.extend(
                            {protocol: protocol}, config[key])
                    );
                }
            }
       }
       return protocol;
    }
};
