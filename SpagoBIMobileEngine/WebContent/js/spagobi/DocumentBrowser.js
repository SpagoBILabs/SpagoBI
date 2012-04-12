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
	    title: 'Document Browser',
	    useTitleAsBackText: false,
	    displayField: 'name',
	    toolbar:{style:'-webkit-border-radius: 0.55em; border-radius: 0.55em;'
				,height: 60
				},

		getItemTextTpl: function(node) {
			var tplTxt = '<tpl if="typeCode == \'' + Sbi.constants.documenttype.report + '\'">'+
	        '<div class="table-item">{name}</div>'+
    	    '</tpl>'+
    	    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.chart + '\'">'+
	        	'<div class="chart-item">{name}</div>'+
	        '</tpl>'+
    	    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.cockpit + '\'">'+
		        '<div class="composed-item">{name}</div>'+
		    '</tpl>'+
		    '<tpl if="typeCode == undefined || typeCode == null || typeCode ==\'\'">'+
		        '<div class="navigate">{name}</div>'+
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
			//direct execution: no preview
			var rec = record.attributes.record.data;
			  Ext.dispatch({
				  controller: app.controllers.mobileController,
				  action: 'getRoles',
				  id: rec.id,
				  label: rec.label, 
				  engine: rec.engine, 
				  typeCode: rec.typeCode
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