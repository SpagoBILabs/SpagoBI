/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.define('app.views.ExecutionView',{
		extend:'Ext.Panel',
		
		config:{
			 fullscreen: true,
			 layout: 'fit',
			 loadingMaskForExec: null,
			 title: 'Execution view',
			 style: {
				    background: 'white'
				},
			 positionInExecutionContainer: null//position of the view in the list of items in the ExecutionContainerView
		}

		,
	   
		initialize: function ()	{
			console.log('init Execution view');

			this.callParent(this, arguments);


		}
		, setWidget: function(resp, type, fromCross, executionInstance) {

			if (type == 'table'){
				var table = Ext.create("app.views.TableExecutionPanel",{ region: "center", resp:resp, fromcomposition:false, fromCross:fromCross, executionInstance:executionInstance});
				table.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
				this.widget = table;
			}
			if (type == 'chart'){
				var chart = Ext.create("app.views.ChartExecutionPanel",{region: "center",fullscreen: true, resp:resp, fromcomposition:false, fromCross:fromCross, executionInstance:executionInstance});
				chart.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
				this.widget = chart;
			}
			if (type == 'composed'){
				var composed = Ext.create("app.views.ComposedExecutionPanel", {region: "center",resp: resp, executionInstance:executionInstance});
				composed.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
				this.widget = composed;
			}

			this.widget.updateTitle();
			this.add(this.widget);
			
		}
		,
		setExecutionInstance : function (executionInstance) {
			this.widget.setExecutionInstance(executionInstance);
		}
		,
		getExecutionInstance : function () {
			return this.widget.getExecutionInstance();
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