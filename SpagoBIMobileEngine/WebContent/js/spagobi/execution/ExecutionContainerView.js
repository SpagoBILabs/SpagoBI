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
		 */
		removeDocumentsOnTheRight: function(documentPos, offset){
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
					toolbar.cleanNavigationToolbarFromPosition(this.executedDocuments);
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
					this.removeDocumentsOnTheRight(positionOfActive,-1);//we should remove also the active document
				}else if(fromCross){
					this.removeDocumentsOnTheRight(positionOfActive);
				}
			}

			var newExecution = Ext.create("app.views.ExecutionView", {positionInExecutionContainer: this.executedDocuments});
			newExecution.setWidget(resp, type, fromCross, executionInstance);
			this.add(newExecution);
			this.executedDocumentsList.push(newExecution);
			this.setActiveItem(newExecution);
			
			//update the navigation toolbar
			if(this.containerToolbars){
				var toolbar;
				
				for(var i=0; i<this.containerToolbars.length; i++){
					toolbar = this.containerToolbars[i];
					toolbar.addDocumentToNavigationToolbar(executionInstance.OBJECT_LABEL, this.executedDocuments);
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
		
//		goToPreviousExecutions: function(){
//			if(this.executedDocuments>1){
//				this.setActiveItem(this.executedDocumentsList[this.executedDocuments-2]);
//				this.remove(this.executedDocumentsList[this.executedDocuments-1]);
//				this.executedDocuments--;
//			}
//		},
		
//		DA FARE: AGGIORNARE LA TOOLBAR DI NAVIGAZIONE
		goToPreviousExecutions: function(){
			var positionOfActive = this.getActiveItem().getPositionInExecutionContainer();
			if(positionOfActive>0){
				this.removeDocumentsOnTheRight(positionOfActive,-1);
				this.setActiveItem(this.executedDocumentsList[position-1]);
			}else{
				app.views.viewport.goHome();//go home with out refresh document browser
			}
			
//			var position = this.getActiveItem().getPositionInExecutionContainer();
//			if(position>0){
//				this.setActiveItem(this.executedDocumentsList[position-1]);
//				for(var i=position; i<this.executedDocuments; i++){
//					this.remove(this.executedDocumentsList[i]);
//				}
//				this.executedDocuments = position;
//			}
		},
//		
		changeActiveDocument:function(documentPosition){
			this.setActiveItem(this.executedDocumentsList[documentPosition]);
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
});