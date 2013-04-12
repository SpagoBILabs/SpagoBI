/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * 
 * This is a grid panel linked to a Store.. It builds the model and the associated store. It adds the page to the grid and the filtering toolbar.
 * It adds the widgets to the grid rows and the buttons in the toolbar according to the configuration.
 * 
 * 		@example
 * 		...
 *		var FixedGridPanelConf= {
 *			pagingConfig:{},
 *			storeConfig:{ 
 *				pageSize: 5
 *			},
 *			columnWidth: 2/5,
 *			buttonToolbarConfig:{
 *					newButton: true,
 *					cloneButton: true
 *			},
 *			buttonColumnsConfig:{
 *				deletebutton:true,
 *				selectbutton: true
 *			},
 *			services: this.services,
 *			fields:this.fields,
 *			columns: this.columns,
 *			filterConfig: {}
 *		};
 *		
 *		Ext.apply(this,config||{});
 *		
 *		this.grid=Ext.create('Sbi.widgets.grid.FixedGridPanel',FixedGridPanelConf);
 *		... 
 * 
 * @author
 * Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.widgets.grid.FixedGridPanel', {
    extend: 'Ext.grid.Panel'

    ,config: {
    	/**
    	 * Stripe rows
    	 */
    	stripeRows: true,
    	/**
    	 * The paging toolbar
    	 */
    	pagingToolbar: null,
    	/**
    	 * The fields list used for build the associated Model and Store
    	 */
    	fields: null,
    	/**
    	 * The optional configuration for the model
    	 */
    	modelConfig:null,
    	/**
    	 * The optional configuration for the store
    	 */
    	storeConfig: null,
    	/**
    	 * The optional configuration for the paging toolbar. Null to hide the toolbar
    	 */
    	pagingConfig:null,
    	/**
    	 * The list of the services. If there is no other specification in the storeConfig configuration variable, the list services should contains the service getAllValues. Thi service is used to load the data from the Ajax store
    	 */
    	services: [],
    	/**
    	 * Configuration object for the widgets buttons to add in every row of the grid
    	 */
    	buttonColumnsConfig:null,
    	/**
    	 * Configuration object for the buttons to add in the toolbar. {@link Sbi.widget.grid.StaticGridDecorator#StaticGridDecorator}
    	 */
    	buttonToolbarConfig: null,
    	/**
    	 * The definition of the columns of the grid. {@link Sbi.widgets.store.InMemoryFilteredStore#InMemoryFilteredStore}
    	 */
    	columns: [],
    	/**
    	 * If true force the grid to fit the width of the container. Default true.
    	 */
    	adjustWidth: true,
    	/**
    	 * Configuration of the filtering toolbar. Null to hide the toolbar
    	 */
    	filterConfig: null,
    	/**
    	 * The list of the properties that should be filtered 
    	 */
    	filteredProperties: new Array()
    }

	/**
	 * The constructor:
	 * 1) builds the model associated to the store. The fields for the model are defined in the configuration variable fields
	 * 2) builds the store. The default is a Ajax Json Store. You can change the behavior using the configuration storeConfig
	 * 3) add pagination and the additional button to the toolbar
	 */
	, constructor: function(config) {
		this.initConfig(config);
		Sbi.debug('FixedGridPanel costructor IN');
		Ext.apply(this,config||{});
		

		//BUILD THE MODEL
		Sbi.debug('FixedGridPanel bulding the model...');
		var d = new Date();
    	var modelname =  'StaticStoreModel'+(d.getTime()%10000000);
    	
    	this.modelConfig = Ext.apply({
    		extend: 'Ext.data.Model',
            fields: this.fields
    	},
    	this.modelConfig||{});
    	Ext.define(modelname, this.modelConfig);
    	Sbi.debug('FixedGridPanel model built');
    	
    	
    	
    	//BUILD THE STORE
    	Sbi.debug('FixedGridPanel bulding the store...');
    	
    	this.storeConfig = Ext.apply({
    		parentGrid: this,
    		model: modelname,
    		filteredProperties: this.filteredProperties,
    		proxy: {
    			type: 'ajax',
    			url:  this.services['getAllValues'],
    			reader: {
    				type:"json",
    				root: "root"
    			}
    		}
    	},this.storeConfig||{});
    	this.store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);
    	Sbi.debug('FixedGridPanel store built');
    	for(var i=0; i<this.columns.length; i++){
    		this.columns[i].renderer =  this.onRenderCell;
    	}
      	
    	//Add the widgets to the rows
      	Sbi.widget.grid.StaticGridDecorator.addButtonColumns(this.buttonColumnsConfig, this.columns, this);
      	
      	this.addPaging();
      	
      	if(this.pagingConfig!=undefined && this.pagingConfig!=null){
      		Sbi.debug('FixedGridPanel load first page');
      		this.store.loadPage(1);
      	}else{
      		Sbi.debug('this.fields load store');
      		this.store.load();
      	}
      	
      	//Adds the additional buttons to the toolbar
      	var additionalButtons = Sbi.widget.grid.StaticGridDecorator.getAdditionalToolbarButtons(this.buttonToolbarConfig, this);
      	if(this.filterConfig!=undefined && this.filterConfig!=null){
      		this.tbar = Ext.create('Sbi.widgets.grid.InLineGridFilter',Ext.apply({store: this.store, additionalButtons:additionalButtons}));
      		this.tbar.on("filter",function(filtercofing){
      			this.filterString = filtercofing.filterString;
      		},this);
      	}else{
      		if(additionalButtons){
      			this.tbar = Ext.create('Ext.toolbar.Toolbar',{items: additionalButtons});
      		}
      	}
      	
      	
      	
    	this.callParent(arguments);
    	if(this.adjustWidth==undefined || this.adjustWidth==null || this.adjustWidth){
    		this.on("resize",this.adjustColumnsWidth,this);
    	}
    	
      	
    	Sbi.debug('FixedGridPanel costructor OUT');
    },
    
    /**
     * @private
     * Add the paging toolbar to the grid
     */
    addPaging: function(){
    	
    	if(this.pagingConfig!=undefined && this.pagingConfig!=null){
    		Sbi.debug('FixedGridPanel add paging IN');
    		var defaultPagingConfig={
                store: this.store,
                displayInfo: true,
                displayMsg: 'Displaying  {0} - {1} of {2}',
                emptyMsg: "No rows to display"
            }
    		defaultPagingConfig = Ext.apply(defaultPagingConfig,this.pagingConfig );
    		this.pagingToolbar = Ext.create('Ext.PagingToolbar',defaultPagingConfig);
    		this.bbar = this.pagingToolbar;
    		Sbi.debug('FixedGridPanel add paging OUT');
    	}
    },
    
    //fit the columns non decorated to the width of the grid
    /**
     * @private
     * Set the width of the columns to force the panel width to fit the container width
     */
    adjustColumnsWidth: function(){
    	var columns = this.columns;
    	var thisw = this.getWidth();
    	var decoratedColumnsWidth = 0;
    	var decoratedColumns = 0;
    	
    	//Search the columns that contains a widget. They have a fixed width
    	for(var i=0; i<columns.length; i++){
    		if(columns[i].columnType == "decorated"){
    			decoratedColumnsWidth = columns[i].width+decoratedColumnsWidth;
    			decoratedColumns++;
    		}
    	}
    	
    	var nondecoratedColumns = columns.length-decoratedColumns;
    	if(thisw && nondecoratedColumns>0){
    		var nondecoratedWidth = ((thisw-decoratedColumnsWidth)/nondecoratedColumns)-1;
        	for(var i=0; i<columns.length; i++){
        		if(columns[i].columnType ==null || columns[i].columnType == undefined || columns[i].columnType != "decorated"){
        			columns[i].setWidth(nondecoratedWidth);
        		}
        	}
    	}
    	

    },
        
    /**
     * Set the size of the page to the store and reloads the first page
     * @param rthe size of the page
     */
    setPageSize: function(size){
    	this.store.pageSize = size;
    	this.store.loadPage(1);
    },
    
    
    onRenderCell: function(value) {
    	var filterString = this.filterString;
    	var startPosition;
    	var tempString = value;
    	var toReturn="";

    	if(filterString){
    		while(tempString.length>0){
    			startPosition = tempString.toLowerCase().indexOf(filterString.toLowerCase());      		
        		if(startPosition>=0){
        			//prefix
        			toReturn = toReturn+ tempString.substring(0,startPosition);
            		toReturn = toReturn+ "<span class='x-livesearch-match'>"+ tempString.substring(startPosition,startPosition+filterString.length)+"</span>";
            		tempString = tempString.substring(startPosition+filterString.length);
        		}else{
        			toReturn=toReturn+tempString;
        			tempString ="";
        		}
    		}
    		return toReturn;
    	}else{
    		return value;
    	}

    	
    }
    

});


