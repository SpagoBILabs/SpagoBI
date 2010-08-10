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

Sbi.formbuilder.StaticCloseFilterEditorPanel = function(config) {
	
	var defaultSettings = {
		
		title: LN('sbi.formbuilder.staticclosefiltereditorpanel.title')
		, emptyMsg: LN('sbi.formbuilder.staticclosefiltereditorpanel.emptymsg')
		, filterItemName: LN('sbi.formbuilder.staticclosefiltereditorpanel.filteritemname')
		
		, layout: 'column'
	    /*, layoutConfig: {
	        columns: 100
	    }*/
		, enableDebugBtn: false
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.staticCloseFilterEditorPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.staticCloseFilterEditorPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	// constructor
    Sbi.formbuilder.StaticCloseFilterEditorPanel.superclass.constructor.call(this, c);
    
    this.on('addrequest', function() {
    	this.showFilterGroupWizard(null);
    }, this);
    this.on('editrequest', function(editor, filterGroup) {
    	this.showFilterGroupWizard(filterGroup);
    }, this);
};

Ext.extend(Sbi.formbuilder.StaticCloseFilterEditorPanel, Sbi.formbuilder.EditorPanel, {
    
	wizard: null
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
		
	, setContents: function(contents) {
		for(var i = 0, l = contents.length; i < l; i++) {
			this.addFilterGroup(contents[i]);
		}
	}

	, addFilterGroup: function(filtersGroupConf) {
		
		c = filtersGroupConf || {};
		
		if(c.options){
			c.baseContents = c.options;
			delete c.options;
		}
		
		if(c.title){
			c.groupTitle = c.title;		
			delete c.title;
		}
		
		var filtersGroup = new Sbi.formbuilder.StaticCloseFilterGroupEditor(c);
		
		filtersGroup.on('editrequest', function(editor) {
	    	this.showFilterGroupWizard(editor);
	    }, this);
		
		this.addFilterItem(filtersGroup);
	}
	
	, showFilterGroupWizard: function(targetFilterGroup) {
		if(this.wizard === null) {
			this.wizard = new Sbi.formbuilder.StaticCloseFilterGroupWizard();
			this.wizard.on('apply', function(win, target, state) {
				if(target === null) {
					this.addFilterGroup(state);
				} else {
					target.modifyFilter(state);
				}
				
			}, this);
		}
		
		this.wizard.setTarget(targetFilterGroup || null);		
		this.wizard.show();
	}

	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	, onDebug: function() {
		
	}
	
  	
});