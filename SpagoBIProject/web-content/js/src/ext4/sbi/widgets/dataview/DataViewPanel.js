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
    	
    	dataView: null,
    	
    	tbar: null,
    	
    	/**
    	 * Configuration object for the widgets buttons to add in every row of the grid
    	 */
    	buttonColumnsConfig:null,
    	/**
    	 * Configuration object for the buttons to add in the toolbar. {@link Sbi.widget.grid.StaticGridDecorator#StaticGridDecorator}
    	 */
    	buttonToolbarConfig: null,
    }

	/**
	 * In this constructor you must pass configuration
	 */
	, constructor: function(config) {
		this.initConfig(config);				
		
		Ext.apply(this,config||{});		
		var newDatasetButton = new Ext.button.Button({
		    	tooltip: LN('sbi.generic.add'),
				iconCls:'icon-add',
				width:50,
				listeners: {
					'click': {
		          		fn: this.addNewDataset,
		          		scope: this
		        	} 
				}
		    });
		     
		var toolbar = Ext.create('Ext.toolbar.Toolbar');
		var toolbar =  Ext.create('Ext.toolbar.Toolbar',{renderTo: Ext.getBody(),height:30});
		toolbar.add('->');
		toolbar.add(' ');
		toolbar.add(newDatasetButton);

		this.tbar = toolbar;
		this.dataView =  Ext.create('Sbi.widgets.dataview.DataViewElement',config);					

		//this.doLayout();
	//	this.callParent(arguments);
	}
	
	, onRender : function(obj, opt) {	    
    	Sbi.widgets.dataview.DataViewPanel.superclass.onRender.call(this, opt);
    }
	
    , addNewDataset: function() {
    	alert("new dataset, chiamare wizard...");
		/*var urlToCall = this.services['newDocument'];
		
		if(this.folderId != null){
			urlToCall += '&FUNCT_ID='+this.folderId;
		}
		
		window.location.href=urlToCall;
		*/	
	}
    
	    /**
	     * @private
	     * Adds the toolbar with the search 
	    
	   , addToolbar: function(){
	      	//Adds the additional buttons to the toolbar
	      	this.additionalButtons = Sbi.widget.grid.StaticDataViewDecorator.getAdditionalToolbarButtons(this.buttonToolbarConfig, this);

	      	this.tbar = Ext.create('Sbi.widgets.grid.InLineGridFilter',Ext.apply({store: this.store, additionalButtons:this.additionalButtons}));
	      	this.tbar.on("filter",function(filtercofing){
	      		this.filterString = filtercofing.filterString;
	      	},this);
	      	  
	    }
	 */
	/**
     * @override
     */
    , buildStore: function(modelName){
		//BUILD THE STORE
    	Sbi.debug('DataViewPanel bulding the store...');

    	this.storeConfig = Ext.apply({
    		model: modelName,
    		filteredProperties: ["label","name"]
    	},{});

    	//creates and returns the store
    	Sbi.debug('DataViewPanel store built.');
    	
    	return store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);
    }
    /**
     * @override
     */
    , buildTpl: function(config){
		//BUILD THE TPL
    	Sbi.debug('DataViewPanel bulding the tpl...');
    	
    	
    	var imageTpl = new Ext.XTemplate(
  			
    		 '<div id="sample-ct">', 	            
 	            '<div style="display:inline;list-style-type:null;padding-right:10px;">',
// 	           '<div class="group">',
 	            '<h2><div >Elenco datasets</div></h2>',
 	            '<ul>',
// 	            '<dl >',
// 	            	'<tpl if="samples.length == 0">',
// 	            		'<div id="empty-group-message">',
// 	            		noItem,
// 	            		'</div>',
// 	            	'</tpl>',        
 	            	'<tpl for=".">',
//	                    '<dd >',
	                    '<li>',
	                    //'<dd class="group-item"  style="width:50px;height:50px;">',
//			                '<div class="item-control-panel">',	 
//		                    	'<tpl for="actions">',   
//		                        	'<div class="button"><img class="action-{name}" title="{description}" src="' + Ext.BLANK_IMAGE_URL + '"/></div>',
//		                        '</tpl>',
//		                    '</div>',
	 	                    '<span>{name}</span>',
							'<img src="'+ config.src  +'" width="50">' ,
							'<br/>', 
//	                    '</dd>',
	                    '</li>',
	                '</tpl>',
	              
// 	            '<div style="clear:left"></div>',
//	          '</dl>',
	          '</ul>',
 	          '</div>',
 	        '</div>'
    	);
    	Sbi.debug('DataViewPanel tpl built.');
    	
    	return imageTpl;

    }
   
});