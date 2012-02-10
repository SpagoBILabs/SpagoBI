app.views.TableExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function (options)	{

			console.log('init table execution');
		    
			app.views.TableExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setTableWidget: function(resp){
			
		      var store = new Ext.data.Store({
		     		root: 'values'
		     		, fields: resp.features.fields
		      		, pageSize: 5
		      		, data: resp
		     		, proxy: {
			              type: 'memory',	              
			              reader: {
			                  type: 'json',
			                  root: 'values',	 
			                  totalProperty: "total",    
			                  totalCount: 'total'
			              }
		          }
		      });

		      store.load();
		      var toolbarForTable = new Ext.Toolbar({
					xtype : "toolbar",
					dock  : "top",						
					title : resp.features.title.value,
					style:  resp.features.title.style,
					layout: 'hbox',
					autoDestroy : true
	                
				});
		      app.views.table = new Ext.ux.TouchGridPanel({
					fullscreen  : true,
					store       : store,
		            //plugins    : [new Ext.ux.touch.PagingToolbar()],
		            
					multiSelect : false,
					dockedItems: [toolbarForTable],
					conditions  : resp.features.conditions,
					colModel    : resp.features.columns
				});
		      
		      this.add(app.views.table);
		      
		}

		
});