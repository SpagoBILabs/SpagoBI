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

Ext.define('Sbi.widgets.dataview.DataViewPanel', {
    extend: 'Ext.Panel'

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
    	
    	dataView: null,
    	
    	/**
    	 * The definition of the columns of the grid. {@link Sbi.widgets.store.InMemoryFilteredStore#InMemoryFilteredStore}
    	 */
    	columns: [],
    	/**
    	 * The list of the properties that should be filtered 
    	 */
    	filteredProperties: new Array()
    }

	/**
	 * In this constructor you must pass configuration
	 */
	, constructor: function(config) {
		//this.initConfig(config);				
		
		Ext.apply(this,config||{});
		
		this.dataView = Ext.create('Ext.view.View', {
			 	store: this.store,
			    tpl: this.tpl,
			    itemSelector: 'div.thumb-wrap',
			    emptyText: 'No images available',
			    renderTo: Ext.getBody()
        });
        
        
		 Ext.create('Ext.Panel', {
	        id: 'images-view',
	        frame: true,
	        collapsible: true,
	        width: 535,
	        renderTo: Ext.getBody(),
	        title: 'Simple DataView (0 items selected)'
	     });
		
		//Defines items of the panel
	//	this.items = [this.dataView];
			
		//this.callParent(arguments);
	
	}
	
	 /**
     * @private
     * Adds the toolbar with the search 
     */
   , addToolbar: function(){
      	//Adds the additional buttons to the toolbar
   //   	this.additionalButtons = Sbi.widget.grid.StaticDataViewDecorator.getAdditionalToolbarButtons(this.buttonToolbarConfig, this);

      	
   //   	this.tbar = Ext.create('Sbi.widgets.grid.InLineGridFilter',Ext.apply({store: this.store, additionalButtons:this.additionalButtons}));
   //   	this.tbar.on("filter",function(filtercofing){
   //   		this.filterString = filtercofing.filterString;
   //   	},this);
      	  
    }
	
	/**
     * @override
     */
    , buildStore: function(modelname){
		//BUILD THE STORE
    	Sbi.debug('DataViewPanel bulding the store...');
    	
    	var storeConfig = Ext.apply({    		
    		model: modelname,
    		filteredProperties: this.filteredProperties
    	},{});
    	    	
    	Sbi.debug('DataViewPanel store built.');
    	
    	return Ext.create('Sbi.widgets.store.InMemoryFilteredStore', storeConfig);

    }
    
    /**
     * @override
     */
    , buildTpl: function(config, store){
		//BUILD THE TPL
    	Sbi.debug('DataViewPanel bulding the tpl...');
    	
    	var imageTpl = new Ext.XTemplate(
		    '<tpl for=".">',
		        '<div style="margin-bottom: 10px;" class="thumb-wrap">',
		          '<img src="{'+ config.src  +'} title={Name}" />',
		          '<br/><span>{Name}</span>',
		        '</div>',
		    '</tpl>'
    	);
    	Sbi.debug('DataViewPanel tpl built.');
    	
    	return imageTpl;

    }
});