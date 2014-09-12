/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *
 * Window that performs calculated members operation
 *
 *
 */

Ext.define('Sbi.olap.toolbar.CalculatedMembersWindow', {
	extend: 'Ext.window.Window',
	config:{
		height: 400,
		width: 400,
		actualVersion: null,
		autoScroll: true,
		bodyStyle: "background-color: white",
		title: LN("sbi.olap.toolbar.calculatedmemberswindow.title")

	},

	grid: null,
	constructor : function(config) {
		debugger;
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.toolbar && Sbi.settings.olap.toolbar.CalculatedMembersWindow) {
			Ext.apply(this, Sbi.settings.olap.toolbar.CalculatedMembersWindow);
		}
		var thisPanel = this;
		var service = Ext.create("Sbi.service.RestService", {
			url: "calculatedmembers",
			method: 'GET',
			async: true
		});
		var user=Ext.define('User', {
		    extend: 'Ext.data.Model',
		    fields: ['id', 'name']
		});

		var calculatedStore = Ext.create('Ext.data.Store', {

			model: user,
			//autoLoad: true,
		    autoSync: true,
			proxy: {
				type: 'rest',
				url: service.getRestUrlWithParameters(),
				extraParams: service.getRequestParams(),
				reader: {
		             type: 'json'
		         }

		/*       afterRequest: function(request, success) {
		        	 debugger;

		        	 var htmlContent = request.responseText;

		             console.log(htmlContent);
		         },
		         listeners: {
		             exception: function(proxy, response, options) {
		               debugger;
		               console.log(proxy, response, options);
		             }
		         }*/
			}
		});

		//calculatedStore.load();
		calculatedStore.load({
	/*	    params: {
		        group: 3,
		        type: 'user'
		    },*/

			callback: function(datastore, records, successful, eOpts ){
		    	debugger;
		    	//thisPanel.update(records.response.responseText);
		    },
		    scope: this
		});


/*		Ext.apply(this,{
			bbar:[
			      '->',    {
			    	  text: LN('sbi.common.ok'),
			    	  handler: function(){
			    		  thisPanel.fireCalculatedMemberOutputEvent();
			    	  },
			    	  scope: this
			      }]
			});*/




		this.grid = Ext.create('Ext.grid.Panel', {
			store: calculatedStore,
			columns: [
			          { text: 'Name',  dataIndex: 'name', flex:1 }
			          ]
		});
		var editorName = Ext.create('Ext.form.field.TextArea', {
			flex: 2
		});

		var editorFormula = Ext.create('Ext.form.field.TextArea', {
			flex: 2
		});

		var button = Ext.create('Ext.Button', {
		    text: 'Submit expression',
		    flex: 1,
		    handler: function() {
		    	debugger;
		    	thisPanel.sendExpression(editorName.getValue(), editorFormula.getValue());
		    }
		});
		var elPanel = Ext.create('Ext.panel.Panel', {
			frame: false,
			layout: {
				align : 'stretch',
				type: 'vbox',
			},
			height: 300,
		   items: [editorName, editorFormula, button]
		});


		/*var form=Ext.create('Ext.form.FormPanel', {
	        title: 'Insert your expression',
	        width: 400,
	        bodyPadding: 5,
	        renderTo: Ext.getBody(),
	        items: [{
	            xtype: 'textareafield',
	            grow: true,
	            name: 'message',
	            //fieldLabel: 'Message',
	            anchor: '90%'
	        }],

	        buttons: [{
	            text: 'Submit',
	            handler: function() {
	                // The getForm() method returns the Ext.form.Basic instance:
	                var form = this.up('form').getForm();
	                if (form.isValid()) {
	                    // Submit the Ajax request and handle the response
	                	thisPanel.sendExpression();
	                    form.submit({
	                        success: function(form, action) {
	                        	debugger;
	                           Ext.Msg.alert('Success', action.result.message);
	                        },
	                        failure: function(form, action) {
	                        	debugger;
	                            Ext.Msg.alert('Failed', action.result ? action.result.message : 'No response');
	                        }
	                    });
	                }
	            }
	        }]

	    });*/

		//this.items= [this.grid, form];
		this.items= [elPanel];
		/*this.bbar = [
		             '->',    {
		            	 text: LN('sbi.common.ok'),
		            	 handler: function(){

		            		 thisPanel.fireCalculatedMemberOutputEvent();
		            	 }
		             }];*/
/*		this.addEvents(

				'showCalculatedMemberOutput'
		);*/


		this.callParent(arguments);
	},

	//fireCalculatedMemberOutputEvent: function(){
	//	Sbi.olap.eventManager.showCalculatedMemberOutput();
		//this.fireEvent('showCalculatedMemberOutput');
		//this.destroy();
	//},

	sendExpression: function(name, formula){
		debugger;
		Sbi.olap.eventManager.executeCalculatedMemberExpression(name, formula);
		//this.fireEvent('showCalculatedMemberOutput');
		this.destroy();
	}

});
