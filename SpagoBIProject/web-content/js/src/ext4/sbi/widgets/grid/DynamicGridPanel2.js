/**
 * 
 */

Ext.define('Sbi.widgets.grid.DynamicGridPanel2', {
    extend: 'Ext.grid.Panel'

    ,config: {
    	stripeRows: true,
    	
    	/**
    	 * The paging toolbar
    	 */
    	pagingToolbar: null
    	
//    ,	loadMask: null
    }

	, constructor: function(config) {		
		
		Sbi.debug("[IN] constructor() DynamicGridPanel");
		
		var attribConfig = config.profileAttrib;
		
		Ext.apply(this,config);
	
//		if (config.parentGrid)
//			this = parentGrid;
		
    	Sbi.debug('DynamicGridPanel build store');
    	    	
    	// We are adding service URL to the existing storeConfig
    	config.storeConfig = Ext.apply(config.storeConfig||{},{serviceUrl: config.serviceUrl, lovProvider: config.lovProvider, profileAttrib: attribConfig});
    	
    	// We don't use this (for POST-ing)
    	if ((config.usePost != null) && (config.usePost != undefined ) ){
        	config.storeConfig.params = config.params;
        	config.storeConfig.usePost = config.usePost;
    	}
    	
    	config.storeConfig.isDynamicStore = true;
    	
    	var store;
    	
    	if (config.storeConfig.storeType && config.storeConfig.storeType == 'InMemoryFilteredStore'){ 
    		store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', config.storeConfig ||{});	
    	}else{
    		store = Ext.create('Sbi.widgets.store.DynamicStore2', config.storeConfig || {}, {lovProvider: config.lovProvider, profileAttrib: attribConfig});	
    	}
    	
    	// Store is created       	
      	this.store = store;
      	
      	this.columns = [];

      	this.store.on('load', this.updateGrid, this);
      	this.addPaging(config); // This function is implemented lower here
      	
//      	if(config.pagingConfig!=undefined && config.pagingConfig!=null){
//      		Sbi.debug('DynamicGridPanel load first page');
//      		this.store.loadPage(1);
//      	}else{
//      		Sbi.debug('DynamicGridPanel load store');
//      		this.store.load();
//      	}
      	
      	var additionalButtons = Sbi.widget.grid.StaticGridDecorator.getAdditionalToolbarButtons(this.decorators);
      	      	
      	if(this.filterConfig!=undefined && this.filterConfig!=null){
      		this.tbar = Ext.create('Sbi.widgets.grid.DynamicFilteringToolbar2',Ext.apply(config.filterConfig||{},
      				{store: this.store, additionalButtons:additionalButtons, lovProvider: config.lovProvider,
      				profileAttributes: config.profileAttrib}));
      	}
      	
    	this.callParent(arguments);
    	this.bbar = this.pagingToolbar;
    	this.on('afterrender', this.loadStore, this);
      	
//    	Sbi.debug('DynamicGridPanel costructor OUT');
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
    		this.pagingToolbar = Ext.create('Ext.PagingToolbar',defaultPagingConfig);
    		this.bbar = this.pagingToolbar;
    		Sbi.debug('DynamicGridPanel add paging OUT');
    	}
    },
    
    
    updateGrid: function()
    {
//        Sbi.debug('DynamicGridPanel updategrid IN');
            	
        var columns = this.store.getColumns();
       
        Sbi.widget.grid.StaticGridDecorator.addButtonColumns(this.decorators, this.columns, this);

        if(this.bbar!=undefined && this.bbar!=null){     
         this.bbar.bindStore(this.store);      
         this.bbar.doLayout();
        }
        
        if(columns && columns.length>0 && columns[0]=="recNo"){
         columns = Ext.Array.slice(columns,1);
        }
        
        this.reconfigure(this.store,columns);	
        
        Sbi.debug("[OUT] constructor() DynamicGridPanel");
    }
    
    , loadStore: function(t){
//    	this.showMask();
    	if(t.pagingConfig!=undefined && t.pagingConfig!=null){
      		Sbi.debug('DynamicGridPanel load first page'); 
      		this.store.loadPage(1);
      	}else{
      		Sbi.debug('DynamicGridPanel load store');
      		this.store.load();
      	}
//    	this.hideMask.defer(2000, this);
    }

//	/**
//	 * Opens the loading mask 
//	 */
//    , showMask : function(){
//    	this.un('afterlayout',this.showMask,this);
//    	if (this.loadMask == null) {    		
//    		this.loadMask = new Ext.LoadMask('DynamicGridPanel', {msg: "Loading.."});
//    	}
//    	if (this.loadMask){
//    		this.loadMask.show();
//    	}
//    }
//
//	/**
//	 * Closes the loading mask
//	*/
//	, hideMask: function() {
//    	if (this.loadMask && this.loadMask != null) {	
//    		this.loadMask.hide();
//    	}
//	} 

});