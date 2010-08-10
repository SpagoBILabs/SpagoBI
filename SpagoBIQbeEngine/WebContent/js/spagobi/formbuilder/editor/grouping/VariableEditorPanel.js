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

Sbi.formbuilder.VariableEditorPanel = function(config) {
	
	var defaultSettings = {
		
		title: LN('sbi.formbuilder.variableeditorpanel.title')
		, emptyMsg: LN('sbi.formbuilder.variableeditorpanel.emptymsg')
		, filterItemName: 'grouping varaibles group'
		
		, layout: 'table'
	    , layoutConfig: {
	        columns: 100
	    }
		, enableDebugBtn: false
		, enableAddBtn: false	
		, enableClearBtn: false	
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.variableEditorPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.variableEditorPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	// constructor
    Sbi.formbuilder.VariableEditorPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formbuilder.VariableEditorPanel, Sbi.formbuilder.EditorPanel, {
    
	wizard: null
	
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
		
	, setContents: function(contents) {
		// the parent-class call this method so do not remove it.
		// It does nothings because the structure of this panel is fixed
	}

	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	, init: function() {
		Sbi.formbuilder.VariableEditorPanel.superclass.init.call(this);
		var variable1GroupEditor, variable2GroupEditor;
		
		
		var bc;
		
		bc = (this.baseContents && this.baseContents.length > 0)? this.baseContents[0].admissibleFields: undefined;
		variable1GroupEditor = new Sbi.formbuilder.VariableGroupEditor({
			groupTitle: LN('sbi.formbuilder.variableeditorpanel.grouptitle') + ' 1',
			baseContents: bc
		});
		this.addFilterItem(variable1GroupEditor);
		
		bc = (this.baseContents && this.baseContents.length > 1)? this.baseContents[1].admissibleFields: undefined;
		variable2GroupEditor = new Sbi.formbuilder.VariableGroupEditor({
			groupTitle: LN('sbi.formbuilder.variableeditorpanel.grouptitle') + ' 2',
			baseContents: bc
		});
		this.addFilterItem(variable2GroupEditor);
		
	}	
	
	
	, onDebug: function() {
		
	}
	
	, getErrors: function() {
		var errors = [];
		var contents = this.getContents();
		var contents1 = contents[0].admissibleFields;
		if (contents1 === undefined || contents1 === null || contents1.length === 0) {
			errors.push(LN('sbi.formbuilder.variableeditorpanel.validationerrors.missingadmissiblefields') + ' 1');
		}
		var contents2 = contents[1].admissibleFields;
		if (contents2 === undefined || contents2 === null || contents2.length === 0) {
			errors.push(LN('sbi.formbuilder.variableeditorpanel.validationerrors.missingadmissiblefields') + ' 2');
		}
		return errors;
	}
  	
});