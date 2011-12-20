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







	