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
		limit: 15	//number of rows for the page in the grid
	}
};

Sbi.settings.console.masterDetailWindow = {
	height: 350
};

Sbi.settings.console.storeManager = {
	limitSS: 15		//number of rows for the pagination server side
  , rowsLimit: 50	//number of rows totally managed by the console. It replace the original configuration <CONSOLE-TABLE-ROWS-LIMIT> presents in the engine-config 
};