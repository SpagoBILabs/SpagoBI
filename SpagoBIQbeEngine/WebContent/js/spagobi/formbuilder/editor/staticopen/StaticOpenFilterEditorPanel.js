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

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.StaticOpenFilterEditorPanel = function(config) {
	
	var defaultSettings = {
		
		title: LN('sbi.formbuilder.staticopenfiltereditorpanel.title')
		, emptyMsg: LN('sbi.formbuilder.staticopenfiltereditorpanel.emptymsg')
		, ddGroup    : 'formbuilderDDGroup'
		, droppable: {
			onFieldDrop: this.onFieldDrop
		} 
		, filterItemName: 'static open filter'        
		, enableAddBtn: false			
		, layout: 'column'
		
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.staticOpenFilterEditorPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.staticOpenFilterEditorPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	// constructor
    Sbi.formbuilder.StaticOpenFilterEditorPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formbuilder.StaticOpenFilterEditorPanel, Sbi.formbuilder.EditorPanel, {
	
	dummy: null
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
		
	, setContents: function(contents) {
		for(var i = 0, l = contents.length; i < l; i++) {
			this.addFilterGroup(contents[i]);
		}
	}
	
	, addFilterGroup: function(filtersGroupConf) {
		var c =  filtersGroupConf || {};
		c.baseContents = [filtersGroupConf];
		var filter = new Sbi.formbuilder.StaticOpenFilterGroupEditor( c );
		this.addFilterItem( filter );
	}
	
	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	
	, onFieldDrop: function(fieldConf) {
		
		
		var filtersGroupConf = {};
		filtersGroupConf.text = fieldConf.alias;
		filtersGroupConf.field = fieldConf.id;
		filtersGroupConf.operator = 'EQUALS TO';
		filtersGroupConf.maxSelectedNumber = 1;
		filtersGroupConf.orderBy = '';
		filtersGroupConf.orderType = '';
		filtersGroupConf.queryRootEntity = false;

		
		var staticOpenFilterWindow = new Sbi.formbuilder.StaticOpenFilterWizard(filtersGroupConf, {});
		
		staticOpenFilterWindow.show();	
		
		staticOpenFilterWindow.on('apply', function(filtersGroupConf) {
			this.addFilterGroup(filtersGroupConf);
		} , this); 
	}
	
});
