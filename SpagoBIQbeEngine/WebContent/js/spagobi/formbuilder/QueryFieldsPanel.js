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

Sbi.formbuilder.QueryFieldsPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.formbuilder.queryfieldspanel.title')
	};
		
	if(Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.queryFieldsPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.queryFieldsPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
	
	this.services = this.services || new Array();	
	this.services['getQueryFields'] = this.services['getQueryFields'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_QUERY_FIELDS_ACTION'
		, baseParams: new Object()
	});
	
	//.addEvents('customEvents');
	
		
		
	this.initGrid(c.gridConfig || {});

	
	c = Ext.apply(c, {
		title: this.title,
		border: true,
		//bodyStyle:'background:green',
		bodyStyle:'padding:3px',
      	layout: 'fit',   
      	items: [this.grid],
      	tools: [{
		    id:'refresh',
		    qtip: LN('sbi.formbuilder.queryfieldspanel.tools.refresh'),
		    handler: function(){
      			this.refresh();
		    }
		    , scope: this
      	}]
	});

	// constructor
	Sbi.formbuilder.QueryFieldsPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.formbuilder.QueryFieldsPanel, Ext.Panel, {
    
    services: null
    , grid: null
    , store: null

    
    // private
    
    , initGrid: function(c) {
	
		this.store = new Ext.data.JsonStore({
			root: 'results'
			, fields: ['id', 'alias', 'funct', 'iconCls', 'nature', 'values']
			, url: this.services['getQueryFields']
		}); 
		
		this.store.on('loadexception', function(store, options, response, e){
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		}, this);
		
        this.template = new Ext.Template( // see Ext.Button.buttonTemplate and Button's onRender method
        		// margin auto in order to have button center alignment
                '<table id="{4}" cellspacing="0" class="x-btn {3} {6}"><tbody class="{1}">',
                '<tr><td class="x-btn-tl"><i>&#160;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-ml"><i>&#160;</i></td><td class="x-btn-mc"><button type="{0}" class=" x-btn-text {5}"></button>{7}</td><td class="x-btn-mr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-bl"><i>&#160;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&#160;</i></td></tr>',
                '</tbody></table>');
        this.template.compile();
		
		this.grid = new Ext.grid.GridPanel(Ext.apply(c || {}, {
	        store: this.store,
	        hideHeaders: true,
	        columns: [
	            {id:'alias' 
            	, header: LN('sbi.formbuilder.queryfieldspanel.fieldname')
            	, width: 160
            	, sortable: true
            	, dataIndex: 'alias'
            	, renderer : function(value, metaData, record, rowIndex, colIndex, store) {
		        	return this.template.apply(
		        			// by now cssborder is defined only for segment_attribute
		        			['button', 'x-btn-small x-btn-icon-small-left', '', 'x-btn-text-icon', Ext.id(), record.data.iconCls, record.data.iconCls+'_border', record.data.alias]		
		        	);
		    	}
	            , scope: this
            	}
	        ],
	        stripeRows: false,
	        autoExpandColumn: 'alias',
	        enableDragDrop: true
	        //ddGroup: c.ddGroup //'formbuilderDDGroup'
	    }));
    }
    
    
    // public methods 
    
	, refresh: function() {
		this.store.load();
	}

    , getFields : function () {
    	var fields = [];
    	var count = this.store.getCount();
    	for (var i = 0; i < count; i++) {
    		fields.push(this.store.getAt(i).data);
    	}
    	return fields;
    }
    
});