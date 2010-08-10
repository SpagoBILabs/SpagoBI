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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.VariableEditor = function(config) {
	
	var defaultSettings = {
		editable: false
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.variableEditor) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.variableEditor);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	c.uniqueName = c.id || c.field || 'not defined';
	if(c.id) delete c.id;
	if(c.field) delete c.field;
	
	c.alias = c.alias || c.text || 'not defined';
	delete c.text;
	
	Ext.apply(this, c);
	
	
	// constructor
    Sbi.formbuilder.VariableEditor.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formbuilder.VariableEditor, Sbi.formbuilder.InlineEditor, {
	
	uniqueName: null
	, alias: null
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
		
	, setContents: function(c) {
	
		this.filedConf = this.filedConf || {};
		
		if(c.id || c.field) this.uniqueName = c.id || c.field;
		if(c.alias || c.text) this.alias = c.alias || c.text;
	
	}
	
	, getContents: function() {
		var c = {};
		
		c.field = this.uniqueName;
		c.text = this.alias;
		
		return c;
	}
	
	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	, init: function() {
		this.filter = new Ext.Panel({
			html: this.alias
		});
	}
	
	
});