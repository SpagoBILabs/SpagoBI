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