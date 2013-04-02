/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * This is a store "wrapper" that loads all the rows in the client memory and adds the capability to filter them live..
 * It extends the Ext.data.Store so it is general and its specialization derive from the configuration passed to the constructor.   
 * 
 * 
 *     @example
 *     ...
 *		//define the store     
 *   	this.storeConfig = Ext.apply({
 *   		parentGrid: this,
 *   		model: modelname,
 *   		filteredProperties: ["DATASOURCE_LABEL","DESCRIPTION"],
 *   		proxy: {
 *   			type: 'ajax',
 *   			url:  this.services['getAllValues'],
 *   			reader: {
 *   				type:"json",
 *   				root: "root"
 *   			}
 *   		}
 *   	},this.storeConfig||{});
 *   	//create the store
 *   	this.store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);
 *   	//adds the pagination toolbar
 *		this.addPaging(config);
 *		//add the filter
 *		this.tbar = Ext.create('Sbi.widgets.grid.InLineGridFilter',Ext.apply({store: this.store, additionalButtons:additionalButtons}));
 *     	...
 * 
 * 
 */
Ext.define('Sbi.widgets.store.InMemoryFilteredStore', {
    extend: 'Ext.data.Store'

    ,config: {
    	/**
    	 * The container of the items loaded from the store
    	 */
    	inMemoryData: null,
    	/**
    	 * The list of the properties that should be filtered 
    	 */
    	filteredProperties: new Array(),
    	/**
    	 * False to reload the wrapped store. It force the refresh of the in memory data
    	 */
    	useChache: true, //cache means that the values are cached
    	/**
    	 * The string used as filter
    	 */
    	filterString: null
    }
      
    /**
     * Creates the store. 
     * @param {Object} config (optional) Config object. This is the normal configuration of a generic store
     */
    , constructor: function(config) {
    	this.initConfig(config)
    	this.callParent(arguments);
    	this.on("load",function(a){
    		if(!this.inMemoryData){
    			this.inMemoryData = this.data.items.slice(0);//clone the items
    		}
   			var items = this.getFilteredItems(this.inMemoryData, this.filteredProperties, this.filterString);
   			items = this.getPageItems(this.start, this.limit, items);
   			this.removeAll();
   			for(var i=0; i<items.length;i++){
   				this.add(items[i]);
   			}
    	},this);
    }

    /**
     * @Override
     */
	, load: function(options) {

		if(options.limit){
			this.limit = options.limit;
		}
		if(options.start){
			this.start = options.start;
		}else if(this.limit){
			this.start = 0;
		}
		
		this.page = options.page;
		
		if(this.useChache ||  this.inMemoryData==null ||  this.inMemoryData==undefined){
			//set null the paging configuration to load all the items from the store
			options.start = null;
			options.limit = null;
			options.page = null;
			this.inMemoryData=null;
			this.callParent([options]);
		}

		this.filterString = options.filterString;
		return this;
		
	}
	
	/**
	 * Pages the in memory data.
     * @private
     * @param {Number} start The first element index
     * @param {Number} limit The number of items in the page
     * @param {Array} items The list of the items to get the page from
     */
	, getPageItems: function(start, limit, items){
		if(start!=null && start!=undefined && limit!=null && limit!=undefined){
			var pageItems = [];
			for(var i = start; (i< items.length && i<limit+start); i++){
				pageItems.push(items[i]);
			}
			return pageItems;
		}
		return items;
	}
	
	/**
	 * Filters the data in memory.
     * @private
     * @param {Array} items The list of the items to filter
	 * @param {Array} properties The list of properties to search
	 * @param {String} filterString string to find (apply a like)
     */
	, getFilteredItems: function(items, properties, filterString){
		var filteredCount = 0;
		if(this.filterString){
			var filteredItems = [];
			for(var i=0; i<items.length; i++){
				var item = items[i];
				for(var p in item.data){
					var bool = (properties==null || properties==undefined  || 
							((properties.contains(p)) && 
									(((item.data[p].toLowerCase()).indexOf(filterString.toLowerCase()))>=0)));
					if(bool){
						filteredCount++;
						filteredItems.push(item);
						break;
					}
				}
			}
			this.totalCount = filteredCount;
			return filteredItems;
		}
		return items;
	}
	


	
});