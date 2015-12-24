/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.define('app.views.ComposedExecutionPanel',{
		extend: 'app.views.WidgetPanel',
		config:{
			executionInstance: null,
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
		
			var documentsList = this.resp.documents.docs;
			var executionInstance = Ext.apply({}, this.resp.executionInstance);
			this.setSubDocumentNumber(documentsList.length);
			this.setSubDocumentsToUpdate(new Array(this.getSubDocumentNumber()));
			this.setSubDocumentsToUpdateNumber(this.getSubDocumentNumber());
			
			if (documentsList != undefined && documentsList != null) {
				this.getSubdocuments().length = documentsList.length;;
				for (var i = 0; i < documentsList.length; i++) {
					//var subDocumentPanel = this.buildPanel(documentsList[i]);
					var mainDocumentParameters = executionInstance.PARAMETERS;
					if(mainDocumentParameters){
						mainDocumentParameters = Ext.decode(mainDocumentParameters);
					}
					var subDocumentDefaultParameters = documentsList[i].IN_PARAMETERS;
					var subDocumentParameters = Ext.apply(subDocumentDefaultParameters, mainDocumentParameters);
					var subDocumentExecutionInstance = Ext.apply({}, documentsList[i]);
					subDocumentExecutionInstance.PARAMETERS = subDocumentParameters;
					subDocumentExecutionInstance.IS_FROM_COMPOSED = true;
					subDocumentExecutionInstance.ROLE = executionInstance.ROLE;
					subDocumentExecutionInstance.position = i;
					app.controllers.composedExecutionController.executeSubDocument(subDocumentExecutionInstance, this,i);
				}
				///to add a slider configuration property
				if(this.resp.slider && this.resp.slider.name){
					this.addSlider(this.resp.slider);
				}
			}
			this.on("updatedOneDocument",this.updateOneDocument,this);
			this.callParent();
		},
		
		addWidgetComposed: function(resp, type, composedComponentOptions, positionInComposition){

			var panel;
			var thisPanel = this;
			
			//Builds teh subdocument panel
			resp.config = Ext.apply(resp.config||{},{IS_FROM_COMPOSED: true});
			resp.config = Ext.apply(resp.config||{},composedComponentOptions.executionInstance||{});
			
			if (type == "chart") {
				panel = Ext.create("app.views.ChartExecutionPanel",{resp: resp, fromcomposition: true, executionInstance: composedComponentOptions.executionInstance, parentDocument:this});
			} else {
				panel = Ext.create("app.views.TableExecutionPanel",{resp: resp, fromcomposition: true, executionInstance: composedComponentOptions.executionInstance, parentDocument:this});
			}

			
			panel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
					
			var height;
			var width;
			if(resp.config && resp.config.height){
				height =resp.config.height;
			}
			if(resp.config && resp.config.width ){
				width =resp.config.width;
			}

			var style = panel.getStyle();
//			if(!style){
				style = "";
//			}
			style = style+" float: left;";
			style = style+" width:"+ width+"; height:"+height;			

			panel.setStyle(style);

			//if its the first execution the subdocument is added to the composition
			if(!this.getCrossNavigated()){
//				this.insert(positionInComposition, panel);
				this.getSubdocuments()[positionInComposition]=(panel);

			}
				//if the document is refreshed we refresh it
				this.fireEvent("updatedOneDocument",panel,composedComponentOptions.executionInstance.position);
		
		}
		
		,
		propagateCrossNavigationEventForSlider : function(paramsArray) {
			console.log('app.views.ComposedExecutionPanel:execCrossNavigation: IN');
			this.setSubDocumentsToUpdate(new Array(this.getSubDocumentNumber()));
			this.setSubDocumentsToUpdateNumber(this.getSubDocumentNumber());
			this.setCrossNavigated(true);
			var params = {};
			for(var i=0; i<(this.getSubdocuments()).length; i++){
				var panel = (this.getSubdocuments())[i];
				console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');

				
				for (var j = 0 ; j < paramsArray.length ; j++) {
					var aParam = paramsArray[j];
					params[aParam.name] = aParam.value;
				}

				app.controllers.composedExecutionController.refreshSubDocument(panel, this, params);
			}
			//update header, footer, title
			this.updatePanel(params);
		},
		
		propagateCrossNavigationEvent : function(sourcePanel, paramsArray, targetDoc) {
			
			if(targetDoc){
				this.fireEvent('execCrossNavigation', sourcePanel, paramsArray, targetDoc);
			}else{
				console.log('app.views.ComposedExecutionPanel:execCrossNavigation: IN');
				sourcePanel.parentDocument.setSubDocumentsToUpdate(new Array(this.getSubDocumentNumber()));
				sourcePanel.parentDocument.setSubDocumentsToUpdateNumber(this.getSubDocumentNumber());
				this.setCrossNavigated(true);
				var params = {};
				for(var i=0; i<(sourcePanel.parentDocument.getSubdocuments()).length; i++){
					var panel = (sourcePanel.parentDocument.getSubdocuments())[i];
					console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');

					for (var j = 0 ; j < paramsArray.length ; j++) {
						var aParam = paramsArray[j];
						params[aParam.name] = aParam.value;
					}

					app.controllers.composedExecutionController.refreshSubDocument(panel,  sourcePanel.parentDocument, params);
				}
				this.updatePanel(params);
			}
			//update header, footer, title
			
		},
		
		updateOneDocument:function(panel,position){
			//update the document in the temp list
			(this.getSubDocumentsToUpdate())[position] = panel;
			this.setSubDocumentsToUpdateNumber(this.getSubDocumentsToUpdateNumber()-1);
			//if all the subdocuments has benen updated 
			if(this.getSubDocumentsToUpdateNumber()==0){				
				this.removeAll();
				for(var i=0; i<this.getSubDocumentNumber();i++){
					this.insert(i, this.getSubDocumentsToUpdate()[i]);//add them to the composition
				}
			}
		}

		, addSlider: function(slider){

			var sliderComp = Ext.create('app.views.Slider',{
				sliderAttributes: slider,
				composedDoc: this
			});
			var minLbl = {
	            xtype: 'label',
	            style: 'width: 7%;color: blue;text-align: right; margin-top:5px;',
	            html: slider.minValue
	        };
			var maxLbl = {
		            xtype: 'label',
		           style: 'width: 7%;color: blue; margin-top:5px;',
		            html: slider.maxValue
		    };
			
			this.sliderToolbar = new Ext.Toolbar({
                xtype: 'toolbar',
                docked: 'bottom',
                height:30,
                ui: 'neutral',
                items : [minLbl, 
                         sliderComp,
                         {xtype:"spacer"},
                         maxLbl]
            });
			
			this.add(this.sliderToolbar);
		}
		
});