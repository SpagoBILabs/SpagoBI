/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('app.views.TableExecutionPanel',{
	extend: 'app.views.WidgetPanel',
	config:{
		cls: 'spagobi-table',
		scroll: 'vertical',		
	},



	initialize : function() {
		this.callParent();
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

		var tbConfig = {
				title       : '',
				store       : store,
				multiSelect : false,
				conditions  : resp.features.conditions,
				columns    : resp.features.columns,
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

		if(fromcomposition || fromCross){
			tbConfig.width='100%';
			tbConfig.height='100%';
		}
		
		
		
		if(this.header){
			var h =0;
			try{
				h = this.header.getHeight();
				if(!h){
					h = this.header.getStyle().height;
					h = h.replace('px','');
					h = h.replace('em','');
					h = h.replace('pt','');
					h = parseFloat(h);
				}
			}catch(e){
				
			}
			
			
			headerFooter = headerFooter+ h;
		}
		if(this.footer){
			var h =0;
			try{
				h = this.footer.getHeight();
				if(!h){
					h = this.footer.getStyle().height;
					h = h.replace('px','');
					h = h.replace('em','');
					h = h.replace('pt','');
					h = parseFloat(h);
				}
			}catch(e){
				
			}
			
			
			headerFooter = headerFooter+ h;
		}
		
		if(!tbConfig.style){
			tbConfig.style =""; 
		}
		
		tbConfig.style = tbConfig.style + "margin-top: "+headerFooter+";";
		
		var table =new Ext.ux.touch.grid.List(tbConfig);
		

		table.applyStore(store);

//
//		if(fromcomposition){
//			this.insert(0, table);
//
//		}else if(fromCross){
//			var r = new Ext.Panel({
//				style:'z-index:100;',
//				height:'100%'
//
//			});
//			r.add(table);
//			this.insert(0, r);
//
//		}else{
			this.add(table);
//		}
		if(this.IS_FROM_COMPOSED){
			this.loadingMask.hide();
		}

	}

	, setCrossNavigation: function(resp, target, crossParams){

		var drill = resp.features.drill;
		if(drill != null && drill != undefined){
			var params = drill.params;

			var serie =null;
			var category = null;

			var cell = target;
			for(var i = 0; i<cell.attributes.length; i++){
				var at = cell.attributes[i];
				if(at.name == 'dataindex'){
					serie = cell.textContent;
					category = at.nodeValue;
				}
			}


			if(params != null && params != undefined){
				for(i=0; i< params.length; i++){
					var param = params[i];
					var name = param.paramName;
					var type = param.paramType;

					/*	RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE */
					if(type == 'SERIE'){
						
						try{
							var parentNode = cell.parentNode  ;
							if(parentNode){
								var childNodes = parentNode.childNodes;
								for(var u=0; u<childNodes.length; u++){
									var node = childNodes[u];
									if(node.attributes.dataindex.nodeValue==name){
										serie = node.textContent;
										break;
									}
								}
							}
						}catch(e){}
						
						crossParams.push({name : name, value : serie});
					}else if(type == 'CATEGORY'){
						crossParams.push({name : name, value : category});
					}else{
						crossParams.push({name : name, value : param.paramValue});
					}
				}
			}				
		}
		return crossParams;
	}

});