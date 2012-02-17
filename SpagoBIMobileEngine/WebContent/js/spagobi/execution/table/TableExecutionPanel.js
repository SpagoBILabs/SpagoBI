app.views.TableExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical'
	    , initComponent: function (options)	{

			console.log('init table execution');
			app.views.TableExecutionPanel.superclass.initComponent.apply(this, arguments);


		},
		setTableWidget: function(resp, fromcomposition){
			
			  var mask = new Ext.LoadMask(this.el, {msg:"Loading table..."});
				
			  this.on('render', mask.show());
			  
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
		      
		      	var tbConfig = {
					store       : store,
					multiSelect : false,
					dockedItems: [toolbarForTable],
					conditions  : resp.features.conditions,
					colModel    : resp.features.columns
			    	,listeners: { el:{ tap:function(e){
		      					//captures event e on target
		      					var target = e.target;
		      					var textCell = target.innerText;
		      					var row = target.parentNode;
		      					var cellsOfRow = row.cells;
		      					var rowIdx = row.rowIndex;
			  			  		//			alert(e);
		      					//put cross navigation code here!
			    							} 
		      						} 
		      		}
		      	};
				if(fromcomposition){
					tbConfig.width='100%';
					tbConfig.height='100%';
				}else{
					tbConfig.bodyMargin='2px 2px 2px 2px';
					tbConfig.fullscreen=true;
				}
				
		      app.views.table = new Ext.ux.TouchGridPanel(tbConfig);
				if(fromcomposition){
				      this.add(app.views.table);		      
				      this.doLayout();
				}
		      mask.hide();
		}

		
});