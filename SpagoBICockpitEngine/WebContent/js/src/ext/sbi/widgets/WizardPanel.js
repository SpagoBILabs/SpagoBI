/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.widgets");

Sbi.widgets.WizardPanel = function(config) { 

	Sbi.trace("[WizardPanel.constructor]: IN");
	
	var defaultSettings = {		
		border: false,
		layout : 'card',
		activeItem : 0, 
		autoScroll : true
	};
		
	if(Sbi.settings && Sbi.widgets && Sbi.widgets.wizardPanel) {
		defaultSettings = Ext.apply(defaultSettings,  Sbi.widgets.wizardPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
		
	this.init();
	
	c = Ext.apply(c, {
		items: this.pages,
		buttons: this.buttons
	}); 
	
	Sbi.trace("[WizardPanel.constructor]: call parent costructor");
	Sbi.widgets.WizardPanel.superclass.constructor.call(this, c);
	Sbi.trace("[WizardPanel.constructor]: parent costructor succesfully called");
	
	this.addEvents('navigate', 'cancel', 'confirm');
	
	Sbi.trace("[WizardPanel.constructor]: OUT");
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
Ext.extend(Sbi.widgets.WizardPanel, Ext.Panel, {
	
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
		return pageNumber >= 0 && pageNumber <= this.getPageCount();
	}
	
	, getPage: function(pageNumber) {
		var page = null;
		if(Sbi.isValorized(pageNumber) && this.isValidPageNumber(pageNumber)) {
			page = this.pages[pageNumber];
		}
		return page;
	}
	
	, moveToNextPage: function() {
		var activePageNumber = this.getActivePageNumber();
		return this.moveToPage(activePageNumber+1);
	}
	
	, moveToPreviousPage: function() {
		var activePageNumber =  this.getActivePageNumber();
		return this.moveToPage(activePageNumber-1);
	}
	
	, moveToPage: function(targetPageNumber){	
		Sbi.trace("[WizardPanel.moveToPage]: IN");
		
		Sbi.trace("[WizardPanel.moveToPage]: target page number is equal to [" + targetPageNumber + "]");
		var activePageNumber =  this.getActivePageNumber();
		var totPageNumber  = this.getPageCount()-1;
		var isTabValid = true;
		 
		if(this.isValidPageNumber(targetPageNumber) === false) {		
			return;
		}
		
		Sbi.trace("[WizardPanel.moveToPage]: target page number is valid");
		
		if (this.doPageValidation(targetPageNumber)){
			
			Sbi.trace("[WizardPanel.moveToPage]: target page is valid");
			
			this.layout.setActiveItem(targetPageNumber);
			Ext.getCmp('move-prev').setDisabled(targetPageNumber==0);
			Ext.getCmp('move-next').setDisabled(targetPageNumber==totPageNumber);
		 	Ext.getCmp('confirm').setVisible(!(parseInt(targetPageNumber)<parseInt(totPageNumber)));
		
		} else {
			Sbi.trace("[WizardPanel.moveToPage]: target page is not valid");
		}	
		
		Sbi.trace("[WizardPanel.moveToPage]:Page [" + this.getActivePageNumber() + "] is now the active page");
				
		Sbi.trace("[WizardPanel.moveToPage]: OUT");
		
		return this.getActivePage();
	}
	
	, doPageValidation: function(pageNumber) {
		Sbi.trace("[WizardPanel.doPageValidation]: IN");
		
		var page = this.getPage(pageNumber);
		if(Sbi.isNull(page)) {
			return false;
		}
		
		var isPageValid = true;
		if(Sbi.isValorized(page.isValid)) {
			isPageValid = isPageValid && page.isValid();
		}
		
		isPageValid = isPageValid && this.isPageValid(page);
		
		Sbi.trace("[WizardPanel.doPageValidation]: OUT");
		return isPageValid;
	}
	
	, getState: function() {
		Sbi.trace("[WizardPanel.getState]: IN");
		var state = {};
		for(var i = 0; i < this.getPageCount(); i++) {
			var page = this.getPage(i);
			if(page.applyPageState) {
				
				Sbi.trace("[WizardPanel.getState]: apply page [" + i + "] state");
				state = page.applyPageState(state);
			}
		}
		
		Sbi.trace("[WizardPanel.getState]: state is equal to [" + Sbi.toSource(state) + "]");
		
		Sbi.trace("[WizardPanel.getState]: OUT");
		return state;
	}
	
	, setState: function(state) {
		for(var i = 0; i < this.getPageCount(); i++) {
			var page = this.getPage(i);
			if(page.setState) {
				page.setState(state);
			}
		}
	}
	
	
	/**
	 * @method 
	 * @abstract 
	 * 
	 * TODO override it in subclasses
	 * 
	 * Validate the target page
	 */
	, isPageValid: function(page) {
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
		Sbi.trace("[WizardPanel.init]: IN");
		this.initPages();
		this.initButtons();
		Sbi.trace("[WizardPanel.init]: OUT");
	}

	/**
	 * @method 
	 * @abstract 
	 * 
	 * TODO override it in subclasses
	 * 
	 * Initialize the pages contained in this wizard
	 */
	, initPages: function(){
		Sbi.trace("[WizardPanel.initPages]: IN");
		
		this.pages = new Array();
		
		
		var page1 = new Ext.Panel({
			itemId: 0
			, html: 'Page 1'
		}); 
		this.pages.push(page1);
		
		var page2 = new Ext.Panel({
			itemId: 1
			, html: 'Page 2'
		}); 
		this.pages.push(page2);
		
		var page3 = new Ext.Panel({
			itemId: 2
			, html: 'Page 3'
		}); 
		this.pages.push(page3);
		
		Sbi.trace("[WizardPanel.initPages]: OUT");
		
		return this.pages;
	}
	
	, initButtons: function() {
		Sbi.trace("[WizardPanel.initButtons]: IN");
		
		var buttonsBar = [];
		
		buttonsBar.push('->');
		buttonsBar.push({ 
			id: 'move-prev'
	        ,text: LN('sbi.ds.wizard.back')
	        , handler: this.onMovePrevious 
	        , scope: this
	        , disabled: (this.activeItem == 0)?true:false
	    });
		
		buttonsBar.push({
			id: 'move-next'
	        , text:  LN('sbi.ds.wizard.next')
	        , handler: this.onMoveNext
	        , scope: this
	        , disabled: (this.activeItem == 0)?false:true
	    });
		
		buttonsBar.push({
			id: 'confirm'
			, hidden: true
	        , text:  LN('sbi.ds.wizard.confirm')
	        , handler: this.onConfirm
	        , scope: this
	    });
		
		buttonsBar.push({
			id: 'apply'
			, hidden: true
	        , text:  LN('sbi.ds.wizard.apply')
	        , handler: this.onApply
	        , scope: this
	    });
		
		buttonsBar.push({
			id: 'cancel'
	        , text:  LN('sbi.ds.wizard.cancel')
	        , handler: this.onCancel
	        , scope: this
	    });
		
		this.buttons = buttonsBar;
		
		Sbi.trace("[WizardPanel.initButtons]: OUT");
		
		return buttonsBar;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onMoveNext: function() {
		Sbi.trace("[WizardPanel.onMoveNext]: IN");
		var page = this.moveToNextPage();
		// pass the current wizard state to the active page so it cat refresh if needed
		// its content. This is useful if some info contained in the active page depends uppon
		// values inserted by user in some other page of the wizard
		if (page.updateValues){
			var wizardState = this.getState();
			page.updateValues(wizardState);
		}
		Sbi.trace("[WizardPanel.onMoveNext]: OUT");
	}
	
	, onMovePrevious: function() {
		Sbi.trace("[WizardPanel.onMovePrevious]: IN");
		var page  = this.moveToPreviousPage();
		// pass the current wizard state to the active page so it cat refresh if needed
		// its content. This is useful if some info contained in the active page depends uppon
		// values inserted by user in some other page of the wizard
		if (page.updateValues){
			var wizardState = this.getState();
			page.updateValues(wizardState);
		}
		Sbi.trace("[WizardPanel.onMovePrevious]: OUT");
	}

	, onCancel: function() {
		Sbi.trace("[WizardPanel.onCancel]: IN");
		var page = this.fireEvent('cancel', this);  
		Sbi.trace("[WizardPanel.onCancel]: OUT");
	}
	
	, onApply: function() {
		Sbi.trace("[WizardPanel.onApply]: IN");
		this.fireEvent('apply', this);  
		Sbi.trace("[WizardPanel.onApply]: OUT");
	}

	, onConfirm: function() {
		Sbi.trace("[WizardPanel.onConfirm]: IN");
		this.fireEvent('confirm', this, this.getState());  
		Sbi.trace("[WizardPanel.onConfirm]: OUT");
	}
});
