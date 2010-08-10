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

Sbi.crosstab.CrosstabDesignerPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.crosstab.crosstabdesignerpanel.title')
  	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c); // this operation should overwrite this.crosstabTemplate content, that is the definition of the crosstab

	this.init(c);
	
	c = Ext.apply(c, {
      	layout: 'border',
      	items: [this.westRegionPanel, this.centerRegionPanel]
	});
	
	// constructor
    Sbi.crosstab.CrosstabDesignerPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.crosstab.CrosstabDesignerPanel, Ext.Panel, {
    
	crosstabTemplate: {}
	
	, init: function(c) {
		this.initWestRegionPanel(c.westConfig || {});
		this.initCenterRegionPanel(c.centerConfig || {});
	}
	
	, initWestRegionPanel: function(c) {
		this.westRegionPanel = new Sbi.formbuilder.QueryFieldsPanel(Ext.apply(c, {
			region: 'west',
			split: true, 
			layout: 'fit',
			border: false,
	        width: 250,
	        collapsible: true,
	        collapseFirst: false,
	        gridConfig: {
				ddGroup: 'crosstabDesignerDDGroup'
	        	, type: 'queryFieldsPanel'
	        }
		}));
	}
	
	, initCenterRegionPanel: function(c) {
		this.centerRegionPanel = new Sbi.crosstab.CrosstabDefinitionPanel(Ext.apply(c, {
			region: 'center'
			, crosstabTemplate: this.crosstabTemplate
		}));
	}
	
	, getCrosstabDefinition: function() {
		return this.centerRegionPanel.getCrosstabDefinition();
	}
	
});