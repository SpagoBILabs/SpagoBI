app.views.TableExecutionPanel = Ext.extend(app.views.WidgetPanel,

		{
	    scroll: 'vertical'
	    , initComponent: function (options)	{

			console.log('init table execution');


			app.views.TableExecutionPanel.superclass.initComponent.apply(this, arguments);
			
			if(this.IS_FROM_COMPOSED){
				this.on('afterlayout',this.showLoadingMask,this);
			}
			this.addEvents('execCrossNavigation');
			

		}

		,setTableWidget: function(resp, fromcomposition, fromCross){

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
					colModel    : resp.features.columns,
					scope: this,
			    	listeners: { 
			    		tap: { 
			    			element : 'el',
			    			fn : function(e) {
		      		 			var crossParams = new Array();
	      						var target = e.target;

	      						this.setCrossNavigation(resp, target, crossParams);
	      						var targetDoc;
	      						if(resp.config != undefined && resp.config.drill != undefined){
		      						targetDoc = this.setTargetDocument(resp);		      						
	      						}
	      						this.fireEvent('execCrossNavigation', this, crossParams, targetDoc);
    						},
    						scope : this
  						}
		      		}
		      	};
				if(fromcomposition || fromCross){
					tbConfig.width='100%';
					tbConfig.height='100%';

				}else{
					tbConfig.bodyMargin='2px 2px 2px 2px';
					tbConfig.fullscreen=true;

				}
				
				app.views.table = new Ext.ux.TouchGridPanel(tbConfig);
				if(fromcomposition){
					  this.insert(0, app.views.table);
				      this.doLayout();
				}
				if(fromCross){
					var r = new Ext.Panel({
						style:'z-index:100',
						height:'100%',
						items: [app.views.table]
						        
					});
					
					this.insert(0, r);
					r.doLayout();
					this.doLayout();
				}
				if(this.IS_FROM_COMPOSED){
					this.loadingMask.hide();
				}
		}

		, setCrossNavigation: function(resp, target, crossParams){
			
			var drill = resp.features.drill;
			if(drill != null && drill != undefined){
				var params = drill.params;
				
				//captures event e on target				
				var textCell = target.innerText;
				var row = target.parentNode;
				var cellsOfRow = row.cells;
				var rowIdx = row.rowIndex;
				var attributes = target.attributes;
				var column= '';
				for(i = 0; i<attributes.length; i++){
					var at = attributes[i];
					if(at.name == 'mapping'){
						column = at.value;
					}
				}
				if(params != null && params != undefined){
					for(i=0; i< params.length; i++){
						var param = params[i];
						var name = param.paramName;
						var type = param.paramType;

						/*	RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE */
						if(type == 'SERIE'){
							crossParams.push({name : name, value : textCell});
						}else if(type == 'CATEGORY'){
							crossParams.push({name : name, value : column});
						}else{
							crossParams.push({name : name, value : param.paramValue});
						}

					}
				}				
			}
			return crossParams;
		}
		
});