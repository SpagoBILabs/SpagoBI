app.views.TableExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical'
	    , initComponent: function (options)	{

			console.log('init table execution');
		    
			app.views.TableExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setTableWidget: function(resp, fromcomposition){
			
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
		      
		      var chartConfig = {
					store       : store,
					multiSelect : false,
					dockedItems: [toolbarForTable],
					conditions  : resp.features.conditions,
					colModel    : resp.features.columns
		      };
				if(fromcomposition){
					chartConfig.width='100%';
					chartConfig.height='100%';
				}else{
					chartConfig.bodyMargin='50px 50px 100px 50px';
					chartConfig.fullscreen=true;
				}
				
		      app.views.table = new Ext.ux.TouchGridPanel(chartConfig);
		      
		      this.add(app.views.table);
		      
		}

		
});