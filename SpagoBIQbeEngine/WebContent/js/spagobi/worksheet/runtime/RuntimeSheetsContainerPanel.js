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
	
	this.services = this.services || new Array();
	this.services['exportWorksheet'] = this.services['exportWorksheet'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'EXPORT_WORKSHEETS_ACTION'
		, baseParams: {'RESPONSE_TYPE' : 'RESPONSE_TYPE_ATTACHMENT', 'SBI_EXECUTION_ID': Sbi.config.serviceRegistry.getExecutionId()}
	});
	
	this.config = config;
	this.sheetItems = [ new Ext.Panel({}) ];
	this.worksheetAdditionalData = {};

	if (sheets != undefined && sheets != null) {
		this.sheetItems = this.buildSheets(config, sheets.sheets, sheets.fieldsOptions);
		this.worksheetAdditionalData.fieldsOptions = sheets.fieldsOptions;
	}
	

	c = {
		border : false,
		tabPosition : 'bottom',
		enableTabScroll : true,
		defaults : {
			autoScroll : true
		},
		items : this.sheetItems
	};
	
	this.addEvents('contentexported');
	
	Sbi.worksheet.runtime.RuntimeSheetsContainerPanel.superclass.constructor.call(this, c);	 
	
	//active the first tab after render
	this.on('render', function() {
		if (this.items.length > 0) {
			this.setActiveTab(0);
		}
	}, this);
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetsContainerPanel, Ext.TabPanel, {
	
	sheetItems: null
	, sheetNumber: 0
	//build the sheets
	
	,
	buildSheets: function(config, sheetsConfig, fieldsOptions){
		
		
		var items = [];
		if(sheetsConfig!=undefined && sheetsConfig!=null){
			var i=0;
			for(; i<sheetsConfig.length; i++){
				items.push(new Sbi.worksheet.runtime.RuntimeSheetPanel(Ext.apply(config||{},{sheetConfig: sheetsConfig[i], fieldsOptions: fieldsOptions})));
			}
		}
		return items;
	}
	
	,
	refresh: function(sheets){
		if(sheets!=undefined && sheets!=null){
			this.removeAll(true);
			this.add(this.buildSheets(this.config, sheets.sheets));
		}
	}
	
	,
	exportContent : function(mimeType, fromDesigner, metadata) {
		// make sure all the sheets have been displayed (necessary for charts' export)
		if (this.sheetItems !== undefined && this.sheetItems !== null) {
			var i = 0;
			this.sheetNumber = this.sheetItems.length;
			for (; i < this.sheetItems.length; i++) {
				if (this.sheetItems[i].contentLoaded === false) {
					// register to the contentloaded event
					this.sheetItems[i].on('contentloaded', function () {this.exportContent.defer(500, this, [mimeType, fromDesigner, metadata]); }, this);
					this.setActiveTab(i);
					return;
				}
			}
		}
		this.doExportContent(mimeType, fromDesigner, metadata);
	}

	,
	doExportContentOld : function(mimeType, fromDesigner) {

		var resultExport = this.exportRenderedContent(mimeType);

		var worksheetDataEncoded = Ext.encode(resultExport);
		
	    Ext.DomHelper.useDom = true; // need to use dom because otherwise an html string is composed as a string concatenation, 
					 // but, if a value contains a " character, then the html produced is not correct!!! 
					 // See source of DomHelper.append and DomHelper.overwrite methods
					 // Must use DomHelper.append method, since DomHelper.overwrite use HTML fragments in any case.
		var dh = Ext.DomHelper;
		
		var form = document.getElementById('export-worksheet-form');
		if (!form) {
			form = dh.append(Ext.getBody(), { // creating the hidden form
				id: 'export-crosstab-form'
				, tag: 'form'
				, method: 'post'
				, cls: 'export-form'
			});
			dh.append(form, {					// creating WORKSHEETS hidden input in form
				tag: 'input'
				, type: 'hidden'
				, name: 'WORKSHEETS'
				, value: ''  // do not put WORKSHEETS value now since DomHelper.overwrite does not work properly!!
			});
			dh.append(form, {					// creating MIME_TYPE hidden input in form
				tag: 'input'
				, type: 'hidden'
				, name: 'MIME_TYPE'
				, value: mimeType  
			});
		}
		// putting the crosstab data into CROSSTAB hidden input
		form.elements[0].value = worksheetDataEncoded;
		form.action = this.services['exportWorksheet'];
		form.target = '_blank';				// result into a new browser tab
		form.submit();
		
		// notify the exporting service has been invoked (in order to hide the load-mask)
		
		if(fromDesigner){
			this.fireEvent('contentexported');
			
		}else{
			sendMessage({}, 'contentexported'); 
		}
	}
	
	, doExportContent : function(mimeType, fromDesigner, metadata) {
		

		var resultExport = this.exportRenderedContent(mimeType);

		var worksheetDataEncoded = Ext.encode(resultExport);
		var worksheetMetadataEncoded = Ext.encode(metadata);
		
	    Ext.DomHelper.useDom = true; // need to use dom because otherwise an html string is composed as a string concatenation, 
					 // but, if a value contains a " character, then the html produced is not correct!!! 
					 // See source of DomHelper.append and DomHelper.overwrite methods
					 // Must use DomHelper.append method, since DomHelper.overwrite use HTML fragments in any case.
		var dh = Ext.DomHelper;
		
		//alert('debug 1: ' + mimeType);
		//alert('debug 1: ' + worksheetDataEncoded);
		
		var form = document.getElementById('export-worksheet-form');
		if (!form) {
			form = dh.append(Ext.getBody(), { // creating the hidden form
				id: 'export-crosstab-form'
				, tag: 'form'
				, method: 'post'
				, cls: 'export-form'
			});
			dh.append(form, {					// creating WORKSHEETS hidden input in form
				tag: 'input'
				, type: 'hidden'
				, name: 'WORKSHEETS'
				, value: ''  // do not put WORKSHEETS value now since DomHelper.overwrite does not work properly!!
			});
			dh.append(form, {					// creating MIME_TYPE hidden input in form
				tag: 'input'
				, type: 'hidden'
				, name: 'METADATA'
				, value: ''  // do not put METADATA value now since DomHelper.overwrite does not work properly!!
			});
			dh.append(form, {					// creating MIME_TYPE hidden input in form
				tag: 'input'
				, type: 'hidden'
				, name: 'MIME_TYPE' 
				, value: mimeType  
			});
		}
		
		
		// putting the crosstab data into CROSSTAB hidden input
		form.elements[0].value = worksheetDataEncoded;
		form.elements[1].value = worksheetMetadataEncoded;
		form.action = this.services['exportWorksheet'];
		form.target = '_blank';				// result into a new browser tab
		form.submit();
		// notify the exporting service has been invoked (in order to hide the load-mask)
		
		if(fromDesigner){
			this.fireEvent('contentexported');
		}else{
			sendMessage({}, 'contentexported'); 
		}
	}
	
	, exportRenderedContent : function(mimeType) {
		var items = new Array();

		if (this.sheetItems != undefined && this.sheetItems != null) {
			var i = 0;
			for (; i < this.sheetItems.length; i++) {
				if (this.sheetItems[i].contentLoaded == true) {
					var exportedSheet = this.sheetItems[i].exportContent(mimeType);
					items.push(exportedSheet);
				}
			}
		}
		var resultExport = {
			SHEETS_NUM : this.sheetItems.length,
			EXPORTED_SHEETS : items,
			WORKSHEETS_ADDITIONAL_DATA: this.worksheetAdditionalData
		};
		return resultExport;
	}
	
	,getAdditionalData: function(){
		var data = new Array();
		if (this.sheetItems != undefined && this.sheetItems != null) {
			var i = 0;
			for (; i < this.sheetItems.length; i++) {
				data. push(this.sheetItems[i].getAdditionalData());
			}
		}
		return data;
	}
	
});