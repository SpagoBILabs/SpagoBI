/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.define('app.views.ExecutionContainerView',{
		extend:'Ext.Panel',
		config:{
			 fullscreen: true,
			 layout: 'card',
			
			 title: 'Execution Container View'
		},
		
		constructor: function(config){
			Ext.apply(this, config||{});
			this.callParent(arguments);
		},

		
		initialize: function ()	{
			console.log('Init Execution Container View');
			this.executedDocuments = 0;
			this.executedDocumentsList = new Array();
			this.callParent(this, arguments);
			

	        // invokes before each ajax request 
	        Ext.Ajax.on('beforerequest', function(){        
	                // showing the loadding mask
	        	app.views.executionContainer.setMasked({xtype:'loadmask',message:'Please wait...'});
	        });

	        // invokes after request completed 
	        Ext.Ajax.on('requestcomplete', function(){      
	                // showing the loadding mask
	        	app.views.executionContainer.setMasked(false);
	        });     
		},
		
		/**
		 * removes the documents on the right of the pivot document (usually the active one)
		 * @documentPos documentPos: position o the pivot document
		 * @param offset: a offset.. If -1 it removes also the pivot document
		 * @param goForward: true if we are adding a new document
		 */
		removeDocumentsOnTheRight: function(documentPos, offset, goForward){
			if(!offset){
				offset = 0;
			}
			var positionOfActive = documentPos+offset;
			for(var i=positionOfActive+1; i<this.executedDocuments; i++){
				this.remove(this.executedDocumentsList[i]);
				var p = this.executedDocumentsList.pop();
				p.destroy();
			}
			this.executedDocuments = positionOfActive+1;
			//remove the old tab in the bread crumb
			if(this.containerToolbars){
				var toolbar;
				
				for(var i=0; i<this.containerToolbars.length; i++){
					toolbar = this.containerToolbars[i];
					toolbar.cleanNavigationToolbarFromPosition(this.executedDocuments, goForward);
				}
			}
			
		},
		
		addExecution: function(resp, type, fromCross, executionInstance,refresh){
			//if we are in a cross navigation, we should remove all the next documents..
			//Suppose we have this breadcrumbs (A,B,C,D) and the active document is  B
			//if the user execute the cross navigation from B to C, we should delete the
			//execution view for C and D because the parameters of C and D are changed
			//The same thing happens if you click on refresh.. But
			//with refresh you should delete also the current active item
			if(this.getActiveItem()!=0){
				var positionOfActive = this.getActiveItem().getPositionInExecutionContainer();
				if(refresh){
					this.removeDocumentsOnTheRight(positionOfActive,-1, true);//we should remove also the active document
				}else if(fromCross){
					this.removeDocumentsOnTheRight(positionOfActive, null, true);
				}
			}

			var newExecution = Ext.create("app.views.ExecutionView", {positionInExecutionContainer: this.executedDocuments});
			newExecution.setWidget(resp, type, fromCross, executionInstance);
			this.add(newExecution);
			this.executedDocumentsList.push(newExecution);
			this.setActiveExecution(newExecution);
			
			//update the navigation toolbar
			if(this.containerToolbars){
				var toolbar;
				
				for(var i=0; i<this.containerToolbars.length; i++){
					toolbar = this.containerToolbars[i];
					var label = executionInstance.OBJECT_NAME;
					try{
						if(Sbi.settings && Sbi.settings.navigationToolbar && Sbi.settings.navigationToolbar.label){
							label = eval(Sbi.settings.navigationToolbar.label);
						}
					}catch(e){
						console.debug(e);
					}

					toolbar.addDocumentToNavigationToolbar(label, this.executedDocuments);
				}
			}
			this.executedDocuments++;
			
		},
		
		clearExecutions: function(){
			this.executedDocuments = 0;
			this.executedDocumentsList = new Array();
			if(this.containerToolbars){
				for(var i=0; i<this.containerToolbars.length; i++){
					this.containerToolbars[i].cleanNavigationToolbarFromPosition(0);
				}
			}
			
			this.removeAll();
		},
		
		/**
		 * restore the previous execution.
		 * If the fromParameters parameter is true
		 * @param fromParameters
		 */
		goToPreviousExecutions: function(fromParameters){
			var noDocumentRendered = true;
			var positionOfActive = this.getActiveItem();
			if(positionOfActive!=0){
				positionOfActive = positionOfActive.getPositionInExecutionContainer();
				noDocumentRendered=false;
			}
			// (fromParameters && !noDocumentRendered): execute the previous in the parameters panel just after the first document is rendered
			if(positionOfActive>0 || (fromParameters && !noDocumentRendered)){
				
				if(fromParameters){
					var noParametersPageNeeded=  (this.executedDocumentsList[positionOfActive]).getItems().items[0].executionInstance.noParametersPageNeeded;
					app.views.viewport.goExecution({noParametersPageNeeded: noParametersPageNeeded});
				}else{
					this.removeDocumentsOnTheRight(positionOfActive,-1);
					this.setActiveExecution(this.executedDocumentsList[positionOfActive-1]);
				}


			}else{
				//app.controllers.mobileController.backToBrowser();
				app.views.viewport.goHome("refresh");//go home with out refresh document browser
			}
			
		},

		changeActiveDocument:function(documentPosition){
			this.setActiveExecution(this.executedDocumentsList[documentPosition]);
		},
		
		/**
		 * gets the execution instance of the active document
		 * @returns
		 */
		getActiveExecutionInstance: function(){
			var activeItem = this.getActiveItem();
			if(activeItem){
				return activeItem.getExecutionInstance();
			}	
			return null;
		}
		
		,refresh: function(){
			if(app.views.executionContainer && app.views.executionContainer.getActiveExecutionInstance()){
				var active = app.views.executionContainer.getActiveExecutionInstance();
				app.controllers.executionController.executeTemplate( { executionInstance: active}, null, true);				
			}
		}
		,showLoadingMask : function(panel){
			this.loadingMask = new Ext.LoadMask(panel.id, {msg:"Loading..."});					
			this.loadingMask.show();
			this.un('afterlayout',this.showLoadingMask,this);
		}
		,setActiveExecution:function(execution){
			this.setActiveItem(execution);
			//manages the parameters panel icon
			if(execution.getExecutionInstance().noParametersPageNeeded){
				//if there is no parameter we hide the parameter icon
				app.views.customTopToolbar.hideItem("params");
				app.views.customBottomToolbar.hideItem("params");
			}else{
				//if there is some parameter to value we show the parameter icon
				app.views.customTopToolbar.showItem("params");
				app.views.customBottomToolbar.showItem("params");
			}
		}
});