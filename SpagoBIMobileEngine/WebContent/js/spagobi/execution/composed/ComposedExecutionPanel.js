/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.define('app.views.ComposedExecutionPanel',{
		extend: 'app.views.WidgetPanel',
		config:{
			scroll: 'vertical',
		    fullscreen: true,
			subdocuments: [],
			subDocumentNumber: 0,
			subDocumentsToUpdate:[],
			subDocumentsToUpdateNumber:0,
			/**
			 * @private
			 * true if at least on navigation has been executed
			 */
			crossNavigated: false
			
		},
		
		constructor: function(config){
			Ext.apply(this,config);
			this.callParent();
			this.addEvents("updatedOneDocument");
		},
		
		
		initialize: function(){
			var title = this.resp.title.value;
			
			var documentsList = this.resp.documents.docs;
			var documentWidth = this.resp.documents.totWidth;
			var documentHeight = this.resp.documents.totHeight;
			
			var executionInstance = Ext.apply({}, this.resp.executionInstance);
			this.setSubDocumentNumber(documentsList.length);
			if (documentsList != undefined && documentsList != null) {
				for (var i = 0; i < documentsList.length; i++) {
					//var subDocumentPanel = this.buildPanel(documentsList[i]);
					var mainDocumentParameters = executionInstance.PARAMETERS;
					var subDocumentDefaultParameters = documentsList[i].IN_PARAMETERS;
					var subDocumentParameters = Ext.apply(subDocumentDefaultParameters, mainDocumentParameters);
					var subDocumentExecutionInstance = Ext.apply({}, documentsList[i]);
					subDocumentExecutionInstance.PARAMETERS = subDocumentParameters;
					subDocumentExecutionInstance.IS_FROM_COMPOSED = true;
					subDocumentExecutionInstance.ROLE = executionInstance.ROLE;
					subDocumentExecutionInstance.position = i;
					app.controllers.composedExecutionController.executeSubDocument(subDocumentExecutionInstance, this);
				}
				///to add a slider configuration property
//				if(this.resp.slider && this.resp.slider.name){
//					this.addSlider(items, this.resp.slider);
//				}
			}
			this.on("updatedOneDocument",this.updateOneDocument,this);

		},
		
		addWidgetComposed: function(resp, type, composedComponentOptions){

			var panel;
			var thisPanel = this;
			
			//Builds teh subdocument panel
			resp.config = Ext.apply(resp.config,{IS_FROM_COMPOSED: true});
			resp.config = Ext.apply(resp.config,composedComponentOptions.executionInstance||{});
			
			if (type == "chart") {
				panel = Ext.create("app.views.ChartExecutionPanel",{resp: resp, fromcomposition: true, executionInstance: composedComponentOptions.executionInstance, parentDocument:this});
			} else {
				panel = Ext.create("app.views.TableExecutionPanel",{resp: resp, fromcomposition: true, executionInstance: composedComponentOptions.executionInstance, parentDocument:this});
			}
			
			panel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
					
			var height;
			var width;
			if(resp.config.height){
				height =resp.config.height;
			}
			if(resp.config.width ){
				width =resp.config.width;
			}
			var style = panel.getStyle();
			if(!style){
				style = "";
			}
			style = style+" float: left;";
			style = style+" width:"+ width+"; height:"+height;
			panel.setStyle(style);

			//if its the first execution the subdocument is added to the composition
			if(!this.getCrossNavigated()){
				this.add(panel);
				this.getSubdocuments().push(panel);
			}else{
				//if the document is refreshed we refresh it
				this.fireEvent("updatedOneDocument",panel,composedComponentOptions.executionInstance.position);
			}
		}
		
		,
		propagateCrossNavigationEvent : function(sourcePanel, paramsArray) {
			console.log('app.views.ComposedExecutionPanel:execCrossNavigation: IN');
			sourcePanel.parentDocument.setSubDocumentsToUpdate(new Array(this.getSubDocumentNumber()));
			sourcePanel.parentDocument.setSubDocumentsToUpdateNumber(this.getSubDocumentNumber());
			this.setCrossNavigated(true);
			
			for(var i=0; i<(sourcePanel.parentDocument.getSubdocuments()).length; i++){
				var panel = (sourcePanel.parentDocument.getSubdocuments())[i];
				console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');

				var params = {};
				for (var j = 0 ; j < paramsArray.length ; j++) {
					var aParam = paramsArray[j];
					params[aParam.name] = aParam.value;
				}

				app.controllers.composedExecutionController.refreshSubDocument(panel,  sourcePanel.parentDocument, params);
			}
		}
		,
		
		updateOneDocument:function(panel,position){
			//update the document in the temp list
			(this.getSubDocumentsToUpdate())[position] = panel;
			this.setSubDocumentsToUpdateNumber(this.getSubDocumentsToUpdateNumber()-1);
			//if all the subdocuments has benen updated 
			if(this.getSubDocumentsToUpdateNumber()==0){
				this.removeAll();
				for(var i=0; i<this.getSubDocumentNumber();i++){
					this.add(this.getSubDocumentsToUpdate()[i]);//add them to the composition
				}
			}
		}
//		,
//		execCrossNavigationHandler : function(sourcePanel, params, targetPanel) {
//			
//			console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');
//			
//			app.controllers.executionController.executeTemplate({
//				executionInstance : targetPanel.getExecutionInstance()
//				, parameters : null
//			}, panel);
//			
//		}
		, addSlider: function(items, slider){

			this.slider = new app.views.Slider({
				sliderAttributes: slider
			});
			var minLbl = {
	            xtype: 'component',
	            style: 'float: left; width: 7%',
	            cls: 'sliderLbl',
	            html: slider.minValue
	        };
			var maxLbl = {
		            xtype: 'component',
		            style: 'float: left; width: 7%',
		            cls: 'sliderLbl',
		            html: slider.maxValue
		    };
			items.push(minLbl);
			items.push(this.slider);
			items.push(maxLbl);
		}
		
});