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

Ext.ns("Sbi.formviewer");

Sbi.formviewer.CrosstabPage = function(config) {	
	var defaultSettings = {
		title: LN('sbi.formviewer.crosstabpage.title')
		, layout: 'fit'
		, autoScroll: true
	};
		
	if(Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.crosstabPage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.crosstabPage);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
		
	this.toolbar = new Ext.Toolbar({
		items: [
		    '->'
		    , {
				text: LN('sbi.formviewer.resultspage.backtoform'),
				handler: function() {this.fireEvent('edit');},
				scope: this
		    }
		  ]
	});
	
	this.init(c);
	
	c = Ext.apply(c, {
	    tbar: this.toolbar,
	    style: 'padding:3px;',
	    items: [this.tabs]
	});
		
	// constructor
	Sbi.formviewer.CrosstabPage.superclass.constructor.call(this, c);
	
	this.addEvents('edit');
};

Ext.extend(Sbi.formviewer.CrosstabPage, Ext.Panel, {

	formState: null
	
    // -- public methods -----------------------------------------------------------------------

    , getFormState: function() {
		return this.formState;
	}

	, setFormState: function(formState) {
		this.formState = formState;
	}
    
    // -- private methods -----------------------------------------------------------------------

	, init: function (c) {
		
		var items = [];

		this.crosstabDesignerPanel = new Sbi.crosstab.CrosstabDesignerPanel(c.crosstab || {});
		items.push(this.crosstabDesignerPanel);
		this.crosstabDesignerPanel.westRegionPanel.store.on('load', this.addCountField, this);
		this.crosstabDesignerPanel.centerRegionPanel.on('preview', this.showCrosstabPreview, this);
		
		this.crosstabPreviewPanel = new Sbi.crosstab.CrosstabPreviewPanel(c.crosstab || {});
		items.push(this.crosstabPreviewPanel);
		
		this.tabs = new Ext.TabPanel({
			border: false,
	  		activeTab: 0,
	  		items: [items]
		});
	}

	, addCountField: function () {
		this.crosstabDesignerPanel.westRegionPanel.store.add(
				new Ext.data.Record({
					id: 'qbe_smartfilter_count'
					, alias: LN('sbi.formviewer.crosstabpage.countfield')
					, funct: 'COUNT'
					, iconCls: 'measure'
					, nature: 'measure'
				})
		);
	}
	
  	, showCrosstabPreview: function(crosstabDefinitionPanel, crosstabDefinition) {
  		this.crosstabPreviewPanel.on('beforeload', this.addFormStateParameter, this);
  		this.tabs.activate(this.crosstabPreviewPanel);
  		this.crosstabPreviewPanel.load(crosstabDefinition);
  	}
  	
  	, addFormStateParameter: function(crosstabPreviewPanel, requestParameters) {
  		requestParameters.formstate = Ext.util.JSON.encode(this.getFormState());
  	}

});