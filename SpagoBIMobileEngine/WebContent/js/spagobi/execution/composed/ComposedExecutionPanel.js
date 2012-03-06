app.views.ComposedExecutionPanel = Ext.extend(app.views.WidgetPanel,

		{
	    scroll: 'vertical',
	     fullscreen: true
		, initComponent: function (options)	{

			console.log('init composed execution');
		    
			app.views.ComposedExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setComposedWidget: function(resp){
			var title = resp.title.value;
			
			var documentsList = resp.documents.docs;
			var documentWidth = resp.documents.totWidth;
			var documentHeight = resp.documents.totHeight;
			
			var items = new Array();
			
			var executionInstance = Ext.apply({}, resp.executionInstance);
			
			if (documentsList != undefined && documentsList != null) {
				for (var i = 0; i < documentsList.length; i++) {
					var subDocumentPanel = this.buildPanel(documentsList[i]);
					var mainDocumentParameters = executionInstance.PARAMETERS;
					var subDocumentDefaultParameters = documentsList[i].IN_PARAMETERS;
					var subDocumentParameters = Ext.apply(subDocumentDefaultParameters, mainDocumentParameters);
					var subDocumentExecutionInstance = Ext.apply({}, documentsList[i]);
					subDocumentExecutionInstance.PARAMETERS = subDocumentParameters;
					subDocumentExecutionInstance.IS_FROM_COMPOSED = true;
					subDocumentExecutionInstance.ROLE = executionInstance.ROLE;
					app.controllers.composedExecutionController.executeSubDocument(subDocumentExecutionInstance, subDocumentPanel);
					items.push(subDocumentPanel);
				}
			}

			var composedDocumentContainerConfig = {
				fullscreen: true,
	            bodyMargin: '20px 50px 100px 50px',
	            items: items
	        };
//
//			if(documentWidth && documentHeight){
//				composedDocumentContainerConfig.height =documentHeight;
//				composedDocumentContainerConfig.width =documentWidth;
//			}
			
			var composedDocumentContainer =	new Ext.Panel(composedDocumentContainerConfig);
			app.views.composed =  composedDocumentContainer;
			this.add(app.views.composed);

		},
		
		buildPanel: function(config){

			var panel;
			config = Ext.apply(config,{style: 'float: left;', bodyMargin:10
				, IS_FROM_COMPOSED: true
				});
			
			if (config.TYPE_CODE == Sbi.constants.documenttype.chart) {
				panel = new app.views.ChartExecutionPanel(config);
			} else {
				panel = new app.views.TableExecutionPanel(config);
			}
			
			panel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
			
			/*
			 * with this instruction, panel is not passed properly
			 */
			//this.on('execCrossNavigation', Ext.createDelegate(this.execCrossNavigationHandler, this, [panel], true));
			
			this.on('execCrossNavigation', function (sourcePanel, paramsArray) {
				console.log('app.views.ComposedExecutionPanel:execCrossNavigationHandler: IN');
				if (panel != sourcePanel) {
					var params = {};
					for (var i = 0 ; i < paramsArray.length ; i++) {
						var aParam = paramsArray[i];
						params[aParam.name] = aParam.value;
					}
					app.controllers.composedExecutionController.refreshSubDocument(panel, params);
				}
			}, this);
			
			return panel;
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

		
});