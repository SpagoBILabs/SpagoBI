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
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */


Ext.ns("Sbi.worksheet");

Sbi.worksheet.WorksheetEditorIframePanelExt3 = function(config) {

	var defaultSettings = {
		autoLoad : true
        , loadMask : true
        , frame : true
        , defaultSrc : 'about:blank'
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.worksheeteditoriframepanelext3) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.worksheeteditoriframepanelext3);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		defaultSrc : this.defaultSrc
		, loadMask: {msg: 'Loading...'}
		, fitToParent: true
        , tbar : this.toolbar
        , disableMessaging : false
        , frameConfig : {
			disableMessaging : false
        }	        
		, listeners : {
			
        	'message': {
        		fn: function(srcFrame, message) {
        			var messageName = message.tag;
        			if (messageName == 'worksheetexporttaberror') {
            			Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.worksheet.export.previewtab.msg'), LN('sbi.worksheet.export.previewtab.title')); 
        			}
        		}
        		, scope: this
        	}
        	
		}
	});
	
	// constructor
    Sbi.worksheet.WorksheetEditorIframePanelExt3.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.worksheet.WorksheetEditorIframePanelExt3, Ext.ux.ManagedIFramePanel, {
	
	datasetLabel : null
	, datasetParameters : null
	
	,
	getDatasetLabel : function () {
		return this.datasetLabel;
	}

	,
	setDatasetLabel : function (datasetLabel) {
		this.datasetLabel = datasetLabel;
	}
	
	,
	init : function () {
		this.initToolbar();
	}

	,
	initToolbar : function () {

		var saveButton = new Ext.Toolbar.Button({
			iconCls : 'icon-saveas' 
			, tooltip: LN('sbi.worksheet.worksheeteditoriframepanelext3.toolbar.saveas')
			, scope : this
    	    , handler : this.saveWorksheet
		});
		
	    var exportMenu = new Ext.menu.Menu({
			   items: [{
					text: LN('sbi.execution.PdfExport')
					, iconCls: 'icon-pdf' 
					, scope: this
					, width: 15
					, handler : function() { this.exportWorksheet('application/pdf'); }
			   }, {
					text: LN('sbi.execution.XlsExport')
					, iconCls: 'icon-xls' 
					, scope: this
					, width: 15
					, handler : function() { this.exportWorksheet('application/vnd.ms-excel'); }
			   }, {
					text: LN('sbi.execution.XlsxExport')
					, iconCls: 'icon-xlsx' 
					, scope: this
					, width: 15
					, handler : function() { this.exportWorksheet('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'); }
			   }]
		});
	    
	    var exportMenuButton = new Ext.Toolbar.Button({
			   id: Ext.id()
			   , tooltip: LN('sbi.worksheet.worksheeteditoriframepanelext3.toolbar.export')
			   , path: 'Exporters'	
			   , iconCls: 'icon-export' 	
			   , menu: exportMenu
			   , width: 15
			   , cls: 'x-btn-menubutton x-btn-text-icon bmenu '
		});	
		
		var items = ['->', saveButton, exportMenuButton];
		this.toolbar = new Ext.Toolbar({
			  items: items
		});
	}
	
	,
	exportWorksheet : function (mimeType) {
    	var thePanel = this.getFrame().getWindow().workSheetPanel;
    	var template = thePanel.validate();	
    	if (template == null){
    		return;
    	}
    	
		// must convert parameters into an array for the export service action
		var parameters = [];
		for (var name in this.datasetParameters) {
			var value = this.datasetParameters[name];
			parameters.push({
				name : name
				, value : value
				, description : value    // required for the export service action
			});
		}
		
    	thePanel.exportContent(mimeType, [], parameters);
	}
	
	,
	saveWorksheet : function() {
    	var thePanel = this.getFrame().getWindow().workSheetPanel;
    	var template = thePanel.validate();	
    	if (template == null){
    		return;
    	}
    	var templateJSON = Ext.util.JSON.decode(template);
		var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
		var documentWindowsParams = {
				'OBJECT_TYPE': 'WORKSHEET',
				//'template': wkDefinition,
				'OBJECT_WK_DEFINITION': wkDefinition,
				'MESSAGE_DET': 'DOC_SAVE_FROM_DATASET',
				'dataset_label': this.datasetLabel,
				'typeid': 'WORKSHEET' 
		};
		this.win_saveDoc = new Sbi.execution.SaveDocumentWindow(documentWindowsParams);
		this.win_saveDoc.show();
    
    }
	
});