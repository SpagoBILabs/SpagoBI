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

Sbi.crosstab.CrosstabDefinitionPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.crosstab.crosstabdefinitionpanel.title')
  	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabDefinitionPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabDefinitionPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c); // this operation should overwrite this.crosstabTemplate content, that is the definition of the crosstab
	
	this.init(c);
	
	c = Ext.apply(c, {
      	items: [this.crosstabDefinitionPanel]
      	, autoScroll: true
      	, tools: [{
		    id: 'gear'
		    , qtip: LN('sbi.crosstab.crosstabdefinitionpanel.tools.preview')
		    , handler: function() {
      			this.fireEvent('preview', this, this.getCrosstabDefinition());
		    }
		    , scope: this
      	}]
	});
	
	// constructor
    Sbi.crosstab.CrosstabDefinitionPanel.superclass.constructor.call(this, c);
    
    this.addEvents('preview');
    
};

Ext.extend(Sbi.crosstab.CrosstabDefinitionPanel, Ext.Panel, {
	
	crosstabTemplate: {}
	, crosstabDefinitionPanel: null
	, columnsContainerPanel: null
	, rowsContainerPanel: null
	, measuresContainerPanel: null
	
	, init: function(c) {
		
		this.columnsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
            title: LN('sbi.crosstab.crosstabdefinitionpanel.columns')
            , width: 400
            , initialData: this.crosstabTemplate.columns
		});
		
		this.rowsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
            title: LN('sbi.crosstab.crosstabdefinitionpanel.rows')
            , width: 200
            , initialData: this.crosstabTemplate.rows
		});
		
		this.measuresContainerPanel = new Sbi.crosstab.MeasuresContainerPanel({
            title: LN('sbi.crosstab.crosstabdefinitionpanel.measures')
            , width: 400
            , initialData: this.crosstabTemplate.measures
            , crosstabConfig: this.crosstabTemplate.config
		});
	
	    this.crosstabDefinitionPanel = new Ext.Panel({
	        layout: 'table'
	        , baseCls:'x-plain'
	        , padding: '30 30 30 100'
	        , layoutConfig: {columns:2}
	        // applied to child components
	        , defaults: {height: 150}
	        , items:[
	            {
		        	border: false
		        }
		        , this.columnsContainerPanel
		        , this.rowsContainerPanel
		        , this.measuresContainerPanel
		    ]
	    });
	
	}

	, getCrosstabDefinition: function() {
		var crosstabDef = {};
		crosstabDef.rows = this.rowsContainerPanel.getContainedAttributes();
		crosstabDef.columns = this.columnsContainerPanel.getContainedAttributes();
		crosstabDef.measures = this.measuresContainerPanel.getContainedMeasures();
		crosstabDef.config = this.measuresContainerPanel.getCrosstabConfig();
		return crosstabDef;
	}
	
});