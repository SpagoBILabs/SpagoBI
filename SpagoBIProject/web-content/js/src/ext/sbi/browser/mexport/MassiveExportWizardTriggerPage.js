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

	services: null
    , mainPanel: null
    , generalConfFields: null
	, cronConfFields: null
    , currentPage: null
    , generalInfoFieldSet: null
    , perMinuteOptionsFieldSet: null
    , perHoureOptionsFieldSet: null
    
    
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
	
	, getName: function(){
		return 'Sbi.browser.mexport.MassiveExportWizardTriggerPage';
	}
	
	, getContent: function() {
		var state;
		
		state = {};
		
		state.generalConf = {}; // job + trigger			
		for(var fieldSet in this.generalConfFields) {
			state.generalConf[fieldSet] = {};
			var fieldsInFieldSet = this.generalConfFields[fieldSet];
			for(var i = 0; i < fieldsInFieldSet.length; i++) {
				var field = fieldsInFieldSet[i];
				state.generalConf[fieldSet][field.getName()] = field.getValue();
			}
		}
		
		state.cronConf = {};
		for(var fieldSet in this.cronConfFields) {
			state.cronConf[fieldSet] = {};
			var fieldsInFieldSet = this.cronConfFields[fieldSet];
			for(var i = 0; i < fieldsInFieldSet.length; i++) {
				var field = fieldsInFieldSet[i];
				state.cronConf[fieldSet][field.getName()] = field.getValue();
			}
		}
		
		return state;
	}
	
    // ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------

    , initMainPanel: function() {
    	
    	this.initGeneralConfFieldSet();
    	this.initCronConfFiledSet();
    	
    	
    	
		this.mainPanel = new Ext.FormPanel({
			labelWidth: 75, // label settings here cascade unless overridden
		    frame:true,
		    bodyStyle:'padding:5px 5px 0',
		    width: 350,
		    autoScroll: true,
	        items: [
				this.generalInfoFieldSet
				, this.perMinuteOptionsFieldSet
				, this.perHoureOptionsFieldSet
			]
		});
    }	
    
    /**
     * Initialize general info conf (job + trigger)
     */
    , initGeneralConfFieldSet: function() {
    	var field;

    	this.generalConfFields = {};
    	this.initJobConfFields();
    	this.initTriggerConfFields();
    	
    	var fields = this.generalConfFields['job'].concat(this.generalConfFields['trigger']);
    	
    	this.generalInfoFieldSet = new Ext.form.FieldSet({
    		//checkboxToggle:true,
            collapsible: true,
            collapsed: false,
            title: 'General info',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            items : fields
    	});
    }
    
    , initJobConfFields: function() {
    	
    	this.generalConfFields['job'] = [];
    	
    	field = new Ext.form.TextField({
            fieldLabel: 'Name',
            name: 'name',
            allowBlank:false
        });
    	this.generalConfFields['job'].push(field);
    	
    	field = new Ext.form.TextField({
            fieldLabel: 'Description',
            name: 'description',
            allowBlank:false
        });
    	this.generalConfFields['job'].push(field);
    }
    
    
    , initTriggerConfFields: function() {
    	
    	this.generalConfFields['trigger'] = [];
    	
    	field = new Ext.form.DateField({
            fieldLabel: 'Start date',
            name: 'startDate',
            format: Sbi.config.localizedDateFormat || 'm/d/Y',
            allowBlank:false
        });
    	this.generalConfFields['trigger'].push(field);
    	
    	field = new Ext.form.TimeField({
    		fieldLabel: 'Start time',
            name: 'startTime',
            maxHeight: 180,
            //format: 'H:i',
            increment: 30,
            allowBlank:true
        });
    	this.generalConfFields['trigger'].push(field);
    	
    	field = new Ext.form.DateField({
    		fieldLabel: 'End date',
            name: 'endDate',
            format: Sbi.config.localizedDateFormat || 'm/d/Y',
			allowBlank:true
    	});
    	this.generalConfFields['trigger'].push(field);
    	
    	field = new Ext.form.TimeField({
   		 	fieldLabel: 'End time',
            name: 'endTime',
            maxHeight: 180,
            //format: 'H:i',
            increment: 30,
            allowBlank:true
    	});
    	this.generalConfFields['trigger'].push(field);
    }
    
    
    /**
     * Initialize cron conf (minute, hourly, weekly, monthly, yearly)
     */    
    , initCronConfFiledSet: function() {
    	this.cronConfFields = {};
    	this.initMinuteConfFieldSet();
    	this.initPerHourlyConfFieldSet();
    }
    
    , initMinuteConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['minutes'] = [];
    	
    	field = new Ext.form.NumberField({
    		fieldLabel: 'Every n minutes',
            name: 'minutes',
            allowBlank:false
    	});
    	this.cronConfFields['minutes'].push(field);
    	
    	this.perMinuteOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Minutes',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['minutes']
    	});	
    }
    
    , initPerHourlyConfFieldSet: function() {
    	var field;
    	
    	this.cronConfFields['hourly'] = [];
    	
    	field = new Ext.form.NumberField({
    		fieldLabel: 'Every n houres',
            name: 'houres',
            allowBlank:false
    	});
    	this.cronConfFields['hourly'].push(field);
    	
    	this.perHoureOptionsFieldSet = new Ext.form.FieldSet({
    		checkboxToggle:true,
            title: 'Hourly',
            autoHeight:true,
            defaults: {width: 210},
            defaultType: 'textfield',
            collapsed: true,
            items : this.cronConfFields['hourly']
    	});	
    }
});