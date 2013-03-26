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
			 title: 'Execution view'
		}

,
	   
		initialize: function ()	{
			console.log('init Execution view');
//	        this.bottomTools = new app.views.BottomToolbar({parameters: this.parameters});
//
//	        this.dockedItems= [this.bottomTools];

			this.callParent(this, arguments);


		}
		, setWidget: function(resp, type, fromCross) {

			if (type == 'table'){
				var table = Ext.create("app.views.TableExecutionPanel",{ resp:resp, fromcomposition:false, fromCross:fromCross});
				table.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
				this.widget = table;
			}
			if (type == 'chart'){
				var chart = Ext.create("app.views.ChartExecutionPanel",{fullscreen: true, resp:resp, fromcomposition:false, fromCross:fromCross});
				chart.on('execCrossNavigation', this.propagateCrossNavigationEvent, this);
				this.widget = chart;
			}
			if (type == 'composed'){
				var composed = Ext.create("app.views.ComposedExecutionPanel", {resp: resp});
				this.widget = composed;
			}
			this.add(this.widget);
		}
		,hideBottomToolbar: function(){
//			this.bottomTools.hide();
		}
		,showBottomToolbar: function(){
//			this.bottomTools.show();
		}
		,
		setExecutionInstance : function (executionInstance) {
			this.widget.setExecutionInstance(executionInstance);
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