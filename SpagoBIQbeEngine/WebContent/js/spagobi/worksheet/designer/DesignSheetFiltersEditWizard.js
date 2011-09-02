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

Sbi.worksheet.designer.DesignSheetFiltersEditWizard = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.designsheetfilterseditwizard.title')
		, frame: true
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.designSheetFiltersEditWizard) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.designSheetFiltersEditWizard);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		title: this.title
		, width: 350
        , items:[this.detailsFormPanel]
	});

	// constructor	
	Sbi.worksheet.designer.DesignSheetFiltersPanel.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.worksheet.designer.DesignSheetFiltersEditWizard, Ext.Window, {
	detailsFormPanel: null //the form panel

	
	, init: function(){
		this.detailsFormPanel = new Ext.form.FormPanel({
			frame: true
			, items: [
	            {	//Radio group Multi/Single selection
		            xtype: 'radiogroup'
		            , fieldLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.selection')
		            , vertical: true
		            , items: [
		                {boxLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.siglevalue'), name: 'selection', inputValue: 'singlevalue'}
		                , {boxLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.multivalue'), name: 'selection', inputValue: 'multivalue', checked: true}
		            ]
	            }
	            , {//Radio group mandatory yes/no
		            xtype: 'radiogroup'
		            , fieldLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.mandatory')
		            , itemCls: 'x-check-group-alt'
		            , items: [
		                  {boxLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.mandatory.yes'), name: 'mandatory', inputValue: 'yes'}
		                , {boxLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.mandatory.no'), name: 'mandatory', inputValue: 'no', checked: true}
		            ]
	            }
			]
			, buttons: [{
				text: LN('sbi.worksheet.designer.designsheetfilterseditwizard.apply')
			    , handler: function() {
		    		this.fireEvent('apply', this.getFormState(), this);
	            	this.hide();
	        	}
	        	, scope: this
		    },{
			    text: LN('sbi.worksheet.designer.designsheetfilterseditwizard.cancel')
			    , handler: function(){ this.hide(); }
	        	, scope: this
			}]
		});
	}
	
	, getFormState: function() {
		return this.detailsFormPanel.getForm().getValues();
	}
	
	, setFormState: function(values) {
		this.detailsFormPanel.getForm().reset(); // it is mandatory, since setValues method does not work properly for checkboxes
		this.detailsFormPanel.getForm().setValues(values);
	}
	
	
});