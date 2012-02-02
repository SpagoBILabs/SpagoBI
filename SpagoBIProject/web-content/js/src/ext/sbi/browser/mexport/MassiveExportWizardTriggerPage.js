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
 *  [list]
 *
 *
 * Public Events
 *
 *  [list]
 *
 * Authors
 *
 * - Giulio gavardi (giulio.gavardi@eng.it)
 */

Ext.ns("Sbi.browser.mexport");

Sbi.browser.mexport.MassiveExportWizardTriggerPage = function(config) {

	var defaultSettings = {
			//title: LN('Sbi.browser.mexport.massiveExportWizardTriggerPage.title')
			layout: 'fit'
			, width: 800
			, height: 300           	
			, closable: true
			, constrain: true
			, hasBuddy: false
			, resizable: true
	};
	if (Sbi.settings && Sbi.settings.browser 
			&& Sbi.settings.browser.mexport && Sbi.settings.browser.mexport.massiveExportWizardTriggerPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.browser.mexport.massiveExportWizardTriggerPage);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);

	this.services = this.services || new Array();

//	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null, TYPE: 'WORKSHEET'};
//	this.services['StartMassiveExportExecutionProcessAction'] = this.services['StartMassiveExportExecutionProcessAction'] || Sbi.config.serviceRegistry.getServiceUrl({
//		serviceName: 'START_MASSIVE_EXPORT_EXECUTION_PROCESS_ACTION'
//		, baseParams: new Object()
//	});	
	
	
	//this.addEvents();
	
	this.initMainPanel(c);	
	c = Ext.apply(c, {
		layout: 'fit'
		, items: [this.mainPanel]	
	});

	// constructor
	Sbi.browser.mexport.MassiveExportWizardTriggerPage.superclass.constructor.call(this, c);
	
	
	this.addEvents('select', 'unselect');
	
	this.on('select', this.onSelection, this);
	this.on('unselect', this.onDeselection, this);	
};

Ext.extend(Sbi.browser.mexport.MassiveExportWizardTriggerPage, Ext.Panel, {

	sservices: null
    , mainPanel: null
    , currentPage: null
    
    
	// ----------------------------------------------------------------------------------------
	// public methods
	// ----------------------------------------------------------------------------------------

	, onSelection: function() {
		this.currentPage = true;
		this.wizard.setPageTitle('Trigger', 'Setup trigger\'s configuration');
	}
	
	, onDeselection: function() {
		this.currentPage = false;
	}
	
	, isTheCurrentPage: function() {
		return this.currentPage;
	}
	
	, getPageIndex: function() {
		var i;		
		for(i = 0; i < this.wizard.pages.length; i++) {
			if(this.wizard.pages[i] == this) break;
		}		
		return i;
	}
	
	, getPreviousPage: function() {
		var pages = this.wizard.pages;
		var i = this.getPageIndex();
		return (i != 0)? this.wizard.pages[i-1]: null;
	}
	
	, getNextPage: function() {
		var pages = this.wizard.pages;
		var i = this.getPageIndex();
		return (i != (pages.length-1))? this.wizard.pages[i+1]: null;
	}
	

	, getContent: function() {
		var state;
		
		state = {};

		return state;
	}
	
    // ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------

    , initMainPanel: function() {
		this.mainPanel = new Ext.FormPanel({
			labelWidth: 75, // label settings here cascade unless overridden
		    frame:true,
		    bodyStyle:'padding:5px 5px 0',
		    width: 350,
		    autoScroll: true,
	        items: [
	        {
	            xtype:'fieldset',
	            //checkboxToggle:true,
	            collapsible: true,
	            collapsed: false,
	            title: 'General info',
	            autoHeight:true,
	            defaults: {width: 210},
	            defaultType: 'textfield',
	            items :[{
	                    fieldLabel: 'Name',
	                    name: 'name',
	                    allowBlank:false
	                },{
	                    fieldLabel: 'Description',
	                    name: 'description'
	                },{
	                    fieldLabel: 'Start date',
	                    name: 'startDate'
	                }, {
	                    fieldLabel: 'Start time',
	                    name: 'startTime'
	                },{
	                    fieldLabel: 'End date',
	                    name: 'endDate'
	                }, {
	                    fieldLabel: 'End time',
	                    name: 'endTime'
	                }
	            ]
	        },{
	            xtype:'fieldset',
	            checkboxToggle:true,
	            title: 'Per minute Execution',
	            autoHeight:true,
	            defaults: {width: 210},
	            defaultType: 'textfield',
	            collapsed: true,
	            items :[{
	            	fieldLabel: 'Every n minutes',
	                name: 'first',
	                allowBlank:false
	            }]
	        },{
	            xtype:'fieldset',
	            title: 'Per hour execution ',
	            checkboxToggle:true,
	            //collapsible: true,
	            collapsed: true,
	            autoHeight:true,
	            defaults: {width: 210},
	            defaultType: 'textfield',
	            items :[{
	            	fieldLabel: 'Every n hours',
	                name: 'first',
	                allowBlank:false
	            }]
	        }]
		});
    }	
});