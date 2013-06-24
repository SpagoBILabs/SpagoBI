/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
Ext.define('app.views.ParametersView',{
		extend: 'Ext.Panel',
		config:{
			fullscreen: true,
			style: 'background-color: #747474;'
		},



			initialize : function() {
				this.html = '  ';
				this.callParent(arguments);

			}

			,refresh : function(items) {

				this.removeAll();
	
				var fieldset = Ext.create("Ext.form.FieldSet",{
						title : 'Parametri del Documento',
						xtype : 'fieldset',
						items : items
				});


				
				var formPanel = Ext.create("Ext.form.FormPanel",{
						
					autoRender : true,
					floating : true,
					modal : false,
					centered : true,
					height : 500,
					width : 600,
					scroll: 'vertical',
					hideOnMaskTap : false,
					items: [fieldset, {
						xtype : 'toolbar',
						docked : 'bottom',
						cls: 'parameters-toolbar',
						height: 30,
						defaults : {
							ui : 'plain',
							iconMask : true
						},
						scroll : 'horizontal',
						layout : {
							pack : 'center'
						},
						items : [
						         {
						    		 text : 'Previous',
						    		 iconCls : 'reply',
						        	 handler : function() {
						        		 app.views.executionContainer.goToPreviousExecutions(true);
						        	 }
						         },
						         {
						        	 title : 'Execute',
						        	 iconCls : 'settings',
						        	 text : 'Execute',
						        	 handler : function() {
						        		 var executionInstance = app.controllers.parametersController.executionInstance;
						        		 var isRefresh = app.controllers.parametersController.isRefresh;
						        		 executionInstance.PARAMETERS = app.controllers.parametersController.getFormState();
						        		 app.controllers.executionController.executeTemplate({
						        			 executionInstance : executionInstance
						        		 }, null, isRefresh);
						        	 }
						         } ]

					} ]
				});
				formPanel.show();
				this.add(formPanel);

				

			}
});