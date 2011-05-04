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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.DesignToolsFieldsPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.designtoolsfieldspanel.title')
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.designToolsFieldsPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.designToolsFieldsPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
		
	
	this.services = this.services || new Array();	
//	this.services['getQueryFields'] = this.services['getQueryFields'] || Sbi.config.serviceRegistry.getServiceUrl({
//		serviceName: 'GET_QUERY_FIELDS_ACTION'
//		, baseParams: new Object()
//	});
	
	this.initGrid(c.gridConfig || {});
	
	c = Ext.apply(c, {
		title: this.title,
		border: true,
		bodyStyle:'padding:3px',
      	layout: 'fit',   
      	items: [this.grid],
      	tools: [{
		    id:'gear',
		    qtip: LN('sbi.worksheet.designer.designtoolsfieldspanel.refresh'),
		    handler: function(){
      			this.refresh();
		    }
		    , scope: this
      	}]
	});

	// constructor	
	Sbi.worksheet.designer.DesignToolsFieldsPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.DesignToolsFieldsPanel, Ext.Panel, {
	
    services: null
    , grid: null
    , store: null
    , myData: [
       ['uno','ID','none','field','attribute'],
       ['due','NOME','none','field','attribute'],
       ['tre','COGNOME','none','field','attribute'],
       ['quattro','MISURA','SUM','field','measure'],
       ['5','MISURA 2','SUM','field','measure'],
       ['6','MISURA 3 ','SUM','field','measure'],
       ['7','MISURA 4 ','SUM','field','measure']
    ]
   
   
    // public methods
    
//    , refresh: function() {
//		this.store.load();
//	}
    
    // private
    
    , initGrid: function(c) {
		
		this.store = new Ext.data.SimpleStore({
			fields: [
			   {name: 'id'},
			   {name: 'alias'},
			   {name: 'funct'},
			   {name: 'iconCls'},
			   {name: 'nature'}
			],
			data: this.myData
		});
		
		/*
		this.store = new Ext.data.JsonStore({
			root: 'results'
			, fields: ['id', 'alias', 'funct', 'iconCls', 'nature']
			, url: this.services['getQueryFields']
		}); 
		
		
		this.store.on('loadexception', function(store, options, response, e){
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		}, this);
		*/
		
        this.template = new Ext.Template( // see Ext.Button.buttonTemplate and Button's onRender method
        		// margin auto in order to have button center alignment
                '<table id="{4}" cellspacing="0" class="x-btn {3}"><tbody class="{1}">',
                '<tr><td class="x-btn-tl"><i>&#160;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-ml"><i>&#160;</i></td><td class="x-btn-mc"><button type="{0}" class=" x-btn-text {5}"></button>{6}</td><td class="x-btn-mr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-bl"><i>&#160;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&#160;</i></td></tr>',
                '</tbody></table>');
        this.template.compile();
		
		this.grid = new Ext.grid.GridPanel(Ext.apply(c || {}, {
	        store: this.store,
	        columns: [
	            {id:'alias', 
            	header: LN('sbi.formbuilder.queryfieldspanel.fieldname')
            	, width: 160
            	, sortable: true
            	, dataIndex: 'alias'
            	, renderer : function(value, metaData, record, rowIndex, colIndex, store) {
		        	return this.template.apply(
		        			['button', 'x-btn-small x-btn-icon-small-left', '', 'x-btn-text-icon', Ext.id(), record.data.iconCls, record.data.alias]		
		        	);
		    	}
	            , scope: this
            	}
	        ],
	        stripeRows: false,
	        autoExpandColumn: 'alias',
	        enableDragDrop: true
	    }));
    }
    
});
