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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.WorksheetPanel = function(config) { 

	
	var defaultSettings = {
			title: LN('sbi.worksheet.title')
	};
	
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.WorksheetPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.WorksheetPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.prevButton =  new Ext.Button({
		    text: '&laquo; Designer'
			, disabled: true
		});
	this.prevButton.on(
			'click',
			function (thePanel, attribute) { 
				this.setActiveItem(0);
			}, 
			this
	)
	
	
	this.nextButton =  new Ext.Button({
	    text: 'Preview &raquo;'
	});
	this.nextButton.on(
			'click',
			function (thePanel, attribute) { 
				this.setActiveItem(1);
			}, 
			this
	)
	
	this.initWorksheetPanel(config);
	
	// JavaScript Toolbar
	c = Ext.apply(c, {
			//id:'WorksheetPanel', 
			items: [this.worksheetDesignerPanel, this.worksheetPreviewPanel]
		    , enableDragDrop: true
		    , border: false
			, layout: 'card'
			, activeItem: 0
			, height: 100
			, style: 'margin-top: 0px; margin-left: auto; margin-right: auto;'
			, width: 250
			, tbar: ['->', this.prevButton, this.nextButton]
	});
	
	Sbi.worksheet.designer.QueryFieldsCardPanel.superclass.constructor.call(this, c);

	this.on('activate', function(meta){
		// recalculate current fields in store and fires validateInvalidFieldsAfterLoad event
		
		this.worksheetDesignerPanel.designToolsPanel.refresh();
		this.setActiveItem(0);
		this.worksheetDesignerPanel.designToolsPanel.on('validateInvalidFieldsAfterLoad', 
				function(){
			this.worksheetDesignerPanel.validate(function(){}, function(){}, this);
		}, this);
		
	}, this);
	
	
	};

Ext.extend(Sbi.worksheet.designer.WorksheetPanel, Ext.Panel, {
	
	worksheetDesignerPanel: null
	, worksheetPreviewPanel: null
	, prevButton: null
	, nextButton: null
	, initWorksheetPanel: function(config) {
		this.worksheetDesignerPanel = config.qbePanel.getWorksheetDesignerPanel(); 
		this.worksheetPreviewPanel = config.qbePanel.getWorksheetPreviewPanel();
	}
	, setActiveItem: function(pageIndex) {
		
		this.getLayout().setActiveItem( pageIndex );
		if(pageIndex == 0){
			this.prevButton.disable();
			this.nextButton.enable();
		}else{
			this.prevButton.enable();
			this.nextButton.disable();			
		}
	}

});
