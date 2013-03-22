/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.define('app.views.ComposedExecutionPanel',{
		extend: 'app.views.WidgetPanel',
		config:{
			scroll: 'vertical',
		    fullscreen: true
				
		},
		
		constructor: function(config){
			Ext.apply(this,config);
			this.callParent();
		},
		
		
		initialize: function(){
			var title = this.resp.title.value;
			
			var documentsList = this.resp.documents.docs;
			var documentWidth = this.resp.documents.totWidth;
			var documentHeight = this.resp.documents.totHeight;
			
			var items = new Array();
			
			var executionInstance = Ext.apply({}, this.resp.executionInstance);
			
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
					app.controllers.composedExecutionController.executeSubDocument(subDocumentExecutionInstance, this);
				}
				///to add a slider configuration property
//				if(this.resp.slider && this.resp.slider.name){
//					this.addSlider(items, this.resp.slider);
//				}
			}


		},
		
		addWidgetComposed: function(resp, type, composedComponentOptions){

			var panel;
			resp.config = Ext.apply(resp.config,{IS_FROM_COMPOSED: true});
			resp.config = Ext.apply(resp.config,composedComponentOptions.executionInstance||{});
			
			if (type == "chart") {
				panel = Ext.create("app.views.ChartExecutionPanel",{resp: resp, fromcomposition: true});
			} else {
				panel = Ext.create("app.views.TableExecutionPanel",{resp: resp, fromcomposition: true});
			}
			
			panel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
			
			/*
			 * with this instruction, panel is not passed properly
			 */
			//this.on('execCrossNavigation', Ext.createDelegate(this.execCrossNavigationHandler, this, [panel], true));
			
			this.on('execCrossNavigation', function (sourcePanel, paramsArray) {
				console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');
				//if (panel != sourcePanel) {
					var params = {};
					for (var i = 0 ; i < paramsArray.length ; i++) {
						var aParam = paramsArray[i];
						params[aParam.name] = aParam.value;
					}
					app.controllers.composedExecutionController.refreshSubDocument(panel, params);
			//	}
			}, this);
			
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
			//panel.setMargin(10);
			this.add(panel);
		}
		
		,
		propagateCrossNavigationEvent : function(sourcePanel, params) {
			
			console.log('app.views.ComposedExecutionPanel:execCrossNavigation: IN');
			
			this.fireEvent('execCrossNavigation', sourcePanel, params);
		}
		
		,
		execCrossNavigationHandler : function(sourcePanel, params, targetPanel) {
			
			console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');
			
			app.controllers.executionController.executeTemplate({
				executionInstance : targetPanel.getExecutionInstance()
				, parameters : null
			}, panel);
			
		}
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