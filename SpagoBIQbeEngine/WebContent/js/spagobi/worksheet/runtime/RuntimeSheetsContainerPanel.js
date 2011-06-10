/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
 * [description]
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 * refresh(sheets): remove all the sheets and add the new sheets
 * 
 * Public Events
 * 

 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeSheetsContainerPanel = function(config, sheets) { 
	
	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeSheetsContainerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetsContainerPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.config = config;
	this.sheetItems = [new Ext.Panel({})];
	if(sheets!= undefined && sheets!=null){
		this.sheetItems= this.buildSheets(config, sheets.sheets);
	}
	
	c ={
			border: false,
			tabPosition: 'bottom', 
	        enableTabScroll:true,
	        defaults: {autoScroll:true},
	        items: this.sheetItems
	};
	this.addEvents();
	Sbi.worksheet.runtime.RuntimeSheetsContainerPanel.superclass.constructor.call(this, c);	 
	
	//active the first tab after render
	this.on('render',function(){
		if(this.items.length>0){
			this.setActiveTab(0);
		}	
	}, this);
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetsContainerPanel, Ext.TabPanel, {
	
	sheetItems: null,
	sheetNumber: 0,
	//build the sheets
	buildSheets: function(config, sheetsConfig){
		var items = [];
		if(sheetsConfig!=undefined && sheetsConfig!=null){
			var i=0;
			for(; i<sheetsConfig.length; i++){
				items.push(new Sbi.worksheet.runtime.RuntimeSheetPanel(Ext.apply(config||{},{sheetConfig: sheetsConfig[i]})));
			}
		}
		return items;
	},
	
	refresh: function(sheets){
		if(sheets!=undefined && sheets!=null){
			this.removeAll(true);
			this.add(this.buildSheets(this.config, sheets.sheets));
		}
	},
	
	exportContent: function(){
		if(this.sheetItems!=undefined && this.sheetItems!=null){
			var i=0;			
			this.sheetNumber = this.sheetItems.length;
			for(; i<this.sheetItems.length; i++){
				if(this.sheetItems[i].contentLoaded == false){
					this.setActiveTab(i);
				}
			}
		}
		this.sleepUntilContentReady();
		var resultExport = this.exportRenderedContent();
		return resultExport;
	}
	
	,sleepUntilContentReady: function(){
		var contentReady = this.checkContentReady();
		if(contentReady == true){
			return true;
		}else{
			this.sleep(400);
			alert('Exporting');
			this.sleepUntilContentReady();
		}
	}
	
	, sleep: function (milliSeconds){
		var startTime = new Date().getTime(); // get the current time
		while (new Date().getTime() < startTime + milliSeconds); // hog cpu
	}

	,checkContentReady: function(){		
		var allLoaded = false;
		if(this.sheetItems!=undefined && this.sheetItems!=null){
			var i=0;
			for(; i<this.sheetItems.length; i++){
				if(this.sheetItems[i].contentLoaded == true){
					allLoaded = true
				}else{
					allLoaded = false;
					break;
				}
			}
		}
		
		if(allLoaded == true){
			return true;
		}else{
			return false;
		}
	}
	
	,exportRenderedContent: function(){
		var items = new Array();
		
		if(this.sheetItems!=undefined && this.sheetItems!=null){
			var i=0;
			for(; i<this.sheetItems.length; i++){
				if(this.sheetItems[i].contentLoaded == true){
					var exportedSheet = this.sheetItems[i].exportContent();
					items.push(exportedSheet);
				}
			}
		}
		var resultExport = { SHEETS_NUM: this.sheetItems.length,
							 EXPORTED_SHEETS: items};
		return resultExport;
	}
	
});