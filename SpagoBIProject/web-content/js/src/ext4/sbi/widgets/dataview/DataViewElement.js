/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/**TODO anto: aggiornare documentazione!!!
 * 
 * Data view for a browser style. It define a layout and provides the stubs methods for the icons browser.
 * This methods should be overridden to define the logic. Only the methods onAddNewRow, onGridSelect and onCloneRow are implemented.
 * The configuration dataView must be passed to the object. It should contains the method setFormState (it is used by onGridSelect)
 * 
 * 
 * 		@example
 *	Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
 *   extend: 'Sbi.widgets.compositepannel.ListDetailPanel'
 *	, constructor: function(config) {
 *		//init services
 *		this.initServices();
 *		//init form
 *		this.form =  Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services: this.services});
 *		this.columns = [{dataIndex:"DATASOURCE_LABEL", header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
 *		this.fields = ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
 *		this.form.on("save",this.onFormSave,this);
 *    	this.callParent(arguments);
 *    }
 *	, initServices: function(baseParams){
 *		this.services["getAllValues"]= Sbi.config.serviceRegistry.getRestServiceUrl({
 *			    							serviceName: 'datasources/listall'
 *			    							, baseParams: baseParams
 *			    						});
 *		...
 *		    	
 *	}
 *	, onDeleteRow: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["delete"],
 *  	        params: {DATASOURCE_ID: record.get('DATASOURCE_ID')},
 *  	       ...
 *	}
 *	, onFormSave: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["save"],
 *  	        params: record,
 *  	   ...
 *	}
 *});
 *			... 
 *
 * 
 * @author
 * Antonella Giachino (antonella.giachino@eng.it)
 */

Ext.define('Sbi.widgets.dataview.DataViewElement', {
    extend: 'Ext.DataView'

    ,config: {
    	/**
    	 * The Ext.data.Store to bind this DataView to.
    	 */
    	store: null,
    	/**
    	 * The HTML fragment or an array of fragments that will make up the template used by this DataView.
    	 */
    	tpl: null ,
    	/**
    	 *  A simple CSS selector that will be used to determine what nodes this DataView will be working with.
    	 */
    	itemSelector: null,    	 
    	
    	/**
    	 * The definition of the columns of the grid. {@link Sbi.widgets.store.InMemoryFilteredStore#InMemoryFilteredStore}
    	 */
    	columns: [],
    	/**
    	 * The list of the properties that should be filtered 
    	 */
    	filteredProperties: new Array(),
    	
        frame:true

    }

	/**
	 * In this constructor you must pass configuration
	 */
	, constructor: function(config) {
		this.initConfig(config);				
		
		Ext.apply(this,config||{});
		
		this.dataView = Ext.create('Ext.view.View', {
			 	store: this.store,
			    tpl: this.tpl,
			    itemSelector: 'dd',
			    overClass: 'dd.over',
			    trackOver: true,
	            overItemCls: 'x-item-over',	
			    frame:true,
			    emptyText: 'No datasets available',
		        inline: {
		            wrap: false
		        },
		        scrollable: 'horizontal',
			    renderTo: Ext.getBody()
        });
			
		this.dataView.addListener('itemclick',	this.onClick, this);
		this.dataView.addListener('itemmouseenter',	this.onMouseOver, this);
		this.dataView.addListener('itemmouseleave',	this.onMouseOut, this);

		//this.callParent(arguments);		
		
	}
    , onRender : function(obj, opt) {	    
    	Sbi.widgets.dataview.DataViewElement.superclass.onRender.call(this, opt);
    }

  , onClick : function(obj, rec, item, idx, e, opt){
		  /*
	        // is of type Ext.EventObject		
	        var group = e.getTarget('div[class=group-header]', 10, true);
	        if(group){
	            group.up('div[class*=group]').toggleClass('collapsed');
	        }
	        
	        return Sbi.browser.FolderView.superclass.onClick.apply(this, arguments);
	        */
		  alert("click of id: " + idx);
	 }
	    
	    , onMouseOver : function(obj, e, opt) {  
	    
	      var group = e.getTarget('div[class=group-header]', 10, true);
	      if(!group){
	            var d = e.getTarget('[class*=group-item]', 5, true);
	            if(d){
	                var t = d.first('div[class*=item-control-panel]', false);
	                if(t){   
	                  t.applyStyles('visibility:visible');
	                }
	            }
	        }
	       // return Sbi.browser.FolderView.superclass.onMouseOver.apply(this, arguments);
	      // return this.callParent.onMouseOver(arguments);	 
	    
	    }
	    
	    , onMouseOut : function(e){
	    	/*
	        var group = e.getTarget('div[class=group-header]', 10, true);
	        if(!group){
	            var d = e.getTarget('[class*=group-item]', 5, true);
	            if(d){
	                var t = d.first('div[class*=item-control-panel]', false);
	                if(t){   
	                  t.applyStyles('visibility:hidden');
	                }
	            }
	        }
	        return Sbi.browser.FolderView.superclass.onMouseOut.apply(this, arguments);
	        */
	    }
});