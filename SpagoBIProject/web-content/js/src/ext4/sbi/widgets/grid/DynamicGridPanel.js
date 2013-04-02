/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * Object name 
 * 
 * 
 * Public Properties
 * 
  * MANDATORY PARAMETERS: serviceUrl: the url for the ajax request
  * OPTIONAL:
  * 	pagingConfig:{} Object. If this object is defined the paging toollbar will be displayed
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
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.widgets.grid.DynamicGridPanel', {
    extend: 'Ext.grid.Panel'

    ,config: {
    	stripeRows: true
    }

	, constructor: function(config) {
		
		Sbi.debug('DynamicGridPanel costructor IN');
		Ext.apply(this,config);
	
    	Sbi.debug('DynamicGridPanel build store');
    	config.storeConfig = Ext.apply(config.storeConfig||{},{serviceUrl: config.serviceUrl});
    	var store = Ext.create('Sbi.widgets.store.DynamicStore', config.storeConfig ||{});
      	this.store = store;
      	
      	this.columns = [];

      	this.store.on('load', this.updateGrid, this);
      	this.addPaging(config);
      	
      	if(config.pagingConfig!=undefined && config.pagingConfig!=null){
      		Sbi.debug('DynamicGridPanel load first page');
      		this.store.loadPage(1);
      	}else{
      		Sbi.debug('DynamicGridPanel load store');
      		this.store.load();
      	}
      	
      	var additionalButtons = Sbi.widget.grid.StaticGridDecorator.getAdditionalToolbarButtons(this.decorators);
      	
      	if(this.filterConfig!=undefined && this.filterConfig!=null){
      		this.tbar = Ext.create('Sbi.widgets.grid.DynamicFilteringToolbar',Ext.apply(config.filterConfig||{},{store: this.store, additionalButtons:additionalButtons}));
      	}
      	
    	this.callParent(arguments);
    	
      	
    	Sbi.debug('DynamicGridPanel costructor OUT');
    },
    
    addPaging: function(config){
    	
    	if(config.pagingConfig!=undefined && config.pagingConfig!=null){
    		Sbi.debug('DynamicGridPanel add paging IN');
    		var defaultPagingConfig={
    				width: 400,
                store: this.store,
                displayInfo: true,
                displayMsg: 'Displaying  {0} - {1} of {2}',
                emptyMsg: "No rows to display"
            }
    		defaultPagingConfig = Ext.apply(defaultPagingConfig,config.pagingConfig );
    		this.bbar = Ext.create('Ext.PagingToolbar',defaultPagingConfig);
    		Sbi.debug('DynamicGridPanel add paging OUT');
    	}
    },
    
    updateGrid: function(){
    	Sbi.debug('DynamicGridPanel updategrid IN');
    	var columns = this.store.getColumns();
    	Sbi.widget.grid.StaticGridDecorator.addButtonColumns(this.decorators, this.columns, this);
    	if(this.bbar!=undefined && this.bbar!=null){
    		this.bbar.bindStore(this.store);
    	}
    	this.reconfigure(this.store, columns);
    }
	
});


