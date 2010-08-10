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

(function() {

    // Some examples, like geostat and search, use XMLHttpRequest's. If those
    // XMLHttpRequest's target a different domain that the serving the page,
    // some proxy is needed. So do not set ProxyHost (and comment this line) if
    // the example pages are served by the same domain than that responding to
    // the XMLHttpRequest's. Otherwise set up a proxy script (for example using
    // the provided proxy.cgi script) and adjust the OpenLayers.ProxyHost
    // variable accordingly.

//OpenLayers.ProxyHost = "/cgi-bin/proxy.cgi?url=";

    // Define a constant with the base url to the MapFish web service. If you
    // want to rely on the MapFish services provided on demo.mapfish.org/1.0,
    // leave this variable untouched; otherwise modify it appropriately. You
    // may want to use ''.

//mapfish.SERVER_BASE_URL = 'http://demo.mapfish.org/mapfishsample/1.0/';
    mapfish.SERVER_BASE_URL = 'http://127.0.0.1:8080/servlet/mapfish?';
})();
