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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.crosstab");

Sbi.crosstab.CrosstabDetailsWizard = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.crosstab.crosstabdetailswizard.title')
		, width: 300
  	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabDetailsWizard) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabDetailsWizard);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	this.init(c);
	
	c = Ext.apply(c, {
		closeAction: 'hide'
      	, items: [this.crosstabDetailsForm]
	});
	
	// constructor
    Sbi.crosstab.CrosstabDetailsWizard.superclass.constructor.call(this, c);
	
    this.addEvents('apply');
    
};

Ext.extend(Sbi.crosstab.CrosstabDetailsWizard, Ext.Window, {
    
	crosstabDetailsForm: null
	
	, init: function(c) {
	
		this.crosstabDetailsForm = new Ext.form.FormPanel({
			frame: true
			, items: [
	            {
		            xtype: 'radiogroup'
		            , fieldLabel: LN('sbi.crosstab.crosstabdetailswizard.measureson')
		            , items: [
		                {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.measuresonrows'), name: 'measureson', inputValue: 'rows'}
		                , {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.measuresoncolumns'), name: 'measureson', inputValue: 'columns', checked: true}
		            ]
	            }
	            , {
	                xtype: 'checkboxgroup',
	                fieldLabel: LN('sbi.crosstab.crosstabdetailswizard.onrows'),
	                itemCls: 'x-check-group-alt',
	                columns: 1,
	                items: [
	                    {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.calculatetotalsonrows'), name: 'calculatetotalsonrows'}
	                    , {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.calculatesubtotalsonrows'), name: 'calculatesubtotalsonrows'}
	                ]
	            }
	            , {
	                xtype: 'checkboxgroup',
	                fieldLabel: LN('sbi.crosstab.crosstabdetailswizard.oncolumns'),
	                columns: 1,
	                items: [
	                    {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.calculatetotalsoncolumns'), name: 'calculatetotalsoncolumns'}
	                    , {boxLabel: LN('sbi.crosstab.crosstabdetailswizard.calculatesubtotalsoncolumns'), name: 'calculatesubtotalsoncolumns'}
	                ]
	            }
			]
			, buttons: [{
    			text: LN('sbi.crosstab.crosstabdetailswizard.buttons.apply')
    		    , handler: function() {
    	    		this.fireEvent('apply', this.getFormState(), this);
                	this.hide();
            	}
            	, scope: this
    	    },{
    		    text: LN('sbi.crosstab.crosstabdetailswizard.buttons.cancel')
    		    , handler: function(){ this.hide(); }
            	, scope: this
    		}]
		});
		
	}

	, getFormState: function() {
		return this.crosstabDetailsForm.getForm().getValues();
	}
	
	, setFormState: function(values) {
		this.crosstabDetailsForm.getForm().reset(); // it is mandatory, since setValues method does not work properly for checkboxes
		this.crosstabDetailsForm.getForm().setValues(values);
	}
	
});