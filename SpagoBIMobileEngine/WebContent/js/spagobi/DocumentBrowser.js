app.views.DocumentBrowser = Ext.extend (Ext.NestedList,
		{	
	    scroll: 'vertical',
	    dock : 'left',
	    layout:'card',
	    activeItem: 0,
		cardSwitchAnimation: 'slide',
	    backText : '&lt;',
	    store: null,
	    data: null,
	    flex:1,
	    displayField: 'name',

		getItemTextTpl: function(node) {
			var tplTxt = '<tpl if="typeCode == \'MOBILE_TABLE\'">'+
	        '<div class="table-item">{name}</div>'+
    	    '</tpl>'+
    	    '<tpl if="typeCode == \'MOBILE_CHART\'">'+
	        	'<div class="chart-item">{name}</div>'+
	        '</tpl>'+
    	    '<tpl if="typeCode == \'MOBILE_COMPOSED\'">'+
		        '<div class="composed-item">{name}</div>'+
		    '</tpl>'+
		    '<tpl if="typeCode == undefined || typeCode == null || typeCode ==\'\'">'+
		        '<div>{name}</div>'+
		    '</tpl>';
		    return tplTxt;
		},
	    getDetailCard: function( record, parentRecord ){
       	
            Ext.dispatch(
            {
              controller: app.controllers.mobileController,
              action: 'showDetail',
              record: record
              
            });
        },

		initComponent: function(){

		    Sbi.config = {};
			
			var url = {
		    	host: hostGlobal
		    	, port: portGlobal
		
		    };
		
		    var executionContext = {};
		    
		    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
		    	baseUrl: url
		
		    });
			var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
			this.services = new Array();
			this.services['loadFolderContentService'] = Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: 'DOCUMENT_BROWSER_ACTION'
				, baseParams: params
			});
			
			this.store = new Ext.data.TreeStore({
			    model: 'browserItems',

			    proxy: {
					type: 'ajax',
					url: this.services['loadFolderContentService'],

			        reader: {
			            type: 'tree',
			            root: 'samples'
			        }
			    }
			});

			this.store.sync();
			
			app.views.DocumentBrowser.superclass.initComponent.apply(this, arguments);

		}

		
});