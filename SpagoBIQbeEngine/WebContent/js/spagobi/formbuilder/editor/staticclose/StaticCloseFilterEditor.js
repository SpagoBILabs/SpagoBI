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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.StaticCloseFilterEditor = function(config) {
	
	var defaultSettings = {
		//style: 'border:1px solid #ccc !important;'
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.staticCloseFilterEditor) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.staticCloseFilterEditor);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	this.baseConfig = config;
	
	Ext.apply(this, c);
	
	// constructor
    Sbi.formbuilder.StaticCloseFilterEditor.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formbuilder.StaticCloseFilterEditor, Sbi.formbuilder.InlineEditor, {
    
	text: null
	, expression: null
	, filters: null
	, baseConfig: null
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
		
	, setContents: function(c) {
		if(this.text !== c.text) {
			this.filter.setBoxLabel(c.text);
			//alert('filter name is changed!');
		}
		this.text = c.text;
		this.filters = c.filters;
		this.expression = c.expression;
	}
	
	, getContents: function() {
		var c = {};
		c.text = this.text;
		c.filters = this.filters;
		c.expression = this.expression;
		return c;
	}
	
	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	, init: function() {
		var filterConf = {
			width: 148
			, hideLabel: true
			, boxLabel: this.text
	        , name: 'options'
	        , inputValue: 'option'
	        //, style: 'background: red'
	        //, bodyStyle: 'background: red'
		};
		
		if(this.singleSelection === true) {
			this.filter = new Ext.form.Radio(filterConf);
		} else {
			this.filter = new Ext.form.Checkbox(filterConf);
		}
	}
});