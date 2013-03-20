/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.define('app.views.ExecutionView',{
		extend:'Ext.Panel',
	    fullscreen: true,
	    layout: 'fit',
	    loadingMaskForExec: null,
		initComponent: function ()	{
			this.title = 'Execution view';
			console.log('init Execution view');
	        this.bottomTools = new app.views.BottomToolbar({parameters: this.parameters});

	        this.dockedItems= [this.bottomTools];

			app.views.tableExecutionPanel = new app.views.TableExecutionPanel();
			app.views.chartExecutionPanel = new app.views.ChartExecutionPanel({fullscreen: true});
			app.views.composedExecutionPanel = new app.views.ComposedExecutionPanel();
			
		    Ext.apply(this, {
		        items: [
		            app.views.tableExecutionPanel,
		            app.views.chartExecutionPanel,
		            app.views.composedExecutionPanel
		        ]
		    });
		    
		    app.views.tableExecutionPanel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
		    app.views.chartExecutionPanel.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);

			app.views.ExecutionView.superclass.initComponent.apply(this, arguments);


		}
		, setWidget: function(resp, type, fromCross) {

			if (type == 'table'){
				app.views.tableExecutionPanel.setTableWidget(resp, false, fromCross);
				this.widget = app.views.tableExecutionPanel;
			}
			if (type == 'chart'){
				app.views.chartExecutionPanel.setChartWidget(resp, false, fromCross);
				this.widget = app.views.chartExecutionPanel;
			}
			if (type == 'composed'){
				app.views.composedExecutionPanel.setComposedWidget(resp);
				this.widget = app.views.composedExecutionPanel;
			}

		}
		,hideBottomToolbar: function(){
			this.bottomTools.hide();
		}
		,showBottomToolbar: function(){
			this.bottomTools.show();
		}
		,
		setExecutionInstance : function (executionInstance) {
			this.widget.setExecutionInstance(executionInstance);
		}

		, setWidgetComposed: function(resp, type, panel){
			if(type == 'table'){
				panel.setTableWidget(resp, true);
			}
			if(type == 'chart'){
				panel.setChartWidget(resp, true);
			}
			if(type == 'composed'){
				panel.setComposedWidget(resp, true);
			}
		}
		,
		propagateCrossNavigationEvent : function(sourcePanel, params, targetDoc) {
			
			  console.log('propagating cross nav');

			
			  app.controllers.executionController.getDocumentInfoForCrossNavExecution({
				  targetDoc: targetDoc,
				  params: params
			  });
		}
});