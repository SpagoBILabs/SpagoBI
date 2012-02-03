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
	Sbi.worksheet.designer.DesignSheetFiltersEditWizard.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.worksheet.designer.DesignSheetFiltersEditWizard, Ext.Window, {
	detailsFormPanel: null, //the form panel
	splittingFilterCheckboxGroup: null,
	splittingFilterCheckbox: null,
	splittingFilterLabel: null,
	splittingFilter: null, //the split filter of the sheet
	row: null// the row of the sheet
	
	, init: function(){

		this.splittingFilterCheckbox = new Ext.form.Checkbox({
			name: 'splittingFilter'
		});

		this.splittingFilterLabel = new Ext.form.Label({
			text: '',
			hidden: true
		});

		this.splittingFilterCheckboxGroup = new Ext.form.CheckboxGroup({
		    fieldLabel: LN('sbi.worksheet.designer.designsheetfilterseditwizard.splittingFilter'),
		    columns: 1,
		    items: [
		            this.splittingFilterCheckbox ,
		            this.splittingFilterLabel
		    ]
		});
		
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
	            },
	            this.splittingFilterCheckboxGroup

			]
			, buttons: [{
				text: LN('sbi.worksheet.designer.designsheetfilterseditwizard.apply')
			    , handler: function() {
			    	Ext.apply(this.row.data, this.getFormState());
		    		this.fireEvent('apply', this);
	            	this.hide();
	        	}
	        	, scope: this
		    },{
			    text: LN('sbi.worksheet.designer.designsheetfilterseditwizard.cancel')
			    , handler: function(){ 
			    	this.fireEvent('cancel', this);
			    	this.hide(); 
			    }
	        	, scope: this
			}]
		});
	}
	
	, getFormState: function() {
		var values = this.detailsFormPanel.getForm().getValues();
		if ( this.splittingFilterCheckbox.hidden.valueOf() || !this.splittingFilterCheckbox.getValue().valueOf() ) {
			values.splittingFilter = 'off';
		} else {
			values.splittingFilter = 'on';
		}
		
		if ( !this.splittingFilterCheckbox.hidden.valueOf() && values.splittingFilter == 'off' ) {
			this.splittingFilter = null;
		} else if (values.splittingFilter == 'on') {
			this.splittingFilter = this.row;
		}
		return values;
	}
	
	, setFormState: function(values) {
		this.detailsFormPanel.getForm().reset(); // it is mandatory, since setValues method does not work properly for checkboxes
		this.detailsFormPanel.getForm().setValues(values);
	}
	
	, setRowState: function(row) {
		this.row = row;
		this.setFormState(row.data);
	}
	
	, getSplitFilter: function() {
		return this.splittingFilter;
	}
	
	,setSplitFilter: function(splittingFilter){
		this.splittingFilter = splittingFilter;
		if (this.splittingFilter!= undefined && this.splittingFilter!= null && this.splittingFilter.data.id!=this.row.data.id){
			this.splittingFilterCheckbox.hide();
			this.splittingFilterLabel.setText(this.splittingFilter.data.alias);
			this.splittingFilterLabel.show();
		}else{
			this.splittingFilterCheckbox.show();
			this.splittingFilterLabel.hide();
		}
	}
	
});