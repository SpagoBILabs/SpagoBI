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

Ext.ns("Sbi.worksheet.config");

Sbi.worksheet.config.OptionsWindow = function(config) {

	var defaultSettings = {
		title : LN('sbi.config.optionswindow.title')
		, width: 550
		, height: 250
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.config && Sbi.settings.worksheet.config.optionsWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.config.optionsWindow);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.addEvents("apply");
	
	this.init();
	
	c = Ext.apply(c, {
		items : [this.optionsForm]
		, buttons :
			[
				{
					text : LN('sbi.worksheet.config.optionswindow.buttons.text.apply')
				    , handler : function() {
						this.fireEvent("apply", this, this.optionsForm.getFormState());
				    	this.close();
					}
					, scope : this
				}
				,
				{
					text : LN('sbi.worksheet.config.optionswindow.buttons.text.cancel')
				    , handler : function() {
				    	this.close();
					}
					, scope : this
				}
		    ]
	});
	
	// constructor
    Sbi.worksheet.config.OptionsWindow.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.worksheet.config.OptionsWindow, Ext.Window, {

	options : null 			// the options to be displayed, it must be in the constructor input object
	, optionsForm : null	// the options's form

	,
	init : function () {
		this.optionsForm = new Sbi.widgets.ConfigurableForm({
			configuredItems : this.options
			, frame : true
		});
	}

	,
	getFormState : function () {
		return this.optionsForm.getFormState();
	}
	
	,
	setFormState : function (state) {
		this.optionsForm.setFormState(state);
	}
	
});