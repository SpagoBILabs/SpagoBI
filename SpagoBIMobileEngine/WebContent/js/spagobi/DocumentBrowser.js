/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('app.views.DocumentBrowser',{
	extend:'Ext.NestedList',

	config:{
		scroll: 'vertical',
		flex:1,
		toolbar: {
			hidden: true
		},
		title: 'Document Browser',
		displayField: 'name',
		listeners: {
			itemtap:function( item, list, index, target, record, e, eOpts ){
				var button = this.getBackButtomFromToolbar();
				if(button){
					button.show();
					button.setText(record.data.name);
				}
			}
		},
		store: new Ext.data.TreeStore({
			model: 'browserItems',
			proxy: {
				type: 'ajax',
				reader:{
					type: 'json',
					rootProperty: 'samples',
					listeners:{
						exception: function(reader, response, error, eOpts ){
							if(response.responseText && response.responseText.indexOf('<SERVICE_RESPONSE')){
								if(response.responseText.indexOf('internal') != -1){
									//exception
									Sbi.exception.ExceptionHandler.handleFailure(response);
								}else{
									//go to login page
									localStorage.removeItem('app.views.launched');
									localStorage.removeItem('app.views.browser');
									window.location.href = Sbi.env.contextPath;
								}
							}
						}
					}
				},
				url: Sbi.config.serviceRegistry.getServiceUrl({
					serviceName: 'DOCUMENT_BROWSER_ACTION'
						, baseParams: {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
				})
				
			}
		})
	},

	constructor: function(config){
		Ext.apply(this, config||{});
		this.callParent(arguments);
	},

	reloadPanel: function(){
		var store = this.getStore();
		store.load();

	},

	
	
	getTitleTextTpl: function() {
		return '<tpl><div>{name}</div></tpl>';
	},

	getItemTextTpl: function(node) {
		var tplTxt = '<tpl if="typeCode == \'' + Sbi.constants.documenttype.report + '\'">'+
		'<div class="table-item">{name}</div>'+
		'</tpl>'+
		'<tpl if="typeCode == \'' + Sbi.constants.documenttype.chart + '\'">'+
		'<div class="chart-item">{name}</div>'+
		'</tpl>'+
		'<tpl if="typeCode == \'' + Sbi.constants.documenttype.cockpit + '\'">'+
		'<div class="composed-item">{name}</div>'+
		'</tpl>'+
		'<tpl if="typeCode == undefined || typeCode == null || typeCode ==\'\'">'+
		'<div class="navigate">{name}</div>'+
		'</tpl>';
		return tplTxt;
	},

	getDetailCard: function( record, parentRecord ){
		if(record){
//			app.controllers.mobileController.showDetail({record: record});
			//direct execution: no preview
			var rec = record.data;
			app.controllers.mobileController.getRoles({
				action: 'getRoles',
				id: rec.id,
				label: rec.label, 
				engine: rec.engine, 
				typeCode: rec.typeCode
			});
		}


	}

	,goBack: function(){
		var node = this.findAncestralNode(this, 1);
		this.goToNode(node);
		var backButton = this.getBackButtomFromToolbar();
		if(backButton){
			if(node.data.text=="Root"){
				backButton.hide();
			}else{
				backButton.show();
				backButton.setText(node.data.name);
			}	
		}

	}
	
	,goToRoot:function(){
		var node = this.findRootNode(this);
		this.goToNode(node);
	}
	
	,findAncestralNode: function (theNestedList,levelsUp){
	    levelsUp = typeof levelsUp !== 'undefined' ? levelsUp : -1;
	    var levelsSoFar = 0;
	    var curNode = theNestedList._lastNode;
	    while( curNode.parentNode !== null && levelsSoFar != levelsUp){
	        curNode = curNode.parentNode;
	        levelsSoFar++;
	    }
	    return curNode;
	}
	
	,findRootNode: function (theNestedList){
	    var curNode = theNestedList._lastNode;
	    while( curNode.parentNode !== null){
	        curNode = curNode.parentNode;
	    }
	    return curNode;
	}
	
	,getBackButtomFromToolbar: function(){
		if(this.containerToolbar && this.containerToolbar.visibleButtons){
			if(this.backbutton){
				return this.backbutton;
			}
			return this.containerToolbar.getToolbarButtonByType('documentbrowser');
		}
		return null;
	}




});