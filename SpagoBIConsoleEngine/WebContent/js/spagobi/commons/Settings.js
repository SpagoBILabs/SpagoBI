/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Ext.ns("Sbi.settings");

Sbi.settings.console = {
  
		
	summaryPanel: {
		height: 300
	}		
		
	, chartWidget: {
		height: 130
	}

	, widgetPanel: {
		columnNumber:3
	}
    
	, gridPanel: {
		limit: 15		//number of rows for the page in the grid
	  , loadMask: true //enable the 'loading...' mask on the grid. Default false.
 	}
};

Sbi.settings.console.masterDetailWindow = {
	height: 350
};

Sbi.settings.console.storeManager = {
	limitSS: 15		//number of rows for the pagination server side
  , rowsLimit: 50	//number of rows totally managed by the console. It replace the original configuration <CONSOLE-TABLE-ROWS-LIMIT> presents in the engine-config 
};