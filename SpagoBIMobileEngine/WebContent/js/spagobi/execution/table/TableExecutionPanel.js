/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.views.TableExecutionPanel',{
		extend: 'app.views.WidgetPanel',
		config:{
			fullscreen:true,
			scroll: 'vertical'
//			style:'border: 3px solid red; background-color: #fff;',
			
			
		},

		constructor : function(config) {
			Ext.apply(this,config);
			this.callParent(arguments);
		
		},
		
		initialize : function() {
			this.setTableWidget(this.resp, this.fromcomposition,this.fromCross );
			console.log('init table execution');		
		}


		,setTableWidget: function(resp, fromcomposition, fromCross){
			console.log(resp.features.fields);
			console.log(resp);
			Ext.define("ListFields", {
			    extend: "Ext.data.Model",
			    config: {
			        fields: resp.features.fields
			    }
			});
			
			var store = Ext.create("Ext.data.Store", {
			    storeId: "ListStore",
			    model: "ListFields",
			    data : resp.values
			});
		     
//		      
//		      var toolbarForTable = new Ext.Toolbar({
//				xtype : "toolbar",
//				dock  : "top",						
//				title : resp.features.title.value,
//				style:  resp.features.title.style,
//				layout: 'hbox',
//				autoDestroy : true
//              
//		      });
		      
		      var tbConfig = {
		    	title       : 'Prova',
		        store       : store,
				multiSelect : false,
//				dockedItems: [toolbarForTable],
				conditions  : resp.features.conditions,
				columns    : resp.features.columns,
				//features    : resp.features,
				features : [
				            {
				                ftype    : 'Ext.ux.touch.grid.feature.Sorter',
				                launchFn : 'initialize'
				            },
				            {
				                ftype    : 'Ext.ux.touch.grid.feature.Editable',
				                launchFn : 'initialize'
				            }
				        ],
				scope: this,
		    	listeners: { 
		    		tap: { 
		    			element : 'element',
		    			fn : function(e) {
	      		 			var crossParams = new Array();
    						var target = e.target;
    						
    						this.setCrossNavigation(resp, target, crossParams);
    						if(crossParams.length != 0){
	      						var targetDoc;
	      						if(resp.features != undefined && resp.features.drill != undefined){
		      						targetDoc = this.setTargetDocument(resp);		      						
	      						}
	      						this.fireEvent('execCrossNavigation', this, crossParams, targetDoc);
    						}
						},
						scope : this
					}
	      		}
	      	};
		      
		      app.views.table =new Ext.ux.touch.grid.List(tbConfig);

		      app.views.table.applyStore(store);
		      this.add(app.views.table);

				if(fromcomposition || fromCross){
					tbConfig.width='100%';
					tbConfig.height='100%';

				}else{
					tbConfig.bodyMargin='2px 2px 2px 2px';
					tbConfig.fullscreen=true;

				}
				
				app.views.table = new Ext.ux.touch.grid.List(tbConfig);
				if(fromcomposition){
					  this.insert(0, app.views.table);
					  this.add(app.views.table);
				      this.doLayout();
				}
				if(fromCross){
					var r = new Ext.Panel({
						style:'z-index:100;',
						height:'100%',
						//items: [app.views.table]
						        
					});
					r.add(app.views.table);
					
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

				var row = target.parentNode;
				var cellsOfRow = row.children;

				var colValues = {};
				for(a=0; a<cellsOfRow.length; a++){
					var cell = cellsOfRow[a];
					for(i = 0; i<cell.attributes.length; i++){
						var at = cell.attributes[i];
						if(at.name == 'dataindex'){
							var nCol=at.value;
							colValues[nCol] = cell.textContent;
							
						}
					}
				}
				if(params != null && params != undefined){
					for(i=0; i< params.length; i++){
						var param = params[i];
						var name = param.paramName;
						var type = param.paramType;

						/*	RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE */
						if(type == 'SERIE'){
								var col = colValues[name];
								if(col){
									crossParams.push({name : name, value : col});
								}	
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