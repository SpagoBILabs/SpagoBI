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







	