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