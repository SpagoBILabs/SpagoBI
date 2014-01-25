/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 

	var defaultSettings = {		
		border: false,
		layout : 'card',
		activeItem : 0, //set to 1 if the dataset was already selected
		autoScroll : true
	};
		
	if(Sbi.settings && Sbi.cockpit && Sbi.cockpit.editor && Sbi.cockpit.editor.widgetEditorWizardPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.cockpit.editor.widgetEditorWizardPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
		
	this.init();
	
	c = Ext.apply(c, {
		items: this.pages,
		buttons: this.buttons
	}); 
	
	Sbi.cockpit.editor.WidgetEditorWizardPanel.superclass.constructor.call(this, c);
	
	this.addEvents('cancel', 'navigate', 'confirm');

};

/**
 * @class Sbi.xxx.Xxxx
 * @extends Ext.util.Observable
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.editor.WidgetEditorWizardPanel, Ext.Panel, {
	
	pages: null
	, buttons: null
	
	, getActivePage: function() {
		return this.layout.activeItem;
	}

	, getActivePageNumber: function() {
		return this.layout.activeItem.itemId;
	}
	
	, getPageNumber: function(page) {
		if(page) return page.itemId;
		return -1;
	}
	
	, getPageCount: function() {
		return this.items.length;
	}
	
	, isValidPageNumber: function(pageNumber) {
		return pageNumber >= 1 && pageNumber <= this.getPageCount();
	}
	
	, getPage: function(pageNumber) {
		var page = null;
		if(Sbi.isValorized(pageNumber) && this.isValidPageNumber(pageNumber)) {
			page = this.pages[pageNumber];
		}
		return page;
	}
	
	, moveToNextPage: function() {
		var activePageNumber =  this.getActivePageNumber();
		this.moveToPage(activePageNumber+1);
	}
	
	, moveToPreviousPage: function() {
		var activePageNumber =  this.getActivePageNumber();
		this.moveToPage(activePageNumber-1);
	}
	
	, moveToPage: function(targetPageNumber){	
		Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: IN");
		
		Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: target page number is equal to [" + targetPageNumber + "]");
		var activePageNumber =  this.getActivePageNumber();
		var totPageNumber  = this.getPageCount();
		var isTabValid = true;
		 
		if(this.isValidPageNumber(targetPageNumber) === false) {
			return;
		}
		
		Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: target page number is valid");
		
		if (this.isPageValid(targetPageNumber)){
			
			Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: target page is valid");
			
			this.layout.setActiveItem(targetPageNumber);
			 
			Ext.getCmp('move-prev').setDisabled(targetPageNumber==0);
			Ext.getCmp('move-next').setDisabled(targetPageNumber==totPageNumber);
		 	Ext.getCmp('confirm').setVisible(!(parseInt(activePageNumber)<parseInt(totPageNumber)));
		
		} else {
			Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: target page is not valid");
		}	
		
		Sbi.trace("[WidgetEditorWizardPanel.moveToPage]:Page [" + this.getActivePageNumber() + "] is now the active page");
				
		Sbi.trace("[WidgetEditorWizardPanel.moveToPage]: OUT");
	}
	
	, isPageValid: function(pageNumber) {
		Sbi.trace("[WidgetEditorWizardPanel.isPageValid]: IN");
		
		var page = this.getPage(pageNumber);
		if(Sbi.isNull(page)) {
			return false;
		}
		
		var isPageValid = true;
		if(Sbi.isValorized(page.isValid)) {
			isPageValid = isPageValid && page.isValid();
		}
		
		isPageValid = isPageValid && this.doPageValidation(page);
		
		Sbi.trace("[WidgetEditorWizardPanel.isPageValid]: OUT");
		
		return isPageValid;
	}
	
	, doPageValidation: function(page) {
		Sbi.trace("[WidgetEditorWizardPanel.doPageValidation]: IN");
		
		var isValid = true;
		
		Sbi.trace("[WidgetEditorWizardPanel.doPageValidation]: Page number is equal to [" + this.getPageNumber(page) + "]");
		if (this.getPageNumber(page) === 1){
			isValid = isValid && this.isDatasetBrowserPageValid();	
		}
		Sbi.trace("[WidgetEditorWizardPanel.doPageValidation]: OUT");
		
		return isValid;
	}
	
	, isDatasetBrowserPageValid: function() {
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: IN");
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: 0.dataset: " + this.pages[0].dataset);
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: 1.dataset: " + this.pages[1].dataset);
		
		if (this.pages[0].widget.dataset === undefined || this.pages[0].widget.dataset === null){
			alert('Per procedere e\' necessario selezionare un dataset!');
			return false;
		}
		Sbi.trace("[WidgetEditorWizardPanel.isDatasetBrowserPageValid]: OUT");
		return true;
	}

	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initPages();
		this.initButtons();
	}

	, initPages: function(){
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: IN");
		
		this.pages = new Array();
		
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: widget: " + this.widget);
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: widgetManager: " + this.widgetManager);
		
		var datasetsBrowserPage = new Sbi.cockpit.editor.WidgetEditorDatasetsBrowser({
			widgetManager: this.widgetManager
			, widget: this.widget
			, itemId: 0
		}); 
		datasetsBrowserPage.addListener('click', this.onClick, this);
		this.pages.push(datasetsBrowserPage);
		
		var widgetDesignerPage = new Sbi.cockpit.editor.WidgetEditor({
			itemId: 1
		});
		this.pages.push(widgetDesignerPage);
		
		Sbi.trace("[WidgetEditorWizardPanel.initPages]: OUT");
		
		return this.pages;
	}
	
	, initButtons: function() {
		var thisPanel = this;
		var buttonsBar = [];
		
		buttonsBar.push('->');
		buttonsBar.push({ id: 'move-prev',
	        text: LN('sbi.ds.wizard.back'),
	        handler: function(btn) {
	        	thisPanel.moveToNextPrevious();
	        }, 
	        scope: this,
	        disabled: true
	    });
		
		buttonsBar.push({id: 'move-next',
	        text:  LN('sbi.ds.wizard.next'),
	        handler: function(btn) {
	        	thisPanel.moveToNextPage();
	        }, scope: this
	    	});
		
		buttonsBar.push({id: 'confirm',
			hidden: true,
	        text:  LN('sbi.ds.wizard.confirm'),
	        handler: function(){
	        	thisPanel.fireEvent('confirm', this);   
	        }, scope: this            
	    	});
		
		buttonsBar.push({id: 'cancel',
	        text:  LN('sbi.ds.wizard.cancel'),
	        handler: function(){
	        	alert("fire event cancel");
	        	thisPanel.hide();  
	        	thisPanel.fireEvent('cancel', this);  
	        }, scope: this
	    	});
		
		this.buttons = buttonsBar;
		
		return buttonsBar;
	}

});
