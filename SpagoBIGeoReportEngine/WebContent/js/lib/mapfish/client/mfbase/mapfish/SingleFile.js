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

/*
 * This file is to be included in the [first] section of MapFish build
 * config file, but after OpenLayers initialization files.
 */

(function() {
    window.mapfish = {
        singleFile: true
    }

    // If OpenLayers and MapFish are built into a single library file
    // (MapFish.js) we need to fool OpenLayers and make it believe its script
    // name is MapFish.js instead of OpenLayers.js. Without this, OpenLayers
    // won't be able to find the path to its images and themes.

    var foolOpenLayers = true;
    
    var scripts = document.getElementsByTagName('script');
    for (var i = 0; i < scripts.length; i++) {
        var src = scripts[i].getAttribute('src');
        if (src && src.lastIndexOf("OpenLayers.js") > -1) {
            foolOpenLayers = false;
            break;
        }
    }

    if (foolOpenLayers) {
        // poor OpenLayers!
        window.OpenLayers._getScriptLocation = function() {
            return mapfish._getScriptLocation() + "../openlayers/";
        }
    }
})();
